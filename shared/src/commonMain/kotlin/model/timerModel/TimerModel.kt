package com.rohengiralt.debatex.model.timerModel

import com.rohengiralt.debatex.dataStructure.ShortenableName
import com.rohengiralt.debatex.dataStructure.competitionTypes.Speaker
import com.rohengiralt.debatex.model.Model
import kotlinx.serialization.Serializable

@Serializable
data class TimerModel<T : Speaker>(
    val name: ShortenableName,
    val totalTime: TimeSpanWrapper,
    val speakers: Set<T>,
) : Model() {
    init {
        require(speakers.isNotEmpty()) { "Number of speakers must be nonzero." }
    }
}
