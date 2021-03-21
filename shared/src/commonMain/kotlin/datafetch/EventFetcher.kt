package com.rohengiralt.debatex.datafetch
//
//import com.benasher44.uuid.Uuid
//import com.rohengiralt.debatex.Logger
//import com.rohengiralt.debatex.dataStructure.competitionTypes.OneVOneSpeaker
//import com.rohengiralt.debatex.dataStructure.SimpleName
//import com.rohengiralt.debatex.dataStructure.competitionTypes.Speaker
//import com.rohengiralt.debatex.dataStructure.competitionTypes.TwoVTwoSpeaker
//import com.rohengiralt.debatex.defaultLogger
//import com.rohengiralt.debatex.model.timerModel.TimerCountStrategy
//import com.rohengiralt.debatex.model.timerModel.TimerModel
//import com.rohengiralt.debatex.model.event.AgeGroup
//import com.rohengiralt.debatex.model.event.DebateFormat
//import com.rohengiralt.debatex.model.event.EventModel
//import com.rohengiralt.debatex.model.event.EventTags
//import com.rohengiralt.debatex.model.event.Location
//import com.rohengiralt.debatex.model.event.Organization
//import com.rohengiralt.debatex.model.event.Region
//import com.rohengiralt.debatex.model.event.SecondaryTimerModel
//import com.soywiz.klock.TimeSpan
//import com.soywiz.klock.minutes
//import com.soywiz.klock.seconds
//import kotlinx.atomicfu.atomic
//import kotlinx.serialization.KSerializer
//import kotlinx.serialization.PolymorphicSerializer
//import kotlinx.serialization.SerializationException
//import kotlinx.serialization.json.Json
//
//typealias MockDirectory = MutableMap<String, String>
//
//val presetEventsMockDirectory: MockDirectory = mutableMapOf()
//val customEventsMockDirectory: MockDirectory = mutableMapOf()
//
//private val eventModelSerializer: KSerializer<EventModel<Speaker<*>>>
//    inline get() = EventModel.serializer(PolymorphicSerializer(Speaker::class))
//
//class UuidEventFetcher<T : Speaker<*>>(val uuid: Uuid) : DataFetcher<EventModel<T>> {
//    override fun fetchOrNull(): EventModel? = try {
//        Json.encodeToString(UuidSerializer, uuid).let { uuid ->
//            (presetEventsMockDirectory + customEventsMockDirectory)[uuid]?.let { presetEvent ->
//                Json.decodeFromString(
//                    eventModelSerializer,
//                    presetEvent
//                )
//            }
//        }
//    } catch (e: SerializationException) {
//        logger.error("Serialization failed in attempting to read eventModel with uuid $uuid")
//        null
//    } as? EventModel<T>
//
//
//    override fun fetch(): EventModel =
//        fetchOrNull() ?: throw IllegalArgumentException("Could not find event model with uuid $uuid")
//
//    private val logger = defaultLogger
//}
//
//private inline fun allEventFetchersFromDirectory(directory: MockDirectory, logger: Logger) =
//    directory.keys.mapNotNull { serializedUuid ->
//        try {
//            Json.decodeFromString(
//                UuidSerializer,
//                serializedUuid
//            )
//        } catch (e: SerializationException) {
//            logger.error("Unable to deserialize preset uuid: $serializedUuid")
//            return@mapNotNull null
//        }
//    }.map { UuidEventFetcher<Speaker<*>>(it) }
//
//object PresetEventsFetcher : DataFetcher<List<DataFetcher<EventModel<*>>>> {
//
//    override fun fetch(): List<DataFetcher<EventModel<*>>> {
//        maybeUpdatePresets()
//
//        return allEventFetchersFromDirectory(presetEventsMockDirectory, logger)
//
////        return presetEventsMockDirectory.values.mapNotNull { serializedEvent ->
////            try {
////                DefaultSerializer.decodeFromString(
////                    eventModelSerializer,
////                    serializedEvent
////                )
////            } catch (e: SerializationException) {
////                logger.error("Unable to deserialize preset event: $serializedEvent")
////                return@mapNotNull null
////            }
////        }.map(::ConstantModelFetcher)
//    }
//
//    private val hasGottenFromAuthoritativeSourceSinceLaunch = atomic(false)
//    private fun maybeUpdatePresets() {
//        if (!hasGottenFromAuthoritativeSourceSinceLaunch.value) {
//            try {
//                getPresetsFromAuthoritativeSource().forEach { (serializedUuid, serializedEvent) ->
//                    presetEventsMockDirectory[serializedUuid] = serializedEvent
//                }
//                hasGottenFromAuthoritativeSourceSinceLaunch.value = true
//            } catch (e: SerializationException) {
//                logger.error("Could not serialize events from server. Old caches will be used instead.")
//            }
//        }
//    }
//
//    private inline fun getPresetsFromAuthoritativeSource(): List<Pair<String, String>> { //TODO: Change to actually get from server
//        val events = with(EventInitUtils) {
//            listOf(
//                EventModel(
//                    type = DebateFormat.Debug,
//                    tags = EventTags(
//                        ageGroup = AgeGroup.MiddleSchool,
//                        country = Location.Britain
//                    ),
//                    pageFetchers = listOf(
//                        ConstantTimePageModel(
//                            shortName = "shr",
//                            longName = @Suppress("SpellCheckingInspection") "longnameee\neeeee\ne\neee\ree\r\n\n\nee",
//                            timer = SettingsCountStrategyTimerFetcher(30.seconds),
//                            speakers = TwoVTwoSpeaker.TeamOne
//                        )
//                    )
//                ),
//                EventModel(
//                    type = DebateFormat.Debug,
//                    tags = EventTags(
//                        ageGroup = AgeGroup.HighSchoolNovice,
//                        organization = Organization.NSDA,
//                        country = Location.Netherlands
//                    ),
//                    pageFetchers = listOf(
//                        ConstantTimePageModel(
//                            shortName = "A Really Really Really Really Really Really Unnecessarily Long Name For a Round",
//                            longName = "ARRRRRRULNFaR",
//                            timer = SettingsCountStrategyTimerFetcher(2.minutes),
//                            speaker = OneVOneSpeaker.Neg
//                        ),
//                        ConstantTimePageModel(
//                            "R",
//                            "",
//                            SettingsCountStrategyTimerFetcher(1.minutes),
//                            speaker = OneVOneSpeaker.Neg
//                        ),
//                        ConstantTimePageModel(
//                            "Short Speech For Testing",
//                            "SRFT",
//                            SettingsCountStrategyTimerFetcher(0.5.minutes),
//                            speaker = OneVOneSpeaker.Aff
//                        )
//                    )
//                ),
//                EventModel(
//                    type = DebateFormat.LincolnDouglas,
//                    tags = EventTags(
//                        organization = Organization.NSDA,
//                    ),
//                    pageFetchers = listOf(
//                        ConstantTimePageModel(
//                            "Affirmative Constructive",
//                            "AC",
//                            SettingsCountStrategyTimerFetcher(6.minutes),
//                            speaker = OneVOneSpeaker.Aff
//                        ),
//                        ConstantTimePageModel(
//                            "Negative Cross-Examination",
//                            "NCX",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            speakers = OneVOneSpeaker.Both
//                        ),
//                        ConstantTimePageModel(
//                            "Negative Constructive/First Rebuttal",
//                            "NC/1NR",
//                            SettingsCountStrategyTimerFetcher(7.minutes),
//                            speaker = OneVOneSpeaker.Neg
//                        ),
//                        ConstantTimePageModel(
//                            "Affirmative Cross-Examination",
//                            "ACX",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            speakers = OneVOneSpeaker.Both
//                        ),
//                        ConstantTimePageModel(
//                            "First Affirmative Rebuttal",
//                            "1AR",
//                            SettingsCountStrategyTimerFetcher(4.minutes),
//                            speaker = OneVOneSpeaker.Aff
//                        ),
//                        ConstantTimePageModel(
//                            "Second Negative Rebuttal",
//                            "2NR",
//                            SettingsCountStrategyTimerFetcher(6.minutes),
//                            speaker = OneVOneSpeaker.Neg
//                        ),
//                        ConstantTimePageModel(
//                            "Second Affirmative Rebuttal",
//                            "2AR",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            speaker = OneVOneSpeaker.Aff
//                        )
//                    ),
//                    secondaryTimerModels = listOf(
//                        SecondaryTimerModel(
//                            SimpleName("Aff Prep"),
//                            ConstantModelFetcher(
//                                CountdownTimer(
//                                    totalTime = 4.minutes
//                                )
//                            ),
//                            OneVOneSpeaker.Aff
//                        ),
//                        SecondaryTimerModel(
//                            SimpleName("Neg Prep"),
//                            CountdownTimer(
//                                totalTime = 4.minutes
//                            ),
//                            OneVOneSpeaker.Neg
//                        )
//                    )
//                ),
//                EventModel(
//                    type = DebateFormat.LincolnDouglas,
//                    tags = EventTags(
//                        organization = Organization.NSDA,
//                        region = Region.National
//                    ),
//                    pageFetchers = listOf(
//                        ConstantTimePageModel(
//                            "Affirmative Constructive",
//                            "AC",
//                            SettingsCountStrategyTimerFetcher(6.minutes),
//                            speaker = OneVOneSpeaker.Aff
//                        ),
//                        ConstantTimePageModel(
//                            "Negative Cross-Examination",
//                            "NCX",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            speakers = OneVOneSpeaker.Both
//                        ),
//                        ConstantTimePageModel(
//                            "Negative Constructive/First Rebuttal",
//                            "NC/1NR",
//                            SettingsCountStrategyTimerFetcher(7.minutes),
//                            speaker = OneVOneSpeaker.Neg
//                        ),
//                        ConstantTimePageModel(
//                            "Affirmative Cross-Examination",
//                            "ACX",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            speakers = OneVOneSpeaker.Both
//                        ),
//                        ConstantTimePageModel(
//                            "First Affirmative Rebuttal",
//                            "1AR",
//                            SettingsCountStrategyTimerFetcher(4.minutes),
//                            speaker = OneVOneSpeaker.Aff
//                        ),
//                        ConstantTimePageModel(
//                            "Second Negative Rebuttal",
//                            "2NR",
//                            SettingsCountStrategyTimerFetcher(6.minutes),
//                            speaker = OneVOneSpeaker.Neg
//                        ),
//                        ConstantTimePageModel(
//                            "Second Affirmative Rebuttal",
//                            "2AR",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            speaker = OneVOneSpeaker.Aff
//                        )
//                    ),
//                    secondaryTimerModels = listOf(
//                        SecondaryTimerModel(
//                            SimpleName("Aff Prep"),
//                            CountdownTimer(
//                                totalTime = 5.minutes
//                            ),
//                            OneVOneSpeaker.Aff
//                        ),
//                        SecondaryTimerModel(
//                            SimpleName("Neg Prep"),
//                            CountdownTimer(
//                                totalTime = 5.minutes
//                            ),
//                            OneVOneSpeaker.Neg
//                        )
//                    )
//                ),
//                EventModel(
//                    type = DebateFormat.PublicForum,
//                    tags = EventTags(organization = Organization.NSDA),
//                    pageFetchers = listOf(
//                        ConstantTimePageModel(
//                            "Team A Constructive",
//                            "AC",
//                            SettingsCountStrategyTimerFetcher(6.minutes),
//                            speaker = TwoVTwoSpeaker.TeamOneFirstSpeaker
//                        ),
//                        ConstantTimePageModel(
//                            "Team B Constructive",
//                            "BC",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            speaker = TwoVTwoSpeaker.TeamTwoFirstSpeaker
//                        ),
//                        ConstantTimePageModel(
//                            "First Speakers' Crossfire",
//                            "1CX",
//                            SettingsCountStrategyTimerFetcher(7.minutes),
//                            speakers = TwoVTwoSpeaker.FirstSpeakers
//                        ),
//                        ConstantTimePageModel(
//                            "Team A Rebuttal",
//                            "AR",
//                            SettingsCountStrategyTimerFetcher(4.minutes),
//                            speaker = TwoVTwoSpeaker.TeamOneSecondSpeaker
//                        ),
//                        ConstantTimePageModel(
//                            "Team B Rebuttal",
//                            "BR",
//                            SettingsCountStrategyTimerFetcher(6.minutes),
//                            speaker = TwoVTwoSpeaker.TeamTwoSecondSpeaker
//                        ),
//                        ConstantTimePageModel(
//                            "Second Speakers' Crossfire",
//                            "2CX",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            speakers = TwoVTwoSpeaker.SecondSpeakers
//                        ),
//                        ConstantTimePageModel(
//                            "Team A Summary",
//                            "AS",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            speaker = TwoVTwoSpeaker.TeamOneFirstSpeaker
//                        ),
//                        ConstantTimePageModel(
//                            "Team B Summary",
//                            "BS",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            speaker = TwoVTwoSpeaker.TeamTwoFirstSpeaker
//                        ),
//                        ConstantTimePageModel(
//                            "Grand Crossfire",
//                            "GC",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            speakers = TwoVTwoSpeaker.AllSpeakers
//                        ),
//                        ConstantTimePageModel(
//                            "Team A Final Focus",
//                            "AFF",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            speaker = TwoVTwoSpeaker.TeamOneSecondSpeaker
//                        ),
//                        ConstantTimePageModel(
//                            "Team B Final Focus",
//                            "BFF",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            speaker = TwoVTwoSpeaker.TeamTwoSecondSpeaker
//                        )
//                    ),
//                    secondaryTimerModels = listOf(
//                        SecondaryTimerModel(
//                            SimpleName("Team A Prep"),
//                            CountdownTimer(
//                                totalTime = 2.minutes
//                            ), TwoVTwoSpeaker.TeamOne
//                        ),
//                        SecondaryTimerModel(
//                            SimpleName("Team B Prep"),
//                            CountdownTimer(
//                                totalTime = 2.minutes
//                            ), TwoVTwoSpeaker.TeamTwo
//                        )
//                    ),
//                    secondaryTimersAutomaticChangeMatchMode = SecondaryTimersAutomaticChangeMatchMode.Any
//                ),
//                EventModel(
//                    type = DebateFormat.BigQuestions,
//                    tags = EventTags(organization = Organization.NSDA),
//                    pageFetchers = listOf(
//                        ConstantTimePageModel(
//                            "Affirmative Constructive",
//                            "ACT",
//                            SettingsCountStrategyTimerFetcher(5.minutes),
//                            OneVOneSpeaker.Aff
//                        ),
//                        ConstantTimePageModel(
//                            "Negative Constructive",
//                            "NCT",
//                            SettingsCountStrategyTimerFetcher(5.minutes),
//                            OneVOneSpeaker.Neg
//                        ),
//                        ConstantTimePageModel(
//                            "Question Segment",
//                            "1QS",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            OneVOneSpeaker.Both
//                        ),
//                        ConstantTimePageModel(
//                            "Affirmative Rebuttal",
//                            "ARB",
//                            SettingsCountStrategyTimerFetcher(4.minutes),
//                            OneVOneSpeaker.Aff
//                        ),
//                        ConstantTimePageModel(
//                            "Negative Rebuttal",
//                            "NRB",
//                            SettingsCountStrategyTimerFetcher(4.minutes),
//                            OneVOneSpeaker.Neg
//                        ),
//                        ConstantTimePageModel(
//                            "Second Question Segment",
//                            "NRB",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            OneVOneSpeaker.Both
//                        ),
//                        ConstantTimePageModel(
//                            "Affirmative Consolidation",
//                            "ACS",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            OneVOneSpeaker.Aff
//                        ),
//                        ConstantTimePageModel(
//                            "Negative Consolidation",
//                            "NCS",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            OneVOneSpeaker.Neg
//                        ),
//                        ConstantTimePageModel(
//                            "Affirmative Rationale",
//                            "ARL",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            OneVOneSpeaker.Aff
//                        ),
//                        ConstantTimePageModel(
//                            "Negative Rationale",
//                            "NRL",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            OneVOneSpeaker.Neg
//                        )
//                    ),
//                    secondaryTimerModels = listOf(
//                        SecondaryTimerModel(
//                            SimpleName("Aff Prep"),
//                            CountdownTimer(3.minutes),
//                            OneVOneSpeaker.Aff
//                        ),
//                        SecondaryTimerModel(
//                            SimpleName("Neg Prep"),
//                            CountdownTimer(3.minutes),
//                            OneVOneSpeaker.Neg
//                        )
//                    )
//                ),
//                EventModel(
//                    type = DebateFormat.Policy,
//                    tags = EventTags(organization = Organization.NSDA),
//                    pageFetchers = listOf(
//                        ConstantTimePageModel(
//                            "First Affirmative Constructive",
//                            "1AC",
//                            SettingsCountStrategyTimerFetcher(8.minutes),
//                            OneVOneSpeaker.Aff
//                        ),
//                        ConstantTimePageModel(
//                            "Negative Cross-Examination",
//                            "1NCX",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            OneVOneSpeaker.Both
//                        ),
//                        ConstantTimePageModel(
//                            "First Negative Constructive",
//                            "1NC",
//                            SettingsCountStrategyTimerFetcher(8.minutes),
//                            OneVOneSpeaker.Neg
//                        ),
//                        ConstantTimePageModel(
//                            "Affirmative Cross-Examination",
//                            "1ACX",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            OneVOneSpeaker.Both
//                        ),
//                        ConstantTimePageModel(
//                            "Second Affirmative Constructive",
//                            "2AC",
//                            SettingsCountStrategyTimerFetcher(8.minutes),
//                            OneVOneSpeaker.Aff
//                        ),
//                        ConstantTimePageModel(
//                            "Negative Cross-Examination",
//                            "2NCX",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            OneVOneSpeaker.Both
//                        ),
//                        ConstantTimePageModel(
//                            "Negative Cross-Examination",
//                            "2NC",
//                            SettingsCountStrategyTimerFetcher(3.minutes),
//                            OneVOneSpeaker.Both
//                        )
//                    )
//                )
//            ) //TODO: Get from filesystem
//        }
//
//        return events.map { event ->
//            Pair(
//                Json.encodeToString(
//                    UuidSerializer,
//                    event.uuid
//                ),
//                Json.encodeToString<EventModel<Speaker<*>>>(
//                    eventModelSerializer,
//                    event
//                )
//            )
//        }
//    }
//
//    inline fun invoke(): PresetEventsFetcher = this
//
//    private object EventInitUtils {
//        @Suppress("FunctionName")
//        inline fun CountdownTimer(totalTime: TimeSpan): TimerModel =
//            TimerModel(
//                totalTime,
//                TimerCountStrategy.CountDown
//            )
//
//        @Suppress("FunctionName")
//        inline fun <T : Speaker<*>> ConstantTimePageModel(
//            longName: String,
//            shortName: String,
//            timer: DataFetcher<TimerModel>,
//            speakers: List<T>,
//        ): DataFetcher<TimePageModel<T>> = ConstantModelFetcher(
//            TimePageModel(longName, shortName, timer, speakers)
//        )
//
//        @Suppress("FunctionName")
//        inline fun <T : Speaker<*>> ConstantTimePageModel(
//            longName: String,
//            shortName: String,
//            timer: DataFetcher<TimerModel>,
//            speaker: T,
//        ): DataFetcher<TimePageModel<T>> = ConstantModelFetcher(
//            TimePageModel(longName, shortName, timer, speaker)
//        )
//    }
//
//    private val logger = defaultLogger
//}
//
//object CustomEventsFetcher : DataFetcher<List<DataFetcher<EventModel<*>>>> {
//    override fun fetch(): /*Mutable*/List<DataFetcher<EventModel<*>>> =
//        allEventFetchersFromDirectory(customEventsMockDirectory, logger)
//
//    private val logger = defaultLogger
//}