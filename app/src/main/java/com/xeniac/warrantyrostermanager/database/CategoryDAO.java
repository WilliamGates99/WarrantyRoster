package com.xeniac.warrantyrostermanager.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.xeniac.warrantyrostermanager.model.Category;

import java.util.List;

@Dao
public interface CategoryDAO {

    @Insert
    void insertAllCategories(List<Category> categoriesList);

    @Insert
    void insertCategory(Category... category);

    @Query("DELETE FROM Category")
    void deleteAllCategories();

    @Delete
    void deleteCategory(Category... category);

    @Query("SELECT COUNT(*) FROM Category")
    int countItems();

    @Query("SELECT * FROM Category ORDER BY title")
    List<Category> getAllCategories();

    @Query("SELECT title FROM Category ORDER BY title")
    List<Integer> getAllCategoryTitles();

    @Query("SELECT * FROM Category WHERE id = :categoryId")
    Category getCategoryById(String categoryId);
}