package com.xeniac.warrantyroster_manager.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.data.remote.models.Warranty
import com.xeniac.warrantyroster_manager.data.repository.FakeMainRepository
import com.xeniac.warrantyroster_manager.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.databinding.FragmentEditWarrantyBinding
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.ui.main.fragments.WarrantiesFragmentDirections
import com.xeniac.warrantyroster_manager.ui.main.fragments.WarrantyDetailsFragmentDirections
import com.xeniac.warrantyroster_manager.ui.viewmodels.WarrantyViewModel
import com.xeniac.warrantyroster_manager.utils.Status
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@HiltAndroidTest
class EditWarrantyFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private var testArgs: Bundle? = null
    private lateinit var testBinding: FragmentEditWarrantyBinding

    private lateinit var testViewModel: WarrantyViewModel

    private val warranty = Warranty(
        id = "1",
        title = "title",
        isLifetime = true,
        startingDate = "2022-07-13"
    )

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)

        testViewModel = WarrantyViewModel(
            FakeUserRepository(),
            FakeMainRepository(),
            FakePreferencesRepository()
        )

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

        launchFragmentInHiltContainer<EditWarrantyFragment>(fragmentArgs = testArgs) {
            Navigation.setViewNavController(requireView(), navController)

            viewModel = testViewModel
            testBinding = binding
        }
    }

    @Test
    fun clickOnTitleEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditTitle.id)).perform(click())

            assertThat(tiLayoutTitle.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
            assertThat(tiLayoutCategory.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutBrand.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutModel.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutSerial.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDateStarting.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDateExpiry.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDescription.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
        }
    }

    @Test
    fun clickOnCategoryAutoCompleteTextView_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiDdCategory.id)).perform(click())

            assertThat(tiLayoutTitle.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutCategory.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
            assertThat(tiLayoutBrand.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutModel.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutSerial.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDateStarting.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDateExpiry.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDescription.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
        }
    }

    @Test
    fun clickOnBrandEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditBrand.id)).perform(click())

            assertThat(tiLayoutTitle.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutCategory.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutBrand.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
            assertThat(tiLayoutModel.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutSerial.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDateStarting.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDateExpiry.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDescription.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
        }
    }

    @Test
    fun clickOnModelEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditModel.id)).perform(click())

            assertThat(tiLayoutTitle.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutCategory.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutBrand.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutModel.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
            assertThat(tiLayoutSerial.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDateStarting.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDateExpiry.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDescription.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
        }
    }

    @Test
    fun clickOnSerialEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditSerial.id)).perform(click())

            assertThat(tiLayoutTitle.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutCategory.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutBrand.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutModel.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutSerial.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
            assertThat(tiLayoutDateStarting.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDateExpiry.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDescription.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
        }
    }

    @Test
    fun clickOnDateStartingEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditDateStarting.id)).perform(click())

            assertThat(tiLayoutTitle.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutCategory.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutBrand.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutModel.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutSerial.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDateStarting.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
            assertThat(tiLayoutDateExpiry.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDescription.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
        }
    }

    @Test
    fun clickOnDateExpiryEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditDateExpiry.id)).perform(click())

            assertThat(tiLayoutTitle.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutCategory.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutBrand.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutModel.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutSerial.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDateStarting.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDateExpiry.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
            assertThat(tiLayoutDescription.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
        }
    }

    @Test
    fun clickOnDescriptionEditText_changesBoxBackgroundColor() {
        testBinding.apply {
            onView(withId(tiEditDescription.id)).perform(click())

            assertThat(tiLayoutTitle.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutCategory.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutBrand.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutModel.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutSerial.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDateStarting.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDateExpiry.boxBackgroundColor).isEqualTo(context.getColor(R.color.grayLight))
            assertThat(tiLayoutDescription.boxBackgroundColor).isEqualTo(context.getColor(R.color.background))
        }
    }

    @Test
    fun clickOnTitleEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditTitle.id)).perform(click())
            assertThat(tiLayoutTitle.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun clickOnCategoryAutoCompleteTextView_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiDdCategory.id)).perform(click())
            assertThat(tiLayoutCategory.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun clickOnBrandEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditBrand.id)).perform(click())
            assertThat(tiLayoutBrand.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun clickOnModelEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditModel.id)).perform(click())
            assertThat(tiLayoutModel.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun clickOnSerialEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditSerial.id)).perform(click())
            assertThat(tiLayoutSerial.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun clickOnDateStartingEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditDateStarting.id)).perform(click())
            assertThat(tiLayoutDateStarting.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun clickOnDateExpiryEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditDateExpiry.id)).perform(click())
            assertThat(tiLayoutDateExpiry.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun clickOnDescriptionEditText_changesBoxStrokeColor() {
        testBinding.apply {
            onView(withId(tiEditDescription.id)).perform(click())
            assertThat(tiLayoutDescription.boxStrokeColor).isEqualTo(context.getColor(R.color.blue))
        }
    }

    @Test
    fun checkLifetimeCheckBox_disablesDateExpiryTextInput() {
        testBinding.apply {
            cbLifetime.isChecked = false
            onView(withId(cbLifetime.id)).perform(click())

            assertThat(tiLayoutDateExpiry.isEnabled).isFalse()
        }
    }

    @Test
    fun uncheckLifetimeCheckBox_enablesDateExpiryTextInput() {
        testBinding.apply {
            cbLifetime.isChecked = true
            onView(withId(cbLifetime.id)).perform(click())

            assertThat(tiLayoutDateExpiry.isEnabled).isTrue()
        }
    }

    @Test
    fun setWarrantyDetails() {
        testBinding.apply {
            assertThat(tiEditTitle.text).isEqualTo(warranty.title)
            assertThat(tiEditBrand.text).isEqualTo(warranty.brand)
            assertThat(tiEditModel.text).isEqualTo(warranty.model)
            assertThat(tiEditSerial.text).isEqualTo(warranty.serialNumber)
            assertThat(tiEditDescription.text).isEqualTo(warranty.description)

            assertThat(tiEditDateStarting.text).isEqualTo("07/13/2022")
            assertThat(cbLifetime.isChecked).isTrue()
        }
    }

    @Test
    fun pressBackButton_popsBackStack() {
        pressBack()
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.warrantyDetailsFragment)
    }

    @Test
    fun clickOnNavigateUpBtn_popsBackStack() {
        onView(
            CoreMatchers.allOf(
                CoreMatchers.instanceOf(AppCompatImageButton::class.java),
                ViewMatchers.withParent(withId(testBinding.toolbar.id))
            )
        ).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.warrantyDetailsFragment)
    }

    @Test
    fun clickOnEditWarrantyMenuWithErrorStatus_returnsError() {
        onView(withId(testBinding.tiEditTitle.id)).perform(replaceText(""))
        onView(withId(R.id.action_menu_edit)).perform(click())

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun clickOnEditWarrantyMenuWithSuccessStatus_returnsSuccess() {
        onView(withId(testBinding.tiEditTitle.id)).perform(replaceText("new_title"))
        onView(withId(R.id.action_menu_edit)).perform(click())

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun clickOnEditWarrantyMenuWithSuccessStatus_navigatesToWarrantyDetails() {
        onView(withId(testBinding.tiEditTitle.id)).perform(replaceText("new_title"))
        onView(withId(R.id.action_menu_edit)).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.warrantyDetailsFragment)
    }
}