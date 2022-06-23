package com.xeniac.warrantyroster_manager.ui.landing.fragments

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentForgotPwBinding
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
class ForgotPwFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fragmentFactory: LandingFragmentFactory

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentForgotPwBinding

    @Before
    fun setUp() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun clickOnEmailEditText_changesBoxBackgroundColor() {
        launchFragmentInHiltContainer<ForgotPwFragment>(fragmentFactory = fragmentFactory) {
            navController.setGraph(R.navigation.nav_graph_landing)
            Navigation.setViewNavController(requireView(), navController)
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
            testBinding = binding
        }

        onView(withId(testBinding.tiEditEmail.id)).perform(click())
        assertThat(testBinding.tiLayoutEmail.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
    }

    @Test
    fun clickOnEmailEditText_changesBoxStrokeColor() {
        launchFragmentInHiltContainer<ForgotPwFragment>(fragmentFactory = fragmentFactory) {
            navController.setGraph(R.navigation.nav_graph_landing)
            Navigation.setViewNavController(requireView(), navController)
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
            testBinding = binding
        }

        onView(withId(testBinding.tiEditEmail.id)).perform(click())
        assertThat(testBinding.tiLayoutEmail.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
    }

    @Test
    fun pressImeActionOnEmailEditTextWithErrorStatus_returnsError() {
        val testViewModel = LandingViewModel(
            ApplicationProvider.getApplicationContext(),
            FakeUserRepository(),
            FakePreferencesRepository()
        )

        launchFragmentInHiltContainer<ForgotPwFragment>(fragmentFactory = fragmentFactory) {
            navController.setGraph(R.navigation.nav_graph_landing)
            Navigation.setViewNavController(requireView(), navController)
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
            viewModel = testViewModel
            testBinding = binding
        }

        onView(withId(testBinding.tiEditEmail.id)).perform(ViewActions.replaceText("email"))
        onView(withId(testBinding.tiEditEmail.id)).perform(pressImeActionButton())

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun pressImeActionOnEmailEditTextWithSuccessStatus_returnsSuccess() {
        val fakeUserRepository = FakeUserRepository()

        val email = "email@test.com"
        val password = "password"
        fakeUserRepository.addUser(email, password)

        val testViewModel = LandingViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeUserRepository,
            FakePreferencesRepository()
        )

        launchFragmentInHiltContainer<ForgotPwFragment>(fragmentFactory = fragmentFactory) {
            navController.setGraph(R.navigation.nav_graph_landing)
            Navigation.setViewNavController(requireView(), navController)
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
            viewModel = testViewModel
            testBinding = binding
        }

        onView(withId(testBinding.tiEditEmail.id)).perform(ViewActions.replaceText(email))
        onView(withId(testBinding.tiEditEmail.id)).perform(pressImeActionButton())

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun pressBack_popsBackStack() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<ForgotPwFragment>(fragmentFactory = fragmentFactory) {
            navController.setGraph(R.navigation.nav_graph_landing)
            Navigation.setViewNavController(requireView(), navController)
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
        }

        pressBack()
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.loginFragment)
    }

    @Test
    fun clickOnReturnBtn_popsBackStack() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<ForgotPwFragment>(fragmentFactory = fragmentFactory) {
            navController.setGraph(R.navigation.nav_graph_landing)
            Navigation.setViewNavController(requireView(), navController)
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
            testBinding = binding
        }

        onView(withId(testBinding.btnReturn.id)).perform(click())
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.loginFragment)
    }

    @Test
    fun clickOnSendBtnWithErrorStatus_returnsError() {
        val testViewModel = LandingViewModel(
            ApplicationProvider.getApplicationContext(),
            FakeUserRepository(),
            FakePreferencesRepository()
        )

        launchFragmentInHiltContainer<ForgotPwFragment>(fragmentFactory = fragmentFactory) {
            navController.setGraph(R.navigation.nav_graph_landing)
            Navigation.setViewNavController(requireView(), navController)
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
            viewModel = testViewModel
            testBinding = binding
        }

        onView(withId(testBinding.tiEditEmail.id)).perform(ViewActions.replaceText("email"))
        onView(withId(testBinding.btnSend.id)).perform(click())

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun clickOnSendBtnWithSuccessStatus_returnsSuccess() {
        val fakeUserRepository = FakeUserRepository()

        val email = "email@test.com"
        val password = "password"
        fakeUserRepository.addUser(email, password)

        val testViewModel = LandingViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeUserRepository,
            FakePreferencesRepository()
        )

        launchFragmentInHiltContainer<ForgotPwFragment>(fragmentFactory = fragmentFactory) {
            navController.setGraph(R.navigation.nav_graph_landing)
            Navigation.setViewNavController(requireView(), navController)
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
            viewModel = testViewModel
            testBinding = binding
        }

        onView(withId(testBinding.tiEditEmail.id)).perform(ViewActions.replaceText(email))
        onView(withId(testBinding.btnSend.id)).perform(click())

        val responseEvent = testViewModel.forgotPwLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun clickOnSendBtnWithSuccessStatus_navigatesToForgotPwSentFragment() {
        val fakeUserRepository = FakeUserRepository()

        val email = "email@test.com"
        val password = "password"
        fakeUserRepository.addUser(email, password)

        val testViewModel = LandingViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeUserRepository,
            FakePreferencesRepository()
        )

        launchFragmentInHiltContainer<ForgotPwFragment>(fragmentFactory = fragmentFactory) {
            navController.setGraph(R.navigation.nav_graph_landing)
            Navigation.setViewNavController(requireView(), navController)
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
            viewModel = testViewModel
            testBinding = binding
        }

        onView(withId(testBinding.tiEditEmail.id)).perform(ViewActions.replaceText(email))
        onView(withId(testBinding.btnSend.id)).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(
            ForgotPwFragmentDirections.actionForgotPasswordFragmentToForgotPwSentFragment(email)
        )
    }
}