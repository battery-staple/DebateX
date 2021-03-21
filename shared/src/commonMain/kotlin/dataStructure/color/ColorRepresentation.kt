package com.rohengiralt.debatex.dataStructure.color

import com.rohengiralt.debatex.loggerForClass
import com.rohengiralt.debatex.xkcdError
//import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.reflect.KClass

@Suppress("Unused")
inline fun <reified T : ColorRepresentation> ColorRepresentation.coerce(): T = coerce(T::class)

//@Serializable
sealed class ColorRepresentation {
    //@Serializable
    data class RGB(
        val red: Double,
        val green: Double,
        val blue: Double,
        override val alpha: Double = 1.0
    ) : ColorRepresentation() {

        init {
            require(red in 0.0..1.0) { "Red value must be between zero and one." }
            require(green in 0.0..1.0) { "Green value must be between zero and one." }
            require(blue in 0.0..1.0) { "Blue value must be between zero and one." }
            require(alpha in 0.0..1.0) { "Alpha value must be between zero and one." }
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ColorRepresentation> coerce(representation: KClass<T>): T =
            when (representation) {
                RGB::class -> this
                HSV::class -> {
                    val greatest = maxOf(red, green, blue)
                    val range = greatest - minOf(red, green, blue)

                    HSV(
                        alpha = alpha,
                        hue = (when (greatest) {
                            red -> (((green - blue) / range) + 6) % 6
                            green -> ((blue - red) / range) + 2
                            blue -> ((red - green) / range) + 4
                            else -> throw xkcdError
                        } / 6)
                            .let {
                                if (it.isFinite()) it else 0.0
                            },
                        saturation = (range / greatest)
                            .let {
                                if (it.isFinite()) it else 0.0
                            },
                        value = greatest
                    )
                }
                HSL::class -> {
                    val greatest = maxOf(red, green, blue)
                    val least = minOf(red, green, blue)
                    val range = greatest - least

                    HSL(
                        alpha = alpha,
                        hue = (when (greatest) {
                            red -> (((green - blue) / range) + 6) % 6
                            green -> ((blue - red) / range) + 2
                            blue -> ((red - green) / range) + 4
                            else -> throw xkcdError
                        } / 6)
                            .let {
                                if (it.isFinite()) it else 0.0
                            },
                        saturation = (range / (1 - abs(greatest + least - 1)))
                            .let {
                                if (it.isFinite()) it else 0.0
                            },
                        lightness = (greatest + least) / 2
                    )
                }
                ColorRepresentation::class -> this
                else -> {
                    logger.error("ColorRepresentation RGB was unable to convert to ${representation.simpleName}")
                    throw IllegalArgumentException("Cannot convert from RGB to ${representation.simpleName}")
                }
            } as T

        companion object {
            private val logger by lazy { loggerForClass<RGB>() }
        }
    }

    //@Serializable
    data class HSV(
        val hue: Double,
        val saturation: Double,
        val value: Double,
        override val alpha: Double = 1.0
    ) : ColorRepresentation() {

        init {
            require(hue in 0.0..1.0) { "Hue value must be between zero and one." }
            require(saturation in 0.0..1.0) { "Saturation value must be between zero and one." }
            require(value in 0.0..1.0) { "Value value must be between zero and one." }
            require(alpha in 0.0..1.0) { "Alpha value must be between zero and one." }
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ColorRepresentation> coerce(representation: KClass<T>): T =
            when (representation) {
                HSV::class -> this
                RGB::class -> {
                    val hue360 = hue * 360
                    val chroma = value * saturation
                    val m = value - chroma
                    val x = chroma * (1 - abs((hue360 / 60) % 2 - 1))
                    when (hue360) {
                        in 0.0..60.0 -> Triple(chroma, x, 0.0)
                        in 60.0..120.0 -> Triple(x, chroma, 0.0)
                        in 120.0..180.0 -> Triple(0.0, chroma, x)
                        in 180.0..240.0 -> Triple(0.0, x, chroma)
                        in 240.0..300.0 -> Triple(x, 0.0, chroma)
                        in 300.0..360.0 -> Triple(chroma, 0.0, x)
                        else -> throw IllegalArgumentException("Hue must be between zero and one, not $hue")
                    }.let {
                        RGB(
                            alpha = alpha,
                            red = it.first + m,
                            green = it.second + m,
                            blue = it.third + m
                        )
                    }
                }
                HSL::class -> {
                    val lightness = value * (1 - saturation / 2)

                    HSL(
                        alpha = alpha,
                        hue = hue,
                        saturation = ((value - lightness) / (minOf(lightness, 1.0 - lightness)))
                            .let {
                                if (it.isFinite()) it else 0.0
                            },
                        lightness = value * (1 - saturation / 2)
                    )
                }
                ColorRepresentation::class -> this
                else -> {
                    logger.error("ColorRepresentation HSV was unable to convert to ${representation.simpleName}")
                    throw IllegalArgumentException("Cannot convert from HSV to ${representation.simpleName}")
                }
            } as T

        companion object {
            private val logger by lazy { loggerForClass<HSV>() }
        }
    }

    //@Serializable
    data class HSL(
        val hue: Double,
        val saturation: Double,
        val lightness: Double,
        override val alpha: Double = 1.0
    ) : ColorRepresentation() {

        init {
            require(hue in 0.0..1.0) { "Hue value must be between zero and one." }
            require(saturation in 0.0..1.0) { "Saturation value must be between zero and one." }
            require(lightness in 0.0..1.0) { "Lightness value must be between zero and one." }
            require(alpha in 0.0..1.0) { "Alpha value must be between zero and one." }
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ColorRepresentation> coerce(representation: KClass<T>): T =
            when (representation) {
                HSL::class -> this
                RGB::class -> {
                    val hue360 = hue * 360
                    val chroma = (1 - abs(2 * lightness - 1)) * saturation
                    val m = lightness / 2.0
                    val x = chroma * (1 - abs((hue360 / 60) % 2 - 1))
                    when (hue360) {
                        in 0.0..60.0 -> Triple(chroma, x, 0.0)
                        in 60.0..120.0 -> Triple(x, chroma, 0.0)
                        in 120.0..180.0 -> Triple(0.0, chroma, x)
                        in 180.0..240.0 -> Triple(0.0, x, chroma)
                        in 240.0..300.0 -> Triple(x, 0.0, chroma)
                        in 300.0..360.0 -> Triple(chroma, 0.0, x)
                        else -> throw IllegalArgumentException("Hue must be between zero and one, not $hue")
                    }.let {
                        RGB(it.first + m, it.second + m, it.third + m, alpha)
                    }
                }
                HSV::class -> {
                    val value = lightness + saturation * minOf(lightness, 1 - lightness)

                    HSV(
                        alpha = alpha,
                        hue = hue,
                        saturation = (2 * (1 - lightness / value))
                            .let {
                                if (it.isFinite()) it else 0.0
                            },
                        value = value
                    )
                }
                ColorRepresentation::class -> this
                else -> {
                    logger.error("ColorRepresentation HSL was unable to convert to ${representation.simpleName}")
                    throw IllegalArgumentException("Cannot convert from HSL to ${representation.simpleName}")
                }
            } as T

        companion object {
            private val logger by lazy { loggerForClass<HSL>() }
        }
    }

    abstract fun <T : ColorRepresentation> coerce(representation: KClass<T>): T
    abstract val alpha: Double
}

inline fun <reified T : ColorRepresentation> colorRepresentationFromHexString(
    hexString: String
): T {
    val trimmedString = hexString.trimStart('#')
    return when (trimmedString.length) {
        6 -> ColorRepresentation.RGB(
            red = trimmedString.decodeSubstringHex(0..1) / 255,
            green = trimmedString.decodeSubstringHex(2..3) / 255,
            blue = trimmedString.decodeSubstringHex(4..5) / 255,
            alpha = 1.0
        )
        8 -> ColorRepresentation.RGB(
            alpha = trimmedString.decodeSubstringHex(0..1) / 255,
            red = trimmedString.decodeSubstringHex(2..3) / 255,
            green = trimmedString.decodeSubstringHex(4..5) / 255,
            blue = trimmedString.decodeSubstringHex(6..7) / 255
        )
        else -> throw IllegalArgumentException("\"$hexString\" is not a valid hex string.")
    }.coerce()
}

@PublishedApi
internal fun String.decodeSubstringHex(range: IntRange): Double = substring(range).toDouble(16)

@PublishedApi
internal fun String.toDouble(radix: Int): Double = toLong(radix).toDouble()

internal expect val allColorRepresentations: List<KClass<out ColorRepresentation>>

//internal val colorRepresentationConstructors: Map<KClass<out ColorRepresentation>, (Double, Double, Double, Double) -> ColorRepresentation> =
//    mutableMapOf(
//        ColorRepresentation.RGB::class to { red, green, blue, alpha ->
//            ColorRepresentation.RGB(red, green, blue, alpha)
//        },
//        ColorRepresentation.HSV::class to { hue, saturation, value, alpha ->
//            ColorRepresentation.HSV(hue, saturation, value, alpha)
//        },
//        ColorRepresentation.HSL::class to { hue, saturation, lightness, alpha ->
//            ColorRepresentation.HSL(hue, saturation, lightness, alpha)
//        }
//    )