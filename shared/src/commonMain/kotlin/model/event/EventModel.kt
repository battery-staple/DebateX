package com.rohengiralt.debatex.model.event

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.rohengiralt.debatex.dataStructure.competitionTypes.Speaker
import com.rohengiralt.debatex.model.Model
import com.rohengiralt.debatex.model.timerModel.TimerModel
import com.rohengiralt.debatex.util.serializers.UuidSerializer
import kotlinx.serialization.Serializable

@Serializable
data class EventModel<out T : Speaker>(
    val format: DebateFormat<T>,
    val tags: EventTags = EventTags.NONE,
    val type: Speaker.Type<out T>,
    val primaryTimers: List<TimerModel<T>>,
    val secondaryTimers: List<TimerModel<T>>? = null,
    val secondaryTimerChangeStrategy: SecondaryTimerChangeStrategy
) : Model() {
    init {
        require(primaryTimers.isNotEmpty()) { "Must have at least one primary timer." }
    }

    @Serializable(with = UuidSerializer::class)
    val uuid: Uuid = uuid4()
}