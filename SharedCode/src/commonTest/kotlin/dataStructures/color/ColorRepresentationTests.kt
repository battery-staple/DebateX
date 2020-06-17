package com.rohengiralt.debatex.dataStructures.color

import com.rohengiralt.debatex.approximatelyEquals
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlin.jvm.JvmName
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ColorRepresentationTests {

    private val maximumConversionError = 0.2

    /*
    private val colorsRGB = listOf(
        Triple(0.0, 0.0, 0.0),
        Triple(1.0, 1.0, 1.0),
        Triple(0.5, 0.5, 0.5),
        Triple(1.0, 0.0, 0.0),
        Triple(0.0, 1.0, 0.0),
        Triple(0.0, 0.0, 1.0),
        Triple(1.0, 1.0, 0.0),
        Triple(0.0, 1.0, 1.0),
        Triple(1.0, 0.0, 1.0)

//            Triple(0.3132, 0.48631, 0.811662),
//            Triple(0.6256, 0.5619, 1.0),
//            Triple(1.0, 0.9999, 0.1638)
    )

    private val colorsHSV = listOf(
        // Correctly converted HSV values from Google color picker.
        // https://www.google.com/search?q=color+picker
        Triple(0.0, 0.0, 0.0),
        Triple(0.0, 0.0, 1.0),
        Triple(0.0, 0.0, 0.5),
        Triple(0.0, 1.0, 1.0),
        Triple(0.3333333333, 1.0, 1.0),
        Triple(0.6666666666, 1.0, 1.0),
        Triple(0.1666666666, 1.0, 1.0),
        Triple(0.5, 1.0, 1.0),
        Triple(0.8333333333, 1.0, 1.0)

        //TODO: Add other colors
    )

    @BeforeTest
    fun `Make sure check against lists are the right lengths`(): Unit =
        check(colorsHSV.size == colorsRGB.size) {
            "Test set up improperly; both lists must be the same length."
        }


    @Test
    fun `RGB to HSV conversions work`(): Unit = assertTrue {
        for (index in colorsRGB.indices) {
            val colorHSV = colorsHSV[index]
            val colorRGB = colorsRGB[index].run {
                SingleColor(
                    first,
                    second,
                    third
                )
            }

            if (!approximatelyEquals(
                    colorRGB.hue,
                    colorHSV.first,
                    error = maximumConversionError
                ) ||
                !approximatelyEquals(
                    colorRGB.saturation,
                    colorHSV.second,
                    error = maximumConversionError
                ) ||
                !approximatelyEquals(
                    colorRGB.value,
                    colorHSV.third,
                    error = maximumConversionError
                )
            ) {
                println(
                    "Color $colorRGB (the ${index}th)'s hue, saturation, and value " +
                            "(${colorRGB.hue}, ${colorRGB.saturation}, ${colorRGB.value}) " +
                            "do not match the precalculated hue, saturation, and value " +
                            "(${colorHSV.first}, ${colorHSV.second}, ${colorHSV.third})."
                )

                return@assertTrue false
            }
        }
        return@assertTrue true
    }

    @Test
    fun `HSV to RGB conversions work`(): Unit = assertTrue {
        for (index in colorsRGB.indices) {
            val colorRGB = colorsRGB[index]
            val colorHSV = colorsHSV[index].run {
                singleColorFromHSV(
                    first,
                    second,
                    third
                )
//                object {
//                    val red = HSVtoRGB(first, second, third).first
//                    val green = HSVtoRGB(first, second, third).second
//                    val blue = HSVtoRGB(first, second, third).third
//                }
            }

            if (!approximatelyEquals(
                    colorHSV.red,
                    colorRGB.first,
                    error = maximumConversionError
                ) ||
                !approximatelyEquals(
                    colorHSV.green,
                    colorRGB.second,
                    error = maximumConversionError
                ) ||
                !approximatelyEquals(colorHSV.blue, colorRGB.third, error = maximumConversionError)
            ) {
                println(
                    "Color $colorHSV (the ${index + 1}th)'s red, green and blue values " +
                            "(${colorHSV.red}, ${colorHSV.green}, ${colorHSV.blue}) " +
                            "do not match the precalculated red, green, and blue values " +
                            "(${colorRGB.first}, ${colorRGB.second}, ${colorRGB.third})."
                )

                return@assertTrue false
            }
        }
        return@assertTrue true
    }

    @Test
    fun `Conversions from RGB to HSV and back work`() {
        val acceptableError = 0.005

        val rand = Random(1)

        for (i in 1..1000) {
            val red: Double = rand.nextDouble(0.0, 1.0)
            val green: Double = rand.nextDouble(0.0, 1.0)
            val blue: Double = rand.nextDouble(0.0, 1.0)

            val currentHue: Double
            var currentSaturation: Double
            var currentValue: Double

            SingleColor(
                red = red,
                green = green,
                blue = blue
            ).apply { // Won't test 1.0
                currentHue = hue
                currentSaturation = saturation
                currentValue = value
            }

            singleColorFromHSV(
                hue = currentHue,
                saturation = currentSaturation,
                value = currentValue
            ).apply {
                assertTrue(
                    approximatelyEquals(red, this.red, error = acceptableError) &&
                            approximatelyEquals(green, this.green, error = acceptableError) &&
                            approximatelyEquals(blue, this.blue, error = acceptableError),

                    message = "Error greater than $acceptableError encountered in trial #$i, " +
                            "the conversion of " +
                            "red ($red to ${this.red}) " +
                            "green ($green to ${this.green}) " +
                            "and/or blue ($blue to ${this.blue}), " +
                            "with the intermediate " +
                            "hue of ${currentHue}, " +
                            "saturation of ${currentSaturation}, " +
                            "and value of ${currentValue}."
                )
            }
        }
    }
     */

    private val colors = mutableListOf<ColorRepresentation>()

    @BeforeTest
    fun `Initialize list of colors`() {
        val testsPerColor = 10

        val rand = Random(1)
        for (i in 1..testsPerColor) colors.add(
            ColorRepresentation.RGB(
                alpha = rand.nextDouble(0.0, 1.0),
                red = rand.nextDouble(0.0, 1.0),
                green = rand.nextDouble(0.0, 1.0),
                blue = rand.nextDouble(0.0, 1.0)
            )
        )
        for (i in 1..testsPerColor) colors.add(
            ColorRepresentation.HSV(
                alpha = rand.nextDouble(0.0, 1.0),
                hue = rand.nextDouble(0.0, 1.0),
                saturation = rand.nextDouble(0.0, 1.0),
                value = rand.nextDouble(0.0, 1.0)
            )
        )
        for (i in 1..testsPerColor) colors.add(
            ColorRepresentation.HSL(
                alpha = rand.nextDouble(0.0, 1.0),
                hue = rand.nextDouble(0.0, 1.0),
                saturation = rand.nextDouble(0.0, 1.0),
                lightness = rand.nextDouble(0.0, 1.0)
            )
        )
    }

    private fun jsonToMap(json: String): Map<String, String> {
        val map = mutableMapOf<String, String>()

        var netOpenBrackets = 0
        var currentToken = ""
        var currentKey = ""
        var currentValue = ""
        loop@ for (char in json.trimStart('{').trimEnd('}')) {
            when (char) {
                '{' -> {
                    ++netOpenBrackets
                    currentToken += char
                    continue@loop
                }
                '}' -> {
                    --netOpenBrackets
                    currentToken += char
                    continue@loop
                }
            }
            if (netOpenBrackets <= 0) when (char) {
                '"', ' ', '\n' -> {
                }
                ':' -> {
                    currentKey = currentToken
                    currentToken = ""
                }
                ',' -> {
                    map[currentKey] = currentToken
                    currentKey = ""
                    currentToken = ""
                }
                else -> currentToken += char
            }
            else {
                currentToken += char
            }
        }
        map[currentKey] = currentToken

        return map
    }

    @JvmName("jsonToMapKt")
    @Suppress("NOTHING_TO_INLINE")
    private inline fun String.jsonToMap(): Map<String, String> = jsonToMap(this)

    private fun String.getColorFraction(colorName: String): Map<String, String> =
        this.jsonToMap()[colorName]?.jsonToMap()?.get("fraction")?.jsonToMap() ?: mapOf("" to "")

    @ExperimentalCoroutinesApi
    @Test
    fun test() {
        val client = HttpClient {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }

        for (color in colors) {
            val colorIdentifier = when (color) {
                is ColorRepresentation.RGB -> "rgb=${color.red},${color.green},${color.blue}"
                is ColorRepresentation.HSV -> "hsv=${color.hue},${color.saturation},${color.value}"
                is ColorRepresentation.HSL -> "hsl=${color.hue},${color.saturation},${color.lightness}"
            }

            val response = GlobalScope.async {
                client.get<String>("http://www.thecolorapi.com/id?$colorIdentifier") {}
            }.let {
                while (!it.isCompleted) {
                }
                return@let it.getCompleted()
            }

            val rgbTest = color.coerce<ColorRepresentation.RGB>()
            val rgbTrue = ColorRepresentation.RGB(
                alpha = color.alpha,
                red = response.getColorFraction("rgb")["r"]?.toDouble() ?: throw Error(),
                green = response.getColorFraction("rgb")["g"]?.toDouble() ?: throw Error(),
                blue = response.getColorFraction("rgb")["b"]?.toDouble() ?: throw Error()
            )

            println(color)
            println(colorIdentifier)
            println(rgbTest)
            println(rgbTrue)

            assertTrue {
                approximatelyEquals(rgbTest.alpha, rgbTrue.alpha, error = maximumConversionError) &&
                        approximatelyEquals(
                            rgbTest.red,
                            rgbTrue.red,
                            error = maximumConversionError
                        ) &&
                        approximatelyEquals(
                            rgbTest.green,
                            rgbTrue.green,
                            error = maximumConversionError
                        ) &&
                        approximatelyEquals(
                            rgbTest.blue,
                            rgbTrue.blue,
                            error = maximumConversionError
                        )
            }

        }
    }
}