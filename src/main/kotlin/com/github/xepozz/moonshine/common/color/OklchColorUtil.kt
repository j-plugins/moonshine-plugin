package com.github.xepozz.moonshine.common.color

import com.intellij.ui.JBColor
import java.awt.Color
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Utility class for parsing and converting OKLCH colors
 * OKLCH format: oklch(lightness chroma hue)
 * - lightness: 0-1 (0% to 100%)
 * - chroma: 0+ (typically 0-0.4)
 * - hue: 0-360 degrees
 */
object OklchColorUtil {

    /**
     * Regex pattern to match OKLCH color strings
     * Supports various formats:
     * - oklch(0.5 0.2 180)
     * - oklch(50% 0.2 180deg)
     * - oklch(0.5, 0.2, 180)
     */
    private val withFunctionPattern = Regex(
        """
        ^oklch\s*\(\s*(\d*\.?\d+)%?,?\s+(\d*\.?\d+),?\s+(\d*\.?\d+)(?:deg)?\)$
        """.trimIndent(),
        setOf(RegexOption.IGNORE_CASE)
    )
    private val withoutFunctionPattern = Regex(
        """
        ^\s*(\d*\.?\d+)%?,?\s+(\d*\.?\d+),?\s+(\d*\.?\d+)(?:deg)?$
        """.trimIndent(),
        setOf(RegexOption.IGNORE_CASE)
    )
    private val hexColorPattern = Regex(
        """
        ^#?([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$
        """.trimIndent(),
        setOf(RegexOption.IGNORE_CASE)
    )

    fun parseHexColor(hexColor: String): Color? {
        return try {
            Color.decode(hexColor)
        } catch (e: NumberFormatException) {
            null
        }
    }

    fun parseOklchColor(colorString: String): OklchColor? {
        val colorString = colorString.trim()
        val matchResult =
            withFunctionPattern.find(colorString) ?: withoutFunctionPattern.find(colorString) ?: return null

        val (lightnessStr, chromaStr, hueStr) = matchResult.destructured

        try {
            var lightness = lightnessStr.toDouble()
            val chroma = chromaStr.toDouble()
            var hue = hueStr.toDouble()

            // Convert percentage to decimal if needed
            if (colorString.contains("%")) {
                lightness /= 100.0
            }

            // Ensure values are within valid ranges
            lightness = lightness.coerceIn(0.0, 1.0)
            hue %= 360.0
            if (hue < 0) hue += 360.0

            return OklchColor(lightness, chroma, hue)
        } catch (e: NumberFormatException) {
            return null
        }
    }

    fun isOklchColor(text: String): Boolean {
        return withFunctionPattern.containsMatchIn(text) || withoutFunctionPattern.containsMatchIn(text)
    }

    fun isHexColor(text: String): Boolean {
        return hexColorPattern.containsMatchIn(text)
    }

    /**
     * Find all OKLCH colors in text
     */
    fun findOklchColors(text: String): List<Pair<IntRange, OklchColor>> {
        return withFunctionPattern.findAll(text)
            .ifEmpty { withoutFunctionPattern.findAll(text) }
            .mapNotNull { matchResult ->
                val color = parseOklchColor(matchResult.value)
                color?.let { matchResult.range to it }
            }.toList()
    }

    /**
     * Convert OKLCH to RGB
     * Based on the OKLCH to RGB conversion algorithm
     */
    fun oklchToRgb(lightness: Double, chroma: Double, hue: Double): Color {
        // Convert to OKLab first
        val hueRad = Math.toRadians(hue)
        val alpha = chroma * cos(hueRad)
        val beta = chroma * sin(hueRad)

        // Convert OKLab to linear RGB
        val l_ = lightness + 0.3963377774 * alpha + 0.2158037573 * beta
        val m_ = lightness - 0.1055613458 * alpha - 0.0638541728 * beta
        val s_ = lightness - 0.0894841775 * alpha - 1.2914855480 * beta

        val l = l_ * l_ * l_
        val m = m_ * m_ * m_
        val s = s_ * s_ * s_

        // Convert to linear RGB
        val lr = +4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s
        val lg = -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s
        val lb = -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s

        // Apply gamma correction (linear to sRGB)
        val r = gammaCorrection(lr)
        val g = gammaCorrection(lg)
        val b = gammaCorrection(lb)

        // Convert to 0-255 range and clamp
        val red = (r * 255).toInt().coerceIn(0, 255)
        val green = (g * 255).toInt().coerceIn(0, 255)
        val blue = (b * 255).toInt().coerceIn(0, 255)

        val color = Color(red, green, blue)

        return JBColor(color, color)
    }

    /**
     * Apply gamma correction for sRGB
     */
    private fun gammaCorrection(value: Double): Double {
        return if (abs(value) <= 0.0031308) {
            value * 12.92
        } else {
            val sign = if (value >= 0) 1.0 else -1.0
            sign * (1.055 * abs(value).pow(1.0 / 2.4) - 0.055)
        }
    }

    /**
     * Convert RGB to OKLCH for color picker updates
     */
    fun rgbToOklch(color: Color): OklchColor {
        // Convert sRGB to linear RGB
        val r = inverseGammaCorrection(color.red / 255.0)
        val g = inverseGammaCorrection(color.green / 255.0)
        val b = inverseGammaCorrection(color.blue / 255.0)

        // Convert linear RGB to OKLab
        val l = 0.4122214708 * r + 0.5363325363 * g + 0.0514459929 * b
        val m = 0.2119034982 * r + 0.6806995451 * g + 0.1073969566 * b
        val s = 0.0883024619 * r + 0.2817188376 * g + 0.6299787005 * b

        val l_ = Math.cbrt(l)
        val m_ = Math.cbrt(m)
        val s_ = Math.cbrt(s)

        val lightness = 0.2104542553 * l_ + 0.7936177850 * m_ - 0.0040720468 * s_
        val a = 1.9779984951 * l_ - 2.4285922050 * m_ + 0.4505937099 * s_
        val bVal = 0.0259040371 * l_ + 0.7827717662 * m_ - 0.8086757660 * s_

        // Convert to OKLCH
        val chroma = sqrt(a * a + bVal * bVal)
        val hue = Math.toDegrees(atan2(bVal, a))
        val normalizedHue = if (hue < 0) hue + 360 else hue

        return OklchColor(
            lightness.coerceIn(0.0, 1.0),
            chroma.coerceAtLeast(0.0),
            normalizedHue
        )
    }

    /**
     * Inverse gamma correction for sRGB to linear conversion
     */
    private fun inverseGammaCorrection(value: Double): Double {
        return if (value <= 0.04045) {
            value / 12.92
        } else {
            ((value + 0.055) / 1.055).pow(2.4)
        }
    }
}

data class OklchColor(
    val lightness: Double,  // 0-1
    val chroma: Double,     // 0+
    val hue: Double         // 0-360
) {
    fun toRgb(): Color {
        return OklchColorUtil.oklchToRgb(lightness, chroma, hue)
    }

    fun toCssString(): String {
        val l = String.format("%.3f", lightness)
        val c = String.format("%.3f", chroma)
        val h = String.format("%.1f", hue)

        return "oklch($l $c $h)"
    }
}