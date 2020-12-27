package com.rohengiralt.debatex.propertyDelegates

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Sorted<in Reference, ListContents, T : Comparable<T>>(
    initialValue: List<ListContents>,
    private val sortedBy: (ListContents) -> T
) : ReadWriteProperty<Reference, List<ListContents>> {

    private var backingProperty: List<ListContents> = initialValue.sortedBy(sortedBy)

    override operator fun getValue(thisRef: Reference, property: KProperty<*>): List<ListContents> {
        //TODO: Remove when properly tested
        check(backingProperty == backingProperty.sortedBy(sortedBy))

        return backingProperty
    }

    override operator fun setValue(
        thisRef: Reference,
        property: KProperty<*>,
        value: List<ListContents>
    ) {
        backingProperty = value.sortedBy(sortedBy)
    }
}