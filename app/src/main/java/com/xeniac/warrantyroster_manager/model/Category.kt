package com.xeniac.warrantyroster_manager.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xeniac.warrantyroster_manager.Constants

@Entity(tableName = Constants.DB_TABLE_NAME_CATEGORIES)
data class Category(
    @PrimaryKey
    var id: String,
    var title: Map<String, String>,
    var icon: String
)