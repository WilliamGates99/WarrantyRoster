package com.xeniac.warrantyroster_manager.ui.main

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.android.play.core.review.testing.FakeReviewManager
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.ActivityMainBinding
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController

    private lateinit var activityScenario: ActivityScenario<MainActivity>
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
            testBinding = it.binding
            Navigation.setViewNavController(testBinding.root, navController)
        }
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun launchMainActivity_navigatesToWarrantiesFragment() {
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.warrantiesFragment)
    }

    /*
    @Test
    fun clickOnAddWarrantyFab_navigatesToAddWarrantyFragment() {
        onView(withId(testBinding.fab.id)).perform(click())

        //GETS STUCK HERE
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.addWarrantyFragment)
    }

    @Test
    fun clickOnSettingsMenu_navigatesToSettingsFragment() {
//        onView(withId(testBinding.fab.id)).perform(NavigationViewActions.)
//
//        onView(withId(testBinding.bnv.id)).perform(
//            NavigationViewActions.navigateTo(R.menu.menu_bottom_nav)
//        )
//
//        assertThat(navController.currentDestination?.id).isEqualTo(R.id.addWarrantyFragment)
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.settingsFragment)
    }
    */

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