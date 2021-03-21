package com.rohengiralt.debatex.util

import kotlin.text.isDigit as ktIsDigit

actual fun Char.isDigit(): Boolean = ktIsDigit()