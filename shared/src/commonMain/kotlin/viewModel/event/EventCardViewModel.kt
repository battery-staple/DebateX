package com.rohengiralt.debatex.viewModel.event

import com.rohengiralt.debatex.dataStructure.Image
import com.rohengiralt.debatex.viewModel.ViewModel
import com.rohengiralt.debatex.viewModel.ViewModelOnly

@ViewModelOnly
abstract class EventCardViewModel : ViewModel() {
    abstract val title: String
    abstract val subtitle: String
    abstract val favorited: Boolean
    abstract val body: String
    abstract val captionedImages: List<CaptionedImage>?
    abstract var showingInfo: Boolean

    abstract fun open()

    data class CaptionedImage(val image: Image, val caption: String?)
}

//@ViewModelOnly
//class CompetitorsTimingsEventCardViewModel(val model: CompetitorsTimingsEventCardModel, val sectionViewModel: EventsSectionViewModel) : EventCardViewModel() {
//    override val title: String = model.name
//    override val subtitle: String = model.tags.joinToString("\n") { "($it)" }
//    override val favorited: Boolean by observationHandler.published(false) //TODO: Store
//    override val body: String = model.timings.joinToString("-") { it.minutes.toInt().toString() }
//    override val captionedImages: Pair<Image, Image>? = when (model.competitionType) {
//        OneVOneSpeaker -> (AssetImage.Person.One.Green to model.) to AssetImage.Person.One.Red
//        TwoVTwoSpeaker -> AssetImage.Person.Two.Green to AssetImage.Person.Two.Red
////            CongressSpeaker -> AssetImage.Person.Many.Green to AssetImage.Person.Many.Red
//        else -> null
//    }
//
//    override fun open() {
//        logger.info { "About to request to open" }
//        sectionViewModel.open(this)
//    }
//
//    override var showingInfo: Boolean by observationHandler.published(false, set = { value ->
//        if (value) sectionViewModel.openInfo(this@CompetitorsTimingsEventCardViewModel)
//        field = value
//    })
//
//    companion object {
//        private val logger = loggerForClass<CompetitorsTimingsEventCardViewModel>()
//    }
//}