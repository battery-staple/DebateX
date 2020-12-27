package com.rohengiralt.debatex.model.event
//
//import com.rohengiralt.debatex.model.TimePageModel
//import com.rohengiralt.debatex.loggerForClass
//import com.rohengiralt.debatex.model.CountdownTimerModel
//import com.rohengiralt.debatex.model.TimerModel
//import com.rohengiralt.debatex.viewModel.ViewModel
//import com.soywiz.klock.DateTime
//import com.soywiz.klock.TimeSpan
//import com.soywiz.klock.minutes
////import kotlinx.coroutines.*
////import kotlinx.serialization.*
//import kotlin.math.absoluteValue
//import kotlin.math.floor
//import kotlin.math.pow
//
///**
// * A pauseable countdown timer.
// *
// * This class handles the current time of each [TimePageModel], counting down from
// * its initialized [totalTime] and into the negatives until reset.
// *
// * @constructor initializes the [CountdownTimer]'s total time.
// * @property[totalTime] the starting time of this [CountdownTimer],
// * and the value to which it will be reset on [reset]
// * @see TimePageModel
// * @author Rohen Giralt
// */
////@Serializable
//data class CountdownTimer(override val totalTime: TimeSpan) : Timer {
//    override fun start() {
//        if (isEnabled) isRunning = true
//    }
//
//    override fun stop() {
//        if (isEnabled) isRunning = false
//    }
//
//    override fun toggleRunning() {
//        if (isEnabled) isRunning = !isRunning
//    }
//
//    override fun disable() {
//        isEnabled = true
//    }
//
//    override fun enable() {
//        isEnabled = false
//    }
//
//    override fun toggleEnabled() {
//        isEnabled = !isEnabled
//    }
//
//    override fun reset() {
//        if (isEnabled) {
//            currentTime = totalTime
//            timeAtLastStop = totalTime
//        }
//    }
//
//    override var isEnabled: Boolean = true
//        private set
//
//    override var currentTime: TimeSpan = totalTime
//        get() {
//            if (isRunning)
//                field = timeAtLastStop - timeSinceLastStart
//            return field
//        }
//        private set
//
//    override var isRunning: Boolean = false
//        private set(run) {
//            if (run) {
//                lastStartTime = DateTime.now()
//            } else {
//                timeAtLastStop = currentTime
//            }
//            field = run
//        }
//
//    override var progress: Double
//        set(value) {
//            currentTime = totalTime * (1 - value)
//        }
//        get() = super.progress
//
//    private var timeAtLastStop = totalTime
//
//    private var lastStartTime: DateTime = DateTime.now()
//
//    private val timeSinceLastStart
//        get() = (DateTime.now() - lastStartTime)
//
////    @Serializer(forClass = CountdownTimer::class) //TODO: Remove once inline class serializers are added.
//    companion object {
////        @ImplicitReflectionSerializer
////        override val descriptor: SerialDescriptor = SerialDescriptor("CountdownTimer") {
////            element<Double>("totalTime")
////            element<Double>("currentTime")
////        }
////
////        @ImplicitReflectionSerializer
////        override fun serialize(encoder: Encoder, value: CountdownTimer) {
////            value.stop()
////            val compositeOutput = encoder.beginStructure(descriptor)
////            compositeOutput.encodeDoubleElement(descriptor, 0, value.totalTime.minutes)
////            compositeOutput.encodeDoubleElement(descriptor, 1, value.currentTime.minutes)
////            compositeOutput.endStructure(descriptor)
////        }
////
////        @ImplicitReflectionSerializer
////        override fun deserialize(decoder: Decoder): CountdownTimer {
////            val dec: CompositeDecoder = decoder.beginStructure(descriptor)
////            var totalTime: Double? = null
////            var currentTime: Double? = null
////            loop@ while (true) {
////                when (val index = dec.decodeElementIndex(descriptor)) {
////                    CompositeDecoder.READ_DONE -> break@loop
////                    0 -> totalTime = dec.decodeDoubleElement(descriptor, index)
////                    1 -> currentTime = dec.decodeDoubleElement(descriptor, index)
////                    else -> throw SerializationException("Unknown index $index")
////                }
////            }
////            dec.endStructure(descriptor)
////            return CountdownTimer(
////                (totalTime ?: throw MissingFieldException("currentTime")).minutes
////            ).apply {
////                this.currentTime =
////                    (currentTime ?: throw MissingFieldException("currentTime")).minutes
////            }
////        }
//
//        private val logger = loggerForClass<CountdownTimer>()
//    }
//}
//
////TODO: Don't just define this here; get it from somewhere else
//private const val SECONDS_PER_MINUTE = 60
//
////@InternalCoroutinesApi
//class CountdownTimerViewModel(
//    override val model: CountdownTimerModel,
//    private val secondsDecimalPlaces: Int,
//    private val minutesDigits: Int,
//    private val useAbsoluteValue: Boolean = false
//) : Timer by model.timer, ViewModel<CountdownTimerModel>() {
//
//    @Suppress("NOTHING_TO_INLINE")
//    private inline fun Int.coerceWithDigits(digits: Int): Int {
//        return coerceAtMost((10.0.pow(digits)).toInt() - 1)
//    }
//
//    @Suppress("NOTHING_TO_INLINE")
//    private inline fun Double.floor(decimalPlaces: Int): Double =
//        floor(this * 10.0.pow(decimalPlaces)) / (10.0.pow(decimalPlaces))
//
////    private object Updater : MutableList<TimerViewModel> by mutableListOf() {
////        @InternalCoroutinesApi
////        val listContext = MainLoopDispatcher + SupervisorJob()
////
////        private var started = false
////
////        @InternalCoroutinesApi
////        @Suppress("NOTHING_TO_INLINE")
////        inline fun start(): Unit {
////            if (!started) runBlocking {
////
////                CoroutineScope(listContext).launch {
////                    while (true) {
////                        for (timer in this@Updater) {
////                            timer.update()
////                        }
////                        delay(1000)
////                    }
////                }
////            }
////        }
////    }
//
//    companion object {
//        private val logger = loggerForClass<CountdownTimerViewModel>()
//    }
//}