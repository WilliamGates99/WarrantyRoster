package com.xeniac.warrantyroster_manager.warranty_management.domain.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Category

interface CategoryRepository {

    suspend fun getAllCategoriesList(): List<Category>

    suspend fun getCachedCategoryDocuments(): List<DocumentSnapshot>

    suspend fun getCategoryDocumentsFromFirestore(): List<DocumentSnapshot>

    fun getAllCategoryTitlesList(titleMapKey: String): List<String>

    fun getCategoryById(categoryId: String): Category

    fun getCategoryByTitle(categoryTitle: String): Category
}