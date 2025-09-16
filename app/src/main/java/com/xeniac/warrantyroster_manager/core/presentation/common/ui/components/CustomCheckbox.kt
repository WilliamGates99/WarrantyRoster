package com.xeniac.warrantyroster_manager.core.presentation.common.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicBlack
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGrayDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.addTestTag

@Composable
fun CustomCheckbox(
    isChecked: Boolean,
    text: String?,
    modifier: Modifier = Modifier,
    testTag: String? = null,
    shape: Shape = RoundedCornerShape(4.dp),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 4.dp,
        vertical = 4.dp
    ),
    colors: CheckboxColors = CheckboxDefaults.colors(
        checkedColor = MaterialTheme.colorScheme.primary,
        uncheckedColor = MaterialTheme.colorScheme.dynamicGrayDark,
        checkmarkColor = MaterialTheme.colorScheme.onPrimary
    ),
    textColor: Color = MaterialTheme.colorScheme.dynamicBlack,
    textFontSize: TextUnit = 12.sp,
    textLineHeight: TextUnit = 16.sp,
    textFontWeight: FontWeight = FontWeight.ExtraLight,
    textFontStyle: FontStyle = FontStyle.Normal,
    textLetterSpacing: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign = TextAlign.Unspecified,
    textDecoration: TextDecoration? = null,
    textStyle: TextStyle = LocalTextStyle.current.copy(
        color = textColor,
        fontSize = textFontSize,
        fontWeight = textFontWeight,
        fontStyle = textFontStyle,
        lineHeight = textLineHeight,
        letterSpacing = textLetterSpacing,
        textAlign = textAlign,
        textDecoration = textDecoration
    ),
    textOverflow: TextOverflow = TextOverflow.Ellipsis,
    textSoftWrap: Boolean = true,
    isLoading: Boolean = false,
    checkboxAndTextSpace: Dp = 4.dp,
    onCheckedChange: (isChecked: Boolean) -> Unit
) {
    val direction = LocalLayoutDirection.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(space = checkboxAndTextSpace),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(shape)
            .selectable(
                enabled = !isLoading,
                selected = isChecked,
                onClick = { onCheckedChange(!isChecked) },
                role = Role.Checkbox
            )
            .padding(contentPadding)
            .addTestTag(tag = testTag)
    ) {
        if (text != null) {
            Text(
                text = text,
                color = textColor,
                style = textStyle,
                overflow = textOverflow,
                softWrap = textSoftWrap,
                modifier = Modifier.animateContentSize(
                    alignment = when (direction) {
                        LayoutDirection.Ltr -> Alignment.CenterStart
                        LayoutDirection.Rtl -> Alignment.CenterEnd
                    }
                )
            )
        }

        Checkbox(
            checked = isChecked,
            colors = colors,
            onCheckedChange = null,
            modifier = Modifier
        )
    }
}