package com.xeniac.warrantyroster.mainactivity.warrantiesfragment;

import androidx.annotation.NonNull;

public class WarrantyDataModel {

    private String id;
    private String title;
    private String brand;
    private String model;
    private String serialNumber;
    private String startingDate;
    private String expiryDate;
    private String description;
    private String categoryId;

    public WarrantyDataModel() {
    }

    public WarrantyDataModel(String id, String title, String brand, String model,
                             String serialNumber, String startingDate, String expiryDate,
                             String description, String categoryId) {
        this.id = id;
        this.title = title;
        this.brand = brand;
        this.model = model;
        this.serialNumber = serialNumber;
        this.startingDate = startingDate;
        this.expiryDate = expiryDate;
        this.description = description;
        this.categoryId = categoryId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @NonNull
    @Override
    public String toString() {
        return "WarrantyDataModel{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", startingDate='" + startingDate + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", description='" + description + '\'' +
                ", categoryId='" + categoryId + '\'' +
                '}';
    }
}