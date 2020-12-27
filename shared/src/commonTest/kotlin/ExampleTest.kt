package com.rohengiralt.debatex

import kotlin.test.Test

class ExampleTest {
    val testing = Example()

    @Test
    fun printsHiPrintsHi() {
        testing.printHi()
        loggerForClass<ExampleTest>().info("Worked!")
    }

//    @Test
//    fun errorFails() {
//        error("Failed!")
//    }

}