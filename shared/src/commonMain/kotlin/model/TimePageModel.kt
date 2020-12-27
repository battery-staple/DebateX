package com.rohengiralt.debatex.model

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.rohengiralt.debatex.dataStructure.ShortenableName
import com.rohengiralt.debatex.dataStructure.Speaker
import com.rohengiralt.debatex.datafetch.DataFetcher
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.LongAsStringSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

@Serializable
data class TimePageModel<out T : Speaker<*>>(
    val name: ShortenableName,
    /*private*/
    val timerFetcher: DataFetcher<TimerModel>,
    val belongingTo: List<T>,
//    val sectionBreaks: List<*>? = null
) : Model() {
    init {
        require(belongingTo.isNotEmpty())
    }

    @Serializable(with = UuidSerializer::class)
    val uuid: Uuid = uuid4() //TODO: Ensure no collisions

    @Suppress("UNUSED")
    constructor(longName: String, shortName: String, timer: DataFetcher<TimerModel>, speakers: List<T>) :
            this(
                ShortenableName(
                    longName,
                    shortName
                ),
                timer,
                speakers
            )

    @Suppress("UNUSED")
    constructor(name: ShortenableName, timer: DataFetcher<TimerModel>, speaker: T) :
            this(name, timer, listOf(speaker))

    @Suppress("UNUSED")
    constructor(longName: String, shortName: String, timer: DataFetcher<TimerModel>, speaker: T) :
            this(
                ShortenableName(
                    longName,
                    shortName
                ), timer, listOf(speaker))
}

@ExperimentalSerializationApi
@Serializer(forClass = Uuid::class)
object UuidSerializer {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Uuid") {
        element<String>("Most Significant Bits")
        element<String>("Least Significant Bits")
    }

    override fun serialize(encoder: Encoder, value: Uuid) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, LongAsStringSerializer, value.mostSignificantBits)
            encodeSerializableElement(descriptor, 1, LongAsStringSerializer, value.leastSignificantBits)
        }
    }

    override fun deserialize(decoder: Decoder): Uuid {
        decoder.decodeStructure(descriptor) {
            var mostSignificantBits: Long? = null
            var leastSignificantBits: Long? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break
                    0 -> mostSignificantBits = decodeSerializableElement(descriptor, index, LongAsStringSerializer)
                    1 -> leastSignificantBits = decodeSerializableElement(descriptor, index, LongAsStringSerializer)
                    else -> throw SerializationException("Unknown index $index")
                }
            }

            return Uuid(
                /*msb = (commented out due to old version not retaining parameter names; TODO: uncomment when this is valid)*/ mostSignificantBits ?: throw SerializationException("Missing field Most Significant Bits"),
                /*lsb =*/ leastSignificantBits ?: throw SerializationException("Missing field Least Significant Bits"),
            )
        }
    }
}