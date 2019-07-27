package com.example.budgetapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Stats_Table")
class Stat implements Serializable {


    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "Description")
    private String description;

    @ColumnInfo(name = "Amount")
    private String amount;

    @ColumnInfo(name = "Percentage")
    private String percentage;

    @ColumnInfo(name = "ImgResource")
    private int imageResource;

    //empty Constructor
    Stat() {
    }

    Stat(int mImageResource, String mDescription, String mAmount, String mPercentage) {
        this.imageResource = mImageResource;
        this.description = mDescription;
        this.amount = mAmount;
        this.percentage = mPercentage;
    }


    int getUid() {
        return uid;
    }

    int getImageResource() {
        return imageResource;
    }

    String getDescription() {
        return description;
    }

    String getAmount() {
        return amount;
    }

    String getPercentage() {
        return percentage;
    }


    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
