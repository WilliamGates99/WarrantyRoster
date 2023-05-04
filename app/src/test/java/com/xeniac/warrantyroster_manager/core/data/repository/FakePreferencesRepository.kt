package com.xeniac.warrantyroster_manager.core.data.repository

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.util.Constants.LANGUAGE_DEFAULT_OR_EMPTY
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_DEFAULT_OR_EMPTY
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_PERSIAN_IRAN
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_TAG_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_TAG_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_TAG_PERSIAN_IRAN
import com.xeniac.warrantyroster_manager.util.Constants.THEME_INDEX_DARK
import com.xeniac.warrantyroster_manager.util.Constants.THEME_INDEX_DEFAULT
import com.xeniac.warrantyroster_manager.util.Constants.THEME_INDEX_LIGHT
import com.xeniac.warrantyroster_manager.util.UiText

class FakePreferencesRepository : PreferencesRepository {

    private var isUserLoggedIn = false
    private var currentAppLocaleIndex = LOCALE_INDEX_ENGLISH_UNITED_STATES
    private var currentAppThemeIndex = THEME_INDEX_DEFAULT
    private var rateAppDialogChoice = 0
    private var previousRequestTimeInMillis = 0L

    override fun getCurrentAppThemeIndexSynchronously(): Int = currentAppThemeIndex

    override fun isUserLoggedInSynchronously(): Boolean = isUserLoggedIn

    override suspend fun getCurrentAppLocaleIndex(): Int = currentAppLocaleIndex

    override suspend fun getCurrentAppLanguage(): String {
        return when (currentAppLocaleIndex) {
            LOCALE_INDEX_DEFAULT_OR_EMPTY -> LANGUAGE_DEFAULT_OR_EMPTY
            LOCALE_INDEX_ENGLISH_UNITED_STATES -> LOCALE_TAG_ENGLISH_UNITED_STATES
            LOCALE_INDEX_ENGLISH_GREAT_BRITAIN -> LOCALE_TAG_ENGLISH_GREAT_BRITAIN
            LOCALE_INDEX_PERSIAN_IRAN -> LOCALE_TAG_PERSIAN_IRAN
            else -> LANGUAGE_DEFAULT_OR_EMPTY
        }
    }

    override suspend fun getCurrentAppLocaleUiText(): UiText {
        return when (currentAppLocaleIndex) {
            LOCALE_INDEX_DEFAULT_OR_EMPTY -> UiText.StringResource(R.string.settings_text_settings_language_english_us)
            LOCALE_INDEX_ENGLISH_UNITED_STATES -> UiText.StringResource(R.string.settings_text_settings_language_english_us)
            LOCALE_INDEX_ENGLISH_GREAT_BRITAIN -> UiText.StringResource(R.string.settings_text_settings_language_english_gb)
            LOCALE_INDEX_PERSIAN_IRAN -> UiText.StringResource(R.string.settings_text_settings_language_persian_ir)
            else -> UiText.StringResource(R.string.settings_text_settings_language_english_us)
        }
    }

    override suspend fun getCurrentAppThemeIndex(): Int = currentAppThemeIndex

    override suspend fun getCurrentAppThemeUiText(): UiText {
        return when (currentAppThemeIndex) {
            THEME_INDEX_DEFAULT -> UiText.StringResource(R.string.settings_text_settings_theme_default)
            THEME_INDEX_LIGHT -> UiText.StringResource(R.string.settings_text_settings_theme_light)
            THEME_INDEX_DARK -> UiText.StringResource(R.string.settings_text_settings_theme_dark)
            else -> UiText.StringResource(R.string.settings_text_settings_theme_default)
        }
    }

    override suspend fun getRateAppDialogChoice(): Int = rateAppDialogChoice

    override suspend fun getPreviousRequestTimeInMillis(): Long = previousRequestTimeInMillis

    override fun getCategoryTitleMapKey(): String {
        return when (currentAppLocaleIndex) {
            LOCALE_INDEX_DEFAULT_OR_EMPTY -> LOCALE_TAG_ENGLISH_UNITED_STATES
            LOCALE_INDEX_ENGLISH_UNITED_STATES -> LOCALE_TAG_ENGLISH_UNITED_STATES
            LOCALE_INDEX_ENGLISH_GREAT_BRITAIN -> LOCALE_TAG_ENGLISH_GREAT_BRITAIN
            LOCALE_INDEX_PERSIAN_IRAN -> LOCALE_TAG_PERSIAN_IRAN
            else -> LOCALE_TAG_ENGLISH_UNITED_STATES
        }
    }

    override suspend fun isUserLoggedIn(isLoggedIn: Boolean) {
        isUserLoggedIn = isLoggedIn
    }

    override suspend fun setCurrentAppLocale(index: Int): Boolean {
        currentAppLocaleIndex = index
        return false
    }

    override suspend fun setCurrentAppTheme(index: Int) {
        currentAppThemeIndex = index
    }

    override suspend fun setRateAppDialogChoice(value: Int) {
        rateAppDialogChoice = value
    }

    override suspend fun setPreviousRequestTimeInMillis(timeInMillis: Long) {
        previousRequestTimeInMillis = timeInMillis
    }
}