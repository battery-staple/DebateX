package com.rohengiralt.debatex.viewModel.section

import com.rohengiralt.debatex.Logger
import com.rohengiralt.debatex.dataStructure.AssetImage
import com.rohengiralt.debatex.dataStructure.NullableSelectableList
import com.rohengiralt.debatex.dataStructure.ShortenableName
import com.rohengiralt.debatex.dataStructure.competitionTypes.OneVOneSpeaker
import com.rohengiralt.debatex.dataStructure.competitionTypes.Speaker
import com.rohengiralt.debatex.dataStructure.competitionTypes.TwoVTwoSpeaker
import com.rohengiralt.debatex.dataStructure.toNullableSelectable
import com.rohengiralt.debatex.loggerForClass
import com.rohengiralt.debatex.model.event.AgeGroup
import com.rohengiralt.debatex.model.event.DebateFormat
import com.rohengiralt.debatex.model.event.EventModel
import com.rohengiralt.debatex.model.event.EventTags
import com.rohengiralt.debatex.model.event.EventVariant
import com.rohengiralt.debatex.model.event.Location
import com.rohengiralt.debatex.model.event.Organization
import com.rohengiralt.debatex.model.event.Region
import com.rohengiralt.debatex.model.event.SecondaryTimerChangeStrategy
import com.rohengiralt.debatex.model.timerModel.TimerModel
import com.rohengiralt.debatex.model.timerModel.wrap
import com.rohengiralt.debatex.viewModel.ViewModel
import com.rohengiralt.debatex.viewModel.ViewModelOnly
import com.rohengiralt.debatex.viewModel.event.BasicEventViewModel
import com.rohengiralt.debatex.viewModel.event.EventCardViewModel
import com.rohengiralt.debatex.viewModel.event.EventViewModel
import com.soywiz.klock.minutes
import com.soywiz.klock.seconds

@OptIn(ViewModelOnly::class)
class EventsSectionViewModel : ViewModel() {

    inner class Card(private val index: Int, private val model: EventModel<*>) : EventCardViewModel() {
        override val title: String = model.format.name.shortNameOrLong
        override val subtitle: String =
            model.uniqueTags.also { Logger("sub").info("$it") }.joinToString(", ") { "$it" }

        private inline val EventModel<*>.uniqueTags: Set<EventVariant>
            get() = tags subtract (presetModel
                .filter { it.format == format && it !== this }
                .map { it.tags.toSet() }
                .reduceOrNull { acc, set -> acc union set } ?: setOf())

        override val favorited: Boolean by observationHandler.published(false) //TODO: Store
        override val body: String = model.primaryTimers.toDisplayableString()

        private inline fun List<TimerModel<*>>.toDisplayableString(crossinline transform: (Int) -> String = Int::toString) =
            joinToString { timer ->
                transform(timer.totalTime.timeSpan.minutes.toInt())
            }

        override val captionedImages: List<CaptionedImage>?
            get() {
                val images = when (model.type) {
                    OneVOneSpeaker -> AssetImage.Person.One.Green to AssetImage.Person.One.Red
                    TwoVTwoSpeaker -> AssetImage.Person.Two.Green to AssetImage.Person.Two.Red
//                    CongressSpeaker -> AssetImage.Person.Many.Green to AssetImage.Person.Many.Red
                    else -> null
                } ?: return null

                return listOf(
                    CaptionedImage(images.first, images.first.caption),
                    CaptionedImage(images.second, images.second.caption)
                )
            }

        private val AssetImage.Person.speakers: Set<Speaker>
            get() = when (this) {
                AssetImage.Person.One.Green -> setOf(OneVOneSpeaker.Aff)
                AssetImage.Person.One.Red -> setOf(OneVOneSpeaker.Neg)
                AssetImage.Person.Two.Green -> TwoVTwoSpeaker.TeamOne
                AssetImage.Person.Two.Red -> TwoVTwoSpeaker.TeamTwo
                AssetImage.Person.Many.Red -> emptySet() //TODO: CongressSpeaker
                AssetImage.Person.Many.Green -> emptySet()
            }

        private val AssetImage.Person.caption: String?
            get() = with(model.secondaryTimerChangeStrategy) {
                model.secondaryTimers?.filter {
                    matches(it.speakers, speakers)
                }?.toDisplayableString {
                    "$it min"
                }
            }

        override var showingInfo by observationHandler.published(false, set = { value ->
            if (value) openInfo(this@Card.index)
            field = value
        })

        override fun open() {
            open(this.index)
        }
    }

