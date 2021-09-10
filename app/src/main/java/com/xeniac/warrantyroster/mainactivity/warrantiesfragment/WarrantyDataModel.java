package com.xeniac.warrantyroster.mainactivity.warrantiesfragment;

public class WarrantyDataModel {

    private int id;
    private String title;
    private String expiryDate;
    private String status;
    private int icon;

    public WarrantyDataModel() {
    }

    public WarrantyDataModel(int id, String title, String expiryDate, String status, int icon) {
        this.id = id;
        this.title = title;
        this.expiryDate = expiryDate;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "WarrantyDataModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", status='" + status + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}