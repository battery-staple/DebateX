package com.rohengiralt.debatex.random

import kotlin.random.Random

@Suppress("RemoveExplicitTypeArguments")
fun Random.nextAny(): Any =
    listOf<() -> Any>(
        ::nextInt,
        ::nextLong,
        ::nextBoolean,
        ::nextDouble,
        ::nextFloat,
        ::nextChar,
        { nextString(nextInt(0, 100)) },
    )
        .random(this)
        .invoke()


fun Random.nextChar(): Char = nextInt(from = Char.MIN_VALUE.toInt(), until = Char.MAX_VALUE.toInt() + 1).toChar()

fun Random.nextString(length: Int, allowedCharacters: Set<Char>? = null, uniqueCharacters: Boolean = false): String {
    val allowedCharactersList = allowedCharacters?.toList()

    @Suppress("RemoveExplicitTypeArguments")
    return randomList<Char>(length, uniqueCharacters, this) {
        allowedCharactersList?.get(
            nextInt(until = allowedCharactersList.size)
        ) ?: nextChar()
    }.joinToString("")
}