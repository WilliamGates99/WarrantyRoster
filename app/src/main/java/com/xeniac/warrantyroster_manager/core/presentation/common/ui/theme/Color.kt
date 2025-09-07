package com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.utils.dynamicColor

val BlueNotificationLight = Color(0xFF1C53F4)

// TODO: UPDATE SHIMMER COLORS
val ShimmerEffectFirstColor = Color(0xFF273446)
val ShimmerEffectSecondColor = Color(0xFF161E28)
val ShimmerEffectThirdColor = Color(0xFF191D22)

val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

val NavyBlue = Color(0xFF00144F)
val SkyBlue = Color(0xFFDEF4FD)
val Blue = Color(0xFF1C53F4)
val Green = Color(0xFF00BD62)
val Red = Color(0xFFFF3442)
val Orange = Color(0xFFFF9100)

// TODO: RENAME GRAY COLORS
val GrayLight = Color(0xFFF3F3F3)
val GrayMedium = Color(0xFFE9E9E9)
val GrayDark = Color(0xFF959595)
val GrayDarkest = Color(0xFF666666)
val GrayDarkest2 = Color(0xFF404040)

val NavyBlueDark = Color(0xFFDEF4FD)
val SkyBlueDark = Color(0xFF01040F)
val GrayLightDark = Color(0xFF1F1F1F)
val GrayMediumDark = Color(0xFF2E2E2E)
val GrayDarkDark = Color(0xFF808080)
val GrayDarkestDark = Color(0xFFA6A6A6)
val GrayDarkest2Dark = Color(0xFFCCCCCC)

val ColorScheme.dynamicBlack: Color
    @Composable get() = dynamicColor(
        lightColor = Black,
        darkColor = White
    )

val ColorScheme.dynamicNavyBlue: Color
    @Composable get() = dynamicColor(
        lightColor = NavyBlue,
        darkColor = NavyBlueDark
    )

val ColorScheme.dynamicSkyBlue: Color
    @Composable get() = dynamicColor(
        lightColor = SkyBlue,
        darkColor = SkyBlueDark
    )

val ColorScheme.dynamicGrayLight: Color
    @Composable get() = dynamicColor(
        lightColor = GrayLight,
        darkColor = GrayLightDark
    )

val ColorScheme.dynamicGrayMedium: Color
    @Composable get() = dynamicColor(
        lightColor = GrayMedium,
        darkColor = GrayMediumDark
    )

val ColorScheme.dynamicGrayDark: Color
    @Composable get() = dynamicColor(
        lightColor = GrayDark,
        darkColor = GrayDarkDark
    )

val ColorScheme.dynamicGrayDarkest: Color
    @Composable get() = dynamicColor(
        lightColor = GrayDarkest,
        darkColor = GrayDarkestDark
    )

val ColorScheme.dynamicGrayDarkest2: Color
    @Composable get() = dynamicColor(
        lightColor = GrayDarkest2,
        darkColor = GrayDarkest2Dark
    )

val primaryLight = Blue
val onPrimaryLight = White
val primaryContainerLight = Blue
val onPrimaryContainerLight = White
val secondaryLight = Blue
val onSecondaryLight = White
val secondaryContainerLight = Blue
val onSecondaryContainerLight = White
val tertiaryLight = Color(0xFF003CC8)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFF1C53F4)
val onTertiaryContainerLight = Color(0xFFDDE1FF)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF93000A)
val backgroundLight = Color(0xFFFFFFFF)
val onBackgroundLight = Color(0xFF191B24)
val surfaceLight = Color(0xFFFFFFFF)
val onSurfaceLight = Color(0xFF191B24)
val surfaceVariantLight = Color(0xFFE0E1F5)
val onSurfaceVariantLight = Color(0xFF434656)
val outlineLight = Color(0xFF747687)
val outlineVariantLight = Color(0xFFC4C5D9)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF2E303A)
val inverseOnSurfaceLight = Color(0xFFF0EFFD)
val inversePrimaryLight = Color(0xFFB8C4FF)
val surfaceDimLight = Color(0xFFD9D9E6)
val surfaceBrightLight = Color(0xFFFBF8FF)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF3F2FF)
val surfaceContainerLight = Color(0xFFEDEDFA)
val surfaceContainerHighLight = Color(0xFFE7E7F4)
val surfaceContainerHighestLight = Color(0xFFE2E1EE)

