package com.rohengiralt.debatex.viewModel

import com.rohengiralt.debatex.dataStructure.ShortenableName
import com.rohengiralt.debatex.loggerForClass
import com.rohengiralt.debatex.model.event.Timer
import com.rohengiralt.debatex.model.sectionModel.SettingModel
import com.rohengiralt.debatex.model.timerModel.TimerCountStrategy
import com.rohengiralt.debatex.model.timerModel.TimerModel
import com.rohengiralt.debatex.viewModel.section.registerSetting
import com.soywiz.klock.TimeSpan
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.pow
import kotlin.native.concurrent.ThreadLocal

//TODO: Don't just define this here; get it from somewhere else
private const val SECONDS_PER_MINUTE = 60

@ViewModelOnly
class TimerViewModel(
    model: TimerModel<*>,
    private val secondsDecimalPlaces: Int,
    private val minutesDigits: Int,
    private val useAbsoluteValue: Boolean = false,
) : ViewModel() {

    init {
        countStrategySetting.addSubscriber(this)
    }

    private val timer = Timer(
        model.totalTime.timeSpan,
        countStrategy.option
    ).also { it.addSubscriber(this) }

    internal val totalTime = model.totalTime
    internal val speakers = model.speakers

    fun reset() {
        isRunning = false
        timer.reset()
        observationHandler.publish()
    }

    val progress get() = timer.progress
    var isRunning: Boolean by observationHandler.published(timer::isRunning, true)

    private val currentTime inline get() = timer.currentTime

    @Suppress("UNUSED")
    val name: ShortenableName = model.name

    @Suppress("UNUSED")
    val timeString: String
        get() {
            require(secondsDecimalPlaces >= 0) { "Number of decimal places must be nonnegative." }
            require(minutesDigits >= 0) { "Number of digits must be nonnegative." }

            val minutes =
                currentTime.minutes
                    .toInt()
                    .absoluteValue
                    .coerceWithDigits(minutesDigits)
                    .toString()
                    .padStart(length = minutesDigits, padChar = '0')

            val seconds =
                (currentTime.seconds % SECONDS_PER_MINUTE)
                    .absoluteValue
                    .floor(secondsDecimalPlaces)
                    .toString()
                    .let {
                        if (secondsDecimalPlaces == 0)
                            it.removeSuffix(".0")
                        else it
                    }
                    .padStart(
                        length = 3 + if (secondsDecimalPlaces == 0) -1 else secondsDecimalPlaces,
                        padChar = '0'
                    )

            return "$timeStringPrefix$minutes:$seconds"
        }

    private val timeStringPrefix: String
        inline get() =
            if (!useAbsoluteValue && currentTime < TimeSpan.ZERO)
                "-"
            else
                ""

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Int.coerceWithDigits(digits: Int): Int {
        return coerceAtMost((10.0.pow(digits)).toInt() - 1)
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Double.floor(decimalPlaces: Int): Double =
        floor(this * 10.0.pow(decimalPlaces)) / (10.0.pow(decimalPlaces))

    override fun notify() {
        super.notify()
        timer.strategy = countStrategy.option
    }

    @ThreadLocal
    companion object {
        private val countStrategySetting =
            registerSetting(
                "Timers Count",
                SettingModel.SettingOptions.MultipleChoice(
                    SettingModel.SettingOptions.MultipleChoice.MultipleChoiceOption(
                        "Down", TimerCountStrategy.CountDown
                    ),
                    SettingModel.SettingOptions.MultipleChoice.MultipleChoiceOption(
                        "Up", TimerCountStrategy.CountUp
                    ),
                    initialIndex = 0,
                    serializer = TimerCountStrategy.serializer()
                )
            )

        private val countStrategy: SettingModel.SettingOptions.MultipleChoice.MultipleChoiceOption<TimerCountStrategy> by countStrategySetting

        private val logger = loggerForClass<TimerViewModel>()
    }
}