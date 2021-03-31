package com.rohengiralt.debatex.dataStructure.competitionTypes

import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

abstract class Speaker {
    @Serializable
    abstract class Type<T : Speaker>(
        val speakerKClass: KClass<T>,
        val all: Set<T>
    )
}

sealed class OneVOneSpeaker : Speaker() {
    object Aff : OneVOneSpeaker()
    object Neg : OneVOneSpeaker()

    companion object : Type<OneVOneSpeaker>(OneVOneSpeaker::class, setOf(Aff, Neg)) { //TODO: Require to include all
        val Both: Set<OneVOneSpeaker> inline get() = all
    }
}

sealed class TwoVTwoSpeaker : Speaker() {
    object TeamOneFirstSpeaker : TwoVTwoSpeaker()
    object TeamOneSecondSpeaker : TwoVTwoSpeaker()
    object TeamTwoFirstSpeaker : TwoVTwoSpeaker()
    object TeamTwoSecondSpeaker : TwoVTwoSpeaker()

    companion object : Type<TwoVTwoSpeaker>(
        TwoVTwoSpeaker::class,
        setOf(
            TeamOneFirstSpeaker,
            TeamTwoFirstSpeaker,
            TeamOneSecondSpeaker,
            TeamTwoSecondSpeaker,
        )
    ) {
        val FirstSpeakers: Set<TwoVTwoSpeaker> = setOf(
            TeamOneFirstSpeaker,
            TeamTwoFirstSpeaker,
        )
        val SecondSpeakers: Set<TwoVTwoSpeaker> = setOf(
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
        val AllSpeakers: Set<TwoVTwoSpeaker> inline get() = all
    }
}