package com.rohengiralt.debatex.settings

import com.rohengiralt.debatex.random.nextAny
import com.rohengiralt.debatex.random.randomAnyList
import com.rohengiralt.debatex.settings.settingsStore.SettingsStore
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SettingsTest {
    @Test
    fun givenUninitializedType_shouldSetInType_whenSettingInstantiated() {
        withEmptySettingsAccess { store ->
            randomKeys(100).forEach { key ->
                randomAnyList(100).forEach { value ->
                    val type = StubType()
                    Setting(key, type, value)

                    assertEquals(value, type.state[store]?.get(key))
                }
            }
        }
    }

    @Test
    fun givenInitializedType_shouldNotCallTypeSetIn_whenSettingInstantiated() {
        withEmptySettingsAccess { store ->
            val keys = randomKeys(100)
            val values = randomAnyList(100)

            keys.forEach { key ->
                values.forEach { value ->
                    val type = StubType(
                        mutableMapOf(
                            store to mutableMapOf(
                                key to generateSequence(Random(0)::nextAny).first { it != value }
                            )
                        )
                    )
                    Setting(key, type, value)

                    assertNotEquals(value, type.state[store]?.get(key))
                }
            }
        }
    }

    @Test
    fun givenInitializedType_shouldCallTypeSetIn_whenDistinctSetCalled() {
        withEmptySettingsAccess { store ->
            val keys = randomKeys(100)
            val values = randomAnyList(100)
            val initialState: MutableMap<SettingsStore, MutableMap<String, Any>> =
                mutableMapOf(store to mutableMapOf(*(keys zip values).toTypedArray()))

            keys.forEach { key ->
                values.forEach { value ->
                    val type = StubType(initialState)
                    Setting(key, type, generateSequence(Random(0)::nextAny).first { it != value })
                        .set(value)

                    assertEquals(value, type.state[store]?.get(key))
                }
            }
        }
    }

    private fun <T> withEmptySettingsAccess(block: SettingsAccess.(store: FakeSettingsStore) -> T): T {
        val fakeSettingsStore = FakeSettingsStore()
        return with(SettingsAccess(fakeSettingsStore)) {
            block(fakeSettingsStore)
        }
    }

}

private class StubType(initialState: MutableMap<SettingsStore, MutableMap<String, Any>> = mutableMapOf()) :
    SettingsAccess.Type<Any> {
    override fun getFrom(settingsStore: SettingsStore, key: String): Any =
        state[settingsStore]?.get(key)
            ?: throw IllegalArgumentException("Nothing found with key $key in store $settingsStore")

    override fun setIn(settingsStore: SettingsStore, key: String, value: Any) {
        state.getOrPut(settingsStore, ::mutableMapOf)[key] = value
    }

    override fun isInitializedIn(settingsStore: SettingsStore, key: String): Boolean =
        state[settingsStore]?.get(key) != null

    val state = initialState
}