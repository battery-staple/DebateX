package com.rohengiralt.debatex.dataStructure

import kotlinx.serialization.Serializable

interface Named { //TODO: Remove
    val name: String
}

@Serializable // TODO: Convert to inline once supported by Serialization
data class SimpleName(
    override val name: String
) : Named {
    operator fun plus(other: SimpleName): SimpleName = SimpleName(this.name + other.name)
    operator fun plus(other: String): SimpleName = SimpleName(this.name + other)
    override fun equals(other: Any?): Boolean = when (other) {
        is Named -> this.name == other.name
        else -> false
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

/**
 * A data class that represents a full and shortened name.
 *
 * @property long the full name
 * @property short the shortened name
 */
@Serializable
data class ShortenableName(
    val long: SimpleName,
    val short: SimpleName? = null
) : Named by long {
    constructor(longName: String, shortName: String?) : this(
        SimpleName(longName),
        shortName?.let { SimpleName(it) }
    )

    val longName: String inline get() = long.name
    val shortName: String? inline get() = short?.name

    val shortOrLong: SimpleName inline get() = short ?: long
    val shortNameOrLong: String inline get() = shortName ?: longName

    operator fun plus(other: ShortenableName): ShortenableName =
        ShortenableName(
            long + other.long,
            if (short != null && other.short != null) {
                short + other.short
            } else null
        )

    operator fun plus(other: String): ShortenableName =
        ShortenableName(long + other, short?.plus(other))

    override fun equals(other: Any?): Boolean = when (other) {
        is ShortenableName -> this.long == other.long && this.short == other.short
        is Named -> this.long.name == other.name
        else -> false
    }

    override fun hashCode(): Int {
        var result = long.hashCode()
        result = 31 * result + (short?.hashCode() ?: 0)
        return result
    }
}

//@Serializable
data class AcronymableName(
    override val name: String,
    val acronymName: String? = null
) : Named {
    fun plus(other: AcronymableName): AcronymableName =
        AcronymableName(this.name + other.name, this.acronymName + other.acronymName)

    fun plus(other: String): AcronymableName =
        this.copy(name = this.name + other)

    override fun equals(other: Any?): Boolean = when (other) {
        is AcronymableName -> this.name == other.name && this.acronymName == other.acronymName
        is Named -> this.name == other.name
        else -> false
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (acronymName?.hashCode() ?: 0)
        return result
    }
}