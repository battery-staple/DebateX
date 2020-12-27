package com.rohengiralt.debatex.dataStructure

actual sealed class Image {
//    abstract fun as___idkimage()
}

actual sealed class SystemImage(val materialName: String) : Image() {
    actual object Timer : SystemImage("timer")
    actual object Document : SystemImage("article")
    actual object Settings : SystemImage("settings")
    actual object Hamburger : SystemImage("menu")
    actual object X : SystemImage("close")
    actual object Reset : SystemImage("refresh")
}

actual sealed class AssetImage : Image() {
    actual sealed class Person : AssetImage() {
        actual sealed class One : Person() {
            actual object Red : One()
            actual object Green : One()
        }

        actual sealed class Two : Person() {
            actual object Red : Two()
            actual object Green : Two()
        }

        actual sealed class Many : Person() {
            actual object Red : Many()
            actual object Green : Many()
        }
    }
}