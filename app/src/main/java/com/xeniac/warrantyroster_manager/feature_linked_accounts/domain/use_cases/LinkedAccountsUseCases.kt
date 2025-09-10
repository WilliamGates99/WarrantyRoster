package com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases

import dagger.Lazy

data class LinkedAccountsUseCases(
    val getLinkedAccountProvidersUseCase: Lazy<GetLinkedAccountProvidersUseCase>,
    val getGoogleCredentialUseCase: Lazy<GetGoogleCredentialUseCase>,
    val linkGoogleAccountUseCase: Lazy<LinkGoogleAccountUseCase>,
    val unlinkGoogleAccountUseCase: Lazy<UnlinkGoogleAccountUseCase>,
    val unlinkXAccountUseCase: Lazy<UnlinkXAccountUseCase>,
    val unlinkGithubAccountUseCase: Lazy<UnlinkGithubAccountUseCase>
)