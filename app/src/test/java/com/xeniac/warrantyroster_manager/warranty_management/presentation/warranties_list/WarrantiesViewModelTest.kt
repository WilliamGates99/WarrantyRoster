package com.xeniac.warrantyroster_manager.warranty_management.presentation.warranties_list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.core.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.getOrAwaitValue
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_TAG_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.warranty_management.data.repository.FakeCategoryRepository
import com.xeniac.warrantyroster_manager.warranty_management.data.repository.FakeWarrantyRepository
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Category
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WarrantiesViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeCategoryRepository: FakeCategoryRepository
    private lateinit var fakeWarrantyRepository: FakeWarrantyRepository

    private lateinit var testViewModel: WarrantiesViewModel

    @Before
    fun setUp() {
        fakeCategoryRepository = FakeCategoryRepository()
        fakeWarrantyRepository = FakeWarrantyRepository()

        testViewModel = WarrantiesViewModel(
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
    fun getAllCategoriesListWithEmptyList_returnsError() {
        testViewModel.getAllCategoriesList()

        val responseEvent = testViewModel.categoriesListLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun getAllCategoriesListWithFullList_returnsSuccess() {
        for (i in 1..5) {
            fakeCategoryRepository.addCategory(
                id = i.toString(),
                title = mapOf(Pair(LOCALE_TAG_ENGLISH_UNITED_STATES, "Title $i")),
                icon = "$i.svg"
            )
        }

        testViewModel.getAllCategoriesList()

        val responseEvent = testViewModel.categoriesListLiveData.getOrAwaitValue()

        assertThat(responseEvent.peekContent()).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun getCategoryById_returnsCategory() {
        val id = "3"
        val testCategory = Category(
            id = id,
            title = mapOf(Pair(LOCALE_TAG_ENGLISH_UNITED_STATES, "Title $id")),
            icon = "$id.svg"
        )

        for (i in 1..5) {
            fakeCategoryRepository.addCategory(
                id = i.toString(),
                title = mapOf(Pair(LOCALE_TAG_ENGLISH_UNITED_STATES, "Title $i")),
                icon = "$i.svg"
            )
        }

        val result = testViewModel.getCategoryById(id)

        assertThat(result).isEqualTo(testCategory)
    }
}