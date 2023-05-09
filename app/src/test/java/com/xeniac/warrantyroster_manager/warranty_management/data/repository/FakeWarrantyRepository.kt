package com.xeniac.warrantyroster_manager.warranty_management.data.repository

import com.google.firebase.firestore.Query
import com.xeniac.warrantyroster_manager.warranty_management.data.mapper.toWarranty
import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.WarrantyDto
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Warranty
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.WarrantyInput
import com.xeniac.warrantyroster_manager.warranty_management.domain.repository.WarrantyRepository

class FakeWarrantyRepository : WarrantyRepository {

    private val warranties = mutableListOf<Warranty>()

    private var shouldReturnNetworkError = false

    fun addWarranty(
        warrantyInput: WarrantyInput,
        warrantyId: String = "1"
    ) {
        val warranty = WarrantyDto(
            id = warrantyId,
            title = warrantyInput.title,
            brand = warrantyInput.brand,
            model = warrantyInput.model,
            serialNumber = warrantyInput.serialNumber,
            isLifetime = warrantyInput.lifetime,
            startingDate = warrantyInput.startingDate,
            expiryDate = warrantyInput.expiryDate,
            description = warrantyInput.description,
            categoryId = warrantyInput.categoryId
        ).toWarranty()
        warranties.add(warranty)
    }

    fun deleteWarranty(warrantyId: String) {
        val warranty = warranties.find { it.id == warrantyId }
        warranties.remove(warranty)
    }

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override fun getWarrantiesFromFirestore(): Query {
        TODO("Not yet implemented")
        /*
        if (shouldReturnNetworkError) {
            throw Exception()
        }

        if (warranties.size == 0) {
            throw Exception()
        }

        return warranties
        */
    }

    override suspend fun addWarrantyToFirestore(warrantyInput: WarrantyInput) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            addWarranty(warrantyInput)
        }
    }

    override suspend fun deleteWarrantyFromFirestore(warrantyId: String) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            val warranty = warranties.find { it.id == warrantyId }
            val isWarrantySuccessfullyDeleted = warranties.remove(warranty)
            val warrantyDoesNotExist = !isWarrantySuccessfullyDeleted

            if (warrantyDoesNotExist) {
                throw Exception()
            }
        }
    }

    override suspend fun updateWarrantyInFirestore(
        warrantyId: String,
        warrantyInput: WarrantyInput
    ) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            val warranty = warranties.find { it.id == warrantyId }
            val isWarrantySuccessfullyDeleted = warranties.remove(warranty)

            if (isWarrantySuccessfullyDeleted) {
                addWarranty(warrantyInput)
            } else {
                throw Exception()
            }
        }
    }

    override suspend fun getUpdatedWarrantyFromFirestore(warrantyId: String): Warranty {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            val warranty = warranties.find { it.id == warrantyId }

            return warranty ?: throw Exception()
        }
    }
}