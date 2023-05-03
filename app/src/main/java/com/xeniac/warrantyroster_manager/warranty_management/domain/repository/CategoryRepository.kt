package com.xeniac.warrantyroster_manager.warranty_management.domain.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Category

interface CategoryRepository {

    suspend fun getAllCategoriesList(): List<Category>

    suspend fun getCachedCategoryDocuments(): List<DocumentSnapshot>

    suspend fun getCategoryDocumentsFromFirestore(): List<DocumentSnapshot>

    suspend fun getAllCategoryTitlesList(titleMapKey: String): Resource<List<String>>

    suspend fun getCategoryById(categoryId: String): Resource<Category>

    suspend fun getCategoryByTitle(categoryTitle: String): Resource<Category>
}