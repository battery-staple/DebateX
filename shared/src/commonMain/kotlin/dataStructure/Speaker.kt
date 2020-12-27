package com.rohengiralt.debatex.dataStructure

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Polymorphic
interface Speaker<out T> where T : Speaker<T>, T : Enum<out T> {
    fun equals(otherSpeaker: Speaker<*>?): Boolean = equals(other = otherSpeaker)
}

val speakerSerializerModule: SerializersModule = SerializersModule {
    polymorphic(Speaker::class) {
        subclass(OneVOneSpeaker.serializer())
        subclass(TwoVTwoSpeaker.serializer())
        subclass(CongressSpeaker.serializer())
    }
}

@Serializable
enum class OneVOneSpeaker : Speaker<OneVOneSpeaker> {
    Aff, Neg;

    companion object {
        val Both: List<OneVOneSpeaker> = listOf(
            Aff,
            Neg
        )
    }
}

@Serializable
enum class TwoVTwoSpeaker :
    Speaker<TwoVTwoSpeaker> {
    TeamOneFirstSpeaker, TeamOneSecondSpeaker, TeamTwoFirstSpeaker, TeamTwoSecondSpeaker;

    companion object {
        val FirstSpeakers: List<TwoVTwoSpeaker> =
            listOf(
                TeamOneFirstSpeaker,
                TeamTwoFirstSpeaker
            )
        val SecondSpeakers: List<TwoVTwoSpeaker> =
            listOf(
                TeamOneSecondSpeaker,
                TeamTwoSecondSpeaker
            )
        val AllSpeakers: List<TwoVTwoSpeaker> = listOf(
            TeamOneFirstSpeaker,
            TeamTwoFirstSpeaker,
            TeamOneSecondSpeaker,
            TeamTwoSecondSpeaker
        )
        val TeamOne: List<TwoVTwoSpeaker> = listOf(
            TeamOneFirstSpeaker,
            TeamOneSecondSpeaker
        )
        val TeamTwo: List<TwoVTwoSpeaker> = listOf(
            TeamTwoFirstSpeaker,
            TeamTwoSecondSpeaker
        )
    }
}

@Serializable
enum class CongressSpeaker :
    Speaker<CongressSpeaker> {
    CongressPerson, Parliamentarian
    // TODO: Add
}