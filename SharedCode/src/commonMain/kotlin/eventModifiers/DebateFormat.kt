package com.rohengiralt.debatex.eventModifiers

import com.rohengiralt.debatex.dataStructures.ShortenableName
import com.rohengiralt.debatex.event.CongressSpeaker
import com.rohengiralt.debatex.event.LincolnDouglasSpeaker
import com.rohengiralt.debatex.event.PublicForumSpeaker
import com.rohengiralt.debatex.event.Speaker
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
    object Debug : DebateFormat<LincolnDouglasSpeaker>("Debug", "DBG")

    @Serializable
    object LincolnDouglas : DebateFormat<LincolnDouglasSpeaker>("Lincoln-Douglas", "LD")

    @Serializable
    object PublicForum : DebateFormat<PublicForumSpeaker>("Public Forum", "PF")

    @Serializable
    object Congress : DebateFormat<CongressSpeaker>("Congress")
}

