package com.rohengiralt.debatex.datafetch

import com.rohengiralt.debatex.model.Model
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun interface DataFetcher<out M/* : Model*/> : //TODO: Any way to make List/Collection : Model?
    ReadOnlyProperty<Any?, M> { //TODO: Require all models to have only dataFetchers, not submodels
    fun fetch(): M

    fun fetchOrNull(): M? = fetch()

    override fun getValue(thisRef: Any?, property: KProperty<*>): M = fetch()
}

//TODO: Convert to generic inline class if they ever become supported
interface ConstantModelFetcher<M : Model> : DataFetcher<M> {
    val model: M

    override fun fetch(): M = model
}

@Suppress("FunctionName")
fun <M : Model> ConstantModelFetcher(model: M): ConstantModelFetcher<M> =
    object : ConstantModelFetcher<M> {
        override val model: M = model
    }