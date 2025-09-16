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
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.Event
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.NetworkObserverHelper.hasNetworkConnection
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.errors.ObserveCategoriesError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.presentation.utils.asUiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.errors.UpsertWarrantyError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.use_cases.UpsertWarrantyUseCases
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.states.UpsertWarrantyState
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.utils.asUiText
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
import kotlinx.datetime.toLocalDateTime
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

    private val _addWarrantyEventChannel = Channel<UiEvent>()
    val addWarrantyEventChannel = _addWarrantyEventChannel.receiveAsFlow()

    private val _editWarrantyEventChannel = Channel<Event>()
    val editWarrantyEventChannel = _editWarrantyEventChannel.receiveAsFlow()

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
            UpsertWarrantyAction.AddWarranty -> addWarranty()
            UpsertWarrantyAction.EditWarranty -> editWarranty()
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
                    selectedExpiryDate = it.expiryDate.atStartOfDayIn(timeZone = timeZone),
                    isUpdatingWarrantyDataSet = true
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
                selectedStartingAndExpiryDatesError = null
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
                selectedStartingAndExpiryDatesError = null
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
                selectedStartingAndExpiryDatesError = null
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

    private fun addWarranty() {
        if (!hasNetworkConnection()) {
            _addWarrantyEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        upsertWarrantyUseCases.addWarrantyUseCase.get()(
            title = _state.value.titleState.value.text,
            brand = _state.value.brandState.value.text,
            model = _state.value.modelState.value.text,
            serialNumber = _state.value.serialNumberState.value.text,
            description = _state.value.descriptionState.value.text,
            selectedCategory = _state.value.selectedCategory,
            isLifetime = _state.value.isLifetimeWarranty,
            selectedStartingDate = _state.value.selectedStartingDate,
            selectedExpiryDate = _state.value.selectedExpiryDate
        ).onStart {
            _state.update {
                it.copy(isUpsertLoading = true)
            }
        }.onEach { upsertWarrantyResult ->
            upsertWarrantyResult.warrantyIdError?.let { warrantyIdError ->
                _addWarrantyEventChannel.send(UiEvent.ShowLongSnackbar(warrantyIdError.asUiText()))
            }

            upsertWarrantyResult.titleError?.let { titleError ->
                _state.update {
                    it.copy(
                        titleState = it.titleState.copy(
                            errorText = titleError.asUiText()
                        )
                    )
                }
            }

            upsertWarrantyResult.brandError?.let { brandError ->
                _state.update {
                    it.copy(
                        brandState = it.brandState.copy(
                            errorText = brandError.asUiText()
                        )
                    )
                }
            }

            upsertWarrantyResult.modelError?.let { modelError ->
                _state.update {
                    it.copy(
                        modelState = it.modelState.copy(
                            errorText = modelError.asUiText()
                        )
                    )
                }
            }

            upsertWarrantyResult.serialNumberError?.let { serialNumberError ->
                _state.update {
                    it.copy(
                        serialNumberState = it.serialNumberState.copy(
                            errorText = serialNumberError.asUiText()
                        )
                    )
                }
            }

            upsertWarrantyResult.descriptionError?.let { descriptionError ->
                _state.update {
                    it.copy(
                        descriptionState = it.descriptionState.copy(
                            errorText = descriptionError.asUiText()
                        )
                    )
                }
            }

            upsertWarrantyResult.selectedCategoryError?.let { selectedCategoryError ->
                _state.update {
                    it.copy(categoriesErrorMessage = selectedCategoryError.asUiText())
                }
            }

            upsertWarrantyResult.startingAndExpiryDatesError?.let { startingAndExpiryDatesError ->
                _state.update {
                    it.copy(selectedStartingAndExpiryDatesError = startingAndExpiryDatesError.asUiText())
                }
            }

            when (val result = upsertWarrantyResult.result) {
                is Result.Success -> {
                    _addWarrantyEventChannel.send(UiEvent.NavigateUp)
                }
                is Result.Error -> {
                    when (val error = result.error) {
                        UpsertWarrantyError.Network.FirebaseAuthUnauthorizedUser -> {
                            _addWarrantyEventChannel.send(UiEvent.ForceLogoutUnauthorizedUser)
                        }
                        else -> {
                            _addWarrantyEventChannel.send(
                                UiEvent.ShowLongSnackbar(error.asUiText())
                            )
                        }
                    }
                }
                null -> Unit
            }
        }.onCompletion {
            _state.update {
                it.copy(isUpsertLoading = false)
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun editWarranty() {
        if (!hasNetworkConnection()) {
            _editWarrantyEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        upsertWarrantyUseCases.editWarrantyUseCase.get()(
            warrantyId = _state.value.updatingWarranty?.id,
            title = _state.value.titleState.value.text,
            brand = _state.value.brandState.value.text,
            model = _state.value.modelState.value.text,
            serialNumber = _state.value.serialNumberState.value.text,
            description = _state.value.descriptionState.value.text,
            selectedCategory = _state.value.selectedCategory,
            isLifetime = _state.value.isLifetimeWarranty,
            selectedStartingDate = _state.value.selectedStartingDate,
            selectedExpiryDate = _state.value.selectedExpiryDate
        ).onStart {
            _state.update {
                it.copy(isUpsertLoading = true)
            }
        }.onEach { upsertWarrantyResult ->
            upsertWarrantyResult.warrantyIdError?.let { warrantyIdError ->
                _editWarrantyEventChannel.send(UiEvent.ShowLongSnackbar(warrantyIdError.asUiText()))
            }

            upsertWarrantyResult.titleError?.let { titleError ->
                _state.update {
                    it.copy(
                        titleState = it.titleState.copy(
                            errorText = titleError.asUiText()
                        )
                    )
                }
            }

            upsertWarrantyResult.brandError?.let { brandError ->
                _state.update {
                    it.copy(
                        brandState = it.brandState.copy(
                            errorText = brandError.asUiText()
                        )
                    )
                }
            }

            upsertWarrantyResult.modelError?.let { modelError ->
                _state.update {
                    it.copy(
                        modelState = it.modelState.copy(
                            errorText = modelError.asUiText()
                        )
                    )
                }
            }

            upsertWarrantyResult.serialNumberError?.let { serialNumberError ->
                _state.update {
                    it.copy(
                        serialNumberState = it.serialNumberState.copy(
                            errorText = serialNumberError.asUiText()
                        )
                    )
                }
            }

            upsertWarrantyResult.descriptionError?.let { descriptionError ->
                _state.update {
                    it.copy(
                        descriptionState = it.descriptionState.copy(
                            errorText = descriptionError.asUiText()
                        )
                    )
                }
            }

            upsertWarrantyResult.selectedCategoryError?.let { selectedCategoryError ->
                _state.update {
                    it.copy(categoriesErrorMessage = selectedCategoryError.asUiText())
                }
            }

            upsertWarrantyResult.startingAndExpiryDatesError?.let { startingAndExpiryDatesError ->
                _state.update {
                    it.copy(selectedStartingAndExpiryDatesError = startingAndExpiryDatesError.asUiText())
                }
            }

            when (val result = upsertWarrantyResult.result) {
                is Result.Success -> {
                    _state.value.updatingWarranty?.let {
                        val timeZone = TimeZone.currentSystemDefault()

                        val updatedWarranty = it.copy(
                            title = _state.value.titleState.value.text.trim(),
                            brand = _state.value.brandState.value.text.trim(),
                            model = _state.value.modelState.value.text.trim(),
                            serialNumber = _state.value.serialNumberState.value.text.trim(),
                            description = _state.value.descriptionState.value.text.trim(),
                            category = _state.value.selectedCategory ?: it.category,
                            isLifetime = _state.value.isLifetimeWarranty,
                            startingDate = _state.value.selectedStartingDate?.toLocalDateTime(
                                timeZone = timeZone
                            )?.date ?: it.startingDate,
                            expiryDate = _state.value.selectedExpiryDate?.toLocalDateTime(
                                timeZone = timeZone
                            )?.date ?: it.expiryDate
                        )

                        _editWarrantyEventChannel.send(
                            UpsertWarrantyUiEvent.NavigateToWarrantyDetailsScreen(
                                updatedWarranty = updatedWarranty
                            )
                        )
                    }
                }
                is Result.Error -> {
                    when (val error = result.error) {
                        UpsertWarrantyError.Network.FirebaseAuthUnauthorizedUser -> {
                            _editWarrantyEventChannel.send(UiEvent.ForceLogoutUnauthorizedUser)
                        }
                        else -> {
                            _editWarrantyEventChannel.send(
                                UiEvent.ShowLongSnackbar(error.asUiText())
                            )
                        }
                    }
                }
                null -> Unit
            }
        }.onCompletion {
            _state.update {
                it.copy(isUpsertLoading = false)
            }
        }.launchIn(scope = viewModelScope)
    }
}