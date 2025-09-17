package com.xeniac.warrantyroster_manager.onboarding.presentation.onboarding

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.landing.TestLandingFragmentFactory
import com.xeniac.warrantyroster_manager.databinding.FragmentOnboardingBinding
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.util.Constants.ONBOARDING_1ST_INDEX
import com.xeniac.warrantyroster_manager.util.Constants.ONBOARDING_2ND_INDEX
import com.xeniac.warrantyroster_manager.util.Constants.ONBOARDING_3RD_INDEX
import com.xeniac.warrantyroster_manager.util.Constants.ONBOARDING_4TH_INDEX
import com.xeniac.warrantyroster_manager.util.pixelsEqualTo
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
class OnBoardingFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var testFragmentFactory: TestLandingFragmentFactory

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentOnboardingBinding

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()

        navController = TestNavHostController(context)
        navController.setGraph(R.navigation.nav_graph_landing)

        launchFragmentInHiltContainer<OnBoardingFragment>(fragmentFactory = testFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding
        }
    }

    @Test
    fun launchingOnBoardingFragment_shows1stOnBoardingFragment() {
        onView(withId(testBinding.viewpager.id)).check(matches(isDisplayed()))

        onView(withText(context.getString(R.string.onboarding_1st_description)))
            .check(matches(isDisplayed()))
        assertThat(testBinding.viewpager.currentItem).isEqualTo(ONBOARDING_1ST_INDEX)
    }

    @Test
    fun clickOnOnBoarding1stNextBtn_shows2ndOnBoardingFragment() {
        onView(withId(testBinding.viewpager.id)).check(matches(isDisplayed()))

        onView(withId(R.id.btn_onboarding_1st_next)).perform(click())

        onView(withText(context.getString(R.string.onboarding_2nd_description)))
            .check(matches(isDisplayed()))
        assertThat(testBinding.viewpager.currentItem).isEqualTo(ONBOARDING_2ND_INDEX)
    }

    @Test
    fun clickOnOnBoarding1stSkipBtn_shows4thOnBoardingFragment() {
        onView(withId(testBinding.viewpager.id)).check(matches(isDisplayed()))

        onView(withId(R.id.btn_onboarding_1st_skip)).perform(click())

        onView(withText(context.getString(R.string.onboarding_4th_description)))
            .check(matches(isDisplayed()))
        assertThat(testBinding.viewpager.currentItem).isEqualTo(ONBOARDING_4TH_INDEX)
    }

    @Test
    fun clickOnOnBoarding2ndNextBtn_shows3rdOnBoardingFragment() {
        onView(withId(testBinding.viewpager.id)).check(matches(isDisplayed()))

        // Swipe to 2nd page
        onView(withId(R.id.btn_onboarding_1st_next)).perform(click())

        // Click on Next button
        onView(withId(R.id.btn_onboarding_2nd_next)).perform(click())

        onView(withText(context.getString(R.string.onboarding_3rd_description)))
            .check(matches(isDisplayed()))
        assertThat(testBinding.viewpager.currentItem).isEqualTo(ONBOARDING_3RD_INDEX)
    }

    @Test
    fun clickOnOnBoarding2ndBackBtn_shows1stOnBoardingFragment() {
        onView(withId(testBinding.viewpager.id)).check(matches(isDisplayed()))

        // Swipe to 2nd page
        onView(withId(R.id.btn_onboarding_1st_next)).perform(click())

        // Click on Back button
        onView(withId(R.id.btn_onboarding_2nd_back)).perform(click())

        onView(withText(context.getString(R.string.onboarding_1st_description)))
            .check(matches(isDisplayed()))
        assertThat(testBinding.viewpager.currentItem).isEqualTo(ONBOARDING_1ST_INDEX)
    }

    @Test
    fun clickOnOnBoarding3rdNextBtn_shows4thOnBoardingFragment() {
        onView(withId(testBinding.viewpager.id)).check(matches(isDisplayed()))

        // Swipe to 3rd page
        onView(withId(R.id.btn_onboarding_1st_next)).perform(click())
        onView(withId(R.id.btn_onboarding_2nd_next)).perform(click())

        // Click on Next button
        onView(withId(R.id.btn_onboarding_3rd_next)).perform(click())

        onView(withText(context.getString(R.string.onboarding_4th_description)))
            .check(matches(isDisplayed()))
        assertThat(testBinding.viewpager.currentItem).isEqualTo(ONBOARDING_4TH_INDEX)
    }

    @Test
    fun clickOnOnBoarding3rdBackBtn_shows2ndOnBoardingFragment() {
        onView(withId(testBinding.viewpager.id)).check(matches(isDisplayed()))

        // Swipe to 3rd page
        onView(withId(R.id.btn_onboarding_1st_next)).perform(click())
        onView(withId(R.id.btn_onboarding_2nd_next)).perform(click())

        // Click on Back button
        onView(withId(R.id.btn_onboarding_3rd_back)).perform(click())

        onView(withText(context.getString(R.string.onboarding_2nd_description)))
            .check(matches(isDisplayed()))
        assertThat(testBinding.viewpager.currentItem).isEqualTo(ONBOARDING_2ND_INDEX)
    }

    @Test
    fun clickOnOnBoarding4thStartBtn_navigatesToAuthFragment() {
        onView(withId(testBinding.viewpager.id)).check(matches(isDisplayed()))

        // Swipe to 4th page
        onView(withId(R.id.btn_onboarding_1st_next)).perform(click())
        onView(withId(R.id.btn_onboarding_2nd_next)).perform(click())
        onView(withId(R.id.btn_onboarding_3rd_next)).perform(click())

        // Click on Start button
        onView(withId(R.id.btn_onboarding_4th_start)).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.authFragment)
    }

    @Test
    fun pressBackOn2ndOnBoardingPage_shows1stOnBoardingFragment() {
        onView(withId(testBinding.viewpager.id)).check(matches(isDisplayed()))

        // Swipe to 2nd page
        onView(withId(R.id.btn_onboarding_1st_next)).perform(click())

        pressBackUnconditionally()

        onView(withText(context.getString(R.string.onboarding_1st_description)))
            .check(matches(isDisplayed()))
        assertThat(testBinding.viewpager.currentItem).isEqualTo(ONBOARDING_1ST_INDEX)
    }

    @Test
    fun pressBackOn3rdOnBoardingPage_shows2ndOnBoardingFragment() {
        onView(withId(testBinding.viewpager.id)).check(matches(isDisplayed()))

        // Swipe to 3rd page
        onView(withId(R.id.btn_onboarding_1st_next)).perform(click())
        onView(withId(R.id.btn_onboarding_2nd_next)).perform(click())

        pressBackUnconditionally()

        onView(withText(context.getString(R.string.onboarding_2nd_description)))
            .check(matches(isDisplayed()))
        assertThat(testBinding.viewpager.currentItem).isEqualTo(ONBOARDING_2ND_INDEX)
    }

    @Test
    fun pressBackOn4thOnBoardingPage_shows3rdOnBoardingFragment() {
        onView(withId(testBinding.viewpager.id)).check(matches(isDisplayed()))

        // Swipe to 4th page
        onView(withId(R.id.btn_onboarding_1st_next)).perform(click())
        onView(withId(R.id.btn_onboarding_2nd_next)).perform(click())
        onView(withId(R.id.btn_onboarding_3rd_next)).perform(click())

        pressBackUnconditionally()

        onView(withText(context.getString(R.string.onboarding_3rd_description)))
            .check(matches(isDisplayed()))
        assertThat(testBinding.viewpager.currentItem).isEqualTo(ONBOARDING_3RD_INDEX)
    }

    @Test
    fun getCurrentAppLocaleIndex_setsCurrentLocaleFlagToUsaFlag() {
        onView(withId(testBinding.clLanguage.id)).check(matches(isDisplayed()))

        val usaFlag = context.getDrawable(R.drawable.ic_flag_usa)
        val currentLocaleFlag = testBinding.ivLocaleFlag.drawable

        val isUsaFlagShowing = currentLocaleFlag.pixelsEqualTo(usaFlag)

        assertThat(isUsaFlagShowing).isTrue()
    }

    @Test
    fun clickOnLanguageBtn_showsSelectLanguageDialog() {
        onView(withId(testBinding.clLanguage.id)).check(matches(isDisplayed()))

        onView(withId(testBinding.clLanguage.id)).perform(click())

        onView(withText(context.getString(R.string.onboarding_dialog_title_language)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }
}