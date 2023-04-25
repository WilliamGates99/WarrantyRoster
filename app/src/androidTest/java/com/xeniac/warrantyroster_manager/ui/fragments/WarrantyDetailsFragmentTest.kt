package com.xeniac.warrantyroster_manager.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.data.remote.models.Warranty
import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.WarrantyInput
import com.xeniac.warrantyroster_manager.data.repository.FakeWarrantyRepository
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.databinding.FragmentWarrantyDetailsBinding
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.warranty_management.presentation.WarrantyViewModel
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.warranty_management.presentation.warranty_details.WarrantyDetailsFragment
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
class WarrantyDetailsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private var testArgs: Bundle? = null
    private lateinit var testBinding: FragmentWarrantyDetailsBinding

    private lateinit var fakeMainRepository: FakeWarrantyRepository
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

        fakeMainRepository = FakeWarrantyRepository()
        testViewModel = WarrantyViewModel(FakeUserRepository(), fakeMainRepository)

        navController.setGraph(R.navigation.nav_graph_main)
        navController.navigate(
            WarrantiesFragmentDirections.actionWarrantiesFragmentToWarrantyDetailsFragment(warranty)
        )
        testArgs = navController.backStack.last().arguments

        launchFragmentInHiltContainer<WarrantyDetailsFragment>(fragmentArgs = testArgs) {
            Navigation.setViewNavController(requireView(), navController)

            viewModel = testViewModel
            testBinding = binding
        }
    }

    @Test
    fun pressBack_popsBackStack() {
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
    fun setWarrantyDetails() {
        testBinding.apply {
            assertThat(toolbar.title).isEqualTo(warranty.title)

            assertThat(tvBrand.text).isEqualTo(context.getString(R.string.warranty_details_empty_device))
            assertThat(tvBrand.currentTextColor).isEqualTo(
                ContextCompat.getColor(context, R.color.grayDark)
            )

            assertThat(tvModel.text).isEqualTo(context.getString(R.string.warranty_details_empty_device))
            assertThat(tvModel.currentTextColor).isEqualTo(
                ContextCompat.getColor(context, R.color.grayDark)
            )

            assertThat(tvSerial.text).isEqualTo(context.getString(R.string.warranty_details_empty_device))
            assertThat(tvSerial.currentTextColor).isEqualTo(
                ContextCompat.getColor(context, R.color.grayDark)
            )

            assertThat(tvDescription.text).isEqualTo(context.getString(R.string.warranty_details_empty_description))
            assertThat(tvDescription.currentTextColor).isEqualTo(
                ContextCompat.getColor(context, R.color.grayDark)
            )
            assertThat(tvDescription.gravity).isEqualTo(Gravity.CENTER)

            assertThat(tvDateStarting.text).isEqualTo("07/13/2022")
            assertThat(tvDateExpiry.text).isEqualTo(context.getString(R.string.warranty_details_is_lifetime))

            assertThat(tvStatus.text).isEqualTo(context.getString(R.string.warranty_details_status_valid))
            assertThat(tvStatus.currentTextColor).isEqualTo(
                ContextCompat.getColor(context, R.color.green)
            )
            assertThat(tvStatus.backgroundTintList).isEqualTo(
                ContextCompat.getColorStateList(context, R.color.green20)
            )
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
            .inRoot(isDialog()).check(matches(isDisplayed()))
    }

    @Test
    fun clickOnNegativeButtonOfDialog_dismissesTheDialog() {
        onView(withId(R.id.action_menu_delete)).perform(click())
        onView(withText(context.getString(R.string.warranty_details_delete_dialog_negative)))
            .inRoot(isDialog()).perform(click())

        onView(withText(context.getString(R.string.warranty_details_delete_dialog_title)))
            .check(doesNotExist())
    }

    @Test
    fun clickOnPositiveButtonOfDialogWithErrorStatus_returnsError() {
        fakeMainRepository.setShouldReturnNetworkError(true)
        onView(withId(R.id.action_menu_delete)).perform(click())
        onView(withText(context.getString(R.string.warranty_details_delete_dialog_positive)))
            .inRoot(isDialog()).perform(click())

        val responseEvent = testViewModel.deleteWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun clickOnPositiveButtonOfDialogWithSuccessStatus_returnsSuccess() {
        fakeMainRepository.addWarranty(
            WarrantyInput(
                title = "title",
                brand = null,
                model = null,
                serialNumber = null,
                lifetime = true,
                startingDate = "2022-07-13",
                expiryDate = null,
                description = null,
                uuid = "uuid"
            )
        )

        onView(withId(R.id.action_menu_delete)).perform(click())
        onView(withText(context.getString(R.string.warranty_details_delete_dialog_positive)))
            .inRoot(isDialog()).perform(click())

        val responseEvent = testViewModel.deleteWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }
}