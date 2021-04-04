package com.rohengiralt.debatex.events

import com.rohengiralt.debatex.Logger
import com.rohengiralt.debatex.TestBase
import com.rohengiralt.debatex.di.KoinLoggerAdapter
import com.rohengiralt.debatex.model.sectionModel.EventsSectionModel
import com.rohengiralt.debatex.settings.givenEmptySettingsAccess
import com.rohengiralt.debatex.util.Closeable
import com.rohengiralt.debatex.util.use
import com.rohengiralt.debatex.viewModel.section.EventsSectionViewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TestKoin : Closeable {
    init {
        startKoin {
            logger(KoinLoggerAdapter(Logger("Test Koin")))
        }
    }

    override fun close() {
        stopKoin()
    }
}

class EventsSectionViewModelTest : TestBase() {
    @Test
    fun givenNoEvents_whenInitialized_shouldHaveEmptyEvents(): Unit = TestKoin().use {
        givenNoEvents()
        assertEquals(0, EventsSectionViewModel().events.size)
    }

    @Test
    fun givenNoEvents_whenInitialized_shouldHaveNoCurrentEvent(): Unit = TestKoin().use {
        givenNoEvents()
        assertNull(EventsSectionViewModel().currentEvent)
    }

    @Test
    fun givenNoEvents_whenInitialized_shouldNotBeShowingEvent(): Unit = TestKoin().use {
        givenNoEvents()
        assertFalse(EventsSectionViewModel().showingEvent)
    }

    @Test
    fun givenRandomEvents_whenInitialized_shouldHaveOneEventViewModelPerEvent() {
        repeat(100) { iteration ->
            TestKoin().use {
                val numberOfSections = iteration % 20
                givenRandomEvents(numberOfSections)
                givenEmptySettingsAccess()

                assertEquals(numberOfSections, EventsSectionViewModel().events.size)
            }
        }
    }

    @Test
    fun givenRandomEvents_whenInitialized_shouldBeShowingNoCards() {
        repeat(100) { iteration ->
            TestKoin().use {
                givenRandomEvents(iteration % 20)

                EventsSectionViewModel().cards.forEach { card ->
                    assertEquals(false, card.showingInfo)
                }
            }
        }
    }

    @Test
    fun givenRandomEvents_whenOneCardShowingInfoSetToTrue_shouldBeShowingOnlyThatCard() {
        repeat(100) { iteration ->
            TestKoin().use {
                givenRandomEvents(iteration % 20)

                val cards = EventsSectionViewModel().cards

                cards.shuffled(random).forEach { card ->
                    card.showingInfo = true

                    assertEquals(card, cards.single { it.showingInfo })
                }
            }
        }
    }

    @Test
    fun givenRandomEvents_whenDistinctOpenCalled_shouldBeShowingEvent() {
        repeat(100) { iteration ->
            TestKoin().use {
                givenRandomEvents(iteration % 20)

                val viewModel = EventsSectionViewModel()

                viewModel.cards.shuffled(random).forEach { card ->
                    viewModel.showingEvent = false // what if this fails?
                    card.open()

                    assertEquals(true, viewModel.showingEvent)
                }
            }
        }
    }

    @Test
    fun givenRandomEvents_whenSameOpenCalled_shouldBeShowingEvent() {
        repeat(100) { iteration ->
            TestKoin().use {
                givenRandomEvents(iteration % 20)

                val viewModel = EventsSectionViewModel()

                viewModel.cards.shuffled(random).forEach { card ->
                    viewModel.showingEvent = false // what if this fails?

                    card.open()

                    assertTrue(viewModel.showingEvent)

                    card.open()

                    assertTrue(viewModel.showingEvent)
                }
            }
        }
    }

    @Test
    fun givenRandomEvents_whenDistinctOpenCalled_shouldHaveDifferentEvent() {
        repeat(100) { iteration ->
            TestKoin().use {
                givenRandomEvents(iteration % 20)

                val viewModel = EventsSectionViewModel()

                viewModel.cards.shuffled(random).forEach { card ->
                    val previousEvent = viewModel.currentEvent

                    card.open()

                    assertNotNull(viewModel.currentEvent)
                    assertNotEquals(previousEvent, viewModel.currentEvent)
                }
            }
        }
    }

    @Test
    fun givenRandomEvents_whenSameOpenCalled_shouldHaveSameEvent() {
        repeat(100) { iteration ->
            TestKoin().use {
                givenRandomEvents(iteration % 20)

                val viewModel = EventsSectionViewModel()

                viewModel.cards.shuffled(random).forEach { card ->
                    card.open()
                    val previousEvent = viewModel.currentEvent
                    card.open()

                    assertEquals(previousEvent, viewModel.currentEvent)
                }
            }
        }
    }

    private fun givenNoEvents() {
        loadKoinModules(module { single { EventsSectionModel(listOf()) } })
    }

    private fun givenRandomEvents(sections: Int) {
        loadKoinModules(
            module {
                single {
                    EventsSectionModel(
                        randomEventModelList(sections, random = random)
                    )
                }
            }
        )
    }
}