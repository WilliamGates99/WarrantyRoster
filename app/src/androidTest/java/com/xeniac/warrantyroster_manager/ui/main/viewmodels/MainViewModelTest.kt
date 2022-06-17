package com.xeniac.warrantyroster_manager.ui.main.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.data.remote.models.WarrantyInput
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.repositories.FakeMainRepository
import com.xeniac.warrantyroster_manager.repositories.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.utils.Status
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@HiltAndroidTest
class MainViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeMainRepository: FakeMainRepository

    private lateinit var testViewModel: MainViewModel

    @Before
    fun setUp() {
        hiltRule.inject()

        fakeMainRepository = FakeMainRepository()

        testViewModel = MainViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeMainRepository,
            FakePreferencesRepository()
        )
    }

//    @Test
//    fun getAllCategoryTitles() {
//        val titles = testViewModel.getAllCategoryTitles()
//    }

//    @Test
//    fun getCategoryById(){
//
//    }

//    @Test
//    fun getCategoryByTitle(){
//
//    }

//    @Test
//    fun getCategoriesWithNoInternet_returnsError() {
//        fakeMainRepository.setShouldReturnNetworkError(true)
//        testViewModel.getCategoriesFromFirestore()
//
//        val responseEvent = testViewModel.categoriesLiveData.getOrAwaitValue()
//        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
//    }

