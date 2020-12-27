package com.rohengiralt.debatex.viewModel

import com.rohengiralt.debatex.datafetch.ConstantModelFetcher
import com.rohengiralt.debatex.datafetch.DataFetcher
import com.rohengiralt.debatex.model.TimerModel
import com.rohengiralt.debatex.model.event.Timer
import com.soywiz.klock.TimeSpan
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.pow

//TODO: Don't just define this here; get it from somewhere else
private const val SECONDS_PER_MINUTE = 60

class TimerViewModel(
    override val modelFetcher: DataFetcher<TimerModel>,
    private val secondsDecimalPlaces: Int,
    private val minutesDigits: Int,
    private val useAbsoluteValue: Boolean = false
) : ViewModel<TimerModel>(), Timer by Timer(modelFetcher) {

/*//    init {
//        Updater.start()
//        logger.info("About to be about to be about to add this TimerViewModel to the updater.")
//        runBlocking(Updater.listContext) {
//            logger.info("About to add this TimerViewModel to the updater.")
//            Updater.add(this@TimerViewModel)
//            logger.info("Added this TimerViewModel to the updater.")
//        }
//    }

//    override var updating: Boolean = true //TODO: DELETE*/

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

/*//    private object Updater : MutableList<TimerViewModel> by mutableListOf() {
//        @InternalCoroutinesApi
//        val listContext = MainLoopDispatcher + SupervisorJob()
//
//        private var started = false
//
//        @InternalCoroutinesApi
//        @Suppress("NOTHING_TO_INLINE")
//        inline fun start(): Unit {
//            if (!started) runBlocking {
//
//                CoroutineScope(listContext).launch {
//                    while (true) {
//                        for (timer in this@Updater) {
//                            timer.update()
//                        }
//                        delay(1000)
//                    }
//                }
//            }
//        }
//    }*/

//    companion object {
//        private val logger = loggerForClass<TimerViewModel>()
//    }
}