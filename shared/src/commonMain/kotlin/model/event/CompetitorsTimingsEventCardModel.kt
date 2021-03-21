package com.rohengiralt.debatex.model.event

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.rohengiralt.debatex.dataStructure.competitionTypes.Speaker
import com.rohengiralt.debatex.model.Model
import com.rohengiralt.debatex.util.serializers.TimeSpanSerializer
import com.soywiz.klock.TimeSpan
import kotlinx.serialization.Serializable

data class CompetitorsTimingsEventCardModel(
    val name: String,
    val tags: EventTags,
    val timings: Collection<@Serializable(with = TimeSpanSerializer::class) TimeSpan>,
    val competitionType: Speaker.Type<*>,
) : Model() {
    val uuid: Uuid = uuid4()
}