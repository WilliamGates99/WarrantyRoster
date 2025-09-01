package com.xeniac.warrantyroster_manager.feature_auth.common.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Black
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.GrayDarkDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.GrayDarkLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.GrayMediumDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.GrayMediumLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.NavyBlueDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.NavyBlueLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.White
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.addBorder
import com.xeniac.warrantyroster_manager.feature_auth.common.presentation.states.OtherLoginMethods

@Composable
fun OtherLoginMethodsDividerCompactWidth(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 18.dp
    ),
    title: String = stringResource(id = R.string.auth_other_login_methods_title_compact_width),
    titleStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 12.sp,
        lineHeight = 12.sp,
        fontWeight = FontWeight.Light,
        textAlign = TextAlign.Center,
        color = if (isDarkTheme) GrayDarkDark else GrayDarkLight
    ),
    titleMaxLines: Int = 1,
    dividerThickness: Dp = 1.dp,
    dividerShape: Shape = CircleShape,
    dividerColor: Color = if (isDarkTheme) GrayMediumDark else GrayMediumLight
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        HorizontalDivider(
            thickness = dividerThickness,
            color = dividerColor,
            modifier = Modifier
                .weight(1f)
                .clip(dividerShape)
        )

        Text(
            text = title,
            style = titleStyle,
            maxLines = titleMaxLines
        )

        HorizontalDivider(
            thickness = dividerThickness,
            color = dividerColor,
            modifier = Modifier
                .weight(1f)
                .clip(dividerShape)
        )
    }
}

@Composable
fun OtherLoginMethodsCompactWidth(
    isLoginWithGoogleLoading: Boolean,
    isLoginWithXLoading: Boolean,
    isLoginWithFacebookLoading: Boolean,
    modifier: Modifier = Modifier,
    onLoginWithGoogleClick: () -> Unit,
    onLoginWithXClick: () -> Unit,
    onLoginWithFacebookClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        OtherLoginMethods.entries.forEach { loginMethod ->
            OtherLoginMethodButton(
                loginMethod = loginMethod,
                isLoading = when (loginMethod) {
                    OtherLoginMethods.GOOGLE -> isLoginWithGoogleLoading
                    OtherLoginMethods.X -> isLoginWithXLoading
                    OtherLoginMethods.FACEBOOK -> isLoginWithFacebookLoading
                },
                onClick = when (loginMethod) {
                    OtherLoginMethods.GOOGLE -> onLoginWithGoogleClick
                    OtherLoginMethods.X -> onLoginWithXClick
                    OtherLoginMethods.FACEBOOK -> onLoginWithFacebookClick
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OtherLoginMethodButton(
    loginMethod: OtherLoginMethods,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    shape: Shape = RoundedCornerShape(12.dp),
    border: BorderStroke = BorderStroke(
        width = 1.dp,
        color = if (isDarkTheme) GrayMediumDark else GrayMediumLight
    ),
    contentPadding: PaddingValues = PaddingValues(all = 12.dp),
    contentSize: Dp = 32.dp,
    progressIndicatorColor: Color = if (isDarkTheme) NavyBlueDark else NavyBlueLight,
    progressIndicatorStrokeWidth: Dp = 4.dp,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(shape)
            .addBorder(
                border = border,
                shape = shape
            )
            .clickable(
                enabled = !isLoading,
                role = Role.Button,
                onClick = onClick
            )
            .padding(contentPadding)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(contentSize)
        ) {
            when {
                isLoading -> {
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
                else -> when (loginMethod) {
                    OtherLoginMethods.X -> Icon(
                        painter = painterResource(id = loginMethod.iconId),
                        contentDescription = stringResource(id = loginMethod.contentDescriptionId),
                        tint = if (isDarkTheme) White else Black,
                        modifier = Modifier.fillMaxSize()
                    )
                    else -> Image(
                        painter = painterResource(id = loginMethod.iconId),
                        contentDescription = stringResource(id = loginMethod.contentDescriptionId),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}