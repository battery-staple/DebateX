package com.rohengiralt.debatex.util

actual interface Closeable {
    actual fun close()
}

@PublishedApi
internal actual fun Throwable.addSuppressedInternal(other: Throwable) {
}
