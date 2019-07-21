package com.example.budgetapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.ads.MobileAds;

import in.championswimmer.sfg.lib.SimpleFingerGestures;

public class MainActivity extends AppCompatActivity {

    private EditText editTextAmount, editTextDescription;
    private TextView textViewAmountLeft;
    private Button buttonAdd;
    private Spinner spinnerAmountType, spinnerCategory;
    private DonutProgress progress;
    private int categoryPosition = 0;
    private int[] categoryIcon = {R.drawable.ic_add, R.drawable.ic_shoping, R.drawable.ic_restaurant, R.drawable.ic_bus,
            R.drawable.ic_movies, R.drawable.ic_petrol, R.drawable.ic_taxi, R.drawable.ic_laundry, R.drawable.ic_train};
    private int imageResource;
    private float totalPercentage = 0, currentPercentage = 0, totalAmount = 0, inputAmount = 0, highestAmount = 0;
    private String mCurrentAmount;
    private String description, amount, percentage;
    private String[] type = {"Saving", "Expense"};
    private SimpleFingerGestures gesture = new SimpleFingerGestures();

    private SQLiteDatabase sqLiteDatabase;
    private DBHelper dbHelper;


    Stats tempClassStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        dbHelper = new DBHelper(MainActivity.this, null);
        sqLiteDatabase = dbHelper.getWritableDatabase();

        //connecting widgets by id
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonAdd = findViewById(R.id.buttonAddAmount);
        spinnerAmountType = findViewById(R.id.spinnerAmountType);
        spinnerCategory = findViewById(R.id.spinnerCatagory);
        progress = findViewById(R.id.donutProgress);
        textViewAmountLeft = findViewById(R.id.textViewTotalAmount);
        RelativeLayout relativeLayoutMain = findViewById(R.id.relativeLayoutMain);

