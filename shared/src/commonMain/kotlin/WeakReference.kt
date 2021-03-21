package com.rohengiralt.debatex

expect class WeakReference<T : Any>(referred: T) {
    val value: T?
}