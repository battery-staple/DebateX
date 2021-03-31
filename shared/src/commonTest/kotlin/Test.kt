package com.rohengiralt.debatex

import com.rohengiralt.debatex.di.KoinLoggerAdapter
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.dsl.koinApplication

abstract class TestBase : KoinComponent {
    val koin: KoinApplication = koinApplication {
        logger(KoinLoggerAdapter(Logger("Test Internal Koin")))
    }

    override fun getKoin(): Koin = koin.koin
}