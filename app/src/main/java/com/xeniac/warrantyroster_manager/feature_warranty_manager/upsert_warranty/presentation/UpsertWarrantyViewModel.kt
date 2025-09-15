package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.UpsertWarrantyScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.utils.WarrantyNavType
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.states.UpsertWarrantyState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class UpsertWarrantyViewModel @Inject constructor(
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
        if (with(_state.value) { !isDataInitialized && updatingWarranty != null }) {
            initializeData()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 2.minutes),
        initialValue = _state.value
    )

    fun onAction(action: UpsertWarrantyAction) {
//        when (action) {
//            else -> TODO("Handle actions")
//        }
    }

    private fun initializeData() = viewModelScope.launch {
        // TODO: IMPLEMENT
    }

    // TODO: UPDATE UPDATING_WARRANTY BEFORE NAVIGATING BACK TO WARRANTY DETAILS SCREEN
}