@file:Suppress("SpellCheckingInspection")

package com.rohengiralt.debatex.settings.settingsStore

import com.russhwolf.settings.Settings as RusshwolfSettings

//TODO: Inject russhwolf
@Suppress("Reformat")
class RusshwolfSettingsStoreAdapter(private val russhwolfSettings: RusshwolfSettings) : SettingsStore {
    override fun getIntOrNull(key: String): Int? = russhwolfSettings.getIntOrNull(key)
    override fun getLongOrNull(key: String): Long? = russhwolfSettings.getLongOrNull(key)
    override fun getStringOrNull(key: String): String? = russhwolfSettings.getStringOrNull(key)
    override fun getFloatOrNull(key: String): Float? = russhwolfSettings.getFloatOrNull(key)
    override fun getDoubleOrNull(key: String): Double? = russhwolfSettings.getDoubleOrNull(key)
    override fun getBooleanOrNull(key: String): Boolean? = russhwolfSettings.getBooleanOrNull(key)

    override fun setInt(key: String, value: Int) { russhwolfSettings.putInt(key, value) }
    override fun setLong(key: String, value: Long) { russhwolfSettings.putLong(key, value) }
    override fun setString(key: String, value: String) { russhwolfSettings.putString(key, value) }
    override fun setFloat(key: String, value: Float) { russhwolfSettings.putFloat(key, value) }
    override fun setDouble(key: String, value: Double) { russhwolfSettings.putDouble(key, value) }
    override fun setBoolean(key: String, value: Boolean) { russhwolfSettings.putBoolean(key, value) }

    override fun remove(key: String) { russhwolfSettings.remove(key) }
    override fun removeAll() { russhwolfSettings.clear() }
}