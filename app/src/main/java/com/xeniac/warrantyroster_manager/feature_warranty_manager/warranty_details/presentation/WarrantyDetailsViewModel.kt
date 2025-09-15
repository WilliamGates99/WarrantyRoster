package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.WarrantyDetailsScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.utils.WarrantyNavType
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.NetworkObserverHelper.hasNetworkConnection
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.domain.errors.DeleteWarrantyError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.domain.use_cases.DeleteWarrantyUseCase
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation.states.WarrantyDetailsState
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation.utils.asUiText
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class WarrantyDetailsViewModel @Inject constructor(
    private val deleteWarrantyUseCase: Lazy<DeleteWarrantyUseCase>,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = savedStateHandle.toRoute<WarrantyDetailsScreen>(
        typeMap = mapOf(typeOf<Warranty>() to WarrantyNavType)
    )

    private val _state = MutableStateFlow(
        WarrantyDetailsState(warranty = args.warranty)
    )
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 2.minutes),
        initialValue = _state.value
    )

    private val _deleteWarrantyEventChannel = Channel<UiEvent>()
    val deleteWarrantyEventChannel = _deleteWarrantyEventChannel.receiveAsFlow()

    fun onAction(action: WarrantyDetailsAction) {
        when (action) {
            WarrantyDetailsAction.ShowDeleteWarrantyDialog -> showDeleteWarrantyDialog()
            WarrantyDetailsAction.DismissDeleteWarrantyDialog -> dismissDeleteWarrantyDialog()
            WarrantyDetailsAction.DeleteWarranty -> deleteWarranty()
        }
    }

    private fun showDeleteWarrantyDialog() = viewModelScope.launch {
        _state.update {
            it.copy(isDeleteWarrantyDialogVisible = true)
        }
    }

    private fun dismissDeleteWarrantyDialog() = viewModelScope.launch {
        _state.update {
            it.copy(isDeleteWarrantyDialogVisible = false)
        }
    }

    private fun deleteWarranty() {
        if (!hasNetworkConnection()) {
            _deleteWarrantyEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        deleteWarrantyUseCase.get()(
            warrantyId = _state.value.warranty.id
        ).onStart {
            _state.update {
                it.copy(isDeleteLoading = true)
            }
        }.onEach { result ->
            when (result) {
                is Result.Success -> {
                    _deleteWarrantyEventChannel.send(
                        UiEvent.ShowLongToast(
                            UiText.StringResource(R.string.warranty_details_delete_message_success)
                        )
                    )
                    _deleteWarrantyEventChannel.send(UiEvent.NavigateUp)
                }
                is Result.Error -> {
                    when (val error = result.error) {
                        DeleteWarrantyError.Network.FirebaseAuthUnauthorizedUser -> {
                            _deleteWarrantyEventChannel.send(UiEvent.ForceLogoutUnauthorizedUser)
                        }
                        else -> {
                            _deleteWarrantyEventChannel.send(
                                UiEvent.ShowLongSnackbar(error.asUiText())
                            )
                        }
                    }
                }
            }
        }.onCompletion {
            _state.update {
                it.copy(isDeleteLoading = false)
            }
        }.launchIn(scope = viewModelScope)
    }
}