package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.utils.convertDigitsToEnglish
import com.xeniac.warrantyroster_manager.core.presentation.common.states.CustomTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.UpsertWarrantyScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.utils.WarrantyNavType
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.NetworkObserverHelper.hasNetworkConnection
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.errors.ObserveCategoriesError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.presentation.utils.asUiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.use_cases.UpsertWarrantyUseCases
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.states.UpsertWarrantyState
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
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import javax.inject.Inject
import kotlin.reflect.typeOf
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@HiltViewModel
class UpsertWarrantyViewModel @Inject constructor(
    private val upsertWarrantyUseCases: UpsertWarrantyUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = savedStateHandle.toRoute<UpsertWarrantyScreen>(
        typeMap = mapOf(typeOf<Warranty?>() to WarrantyNavType)
    )

    private val _state = savedStateHandle.getMutableStateFlow(
        key = "upsertWarrantyState",
        initialValue = UpsertWarrantyState(updatingWarranty = args.updatingWarranty)
    )
    val state = _state.onStart {
        val shouldSetUpdatingWarrantyData = with(_state.value) {
            !isUpdatingWarrantyDataSet && updatingWarranty != null
        }
        if (shouldSetUpdatingWarrantyData) {
            setUpdatingWarrantyData()
        }
        getCategories()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 2.minutes),
        initialValue = _state.value
    )

    private val _getCategoriesEventChannel = Channel<UiEvent>()
    val getCategoriesEventChannel = _getCategoriesEventChannel.receiveAsFlow()

    private val _upsertWarrantyEventChannel = Channel<UiEvent>()
    val upsertWarrantyEventChannel = _upsertWarrantyEventChannel.receiveAsFlow()

    private var getCategoriesJob: Job? = null

    override fun onCleared() {
        getCategoriesJob?.cancel()
        super.onCleared()
    }

    fun onAction(action: UpsertWarrantyAction) {
        when (action) {
            UpsertWarrantyAction.ShowWarrantiesBottomSheet -> showWarrantiesBottomSheet()
            UpsertWarrantyAction.DismissWarrantiesBottomSheet -> dismissWarrantiesBottomSheet()
            UpsertWarrantyAction.ShowStartingDatePickerDialog -> showStartingDatePickerDialog()
            UpsertWarrantyAction.DismissStartingDatePickerDialog -> dismissStartingDatePickerDialog()
            UpsertWarrantyAction.ShowExpiryDatePickerDialog -> showExpiryDatePickerDialog()
            UpsertWarrantyAction.DismissExpiryDatePickerDialog -> dismissExpiryDatePickerDialog()
            is UpsertWarrantyAction.TitleChanged -> titleChanged(action.newValue)
            is UpsertWarrantyAction.BrandChanged -> brandChanged(action.newValue)
            is UpsertWarrantyAction.ModelChanged -> modelChanged(action.newValue)
            is UpsertWarrantyAction.SerialNumberChanged -> serialNumberChanged(action.newValue)
            is UpsertWarrantyAction.DescriptionChanged -> descriptionChanged(action.newValue)
            is UpsertWarrantyAction.SelectedCategoryChanged -> selectedCategoryChanged(action.category)
            is UpsertWarrantyAction.IsLifetimeWarrantyChanged -> isLifetimeWarrantyChanged(action.isChecked)
            is UpsertWarrantyAction.StartingDateChanged -> startingDateChanged(action.startingDateInMs)
            is UpsertWarrantyAction.ExpiryDateChanged -> expiryDateChanged(action.expiryDateInMs)
            UpsertWarrantyAction.GetCategories -> getCategories()
            UpsertWarrantyAction.UpsertWarranty -> upsertWarranty()
        }
    }

    private fun setUpdatingWarrantyData(
        timeZone: TimeZone = TimeZone.currentSystemDefault()
    ) = viewModelScope.launch {
        _state.update { state ->
            state.updatingWarranty?.let {
                state.copy(
                    titleState = CustomTextFieldState(value = TextFieldValue(text = it.title)),
                    brandState = CustomTextFieldState(value = TextFieldValue(text = it.brand)),
                    modelState = CustomTextFieldState(value = TextFieldValue(text = it.model)),
                    serialNumberState = CustomTextFieldState(value = TextFieldValue(text = it.serialNumber)),
                    descriptionState = CustomTextFieldState(value = TextFieldValue(text = it.description)),
                    selectedCategory = it.category,
                    isLifetimeWarranty = it.isLifetime,
                    selectedStartingDate = it.startingDate.atStartOfDayIn(timeZone = timeZone),
                    selectedExpiryDate = it.expiryDate.atStartOfDayIn(timeZone = timeZone)
                )
            } ?: state
        }
    }

    private fun showWarrantiesBottomSheet() = viewModelScope.launch {
        _state.update {
            it.copy(isWarrantiesBottomSheetVisible = true)
        }
    }

    private fun dismissWarrantiesBottomSheet() = viewModelScope.launch {
        _state.update {
            it.copy(isWarrantiesBottomSheetVisible = false)
        }
    }

    private fun showStartingDatePickerDialog() = viewModelScope.launch {
        _state.update {
            it.copy(isStartingDatePickerDialogVisible = true)
        }
    }

    private fun dismissStartingDatePickerDialog() = viewModelScope.launch {
        _state.update {
            it.copy(isStartingDatePickerDialogVisible = false)
        }
    }

    private fun showExpiryDatePickerDialog() = viewModelScope.launch {
        _state.update {
            it.copy(isExpiryDatePickerDialogVisible = true)
        }
    }

    private fun dismissExpiryDatePickerDialog() = viewModelScope.launch {
        _state.update {
            it.copy(isExpiryDatePickerDialogVisible = false)
        }
    }

    private fun titleChanged(
        newValue: TextFieldValue
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                titleState = it.titleState.copy(
                    value = newValue.copy(
                        text = newValue.text.convertDigitsToEnglish(shouldTrim = false)
                    ),
                    errorText = null
                )
            )
        }
    }

    private fun brandChanged(
        newValue: TextFieldValue
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                brandState = it.brandState.copy(
                    value = newValue.copy(
                        text = newValue.text.convertDigitsToEnglish(shouldTrim = false)
                    ),
                    errorText = null
                )
            )
        }
    }

    private fun modelChanged(
        newValue: TextFieldValue
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                modelState = it.modelState.copy(
                    value = newValue.copy(
                        text = newValue.text.convertDigitsToEnglish(shouldTrim = false)
                    ),
                    errorText = null
                )
            )
        }
    }

    private fun serialNumberChanged(
        newValue: TextFieldValue
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                serialNumberState = it.serialNumberState.copy(
                    value = newValue.copy(
                        text = newValue.text.convertDigitsToEnglish(shouldTrim = false)
                    ),
                    errorText = null
                )
            )
        }
    }

    private fun descriptionChanged(
        newValue: TextFieldValue
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                brandState = it.brandState.copy(
                    value = newValue.copy(
                        text = newValue.text.convertDigitsToEnglish(shouldTrim = false)
                    ),
                    errorText = null
                )
            )
        }
    }

    private fun selectedCategoryChanged(
        category: WarrantyCategory
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                selectedCategory = category,
                selectedCategoryError = null
            )
        }
    }

    private fun isLifetimeWarrantyChanged(
        isChecked: Boolean
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                isLifetimeWarranty = isChecked,
                selectedExpiryDate = null,
                selectedExpiryDateError = null
            )
        }
    }

    private fun startingDateChanged(
        startingDateInMs: Long?
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                selectedStartingDate = startingDateInMs?.let { dateInMs ->
                    Instant.fromEpochMilliseconds(epochMilliseconds = dateInMs)
                },
                selectedStartingDateError = null
            )
        }
    }

    private fun expiryDateChanged(
        expiryDateInMs: Long?
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                selectedExpiryDate = expiryDateInMs?.let { dateInMs ->
                    Instant.fromEpochMilliseconds(epochMilliseconds = dateInMs)
                },
                selectedExpiryDateError = null
            )
        }
    }

    private fun getCategories() {
        getCategoriesJob?.cancel()

        getCategoriesJob = upsertWarrantyUseCases.observeCategoriesUseCase.get()().onStart {
            _state.update {
                it.copy(
                    isCategoriesLoading = true,
                    categoriesErrorMessage = null
                )
            }
        }.onEach { result ->
            when (result) {
                is Result.Success -> {
                    _state.update {
                        it.copy(categories = result.data)
                    }
                }
                is Result.Error -> {
                    when (val error = result.error) {
                        ObserveCategoriesError.Network.FirebaseAuthUnauthorizedUser -> {
                            _getCategoriesEventChannel.send(UiEvent.ShowLongSnackbar(error.asUiText()))
                            _getCategoriesEventChannel.send(UiEvent.ForceLogoutUnauthorizedUser)
                        }
                        else -> {
                            _state.update {
                                it.copy(categoriesErrorMessage = error.asUiText())
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

    private fun upsertWarranty() {
        if (!hasNetworkConnection()) {
            _upsertWarrantyEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

//        upsertWarrantyUseCases.upsert.get()(
//            warrantyId = _state.value.warranty.id
//        ).onStart {
//            _state.update {
//                it.copy(isDeleteLoading = true)
//            }
//        }.onEach { result ->
//            when (result) {
//                is Result.Success -> {
//                    // TODO: UPDATE UPDATING_WARRANTY BEFORE NAVIGATING BACK TO WARRANTY DETAILS SCREEN
//                    _deleteWarrantyEventChannel.send(
//                        UiEvent.ShowLongToast(
//                            UiText.StringResource(
//                                resId = R.string.warranty_details_delete_message_success,
//                                _state.value.warranty.title
//                            )
//                        )
//                    )
//                    _deleteWarrantyEventChannel.send(UiEvent.NavigateUp)
//                }
//                is Result.Error -> {
//                    when (val error = result.error) {
//                        DeleteWarrantyError.Network.FirebaseAuthUnauthorizedUser -> {
//                            _deleteWarrantyEventChannel.send(UiEvent.ForceLogoutUnauthorizedUser)
//                        }
//                        else -> {
//                            _deleteWarrantyEventChannel.send(
//                                UiEvent.ShowLongSnackbar(error.asUiText())
//                            )
//                        }
//                    }
//                }
//            }
//        }.onCompletion {
//            _state.update {
//                it.copy(isDeleteLoading = false)
//            }
//        }.launchIn(scope = viewModelScope)
    }
}