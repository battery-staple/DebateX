@file:Suppress("UNUSED")

package com.rohengiralt.debatex.random

import kotlin.random.Random

fun <T> randomList(size: Int, unique: Boolean, random: Random, nextT: Random.() -> T): List<T> {
    require(size >= 0) { "Size must be greater or equal than 0" }
    return if (unique) {
        mutableSetOf<T>()
    } else {
        mutableListOf<T>()
    }
        .apply {
            @Suppress("ControlFlowWithEmptyBody", "Reformat")
            while (this.size < size && add(nextT(random))) {
            }
        }
        .toList()
}

fun randomIntList(size: Int, unique: Boolean = false, random: Random = Random(0)): List<Int> =
    randomList(size, unique, random, Random::nextInt)

fun randomIntList(size: Int, from: Int, until: Int, unique: Boolean = false, random: Random = Random(0)): List<Int> =
    randomList(size, unique, random) { nextInt(from, until) }

fun randomLongList(size: Int, unique: Boolean = false, random: Random = Random(0)): List<Long> =
    randomList(size, unique, random, Random::nextLong)

fun randomLongList(
    size: Int,
    from: Long,
    until: Long,
    unique: Boolean = false,
    random: Random = Random(0),
): List<Long> =
    randomList(size, unique, random) { nextLong(from, until) }

fun randomDoubleList(size: Int, unique: Boolean = false, random: Random = Random(0)): List<Double> =
    randomList(size, unique, random, Random::nextDouble)

fun randomDoubleList(
    size: Int,
    from: Double,
    until: Double,
    unique: Boolean = false,
    random: Random = Random(0),
): List<Double> =
    randomList(size, unique, random) { nextDouble(from, until) }

fun randomFloatList(size: Int, unique: Boolean = false, random: Random = Random(0)): List<Float> =
    randomList(size, unique, random, Random::nextFloat)

fun randomBooleanList(size: Int, unique: Boolean = false, random: Random = Random(0)): List<Boolean> =
    randomList(size, unique, random, Random::nextBoolean)

fun randomStringList(
    size: Int,
    fromLength: Int,
    untilLength: Int,
    unique: Boolean = false,
    allowedCharacters: Set<Char>? = null,
    random: Random = Random(0),
): List<String> =
    randomList(size, unique, random) {
        nextString(
            nextInt(fromLength, untilLength),
            allowedCharacters,
            unique
        )
    }

fun randomAnyList(size: Int, unique: Boolean = false, random: Random = Random(0)): List<Any> =
    randomList(size, unique, random, Random::nextAny)

fun <T> List<T>.withRandomNulls(nulls: Int, random: Random = Random(0)): List<T?> {
    val newList = mutableListOf<T?>()
    val indicesToReplace = randomIntList(nulls, from = 0, until = size, unique = true, random).toSet()
    forEachIndexed { index, element ->
        newList.add(if (index in indicesToReplace) null else element)
    }

    return newList
}

fun <T> List<T>.withRandomNulls(proportion: Double, random: Random = Random(0)): List<T?> =
    withRandomNulls((size * proportion).toInt(), random)