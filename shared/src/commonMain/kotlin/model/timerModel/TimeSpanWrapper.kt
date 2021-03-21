package com.rohengiralt.debatex.model.timerModel

import com.soywiz.klock.TimeSpan
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

@OptIn(ExperimentalSerializationApi::class)
@Serializable(with = TimeSpanWrapper.Companion::class)
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

fun TimeSpan.wrap() = TimeSpanWrapper(this)

operator fun TimeSpan.compareTo(other: TimeSpanWrapper): Int = other.timeSpan.compareTo(this)