package com.example.alantang.lunchbuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
import android.content.Loader;
import android.content.Context;


public class ProfileActivity extends Activity implements LoaderCallbacks<Cursor>, ConfirmedListAdapter.customButtonListener
{

    //Todo: remove / change title bar

    private static final String TAG = "log_message";
    ArrayList<ParseObject> acceptedAppts = new ArrayList<ParseObject>() ;

    ListView mListViewAppointments, mListViewAvailable;
    ArrayList mListAppointments = new ArrayList<String>();
    ConfirmedListAdapter mAppointmentsAdapter;
    DatesAvailCursorAdapter datesAvailAdapter;
    Cursor datesAvailCursor;

    String displayDate;

    SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy hh:mm aaa");

    String formattedDate;

    ParseQueries parseQueries = new ParseQueries();
    String value = "";
    Date date;

    private static final int availableLoader = 0;
    SQLiteDatabase datesAvailDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        DatesAvailDatabase handler = new DatesAvailDatabase(this);
        datesAvailDb = handler.getWritableDatabase();

        // download list of appointments

        mListViewAppointments = (ListView)findViewById(R.id.listview_current_appointments);

        mAppointmentsAdapter = new ConfirmedListAdapter(ProfileActivity.this, mListAppointments);
        mAppointmentsAdapter.setCustomButtonListner(ProfileActivity.this);


        mListViewAppointments.setAdapter(mAppointmentsAdapter);

        Button buttonRefresh = (Button) findViewById(R.id.profileRefreshButton);

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });

        if (isNetworkConnected()) {
            parseQueries.retrieveAcceptedAppts();
            parseQueries.retrieveDatesAvailable();

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
    public void onClearClickListener(int position, String value) {
        AlertDialog clearBox = clearOption(position);
        clearBox.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                DatesAvailDatabase.ID, DatesAvailDatabase.COL_DATE };
        CursorLoader cursorLoader = new CursorLoader(this,
                DatesAvailListProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        datesAvailAdapter.swapCursor(data);
        datesAvailAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        datesAvailAdapter.swapCursor(null);
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



    // opens up Calendar dialogue to edit date
    private void editObject(Date date) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("DatesAvailable");
        query.whereEqualTo("Date", date);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        String objectId = objects.get(0).getObjectId();
                        String date = objects.get(0).getDate("Date").toString();
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
//                        mListDatesAvailable.clear();
                        //clear database contents
                        datesAvailDb.delete("dates", null, null);
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject object = objects.get(i);
                             if (object.getACL().getWriteAccess(ParseUser.getCurrentUser())) {
                                 Date date = (object).getDate("Date");
                                 Log.d(TAG, "Original format: " + date.toString());
                                 formattedDate = dateFormat.format(date);
                                 Log.d(TAG, "Formatted day: " + formattedDate);

                                 ContentValues values = new ContentValues();
                                 values.put(DatesAvailDatabase.COL_USER, object.getString("Creator"));
                                 values.put(DatesAvailDatabase.COL_DATE, formattedDate);
                                 values.put(DatesAvailDatabase.COL_UPDATED, dateFormat.format(object.getUpdatedAt()));
                                 datesAvailDb.insert(DatesAvailDatabase.TABLE_DATES_AVAIL, null, values);
                             }
                        }
                        datesAvailCursor = datesAvailDb.rawQuery("SELECT * FROM dates", null);

                        displayListView();


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
    }

    public boolean isNetworkConnected() {
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED;
    }


    private void displayListView() {


        // The desired columns to be bound
        String[] columns = new String[] {
                DatesAvailDatabase.COL_DATE,
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[] {
                R.id.childTextView,
        };

        // create an adapter from the SimpleCursorAdapter
        datesAvailAdapter = new DatesAvailCursorAdapter(
                this,
                null,
                0);
        // get reference to the ListView
        mListViewAvailable = (ListView)findViewById(R.id.listview_dates_available); //moved to botton

        // Assign adapter to ListView
        mListViewAvailable.setAdapter(datesAvailAdapter);
        //Ensures a loader is initialized and active.
        getLoaderManager().initLoader(availableLoader, null, this);

    }


}
