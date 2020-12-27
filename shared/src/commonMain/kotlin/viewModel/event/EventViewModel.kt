package com.rohengiralt.debatex.viewModel.event

import com.rohengiralt.debatex.dataStructure.ShortenableName
import com.rohengiralt.debatex.model.event.EventModel
import com.rohengiralt.debatex.model.event.SecondaryTimersAutomaticChangeMatchMode.*
import com.rohengiralt.debatex.dataStructure.Speaker
import com.rohengiralt.debatex.datafetch.DataFetcher
import com.rohengiralt.debatex.model.TimePageModel
import com.rohengiralt.debatex.settings.applicationSettings
import com.rohengiralt.debatex.viewModel.SecondaryTimerViewModel
import com.rohengiralt.debatex.viewModel.ViewModel
import com.rohengiralt.debatex.viewModel.timePage.TimePageViewModel

//import kotlinx.coroutines.InternalCoroutinesApi

class EventViewModel<T : Speaker<*>>(
    override val modelFetcher: DataFetcher<EventModel<T>>
) : ViewModel<EventModel<T>>() {
    internal val untaggedName: ShortenableName get() = model.overrideName ?: model.type.name

    @Suppress("UNUSED")
    val displayName: ShortenableName
        get() = untaggedName + " " + model.tags.joinToString(separator = " ") { "(${it.representableName})" }

    //    @InternalCoroutinesApi
    @Suppress("UNUSED")
    val pages: List<TimePageViewModel<T>> by lazy {
        model.pageFetchers.map {
            TimePageViewModel(it)
        }
    }

    @Suppress("UNUSED")
    val secondaryTimers: List<SecondaryTimerViewModel<T>>? =
        model.secondaryTimerModelFetchers?.map(::SecondaryTimerViewModel)

    @Suppress("UNUSED")
//    @ExperimentalMultiplatform
//    @Throws(IndexOutOfBoundsException::class)
    fun setSecondaryTimerIndexToOnPageChange(toIndex: Int): Int? {
        if (secondaryTimers != null) {
            val currentTimePage = model.pageFetchers[toIndex].fetch()

            val secondaryTimerIndicesMatchingCurrentPage: List<Int> =
                secondaryTimers.mapIndexedNotNull { index, secondaryTimer ->
                    if (secondaryTimer matches currentTimePage) index else null
                }

            if (secondaryTimerIndicesMatchingCurrentPage.size == 1) return secondaryTimerIndicesMatchingCurrentPage[0]
        }

        return null
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline infix fun SecondaryTimerViewModel<*>.matches(primaryTimer: TimePageModel<*>) =
        when (model.secondaryTimersAutomaticChangeMatchMode) {
            All -> this.speakers == primaryTimer.belongingTo
            Any -> this.speakers.any { it in primaryTimer.belongingTo }
            Never -> false
        }

    @Suppress("UNUSED")
    val card: EventCardViewModel = EventCardViewModel(this, modelFetcher)
}