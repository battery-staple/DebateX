@file:Suppress("UNUSED")

package com.rohengiralt.debatex.event

import com.rohengiralt.debatex.dataStructures.ShortenableName
import com.rohengiralt.debatex.dataStructures.TimePage
import com.rohengiralt.debatex.eventModifiers.DebateFormat
import com.rohengiralt.debatex.eventModifiers.EventTags
import kotlinx.serialization.Serializable

@Serializable
data class Event<out T : Speaker<*>>(
    val type: DebateFormat<T>,
    val tags: EventTags = EventTags.NONE,
    val overrideName: ShortenableName? = null,
    val pages: List<TimePage<T>>,
    val secondaryTimers: List<SecondaryTimer<T>>? = null
)

