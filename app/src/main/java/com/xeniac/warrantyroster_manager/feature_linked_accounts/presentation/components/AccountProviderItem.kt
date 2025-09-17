package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Black
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Green
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Red
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicBlack
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.models.AccountProviders
import com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.LinkedAccountsAction
import com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.states.UiAccountProvider

@Composable
fun AccountProviderItem(
    uiAccountProvider: UiAccountProvider,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    background: Color = MaterialTheme.colorScheme.surface,
    onAction: (action: LinkedAccountsAction) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 2.dp,
                    color = Black,
                    alpha = 0.16f
                )
            )
            .clip(shape)
            .background(background)
            .clickable(
                enabled = !uiAccountProvider.isLoading,
                onClick = {
                    when (uiAccountProvider.isLinked) {
                        true -> when (uiAccountProvider.accountProvider) {
                            AccountProviders.GOOGLE -> onAction(LinkedAccountsAction.UnlinkGoogleAccount)
                            AccountProviders.X -> onAction(LinkedAccountsAction.UnlinkXAccount)
                            AccountProviders.GITHUB -> onAction(LinkedAccountsAction.UnlinkGithubAccount)
                        }
                        false -> when (uiAccountProvider.accountProvider) {
                            AccountProviders.GOOGLE -> onAction(LinkedAccountsAction.LinkGoogleAccount)
                            AccountProviders.X -> onAction(LinkedAccountsAction.CheckPendingLinkXAccount)
                            AccountProviders.GITHUB -> onAction(LinkedAccountsAction.CheckPendingLinkGithubAccount)
                        }
                    }
                }
            )
    ) {
        AccountProviderInfo(
            isLoading = uiAccountProvider.isLoading,
            isLinked = uiAccountProvider.isLinked,
            accountProvider = uiAccountProvider.accountProvider
        )

        ConnectionIndicator(
            isLinked = uiAccountProvider.isLinked,
            modifier = Modifier.align(Alignment.TopStart)
        )
    }
}

@Composable
private fun ConnectionIndicator(
    isLinked: Boolean,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(8.dp),
    size: Dp = 4.dp,
    shape: Shape = CircleShape,
    color: Color = if (isLinked) Green else Red
) {
    Box(
        modifier = modifier
            .padding(padding)
            .size(size)
            .clip(shape)
            .background(color)
    )
}

@Composable
private fun AccountProviderInfo(
    isLoading: Boolean,
    isLinked: Boolean,
    accountProvider: AccountProviders,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 16.dp
    ),
    titleStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.dynamicBlack
    ),
    titleMaxLines: Int = 1,
    titleOverflow: TextOverflow = TextOverflow.Ellipsis
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        AccountProviderLogo(
            logo = painterResource(id = accountProvider.logoId),
            contentDescription = stringResource(id = accountProvider.titleId)
        )

        Text(
            text = stringResource(id = accountProvider.titleId),
            style = titleStyle,
            maxLines = titleMaxLines,
            overflow = titleOverflow,
            modifier = Modifier.weight(1f)
        )

        AnimatedContent(
            targetState = isLinked,
            transitionSpec = { fadeIn().togetherWith(exit = fadeOut()) }
        ) { isLinked ->
            when (isLinked) {
                true -> DisconnectButton(isLoading = isLoading)
                false -> ConnectButton(isLoading = isLoading)
            }
        }
    }
}

@Composable
private fun AccountProviderLogo(
    logo: Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    Image(
        painter = logo,
        contentDescription = contentDescription,
        modifier = modifier.size(size)
    )
}