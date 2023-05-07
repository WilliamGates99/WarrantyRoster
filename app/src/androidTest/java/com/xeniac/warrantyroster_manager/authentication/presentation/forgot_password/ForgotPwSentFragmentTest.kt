package com.xeniac.warrantyroster_manager.authentication.presentation.forgot_password

import android.content.Context
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.authentication.presentation.login.LoginFragmentDirections
import com.xeniac.warrantyroster_manager.core.presentation.landing.TestLandingFragmentFactory
import com.xeniac.warrantyroster_manager.databinding.FragmentForgotPwSentBinding
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
class ForgotPwSentFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var testFragmentFactory: TestLandingFragmentFactory

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private var testArgs: Bundle? = null
    private lateinit var testBinding: FragmentForgotPwSentBinding

    private val email = "email@test.com"

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)
        navController.setGraph(R.navigation.nav_graph_auth)
        navController.navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
        navController.navigate(
            ForgotPwFragmentDirections.actionForgotPasswordFragmentToForgotPwSentFragment(email)
        )
        testArgs = navController.backStack.last().arguments
    }

    @Test
    fun pressBack_popsBackStack() {
        launchFragmentInHiltContainer<ForgotPwSentFragment>(
            fragmentArgs = testArgs,
            fragmentFactory = testFragmentFactory
        ) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding
        }

        pressBackUnconditionally()

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.loginFragment)
    }

    @Test
    fun clickOnReturnBtn_popsBackStack() {
        launchFragmentInHiltContainer<ForgotPwSentFragment>(
            fragmentArgs = testArgs,
            fragmentFactory = testFragmentFactory
        ) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding
        }

        onView(withId(testBinding.btnReturn.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.loginFragment)
    }

    @Test
    fun clickOnResendBtnWithRemainingTimer_showsTimerIsNotZeroErrorSnackbar() {
        var millisUntilFinished = 0L

        launchFragmentInHiltContainer<ForgotPwSentFragment>(
            fragmentArgs = testArgs,
            fragmentFactory = testFragmentFactory
        ) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding

            viewModel!!.previousSentEmail = email
            viewModel!!.timerMillisUntilFinished = 10 * 1000L // 10 Seconds

            millisUntilFinished = timerMillisUntilFinished
        }

        onView(withId(testBinding.btnResend.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        val message = context.resources.getQuantityString(
            R.plurals.forgot_pw_error_timer_is_not_zero,
            millisUntilFinished.toInt(),
            millisUntilFinished.toInt()
        )

        onView(withText(message)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun clickOnResendBtnWithSuccessStatus_showsTimerText() {
        launchFragmentInHiltContainer<ForgotPwSentFragment>(
            fragmentArgs = testArgs,
            fragmentFactory = testFragmentFactory
        ) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding
        }

        onView(withId(testBinding.btnResend.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(testBinding.tvTimer.id)).check(matches(isDisplayed()))
    }

    @Test
    fun clickOnResendBtnWithErrorStatus_hidesTimerText() {
        launchFragmentInHiltContainer<ForgotPwSentFragment>(
            fragmentArgs = testArgs,
            fragmentFactory = testFragmentFactory
        ) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding

            viewModel!!.previousSentEmail = email
            viewModel!!.timerMillisUntilFinished = 10 * 1000L // 10 Seconds
        }

        onView(withId(testBinding.btnResend.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(testBinding.tvTimer.id)).check(matches(not(isDisplayed())))
        onView(withText(context.getString(R.string.forgot_pw_sent_btn_resend)))
            .check(matches(isDisplayed()))
    }
}