package com.xeniac.warrantyroster.mainactivity.warrantiesfragment;

import com.xeniac.warrantyroster.R;

import java.util.ArrayList;
import java.util.List;

public class WarrantyDataProvider {

    public static List<WarrantyDataModel> warrantyList;

    static {
        warrantyList = new ArrayList<>();

        warrantyList.add(new WarrantyDataModel(1, "Samsung TV", "January 2nd, 2025", 1, "Accessory", R.drawable.ic_category_accessory));
        warrantyList.add(new WarrantyDataModel(2, "Aunt’s Pixel", "February 22nd, 2022", 0, "Air Conditioner", R.drawable.ic_category_air_conditioner));
        warrantyList.add(new WarrantyDataModel(3, "Sony WF-1000XM4", "March 15th, 2020", -1, "Camera", R.drawable.ic_category_camera));
        warrantyList.add(new WarrantyDataModel(4, "Samsung Second TV", "April 4th, 2022", 1, "Vehicle", R.drawable.ic_category_car));
        warrantyList.add(new WarrantyDataModel(5, "Samsung TV", "January 2nd, 2025", 1, "Accessory", R.drawable.ic_category_accessory));
        warrantyList.add(new WarrantyDataModel(6, "Aunt’s Pixel", "February 22nd, 2022", 0, "Air Conditioner", R.drawable.ic_category_air_conditioner));
        warrantyList.add(new WarrantyDataModel(7, "Sony WF-1000XM4", "March 15th, 2020", -1, "Camera", R.drawable.ic_category_camera));
        warrantyList.add(new WarrantyDataModel(8, "Samsung Second TV", "April 4th, 2022", 1, "Vehicle", R.drawable.ic_category_car));
        warrantyList.add(new WarrantyDataModel(9, "Samsung TV", "January 2nd, 2025", 1, "Accessory", R.drawable.ic_category_accessory));
        warrantyList.add(new WarrantyDataModel(10, "Aunt’s Pixel", "February 22nd, 2022", 0, "Air Conditioner", R.drawable.ic_category_air_conditioner));
        warrantyList.add(new WarrantyDataModel(11, "Sony WF-1000XM4", "March 15th, 2020", -1, "Camera", R.drawable.ic_category_camera));
        warrantyList.add(new WarrantyDataModel(12, "Samsung Second TV", "April 4th, 2022", 1, "Vehicle", R.drawable.ic_category_car));
    }

    private static void addItem(WarrantyDataModel item) {
        warrantyList.add(item);
    }
}