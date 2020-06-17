//package com.rohengiralt.debatex.dataStructures.color
//
//import kotlin.math.abs
//
//internal object ColorRepresentationConverter {
//    @Suppress("FunctionName")
//    fun HSVtoRGB(
//        hue: Double,
//        saturation: Double,
//        value: Double
//    ): Triple<Double, Double, Double> {
//        val hue360 = hue * 360
//        val chroma = value * saturation
//        val m = value - chroma
//        val x = chroma * (1 - abs((hue360 / 60) % 2 - 1))
//        return when (hue360) {
//            in 0.0..60.0 -> Triple(chroma, x, 0.0)
//            in 60.0..120.0 -> Triple(x, chroma, 0.0)
//            in 120.0..180.0 -> Triple(0.0, chroma, x)
//            in 180.0..240.0 -> Triple(0.0, x, chroma)
//            in 240.0..300.0 -> Triple(x, 0.0, chroma)
//            in 300.0..360.0 -> Triple(chroma, 0.0, x)
//            else -> throw IllegalArgumentException("Hue must be between zero and one, not $hue")
//        }.let { Triple(it.first + m, it.second + m, it.third + m) }
//    }
//}