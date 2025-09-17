package com.xeniac.warrantyroster_manager.feature_base.di

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.xeniac.warrantyroster_manager.core.domain.repositories.MiscellaneousDataStoreRepository
import com.xeniac.warrantyroster_manager.core.domain.repositories.PermissionsDataStoreRepository
import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.AppReviewRepository
import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.AppUpdateRepository
import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.UpdateType
import com.xeniac.warrantyroster_manager.feature_base.domain.use_cases.BaseUseCases
import com.xeniac.warrantyroster_manager.feature_base.domain.use_cases.CheckFlexibleUpdateDownloadStateUseCase
import com.xeniac.warrantyroster_manager.feature_base.domain.use_cases.CheckForAppUpdatesUseCase
import com.xeniac.warrantyroster_manager.feature_base.domain.use_cases.CheckIsFlexibleUpdateStalledUseCase
import com.xeniac.warrantyroster_manager.feature_base.domain.use_cases.CheckIsImmediateUpdateStalledUseCase
import com.xeniac.warrantyroster_manager.feature_base.domain.use_cases.GetLatestAppVersionUseCase
import com.xeniac.warrantyroster_manager.feature_base.domain.use_cases.GetNotificationPermissionCountUseCase
import com.xeniac.warrantyroster_manager.feature_base.domain.use_cases.GetPreviousRateAppRequestDateTimeUseCase
import com.xeniac.warrantyroster_manager.feature_base.domain.use_cases.GetSelectedRateAppOptionUseCase
import com.xeniac.warrantyroster_manager.feature_base.domain.use_cases.RequestInAppReviewsUseCase
import com.xeniac.warrantyroster_manager.feature_base.domain.use_cases.StoreNotificationPermissionCountUseCase
import com.xeniac.warrantyroster_manager.feature_base.domain.use_cases.StorePreviousRateAppRequestDateTimeUseCase
import com.xeniac.warrantyroster_manager.feature_base.domain.use_cases.StoreSelectedRateAppOptionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

typealias FirstInstallTimeInMs = Long

@Module
@InstallIn(ViewModelComponent::class)
internal object HomeModule {

    @Provides
    @ViewModelScoped
    fun provideAppUpdateType(): UpdateType = AppUpdateType.FLEXIBLE

    @Provides
    @ViewModelScoped
    fun provideAppUpdateManager(
        @ApplicationContext context: Context
    ): AppUpdateManager = AppUpdateManagerFactory.create(context)

    @Provides
    @ViewModelScoped
    fun provide(
        appUpdateType: UpdateType
    ): AppUpdateOptions = AppUpdateOptions.newBuilder(appUpdateType).apply {
        setAllowAssetPackDeletion(true)
    }.build()

    @Provides
    @ViewModelScoped
    fun provideReviewManager(
        @ApplicationContext context: Context
    ): ReviewManager = ReviewManagerFactory.create(context)

