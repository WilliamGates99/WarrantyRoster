package com.xeniac.warrantyroster.mainactivity.warrantiesfragment;

import androidx.annotation.NonNull;

public class WarrantyDataModel {

    private int id;
    private String title;
    private String expiryDate;
    private int status;
    private String Category;
    private int icon;

    public WarrantyDataModel() {
    }

    public WarrantyDataModel(int id, String title, String expiryDate, int status, String category, int icon) {
        this.id = id;
        this.title = title;
        this.expiryDate = expiryDate;
        this.status = status;
        Category = category;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
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
        return "WarrantyDataModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", status=" + status +
                ", Category='" + Category + '\'' +
                ", icon=" + icon +
                '}';
    }
}