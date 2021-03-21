package com.rohengiralt.debatex.dataStructure.competitionTypes

import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

abstract class Speaker {
    @Serializable
    abstract class Type<T : Speaker>(
        val speakerKClass: KClass<T>
    )
}

sealed class OneVOneSpeaker : Speaker() {
    object Aff : OneVOneSpeaker()
    object Neg : OneVOneSpeaker()

    companion object : Type<OneVOneSpeaker>(OneVOneSpeaker::class) {
        val Both: Set<OneVOneSpeaker> = setOf(Aff, Neg)
    }
}

sealed class TwoVTwoSpeaker : Speaker() {
    object TeamOneFirstSpeaker : TwoVTwoSpeaker()
    object TeamOneSecondSpeaker : TwoVTwoSpeaker()
    object TeamTwoFirstSpeaker : TwoVTwoSpeaker()
    object TeamTwoSecondSpeaker : TwoVTwoSpeaker()

    companion object : Type<TwoVTwoSpeaker>(TwoVTwoSpeaker::class) {
        val FirstSpeakers: Set<TwoVTwoSpeaker> = setOf(
            TeamOneFirstSpeaker,
            TeamTwoFirstSpeaker,
        )
        val SecondSpeakers: Set<TwoVTwoSpeaker> = setOf(
            TeamOneSecondSpeaker,
            TeamTwoSecondSpeaker,
        )
        val AllSpeakers: Set<TwoVTwoSpeaker> = setOf(
            TeamOneFirstSpeaker,
            TeamTwoFirstSpeaker,
            TeamOneSecondSpeaker,
            TeamTwoSecondSpeaker,
        )
        val TeamOne: Set<TwoVTwoSpeaker> = setOf(
            TeamOneFirstSpeaker,
            TeamOneSecondSpeaker,
        )
        val TeamTwo: Set<TwoVTwoSpeaker> = setOf(
            TeamTwoFirstSpeaker,
            TeamTwoSecondSpeaker,
        )
    }
}

//
//import kotlinx.serialization.Polymorphic
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.modules.SerializersModule
//import kotlinx.serialization.modules.polymorphic
//import kotlinx.serialization.modules.subclass
//
//@Polymorphic
//interface Speaker<out T> where T : Speaker<T>, T : Enum<out T> {
//    fun equals(otherSpeaker: Speaker<*>?): Boolean = equals(other = otherSpeaker)
//}
//
//val speakerSerializerModule: SerializersModule = SerializersModule {
//    polymorphic(Speaker::class) {
//        subclass(OneVOneSpeaker.serializer())
//        subclass(TwoVTwoSpeaker.serializer())
//        subclass(CongressSpeaker.serializer())
//    }
//}
//
//@Serializable
//enum class OneVOneSpeaker : Speaker<OneVOneSpeaker> {
//    Aff, Neg;
//
//    companion object : CompetitionType<OneVOneSpeaker>() {
//        val Both: List<OneVOneSpeaker> = listOf(
//            Aff,
//            Neg
//        )
//    }
//}
//
//@Serializable
//enum class TwoVTwoSpeaker :
//    Speaker<TwoVTwoSpeaker> {
//    TeamOneFirstSpeaker, TeamOneSecondSpeaker, TeamTwoFirstSpeaker, TeamTwoSecondSpeaker;
//
//    companion object : CompetitionType<TwoVTwoSpeaker>() {
//        val FirstSpeakers: List<TwoVTwoSpeaker> =
//            listOf(
//                TeamOneFirstSpeaker,
//                TeamTwoFirstSpeaker
//            )
//        val SecondSpeakers: List<TwoVTwoSpeaker> =
//            listOf(
//                TeamOneSecondSpeaker,
//                TeamTwoSecondSpeaker
//            )
//        val AllSpeakers: List<TwoVTwoSpeaker> = listOf(
//            TeamOneFirstSpeaker,
//            TeamTwoFirstSpeaker,
//            TeamOneSecondSpeaker,
//            TeamTwoSecondSpeaker
//        )
//        val TeamOne: List<TwoVTwoSpeaker> = listOf(
//            TeamOneFirstSpeaker,
//            TeamOneSecondSpeaker
//        )
//        val TeamTwo: List<TwoVTwoSpeaker> = listOf(
//            TeamTwoFirstSpeaker,
//            TeamTwoSecondSpeaker
//        )
//    }
//}
//
//@Serializable
//enum class CongressSpeaker :
//    Speaker<CongressSpeaker> {
//    CongressPerson, Parliamentarian;
//    // TODO: Add more
//
//    companion object : CompetitionType<CongressSpeaker>()
//}