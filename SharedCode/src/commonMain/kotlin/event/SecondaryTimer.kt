@file:Suppress("UNUSED")

package com.rohengiralt.debatex.event

import com.rohengiralt.debatex.dataStructures.Named
import com.rohengiralt.debatex.dataStructures.SimpleName
import com.rohengiralt.debatex.dataStructures.color.DarkModeSafeColor
import com.rohengiralt.debatex.dataStructures.text.Text
import com.rohengiralt.debatex.viewModels.CardViewModel
import kotlinx.serialization.Serializable
import kotlin.native.concurrent.ThreadLocal

@Serializable
data class SecondaryTimer<out T : Speaker<*>>(
    val namedBy: SimpleName,
    private val timer: Timer,
    val belongingTo: List<T>
) : Named by namedBy, Timer by timer {
    constructor(namedBy: SimpleName, timer: CountdownTimer, belongingTo: T) :
            this(namedBy, timer, listOf(belongingTo))

    init {
        require(belongingTo.isNotEmpty())
    }
}

class SecondaryTimerViewModel<out T : Speaker<*>>(
    private val secondaryTimer: SecondaryTimer<T>,
    backgroundColor: DarkModeSafeColor = DEFAULT_BACKGROUND_COLOR
) : CardViewModel(Text(secondaryTimer.name), backgroundColor) {

    @ThreadLocal
    private val timerViewModel: TimerViewModel = TimerViewModel(
        secondaryTimer,
        secondsDecimalPlaces = 1,
        minutesDigits = 1,
        useAbsoluteValue = true
    )

    fun buttonIsTapped(): Unit = secondaryTimer.toggleRunning()

    val displayContents: String
        get() = timerViewModel.timeString
}