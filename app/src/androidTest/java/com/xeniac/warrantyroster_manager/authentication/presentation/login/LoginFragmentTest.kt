package com.xeniac.warrantyroster_manager.authentication.presentation.login

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.landing.TestLandingFragmentFactory
import com.xeniac.warrantyroster_manager.core.presentation.main.MainActivity
import com.xeniac.warrantyroster_manager.databinding.FragmentLoginBinding
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
class LoginFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var testFragmentFactory: TestLandingFragmentFactory

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentLoginBinding

    private val email = "email@test.com"
    private val password = "password"

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)
        navController.setGraph(R.navigation.nav_graph_auth)

        launchFragmentInHiltContainer<LoginFragment>(fragmentFactory = testFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding
        }
    }

    @Test
    fun clickOnEmailEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditEmail.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutEmail.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
            assertThat(tiLayoutPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
        }
    }

    @Test
    fun clickOnPasswordEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditPassword.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutEmail.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
        }
    }

    @Test
    fun clickOnEmailEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditEmail.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutEmail.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
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
    fun clickOnForgotPwBtn_navigatesToForgotPwFragment() {
        onView(withId(testBinding.btnForgotPw.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.forgotPwFragment)
    }

    @Test
    fun clickOnRegisterBtn_navigatesToRegisterFragment() {
        onView(withId(testBinding.btnRegister.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.registerFragment)
    }

    @Test
    fun pressImeActionOnPasswordEditTextWithBlankEmail_showsEmailError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText("")
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password),
            pressImeActionButton()
        )

        assertThat(testBinding.tiLayoutEmail.error).isNotNull()
    }

    @Test
    fun pressImeActionOnPasswordEditTextWithBlankPassword_showsPasswordError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(""),
            pressImeActionButton()
        )

        assertThat(testBinding.tiLayoutPassword.error).isNotNull()
    }

    @Test
    fun pressImeActionOnPasswordEditTextWithInvalidEmail_showsEmailError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText("invalid_email")
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password),
            pressImeActionButton()
        )

        assertThat(testBinding.tiLayoutEmail.error).isNotNull()
    }

    @Test
    fun pressImeActionOnPasswordEditTextWithInvalidCredentials_showsCredentialsErrorSnackbar() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText("wrong_password"),
            pressImeActionButton()
        )

        onView(withText(context.getString(R.string.login_error_credentials)))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun pressImeActionOnPasswordEditTextWithSuccessStatus_navigatesToMainActivity() {
        Intents.init()

        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password),
            pressImeActionButton()
        )

        Intents.intended(
            hasComponent(MainActivity::class.java.name)
        )

        Intents.release()
    }

    @Test
    fun clickOnLoginBtnWithBlankEmail_showsEmailError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText("")
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.btnLogin.id)).perform(
            scrollTo(),
            click()
        )

        assertThat(testBinding.tiLayoutEmail.error).isNotNull()
    }

    @Test
    fun clickOnLoginBtnWithBlankPassword_showsPasswordError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText("")
        )
        onView(withId(testBinding.btnLogin.id)).perform(
            scrollTo(),
            click()
        )

        assertThat(testBinding.tiLayoutPassword.error).isNotNull()
    }

    @Test
    fun clickOnLoginBtnWithInvalidEmail_showsEmailError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText("invalid_email")
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.btnLogin.id)).perform(
            scrollTo(),
            click()
        )

        assertThat(testBinding.tiLayoutEmail.error).isNotNull()
    }

    @Test
    fun clickOnLoginBtnWithInvalidCredentials_showsCredentialsErrorSnackbar() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText("wrong_password")
        )
        onView(withId(testBinding.btnLogin.id)).perform(
            scrollTo(),
            click()
        )

        onView(withText(context.getString(R.string.login_error_credentials)))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun clickOnLoginBtnWithSuccessStatus_navigatesToMainActivity() {
        Intents.init()

        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.btnLogin.id)).perform(
            scrollTo(),
            click()
        )

        Intents.intended(
            hasComponent(MainActivity::class.java.name)
        )

        Intents.release()
    }
}