package com.rohengiralt.debatex.viewModel.timePage

import com.rohengiralt.debatex.Configurable
import com.rohengiralt.debatex.dataStructure.Size
import com.rohengiralt.debatex.model.TimePageModel
import com.rohengiralt.debatex.dataStructure.color.ColorRepresentation
import com.rohengiralt.debatex.dataStructure.color.SingleColor
import com.rohengiralt.debatex.dataStructure.text.FontWeight
import com.rohengiralt.debatex.dataStructure.text.Text
import com.rohengiralt.debatex.dataStructure.text.textSize
import com.rohengiralt.debatex.dataStructure.Speaker
import com.rohengiralt.debatex.datafetch.DataFetcher
import com.rohengiralt.debatex.viewModel.TimerViewModel
import com.rohengiralt.debatex.viewModel.ViewModel
import com.rohengiralt.debatex.viewModel.timePage.linearHueChange.LinearHueChange
import com.soywiz.klock.milliseconds
import com.soywiz.klock.seconds

class TimePageViewModel<T : Speaker<*>>(
    override val modelFetcher: DataFetcher<TimePageModel<T>>
) : ViewModel<TimePageModel<T>>() { // TODO: Make BackgroundColored?

    @Suppress("UNUSED")
    val pageName: Text = Text(
        rawText = model.name.shortNameOrLong,
        fontWeight = FontWeight.Bold,
        height = textSize(
            screenProportion = 0.11,
            screenDimension = Size.ScreenDimension.Constant.Longest
        )
    )

    @Suppress("UNUSED")
    val speakers: List<T> = model.belongingTo

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
            endTime = model.timerFetcher.fetch().totalTime.timeSpan,
            startHue = 0.3,
            jumpAtIntervals = @Configurable listOf(
                16.seconds + 1.milliseconds to 0.75,
                16.seconds to 0.08,
                6.seconds to 0.0
            ),
            jumpsAreFromEnd = true
        ).invoke(timerViewModel.progress)

    //    @InternalCoroutinesApi
    @Suppress("UNUSED")
    val timerViewModel: TimerViewModel =
        TimerViewModel(
            model.timerFetcher,
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