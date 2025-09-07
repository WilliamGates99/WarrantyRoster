package com.xeniac.warrantyroster_manager.feature_settings.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.domain.models.UserProfile
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Blue
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Green
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Red
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicBlack
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.toDp
import com.xeniac.warrantyroster_manager.feature_settings.presentation.SettingsAction

@Composable
fun EmailSection(
    userProfile: UserProfile?,
    isUserProfileLoading: Boolean,
    isSendVerificationEmailLoading: Boolean,
    modifier: Modifier = Modifier,
    enterTransition: EnterTransition = fadeIn(),
    exitTransition: ExitTransition = fadeOut(),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 12.dp,
        vertical = 14.dp
    ),
    emailStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.dynamicBlack
    ),
    emailMaxLines: Int = 1,
    emailOverflow: TextOverflow = TextOverflow.MiddleEllipsis,
    onAction: (action: SettingsAction) -> Unit
) {
    AnimatedVisibility(
        visible = !isUserProfileLoading,
        enter = enterTransition,
        exit = exitTransition,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            EmailVerificationStatusAnimation(isEmailVerified = userProfile?.isEmailVerified)

            Text(
                text = userProfile?.email.orEmpty(),
                style = emailStyle,
                maxLines = emailMaxLines,
                overflow = emailOverflow,
                softWrap = false,
                modifier = Modifier.weight(1f)
            )

            EmailVerificationStatusButton(
                isEmailVerified = userProfile?.isEmailVerified,
                isLoading = isSendVerificationEmailLoading,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun EmailVerificationStatusAnimation(
    isEmailVerified: Boolean?,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    background: Color = when (isEmailVerified) {
        true -> Green.copy(alpha = 0.10f)
        else -> Red.copy(alpha = 0.10f)
    },
    animationIteration: Int = when (isEmailVerified) {
        true -> 1
        else -> LottieConstants.IterateForever
    },
    animationSpeed: Float = when (isEmailVerified) {
        true -> 0.60f
        else -> 0.60f
    }
) {
    val layoutDirection = LocalLayoutDirection.current
    val rotationDegree = when (layoutDirection) {
        LayoutDirection.Ltr -> 0f
        LayoutDirection.Rtl -> 180f
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(background)
    ) {
        LottieAnimation(
            composition = rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(
                    resId = when (isEmailVerified) {
                        true -> R.raw.anim_settings_account_verified
                        else -> R.raw.anim_settings_account_not_verified
                    }
                )
            ).value,
            iterations = animationIteration,
            speed = animationSpeed,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotationDegree
                }
        )
    }
}

@Composable
private fun EmailVerificationStatusButton(
    isEmailVerified: Boolean?,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    background: Color = when (isEmailVerified) {
        true -> Green.copy(alpha = 0.20f)
        else -> Blue.copy(alpha = 0.20f)
    },
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 8.dp,
        vertical = 4.dp
    ),
    text: String = stringResource(
        id = when (isEmailVerified) {
            true -> R.string.settings_account_settings_status_verified
            else -> R.string.settings_account_settings_status_verify
        }
    ).uppercase(),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 10.sp,
        lineHeight = 10.sp,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center,
        color = when (isEmailVerified) {
            true -> Green
            else -> Blue
        }
    ),
    maxLines: Int = 1,
    progressIndicatorColor: Color = textStyle.color,
    progressIndicatorStrokeWidth: Dp = 2.dp,
    onAction: (action: SettingsAction) -> Unit
) {
    var textWidthPx by rememberSaveable { mutableIntStateOf(0) }
    var textHeightPx by rememberSaveable { mutableIntStateOf(0) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(shape)
            .background(background)
            .clickable(
                enabled = !isLoading && isEmailVerified != true,
                role = Role.Button,
                onClick = { onAction(SettingsAction.SendVerificationEmail) }
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