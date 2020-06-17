package com.rohengiralt.debatex

import com.rohengiralt.debatex.dataStructures.TimePage
import com.rohengiralt.debatex.event.*
import com.rohengiralt.debatex.eventModifiers.*
import com.soywiz.klock.minutes
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LogicTests {
    @Test
    fun `True is true`() {
        assertTrue(true)
    }

    @Test
    fun `False is false`() {
        assertTrue(false == false)
    }

    @Test
    fun `True is not false`() {
        assertTrue(true != false)
    }
}

class ColorTests

class SerializationTests {
    private val json = Json(
        JsonConfiguration.Stable.copy(useArrayPolymorphism = true),
        context = speakerSerializersModule
    )
    private val debugEvent = Event(
        type = DebateFormat.Debug,
        tags = EventTags(
            ageGroup = AgeGroup.HighSchoolNovice,
            organization = Organization.NSDA,
            country = Location.Netherlands
        ),
        pages = listOf(
            TimePage(
                shortName = "A Really Really Really Really Really Really Unnecessarily Long Name For a Round",
                longName = "ARRRRRRULNFaR",
                timer = CountdownTimer(2.minutes),
                speaker = LincolnDouglasSpeaker.Neg
            ),
            TimePage(
                "R",
                "",
                CountdownTimer(1.minutes),
                speaker = LincolnDouglasSpeaker.Neg
            ),
            TimePage(
                "Short Speech For Testing",
                "SRFT",
                CountdownTimer(0.5.minutes),
                speaker = LincolnDouglasSpeaker.Aff
            )
        )
    )
    private val debugEventJson: String =
        """{"type":["com.rohengiralt.debatex.eventModifiers.DebateFormat.Debug",{}],"tags":{"country":"Netherlands","ageGroup":"HighSchoolNovice","organization":"NSDA"},"overrideName":null,"pages":[{"name":{"name":"A Really Really Really Really Really Really Unnecessarily Long Name For a Round","shortName":"ARRRRRRULNFaR"},"timer":{"totalTime":2.0,"currentTime":2.0},"speakers":[["com.rohengiralt.debatex.event.LincolnDouglasSpeaker","Neg"]]},{"name":{"name":"R","shortName":""},"timer":{"totalTime":1.0,"currentTime":1.0},"speakers":[["com.rohengiralt.debatex.event.LincolnDouglasSpeaker","Neg"]]},{"name":{"name":"Short Speech For Testing","shortName":"SRFT"},"timer":{"totalTime":0.5,"currentTime":0.5},"speakers":[["com.rohengiralt.debatex.event.LincolnDouglasSpeaker","Aff"]]}],"secondaryTimers":null}"""

//    @Test
//    fun serialize() {
//        println(json.stringify(Event.serializer(PolymorphicSerializer(Speaker::class)), debugEvent))
//    }

    @Test
    fun `Serialization of events works`() {
        println(json.stringify(Event.serializer(PolymorphicSerializer(Speaker::class)), debugEvent))
        assertEquals(
            json.stringify(Event.serializer(PolymorphicSerializer(Speaker::class)), debugEvent),
            debugEventJson
        )
    }

    @Test
    @ImplicitReflectionSerializer
    fun `Deserialization of events works`() {
        val parsedJsonEvent: Event<Speaker<*>> =
            json.parse(Event.serializer(PolymorphicSerializer(Speaker::class)), debugEventJson)
        assertEquals(parsedJsonEvent.overrideName, debugEvent.overrideName)
        assertEquals(parsedJsonEvent.pages.size, debugEvent.pages.size)
        for (index in parsedJsonEvent.pages.indices) {
            assertEquals(
                parsedJsonEvent.pages[index].name,
                debugEvent.pages[index].name
            )
            assertEquals(
                parsedJsonEvent.pages[index].currentTime,
                debugEvent.pages[index].currentTime
            )
            assertEquals(
                parsedJsonEvent.pages[index].totalTime,
                debugEvent.pages[index].totalTime
            )
        }
        assertEquals(parsedJsonEvent.secondaryTimers, debugEvent.secondaryTimers)
        assertEquals(parsedJsonEvent.type, debugEvent.type)
    }
}