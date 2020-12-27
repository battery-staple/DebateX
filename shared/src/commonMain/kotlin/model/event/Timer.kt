package com.rohengiralt.debatex.model.event

import com.rohengiralt.debatex.datafetch.DataFetcher
import com.rohengiralt.debatex.model.TimerImpl
import com.rohengiralt.debatex.model.TimerModel
import com.soywiz.klock.TimeSpan
//import kotlinx.serialization.KSerializer
//import kotlinx.serialization.Polymorphic
//import kotlinx.serialization.modules.SerialModule
//import kotlinx.serialization.modules.SerializersModule

//@Polymorphic
interface Timer {
    val totalTime: TimeSpan
    val currentTime: TimeSpan
    val isRunning: Boolean
    val isEnabled: Boolean
    val progress: Double
    val isOvertime: Boolean get() = progress > 1

    fun start()
    fun stop()
    fun toggleRunning()
//    fun disable()
//    fun enable()
//    fun toggleEnabled()
    fun reset()
    fun stopAndReset() {
        stop()
        reset()
    }

    companion object {
        operator fun invoke(modelFetcher: DataFetcher<TimerModel>): Timer = TimerImpl(modelFetcher)
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