package com.xeniac.warrantyroster.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class CategoryDataModel {

    @PrimaryKey
    @NonNull
    private String id;
    @ColumnInfo
    private int title;
    @ColumnInfo
    private int icon;

    public CategoryDataModel() {
    }

    @Ignore
    public CategoryDataModel(@NotNull String id, int title, int icon) {
        this.id = id;
        this.title = title;
        this.icon = icon;
    }

    @NotNull
    public String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    @NonNull
    @Override
    public String toString() {
        return "CategoryDataModel{" +
                "id='" + id + '\'' +
                ", title=" + title +
                ", icon=" + icon +
                '}';
    }
}