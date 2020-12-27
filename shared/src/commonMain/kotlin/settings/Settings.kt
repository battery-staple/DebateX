package com.rohengiralt.debatex.settings

import com.rohengiralt.debatex.defaultLogger
import com.rohengiralt.debatex.loggerForClass
import com.rohengiralt.debatex.model.TimerCountStrategy
import com.rohengiralt.debatex.settings.settingsStore.SettingsStore
import com.russhwolf.settings.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlin.native.concurrent.ThreadLocal
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import com.russhwolf.settings.Settings as RusshwolfSettings

@ThreadLocal //TODO: IMPORTANT: Is ThreadLocal the best way to do this? Refactoring may be necessary when async added.
val applicationSettings: Settings = Settings(RusshwolfSettings())

open class Settings internal constructor(settingsStore: RusshwolfSettings) {

    init {
        logger.info("settingsStore: $settingsStore")
    }

    internal val allSettings: MutableList<Setting<*>> = mutableListOf()

    val playSoundOnComplete: Setting<Boolean>
            by settingsStore.newSetting(settingType = SettingType.Boolean(), defaultValue = false)

    //        val initialHue: Setting<Double> by settingsStore.newSetting(settingType = SettingType.Number { it < 1.0 && it >= 0.0 }, defaultValue = 0.3)
    val countStrategy: Setting<TimerCountStrategy>
            by settingsStore.newSetting(
                settingType = SettingType.MultipleChoice(TimerCountStrategy.CountUp, TimerCountStrategy.CountDown),
                serializer = TimerCountStrategy.serializer(),
                defaultValue = TimerCountStrategy.CountDown
            )

    fun update() {
        allSettings.forEach {
            it.update()
            logger.info("Updated setting $it")
        }
    }

    private inline fun <reified T> RusshwolfSettings.newSetting(
        name: String? = null,
        settingType: SettingType<T>,
        defaultValue: T,
        throwIfInvalid: Boolean = false,
    ): PropertyDelegateProvider<Settings, ReadOnlyProperty<Settings, Setting<T>>> =
        object : PropertyDelegateProvider<Settings, ReadOnlyProperty<Settings, Setting<T>>> {
            private val logger = defaultLogger()
            lateinit var setting: Setting<T>

            override fun provideDelegate(
                thisRef: Settings,
                property: KProperty<*>,
            ): ReadOnlyProperty<Settings, Setting<T>> {
                val concreteName = name ?: property.name
                if (!::setting.isInitialized) setting = Setting(
                    name = concreteName,
                    get = { setting(concreteName, defaultValue) },
                    set = { newValue -> set(concreteName, newValue) },
                    settingType = settingType,
                    throwIfInvalid = throwIfInvalid
                ).also {
                    allSettings.add(it)
                }

                return ReadOnlyProperty { _, _ -> setting }
            }
        }

    private inline fun <T, U : KSerializer<T>> RusshwolfSettings.newSetting(
        name: String? = null,
        settingType: SettingType<T>,
        serializer: U,
        defaultValue: T,
        throwIfInvalid: Boolean = false,
    ): PropertyDelegateProvider<Settings, ReadOnlyProperty<Settings, Setting<T>>> =
        object : PropertyDelegateProvider<Settings, ReadOnlyProperty<Settings, Setting<T>>> {
            lateinit var setting: Setting<T>

            override fun provideDelegate(
                thisRef: Settings,
                property: KProperty<*>,
            ): ReadOnlyProperty<Settings, Setting<T>> {
                val concreteName = name ?: property.name
                if (!::setting.isInitialized) setting = Setting(
                    name = concreteName,
                    get = { getSerializable(concreteName, serializer, defaultValue) },
                    set = { newValue -> putSerializable(concreteName, serializer, newValue) },
                    settingType = settingType,
                    throwIfInvalid = throwIfInvalid
                ).also {
                    allSettings.add(it)
                }

                return ReadOnlyProperty { _, _ -> setting }
            }
        }

    companion object {
        private val logger = defaultLogger()
    }
}

