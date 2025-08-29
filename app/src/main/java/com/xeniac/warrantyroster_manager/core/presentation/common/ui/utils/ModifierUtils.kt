package com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics

@Composable
fun Modifier.addTestTag(
    tag: String?
): Modifier = tag?.let { this.testTag(tag = it) } ?: this

@Composable
fun Modifier.addBorder(
    border: BorderStroke?,
    shape: Shape
): Modifier = border?.let {
    border(
        border = border,
        shape = shape
    ).padding(all = border.width)
} ?: this

@Composable
fun Modifier.addTextFieldContentType(
    contentType: ContentType?
): Modifier = contentType?.let {
    this.semantics {
        this.contentType = it
    }
} ?: this

@Composable
fun Modifier.addClickable(
    enabled: Boolean = true,
    indication: Indication? = LocalIndication.current,
    interactionSource: MutableInteractionSource? = if (indication is IndicationNodeFactory) null
    else remember { MutableInteractionSource() },
    role: Role? = null,
    onClickLabel: String? = null,
    onClick: (() -> Unit)?
): Modifier = onClick?.let {
    this.clickable(
        enabled = enabled,
        indication = indication,
        interactionSource = interactionSource,
        role = role,
        onClickLabel = onClickLabel,
        onClick = onClick
    )
} ?: this