package com.xeniac.warrantyroster_manager.settings.presentation.change_email

import android.content.Context
import androidx.appcompat.widget.AppCompatImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.main.TestMainFragmentFactory
import com.xeniac.warrantyroster_manager.databinding.FragmentChangeEmailBinding
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.settings.presentation.settings.SettingsFragmentDirections
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
class ChangeEmailFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var testFragmentFactory: TestMainFragmentFactory

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentChangeEmailBinding

    private val currentEmail = "email@test.com"
    private val password = "password"
    private val newEmail = "new_email@test.com"

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)

        navController.setGraph(R.navigation.nav_graph_main)
        navController.setCurrentDestination(R.id.settingsFragment)
        navController.navigate(SettingsFragmentDirections.actionSettingsFragmentToChangeEmailFragment())

        launchFragmentInHiltContainer<ChangeEmailFragment>(fragmentFactory = testFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding
        }
    }

    @Test
    fun clickOnPasswordEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditPassword.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
            assertThat(tiLayoutNewEmail.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
        }
    }

    @Test
    fun clickOnNewEmailEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditNewEmail.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutNewEmail.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
        }
    }

    @Test
    fun clickOnPasswordEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditPassword.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutPassword.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun clickOnNewEmailEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditNewEmail.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutNewEmail.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun pressBackButton_popsBackStack() {
        pressBackUnconditionally()

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.settingsFragment)
    }

    @Test
    fun clickOnNavigateUpBtn_popsBackStack() {
        onView(
            CoreMatchers.allOf(
                instanceOf(AppCompatImageButton::class.java),
                withParent(withId(testBinding.toolbar.id))
            )
        ).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.settingsFragment)
    }

    @Test
    fun pressImeActionOnNewEmailEditTextWithBlankPassword_showsPasswordError() {
        testBinding.apply {
            onView(withId(tiEditPassword.id)).perform(
                scrollTo(),
                replaceText("")
            )
            onView(withId(tiEditNewEmail.id)).perform(
                scrollTo(),
                replaceText(currentEmail),
                pressImeActionButton()
            )

            assertThat(tiLayoutPassword.error).isNotNull()
        }
    }

    @Test
    fun pressImeActionOnNewEmailEditTextWithBlankEmail_showsEmailError() {
        testBinding.apply {
            onView(withId(tiEditPassword.id)).perform(
                scrollTo(),
                replaceText(password)
            )
            onView(withId(tiEditNewEmail.id)).perform(
                scrollTo(),
                replaceText(""),
                pressImeActionButton()
            )

            assertThat(tiLayoutNewEmail.error).isNotNull()
        }
    }

    @Test
    fun pressImeActionOnNewEmailEditTextWithInvalidEmail_showsEmailError() {
        testBinding.apply {
            onView(withId(tiEditPassword.id)).perform(
                scrollTo(),
                replaceText(password)
            )
            onView(withId(tiEditNewEmail.id)).perform(
                scrollTo(),
                replaceText("invalid_email"),
                pressImeActionButton()
            )

            assertThat(tiLayoutNewEmail.error).isNotNull()
        }
    }

    @Test
    fun pressImeActionOnNewEmailEditTextWithSameEmail_showsEmailError() {
        testBinding.apply {
            onView(withId(tiEditPassword.id)).perform(
                scrollTo(),
                replaceText(password)
            )
            onView(withId(tiEditNewEmail.id)).perform(
                scrollTo(),
                replaceText(currentEmail),
                pressImeActionButton()
            )

            assertThat(tiLayoutNewEmail.error).isNotNull()
        }
    }

    @Test
    fun pressImeActionOnNewEmailEditTextWithInvalidCredentials_showsCredentialsErrorSnackbar() {
        testBinding.apply {
            onView(withId(tiEditPassword.id)).perform(
                scrollTo(),
                replaceText("wrong_password")
            )
            onView(withId(tiEditNewEmail.id)).perform(
                scrollTo(),
                replaceText(newEmail),
                pressImeActionButton()
            )
        }

        onView(withText(context.getString(R.string.change_email_error_credentials)))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun pressImeActionOnNewEmailEditTextWithSuccessStatus_showsSuccessMessageDialog() {
        testBinding.apply {
            onView(withId(tiEditPassword.id)).perform(
                scrollTo(),
                replaceText(password)
            )
            onView(withId(tiEditNewEmail.id)).perform(
                scrollTo(),
                replaceText(newEmail),
                pressImeActionButton()
            )
        }

        onView(withText(context.getString(R.string.change_email_dialog_message)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickOnChangeEmailBtnWithBlankPassword_showsPasswordError() {
        testBinding.apply {
            onView(withId(tiEditPassword.id)).perform(
                scrollTo(),
                replaceText("")
            )
            onView(withId(tiEditNewEmail.id)).perform(
                scrollTo(),
                replaceText(currentEmail)
            )
            onView(withId(btnChangeEmail.id)).perform(
                scrollTo(),
                click()
            )

            assertThat(tiLayoutPassword.error).isNotNull()
        }
    }

    @Test
    fun clickOnChangeEmailBtnWithBlankEmail_showsEmailError() {
        testBinding.apply {
            onView(withId(tiEditPassword.id)).perform(
                scrollTo(),
                replaceText(password)
            )
            onView(withId(tiEditNewEmail.id)).perform(
                scrollTo(),
                replaceText("")
            )
            onView(withId(btnChangeEmail.id)).perform(
                scrollTo(),
                click()
            )

            assertThat(tiLayoutNewEmail.error).isNotNull()
        }
    }

    @Test
    fun clickOnChangeEmailBtnWithInvalidEmail_showsEmailError() {
        testBinding.apply {
            onView(withId(tiEditPassword.id)).perform(
                scrollTo(),
                replaceText(password)
            )
            onView(withId(tiEditNewEmail.id)).perform(
                scrollTo(),
                replaceText("invalid_email")
            )
            onView(withId(btnChangeEmail.id)).perform(
                scrollTo(),
                click()
            )

            assertThat(tiLayoutNewEmail.error).isNotNull()
        }
    }

    @Test
    fun clickOnChangeEmailBtnWithSameEmail_showsEmailError() {
        testBinding.apply {
            onView(withId(tiEditPassword.id)).perform(
                scrollTo(),
                replaceText(password)
            )
            onView(withId(tiEditNewEmail.id)).perform(
                scrollTo(),
                replaceText(currentEmail)
            )
            onView(withId(btnChangeEmail.id)).perform(
                scrollTo(),
                click()
            )

            assertThat(tiLayoutNewEmail.error).isNotNull()
        }
    }

    @Test
    fun clickOnChangeEmailBtnWithInvalidCredentials_showsCredentialsErrorSnackbar() {
        testBinding.apply {
            onView(withId(tiEditPassword.id)).perform(
                scrollTo(),
                replaceText("wrong_password")
            )
            onView(withId(tiEditNewEmail.id)).perform(
                scrollTo(),
                replaceText(newEmail)
            )
            onView(withId(btnChangeEmail.id)).perform(
                scrollTo(),
                click()
            )
        }

        onView(withText(context.getString(R.string.change_email_error_credentials)))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun clickOnChangeEmailBtnWithSuccessStatus_showsSuccessMessageDialog() {
        testBinding.apply {
            onView(withId(tiEditPassword.id)).perform(
                scrollTo(),
                replaceText(password)
            )
            onView(withId(tiEditNewEmail.id)).perform(
                scrollTo(),
                replaceText(newEmail)
            )
            onView(withId(btnChangeEmail.id)).perform(
                scrollTo(),
                click()
            )
        }

        onView(withText(context.getString(R.string.change_email_dialog_message)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }
}