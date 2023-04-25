package com.xeniac.warrantyroster_manager.warranty_management.presentation.warranties_list

import com.xeniac.warrantyroster_manager.data.remote.models.Warranty

interface WarrantyListClickInterface {
    fun onItemClick(warranty: Warranty)
}