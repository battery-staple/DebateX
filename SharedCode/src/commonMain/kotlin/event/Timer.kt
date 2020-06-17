package com.rohengiralt.debatex.event

import com.soywiz.klock.TimeSpan

interface Timer {
    val totalTime: TimeSpan
    val currentTime: TimeSpan
    val isRunning: Boolean
    val progress: Double
        get() = 1 - (currentTime / totalTime)
    val isOvertime: Boolean get() = progress > 1

    fun start()
    fun stop()
    fun toggleRunning()
    fun reset()
    fun stopAndReset() {
        stop()
        reset()
    }
}