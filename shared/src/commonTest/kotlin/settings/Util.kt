package com.rohengiralt.debatex.settings

import com.rohengiralt.debatex.random.randomStringList
import com.rohengiralt.debatex.settings.settingsStore.SettingsStore
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

private val allAlphanumerics = (('A'..'Z') + ('a'..'z') + ('0'..'9')).toSet()

internal fun randomKeys(numberOfKeys: Int) =
    randomStringList(
        numberOfKeys,
        1,
        100,
        unique = true,
        allowedCharacters = allAlphanumerics
    )

class FakeSettingsStore(val state: MutableMap<String, Any> = mutableMapOf()) : SettingsStore {
    private inline fun <reified T : Any> getOrNull(key: String): T? =
        state.getOrElse(key) { null } as? T?

    override fun getIntOrNull(key: String): Int? = getOrNull(key)

    override fun getLongOrNull(key: String): Long? = getOrNull(key)

    override fun getStringOrNull(key: String): String? = getOrNull(key)

    override fun getFloatOrNull(key: String): Float? = getOrNull(key)

    override fun getDoubleOrNull(key: String): Double? = getOrNull(key)

    override fun getBooleanOrNull(key: String): Boolean? = getOrNull(key)

    private inline fun set(key: String, value: Any) {
        state[key] = value
    }

    override fun setInt(key: String, value: Int): Unit = set(key, value)

    override fun setLong(key: String, value: Long): Unit = set(key, value)

    override fun setString(key: String, value: String): Unit = set(key, value)

    override fun setFloat(key: String, value: Float): Unit = set(key, value)

    override fun setDouble(key: String, value: Double): Unit = set(key, value)

    override fun setBoolean(key: String, value: Boolean): Unit = set(key, value)

    override fun remove(key: String) {
        state.remove(key)
    }

    override fun removeAll() {
        state.clear()
    }
}

fun givenEmptySettingsAccess() {
    loadKoinModules(
        module {
            single {
                SettingsAccess(FakeSettingsStore())
            }
        }
    )
}