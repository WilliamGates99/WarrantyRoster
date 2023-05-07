package com.xeniac.warrantyroster_manager.onboarding.presentation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentOnboarding1stBinding
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@HiltAndroidTest
class OnBoarding1stFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentOnboarding1stBinding

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)
        navController.setGraph(R.navigation.nav_graph_landing)

        launchFragmentInHiltContainer<OnBoarding1stFragment> {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding
        }
    }

    @Test
    fun clickOnNextBtn_shows2ndOnBoardingFragment() {
        onView(withText(context.getString(R.string.onboarding_1st_title)))
            .check(matches(isDisplayed()))

        onView(withId(R.id.btn_onboarding_1st_next)).perform(click())

        onView(withText(context.getString(R.string.onboarding_2nd_title)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickOnOnSkipBtn_shows4thOnBoardingFragment() {
        onView(withText(context.getString(R.string.onboarding_1st_title)))
            .check(matches(isDisplayed()))

        onView(withId(R.id.btn_onboarding_1st_skip)).perform(click())

        onView(withText(context.getString(R.string.onboarding_4th_title)))
            .check(matches(isDisplayed()))
    }
}