val primaryLightMediumContrast = Color(0xFF002A92)
val onPrimaryLightMediumContrast = White
val primaryContainerLightMediumContrast = Color(0xFF1C53F4)
val onPrimaryContainerLightMediumContrast = White
val secondaryLightMediumContrast = Color(0xFF002A92)
val onSecondaryLightMediumContrast = White
val secondaryContainerLightMediumContrast = Color(0xFF1C53F4)
val onSecondaryContainerLightMediumContrast = White
val tertiaryLightMediumContrast = Color(0xFF002A92)
val onTertiaryLightMediumContrast = Color(0xFFFFFFFF)
val tertiaryContainerLightMediumContrast = Color(0xFF1C53F4)
val onTertiaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val errorLightMediumContrast = Color(0xFF740006)
val onErrorLightMediumContrast = Color(0xFFFFFFFF)
val errorContainerLightMediumContrast = Color(0xFFCF2C27)
val onErrorContainerLightMediumContrast = Color(0xFFFFFFFF)
val backgroundLightMediumContrast = Color(0xFFFFFFFF)
val onBackgroundLightMediumContrast = Color(0xFF191B24)
val surfaceLightMediumContrast = Color(0xFFFBF8FF)
val onSurfaceLightMediumContrast = Color(0xFF0F1119)
val surfaceVariantLightMediumContrast = Color(0xFFE0E1F5)
val onSurfaceVariantLightMediumContrast = Color(0xFF333545)
val outlineLightMediumContrast = Color(0xFF4F5262)
val outlineVariantLightMediumContrast = Color(0xFF6A6C7D)
val scrimLightMediumContrast = Color(0xFF000000)
val inverseSurfaceLightMediumContrast = Color(0xFF2E303A)
val inverseOnSurfaceLightMediumContrast = Color(0xFFF0EFFD)
val inversePrimaryLightMediumContrast = Color(0xFFB8C4FF)
val surfaceDimLightMediumContrast = Color(0xFFC5C5D2)
val surfaceBrightLightMediumContrast = Color(0xFFFBF8FF)
val surfaceContainerLowestLightMediumContrast = Color(0xFFFFFFFF)
val surfaceContainerLowLightMediumContrast = Color(0xFFF3F2FF)
val surfaceContainerLightMediumContrast = Color(0xFFE7E7F4)
val surfaceContainerHighLightMediumContrast = Color(0xFFDCDCE9)
val surfaceContainerHighestLightMediumContrast = Color(0xFFD1D0DD)

