package com.rohengiralt.debatex.viewModel

import com.rohengiralt.debatex.observation.Observable
import com.rohengiralt.debatex.observation.Observer
import com.rohengiralt.debatex.observation.PassthroughPublisher
import com.rohengiralt.debatex.observation.WeakReferencePublisher

@RequiresOptIn
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
internal annotation class ViewModelOnly

@OptIn(ViewModelOnly::class)
class ViewModelPublisher(val viewModel: ViewModel? /* needs to be passed in constructor, so may be null */) : WeakReferencePublisher<Observer>() {
    override fun publish() {
        super.publish()
        viewModel.updateViewModel()
    }
}

@ViewModelOnly
abstract class ViewModel : Observer, Observable<Observer> {
    protected open val observationHandler: PassthroughPublisher<Observer> =
        PassthroughPublisher(ViewModelPublisher(this))

    override fun addSubscriber(observer: Observer): Unit = observationHandler.addSubscriber(observer)
    override fun removeSubscriber(observer: Observer): Unit = observationHandler.removeSubscriber(observer)

    override fun notify(): Unit = observationHandler.notify()
}

@OptIn(ViewModelOnly::class)
var updateViewModel: ViewModel?.() -> Unit = {} //TODO: InjectAtLaunch