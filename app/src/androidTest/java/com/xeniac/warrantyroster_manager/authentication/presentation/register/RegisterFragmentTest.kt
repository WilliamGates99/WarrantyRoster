package com.xeniac.warrantyroster_manager.authentication.presentation.register

import android.content.Context
import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.authentication.presentation.login.LoginFragmentDirections
import com.xeniac.warrantyroster_manager.core.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.core.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.databinding.FragmentRegisterBinding
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.util.Constants.URL_PRIVACY_POLICY
import com.xeniac.warrantyroster_manager.util.Resource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@HiltAndroidTest
class RegisterFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentRegisterBinding

    private lateinit var testViewModel: RegisterViewModel

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)

        testViewModel = RegisterViewModel(
            FakeUserRepository(),
            FakePreferencesRepository()
        )

        launchFragmentInHiltContainer<RegisterFragment> {
            navController.setGraph(R.navigation.nav_graph_auth)
            Navigation.setViewNavController(requireView(), navController)
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())

            viewModel = testViewModel
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
            assertThat(tiLayoutConfirmPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
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
            assertThat(tiLayoutConfirmPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
        }
    }

    @Test
    fun clickOnRetypePasswordEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditConfirmPassword.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutEmail.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutConfirmPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
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
    fun clickOnConfirmPasswordEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditConfirmPassword.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutConfirmPassword.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
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
    fun pressImeActionOnRetypePasswordEditTextWithErrorStatus_returnsError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText("email")
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText("password")
        )
        onView(withId(testBinding.tiEditConfirmPassword.id)).perform(
            scrollTo(),
            replaceText("retype_password"),
            pressImeActionButton()
        )

        val responseEvent = testViewModel.registerWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithSuccessStatus_returnsSuccess() {
        val email = "email@test.com"
        val password = "password"

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

        val responseEvent = testViewModel.registerWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun clickOnRegisterBtnWithErrorStatus_returnsError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText("email")
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText("password")
        )
        onView(withId(testBinding.tiEditConfirmPassword.id)).perform(
            scrollTo(),
            replaceText("retype_password")
        )
        onView(withId(testBinding.btnRegister.id)).perform(
            scrollTo(),
            click()
        )

        val responseEvent = testViewModel.registerWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun clickOnRegisterBtnWithSuccessStatus_returnsSuccess() {
        val email = "email@test.com"
        val password = "password"

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

        val responseEvent = testViewModel.registerWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }
}