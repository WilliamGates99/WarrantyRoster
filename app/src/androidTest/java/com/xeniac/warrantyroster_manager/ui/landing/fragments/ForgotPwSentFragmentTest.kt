package com.xeniac.warrantyroster_manager.ui.landing.fragments

import android.content.Context
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentForgotPwSentBinding
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.ui.landing.viewmodels.LandingViewModel
import com.xeniac.warrantyroster_manager.utils.Status
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var testViewModel: LandingViewModel

    private val email = "email@test.com"

    private lateinit var navController: TestNavHostController
    private var testArgs: Bundle? = null
    private lateinit var testBinding: FragmentForgotPwSentBinding

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        fakeUserRepository = FakeUserRepository()
        testViewModel = LandingViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeUserRepository,
            FakePreferencesRepository()
        )

        fakeUserRepository.addUser(email, "password")

        navController = TestNavHostController(context)
        navController.setGraph(R.navigation.nav_graph_landing)
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
        pressBack()
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.loginFragment)
    }

    @Test
    fun clickOnReturnBtn_popsBackStack() {
        onView(withId(testBinding.btnReturn.id)).perform(click())
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.loginFragment)
    }

    @Test
    fun clickOnResendBtnWithRemainingTimer_returnsError() {
        testViewModel.forgotPwEmail = email
        testViewModel.timerInMillis = 10L
        onView(withId(testBinding.btnResend.id)).perform(click())

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun clickOnResendBtnWithSuccessStatus_returnsSuccess() {
        onView(withId(testBinding.btnResend.id)).perform(click())

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun timerWith0RemainingTime_showsGroupResend() {
        onView(withId(testBinding.btnResend.id)).perform(click())

        assertThat(testBinding.groupTimer.isVisible).isFalse()
        assertThat(testBinding.groupResend.isVisible).isTrue()
    }
}