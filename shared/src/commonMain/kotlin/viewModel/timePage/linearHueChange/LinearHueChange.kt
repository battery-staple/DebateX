package com.rohengiralt.debatex.viewModel.timePage.linearHueChange

import com.rohengiralt.debatex.propertyDelegates.Sorted
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.seconds
import kotlin.jvm.JvmName

internal class LinearHueChange(
    private val startHue: Double,
    jumpAtProgresses: List<Pair<Double, Double>>,
    jumpsAreFromEnd: Boolean = false
) : (Double) -> Double { //TODO: ensure in range [0,1]
    constructor(
        startHue: Double,
        endHue: Double
    ) : this(
        startHue = startHue,
        jumpAtProgresses = listOf(1.0 to endHue)
    )

    constructor(
        start: Pair<TimeSpan, Double>,
        totalTime: TimeSpan,
        jumpAtIntervals: List<Pair<TimeSpan, Double>>,
        jumpsAreFromEnd: Boolean = false
    ) : this(
        startHue = start.hue,
        jumpAtProgresses = jumpAtIntervals.map { jumpInterval ->
            jumpInterval.time / totalTime to jumpInterval.hue
        },
        jumpsAreFromEnd = jumpsAreFromEnd
    )

    constructor(
        startTime: TimeSpan = 0.seconds,
        startHue: Double,
        endTime: TimeSpan,
        jumpAtIntervals: List<Pair<TimeSpan, Double>>,
        jumpsAreFromEnd: Boolean = false
    ) : this(
        start = startTime to startHue,
        totalTime = endTime - startTime,
        jumpAtIntervals = jumpAtIntervals,
        jumpsAreFromEnd = jumpsAreFromEnd
    )

    private val jumpAtProgresses: List<Pair<Double, Double>> by Sorted(
        when {
            jumpAtProgresses.isEmpty() -> listOf(1.0 to startHue)
            jumpsAreFromEnd -> jumpAtProgresses.map { jump ->
                (1 - jump.progress) to jump.hue
            }
            else -> jumpAtProgresses
        }
    ) { it.progress }

    override fun invoke(progress: Double): Double {
        val linearChangeEnd = jumpAtProgresses[0]

        return when {
            progress > linearChangeEnd.progress -> {
                var greatestJumpNotGreaterThanProgress = 0.0 to 0.0
                for (jump in jumpAtProgresses) {
                    if (jump.progress > progress) break
                    else greatestJumpNotGreaterThanProgress = jump
                }
                greatestJumpNotGreaterThanProgress.hue
            }
            else -> DoubleLinearFunction(
                Point.from(
                    0.0 to startHue
                ),
                Point.from(
                    linearChangeEnd
                )
            ).invoke(progress)
        }
    }
}

private val Pair<Double, Double>.progress
    @JvmName("getDoublePairProgress")
    get() = first
val Pair<Double, Double>.hue: Double
    @JvmName("getDoublePairHue") get() = second

private val Pair<TimeSpan, Double>.time
    @JvmName("getTimeSpanPairTime") get() = first
private val Pair<TimeSpan, Double>.hue
    @JvmName("getTimeSpanPairHue") get() = second