package com.rohengiralt.debatex.datafetch

import com.rohengiralt.debatex.settings.Settings
import com.rohengiralt.debatex.dataStructure.OneVOneSpeaker
import com.rohengiralt.debatex.dataStructure.SimpleName
import com.rohengiralt.debatex.dataStructure.Speaker
import com.rohengiralt.debatex.dataStructure.TwoVTwoSpeaker
import com.rohengiralt.debatex.model.TimePageModel
import com.rohengiralt.debatex.model.TimerCountStrategy
import com.rohengiralt.debatex.model.TimerModel
import com.rohengiralt.debatex.model.event.DebateFormat
import com.rohengiralt.debatex.model.event.EventModel
import com.rohengiralt.debatex.model.event.EventTags
import com.rohengiralt.debatex.model.event.Organization
import com.rohengiralt.debatex.model.event.Region
import com.rohengiralt.debatex.model.event.SecondaryTimerModel
import com.rohengiralt.debatex.model.event.SecondaryTimersAutomaticChangeMatchMode
import com.rohengiralt.debatex.model.sectionModel.EventsSectionModel
import com.rohengiralt.debatex.model.sectionModel.SectionModel
import com.rohengiralt.debatex.model.sectionModel.SettingsSectionModel
import com.rohengiralt.debatex.model.sectionModel.SpeechesSectionModel
import com.rohengiralt.debatex.settings.applicationSettings
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.minutes

sealed class SectionFetcher<M : SectionModel> : DataFetcher<M>

