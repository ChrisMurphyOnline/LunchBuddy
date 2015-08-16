package com.example.alantang.lunchbuddy;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.net.URI;

/**
 * Created by Alan on 8/12/2015.
 */
public class DatesAvailListProvider extends ContentProvider {

    private DatesAvailDatabase mDB;

    private static final String AUTHORITY = "com.example.alantang.lunchbuddy";
    public static final int DATES = 100;
    public static final int DATE_ID = 110;
    private static final String DATES_BASE_PATH = "dates";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + DATES_BASE_PATH);

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/date";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/dates";


    @Override
    public boolean onCreate() {
        mDB = new DatesAvailDatabase(getContext());
        return true;
    }

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, DATES_BASE_PATH, DATES);
        sURIMatcher.addURI(AUTHORITY, DATES_BASE_PATH + "/#", DATE_ID);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DatesAvailDatabase.TABLE_DATES_AVAIL);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case DATE_ID:
                queryBuilder.appendWhere(DatesAvailDatabase.ID + "="
                        + uri.getLastPathSegment());
                break;
            case DATES:
                // no filter
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        int rowsAffected = 0;
        switch (uriType) {
            case DATES:
                rowsAffected = sqlDB.delete(DatesAvailDatabase.TABLE_DATES_AVAIL,
                        selection, selectionArgs);
                break;
            case DATE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = sqlDB.delete(DatesAvailDatabase.TABLE_DATES_AVAIL,
                            DatesAvailDatabase.ID + "=" + id, null);
                } else {
                    rowsAffected = sqlDB.delete(DatesAvailDatabase.TABLE_DATES_AVAIL,
                            selection + " and " + DatesAvailDatabase.ID + "=" + id,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public String getType(Uri uri) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case DATES:
                return CONTENT_TYPE;
            case DATE_ID:
                return CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        if (uriType != DATES) {
            throw new IllegalArgumentException("Invalid URI for insert");
        }
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        long newID = sqlDB
                .insert(DatesAvailDatabase.TABLE_DATES_AVAIL, null, values);
        if (newID > 0) {
            Uri newUri = ContentUris.withAppendedId(uri, newID);
            getContext().getContentResolver().notifyChange(uri, null);
            return newUri;
        } else {
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();

        int rowsAffected;

        switch (uriType) {
            case DATE_ID:
                String id = uri.getLastPathSegment();
                StringBuilder modSelection = new StringBuilder(DatesAvailDatabase.ID
                        + "=" + id);

                if (!TextUtils.isEmpty(selection)) {
                    modSelection.append(" AND " + selection);
                }

                rowsAffected = sqlDB.update(DatesAvailDatabase.TABLE_DATES_AVAIL,
                        values, modSelection.toString(), null);
                break;
            case DATES:
                rowsAffected = sqlDB.update(DatesAvailDatabase.TABLE_DATES_AVAIL,
                        values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }


}
