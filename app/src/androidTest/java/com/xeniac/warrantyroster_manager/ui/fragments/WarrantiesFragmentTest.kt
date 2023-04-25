package com.xeniac.warrantyroster_manager.ui.fragments

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.WarrantyInput
import com.xeniac.warrantyroster_manager.data.repository.FakeWarrantyRepository
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.databinding.FragmentWarrantiesBinding
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.warranty_management.presentation.warranties_list.WarrantyAdapter
import com.xeniac.warrantyroster_manager.warranty_management.presentation.WarrantyViewModel
import com.xeniac.warrantyroster_manager.warranty_management.presentation.warranties_list.WarrantiesFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@HiltAndroidTest
class WarrantiesFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var navController: TestNavHostController
    private lateinit var testBinding: FragmentWarrantiesBinding

    private lateinit var fakeMainRepository: FakeWarrantyRepository
    private lateinit var testViewModel: WarrantyViewModel

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)

        fakeMainRepository = FakeWarrantyRepository()

        testViewModel = WarrantyViewModel(FakeUserRepository(), fakeMainRepository)

        launchFragmentInHiltContainer<WarrantiesFragment> {
            navController.setGraph(R.navigation.nav_graph_main)
            Navigation.setViewNavController(requireView(), navController)
            navController.setCurrentDestination(R.id.warrantiesFragment)

            viewModel = testViewModel
            testBinding = binding
        }
    }

    @Test
    fun emptyWarrantiesList_showsWarrantiesEmptyList() {
        assertThat(testBinding.rv.isVisible).isFalse()
        assertThat(testBinding.groupEmptyWarrantiesList.isVisible).isTrue()
    }

    @Test
    fun warrantiesListWithItemInIt_showsWarrantiesList() {
        val warrantyInput = WarrantyInput(
            "title", "brand", "model", "serial", true,
            "2020-05-10", "",
            "description", "10", "uuid"
        )
        fakeMainRepository.addWarranty(warrantyInput)
        testViewModel.getWarrantiesListFromFirestore()

        assertThat(testBinding.groupEmptyWarrantiesList.isVisible).isFalse()
        assertThat(testBinding.rv.isVisible).isTrue()
    }

    @Test
    fun clickOnWarrantyItem_navigatesToWarrantyDetailsFragment() {
        val warrantyInput = WarrantyInput(
            "title", "brand", "model", "serial", true,
            "2020-05-10", "",
            "description", "10", "uuid"
        )
        fakeMainRepository.addWarranty(warrantyInput)
        testViewModel.getWarrantiesListFromFirestore()

        onView(withId(testBinding.rv.id)).perform(
            RecyclerViewActions.actionOnItemAtPosition<WarrantyAdapter.WarrantyViewHolder>(
                0, click()
            )
        )

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.warrantyDetailsFragment)
    }

    @Test
    fun clickOnSearchView_setsToolbarTitleToNull() {
        val warrantyInput = WarrantyInput(
            "title", "brand", "model", "serial", true,
            "2020-05-10", "",
            "description", "10", "uuid"
        )
        fakeMainRepository.addWarranty(warrantyInput)
        testViewModel.getWarrantiesListFromFirestore()

        onView(withId(testBinding.searchView.id)).perform(click())

        assertThat(testBinding.toolbar.title).isNull()
    }
}