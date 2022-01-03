package com.xeniac.warrantyroster_manager.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapConverter {

    @TypeConverter
    fun toMap(value: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromMap(titleMap: Map<String, String>): String {
        return Gson().toJson(titleMap)
    }
}