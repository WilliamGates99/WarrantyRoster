package com.xeniac.warrantyroster_manager.authentication.presentation.forgot_password

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
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
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.authentication.presentation.login.LoginFragmentDirections
import com.xeniac.warrantyroster_manager.core.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.databinding.FragmentForgotPwBinding
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.util.Resource
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
    private lateinit var testViewModel: ForgotPwViewModel

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)

        fakeUserRepository = FakeUserRepository()
        testViewModel = ForgotPwViewModel(
            fakeUserRepository,
            SavedStateHandle()
        )

        launchFragmentInHiltContainer<ForgotPwFragment> {
            navController.setGraph(R.navigation.nav_graph_auth)
            Navigation.setViewNavController(requireView(), navController)
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())

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
        }
    }

    @Test
    fun clickOnEmailEditText_changesBoxStrokeColor() {
        onView(withId(testBinding.tiEditEmail.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        assertThat(testBinding.tiLayoutEmail.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
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
    fun pressImeActionOnEmailEditTextWithErrorStatus_returnsError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText("email"),
            pressImeActionButton()
        )

        val responseEvent = testViewModel.sendResetPasswordEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun pressImeActionOnEmailEditTextWithSuccessStatus_returnsSuccess() {
        val email = "email@test.com"
        val password = "password"
        fakeUserRepository.addUser(email, password)

        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email),
            pressImeActionButton()
        )

        val responseEvent = testViewModel.sendResetPasswordEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun pressImeActionOnEmailEditTextWithSuccessStatus_navigatesToForgotPwSentFragment() {
        val email = "email@test.com"
        val password = "password"
        fakeUserRepository.addUser(email, password)

        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            pressImeActionButton()
        )

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.forgotPwSentFragment)
    }

    @Test
    fun clickOnSendBtnWithErrorStatus_returnsError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText("email")
        )
        onView(withId(testBinding.btnSend.id)).perform(
            scrollTo(),
            click()
        )

        val responseEvent = testViewModel.sendResetPasswordEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun clickOnSendBtnWithSuccessStatus_returnsSuccess() {
        val email = "email@test.com"
        val password = "password"
        fakeUserRepository.addUser(email, password)

        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.btnSend.id)).perform(
            scrollTo(),
            click()
        )

        val responseEvent = testViewModel.sendResetPasswordEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun clickOnSendBtnWithSuccessStatus_navigatesToForgotPwSentFragment() {
        val email = "email@test.com"
        val password = "password"
        fakeUserRepository.addUser(email, password)

        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.btnSend.id)).perform(
            scrollTo(),
            click()
        )

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.forgotPwSentFragment)
    }
}