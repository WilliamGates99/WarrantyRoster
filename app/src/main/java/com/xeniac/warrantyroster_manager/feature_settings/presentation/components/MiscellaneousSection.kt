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
import androidx.compose.ui.platform.LocalContext
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
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Black
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicBlack
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGray50
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicNavyBlue
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.Constants.URL_PRIVACY_POLICY
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.isAppInstalledFromMyket
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.openAppPageInStore
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.openLinkInExternalBrowser
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.openLinkInInAppBrowser
import com.xeniac.warrantyroster_manager.feature_settings.presentation.utils.Constants

enum class MiscellaneousItems(
    @param:DrawableRes val iconId: Int,
    @param:StringRes val titleId: Int,
    val url: String?
) {
    Donate(
        iconId = R.drawable.ic_settings_donate,
        titleId = R.string.settings_miscellaneous_donate_title,
        url = Constants.URL_DONATE
    ),
    ImproveTranslations(
        iconId = R.drawable.ic_settings_improve_translations,
        titleId = R.string.settings_miscellaneous_improve_translations_title,
        url = Constants.URL_CROWDIN
    ),
    RateUs(
        iconId = R.drawable.ic_settings_rate_us,
        titleId = R.string.settings_miscellaneous_rate_us_title,
        url = null
    ),
    PrivacyPolicy(
        iconId = R.drawable.ic_settings_privacy_policy,
        titleId = R.string.settings_miscellaneous_privacy_policy_title,
        url = URL_PRIVACY_POLICY
    )
}

@Composable
fun MiscellaneousSection(
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.settings_miscellaneous_title),
    titleStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.dynamicNavyBlue
    )
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

        MiscellaneousCard()
    }
}

@Composable
private fun MiscellaneousCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    background: Color = MaterialTheme.colorScheme.surface,
    dividerThickness: Dp = 1.dp,
    dividerColor: Color = MaterialTheme.colorScheme.dynamicGray50
) {
    val context = LocalContext.current

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
        MiscellaneousItems.entries.forEachIndexed { index, miscellaneousItem ->
            val shouldShowItem = if (isAppInstalledFromMyket()) {
                miscellaneousItem != MiscellaneousItems.Donate
            } else true

            if (shouldShowItem) {
                MiscellaneousItem(
                    icon = painterResource(id = miscellaneousItem.iconId),
                    title = stringResource(id = miscellaneousItem.titleId),
                    onClick = {
                        when (miscellaneousItem) {
                            MiscellaneousItems.RateUs -> context.openAppPageInStore()
                            MiscellaneousItems.PrivacyPolicy -> {
                                miscellaneousItem.url?.let { url ->
                                    context.openLinkInInAppBrowser(urlString = url)
                                }
                            }
                            else -> {
                                miscellaneousItem.url?.let { url ->
                                    context.openLinkInExternalBrowser(urlString = url)
                                }
                            }
                        }
                    }
                )
            }

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
private fun MiscellaneousItem(
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
        MiscellaneousItemIcon(
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

        ExternalLinkIcon()
    }
}

@Composable
private fun MiscellaneousItemIcon(
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
private fun ExternalLinkIcon(
    modifier: Modifier = Modifier,
    icon: Painter = painterResource(id = R.drawable.ic_settings_external_link),
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