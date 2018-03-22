package com.mobileappclass.assignment3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Xinyu on 2016/11/3.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "test.db";
    public static final String TABLE_NAME = "location";
    public static final String DATABASE_COLUMN_ID = "id";
    public static final String DATABASE_COLUMN_TIME = "time";
    public static final String DATABASE_COLUMN_LAT ="lat";
    public static final String DATABASE_COLUMN_LONG ="long";

    public DBHelper(Context context) {
        // Superclass method to handle the database construction
        // Don't concern with the null; that's just a CursorFactory we don't use
        // And 1 is the first version number of our database
        // Context is application context of the activity that called us
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // In here is where we manage any type of table creation
        // that we need to do
        db.execSQL("CREATE TABLE IF NOT EXISTS location (id INTEGER PRIMARY KEY AUTOINCREMENT,time VARCHAR(20), lat DOUBLE, long DOUBLE)");
    }

    public void addEntry(String ts, String mylat, String mylng){
        // we are grabbing a reference to a cached version of the DB we created
        // in onCreate
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DATABASE_COLUMN_TIME,ts);
        cv.put(DATABASE_COLUMN_LAT,mylat);
        cv.put(DATABASE_COLUMN_LONG,mylng);

        db.insert(TABLE_NAME,null,cv);
    }

    public ArrayList<String> getEntireColumn(String columnName){
        ArrayList<String> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        do{
            String value = (String) cursor.getString(cursor.getColumnIndex(columnName));
            values.add(value);
        }while(cursor.moveToNext());
        return values;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // To-do: nothing for us
    }
}
