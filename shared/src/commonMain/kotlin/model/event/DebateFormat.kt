package com.rohengiralt.debatex.model.event

import com.rohengiralt.debatex.dataStructure.ShortenableName
import com.rohengiralt.debatex.dataStructure.competitionTypes.OneVOneSpeaker
import com.rohengiralt.debatex.dataStructure.competitionTypes.Speaker
import com.rohengiralt.debatex.dataStructure.competitionTypes.TwoVTwoSpeaker
import kotlinx.serialization.Serializable

@Serializable
sealed class DebateFormat<T : Speaker>(
    val name: ShortenableName,
    val speakerType: Speaker.Type<T>
) {
    constructor(
        longName: String,
        shortName: String? = null,
        speakerType: Speaker.Type<T>
    ) : this(
        ShortenableName(
            longName,
            shortName
        ),
        speakerType
    )

    @Serializable
    object Debug : DebateFormat<OneVOneSpeaker>("Debug", "DBG", OneVOneSpeaker)

    @Serializable
    object LincolnDouglas : DebateFormat<OneVOneSpeaker>("Lincoln-Douglas", "LD", OneVOneSpeaker)

    @Serializable
    object PublicForum : DebateFormat<TwoVTwoSpeaker>("Public Forum", "PF", TwoVTwoSpeaker)

    @Serializable
    object BigQuestions : DebateFormat<OneVOneSpeaker>("Big Questions", "BQ", OneVOneSpeaker)

    @Serializable
    object Policy : DebateFormat<OneVOneSpeaker>("Policy", "PO", OneVOneSpeaker)

//    @Serializable
//    object Congress : DebateFormat<CongressSpeaker>("Congress", "CRS", CongressSpeaker)
}

