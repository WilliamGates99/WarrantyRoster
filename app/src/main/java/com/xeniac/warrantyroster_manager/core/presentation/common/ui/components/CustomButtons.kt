package com.xeniac.warrantyroster_manager.core.presentation.common.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Blue
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.White
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.toDp

@Composable
fun bigButtonColors(): ButtonColors = ButtonDefaults.buttonColors().copy(
    containerColor = Blue,
    contentColor = White,
    disabledContainerColor = Blue,
    disabledContentColor = White
)

@Composable
fun BigButton(
    text: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isEnabled: Boolean = true,
    shape: Shape = RoundedCornerShape(12.dp),
    colors: ButtonColors = bigButtonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 16.dp
    ),
    interactionSource: MutableInteractionSource? = null,
    textFontSize: TextUnit = 16.sp,
    textLineHeight: TextUnit = TextUnit.Unspecified,
    textFontWeight: FontWeight = FontWeight.Bold,
    textAlign: TextAlign = TextAlign.Center,
    textMaxLines: Int = 1,
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = textFontSize,
        lineHeight = textLineHeight,
        fontWeight = textFontWeight,
        textAlign = textAlign
    ),
    icon: Painter? = null,
    iconSize: Dp = 28.dp,
    iconColor: Color = colors.contentColor,
    progressIndicatorColor: Color = colors.contentColor,
    progressIndicatorStrokeWidth: Dp = 3.dp,
    onClick: () -> Unit
) {
    var textWidthPx by rememberSaveable { mutableIntStateOf(0) }
    var textHeightPx by rememberSaveable { mutableIntStateOf(0) }

    Button(
        enabled = isEnabled && !isLoading,
        onClick = onClick,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        modifier = modifier
    ) {
        when {
            isLoading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(
                        width = textWidthPx.toDp(),
                        height = textHeightPx.toDp()
                    )
                ) {
                    CircularProgressIndicator(
                        color = progressIndicatorColor,
                        strokeWidth = progressIndicatorStrokeWidth,
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(
                                ratio = 1f,
                                matchHeightConstraintsFirst = true
                            )
                    )
                }
            }
            else -> {
                icon?.let { icon ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(iconSize)
                    ) {
                        Icon(
                            painter = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.widthIn(8.dp))
                }

                Text(
                    text = text,
                    style = textStyle,
                    maxLines = textMaxLines,
                    modifier = Modifier.onSizeChanged {
                        textWidthPx = it.width
                        textHeightPx = it.height
                    }
                )
            }
        }
    }
}

@Composable
fun BigButton(
    text: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isEnabled: Boolean = true,
    shape: Shape = RoundedCornerShape(12.dp),
    colors: ButtonColors = bigButtonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 16.dp
    ),
    interactionSource: MutableInteractionSource? = null,
    textFontSize: TextUnit = 16.sp,
    textLineHeight: TextUnit = TextUnit.Unspecified,
    textFontWeight: FontWeight = FontWeight.Bold,
    textAlign: TextAlign = TextAlign.Center,
    textMaxLines: Int = 1,
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = textFontSize,
        lineHeight = textLineHeight,
        fontWeight = textFontWeight,
        textAlign = textAlign
    ),
    progressIndicatorColor: Color = colors.contentColor,
    progressIndicatorStrokeWidth: Dp = 3.dp,
    iconRow: @Composable (RowScope.() -> Unit)? = null,
    onClick: () -> Unit
) {
    var textWidthPx by rememberSaveable { mutableIntStateOf(0) }
    var textHeightPx by rememberSaveable { mutableIntStateOf(0) }

    Button(
        enabled = isEnabled && !isLoading,
        onClick = onClick,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        modifier = modifier
    ) {
        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(
                    width = textWidthPx.toDp(),
                    height = textHeightPx.toDp()
                )
            ) {
                CircularProgressIndicator(
                    color = progressIndicatorColor,
                    strokeWidth = progressIndicatorStrokeWidth,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(
                            ratio = 1f,
                            matchHeightConstraintsFirst = true
                        )
                )
            }
        } else {
            iconRow?.let { iconRow ->
                iconRow()
            }

            Text(
                text = text,
                style = textStyle,
                maxLines = textMaxLines,
                modifier = Modifier.onSizeChanged {
                    textWidthPx = it.width
                    textHeightPx = it.height
                }
            )
        }
    }
}