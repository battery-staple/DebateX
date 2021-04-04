package com.rohengiralt.debatex.events

import com.rohengiralt.debatex.dataStructure.ShortenableName
import com.rohengiralt.debatex.dataStructure.SimpleName
import com.rohengiralt.debatex.dataStructure.competitionTypes.Speaker
import com.rohengiralt.debatex.model.event.AgeGroup
import com.rohengiralt.debatex.model.event.DebateFormat
import com.rohengiralt.debatex.model.event.EventModel
import com.rohengiralt.debatex.model.event.EventTags
import com.rohengiralt.debatex.model.event.Location
import com.rohengiralt.debatex.model.event.Organization
import com.rohengiralt.debatex.model.event.Region
import com.rohengiralt.debatex.model.event.SecondaryTimerChangeStrategy
import com.rohengiralt.debatex.model.timerModel.TimerModel
import com.rohengiralt.debatex.model.timerModel.wrap
import com.rohengiralt.debatex.random.nextString
import com.rohengiralt.debatex.random.randomList
import com.rohengiralt.debatex.random.randomSubset
import com.soywiz.klock.minutes
import kotlin.random.Random
import kotlin.random.nextInt

internal fun Random.nextDebateFormat(): DebateFormat<*> =
    debateFormats.random(this)

internal fun Random.nextEventTags(): EventTags =
    EventTags(
        country = (Location.values().toSet() + null).random(this),
        ageGroup = (AgeGroup.values().toSet() + null).random(this),
        organization = (Organization.values().toSet() + null).random(this),
        region = (Region.values().toSet() + null).random(this),
    )

internal fun Random.nextSecondaryTimerChangeStrategy(): SecondaryTimerChangeStrategy =
    secondaryTimerChangeStrategies.random(this)

internal fun Random.nextShortenableName(
    shortLength: Int = 5,
    longLength: Int = 20,
    allowedCharacters: Set<Char>? = null,
): ShortenableName =
    ShortenableName(
        short = SimpleName(nextString(shortLength, allowedCharacters)),
        long = SimpleName(nextString(longLength, allowedCharacters))
    )

internal fun <T : Speaker> Random.nextTimerModel(speakerType: Speaker.Type<T>): TimerModel<T> =
    TimerModel(
        ShortenableName(
            nextString(nextInt(0..50)),
            nextString(nextInt(0..50))
        ),
        nextDouble(0.0, 99.5).minutes.wrap(),
        speakerType.all
            .randomSubset(this)
            .toSet()
    )

internal fun Random.nextEventModel(
    format: DebateFormat<*> = nextDebateFormat(),
    tags: EventTags = nextEventTags(),
    type: Speaker.Type<*> = format.speakerType,
    totalPrimaryTimers: Int = nextInt(1..30),
    primaryTimers: List<TimerModel<*>> = randomTimerModelList(totalPrimaryTimers, format.speakerType, random = this),
    totalSecondaryTimers: Int = nextInt(1..30),
    nullSecondaryTimers: Boolean = nextInt(1..10) <= 1,
    secondaryTimers: List<TimerModel<*>>? =
        if (nullSecondaryTimers) null else
            randomTimerModelList(totalSecondaryTimers, format.speakerType, random = this),
    secondaryTimerChangeStrategy: SecondaryTimerChangeStrategy = nextSecondaryTimerChangeStrategy(),
): EventModel<*> =
    EventModel(format, tags, type, primaryTimers, secondaryTimers, secondaryTimerChangeStrategy)

internal fun <T : Speaker> randomTimerModelList(
    size: Int,
    speakerType: Speaker.Type<T>,
    unique: Boolean = false,
    random: Random,
): List<TimerModel<T>> =
    randomList(size, unique, random) { nextTimerModel(speakerType) }

internal fun randomEventModelList(
    size: Int,
    unique: Boolean = false,
    random: Random,
): List<EventModel<*>> =
    randomList(size, unique, random, Random::nextEventModel)

internal val debateFormats: Set<DebateFormat<*>> =
    setOf( //TODO: ensure contains all formats
        DebateFormat.Debug,
        DebateFormat.LincolnDouglas,
        DebateFormat.PublicForum,
        DebateFormat.BigQuestions,
        DebateFormat.Policy
    )

internal val secondaryTimerChangeStrategies: Set<SecondaryTimerChangeStrategy> =
    setOf( //TODO: ensure contains all strategies
        SecondaryTimerChangeStrategy.All,
        SecondaryTimerChangeStrategy.Any,
        SecondaryTimerChangeStrategy.Never
    )