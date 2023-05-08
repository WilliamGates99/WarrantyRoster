package com.xeniac.warrantyroster_manager.settings.presentation.linked_accounts

import android.content.Context
import androidx.appcompat.widget.AppCompatImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.main.TestMainFragmentFactory
import com.xeniac.warrantyroster_manager.databinding.FragmentLinkedAccountsBinding
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.settings.presentation.settings.SettingsFragmentDirections
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
class LinkedAccountsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var testFragmentFactory: TestMainFragmentFactory

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentLinkedAccountsBinding

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)

        navController.setGraph(R.navigation.nav_graph_main)
        navController.setCurrentDestination(R.id.settingsFragment)
        navController.navigate(SettingsFragmentDirections.actionSettingsFragmentToLinkedAccountsFragment())
    }

    @Test
    fun pressBackButton_popsBackStack() {
        launchFragmentInHiltContainer<LinkedAccountsFragment>(fragmentFactory = testFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding
        }

        pressBackUnconditionally()

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.settingsFragment)
    }

    @Test
    fun clickOnNavigateUpBtn_popsBackStack() {
        launchFragmentInHiltContainer<LinkedAccountsFragment>(fragmentFactory = testFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding
        }

        onView(
            CoreMatchers.allOf(
                instanceOf(AppCompatImageButton::class.java),
                withParent(withId(testBinding.toolbar.id))
            )
        ).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.settingsFragment)
    }

    @Test
    fun userWithLinkedGoogleAccount_setsStatusIndicatorsToGreen() {
        val greenColor = ContextCompat.getColorStateList(context, R.color.green)

        launchFragmentInHiltContainer<LinkedAccountsFragment>(fragmentFactory = testFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding
        }

        onView(withId(testBinding.flStatusGoogle.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        assertThat(testBinding.flStatusGoogle.backgroundTintList).isEqualTo(greenColor)
    }

    @Test
    fun userWithLinkedTwitterAccount_setsStatusIndicatorsToGreen() {
        val greenColor = ContextCompat.getColorStateList(context, R.color.green)

        launchFragmentInHiltContainer<LinkedAccountsFragment>(fragmentFactory = testFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding
        }

        onView(withId(testBinding.flStatusTwitter.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        assertThat(testBinding.flStatusTwitter.backgroundTintList).isEqualTo(greenColor)
    }

    @Test
    fun userWithLinkedFacebookAccount_setsStatusIndicatorsToGreen() {
        val greenColor = ContextCompat.getColorStateList(context, R.color.green)

        launchFragmentInHiltContainer<LinkedAccountsFragment>(fragmentFactory = testFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding
        }

        onView(withId(testBinding.flStatusFacebook.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        assertThat(testBinding.flStatusFacebook.backgroundTintList).isEqualTo(greenColor)
    }

    @Test
    fun userWithoutLinkedGoogleAccount_setsStatusIndicatorsToRed() {
        val redColor = ContextCompat.getColorStateList(context, R.color.red)

        launchFragmentInHiltContainer<LinkedAccountsFragment>(fragmentFactory = testFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding

            viewModel!!.unlinkGoogleAccount()
        }

        onView(withId(testBinding.flStatusGoogle.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        assertThat(testBinding.flStatusGoogle.backgroundTintList).isEqualTo(redColor)
    }

    @Test
    fun userWithoutLinkedTwitterAccount_setsStatusIndicatorsToRed() {
        val redColor = ContextCompat.getColorStateList(context, R.color.red)

        launchFragmentInHiltContainer<LinkedAccountsFragment>(fragmentFactory = testFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding

            viewModel!!.unlinkTwitterAccount()
        }

        onView(withId(testBinding.flStatusTwitter.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        assertThat(testBinding.flStatusTwitter.backgroundTintList).isEqualTo(redColor)
    }

    @Test
    fun userWithoutLinkedFacebookAccount_setsStatusIndicatorsToRed() {
        val redColor = ContextCompat.getColorStateList(context, R.color.red)

        launchFragmentInHiltContainer<LinkedAccountsFragment>(fragmentFactory = testFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding

            viewModel!!.unlinkFacebookAccount()
        }

        onView(withId(testBinding.flStatusFacebook.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        assertThat(testBinding.flStatusFacebook.backgroundTintList).isEqualTo(redColor)
    }

    @Test
    fun clickOnGoogleBtnOnUserWithLinkedGoogleAccount_unlinksGoogleAccount() {
        val redColor = ContextCompat.getColorStateList(context, R.color.red)

        launchFragmentInHiltContainer<LinkedAccountsFragment>(fragmentFactory = testFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding

            viewModel!!.unlinkGoogleAccount()
        }

        onView(withId(testBinding.cvGoogle.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(testBinding.flStatusGoogle.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        assertThat(testBinding.flStatusGoogle.backgroundTintList).isEqualTo(redColor)
    }

    @Test
    fun clickOnTwitterBtnOnUserWithLinkedTwitterAccount_unlinksTwitterAccount() {
        val redColor = ContextCompat.getColorStateList(context, R.color.red)

        launchFragmentInHiltContainer<LinkedAccountsFragment>(fragmentFactory = testFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding

            viewModel!!.unlinkTwitterAccount()
        }

        onView(withId(testBinding.cvTwitter.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(testBinding.flStatusTwitter.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        assertThat(testBinding.flStatusTwitter.backgroundTintList).isEqualTo(redColor)
    }

    @Test
    fun clickOnFacebookBtnOnUserWithLinkedFacebookAccount_unlinksFacebookAccount() {
        val redColor = ContextCompat.getColorStateList(context, R.color.red)

        launchFragmentInHiltContainer<LinkedAccountsFragment>(fragmentFactory = testFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding

            viewModel!!.unlinkFacebookAccount()
        }

        onView(withId(testBinding.cvFacebook.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(testBinding.flStatusFacebook.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        assertThat(testBinding.flStatusFacebook.backgroundTintList).isEqualTo(redColor)
    }
}