@file:Suppress("UNUSED")

package com.rohengiralt.debatex

import kotlin.reflect.KClass

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any> loggerForClass(clazz: KClass<out T>, customName: String? = null): Logger =
    Logger((clazz.qualifiedName ?: "Anonymous class") + (customName ?: ""))

inline fun <reified T : Any> loggerForClass(customName: String? = null): Logger =
    loggerForClass(T::class, customName)

inline fun <reified T : Any> T.defaultLogger(customName: String? = null): Logger =
    loggerForClass<T>(customName)

val <reified T : Any> T.defaultLogger: Logger inline get() = defaultLogger()

expect class Logger(className: String) {
    inline fun info(msg: String)
    inline fun warn(msg: String)
    inline fun error(msg: String)
    inline fun debug(msg: String)
}

inline fun Logger.info(msg: () -> String): Unit = info(msg())
inline fun Logger.warn(msg: () -> String): Unit = warn(msg())
inline fun Logger.error(msg: () -> String): Unit = error(msg())
inline fun Logger.debug(msg: () -> String): Unit = debug(msg())