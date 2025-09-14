package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.WarrantyDetailsScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.utils.WarrantyNavType
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation.states.WarrantyDetailsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.reflect.typeOf
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class WarrantyDetailsViewModel @Inject constructor(
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

    // TODO: IMPLEMENT DELETE WARRANTY

    fun onAction(action: WarrantyDetailsAction) {
//        when (action) {
//            else -> TODO("Handle actions")
//        }
    }
}