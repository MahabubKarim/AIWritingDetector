package com.mmk.aiwritingdetector.domain.util

import kotlin.math.pow
import kotlin.math.round

/**
 * Common formatting utility for Kotlin Multiplatform.
 * Replaces String.format for basic numeric use cases.
 */
fun Double.format(decimals: Int): String {
    if (decimals < 0) return this.toString()
    val multiplier = 10.0.pow(decimals)
    val rounded = round(this * multiplier) / multiplier
    val s = rounded.toString()
    if (decimals == 0) return rounded.toLong().toString()
    
    val parts = s.split(".")
    val intPart = parts[0]
    val decPart = if (parts.size > 1) parts[1] else ""
    val paddedDecimal = decPart.padEnd(decimals, '0').take(decimals)
    return if (paddedDecimal.isEmpty()) intPart else "$intPart.$paddedDecimal"
}
