package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGrayDark

@Composable
fun EmptyWarrantiesMessage(
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
    val layoutDirection = LocalLayoutDirection.current
    val rotationDegree = when (layoutDirection) {
        LayoutDirection.Ltr -> 0f
        LayoutDirection.Rtl -> 180f
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // TODO: CHECK SPEED AND ITERATION
        LottieAnimation(
            composition = rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(resId = R.raw.anim_warranties_empty_list)
            ).value,
            iterations = 1,
            speed = 1f,
            contentScale = ContentScale.Inside,
            modifier = modifier
                .size(250.dp)
                .graphicsLayer {
                    rotationY = rotationDegree
                }
        )

        Text(
            text = message?.asString().orEmpty(),
            style = messageStyle
        )
    }
}