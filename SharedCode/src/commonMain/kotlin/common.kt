package com.rohengiralt.debatex

import com.rohengiralt.debatex.viewModels.ViewModel
import kotlin.reflect.KClass

var updateObservableObject: (ViewModel) -> Unit =
    { throw RuntimeException("UpdateObservableObject not set") }

@ExperimentalMultiplatform
@OptionalExpectation
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.SOURCE)
expect annotation class Throws(vararg val exceptionClasses: KClass<out Throwable>)