package com.rohengiralt.debatex.dataStructure

expect sealed class Image()
//sealed class ResourceImage : Image() {
//
//}

expect sealed class SystemImage : Image {
    object Timer : SystemImage
    object Document : SystemImage
    object Settings : SystemImage
    object Hamburger : SystemImage
    object X : SystemImage
    object Reset : SystemImage
}

expect sealed class AssetImage : Image {
    sealed class Person : AssetImage {
//        sealed class One : Person
//        object Two : Person
//        object Many : Person

        sealed class One : Person {
            object Red : One
            object Green : One
        }

        sealed class Two : Person {
            object Red : Two
            object Green : Two
        }

        sealed class Many : Person {
            object Red : Many
            object Green : Many
        }
    }
}