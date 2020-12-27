package com.rohengiralt.debatex.viewModel

import com.rohengiralt.debatex.dataStructure.Size
import com.rohengiralt.debatex.dataStructure.Speaker
import com.rohengiralt.debatex.dataStructure.color.DarkModeSafeColor
import com.rohengiralt.debatex.dataStructure.text.Text
import com.rohengiralt.debatex.dataStructure.text.textSize
import com.rohengiralt.debatex.datafetch.DataFetcher
import com.rohengiralt.debatex.model.event.SecondaryTimerModel

class SecondaryTimerViewModel<T : Speaker<*>>(
    override val modelFetcher: DataFetcher<SecondaryTimerModel<T>>,
    backgroundColor: DarkModeSafeColor? = null
) : CardViewModel<SecondaryTimerModel<T>>(
    title = Text(
        rawText = modelFetcher.fetch().name,
        height = textSize(
            screenProportion = 0.12,
            screenDimension = Size.ScreenDimension.Constant.Shortest
        )
    ),
    backgroundColor = backgroundColor ?: DEFAULT_BACKGROUND_COLOR,
    cornerRadius = 20.0
) {
    private val timerViewModel: TimerViewModel = TimerViewModel(
        model.timerModelFetcher,
        secondsDecimalPlaces = 1,
        minutesDigits = 1,
        useAbsoluteValue = true
    )

    fun onResetButtonTap(): Unit = timerViewModel.reset()

//    fun setEnabled(to: Boolean): Unit = with(model) {
//        if (to) timerModel.enable() else disable()
//    }

    val speakers: List<T> = model.belongingTo

    fun onTimeTextTap(): Unit = timerViewModel.toggleRunning()

    val displayContents: String
        get() = timerViewModel.timeString
}