package com.xeniac.warrantyroster_manager.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xeniac.warrantyroster_manager.utils.Constants.DB_TABLE_NAME_CATEGORIES

@Entity(tableName = DB_TABLE_NAME_CATEGORIES)
data class Category(
    @PrimaryKey
    val id: String,
    val title: Map<String, String>,
    val icon: String
)