package com.example.budgetapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class EditDataActivity extends AppCompatActivity {

    private Spinner spinnerAmountType, spinnerCategory;
    private Button buttonSubmitEdit;
    private EditText editTextDescription, editTextAmount;
    Stats tempStats;

    private SQLiteDatabase sqLiteDatabase;
    private DBHelper dbHelper;


    private ArrayList<Stats> tempArrayListStats = new ArrayList<Stats>();

    private int categoryPosition = 0;
    private int[] categoryIcon = {R.drawable.ic_add, R.drawable.ic_shoping, R.drawable.ic_restaurant, R.drawable.ic_bus,
            R.drawable.ic_movies, R.drawable.ic_petrol, R.drawable.ic_taxi, R.drawable.ic_laundry, R.drawable.ic_train};
    private int imageResource;
    private String[] type = {"Saving", "Expense"};

    private float totalPercentage = 0, currentPercentage = 0, totalAmount = 0, inputAmount = 0, highestAmount = 0;
    private String mCurrentAmount;
    public String description, amount, percentage;
    private float currentItemAmount;
    String amountTypeSign;


    private float updatedAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        getDataOnApplicationStart();


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, type);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerAmountType = findViewById(R.id.spinnerAmountType);
        spinnerCategory = findViewById(R.id.spinnerCatagory);

        buttonSubmitEdit = findViewById(R.id.buttonSubmitEdit);

        editTextDescription = findViewById(R.id.editTextDescription);
        editTextAmount = findViewById(R.id.editTextAmount);

        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(getApplicationContext(), categoryIcon);
        spinnerCategory.setAdapter(customSpinnerAdapter);

        spinnerAmountType.setAdapter(arrayAdapter);

        onSpinnerItemChange();
        onSpinnerCategoryChange();

        Slidr.attach(this);
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

    }/// category position

    private void onSpinnerItemChange() {
        spinnerAmountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                onSubmitButtonClick(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    private void onSubmitButtonClick(final int mAmountType) {   ///selected item position
        buttonSubmitEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = getIntent().getIntExtra("itemPosition", -1);
                tempArrayListStats = dbHelper.getTransactions();
                updatedAmount = Float.parseFloat(editTextAmount.getText().toString());
                inputAmount = Float.parseFloat(tempArrayListStats.get(itemPosition).getAmount());


                ///handle amounts
                switch (mAmountType) {
                    case 0:
                        ///updatedAmount +Ve & inputAmount +Ve
                        if (inputAmount > 0) {
                            ///updatedAmount +Ve & inputAmount +Ve & totalAmount +Ve
                            if (totalAmount > 0) {
                                totalAmount = totalAmount - inputAmount;
                                totalAmount += updatedAmount;
                                if (totalAmount < 0) {
                                    totalPercentage = 0;
                                } else {
                                    totalPercentage = ((int) totalAmount / highestAmount * 100);
                                }
                            } else {
                                ///updatedAmount +Ve & inputAmount +Ve & totalAmount -Ve
                                totalAmount = totalAmount - inputAmount;
                                totalAmount += updatedAmount;
                                if (totalAmount > highestAmount) {
                                    highestAmount = totalAmount;
                                } else {
                                    if (totalAmount < 0) {
                                        totalPercentage = 0;
                                    } else {
                                        totalPercentage = ((int) totalAmount / highestAmount * 100);
                                    }
                                }
                            }
                        } else {
                            ///updatedAmount +Ve & inputAmount -Ve
                            if (totalAmount > 0) {
                                ///updatedAmount +Ve & inputAmount -Ve & totalAmount +ve
                                totalAmount = totalAmount - inputAmount;
                                totalAmount += updatedAmount;

                                if(totalAmount >= highestAmount){
                                    highestAmount = totalAmount;
                                    totalPercentage = 100;
                                }else if(totalAmount < 0){
                                    totalPercentage = 0;
                                }else{
                                    totalPercentage = ((int) totalAmount / highestAmount * 100);
                                }
                            } else {
                                ///updatedAmount +Ve & inputAmount -Ve & totalAmount -Ve

                                totalAmount = totalAmount - inputAmount;
                                totalAmount += updatedAmount;

                                if (totalAmount > highestAmount) {
                                    highestAmount = totalAmount;
                                }

                                if (totalAmount + inputAmount < 0) {
                                    totalPercentage = 0;
                                } else {
                                    totalPercentage = ((int) totalAmount / highestAmount * 100);
                                }

                            }
                        }
                        break;
                    case 1:
                        ///updatedAmount -Ve & inputAmount +Ve
                        if (inputAmount > 0) {
                            ///updatedAmount -Ve & inputAmount +Ve & totalAmount +Ve
                            if (totalAmount > 0) {

                                if (totalAmount > inputAmount) {
                                    totalAmount = totalAmount - inputAmount;
                                } else {
                                    totalAmount = inputAmount - totalAmount;
                                }

                                if (totalAmount - inputAmount > 0) {
                                    totalPercentage = ((int) totalAmount / highestAmount * 100);
                                } else {
                                    totalPercentage = 0;
                                }
                                highestAmount -= totalAmount;

                            } else {
                                ///updatedAmount -Ve & inputAmount +Ve & totalAmount -Ve
                                if (inputAmount - updatedAmount == 0) {
                                    totalAmount = totalAmount - inputAmount;
                                    highestAmount = totalAmount;
                                } else {
                                    totalAmount = totalAmount - inputAmount;
                                    totalAmount -= updatedAmount;
                                    highestAmount -= totalAmount;
                                }
                                if (totalAmount < 0) {
                                    totalPercentage = 0;
                                } else {
                                    totalPercentage = ((int) totalAmount / highestAmount * 100);
                                }
                            }

                        } else {
                            ///updatedAmount -Ve & inputAmount -Ve & totalAmount +Ve
                            if (totalAmount > 0) {
                                if (totalAmount - inputAmount < 0) {
                                    totalPercentage = 0;
                                } else {
                                    totalPercentage = ((int) totalAmount / highestAmount * 100);
                                }
                                totalAmount = totalAmount - inputAmount;
                                totalAmount -= updatedAmount;
                                highestAmount -= totalAmount;

                            } else {
                                ///updatedAmount -Ve & inputAmount -Ve & totalAmount -Ve
                                totalAmount = totalAmount - inputAmount;
                                totalAmount -= updatedAmount;
                                highestAmount -= totalAmount;
                                if (totalAmount < 0) {
                                    totalPercentage = 0;
                                } else {
                                    totalPercentage = ((int) totalAmount / highestAmount * 100);
                                }
                            }

                        }
                        break;
                }


                //handles description and Amount
                if ((!editTextDescription.getText().toString().isEmpty() && !editTextAmount.getText().toString().isEmpty())) {
                    description = editTextDescription.getText().toString();
                    amount = editTextAmount.getText().toString();

                } else {


                    if (editTextDescription.getText().toString().isEmpty()) {
                        Date currentTime = Calendar.getInstance().getTime();
                        description = (String.valueOf(currentTime).toUpperCase());
                    }
                    if (editTextAmount.getText().toString().isEmpty()) {
                        Toast.makeText(EditDataActivity.this, "Type the Amount First", Toast.LENGTH_SHORT).show();
                    }
                }

                ///handles the sign
                if (mAmountType == 0) {
                    amountTypeSign = "+";
                } else if (mAmountType == 1) {
                    amountTypeSign = "-";
                }

                tempStats = new Stats(categoryIcon[categoryPosition], description, (amountTypeSign + updatedAmount), String.valueOf(currentPercentage));
                dbHelper.updateTransactionTable(itemPosition, tempStats);
                setDataOnApplicationEnd();
                finish();


            }
        });
    }

    public void getDataOnApplicationStart() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("transactionStats", MODE_PRIVATE);
        highestAmount = sharedPreferences.getFloat("highestAmount", 0);
        mCurrentAmount = sharedPreferences.getString("amountLeft", null);
        totalAmount = sharedPreferences.getFloat("totalAmount", 0);
        totalPercentage = sharedPreferences.getFloat("totalPercentage", 0);
        currentPercentage = sharedPreferences.getFloat("currentPercentage", 0);
        highestAmount = sharedPreferences.getFloat("highestAmount", 0);
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
}
