package com.rohengiralt.debatex.viewModel.timePage.linearHueChange

interface LinearFunction<T, R> : (T) -> R

typealias DoublePoint = Point<Double, Double>

class DoubleLinearFunction(
    @Suppress("WEAKER_ACCESS") val slope: Double,
    @Suppress("WEAKER_ACCESS") val yIntercept: Double
) : LinearFunction<Double, Double> {

    constructor(point1: DoublePoint, point2: DoublePoint) : this(
        slopeBetweenPoints(
            point1,
            point2
        ),
        yInterceptOfLineBetweenPoints(
            point1,
            point2
        )
    )

    override fun invoke(p1: Double): Double {
        return slope * p1 + yIntercept
    }

    companion object {
        private fun slopeBetweenPoints(point1: DoublePoint, point2: DoublePoint): Double {
            require(!(point1 hasSameXValueAs point2)) { "Points must have different x-values." }

            return (point2.y - point1.y) / (point2.x - point1.x)
        }

        private fun yInterceptOfLineBetweenPoints(
            point1: DoublePoint,
            point2: DoublePoint
        ): Double {
            return point1.y - slopeBetweenPoints(
                point1,
                point2
            ) * point1.x
        }
    }
}

class Point<X, Y>(val x: X, val y: Y) {
    val coordinates: Pair<X, Y>
        get() = Pair(x, y)

    infix fun hasSameXValueAs(other: Point<X, *>): Boolean {
        return this.x == other.x
    }

    infix fun hasSameYValueAs(other: Point<*, Y>): Boolean {
        return this.y == other.y
    }

    companion object Factory {
        fun <A, B> from(pair: Pair<A, B>) =
            Point(
                x = pair.first,
                y = pair.second
            )
    }
}