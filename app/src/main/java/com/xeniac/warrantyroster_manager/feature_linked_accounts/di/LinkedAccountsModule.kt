package com.xeniac.warrantyroster_manager.feature_linked_accounts.di

import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.LinkedAccountsRepository
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.UnlinkAccountsRepository
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases.GetLinkedAccountProvidersUseCase
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases.LinkedAccountsUseCases
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases.UnlinkGithubAccountUseCase
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases.UnlinkGoogleAccountUseCase
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases.UnlinkXAccountUseCase
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
    fun provideUnlinkGoogleAccountUseCase(
        unlinkAccountsRepository: UnlinkAccountsRepository
    ): UnlinkGoogleAccountUseCase = UnlinkGoogleAccountUseCase(unlinkAccountsRepository)

    @Provides
    @ViewModelScoped
    fun provideUnlinkXAccountUseCase(
        unlinkAccountsRepository: UnlinkAccountsRepository
    ): UnlinkXAccountUseCase = UnlinkXAccountUseCase(unlinkAccountsRepository)

    @Provides
    @ViewModelScoped
    fun provideUnlinkGithubAccountUseCase(
        unlinkAccountsRepository: UnlinkAccountsRepository
    ): UnlinkGithubAccountUseCase = UnlinkGithubAccountUseCase(unlinkAccountsRepository)

    @Provides
    @ViewModelScoped
    fun provideLinkedAccountsUseCases(
        getLinkedAccountProvidersUseCase: GetLinkedAccountProvidersUseCase,
        unlinkGoogleAccountUseCase: UnlinkGoogleAccountUseCase,
        unlinkXAccountUseCase: UnlinkXAccountUseCase,
        unlinkGithubAccountUseCase: UnlinkGithubAccountUseCase
    ): LinkedAccountsUseCases = LinkedAccountsUseCases(
        { getLinkedAccountProvidersUseCase },
        { unlinkGoogleAccountUseCase },
        { unlinkXAccountUseCase },
        { unlinkGithubAccountUseCase }
    )
}