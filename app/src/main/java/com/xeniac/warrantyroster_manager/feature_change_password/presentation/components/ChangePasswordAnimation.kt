package com.xeniac.warrantyroster_manager.feature_change_password.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.xeniac.warrantyroster_manager.R

@Composable
fun ChangePasswordAnimation(
    modifier: Modifier = Modifier
) {
    val layoutDirection = LocalLayoutDirection.current
    val rotationDegree = when (layoutDirection) {
        LayoutDirection.Ltr -> 0f
        LayoutDirection.Rtl -> 180f
    }

    LottieAnimation(
        composition = rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(resId = R.raw.anim_change_password)
        ).value,
        iterations = LottieConstants.IterateForever,
        speed = 1f,
        contentScale = ContentScale.Inside,
        modifier = modifier
            .graphicsLayer {
                rotationY = rotationDegree
            }
    )
}