//    @Test
//    fun getWarrantiesWithNoInternet_returnsError() {
//        fakeMainRepository.setShouldReturnNetworkError(true)
//        testViewModel.getWarrantiesListFromFirestore()
//
//        val responseEvent = testViewModel.warrantiesLiveData.getOrAwaitValue()
//        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
//    }

    @Test
    fun checkAddWarrantyInputsWithBlankTitle_returnsError() {
        val blank = ""
        testViewModel.checkAddWarrantyInputs(
            blank, blank, blank, blank, false,
            "2020-05-10", "2020-05-10",
            blank, "10", 0L, 0L
        )

        val responseEvent = testViewModel.addWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkAddWarrantyInputsWithBlankStartingDate_returnsError() {
        val blank = ""
        testViewModel.checkAddWarrantyInputs(
            "title", blank, blank, blank, false, blank, "2020-05-10",
            blank, "10", 0L, 0L
        )

        val responseEvent = testViewModel.addWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkAddWarrantyInputsWithBlankExpiryDate_returnsError() {
        val blank = ""
        testViewModel.checkAddWarrantyInputs(
            "title", blank, blank, blank, false, "2020-05-10", blank,
            blank, "10", 0L, 0L
        )

        val responseEvent = testViewModel.addWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkAddWarrantyInputsWithInvalidStartingDate_returnsError() {
        val blank = ""
        testViewModel.checkAddWarrantyInputs(
            "title", blank, blank, blank, false,
            "2022-05-10", "2020-05-10",
            blank, "10", 100L, 50L
        )

        val responseEvent = testViewModel.addWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkAddWarrantyInputsWithIsLifeTimeAndBlankExpiryDate_returnsSuccess() {
        val blank = ""
        testViewModel.checkAddWarrantyInputs(
            "title", blank, blank, blank, true, "2020-05-10", blank,
            blank, "10", 0L, 0L
        )

        val responseEvent = testViewModel.addWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun checkAddWarrantyInputsWithValidInputs_returnsSuccess() {
        val blank = ""
        testViewModel.checkAddWarrantyInputs(
            "title", blank, blank, blank, false,
            "2020-05-10", "2022-05-10",
            blank, "10", 50L, 100L
        )

        val responseEvent = testViewModel.addWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun addWarrantyWithNoInternet_returnsError() {
        fakeMainRepository.setShouldReturnNetworkError(true)
        val warrantyInput = WarrantyInput(
            "title", "brand", "model", "serial", true,
            "2020-05-10", "",
            "description", "10", "uuid"
        )
        testViewModel.addWarrantyToFirestore(warrantyInput)

        val responseEvent = testViewModel.addWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkEditWarrantyInputsWithBlankTitle_returnsError() {
        val blank = ""
        testViewModel.checkEditWarrantyInputs(
            "1", blank, blank, blank, blank, false,
            "2020-05-10", "2020-05-10",
            blank, "10", 0L, 0L
        )

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkEditWarrantyInputsWithBlankStartingDate_returnsError() {
        val blank = ""
        testViewModel.checkEditWarrantyInputs(
            "1", "title", blank, blank, blank, false,
            blank, "2020-05-10", blank, "10",
            0L, 0L
        )

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkEditWarrantyInputsWithBlankExpiryDate_returnsError() {
        val blank = ""
        testViewModel.checkEditWarrantyInputs(
            "1", "title", blank, blank, blank, false,
            "2020-05-10", blank, blank, "10",
            0L, 0L
        )

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun checkEditWarrantyInputsWithInvalidStartingDate_returnsError() {
        val blank = ""
        testViewModel.checkEditWarrantyInputs(
            "1", "title", blank, blank, blank, false,
            "2022-05-10", "2020-05-10",
            blank, "10", 100L, 50L
        )

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

//    @Test
//    fun checkEditWarrantyInputsWithIsLifeTimeAndBlankExpiryDate_returnsSuccess() {
//        val title = "title"
//        val brand = ""
//        val model = ""
//        val serial = ""
//        val isLifeTime = true
//        val startingDate = "2020-05-10"
//        val expiryDate = ""
//        val description = ""
//        val categoryId = "10"
//        val uuid = "uuid"
//        val dataInMillis = 0L
//
//        fakeMainRepository.addWarranty(
//            WarrantyInput(
//                title, brand, model, serial, isLifeTime,
//                startingDate, expiryDate, description, categoryId, uuid
//            )
//        )
//        testViewModel.checkEditWarrantyInputs(
//            "1", title, brand, model, serial, isLifeTime,
//            startingDate, expiryDate, description, categoryId, dataInMillis, dataInMillis
//        )
//
//        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()
//        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
//    }

    @Test
    fun checkEditWarrantyInputsWithValidInputs_returnsSuccess() {
        val title = "title"
        val brand = ""
        val model = ""
        val serial = ""
        val isLifeTime = false
        val startingDate = "2020-05-10"
        val expiryDate = "2022-05-10"
        val description = ""
        val categoryId = "10"
        val uuid = "uuid"

        fakeMainRepository.addWarranty(
            WarrantyInput(
                title, brand, model, serial, isLifeTime,
                startingDate, expiryDate, description, categoryId, uuid
            )
        )
        testViewModel.checkEditWarrantyInputs(
            "1", title, brand, model, serial, isLifeTime,
            startingDate, expiryDate, description, categoryId,
            50L, 100L
        )

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun updateWarrantyWithNoInternet_returnsError() {
        fakeMainRepository.setShouldReturnNetworkError(true)
        val warrantyInput = WarrantyInput(
            "title", "brand", "model", "serial", true,
            "2020-05-10", "",
            "description", "10", "uuid"
        )
        testViewModel.updateWarrantyInFirestore("1", warrantyInput)

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun deleteWarrantyWithNoInternet_returnsError() {
        fakeMainRepository.setShouldReturnNetworkError(true)
        val warrantyInput = WarrantyInput(
            "title", "brand", "model", "serial", true,
            "2020-05-10", "",
            "description", "10", "uuid"
        )
        fakeMainRepository.addWarranty(warrantyInput)
        testViewModel.deleteWarrantyFromFirestore("1")

        val responseEvent = testViewModel.deleteWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun deleteWarrantyWithNotExistingWarrantyId_returnsError() {
        fakeMainRepository.setShouldReturnNetworkError(true)
        val warrantyInput = WarrantyInput(
            "title", "brand", "model", "serial", true,
            "2020-05-10", "",
            "description", "10", "uuid"
        )
        fakeMainRepository.addWarranty(warrantyInput)
        testViewModel.deleteWarrantyFromFirestore("2")

        val responseEvent = testViewModel.deleteWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun deleteWarrantyWithExistingWarrantyId_returnsSuccess() {
        fakeMainRepository.setShouldReturnNetworkError(true)
        val warrantyInput = WarrantyInput(
            "title", "brand", "model", "serial", true,
            "2020-05-10", "",
            "description", "10", "uuid"
        )
        fakeMainRepository.addWarranty(warrantyInput)
        testViewModel.deleteWarrantyFromFirestore("1")

        val responseEvent = testViewModel.deleteWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }
}