package com.rohengiralt.debatex.dataStructure

//import kotlinx.serialization.Serializable

sealed class Size {
    //@Serializable
    sealed class ScreenDimension {
        //@Serializable
        sealed class OrientationBased : ScreenDimension() {
            //@Serializable
            object Vertical : OrientationBased()

            //@Serializable
            object Horizontal : OrientationBased()
        }

        //@Serializable
        sealed class Constant : ScreenDimension() {
            //@Serializable
            object Longest : Constant()

            //@Serializable
            object Shortest : Constant()
        }
    }
}

//@Serializable
sealed class FixedSize : Size()

//@Serializable
data class LinearFixedSize<T : Size.ScreenDimension.OrientationBased, out U : Size.ScreenDimension>(
    val screenProportion: Double,
    val viewDimension: T,
    val screenDimension: U
) : FixedSize()

sealed class PlanarFixedSize : FixedSize()

data class OrientationBasedPlanarFixedSize(
    val vertical: LinearFixedSize<ScreenDimension.OrientationBased.Vertical, ScreenDimension.OrientationBased.Vertical>,
    val horizontal: LinearFixedSize<ScreenDimension.OrientationBased.Horizontal, ScreenDimension.OrientationBased.Horizontal>
) : PlanarFixedSize()

data class ConstantPlanarFixedSize(
    val largest: LinearFixedSize<ScreenDimension.OrientationBased.Vertical, ScreenDimension.Constant.Longest>,
    val smallest: LinearFixedSize<ScreenDimension.OrientationBased.Vertical, ScreenDimension.Constant.Shortest>
) : PlanarFixedSize()

data class VariableSize<T : FixedSize>(
    val minimumSize: T,
    val idealSize: T,
    val maximumSize: T
) : Size()