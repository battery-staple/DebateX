package com.rohengiralt.debatex.viewModel

import com.rohengiralt.debatex.Logger
import com.rohengiralt.debatex.dataStructure.Observable
import com.rohengiralt.debatex.datafetch.DataFetcher
import com.rohengiralt.debatex.loggerForClass
import com.rohengiralt.debatex.model.Model
import com.rohengiralt.debatex.updateObservableObject
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class ViewModel<M : Model> {

    protected abstract val modelFetcher: DataFetcher<M>

    protected val model: M
        get() = modelFetcher.fetch()

    /*open*/ var updating: Boolean = true

    @Suppress("NOTHING_TO_INLINE")
    protected inline fun subscribeTo(observable: Observable?) {
        observable?.subscriber = this
    }

    @Suppress("NOTHING_TO_INLINE")
    protected inline fun maybeSubscribeTo(maybeObservable: Any?): Unit =
        subscribeTo(maybeObservable as? Observable)

    protected inline fun <T> observingChangeOf(
        initialValue: T,
        crossinline alsoExecute: (T, T) -> Unit = { _, _ -> }
    ): ReadWriteProperty<ViewModel<*>?, T> {
        maybeSubscribeTo(initialValue)

        return Delegates.observable(initialValue) { _, old, new ->
            update()
            maybeSubscribeTo(new)
            logger.info("Updated observable from $old to $new")
            alsoExecute(new, old)
        }
    }

    protected inline fun <T : Observable> observable(
        observable: T
//        crossinline alsoExecute: () -> Unit = { }
    ): ReadOnlyProperty<ViewModel<*>, T> =
        object : ReadOnlyProperty<ViewModel<*>, T> {
            init {
                subscribeTo(observable)
            }

            override fun getValue(thisRef: ViewModel<*>, property: KProperty<*>): T =
                observable
        }

    @Suppress("WEAKER_ACCESS")
    fun update() {
        if (updating) updateObservableObject(this)
    }

    companion object {
        val logger: Logger = loggerForClass<ViewModel<*>>()
    }
}

//abstract class FetchedModelViewModel<M : Model>(private val fetcher: DataFetcher<M>) : ViewModel<M>() {
//    override val model: M
//        get() = fetcher.fetch()
//}