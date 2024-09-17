package com.rhix.newsplashactivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database name and version
    private static final String DATABASE_NAME = "products.db";
    private static final int DATABASE_VERSION = 2;

    // Table and columns
    public static final String TABLE_NAME = "products";
    public static final String COL_ID = "_id"; // Changed column name to _id
    public static final String COL_PRODUCT_NAME = "PRODUCT_NAME";
    public static final String COL_QUANTITY = "QUANTITY";
    public static final String COL_DESCRIPTION = "DESCRIPTION";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the products table
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRODUCT_NAME + " TEXT, " +
                COL_QUANTITY + " INTEGER, " +
                COL_DESCRIPTION + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert a new product into the database
    public boolean insertData(String productName, String quantity, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PRODUCT_NAME, productName);
        contentValues.put(COL_QUANTITY, quantity);
        contentValues.put(COL_DESCRIPTION, description);

        // Insert the new row
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1; // If result is -1, insertion failed
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT _id, PRODUCT_NAME, QUANTITY, DESCRIPTION FROM products", null);
    }

    public boolean deleteData(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("products", "_id = ?", new String[]{String.valueOf(id)}) > 0;
    }
}
