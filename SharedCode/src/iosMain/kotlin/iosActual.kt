package com.rohengiralt.debatex

import com.rohengiralt.debatex.dataStructures.color.Color
import com.rohengiralt.debatex.dataStructures.color.ColorRepresentation
import com.rohengiralt.debatex.dataStructures.color.representationAs
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.getOriginalKotlinClass
import platform.UIKit.UIDevice


fun platformName(): String {
    return UIDevice.currentDevice.systemName() +
            " " +
            UIDevice.currentDevice.systemVersion
}

fun kotlinClass(from: ObjCClass) =
    getOriginalKotlinClass(from)

fun asRGB(color: Color): ColorRepresentation.RGB = color.representationAs()