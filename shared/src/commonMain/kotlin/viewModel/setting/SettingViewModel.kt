package com.rohengiralt.debatex.viewModel.setting

import com.rohengiralt.debatex.model.sectionModel.SettingModel
import com.rohengiralt.debatex.model.sectionModel.SettingModel.SettingOptions.MultipleChoice.MultipleChoiceOption
import com.rohengiralt.debatex.observation.Observer
import com.rohengiralt.debatex.observation.PassthroughPublisher
import com.rohengiralt.debatex.settings.SettingsAccess
import com.rohengiralt.debatex.util.isDigit
import com.rohengiralt.debatex.viewModel.ViewModel
import kotlin.math.pow
import kotlin.math.round

sealed class /*TODO: interface in 1.5*/ SettingViewModel<T : Any>(
    //TODO: I don't really like this architecture
    protected val model: SettingModel<*>,
    private val transformedSetting: TransformedSetting<*, T>,
) : ViewModel() {
    val name: String = model.name
    abstract val type: Type

    final override val observationHandler: PassthroughPublisher<Observer> get() = super.observationHandler
    open var currentEntry: T by observationHandler.published(transformedSetting.get())

    fun save() {
        transformedSetting.set(currentEntry)
    }

    enum class Type { // used for exhaustive switch in Swift, since it doesn't support sealed classes.
        SWITCH, TEXT_FIELD, PICKER
    }

    companion object {
        operator fun <T : Any> invoke(model: SettingModel<T>, setting: SettingsAccess.Setting<T>) =
            when (model.options) {
                is SettingModel.SettingOptions.Integer ->
                    IntegerContentTextFieldSettingViewModel(model as SettingModel<Int>, setting as SettingsAccess.Setting<Int>)
                is SettingModel.SettingOptions.FloatingPoint ->
                    FloatContentTextFieldSettingViewModel(model as SettingModel<Double>, setting as SettingsAccess.Setting<Double>)
                is SettingModel.SettingOptions.Alphanumeric ->
                    StringContentTextFieldSettingViewModel(model as SettingModel<String>, setting as SettingsAccess.Setting<String>)
                is SettingModel.SettingOptions.Boolean ->
                    SwitchSettingViewModel(model as SettingModel<Boolean>, setting as SettingsAccess.Setting<Boolean>)
                is SettingModel.SettingOptions.MultipleChoice<*> ->
                    PickerSettingViewModel(model as SettingModel<MultipleChoiceOption<T>>, setting as SettingsAccess.Setting<MultipleChoiceOption<T>>)
            } as SettingViewModel<T>
    }
}

interface TransformedSetting<S, D> {
    val setting: SettingsAccess.Setting<S>
    fun get(): D
    fun set(value: D)
}

inline class NoTransformSetting<T>(override val setting: SettingsAccess.Setting<T>) : TransformedSetting<T, T> {
    override fun get(): T = setting.get()
    override fun set(value: T): Unit = setting.set(value)
}

class CustomTransformedSetting<S, D>(
    override val setting: SettingsAccess.Setting<S>,
    private val getTransform: (value: S) -> D,
    private val setTransform: (value: D) -> S,
) : TransformedSetting<S, D> {
    override fun get(): D = getTransform(setting.get())
    override fun set(value: D): Unit = setting.set(setTransform(value))
}

class SwitchSettingViewModel(
    model: SettingModel<Boolean>,
    setting: SettingsAccess.Setting<Boolean>,
) : SettingViewModel<Boolean>(model, NoTransformSetting(setting)) {
    override val type: Type = Type.SWITCH
}

abstract class TextFieldSettingViewModel(
    model: SettingModel<*>,
    transformedSetting: TransformedSetting<*, String>,
) : SettingViewModel<String>(model, transformedSetting) {
    override val type: Type = Type.TEXT_FIELD

    abstract val keyboardType: KeyboardType

    abstract fun inputTransform(input: String): String
    abstract fun errorMessageForInput(input: String): String?

    override var currentEntry: String
        get() = super.currentEntry
        set(value) {
            errorMessage = errorMessageForInput(value)
            super.currentEntry = inputTransform(value)
        }

    var errorMessage: String? = null
        private set

    enum class KeyboardType {
        TEXT, NUMERIC, NUMERIC_DECIMAL
    }
}

class StringContentTextFieldSettingViewModel(
    model: SettingModel<String>,
    setting: SettingsAccess.Setting<String>
) : TextFieldSettingViewModel(model, NoTransformSetting(setting)) {
    init {
        require(model.options is SettingModel.SettingOptions.Alphanumeric)
    }

    override val keyboardType: KeyboardType = KeyboardType.TEXT

    private val options inline get() = model.options as SettingModel.SettingOptions.Alphanumeric

    override fun inputTransform(input: String): String =
        options.excludedConditions.fold(input) { string, condition ->
            if (condition.matches(string)) condition.sanitize(string) else string
        }

    override fun errorMessageForInput(input: String): String? {
        for (option in options.excludedConditions) option.errorMessage(input).let {
            if (it != null) return it
        }

        return null
    }
}

