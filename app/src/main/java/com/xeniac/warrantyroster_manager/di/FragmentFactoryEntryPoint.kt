package com.xeniac.warrantyroster_manager.di

import com.xeniac.warrantyroster_manager.core.presentation.landing.LandingFragmentFactory
import com.xeniac.warrantyroster_manager.core.presentation.main.MainFragmentFactory
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
interface FragmentFactoryEntryPoint {

    fun getLandingFragmentFactory(): LandingFragmentFactory

    fun getMainFragmentFactory(): MainFragmentFactory
}