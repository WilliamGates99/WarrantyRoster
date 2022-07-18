package com.xeniac.warrantyroster_manager.ui.main.fragments

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
import com.xeniac.warrantyroster_manager.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.databinding.FragmentChangePasswordBinding
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.ui.viewmodels.SettingsViewModel
import com.xeniac.warrantyroster_manager.utils.Status
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@HiltAndroidTest
class ChangePasswordFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentChangePasswordBinding

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var testViewModel: SettingsViewModel

    private val email = "email@test.com"
    private val password = "password"

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)

        fakeUserRepository = FakeUserRepository()
        fakeUserRepository.addUser(email, password)

        testViewModel = SettingsViewModel(
            fakeUserRepository,
            FakePreferencesRepository()
        )

        navController.setGraph(R.navigation.nav_graph_main)
        navController.setCurrentDestination(R.id.settingsFragment)
        navController.navigate(SettingsFragmentDirections.actionSettingsFragmentToChangePasswordFragment())

        launchFragmentInHiltContainer<ChangePasswordFragment> {
            Navigation.setViewNavController(requireView(), navController)

            viewModel = testViewModel
            testBinding = binding
        }
    }

    @Test
    fun clickOnCurrentPasswordEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id)).perform(click())

            assertThat(tiLayoutCurrentPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
            assertThat(tiLayoutNewPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutRetypePassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
        }
    }

    @Test
    fun clickOnNewPasswordEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditNewPassword.id)).perform(click())

            assertThat(tiLayoutCurrentPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutNewPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
            assertThat(tiLayoutRetypePassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
        }
    }

    @Test
    fun clickOnRetypePasswordEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditRetypePassword.id)).perform(click())

            assertThat(tiLayoutCurrentPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutNewPassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutRetypePassword.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
        }
    }

    @Test
    fun clickOnCurrentPasswordEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id)).perform(click())
            assertThat(tiLayoutCurrentPassword.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun clickOnNewPasswordEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditNewPassword.id)).perform(click())
            assertThat(tiLayoutNewPassword.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun clickOnRetypePasswordEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditRetypePassword.id)).perform(click())
            assertThat(tiLayoutRetypePassword.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithErrorStatus_returnsError() {
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id)).perform(replaceText(password))
            onView(withId(tiEditNewPassword.id)).perform(replaceText("new_password"))
            onView(withId(tiEditRetypePassword.id)).perform(replaceText("not_password"))
            onView(withId(tiEditRetypePassword.id)).perform(pressImeActionButton())
        }

        val responseEvent = testViewModel.checkInputsLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun pressImeActionOnRetypePasswordEditTextWithSuccessStatus_returnsSuccess() {
        val newPassword = "new_password"
        testBinding.apply {
            onView(withId(tiEditCurrentPassword.id)).perform(replaceText(password))
            onView(withId(tiEditNewPassword.id)).perform(replaceText(newPassword))
            onView(withId(tiEditRetypePassword.id)).perform(replaceText(newPassword))
            onView(withId(tiEditRetypePassword.id)).perform(pressImeActionButton())
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
            allOf(
                instanceOf(AppCompatImageButton::class.java),
                withParent(withId(testBinding.toolbar.id))
            )
        ).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.settingsFragment)
    }
}