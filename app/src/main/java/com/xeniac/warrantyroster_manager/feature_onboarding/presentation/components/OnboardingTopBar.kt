package com.xeniac.warrantyroster_manager.feature_onboarding.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Black
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.NavyBlue
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.addBorder
import com.xeniac.warrantyroster_manager.feature_onboarding.presentation.OnboardingAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingTopBar(
    currentAppLocale: AppLocale?,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 16.dp
    ),
    onAction: (action: OnboardingAction) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .windowInsetsPadding(insets = TopAppBarDefaults.windowInsets)
            .fillMaxWidth()
            .displayCutoutPadding()
            .padding(contentPadding)
    ) {
        Box(modifier = Modifier.size(32.dp))

        ExpandingDotIndicator(
            pagerState = pagerState,
            modifier = Modifier.weight(1f)
        )

        LocaleButton(
            currentAppLocale = currentAppLocale,
            onAction = onAction
        )
    }
}

@Composable
private fun LocaleButton(
    currentAppLocale: AppLocale?,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    border: BorderStroke = BorderStroke(
        width = 2.dp,
        color = NavyBlue
    ),
    onAction: (action: OnboardingAction) -> Unit
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 8.dp,
                    color = Black,
                    alpha = 0.24f
                )
            )
            .clip(shape)
            .addBorder(
                border = border,
                shape = shape
            )
            .clickable(
                role = Role.Button,
                onClick = { onAction(OnboardingAction.ShowLocaleBottomSheet) }
            )
    ) {
        Image(
            painter = painterResource(
                id = currentAppLocale?.flagIconId ?: AppLocale.DEFAULT.flagIconId
            ),
            contentDescription = currentAppLocale?.let {
                stringResource(id = currentAppLocale.fullTitleId)
            },
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}