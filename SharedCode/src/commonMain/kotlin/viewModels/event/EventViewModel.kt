package com.rohengiralt.debatex.viewModels.event

import com.rohengiralt.debatex.dataStructures.ShortenableName
import com.rohengiralt.debatex.dataStructures.TimePageViewModel
import com.rohengiralt.debatex.event.Event
import com.rohengiralt.debatex.event.SecondaryTimerViewModel
import com.rohengiralt.debatex.event.Speaker
import com.rohengiralt.debatex.viewModels.ViewModel

class EventViewModel<out T : Speaker<*>>(
    internal val event: Event<T>
) : ViewModel() {

    @Suppress("UNUSED")
    val displayName: ShortenableName
        get() = event.overrideName ?: event.type.name + "(${event.tags})"

    @Suppress("UNUSED")
    val pages: List<TimePageViewModel<T>> by lazy {
        event.pages.map {
            TimePageViewModel(it)
        }
    }

    @Suppress("UNUSED")
    val secondaryTimers: List<SecondaryTimerViewModel<T>>? =
        event.secondaryTimers?.map {
            SecondaryTimerViewModel(
                it
            )
        }

    val card: EventCardViewModel = EventCardViewModel(this)
}