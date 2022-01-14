package com.xeniac.warrantyroster_manager.db

import android.content.Context
import androidx.room.*
import com.xeniac.warrantyroster_manager.models.Category
import com.xeniac.warrantyroster_manager.util.Constants.Companion.DB_FILE_NAME_NEW

@Database(entities = [Category::class], exportSchema = true, version = 1)
@TypeConverters(MapConverter::class)
abstract class WarrantyRosterDatabase : RoomDatabase() {

    abstract fun getCategoryDao(): CategoryDao

    companion object {
        @Volatile
        private var instance: WarrantyRosterDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                WarrantyRosterDatabase::class.java, DB_FILE_NAME_NEW
            ).build()
    }
}