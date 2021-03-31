package com.rohengiralt.debatex.model.event

import com.rohengiralt.debatex.model.timerModel.TimerCountStrategy
import com.rohengiralt.debatex.observation.Observable
import com.rohengiralt.debatex.observation.Observer
import com.rohengiralt.debatex.observation.WeakReferencePublisher
import com.soywiz.klock.DateTime
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//import kotlinx.serialization.KSerializer
//import kotlinx.serialization.Polymorphic
//import kotlinx.serialization.modules.SerialModule
//import kotlinx.serialization.modules.SerializersModule

//@Polymorphic
//interface Timer {
//    val totalTime: TimeSpan
//    val currentTime: TimeSpan
//    val isRunning: Boolean
//    val isEnabled: Boolean
//    val progress: Double
//    val isOvertime: Boolean get() = progress > 1
//
//    fun start()
//    fun stop()
//    fun toggleRunning()
////    fun disable()
////    fun enable()
////    fun toggleEnabled()
//    fun reset()
//    fun stopAndReset() {
//        stop()
//        reset()
//    }
//}

class Timer private constructor(
    private val totalTime: TimeSpan,
    var strategy: TimerCountStrategy,
    private val observationHandler: WeakReferencePublisher<Observer>,
) : Observable<Observer> by observationHandler {
    constructor(
        totalTime: TimeSpan,
        strategy: TimerCountStrategy,
    ) : this(totalTime, strategy, WeakReferencePublisher())

    private var updater: Job? = null

    var isRunning: Boolean = false
        set(startRunning) {
            if (startRunning == field) return

            if (startRunning) {
                timeAtLastStart = DateTime.now()

                updater = GlobalScope.launch(Dispatchers.Main) {
                    while (true) {
                        observationHandler.publish()
                        delay(UPDATE_DELAY)
                    }
                }
            } else {
                elapsedTimeAtLastStop = elapsedTime
                updater?.cancel()
                updater = null
            }
            field = startRunning
        }

    private var elapsedTimeAtLastStop = 0.seconds
    private var timeAtLastStart: DateTime = DateTime.now()

    private val elapsedTime: TimeSpan
        get() = elapsedTimeAtLastStop + runTimeSinceLastStop

    private val runTimeSinceLastStop: TimeSpan
        get() = if (isRunning) {
            runTimeSinceLastStart
        } else 0.seconds

    private val runTimeSinceLastStart: TimeSpan get() = DateTime.now() - timeAtLastStart

    val currentTime: TimeSpan get() = strategy.currentTimeAfter(elapsedTime, totalTime)
    val progress: Double get() = strategy.progressAfter(elapsedTime, totalTime)
    val isOvertime: Boolean get() = progress > 1


    fun reset() {
        timeAtLastStart = DateTime.now()
        elapsedTimeAtLastStop = 0.seconds
    }

    companion object {
        private const val UPDATE_DELAY: Long = 100L
    }
}

//abstract class AbstractTimer : Timer {
//    override val progress: Double
//        get() = 1 - (currentTime / totalTime)
//
//    override val isOvertime: Boolean
//        get() = progress > 1
//}

//abstract class Timer1 {
//    abstract val totalTime: TimeSpan
//    abstract val currentTime: TimeSpan
//    abstract val isRunning: Boolean
//    abstract val isEnabled: Boolean
//    open val progress: Double get() = 1 - (currentTime / totalTime)
//    open val isOvertime: Boolean get() = progress > 1
//
//    abstract fun start()
//    abstract fun stop()
//    abstract fun toggleRunning()
//    abstract fun disable()
//    abstract fun enable()
//    abstract fun toggleEnabled()
//    abstract fun reset()
//    open fun stopAndReset() {
//        stop()
//        reset()
//    }
//}

//val timerSerializerModule: SerialModule = SerializersModule {
//    polymorphic<Timer>(Timer::class) {
//        CountdownTimer::class with CountdownTimer.serializer()
//        SecondaryTimer::class with SecondaryTimer.serializer(typeSerial0 = )
//    }
//}