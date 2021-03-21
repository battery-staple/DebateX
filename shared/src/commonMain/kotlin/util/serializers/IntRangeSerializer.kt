package com.rohengiralt.debatex.util.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = IntRange::class)
object IntRangeSerializer {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("IntRange") {
        element<Int>("first")
        element<Int>("last")
    }

    override fun serialize(encoder: Encoder, value: IntRange) {
        with(encoder.beginStructure(descriptor)) {
            encodeIntElement(descriptor, index = 0, value = value.first)
            encodeIntElement(descriptor, index = 1, value = value.last)
            endStructure(descriptor)
        }
    }

    override fun deserialize(decoder: Decoder): IntRange {
        with(decoder.beginStructure(descriptor)) {
            var first: Int? = null
            var last: Int? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break
                    0 -> first = decodeIntElement(descriptor, index)
                    1 -> last = decodeIntElement(descriptor, index)
                    else -> throw SerializationException("Unexpected index $index")
                }
            }
            endStructure(descriptor)

            return IntRange(
                start = first ?: throw SerializationException("Couldn't find element first"),
                endInclusive = last ?: throw SerializationException("Couldn't find element last")
            )
        }
    }
}