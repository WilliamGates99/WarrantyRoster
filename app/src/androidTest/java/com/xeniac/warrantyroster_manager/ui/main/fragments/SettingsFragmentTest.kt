package com.xeniac.warrantyroster_manager.ui.main.fragments

import android.content.Context
import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.databinding.FragmentSettingsBinding
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.ui.viewmodels.SettingsViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.URL_DONATE
import com.xeniac.warrantyroster_manager.utils.Constants.URL_PRIVACY_POLICY
import com.xeniac.warrantyroster_manager.utils.Status
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@HiltAndroidTest
class SettingsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentSettingsBinding

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var testViewModel: SettingsViewModel

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)

        fakeUserRepository = FakeUserRepository()
        fakeUserRepository.addUser("email@test.com", "password")

        testViewModel = SettingsViewModel(
            fakeUserRepository,
            FakePreferencesRepository()
        )

        launchFragmentInHiltContainer<SettingsFragment> {
            navController.setGraph(R.navigation.nav_graph_main)
            Navigation.setViewNavController(requireView(), navController)
            navController.setCurrentDestination(R.id.settingsFragment)

            viewModel = testViewModel
            testBinding = binding
        }
    }

    @Test
    fun getCurrentAppLocale_setsCurrentLanguageTextToEnglish() {
        assertThat(testBinding.currentLanguage).isEqualTo(
            context.getString(R.string.settings_text_settings_language_english_us)
        )
    }

    @Test
    fun getCurrentAppTheme_setsCurrentThemeTextToSystemDefault() {
        assertThat(testBinding.currentTheme).isEqualTo(
            context.getString(R.string.settings_text_settings_theme_default)
        )
    }

    @Test
    fun clickOnVerifyBtnWithErrorStatus_returnsError() {
        fakeUserRepository.setShouldReturnNetworkError(true)
        onView(withId(testBinding.btnAccountVerification.id)).perform(click())

        val responseEvent = testViewModel.sendVerificationEmailLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun clickOnVerifyBtnWithSuccessStatus_returnsSuccess() {
        onView(withId(testBinding.btnAccountVerification.id)).perform(click())

        val responseEvent = testViewModel.sendVerificationEmailLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun clickOnChangeEmailBtn_navigatesToChangeEmailFragment() {
        onView(withId(testBinding.clAccountChangeEmail.id)).perform(click())
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.changeEmailFragment)
    }

    @Test
    fun clickOnChangePasswordBtn_navigatesToChangePasswordFragment() {
        onView(withId(testBinding.clAccountChangePassword.id)).perform(click())
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.changePasswordFragment)
    }

    @Test
    fun clickOnLanguageBtn_showsSelectLanguageDialog() {
        onView(withId(testBinding.clSettingsLanguage.id)).perform(click())

        onView(withText(context.getString(R.string.settings_text_settings_language)))
            .inRoot(isDialog()).check(matches(isDisplayed()))
    }

    @Test
    fun clickOnThemeBtn_showsSelectThemeDialog() {
        onView(withId(testBinding.clSettingsTheme.id)).perform(click())

        onView(withText(context.getString(R.string.settings_text_settings_theme)))
            .inRoot(isDialog()).check(matches(isDisplayed()))
    }

    @Test
    fun clickOnDonateBtn_opensLinkInBrowser() {
        Intents.init()

        onView(withId(testBinding.clSettingsDonate.id)).perform(click())
        Intents.intended(
            CoreMatchers.allOf(
                IntentMatchers.hasAction(Intent.ACTION_VIEW),
                IntentMatchers.hasData(URL_DONATE)
            )
        )

        Intents.release()
    }

    @Test
    fun clickOnPrivacyPolicyBtn_opensLinkInBrowser() {
        Intents.init()

        onView(withId(testBinding.clSettingsPrivacyPolicy.id)).perform(click())
        Intents.intended(
            CoreMatchers.allOf(
                IntentMatchers.hasAction(Intent.ACTION_VIEW),
                IntentMatchers.hasData(URL_PRIVACY_POLICY)
            )
        )

        Intents.release()
    }

    @Test
    fun clickOnLogoutBtnWithSuccessStatus_returnsSuccess() {
        onView(withId(testBinding.btnLogout.id)).perform(click())

        val responseEvent = testViewModel.logoutLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }
}