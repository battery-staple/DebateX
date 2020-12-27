package com.rohengiralt.debatex.dataStructure.color

import com.rohengiralt.debatex.dataStructure.DataStructure
import kotlin.reflect.KClass

interface Color : DataStructure {
    val representation: ColorRepresentation

    fun <T : ColorRepresentation> representationAs(clazz: KClass<T>): T
}

inline fun <reified T : ColorRepresentation> Color.representationAs(): T =
    representationAs(T::class)

inline fun <reified T : ColorRepresentation, reified R> Color.withRepresentationAs(block: T.() -> R): R =
    with(representationAs(), block)

@Suppress("UNUSED")
inline fun <reified T : ColorRepresentation> Color.withRepresentationAs(block: T.() -> T): T =
    withRepresentationAs<T, T>(block)