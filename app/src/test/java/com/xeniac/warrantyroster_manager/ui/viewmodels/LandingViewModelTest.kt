package com.xeniac.warrantyroster_manager.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.xeniac.warrantyroster_manager.MainCoroutineRule
import com.xeniac.warrantyroster_manager.data.repository.FakePreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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

    @Test
    fun isUserLoggedIn_returnsDefaultValue() {
        val isUserLoggedIn = testViewModel.isUserLoggedIn()
        assertThat(isUserLoggedIn).isFalse()
    }
}