package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.reset_pw_instruction.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.xeniac.warrantyroster_manager.R

@Composable
fun EmailSentAnimation(
    sentResetPasswordEmailsCount: Int,
    modifier: Modifier = Modifier
) {
    val animationComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(resId = R.raw.anim_auth_reset_pw_email_sent)
    )
    val animationProgress = remember { Animatable(initialValue = 0f) }

    // Trigger animation when sentResetPasswordEmailsCount changes
    LaunchedEffect(key1 = sentResetPasswordEmailsCount) {
        animationProgress.snapTo(targetValue = 0f) // Reset to start
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 6_500,
                easing = LinearEasing
            )
        )
    }

    LottieAnimation(
        composition = animationComposition,
        progress = { animationProgress.value },
        modifier = modifier
    )
}