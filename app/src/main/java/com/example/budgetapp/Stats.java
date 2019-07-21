package com.example.budgetapp;

class Stats {

    private String description, amount, percentage;
    private int imageResource;

    //empty Constructor
     Stats(int mImageResource, String mDescription, String mAmount, String mPercentage) {
        this.imageResource = mImageResource;
        this.description = mDescription;
        this.amount = mAmount;
        this.percentage = mPercentage;
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
}
