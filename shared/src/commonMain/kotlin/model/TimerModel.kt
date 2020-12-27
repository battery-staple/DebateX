package com.rohengiralt.debatex.model

import com.rohengiralt.debatex.datafetch.DataFetcher
import com.rohengiralt.debatex.model.event.Timer
import com.soywiz.klock.DateTime
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.seconds
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

//@Polymorphic
//abstract class TimerModel : Model() {
//    abstract val timer: Timer
//
//    companion object
////    @Serializer(forClass = TimerModel::class)
////    companion object : KSerializer<TimerModel> {
////        override val descriptor: SerialDescriptor = SerialDescriptor()
////    }
//}

@Serializable
data class TimerModel(
    val totalTime: TimeSpanWrapper,
    val countStrategy: TimerCountStrategy,
) : Model() {
    constructor(totalTime: TimeSpan, countStrategy: TimerCountStrategy) : this(TimeSpanWrapper(totalTime),
        countStrategy)

    inline fun currentTimeAfter(elapsedTime: TimeSpan): TimeSpan = countStrategy.currentTimeAfter(elapsedTime, this)
    inline fun progressAfter(elapsedTime: TimeSpan): Double = countStrategy.progressAfter(elapsedTime, this)
}

class TimerImpl(private val modelFetcher: DataFetcher<TimerModel>) : Timer {
    private val model inline get() = modelFetcher.fetch()

    override val isEnabled: Boolean
        get() = false //TODO("Not yet implemented")

    override val totalTime: TimeSpan = model.totalTime.timeSpan

    override var isRunning: Boolean = false
        private set(running) {
            if (running) {
                timeAtLastStart = DateTime.now()
            } else {
                elapsedTimeAtLastStop = elapsedTime
            }
            field = running
        }
    private var elapsedTimeAtLastStop = 0.seconds
    private var timeAtLastStart: DateTime = DateTime.now()

    private val elapsedTime: TimeSpan
        get() =
            elapsedTimeAtLastStop + runTimeSinceLastStop

    private val runTimeSinceLastStop: TimeSpan
        get() =
            if (isRunning) {
                runTimeSinceLastStart
            } else 0.seconds

    private val runTimeSinceLastStart: TimeSpan get() = DateTime.now() - timeAtLastStart

    override val currentTime: TimeSpan get() = model.currentTimeAfter(elapsedTime)
    override val progress: Double get() = model.progressAfter(elapsedTime)

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
        timeAtLastStart = DateTime.now()
        elapsedTimeAtLastStop = 0.seconds
    }
}

@Serializable
sealed class TimerCountStrategy(val name: String) {
    final override fun toString(): String = name
    abstract fun currentTimeAfter(elapsedTime: TimeSpan, model: TimerModel): TimeSpan
    abstract fun progressAfter(elapsedTime: TimeSpan, model: TimerModel): Double

    @Serializable
    object CountUp : TimerCountStrategy("Up") {
        override fun currentTimeAfter(elapsedTime: TimeSpan, model: TimerModel): TimeSpan = elapsedTime
        override fun progressAfter(elapsedTime: TimeSpan, model: TimerModel): Double =
            elapsedTime / model.totalTime.timeSpan
    }

    @Serializable
    object CountDown : TimerCountStrategy("Down") {
        override fun currentTimeAfter(elapsedTime: TimeSpan, model: TimerModel): TimeSpan =
            model.totalTime.timeSpan - elapsedTime

        override fun progressAfter(elapsedTime: TimeSpan, model: TimerModel): Double =
            elapsedTime / model.totalTime.timeSpan
    }
}

@Serializable
data class TimeSpanWrapper(val timeSpan: TimeSpan) : Comparable<TimeSpan> {
    override fun compareTo(other: TimeSpan): Int = timeSpan.compareTo(other)

    @ExperimentalSerializationApi
    @Serializer(forClass = TimeSpanWrapper::class)
    companion object {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TimeSpanWrapper") {
            element<Double>("milliseconds")
        }

        override fun serialize(encoder: Encoder, value: TimeSpanWrapper) {
            val compositeOutput = encoder.beginStructure(descriptor)
            compositeOutput.encodeDoubleElement(descriptor, 0, value.timeSpan.milliseconds)
            compositeOutput.endStructure(descriptor)
        }

        override fun deserialize(decoder: Decoder): TimeSpanWrapper {
            val compositeDecoder: CompositeDecoder = decoder.beginStructure(descriptor)

            var milliseconds: Double? = null

            loop@ while (true) {
                when (val index = compositeDecoder.decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break@loop
                    0 -> milliseconds = compositeDecoder.decodeDoubleElement(descriptor, index)
                    else -> throw SerializationException("Unknown index $index")
                }
            }
            compositeDecoder.endStructure(descriptor)

            return TimeSpanWrapper(TimeSpan(milliseconds
                ?: throw SerializationException("Missing field milliseconds"))) // TODO: Find out how to use MissingFieldException here
        }
    }
}

operator fun TimeSpan.compareTo(other: TimeSpanWrapper): Int = other.timeSpan.compareTo(this)

//@Serializer(forClass = TimeSpan::class) //TODO: Contextual Serialization?
//object TimeSpanSerializer {
//    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TimeSpan") {
//        element<Double>("milliseconds")
//    }
//
//    override fun serialize(encoder: Encoder, value: TimeSpan) {
//        val compositeOutput = encoder.beginStructure(descriptor)
//        compositeOutput.encodeDoubleElement(descriptor, 0, value.milliseconds)
//        compositeOutput.endStructure(descriptor)
//    }
//
//    override fun deserialize(decoder: Decoder): TimeSpan {
//        val compositeDecoder: CompositeDecoder = decoder.beginStructure(descriptor)
//
//        var milliseconds: Double? = null
//
//        loop@ while(true) {
//            when (val index = compositeDecoder.decodeElementIndex(descriptor)) {
//                CompositeDecoder.DECODE_DONE -> break@loop
//                0 -> milliseconds = compositeDecoder.decodeDoubleElement(descriptor, index)
//                else -> throw SerializationException("Unknown index $index")
//            }
//        }
//        compositeDecoder.endStructure(descriptor)
//
//        return TimeSpan(milliseconds ?: throw SerializationException("Missing field milliseconds")) // TODO: Find out how to use MissingFieldException here
//    }
//}