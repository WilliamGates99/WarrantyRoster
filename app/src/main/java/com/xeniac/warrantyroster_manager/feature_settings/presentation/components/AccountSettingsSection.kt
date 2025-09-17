package com.xeniac.warrantyroster_manager.feature_settings.presentation.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.domain.models.UserProfile
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.ChangeEmailScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.ChangePasswordScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.LinkedAccountsScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Black
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicBlack
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGray50
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicNavyBlue
import com.xeniac.warrantyroster_manager.feature_settings.presentation.SettingsAction

enum class AccountSettingsItems(
    val destinationScreen: Any,
    @param:StringRes val titleId: Int,
    @param:DrawableRes val iconId: Int
) {
    LINKED_ACCOUNTS(
        destinationScreen = LinkedAccountsScreen,
        titleId = R.string.settings_account_settings_linked_accounts_title,
        iconId = R.drawable.ic_settings_linked_accounts
    ),
    CHANGE_EMAIL(
        destinationScreen = ChangeEmailScreen,
        titleId = R.string.settings_account_settings_change_email_title,
        iconId = R.drawable.ic_settings_change_email
    ),
    CHANGE_PASSWORD(
        destinationScreen = ChangePasswordScreen,
        titleId = R.string.settings_account_settings_change_password_title,
        iconId = R.drawable.ic_settings_change_password
    )
}

@Composable
fun AccountSettingsSection(
    userProfile: UserProfile?,
    isUserProfileLoading: Boolean,
    isSendVerificationEmailLoading: Boolean,
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.settings_account_settings_title),
    titleStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.dynamicNavyBlue
    ),
    onAction: (action: SettingsAction) -> Unit,
    onNavigateToScreen: (destinationScreen: Any) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title.uppercase(),
            style = titleStyle,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        )

        AccountSettingsCard(
            userProfile = userProfile,
            isUserProfileLoading = isUserProfileLoading,
            isSendVerificationEmailLoading = isSendVerificationEmailLoading,
            onAction = onAction,
            onNavigateToScreen = onNavigateToScreen
        )
    }
}

@Composable
private fun AccountSettingsCard(
    userProfile: UserProfile?,
    isUserProfileLoading: Boolean,
    isSendVerificationEmailLoading: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    background: Color = MaterialTheme.colorScheme.surface,
    dividerThickness: Dp = 1.dp,
    dividerColor: Color = MaterialTheme.colorScheme.dynamicGray50,
    onAction: (action: SettingsAction) -> Unit,
    onNavigateToScreen: (destinationScreen: Any) -> Unit
) {
    Column(
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
    ) {
        EmailSection(
            userProfile = userProfile,
            isUserProfileLoading = isUserProfileLoading,
            isSendVerificationEmailLoading = isSendVerificationEmailLoading,
            onAction = onAction
        )

        if (!isUserProfileLoading) {
            HorizontalDivider(
                thickness = dividerThickness,
                color = dividerColor
            )
        }

        AccountSettingsItems.entries.forEachIndexed { index, accountSettingsItem ->
            AccountSettingsItem(
                icon = painterResource(id = accountSettingsItem.iconId),
                title = stringResource(id = accountSettingsItem.titleId),
                onClick = { onNavigateToScreen(accountSettingsItem.destinationScreen) }
            )

            val isNotLastItem = index != MiscellaneousItems.entries.lastIndex
            if (isNotLastItem) {
                HorizontalDivider(
                    thickness = dividerThickness,
                    color = dividerColor
                )
            }
        }
    }
}

@Composable
private fun AccountSettingsItem(
    icon: Painter,
    title: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 12.dp,
        vertical = 14.dp
    ),
    titleStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.dynamicBlack
    ),
    titleMaxLines: Int = 1,
    titleOverflow: TextOverflow = TextOverflow.Ellipsis,
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .padding(contentPadding)
    ) {
        AccountSettingsItemIcon(
            icon = icon,
            contentDescription = title
        )

        Text(
            text = title,
            style = titleStyle,
            maxLines = titleMaxLines,
            overflow = titleOverflow,
            modifier = Modifier.weight(1f)
        )

        NavigationArrowIcon()
    }
}

@Composable
private fun AccountSettingsItemIcon(
    icon: Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    background: Color = MaterialTheme.colorScheme.dynamicNavyBlue.copy(alpha = 0.10f),
    iconColor: Color = MaterialTheme.colorScheme.dynamicNavyBlue
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(background)
            .padding(all = 4.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun NavigationArrowIcon(
    modifier: Modifier = Modifier,
    icon: Painter = painterResource(id = R.drawable.ic_settings_navigation_arrow),
    size: Dp = 16.dp,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.fillMaxSize()
        )
    }
}