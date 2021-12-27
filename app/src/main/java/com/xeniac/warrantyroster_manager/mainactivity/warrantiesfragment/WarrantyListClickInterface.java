package com.xeniac.warrantyroster_manager.mainactivity.warrantiesfragment;

import com.xeniac.warrantyroster_manager.model.Warranty;

public interface WarrantyListClickInterface {
    void onItemClick(Warranty warranty, long daysUntilExpiry);
}