package com.jetbrains.debatex

import com.soywiz.klock.DateTime
import com.soywiz.klock.TimeSpan

class Timer(var totalTime: TimeSpan) {
    private var timeAtLastStop = totalTime

    var currentTime: TimeSpan = totalTime
        get() {
            if (isRunning)
                field = timeAtLastStop - timeSinceLastStart
            return field
        }
    private var lastStartTime: DateTime = DateTime.EPOCH

    private val timeSinceLastStart get() = DateTime.now() - lastStartTime

    var isRunning = false
        set(run) {
            if (run) {
                lastStartTime = DateTime.now()
            } else {
                timeAtLastStop = currentTime
            }
            field = run
        }

    var progressProportion: Double
        get() = 1 - (currentTime / totalTime)
        set(value) {
            currentTime = totalTime * (1 - value)
        }

    fun start() {
        isRunning = true
    }

    fun stop() {
        isRunning = false
    }

    fun reset() {
        currentTime = totalTime
        timeAtLastStop = totalTime
    }

    fun stopAndReset() {
        stop(); reset()
    }
}