class EventsSectionFetcher : SectionFetcher<EventsSectionModel>() {
    private val presetModel = with(EventInitUtils) {
        EventsSectionModel(
            listOf(
//                ConstantModelFetcher(
//                    EventModel(
//                        type = DebateFormat.Debug,
//                        tags = EventTags(
//                            ageGroup = AgeGroup.MiddleSchool,
//                            country = Location.Britain
//                        ),
//                        pageFetchers = listOf(
//                            TimePageModel(
//                                shortName = "shr",
//                                longName = @Suppress("SpellCheckingInspection") "longnameee\neeeee\ne\neee\ree\r\n\n\nee",
//                                timer = SettingsCountStrategyTimerFetcher(30.seconds),
//                                speakers = TwoVTwoSpeaker.TeamOne
//                            )
//                        )
//                    )
//                ),
//                ConstantModelFetcher(
//                    EventModel(
//                        type = DebateFormat.Debug,
//                        tags = EventTags(
//                            ageGroup = AgeGroup.HighSchoolNovice,
//                            organization = Organization.NSDA,
//                            country = Location.Netherlands
//                        ),
//                        pageFetchers = listOf(
//                            TimePageModel(
//                                shortName = "A Really Really Really Really Really Really Unnecessarily Long Name For a Round",
//                                longName = "ARRRRRRULNFaR",
//                                timer = SettingsCountStrategyTimerFetcher(2.minutes),
//                                speaker = OneVOneSpeaker.Neg
//                            ),
//                            TimePageModel(
//                                "R",
//                                "",
//                                SettingsCountStrategyTimerFetcher(1.minutes),
//                                speaker = OneVOneSpeaker.Neg
//                            ),
//                            TimePageModel(
//                                "Short Speech For Testing",
//                                "SRFT",
//                                SettingsCountStrategyTimerFetcher(0.5.minutes),
//                                speaker = OneVOneSpeaker.Aff
//                            )
//                        )
//                    )
//                ),
                ConstantModelFetcher(
                    EventModel(
                        type = DebateFormat.LincolnDouglas,
                        tags = EventTags(
                            organization = Organization.NSDA,
                        ),
                        pageFetchers = listOf(
                            TimePageModel(
                                "Affirmative Constructive",
                                "AC",
                                SettingsCountStrategyTimerFetcher(6.minutes),
                                speaker = OneVOneSpeaker.Aff
                            ),
                            TimePageModel(
                                "Negative Cross-Examination",
                                "NCX",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                speakers = OneVOneSpeaker.Both
                            ),
                            TimePageModel(
                                "Negative Constructive/First Rebuttal",
                                "NC/1NR",
                                SettingsCountStrategyTimerFetcher(7.minutes),
                                speaker = OneVOneSpeaker.Neg
                            ),
                            TimePageModel(
                                "Affirmative Cross-Examination",
                                "ACX",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                speakers = OneVOneSpeaker.Both
                            ),
                            TimePageModel(
                                "First Affirmative Rebuttal",
                                "1AR",
                                SettingsCountStrategyTimerFetcher(4.minutes),
                                speaker = OneVOneSpeaker.Aff
                            ),
                            TimePageModel(
                                "Second Negative Rebuttal",
                                "2NR",
                                SettingsCountStrategyTimerFetcher(6.minutes),
                                speaker = OneVOneSpeaker.Neg
                            ),
                            TimePageModel(
                                "Second Affirmative Rebuttal",
                                "2AR",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                speaker = OneVOneSpeaker.Aff
                            )
                        ),
                        secondaryTimerModels = listOf(
                            SecondaryTimerModel(
                                SimpleName("Aff Prep"),
                                ConstantModelFetcher(
                                    CountdownTimer(
                                        totalTime = 4.minutes
                                    )
                                ),
                                OneVOneSpeaker.Aff
                            ),
                            SecondaryTimerModel(
                                SimpleName("Neg Prep"),
                                CountdownTimer(
                                    totalTime = 4.minutes
                                ),
                                OneVOneSpeaker.Neg
                            )
                        )
                    )
                ),
                ConstantModelFetcher(
                    EventModel(
                        type = DebateFormat.LincolnDouglas,
                        tags = EventTags(
                            organization = Organization.NSDA,
                            region = Region.National
                        ),
                        pageFetchers = listOf(
                            TimePageModel(
                                "Affirmative Constructive",
                                "AC",
                                SettingsCountStrategyTimerFetcher(6.minutes),
                                speaker = OneVOneSpeaker.Aff
                            ),
                            TimePageModel(
                                "Negative Cross-Examination",
                                "NCX",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                speakers = OneVOneSpeaker.Both
                            ),
                            TimePageModel(
                                "Negative Constructive/First Rebuttal",
                                "NC/1NR",
                                SettingsCountStrategyTimerFetcher(7.minutes),
                                speaker = OneVOneSpeaker.Neg
                            ),
                            TimePageModel(
                                "Affirmative Cross-Examination",
                                "ACX",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                speakers = OneVOneSpeaker.Both
                            ),
                            TimePageModel(
                                "First Affirmative Rebuttal",
                                "1AR",
                                SettingsCountStrategyTimerFetcher(4.minutes),
                                speaker = OneVOneSpeaker.Aff
                            ),
                            TimePageModel(
                                "Second Negative Rebuttal",
                                "2NR",
                                SettingsCountStrategyTimerFetcher(6.minutes),
                                speaker = OneVOneSpeaker.Neg
                            ),
                            TimePageModel(
                                "Second Affirmative Rebuttal",
                                "2AR",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                speaker = OneVOneSpeaker.Aff
                            )
                        ),
                        secondaryTimerModels = listOf(
                            SecondaryTimerModel(
                                SimpleName("Aff Prep"),
                                CountdownTimer(
                                    totalTime = 5.minutes
                                ),
                                OneVOneSpeaker.Aff
                            ),
                            SecondaryTimerModel(
                                SimpleName("Neg Prep"),
                                CountdownTimer(
                                    totalTime = 5.minutes
                                ),
                                OneVOneSpeaker.Neg
                            )
                        )
                    )
                ),
                ConstantModelFetcher(
                    EventModel(
                        type = DebateFormat.PublicForum,
                        tags = EventTags(organization = Organization.NSDA),
                        pageFetchers = listOf(
                            TimePageModel(
                                "Team A Constructive",
                                "AC",
                                SettingsCountStrategyTimerFetcher(6.minutes),
                                speaker = TwoVTwoSpeaker.TeamOneFirstSpeaker
                            ),
                            TimePageModel(
                                "Team B Constructive",
                                "BC",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                speaker = TwoVTwoSpeaker.TeamTwoFirstSpeaker
                            ),
                            TimePageModel(
                                "First Speakers' Crossfire",
                                "1CX",
                                SettingsCountStrategyTimerFetcher(7.minutes),
                                speakers = TwoVTwoSpeaker.FirstSpeakers
                            ),
                            TimePageModel(
                                "Team A Rebuttal",
                                "AR",
                                SettingsCountStrategyTimerFetcher(4.minutes),
                                speaker = TwoVTwoSpeaker.TeamOneSecondSpeaker
                            ),
                            TimePageModel(
                                "Team B Rebuttal",
                                "BR",
                                SettingsCountStrategyTimerFetcher(6.minutes),
                                speaker = TwoVTwoSpeaker.TeamTwoSecondSpeaker
                            ),
                            TimePageModel(
                                "Second Speakers' Crossfire",
                                "2CX",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                speakers = TwoVTwoSpeaker.SecondSpeakers
                            ),
                            TimePageModel(
                                "Team A Summary",
                                "AS",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                speaker = TwoVTwoSpeaker.TeamOneFirstSpeaker
                            ),
                            TimePageModel(
                                "Team B Summary",
                                "BS",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                speaker = TwoVTwoSpeaker.TeamTwoFirstSpeaker
                            ),
                            TimePageModel(
                                "Grand Crossfire",
                                "GC",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                speakers = TwoVTwoSpeaker.AllSpeakers
                            ),
                            TimePageModel(
                                "Team A Final Focus",
                                "AFF",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                speaker = TwoVTwoSpeaker.TeamOneSecondSpeaker
                            ),
                            TimePageModel(
                                "Team B Final Focus",
                                "BFF",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                speaker = TwoVTwoSpeaker.TeamTwoSecondSpeaker
                            )
                        ),
                        secondaryTimerModels = listOf(
                            SecondaryTimerModel(
                                SimpleName("Team A Prep"),
                                CountdownTimer(
                                    totalTime = 2.minutes
                                ), TwoVTwoSpeaker.TeamOne
                            ),
                            SecondaryTimerModel(
                                SimpleName("Team B Prep"),
                                CountdownTimer(
                                    totalTime = 2.minutes
                                ), TwoVTwoSpeaker.TeamTwo
                            )
                        ),
                        secondaryTimersAutomaticChangeMatchMode = SecondaryTimersAutomaticChangeMatchMode.Any
                    )
                ),
                ConstantModelFetcher(
                    EventModel(
                        type = DebateFormat.BigQuestions,
                        tags = EventTags(organization = Organization.NSDA),
                        pageFetchers = listOf(
                            TimePageModel(
                                "Affirmative Constructive",
                                "ACT",
                                SettingsCountStrategyTimerFetcher(5.minutes),
                                OneVOneSpeaker.Aff
                            ),
                            TimePageModel(
                                "Negative Constructive",
                                "NCT",
                                SettingsCountStrategyTimerFetcher(5.minutes),
                                OneVOneSpeaker.Neg
                            ),
                            TimePageModel(
                                "Question Segment",
                                "1QS",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                OneVOneSpeaker.Both
                            ),
                            TimePageModel(
                                "Affirmative Rebuttal",
                                "ARB",
                                SettingsCountStrategyTimerFetcher(4.minutes),
                                OneVOneSpeaker.Aff
                            ),
                            TimePageModel(
                                "Negative Rebuttal",
                                "NRB",
                                SettingsCountStrategyTimerFetcher(4.minutes),
                                OneVOneSpeaker.Neg
                            ),
                            TimePageModel(
                                "Second Question Segment",
                                "NRB",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                OneVOneSpeaker.Both
                            ),
                            TimePageModel(
                                "Affirmative Consolidation",
                                "ACS",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                OneVOneSpeaker.Aff
                            ),
                            TimePageModel(
                                "Negative Consolidation",
                                "NCS",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                OneVOneSpeaker.Neg
                            ),
                            TimePageModel(
                                "Affirmative Rationale",
                                "ARL",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                OneVOneSpeaker.Aff
                            ),
                            TimePageModel(
                                "Negative Rationale",
                                "NRL",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                OneVOneSpeaker.Neg
                            )
                        ),
                        secondaryTimerModels = listOf(
                            SecondaryTimerModel(
                                SimpleName("Aff Prep"),
                                CountdownTimer(3.minutes),
                                OneVOneSpeaker.Aff
                            ),
                            SecondaryTimerModel(
                                SimpleName("Neg Prep"),
                                CountdownTimer(3.minutes),
                                OneVOneSpeaker.Neg
                            )
                        )
                    )
                ),
                ConstantModelFetcher(
                    EventModel(
                        type = DebateFormat.Policy,
                        tags = EventTags(organization = Organization.NSDA),
                        pageFetchers = listOf(
                            TimePageModel(
                                "First Affirmative Constructive",
                                "1AC",
                                SettingsCountStrategyTimerFetcher(8.minutes),
                                OneVOneSpeaker.Aff
                            ),
                            TimePageModel(
                                "Negative Cross-Examination",
                                "1NCX",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                OneVOneSpeaker.Both
                            ),
                            TimePageModel(
                                "First Negative Constructive",
                                "1NC",
                                SettingsCountStrategyTimerFetcher(8.minutes),
                                OneVOneSpeaker.Neg
                            ),
                            TimePageModel(
                                "Affirmative Cross-Examination",
                                "1ACX",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                OneVOneSpeaker.Both
                            ),
                            TimePageModel(
                                "Second Affirmative Constructive",
                                "2AC",
                                SettingsCountStrategyTimerFetcher(8.minutes),
                                OneVOneSpeaker.Aff
                            ),
                            TimePageModel(
                                "Negative Cross-Examination",
                                "2NCX",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                OneVOneSpeaker.Both
                            ),
                            TimePageModel(
                                "Negative Cross-Examination",
                                "2NC",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                OneVOneSpeaker.Both
                            ),
                            TimePageModel(
                                "Affirmative Cross-Examination",
                                "2ACX",
                                SettingsCountStrategyTimerFetcher(3.minutes),
                                OneVOneSpeaker.Both
                            ),
                            TimePageModel(
                                "First Negative Rebuttal",
                                "1NR",
                                SettingsCountStrategyTimerFetcher(5.minutes),
                                OneVOneSpeaker.Neg
                            ),
                            TimePageModel(
                                "First Affirmative Rebuttal",
                                "1AR",
                                SettingsCountStrategyTimerFetcher(5.minutes),
                                OneVOneSpeaker.Aff
                            ),
                            TimePageModel(
                                "Second Negative Rebuttal",
                                "2NR",
                                SettingsCountStrategyTimerFetcher(5.minutes),
                                OneVOneSpeaker.Neg
                            ),
                            TimePageModel(
                                "Second Affirmative Rebuttal",
                                "1NR",
                                SettingsCountStrategyTimerFetcher(5.minutes),
                                OneVOneSpeaker.Neg
                            ),
                        ),
                        secondaryTimerModels = listOf(
                            SecondaryTimerModel(
                                SimpleName("Aff Prep"),
                                CountdownTimer(5.minutes),
                                OneVOneSpeaker.Aff
                            ),
                            SecondaryTimerModel(
                                SimpleName("Neg Prep"),
                                CountdownTimer(5.minutes),
                                OneVOneSpeaker.Neg
                            )
                        )
                    )
                )
            )
        )
    }

