package com.xeniac.warrantyroster_manager.settings.presentation.settings

import android.content.Context
import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.airbnb.lottie.LottieDrawable
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.BuildConfig
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.landing.LandingActivity
import com.xeniac.warrantyroster_manager.core.presentation.main.TestMainFragmentFactory
import com.xeniac.warrantyroster_manager.databinding.FragmentSettingsBinding
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.util.Constants.URL_CROWDIN
import com.xeniac.warrantyroster_manager.util.Constants.URL_DONATE
import com.xeniac.warrantyroster_manager.util.Constants.URL_PRIVACY_POLICY
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
class SettingsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var testFragmentFactory: TestMainFragmentFactory

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentSettingsBinding

    private val email = "email@test.com"
    private val isEmailVerified = false

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)

        launchFragmentInHiltContainer<SettingsFragment>(fragmentFactory = testFragmentFactory) {
            navController.setGraph(R.navigation.nav_graph_main)
            Navigation.setViewNavController(requireView(), navController)
            navController.setCurrentDestination(R.id.settingsFragment)

            testBinding = binding
        }
    }

    @Test
    fun getUserInfo_setsAccountDetails() {
        onView(withId(testBinding.tvAccountEmail.id)).check(matches(isDisplayed()))
        onView(withId(testBinding.btnAccountVerification.id)).check(matches(isDisplayed()))

        assertThat(testBinding.userEmail).isEqualTo(email)
        assertThat(testBinding.isUserVerified).isEqualTo(isEmailVerified)
        assertThat(testBinding.lavAccountVerification.speed).isEqualTo(1.00f)
        assertThat(testBinding.lavAccountVerification.repeatCount).isEqualTo(LottieDrawable.INFINITE)
    }

    @Test
    fun getCurrentAppLocaleUiText_setsCurrentLanguageTextToEnglishUs() {
        onView(withId(testBinding.tvSettingsLanguageCurrent.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        assertThat(testBinding.tvSettingsLanguageCurrent.text).isEqualTo(
            context.getString(R.string.settings_text_settings_language_english_us)
        )
    }

    @Test
    fun getCurrentAppThemeUiText_setsCurrentThemeTextToSystemDefault() {
        onView(withId(testBinding.tvSettingsThemeCurrent.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        assertThat(testBinding.tvSettingsThemeCurrent.text).isEqualTo(
            context.getString(R.string.settings_text_settings_theme_default)
        )
    }

    @Test
    fun clickOnVerifyBtn_showsSelectLanguageDialog() {
        onView(withId(testBinding.btnAccountVerification.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText(context.getString(R.string.settings_dialog_message_verification_email_sent)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickOnLinkedAccountsBtn_navigatesToLinkedAccountsFragment() {
        onView(withId(testBinding.clAccountLinkedAccounts.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.linkedAccountsFragment)
    }

    @Test
    fun clickOnChangeEmailBtn_navigatesToChangeEmailFragment() {
        onView(withId(testBinding.clAccountChangeEmail.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.changeEmailFragment)
    }

    @Test
    fun clickOnChangePasswordBtn_navigatesToChangePasswordFragment() {
        onView(withId(testBinding.clAccountChangePassword.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.changePasswordFragment)
    }

    @Test
    fun clickOnLanguageBtn_showsSelectLanguageDialog() {
        onView(withId(testBinding.clSettingsLanguage.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText(context.getString(R.string.settings_dialog_title_language)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickOnThemeBtn_showsSelectThemeDialog() {
        onView(withId(testBinding.clSettingsTheme.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText(context.getString(R.string.settings_dialog_title_theme)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickOnDonateBtn_opensDonateUrlInBrowser() {
        Intents.init()

        onView(withId(testBinding.clSettingsDonate.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        Intents.intended(
            CoreMatchers.allOf(
                IntentMatchers.hasAction(Intent.ACTION_VIEW),
                IntentMatchers.hasData(URL_DONATE)
            )
        )

        Intents.release()
    }

    @Test
    fun clickOnImproveTranslationsBtn_opensCrowdinUrlInBrowser() {
        Intents.init()

        onView(withId(testBinding.clSettingsImproveTranslations.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        Intents.intended(
            CoreMatchers.allOf(
                IntentMatchers.hasAction(Intent.ACTION_VIEW),
                IntentMatchers.hasData(URL_CROWDIN)
            )
        )

        Intents.release()
    }

    @Test
    fun clickOnRateUsBtn_opensAppStoreUrlInBrowser() {
        Intents.init()

        onView(withId(testBinding.clSettingsRateUs.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        Intents.intended(
            CoreMatchers.allOf(
                IntentMatchers.hasAction(Intent.ACTION_VIEW),
                IntentMatchers.hasData(BuildConfig.URL_APP_STORE)
            )
        )

        Intents.release()
    }

    @Test
    fun clickOnPrivacyPolicyBtn_opensPrivacyPolicyUrlInBrowser() {
        Intents.init()

        onView(withId(testBinding.clSettingsPrivacyPolicy.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        Intents.intended(
            CoreMatchers.allOf(
                IntentMatchers.hasAction(Intent.ACTION_VIEW),
                IntentMatchers.hasData(URL_PRIVACY_POLICY)
            )
        )

        Intents.release()
    }

    @Test
    fun clickOnLogoutBtn_navigatesToLandingActivity() {
        Intents.init()

        onView(withId(testBinding.btnLogout.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        Intents.intended(
            hasComponent(LandingActivity::class.java.name)
        )

        Intents.release()
    }
}