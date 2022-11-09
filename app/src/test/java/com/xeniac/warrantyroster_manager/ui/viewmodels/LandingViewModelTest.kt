package com.xeniac.warrantyroster_manager.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.data.repository.FakePreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule

@ExperimentalCoroutinesApi
class LandingViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var testViewModel: LandingViewModel

    @Before
    fun setUp() {
        testViewModel = LandingViewModel(FakePreferencesRepository())
    }
}