package com.xeniac.warrantyroster_manager.ui.fragments.landing

import android.content.Context
import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.databinding.FragmentRegisterBinding
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.ui.landing.fragments.LoginFragmentDirections
import com.xeniac.warrantyroster_manager.ui.viewmodels.RegisterViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.URL_PRIVACY_POLICY
import com.xeniac.warrantyroster_manager.utils.Status
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.allOf
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
            navController.setGraph(R.navigation.nav_graph_landing)
            Navigation.setViewNavController(requireView(), navController)
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())

            viewModel = testViewModel
            testBinding = binding
        }
    }

    @Test
    fun clickOnEmailEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditEmail.id)).perform(click())

            assertThat(tiLayoutEmail.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
            assertThat(tiLayoutPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutConfirmPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
        }
    }

    @Test
    fun clickOnPasswordEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditPassword.id)).perform(click())

            assertThat(tiLayoutEmail.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
            assertThat(tiLayoutConfirmPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
        }
    }

    @Test
    fun clickOnRetypePasswordEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditConfirmPassword.id)).perform(click())

            assertThat(tiLayoutEmail.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutConfirmPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
        }
    }

    @Test
    fun clickOnEmailEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditEmail.id)).perform(click())
            assertThat(tiLayoutEmail.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun clickOnPasswordEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditPassword.id)).perform(click())
            assertThat(tiLayoutPassword.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun clickOnRetypePasswordEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditConfirmPassword.id)).perform(click())
            assertThat(tiLayoutConfirmPassword.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithErrorStatus_returnsError() {
        testBinding.apply {
            onView(withId(tiEditEmail.id)).perform(replaceText("email"))
            onView(withId(tiEditPassword.id)).perform(replaceText("password"))
            onView(withId(tiEditConfirmPassword.id)).perform(replaceText("retype_password"))
            onView(withId(tiEditConfirmPassword.id)).perform(pressImeActionButton())
        }

        val responseEvent = testViewModel.registerLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithSuccessStatus_returnsSuccess() {
        val email = "email@test.com"
        val password = "password"

        testBinding.apply {
            onView(withId(tiEditEmail.id)).perform(replaceText(email))
            onView(withId(tiEditPassword.id)).perform(replaceText(password))
            onView(withId(tiEditConfirmPassword.id)).perform(replaceText(password))
            onView(withId(tiEditConfirmPassword.id)).perform(pressImeActionButton())
        }

        val responseEvent = testViewModel.registerLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun pressBack_popsBackStack() {
        pressBack()
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.loginFragment)
    }

    @Test
    fun clickOnAgreementBtn_opensLinkInBrowser() {
        Intents.init()

        onView(withId(testBinding.btnAgreement.id)).perform(click())
        intended(allOf(hasAction(Intent.ACTION_VIEW), hasData(URL_PRIVACY_POLICY)))

        Intents.release()
    }

    @Test
    fun clickOnLoginBtn_popsBackStack() {
        onView(withId(testBinding.btnLogin.id)).perform(click())
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.loginFragment)
    }

    @Test
    fun clickOnRegisterBtnWithErrorStatus_returnsError() {
        testBinding.apply {
            onView(withId(tiEditEmail.id)).perform(replaceText("email"))
            onView(withId(tiEditPassword.id)).perform(replaceText("password"))
            onView(withId(tiEditConfirmPassword.id)).perform(replaceText("retype_password"))
            onView(withId(btnRegister.id)).perform(click())
        }

        val responseEvent = testViewModel.registerLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun clickOnRegisterBtnWithSuccessStatus_returnsSuccess() {
        val email = "email@test.com"
        val password = "password"

        testBinding.apply {
            onView(withId(tiEditEmail.id)).perform(replaceText(email))
            onView(withId(tiEditPassword.id)).perform(replaceText(password))
            onView(withId(tiEditConfirmPassword.id)).perform(replaceText(password))
            onView(withId(btnRegister.id)).perform(click())
        }

        val responseEvent = testViewModel.registerLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }
}