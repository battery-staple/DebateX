package com.rohengiralt.debatex.dataStructure

import com.rohengiralt.debatex.viewModel.ViewModel

class Button<out T : ViewModel<*>>(
    val display: T,
    val onClick: () -> Unit
)