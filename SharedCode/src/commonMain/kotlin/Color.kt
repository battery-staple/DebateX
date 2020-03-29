package com.jetbrains.debatex

import kotlin.math.abs

@ExperimentalUnsignedTypes
data class Color(
    var alpha: Double = 1.0,
    var red: Double,
    var green: Double,
    var blue: Double
) {

    private val greatestRGB
        get() = maxOf(red, green, blue)

    private val leastRGB
        get() = minOf(red, green, blue)

    private val rangeRGB
        get() = greatestRGB - leastRGB

    val chroma
        get() = value * saturation

    var value: Double
        set(_value) {
            val (r, g, b) = HSVtoRGB(hue, saturation, _value)
            red = r
            green = g
            blue = b
            println("value set to $_value and is now $value")
        }
        get() = greatestRGB

    var saturation: Double
        set(_value) {
            val (r, g, b) = HSVtoRGB(hue, _value, value)
            red = r
            green = g
            blue = b
        }
        get() = if (greatestRGB != 0.0) {
            rangeRGB / greatestRGB
        } else {
            0.0
        }

    var hue: Double
        set(_value) {
            val (r, g, b) = HSVtoRGB(_value, saturation, value)
            red = r
            green = g
            blue = b
        }
        get() = if (rangeRGB == 0.0) 0.0 else when (greatestRGB) {
            red -> (((green - blue) / rangeRGB) + 6) % 6
            green -> ((blue - red) / rangeRGB) + 2
            blue -> ((red - green) / rangeRGB) + 4
            else -> throw IllegalStateException("Color mutated while calculating hue.")
        } / 6

    companion object {
        fun HSVtoRGB(
            hue: Double,
            saturation: Double,
            value: Double
        ): Triple<Double, Double, Double> {
            if (hue !in 0.0..1.0) {
                throw IllegalArgumentException("Hue must be between 0 and 1")
            }
            if (saturation !in 0.0..1.0) {
                throw IllegalArgumentException("Saturation must be between 0 and 1")
            }
            if (value !in 0.0..1.0) {
                throw IllegalArgumentException("Value must be between 0 and 1")
            }

            val chroma = value * saturation
            val x = chroma * (1 - abs((hue * 6) % 2 - 1))
            val m = value - chroma
            return when (hue * 6) {
                in 0.0..1.0 -> Triple(chroma, x, 0.0)
                in 1.0..2.0 -> Triple(x, chroma, 0.0)
                in 2.0..3.0 -> Triple(0.0, chroma, x)
                in 3.0..4.0 -> Triple(0.0, x, chroma)
                in 4.0..5.0 -> Triple(x, 0.0, chroma)
                in 5.0..6.0 -> Triple(chroma, 0.0, x)
                else -> throw xkcdException() //TODO: Throw a real exception
            }.let { Triple(it.first + m, it.second + m, it.third + m) }
        }
    }
}