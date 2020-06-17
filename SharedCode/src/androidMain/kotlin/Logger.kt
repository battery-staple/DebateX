package com.rohengiralt.debatex

actual class Logger actual constructor(private val className: String) {
    @Suppress("NOTHING_TO_INLINE")
    actual inline fun info(msg: String) {
        println("[info] $msg")
    }

    @Suppress("NOTHING_TO_INLINE")
    actual inline fun warn(msg: String) {
        println("[warn] $msg")
    }

    @Suppress("NOTHING_TO_INLINE")
    actual inline fun error(msg: String) {
        println("[error] $msg")
    }

    @Suppress("NOTHING_TO_INLINE")
    actual inline fun debug(msg: String) {
        println("[debug] $msg")
    }
}