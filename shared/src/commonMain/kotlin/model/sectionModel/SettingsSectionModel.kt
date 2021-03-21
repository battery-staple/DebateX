package com.rohengiralt.debatex.model.sectionModel

import com.rohengiralt.debatex.util.serializers.IntRangeSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlin.Int.Companion.MAX_VALUE as INT_MAX //IntelliJ complains if not imported
import kotlin.Int.Companion.MIN_VALUE as INT_MIN

data class SettingsSectionModel(val settings: Iterable<SettingModel<*>>) {
    constructor(vararg settings: SettingModel<*>) : this(settings.toList())
}

@Serializable
data class SettingModel<T>(
    val name: String,
    val sectionName: String?,
    val options: SettingOptions<T>,
) {
    @Serializable
    sealed class SettingOptions<T> {
        abstract val defaultValue: T

        @Serializable
        data class Integer(
            val includedRanges: Iterable<@Serializable(with = IntRangeSerializer::class) IntRange> = listOf(INT_MIN..INT_MAX),
            override val defaultValue: Int,
        ) : SettingOptions<Int>() {
            init {
                require(includedRanges.all { it.step == 1 }) { "Step must be one." }
            }
        }

        @Serializable
        data class FloatingPoint(
            val includedRanges: Iterable<ClosedFloatingPointRange<Double>>,
            override val defaultValue: Double,
        ) : SettingOptions<Double>()

        data class Alphanumeric(val excludedConditions: List<Condition>, override val defaultValue: String) :
            SettingOptions<String>() { //TODO: be more specific about conditions
            fun String.matches(condition: Condition): kotlin.Boolean = condition.matches(this)

            interface Condition {
                fun matches(string: String): kotlin.Boolean
                fun sanitize(string: String): String
                fun errorMessage(string: String): String?
            }
        }

        @Serializable
        data class Boolean(override val defaultValue: kotlin.Boolean) : SettingOptions<kotlin.Boolean>()

        @Serializable
        data class MultipleChoice<T>(
            val options: Collection<MultipleChoiceOption<T>>,
            override val defaultValue: MultipleChoiceOption<T>,
            val serializer: KSerializer<T>,
        ) : SettingOptions<MultipleChoice.MultipleChoiceOption<T>>() {
            constructor(options: List<MultipleChoiceOption<T>>, initialIndex: Int, serializer: KSerializer<T>) :
                    this(options, options[initialIndex], serializer)

            constructor(
                vararg options: MultipleChoiceOption<T>,
                initialValue: MultipleChoiceOption<T>,
                serializer: KSerializer<T>
            ) : this(options.toList(), initialValue, serializer)

            constructor(vararg options: MultipleChoiceOption<T>, initialIndex: Int, serializer: KSerializer<T>) :
                    this(options.toList(), initialIndex, serializer)

            init {
                require(defaultValue in options) { "initialValue must be in options." }
            }

            @Serializable
            data class MultipleChoiceOption<T>(val name: String, val option: T)
        }
    }
}