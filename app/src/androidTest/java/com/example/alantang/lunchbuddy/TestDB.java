package com.example.alantang.lunchbuddy;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Alan on 8/14/2015.
 */
public class TestDB extends AndroidTestCase {
    private static final String TAG = "log_message";
    void deleteTheDatabase() {
        mContext.deleteDatabase(DatesAvailDatabase.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(DatesAvailDatabase.TABLE_DATES_AVAIL);

        mContext.deleteDatabase(DatesAvailDatabase.DATABASE_NAME);
        SQLiteDatabase db = new DatesAvailDatabase(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + DatesAvailDatabase.TABLE_DATES_AVAIL + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(DatesAvailDatabase.ID);
        locationColumnHashSet.add(DatesAvailDatabase.COL_USER);
        locationColumnHashSet.add(DatesAvailDatabase.COL_DATE);
        locationColumnHashSet.add(DatesAvailDatabase.COL_UPDATED);


        int columnNameIndex = c.getColumnIndex("name");
        Log.d(TAG, String.valueOf(columnNameIndex));
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    public void testDatesDb() throws Throwable {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        DatesAvailDatabase dbHelper = new DatesAvailDatabase(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        ContentValues testValues = new ContentValues();
        testValues.put(DatesAvailDatabase.COL_USER, "test user");
        testValues.put(DatesAvailDatabase.COL_DATE, "test date");
        testValues.put(DatesAvailDatabase.COL_UPDATED, "test updated");

        // Third Step: Insert ContentValues into database and get a row ID back
        long locationRowId;
        locationRowId = db.insert(DatesAvailDatabase.TABLE_DATES_AVAIL, null, testValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        Log.d(TAG, "Row number:" + String.valueOf(locationRowId));

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                DatesAvailDatabase.TABLE_DATES_AVAIL,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        //output contents of cursor
        if (cursor.moveToFirst()) {
            Log.d(TAG, DatabaseUtils.dumpCursorToString(cursor));
        }


        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query

        assertTrue("Error: No Records returned from location query", cursor.moveToFirst());

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        validateCurrentRecord("Error: Location Query Validation Failed", cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from location query",
                cursor.moveToNext());

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
    }


    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

}
