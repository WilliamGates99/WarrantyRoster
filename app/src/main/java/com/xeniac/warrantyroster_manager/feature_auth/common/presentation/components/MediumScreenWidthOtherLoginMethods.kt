package com.xeniac.warrantyroster_manager.feature_auth.common.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.GrayDarkDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.GrayDarkLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.GrayMediumDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.GrayMediumLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.NavyBlueDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.NavyBlueLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.addBorder
import com.xeniac.warrantyroster_manager.feature_auth.common.presentation.states.OtherLoginMethods

@Composable
fun OtherLoginMethodsDividerMediumWidth(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp),
    title: String = stringResource(id = R.string.auth_other_login_methods_title_medium_width),
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        modifier = modifier
            .fillMaxHeight()
            .padding(contentPadding)
    ) {
        VerticalDivider(
            thickness = dividerThickness,
            color = dividerColor,
            modifier = Modifier
                .weight(1f)
                .clip(dividerShape)
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.rotate(degrees = -90f)
        ) {
            Text(
                text = title,
                style = titleStyle,
                maxLines = titleMaxLines
            )
        }

        VerticalDivider(
            thickness = dividerThickness,
            color = dividerColor,
            modifier = Modifier
                .weight(1f)
                .clip(dividerShape)
        )
    }
}

@Composable
fun OtherLoginMethodsMediumWidth(
    isLoginWithGoogleLoading: Boolean,
    isLoginWithXLoading: Boolean,
    isLoginWithGithubLoading: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        end = 18.dp,
        top = 24.dp,
        bottom = 24.dp
    ),
    onLoginWithGoogleClick: () -> Unit,
    onLoginWithXClick: () -> Unit,
    onLoginWithGithubClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        modifier = modifier
            .fillMaxHeight()
            .padding(contentPadding)
    ) {
        OtherLoginMethods.entries.forEach { loginMethod ->
            OtherLoginMethodButton(
                loginMethod = loginMethod,
                isLoading = when (loginMethod) {
                    OtherLoginMethods.GOOGLE -> isLoginWithGoogleLoading
                    OtherLoginMethods.X -> isLoginWithXLoading
                    OtherLoginMethods.GITHUB -> isLoginWithGithubLoading
                },
                onClick = when (loginMethod) {
                    OtherLoginMethods.GOOGLE -> onLoginWithGoogleClick
                    OtherLoginMethods.X -> onLoginWithXClick
                    OtherLoginMethods.GITHUB -> onLoginWithGithubClick
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
                else -> {
                    Image(
                        painter = painterResource(id = loginMethod.iconId),
                        contentDescription = stringResource(id = loginMethod.contentDescriptionId),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}