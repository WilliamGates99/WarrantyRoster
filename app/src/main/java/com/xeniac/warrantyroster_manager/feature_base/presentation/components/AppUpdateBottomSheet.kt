package com.xeniac.warrantyroster_manager.feature_base.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.SecureFlagPolicy
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGray800
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicNavyBlue
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.openAppUpdatePageInStore
import com.xeniac.warrantyroster_manager.feature_base.presentation.BaseAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppUpdateBottomSheet(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    dismissOnBackPress: Boolean = true,
    securePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit,
    sheetProperties: ModalBottomSheetProperties = ModalBottomSheetProperties(
        shouldDismissOnBackPress = dismissOnBackPress,
        securePolicy = securePolicy
    ),
    animationComposition: LottieComposition? = rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.anim_base_app_update)
    ).value,
    animationIteration: Int = LottieConstants.IterateForever,
    animationSpeed: Float = 1f,
    animationRotationDegree: Float = when (layoutDirection) {
        LayoutDirection.Ltr -> 0f
        LayoutDirection.Rtl -> 180f
    },
    headline: String = stringResource(id = R.string.base_app_update_sheet_title).uppercase(),
    headlineFontSize: TextUnit = 18.sp,
    headlineLineHeight: TextUnit = 24.sp,
    headlineFontWeight: FontWeight = FontWeight.Black,
    headlineTextAlign: TextAlign = TextAlign.Center,
    headlineColor: Color = MaterialTheme.colorScheme.onSurface,
    message: String = stringResource(
        id = R.string.base_app_update_sheet_message,
        stringResource(id = R.string.app_name)
    ),
    messageFontSize: TextUnit = 16.sp,
    messageLineHeight: TextUnit = 20.sp,
    messageFontWeight: FontWeight = FontWeight.Medium,
    messageTextAlign: TextAlign = TextAlign.Center,
    messageColor: Color = MaterialTheme.colorScheme.onSurface,
    onAction: (action: BaseAction) -> Unit
) {
    if (isVisible) {
        val context = LocalContext.current
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            sheetState = sheetState,
            properties = sheetProperties,
            onDismissRequest = { onAction(BaseAction.DismissAppUpdateSheet) },
            modifier = modifier
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 4.dp,
                        bottom = 20.dp
                    )
            ) {
                LottieAnimation(
                    composition = animationComposition,
                    iterations = animationIteration,
                    speed = animationSpeed,
                    modifier = Modifier
                        .size(150.dp)
                        .graphicsLayer {
                            rotationY = animationRotationDegree
                        }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = headline,
                    fontSize = headlineFontSize,
                    lineHeight = headlineLineHeight,
                    fontWeight = headlineFontWeight,
                    textAlign = headlineTextAlign,
                    color = headlineColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = message,
                    fontSize = messageFontSize,
                    lineHeight = messageLineHeight,
                    fontWeight = messageFontWeight,
                    textAlign = messageTextAlign,
                    color = messageColor
                )

                Spacer(modifier = Modifier.height(28.dp))

                UpdateButton(
                    onClick = {
                        onAction(BaseAction.DismissAppUpdateSheet)
                        context.openAppUpdatePageInStore()
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                DismissButton(
                    onClick = { onAction(BaseAction.DismissAppUpdateSheet) }
                )
            }
        }
    }
}

@Composable
private fun UpdateButton(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 14.dp),
    text: String = stringResource(id = R.string.base_app_update_sheet_btn_update),
    textFontSize: TextUnit = 16.sp,
    textLineHeight: TextUnit = 20.sp,
    textFontWeight: FontWeight = FontWeight.Bold,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        contentPadding = contentPadding,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = textFontSize,
            lineHeight = textLineHeight,
            fontWeight = textFontWeight
        )
    }
}

@Composable
private fun DismissButton(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 12.dp),
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors().copy(
        contentColor = MaterialTheme.colorScheme.dynamicGray800,
        disabledContentColor = MaterialTheme.colorScheme.dynamicNavyBlue.copy(alpha = 0.38f)
    ),
    text: String = stringResource(id = R.string.base_app_update_sheet_btn_dismiss),
    textFontSize: TextUnit = 14.sp,
    textLineHeight: TextUnit = 20.sp,
    textFontWeight: FontWeight = FontWeight.Medium,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        contentPadding = contentPadding,
        colors = colors,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
    ) {
        Text(
            text = text,
            fontSize = textFontSize,
            lineHeight = textLineHeight,
            fontWeight = textFontWeight
        )
    }
}