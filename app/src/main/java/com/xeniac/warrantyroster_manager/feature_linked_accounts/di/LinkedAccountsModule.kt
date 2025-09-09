package com.xeniac.warrantyroster_manager.feature_linked_accounts.di

import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.LinkedAccountsRepository
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases.GetLinkedAccountProvidersUseCase
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases.LinkedAccountsUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object LinkedAccountsModule {

    @Provides
    @ViewModelScoped
    fun provideGetLinkedAccountProvidersUseCase(
        linkedAccountsRepository: LinkedAccountsRepository
    ): GetLinkedAccountProvidersUseCase = GetLinkedAccountProvidersUseCase(linkedAccountsRepository)

    @Provides
    @ViewModelScoped
    fun provideLinkedAccountsUseCases(
        getLinkedAccountProvidersUseCase: GetLinkedAccountProvidersUseCase,
    ): LinkedAccountsUseCases = LinkedAccountsUseCases(
        { getLinkedAccountProvidersUseCase },
    )
}