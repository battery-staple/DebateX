package com.rohengiralt.debatex.di

import com.rohengiralt.debatex.Logger
import org.koin.core.logger.Level
import org.koin.core.logger.MESSAGE
import org.koin.core.logger.Logger as KoinLogger

class KoinLoggerAdapter(private val delegate: Logger) : KoinLogger(Level.DEBUG) {

    override fun log(level: Level, msg: MESSAGE) {
        val log: Logger.(String) -> Unit = when (level) {
            Level.DEBUG -> Logger::debug
            Level.INFO -> Logger::info
            Level.ERROR -> Logger::error
            Level.NONE -> {
                { info("[Logged as None] $it") }
            }
        }

        delegate.log(msg)
    }
}