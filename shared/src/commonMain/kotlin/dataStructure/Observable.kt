package com.rohengiralt.debatex.dataStructure

import com.rohengiralt.debatex.viewModel.ViewModel

interface Observable {
    var subscriber: ViewModel<*>?
}