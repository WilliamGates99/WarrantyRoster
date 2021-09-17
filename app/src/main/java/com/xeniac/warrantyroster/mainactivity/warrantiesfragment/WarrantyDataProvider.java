package com.xeniac.warrantyroster.mainactivity.warrantiesfragment;

import java.util.ArrayList;
import java.util.List;

public class WarrantyDataProvider {

    public static List<WarrantyDataModel> warrantyList;

    static {
        warrantyList = new ArrayList<>();

        warrantyList.add(new WarrantyDataModel(1, "Samsung TV", "January 2nd, 2025", 1, "Accessory", 2131165285));
        warrantyList.add(new WarrantyDataModel(2, "Aunt’s Pixel", "February 22nd, 2022", 0, "Air Conditioner", 2131165286));
        warrantyList.add(new WarrantyDataModel(3, "Sony WF-1000XM4", "March 15th, 2020", -1, "Audio Device", 2131165287));
        warrantyList.add(new WarrantyDataModel(4, "Samsung Second TV", "April 4th, 2022", 1, "Camera", 2131165288));
        warrantyList.add(new WarrantyDataModel(5, "Samsung TV", "January 2nd, 2025", 1, "Game Console", 2131165289));
        warrantyList.add(new WarrantyDataModel(6, "Aunt’s Pixel", "February 22nd, 2022", 0, "Headset", 2131165290));
        warrantyList.add(new WarrantyDataModel(7, "Sony WF-1000XM4", "March 15th, 2020", -1, "Healthcare", 2131165291));
        warrantyList.add(new WarrantyDataModel(8, "Samsung Second TV", "April 4th, 2022", 1, "Home Appliance", 2131165292));
        warrantyList.add(new WarrantyDataModel(9, "Samsung TV", "January 2nd, 2025", 1, "Memory and Storage", 2131165293));
        warrantyList.add(new WarrantyDataModel(10, "Aunt’s Pixel", "February 22nd, 2022", 0, "Miscellaneous", 2131165294));
        warrantyList.add(new WarrantyDataModel(11, "Sony WF-1000XM4", "March 15th, 2020", -1, "Monitor", 2131165295));
        warrantyList.add(new WarrantyDataModel(12, "Samsung Second TV", "April 4th, 2022", 1, "Musical Instrument", 2131165296));
        warrantyList.add(new WarrantyDataModel(13, "Samsung Second TV", "April 4th, 2022", 1, "Peripheral Device", 2131165297));
        warrantyList.add(new WarrantyDataModel(14, "Samsung Second TV", "April 4th, 2022", 1, "Personal Care", 2131165298));
        warrantyList.add(new WarrantyDataModel(15, "Samsung Second TV", "April 4th, 2022", 1, "Personal Computer", 2131165299));
        warrantyList.add(new WarrantyDataModel(16, "Samsung Second TV", "April 4th, 2022", 1, "Phone and Tablet", 2131165300));
        warrantyList.add(new WarrantyDataModel(17, "Samsung Second TV", "April 4th, 2022", 1, "Smart Home", 2131165301));
        warrantyList.add(new WarrantyDataModel(18, "Samsung Second TV", "April 4th, 2022", 1, "Smart Watch", 2131165302));
        warrantyList.add(new WarrantyDataModel(19, "Samsung Second TV", "April 4th, 2022", 1, "Sport and Fitness", 2131165303));
        warrantyList.add(new WarrantyDataModel(20, "Samsung Second TV", "April 4th, 2022", 1, "Tool", 2131165304));
        warrantyList.add(new WarrantyDataModel(21, "Samsung Second TV", "April 4th, 2022", 1, "Vehicle", 2131165305));
    }
}