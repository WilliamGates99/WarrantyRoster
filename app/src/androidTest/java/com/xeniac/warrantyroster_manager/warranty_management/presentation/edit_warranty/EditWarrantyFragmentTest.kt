package com.xeniac.warrantyroster_manager.warranty_management.presentation.edit_warranty

import android.content.Context
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.main.TestMainFragmentFactory
import com.xeniac.warrantyroster_manager.databinding.FragmentEditWarrantyBinding
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Warranty
import com.xeniac.warrantyroster_manager.warranty_management.presentation.warranties_list.WarrantiesFragmentDirections
import com.xeniac.warrantyroster_manager.warranty_management.presentation.warranty_details.WarrantyDetailsFragmentDirections
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
class EditWarrantyFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var testFragmentFactory: TestMainFragmentFactory

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private var testArgs: Bundle? = null
    private lateinit var testBinding: FragmentEditWarrantyBinding

    private var backgroundColor = 0
    private var grayLightColor = 0
    private var blueColor = 0

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
        navController.navigate(
            WarrantyDetailsFragmentDirections.actionWarrantyDetailsFragmentToEditWarrantyFragment(
                warranty
            )
        )
        testArgs = navController.backStack.last().arguments

        launchFragmentInHiltContainer<EditWarrantyFragment>(
            fragmentArgs = testArgs,
            fragmentFactory = testFragmentFactory
        ) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding
        }
    }

    @Test
    fun clickOnTitleEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditTitle.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutTitle.boxBackgroundColor).isEqualTo(backgroundColor)
            assertThat(tiLayoutCategory.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutBrand.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutModel.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutSerial.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutDateStarting.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutDateExpiry.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutDescription.boxBackgroundColor).isEqualTo(grayLightColor)
        }
    }

    @Test
    fun clickOnCategoryAutoCompleteTextView_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiDdCategory.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutTitle.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutCategory.boxBackgroundColor).isEqualTo(backgroundColor)
            assertThat(tiLayoutBrand.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutModel.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutSerial.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutDateStarting.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutDateExpiry.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutDescription.boxBackgroundColor).isEqualTo(grayLightColor)
        }
    }

    @Test
    fun clickOnBrandEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditBrand.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutTitle.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutCategory.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutBrand.boxBackgroundColor).isEqualTo(backgroundColor)
            assertThat(tiLayoutModel.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutSerial.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutDateStarting.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutDateExpiry.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutDescription.boxBackgroundColor).isEqualTo(grayLightColor)
        }
    }

    @Test
    fun clickOnModelEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditModel.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutTitle.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutCategory.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutBrand.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutModel.boxBackgroundColor).isEqualTo(backgroundColor)
            assertThat(tiLayoutSerial.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutDateStarting.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutDateExpiry.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutDescription.boxBackgroundColor).isEqualTo(grayLightColor)
        }
    }

    @Test
    fun clickOnSerialEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditSerial.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutTitle.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutCategory.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutBrand.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutModel.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutSerial.boxBackgroundColor).isEqualTo(backgroundColor)
            assertThat(tiLayoutDateStarting.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutDateExpiry.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutDescription.boxBackgroundColor).isEqualTo(grayLightColor)
        }
    }

    @Test
    fun clickOnDescriptionEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditDescription.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutTitle.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutCategory.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutBrand.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutModel.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutSerial.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutDateStarting.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutDateExpiry.boxBackgroundColor).isEqualTo(grayLightColor)
            assertThat(tiLayoutDescription.boxBackgroundColor).isEqualTo(backgroundColor)
        }
    }

    @Test
    fun clickOnTitleEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditTitle.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutTitle.boxStrokeColor).isEqualTo(blueColor)
        }
    }

    @Test
    fun clickOnCategoryAutoCompleteTextView_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiDdCategory.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutCategory.boxStrokeColor).isEqualTo(blueColor)
        }
    }

    @Test
    fun clickOnBrandEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditBrand.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutBrand.boxStrokeColor).isEqualTo(blueColor)
        }
    }

    @Test
    fun clickOnModelEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditModel.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutModel.boxStrokeColor).isEqualTo(blueColor)
        }
    }

    @Test
    fun clickOnSerialEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditSerial.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutSerial.boxStrokeColor).isEqualTo(blueColor)
        }
    }

    @Test
    fun clickOnDateStartingEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditDateStarting.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutDateStarting.boxStrokeColor).isEqualTo(blueColor)
        }
    }

    @Test
    fun clickOnDateExpiryEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditDateExpiry.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutDateExpiry.boxStrokeColor).isEqualTo(blueColor)
        }
    }

    @Test
    fun clickOnDescriptionEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditDescription.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutDescription.boxStrokeColor).isEqualTo(blueColor)
        }
    }

    @Test
    fun checkLifetimeCheckBox_disablesDateExpiryTextInput() {
        testBinding.apply {
            cbLifetime.isChecked = false

            onView(withId(cbLifetime.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutDateExpiry.isEnabled).isFalse()
        }
    }

    @Test
    fun uncheckLifetimeCheckBox_enablesDateExpiryTextInput() {
        testBinding.apply {
            cbLifetime.isChecked = true

            onView(withId(cbLifetime.id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click())

            assertThat(tiLayoutDateExpiry.isEnabled).isTrue()
        }
    }

    @Test
    fun pressBackButton_popsBackStack() {
        pressBackUnconditionally()

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.warrantyDetailsFragment)
    }

    @Test
    fun clickOnNavigateUpBtn_popsBackStack() {
        onView(
            allOf(
                instanceOf(AppCompatImageButton::class.java),
                withParent(withId(testBinding.toolbar.id))
            )
        ).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.warrantyDetailsFragment)
    }

    @Test
    fun clickOnDateStartingEditText_opensSelectStartingDateDialog() {
        onView(withId(testBinding.tiEditDateStarting.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText(context.getString(R.string.edit_warranty_title_date_picker_starting)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickOnDateExpiryEditText_opensSelectExpiryDateDialog() {
        onView(withId(testBinding.tiEditDateExpiry.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText(context.getString(R.string.edit_warranty_title_date_picker_expiry)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun setWarrantyDetails() {
        val startingDateText = "07/13/2022"
        val expiryDateText = "07/13/2050"
        val defaultCategory = "Miscellaneous"

        testBinding.apply {
            // Warranty Title
            onView(withId(tiEditTitle.id)).perform(scrollTo()).check(matches(isDisplayed()))
            assertThat(tiEditTitle.text).isEqualTo(warrantyTitle)

            // Warranty Starting Date
            onView(withId(tiEditDateStarting.id)).perform(scrollTo()).check(matches(isDisplayed()))
            assertThat(tiEditDateStarting.text).isEqualTo(startingDateText)

            // Warranty Expiry Date
            onView(withId(tiEditDateExpiry.id)).perform(scrollTo()).check(matches(isDisplayed()))
            assertThat(tiEditDateExpiry.text).isEqualTo(expiryDateText)

            // Warranty isLifeTime
            onView(withId(cbLifetime.id)).perform(scrollTo()).check(matches(isDisplayed()))
            assertThat(cbLifetime.isChecked).isFalse()

            // Warranty Brand
            onView(withId(tiEditBrand.id)).perform(scrollTo()).check(matches(isDisplayed()))
            assertThat(tiEditBrand.text).isEqualTo(warrantyBrand)

            // Warranty Model
            onView(withId(tiEditModel.id)).perform(scrollTo()).check(matches(isDisplayed()))
            assertThat(tiEditModel.text).isEqualTo(warrantyModel)

            // Warranty Serial Number
            onView(withId(tiEditSerial.id)).perform(scrollTo()).check(matches(isDisplayed()))
            assertThat(tiEditSerial.text).isEqualTo(warrantySerialNumber)

            // Warranty Category
            onView(withId(tiDdCategory.id)).perform(scrollTo()).check(matches(isDisplayed()))
            assertThat(tiEditSerial.text).isEqualTo(defaultCategory)

            // Warranty Description
            onView(withId(tiEditDescription.id)).perform(scrollTo()).check(matches(isDisplayed()))
            assertThat(tiEditDescription.text).isEqualTo(warrantyDescription)
        }
    }

    @Test
    fun clickOnEditWarrantyMenuWithBlankTitle_showsTitleError() {
        testBinding.apply {
            onView(withId(tiEditTitle.id)).perform(
                scrollTo(),
                replaceText("")
            )

            onView(withId(R.id.action_menu_edit)).perform(click())

            assertThat(tiLayoutTitle.error).isNotNull()
        }
    }

    @Test
    fun clickOnEditWarrantyMenuWithSuccessStatus_navigatesToWarrantyDetails() {
        val newWarrantyTitle = "New Warranty Title"
        testBinding.apply {
            onView(withId(tiEditTitle.id)).perform(
                scrollTo(),
                replaceText(newWarrantyTitle)
            )
        }

        onView(withId(R.id.action_menu_edit)).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.warrantyDetailsFragment)
    }
}