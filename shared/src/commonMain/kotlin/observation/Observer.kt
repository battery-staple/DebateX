package com.rohengiralt.debatex.observation

fun interface Observer {
    fun update()

    fun Observable<Observer>.subscribe(): Unit = addSubscriber(this@Observer)
}