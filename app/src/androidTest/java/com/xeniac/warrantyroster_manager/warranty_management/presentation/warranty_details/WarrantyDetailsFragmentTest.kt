package com.xeniac.warrantyroster_manager.warranty_management.presentation.warranty_details

import android.content.Context
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.main.TestMainFragmentFactory
import com.xeniac.warrantyroster_manager.databinding.FragmentWarrantyDetailsBinding
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Warranty
import com.xeniac.warrantyroster_manager.warranty_management.presentation.warranties_list.WarrantiesFragmentDirections
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
class WarrantyDetailsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var testFragmentFactory: TestMainFragmentFactory

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private var testArgs: Bundle? = null
    private lateinit var testBinding: FragmentWarrantyDetailsBinding

    private val warrantyId = "1"
    private val warrantyTitle = "Warranty Title"
    private val warrantyBrand = "Warranty Brand"
    private val warrantyModel = "Warranty Model"
    private val warrantySerialNumber = "WARRANTY_SERIAL"
    private val warrantyDescription = "This is warranty description."
    private val warrantyStartingDate = "2022-07-13"
    private val warrantyExpiryDate = "2050-07-13"

    private val warranty = Warranty(
        id = warrantyId,
        title = warrantyTitle,
        brand = warrantyBrand,
        model = warrantyModel,
        serialNumber = warrantySerialNumber,
        description = warrantyDescription,
        startingDate = warrantyStartingDate,
        expiryDate = warrantyExpiryDate
    )

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()

        navController = TestNavHostController(context)

        navController.setGraph(R.navigation.nav_graph_main)
        navController.navigate(
            WarrantiesFragmentDirections.actionWarrantiesFragmentToWarrantyDetailsFragment(warranty)
        )
        testArgs = navController.backStack.last().arguments

        launchFragmentInHiltContainer<WarrantyDetailsFragment>(
            fragmentArgs = testArgs,
            fragmentFactory = testFragmentFactory
        ) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding
        }
    }

    @Test
    fun pressBackButton_popsBackStack() {
        pressBackUnconditionally()

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.warrantiesFragment)
    }

    @Test
    fun clickOnNavigateUpBtn_popsBackStack() {
        onView(
            allOf(
                instanceOf(AppCompatImageButton::class.java),
                withParent(withId(testBinding.toolbar.id))
            )
        ).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.warrantiesFragment)
    }

    @Test
    fun setWarrantyDetails() {
        val blackColor = ContextCompat.getColor(context, R.color.black)

        val validStatusText = context.getString(R.string.warranty_details_status_valid)
        val validStatusTextColor = ContextCompat.getColor(context, R.color.green)
        val validStatusBackgroundTint = ContextCompat.getColorStateList(context, R.color.green20)

        val startingDateText = "07/13/2022"
        val expiryDateText = "07/13/2050"

        testBinding.apply {
            // Warranty Title
            onView(withId(toolbar.id)).check(matches(isDisplayed()))
            assertThat(toolbar.title).isEqualTo(warrantyTitle)

            // Warranty Status
            onView(withId(tvStatus.id)).check(matches(isDisplayed()))
            assertThat(tvStatus.text).isEqualTo(validStatusText)
            assertThat(tvStatus.currentTextColor).isEqualTo(validStatusTextColor)
            assertThat(tvStatus.backgroundTintList).isEqualTo(validStatusBackgroundTint)

            // Warranty Starting Date
            onView(withId(tvDateStarting.id)).check(matches(isDisplayed()))
            assertThat(tvDateStarting.text).isEqualTo(startingDateText)

            // Warranty Expiry Date
            onView(withId(tvDateExpiry.id)).check(matches(isDisplayed()))
            assertThat(tvDateExpiry.text).isEqualTo(expiryDateText)

            // Warranty Brand
            onView(withId(tvBrand.id)).check(matches(isDisplayed()))
            assertThat(tvBrand.text).isEqualTo(warrantyBrand)
            assertThat(tvBrand.currentTextColor).isEqualTo(blackColor)

            // Warranty Model
            onView(withId(tvModel.id)).check(matches(isDisplayed()))
            assertThat(tvModel.text).isEqualTo(warrantyModel)
            assertThat(tvModel.currentTextColor).isEqualTo(blackColor)

            // Warranty Serial Number
            onView(withId(tvSerial.id)).check(matches(isDisplayed()))
            assertThat(tvSerial.text).isEqualTo(warrantySerialNumber)
            assertThat(tvSerial.currentTextColor).isEqualTo(blackColor)

            // Warranty Description
            onView(withId(tvDescription.id)).check(matches(isDisplayed()))
            assertThat(tvDescription.text).isEqualTo(warrantyDescription)
            assertThat(tvDescription.currentTextColor).isEqualTo(blackColor)
        }
    }

    @Test
    fun clickOnEditWarranty_navigatesToEditWarrantyFragment() {
        onView(withId(testBinding.fab.id)).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.editWarrantyFragment)
    }

    @Test
    fun clickOnDeleteWarrantyMenu_showsDeleteWarrantyDialog() {
        onView(withId(R.id.action_menu_delete)).perform(click())

        onView(withText(context.getString(R.string.warranty_details_delete_dialog_title)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickOnNegativeButtonOfDeleteWarrantyDialog_dismissesTheDialog() {
        onView(withId(R.id.action_menu_delete)).perform(click())

        onView(withText(context.getString(R.string.warranty_details_delete_dialog_negative)))
            .inRoot(isDialog())
            .perform(click())

        onView(withText(context.getString(R.string.warranty_details_delete_dialog_title)))
            .check(doesNotExist())
    }

    @Test
    fun clickOnPositiveButtonOfDeleteWarrantyDialogWithErrorStatus_showsSomethingWentWrongErrorSnackbar() {
        testFragmentFactory.deleteTestWarrantyFromWarrantyRepository(warranty.id)

        onView(withId(R.id.action_menu_delete)).perform(click())

        onView(withText(context.getString(R.string.warranty_details_delete_dialog_positive)))
            .inRoot(isDialog())
            .perform(click())

        onView(withText(context.getString(R.string.error_something_went_wrong)))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun clickOnPositiveButtonOfDeleteWarrantyDialogWithSuccessStatus_navigatesToEditWarrantyFragment() {
        onView(withId(R.id.action_menu_delete)).perform(click())

        onView(withText(context.getString(R.string.warranty_details_delete_dialog_positive)))
            .inRoot(isDialog())
            .perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.warrantiesFragment)
    }
}