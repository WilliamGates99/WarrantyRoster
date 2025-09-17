package com.xeniac.warrantyroster_manager.authentication.presentation.register

import android.content.Context
import android.content.Intent
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
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.authentication.presentation.login.LoginFragmentDirections
import com.xeniac.warrantyroster_manager.core.presentation.landing.TestLandingFragmentFactory
import com.xeniac.warrantyroster_manager.core.presentation.main.MainActivity
import com.xeniac.warrantyroster_manager.databinding.FragmentRegisterBinding
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.util.Constants.URL_PRIVACY_POLICY
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
class RegisterFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var testFragmentFactory: TestLandingFragmentFactory

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentRegisterBinding

    private var backgroundColor = 0
    private var grayLightColor = 0
    private var blueColor = 0

    private val email = "new_user@test.com"
    private val password = "password"
    private val existingEmail = "email@test.com"

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()

        backgroundColor = context.getColor(R.color.background)
        grayLightColor = context.getColor(R.color.grayLight)
        blueColor = context.getColor(R.color.blue)

        navController = TestNavHostController(context)
        navController.setGraph(R.navigation.nav_graph_auth)
        navController.navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())

        launchFragmentInHiltContainer<RegisterFragment>(fragmentFactory = testFragmentFactory) {
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

            assertThat(tiLayoutEmail.boxBackgroundColor).isEqualTo(backgroundColor)
            assertThat(tiLayoutPassword.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutConfirmPassword.boxBackgroundColor).isEqualTo(grayLightColor)
        }
    }

    @Test
    fun clickOnPasswordEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditPassword.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutEmail.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutPassword.boxBackgroundColor).isEqualTo(backgroundColor)
            assertThat(tiLayoutConfirmPassword.boxBackgroundColor).isEqualTo(grayLightColor)
        }
    }

    @Test
    fun clickOnRetypePasswordEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditConfirmPassword.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutEmail.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutPassword.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutConfirmPassword.boxBackgroundColor).isEqualTo(backgroundColor)
        }
    }

    @Test
    fun clickOnEmailEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditEmail.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutEmail.boxStrokeColor).isEqualTo(blueColor)
        }
    }

    @Test
    fun clickOnPasswordEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditPassword.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutPassword.boxStrokeColor).isEqualTo(blueColor)
        }
    }

    @Test
    fun clickOnConfirmPasswordEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditConfirmPassword.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutConfirmPassword.boxStrokeColor).isEqualTo(blueColor)
        }
    }

    @Test
    fun pressBack_popsBackStack() {
        pressBackUnconditionally()

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.loginFragment)
    }

    @Test
    fun clickOnAgreementBtn_opensLinkInBrowser() {
        Intents.init()

        onView(withId(testBinding.btnAgreement.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        intended(
            allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(URL_PRIVACY_POLICY)
            )
        )

        Intents.release()
    }

    @Test
    fun clickOnLoginBtn_popsBackStack() {
        onView(withId(testBinding.btnLogin.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.loginFragment)
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithBlankEmail_showsEmailError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText("")
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.tiEditConfirmPassword.id)).perform(
            scrollTo(),
            replaceText(password),
            pressImeActionButton()
        )

        assertThat(testBinding.tiLayoutEmail.error).isNotNull()
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithBlankPassword_showsPasswordError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText("")
        )
        onView(withId(testBinding.tiEditConfirmPassword.id)).perform(
            scrollTo(),
            replaceText(password),
            pressImeActionButton()
        )

        assertThat(testBinding.tiLayoutPassword.error).isNotNull()
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithBlankConfirmPassword_showsConfirmPasswordError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.tiEditConfirmPassword.id)).perform(
            scrollTo(),
            replaceText(""),
            pressImeActionButton()
        )

        assertThat(testBinding.tiLayoutConfirmPassword.error).isNotNull()
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithInvalidEmail_showsEmailError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText("invalid_email")
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.tiEditConfirmPassword.id)).perform(
            scrollTo(),
            replaceText(password),
            pressImeActionButton()
        )

        assertThat(testBinding.tiLayoutEmail.error).isNotNull()
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithShortPassword_showsPasswordError() {
        val shortPassword = "1234"

        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(shortPassword)
        )
        onView(withId(testBinding.tiEditConfirmPassword.id)).perform(
            scrollTo(),
            replaceText(shortPassword),
            pressImeActionButton()
        )

        assertThat(testBinding.tiLayoutPassword.error).isNotNull()
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithNotMatchingPasswords_showsConfirmPasswordError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.tiEditConfirmPassword.id)).perform(
            scrollTo(),
            replaceText("another_password"),
            pressImeActionButton()
        )

        assertThat(testBinding.tiLayoutConfirmPassword.error).isNotNull()
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithExistingEmail_showsAccountExistsErrorSnackbar() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(existingEmail)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.tiEditConfirmPassword.id)).perform(
            scrollTo(),
            replaceText(password),
            pressImeActionButton()
        )

        onView(withText(context.getString(R.string.register_error_account_with_same_email_exists)))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithSuccessStatus_navigatesToMainActivity() {
        Intents.init()

        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.tiEditConfirmPassword.id)).perform(
            scrollTo(),
            replaceText(password),
            pressImeActionButton()
        )

        intended(
            hasComponent(MainActivity::class.java.name)
        )

        Intents.release()
    }

    @Test
    fun clickOnRegisterBtnWithBlankEmail_showsEmailError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText("")
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.tiEditConfirmPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.btnRegister.id)).perform(
            scrollTo(),
            click()
        )

        assertThat(testBinding.tiLayoutEmail.error).isNotNull()
    }

    @Test
    fun clickOnRegisterBtnWithBlankPassword_showsPasswordError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText("")
        )
        onView(withId(testBinding.tiEditConfirmPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.btnRegister.id)).perform(
            scrollTo(),
            click()
        )

        assertThat(testBinding.tiLayoutPassword.error).isNotNull()
    }

    @Test
    fun clickOnRegisterBtnWithBlankConfirmPassword_showsConfirmPasswordError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.tiEditConfirmPassword.id)).perform(
            scrollTo(),
            replaceText("")
        )
        onView(withId(testBinding.btnRegister.id)).perform(
            scrollTo(),
            click()
        )

        assertThat(testBinding.tiLayoutConfirmPassword.error).isNotNull()
    }

    @Test
    fun clickOnRegisterBtnWithInvalidEmail_showsEmailError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText("invalid_email")
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.tiEditConfirmPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.btnRegister.id)).perform(
            scrollTo(),
            click()
        )

        assertThat(testBinding.tiLayoutEmail.error).isNotNull()
    }

    @Test
    fun clickOnRegisterBtnWithShortPassword_showsPasswordError() {
        val shortPassword = "1234"

        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(shortPassword)
        )
        onView(withId(testBinding.tiEditConfirmPassword.id)).perform(
            scrollTo(),
            replaceText(shortPassword)
        )
        onView(withId(testBinding.btnRegister.id)).perform(
            scrollTo(),
            click()
        )

        assertThat(testBinding.tiLayoutPassword.error).isNotNull()
    }

    @Test
    fun clickOnRegisterBtnWithNotMatchingPasswords_showsConfirmPasswordError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.tiEditConfirmPassword.id)).perform(
            scrollTo(),
            replaceText("another_password")
        )
        onView(withId(testBinding.btnRegister.id)).perform(
            scrollTo(),
            click()
        )

        assertThat(testBinding.tiLayoutConfirmPassword.error).isNotNull()
    }

    @Test
    fun clickOnRegisterBtnWithExistingEmail_showsAccountExistsErrorSnackbar() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(existingEmail)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.tiEditConfirmPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.btnRegister.id)).perform(
            scrollTo(),
            click()
        )

        onView(withText(context.getString(R.string.register_error_account_with_same_email_exists)))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun clickOnRegisterBtnWithSuccessStatus_navigatesToMainActivity() {
        Intents.init()

        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.tiEditConfirmPassword.id)).perform(
            scrollTo(),
            replaceText(password)
        )
        onView(withId(testBinding.btnRegister.id)).perform(
            scrollTo(),
            click()
        )

        intended(
            hasComponent(MainActivity::class.java.name)
        )

        Intents.release()
    }
}