    override fun fetch(): EventsSectionModel = presetModel

    private object EventInitUtils {
        @Suppress("FunctionName")
        inline fun CountdownTimer(totalTime: TimeSpan): TimerModel =
            TimerModel(
                totalTime,
                TimerCountStrategy.CountDown
            )

        @Suppress("FunctionName")
        inline fun <T : Speaker<*>> TimePageModel(
            longName: String,
            shortName: String,
            timer: DataFetcher<TimerModel>,
            speakers: List<T>,
        ): DataFetcher<TimePageModel<T>> = ConstantModelFetcher(
            com.rohengiralt.debatex.model.TimePageModel(longName, shortName, timer, speakers)
        )

        @Suppress("FunctionName")
        inline fun <T : Speaker<*>> TimePageModel(
            longName: String,
            shortName: String,
            timer: DataFetcher<TimerModel>,
            speaker: T,
        ): DataFetcher<TimePageModel<T>> = ConstantModelFetcher(
            com.rohengiralt.debatex.model.TimePageModel(longName, shortName, timer, speaker)
        )
    }
}


object SpeechesSectionFetcher : SectionFetcher<SpeechesSectionModel>() {
    override fun fetch(): SpeechesSectionModel =
        SpeechesSectionModel(
            emptyList()
        )

    operator fun invoke(): SpeechesSectionFetcher = this
}

object SettingsSectionFetcher : SectionFetcher<SettingsSectionModel>() {
    override fun fetch(): SettingsSectionModel {
        applicationSettings.update()
        return SettingsSectionModel(
            listOf(
                applicationSettings.countStrategy
            )
        )
    }

    operator fun invoke(): SettingsSectionFetcher = this
}