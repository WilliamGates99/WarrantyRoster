package com.xeniac.warrantyroster_manager.warranty_management.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.xeniac.warrantyroster_manager.BuildConfig
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_EMPTY_CATEGORY_LIST
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_TAG_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_TAG_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_TAG_PERSIAN_IRAN
import com.xeniac.warrantyroster_manager.warranty_management.data.mapper.toCategory
import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.CategoryDto
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Category
import com.xeniac.warrantyroster_manager.warranty_management.domain.repository.CategoryRepository
import timber.log.Timber

class FakeCategoryRepository : CategoryRepository {

    private val categoriesList = mutableListOf<Category>()

    private var shouldReturnNetworkError = false

    fun addCategory(id: String, title: Map<String, String>, icon: String) {
        categoriesList.add(
            CategoryDto(
                id = id,
                title = title,
                icon = icon
            ).toCategory()
        )
    }

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override suspend fun getAllCategoriesList(): List<Category> {
        if (categoriesList.isEmpty()) {
            throw Exception(ERROR_EMPTY_CATEGORY_LIST)
        }

        return categoriesList
    }

    override suspend fun getCachedCategoryDocuments(): List<DocumentSnapshot> = emptyList()

    override suspend fun getCategoryDocumentsFromFirestore(): List<DocumentSnapshot> = emptyList()

    override fun getAllCategoryTitlesList(titleMapKey: String): List<String> {
        val titlesList = mutableListOf<String>()

        categoriesList.forEach { category ->
            titlesList.add(category.title[titleMapKey].toString())
        }

        Timber.i("All category titles retrieved successfully.")

        return titlesList
    }

    override fun getCategoryById(categoryId: String): Category {
        val category = categoriesList.find { it.id == categoryId }

        return if (category != null) {
            Timber.i("Category successfully found by id.")
            category
        } else {
            Timber.e("Couldn't find category by id.")
            Category(
                id = "10",
                title = mapOf(
                    Pair(LOCALE_TAG_ENGLISH_UNITED_STATES, "Miscellaneous"),
                    Pair(LOCALE_TAG_ENGLISH_GREAT_BRITAIN, "Miscellaneous"),
                    Pair(LOCALE_TAG_PERSIAN_IRAN, "متفرقه")
                ),
                icon = BuildConfig.CATEGORY_MISCELLANEOUS_ICON
            )
        }
    }

    override fun getCategoryByTitle(categoryTitle: String): Category {
        val category = categoriesList.find { it.title.containsValue(categoryTitle) }

        return if (category != null) {
            Timber.i("Category successfully found by title.")
            category
        } else {
            Timber.e("Couldn't find category by title.")
            Category(
                id = "10",
                title = mapOf(
                    Pair(LOCALE_TAG_ENGLISH_UNITED_STATES, "Miscellaneous"),
                    Pair(LOCALE_TAG_ENGLISH_GREAT_BRITAIN, "Miscellaneous"),
                    Pair(LOCALE_TAG_PERSIAN_IRAN, "متفرقه")
                ),
                icon = BuildConfig.CATEGORY_MISCELLANEOUS_ICON
            )
        }
    }
}