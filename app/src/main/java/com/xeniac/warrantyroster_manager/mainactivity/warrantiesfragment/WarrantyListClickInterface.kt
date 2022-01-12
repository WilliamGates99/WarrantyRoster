package com.xeniac.warrantyroster_manager.mainactivity.warrantiesfragment

import com.xeniac.warrantyroster_manager.model.Warranty

interface WarrantyListClickInterface {
    fun onItemClick(warranty: Warranty, daysUntilExpiry: Long)
}