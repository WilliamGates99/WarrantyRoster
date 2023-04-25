package com.xeniac.warrantyroster_manager.ui.fragments.auth

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
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.databinding.FragmentForgotPwSentBinding
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.ui.viewmodels.ForgotPwViewModel
import com.xeniac.warrantyroster_manager.util.Resource
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
        testViewModel = ForgotPwViewModel(fakeUserRepository)

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
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun clickOnResendBtnWithSuccessStatus_returnsSuccess() {
        onView(withId(testBinding.btnResend.id)).perform(click())

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun timerWith0RemainingTime_showsGroupResend() {
        testBinding.apply {
            onView(withId(btnResend.id)).perform(click())

            assertThat(groupTimer.isVisible).isFalse()
            assertThat(groupResend.isVisible).isTrue()
        }
    }
}