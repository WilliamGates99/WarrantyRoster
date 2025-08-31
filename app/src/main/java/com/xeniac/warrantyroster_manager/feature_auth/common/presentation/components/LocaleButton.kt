package com.xeniac.warrantyroster_manager.feature_auth.common.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
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
import com.xeniac.warrantyroster_manager.feature_auth.common.presentation.AuthAction

@Composable
fun LocaleButton(
    currentAppLocale: AppLocale?,
    modifier: Modifier = Modifier,
    outerPadding: PaddingValues = PaddingValues(
        horizontal = 12.dp,
        vertical = 12.dp
    ),
    shape: Shape = CircleShape,
    border: BorderStroke = BorderStroke(
        width = 2.dp,
        color = NavyBlue
    ),
    onAction: (action: AuthAction) -> Unit
) {
    Box(
        modifier = modifier
            .padding(outerPadding)
            .size(28.dp)
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 8.dp,
                    color = Black.copy(alpha = 0.24f)
                )
            )
            .clip(shape)
            .addBorder(
                border = border,
                shape = shape
            )
            .clickable(
                role = Role.Button,
                onClick = { onAction(AuthAction.ShowLocaleBottomSheet) }
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