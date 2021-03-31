package com.rohengiralt.debatex.di

import com.rohengiralt.debatex.Logger
import com.rohengiralt.debatex.dataStructure.ShortenableName
import com.rohengiralt.debatex.dataStructure.competitionTypes.OneVOneSpeaker
import com.rohengiralt.debatex.dataStructure.competitionTypes.TwoVTwoSpeaker
import com.rohengiralt.debatex.model.event.DebateFormat
import com.rohengiralt.debatex.model.event.EventModel
import com.rohengiralt.debatex.model.event.EventTags
import com.rohengiralt.debatex.model.event.Organization
import com.rohengiralt.debatex.model.event.Region
import com.rohengiralt.debatex.model.event.SecondaryTimerChangeStrategy
import com.rohengiralt.debatex.model.sectionModel.EventsSectionModel
import com.rohengiralt.debatex.model.timerModel.TimerModel
import com.rohengiralt.debatex.model.timerModel.wrap
import com.rohengiralt.debatex.settings.SettingsAccess
import com.rohengiralt.debatex.settings.settingsStore.RusshwolfSettingsStoreAdapter
import com.russhwolf.settings.Settings
import com.russhwolf.settings.invoke
import com.soywiz.klock.minutes
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoin() {
    startKoin {
        logger(KoinLoggerAdapter(Logger("Koin")))
        modules(models, settingsAccess)
    }
}

