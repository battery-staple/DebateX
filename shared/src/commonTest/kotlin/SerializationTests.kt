package com.rohengiralt.debatex
//
//import com.rohengiralt.debatex.dataStructure.OneVOneSpeaker
//import com.rohengiralt.debatex.dataStructure.Speaker
//import com.rohengiralt.debatex.model.CountdownTimerModel
//import com.rohengiralt.debatex.model.TimePageModel
//import com.rohengiralt.debatex.model.event.*
//import com.soywiz.klock.minutes
//import kotlinx.serialization.PolymorphicSerializer
//import kotlinx.serialization.json.Json
//import kotlin.js.JsName
//import kotlin.test.Test
//import kotlin.test.assertEquals
//
//class SerializationTests {
//
//    private val json =
//        Json {
//            allowStructuredMapKeys = true
//            useArrayPolymorphism = true
//        }
//
//    private val debugEvent = EventModel(
//        type = DebateFormat.Debug,
//        tags = EventTags(
//            ageGroup = AgeGroup.HighSchoolNovice,
//            organization = Organization.NSDA,
//            country = Location.Netherlands
//        ),
//        pages = listOf(
//            TimePageModel(
//                shortName = "A Really Really Really Really Really Really Unnecessarily Long Name For a Round",
//                longName = "ARRRRRRULNFaR",
//                timer = CountdownTimerModel(2.minutes),
//                speaker = OneVOneSpeaker.Neg
//            ),
//            TimePageModel(
//                "R",
//                "",
//                CountdownTimerModel(1.minutes),
//                speaker = OneVOneSpeaker.Neg
//            ),
//            TimePageModel(
//                "Short Speech For Testing",
//                "SRFT",
//                CountdownTimerModel(0.5.minutes),
//                speaker = OneVOneSpeaker.Aff
//            )
//        )
//    )
//    private val debugEventJson: String =
//        """{"type":["com.rohengiralt.debatex.eventModifiers.DebateFormat.Debug",{}],"tags":{"country":"Netherlands","ageGroup":"HighSchoolNovice","organization":"NSDA"},"overrideName":null,"pages":[{"name":{"long":{"name":"A Really Really Really Really Really Really Unnecessarily Long Name For a Round"},"short":{"name":"ARRRRRRULNFaR"}},"timerModel":{"totalTime":2.0,"currentTime":2.0},"speakers":[["com.rohengiralt.debatex.dataStructures.LincolnDouglasSpeaker","Neg"]]},{"name":{"long":{"name":"R"},"short":{"name":""}},"timerModel":{"totalTime":1.0,"currentTime":1.0},"speakers":[["com.rohengiralt.debatex.dataStructures.LincolnDouglasSpeaker","Neg"]]},{"name":{"long":{"name":"Short Speech For Testing"},"short":{"name":"SRFT"}},"timerModel":{"totalTime":0.5,"currentTime":0.5},"speakers":[["com.rohengiralt.debatex.dataStructures.LincolnDouglasSpeaker","Aff"]]}],"secondaryTimers":null}"""
//
//    @Test
//    @JsName("SerializationOfEventsWorks")
//    fun `Serialization of events works`() {
//        println(
//            json.encodeToString(
//                EventModel.serializer(PolymorphicSerializer(Speaker::class)),
//                debugEvent
//            )
//        )
////        assertEquals(
////            json.encodeToString(
////                EventModel.serializer(PolymorphicSerializer(Speaker::class)),
////                debugEvent
////            ),
////            debugEventJson
////        )
//    }
//
//    @Test
//    @JsName("DeserializationOfEventsWorks")
//    fun `Deserialization of events works`() {
//        val parsedJsonEventModel: EventModel<Speaker<*>> =
//            json.decodeFromString(EventModel.serializer(PolymorphicSerializer(Speaker::class)), debugEventJson)
//
//        println("pjem: $parsedJsonEventModel")
//        assertEquals(parsedJsonEventModel.overrideName, debugEvent.overrideName)
//        assertEquals(parsedJsonEventModel.pages.size, debugEvent.pages.size)
//        println("hi")
//        for (index in parsedJsonEventModel.pages.indices) {
//            println("index=$index")
//            println(debugEvent.pages[index].timerModel.timer.currentTime)
//            assertEquals(
//                parsedJsonEventModel.pages[index].name,
//                debugEvent.pages[index].name
//            )
//            assertEquals(
//                parsedJsonEventModel.pages[index].timerModel.timer.currentTime.milliseconds,
//                debugEvent.pages[index].timerModel.timer.currentTime.milliseconds
//            )
//            assertEquals(
//                parsedJsonEventModel.pages[index].timerModel.timer.totalTime.milliseconds,
//                debugEvent.pages[index].timerModel.timer.totalTime.milliseconds
//            )
//        }
//        assertEquals(parsedJsonEventModel.secondaryTimerModels, debugEvent.secondaryTimerModels)
//        assertEquals(parsedJsonEventModel.type, debugEvent.type)
//    }
//}