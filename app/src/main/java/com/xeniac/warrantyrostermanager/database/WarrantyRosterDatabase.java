package com.xeniac.warrantyrostermanager.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.xeniac.warrantyrostermanager.Constants;
import com.xeniac.warrantyrostermanager.model.Category;

@Database(entities = {Category.class}, version = Constants.DB_VERSION)
public abstract class WarrantyRosterDatabase extends RoomDatabase {

    private static WarrantyRosterDatabase instance;

    public abstract CategoryDAO categoryDAO();

    public static WarrantyRosterDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    WarrantyRosterDatabase.class, Constants.DB_FILE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }
}