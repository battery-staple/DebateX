package com.rohengiralt.debatex.model

import com.rohengiralt.debatex.observation.Observable
import com.rohengiralt.debatex.observation.Observer
import com.rohengiralt.debatex.observation.PassthroughPublisher
import com.rohengiralt.debatex.observation.WeakReferencePublisher
import com.rohengiralt.debatex.viewModel.ViewModel

abstract class Model(
    protected open val observationHandler: PassthroughPublisher<ViewModel>
) : Observer by observationHandler, Observable<ViewModel> by observationHandler {
    constructor() : this(PassthroughPublisher(WeakReferencePublisher()))
}