    private fun open(index: Int) {
        events.currentIndex = index
        showingEvent = true
    }

    fun openInfo(index: Int) {
        cards.forEachIndexed { viewModelIndex, viewModel ->
            if (index != viewModelIndex) viewModel.showingInfo = false
        }
    }

    val cards: List<EventCardViewModel> = presetModel.mapIndexed { index, model ->
        Card(index, model)
    }

    val currentEvent: EventViewModel? get() = events.currentSelection.value
    var showingEvent: Boolean by observationHandler.published(false)

    private val events: NullableSelectableList<EventViewModel> = presetModel.map {
        BasicEventViewModel(it)
    }.toNullableSelectable().also { it.addSubscriber { showingEvent = true } }

    companion object {
        private val logger: Logger = loggerForClass<EventsSectionViewModel>()
    }
}

private val presetModel =
    listOf(
        EventModel(
            format = DebateFormat.Debug,
            tags = EventTags(
                ageGroup = AgeGroup.MiddleSchool,
                country = Location.Britain
            ),
            type = OneVOneSpeaker,
            primaryTimers = listOf(
                TimerModel(
                    name = ShortenableName(
                        "shr",
                        @Suppress("SpellCheckingInspection") "longnameee\neeeee\ne\neee\ree\r\n\n\nee"
                    ),
                    totalTime = 30.seconds.wrap(),
                    speakers = setOf(OneVOneSpeaker.Aff)
                )
            ),
            secondaryTimerChangeStrategy = SecondaryTimerChangeStrategy.All
        ),
        EventModel(
            format = DebateFormat.Debug,
            tags = EventTags(
                ageGroup = AgeGroup.HighSchoolNovice,
                organization = Organization.NSDA,
                country = Location.Netherlands
            ),
            type = OneVOneSpeaker,
            primaryTimers = listOf(
                TimerModel(
                    name = ShortenableName(
                        "A Really Really Really Really Really Really Unnecessarily Long Name For a Round",
                        "ARRRRRRULNFaR"
                    ),
                    totalTime = 2.minutes.wrap(),
                    speakers = setOf(OneVOneSpeaker.Neg)
                ),
                TimerModel(
                    ShortenableName(
                        "R",
                        ""
                    ),
                    1.minutes.wrap(),
                    setOf(OneVOneSpeaker.Neg)
                ),
                TimerModel(
                    ShortenableName(
                        "Short Speech For Testing",
                        "SRFT"
                    ),
                    0.5.minutes.wrap(),
                    setOf(OneVOneSpeaker.Aff)
                )
            ),
            secondaryTimerChangeStrategy = SecondaryTimerChangeStrategy.All
        ),
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

        )

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
//ConstantModelFetcher (
//EventModel(
//type = DebateFormat.LincolnDouglas,
//tags = EventTags(
//organization = Organization.NSDA,
//),
//pageFetchers = listOf(
//TimePageModel(
//"Affirmative Constructive",
//"AC",
//SettingsCountStrategyTimerFetcher(6.minutes),
//speaker = OneVOneSpeaker.Aff
//),
//TimePageModel(
//"Negative Cross-Examination",
//"NCX",
//SettingsCountStrategyTimerFetcher(3.minutes),
//speakers = OneVOneSpeaker.Both
//),
//TimePageModel(
//"Negative Constructive/First Rebuttal",
//"NC/1NR",
//SettingsCountStrategyTimerFetcher(7.minutes),
//speaker = OneVOneSpeaker.Neg
//),
//TimePageModel(
//"Affirmative Cross-Examination",
//"ACX",
//SettingsCountStrategyTimerFetcher(3.minutes),
//speakers = OneVOneSpeaker.Both
//),
//TimePageModel(
//"First Affirmative Rebuttal",
//"1AR",
//SettingsCountStrategyTimerFetcher(4.minutes),
//speaker = OneVOneSpeaker.Aff
//),
//TimePageModel(
//"Second Negative Rebuttal",
//"2NR",
//SettingsCountStrategyTimerFetcher(6.minutes),
//speaker = OneVOneSpeaker.Neg
//),
//TimePageModel(
//"Second Affirmative Rebuttal",
//"2AR",
//SettingsCountStrategyTimerFetcher(3.minutes),
//speaker = OneVOneSpeaker.Aff
//)
//),
//secondaryTimerModels = listOf(
//SecondaryTimerModel(
//SimpleName("Aff Prep"),
//ConstantModelFetcher(
//CountdownTimer(
//totalTime = 4.minutes
//)
//),
//OneVOneSpeaker.Aff
//),
//SecondaryTimerModel(
//SimpleName("Neg Prep"),
//CountdownTimer(
//totalTime = 4.minutes
//),
//OneVOneSpeaker.Neg
//)
//)
//)
//),
//ConstantModelFetcher(
//EventModel(
//type = DebateFormat.LincolnDouglas,
//tags = EventTags(
//organization = Organization.NSDA,
//region = Region.National
//),
//pageFetchers = listOf(
//TimePageModel(
//"Affirmative Constructive",
//"AC",
//SettingsCountStrategyTimerFetcher(6.minutes),
//speaker = OneVOneSpeaker.Aff
//),
//TimePageModel(
//"Negative Cross-Examination",
//"NCX",
//SettingsCountStrategyTimerFetcher(3.minutes),
//speakers = OneVOneSpeaker.Both
//),
//TimePageModel(
//"Negative Constructive/First Rebuttal",
//"NC/1NR",
//SettingsCountStrategyTimerFetcher(7.minutes),
//speaker = OneVOneSpeaker.Neg
//),
//TimePageModel(
//"Affirmative Cross-Examination",
//"ACX",
//SettingsCountStrategyTimerFetcher(3.minutes),
//speakers = OneVOneSpeaker.Both
//),
//TimePageModel(
//"First Affirmative Rebuttal",
//"1AR",
//SettingsCountStrategyTimerFetcher(4.minutes),
//speaker = OneVOneSpeaker.Aff
//),
//TimePageModel(
//"Second Negative Rebuttal",
//"2NR",
//SettingsCountStrategyTimerFetcher(6.minutes),
//speaker = OneVOneSpeaker.Neg
//),
//TimePageModel(
//"Second Affirmative Rebuttal",
//"2AR",
//SettingsCountStrategyTimerFetcher(3.minutes),
//speaker = OneVOneSpeaker.Aff
//)
//),
//secondaryTimerModels = listOf(
//SecondaryTimerModel(
//SimpleName("Aff Prep"),
//CountdownTimer(
//totalTime = 5.minutes
//),
//OneVOneSpeaker.Aff
//),
//SecondaryTimerModel(
//SimpleName("Neg Prep"),
//CountdownTimer(
//totalTime = 5.minutes
//),
//OneVOneSpeaker.Neg
//)
//)
//)
//),
//ConstantModelFetcher(
//EventModel(
//type = DebateFormat.PublicForum,
//tags = EventTags(organization = Organization.NSDA),
//pageFetchers = listOf(
//TimePageModel(
//"Team A Constructive",
//"AC",
//SettingsCountStrategyTimerFetcher(6.minutes),
//speaker = TwoVTwoSpeaker.TeamOneFirstSpeaker
//),
//TimePageModel(
//"Team B Constructive",
//"BC",
//SettingsCountStrategyTimerFetcher(3.minutes),
//speaker = TwoVTwoSpeaker.TeamTwoFirstSpeaker
//),
//TimePageModel(
//"First Speakers' Crossfire",
//"1CX",
//SettingsCountStrategyTimerFetcher(7.minutes),
//speakers = TwoVTwoSpeaker.FirstSpeakers
//),
//TimePageModel(
//"Team A Rebuttal",
//"AR",
//SettingsCountStrategyTimerFetcher(4.minutes),
//speaker = TwoVTwoSpeaker.TeamOneSecondSpeaker
//),
//TimePageModel(
//"Team B Rebuttal",
//"BR",
//SettingsCountStrategyTimerFetcher(6.minutes),
//speaker = TwoVTwoSpeaker.TeamTwoSecondSpeaker
//),
//TimePageModel(
//"Second Speakers' Crossfire",
//"2CX",
//SettingsCountStrategyTimerFetcher(3.minutes),
//speakers = TwoVTwoSpeaker.SecondSpeakers
//),
//TimePageModel(
//"Team A Summary",
//"AS",
//SettingsCountStrategyTimerFetcher(3.minutes),
//speaker = TwoVTwoSpeaker.TeamOneFirstSpeaker
//),
//TimePageModel(
//"Team B Summary",
//"BS",
//SettingsCountStrategyTimerFetcher(3.minutes),
//speaker = TwoVTwoSpeaker.TeamTwoFirstSpeaker
//),
//TimePageModel(
//"Grand Crossfire",
//"GC",
//SettingsCountStrategyTimerFetcher(3.minutes),
//speakers = TwoVTwoSpeaker.AllSpeakers
//),
//TimePageModel(
//"Team A Final Focus",
//"AFF",
//SettingsCountStrategyTimerFetcher(3.minutes),
//speaker = TwoVTwoSpeaker.TeamOneSecondSpeaker
//),
//TimePageModel(
//"Team B Final Focus",
//"BFF",
//SettingsCountStrategyTimerFetcher(3.minutes),
//speaker = TwoVTwoSpeaker.TeamTwoSecondSpeaker
//)
//),
//secondaryTimerModels = listOf(
//SecondaryTimerModel(
//SimpleName("Team A Prep"),
//CountdownTimer(
//totalTime = 2.minutes
//), TwoVTwoSpeaker.TeamOne
//),
//SecondaryTimerModel(
//SimpleName("Team B Prep"),
//CountdownTimer(
//totalTime = 2.minutes
//), TwoVTwoSpeaker.TeamTwo
//)
//),
//secondaryTimersAutomaticChangeMatchMode = SecondaryTimersAutomaticChangeMatchMode.Any
//)
//),
//ConstantModelFetcher(
//EventModel(
//type = DebateFormat.BigQuestions,
//tags = EventTags(organization = Organization.NSDA),
//pageFetchers = listOf(
//TimePageModel(
//"Affirmative Constructive",
//"ACT",
//SettingsCountStrategyTimerFetcher(5.minutes),
//OneVOneSpeaker.Aff
//),
//TimePageModel(
//"Negative Constructive",
//"NCT",
//SettingsCountStrategyTimerFetcher(5.minutes),
//OneVOneSpeaker.Neg
//),
//TimePageModel(
//"Question Segment",
//"1QS",
//SettingsCountStrategyTimerFetcher(3.minutes),
//OneVOneSpeaker.Both
//),
//TimePageModel(
//"Affirmative Rebuttal",
//"ARB",
//SettingsCountStrategyTimerFetcher(4.minutes),
//OneVOneSpeaker.Aff
//),
//TimePageModel(
//"Negative Rebuttal",
//"NRB",
//SettingsCountStrategyTimerFetcher(4.minutes),
//OneVOneSpeaker.Neg
//),
//TimePageModel(
//"Second Question Segment",
//"NRB",
//SettingsCountStrategyTimerFetcher(3.minutes),
//OneVOneSpeaker.Both
//),
//TimePageModel(
//"Affirmative Consolidation",
//"ACS",
//SettingsCountStrategyTimerFetcher(3.minutes),
//OneVOneSpeaker.Aff
//),
//TimePageModel(
//"Negative Consolidation",
//"NCS",
//SettingsCountStrategyTimerFetcher(3.minutes),
//OneVOneSpeaker.Neg
//),
//TimePageModel(
//"Affirmative Rationale",
//"ARL",
//SettingsCountStrategyTimerFetcher(3.minutes),
//OneVOneSpeaker.Aff
//),
//TimePageModel(
//"Negative Rationale",
//"NRL",
//SettingsCountStrategyTimerFetcher(3.minutes),
//OneVOneSpeaker.Neg
//)
//),
//secondaryTimerModels = listOf(
//SecondaryTimerModel(
//SimpleName("Aff Prep"),
//CountdownTimer(3.minutes),
//OneVOneSpeaker.Aff
//),
//SecondaryTimerModel(
//SimpleName("Neg Prep"),
//CountdownTimer(3.minutes),
//OneVOneSpeaker.Neg
//)
//)
//)
//),
//ConstantModelFetcher(
//EventModel(
//type = DebateFormat.Policy,
//tags = EventTags(organization = Organization.NSDA),
//pageFetchers = listOf(
//TimePageModel(
//"First Affirmative Constructive",
//"1AC",
//SettingsCountStrategyTimerFetcher(8.minutes),
//OneVOneSpeaker.Aff
//),
//TimePageModel(
//"Negative Cross-Examination",
//"1NCX",
//SettingsCountStrategyTimerFetcher(3.minutes),
//OneVOneSpeaker.Both
//),
//TimePageModel(
//"First Negative Constructive",
//"1NC",
//SettingsCountStrategyTimerFetcher(8.minutes),
//OneVOneSpeaker.Neg
//),
//TimePageModel(
//"Affirmative Cross-Examination",
//"1ACX",
//SettingsCountStrategyTimerFetcher(3.minutes),
//OneVOneSpeaker.Both
//),
//TimePageModel(
//"Second Affirmative Constructive",
//"2AC",
//SettingsCountStrategyTimerFetcher(8.minutes),
//OneVOneSpeaker.Aff
//),
//TimePageModel(
//"Negative Cross-Examination",
//"2NCX",
//SettingsCountStrategyTimerFetcher(3.minutes),
//OneVOneSpeaker.Both
//),
//TimePageModel(
//"Negative Cross-Examination",
//"2NC",
//SettingsCountStrategyTimerFetcher(3.minutes),
//OneVOneSpeaker.Both
//),
//TimePageModel(
//"Affirmative Cross-Examination",
//"2ACX",
//SettingsCountStrategyTimerFetcher(3.minutes),
//OneVOneSpeaker.Both
//),
//TimePageModel(
//"First Negative Rebuttal",
//"1NR",
//SettingsCountStrategyTimerFetcher(5.minutes),
//OneVOneSpeaker.Neg
//),
//TimePageModel(
//"First Affirmative Rebuttal",
//"1AR",
//SettingsCountStrategyTimerFetcher(5.minutes),
//OneVOneSpeaker.Aff
//),
//TimePageModel(
//"Second Negative Rebuttal",
//"2NR",
//SettingsCountStrategyTimerFetcher(5.minutes),
//OneVOneSpeaker.Neg
//),
//TimePageModel(
//"Second Affirmative Rebuttal",
//"1NR",
//SettingsCountStrategyTimerFetcher(5.minutes),
//OneVOneSpeaker.Neg
//),
//),
//secondaryTimerModels = listOf(
//SecondaryTimerModel(
//SimpleName("Aff Prep"),
//CountdownTimer(5.minutes),
//OneVOneSpeaker.Aff
//),
//SecondaryTimerModel(
//SimpleName("Neg Prep"),
//CountdownTimer(5.minutes),
//OneVOneSpeaker.Neg
//)
//)
//)
//)
//).map {
//    it to CompetitorsTimingsEventCardModel(it)
//}