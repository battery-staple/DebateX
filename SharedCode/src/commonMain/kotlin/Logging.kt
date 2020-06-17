package com.rohengiralt.debatex

inline fun <reified T> loggerForClass(customName: String? = null): Logger =
    Logger((T::class.qualifiedName ?: "Anonymous class") + (customName ?: ""))

inline fun <reified T> T.defaultLogger(customName: String? = null): Logger = loggerForClass<T>()

expect class Logger(className: String) {
    inline fun info(msg: String)
    inline fun warn(msg: String)
    inline fun error(msg: String)
    inline fun debug(msg: String)
}

inline fun Logger.info(msg: () -> String) = info(msg())
inline fun Logger.warn(msg: () -> String) = warn(msg())
inline fun Logger.error(msg: () -> String) = error(msg())
inline fun Logger.debug(msg: () -> String) = debug(msg())