    @Provides
    @ViewModelScoped
    fun provideFirstInstallTimeInMs(
        @ApplicationContext context: Context
    ): FirstInstallTimeInMs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.packageManager.getPackageInfo(
            /* packageName = */ context.packageName,
            /* flags = */ PackageManager.PackageInfoFlags.of(0)
        ).firstInstallTime
    } else {
        context.packageManager.getPackageInfo(
            /* packageName = */ context.packageName,
            /* flags = */ 0
        ).firstInstallTime
    }

    @Provides
    @ViewModelScoped
    fun provideCheckFlexibleUpdateDownloadStateUseCase(
        appUpdateRepository: AppUpdateRepository
    ): CheckFlexibleUpdateDownloadStateUseCase = CheckFlexibleUpdateDownloadStateUseCase(
        appUpdateRepository
    )

    @Provides
    @ViewModelScoped
    fun provideCheckIsFlexibleUpdateStalledUseCase(
        appUpdateRepository: AppUpdateRepository
    ): CheckIsFlexibleUpdateStalledUseCase = CheckIsFlexibleUpdateStalledUseCase(
        appUpdateRepository
    )

    @Provides
    @ViewModelScoped
    fun provideCheckIsImmediateUpdateStalledUseCase(
        appUpdateRepository: AppUpdateRepository
    ): CheckIsImmediateUpdateStalledUseCase = CheckIsImmediateUpdateStalledUseCase(
        appUpdateRepository
    )

    @Provides
    @ViewModelScoped
    fun provideCheckForAppUpdatesUseCase(
        appUpdateRepository: AppUpdateRepository
    ): CheckForAppUpdatesUseCase = CheckForAppUpdatesUseCase(appUpdateRepository)

    @Provides
    @ViewModelScoped
    fun provideRequestInAppReviewsUseCase(
        appReviewRepository: AppReviewRepository
    ): RequestInAppReviewsUseCase = RequestInAppReviewsUseCase(appReviewRepository)

    @Provides
    @ViewModelScoped
    fun provideGetLatestAppVersionUseCase(
        appUpdateRepository: AppUpdateRepository
    ): GetLatestAppVersionUseCase = GetLatestAppVersionUseCase(appUpdateRepository)

    @Provides
    @ViewModelScoped
    fun provideGetNotificationPermissionCountUseCase(
        permissionsDataStoreRepository: PermissionsDataStoreRepository
    ): GetNotificationPermissionCountUseCase = GetNotificationPermissionCountUseCase(
        permissionsDataStoreRepository
    )

    @Provides
    @ViewModelScoped
    fun provideStoreNotificationPermissionCountUseCase(
        permissionsDataStoreRepository: PermissionsDataStoreRepository
    ): StoreNotificationPermissionCountUseCase = StoreNotificationPermissionCountUseCase(
        permissionsDataStoreRepository
    )

    @Provides
    @ViewModelScoped
    fun provideGetSelectedRateAppOptionUseCase(
        miscellaneousDataStoreRepository: MiscellaneousDataStoreRepository
    ): GetSelectedRateAppOptionUseCase = GetSelectedRateAppOptionUseCase(
        miscellaneousDataStoreRepository
    )

    @Provides
    @ViewModelScoped
    fun provideStoreSelectedRateAppOptionUseCase(
        miscellaneousDataStoreRepository: MiscellaneousDataStoreRepository
    ): StoreSelectedRateAppOptionUseCase = StoreSelectedRateAppOptionUseCase(
        miscellaneousDataStoreRepository
    )

    @Provides
    @ViewModelScoped
    fun provideGetPreviousRateAppRequestDateTimeUseCase(
        miscellaneousDataStoreRepository: MiscellaneousDataStoreRepository
    ): GetPreviousRateAppRequestDateTimeUseCase = GetPreviousRateAppRequestDateTimeUseCase(
        miscellaneousDataStoreRepository
    )

    @Provides
    @ViewModelScoped
    fun provideStorePreviousRateAppRequestDateTimeUseCase(
        miscellaneousDataStoreRepository: MiscellaneousDataStoreRepository
    ): StorePreviousRateAppRequestDateTimeUseCase = StorePreviousRateAppRequestDateTimeUseCase(
        miscellaneousDataStoreRepository
    )

    @Provides
    @ViewModelScoped
    fun provideBaseUseCases(
        checkFlexibleUpdateDownloadStateUseCase: CheckFlexibleUpdateDownloadStateUseCase,
        checkIsFlexibleUpdateStalledUseCase: CheckIsFlexibleUpdateStalledUseCase,
        checkIsImmediateUpdateStalledUseCase: CheckIsImmediateUpdateStalledUseCase,
        checkForAppUpdatesUseCase: CheckForAppUpdatesUseCase,
        requestInAppReviewsUseCase: RequestInAppReviewsUseCase,
        getLatestAppVersionUseCase: GetLatestAppVersionUseCase,
        getNotificationPermissionCountUseCase: GetNotificationPermissionCountUseCase,
        storeNotificationPermissionCountUseCase: StoreNotificationPermissionCountUseCase,
        getSelectedRateAppOptionUseCase: GetSelectedRateAppOptionUseCase,
        storeSelectedRateAppOptionUseCase: StoreSelectedRateAppOptionUseCase,
        getPreviousRateAppRequestDateTimeUseCase: GetPreviousRateAppRequestDateTimeUseCase,
        storePreviousRateAppRequestDateTimeUseCase: StorePreviousRateAppRequestDateTimeUseCase
    ): BaseUseCases = BaseUseCases(
        { checkFlexibleUpdateDownloadStateUseCase },
        { checkIsFlexibleUpdateStalledUseCase },
        { checkIsImmediateUpdateStalledUseCase },
        { checkForAppUpdatesUseCase },
        { requestInAppReviewsUseCase },
        { getLatestAppVersionUseCase },
        { getNotificationPermissionCountUseCase },
        { storeNotificationPermissionCountUseCase },
        { getSelectedRateAppOptionUseCase },
        { storeSelectedRateAppOptionUseCase },
        { getPreviousRateAppRequestDateTimeUseCase },
        { storePreviousRateAppRequestDateTimeUseCase }
    )
}