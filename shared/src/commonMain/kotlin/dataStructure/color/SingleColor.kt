package com.rohengiralt.debatex.dataStructure.color

//import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

//@Serializable
/*inline*/ class SingleColor( //TODO: Make inline once serialization supports inline classes
    @Suppress("WEAKER_ACCESS") override val representation: ColorRepresentation
) : Color {
    override fun <T : ColorRepresentation> representationAs(clazz: KClass<T>): T =
        representation.coerce(clazz)

    fun withOpacity(opacity: Double): SingleColor = SingleColor(
        withRepresentationAs<ColorRepresentation.RGB> { copy(alpha = opacity) }
    )

    companion object {
        val WHITE: SingleColor =
            singleColorFromHexString("#FFFFFF")
        val BLACK: SingleColor =
            singleColorFromHexString("#000000")
        val MEDIUM_GRAY: SingleColor =
            singleColorFromHexString("#808080")
        val RED: SingleColor =
            singleColorFromHexString("#FF0000")
        val GREEN: SingleColor =
            singleColorFromHexString("#00FF00")
        val BLUE: SingleColor =
            singleColorFromHexString("#0000FF")
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun ColorRepresentation.toSingleColor(): SingleColor = SingleColor(this)

@Suppress("NOTHING_TO_INLINE")
private inline fun singleColorFromHexString(hexString: String) =
    colorRepresentationFromHexString<ColorRepresentation>(hexString).toSingleColor()