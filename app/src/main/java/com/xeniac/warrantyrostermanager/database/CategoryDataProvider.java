package com.xeniac.warrantyrostermanager.database;

import com.xeniac.warrantyrostermanager.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryDataProvider {

    public static List<CategoryDataModel> categoriesList;

    static {
        categoriesList = new ArrayList<>();

        categoriesList.add(new CategoryDataModel("1", R.string.category_accessory, R.drawable.ic_category_accessory));
        categoriesList.add(new CategoryDataModel("2", R.string.category_air_conditioner, R.drawable.ic_category_air_conditioner));
        categoriesList.add(new CategoryDataModel("3", R.string.category_audio_device, R.drawable.ic_category_audio_device));
        categoriesList.add(new CategoryDataModel("4", R.string.category_camera, R.drawable.ic_category_camera));
        categoriesList.add(new CategoryDataModel("5", R.string.category_game_console, R.drawable.ic_category_game_console));
        categoriesList.add(new CategoryDataModel("6", R.string.category_headset, R.drawable.ic_category_headset));
        categoriesList.add(new CategoryDataModel("7", R.string.category_healthcare, R.drawable.ic_category_healthcare));
        categoriesList.add(new CategoryDataModel("8", R.string.category_home_appliance, R.drawable.ic_category_home_appliance));
        categoriesList.add(new CategoryDataModel("9", R.string.category_memory_and_storage, R.drawable.ic_category_memory_and_storage));
        categoriesList.add(new CategoryDataModel("10", R.string.category_miscellaneous, R.drawable.ic_category_miscellaneous));
        categoriesList.add(new CategoryDataModel("11", R.string.category_monitor, R.drawable.ic_category_monitor));
        categoriesList.add(new CategoryDataModel("12", R.string.category_musical_instrument, R.drawable.ic_category_musical_instrument));
        categoriesList.add(new CategoryDataModel("13", R.string.category_peripheral_device, R.drawable.ic_category_peripheral_device));
        categoriesList.add(new CategoryDataModel("14", R.string.category_personal_care, R.drawable.ic_category_personal_care));
        categoriesList.add(new CategoryDataModel("15", R.string.category_personal_computer, R.drawable.ic_category_personal_computer));
        categoriesList.add(new CategoryDataModel("16", R.string.category_phone_and_tablet, R.drawable.ic_category_phone_and_tablet));
        categoriesList.add(new CategoryDataModel("17", R.string.category_smart_home, R.drawable.ic_category_smart_home));
        categoriesList.add(new CategoryDataModel("18", R.string.category_smart_watch, R.drawable.ic_category_smart_watch));
        categoriesList.add(new CategoryDataModel("19", R.string.category_sport_and_fitness, R.drawable.ic_category_sport_and_fitness));
        categoriesList.add(new CategoryDataModel("20", R.string.category_tool, R.drawable.ic_category_tool));
        categoriesList.add(new CategoryDataModel("21", R.string.category_vehicle, R.drawable.ic_category_vehicle));
    }
}