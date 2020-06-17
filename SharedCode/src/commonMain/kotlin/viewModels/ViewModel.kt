package com.rohengiralt.debatex.viewModels

import com.rohengiralt.debatex.Logger
import com.rohengiralt.debatex.loggerForClass
import com.rohengiralt.debatex.updateObservableObject
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

////@ThreadLocal
val viewModels: MutableList<ViewModel> = mutableListOf()

abstract class ViewModel {

    open var updating: Boolean = true
        set(value) {
            field = value
            logger.info("updating set to $field")
        }
//        get() {
//            logger.info("updating queried as $field")
//            return field
//        }

    protected fun <T> updatesViewModelOnChange(initialValue: T): ReadWriteProperty<Any?, T> =
        Delegates.observable(initialValue) { _, old, new ->
            update()
            logger.info("Updated observable from $old to $new")
        }

    @Suppress("WEAKER_ACCESS")
    protected fun update() {
//        if (updating) {
        updateObservableObject(this)
//        }
    }

    companion object {
        private val logger: Logger = loggerForClass<ViewModel>()
    }
}

