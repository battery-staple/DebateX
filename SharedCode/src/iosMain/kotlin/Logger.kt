package com.rohengiralt.debatex

import kotlinx.cinterop.ptr
import platform.darwin.*

//https://github.com/JetBrains/kotlin-native/issues/2989
actual class Logger actual constructor(private val className: String) {

    @Suppress("NOTHING_TO_INLINE")
    actual inline fun info(msg: String): Unit =
        _os_log_internal(
            __dso_handle.ptr,
            OS_LOG_DEFAULT,
            OS_LOG_TYPE_INFO,
            transform(msg)
        )

    @Suppress("NOTHING_TO_INLINE")
    actual inline fun warn(msg: String): Unit =
        _os_log_internal(
            __dso_handle.ptr,
            OS_LOG_DEFAULT,
            OS_LOG_TYPE_INFO,
            transform("WARNING: $msg") // TODO: Better alternative for Warning level?
        )

    @Suppress("NOTHING_TO_INLINE")
    actual inline fun error(msg: String): Unit =
        _os_log_internal(
            __dso_handle.ptr,
            OS_LOG_DEFAULT,
            OS_LOG_TYPE_ERROR,
            transform(msg)
        )

    @Suppress("NOTHING_TO_INLINE")
    actual inline fun debug(msg: String): Unit =
        _os_log_internal(
            __dso_handle.ptr,
            OS_LOG_DEFAULT,
            OS_LOG_TYPE_DEBUG,
            transform(msg)
        )

    @Suppress("NOTHING_TO_INLINE")
    @PublishedApi
    internal fun transform(msg: String): String =
        "$className: $msg"

}
