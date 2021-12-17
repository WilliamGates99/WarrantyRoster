package com.xeniac.warrantyrostermanager.mainactivity.warrantiesfragment;

import com.xeniac.warrantyrostermanager.model.Warranty;

public interface WarrantyListClickInterface {
    void onItemClick(Warranty warranty, long daysUntilExpiry);
}