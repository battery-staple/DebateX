@file:JvmMultifileClass

package com.rohengiralt.debatex.settings

import com.rohengiralt.debatex.ImproperTestSetupError
import com.rohengiralt.debatex.Logger
import com.rohengiralt.debatex.loggerForClass
import com.rohengiralt.debatex.model.TimerCountStrategy
import com.russhwolf.settings.MockSettings
import kotlin.js.JsName
import kotlin.jvm.JvmMultifileClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.test.Test
import kotlin.test.assertEquals

internal abstract class SettingsProxy(val settings: Settings) { //TODO: Generic Accessor<Settings> ?

    protected abstract val settingProxies: List<SettingProxy<*>>

    inner class SettingProxy<T>(setting: Setting<T>) {

        private val _setting = setting

        init {
            require(_setting in settings.allSettings)
        }

        val name get() = _setting.name

        val setting: Setting<T>
            get() {
                accessedSettings.add(_setting)
                return _setting
            }

        override fun equals(other: Any?): Boolean =
            if (other is SettingProxy<*>) {
                other._setting == _setting
            } else false

        override fun hashCode(): Int {
            return _setting.hashCode()
        }
    }

    private val accessedSettings = mutableListOf<Setting<*>>()

    inline fun <R> ruan(ensureAllSettingsAccessed: Boolean = true, block: SettingsProxy.() -> R): R {
        accessedSettings.clear()
        return this.block().also {
            if (ensureAllSettingsAccessed && !allSettingsAreAccessed()) {
                throw ImproperTestSetupError("Not all settings accessed in test.")
            }
        }
    }

    private inline fun allSettingsAreAccessed(): Boolean =
        settingProxies.all { it.setting in accessedSettings }

//    inline fun <T> proxyOfSettingNamed(name: String, type: SettingType<T>? = null): SettingProxy<T> =
//        allSettingsProxy.filter {
//            with(it.get()) { settingType == type && it.name == name }
//        }.let { listOfMatches ->
//            if (listOfMatches.size == 1) {
//                listOfMatches[0]
//            } else {
//                throw IllegalStateException("Multiple Settings found named $name")
//            } as SettingProxy<T>
//        }

    companion object {
        private val logger = loggerForClass<SettingsProxy>()
    }
}


class SettingsTest {
    internal object Proxy : SettingsProxy(Settings(MockSettings())) {
        val countStrategy get() = SettingProxy(settings.countStrategy)
        val playSoundOnComplete get() = SettingProxy(settings.playSoundOnComplete)

        override val settingProxies: List<SettingProxy<*>> = listOf(countStrategy, playSoundOnComplete)
    }

    @Test
    @JsName("InitialValuesAreAsExpected")
    fun `Initial values are as expected`(): Unit = Proxy.run {
        assertEquals(countStrategy.setting.value, TimerCountStrategy.CountDown)
        assertEquals(playSoundOnComplete.setting.value, false)
//        assertEquals(Settings.initialHue.value, 0.3)
    }

//    @Test
//    @JsName("SettingsPropertyContainsAllValidSettings")
//    fun `Setting Property Contains All Valid Settings`(): Unit = Proxy.run {
//        assertEquals(
//            settings.allSettings.toSet(),
//            settings.allProperties.mapNotNull { it.getCoerced<Settings, Setting<*>>(receiver = settings) }.toSet()
//        )
//    }

//    private inline fun <reified T> getTestValues(): List<T> =
//        when (T::class) {
//            Boolean::class -> listOf(true, false, false, true, true, true, false, true)
//            TimerCountStrategy::class -> listOf(
//                TimerCountStrategy.CountDown,
//                TimerCountStrategy.CountDown,
//                TimerCountStrategy.CountUp,
//                TimerCountStrategy.CountDown,
//                TimerCountStrategy.CountUp,
//                TimerCountStrategy.CountUp,
//                TimerCountStrategy.CountUp,
//                TimerCountStrategy.CountUp,
//                TimerCountStrategy.CountDown
//            )
//            else -> {
//                logger.warn("No test values found for type ${T::class.simpleName}")
//                emptyList()
//            }
//        } as List<T>