        ///background animation
        backgroundAnimation(relativeLayoutMain);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, type);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(getApplicationContext(), categoryIcon);
        spinnerCategory.setAdapter(customSpinnerAdapter);

        spinnerAmountType.setAdapter(arrayAdapter);

        onSpinnerItemChange();
        onSpinnerCategoryChange();
        onFingerSwipe(this.getWindow().getDecorView());

    }

    //
    public void backgroundAnimation(RelativeLayout relativeLayout) {
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }
    private void onSpinnerCategoryChange() {
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void onSpinnerItemChange() {
        spinnerAmountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                onButtonClick(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }
    private void onButtonClick(final int amountType) {

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (!editTextAmount.getText().toString().isEmpty()) {
                    //Cast Input to Float
                    inputAmount = Float.parseFloat(editTextAmount.getText().toString());

                    switch (amountType) {
                        case 0:
                            //saving
                            if (totalAmount < 0) {
                                if (totalAmount + inputAmount > 0) {
                                    totalPercentage = 100;
                                } else {
                                    totalPercentage = 0;
                                }

                            } else {
                                if (inputAmount > totalAmount) {
                                    totalPercentage = 100;
                                } else if (inputAmount < totalAmount) {
                                    if(totalAmount == highestAmount){
                                        currentPercentage = 0;
                                    }else{
                                        currentPercentage = ((int) (inputAmount / highestAmount * 100));
                                    }
                                    if (totalPercentage < 100) {
                                        if (currentPercentage + totalPercentage > 100) {
                                            totalPercentage = 100;
                                        } else {
                                            totalPercentage += currentPercentage;
                                        }
                                    } else {
                                        totalPercentage = 100;
                                    }
                                } else if (totalAmount == 0 || totalAmount < 0) {
                                    totalPercentage = 0;
                                }
                            }
                            totalPercentage = Math.round(totalPercentage);

                            //Setting Description
                            if (!editTextDescription.getText().toString().isEmpty()) {
                                description = (editTextDescription.getText().toString());
                            } else {
                                Date currentTime = Calendar.getInstance().getTime();
                                description = (String.valueOf(currentTime).toUpperCase());
                            }
                            ///sending data
                            percentage = String.valueOf((currentPercentage));
                            amount = (String.format("+%s", String.valueOf(inputAmount)));
                            imageResource = (R.drawable.ic_add);


                            totalAmount += inputAmount;
                            highestAmount = totalAmount;
                            break;

                        case 1:
                            //expense
                            progress.setUnfinishedStrokeColor(R.color.colorRed);

                            if (totalAmount - inputAmount > 0) {
                                totalPercentage = (((totalAmount - inputAmount) / highestAmount) * 100);
                                currentPercentage = inputAmount / highestAmount * 100;
                            } else if (totalAmount - inputAmount == 0) {
                                totalPercentage = 0;

                            } else {
                                totalPercentage = 0;
                            }


                            if (!editTextDescription.getText().toString().isEmpty()) {
                                description = (editTextDescription.getText().toString());
                            } else {
                                Date currentTime = Calendar.getInstance().getTime();
                                description = (String.valueOf(currentTime).toUpperCase());
                            }
                            ///sending data into tempClass and later save into array list
                            percentage = String.valueOf(((currentPercentage)));
                            amount = (String.format("-%s", String.valueOf(inputAmount)));

                            totalAmount -= inputAmount;
                            totalPercentage = Math.round(totalPercentage);
                            break;
                    }
                }
                if (!editTextAmount.getText().toString().isEmpty()) {
                    tempClassStats = new Stats(categoryIcon[categoryPosition], description, amount, percentage);
                    //empty Position
                    dbHelper.setTransaction(tempClassStats);
                }


                ///set Left Amount
                textViewAmountLeft.setText(String.format("$%s", totalAmount));
                mCurrentAmount = String.format("$%s", totalAmount);

                ///making editTexts Null after every single entry
                editTextAmount.setText("");
                editTextDescription.setText("");
                //set Progress
                progress.setProgress(totalPercentage);
                setDataOnApplicationEnd();
            }
        });
    }

    // TODO: 5/30/2019 replace with proper fragment
    public void onFingerSwipe(View v) {
        gesture.setOnFingerGestureListener(new SimpleFingerGestures.OnFingerGestureListener() {
            @Override
            public boolean onSwipeUp(int i, long l, double v) {
                return false;
            }

            @Override
            public boolean onSwipeDown(int i, long l, double v) {
                return false;
            }

            @Override
            public boolean onSwipeLeft(int i, long l, double v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_animation, R.anim.slide_out_animation);
                return false;
            }

            @Override
            public boolean onSwipeRight(int i, long l, double v) {
                return false;
            }

            @Override
            public boolean onPinch(int i, long l, double v) {
                return false;
            }

            @Override
            public boolean onUnpinch(int i, long l, double v) {
                return false;
            }

            @Override
            public boolean onDoubleTap(int i) {
                return false;
            }
        });

        v.setOnTouchListener(gesture);
    }
    public void setDataOnApplicationEnd() {
        SharedPreferences sharedPreferences = getSharedPreferences("transactionStats", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!String.valueOf(totalPercentage).isEmpty() && !mCurrentAmount.isEmpty() && !String.valueOf(totalAmount).isEmpty() &&
                !String.valueOf(currentPercentage).isEmpty() && !String.valueOf(highestAmount).isEmpty()) {

            editor.putString("amountLeft", mCurrentAmount);
            editor.putFloat("totalAmount", totalAmount);
            editor.putFloat("totalPercentage", totalPercentage);
            editor.putFloat("currentPercentage", currentPercentage);
            editor.putFloat("highestAmount", highestAmount);

            editor.apply();
        }
    }
    public void getDataOnApplicationStart() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("transactionStats", MODE_PRIVATE);

        highestAmount = sharedPreferences.getFloat("highestAmount", 0);
        mCurrentAmount = sharedPreferences.getString("amountLeft", null);
        totalAmount = sharedPreferences.getFloat("totalAmount", 0);
        totalPercentage = sharedPreferences.getFloat("totalPercentage", 0);
        currentPercentage = sharedPreferences.getFloat("currentPercentage", 0);
        highestAmount = sharedPreferences.getFloat("highestAmount", 0);

        textViewAmountLeft.setText(String.format("$%s", totalAmount));
        mCurrentAmount = String.format("$%s", totalAmount);
        editTextAmount.setText("");
        editTextDescription.setText("");
        progress.setProgress(totalPercentage);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("CurrentAmount", mCurrentAmount);
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentAmount = savedInstanceState.getString("CurrentAmount");
        textViewAmountLeft.setText(mCurrentAmount);
    }
    @Override
    protected void onStart() {
        super.onStart();
        getDataOnApplicationStart();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        setDataOnApplicationEnd();
        dbHelper.close();
    }
    @Override
    protected void onPause() {
        super.onPause();
        dbHelper.close();
    }

    @Override
    protected void onResume() {
        getDataOnApplicationStart();
        super.onResume();
    }
}
