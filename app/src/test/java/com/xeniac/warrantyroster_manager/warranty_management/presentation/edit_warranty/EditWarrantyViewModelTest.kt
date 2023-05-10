package com.xeniac.warrantyroster_manager.warranty_management.presentation.edit_warranty

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.core.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.core.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_TAG_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.warranty_management.data.repository.FakeCategoryRepository
import com.xeniac.warrantyroster_manager.warranty_management.data.repository.FakeWarrantyRepository
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Category
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.WarrantyInput
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class EditWarrantyViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeCategoryRepository: FakeCategoryRepository
    private lateinit var fakeWarrantyRepository: FakeWarrantyRepository

    private lateinit var testViewModel: EditWarrantyViewModel

    private lateinit var allCategoryTitlesList: MutableList<String>
    private lateinit var warrantyInput: WarrantyInput
    private val warrantyId = "1"

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        fakeCategoryRepository = FakeCategoryRepository()
        fakeWarrantyRepository = FakeWarrantyRepository()

        fakeUserRepository.addUser("email@test.com", "password")

        allCategoryTitlesList = mutableListOf()
        for (i in 1..5) {
            val titleString = "Title $i"
            allCategoryTitlesList.add(titleString)

            fakeCategoryRepository.addCategory(
                id = i.toString(),
                title = mapOf(Pair(LOCALE_TAG_ENGLISH_UNITED_STATES, titleString)),
                icon = "$i.svg"
            )
        }

        warrantyInput = WarrantyInput(
            title = "title",
            brand = "brand",
            model = "model",
            serialNumber = "serialNumber",
            lifetime = true,
            startingDate = "2020-05-10",
            expiryDate = "",
            description = "description",
            categoryId = "10",
            uuid = "uuid"
        )

        fakeWarrantyRepository.addWarranty(warrantyInput, warrantyId)

        testViewModel = EditWarrantyViewModel(
            fakeUserRepository,
            fakeCategoryRepository,
            fakeWarrantyRepository,
            FakePreferencesRepository()
        )
    }

    @Test
    fun getCategoryTitleMapKey_returnsDefaultCategoryTitleMapKey() {
        val defaultCategoryTitleMapKey = LOCALE_TAG_ENGLISH_UNITED_STATES

        val result = testViewModel.getCategoryTitleMapKey()

        assertThat(result).isEqualTo(defaultCategoryTitleMapKey)
    }

    @Test
    fun getAllCategoryTitles_returnsAllCategoryTitlesList() {
        val categoryTitleMapKey = testViewModel.getCategoryTitleMapKey()
        val result = testViewModel.getAllCategoryTitles(categoryTitleMapKey)

        assertThat(result).isEqualTo(allCategoryTitlesList)
    }

    @Test
    fun getCategoryById_returnsCategory() {
        val id = "3"
        val testCategory = Category(
            id = id,
            title = mapOf(Pair(LOCALE_TAG_ENGLISH_UNITED_STATES, "Title $id")),
            icon = "$id.svg"
        )

        val result = testViewModel.getCategoryById(id)

        assertThat(result).isEqualTo(testCategory)
    }

    @Test
    fun getCategoryByTitle_returnsCategory() {
        val id = "3"
        val title = "Title $id"
        val testCategory = Category(
            id = id,
            title = mapOf(Pair(LOCALE_TAG_ENGLISH_UNITED_STATES, title)),
            icon = "$id.svg"
        )

        val result = testViewModel.getCategoryByTitle(title)

        assertThat(result).isEqualTo(testCategory)
    }

    @Test
    fun validateEditWarrantyInputsWithBlankTitle_returnsError() {
        testViewModel.validateEditWarrantyInputs(
            warrantyId = warrantyId,
            title = "",
            brand = "brand",
            model = "model",
            serialNumber = "serialNumber",
            isLifetime = false,
            startingDateInput = "2020-05-10",
            expiryDateInput = "2022-05-10",
            description = "description",
            categoryId = "10",
            selectedStartingDateInMillis = 50L,
            selectedExpiryDateInMillis = 100L
        )

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateEditWarrantyInputsWithBlankStartingDate_returnsError() {
        testViewModel.validateEditWarrantyInputs(
            warrantyId = warrantyId,
            title = "title",
            brand = "brand",
            model = "model",
            serialNumber = "serialNumber",
            isLifetime = false,
            startingDateInput = "",
            expiryDateInput = "2022-05-10",
            description = "description",
            categoryId = "10",
            selectedStartingDateInMillis = 50L,
            selectedExpiryDateInMillis = 100L
        )

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateEditWarrantyInputsWithFalseIsLifeTimeAndBlankExpiryDate_returnsError() {
        testViewModel.validateEditWarrantyInputs(
            warrantyId = warrantyId,
            title = "title",
            brand = "brand",
            model = "model",
            serialNumber = "serialNumber",
            isLifetime = false,
            startingDateInput = "2020-05-10",
            expiryDateInput = "",
            description = "description",
            categoryId = "10",
            selectedStartingDateInMillis = 50L,
            selectedExpiryDateInMillis = 100L
        )

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateEditWarrantyInputsWithTrueIsLifeTimeAndBlankExpiryDate_returnsSuccess() {
        testViewModel.validateEditWarrantyInputs(
            warrantyId = warrantyId,
            title = "title",
            brand = "brand",
            model = "model",
            serialNumber = "serialNumber",
            isLifetime = true,
            startingDateInput = "2020-05-10",
            expiryDateInput = "",
            description = "description",
            categoryId = "10",
            selectedStartingDateInMillis = 50L,
            selectedExpiryDateInMillis = 100L
        )

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun validateEditWarrantyInputsWithInvalidStartingDate_returnsError() {
        testViewModel.validateEditWarrantyInputs(
            warrantyId = warrantyId,
            title = "title",
            brand = "brand",
            model = "model",
            serialNumber = "serialNumber",
            isLifetime = false,
            startingDateInput = "2022-05-10",
            expiryDateInput = "2020-05-10",
            description = "description",
            categoryId = "10",
            selectedStartingDateInMillis = 100L,
            selectedExpiryDateInMillis = 50L
        )

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun validateEditWarrantyInputsWithValidInputs_returnsSuccess() {
        testViewModel.validateEditWarrantyInputs(
            warrantyId = warrantyId,
            title = "title",
            brand = "brand",
            model = "model",
            serialNumber = "serialNumber",
            isLifetime = false,
            startingDateInput = "2020-05-10",
            expiryDateInput = "2022-05-10",
            description = "description",
            categoryId = "10",
            selectedStartingDateInMillis = 50L,
            selectedExpiryDateInMillis = 100L
        )

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun updateWarrantyInFirestoreWithNonExistingWarrantyId_returnsError() {
        val warrantyInput = WarrantyInput(
            title = "title",
            brand = "brand",
            model = "model",
            serialNumber = "serialNumber",
            lifetime = true,
            startingDate = "2020-05-10",
            expiryDate = "",
            description = "description",
            categoryId = "10",
            uuid = "uuid"
        )

        testViewModel.updateWarrantyInFirestore(
            warrantyId = "2",
            warrantyInput = warrantyInput
        )

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun updateWarrantyInFirestoreWithValidInputsWithNoInternet_returnsError() {
        val warrantyInput = WarrantyInput(
            title = "title",
            brand = "brand",
            model = "model",
            serialNumber = "serialNumber",
            lifetime = true,
            startingDate = "2020-05-10",
            expiryDate = "",
            description = "description",
            categoryId = "10",
            uuid = "uuid"
        )

        fakeWarrantyRepository.setShouldReturnNetworkError(true)
        testViewModel.updateWarrantyInFirestore(warrantyId, warrantyInput)

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun updateWarrantyInFirestoreWithValidInputs_returnsSuccess() {
        val warrantyInput = WarrantyInput(
            title = "title",
            brand = "brand",
            model = "model",
            serialNumber = "serialNumber",
            lifetime = true,
            startingDate = "2020-05-10",
            expiryDate = "",
            description = "description",
            categoryId = "10",
            uuid = "uuid"
        )

        testViewModel.updateWarrantyInFirestore(warrantyId, warrantyInput)

        val responseEvent = testViewModel.updateWarrantyLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun getUpdatedWarrantyFromFirestoreWithNonExistingWarrantyId_returnsError() {
        testViewModel.getUpdatedWarrantyFromFirestore(warrantyId = "2")

        val responseEvent = testViewModel.updatedWarrantyLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun getUpdatedWarrantyFromFirestoreWithValidWarrantyIdWithNoInternet_returnsError_returnsError() {
        fakeWarrantyRepository.setShouldReturnNetworkError(true)
        testViewModel.getUpdatedWarrantyFromFirestore(warrantyId)

        val responseEvent = testViewModel.updatedWarrantyLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun getUpdatedWarrantyFromFirestoreWithValidWarrantyId_returnsError_returnsSuccess() {
        testViewModel.getUpdatedWarrantyFromFirestore(warrantyId)

        val responseEvent = testViewModel.updatedWarrantyLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }
}