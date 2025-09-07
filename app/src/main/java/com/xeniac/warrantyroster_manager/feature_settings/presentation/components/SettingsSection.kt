package com.xeniac.warrantyroster_manager.feature_settings.presentation.components

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
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.domain.models.AppTheme
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Black
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicBlack
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGrayDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGrayLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicNavyBlue
import com.xeniac.warrantyroster_manager.feature_settings.presentation.SettingsAction

@Composable
fun SettingsSection(
    currentAppLocale: AppLocale?,
    currentAppTheme: AppTheme?,
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.settings_settings_title),
    titleStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.dynamicNavyBlue
    ),
    onAction: (action: SettingsAction) -> Unit
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

        SettingsCard(
            currentAppLocale = currentAppLocale,
            currentAppTheme = currentAppTheme,
            onAction = onAction
        )
    }
}

@Composable
private fun SettingsCard(
    currentAppLocale: AppLocale?,
    currentAppTheme: AppTheme?,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    background: Color = MaterialTheme.colorScheme.surface,
    dividerThickness: Dp = 1.dp,
    dividerColor: Color = MaterialTheme.colorScheme.dynamicGrayLight,
    onAction: (action: SettingsAction) -> Unit
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
        SettingsItem(
            icon = painterResource(id = R.drawable.ic_settings_locale),
            title = stringResource(id = R.string.settings_settings_locale_title),
            currentValue = currentAppLocale?.titleId?.let { titleId ->
                stringResource(id = titleId)
            },
            onClick = { onAction(SettingsAction.ShowLocaleBottomSheet) }
        )

        HorizontalDivider(
            thickness = dividerThickness,
            color = dividerColor
        )

        SettingsItem(
            icon = painterResource(id = R.drawable.ic_settings_theme),
            title = stringResource(id = R.string.settings_settings_theme_title),
            currentValue = currentAppTheme?.titleId?.let { titleId ->
                stringResource(id = titleId)
            },
            onClick = { onAction(SettingsAction.ShowThemeBottomSheet) }
        )
    }
}

@Composable
private fun SettingsItem(
    icon: Painter,
    title: String,
    currentValue: String?,
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
    currentValueStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.dynamicGrayDark
    ),
    currentValueMaxLines: Int = 1,
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
        SettingsItemIcon(
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

        Text(
            text = currentValue?.uppercase().orEmpty(),
            style = currentValueStyle,
            maxLines = currentValueMaxLines
        )
    }
}

@Composable
private fun SettingsItemIcon(
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