val primaryLightHighContrast = Color(0xFF00217A)
val onPrimaryLightHighContrast = White
val primaryContainerLightHighContrast = Color(0xFF0039BF)
val onPrimaryContainerLightHighContrast = White
val secondaryLightHighContrast = Color(0xFF00217A)
val onSecondaryLightHighContrast = White
val secondaryContainerLightHighContrast = Color(0xFF0039BF)
val onSecondaryContainerLightHighContrast = White
val tertiaryLightHighContrast = Color(0xFF00217A)
val onTertiaryLightHighContrast = Color(0xFFFFFFFF)
val tertiaryContainerLightHighContrast = Color(0xFF0039BF)
val onTertiaryContainerLightHighContrast = Color(0xFFFFFFFF)
val errorLightHighContrast = Color(0xFF600004)
val onErrorLightHighContrast = Color(0xFFFFFFFF)
val errorContainerLightHighContrast = Color(0xFF98000A)
val onErrorContainerLightHighContrast = Color(0xFFFFFFFF)
val backgroundLightHighContrast = Color(0xFFFFFFFF)
val onBackgroundLightHighContrast = Color(0xFF191B24)
val surfaceLightHighContrast = Color(0xFFFBF8FF)
val onSurfaceLightHighContrast = Color(0xFF000000)
val surfaceVariantLightHighContrast = Color(0xFFE0E1F5)
val onSurfaceVariantLightHighContrast = Color(0xFF000000)
val outlineLightHighContrast = Color(0xFF282B3A)
val outlineVariantLightHighContrast = Color(0xFF464858)
val scrimLightHighContrast = Color(0xFF000000)
val inverseSurfaceLightHighContrast = Color(0xFF2E303A)
val inverseOnSurfaceLightHighContrast = Color(0xFFFFFFFF)
val inversePrimaryLightHighContrast = Color(0xFFB8C4FF)
val surfaceDimLightHighContrast = Color(0xFFB8B8C4)
val surfaceBrightLightHighContrast = Color(0xFFFBF8FF)
val surfaceContainerLowestLightHighContrast = Color(0xFFFFFFFF)
val surfaceContainerLowLightHighContrast = Color(0xFFF0EFFD)
val surfaceContainerLightHighContrast = Color(0xFFE2E1EE)
val surfaceContainerHighLightHighContrast = Color(0xFFD3D3E0)
val surfaceContainerHighestLightHighContrast = Color(0xFFC5C5D2)

val primaryDark = Blue
val onPrimaryDark = White
val primaryContainerDark = Blue
val onPrimaryContainerDark = White
val secondaryDark = Blue
val onSecondaryDark = White
val secondaryContainerDark = Blue
val onSecondaryContainerDark = White
val tertiaryDark = Color(0xFFB8C4FF)
val onTertiaryDark = Color(0xFF002584)
val tertiaryContainerDark = Color(0xFF1C53F4)
val onTertiaryContainerDark = Color(0xFFDDE1FF)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF101010)
val onBackgroundDark = Color(0xFFE2E1EE)
val surfaceDark = Color(0xFF191919)
val onSurfaceDark = Color(0xFFE2E1EE)
val surfaceVariantDark = Color(0xFF434656)
val onSurfaceVariantDark = Color(0xFFC4C5D9)
val outlineDark = Color(0xFF8D90A2)
val outlineVariantDark = Color(0xFF434656)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE2E1EE)
val inverseOnSurfaceDark = Color(0xFF2E303A)
val inversePrimaryDark = Color(0xFF0A4BED)
val surfaceDimDark = Color(0xFF11131C)
val surfaceBrightDark = Color(0xFF373943)
val surfaceContainerLowestDark = Color(0xFF0C0E16)
val surfaceContainerLowDark = Color(0xFF191B24)
val surfaceContainerDark = Color(0xFF1D1F28)
val surfaceContainerHighDark = Color(0xFF282933)
val surfaceContainerHighestDark = Color(0xFF33343E)

