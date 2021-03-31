package com.rohengiralt.debatex.dataStructure

import com.rohengiralt.debatex.observation.Observable
import com.rohengiralt.debatex.observation.Observer
import com.rohengiralt.debatex.observation.Publisher
import com.rohengiralt.debatex.observation.WeakReferencePublisher

interface SelectableList<out T, I : Int?> : List<T>, Observable<Observer> {
    var currentIndex: I
    val currentSelection: Selection<T, I>

    sealed class Selection<out T, out I : Int?> {
        abstract val value: T?

        class None<T> : Selection<T, Nothing?>() {
            override val value: T? = null
        }

        data class Some<T>(override val value: T) : Selection<T, Int>() //TODO: value class in kt 1.5?
    }
}

class NullableSelectableList<out T> private constructor(
    private val publisher: Publisher<Observer>,
    private val reference: List<T>,
    initialIndex: Int? = null
) : SelectableList<T, Int?>, List<T> by reference, Observable<Observer> by publisher {
    constructor(iterable: Iterable<T>, initialIndex: Int? = null) :
            this(WeakReferencePublisher(), iterable.toList(), initialIndex)

    constructor(vararg items: T, initialIndex: Int? = null) : this(items.toList(), initialIndex)

    override var currentIndex: Int? = initialIndex
        set(value) {
            require(value in indices) { "Invalid index." }
            field = value
            publisher.publish()
        }

    override val currentSelection: SelectableList.Selection<T, Int?> //Implicit contract: None() IFF currentIndex == null, else Some()
        get() = currentIndex?.let { SelectableList.Selection.Some(this[it]) } ?: SelectableList.Selection.None()

}

class NonNullSelectableList<out T>(
    private val nullableSelectable: NullableSelectableList<T>,
    initialIndex: Int = 0,
) : SelectableList<T, Int>, List<T> by nullableSelectable, Observable<Observer> by nullableSelectable {
    constructor(collection: Iterable<T>, initialIndex: Int = 0) :
            this(NullableSelectableList(collection, initialIndex))

    constructor(vararg items: T, initialIndex: Int = 0) : this(items.toList(), initialIndex)

    init {
        nullableSelectable.currentIndex = initialIndex
    }

    override var currentIndex: Int
        get() = nullableSelectable.currentIndex!!
        set(value) {
            nullableSelectable.currentIndex = value
        }

    override val currentSelection: SelectableList.Selection.Some<out T>
        get() = nullableSelectable.currentSelection as SelectableList.Selection.Some
}

fun <T> Iterable<T>.toNullableSelectable(): NullableSelectableList<T> = NullableSelectableList(this)
fun <T> Iterable<T>.toNonNullSelectable(): NonNullSelectableList<T> = NonNullSelectableList(this)
fun <T> Array<T>.toNullableSelectable(): NullableSelectableList<T> = NullableSelectableList(*this)
fun <T> Array<T>.toNonNullSelectable(): NonNullSelectableList<T> = NonNullSelectableList(*this)