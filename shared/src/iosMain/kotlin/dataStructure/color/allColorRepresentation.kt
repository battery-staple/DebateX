package com.rohengiralt.debatex.dataStructure.color

import com.rohengiralt.debatex.Logger
import kotlin.reflect.KClass

val logger: Logger by lazy { Logger("allColorRepresentations") }

internal actual val allColorRepresentations: List<KClass<out ColorRepresentation>> =
    listOf(
        ColorRepresentation.RGB::class,
//        ColorRepresentation.HSV::class,
        ColorRepresentation.HSL::class
    ).also {
        logger.warn(
            "The list of all color representations cannot be guaranteed complete in Kotlin/Native due to the lack of complete reflection. " +
                    "Ensure it is up to date before running."
        )
    }
