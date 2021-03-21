package com.rohengiralt.debatex.observation

interface Observable<in O : Observer> {
    fun addSubscriber(observer: O)
    fun removeSubscriber(observer: O)
}