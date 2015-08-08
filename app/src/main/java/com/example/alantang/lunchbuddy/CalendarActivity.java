package com.example.alantang.lunchbuddy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CalendarActivity extends Activity
{

    private static final String TAG = "log_message";
    SimpleDateFormat dateFormat = new SimpleDateFormat("E dd MMM yyyy hh:mm aa z");
    String objectId;

    Button buttonSelectDate,buttonSelectTime, buttonSubmit;

    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;

    // variables to save user selected date and time
    public int year, month, day, hour, minute;
    // declare  the variables to Show/Set the date and time when Time and  Date Picker Dialog first appears
    int mYear, mMonth, mDay, mHour, mMinute;

    Date dateSelected;
    boolean fromProfile = false;

    // constructor

    public CalendarActivity()
    {
        // Assign current Date and Time Values to Variables
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // check if information is received from Profile Edit
        receiveIntent();

        // get the references of buttons
        buttonSelectDate=(Button)findViewById(R.id.buttonSelectDate);
        buttonSelectTime=(Button)findViewById(R.id.buttonSelectTime);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);

        // Set ClickListener on btnSelectDate
        buttonSelectDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Show the DatePickerDialog
                showDialog(DATE_DIALOG_ID);
            }
        });

        // Set ClickListener on btnSelectTime
        buttonSelectTime.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Show the TimePickerDialog
                showDialog(TIME_DIALOG_ID);
            }
        });

        //Todo: check for duplicates
        //Todo: make sure select date and select time are clicked to avoid erros
        // Set ClickListener for submit button
        buttonSubmit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (dateSelected != null) {
                    if (fromProfile) {

                        Log.i(TAG, "In from profile, objectId: " + objectId);

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("DatesAvailable");
                        // Retrieve the object by id
                        query.getInBackground(objectId, new GetCallback<ParseObject>() {
                            public void done(ParseObject object, ParseException e) {
                                if (e == null) {
                                    object.put("Date", dateSelected);
                                    Toast.makeText(getApplicationContext(), "Uploading date, please wait...", Toast.LENGTH_LONG).show();
                                    object.saveInBackground();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "Error: " + e.getMessage());
                                }
                            }
                        });
                    } else {
                        if (dateSelected != null) {
                            if (isNetworkConnected()) {
                                ParseObject appointment = new ParseObject("DatesAvailable");
                                ParseACL postACL = new ParseACL(ParseUser.getCurrentUser());
                                postACL.setPublicReadAccess(true);
                                postACL.setPublicWriteAccess(false);
                                appointment.setACL(postACL);
                                appointment.put("Date", dateSelected);
                                appointment.put("Creator", ParseUser.getCurrentUser().getUsername());


                                Log.i(TAG, ParseUser.getCurrentUser().getUsername());
                                Log.i(TAG, "Date: " + dateSelected);
                                Toast.makeText(getApplicationContext(), "Uploading date, please wait...", Toast.LENGTH_LONG).show();
                                appointment.saveInBackground();
                                Toast.makeText(getApplicationContext(), "Uploaded!", Toast.LENGTH_LONG).show();
                                Log.i(TAG, "Uploaded to Parse!");
                            } else {
                                Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Log.i(TAG, "Date and time not inputted.");
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error: please fill in date", Toast.LENGTH_LONG).show();
                }
            }



        });
        //test parse
    }

    private void receiveIntent() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // flag that Calendar was accessed from profile
            fromProfile = true;

            String profileDate = extras.getString("DATE_FROM_PROFILE");
            objectId = extras.getString("OBJECTID_FROM_PROFILE");
            Log.i(TAG, "object id receive intent: " + objectId);

            Calendar date = Calendar.getInstance();
            try {
                date.setTime(dateFormat.parse(profileDate));

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            mYear = date.get(Calendar.YEAR);
            mMonth = date.get(Calendar.MONTH);
            mDay = date.get(Calendar.DAY_OF_MONTH);
            mHour = date.get(Calendar.HOUR_OF_DAY);
            mMinute = date.get(Calendar.MINUTE);
            Log.i(TAG, "Receive intent date: " + profileDate);
        }
    }

    // Register  DatePickerDialog listener
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                // the callback received when the user "sets" the Date in the DatePickerDialog
                public void onDateSet(DatePicker view, int yearSelected,
                                      int monthOfYear, int dayOfMonth) {
                    // quick fix for wrong year
                    year = yearSelected - 1900;
                    month = monthOfYear;
                    day = dayOfMonth;
                    // Set the Selected Date in Select date Button
                    dateSelected = new Date(year, month, day, hour, minute);
                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("E dd MMM yyyy");

//                    buttonSelectDate.setText("Date selected: "+day+" - "+month+" - "+year);
                    String dateDisplay = dateFormat2.format(dateSelected);
                    buttonSelectDate.setText("Date: " + dateDisplay);
                }
            };

    // Register  TimePickerDialog listener
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =

            new TimePickerDialog.OnTimeSetListener() {
                // the callback received when the user "sets" the TimePickerDialog in the dialog
                public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                    hour = hourOfDay;
                    minute = min;
                    //check if date has been filled in
                    if (year != 0 &&  month != 0 && day != 0) {
                        SimpleDateFormat dateFormat3 = new SimpleDateFormat("hh:mm aa");
                        dateSelected = new Date(year, month, day, hour, minute);
                        String timeDisplay = dateFormat3.format(dateSelected);
                        Log.i(TAG, "Date successfully updated with time");
                        Log.i(TAG, "Date: " + dateSelected);
                        buttonSelectTime.setText("Time: " + timeDisplay);
                    }
                    else {
                        Log.i(TAG, "Date not inputted ");
                    }
                    // Set the Selected Date in Select date Button

                }
            };


    // Method automatically gets Called when you call showDialog()  method
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // create a new DatePickerDialog with values you want to show
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
            // create a new TimePickerDialog with values you want to show
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, mHour, mMinute, false);

        }
        return null;
    }


    public boolean isNetworkConnected() {
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED;
    }


}