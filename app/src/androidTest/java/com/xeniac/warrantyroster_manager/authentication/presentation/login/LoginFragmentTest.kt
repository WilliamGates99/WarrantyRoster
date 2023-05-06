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
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.core.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.databinding.FragmentLoginBinding
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
class LoginFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentLoginBinding

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var testViewModel: LoginViewModel

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)

        fakeUserRepository = FakeUserRepository()
        testViewModel = LoginViewModel(
            fakeUserRepository,
            FakePreferencesRepository()
        )

        launchFragmentInHiltContainer<LoginFragment> {
            navController.setGraph(R.navigation.nav_graph_auth)
            Navigation.setViewNavController(requireView(), navController)

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
    fun pressImeActionOnPasswordEditTextWithErrorStatus_returnsError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText("email")
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText("password"),
            pressImeActionButton()
        )

        val responseEvent = testViewModel.loginWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun pressImeActionOnPasswordEditTextWithSuccessStatus_returnsSuccess() {
        val email = "email@test.com"
        val password = "password"

        fakeUserRepository.addUser(email, password)

        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password),
            pressImeActionButton()
        )

        val responseEvent = testViewModel.loginWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun clickOnLoginBtnWithErrorStatus_returnsError() {
        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText("email")
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText("password")
        )
        onView(withId(testBinding.btnLogin.id)).perform(
            scrollTo(),
            click()
        )

        val responseEvent = testViewModel.loginWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun clickOnLoginBtnWithSuccessStatus_returnsSuccess() {
        val email = "email@test.com"
        val password = "password"

        fakeUserRepository.addUser(email, password)

        onView(withId(testBinding.tiEditEmail.id)).perform(
            scrollTo(),
            replaceText(email)
        )
        onView(withId(testBinding.tiEditPassword.id)).perform(
            scrollTo(),
            replaceText(password),
            pressImeActionButton()
        )
        onView(withId(testBinding.btnLogin.id)).perform(
            scrollTo(),
            click()
        )

        val responseEvent = testViewModel.loginWithEmailLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }
}