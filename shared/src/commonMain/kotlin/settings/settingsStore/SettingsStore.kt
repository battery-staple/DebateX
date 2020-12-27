package com.rohengiralt.debatex.settings.settingsStore

import com.rohengiralt.debatex.defaultLogger
import com.rohengiralt.debatex.settings.Setting
import com.rohengiralt.debatex.settings.SettingType
import com.rohengiralt.debatex.settings.getSerializable
import com.rohengiralt.debatex.settings.putSerializable
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.serialization.KSerializer
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface SettingsStore {
    fun getIntOrNull(key: String): Int?
    fun getLongOrNull(key: String): Long?
    fun getStringOrNull(key: String): String?
    fun getFloatOrNull(key: String): Float?
    fun getDoubleOrNull(key: String): Double?
    fun getBooleanOrNull(key: String): Boolean?

    fun getInt(key: String): Int =
        getIntOrNull(key) ?: throw IllegalArgumentException("No integer found with key $key")

    fun getLong(key: String): Long =
        getLongOrNull(key) ?: throw IllegalArgumentException("No long integer found with key $key")

    fun getString(key: String): String =
        getStringOrNull(key) ?: throw IllegalArgumentException("No string found with key $key")

    fun getFloat(key: String): Float =
        getFloatOrNull(key) ?: throw IllegalArgumentException("No float found with key $key")

    fun getDouble(key: String): Double =
        getDoubleOrNull(key) ?: throw IllegalArgumentException("No double-precision float found with key $key")

    fun getBoolean(key: String): Boolean =
        getBooleanOrNull(key) ?: throw IllegalArgumentException("No boolean found with key $key")
}

inline operator fun <reified T> SettingsStore.get(value: String): T {
    @Suppress("UNCHECKED_CAST")
    val getT: (String) -> T = when (val tClass = T::class) {
        Int::class -> ::getInt
        Long::class -> ::getLong
        String::class -> ::getString
        Float::class -> ::getFloat
        Double::class -> ::getDouble
        Boolean::class -> ::getBoolean
        else -> throw UnsupportedOperationException("Type ${tClass.simpleName} is not supported for storage.")
    } as (String) -> T

    return getT(value)
}

inline fun <reified T> SettingsStore.getOrNull(value: String): T? {
    @Suppress("UNCHECKED_CAST")
    val getT: (String) -> T? = when (T::class) {
        Int::class -> ::getIntOrNull
        Long::class -> ::getLongOrNull
        String::class -> ::getStringOrNull
        Float::class -> ::getFloatOrNull
        Double::class -> ::getDoubleOrNull
        Boolean::class -> ::getBooleanOrNull
        else -> return null
    } as (String) -> T?

    return getT(value)
}