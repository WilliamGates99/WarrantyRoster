package com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale

@Composable
fun getFontFamilyForLocale(): FontFamily {
    val appLocaleList = AppCompatDelegate.getApplicationLocales()
    val currentAppLocale = if (appLocaleList.isEmpty) {
        AppLocale.DEFAULT
    } else {
        val localeString = appLocaleList[0].toString()
        when (localeString) {
            AppLocale.ENGLISH_US.localeString -> AppLocale.ENGLISH_US
            AppLocale.ENGLISH_GB.localeString -> AppLocale.ENGLISH_GB
            AppLocale.FARSI_IR.localeString -> AppLocale.FARSI_IR
            else -> AppLocale.DEFAULT
        }
    }

    return remember(key1 = currentAppLocale) {
        when (currentAppLocale) {
            AppLocale.DEFAULT, AppLocale.ENGLISH_GB, AppLocale.ENGLISH_US -> FontFamily(
                listOf(
                    Font(
                        resId = R.font.nunito_thin,
                        weight = FontWeight.Thin
                    ),
                    Font(
                        resId = R.font.nunito_extra_light,
                        weight = FontWeight.ExtraLight
                    ),
                    Font(
                        resId = R.font.nunito_light,
                        weight = FontWeight.Light
                    ),
                    Font(
                        resId = R.font.nunito_regular,
                        weight = FontWeight.Normal
                    ),
                    Font(
                        resId = R.font.nunito_medium,
                        weight = FontWeight.Medium
                    ),
                    Font(
                        resId = R.font.nunito_semi_bold,
                        weight = FontWeight.SemiBold
                    ),
                    Font(
                        resId = R.font.nunito_bold,
                        weight = FontWeight.Bold
                    ),
                    Font(
                        resId = R.font.nunito_extra_bold,
                        weight = FontWeight.ExtraBold
                    ),
                    Font(
                        resId = R.font.nunito_black,
                        weight = FontWeight.Black
                    )
                )
            )
            AppLocale.FARSI_IR -> FontFamily(
                listOf(
                    Font(
                        resId = R.font.vazirmatn_rd_fd_thin,
                        weight = FontWeight.Thin
                    ),
                    Font(
                        resId = R.font.vazirmatn_rd_fd_extra_light,
                        weight = FontWeight.ExtraLight
                    ),
                    Font(
                        resId = R.font.vazirmatn_rd_fd_light,
                        weight = FontWeight.Light
                    ),
                    Font(
                        resId = R.font.vazirmatn_rd_fd_regular,
                        weight = FontWeight.Normal
                    ),
                    Font(
                        resId = R.font.vazirmatn_rd_fd_medium,
                        weight = FontWeight.Medium
                    ),
                    Font(
                        resId = R.font.vazirmatn_rd_fd_semi_bold,
                        weight = FontWeight.SemiBold
                    ),
                    Font(
                        resId = R.font.vazirmatn_rd_fd_bold,
                        weight = FontWeight.Bold
                    ),
                    Font(
                        resId = R.font.vazirmatn_rd_fd_extra_bold,
                        weight = FontWeight.ExtraBold
                    ),
                    Font(
                        resId = R.font.vazirmatn_rd_fd_black,
                        weight = FontWeight.Black
                    )
                )
            )
        }
    }
}

@Composable
fun getTypography(): Typography {
    val defaultTypography = Typography()
    val futSaleFont = getFontFamilyForLocale()

    return remember(key1 = futSaleFont) {
        Typography(
            displayLarge = defaultTypography.displayLarge.copy(fontFamily = futSaleFont),
            displayMedium = defaultTypography.displayMedium.copy(fontFamily = futSaleFont),
            displaySmall = defaultTypography.displaySmall.copy(fontFamily = futSaleFont),
            headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = futSaleFont),
            headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = futSaleFont),
            headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = futSaleFont),
            titleLarge = defaultTypography.titleLarge.copy(fontFamily = futSaleFont),
            titleMedium = defaultTypography.titleMedium.copy(fontFamily = futSaleFont),
            titleSmall = defaultTypography.titleSmall.copy(fontFamily = futSaleFont),
            bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = futSaleFont),
            bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = futSaleFont),
            bodySmall = defaultTypography.bodySmall.copy(fontFamily = futSaleFont),
            labelLarge = defaultTypography.labelLarge.copy(fontFamily = futSaleFont),
            labelMedium = defaultTypography.labelMedium.copy(fontFamily = futSaleFont),
            labelSmall = defaultTypography.labelSmall.copy(fontFamily = futSaleFont)
        )
    }
}