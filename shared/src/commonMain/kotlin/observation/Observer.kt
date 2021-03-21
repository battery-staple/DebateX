package com.rohengiralt.debatex.observation

fun interface Observer {
    fun notify()

    fun Observable<Observer>.subscribe(): Unit = addSubscriber(this@Observer)
}