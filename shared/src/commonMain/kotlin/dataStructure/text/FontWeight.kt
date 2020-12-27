package com.rohengiralt.debatex.dataStructure.text

enum class FontWeight {
    Thinnest, VeryThin, Thin, Regular, SlightlyBold, MediumBold, Bold, VeryBold, Boldest;

    open val numericWeight: Int get() = TODO()

//    fun closestTo(possibleSizes: Array<FontWeight>): FontWeight = TODO()
}