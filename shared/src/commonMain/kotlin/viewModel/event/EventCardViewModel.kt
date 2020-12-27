package com.rohengiralt.debatex.viewModel.event

import com.rohengiralt.debatex.Configurable
import com.rohengiralt.debatex.dataStructure.Size.ScreenDimension.*
import com.rohengiralt.debatex.dataStructure.text.Alignment
import com.rohengiralt.debatex.dataStructure.text.FontWeight
import com.rohengiralt.debatex.dataStructure.text.Text
import com.rohengiralt.debatex.dataStructure.text.textSize
import com.rohengiralt.debatex.datafetch.DataFetcher
import com.rohengiralt.debatex.model.event.EventModel
import com.rohengiralt.debatex.viewModel.CardViewModel
import com.rohengiralt.debatex.viewModel.Linked

class EventCardViewModel internal constructor(
    override val link: EventViewModel<*>,
    override val modelFetcher: DataFetcher<EventModel<*>>
) : Linked<EventViewModel<*>>,
    CardViewModel<EventModel<*>>(
        title = @Configurable Text(
            rawText = link.untaggedName.shortNameOrLong,
            fontWeight = FontWeight.VeryBold,
            height = textSize(
                screenProportion = 0.2,
                screenDimension = OrientationBased.Horizontal
            )
        ),
        cornerRadius = @Configurable 25.0
    ) {

    @Suppress("UNUSED")
    val subtitle: Text =
        Text(
            rawText = model.tags.joinToString(separator = "\n") { it.representableName },
            alignment = Alignment.Center,
            height = textSize(
                screenProportion = 0.06,
                screenDimension = OrientationBased.Horizontal
            )
        )

    @Suppress("UNUSED")
    val body: Text =
        Text(
            rawText = model.pageFetchers.joinToString(separator = "-") {
                it.fetch().timerFetcher.fetch().totalTime.timeSpan.minutes.toString().removeSuffix(".0")
            },
            fontWeight = FontWeight.SlightlyBold,
            height = textSize(
                screenProportion = 0.1,
                screenDimension = OrientationBased.Horizontal
            )
        )

//    val image = model.
}