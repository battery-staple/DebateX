package com.rohengiralt.debatex.dataStructures

import com.rohengiralt.debatex.event.CountdownTimer
import com.rohengiralt.debatex.event.Speaker
import com.rohengiralt.debatex.event.Timer
import kotlinx.serialization.Serializable

@Serializable
data class TimePage<out T : Speaker<*>>(
    val name: ShortenableName,
    private val timer: CountdownTimer,
    val speakers: List<T>
) : Timer by timer {
    init {
        require(speakers.isNotEmpty())
    }

    @Suppress("UNUSED")
    constructor(shortName: String, longName: String, timer: CountdownTimer, speakers: List<T>) :
            this(ShortenableName(shortName, longName), timer, speakers)

    @Suppress("UNUSED")
    constructor(name: ShortenableName, timer: CountdownTimer, speaker: T) :
            this(name, timer, listOf(speaker))

    @Suppress("UNUSED")
    constructor(shortName: String, longName: String, timer: CountdownTimer, speaker: T) :
            this(ShortenableName(shortName, longName), timer, listOf(speaker))
}

