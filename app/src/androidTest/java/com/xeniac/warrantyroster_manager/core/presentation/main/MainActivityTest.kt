package com.xeniac.warrantyroster_manager.core.presentation.main

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.android.play.core.review.testing.FakeReviewManager
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.ActivityMainBinding
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@HiltAndroidTest
class MainActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var activityScenario: ActivityScenario<MainActivity>

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: ActivityMainBinding

    @Before
    fun setUp() {
        hiltRule.inject()
        Intents.init()

        context = ApplicationProvider.getApplicationContext()

        navController = TestNavHostController(context)
        navController.setGraph(R.navigation.nav_graph_main)

        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity {
            Navigation.setViewNavController(testBinding.root, navController)

            testBinding = it.binding
        }
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun launchMainActivityWithTrueIsUserLoggedIn_navigatesToWarrantiesFragment() {
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.warrantiesFragment)
    }

    @Test
    fun clickOnWarrantiesMenu_navigatesToWarrantiesFragment() {
        onView(withId(R.id.warrantiesFragment)).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.warrantiesFragment)
    }

    @Test
    fun clickOnSettingsMenu_navigatesToSettingsFragment() {
        onView(withId(R.id.settingsFragment)).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.settingsFragment)
    }

    @Test
    fun clickOnAddWarrantyFab_navigatesToAddWarrantyFragment() {
        onView(withId(testBinding.fab.id)).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.addWarrantyFragment)
    }

    @Test
    fun clickOnAddWarrantyFab_hidesFab() {
        onView(withId(testBinding.fab.id))
            .perform(click())
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun clickOnAddWarrantyFab_hidesAppBar() {
        onView(withId(testBinding.fab.id)).perform(click())

        onView(withId(testBinding.appbar.id)).check(matches(not(isDisplayed())))
    }

    @Test
    fun requestInAppReviews_returnsSuccess() {
        val fakeReviewManager = FakeReviewManager(context)
        val fakeRequest = fakeReviewManager.requestReviewFlow()

        fakeRequest.addOnCompleteListener { task ->
            assertThat(task.isSuccessful).isTrue()
        }
    }

    @Test
    fun callingShowRateAppDialogWithSuccessfulTask_showsRateAppDialog() {
        val fakeReviewManager = FakeReviewManager(context)
        val fakeRequest = fakeReviewManager.requestReviewFlow()

        fakeRequest.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                activityScenario.onActivity {
                    it.reviewInfo = task.result
                    it.showRateAppDialog()
                }
            }
        }

        onView(withText(context.getString(R.string.main_rate_app_dialog_message)))
            .inRoot(isDialog()).check(matches(isDisplayed()))
    }
}