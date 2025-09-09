package com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases

import dagger.Lazy

data class LinkedAccountsUseCases(
    val getLinkedAccountProvidersUseCase: Lazy<GetLinkedAccountProvidersUseCase>,
)