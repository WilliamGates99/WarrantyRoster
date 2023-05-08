package com.xeniac.warrantyroster_manager.settings.presentation.change_password

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
import com.xeniac.warrantyroster_manager.databinding.FragmentChangePasswordBinding
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
class ChangePasswordFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var testFragmentFactory: TestMainFragmentFactory

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentChangePasswordBinding

    private val currentPassword = "password"
    private val newPassword = "new_password"

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)

        navController.setGraph(R.navigation.nav_graph_main)
        navController.setCurrentDestination(R.id.settingsFragment)
        navController.navigate(SettingsFragmentDirections.actionSettingsFragmentToChangePasswordFragment())

        launchFragmentInHiltContainer<ChangePasswordFragment>(fragmentFactory = testFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding
        }
    }

    @Test
    fun clickOnCurrentPasswordEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutCurrentPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
            assertThat(tiLayoutNewPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutConfirmNewPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
        }
    }

    @Test
    fun clickOnNewPasswordEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditNewPassword.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutCurrentPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutNewPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
            assertThat(tiLayoutConfirmNewPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
        }
    }

    @Test
    fun clickOnRetypePasswordEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditConfirmNewPassword.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutCurrentPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutNewPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutConfirmNewPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
        }
    }

    @Test
    fun clickOnCurrentPasswordEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutCurrentPassword.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun clickOnNewPasswordEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditNewPassword.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutNewPassword.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun clickOnRetypePasswordEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditConfirmNewPassword.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutConfirmNewPassword.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
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
    fun pressImeActionOnRetypePasswordEditTextWithBlankCurrentPassword_showsCurrentPasswordError() {
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id)).perform(
                scrollTo(),
                replaceText("")
            )
            onView(withId(tiEditNewPassword.id)).perform(
                scrollTo(),
                replaceText(newPassword)
            )
            onView(withId(tiEditConfirmNewPassword.id)).perform(
                scrollTo(),
                replaceText(newPassword),
                pressImeActionButton()
            )

            assertThat(tiLayoutCurrentPassword.error).isNotNull()
        }
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithBlankNewPassword_showsNewPasswordError() {
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id)).perform(
                scrollTo(),
                replaceText(currentPassword)
            )
            onView(withId(tiEditNewPassword.id)).perform(
                scrollTo(),
                replaceText("")
            )
            onView(withId(tiEditConfirmNewPassword.id)).perform(
                scrollTo(),
                replaceText(newPassword),
                pressImeActionButton()
            )

            assertThat(tiLayoutNewPassword.error).isNotNull()
        }
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithBlankConfirmNewPassword_showsConfirmNewPasswordError() {
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id)).perform(
                scrollTo(),
                replaceText(currentPassword)
            )
            onView(withId(tiEditNewPassword.id)).perform(
                scrollTo(),
                replaceText(newPassword)
            )
            onView(withId(tiEditConfirmNewPassword.id)).perform(
                scrollTo(),
                replaceText(""),
                pressImeActionButton()
            )

            assertThat(tiLayoutConfirmNewPassword.error).isNotNull()
        }
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithShortNewPassword_showsNewPasswordError() {
        val shortNewPassword = "1234"
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id)).perform(
                scrollTo(),
                replaceText(currentPassword)
            )
            onView(withId(tiEditNewPassword.id)).perform(
                scrollTo(),
                replaceText(shortNewPassword)
            )
            onView(withId(tiEditConfirmNewPassword.id)).perform(
                scrollTo(),
                replaceText(shortNewPassword),
                pressImeActionButton()
            )

            assertThat(tiLayoutNewPassword.error).isNotNull()
        }
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithNotMatchingNewPasswords_showsConfirmNewPasswordError() {
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id)).perform(
                scrollTo(),
                replaceText(currentPassword)
            )
            onView(withId(tiEditNewPassword.id)).perform(
                scrollTo(),
                replaceText(newPassword)
            )
            onView(withId(tiEditConfirmNewPassword.id)).perform(
                scrollTo(),
                replaceText("another_password"),
                pressImeActionButton()
            )

            assertThat(tiLayoutConfirmNewPassword.error).isNotNull()
        }
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithInvalidCredentials_showsCredentialsErrorSnackbar() {
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id)).perform(
                scrollTo(),
                replaceText("wrong_password")
            )
            onView(withId(tiEditNewPassword.id)).perform(
                scrollTo(),
                replaceText(newPassword)
            )
            onView(withId(tiEditConfirmNewPassword.id)).perform(
                scrollTo(),
                replaceText(newPassword),
                pressImeActionButton()
            )
        }

        onView(withText(context.getString(R.string.change_password_error_credentials)))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithSuccessStatus_showsSuccessMessageDialog() {
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id)).perform(
                scrollTo(),
                replaceText(currentPassword)
            )
            onView(withId(tiEditNewPassword.id)).perform(
                scrollTo(),
                replaceText(newPassword)
            )
            onView(withId(tiEditConfirmNewPassword.id)).perform(
                scrollTo(),
                replaceText(newPassword),
                pressImeActionButton()
            )
        }

        onView(withText(context.getString(R.string.change_password_dialog_message)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickOnChangePasswordBtnWithBlankCurrentPassword_showsCurrentPasswordError() {
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id)).perform(
                scrollTo(),
                replaceText("")
            )
            onView(withId(tiEditNewPassword.id)).perform(
                scrollTo(),
                replaceText(newPassword)
            )
            onView(withId(tiEditConfirmNewPassword.id)).perform(
                scrollTo(),
                replaceText(newPassword)
            )
            onView(withId(btnChangePassword.id)).perform(
                scrollTo(),
                click()
            )

            assertThat(tiLayoutCurrentPassword.error).isNotNull()
        }
    }

    @Test
    fun clickOnChangePasswordBtnWithBlankNewPassword_showsNewPasswordError() {
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id)).perform(
                scrollTo(),
                replaceText(currentPassword)
            )
            onView(withId(tiEditNewPassword.id)).perform(
                scrollTo(),
                replaceText("")
            )
            onView(withId(tiEditConfirmNewPassword.id)).perform(
                scrollTo(),
                replaceText(newPassword)
            )
            onView(withId(btnChangePassword.id)).perform(
                scrollTo(),
                click()
            )

            assertThat(tiLayoutNewPassword.error).isNotNull()
        }
    }

    @Test
    fun clickOnChangePasswordBtnWithBlankConfirmNewPassword_showsConfirmNewPasswordError() {
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id)).perform(
                scrollTo(),
                replaceText(currentPassword)
            )
            onView(withId(tiEditNewPassword.id)).perform(
                scrollTo(),
                replaceText(newPassword)
            )
            onView(withId(tiEditConfirmNewPassword.id)).perform(
                scrollTo(),
                replaceText("")
            )
            onView(withId(btnChangePassword.id)).perform(
                scrollTo(),
                click()
            )

            assertThat(tiLayoutConfirmNewPassword.error).isNotNull()
        }
    }

    @Test
    fun clickOnChangePasswordBtnWithShortNewPassword_showsNewPasswordError() {
        val shortNewPassword = "1234"
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id)).perform(
                scrollTo(),
                replaceText(currentPassword)
            )
            onView(withId(tiEditNewPassword.id)).perform(
                scrollTo(),
                replaceText(shortNewPassword)
            )
            onView(withId(tiEditConfirmNewPassword.id)).perform(
                scrollTo(),
                replaceText(shortNewPassword)
            )
            onView(withId(btnChangePassword.id)).perform(
                scrollTo(),
                click()
            )

            assertThat(tiLayoutNewPassword.error).isNotNull()
        }
    }

    @Test
    fun clickOnChangePasswordBtnWithNotMatchingNewPasswords_showsConfirmNewPasswordError() {
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id)).perform(
                scrollTo(),
                replaceText(currentPassword)
            )
            onView(withId(tiEditNewPassword.id)).perform(
                scrollTo(),
                replaceText(newPassword)
            )
            onView(withId(tiEditConfirmNewPassword.id)).perform(
                scrollTo(),
                replaceText("another_password")
            )
            onView(withId(btnChangePassword.id)).perform(
                scrollTo(),
                click()
            )

            assertThat(tiLayoutConfirmNewPassword.error).isNotNull()
        }
    }

    @Test
    fun clickOnChangePasswordBtnWithInvalidCredentials_showsCredentialsErrorSnackbar() {
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id)).perform(
                scrollTo(),
                replaceText("wrong_password")
            )
            onView(withId(tiEditNewPassword.id)).perform(
                scrollTo(),
                replaceText(newPassword)
            )
            onView(withId(tiEditConfirmNewPassword.id)).perform(
                scrollTo(),
                replaceText(newPassword)
            )
            onView(withId(btnChangePassword.id)).perform(
                scrollTo(),
                click()
            )
        }

        onView(withText(context.getString(R.string.change_password_error_credentials)))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun clickOnChangePasswordBtnWithSuccessStatus_showsSuccessMessageDialog() {
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id)).perform(
                scrollTo(),
                replaceText(currentPassword)
            )
            onView(withId(tiEditNewPassword.id)).perform(
                scrollTo(),
                replaceText(newPassword)
            )
            onView(withId(tiEditConfirmNewPassword.id)).perform(
                scrollTo(),
                replaceText(newPassword)
            )
            onView(withId(btnChangePassword.id)).perform(
                scrollTo(),
                click()
            )
        }

        onView(withText(context.getString(R.string.change_password_dialog_message)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }
}