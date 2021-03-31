package com.rohengiralt.debatex.util

/**
 * Copied from https://github.com/Kotlin/kotlinx-io/blob/master/core/commonMain/src/kotlinx/io/Closeable.common.kt
 * Remove if dependency on kotlinx.io is ever added.
 */


/**
 * Closeable resource.
 */
expect interface Closeable {
    fun close()
}

/**
 * Executes the given [block] function on this resource and then closes it down correctly whether an exception
 * is thrown or not.
 *
 * @param block a function to process this [Closeable] resource.
 * @return the result of [block] function invoked on this resource.
 */
inline fun <C : Closeable, R> C.use(block: (C) -> R): R {
    var closed = false

    return try {
        block(this)
    } catch (first: Throwable) {
        try {
            closed = true
            close()
        } catch (second: Throwable) {
            first.addSuppressedInternal(second)
        }

        throw first
    } finally {
        if (!closed) {
            close()
        }
    }
}

@PublishedApi
internal expect fun Throwable.addSuppressedInternal(other: Throwable)