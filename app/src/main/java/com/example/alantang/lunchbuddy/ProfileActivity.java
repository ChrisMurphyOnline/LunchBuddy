package com.example.alantang.lunchbuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.alantang.lunchbuddy.ListAdapter.customButtonListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.content.Context;


public class ProfileActivity extends Activity implements LoaderCallbacks<Void>, customButtonListener, ConfirmedListAdapter.customButtonListener
{

    //Todo: remove / change title bar

    private static final String TAG = "log_message";
    ArrayList<ParseObject> acceptedAppts = new ArrayList<ParseObject>() ;

    ListView mListViewAppointments, mListViewAvailable;
    ArrayList mListAppointments = new ArrayList<String>();
    ArrayList mListDatesAvailable = new ArrayList<String>();
    ConfirmedListAdapter mAppointmentsAdapter;
    ListAdapter mAvailableAdapter;

    String displayDate;

//    SimpleDateFormat dateFormat = new SimpleDateFormat("E dd MMM yyyy hh:mm aa z");
    SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy hh:mm aaa");

    String formattedDate;

    ParseQueries parseQueries = new ParseQueries();
//    public static String objectId = "";
    boolean deleteDialogue = false;
    String value = "";
    Date date;

    private static final int availableLoader = 0;
    private static final int appointmentLoader = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SQLiteDatabase db = new DatesAvailDatabase(this).getWritableDatabase();

        ContentValues testValues = new ContentValues();
        testValues.put(DatesAvailDatabase.COL_USER, "test user");
        testValues.put(DatesAvailDatabase.COL_DATE, "test date");
        testValues.put(DatesAvailDatabase.COL_UPDATED, "test updated");

        db.insert(DatesAvailDatabase.TABLE_DATES_AVAIL, null, testValues);

        Cursor cursor = db.query(
                DatesAvailDatabase.TABLE_DATES_AVAIL,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            Log.d(TAG, DatabaseUtils.dumpCursorToString(cursor));
        }

        // download list of appointments

//        mAppointmentsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListAppointments);
//        mAvailableAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListDatesAvailable);

        mListViewAppointments = (ListView)findViewById(R.id.listview_current_appointments);
        mListViewAvailable = (ListView)findViewById(R.id.listview_dates_available);

        mAppointmentsAdapter = new ConfirmedListAdapter(ProfileActivity.this, mListAppointments);
        mAppointmentsAdapter.setCustomButtonListner(ProfileActivity.this);
        mAvailableAdapter = new ListAdapter(ProfileActivity.this, mListDatesAvailable);
        mAvailableAdapter.setCustomButtonListner(ProfileActivity.this);

        mListViewAppointments.setAdapter(mAppointmentsAdapter);
        mListViewAvailable.setAdapter(mAvailableAdapter);
        if (isNetworkConnected()) {
            getLoaderManager().initLoader(availableLoader, null, this);
            getLoaderManager().initLoader(appointmentLoader, null, this);
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_LONG).show();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditClickListener(int position, String value) {
        Date date = convertStringToDate(value);
        setDate(date);
        AlertDialog editBox = editOption();
        editBox.show();

    }

    @Override
    public void onDeleteClickListener(int position, String value) {
        resetValue();
        setValue(value);
        AlertDialog deleteBox = deleteOption();
        deleteBox.show();
    }

    @Override
    public void onClearClickListener(int position, String value) {
        AlertDialog clearBox = clearOption(position);
        clearBox.show();
    }

