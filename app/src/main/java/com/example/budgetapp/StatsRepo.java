package com.example.budgetapp;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class StatsRepo {
    public String DB_NAME = "Stats_DB";
    public AppDatabase appDatabase;
    Stat tempStat;

    public StatsRepo(Context context) {
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
    }


    ///Insert Data
    public void insertCurrentStat(final int mImageResource, final String mDescription, final String mAmount, final String mPercentage){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                tempStat = new Stat(mImageResource, mDescription, mAmount, mPercentage);
                appDatabase.statsDao().insertCurrentStat(tempStat);
                Log.i("LOG_TAG", "Insertion Task Started with: Description: " +
                      mDescription + " Amount: " + mAmount + " Percentage: " + mPercentage );
            }
        });

    }

    public Stat getStatByID(final int id){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int imageResource = appDatabase.statsDao().getStatByID(id).getImageResource();

                String description = appDatabase.statsDao().getStatByID(id).getDescription();
                String amount = appDatabase.statsDao().getStatByID(id).getAmount();
                String percentage = appDatabase.statsDao().getStatByID(id).getPercentage();

                tempStat = new Stat(imageResource, description, amount, percentage);

                Log.i("LOG_TAG", "Fetched  Description: " +
                        description + " Amount: " + amount + " Percentage: " + percentage );
            }
        });
        return tempStat;
    }

    public List<Stat> getAllStatus(){
        return appDatabase.statsDao().getStats();
    }
}
