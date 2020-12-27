package com.rohengiralt.debatex.model.event

import com.rohengiralt.debatex.dataStructure.ShortenableName
import com.rohengiralt.debatex.dataStructure.CongressSpeaker
import com.rohengiralt.debatex.dataStructure.OneVOneSpeaker
import com.rohengiralt.debatex.dataStructure.TwoVTwoSpeaker
import com.rohengiralt.debatex.dataStructure.Speaker
import kotlinx.serialization.Serializable

@Serializable // TODO: Test: does this serialize the type parameter too? If not, add custom serializer
sealed class DebateFormat<out T>(
    val name: ShortenableName
) where T : Speaker<*> {
    constructor(
        longName: String,
        shortName: String? = null
    ) : this(
        ShortenableName(
            longName,
            shortName
        )
    )

    @Serializable
    object Debug : DebateFormat<OneVOneSpeaker>("Debug", "DBG")

    @Serializable
    object LincolnDouglas : DebateFormat<OneVOneSpeaker>("Lincoln-Douglas", "LD")

    @Serializable
    object PublicForum : DebateFormat<TwoVTwoSpeaker>("Public Forum", "PF")

    @Serializable
    object BigQuestions : DebateFormat<OneVOneSpeaker>("Big Questions", "BQ")

    @Serializable
    object Policy : DebateFormat<OneVOneSpeaker>("Policy", "PO")

    @Serializable
    object Congress : DebateFormat<CongressSpeaker>("Congress")
}