    @Override
    public Loader<Void> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "in create loader");
        switch (id) {
            case availableLoader:
                parseQueries.retrieveDatesAvailable();
                break;
            case appointmentLoader:
                parseQueries.retrieveAcceptedAppts();
                break;
            default:
                return null;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Void> loader, Void params) {
        getLoaderManager().destroyLoader(availableLoader);
        getLoaderManager().destroyLoader(appointmentLoader);

    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {
        mListViewAvailable.setAdapter(null);
        mListViewAppointments.setAdapter(null);
    }

    public Date convertStringToDate (String date) {
        Date result = new Date();
        try {
            result = dateFormat.parse(date);

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void setValue(String val) {
        value = val;
    }

    public void resetValue() {
        value = "";
    }

    public void setDate(Date val) {
        date = val;
    }
    private void deleteObject(String value) {
        Date date = convertStringToDate(value);
        parseQueries.deleteObject(date);

    }


    // opens up Calendar dialogue to edit date
    private void editObject(Date date) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("DatesAvailable");
        query.whereEqualTo("Date", date);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Retrieved " + objects.size() + " objects");
                    if (objects.size() > 0) {
                        String objectId = objects.get(0).getObjectId();
                        String date = objects.get(0).getDate("Date").toString();
                        Log.d(TAG, "Date: " + objectId);
                        Log.d(TAG, "Object ID: " + objectId);
                        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("DATE_FROM_PROFILE", date);
                        extras.putString("OBJECTID_FROM_PROFILE", objectId);
                        intent.putExtras(extras);
                        startActivity(intent);
                    } else {
                        Log.d(TAG, "No object found!");
                    }
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });
    }

    private AlertDialog deleteOption()
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String val = ProfileActivity.this.value;
                        ProfileActivity.this.deleteObject(val);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    private AlertDialog editOption()
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Edit")
                .setMessage("Would you like to edit the item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Date date  = ProfileActivity.this.date;
                        Log.i(TAG, "Date in editDialogue: " + date.toString());
                        ProfileActivity.this.editObject(date);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    private AlertDialog clearOption(int position)
    {
        final int finalPosition = position;
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Clear")
                .setMessage("Clear this appointment?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        acceptedAppts.get(finalPosition).deleteInBackground();
                        Toast.makeText(getApplicationContext(), "Clearing appointment, please wait...", Toast.LENGTH_LONG).show();
                        //todo: check if request actually sent
                        Toast.makeText(getApplicationContext(), "Appointment cleared!", Toast.LENGTH_LONG).show();
                        parseQueries.retrieveAcceptedAppts();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }


    public class ParseQueries <T extends ParseObject> extends Object {

        public void retrieveDatesAvailable() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("DatesAvailable");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        mListDatesAvailable.clear();
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject object = objects.get(i);
                             if (object.getACL().getWriteAccess(ParseUser.getCurrentUser())) {
                                 Date date = (object).getDate("Date");
                                 Log.d(TAG, "Original format: " + date.toString());
                                 formattedDate = dateFormat.format(date);
                                 Log.d(TAG, "Formatted day: " + formattedDate);
                                 mListDatesAvailable.add(formattedDate);
                                 Log.d(TAG, "Reconverted day: " + convertStringToDate(formattedDate));
                             }
                        }
                        mAvailableAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Retrieved " + objects.size() + " appointments");

                    } else {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Error: " + e.getMessage());
                    }
                }
            });
        }

        public void retrieveAcceptedAppts() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("AcceptedAppts");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        mListAppointments.clear();
                        acceptedAppts.clear();
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject object = objects.get(i);
                            if (object.getString("PosterId").equals(ParseUser.getCurrentUser().getUsername())) {
                                if (object.getString("Owner").equals(ParseUser.getCurrentUser().getUsername())) {
                                    String companion = object.getString("RequestorName");
                                    try {
                                        displayDate = dateFormat.format(object.getDate("Appt"));
                                    } catch (NullPointerException f) {
                                        displayDate = "Immediate";
                                    }
                                    acceptedAppts.add(object);
                                    mListAppointments.add(companion + ", " + displayDate);
                                }

                            } else if (object.getString("RequestorId").equals(ParseUser.getCurrentUser().getUsername())) {
                                if (object.getString("Owner").equals(ParseUser.getCurrentUser().getUsername())) {
                                    String companion = object.getString("PosterName");
                                    try {
                                        displayDate = dateFormat.format(object.getDate("Appt"));
                                    } catch (NullPointerException f) {
                                        displayDate = "Immediate";
                                    }
                                    acceptedAppts.add(object);
                                    mListAppointments.add(companion + ", " + displayDate);
                                }
                            }
                        }
                        mAppointmentsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Error: " + e.getMessage());
                    }
                }
            });
        }


        public ParseQuery<ParseObject> deleteObject (Date date) {
            Log.d(TAG, "in deleteObject");
            Log.d(TAG, date.toString());
            ParseQuery<ParseObject> query = ParseQuery.getQuery("DatesAvailable");
            query.whereEqualTo("Date", date);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "Retrieved " + objects.size() + " objects");
                        if (objects.size() > 0) {
                            objects.get(0).deleteInBackground();
                            Toast.makeText(ProfileActivity.this, "Item deleted!", Toast.LENGTH_SHORT).show();
                            mListDatesAvailable.clear();
                            mAvailableAdapter.clear();
                            parseQueries.retrieveDatesAvailable();
                        } else {
                            Log.d(TAG, "No object found!");
                        }


                    } else {
                        Toast.makeText(ProfileActivity.this, "Oops! Item does not exist. Please refresh the page.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Error: " + e.getMessage());
                    }
                }
            });

            return null;
        }
    }

    public boolean isNetworkConnected() {
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED;
    }



}
