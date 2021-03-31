@file:JvmName("AndroidCloseable") //TODO: remove workaround when fixed

package com.rohengiralt.debatex.util

import java.io.Closeable
import java.lang.reflect.Method

actual typealias Closeable = Closeable

@PublishedApi
internal actual fun Throwable.addSuppressedInternal(other: Throwable) {
    AddSuppressedMethod?.invoke(this, other)
}

private val AddSuppressedMethod: Method? by lazy {
    try {
        Throwable::class.java.getMethod("addSuppressed", Throwable::class.java)
    } catch (t: Throwable) {
        null
    }
}