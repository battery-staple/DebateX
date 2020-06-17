package com.rohengiralt.debatex.dataStructures

import com.rohengiralt.debatex.dataStructures.color.ColorRepresentation
import com.rohengiralt.debatex.dataStructures.color.SingleColor
import com.rohengiralt.debatex.event.LinearHueChange
import com.rohengiralt.debatex.event.Speaker
import com.rohengiralt.debatex.event.TimerViewModel
import com.rohengiralt.debatex.viewModels.ViewModel
import com.soywiz.klock.milliseconds
import com.soywiz.klock.seconds

class TimePageViewModel<out T : Speaker<*>>(
    private val timePage: TimePage<T>
) : ViewModel() { // TODO: Make BackgroundColored?

    @Suppress("UNUSED")
    val currentTopColor: SingleColor
        get() = SingleColor(
            ColorRepresentation.HSV(
                hue = currentHue,
                saturation = BACKGROUND_SATURATION,
                value = BACKGROUND_TOP_VALUE
            )
        )

    @Suppress("UNUSED")
    val currentBottomColor: SingleColor
        get() = SingleColor(
            ColorRepresentation.HSV(
                hue = currentHue,
                saturation = BACKGROUND_SATURATION,
                value = BACKGROUND_BOTTOM_VALUE
            )
        )

    private val currentHue: Double
        get() = LinearHueChange(
            endTime = timePage.totalTime,
            startHue = 0.3,
            jumpAtIntervals = listOf(
                16.seconds + 1.milliseconds to 0.75, // TODO: Get from config file
                16.seconds to 0.08,
                6.seconds to 0.0
            ),
            jumpsAreFromEnd = true
        ).invoke(timePage.progress)

    @Suppress("UNUSED")
    val timerViewModel: TimerViewModel =
        TimerViewModel(
            timePage,
            minutesDigits = 2,
            secondsDecimalPlaces = 0,
            useAbsoluteValue = true
        )

    companion object {
        private const val BACKGROUND_SATURATION: Double = 0.8 // TODO: Get from config file
        private const val BACKGROUND_TOP_VALUE: Double = 0.75
        private const val BACKGROUND_BOTTOM_VALUE: Double = 0.4
    }
}