package com.rohengiralt.debatex.viewModel

interface Linked<out T : ViewModel<*>> {
    val link: T
}