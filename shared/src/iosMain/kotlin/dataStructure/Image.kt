package com.rohengiralt.debatex.dataStructure

//import platform.UIKit.UIImage

actual sealed class Image {
//    abstract fun asUIImage(): UIImage
}

actual sealed class SystemImage(val sfSymbolName: String) : Image() {
    actual object Timer : SystemImage("timer")
    actual object Document : SystemImage("doc")
    actual object Settings : SystemImage("gear")
    actual object Hamburger : SystemImage("line.horizontal.fill")
    actual object X : SystemImage("xmark")
    actual object Reset : SystemImage("arrow.clockwise")

//    override fun asUIImage(): UIImage {
//        UIImage(systemName = sfSymbolName) TODO: Use a Swift framework to add this
//    }
}

actual sealed class AssetImage(val imageName: String) : Image() {
    actual sealed class Person(imageName: String) : AssetImage("Person$imageName") {
        actual sealed class One(imageName: String) : Person("One$imageName") {
            actual object Red : One("Red")
            actual object Green : One("Green")
        }

        actual sealed class Two(imageName: String) : Person("Two$imageName") {
            actual object Red : Two("Red")
            actual object Green : Two("Green")
        }

        actual sealed class Many(imageName: String) : Person("Many$imageName") {
            actual object Red : Many("Red")
            actual object Green : Many("Green")
        }
    }
}