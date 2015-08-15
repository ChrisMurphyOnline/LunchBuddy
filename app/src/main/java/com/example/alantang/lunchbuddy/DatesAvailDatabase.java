package com.example.alantang.lunchbuddy;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Alan on 8/12/2015.
 */
public class DatesAvailDatabase extends SQLiteOpenHelper {

    private static final String TAG = "log_message";
    private static final int DB_VERSION = 1;
//    private static final String DB_NAME = "dates_available";

    //database schema

    static final String DATABASE_NAME = "dates.db";

    public static final String TABLE_DATES_AVAIL = "dates";
    public static final String ID = "_id";
    public static final String COL_USER = "user";
    public static final String COL_DATE = "date";
    public static final String COL_UPDATED = "updated";

    private static final String CREATE_TABLE_TUTORIALS = "create table " + TABLE_DATES_AVAIL + " (" +
            ID + " integer primary key autoincrement, " +
            COL_USER + " text not null, " +
            COL_DATE + " text not null, " +
            COL_UPDATED + " text not null);";

    private static final String DB_SCHEMA = CREATE_TABLE_TUTORIALS;


    public DatesAvailDatabase(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_SCHEMA);
//        seedData(db);
//        Log.d(TAG, "Database seeded!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database. Existing contents will be lost. ["
                + oldVersion + "]->[" + newVersion + "]");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATES_AVAIL);
        onCreate(db);
    }

    /**
     * Create sample data to use
     *
     * @param db
     *            The open database
     */
    private void seedData(SQLiteDatabase db) {
        ContentValues testValues = new ContentValues();
        testValues.put(COL_USER, "test user");
        testValues.put(COL_DATE, "test date");
        testValues.put(COL_UPDATED, "test updated");

        db.insert(TABLE_DATES_AVAIL, null, testValues);
//        db.execSQL("insert into tutorials (user, date, updated) values ('Hello', 'Test1', 'End of Test');");
//        db.execSQL("insert into tutorials (user, date, updated) values ('Hello', 'Test2', 'End of Test');");
    }



}
