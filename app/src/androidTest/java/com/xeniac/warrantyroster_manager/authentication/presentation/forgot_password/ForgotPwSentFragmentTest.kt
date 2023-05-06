package com.xeniac.warrantyroster_manager.authentication.presentation.forgot_password

import android.content.Context
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.authentication.presentation.login.LoginFragmentDirections
import com.xeniac.warrantyroster_manager.core.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.databinding.FragmentForgotPwSentBinding
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.util.Resource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@HiltAndroidTest
class ForgotPwSentFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private var testArgs: Bundle? = null
    private lateinit var testBinding: FragmentForgotPwSentBinding

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var testViewModel: ForgotPwViewModel

    private val email = "email@test.com"

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)

        fakeUserRepository = FakeUserRepository()
        fakeUserRepository.addUser(email, "password")

        testViewModel = ForgotPwViewModel(
            fakeUserRepository,
            SavedStateHandle()
        )

        navController.setGraph(R.navigation.nav_graph_auth)
        navController.navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
        navController.navigate(
            ForgotPwFragmentDirections.actionForgotPasswordFragmentToForgotPwSentFragment(email)
        )
        testArgs = navController.backStack.last().arguments

        launchFragmentInHiltContainer<ForgotPwSentFragment>(fragmentArgs = testArgs) {
            Navigation.setViewNavController(requireView(), navController)

            viewModel = testViewModel
            testBinding = binding
        }
    }

    @Test
    fun pressBack_popsBackStack() {
        pressBackUnconditionally()

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.loginFragment)
    }

    @Test
    fun clickOnReturnBtn_popsBackStack() {
        onView(withId(testBinding.btnReturn.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.loginFragment)
    }

    @Test
    fun clickOnResendBtnWithRemainingTimer_returnsError() {
        testViewModel.previousSentEmail = email
        testViewModel.timerMillisUntilFinished = 10 * 1000L // 10 Seconds

        onView(withId(testBinding.btnResend.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        val responseEvent = testViewModel.sendResetPasswordEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun clickOnResendBtnWithSuccessStatus_returnsSuccess() {
        onView(withId(testBinding.btnResend.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        val responseEvent = testViewModel.sendResetPasswordEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun clickOnResendBtnWithErrorStatus_hidesTimer() {
        testViewModel.previousSentEmail = email
        testViewModel.timerMillisUntilFinished = 10 * 1000L // 10 Seconds

        onView(withId(testBinding.btnResend.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(testBinding.tvTimer.id)).check(matches(not(isDisplayed())))
        onView(withText(context.getString(R.string.forgot_pw_sent_btn_resend)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickOnResendBtnWithSuccessStatus_startsTimer() {
        onView(withId(testBinding.btnResend.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        Thread.sleep(2000)

        onView(withId(testBinding.tvTimer.id)).check(matches(isDisplayed()))
        onView(withText(context.getString(R.string.forgot_pw_sent_btn_resend)))
            .check(matches(not(isDisplayed())))
    }
}