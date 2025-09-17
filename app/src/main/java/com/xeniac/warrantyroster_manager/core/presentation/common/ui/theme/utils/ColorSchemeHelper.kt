package com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.utils

import android.app.UiModeManager
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.backgroundDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.backgroundDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.backgroundDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.backgroundLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.backgroundLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.backgroundLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.errorContainerDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.errorContainerDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.errorContainerDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.errorContainerLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.errorContainerLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.errorContainerLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.errorDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.errorDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.errorDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.errorLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.errorLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.errorLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.inverseOnSurfaceDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.inverseOnSurfaceDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.inverseOnSurfaceDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.inverseOnSurfaceLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.inverseOnSurfaceLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.inverseOnSurfaceLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.inversePrimaryDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.inversePrimaryDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.inversePrimaryDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.inversePrimaryLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.inversePrimaryLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.inversePrimaryLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.inverseSurfaceDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.inverseSurfaceDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.inverseSurfaceDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.inverseSurfaceLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.inverseSurfaceLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.inverseSurfaceLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onBackgroundDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onBackgroundDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onBackgroundDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onBackgroundLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onBackgroundLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onBackgroundLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onErrorContainerDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onErrorContainerDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onErrorContainerDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onErrorContainerLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onErrorContainerLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onErrorContainerLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onErrorDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onErrorDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onErrorDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onErrorLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onErrorLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onErrorLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onPrimaryContainerDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onPrimaryContainerDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onPrimaryContainerDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onPrimaryContainerLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onPrimaryContainerLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onPrimaryContainerLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onPrimaryDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onPrimaryDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onPrimaryDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onPrimaryLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onPrimaryLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onPrimaryLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSecondaryContainerDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSecondaryContainerDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSecondaryContainerDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSecondaryContainerLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSecondaryContainerLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSecondaryContainerLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSecondaryDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSecondaryDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSecondaryDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSecondaryLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSecondaryLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSecondaryLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSurfaceDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSurfaceDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSurfaceDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSurfaceLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSurfaceLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSurfaceLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSurfaceVariantDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSurfaceVariantDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSurfaceVariantDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSurfaceVariantLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSurfaceVariantLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onSurfaceVariantLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onTertiaryContainerDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onTertiaryContainerDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onTertiaryContainerDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onTertiaryContainerLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onTertiaryContainerLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onTertiaryContainerLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onTertiaryDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onTertiaryDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onTertiaryDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onTertiaryLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onTertiaryLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.onTertiaryLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.outlineDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.outlineDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.outlineDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.outlineLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.outlineLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.outlineLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.outlineVariantDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.outlineVariantDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.outlineVariantDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.outlineVariantLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.outlineVariantLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.outlineVariantLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.primaryContainerDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.primaryContainerDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.primaryContainerDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.primaryContainerLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.primaryContainerLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.primaryContainerLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.primaryDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.primaryDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.primaryDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.primaryLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.primaryLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.primaryLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.scrimDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.scrimDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.scrimDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.scrimLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.scrimLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.scrimLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.secondaryContainerDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.secondaryContainerDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.secondaryContainerDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.secondaryContainerLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.secondaryContainerLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.secondaryContainerLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.secondaryDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.secondaryDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.secondaryDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.secondaryLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.secondaryLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.secondaryLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceBrightDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceBrightDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceBrightDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceBrightLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceBrightLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceBrightLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerHighDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerHighDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerHighDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerHighLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerHighLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerHighLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerHighestDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerHighestDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerHighestDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerHighestLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerHighestLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerHighestLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerLowDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerLowDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerLowDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerLowLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerLowLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerLowLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerLowestDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerLowestDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerLowestDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerLowestLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerLowestLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceContainerLowestLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceDimDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceDimDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceDimDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceDimLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceDimLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceDimLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceVariantDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceVariantDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceVariantDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceVariantLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceVariantLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.surfaceVariantLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.tertiaryContainerDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.tertiaryContainerDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.tertiaryContainerDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.tertiaryContainerLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.tertiaryContainerLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.tertiaryContainerLightMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.tertiaryDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.tertiaryDarkHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.tertiaryDarkMediumContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.tertiaryLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.tertiaryLightHighContrast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.tertiaryLightMediumContrast

@Composable
fun dynamicColor(
    lightColor: Color,
    darkColor: Color
): Color = if (isSystemInDarkTheme()) darkColor else lightColor

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast
)

@Composable
fun getContrastAwareColorScheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    isDynamicColorEnabled: Boolean = false // Dynamic color is available on Android 12+
): ColorScheme {
    val context = LocalContext.current

    if (isDynamicColorEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        return when {
            isDarkTheme -> dynamicDarkColorScheme(context = context)
            else -> dynamicLightColorScheme(context = context)
        }
    }

    val isPreview = LocalInspectionMode.current
    return when {
        // UIModeManager is not yet supported in preview
        !isPreview && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            val uiModeManager = context.getSystemService(UiModeManager::class.java)
            val contrastLevel = uiModeManager.contrast

            when (contrastLevel) {
                in 0.0f..0.33f -> when { // Default Contrast
                    isDarkTheme -> darkScheme
                    else -> lightScheme
                }
                in 0.34f..0.66f -> when { // Medium Contrast
                    isDarkTheme -> mediumContrastDarkColorScheme
                    else -> mediumContrastLightColorScheme
                }
                in 0.67f..1.0f -> when { // High Contrast
                    isDarkTheme -> highContrastDarkColorScheme
                    else -> highContrastLightColorScheme
                }
                else -> when {
                    isDarkTheme -> darkScheme
                    else -> lightScheme
                }
            }
        }
        else -> when {
            isDarkTheme -> darkScheme
            else -> lightScheme
        }
    }
}