package com.rohengiralt.debatex.util

inline fun <S, T : S> Iterable<T>.reduceWhileNotNull(
    operation: (acc: S, T) -> S?
): Pair<S, Iterable<S>?> {
    val iterator = this.iterator()
    if (!iterator.hasNext()) throw UnsupportedOperationException("Empty collection can't be reduced.")
    var accumulator: S = iterator.next()
    val failedValue: T = run {
        while (iterator.hasNext()) {
            val next = iterator.next()
            accumulator = operation(accumulator, next) ?: return@run next
        }
        return@run null
    } ?: return accumulator to null

    val leftover = mutableListOf<S>()
    leftover.add(failedValue)
    while (iterator.hasNext()) {
        leftover.add(iterator.next())
    }
    return accumulator to leftover
}