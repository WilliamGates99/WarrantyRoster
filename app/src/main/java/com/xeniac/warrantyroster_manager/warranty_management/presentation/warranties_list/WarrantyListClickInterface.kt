package com.xeniac.warrantyroster_manager.warranty_management.presentation.warranties_list

import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.Warranty

interface WarrantyListClickInterface {
    fun onItemClick(warranty: Warranty)
}