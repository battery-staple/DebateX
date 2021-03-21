package com.rohengiralt.debatex.settings.settingsStore

import kotlin.reflect.KClass

interface SettingsStore {
    fun getIntOrNull(key: String): Int?
    fun getLongOrNull(key: String): Long?
    fun getStringOrNull(key: String): String?
    fun getFloatOrNull(key: String): Float?
    fun getDoubleOrNull(key: String): Double?
    fun getBooleanOrNull(key: String): Boolean?

    fun setInt(key: String, value: Int)
    fun setLong(key: String, value: Long)
    fun setString(key: String, value: String)
    fun setFloat(key: String, value: Float)
    fun setDouble(key: String, value: Double)
    fun setBoolean(key: String, value: Boolean)

    fun remove(key: String)
    fun removeAll()

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

inline operator fun <reified T : Any> SettingsStore.get(key: String): T = get(T::class, key)

operator fun <T : Any> SettingsStore.get(type: KClass<T>, key: String): T = when (type) {
    Int::class -> ::getInt
    Long::class -> ::getLong
    String::class -> ::getString
    Float::class -> ::getFloat
    Double::class -> ::getDouble
    Boolean::class -> ::getBoolean
    else -> throw UnsupportedOperationException("Type ${type.simpleName} is not supported for storage.")
}.invoke(key) as T

fun <T : Any> SettingsStore.getOrNull(type: KClass<T>, key: String): T? = when (type) {
    Int::class -> ::getIntOrNull
    Long::class -> ::getLongOrNull
    String::class -> ::getStringOrNull
    Float::class -> ::getFloatOrNull
    Double::class -> ::getDoubleOrNull
    Boolean::class -> ::getBooleanOrNull
    else -> { _ -> null }
}.invoke(key) as T?

inline fun <reified T : Any> SettingsStore.getOrNull(key: String): T? =
    getOrNull(T::class, key)

operator fun <T : Any> SettingsStore.set(type: KClass<T>, key: String, value: T) {
    (when (type) {
        Int::class -> ::setInt
        Long::class -> ::setLong
        String::class -> ::setString
        Float::class -> ::setFloat
        Double::class -> ::setDouble
        Boolean::class -> ::setBoolean
        else -> throw UnsupportedOperationException("Type ${type.simpleName} is not supported for storage.")
    } as (String, T) -> Unit).invoke(key, value)
}

inline operator fun <reified T : Any> SettingsStore.set(key: String, value: T) {
    this[T::class, key] = value
}