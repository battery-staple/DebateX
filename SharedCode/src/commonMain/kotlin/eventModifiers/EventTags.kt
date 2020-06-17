package com.rohengiralt.debatex.eventModifiers

import kotlinx.serialization.Serializable

@Serializable
data class EventTags(
    val country: Location? = null,
    val ageGroup: AgeGroup? = null,
    val organization: Organization? = null
) : Iterable<EventVariant> {
    @Deprecated(
        level = DeprecationLevel.WARNING,
        message = "Do not use an empty constructor; call EventTags.NONE instead",
        replaceWith = ReplaceWith("EventTags.NONE")
    )
    constructor() : this(null, null, null)

    override fun toString(): String {
        return "${country?.representableName}, " +
                "${ageGroup?.representableName}, " +
                "${organization?.representableName}"
    }

    override fun iterator(): Iterator<EventVariant> =
        listOfNotNull<EventVariant>(country, ageGroup, organization).iterator()

    companion object {
        @Suppress("DEPRECATION")
        val NONE: EventTags = EventTags()
    }
}

interface EventVariant {
    val representableName: String
}

enum class AgeGroup(override val representableName: String) : EventVariant {
    MiddleSchool("Middle School"),
    HighSchool("High School"),
    HighSchoolNovice("High School Novice"),
    HighSchoolJunior("High School Junior"),
    HighSchoolSenior("High School Senior"),
    College("College")
}

enum class Organization(override val representableName: String) : EventVariant {
    NSDA("NSDA"),
    NCFL("NCFL"),
    NPDA("NPDA")
}

enum class Location(override val representableName: String) : EventVariant {
    UnitedStates("American"),
    Britain("British"),
    Canada("Canadian"),
    Netherlands("Dutch")
}

//
//@Serializable
//sealed class EventVariant(val name: String) {
//
//
//    sealed class AgeGroup(name: String) : EventVariant(name) {
//        object MiddleSchool : AgeGroup("Middle School")
//        object College : AgeGroup("College")
//        sealed class HighSchool(name: String) :
//            AgeGroup(this.name + name) {
//            companion object : AgeGroup("High School")
//            object Senior : HighSchool("High School Senior")
//            object Junior : HighSchool("High School Junior")
//            object Novice : HighSchool("High School Novice")
//        }
//    }
//
//    sealed class CompetitorNumber(name: String) : EventVariant(name) {
//        object Individual : CompetitorNumber("Individual")
//        object TwoPersonTeam : CompetitorNumber("Two Person Team")
//    }
//
//    sealed class Organization(name: String) : EventVariant(name) {
//        object NSDA : Organization("NSDA")
//        object NPDA : Organization("NPDA")
//    }
//
//    sealed class Country(name: String) : EventVariant(name) {
//        object UnitedStates : Country("American")
//        object Britain : Country("British")
//        object Canada : Country("Canadian")
//        object Netherlands : Country("Dutch")
//    }
//}