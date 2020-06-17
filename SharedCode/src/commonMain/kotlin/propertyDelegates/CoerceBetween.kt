package com.rohengiralt.debatex.propertyDelegates

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Ensures a property is always between [lowerBound] and [upperBound];
 * if the property is attempted to be set outside that range either
 * an error will be thrown or it will be coerced into the range depending
 * on the [throwIfOutOfRange] flag.
 */
class CoerceBetween<in Reference, Parameter : Comparable<Parameter>>(
    initialValue: Parameter,
    private val lowerBound: Parameter,
    private val upperBound: Parameter,
    var throwIfOutOfRange: Boolean = false
) : ReadWriteProperty<Reference, Parameter> {

    init {
        require(lowerBound < upperBound) {
            "upperBound must be greater than lowerBound."
        }
        require(initialValue in lowerBound..upperBound) {
            "initialValue must be between the lower and upper bounds."
        }
    }

    private var backingProperty = initialValue

    override fun getValue(thisRef: Reference, property: KProperty<*>): Parameter {
        //TODO: Probably not a necessary check
        check(backingProperty in lowerBound..upperBound) {
            "Property ${property.name} of $thisRef mutated outside of setter to ${backingProperty}."
        }

        return backingProperty
    }

    override fun setValue(thisRef: Reference, property: KProperty<*>, value: Parameter) {
        if (throwIfOutOfRange) require(value in lowerBound..upperBound) {
            "Cannot set ${property.name} of $thisRef to value " +
                    "outside of range $lowerBound to $upperBound"
        }

        backingProperty = when {
            value > upperBound -> upperBound
            value < lowerBound -> lowerBound
            else -> value
        }
    }
}