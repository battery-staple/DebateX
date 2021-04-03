package com.rohengiralt.debatex.observation

import com.rohengiralt.debatex.WeakReference
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

abstract class Publisher<in O : Observer> : Observable<O> {
    abstract fun publish()

    inner class FieldPublishingProperty<T>(
        initialValue: T,
        var get: (FieldPublishingProperty<T>.() -> T)? = null,
        private val set: (FieldPublishingProperty<T>.(value: T) -> Unit)? = null,
        private val onlyPublishOnChange: Boolean = false
    ) : ReadWriteProperty<Any?, T> {
        var field: T = initialValue

        override fun getValue(thisRef: Any?, property: KProperty<*>): T = get?.invoke(this) ?: field

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            field.let { oldField ->
                (set ?: { field = it }).invoke(this, value)
                if (onlyPublishOnChange) {
                    if (oldField != value) publish()
                } else publish()
            }
        }
    }

    inner class PublishingProperty<T>(
        private val get: () -> T,
        private val set: (value: T) -> Unit,
        private val onlyPublishOnChange: Boolean = false,
    ) : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T = get()

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            if (onlyPublishOnChange) {
                get().let { oldValue ->
                    set(value)
                    if (oldValue != value) publish()
                }
            } else {
                set(value)
                publish()
            }
        }
    }

    inline fun <T> published(
        initialValue: T,
        noinline get: (FieldPublishingProperty<T>.() -> T)? = null,
        noinline set: (FieldPublishingProperty<T>.(value: T) -> Unit)? = null,
        onlyPublishOnChange: Boolean = false,
    ): ReadWriteProperty<Any?, T> =
        FieldPublishingProperty(initialValue, get, set, onlyPublishOnChange)

    inline fun <T> published(
        noinline get: () -> T,
        noinline set: (value: T) -> Unit,
        onlyPublishOnChange: Boolean = false,
    ): ReadWriteProperty<Any?, T> =
        PublishingProperty(get, set, onlyPublishOnChange)

    inline fun <T> published(
        delegate: ReadWriteProperty<Any?, T>,
        onlyPublishOnChange: Boolean = false,
    ): PropertyDelegateProvider<Any?, ReadWriteProperty<Any?, T>> = PropertyDelegateProvider { thisRef, property ->
        PublishingProperty(
            { delegate.getValue(thisRef, property) },
            { value -> delegate.setValue(thisRef, property, value) },
            onlyPublishOnChange
        )
    }

    inline fun <T> published(
        delegate: KMutableProperty0<T>,
        onlyPublishOnChange: Boolean = false,
    ): PropertyDelegateProvider<Any?, ReadWriteProperty<Any?, T>> = PropertyDelegateProvider { thisRef, property ->
        PublishingProperty(
            { delegate.getValue(thisRef, property) },
            { value -> delegate.setValue(thisRef, property, value) },
            onlyPublishOnChange
        )
    }

    inline fun <T> published(
        noinline getDelegate: () -> KMutableProperty0<T>,
        onlyPublishOnChange: Boolean = false,
    ): PropertyDelegateProvider<Any?, ReadWriteProperty<Any?, T>> {
        val delegate by lazy(getDelegate)
        return published(delegate, onlyPublishOnChange)
    }
}

open class WeakReferencePublisher<O : Observer> : Publisher<O>() {
    private val weakSubscribers =
        mutableListOf<WeakReference<O>>() //TODO: Using Set here wouldn't allow two objects that compare == but not === - is that acceptable?
    private val strongSubscribers =
        mutableListOf<O>()

    private val subscribers: Iterable<O> get() = weakSubscribers.mapNotNull { it.value } + strongSubscribers

    override fun publish() {
        subscribers.forEach { it.update() }
    }

    override fun addSubscriber(observer: O) {
        if (subscribers.any { it === observer }) return
        weakSubscribers.add(WeakReference(observer))
    }

    fun addStrongSubscriber(observer: O) {
        if (subscribers.any { it === observer }) return
        strongSubscribers.add(observer)
    }

    override fun removeSubscriber(observer: O) {
        with(weakSubscribers) {
            indexOfFirst { it.value === observer }.let { index ->
                if (index != -1) removeAt(index)
            }
        }
        with(strongSubscribers) {
            indexOfFirst { it === observer }.let { index ->
                if (index != -1) removeAt(index)
            }
        }
    }
}

open class PassthroughPublisher<O : Observer>(
    private val publisher: Publisher<O>,
) : Publisher<O>(), Observable<O> by publisher, Observer {
    @Suppress("PublicApiImplicitType")
    override fun publish() = publisher.publish()

    @Suppress("PublicApiImplicitType")
    override fun update() = publish()
}