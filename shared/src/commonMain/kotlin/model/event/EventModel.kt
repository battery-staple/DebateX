package com.rohengiralt.debatex.model.event

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.rohengiralt.debatex.dataStructure.ShortenableName
import com.rohengiralt.debatex.dataStructure.Speaker
import com.rohengiralt.debatex.datafetch.ConstantModelFetcher
import com.rohengiralt.debatex.datafetch.DataFetcher
import com.rohengiralt.debatex.model.TimePageModel
import com.rohengiralt.debatex.model.Model
import com.rohengiralt.debatex.model.UuidSerializer
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmName

@Serializable
data class EventModel<out T : Speaker<*>>(
    val type: DebateFormat<T>,
    val tags: EventTags = EventTags.NONE,
    val overrideName: ShortenableName? = null,
    val pageFetchers: List<DataFetcher<TimePageModel<T>>>,
    val secondaryTimerModelFetchers: List<DataFetcher<SecondaryTimerModel<T>>>?,
    val secondaryTimersAutomaticChangeMatchMode: SecondaryTimersAutomaticChangeMatchMode = SecondaryTimersAutomaticChangeMatchMode.All,
    val favorited: Boolean = false,
) : Model() {
    constructor(
        type: DebateFormat<T>,
        tags: EventTags = EventTags.NONE,
        overrideName: ShortenableName? = null,
        pageFetchers: List<DataFetcher<TimePageModel<T>>>,
        secondaryTimerModels: List<SecondaryTimerModel<T>>?,
        secondaryTimersAutomaticChangeMatchMode: SecondaryTimersAutomaticChangeMatchMode = SecondaryTimersAutomaticChangeMatchMode.All,
        favorited: Boolean = false,
        jvmConflictingOverloadsWorkaround: Unit = Unit
    ) : this(
        type, tags, overrideName, pageFetchers,
        secondaryTimerModelFetchers = secondaryTimerModels?.map(::ConstantModelFetcher),
        secondaryTimersAutomaticChangeMatchMode, favorited
    )

    constructor(
        type: DebateFormat<T>,
        tags: EventTags = EventTags.NONE,
        overrideName: ShortenableName? = null,
        pageFetchers: List<DataFetcher<TimePageModel<T>>>,
        secondaryTimersAutomaticChangeMatchMode: SecondaryTimersAutomaticChangeMatchMode = SecondaryTimersAutomaticChangeMatchMode.All,
        favorited: Boolean = false,
        jvmConflictingOverloadsWorkaround: Unit = Unit,
        jvmConflictingOverloadsWorkaround2: Unit = Unit
    ) : this(
        type,
        tags,
        overrideName,
        pageFetchers,
        secondaryTimerModelFetchers = null,
        secondaryTimersAutomaticChangeMatchMode,
        favorited
    )

    @Serializable(with = UuidSerializer::class)
    val uuid: Uuid = uuid4()
}

@Serializable
enum class SecondaryTimersAutomaticChangeMatchMode {
    All, Any, Never
}
