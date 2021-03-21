package com.rohengiralt.debatex.model.timerModel

import com.soywiz.klock.TimeSpan
import kotlinx.serialization.Serializable

@Serializable
sealed class TimerCountStrategy(val name: String) {
    final override fun toString(): String = name
    abstract fun currentTimeAfter(elapsedTime: TimeSpan, totalTime: TimeSpan): TimeSpan
    abstract fun progressAfter(elapsedTime: TimeSpan, totalTime: TimeSpan): Double

    @Serializable
    object CountUp : TimerCountStrategy("Up") {
        override fun currentTimeAfter(elapsedTime: TimeSpan, totalTime: TimeSpan): TimeSpan = elapsedTime
        override fun progressAfter(elapsedTime: TimeSpan, totalTime: TimeSpan): Double =
            elapsedTime / totalTime
    }

    @Serializable
    object CountDown : TimerCountStrategy("Down") {
        override fun currentTimeAfter(elapsedTime: TimeSpan, totalTime: TimeSpan): TimeSpan =
            totalTime - elapsedTime

        override fun progressAfter(elapsedTime: TimeSpan, totalTime: TimeSpan): Double =
            elapsedTime / totalTime
    }
}