package com.rohengiralt.debatex.util.serializers

import com.soywiz.klock.TimeSpan
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = TimeSpan::class)
object TimeSpanSerializer {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TimeSpan") {
        element<Double>("milliseconds")
    }

    override fun serialize(encoder: Encoder, value: TimeSpan) {
        with(encoder.beginStructure(descriptor)) {
            encodeDoubleElement(descriptor, 0, value.milliseconds)
            endStructure(descriptor)
        }
    }

    override fun deserialize(decoder: Decoder): TimeSpan {
        with(decoder.beginStructure(descriptor)) {
            val milliseconds = decodeDoubleElement(descriptor, 0)
            endStructure(descriptor)

            return TimeSpan(milliseconds)
        }
    }
}