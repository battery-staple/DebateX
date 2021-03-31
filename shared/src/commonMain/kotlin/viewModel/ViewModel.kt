package com.rohengiralt.debatex.viewModel

import com.rohengiralt.debatex.observation.Observable
import com.rohengiralt.debatex.observation.Observer
import com.rohengiralt.debatex.observation.PassthroughPublisher
import com.rohengiralt.debatex.observation.WeakReferencePublisher

class ViewModelPublisher(val viewModel: ViewModel? /* needs to be passed in constructor, so may be null */) : WeakReferencePublisher<Observer>() {
    override fun publish() {
        super.publish()
        viewModel.updateViewModel()
    }
}


abstract class ViewModel : Observer, Observable<Observer> {
    protected open val observationHandler: PassthroughPublisher<Observer> =
        PassthroughPublisher(ViewModelPublisher(this))

    override fun addSubscriber(observer: Observer): Unit = observationHandler.addSubscriber(observer)
    override fun removeSubscriber(observer: Observer): Unit = observationHandler.removeSubscriber(observer)

    override fun update(): Unit = observationHandler.update()
}

var updateViewModel: ViewModel?.() -> Unit = {} //TODO: InjectAtLaunch