val primaryDarkMediumContrast = Blue
val onPrimaryDarkMediumContrast = White
val primaryContainerDarkMediumContrast = Blue
val onPrimaryContainerDarkMediumContrast = White
val secondaryDarkMediumContrast = Blue
val onSecondaryDarkMediumContrast = White
val secondaryContainerDarkMediumContrast = Blue
val onSecondaryContainerDarkMediumContrast = White
val tertiaryDarkMediumContrast = Color(0xFFD5DAFF)
val onTertiaryDarkMediumContrast = Color(0xFF001C6B)
val tertiaryContainerDarkMediumContrast = Color(0xFF6B89FF)
val onTertiaryContainerDarkMediumContrast = Color(0xFF000000)
val errorDarkMediumContrast = Color(0xFFFFD2CC)
val onErrorDarkMediumContrast = Color(0xFF540003)
val errorContainerDarkMediumContrast = Color(0xFFFF5449)
val onErrorContainerDarkMediumContrast = Color(0xFF000000)
val backgroundDarkMediumContrast = Color(0xFF101010)
val onBackgroundDarkMediumContrast = Color(0xFFE2E1EE)
val surfaceDarkMediumContrast = Color(0xFF11131C)
val onSurfaceDarkMediumContrast = Color(0xFFFFFFFF)
val surfaceVariantDarkMediumContrast = Color(0xFF434656)
val onSurfaceVariantDarkMediumContrast = Color(0xFFDADBEF)
val outlineDarkMediumContrast = Color(0xFFAFB1C4)
val outlineVariantDarkMediumContrast = Color(0xFF8D8FA1)
val scrimDarkMediumContrast = Color(0xFF000000)
val inverseSurfaceDarkMediumContrast = Color(0xFFE2E1EE)
val inverseOnSurfaceDarkMediumContrast = Color(0xFF282933)
val inversePrimaryDarkMediumContrast = Color(0xFF0038BC)
val surfaceDimDarkMediumContrast = Color(0xFF11131C)
val surfaceBrightDarkMediumContrast = Color(0xFF42444E)
val surfaceContainerLowestDarkMediumContrast = Color(0xFF06070F)
val surfaceContainerLowDarkMediumContrast = Color(0xFF1B1D26)
val surfaceContainerDarkMediumContrast = Color(0xFF262731)
val surfaceContainerHighDarkMediumContrast = Color(0xFF30323C)
val surfaceContainerHighestDarkMediumContrast = Color(0xFF3C3D47)

val primaryDarkHighContrast = Blue
val onPrimaryDarkHighContrast = White
val primaryContainerDarkHighContrast = Blue
val onPrimaryContainerDarkHighContrast = White
val secondaryDarkHighContrast = Blue
val onSecondaryDarkHighContrast = White
val secondaryContainerDarkHighContrast = Blue
val onSecondaryContainerDarkHighContrast = White
val tertiaryDarkHighContrast = Color(0xFFEEEFFF)
val onTertiaryDarkHighContrast = Color(0xFF000000)
val tertiaryContainerDarkHighContrast = Color(0xFFB2C0FF)
val onTertiaryContainerDarkHighContrast = Color(0xFF00072D)
val errorDarkHighContrast = Color(0xFFFFECE9)
val onErrorDarkHighContrast = Color(0xFF000000)
val errorContainerDarkHighContrast = Color(0xFFFFAEA4)
val onErrorContainerDarkHighContrast = Color(0xFF220001)
val backgroundDarkHighContrast = Color(0xFF101010)
val onBackgroundDarkHighContrast = Color(0xFFE2E1EE)
val surfaceDarkHighContrast = Color(0xFF11131C)
val onSurfaceDarkHighContrast = Color(0xFFFFFFFF)
val surfaceVariantDarkHighContrast = Color(0xFF434656)
val onSurfaceVariantDarkHighContrast = Color(0xFFFFFFFF)
val outlineDarkHighContrast = Color(0xFFEEEFFF)
val outlineVariantDarkHighContrast = Color(0xFFC0C1D5)
val scrimDarkHighContrast = Color(0xFF000000)
val inverseSurfaceDarkHighContrast = Color(0xFFE2E1EE)
val inverseOnSurfaceDarkHighContrast = Color(0xFF000000)
val inversePrimaryDarkHighContrast = Color(0xFF0038BC)
val surfaceDimDarkHighContrast = Color(0xFF11131C)
val surfaceBrightDarkHighContrast = Color(0xFF4E4F5A)
val surfaceContainerLowestDarkHighContrast = Color(0xFF000000)
val surfaceContainerLowDarkHighContrast = Color(0xFF1D1F28)
val surfaceContainerDarkHighContrast = Color(0xFF2E303A)
val surfaceContainerHighDarkHighContrast = Color(0xFF393B45)
val surfaceContainerHighestDarkHighContrast = Color(0xFF454651)