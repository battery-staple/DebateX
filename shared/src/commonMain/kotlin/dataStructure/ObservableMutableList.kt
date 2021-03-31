package com.rohengiralt.debatex.dataStructure

import com.rohengiralt.debatex.observation.Observable
import com.rohengiralt.debatex.observation.Observer
import com.rohengiralt.debatex.observation.Publisher
import com.rohengiralt.debatex.observation.WeakReferencePublisher

//TODO: __**TEST**__
class ObservableMutableList<E> private constructor(
    private val reference: MutableList<E>,
    private val publisher: Publisher<Observer>,
) : MutableList<E>, List<E> by reference, Observable<Observer> by publisher {
    constructor(collection: Collection<E> = listOf()) : this(collection.toMutableList(), WeakReferencePublisher())

    override val size: Int get() = reference.size

    override fun get(index: Int): E = reference[index]

    override fun add(index: Int, element: E): Unit = withPublishing {
        reference.add(index, element)
    }

    override fun removeAt(index: Int): E = withPublishing {
        reference.removeAt(index)
    }

    override fun set(index: Int, element: E): E = withPublishing {
        reference.set(index, element)
    }

    private fun <T> withPublishing(block: () -> T): T = block().also { publisher.publish() }

    override fun add(element: E): Boolean = withPublishing {
        reference.add(element)
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean = withPublishing {
        reference.addAll(index, elements)
    }

    override fun addAll(elements: Collection<E>): Boolean = withPublishing {
        reference.addAll(elements)
    }

    override fun clear(): Unit = withPublishing {
        reference.clear()
    }

    override fun remove(element: E): Boolean = withPublishing {
        reference.remove(element)
    }

    override fun removeAll(elements: Collection<E>): Boolean = withPublishing {
        reference.removeAll(elements)
    }

    override fun retainAll(elements: Collection<E>): Boolean = withPublishing {
        reference.retainAll(elements)
    }

    override fun iterator(): MutableIterator<E> = Itr(this)

    override fun listIterator(): MutableListIterator<E> = Itr(this)

    override fun listIterator(index: Int): MutableListIterator<E> = Itr(this, index)

    override fun subList(fromIndex: Int, toIndex: Int): ObservableMutableList<E> =
        ObservableMutableList(reference.subList(fromIndex, toIndex))

    class Itr<T>(
        private val list: ObservableMutableList<T>,
        index: Int = 0,
    ) : MutableListIterator<T> {
        private var currentIndex = index

        override fun nextIndex(): Int = currentIndex + 1

        override fun previousIndex(): Int = currentIndex - 1

        override fun hasNext(): Boolean = nextIndex() + 1 in 0 until list.size

        override fun hasPrevious(): Boolean = previousIndex() in 0 until list.size

        override fun next(): T = list[++currentIndex]

        override fun previous(): T = list[--currentIndex]

        override fun add(element: T): Unit = list.add(currentIndex, element)

        override fun remove() {
            list.removeAt(currentIndex)
        }

        override fun set(element: T) {
            list[currentIndex] = element
        }

    }
}


//class OldObservableMutableList<E>(
//    private val mutableList: MutableList<E> = arrayListOf(),
//    override var subscriber: OldViewModel<*>? = null,
//) : MutableList<E> by mutableList, Observable, RandomAccess { //TODO: Override iterator?
//    constructor(
//        initialCapacity: Int,
//        subscriber: OldViewModel<*>? = null,
//    ) : this(ArrayList(initialCapacity), subscriber)
//
//    constructor(
//        elements: Collection<E>,
//        subscriber: OldViewModel<*>? = null,
//    ) : this(ArrayList(elements), subscriber)
//
//    @Suppress("NOTHING_TO_INLINE")
//    private inline fun <T> T.alsoUpdateSubscriberIf(predicate: T.() -> Boolean) =
//        also { if (predicate()) subscriber?.update() }/*.also { logger.info("alsoupdatesubscriberif update! (now ${mutableList.joinToString { it.toString() }})") }*/
//
//    @Suppress("NOTHING_TO_INLINE")
//    private inline fun <T> T.alsoUpdateSubscriber() =
//        also { subscriber?.update() }/*.also { logger.info("alsoupdatesubscriber update! (now ${mutableList.joinToString { it.toString() }})") }*/
//
//    override fun add(element: E): Boolean =
//        mutableList.add(element).alsoUpdateSubscriberIf { this }
//
//    override fun addAll(elements: Collection<E>): Boolean =
//        mutableList.addAll(elements).alsoUpdateSubscriberIf { this }
//
//    override fun add(index: Int, element: E) =
//        mutableList.add(index, element).alsoUpdateSubscriber()
//
//    override fun addAll(index: Int, elements: Collection<E>): Boolean =
//        mutableList.addAll(index, elements).alsoUpdateSubscriberIf { this }
//
//    override fun remove(element: E): Boolean =
//        mutableList.remove(element).alsoUpdateSubscriberIf { this }
//
//    override fun removeAt(index: Int): E =
//        mutableList.removeAt(index).alsoUpdateSubscriber()
//
//    override fun removeAll(elements: Collection<E>): Boolean =
//        mutableList.removeAll(elements).alsoUpdateSubscriberIf { this }
//
//    override fun retainAll(elements: Collection<E>): Boolean =
//        mutableList.retainAll(elements).alsoUpdateSubscriberIf { this }
//
//    override fun clear() =
//        mutableList.clear().alsoUpdateSubscriber()
//
//    override fun set(index: Int, element: E): E =
//        mutableList.set(index, element).alsoUpdateSubscriber()
//
//    companion object {
//        val logger = loggerForClass<ObservableMutableList<*>>()
//    }
//}
//
//@Suppress("FunctionName")
//inline fun <reified T> ObservableMutableList(
//    size: Int,
//    init: (Int) -> T,
//): ObservableMutableList<T> =
//    MutableList(size, init).toObservable()

@Suppress("NOTHING_TO_INLINE")
inline fun <E> Collection<E>.toObservable(): ObservableMutableList<E> =
    ObservableMutableList(this)

//@Suppress("NOTHING_TO_INLINE")
//inline fun <E> Iterable<E>.toObservable(subscriber: OldViewModel<*>?) =
//    this.toMutableList().toObservable(subscriber)
//
//inline fun <reified E> observableMutableListOf(
//    vararg elements: E,
//    subscriber: OldViewModel<*>? = null,
//) = elements.toMutableList().toObservable(subscriber)
