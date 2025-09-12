package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.repositories.WarrantiesRepository
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.states.WarrantiesState
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class WarrantiesViewModel @Inject constructor(
    private val warrantiesRepository: Lazy<WarrantiesRepository>,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = savedStateHandle.getMutableStateFlow(
        key = "warrantiesState",
        initialValue = WarrantiesState()
    )
    val state = _state.onStart {
        observeWarranties()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 2.minutes),
        initialValue = _state.value
    )

    private var observeWarrantiesJob: Job? = null

    fun onAction(action: WarrantiesAction) {
        when (action) {
            WarrantiesAction.ObserveWarranties -> observeWarranties()
        }
    }

    private fun observeWarranties() {
        // TODO: DO "NOT" CHECK NETWORK CONNECTION
        observeWarrantiesJob?.cancel()

        observeWarrantiesJob = warrantiesRepository.get().observeWarranties().onStart {
            Timber.d("onStart")
        }.onEach { result ->
            when (result) {
                is Result.Success -> {
                    Timber.i("warranties size = ${result.data.size}")
                }
                is Result.Error -> {
                    Timber.e("error = ${result.error}")
                }
            }
        }.onCompletion {
            Timber.d("onCompletion")
        }.launchIn(scope = viewModelScope)
    }
}