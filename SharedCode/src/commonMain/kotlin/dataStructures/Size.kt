package com.rohengiralt.debatex.dataStructures

import kotlinx.serialization.Serializable

//interface Size

@Serializable
data class Size(
    val screenProportion: Double,
    val proportionalTo: ScreenDimension
) {
    companion object {
        enum class ScreenDimension {
            Vertical, Horizontal, Largest, Smallest
        }
    }
}
