package com.rohengiralt.debatex.model

import com.rohengiralt.debatex.observation.Observable
import com.rohengiralt.debatex.observation.Observer
import com.rohengiralt.debatex.observation.PassthroughPublisher
import com.rohengiralt.debatex.observation.WeakReferencePublisher
import com.rohengiralt.debatex.viewModel.ViewModel
import com.rohengiralt.debatex.viewModel.ViewModelOnly

abstract class Model(
    @OptIn(ViewModelOnly::class) protected open val observationHandler: PassthroughPublisher<ViewModel>
) : Observer by observationHandler, Observable<@OptIn(ViewModelOnly::class) ViewModel> by observationHandler {
    constructor() : this(PassthroughPublisher(WeakReferencePublisher()))
}