package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.R

@Composable
fun ReturnToLoginButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    contentPadding: PaddingValues = PaddingValues(all = 4.dp),
    text: String = stringResource(id = R.string.forgot_pw_btn_return),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 14.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary
    ),
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = textStyle,
        modifier = modifier
            .clip(shape)
            .clickable(
                role = Role.Button,
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = textStyle.color)
            )
            .padding(contentPadding)
    )
}