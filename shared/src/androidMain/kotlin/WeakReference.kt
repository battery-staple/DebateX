package com.rohengiralt.debatex

import java.lang.ref.WeakReference

actual class WeakReference<T : Any> actual constructor(referred: T) {
    private val reference = WeakReference(referred)
    actual val value get() = reference.get()
}
