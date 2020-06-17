package com.rohengiralt.debatex

fun approximatelyEquals(value1: Double, value2: Double, error: Double = 0.1): Boolean {
    if (error < 0.0) {
        throw IllegalArgumentException("Error must be nonnegative.")
    }
    return value1 in (value2 - error)..(value2 + error)
}