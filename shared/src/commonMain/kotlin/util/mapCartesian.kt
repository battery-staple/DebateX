package com.rohengiralt.debatex.util

fun <T, R, C : MutableCollection<in R>> Iterable<T>.mapCartesianTo(
    destination: C,
    other: Iterable<T> = this,
    transform: (T, T) -> R
): C {
    for (e1 in this) {
        for (e2 in other) {
            destination.add(
                transform(e1, e2)
            )
        }
    }

    return destination
}

fun <T, R> Iterable<T>.mapCartesian(
    other: Iterable<T> = this,
    transform: (T, T) -> R
): List<R> = mapCartesianTo(ArrayList(), other, transform)