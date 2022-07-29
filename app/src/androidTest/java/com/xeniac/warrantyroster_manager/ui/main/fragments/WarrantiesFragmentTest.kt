package com.xeniac.warrantyroster_manager.ui.main.fragments

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.data.repository.FakeMainRepository
import com.xeniac.warrantyroster_manager.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.databinding.FragmentWarrantiesBinding
import com.xeniac.warrantyroster_manager.launchFragmentInHiltContainer
import com.xeniac.warrantyroster_manager.ui.viewmodels.MainViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule

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

    private lateinit var fakeMainRepository: FakeMainRepository
    private lateinit var testViewModel: MainViewModel

    @Before
    fun setUp() {
        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
        navController = TestNavHostController(context)

        fakeMainRepository = FakeMainRepository()

        testViewModel = MainViewModel(
            FakeUserRepository(),
            fakeMainRepository,
            FakePreferencesRepository()
        )

        launchFragmentInHiltContainer<WarrantiesFragment> {
            navController.setGraph(R.navigation.nav_graph_main)
            Navigation.setViewNavController(requireView(), navController)
            navController.setCurrentDestination(R.id.warrantiesFragment)

            viewModel = testViewModel
            testBinding = binding
        }
    }

    /*
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
    */
}