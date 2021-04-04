package com.rohengiralt.debatex.viewModel

import com.rohengiralt.debatex.dataStructure.ShortenableName
import com.rohengiralt.debatex.dataStructure.competitionTypes.Speaker
import com.rohengiralt.debatex.model.event.Timer
import com.rohengiralt.debatex.model.sectionModel.SettingModel
import com.rohengiralt.debatex.model.timerModel.TimeSpanWrapper
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

abstract class TimerViewModel : ViewModel() {
    abstract val name: ShortenableName
    abstract val timeString: String
    abstract val progress: Double
    abstract var isRunning: Boolean

    abstract fun reset()

    internal abstract val totalTime: TimeSpanWrapper
    internal abstract val speakers: Set<Speaker>

    data class DisplayConfiguration(
        val secondsDecimalPlaces: Int, //TODO: UInt once stable
        val minutesDigits: Int,
        val useAbsoluteValue: Boolean = false,
    ) {
        init {
            require(secondsDecimalPlaces >= 0) { "Number of decimal places must be nonnegative." }
            require(minutesDigits >= 0) { "Number of digits must be nonnegative." }
        }
    }
}

class BasicTimerViewModel(
    model: TimerModel<*>,
    private val configuration: DisplayConfiguration,
) : TimerViewModel() {

    init {
        countStrategySetting.addSubscriber(this)
    }

    private val timer by lazy {
        Timer(
            model.totalTime.timeSpan,
            countStrategy.option
        ).also { it.addSubscriber(this) }
    }

    override val totalTime = model.totalTime
    override val speakers = model.speakers

    override fun reset() {
        isRunning = false
        timer.reset()
        observationHandler.publish()
    }

    override val progress: Double get() = timer.progress
    override var isRunning: Boolean by observationHandler.published({ timer::isRunning }, true)

    private val currentTime inline get() = timer.currentTime

    @Suppress("UNUSED")
    override val name: ShortenableName = model.name

    @Suppress("UNUSED")
    override val timeString: String
        get() {
            val minutes =
                currentTime.minutes
                    .toInt()
                    .absoluteValue
                    .coerceWithDigits(configuration.minutesDigits)
                    .toString()
                    .padStart(length = configuration.minutesDigits, padChar = '0')

            val seconds =
                (currentTime.seconds % SECONDS_PER_MINUTE)
                    .absoluteValue
                    .floor(configuration.secondsDecimalPlaces)
                    .toString()
                    .let {
                        if (configuration.secondsDecimalPlaces == 0)
                            it.removeSuffix(".0")
                        else it
                    }
                    .padStart(
                        length = 3 + if (configuration.secondsDecimalPlaces == 0) -1 else configuration.secondsDecimalPlaces,
                        padChar = '0'
                    )

            return "$timeStringPrefix$minutes:$seconds"
        }

    private val timeStringPrefix: String
        inline get() =
            if (configuration.useAbsoluteValue || currentTime >= TimeSpan.ZERO) "" else "-"

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Int.coerceWithDigits(digits: Int): Int {
        return coerceAtMost((10.0.pow(digits)).toInt() - 1)
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Double.floor(decimalPlaces: Int): Double =
        floor(this * 10.0.pow(decimalPlaces)) / (10.0.pow(decimalPlaces))

    override fun update() {
        super.update()
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
    }
}