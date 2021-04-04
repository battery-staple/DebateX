package com.rohengiralt.debatex

import com.rohengiralt.debatex.di.KoinLoggerAdapter
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.dsl.koinApplication
import kotlin.random.Random
import kotlin.test.BeforeTest

abstract class TestBase : KoinComponent {
    val koin: KoinApplication = koinApplication {
        logger(KoinLoggerAdapter(Logger("Test Internal Koin")))
    }

    override fun getKoin(): Koin = koin.koin

    protected var random: Random = Random(0)

    @BeforeTest
    fun newTestRandom() {
        random = Random(0)
    }
}