sealed class SettingType<in ValidType>(val isValid: (ValidType) -> kotlin.Boolean) {
    //    class Integer(isValid: (Int) -> kotlin.Boolean) : SettingType<Int>(isValid)
//    class Number(isValid: (Double) -> kotlin.Boolean) : SettingType<Double>(isValid)
    class String(isValid: (kotlin.String) -> kotlin.Boolean) : SettingType<kotlin.String>(isValid)
    class Boolean(isValid: (kotlin.Boolean) -> kotlin.Boolean = { true }) : SettingType<kotlin.Boolean>(isValid)
    class MultipleChoice<SuperType>(
        @Suppress("WEAKER_ACCESS") val options: Set<SuperType>
    ) : SettingType<SuperType>({ it in options }) { //TODO: Why does IntelliJ complain about using trailing closure syntax here?
        constructor(vararg options: SuperType) : this(options.toSet())
    }

    companion object {
        val all: Set<KClass<out SettingType<*>>> = setOf(
//            Integer::class,
//            Number::class,
            String::class,
            Boolean::class,
            MultipleChoice::class
        )
    }
}

@Suppress("UNCHECKED_CAST")
private inline fun <reified T> RusshwolfSettings.setting(key: String, defaultValue: T): T {
    val getT = when (defaultValue) {
        is Int -> ::getInt
        is Long -> ::getLong
        is String -> ::getString
        is Float -> ::getFloat
        is Double -> ::getDouble
        is Boolean -> ::getBoolean
//        is Enum -> ::getEnum TODO: Add
        else -> throw UnsupportedOperationException("Storing objects of type ${T::class.simpleName} is not supported.")
    } as (String, T) -> T

    return getT(key, defaultValue)
}

//private val russhwolfSerializer = Json {
//    allowStructuredMapKeys = true
//    useArrayPolymorphism = true
//}

fun <T, U : DeserializationStrategy<T>> RusshwolfSettings.getSerializable(
    key: String,
    serializer: U,
    defaultValue: T,
): T {
    val serializedObject = getStringOrNull(key) ?: return defaultValue

    return Json.decodeFromString(serializer, serializedObject)
}

fun <T, U : SerializationStrategy<T>> RusshwolfSettings.putSerializable(key: String, serializer: U, value: T) {
    val serializedObject = Json.encodeToString(serializer, value)

    putString(key, serializedObject)
}

class Setting<T> internal constructor(
    val name: String, // Must be unique (for testing and more) TODO: enforce?
    private val get: () -> T,
    private val set: (T) -> Unit,
    val settingType: SettingType<T>,
    var throwIfInvalid: Boolean = false,
) : ReadWriteProperty<Any?, T> {
    private fun setValidated(value: T) {
        if (settingType.isValid(value)) {
            set(value)
        } else if (throwIfInvalid) throw IllegalArgumentException("$value is not a valid state of this setting.") else {
            logger.warn("Could not set value to $value")
        }
    }

    var value: T
        get() = get().also { logger.debug("Got setting as $it") }
        set(value) = setValidated(value).also { logger.debug("Set setting to $value") }

//    private val listeners = mutableListOf<KMutableProperty0<in T>>()
//    internal fun addListener(property: KMutableProperty0<in T>) {
//        listeners.add(property)
//    }

    internal fun update() {
//        listeners.forEach {
//            try {
//                it.set(value)
//            } catch (e: ClassCastException) {
//                TODO()
//            }
//        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T): Unit = set(value)
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = get()

    companion object {
        private val logger = loggerForClass<Setting<*>>()
    }
}

//
//@Suppress("UNCHECKED_CAST")
//private inline operator fun <reified T> Settings.invoke(key: String? = null, defaultValue: T) =
//    (when (defaultValue) {
//        is Int -> ::int
//        is Long -> ::long
//        is String -> ::string
//        is Float -> ::float
//        is Double -> ::double
//        is Boolean -> ::boolean
//        is Enum<*> -> ::enum
//        else -> throw UnsupportedOperationException("Storing objects of type ${T::class.simpleName} is not supported.")
//    } as (String?, T) -> ReadWriteProperty<Any?, T>)(key, defaultValue)
//
//private fun <T : Enum<*>> Settings.enum(key: String? = null, defaultValue: Enum<*>): ReadWriteProperty<Any?, Enum<*>> =
//    object : ReadWriteProperty<Any?, Enum<*>> {
//        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Enum<*>) {
//            this@enum[key ?: property.name] = value.name
//        }
//
//        override fun getValue(thisRef: Any?, property: KProperty<*>): Enum<*> {
//            return this@enum.getStringOrNull(key ?: property.name)?.let { enumValueOf<>(it) } ?: defaultValue
//        }
//    }
