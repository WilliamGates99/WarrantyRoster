package com.xeniac.warrantyroster_manager.ui.fragments

import android.content.Context
import androidx.appcompat.widget.AppCompatImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.NavGraphMainDirections
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.data.repository.FakeMainRepository
import com.xeniac.warrantyroster_manager.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.databinding.FragmentAddWarrantyBinding
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.ui.viewmodels.WarrantyViewModel
import com.xeniac.warrantyroster_manager.utils.Resource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@HiltAndroidTest
class AddWarrantyFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentAddWarrantyBinding

    private lateinit var testViewModel: WarrantyViewModel

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

        launchFragmentInHiltContainer<AddWarrantyFragment> {
            navController.setGraph(R.navigation.nav_graph_main)
            Navigation.setViewNavController(requireView(), navController)
            navController.navigate(NavGraphMainDirections.actionMainActivityToAddWarrantyFragment())

            viewModel = testViewModel
            testBinding = binding
            startingDateInput = "2022-07-13"
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
    fun pressBackButton_popsBackStack() {
        pressBack()
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
    fun clickOnAddWarrantyMenuWithErrorStatus_returnsError() {
        onView(withId(R.id.action_menu_add)).perform(click())

        val responseEvent = testViewModel.addWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun clickOnAddWarrantyMenuWithSuccessStatus_returnsSuccess() {
        testBinding.apply {
            cbLifetime.isChecked = true
            onView(withId(tiEditTitle.id)).perform(replaceText("title"))
            onView(withId(tiEditDateStarting.id)).perform(replaceText("title"))
        }
        onView(withId(R.id.action_menu_add)).perform(click())

        val responseEvent = testViewModel.addWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun clickOnAddWarrantyMenuWithSuccessStatus_navigatesToWarrantiesFragment() {
        testBinding.apply {
            cbLifetime.isChecked = true
            onView(withId(tiEditTitle.id)).perform(replaceText("title"))
            onView(withId(tiEditDateStarting.id)).perform(replaceText("title"))
        }
        onView(withId(R.id.action_menu_add)).perform(click())

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.warrantiesFragment)
    }
}