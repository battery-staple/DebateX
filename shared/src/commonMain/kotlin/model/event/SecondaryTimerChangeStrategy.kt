package com.rohengiralt.debatex.model.event

import com.rohengiralt.debatex.dataStructure.competitionTypes.Speaker
import kotlinx.serialization.Serializable

@Serializable
sealed class SecondaryTimerChangeStrategy {
    //    abstract infix fun TimerModel<*>.matches(other: TimerModel<*>): Boolean //TODO: infix with multiple receivers in Kt 1.5
    abstract fun matches(s1: Set<Speaker>, s2: Set<Speaker>): Boolean

    object All : SecondaryTimerChangeStrategy() {
//        override infix fun TimerModel<*>.matches(other: TimerModel<*>): Boolean = speakers == other.speakers

        override fun matches(s1: Set<Speaker>, s2: Set<Speaker>): Boolean = s1 == s2
    }

    object Any : SecondaryTimerChangeStrategy() {
//        fun TimerModel<*>.matches(other: TimerModel<*>): Boolean =
//            (this.speakers intersect other.speakers).isNotEmpty()

        override fun matches(s1: Set<Speaker>, s2: Set<Speaker>): Boolean =
            (s1 intersect s2).isNotEmpty()
    }

    object Never : SecondaryTimerChangeStrategy() {
//        override fun TimerModel<*>.matches(other: TimerModel<*>): Boolean = false

        override fun matches(s1: Set<Speaker>, s2: Set<Speaker>): Boolean = false
    }
}