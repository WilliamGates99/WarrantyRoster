package com.xeniac.warrantyroster_manager.core.domain.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Formats a [Number] object to produce a string.
 *
 * @param pattern A non-localized pattern string
 * @param symbols The set of symbols to be used
 *
 * @return Formatted string.
 *
 * @throws IllegalArgumentException if the Format cannot format the given object.
 */
fun Number.formatToString(
    pattern: String = "#,###",
    symbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.getDefault())
): String = DecimalFormat(
    /* pattern = */ pattern,
    /* symbols = */ symbols
).format(this)

/**
 * Removes formatting from a string that was previously formatted using a specific pattern and symbols.
 *
 * This function takes a formatted string as input and attempts to parse it using a [DecimalFormat]
 * with the provided pattern and symbols.
 *
 * If the parsing is successful, it returns the parsed value as a plain string without any formatting.
 * If the parsing fails, it returns an empty string.
 *
 * This is commonly used to extract the original numeric value from a formatted display string,
 * such as removing commas or currency symbols.
 *
 * @param pattern The formatting pattern used to format the string. Defaults to "#,###",
 * which represents a number with thousands separators (commas).
 * @param symbols The set of symbols to be used for parsing.
 * Defaults to the symbols for the default locale.
 *
 * @return The unformatted string if parsing is successful, or an empty string if parsing fails.
 *
 * @see DecimalFormat
 * @see DecimalFormatSymbols
 */
fun String.unformatNumber(
    pattern: String = "#,###",
    symbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.getDefault())
): String = DecimalFormat(
    /* pattern = */ pattern,
    /* symbols = */ symbols
).parse(this)?.toString().orEmpty()

/**
 * Limits the decimal places of a Float to the specified number of digits.
 *
 * This function formats the Float value to a String with the specified number of
 * decimal places using `String.format()`.
 *
 * @param decimalPlaces The number of decimal places to keep. Defaults to 1.
 * @param locale The locale to use for formatting. Defaults to Locale.US.
 * @return A String representation of the Float with the limited decimal places.
 *
 * @sample
 * val floatValue = 3.14159f
 * val formattedValue = floatValue.formatWithDecimalPlaces() // "3.1"
 * val formattedValue2 = floatValue.formatWithDecimalPlaces(decimalPlaces = 2) // "3.14"
 * val formattedValue3 = floatValue.formatWithDecimalPlaces(decimalPlaces = 2, locale = Locale.FRANCE) // "3,14"
 */
fun Float.formatWithDecimalPlaces(
    decimalPlaces: Int = 1,
    locale: Locale = Locale.US
): String = String.format(
    locale = locale,
    format = "%.${decimalPlaces}f",
    this
)

/**
 * Converts Persian digits within a [String] to their corresponding English digits.
 *
 * This function iterates through the input string and replaces any Persian digits (۰-۹)
 * with their equivalent English digits (0-9).
 *
 * If `shouldIncludeDecimalPoint` is true, the first occurrence of the decimal point (`.`)
 * will be included in the output. Any subsequent decimal points will be omitted.
 * If `shouldIncludeDecimalPoint` is false (default), all decimal points will be omitted.
 *
 * Non-digit characters, other than the decimal point (if included), will be omitted.
 *
 * @param shouldIncludeDecimalPoint Whether to include the first decimal point in the output. Defaults to false.
 *
 * @return A new string with Persian digits converted to English digits.
 */
fun String.toEnglishDigits(
    shouldIncludeDecimalPoint: Boolean = false
): String {
    var isDecimalPointFound = false

    val input = this.filter {
        val isDecimalPoint = shouldIncludeDecimalPoint && it == '.'
        it.isDigit() || isDecimalPoint
    }.trim()

    var output = ""
    input.forEach { char ->
        if (char == '.' && !isDecimalPointFound) {
            output += char
            isDecimalPointFound = true
        } else if (char != '.') {
            output += when (char) {
                '۱' -> '1'
                '۲' -> '2'
                '۳' -> '3'
                '۴' -> '4'
                '۵' -> '5'
                '۶' -> '6'
                '۷' -> '7'
                '۸' -> '8'
                '۹' -> '9'
                '۰' -> '0'
                else -> char
            }
        }
    }

    return output
}

/**
 * Converts Persian digits within a [String] to their corresponding English digits.
 *
 * This function iterates through the input string and replaces any Persian digits (۰-۹)
 * with their equivalent English digits (0-9).
 * * **Other characters in the string remain unchanged.**
 *
 * @return A new string with Persian digits converted to English digits.
 */
fun String.convertDigitsToEnglish(): String {
    val input = this.trim()

    var output = ""
    input.forEach { char ->
        output += when (char) {
            '۱' -> '1'
            '۲' -> '2'
            '۳' -> '3'
            '۴' -> '4'
            '۵' -> '5'
            '۶' -> '6'
            '۷' -> '7'
            '۸' -> '8'
            '۹' -> '9'
            '۰' -> '0'
            else -> char
        }
    }

    return output
}