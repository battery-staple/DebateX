package com.rohengiralt.debatex.settings

import com.rohengiralt.debatex.random.randomBooleanList
import com.rohengiralt.debatex.random.randomDoubleList
import com.rohengiralt.debatex.random.randomFloatList
import com.rohengiralt.debatex.random.randomIntList
import com.rohengiralt.debatex.random.randomLongList
import com.rohengiralt.debatex.random.randomStringList
import com.rohengiralt.debatex.settings.settingsStore.RusshwolfSettingsStoreAdapter
import com.russhwolf.settings.MockSettings
import kotlin.jvm.JvmName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

typealias RusshwolfStateType = MutableMap<String, Any>

class RusshwolfSettingsStoreAdapterTest {
    private val keysToTest = 500

    @Test
    fun givenEmptyRusshwolf_shouldReturnNull_whenGetOrNullsCalled() {
        for (getOrNull in getOrNullFunctions) {
            withEmptyRusshwolf {
                randomKeys(keysToTest).forEach { key ->
                    assertNull(getOrNull(key))
                }
            }
        }
    }

    @Test
    fun givenEmptyRusshwolf_shouldFail_whenGetCalled() {
        for (get in getFunctions) {
            withEmptyRusshwolf {
                randomKeys(keysToTest).forEach { key ->
                    assertFailsWith<IllegalArgumentException> { get(key) }
                }
            }
        }
    }

    @Test
    fun givenFilledRusshwolf_shouldRetrieveValues_whenGetOrNullCalled() {
        withEachType {
            withFilledRusshwolf { state ->
                state.entries.forEach { (key, value) ->
                    assertEquals(value, getTypeOrNull(key))
                }
            }
        }
    }

    @Test
    fun givenFilledRusshwolf_shouldRetrieveValues_whenGetCalled() {
        withEachType {
            withFilledRusshwolf { state ->
                state.entries.forEach { (key, value) ->
                    assertEquals(value, getType(key))
                }
            }
        }
    }

    @Test
    fun givenEmptyRusshwolf_shouldSetValues_whenSetCalled() {
        withEachType {
            withEmptyRusshwolf { state ->
                (randomKeys(keysToTest) zip randomList(keysToTest)).forEach { (key, value) ->
                    setType(key, value)
                    assertEquals(state[key], value)
                }
            }
        }
    }

    @Test
    fun givenFilledRusshwolf_shouldDeleteOrNullValues_whenRemoveCalled() {
        withEachType {
            withFilledRusshwolf { state ->
                state.keys.toSet().forEach { key ->
                    remove(key)
                    assertNull(state.getOrElse(key) { null })
                }
            }
        }
    }

    @Test
    fun givenFilledRusshwolf_shouldDeleteOrNullAllValues_whenRemoveAllCalled() {
        withFilledRusshwolf { state ->
            removeAll()
            state.keys.toSet().forEach { key ->
                assertNull(state.getOrElse(key) { null })
            }
        }
    }

    private data class StorableType<T : Any>(
        val getType: RusshwolfSettingsStoreAdapter.(String) -> T,
        val getTypeOrNull: RusshwolfSettingsStoreAdapter.(String) -> T?,
        val setType: RusshwolfSettingsStoreAdapter.(String, T) -> Unit,
        val randomList: (size: Int) -> List<T>,
    )

    private val types = listOf<StorableType<*>>(
        StorableType(
            RusshwolfSettingsStoreAdapter::getInt,
            RusshwolfSettingsStoreAdapter::getIntOrNull,
            RusshwolfSettingsStoreAdapter::setInt,
            ::randomIntList
        ),
        StorableType(
            RusshwolfSettingsStoreAdapter::getLong,
            RusshwolfSettingsStoreAdapter::getLongOrNull,
            RusshwolfSettingsStoreAdapter::setLong,
            ::randomLongList
        ),
        StorableType(
            RusshwolfSettingsStoreAdapter::getString,
            RusshwolfSettingsStoreAdapter::getStringOrNull,
            RusshwolfSettingsStoreAdapter::setString,
        ) { randomStringList(it, 0, 20) },
        StorableType(
            RusshwolfSettingsStoreAdapter::getFloat,
            RusshwolfSettingsStoreAdapter::getFloatOrNull,
            RusshwolfSettingsStoreAdapter::setFloat,
            ::randomFloatList
        ),
        StorableType(
            RusshwolfSettingsStoreAdapter::getDouble,
            RusshwolfSettingsStoreAdapter::getDoubleOrNull,
            RusshwolfSettingsStoreAdapter::setDouble,
            ::randomDoubleList
        ),
        StorableType(
            RusshwolfSettingsStoreAdapter::getBoolean,
            RusshwolfSettingsStoreAdapter::getBooleanOrNull,
            RusshwolfSettingsStoreAdapter::setBoolean,
            ::randomBooleanList
        )
    )

    private val getOrNullFunctions = types.map { it.getTypeOrNull }
    private val getFunctions = types.map { it.getType }
    private val randomListFunctions = types.map { it.randomList }

    private fun withEachType(block: StorableType<Any>.() -> Unit) =
        types.forEach { type -> with(type as StorableType<Any>, block) }

    @JvmName("russhwolfFromMutableMap")
    private fun russhwolf(contents: RusshwolfStateType) =
        RusshwolfSettingsStoreAdapter(MockSettings(contents))

    @JvmName("russhwolfFromMap")
    private fun russhwolf(contents: Map<String, Any>) =
        russhwolf(contents.toMutableMap())

    private fun <T> withRusshwolf(
        state: MutableMap<String, Any>,
        block: RusshwolfSettingsStoreAdapter.(state: RusshwolfStateType) -> T,
    ): T =
        with(russhwolf(state)) { block(state) }

    private fun <T : Any, R> StorableType<T>.withFilledRusshwolf(block: RusshwolfSettingsStoreAdapter.(state: RusshwolfStateType) -> R): R =
        withRusshwolf(
            (randomKeys(keysToTest) zip randomList(keysToTest)).toMap().toMutableMap(),
            block
        )

    private fun <T> withFilledRusshwolf(block: RusshwolfSettingsStoreAdapter.(state: RusshwolfStateType) -> T): T =
        withRusshwolf(
            (randomKeys(keysToTest * randomListFunctions.size) zip randomListFunctions.flatMap { it.invoke(keysToTest) })
                .toMap().toMutableMap(),
            block
        )

    private fun <T> withEmptyRusshwolf(block: RusshwolfSettingsStoreAdapter.(state: RusshwolfStateType) -> T): T =
        withRusshwolf(mutableMapOf(), block)
}