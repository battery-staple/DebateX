package com.rohengiralt.debatex.events

import com.rohengiralt.debatex.TestBase
import com.rohengiralt.debatex.dataStructure.ShortenableName
import com.rohengiralt.debatex.dataStructure.competitionTypes.Speaker
import com.rohengiralt.debatex.model.timerModel.TimeSpanWrapper
import com.rohengiralt.debatex.model.timerModel.TimerModel
import com.rohengiralt.debatex.model.timerModel.wrap
import com.rohengiralt.debatex.random.nextString
import com.rohengiralt.debatex.random.randomSubset
import com.rohengiralt.debatex.settings.givenEmptySettingsAccess
import com.rohengiralt.debatex.util.use
import com.rohengiralt.debatex.viewModel.BasicTimerViewModel
import com.rohengiralt.debatex.viewModel.TimerViewModel
import com.rohengiralt.debatex.viewModel.event.BasicEventViewModel
import com.soywiz.klock.TimeSpan
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BasicEventViewModelTests : TestBase() {
    @Test
    fun givenNoPrimaryTimersInModel_whenInitialized_shouldFail() {
        repeat(100) {
            assertFailsWith<IllegalArgumentException> {
                BasicEventViewModel(
                    random.nextEventModel(primaryTimers = listOf())
                )
            }
        }
    }

    @Test
    fun givenNullSecondaryTimersInModel_whenInitialized_shouldHaveNullSecondaryTimers() {
        repeat(120) {
            assertNull(
                BasicEventViewModel(
                    random.nextEventModel(nullSecondaryTimers = true)
                ).secondaryTimers
            )
        }
    }

    @Test
    fun givenNoSecondaryTimersInModel_whenInitialized_shouldHaveNullSecondaryTimers() {
        repeat(120) {
            assertNull(
                BasicEventViewModel(
                    random.nextEventModel(secondaryTimers = listOf())
                ).secondaryTimers
            )
        }
    }

    @Test
    fun givenRandomEventModel_whenInitialized_shouldHaveOnePrimaryTimerPerModelPrimaryTimer() {
        repeat(120) { iteration ->
            TestKoin().use {
                givenEmptySettingsAccess()
                givenBasicTimerViewModel()

                val totalPrimaryTimers = iteration % 30 + 1
                val model = random.nextEventModel(totalPrimaryTimers = totalPrimaryTimers)
                val viewModel = BasicEventViewModel(model)

                assertEquals(totalPrimaryTimers, viewModel.primaryTimers.size)
            }
        }
    }

    @Test
    fun givenRandomEventModel_whenInitialized_shouldHaveOneSecondaryTimerPerModelSecondaryTimer() {
        repeat(120) { iteration ->
            TestKoin().use {
                givenEmptySettingsAccess()
                givenBasicTimerViewModel()

                val totalSecondaryTimers = iteration % 30 + 1
                val model =
                    random.nextEventModel(totalSecondaryTimers = totalSecondaryTimers, nullSecondaryTimers = false)
                val viewModel = BasicEventViewModel(model)

                assertNotNull(viewModel.secondaryTimers)
                @Suppress("ReplaceNotNullAssertionWithElvisReturn")
                assertEquals(totalSecondaryTimers, viewModel.secondaryTimers!!.size)
            }
        }
    }

    @Test
    fun givenRandomEventModel_whenInitialized_shouldHaveSameProgressAsTimerViewModel() { //TODO ALKDHFLKJSGDHCKd
        repeat(100) {
            TestKoin().use {
                givenRandomizedTimerViewModel()
                assertEquals(0.0, BasicEventViewModel(random.nextEventModel()).progress)
            }
        }
    }

    @Test
    fun givenRandomEventModel_whenTimerModelChanges_shouldHaveSameProgressAsTimerModel() {
    }

    private class StubTimerViewModel(
        override val name: ShortenableName,
        override val timeString: String,
        override val progress: Double,
        override var isRunning: Boolean,
        override val totalTime: TimeSpanWrapper,
        override val speakers: Set<Speaker>,
    ) : TimerViewModel() {
        override fun reset() {}
    }

    private fun givenBasicTimerViewModel() {
        loadKoinModules(
            module {
                factory<TimerViewModel> { (model: TimerModel<*>, configuration: TimerViewModel.DisplayConfiguration) ->
                    BasicTimerViewModel(model, configuration)
                }
            }
        )
    }

    private fun givenRandomizedTimerViewModel(
        name: ShortenableName = random.nextShortenableName(),
        timeString: String = random.nextString(5),
        progress: Double = random.nextDouble(0.0, 1.0),
        isRunning: Boolean = random.nextBoolean(),
        totalTime: TimeSpanWrapper = TimeSpan(random.nextDouble()).wrap(),
        speakers: Set<Speaker> =
            random.nextDebateFormat()
                .speakerType.all
                .randomSubset(random).toSet(),
    ) {
        loadKoinModules(
            module {
                factory<TimerViewModel> { (_: TimerModel<*>, _: TimerViewModel.DisplayConfiguration) ->
                    StubTimerViewModel(name, timeString, progress, isRunning, totalTime, speakers)
                }
            }
        )
    }
}
