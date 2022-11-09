package com.xeniac.warrantyroster_manager.ui.adapters

import com.xeniac.warrantyroster_manager.data.remote.models.Warranty

interface WarrantyListClickInterface {
    fun onItemClick(warranty: Warranty)
}