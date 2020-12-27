package com.rohengiralt.debatex.dataStructure.text

import com.rohengiralt.debatex.dataStructure.LinearFixedSize
import com.rohengiralt.debatex.dataStructure.Size
//import kotlinx.serialization.Serializable

//@Serializable
data class Text(
    val rawText: String,
    val font: Font = Font.System,
    val fontWeight: FontWeight = FontWeight.Regular,
    val alignment: Alignment = Alignment.Left,
    val height: LinearFixedSize<Size.ScreenDimension.OrientationBased.Vertical, *>?
)

fun textSize(
    screenProportion: Double,
    screenDimension: Size.ScreenDimension
) = LinearFixedSize(
    screenProportion,
    Size.ScreenDimension.OrientationBased.Vertical,
    screenDimension
)