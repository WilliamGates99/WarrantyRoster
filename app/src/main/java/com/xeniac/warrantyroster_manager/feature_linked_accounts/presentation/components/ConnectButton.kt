package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGray100
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGray400
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.addBorder
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.toDp

@Composable
fun ConnectButton(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    border: BorderStroke = BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.dynamicGray100
    ),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 8.dp,
        vertical = 4.dp
    ),
    text: String = stringResource(id = R.string.linked_accounts_provider_action_connect).uppercase(),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 10.sp,
        lineHeight = 10.sp,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.dynamicGray400
    ),
    maxLines: Int = 1,
    progressIndicatorColor: Color = textStyle.color,
    progressIndicatorStrokeWidth: Dp = 2.dp
) {
    var textWidthPx by rememberSaveable { mutableIntStateOf(0) }
    var textHeightPx by rememberSaveable { mutableIntStateOf(0) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(shape)
            .addBorder(
                border = border,
                shape = shape
            )
            .padding(contentPadding)
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
                Text(
                    text = text,
                    style = textStyle,
                    maxLines = maxLines,
                    modifier = Modifier.onSizeChanged {
                        textWidthPx = it.width
                        textHeightPx = it.height
                    }
                )
            }
        }
    }
}