private class NumericErrorMessageDelegate<T : Comparable<T>>(
    allowedRanges: Iterable<ClosedRange<T>>,
    private val firstAndLastString: ClosedRange<T>.() -> String,
) {
    private val sanitizedRanges = rangeJoin(allowedRanges)

    operator fun invoke(input: T?): String? = if (input == null) {
        "Enter a number."
    } else {
        if (sanitizedRanges.any { input in it }) {
            null
        } else {
            "Number must be between ${
                when (sanitizedRanges.size) {
                    1 -> sanitizedRanges.single().firstAndLastString()
                    2 -> "${
                        sanitizedRanges.first().firstAndLastString()
                    } or ${
                        sanitizedRanges.last().firstAndLastString()
                    }"
                    else -> sanitizedRanges.dropLast(1).joinToString { "${it.firstAndLastString()}, " } +
                            sanitizedRanges.last().let { "or ${it.firstAndLastString()}" }
                }
            }."
        }
    }

}

class IntegerContentTextFieldSettingViewModel(
    model: SettingModel<Int>,
    setting: SettingsAccess.Setting<Int>,
) : TextFieldSettingViewModel(
    model,
    CustomTransformedSetting(
        setting,
        getTransform = Int::toString,
        setTransform = String::toInt
    )
) {
    init {
        require(model.options is SettingModel.SettingOptions.Integer)
    }

    override val keyboardType: KeyboardType = KeyboardType.NUMERIC

    private val allowedRanges: Iterable<IntRange> inline get() = (model.options as SettingModel.SettingOptions.Integer).includedRanges

    private val _errorMessageForInput = NumericErrorMessageDelegate(allowedRanges) { "$start and $endInclusive" }
    override fun errorMessageForInput(input: String): String? = _errorMessageForInput(input.toIntOrNull())

    override fun inputTransform(input: String): String = input.filter { it.isDigit() }
}

class FloatContentTextFieldSettingViewModel(
    model: SettingModel<Double>,
    setting: SettingsAccess.Setting<Double>,
) : TextFieldSettingViewModel(
    model,
    CustomTransformedSetting(
        setting,
        getTransform = Double::toString,
        setTransform = String::toDouble
    )
) {
    init {
        require(model.options is SettingModel.SettingOptions.FloatingPoint)
    }

    override val keyboardType: KeyboardType = KeyboardType.NUMERIC_DECIMAL

    private val allowedRanges: Iterable<ClosedFloatingPointRange<Double>> inline get() = (model.options as SettingModel.SettingOptions.FloatingPoint).includedRanges

    override fun inputTransform(input: String): String = input.filter { it.isDigit() || it in ",." }

    private val _errorMessageForInput =
        NumericErrorMessageDelegate(allowedRanges) { "${start.stringRound(2)} and ${endInclusive.stringRound(2)}" }

    override fun errorMessageForInput(input: String): String? = _errorMessageForInput(input.toDoubleOrNull())

    private inline fun Double.stringRound(decimalPlaces: Int): String = if (decimalPlaces < 1) "" else
        round(10.0.pow(decimalPlaces) * this).toString().toMutableList()
            .let { it.add(it.lastIndex - decimalPlaces, '.') }.toString()
}

class PickerSettingViewModel<T>(
    model: SettingModel<MultipleChoiceOption<T>>,
    setting: SettingsAccess.Setting<MultipleChoiceOption<T>>,
) : SettingViewModel<MultipleChoiceOption<T>>(model, NoTransformSetting(setting)) {
    init {
        require(model.options is SettingModel.SettingOptions.MultipleChoice<T>)
    }

    override val type: Type = Type.PICKER

    override var currentEntry: MultipleChoiceOption<T> by observationHandler.published(setting.get(),
        set = { value ->
            field = value
            setting.set(field)
        }
    )

    private inline val options: SettingModel.SettingOptions.MultipleChoice<T>
        inline get() = model.options as SettingModel.SettingOptions.MultipleChoice<T>

    val possibleSelections: List<MultipleChoiceOption<T>> get() = options.options.toList()
}

fun <T : Comparable<T>> rangeJoin(ranges: Iterable<ClosedRange<T>>): List<ClosedRange<T>> { //TODO: Test
    @Suppress("SpellCheckingInspection")
    val unjoined: List<ClosedRange<T>> = ranges.filter {
        !it.isEmpty()
    }.sortedBy {
        it.start
    }

    if (unjoined.isEmpty()) return emptyList()

    val joinedRanges = mutableListOf<ClosedRange<T>>()
    var acc: ClosedRange<T>? = null
    for (range in unjoined) {
        if (acc == null) {
            acc = range
            continue
        }

        acc = if (acc.endInclusive >= range.start) {
            acc.start..range.endInclusive
        } else {
            joinedRanges.add(acc)
            null
        }
    }

    if (acc != null) joinedRanges.add(acc)

    return joinedRanges
}