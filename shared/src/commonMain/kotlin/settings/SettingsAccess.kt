package com.rohengiralt.debatex.settings

import com.rohengiralt.debatex.loggerForClass
import com.rohengiralt.debatex.settings.settingsStore.SettingsStore
import com.rohengiralt.debatex.settings.settingsStore.get
import com.rohengiralt.debatex.settings.settingsStore.getOrNull
import com.rohengiralt.debatex.settings.settingsStore.set
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

open class SettingsAccess(private val settingStore: SettingsStore) {
    val allSettings: MutableList<Setting<*>> = mutableListOf()

    @Suppress("FunctionName")
    inline fun <T> Setting(type: Type<T>, initialValue: T): PropertyDelegateProvider<SettingsAccess, Setting<T>> =
        PropertyDelegateProvider { _, property ->
            Setting(property.name, type, initialValue)
        }

    private val settingLogger = loggerForClass<Setting<*>>()

    inner class Setting<T>(
        val name: String,
        val type: Type<T>,
        initialValue: T,
        key: String? = null,
    ) : ReadWriteProperty<SettingsAccess, T> {

        init {
            allSettings.add(this) //Unsafe leaking of this? TODO: Test
        }

        private val key = key ?: name

        init {
            if (!settingStore.isInitialized(type, this.key)) {
                set(initialValue)
            }
        }

        fun get(): T = settingStore[type, key]
        fun getOrNull(): T? = if (settingStore.isInitialized(type, key)) get() else null

        fun set(value: T) {
            settingLogger.info("Setting $name to $value from ${getOrNull() ?: "uninitialized"}.")
            settingStore[type, key] = value
        }

        override fun getValue(thisRef: SettingsAccess, property: KProperty<*>): T = get()
        override fun setValue(thisRef: SettingsAccess, property: KProperty<*>, value: T) {
            set(value)
        }
    }

    interface Type<T> {
        fun getFrom(settingsStore: SettingsStore, key: kotlin.String): T
        fun setIn(settingsStore: SettingsStore, key: kotlin.String, value: T)
        fun isInitializedIn(settingsStore: SettingsStore, key: kotlin.String): kotlin.Boolean
//        infix fun SettingsStore.isInitializedIn(key: kotlin.String): kotlin.Boolean

        abstract class PreImplementedType<T : Any> internal constructor() : Type<T> {
            abstract val kClass: KClass<T>

            override fun isInitializedIn(settingsStore: SettingsStore, key: kotlin.String): kotlin.Boolean =
                settingsStore.getOrNull(kClass, key) != null

            override fun getFrom(settingsStore: SettingsStore, key: kotlin.String): T = settingsStore[kClass, key]
            override fun setIn(settingsStore: SettingsStore, key: kotlin.String, value: T) {
                settingsStore[kClass, key] = value
            }
        }

        object Int : PreImplementedType<kotlin.Int>() {
            override val kClass: KClass<kotlin.Int> get() = kotlin.Int::class
        }

        object Long : PreImplementedType<kotlin.Long>() {
            override val kClass: KClass<kotlin.Long> get() = kotlin.Long::class
        }

        object String : PreImplementedType<kotlin.String>() {
            override val kClass: KClass<kotlin.String> get() = kotlin.String::class
        }

        object Float : PreImplementedType<kotlin.Float>() {
            override val kClass: KClass<kotlin.Float> get() = kotlin.Float::class
        }

        object Double : PreImplementedType<kotlin.Double>() {
            override val kClass: KClass<kotlin.Double> get() = kotlin.Double::class
        }

        object Boolean : PreImplementedType<kotlin.Boolean>() {
            override val kClass: KClass<kotlin.Boolean> get() = kotlin.Boolean::class
        }

        @OptIn(ExperimentalSerializationApi::class)
        class Serializable<T>(
            private val serializer: KSerializer<T>,
            private val format: StringFormat = Json.Default,
        ) : Type<T> {
            override fun isInitializedIn(settingsStore: SettingsStore, key: kotlin.String): kotlin.Boolean =
                settingsStore.getStringOrNull(key) != null

            override fun getFrom(settingsStore: SettingsStore, key: kotlin.String): T =
                format.decodeFromString(serializer, settingsStore.getString(key))

            override fun setIn(settingsStore: SettingsStore, key: kotlin.String, value: T) {
                settingsStore[key] = format.encodeToString(serializer, value)
            }
        }
    }
}

private inline operator fun <T> SettingsStore.get(type: SettingsAccess.Type<T>, key: String): T =
    type.getFrom(this, key)

private inline operator fun <T> SettingsStore.set(type: SettingsAccess.Type<T>, key: String, value: T): Unit =
    type.setIn(this, key, value)

private inline fun <T> SettingsStore.isInitialized(type: SettingsAccess.Type<T>, key: String) =
    type.isInitializedIn(this, key)