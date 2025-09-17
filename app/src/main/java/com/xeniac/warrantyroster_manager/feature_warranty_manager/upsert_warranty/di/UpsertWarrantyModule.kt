package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.di

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.use_cases.ObserveCategoriesUseCase
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.repositories.AddWarrantyRepository
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.repositories.EditWarrantyRepository
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.use_cases.AddWarrantyUseCase
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.use_cases.EditWarrantyUseCase
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.use_cases.UpsertWarrantyUseCases
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation.ValidateBrand
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation.ValidateDescription
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation.ValidateModel
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation.ValidateSelectedCategory
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation.ValidateSerialNumber
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation.ValidateStartingAndExpiryDates
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation.ValidateTitle
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation.ValidateWarrantyId
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object UpsertWarrantyModule {

    @Provides
    @ViewModelScoped
    fun provideValidateWarrantyId(): ValidateWarrantyId = ValidateWarrantyId()

    @Provides
    @ViewModelScoped
    fun provideValidateTitle(): ValidateTitle = ValidateTitle()

    @Provides
    @ViewModelScoped
    fun provideValidateBrand(): ValidateBrand = ValidateBrand()

    @Provides
    @ViewModelScoped
    fun provideValidateModel(): ValidateModel = ValidateModel()

    @Provides
    @ViewModelScoped
    fun provideValidateSerialNumber(): ValidateSerialNumber = ValidateSerialNumber()

    @Provides
    @ViewModelScoped
    fun provideValidateDescription(): ValidateDescription = ValidateDescription()

    @Provides
    @ViewModelScoped
    fun provideValidateSelectedCategory(): ValidateSelectedCategory = ValidateSelectedCategory()

    @Provides
    @ViewModelScoped
    fun provideValidateStartingAndExpiryDates(): ValidateStartingAndExpiryDates =
        ValidateStartingAndExpiryDates()

    @Provides
    @ViewModelScoped
    fun provideAddWarrantyUseCase(
        addWarrantyRepository: AddWarrantyRepository,
        validateTitle: ValidateTitle,
        validateBrand: ValidateBrand,
        validateModel: ValidateModel,
        validateSerialNumber: ValidateSerialNumber,
        validateDescription: ValidateDescription,
        validateSelectedCategory: ValidateSelectedCategory,
        validateStartingAndExpiryDates: ValidateStartingAndExpiryDates
    ): AddWarrantyUseCase = AddWarrantyUseCase(
        addWarrantyRepository,
        validateTitle,
        validateBrand,
        validateModel,
        validateSerialNumber,
        validateDescription,
        validateSelectedCategory,
        validateStartingAndExpiryDates
    )

    @Provides
    @ViewModelScoped
    fun provideEditWarrantyUseCase(
        editWarrantyRepository: EditWarrantyRepository,
        validateWarrantyId: ValidateWarrantyId,
        validateTitle: ValidateTitle,
        validateBrand: ValidateBrand,
        validateModel: ValidateModel,
        validateSerialNumber: ValidateSerialNumber,
        validateDescription: ValidateDescription,
        validateSelectedCategory: ValidateSelectedCategory,
        validateStartingAndExpiryDates: ValidateStartingAndExpiryDates
    ): EditWarrantyUseCase = EditWarrantyUseCase(
        editWarrantyRepository,
        validateWarrantyId,
        validateTitle,
        validateBrand,
        validateModel,
        validateSerialNumber,
        validateDescription,
        validateSelectedCategory,
        validateStartingAndExpiryDates
    )

    @Provides
    @ViewModelScoped
    fun provideUpsertWarrantyUseCases(
        observeCategoriesUseCase: ObserveCategoriesUseCase,
        addWarrantyUseCase: AddWarrantyUseCase,
        editWarrantyUseCase: EditWarrantyUseCase
    ): UpsertWarrantyUseCases = UpsertWarrantyUseCases(
        { observeCategoriesUseCase },
        { addWarrantyUseCase },
        { editWarrantyUseCase }
    )
}