package com.rohengiralt.debatex.dataStructure.color

import kotlin.reflect.KClass

internal actual val allColorRepresentations: List<KClass<out ColorRepresentation>> =
    ColorRepresentation::class.sealedSubclasses