package com.rohengiralt.debatex.viewModel.section

import com.rohengiralt.debatex.Logger
import com.rohengiralt.debatex.dataStructure.AssetImage
import com.rohengiralt.debatex.dataStructure.NullableSelectableList
import com.rohengiralt.debatex.dataStructure.competitionTypes.OneVOneSpeaker
import com.rohengiralt.debatex.dataStructure.competitionTypes.Speaker
import com.rohengiralt.debatex.dataStructure.competitionTypes.TwoVTwoSpeaker
import com.rohengiralt.debatex.dataStructure.toNullableSelectable
import com.rohengiralt.debatex.model.event.EventModel
import com.rohengiralt.debatex.model.event.EventVariant
import com.rohengiralt.debatex.model.sectionModel.EventsSectionModel
import com.rohengiralt.debatex.model.timerModel.TimerModel
import com.rohengiralt.debatex.viewModel.ViewModel
import com.rohengiralt.debatex.viewModel.event.BasicEventViewModel
import com.rohengiralt.debatex.viewModel.event.EventCardViewModel
import com.rohengiralt.debatex.viewModel.event.EventViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EventsSectionViewModel : ViewModel(), KoinComponent {
    private val model: EventsSectionModel by inject()

    inner class Card(
        private val index: Int,
        private val model: EventModel<*>,
    ) : EventCardViewModel() {
        override val title: String = model.format.name.shortNameOrLong
        override val subtitle: String =
            model.uniqueTags.also { Logger("sub").info("$it") }.joinToString(", ") { "$it" }

        private inline val EventModel<*>.uniqueTags: Set<EventVariant>
            get() = tags subtract (this@EventsSectionViewModel.model.eventModels
                .filter { it.format == format && it !== this }
                .map { it.tags.toSet() }
                .reduceOrNull(Set<EventVariant>::union) ?: emptySet())

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

        override var showingInfo: Boolean by observationHandler.published(false, set = { value ->
            if (value) openInfo(this@Card.index)
            field = value
        })

        override fun open() {
            this@EventsSectionViewModel.open(this.index)
        }
    }

    private fun open(index: Int) {
        events.currentIndex = index
        showingEvent = true
    }

    private fun openInfo(index: Int) {
        cards.forEachIndexed { viewModelIndex, viewModel ->
            if (index != viewModelIndex) viewModel.showingInfo = false
        }
    }

    val cards: List<EventCardViewModel> = model.eventModels.mapIndexed { index, model ->
        Card(index, model)
    }

    val currentEvent: EventViewModel? get() = events.currentSelection.value
    var showingEvent: Boolean by observationHandler.published(false)

    val events: NullableSelectableList<EventViewModel> = model.eventModels.map {
        BasicEventViewModel(it)
    }.toNullableSelectable().also { it.addSubscriber { showingEvent = true } }
}