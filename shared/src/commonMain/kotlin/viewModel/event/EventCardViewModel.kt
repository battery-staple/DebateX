package com.rohengiralt.debatex.viewModel.event

import com.rohengiralt.debatex.dataStructure.Image
import com.rohengiralt.debatex.viewModel.ViewModel


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