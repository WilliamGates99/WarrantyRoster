package com.xeniac.warrantyroster_manager.ui.landing.fragments

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.repositories.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.repositories.FakeUserRepository
import com.xeniac.warrantyroster_manager.ui.landing.LandingFragmentFactory
import com.xeniac.warrantyroster_manager.ui.landing.viewmodels.LandingViewModel
import com.xeniac.warrantyroster_manager.utils.Status
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
class LoginFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fragmentFactory: LandingFragmentFactory

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun clickOnForgotPwBtn_navigatesToForgotPwFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<LoginFragment>(fragmentFactory = fragmentFactory) {
            navController.setGraph(R.navigation.nav_graph_landing)
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.btn_forgot_pw)).perform(click())
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.forgotPwFragment)
    }

    @Test
    fun clickOnRegisterBtn_navigatesToRegisterFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<LoginFragment>(fragmentFactory = fragmentFactory) {
            navController.setGraph(R.navigation.nav_graph_landing)
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.btn_register)).perform(click())
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.registerFragment)
    }

    @Test
    fun clickOnLoginBtnWithErrorStatus_returnsError() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        val testViewModel = LandingViewModel(
            ApplicationProvider.getApplicationContext(),
            FakeUserRepository(),
            FakePreferencesRepository()
        )

        launchFragmentInHiltContainer<LoginFragment>(fragmentFactory = fragmentFactory) {
            navController.setGraph(R.navigation.nav_graph_landing)
            Navigation.setViewNavController(requireView(), navController)
            viewModel = testViewModel
        }

        onView(withId(R.id.ti_edit_email)).perform(replaceText("email"))
        onView(withId(R.id.ti_edit_password)).perform(replaceText("password"))
        onView(withId(R.id.btn_login)).perform(click())

        val responseEvent = testViewModel.loginLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun clickOnLoginBtnWithSuccessStatus_returnsSuccess() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val fakeUserRepository = FakeUserRepository()

        val email = "email@test.com"
        val password = "password"
        fakeUserRepository.addUser(email, password)

        val testViewModel = LandingViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeUserRepository,
            FakePreferencesRepository()
        )

        launchFragmentInHiltContainer<LoginFragment>(fragmentFactory = fragmentFactory) {
            navController.setGraph(R.navigation.nav_graph_landing)
            Navigation.setViewNavController(requireView(), navController)
            viewModel = testViewModel
        }

        onView(withId(R.id.ti_edit_email)).perform(replaceText(email))
        onView(withId(R.id.ti_edit_password)).perform(replaceText(password))
        onView(withId(R.id.btn_login)).perform(click())

        val responseEvent = testViewModel.loginLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }
}