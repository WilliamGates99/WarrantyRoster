package com.xeniac.warrantyroster_manager.warranty_management.presentation.add_warranty

import android.content.Context
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
import com.xeniac.warrantyroster_manager.NavGraphMainDirections
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.main.TestMainFragmentFactory
import com.xeniac.warrantyroster_manager.databinding.FragmentAddWarrantyBinding
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
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
class AddWarrantyFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var testFragmentFactory: TestMainFragmentFactory

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentAddWarrantyBinding

    private var backgroundColor = 0
    private var grayLightColor = 0
    private var blueColor = 0

    private val title = "title"
    private val startingDate = "2022-07-13"
    private val brand = "brand"
    private val model = "model"
    private val serialNumber = "SERIAL_NUMBER"
    private val description = "This is description."

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()

        backgroundColor = context.getColor(R.color.background)
        grayLightColor = context.getColor(R.color.grayLight)
        blueColor = context.getColor(R.color.blue)

        navController = TestNavHostController(context)
        navController.setGraph(R.navigation.nav_graph_main)
        navController.navigate(NavGraphMainDirections.actionMainActivityToAddWarrantyFragment())

        launchFragmentInHiltContainer<AddWarrantyFragment>(fragmentFactory = testFragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)

            testBinding = binding
            startingDateInput = startingDate
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
    fun clickOnDateStartingEditText_opensSelectStartingDateDialog() {
        onView(withId(testBinding.tiEditDateStarting.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText(context.getString(R.string.add_warranty_title_date_picker_starting)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickOnDateExpiryEditText_opensSelectExpiryDateDialog() {
        onView(withId(testBinding.tiEditDateExpiry.id))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText(context.getString(R.string.add_warranty_title_date_picker_expiry)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickOnAddWarrantyMenuWithBlankTitle_showsTitleError() {
        testBinding.apply {
            cbLifetime.isChecked = true

            onView(withId(tiEditTitle.id)).perform(
                scrollTo(),
                replaceText("")
            )
            onView(withId(tiEditDateStarting.id)).perform(
                scrollTo(),
                replaceText(startingDate)
            )
            onView(withId(tiEditBrand.id)).perform(
                scrollTo(),
                replaceText(brand)
            )
            onView(withId(tiEditModel.id)).perform(
                scrollTo(),
                replaceText(model)
            )
            onView(withId(tiEditSerial.id)).perform(
                scrollTo(),
                replaceText(serialNumber)
            )
            onView(withId(tiEditDescription.id)).perform(
                scrollTo(),
                replaceText(description)
            )

            onView(withId(R.id.action_menu_add)).perform(click())

            assertThat(tiLayoutTitle.error).isNotNull()
        }
    }

    @Test
    fun clickOnAddWarrantyMenuWithBlankExpiryDate_opensSelectExpiryDateDialog() {
        testBinding.apply {
            cbLifetime.isChecked = false

            onView(withId(tiEditTitle.id)).perform(
                scrollTo(),
                replaceText(title)
            )
            onView(withId(tiEditDateStarting.id)).perform(
                scrollTo(),
                replaceText(startingDate)
            )
            onView(withId(tiEditDateExpiry.id)).perform(
                scrollTo(),
                replaceText("")
            )
            onView(withId(tiEditBrand.id)).perform(
                scrollTo(),
                replaceText(brand)
            )
            onView(withId(tiEditModel.id)).perform(
                scrollTo(),
                replaceText(model)
            )
            onView(withId(tiEditSerial.id)).perform(
                scrollTo(),
                replaceText(serialNumber)
            )
            onView(withId(tiEditDescription.id)).perform(
                scrollTo(),
                replaceText(description)
            )
        }

        onView(withId(R.id.action_menu_add)).perform(click())

        onView(withText(context.getString(R.string.add_warranty_title_date_picker_expiry)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickOnAddWarrantyMenuWithSuccessStatus_navigatesToWarrantiesFragment() {
        testBinding.apply {
            cbLifetime.isChecked = true

            onView(withId(tiEditTitle.id)).perform(
                scrollTo(),
                replaceText(title)
            )
            onView(withId(tiEditDateStarting.id)).perform(
                scrollTo(),
                replaceText(startingDate)
            )
            onView(withId(tiEditBrand.id)).perform(
                scrollTo(),
                replaceText(brand)
            )
            onView(withId(tiEditModel.id)).perform(
                scrollTo(),
                replaceText(model)
            )
            onView(withId(tiEditSerial.id)).perform(
                scrollTo(),
                replaceText(serialNumber)
            )
            onView(withId(tiEditDescription.id)).perform(
                scrollTo(),
                replaceText(description)
            )
        }

        onView(withId(R.id.action_menu_add)).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.warrantiesFragment)
    }
}