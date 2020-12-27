package com.rohengiralt.debatex.dataStructure

import com.rohengiralt.debatex.loggerForClass
import com.rohengiralt.debatex.viewModel.ViewModel

class ObservableMutableList<E>(
    private val mutableList: MutableList<E> = arrayListOf(),
    override var subscriber: ViewModel<*>? = null
) : MutableList<E> by mutableList, Observable, RandomAccess { //TODO: Override iterator?
    constructor(
        initialCapacity: Int,
        subscriber: ViewModel<*>? = null
    ) : this(ArrayList(initialCapacity), subscriber)

    constructor(
        elements: Collection<E>,
        subscriber: ViewModel<*>? = null
    ) : this(ArrayList(elements), subscriber)

    @Suppress("NOTHING_TO_INLINE")
    private inline fun <T> T.alsoUpdateSubscriberIf(predicate: T.() -> Boolean) =
        also { if (predicate()) subscriber?.update() }/*.also { logger.info("alsoupdatesubscriberif update! (now ${mutableList.joinToString { it.toString() }})") }*/

    @Suppress("NOTHING_TO_INLINE")
    private inline fun <T> T.alsoUpdateSubscriber() =
        also { subscriber?.update() }/*.also { logger.info("alsoupdatesubscriber update! (now ${mutableList.joinToString { it.toString() }})") }*/

    override fun add(element: E): Boolean =
        mutableList.add(element).alsoUpdateSubscriberIf { this }

    override fun addAll(elements: Collection<E>): Boolean =
        mutableList.addAll(elements).alsoUpdateSubscriberIf { this }

    override fun add(index: Int, element: E) =
        mutableList.add(index, element).alsoUpdateSubscriber()

    override fun addAll(index: Int, elements: Collection<E>): Boolean =
        mutableList.addAll(index, elements).alsoUpdateSubscriberIf { this }

    override fun remove(element: E): Boolean =
        mutableList.remove(element).alsoUpdateSubscriberIf { this }

    override fun removeAt(index: Int): E =
        mutableList.removeAt(index).alsoUpdateSubscriber()

    override fun removeAll(elements: Collection<E>): Boolean =
        mutableList.removeAll(elements).alsoUpdateSubscriberIf { this }

    override fun retainAll(elements: Collection<E>): Boolean =
        mutableList.retainAll(elements).alsoUpdateSubscriberIf { this }

    override fun clear() =
        mutableList.clear().alsoUpdateSubscriber()

    override fun set(index: Int, element: E): E =
        mutableList.set(index, element).alsoUpdateSubscriber()

    companion object {
        val logger = loggerForClass<ObservableMutableList<*>>()
    }
}

@Suppress("FunctionName")
inline fun <reified T> ObservableMutableList(
    size: Int,
    init: (Int) -> T
): ObservableMutableList<T> =
    MutableList(size, init).toObservable()

@Suppress("NOTHING_TO_INLINE")
inline fun <E> MutableList<E>.toObservable(subscriber: ViewModel<*>? = null): ObservableMutableList<E> =
    ObservableMutableList(mutableList = this, subscriber = subscriber)

@Suppress("NOTHING_TO_INLINE")
inline fun <E> Iterable<E>.toObservable(subscriber: ViewModel<*>?) =
    this.toMutableList().toObservable(subscriber)

inline fun <reified E> observableMutableListOf(
    vararg elements: E,
    subscriber: ViewModel<*>? = null
) = elements.toMutableList().toObservable(subscriber)
