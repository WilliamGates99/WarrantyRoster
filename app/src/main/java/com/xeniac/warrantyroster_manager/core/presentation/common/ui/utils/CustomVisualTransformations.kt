package com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class NumberSeparatorVisualTransformation : VisualTransformation {

    override fun filter(
        text: AnnotatedString
    ): TransformedText {
        val formattedText = formatNumberWithSeparators(number = text.text)
        val offsetMapping = calculateOffsetMapping(
            originalText = text.text,
            formattedText = formattedText
        )

        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = offsetMapping
        )
    }

    private fun formatNumberWithSeparators(
        number: String
    ): String {
        try {
            // Return empty or blank input as is
            if (number.isBlank()) {
                return number
            }

            // Remove existing commas for clean processing
            val cleanNumber = number.replace(
                oldValue = ",",
                newValue = ""
            )

            val reversedNumber = cleanNumber.reversed()
            val formattedNumber = reversedNumber
                .chunked(size = 3)
                .joinToString(separator = ",")

            return formattedNumber.reversed() // Reverse back to original order
        } catch (e: NumberFormatException) {
            return number // Return the original text if formatting fails
        }
    }

    private fun calculateOffsetMapping(
        originalText: String,
        formattedText: String
    ): OffsetMapping {
        if (originalText.isEmpty()) {
            return OffsetMapping.Identity
        }

        return object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return offset

                // Calculate the transformed offset, considering added commas
                val cleanOriginalText = originalText.replace(
                    oldValue = ",",
                    newValue = ""
                )
                val digitsBeforeOffset = cleanOriginalText.take(n = offset).length
                val commasBeforeOffset = (digitsBeforeOffset - 1) / 3

                val transformedOffset = digitsBeforeOffset + commasBeforeOffset

                return transformedOffset
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) {
                    return offset
                }

                // Calculate the original offset, considering added commas
                val commasBeforeOffset = formattedText.take(n = offset).count { it == ',' }
                val digitsBeforeOffset = offset - commasBeforeOffset

                return digitsBeforeOffset
            }
        }
    }
}