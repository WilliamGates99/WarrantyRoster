package com.xeniac.warrantyroster_manager.ui.landing.fragments

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentForgotPwBinding
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.ui.viewmodels.LandingViewModel
import com.xeniac.warrantyroster_manager.utils.Status
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@HiltAndroidTest
class ForgotPwFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentForgotPwBinding

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var testViewModel: LandingViewModel

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)

        fakeUserRepository = FakeUserRepository()
        testViewModel = LandingViewModel(
            fakeUserRepository,
            FakePreferencesRepository()
        )

        launchFragmentInHiltContainer<ForgotPwFragment> {
            Navigation.setViewNavController(requireView(), navController)
            navController.setGraph(R.navigation.nav_graph_landing)
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())

            viewModel = testViewModel
            testBinding = binding
        }
    }

    @Test
    fun clickOnEmailEditText_changesBoxBackgroundColor() {
        onView(withId(testBinding.tiEditEmail.id)).perform(click())
        assertThat(testBinding.tiLayoutEmail.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
    }

    @Test
    fun clickOnEmailEditText_changesBoxStrokeColor() {
        onView(withId(testBinding.tiEditEmail.id)).perform(click())
        assertThat(testBinding.tiLayoutEmail.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
    }

    @Test
    fun pressImeActionOnEmailEditTextWithErrorStatus_returnsError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(replaceText("email"))
        onView(withId(testBinding.tiEditEmail.id)).perform(pressImeActionButton())

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun pressImeActionOnEmailEditTextWithSuccessStatus_returnsSuccess() {
        val email = "email@test.com"
        val password = "password"
        fakeUserRepository.addUser(email, password)

        onView(withId(testBinding.tiEditEmail.id)).perform(replaceText(email))
        onView(withId(testBinding.tiEditEmail.id)).perform(pressImeActionButton())

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
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
    fun clickOnSendBtnWithErrorStatus_returnsError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(replaceText("email"))
        onView(withId(testBinding.btnSend.id)).perform(click())

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun clickOnSendBtnWithSuccessStatus_returnsSuccess() {
        val email = "email@test.com"
        val password = "password"
        fakeUserRepository.addUser(email, password)

        onView(withId(testBinding.tiEditEmail.id)).perform(replaceText(email))
        onView(withId(testBinding.btnSend.id)).perform(click())

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun clickOnSendBtnWithSuccessStatus_navigatesToForgotPwSentFragment() {
        val email = "email@test.com"
        val password = "password"
        fakeUserRepository.addUser(email, password)

        onView(withId(testBinding.tiEditEmail.id)).perform(replaceText(email))
        onView(withId(testBinding.btnSend.id)).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.forgotPwSentFragment)
    }

    @Test
    fun pressImeActionOnEmailEditTextWithSuccessStatus_navigatesToForgotPwSentFragment() {
        val email = "email@test.com"
        val password = "password"
        fakeUserRepository.addUser(email, password)

        onView(withId(testBinding.tiEditEmail.id)).perform(replaceText(email))
        onView(withId(testBinding.tiEditEmail.id)).perform(pressImeActionButton())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.forgotPwSentFragment)
    }
}