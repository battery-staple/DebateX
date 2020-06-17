package com.rohengiralt.debatex.dataStructures.text

import com.rohengiralt.debatex.dataStructures.Size
import kotlinx.serialization.Serializable

@Serializable
data class Text(
    val rawText: String,
    val font: Font = Font.System,
    val size: Size? = null
)