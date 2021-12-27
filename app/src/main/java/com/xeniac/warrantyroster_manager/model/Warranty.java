package com.xeniac.warrantyroster_manager.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Warranty implements Parcelable {

    private String id;
    private String title;
    private String brand;
    private String model;
    private String serialNumber;
    private String startingDate;
    private String expiryDate;
    private String description;
    private String categoryId;
    private ListItemType itemType;

    public Warranty() {
    }

    public Warranty(String id, String title, String brand, String model,
                    String serialNumber, String startingDate, String expiryDate,
                    String description, String categoryId, ListItemType itemType) {
        this.id = id;
        this.title = title;
        this.brand = brand;
        this.model = model;
        this.serialNumber = serialNumber;
        this.startingDate = startingDate;
        this.expiryDate = expiryDate;
        this.description = description;
        this.categoryId = categoryId;
        this.itemType = itemType;
    }

    protected Warranty(Parcel in) {
        id = in.readString();
        title = in.readString();
        brand = in.readString();
        model = in.readString();
        serialNumber = in.readString();
        startingDate = in.readString();
        expiryDate = in.readString();
        description = in.readString();
        categoryId = in.readString();
    }

    public static final Creator<Warranty> CREATOR = new Creator<Warranty>() {
        @Override
        public Warranty createFromParcel(Parcel in) {
            return new Warranty(in);
        }

        @Override
        public Warranty[] newArray(int size) {
            return new Warranty[size];
        }
    };

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

    public ListItemType getItemType() {
        return itemType;
    }

    public void setItemType(ListItemType itemType) {
        this.itemType = itemType;
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
                ", itemType=" + itemType +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(brand);
        dest.writeString(model);
        dest.writeString(serialNumber);
        dest.writeString(startingDate);
        dest.writeString(expiryDate);
        dest.writeString(description);
        dest.writeString(categoryId);
    }
}