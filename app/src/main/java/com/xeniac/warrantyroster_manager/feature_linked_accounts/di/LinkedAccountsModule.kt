package com.xeniac.warrantyroster_manager.feature_linked_accounts.di

import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.LinkGithubAccountRepository
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.LinkGoogleAccountRepository
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.LinkXAccountRepository
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.LinkedAccountsRepository
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.UnlinkAccountsRepository
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases.CheckPendingLinkGithubAccountUseCase
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases.CheckPendingLinkXAccountUseCase
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases.GetGoogleCredentialUseCase
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases.GetLinkedAccountProvidersUseCase
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases.LinkGithubAccountUseCase
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases.LinkGoogleAccountUseCase
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases.LinkXAccountUseCase
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
    fun provideGetGoogleCredentialUseCase(
        nkGoogleAccountRepository: LinkGoogleAccountRepository
    ): GetGoogleCredentialUseCase = GetGoogleCredentialUseCase(nkGoogleAccountRepository)

    @Provides
    @ViewModelScoped
    fun provideLinkGoogleAccountUseCase(
        nkGoogleAccountRepository: LinkGoogleAccountRepository
    ): LinkGoogleAccountUseCase = LinkGoogleAccountUseCase(nkGoogleAccountRepository)

    @Provides
    @ViewModelScoped
    fun provideCheckPendingLinkXAccountUseCase(
        linkXAccountRepository: LinkXAccountRepository
    ): CheckPendingLinkXAccountUseCase = CheckPendingLinkXAccountUseCase(linkXAccountRepository)

    @Provides
    @ViewModelScoped
    fun provideLinkXAccountUseCase(
        linkXAccountRepository: LinkXAccountRepository
    ): LinkXAccountUseCase = LinkXAccountUseCase(linkXAccountRepository)

    @Provides
    @ViewModelScoped
    fun provideCheckPendingLinkGithubAccountUseCase(
        linkGithubAccountRepository: LinkGithubAccountRepository
    ): CheckPendingLinkGithubAccountUseCase = CheckPendingLinkGithubAccountUseCase(
        linkGithubAccountRepository
    )

    @Provides
    @ViewModelScoped
    fun provideLinkGithubAccountUseCase(
        linkGithubAccountRepository: LinkGithubAccountRepository
    ): LinkGithubAccountUseCase = LinkGithubAccountUseCase(linkGithubAccountRepository)

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
        getGoogleCredentialUseCase: GetGoogleCredentialUseCase,
        linkGoogleAccountUseCase: LinkGoogleAccountUseCase,
        checkPendingLinkXAccountUseCase: CheckPendingLinkXAccountUseCase,
        linkXAccountUseCase: LinkXAccountUseCase,
        checkPendingLinkGithubAccountUseCase: CheckPendingLinkGithubAccountUseCase,
        linkGithubAccountUseCase: LinkGithubAccountUseCase,
        unlinkGoogleAccountUseCase: UnlinkGoogleAccountUseCase,
        unlinkXAccountUseCase: UnlinkXAccountUseCase,
        unlinkGithubAccountUseCase: UnlinkGithubAccountUseCase
    ): LinkedAccountsUseCases = LinkedAccountsUseCases(
        { getLinkedAccountProvidersUseCase },
        { getGoogleCredentialUseCase },
        { linkGoogleAccountUseCase },
        { checkPendingLinkXAccountUseCase },
        { linkXAccountUseCase },
        { checkPendingLinkGithubAccountUseCase },
        { linkGithubAccountUseCase },
        { unlinkGoogleAccountUseCase },
        { unlinkXAccountUseCase },
        { unlinkGithubAccountUseCase }
    )
}