package com.xeniac.warrantyroster_manager.ui.fragments

import android.content.Context
import androidx.appcompat.widget.AppCompatImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.databinding.FragmentChangeEmailBinding
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.ui.main.fragments.SettingsFragmentDirections
import com.xeniac.warrantyroster_manager.ui.viewmodels.ChangeEmailViewModel
import com.xeniac.warrantyroster_manager.utils.Status
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@HiltAndroidTest
class ChangeEmailFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentChangeEmailBinding

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var testViewModel: ChangeEmailViewModel

    private val email = "email@test.com"
    private val password = "password"

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)

        fakeUserRepository = FakeUserRepository()
        fakeUserRepository.addUser(email, password)
        testViewModel = ChangeEmailViewModel(fakeUserRepository)

        navController.setGraph(R.navigation.nav_graph_main)
        navController.setCurrentDestination(R.id.settingsFragment)
        navController.navigate(SettingsFragmentDirections.actionSettingsFragmentToChangeEmailFragment())

        launchFragmentInHiltContainer<ChangeEmailFragment> {
            Navigation.setViewNavController(requireView(), navController)

            viewModel = testViewModel
            testBinding = binding
        }
    }

    @Test
    fun clickOnPasswordEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditPassword.id)).perform(click())

            assertThat(tiLayoutPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
            assertThat(tiLayoutNewEmail.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
        }
    }

    @Test
    fun clickOnNewEmailEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditNewEmail.id)).perform(click())

            assertThat(tiLayoutPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutNewEmail.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
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
    fun clickOnNewEmailEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditNewEmail.id)).perform(click())
            assertThat(tiLayoutNewEmail.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun pressImeActionOnNewEmailEditTextWithErrorStatus_returnsError() {
        testBinding.apply {
            onView(withId(tiEditPassword.id)).perform(replaceText(password))
            onView(withId(tiEditNewEmail.id)).perform(replaceText(email))
            onView(withId(tiEditPassword.id)).perform(pressImeActionButton())
        }

        val responseEvent = testViewModel.checkInputsLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun pressImeActionOnNewEmailEditTextWithSuccessStatus_returnsSuccess() {
        testBinding.apply {
            onView(withId(tiEditPassword.id)).perform(replaceText(password))
            onView(withId(tiEditNewEmail.id)).perform(replaceText("newemail@test.com"))
            onView(withId(tiEditPassword.id)).perform(pressImeActionButton())
        }

        val responseEvent = testViewModel.checkInputsLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun pressBackButton_popsBackStack() {
        pressBack()
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.settingsFragment)
    }

    @Test
    fun clickOnNavigateUpBtn_popsBackStack() {
        onView(
            CoreMatchers.allOf(
                instanceOf(AppCompatImageButton::class.java),
                withParent(withId(testBinding.toolbar.id))
            )
        ).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.settingsFragment)
    }
}