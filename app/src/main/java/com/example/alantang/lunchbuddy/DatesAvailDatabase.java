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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database. Existing contents will be lost. ["
                + oldVersion + "]->[" + newVersion + "]");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATES_AVAIL);
        onCreate(db);
    }


}
