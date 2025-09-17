package com.xeniac.warrantyroster_manager.core.domain.utils

/**
 * Checks if this `Float` number has a non-zero decimal component.
 *
 * This extension function determines whether the `Float` value has any digits after the decimal point.
 *
 * It effectively checks if the number is a whole number or not.
 *
 * @return `true` if the `Float` number has a decimal component (e.g., `3.14f`, `-2.5f`),
 *         `false` if the `Float` number is a whole number (e.g., `3.0f`, `-2.0f`, `0.0f`).
 */
fun Float.hasDecimalPoints(): Boolean = this % 1 != 0f

/**
 * Returns the decimal part of this `Float` number.
 *
 * This extension function extracts the decimal component of the `Float` value.
 *
 * For example, if the `Float` is `3.14f`, this function will return `0.1400001`.
 * If the `Float` is a whole number like `3.0f`, it will return `0.0f`.
 *
 * Note that due to the nature of floating-point arithmetic, the result might not be
 * exactly as expected due to precision limitations.
 *
 * @return The decimal part of this `Float` number.
 */
fun Float.getDecimalPart(): Float = this % 1