package com.rohengiralt.debatex

import com.rohengiralt.debatex.dataStructure.ShortenableName
import com.rohengiralt.debatex.dataStructure.competitionTypes.Speaker
import com.rohengiralt.debatex.di.KoinLoggerAdapter
import com.rohengiralt.debatex.model.event.AgeGroup
import com.rohengiralt.debatex.model.event.DebateFormat
import com.rohengiralt.debatex.model.event.DebateFormat.*
import com.rohengiralt.debatex.model.event.EventModel
import com.rohengiralt.debatex.model.event.EventTags
import com.rohengiralt.debatex.model.event.Location
import com.rohengiralt.debatex.model.event.Organization
import com.rohengiralt.debatex.model.event.Region
import com.rohengiralt.debatex.model.event.SecondaryTimerChangeStrategy
import com.rohengiralt.debatex.model.sectionModel.EventsSectionModel
import com.rohengiralt.debatex.model.timerModel.TimerModel
import com.rohengiralt.debatex.model.timerModel.wrap
import com.rohengiralt.debatex.random.nextString
import com.rohengiralt.debatex.random.randomList
import com.rohengiralt.debatex.settings.FakeSettingsStore
import com.rohengiralt.debatex.settings.SettingsAccess
import com.rohengiralt.debatex.util.Closeable
import com.rohengiralt.debatex.util.use
import com.rohengiralt.debatex.viewModel.section.EventsSectionViewModel
import com.soywiz.klock.minutes
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

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
    var random: Random = Random(0)

    @BeforeTest
    fun newTestRandom() {
        random = Random(0)
    }

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

                assertEquals(numberOfSections, EventsSectionViewModel().events.size, "iteration=$iteration")
            }
        }
    }

    @Test
    fun givenRandomEvents_whenInitialized_noCardsShouldBeShowing(): Unit = TestKoin().use {
        givenRandomEvents(random.nextInt(0..20))

        EventsSectionViewModel().cards.forEach { card ->
            assertEquals(false, card.showingInfo)
        }
    }

    @Test
    fun givenRandomEvents_whenShowInfoCalled_onlyCardShouldBeShowing(): Unit = TestKoin().use {
        givenRandomEvents(random.nextInt(0..20))

        EventsSectionViewModel().cards.forEach { card ->
            assertEquals(false, card.showingInfo)
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

    private fun givenEmptySettingsAccess() {
        loadKoinModules(
            module {
                single {
                    SettingsAccess(FakeSettingsStore())
                }
            }
        )
    }

    private fun randomEventModelList(
        size: Int,
        unique: Boolean = false,
        random: Random,
    ): List<EventModel<*>> =
        randomList(size, unique, random) {
            val format = debateFormats.random(random)
            EventModel(
                format,
                random.nextEventTags(),
                format.speakerType,
                randomTimerModelList(random.nextInt(1..30), format.speakerType, random = random),
                if (random.nextInt(1..10) <= 1)
                    randomTimerModelList(random.nextInt(1..30), format.speakerType, random = random)
                else null,
                secondaryTimerChangeStrategies.random(random)
            )
        }

    private fun <T : Speaker> randomTimerModelList(
        size: Int,
        speakerType: Speaker.Type<T>,
        unique: Boolean = false,
        random: Random,
    ): List<TimerModel<T>> =
        randomList(size, unique, random) {
            TimerModel(
                ShortenableName(
                    random.nextString(random.nextInt(0..50)),
                    random.nextString(random.nextInt(0..50))
                ),
                random.nextDouble(0.0, 99.5).minutes.wrap(),
                speakerType.all
                    .shuffled()
                    .take(random.nextInt(speakerType.all.indices) + 1)
                    .toSet()
            )
        }

    private fun Random.nextEventTags(): EventTags =
        EventTags(
            country = (Location.values().toSet() + null).random(this),
            ageGroup = (AgeGroup.values().toSet() + null).random(this),
            organization = (Organization.values().toSet() + null).random(this),
            region = (Region.values().toSet() + null).random(this),
        )


    private val debateFormats: Set<DebateFormat<*>> =
        setOf( //TODO: ensure contains all formats
            Debug,
            LincolnDouglas,
            PublicForum,
            BigQuestions,
            Policy
        )

    private val secondaryTimerChangeStrategies: Set<SecondaryTimerChangeStrategy> =
        setOf( //TODO: ensure contains all strategies
            SecondaryTimerChangeStrategy.All,
            SecondaryTimerChangeStrategy.Any,
            SecondaryTimerChangeStrategy.Never
        )

}