package com.xeniac.warrantyroster_manager.core.presentation.common.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGrayDark
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText

@Composable
fun OfflineErrorMessage(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(all = 32.dp),
    message: String = stringResource(id = R.string.error_network_connection_unavailable),
    messageStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.dynamicGrayDark
    ),
    onRetryClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 4.dp,
            alignment = Alignment.CenterVertically
        ),
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Text(
            text = message,
            style = messageStyle
        )

        onRetryClick?.let { onClick ->
            RetryButton(onClick = onClick)
        }
    }
}

@Composable
fun NetworkErrorMessage(
    message: UiText?,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(all = 32.dp),
    messageStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.dynamicGrayDark
    ),
    onRetryClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 4.dp,
            alignment = Alignment.CenterVertically
        ),
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Text(
            text = message?.asString().orEmpty(),
            style = messageStyle
        )

        onRetryClick?.let { onClick ->
            RetryButton(onClick = onClick)
        }
    }
}

@Composable
fun NetworkErrorMessage(
    message: String?,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(all = 32.dp),
    messageStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.dynamicGrayDark
    ),
    onRetryClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 4.dp,
            alignment = Alignment.CenterVertically
        ),
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Text(
            text = message.orEmpty(),
            style = messageStyle
        )

        onRetryClick?.let { onClick ->
            RetryButton(onClick = onClick)
        }
    }
}

@Composable
private fun RetryButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    contentPadding: PaddingValues = PaddingValues(4.dp),
    colors: ButtonColors = ButtonDefaults.textButtonColors(
        containerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
    ),
    text: String = stringResource(id = R.string.error_btn_retry),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Bold
    ),
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        shape = shape,
        contentPadding = contentPadding,
        colors = colors,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}