private val models = module {
    single {
        EventsSectionModel(
            listOf(
//            EventModel(
//                format = DebateFormat.Debug,
//                tags = EventTags(
//                    ageGroup = AgeGroup.MiddleSchool,
//                    country = Location.Britain
//                ),
//                type = OneVOneSpeaker,
//                primaryTimers = listOf(
//                    TimerModel(
//                        name = ShortenableName(
//                            "shr",
//                            @Suppress("SpellCheckingInspection") "longnameee\neeeee\ne\neee\ree\r\n\n\nee"
//                        ),
//                        totalTime = 30.seconds.wrap(),
//                        speakers = setOf(OneVOneSpeaker.Aff)
//                    )
//                ),
//                secondaryTimerChangeStrategy = SecondaryTimerChangeStrategy.All
//            ),
//            EventModel(
//                format = DebateFormat.Debug,
//                tags = EventTags(
//                    ageGroup = AgeGroup.HighSchoolNovice,
//                    organization = Organization.NSDA,
//                    country = Location.Netherlands
//                ),
//                type = OneVOneSpeaker,
//                primaryTimers = listOf(
//                    TimerModel(
//                        name = ShortenableName(
//                            "A Really Really Really Really Really Really Unnecessarily Long Name For a Round",
//                            "ARRRRRRULNFaR"
//                        ),
//                        totalTime = 2.minutes.wrap(),
//                        speakers = setOf(OneVOneSpeaker.Neg)
//                    ),
//                    TimerModel(
//                        ShortenableName(
//                            "R",
//                            ""
//                        ),
//                        1.minutes.wrap(),
//                        setOf(OneVOneSpeaker.Neg)
//                    ),
//                    TimerModel(
//                        ShortenableName(
//                            "Short Speech For Testing",
//                            "SRFT"
//                        ),
//                        0.5.minutes.wrap(),
//                        setOf(OneVOneSpeaker.Aff)
//                    )
//                ),
//                secondaryTimerChangeStrategy = SecondaryTimerChangeStrategy.All
//            ),
            EventModel(
                format = DebateFormat.LincolnDouglas,
                tags = EventTags(
                    organization = Organization.NSDA,

                    ),
                type = OneVOneSpeaker,
                primaryTimers = listOf(
                    TimerModel(
                        ShortenableName(
                            "Affirmative Constructive",
                            "AC"
                        ),
                        6.minutes.wrap(),
                        setOf(OneVOneSpeaker.Aff)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Negative Cross-Examination",
                            "NCX"
                        ),
                        3.minutes.wrap(),
                        OneVOneSpeaker.Both
                    ),
                    TimerModel(
                        ShortenableName(
                            "Negative Constructive/First Rebuttal",
                            "NC/1NR"
                        ),
                        7.minutes.wrap(),
                        setOf(OneVOneSpeaker.Neg)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Affirmative Cross-Examination",
                            "ACX"
                        ),
                        3.minutes.wrap(),
                        OneVOneSpeaker.Both
                    ),
                    TimerModel(
                        ShortenableName(
                            "First Affirmative Rebuttal",
                            "1AR"
                        ),
                        4.minutes.wrap(),
                        setOf(OneVOneSpeaker.Aff)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Second Negative Rebuttal",
                            "2NR"
                        ),
                        6.minutes.wrap(),
                        setOf(OneVOneSpeaker.Neg)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Second Affirmative Rebuttal",
                            "2AR"
                        ),
                        3.minutes.wrap(),
                        setOf(OneVOneSpeaker.Aff)
                    )
                ),
                secondaryTimers = listOf(
                    TimerModel(
                        ShortenableName("Aff Prep", "Aff"),
                        4.minutes.wrap(),
                        setOf(OneVOneSpeaker.Aff)
                    ),
                    TimerModel(
                        ShortenableName("Neg Prep", "Neg"),
                        4.minutes.wrap(),
                        setOf(OneVOneSpeaker.Neg)
                    )
                ),
                secondaryTimerChangeStrategy = SecondaryTimerChangeStrategy.All
            ),
            EventModel(
                format = DebateFormat.LincolnDouglas,
                tags = EventTags(
                    organization = Organization.NSDA,
                    region = Region.National,
                ),
                type = OneVOneSpeaker,
                primaryTimers = listOf(
                    TimerModel(
                        ShortenableName(
                            "Affirmative Constructive",
                            "AC"
                        ),
                        6.minutes.wrap(),
                        setOf(OneVOneSpeaker.Aff)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Negative Cross-Examination",
                            "NCX"
                        ),
                        3.minutes.wrap(),
                        OneVOneSpeaker.Both
                    ),
                    TimerModel(
                        ShortenableName(
                            "Negative Constructive/First Rebuttal",
                            "NC/1NR"
                        ),
                        7.minutes.wrap(),
                        setOf(OneVOneSpeaker.Neg)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Affirmative Cross-Examination",
                            "ACX"
                        ),
                        3.minutes.wrap(),
                        OneVOneSpeaker.Both
                    ),
                    TimerModel(
                        ShortenableName(
                            "First Affirmative Rebuttal",
                            "1AR"
                        ),
                        4.minutes.wrap(),
                        setOf(OneVOneSpeaker.Aff)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Second Negative Rebuttal",
                            "2NR"
                        ),
                        6.minutes.wrap(),
                        setOf(OneVOneSpeaker.Neg)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Second Affirmative Rebuttal",
                            "2AR"
                        ),
                        3.minutes.wrap(),
                        setOf(OneVOneSpeaker.Aff)
                    )
                ),
                secondaryTimers = listOf(
                    TimerModel(
                        ShortenableName("Aff Prep", "Aff"),
                        5.minutes.wrap(),
                        setOf(OneVOneSpeaker.Aff)
                    ),
                    TimerModel(
                        ShortenableName("Neg Prep", "Neg"),
                        5.minutes.wrap(),
                        setOf(OneVOneSpeaker.Neg)
                    )
                ),
                secondaryTimerChangeStrategy = SecondaryTimerChangeStrategy.All
            ),
            EventModel(
                format = DebateFormat.PublicForum,
                tags = EventTags(organization = Organization.NSDA),
                type = TwoVTwoSpeaker,
                primaryTimers = listOf(
                    TimerModel(
                        ShortenableName(
                            "Team A Constructive",
                            "AC"
                        ),
                        6.minutes.wrap(),
                        speakers = setOf(TwoVTwoSpeaker.TeamOneFirstSpeaker)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Team B Constructive",
                            "BC"
                        ),
                        3.minutes.wrap(),
                        speakers = setOf(TwoVTwoSpeaker.TeamTwoFirstSpeaker)
                    ),
                    TimerModel(
                        ShortenableName(
                            "First Speakers' Crossfire",
                            "1CX"
                        ),
                        7.minutes.wrap(),
                        speakers = TwoVTwoSpeaker.FirstSpeakers
                    ),
                    TimerModel(
                        ShortenableName(
                            "Team A Rebuttal",
                            "AR"
                        ),
                        4.minutes.wrap(),
                        speakers = setOf(TwoVTwoSpeaker.TeamOneSecondSpeaker)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Team B Rebuttal",
                            "BR"
                        ),
                        6.minutes.wrap(),
                        speakers = setOf(TwoVTwoSpeaker.TeamTwoSecondSpeaker)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Second Speakers' Crossfire",
                            "2CX"
                        ),
                        3.minutes.wrap(),
                        speakers = TwoVTwoSpeaker.SecondSpeakers
                    ),
                    TimerModel(
                        ShortenableName(
                            "Team A Summary",
                            "AS"
                        ),
                        3.minutes.wrap(),
                        speakers = setOf(TwoVTwoSpeaker.TeamOneFirstSpeaker)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Team B Summary",
                            "BS"
                        ),
                        3.minutes.wrap(),
                        speakers = setOf(TwoVTwoSpeaker.TeamTwoFirstSpeaker)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Grand Crossfire",
                            "GC"
                        ),
                        3.minutes.wrap(),
                        speakers = TwoVTwoSpeaker.AllSpeakers
                    ),
                    TimerModel(
                        ShortenableName(
                            "Team A Final Focus",
                            "AFF"
                        ),
                        3.minutes.wrap(),
                        speakers = setOf(TwoVTwoSpeaker.TeamOneSecondSpeaker)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Team B Final Focus",
                            "BFF"
                        ),
                        3.minutes.wrap(),
                        speakers = setOf(TwoVTwoSpeaker.TeamTwoSecondSpeaker)
                    )
                ),
                secondaryTimers = listOf(
                    TimerModel(
                        ShortenableName("Team A Prep", "Team A"),
                        2.minutes.wrap(),
                        TwoVTwoSpeaker.TeamOne
                    ),
                    TimerModel(
                        ShortenableName("Team B Prep", "Team A"),
                        2.minutes.wrap(),
                        TwoVTwoSpeaker.TeamTwo
                    )
                ),
                secondaryTimerChangeStrategy = SecondaryTimerChangeStrategy.Any
            ),
            EventModel(
                format = DebateFormat.BigQuestions,
                tags = EventTags(organization = Organization.NSDA),
                type = OneVOneSpeaker,
                primaryTimers = listOf(
                    TimerModel(
                        ShortenableName(
                            "Affirmative Constructive",
                            "ACT",
                        ),
                        5.minutes.wrap(),
                        setOf(OneVOneSpeaker.Aff)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Negative Constructive",
                            "NCT",
                        ),
                        5.minutes.wrap(),
                        setOf(OneVOneSpeaker.Neg)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Question Segment",
                            "1QS",
                        ),
                        3.minutes.wrap(),
                        OneVOneSpeaker.Both
                    ),
                    TimerModel(
                        ShortenableName(
                            "Affirmative Rebuttal",
                            "ARB",
                        ),
                        4.minutes.wrap(),
                        setOf(OneVOneSpeaker.Aff)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Negative Rebuttal",
                            "NRB",
                        ),
                        4.minutes.wrap(),
                        setOf(OneVOneSpeaker.Neg)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Second Question Segment",
                            "NRB",
                        ),
                        3.minutes.wrap(),
                        OneVOneSpeaker.Both
                    ),
                    TimerModel(
                        ShortenableName(
                            "Affirmative Consolidation",
                            "ACS",
                        ),
                        3.minutes.wrap(),
                        setOf(OneVOneSpeaker.Aff)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Negative Consolidation",
                            "NCS",
                        ),
                        3.minutes.wrap(),
                        setOf(OneVOneSpeaker.Neg)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Affirmative Rationale",
                            "ARL",
                        ),
                        3.minutes.wrap(),
                        setOf(OneVOneSpeaker.Aff)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Negative Rationale",
                            "NRL",
                        ),
                        3.minutes.wrap(),
                        setOf(OneVOneSpeaker.Neg)
                    )
                ),
                secondaryTimers = listOf(
                    TimerModel(
                        ShortenableName("Aff Prep", "Aff"),
                        3.minutes.wrap(),
                        setOf(OneVOneSpeaker.Aff)
                    ),
                    TimerModel(
                        ShortenableName("Neg Prep", "Neg"),
                        3.minutes.wrap(),
                        setOf(OneVOneSpeaker.Neg)
                    )
                ),
                secondaryTimerChangeStrategy = SecondaryTimerChangeStrategy.All
            ),
            EventModel(
                format = DebateFormat.Policy,
                tags = EventTags(organization = Organization.NSDA),
                type = OneVOneSpeaker,
                primaryTimers = listOf(
                    TimerModel(
                        ShortenableName(
                            "First Affirmative Constructive",
                            "1AC"
                        ),
                        8.minutes.wrap(),
                        setOf(OneVOneSpeaker.Aff)
                    ),
                    TimerModel(
                        ShortenableName("Negative Cross-Examination",
                            "1NCX"
                        ),
                        3.minutes.wrap(),
                        OneVOneSpeaker.Both
                    ),
                    TimerModel(
                        ShortenableName(
                            "First Negative Constructive",
                            "1NC"
                        ),
                        8.minutes.wrap(),
                        setOf(OneVOneSpeaker.Neg)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Affirmative Cross-Examination",
                            "1ACX"
                        ),
                        3.minutes.wrap(),
                        OneVOneSpeaker.Both
                    ),
                    TimerModel(
                        ShortenableName(
                            "Second Affirmative Constructive",
                            "2AC"
                        ),
                        8.minutes.wrap(),
                        setOf(OneVOneSpeaker.Aff)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Negative Cross-Examination",
                            "2NCX"
                        ),
                        3.minutes.wrap(),
                        OneVOneSpeaker.Both
                    ),
                    TimerModel(
                        ShortenableName(
                            "Negative Cross-Examination",
                            "2NC"
                        ),
                        3.minutes.wrap(),
                        OneVOneSpeaker.Both
                    ),
                    TimerModel(
                        ShortenableName(
                            "Affirmative Cross-Examination",
                            "2ACX"
                        ),
                        3.minutes.wrap(),
                        OneVOneSpeaker.Both
                    ),
                    TimerModel(
                        ShortenableName(
                            "First Negative Rebuttal",
                            "1NR"
                        ),
                        5.minutes.wrap(),
                        setOf(OneVOneSpeaker.Neg)
                    ),
                    TimerModel(
                        ShortenableName(
                            "First Affirmative Rebuttal",
                            "1AR"
                        ),
                        5.minutes.wrap(),
                        setOf(OneVOneSpeaker.Aff)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Second Negative Rebuttal",
                            "2NR"
                        ),
                        5.minutes.wrap(),
                        setOf(OneVOneSpeaker.Neg)
                    ),
                    TimerModel(
                        ShortenableName(
                            "Second Affirmative Rebuttal",
                            "1NR"
                        ),
                        5.minutes.wrap(),
                        setOf(OneVOneSpeaker.Neg)
                    ),
                ),
                secondaryTimers = listOf(
                    TimerModel(
                        ShortenableName("Aff Prep", "Aff"),
                        5.minutes.wrap(),
                        setOf(OneVOneSpeaker.Aff)
                    ),
                    TimerModel(
                        ShortenableName("Neg Prep", "Aff"),
                        5.minutes.wrap(),
                        setOf(OneVOneSpeaker.Neg)
                    )
                ),
                secondaryTimerChangeStrategy = SecondaryTimerChangeStrategy.All
            )
        )
        )
    }
}

private val settingsAccess = module {
    single { SettingsAccess(RusshwolfSettingsStoreAdapter(Settings())) }
}