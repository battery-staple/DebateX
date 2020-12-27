package com.rohengiralt.debatex

actual class Logger actual constructor(private val className: String) {
    @Suppress("NOTHING_TO_INLINE")
    actual inline fun info(msg: String) {
        println("[INFO] $msg")
    }

    @Suppress("NOTHING_TO_INLINE")
    actual inline fun warn(msg: String) {
        println("[WARNING] $msg")
    }

    @Suppress("NOTHING_TO_INLINE")
    actual inline fun error(msg: String) {
        println("[ERROR] $msg")
    }

    @Suppress("NOTHING_TO_INLINE")
    actual inline fun debug(msg: String) {
        println("[DEBUG] $msg")
    }
}