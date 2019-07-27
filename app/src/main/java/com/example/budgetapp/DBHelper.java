package com.example.budgetapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "BudgetAPP.db";
    private static final String TABLE_NAME = "STATS_TABLE";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_IMG_RESOURCE = "IMG_RESOURCE";
    private static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    private static final String COLUMN_AMOUNT = "AMOUNT";
    private static final String COLUMN_PERCENTAGE = "PERCENTAGE";

    private final static String queryDropTable = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private static final String queryCreateTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_IMG_RESOURCE + " INTEGER NOT NULL,"
            + COLUMN_DESCRIPTION + " TEXT NOT NULL,"
            + COLUMN_AMOUNT + " TEXT NOT NULL,"
            + COLUMN_PERCENTAGE + " TEXT NOT NULL" + ");";
    private final static String querySelectTable = "SELECT * FROM " + TABLE_NAME + " WHERE 1";

    private Stat stats;
    private ArrayList<Stat> mStatsArrayList = new ArrayList<Stat>();
    private ArrayList<Stat> mTempStatsArrayList = new ArrayList<Stat>();

    DBHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DB_NAME, factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(queryCreateTable);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);
    }

    public void setTransaction(Stat mStats) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IMG_RESOURCE, mStats.getImageResource());
        contentValues.put(COLUMN_DESCRIPTION, mStats.getDescription());
        contentValues.put(COLUMN_AMOUNT, mStats.getAmount());
        contentValues.put(COLUMN_PERCENTAGE, mStats.getPercentage());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, contentValues);
    }
    public ArrayList<Stat> getTransactions() {

        int imgResource;
        String description;
        String amount;
        String percentage;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querySelectTable, new String[]{});

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            imgResource = cursor.getInt(cursor.getColumnIndex(COLUMN_IMG_RESOURCE));
            description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
            amount = cursor.getString(cursor.getColumnIndex(COLUMN_AMOUNT));
            percentage = cursor.getString(cursor.getColumnIndex(COLUMN_PERCENTAGE));

            mStatsArrayList.add(stats = new Stat(imgResource, description, amount, percentage));
        }
        return mStatsArrayList;
    }
    public void createUpdatedTransactionTable(ArrayList<Stat> tempStatsArrayList) {
        mStatsArrayList = tempStatsArrayList;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        db.execSQL(queryDropTable);
        db.execSQL(queryCreateTable);

        for (int x = 0; x < mStatsArrayList.size(); x++) {
            contentValues.put(COLUMN_IMG_RESOURCE, mStatsArrayList.get(x).getImageResource());
            contentValues.put(COLUMN_DESCRIPTION, mStatsArrayList.get(x).getDescription());
            contentValues.put(COLUMN_AMOUNT, mStatsArrayList.get(x).getAmount());
            contentValues.put(COLUMN_PERCENTAGE, mStatsArrayList.get(x).getPercentage());

            db.insert(TABLE_NAME, null, contentValues);
        }
    }
    public void updateTransactionTable(int itemPosition, Stat tempStats) {
        mStatsArrayList.set(itemPosition, tempStats);

        createUpdatedTransactionTable(mStatsArrayList);
    }
}
