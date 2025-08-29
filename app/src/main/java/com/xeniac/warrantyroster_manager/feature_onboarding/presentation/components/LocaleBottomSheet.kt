package com.xeniac.warrantyroster_manager.feature_onboarding.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.SecureFlagPolicy
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.feature_onboarding.presentation.OnboardingAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocaleBottomSheet(
    isVisible: Boolean,
    currentAppLocale: AppLocale,
    modifier: Modifier = Modifier,
    sheetProperties: ModalBottomSheetProperties = ModalBottomSheetProperties(
        shouldDismissOnBackPress = true,
        securePolicy = SecureFlagPolicy.Inherit
    ),
    headline: String = stringResource(
        id = R.string.onboarding_bottom_sheet_title_locale
    ).uppercase(),
    headlineStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
    ),
    titleStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Start,
        color = MaterialTheme.colorScheme.onSurface
    ),
    onAction: (action: OnboardingAction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    if (isVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            properties = sheetProperties,
            onDismissRequest = { onAction(OnboardingAction.DismissLocaleBottomSheet) },
            modifier = modifier
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = headline,
                    style = headlineStyle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 12.dp)
                        .selectableGroup()
                ) {
                    AppLocale.entries.forEach { localeItem ->
                        val isSelected = currentAppLocale == localeItem

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 8.dp,
                                alignment = Alignment.Start
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = isSelected,
                                    role = Role.RadioButton,
                                    onClick = {
                                        onAction(OnboardingAction.DismissLocaleBottomSheet)
                                        onAction(
                                            OnboardingAction.SetCurrentAppLocale(
                                                newAppLocale = localeItem
                                            )
                                        )
                                    }
                                )
                                .padding(
                                    horizontal = 18.dp,
                                    vertical = 12.dp
                                )
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = null
                            )

                            Text(
                                text = stringResource(id = localeItem.fullTitleId),
                                style = titleStyle,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}