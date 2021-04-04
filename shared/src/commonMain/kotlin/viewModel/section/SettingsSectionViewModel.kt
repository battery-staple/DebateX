package com.rohengiralt.debatex.viewModel.section

import com.rohengiralt.debatex.model.sectionModel.SettingModel
import com.rohengiralt.debatex.model.sectionModel.SettingModel.SettingOptions.MultipleChoice.MultipleChoiceOption
import com.rohengiralt.debatex.model.sectionModel.SettingsSectionModel
import com.rohengiralt.debatex.observation.Observable
import com.rohengiralt.debatex.observation.Observer
import com.rohengiralt.debatex.observation.PassthroughPublisher
import com.rohengiralt.debatex.observation.WeakReferencePublisher
import com.rohengiralt.debatex.settings.SettingsAccess
import com.rohengiralt.debatex.viewModel.ViewModel
import com.rohengiralt.debatex.viewModel.setting.SettingViewModel
import kotlinx.serialization.KSerializer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.native.concurrent.ThreadLocal
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

lateinit var applicationSettings: SettingsSectionModel
    private set

interface ObservableSettingDelegate<in T, V> :
    PropertyDelegateProvider<T, ReadOnlyProperty<T, V>>, Observable<Observer>

fun <V> registerSetting(
    name: String,
    type: SettingModel.SettingOptions<V>,
): ObservableSettingDelegate<Any?, V> =
    object : ObservableSettingDelegate<Any?, V>, Observable<Observer> by SettingsSectionViewModel.settingsPublisher {
        override fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, V> {
            val newSettingModel = SettingModel(name, null, type)

            applicationSettings = if (::applicationSettings.isInitialized) {
                applicationSettings.copy(settings = applicationSettings.settings.filter { it.name != newSettingModel.name } + newSettingModel)
            } else {
                SettingsSectionModel(newSettingModel)
            }

            return SettingsSectionViewModel.delegateForSetting(newSettingModel).provideDelegate(thisRef, property)
        }
    }


@ThreadLocal //TODO: make only on main thread?
object SettingsSectionViewModel : ViewModel(), KoinComponent {
    private val settingsAccess: SettingsAccess by inject()

    internal val model: SettingsSectionModel by lazy { applicationSettings } // static initialization order may cause a problem here

    private val modelsToSettings: MutableMap<SettingModel<*>, SettingsAccess.Setting<*>> =
        mutableMapOf() // Implicit contract: type parameters of SettingModel and Setting are the same

    internal fun <T> delegateForSetting(model: SettingModel<T>): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>> =
        PropertyDelegateProvider { _, _ ->
            val setting = modelsToSettings.getOrPut(model) {
                with(settingsAccess) {
                    when (val options = model.options) {
                        is SettingModel.SettingOptions.Integer ->
                            Setting(model.name, SettingsAccess.Type.Int, options.defaultValue)
                        is SettingModel.SettingOptions.FloatingPoint ->
                            Setting(model.name, SettingsAccess.Type.Double, options.defaultValue)
                        is SettingModel.SettingOptions.Alphanumeric ->
                            Setting(model.name, SettingsAccess.Type.String, options.defaultValue)
                        is SettingModel.SettingOptions.Boolean ->
                            Setting(model.name, SettingsAccess.Type.Boolean, options.defaultValue)
                        is SettingModel.SettingOptions.MultipleChoice<*> ->
                            Setting(
                                model.name,
                                SettingsAccess.Type.Serializable(MultipleChoiceOption.serializer(options.serializer) as KSerializer<MultipleChoiceOption<out Any?>>), //Type-erased MultipleChoice<*> means the compiler can't know this is the correct serializer for the default value
                                options.defaultValue
                            )
                    }
                }
            } as SettingsAccess.Setting<T>

            ReadOnlyProperty { _, _ ->
                setting.get()
            }
        }

    val settings: List<SettingViewModel<*>> by lazy {
        modelsToSettings.map { (model, setting) ->
            SettingViewModel.Companion( model as SettingModel<Any>, setting as SettingsAccess.Setting<Any>)
        }
    }

    internal val settingsPublisher = PassthroughPublisher(WeakReferencePublisher())

    fun updateAllSettings(): Unit =
        settings
            .forEach { it.update() }
            .also { settingsPublisher.publish() }
}