    @Suppress("RemoveExplicitTypeArguments")
    private fun testValues(property: SettingsProxy.SettingProxy<out Any>) =
        when (property) {
            Proxy.playSoundOnComplete -> listOf<Boolean>(true, false, false, true, true, true, false, true)
            Proxy.countStrategy -> listOf<TimerCountStrategy>(
                TimerCountStrategy.CountDown,
                TimerCountStrategy.CountDown,
                TimerCountStrategy.CountUp,
                TimerCountStrategy.CountDown,
                TimerCountStrategy.CountUp,
                TimerCountStrategy.CountUp,
                TimerCountStrategy.CountUp,
                TimerCountStrategy.CountUp,
                TimerCountStrategy.CountDown
            )
//            Proxy.initialHue -> listOf<Double>(0.0, 0.99999999999, 0.5, 0.2, 0.138134, 0.127177172581285, 0.19861291258189295, 0.83222, 0.90001, 0.9999, 0.100001881294), //TODO: Generate with Random
            else -> throw IllegalArgumentException("No test values available for property ${property.name}")
        }

    private fun setToTestValuesAndRun(
        settingProxy: SettingsProxy.SettingProxy<out Any>,
        block: (testValue: Any) -> Unit,
    ) {
        val testValues = testValues(settingProxy)
        for ((index, testValue) in testValues.withIndex()) {
            logger.info("Setting ${settingProxy.name} to $testValue (${index + 1} of ${testValues.size})")

            @Suppress("UNCHECKED_CAST")
            (settingProxy.setting as Setting<Any>).value = testValue

            block((settingProxy.setting as Setting<Any>).value)
        }
    }

    @Test
    @JsName("StoresAndCanRetrieveSettings")
    fun `Can store and retrieve stored settings`(): Unit = Proxy.run {
        val testProxies =
            listOf(playSoundOnComplete, countStrategy)

        for (proxy in testProxies) {
            setToTestValuesAndRun(proxy) { testValue ->
                assertEquals(testValue, proxy.setting.value)
            }
        }
    }

    /*@Test
    @JsName("SettingsUpdateUpdatesListeners")
    fun `Settings update updates listeners`(): Unit = Proxy.run {
        val listener = object {
            var playSoundOnComplete = settings.playSoundOnComplete.value
            var countStrategy = settings.countStrategy.value
        }

        val listenerPropertiesToSettingProxies = with(listener) {
            mapOf(
                ::playSoundOnComplete to Proxy.playSoundOnComplete,
                ::countStrategy to Proxy.countStrategy,
            )
        }

        fun settingProxyForListenerProperty(listenerProperty: KMutableProperty0<out Any>) =
            listenerPropertiesToSettingProxies[listenerProperty]
                ?: throw IllegalArgumentException("${listenerProperty.name} is not a property of the listener.")

        val listenerProperties = listenerPropertiesToSettingProxies.keys

        for (property in listenerProperties) {
            settingProxyForListenerProperty(property).setting.addListener(property as KMutableProperty0<Any?>)

            setToTestValuesAndRun(settingProxyForListenerProperty(property)) { testValue ->
                assertEquals(testValue, property.get())
            }
        }
    }*/

    companion object {
        val logger: Logger = loggerForClass<SettingsTest>()
    }
}

internal expect val <T : Any> T.allProperties: List<KProperty1<T, *>>
internal inline val <T : Any> T.allMutableProperties: List<KMutableProperty1<T, *>>
    get() = allProperties.filterIsInstance<KMutableProperty1<T, *>>()

private inline fun <C, reified T : Any> KProperty1<C, *>.getCoerced(receiver: C): T? =
    try {
        get(receiver) as? T
    } catch (e: Exception) {
        null
    }