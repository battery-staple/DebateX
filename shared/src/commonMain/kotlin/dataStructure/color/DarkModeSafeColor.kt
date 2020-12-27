package com.rohengiralt.debatex.dataStructure.color

import kotlin.reflect.KClass

@Suppress("UNUSED")
data class DarkModeSafeColor(
    val lightModeColor: SingleColor,
    val darkModeColor: SingleColor,
    var mode: LightnessMode
) : Color
//by currentValidColor(lightModeColor, darkModeColor, mode) TODO: Would this work?
{

    private val currentValidColor: SingleColor
        get() = currentValidColor(
            lightModeColor,
            darkModeColor,
            mode
        )

    override val representation: ColorRepresentation
        get() = currentValidColor.representation

    override fun <T : ColorRepresentation> representationAs(clazz: KClass<T>): T =
        currentValidColor.representationAs(clazz)
}

enum class LightnessMode {
    Dark, Light
}

private fun currentValidColor(
    lightModeColor: SingleColor,
    darkModeColor: SingleColor,
    mode: LightnessMode
): SingleColor = when (mode) {
    LightnessMode.Dark -> darkModeColor
    LightnessMode.Light -> lightModeColor
}