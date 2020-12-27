package com.rohengiralt.debatex

import org.junit.Test

class AndroidExampleTest {
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