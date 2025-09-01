package com.xeniac.warrantyroster_manager.core.presentation.common.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Black
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.GrayDarkDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.GrayDarkLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.GrayLightDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.GrayLightLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.GrayMediumDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.GrayMediumLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Red
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.White
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.addTestTag
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.addTextFieldContentType
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText

@Composable
internal fun outlinedTextFieldColors(
    isDarkTheme: Boolean = isSystemInDarkTheme()
): TextFieldColors = OutlinedTextFieldDefaults.colors().copy(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = if (isDarkTheme) GrayLightDark else GrayLightLight,
    disabledContainerColor = if (isDarkTheme) GrayLightDark else GrayLightLight,
    errorContainerColor = if (isDarkTheme) GrayLightDark else GrayLightLight,
    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
    unfocusedIndicatorColor = if (isDarkTheme) GrayMediumDark else GrayMediumLight,
    disabledIndicatorColor = if (isDarkTheme) GrayMediumDark else GrayMediumLight.copy(alpha = 0.12f),
    errorIndicatorColor = Red,
    errorTrailingIconColor = MaterialTheme.colorScheme.error,
    focusedPlaceholderColor = if (isDarkTheme) GrayDarkDark else GrayDarkLight,
    unfocusedPlaceholderColor = if (isDarkTheme) GrayDarkDark else GrayDarkLight,
    disabledPlaceholderColor = if (isDarkTheme) GrayDarkDark else GrayDarkLight,
    errorPlaceholderColor = if (isDarkTheme) GrayDarkDark else GrayDarkLight,
    focusedSupportingTextColor = if (isDarkTheme) GrayDarkDark else GrayDarkLight,
    unfocusedSupportingTextColor = if (isDarkTheme) GrayDarkDark else GrayDarkLight,
    disabledSupportingTextColor = (if (isDarkTheme) GrayDarkDark else GrayDarkLight).copy(alpha = 0.38f),
    errorSupportingTextColor = Red
)

@Composable
fun CustomOutlinedTextField(
    isLoading: Boolean,
    value: TextFieldValue,
    imeAction: ImeAction,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    testTag: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isPasswordTextField: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    shape: Shape = RoundedCornerShape(12.dp),
    colors: TextFieldColors = outlinedTextFieldColors(),
    errorText: UiText? = null,
    title: String? = null,
    titleFontSize: TextUnit = 14.sp,
    titleLineHeight: TextUnit = 18.sp,
    titleFontWeight: FontWeight = FontWeight.Bold,
    titleColor: Color = if (isDarkTheme) White else Black,
    titleMaxLines: Int = Int.MAX_VALUE,
    titleStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = titleFontSize,
        lineHeight = titleLineHeight,
        fontWeight = titleFontWeight,
        color = titleColor
    ),
    textFontSize: TextUnit = 16.sp,
    textLineHeight: TextUnit = TextUnit.Unspecified,
    textFontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Unspecified,
    textDirection: TextDirection = TextDirection.Unspecified,
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = textFontSize,
        lineHeight = textLineHeight,
        fontWeight = textFontWeight,
        textAlign = textAlign,
        textDirection = textDirection
    ),
    placeholder: String? = null,
    placeholderFontSize: TextUnit = 16.sp,
    placeholderLineHeight: TextUnit = TextUnit.Unspecified,
    placeholderFontWeight: FontWeight = FontWeight.Normal,
    placeholderTextAlign: TextAlign = TextAlign.Unspecified,
    placeholderTextDirection: TextDirection = TextDirection.Unspecified,
    placeholderMaxLines: Int = 1,
    placeholderStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = placeholderFontSize,
        lineHeight = placeholderLineHeight,
        fontWeight = placeholderFontWeight,
        textAlign = placeholderTextAlign,
        textDirection = placeholderTextDirection
    ),
    leadingIcon: Painter? = null,
    leadingIconContentDescription: String? = null,
    leadingIconSize: Dp = 24.dp,
    trailingIcon: @Composable (() -> Unit)? = null,
    trailingIconPainter: Painter? = null,
    trailingIconContentDescription: String? = null,
    trailingIconSize: Dp = 24.dp,
    supportingText: String? = null,
    supportingTextFontSize: TextUnit = 12.sp,
    supportingTextLineHeight: TextUnit = 16.sp,
    supportingTextFontWeight: FontWeight = FontWeight.Normal,
    supportingTextStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = supportingTextFontSize,
        lineHeight = supportingTextLineHeight,
        fontWeight = supportingTextFontWeight
    ),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyboardCapitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    autoCorrectEnabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = imeAction,
        keyboardType = keyboardType,
        capitalization = keyboardCapitalization,
        autoCorrectEnabled = autoCorrectEnabled
    ),
    contentType: ContentType? = null,
    spaceBetweenTitleAndTextField: Dp = 4.dp,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    onValueChange: (newValue: TextFieldValue) -> Unit,
    keyboardAction: () -> Unit = {}
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(
            space = spaceBetweenTitleAndTextField,
            alignment = Alignment.Top
        ),
        modifier = modifier
    ) {
        title?.let { title ->
            Text(
                text = title,
                style = titleStyle,
                maxLines = titleMaxLines,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly && !isLoading,
            isError = errorText != null,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            shape = shape,
            colors = colors,
            textStyle = textStyle,
            label = null,
            prefix = prefix,
            suffix = suffix,
            placeholder = when {
                placeholder != null -> {
                    {
                        Text(
                            text = placeholder,
                            style = placeholderStyle,
                            maxLines = placeholderMaxLines,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                else -> null
            },
            supportingText = when {
                supportingText != null -> {
                    {
                        Text(
                            text = supportingText,
                            style = supportingTextStyle
                        )
                    }
                }
                errorText != null -> {
                    {
                        Text(
                            text = errorText.asString(),
                            style = supportingTextStyle
                        )
                    }
                }
                else -> null
            },
            leadingIcon = if (leadingIcon != null) {
                {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(leadingIconSize)
                    ) {
                        Icon(
                            painter = leadingIcon,
                            contentDescription = leadingIconContentDescription,
                            modifier = Modifier.size(leadingIconSize)
                        )
                    }
                }
            } else null,
            trailingIcon = when {
                isPasswordTextField -> {
                    {
                        IconButton(
                            onClick = { isPasswordVisible = !isPasswordVisible }
                        ) {
                            Icon(
                                painter = when {
                                    isPasswordVisible -> painterResource(id = R.drawable.ic_core_password_toggle_visible)
                                    else -> painterResource(id = R.drawable.ic_core_password_toggle_invisible)
                                },
                                contentDescription = when {
                                    isPasswordVisible -> stringResource(id = R.string.core_textfield_content_description_password_toggle_hide)
                                    else -> stringResource(id = R.string.core_textfield_content_description_password_toggle_show)
                                }
                            )
                        }
                    }
                }
                trailingIconPainter != null -> {
                    {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(trailingIconSize)
                        ) {
                            Icon(
                                painter = trailingIconPainter,
                                contentDescription = trailingIconContentDescription,
                                modifier = Modifier.size(trailingIconSize)
                            )
                        }
                    }
                }
                else -> trailingIcon
            },
            visualTransformation = when {
                isPasswordTextField && !isPasswordVisible -> PasswordVisualTransformation()
                else -> visualTransformation
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions {
                defaultKeyboardAction(imeAction)
                keyboardAction()
            },
            modifier = Modifier
                .fillMaxWidth()
                .addTextFieldContentType(contentType = contentType)
                .addTestTag(tag = testTag)
        )
    }
}