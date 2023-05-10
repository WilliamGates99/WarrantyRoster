package com.xeniac.warrantyroster_manager.warranty_management.presentation.warranty_details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
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
class WarrantyDetailsViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeCategoryRepository: FakeCategoryRepository
    private lateinit var fakeWarrantyRepository: FakeWarrantyRepository

    private lateinit var testViewModel: WarrantyDetailsViewModel

    private lateinit var allCategoryTitlesList: MutableList<String>

    @Before
    fun setUp() {
        fakeCategoryRepository = FakeCategoryRepository()
        fakeWarrantyRepository = FakeWarrantyRepository()

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

        testViewModel = WarrantyDetailsViewModel(
            fakeCategoryRepository,
            fakeWarrantyRepository
        )
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
    fun deleteWarrantyFromFirestoreWithNonExistingWarrantyId_returnsError() {
        testViewModel.deleteWarrantyFromFirestore("2")

        val responseEvent = testViewModel.deleteWarrantyLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun deleteWarrantyFromFirestoreWithExistingWarrantyIdWithNoInternet_returnsError() {
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

        fakeWarrantyRepository.addWarranty(warrantyInput)
        fakeWarrantyRepository.setShouldReturnNetworkError(true)
        testViewModel.deleteWarrantyFromFirestore("1")

        val responseEvent = testViewModel.deleteWarrantyLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun deleteWarrantyFromFirestoreWithExistingWarrantyId_returnsSuccess() {
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

        fakeWarrantyRepository.addWarranty(warrantyInput)
        testViewModel.deleteWarrantyFromFirestore("1")

        val responseEvent = testViewModel.deleteWarrantyLiveData.getOrAwaitValue()

        assertThat(responseEvent.getContentIfNotHandled()).isInstanceOf(Resource.Success::class.java)
    }
}