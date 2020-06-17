package com.rohengiralt.debatex.event

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerialModule
import kotlinx.serialization.modules.SerializersModule

@Polymorphic
interface Speaker<out T> where T : Speaker<T>, T : Enum<out T>

val speakerSerializersModule: SerialModule = SerializersModule {
    polymorphic(Speaker::class) {
        LincolnDouglasSpeaker::class with LincolnDouglasSpeaker.serializer()
        PublicForumSpeaker::class with PublicForumSpeaker.serializer()
        CongressSpeaker::class with CongressSpeaker.serializer()
    }
}

@Serializable
enum class LincolnDouglasSpeaker : Speaker<LincolnDouglasSpeaker> {
    Aff, Neg;

    companion object {
        val Both: List<LincolnDouglasSpeaker> = listOf(Aff, Neg)
    }
}

@Serializable
enum class PublicForumSpeaker : Speaker<PublicForumSpeaker> {
    TeamOneFirstSpeaker, TeamOneSecondSpeaker, TeamTwoFirstSpeaker, TeamTwoSecondSpeaker;

    companion object {
        val FirstSpeakers: List<PublicForumSpeaker> =
            listOf(TeamOneFirstSpeaker, TeamTwoFirstSpeaker)
        val SecondSpeakers: List<PublicForumSpeaker> =
            listOf(TeamOneSecondSpeaker, TeamTwoSecondSpeaker)
        val AllSpeakers: List<PublicForumSpeaker> = listOf(
            TeamOneFirstSpeaker,
            TeamTwoFirstSpeaker,
            TeamOneSecondSpeaker,
            TeamTwoSecondSpeaker
        )
        val TeamOne: List<PublicForumSpeaker> = listOf(TeamOneFirstSpeaker, TeamOneSecondSpeaker)
        val TeamTwo: List<PublicForumSpeaker> = listOf(TeamTwoFirstSpeaker, TeamTwoSecondSpeaker)
    }
}

@Serializable
enum class CongressSpeaker : Speaker<CongressSpeaker> {
    CongressPerson, Parliamentarian
    // TODO: Add
}