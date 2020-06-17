@file:Suppress("UNUSED")

package com.rohengiralt.debatex.event

import com.rohengiralt.debatex.dataStructures.TimePage
import com.rohengiralt.debatex.loggerForClass
import com.rohengiralt.debatex.viewModels.ViewModel
import com.soywiz.klock.DateTime
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.minutes
import kotlinx.serialization.*
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.pow

/**
 * A pauseable countdown timer.
 *
 * This class handles the current time of each [TimePage], counting down from
 * its initialized [totalTime] and into the negatives until reset.
 *
 * @constructor initializes the [CountdownTimer]'s total time.
 * @property[totalTime] the starting time of this [CountdownTimer],
 * and the value to which it will be reset on [reset]
 * @see TimePage
 * @author Rohen Giralt
 */
@Serializable
data class CountdownTimer(override val totalTime: TimeSpan) : Timer {
    override fun start() {
        isRunning = true
    }

    override fun stop() {
        isRunning = false
    }

    override fun toggleRunning() {
        isRunning = !isRunning
    }

    override fun reset() {
        currentTime = totalTime
        timeAtLastStop = totalTime
    }

    override var currentTime: TimeSpan = totalTime
        get() {
            if (isRunning)
                field = timeAtLastStop - timeSinceLastStart
            return field
        }
        private set

    override var isRunning: Boolean = false
        private set(run) {
            if (run) {
                lastStartTime = DateTime.now()
                logger.info("Starting")
            } else {
                timeAtLastStop = currentTime
                logger.info("Stopping")
            }
            field = run
        }

    override var progress: Double
        set(value) {
            currentTime = totalTime * (1 - value)
        }
        get() = (super.progress).also { logger.info("progress is $it") }

    private var timeAtLastStop = totalTime

    private var lastStartTime: DateTime = DateTime.now()

    private val timeSinceLastStart
        get() = (DateTime.now() - lastStartTime).also {
            logger.info("timeSinceLastStart is $it")
        }

    @Serializer(forClass = CountdownTimer::class) //TODO: Remove once inline class serializers are added.
    companion object {
        @ImplicitReflectionSerializer
        override val descriptor: SerialDescriptor = SerialDescriptor("CountdownTimer") {
            element<Double>("totalTime")
            element<Double>("currentTime")
        }

        @ImplicitReflectionSerializer
        override fun serialize(encoder: Encoder, value: CountdownTimer) {
            value.stop()
            val compositeOutput = encoder.beginStructure(descriptor)
            compositeOutput.encodeDoubleElement(descriptor, 0, value.totalTime.minutes)
            compositeOutput.encodeDoubleElement(descriptor, 1, value.currentTime.minutes)
            compositeOutput.endStructure(descriptor)
        }

        @ImplicitReflectionSerializer
        override fun deserialize(decoder: Decoder): CountdownTimer {
            val dec: CompositeDecoder = decoder.beginStructure(descriptor)
            var totalTime: Double? = null
            var currentTime: Double? = null
            loop@ while (true) {
                when (val index = dec.decodeElementIndex(descriptor)) {
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> totalTime = dec.decodeDoubleElement(descriptor, index)
                    1 -> currentTime = dec.decodeDoubleElement(descriptor, index)
                    else -> throw SerializationException("Unknown index $index")
                }
            }
            dec.endStructure(descriptor)
            return CountdownTimer(
                (totalTime ?: throw MissingFieldException("currentTime")).minutes
            ).apply {
                this.currentTime =
                    (currentTime ?: throw MissingFieldException("currentTime")).minutes
            }
        }

        private val logger = loggerForClass<CountdownTimer>()
    }
}

//TODO: Don't just define this here; get it from somewhere else
private const val SECONDS_PER_MINUTE = 60

//@ThreadLocal
class TimerViewModel(
    timer: Timer,
    private val secondsDecimalPlaces: Int,
    private val minutesDigits: Int,
    private val useAbsoluteValue: Boolean = false
) : Timer by timer, ViewModel() {

    init {
        logger.info("TimerViewModel Created!")
    }

    override var updating: Boolean = true //TODO: DELETE
        set(value) {
            field = value
            logger.info("updating set to $field (time=$timeString)")
        }

/*//    init {
//        GlobalScope.launch {
//            while (true) {
//                delay(100L)
//                update()
//            }
//        }
//    }

//    ////@ThreadLocal
//    @Suppress("UNUSED")
//    val currentTime: TimeSpan by updatesViewModelOnChange(timer.currentTime)
//
//    ////@ThreadLocal
//    @Suppress("UNUSED")
//    val isRunning: Boolean by updatesViewModelOnChange(timer.isRunning)
//
//    ////@ThreadLocal
//    @Suppress("UNUSED")
//    val progress: Double by updatesViewModelOnChange(timer.progress)
//
//    ////@ThreadLocal
//    @Suppress("UNUSED")
//    val isOvertime: Boolean by updatesViewModelOnChange(timer.isOvertime)
//
//    @Suppress("UNUSED")
//    fun start(): Unit = timer.start()
//
//    @Suppress("UNUSED")
//    fun stop(): Unit = timer.stop()
//
//    @Suppress("UNUSED")
//    fun toggleRunning(): Unit = timer.toggleRunning()
//
//    @Suppress("UNUSED")
//    fun reset(): Unit = timer.reset()
//
//    @Suppress("UNUSED")
//    fun stopAndReset(): Unit = timer.stopAndReset()*/

//    object TimeStringCalculator {
//
//    }

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
                    .removeSuffix(".0")
                    .padStart(length = 2, padChar = '0')

            return "$timeStringPrefix$minutes:$seconds".also {
                logger.info(
                    """TimeString queried as $it,
                    |time is $currentTime,
                    """.trimMargin()
                )
            }
        }

    private val timeStringPrefix: String
        get() =
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

    companion object {
        private val logger = loggerForClass<TimerViewModel>()
    }
}