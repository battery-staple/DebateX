package com.rohengiralt.debatex

import com.rohengiralt.debatex.dataStructure.color.Color
import com.rohengiralt.debatex.dataStructure.color.ColorRepresentation
import com.rohengiralt.debatex.dataStructure.color.representationAs

fun asRGB(color: Color): ColorRepresentation.RGB = color.representationAs()