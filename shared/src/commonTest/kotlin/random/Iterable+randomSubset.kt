package com.rohengiralt.debatex.random

import kotlin.random.Random
import kotlin.random.nextInt

fun <T> Iterable<T>.randomSubset(random: Random): List<T> =
    shuffled(random)
        .run { take(random.nextInt(0 until size)) }