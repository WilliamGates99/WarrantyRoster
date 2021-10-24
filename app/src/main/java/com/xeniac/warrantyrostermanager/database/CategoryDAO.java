package com.xeniac.warrantyrostermanager.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CategoryDAO {

    @Insert
    void insertAllCategories(List<CategoryDataModel> categoriesList);

    @Insert
    void insertCategory(CategoryDataModel... category);

    @Query("DELETE FROM CategoryDataModel")
    void deleteAllCategories();

    @Delete
    void deleteCategory(CategoryDataModel... category);

    @Query("SELECT COUNT(*) FROM CategoryDataModel")
    int countItems();

    @Query("SELECT * FROM CategoryDataModel ORDER BY title")
    List<CategoryDataModel> getAllCategories();

    @Query("SELECT title FROM CategoryDataModel ORDER BY title")
    List<Integer> getAllCategoryTitles();

    @Query("SELECT * FROM categorydatamodel WHERE id = :categoryId")
    CategoryDataModel getCategoryById(String categoryId);
}