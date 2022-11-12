package com.xeniac.warrantyroster_manager.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.data.remote.models.WarrantyInput
import com.xeniac.warrantyroster_manager.data.repository.FakeMainRepository
import com.xeniac.warrantyroster_manager.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WarrantyViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeMainRepository: FakeMainRepository
    private lateinit var fakePreferencesRepository: FakePreferencesRepository

    private lateinit var testViewModel: WarrantyViewModel

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        fakeMainRepository = FakeMainRepository()
        fakePreferencesRepository = FakePreferencesRepository()

        fakeUserRepository.addUser("email@test.com", "password")

        testViewModel = WarrantyViewModel(
            fakeUserRepository,
            fakeMainRepository,
            fakePreferencesRepository
        )
    }

    /*
    @Test
    fun getAllCategoryTitles_returnsAllTitles() = runTest {
        val mapKey = fakePreferencesRepository.getCategoryTitleMapKey()

        fakeMainRepository.addCategory("1", mapOf(Pair(mapKey, "title1")), "icon")
        fakeMainRepository.addCategory("2", mapOf(Pair(mapKey, "title2")), "icon")
        fakeMainRepository.addCategory("3", mapOf(Pair(mapKey, "title3")), "icon")
        testViewModel.getCategoriesFromFirestore()

        val result = testViewModel.getAllCategoryTitles()
        assertThat(result.size).isEqualTo(3)
    }

    @Test
    fun getCategoryById_returnsTheRequestedCategory() = runTest {
        val mapKey = fakePreferencesRepository.getCategoryTitleMapKey()
        val category3 = Category("3", mapOf(Pair(mapKey, "title3")), "icon")

        fakeMainRepository.addCategory("1", mapOf(Pair(mapKey, "title1")), "icon")
        fakeMainRepository.addCategory("2", mapOf(Pair(mapKey, "title2")), "icon")
        fakeMainRepository.addCategory(category3.id, category3.title, category3.icon)
        testViewModel.getCategoriesFromFirestore()

        val result = testViewModel.getCategoryById("3")
        assertThat(result).isEqualTo(category3)
    }

    @Test
    fun getCategoryByTitle_returnsTheRequestedCategory() = runTest {
        val mapKey = fakePreferencesRepository.getCategoryTitleMapKey()
        val title3 = "title3"
        val category3 = Category("3", mapOf(Pair(mapKey, title3)), "icon")

        fakeMainRepository.addCategory("1", mapOf(Pair(mapKey, "title1")), "icon")
        fakeMainRepository.addCategory("2", mapOf(Pair(mapKey, "title2")), "icon")
        fakeMainRepository.addCategory(category3.id, category3.title, category3.icon)
        testViewModel.getCategoriesFromFirestore()

        val result = testViewModel.getCategoryByTitle(title3)
        assertThat(result).isEqualTo(category3)
    }

    @Test
    fun getCategoriesWithNoInternet_returnsError() {
        fakeMainRepository.setShouldReturnNetworkError(true)
        testViewModel.getCategoriesFromFirestore()

        val responseEvent = testViewModel.categoriesLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandle).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun getCategoriesWithEmptyList_returnsError() {
        testViewModel.getCategoriesFromFirestore()

        val responseEvent = testViewModel.categoriesLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandle).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun getCategoriesWithValidList_returnsSuccess() = runTest {
        val mapKey = fakePreferencesRepository.getCategoryTitleMapKey()
        fakeMainRepository.addCategory("1", mapOf(Pair(mapKey, "title1")), "icon")
        fakeMainRepository.addCategory("2", mapOf(Pair(mapKey, "title2")), "icon")
        fakeMainRepository.addCategory("3", mapOf(Pair(mapKey, "title3")), "icon")
        testViewModel.getCategoriesFromFirestore()

        val responseEvent = testViewModel.categoriesLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandle).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun getWarrantiesWithNoInternet_returnsError() {
        fakeMainRepository.setShouldReturnNetworkError(true)
        testViewModel.getWarrantiesListFromFirestore()

        val responseEvent = testViewModel.warrantiesLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandle).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun getWarrantiesWithEmptyList_returnsError() {
        testViewModel.getWarrantiesListFromFirestore()

        val responseEvent = testViewModel.warrantiesLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandle).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun getWarrantiesWithValidList_returnsSuccess() {
        val warrantyInput = WarrantyInput(
            "title", "brand", "model", "serial", true,
            "2020-05-10", "",
            "description", "10", "uuid"
        )
        fakeMainRepository.addWarranty(warrantyInput, "1")
        fakeMainRepository.addWarranty(warrantyInput, "2")
        fakeMainRepository.addWarranty(warrantyInput, "3")
        testViewModel.getWarrantiesListFromFirestore()

        val responseEvent = testViewModel.warrantiesLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandle).isInstanceOf(Resource.Success::class.java)
    }
    */

    @Test
    fun checkAddWarrantyInputsWithBlankTitle_returnsError() {
        val blank = ""
        testViewModel.validateAddWarrantyInputs(
            blank, blank, blank, blank, false,
            "2020-05-10", "2020-05-10",
            blank, "10", 0L, 0L
        )

        val responseEvent = testViewModel.addWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkAddWarrantyInputsWithBlankStartingDate_returnsError() {
        val blank = ""
        testViewModel.validateAddWarrantyInputs(
            "title", blank, blank, blank, false, blank, "2020-05-10",
            blank, "10", 0L, 0L
        )

        val responseEvent = testViewModel.addWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkAddWarrantyInputsWithBlankExpiryDate_returnsError() {
        val blank = ""
        testViewModel.validateAddWarrantyInputs(
            "title", blank, blank, blank, false, "2020-05-10", blank,
            blank, "10", 0L, 0L
        )

        val responseEvent = testViewModel.addWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkAddWarrantyInputsWithInvalidStartingDate_returnsError() {
        val blank = ""
        testViewModel.validateAddWarrantyInputs(
            "title", blank, blank, blank, false,
            "2022-05-10", "2020-05-10",
            blank, "10", 100L, 50L
        )

        val responseEvent = testViewModel.addWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkAddWarrantyInputsWithIsLifeTimeAndBlankExpiryDate_returnsSuccess() {
        val blank = ""
        testViewModel.validateAddWarrantyInputs(
            "title", blank, blank, blank, true, "2020-05-10", blank,
            blank, "10", 0L, 0L
        )

        val responseEvent = testViewModel.addWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun checkAddWarrantyInputsWithValidInputs_returnsSuccess() {
        val blank = ""
        testViewModel.validateAddWarrantyInputs(
            "title", blank, blank, blank, false,
            "2020-05-10", "2022-05-10",
            blank, "10", 50L, 100L
        )

        val responseEvent = testViewModel.addWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
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
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
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
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun deleteWarrantyWithNotExistingWarrantyId_returnsError() {
        val warrantyInput = WarrantyInput(
            "title", "brand", "model", "serial", true,
            "2020-05-10", "",
            "description", "10", "uuid"
        )
        fakeMainRepository.addWarranty(warrantyInput)
        testViewModel.deleteWarrantyFromFirestore("2")

        val responseEvent = testViewModel.deleteWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun deleteWarrantyWithExistingWarrantyId_returnsSuccess() {
        val warrantyInput = WarrantyInput(
            "title", "brand", "model", "serial", true,
            "2020-05-10", "",
            "description", "10", "uuid"
        )
        fakeMainRepository.addWarranty(warrantyInput)
        testViewModel.deleteWarrantyFromFirestore("1")

        val responseEvent = testViewModel.deleteWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun checkEditWarrantyInputsWithBlankTitle_returnsError() {
        val blank = ""
        testViewModel.validateEditWarrantyInputs(
            "1", blank, blank, blank, blank, false,
            "2020-05-10", "2020-05-10",
            blank, "10", 0L, 0L
        )

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkEditWarrantyInputsWithBlankStartingDate_returnsError() {
        val blank = ""
        testViewModel.validateEditWarrantyInputs(
            "1", "title", blank, blank, blank, false,
            blank, "2020-05-10", blank, "10",
            0L, 0L
        )

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkEditWarrantyInputsWithBlankExpiryDate_returnsError() {
        val blank = ""
        testViewModel.validateEditWarrantyInputs(
            "1", "title", blank, blank, blank, false,
            "2020-05-10", blank, blank, "10",
            0L, 0L
        )

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkEditWarrantyInputsWithInvalidStartingDate_returnsError() {
        val blank = ""
        testViewModel.validateEditWarrantyInputs(
            "1", "title", blank, blank, blank, false,
            "2022-05-10", "2020-05-10",
            blank, "10", 100L, 50L
        )

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun checkEditWarrantyInputsWithIsLifeTimeAndBlankExpiryDate_returnsSuccess() {
        val title = "title"
        val brand = ""
        val model = ""
        val serial = ""
        val isLifeTime = true
        val startingDate = "2020-05-10"
        val expiryDate = ""
        val description = ""
        val categoryId = "10"
        val uuid = "uuid"
        val dataInMillis = 0L

        fakeMainRepository.addWarranty(
            WarrantyInput(
                title, brand, model, serial, isLifeTime,
                startingDate, expiryDate, description, categoryId, uuid
            )
        )
        testViewModel.validateEditWarrantyInputs(
            "1", title, brand, model, serial, isLifeTime,
            startingDate, expiryDate, description, categoryId, dataInMillis, dataInMillis
        )

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

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
        testViewModel.validateEditWarrantyInputs(
            "1", title, brand, model, serial, isLifeTime,
            startingDate, expiryDate, description, categoryId,
            50L, 100L
        )

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
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
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun getUpdatedWarrantyWithNoInternet_returnsError() {
        fakeMainRepository.setShouldReturnNetworkError(true)
        testViewModel.getUpdatedWarrantyFromFirestore("1")

        val responseEvent = testViewModel.updatedWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun getUpdatedWarrantyWithNonExistingId_returnsError() {
        testViewModel.getUpdatedWarrantyFromFirestore("1")

        val responseEvent = testViewModel.updatedWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun getUpdatedWarrantyWithValidId_returnsSuccess() {
        val warrantyInput = WarrantyInput(
            "title", "brand", "model", "serial", true,
            "2020-05-10", "",
            "description", "10", "uuid"
        )
        fakeMainRepository.addWarranty(warrantyInput, "1")
        testViewModel.getUpdatedWarrantyFromFirestore("1")

        val responseEvent = testViewModel.updatedWarrantyLiveData.getOrAwaitValue()
        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }
}