package com.rohengiralt.debatex.viewModels.event

import com.rohengiralt.debatex.dataStructures.text.Text
import com.rohengiralt.debatex.viewModels.CardViewModel
import com.rohengiralt.debatex.viewModels.Linked

class EventCardViewModel internal constructor(
    override val link: EventViewModel<*>
) : CardViewModel(Text(link.displayName.shortNameOrLong)),
    Linked<EventViewModel<*>> {
    val subtitles: List<Text> = link.event.tags.map { Text(it.representableName) }
    val body: Text =
        Text(link.event.pages.joinToString(separator = "-") { it.totalTime.minutes.toString() })
}