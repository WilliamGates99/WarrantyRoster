package com.xeniac.warrantyroster_manager.authentication.presentation.auth

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.landing.TestLandingFragmentFactory
import com.xeniac.warrantyroster_manager.databinding.FragmentAuthBinding
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
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
class AuthFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var testFragmentFactory: TestLandingFragmentFactory

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentAuthBinding

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()

        navController = TestNavHostController(context)
        navController.setGraph(R.navigation.nav_graph_landing)

        launchFragmentInHiltContainer<AuthFragment>(fragmentFactory = testFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding
        }
    }

    @Test
    fun navigateToAuthFragment_showsLoginTitle() {
        val loginTitle = context.getString(R.string.login_text_title)
        val currentTitle = testBinding.tvTitle.text

        onView(withId(testBinding.tvTitle.id)).check(matches(isDisplayed()))
        assertThat(currentTitle).isEqualTo(loginTitle)
    }

    @Test
    fun navigateToRegisterFragment_showsRegisterTitle() {
        onView(withId(R.id.btn_register))
            .perform(scrollTo())
            .perform(click())

        val loginTitle = context.getString(R.string.register_text_title)
        val currentTitle = testBinding.tvTitle.text

        onView(withId(testBinding.tvTitle.id)).check(matches(isDisplayed()))
        assertThat(currentTitle).isEqualTo(loginTitle)
    }

    @Test
    fun navigateToForgotPwFragment_showsForgotPwTitle() {
        onView(withId(R.id.btn_forgot_pw))
            .perform(scrollTo())
            .perform(click())

        val forgotPwTitle = context.getString(R.string.forgot_pw_text_title)
        val currentTitle = testBinding.tvTitle.text

        onView(withId(testBinding.tvTitle.id)).check(matches(isDisplayed()))
        assertThat(currentTitle).isEqualTo(forgotPwTitle)
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

        onView(withText(context.getString(R.string.auth_dialog_title_language)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }
}