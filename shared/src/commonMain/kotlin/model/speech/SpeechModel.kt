package com.rohengiralt.debatex.model.speech

import com.rohengiralt.debatex.dataStructure.Speaker
import com.rohengiralt.debatex.datafetch.DataFetcher
import com.rohengiralt.debatex.model.*
import com.soywiz.klock.TimeSpan

class SpeechModel<T : Speaker<*>>(
    val pageFetcher: DataFetcher<TimePageModel<T>/*?*/>,
    segments: Collection<TimeSpan>
) : Model() {
    val page: TimePageModel<T> by pageFetcher

    init {
        segments.forEach {
            require(it < page.timerFetcher.fetch().totalTime)
        }
    }

    val segments: List<TimeSpan> = segments.sorted()
}