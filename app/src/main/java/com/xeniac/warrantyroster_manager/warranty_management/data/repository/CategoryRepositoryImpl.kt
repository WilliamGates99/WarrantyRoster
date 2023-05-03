package com.xeniac.warrantyroster_manager.warranty_management.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.xeniac.warrantyroster_manager.di.CategoriesCollection
import com.xeniac.warrantyroster_manager.util.Constants.FIRESTORE_CATEGORIES_COLLECTION_SIZE
import com.xeniac.warrantyroster_manager.util.Constants.FIRESTORE_CATEGORIES_ICON
import com.xeniac.warrantyroster_manager.util.Constants.FIRESTORE_CATEGORIES_TITLE
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.UiText
import com.xeniac.warrantyroster_manager.warranty_management.data.mapper.toCategory
import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.CategoryDto
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Category
import com.xeniac.warrantyroster_manager.warranty_management.domain.repository.CategoryRepository
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    @CategoriesCollection private val categoriesCollectionRef: CollectionReference,
) : CategoryRepository {

    override suspend fun getAllCategoriesList(): List<Category> {
        var documents = getCachedCategoryDocuments()

        documents = if (documents.isEmpty()) {
            Timber.e("Cached category documents is empty.")
            getCategoryDocumentsFromFirestore()
        } else if (documents.size != FIRESTORE_CATEGORIES_COLLECTION_SIZE) {
            Timber.e("Cached category documents are less than categories collection size.")
            getCategoryDocumentsFromFirestore()
        } else {
            Timber.i("Cached category documents are valid.")
            documents
        }

        val categoriesList = mutableListOf<Category>()

        documents.forEach { document ->
            @Suppress("UNCHECKED_CAST")
            val categoryDto = CategoryDto(
                document.id,
                document.get(FIRESTORE_CATEGORIES_TITLE) as Map<String, String>,
                document.get(FIRESTORE_CATEGORIES_ICON).toString()
            )
            categoriesList.add(categoryDto.toCategory())
        }

        return categoriesList
    }

    override suspend fun getCachedCategoryDocuments(): List<DocumentSnapshot> = try {
        Timber.i("Getting cached category documents.")
        val querySnapshot = categoriesCollectionRef
            .orderBy(FIRESTORE_CATEGORIES_TITLE, Query.Direction.ASCENDING)
            .get(Source.CACHE)
            .await()

        querySnapshot.documents
    } catch (e: Exception) {
        Timber.e("getCachedCategoryDocuments Error: ${e.message}")
        emptyList()
    }

    override suspend fun getCategoryDocumentsFromFirestore(): List<DocumentSnapshot> = try {
        Timber.i("Getting category documents from firestore.")
        val querySnapshot = categoriesCollectionRef
            .orderBy(FIRESTORE_CATEGORIES_TITLE, Query.Direction.ASCENDING)
            .get(Source.DEFAULT)
            .await()

        querySnapshot.documents
    } catch (e: Exception) {
        Timber.e("getCategoryDocumentsFromFirestore Error: ${e.message}")
        emptyList()
    }

    override suspend fun getAllCategoryTitlesList(titleMapKey: String): Resource<List<String>> =
        try {
            val categoriesList = getAllCategoriesList()
            val titlesList = mutableListOf<String>()

            categoriesList.forEach { category ->
                titlesList.add(category.title[titleMapKey].toString())
            }

            Timber.i("All category titles retrieved successfully.")

            Resource.Success(titlesList)
        } catch (e: Exception) {
            Timber.e("getAllCategoryTitlesList Error: ${e.message}")
            Resource.Error(UiText.DynamicString(e.message.toString()))
        }

    override suspend fun getCategoryById(categoryId: String): Resource<Category> = try {
        val categoriesList = getAllCategoriesList()
        val category = categoriesList.find { it.id == categoryId }

        if (category != null) {
            Timber.i("Category successfully found by id.")
            Resource.Success(category)
        } else {
            Timber.e("Couldn't find category by id.")
            Resource.Error(UiText.DynamicString("Couldn't find category by id."))
        }
    } catch (e: Exception) {
        Timber.e("getCategoryById Error: ${e.message}")
        Resource.Error(UiText.DynamicString(e.message.toString()))
    }

    override suspend fun getCategoryByTitle(categoryTitle: String): Resource<Category> = try {
        val categoriesList = getAllCategoriesList()
        val category = categoriesList.find { it.title.containsValue(categoryTitle) }

        if (category != null) {
            Timber.i("Category successfully found by title.")
            Resource.Success(category)
        } else {
            Timber.e("Couldn't find category by title.")
            Resource.Error(UiText.DynamicString("Couldn't find category by id."))
        }
    } catch (e: Exception) {
        Timber.e("getCategoryByTitle Error: ${e.message}")
        Resource.Error(UiText.DynamicString(e.message.toString()))
    }
}