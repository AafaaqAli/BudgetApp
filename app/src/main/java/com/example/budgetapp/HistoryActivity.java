package com.example.budgetapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;

import in.championswimmer.sfg.lib.SimpleFingerGestures;

public class HistoryActivity extends AppCompatActivity {

    private AdView mAdView;

    public RecyclerView mRecyclerView;
    public MyAdapterClass mAdapter;
    public RecyclerView.LayoutManager mLayoutManager;
    ArrayList<Stats> mStatsArrayList = new ArrayList<Stats>();
    RecyclerTouchListener onTouchListener;
    SimpleFingerGestures gesture = new SimpleFingerGestures();

    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        MobileAds.initialize(this, "ca-app-pub-4103825165316777~4034753902");


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mRecyclerView = findViewById(R.id.recyclerViewHistory);

        dbHelper = new DBHelper(HistoryActivity.this, null);
        sqLiteDatabase = dbHelper.getWritableDatabase();
        mLayoutManager = new LinearLayoutManager(this);
        mStatsArrayList = dbHelper.getTransactions();

        mAdapter = new MyAdapterClass(mStatsArrayList);


        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        onTouchListener = new RecyclerTouchListener(HistoryActivity.this, mRecyclerView);
        Slidr.attach(this);


        onTouchListener.setSwipeOptionViews(R.id.delete, R.id.edit)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        if (viewID == R.id.delete) {
                            deleteEntry(position);
                        } else if (viewID == R.id.edit) {
                            Intent intent = new Intent(HistoryActivity.this, EditDataActivity.class);
                            intent.putExtra("itemPosition", position);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
        mRecyclerView.addOnItemTouchListener(onTouchListener);
        mAdapter.notifyDataSetChanged();
    }

    public void deleteEntry(final int mPosition) {
        SharedPreferences sharedPreferences = getSharedPreferences("transactionStats", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        float highestAmount = sharedPreferences.getFloat("highestAmount", 0);
        float amount = Float.parseFloat(mStatsArrayList.get(mPosition).getAmount());

        highestAmount = highestAmount - amount;
        editor.putFloat("highestAmount", highestAmount);
        editor.apply();
        float percentage = Float.parseFloat(mStatsArrayList.get(mPosition).getPercentage());

        setDataOnApplicationEnd(amount, percentage);

        mStatsArrayList.remove(mPosition);
        mAdapter.notifyDataSetChanged();
        dbHelper.createUpdatedTransactionTable(mStatsArrayList);
    }
    public void setDataOnApplicationEnd(float currentAmount, float currentPercentage) {
        float totalAmount, totalPercentage, highestAmount;
        SharedPreferences sharedPreferences = getSharedPreferences("transactionStats", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        totalAmount = sharedPreferences.getFloat("totalAmount", 0);
        totalPercentage = sharedPreferences.getFloat("totalPercentage", 0);
        highestAmount = sharedPreferences.getFloat("highestAmount", 0);


        //if value is positive then subtract to get orignal amount
        if (currentAmount > 0) {
            if (currentAmount >= totalAmount) {
                if ((currentAmount - totalAmount) <= 0) {
                    totalPercentage = ((currentAmount - totalAmount) / highestAmount) * 100;
                } else {
                    totalPercentage = 0;
                }

            } else if (currentAmount < totalAmount) {
                totalPercentage = ((totalAmount - currentAmount) / highestAmount) * 100;
            }
            totalAmount = totalAmount + (-currentAmount);
        } else {
            //if value is negative then add to get orignal amount
            //current transactions is greater or equal to totalCurrentAmount
            if (currentAmount >= totalAmount) {

                //currentTransaction percentage is negative ?
                if ((((currentAmount - totalAmount) / highestAmount) * 100) < 0) {
                    totalPercentage = 0;

                    //if currentTransaction percentage is positive & also less then 200
                } else if ((((currentAmount - totalAmount) / highestAmount) * 100) > 0
                        && (((currentAmount - totalAmount) / highestAmount) * 100) < 100) {

                    totalPercentage = ((currentAmount - totalAmount) / highestAmount) * 100;
                }

                //currentTransactionAmount is less then totalTransaction Amount
            } else if (currentAmount < totalAmount) {
                totalPercentage = ((totalAmount - currentAmount) / highestAmount) * 100;
            }
            totalAmount = totalAmount + (-currentAmount);


            //when no entry is made or every entry has been deleted
            if (mStatsArrayList.size() == 0 && currentPercentage == 0) {
                highestAmount = 0;
                totalPercentage = 0;
            }
        }

        editor.putFloat("totalAmount", totalAmount);
        editor.putFloat("totalPercentage", totalPercentage);
        editor.putFloat("highestAmount", highestAmount);

        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecyclerView.addOnItemTouchListener(onTouchListener);
        mAdapter.notifyDataSetChanged();
    }
}

