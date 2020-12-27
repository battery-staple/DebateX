package com.rohengiralt.debatex.settings

import kotlin.reflect.KProperty1

internal actual val <T : Any> T.allProperties: List<KProperty1<T, *>>
    get() =
        this::class
            .members
            .filterIsInstance<KProperty1<T, *>>()