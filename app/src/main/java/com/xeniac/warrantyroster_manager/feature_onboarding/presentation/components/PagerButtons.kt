package com.xeniac.warrantyroster_manager.feature_onboarding.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.NavyBlue
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.White

@Composable
fun SkipButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    colors: ButtonColors = ButtonDefaults.textButtonColors().copy(
        contentColor = White,
        disabledContentColor = White
    ),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 12.dp,
        vertical = 4.dp
    ),
    text: String = stringResource(id = R.string.onboarding_btn_skip),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 14.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
    ),
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    colors: ButtonColors = ButtonDefaults.textButtonColors().copy(
        contentColor = White,
        disabledContentColor = White
    ),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 12.dp,
        vertical = 4.dp
    ),
    text: String = stringResource(id = R.string.onboarding_btn_back),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 14.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    ),
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}

@Composable
fun NextButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    colors: ButtonColors = ButtonDefaults.buttonColors().copy(
        containerColor = NavyBlue,
        disabledContainerColor = NavyBlue,
        contentColor = White,
        disabledContentColor = White
    ),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 40.dp,
        vertical = 16.dp
    ),
    text: String = stringResource(id = R.string.onboarding_btn_next),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    ),
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}

@Composable
fun StartButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    colors: ButtonColors = ButtonDefaults.buttonColors().copy(
        containerColor = NavyBlue,
        disabledContainerColor = NavyBlue,
        contentColor = White,
        disabledContentColor = White
    ),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 16.dp
    ),
    text: String = stringResource(id = R.string.onboarding_btn_start),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    ),
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}