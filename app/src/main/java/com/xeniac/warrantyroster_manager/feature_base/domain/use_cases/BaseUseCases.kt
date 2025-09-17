package com.xeniac.warrantyroster_manager.feature_base.domain.use_cases

import dagger.Lazy

data class BaseUseCases(
    val checkFlexibleUpdateDownloadStateUseCase: Lazy<CheckFlexibleUpdateDownloadStateUseCase>,
    val checkIsFlexibleUpdateStalledUseCase: Lazy<CheckIsFlexibleUpdateStalledUseCase>,
    val checkIsImmediateUpdateStalledUseCase: Lazy<CheckIsImmediateUpdateStalledUseCase>,
    val checkForAppUpdatesUseCase: Lazy<CheckForAppUpdatesUseCase>,
    val requestInAppReviewsUseCase: Lazy<RequestInAppReviewsUseCase>,
    val getLatestAppVersionUseCase: Lazy<GetLatestAppVersionUseCase>,
    val getNotificationPermissionCountUseCase: Lazy<GetNotificationPermissionCountUseCase>,
    val storeNotificationPermissionCountUseCase: Lazy<StoreNotificationPermissionCountUseCase>,
    val getSelectedRateAppOptionUseCase: Lazy<GetSelectedRateAppOptionUseCase>,
    val storeSelectedRateAppOptionUseCase: Lazy<StoreSelectedRateAppOptionUseCase>,
    val getPreviousRateAppRequestDateTimeUseCase: Lazy<GetPreviousRateAppRequestDateTimeUseCase>,
    val storePreviousRateAppRequestDateTimeUseCase: Lazy<StorePreviousRateAppRequestDateTimeUseCase>
)