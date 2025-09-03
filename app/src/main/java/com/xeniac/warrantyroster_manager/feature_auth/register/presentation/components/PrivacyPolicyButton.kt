package com.xeniac.warrantyroster_manager.feature_auth.register.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.GrayDarkDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.GrayDarkLight
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.Constants
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.openLinkInInAppBrowser

@Composable
fun PrivacyPolicyButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    outerPadding: PaddingValues = PaddingValues(all = 4.dp),
    contentPadding: PaddingValues = PaddingValues(all = 4.dp),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Light,
        color = if (isSystemInDarkTheme()) GrayDarkDark else GrayDarkLight
    ),
    primaryColorHex: String = MaterialTheme.colorScheme.primary.toArgb()
        .toHexString(HexFormat.UpperCase)
        .removeRange(0, 2)
) {
    val context = LocalContext.current

    Text(
        text = AnnotatedString.fromHtml(
            htmlString = stringResource(
                id = R.string.register_btn_privacy_policy,
                primaryColorHex
            )
        ),
        style = textStyle,
        modifier = modifier
            .padding(outerPadding)
            .clip(shape)
            .clickable(
                role = Role.Button,
                onClick = {
                    context.openLinkInInAppBrowser(urlString = Constants.URL_PRIVACY_POLICY)
                },
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = textStyle.color)
            )
            .padding(contentPadding)
    )
}