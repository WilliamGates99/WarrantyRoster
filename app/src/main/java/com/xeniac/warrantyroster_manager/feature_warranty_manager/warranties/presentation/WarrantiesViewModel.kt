package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.errors.ObserveCategoriesError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.presentation.utils.asUiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.errors.ObserveWarrantiesError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.use_cases.WarrantiesUseCases
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.states.WarrantiesState
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class WarrantiesViewModel @Inject constructor(
    private val warrantiesUseCases: WarrantiesUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = savedStateHandle.getMutableStateFlow(
        key = "warrantiesState",
        initialValue = WarrantiesState()
    )
    val state = _state.onStart {
        getCategories()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 5.seconds),
        initialValue = _state.value
    )

    private val _getWarrantiesEventChannel = Channel<UiEvent>()
    val getWarrantiesEventChannel = _getWarrantiesEventChannel.receiveAsFlow()

    private var getCategoriesJob: Job? = null
    private var getWarrantiesJob: Job? = null

    override fun onCleared() {
        getCategoriesJob?.cancel()
        getWarrantiesJob?.cancel()
        super.onCleared()
    }

    fun onAction(action: WarrantiesAction) {
        when (action) {
            WarrantiesAction.GetCategories -> getCategories()
            WarrantiesAction.GetWarranties -> getWarranties()
        }
    }

    private fun getCategories() {
        getCategoriesJob?.cancel()

        getCategoriesJob = warrantiesUseCases.observeCategoriesUseCase.get()().onStart {
            _state.update {
                it.copy(
                    isCategoriesLoading = true,
                    errorMessage = null
                )
            }
        }.onEach { result ->
            when (result) {
                is Result.Success -> {
                    _state.update {
                        it.copy(categories = result.data)
                    }
                    getWarranties()
                }
                is Result.Error -> {
                    when (val error = result.error) {
                        ObserveCategoriesError.Network.FirebaseAuthUnauthorizedUser -> {
                            _state.update {
                                it.copy(errorMessage = error.asUiText())
                            }
                            _getWarrantiesEventChannel.send(UiEvent.ForceLogoutUnauthorizedUser)
                        }
                        else -> {
                            _state.update {
                                it.copy(errorMessage = error.asUiText())
                            }
                        }
                    }
                }
            }
        }.onCompletion {
            _state.update {
                it.copy(isCategoriesLoading = false)
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun getWarranties() {
        getWarrantiesJob?.cancel()

        getWarrantiesJob = warrantiesUseCases.observeWarrantiesUseCase.get()(
            fetchedCategories = _state.value.categories
        ).onStart {
            _state.update {
                it.copy(
                    isWarrantiesLoading = true,
                    errorMessage = null
                )
            }
        }.onEach { result ->
            when (result) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            warranties = result.data,
                            isCategoriesLoading = false,
                            isWarrantiesLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    when (val error = result.error) {
                        ObserveWarrantiesError.Network.FirebaseAuthUnauthorizedUser -> {
                            _state.update {
                                it.copy(errorMessage = error.asUiText())
                            }
                            _getWarrantiesEventChannel.send(UiEvent.ForceLogoutUnauthorizedUser)
                        }
                        else -> {
                            _state.update {
                                it.copy(errorMessage = error.asUiText())
                            }
                        }
                    }
                }
            }
        }.onCompletion {
            _state.update {
                it.copy(isWarrantiesLoading = false)
            }
        }.launchIn(scope = viewModelScope)
    }
}