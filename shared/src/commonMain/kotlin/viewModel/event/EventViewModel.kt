package com.rohengiralt.debatex.viewModel.event

import com.rohengiralt.debatex.dataStructure.NonNullSelectableList
import com.rohengiralt.debatex.dataStructure.color.Color
import com.rohengiralt.debatex.dataStructure.color.ColorRepresentation
import com.rohengiralt.debatex.dataStructure.color.SingleColor
import com.rohengiralt.debatex.loggerForClass
import com.rohengiralt.debatex.model.event.EventModel
import com.rohengiralt.debatex.viewModel.TimerViewModel
import com.rohengiralt.debatex.viewModel.ViewModel
import com.rohengiralt.debatex.viewModel.event.linearHueChange.LinearHueChange
import com.soywiz.klock.milliseconds
import com.soywiz.klock.seconds


abstract class EventViewModel : ViewModel() {
    abstract val title: String
    abstract val progress: Double
    abstract var currentPageIndex: Int
    abstract var currentSecondaryTimerIndex: Int?
    abstract val primaryTimers: List<TimerViewModel>
    abstract val secondaryTimers: List<TimerViewModel>?
    abstract val currentTopColor: Color
    abstract val currentBottomColor: Color

    abstract fun resetCurrent()
    abstract fun resetAll()
    abstract fun stopAll()
}

class BasicEventViewModel(
    private val model: EventModel<*>,
) : EventViewModel() {

    override val title: String get() = model.format.name.longName + " " + model.tags.joinToString(separator = " ") { "(${it.representableName})" }

    override val progress: Double
        get() = primaryTimers.currentSelection.value.progress

    override val primaryTimers: NonNullSelectableList<TimerViewModel> by lazy {
        NonNullSelectableList(
            model.primaryTimers.map {
                TimerViewModel(it, 0, 2, true)
            }
        ).also {
            it.addSubscriber(this)
            onNewTimerSelected(it.currentSelection.value)
        }
    }

    private inline val currentPrimaryTimer: TimerViewModel inline get() = primaryTimers.currentSelection.value

    override val secondaryTimers: NonNullSelectableList<TimerViewModel>? by lazy {
        model.secondaryTimers?.map {
            TimerViewModel(it, 1, 1, true)
        }?.let(::NonNullSelectableList)
    }

    var x = mutableMapOf<Int, Int>()
    fun a() {
        x[0] = 3
    }

    override var currentPageIndex: Int // cannot use property delegate here in order to avoid initializing primaryTimers
        get() = primaryTimers.currentIndex
        set(value) { primaryTimers.currentIndex = value }

    override var currentSecondaryTimerIndex: Int?
        get() = secondaryTimers?.currentIndex
        set(value) {
            require(secondaryTimers != null) { "No secondary timers to set index" }
            require(value != null && value in secondaryTimers!!.indices) { "Invalid index." }
            secondaryTimers!!.currentIndex = value
        }

    override fun update() {
        super.update()

        currentPrimaryTimer.let {
            if (it != lastSelectedTimer) {
                onNewTimerSelected(it)
            }
        }

        secondaryTimers?.mapIndexedNotNull { index, secondaryTimer ->
            with(model.secondaryTimerChangeStrategy) {
                if (matches(secondaryTimer.speakers, primaryTimers.currentSelection.value.speakers)) index else null
            }
        }?.singleOrNull()?.let {
            secondaryTimers!!.currentIndex = it
        }
    }

    private var lastSelectedTimer: TimerViewModel? = null
    private fun onNewTimerSelected(newTimer: TimerViewModel) {
//        primaryTimers.forEach { it.removeSubscriber(this) }
        lastSelectedTimer?.isRunning = false
        lastSelectedTimer?.removeSubscriber(this)
        newTimer.addSubscriber(this)
        lastSelectedTimer = newTimer
    }

    @Suppress("UNUSED")
    override val currentTopColor: SingleColor
        get() = SingleColor(
            ColorRepresentation.HSV(
                hue = currentHue,
                saturation = BACKGROUND_SATURATION,
                value = BACKGROUND_TOP_VALUE
            )
        )

    @Suppress("UNUSED")
    override val currentBottomColor: SingleColor
        get() = SingleColor(
            ColorRepresentation.HSV(
                hue = currentHue,
                saturation = BACKGROUND_SATURATION,
                value = BACKGROUND_BOTTOM_VALUE
            )
        )

    private val currentHue: Double
        get() = LinearHueChange(
            endTime = primaryTimers.currentSelection.value.totalTime.timeSpan,
            startHue = 0.3,
            jumpAtIntervals = listOf(
                16.seconds + 1.milliseconds to 0.75,
                16.seconds to 0.08,
                6.seconds to 0.0
            ),
            jumpsAreFromEnd = true
        ).invoke(progress)

    override fun resetCurrent() {
        primaryTimers.currentSelection.value.reset()
    }

    override fun resetAll() {
        primaryTimers.forEach(TimerViewModel::reset)
    }

    override fun stopAll() {
        primaryTimers.forEach { it.isRunning = false }
    }

    companion object {
        private const val BACKGROUND_SATURATION: Double = 0.8 // TODO: Get from config file
        private const val BACKGROUND_TOP_VALUE: Double = 0.75
        private const val BACKGROUND_BOTTOM_VALUE: Double = 0.4

        private val logger = loggerForClass<BasicEventViewModel>()
    }
}

//class EventViewModel<T : Speaker<*>>(
//    override val modelFetcher: DataFetcher<EventModel<T>>
//) : OldViewModel<EventModel<T>>() {
//    internal val untaggedName: ShortenableName get() = model.overrideName ?: model.type.name
//
//    @Suppress("UNUSED")
//    val displayName: ShortenableName
//        get() = untaggedName + " " + model.tags.joinToString(separator = " ") { "(${it.representableName})" }
//
//    //    @InternalCoroutinesApi
//    @Suppress("UNUSED")
//    val pages: List<TimePageViewModel<T>> by lazy {
//        model.pageFetchers.map {
//            TimePageViewModel(it)
//        }
//    }
//
//    @Suppress("UNUSED")
//    val secondaryTimers: List<SecondaryTimerViewModel<T>>? =
//        model.secondaryTimerModelFetchers?.map(::SecondaryTimerViewModel)
//
//    @Suppress("UNUSED")
////    @ExperimentalMultiplatform
////    @Throws(IndexOutOfBoundsException::class)
//    fun setSecondaryTimerIndexToOnPageChange(toIndex: Int): Int? {
//        if (secondaryTimers != null) {
//            val currentTimePage = model.pageFetchers[toIndex].fetch()
//
//            val secondaryTimerIndicesMatchingCurrentPage: List<Int> =
//                secondaryTimers.mapIndexedNotNull { index, secondaryTimer ->
//                    if (secondaryTimer matches currentTimePage) index else null
//                }
//
//            if (secondaryTimerIndicesMatchingCurrentPage.size == 1) return secondaryTimerIndicesMatchingCurrentPage[0]
//        }
//
//        return null
//    }
//
//    @Suppress("NOTHING_TO_INLINE")
//    private inline infix fun SecondaryTimerViewModel<*>.matches(primaryTimer: TimePageModel<*>) =
//        when (model.secondaryTimersAutomaticChangeMatchMode) {
//            All -> this.speakers == primaryTimer.belongingTo
//            Any -> this.speakers.any { it in primaryTimer.belongingTo }
//            Never -> false
//        }
//
//    @Suppress("UNUSED")
//    val card: EventCardViewModel = EventCardViewModel(this, modelFetcher)
//}