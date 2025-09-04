package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.forgot_pw.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Black
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.White

@Composable
fun ForgotPwDescription(
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.forgot_pw_description),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.ExtraBold,
        color = if (isSystemInDarkTheme()) White else Black
    )
) {
    Text(
        text = title,
        style = textStyle,
        modifier = modifier.fillMaxWidth()
    )
}