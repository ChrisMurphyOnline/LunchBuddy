package com.example.alantang.lunchbuddy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;

//import net.danlew.android.joda.JodaTimeAndroid;

public class CalendarActivity extends Activity
{

    private static final String TAG = "log_message";

    Button buttonSelectDate,buttonSelectTime, buttonSubmit;

    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;

    // variables to save user selected date and time
    public int year, month, day, hour, minute;
    // declare  the variables to Show/Set the date and time when Time and  Date Picker Dialog first appears
    private int mYear, mMonth, mDay, mHour, mMinute;

    Date dateSelected;

    // constructor

    public CalendarActivity()
    {
        // Assign current Date and Time Values to Variables
        final Calendar c = Calendar.getInstance();
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


        // Set ClickListener for submit button
        buttonSubmit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (dateSelected != null) {
                    ParseObject appointment = new ParseObject("DatesAvailable");
                    ParseACL postACL = new ParseACL(ParseUser.getCurrentUser());
                    postACL.setPublicReadAccess(true);
                    postACL.setPublicWriteAccess(false);
                    appointment.setACL(postACL);
                    appointment.put("Date", dateSelected);
                    appointment.saveInBackground();
                    Log.i(TAG, ParseUser.getCurrentUser().toString());
                    Log.i(TAG, "Date: " + dateSelected);
                    Log.i(TAG, "Uploaded to Parse!");

                }
                else {
                    Log.i(TAG, "Date and time not inputted.");
                }
            }
        });
        //test parse
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
                    dateSelected = new Date(year, month, day);
//                    buttonSelectDate.setText("Date selected: "+day+" - "+month+" - "+year);
                    String dateDisplay = dateSelected.toString().split("00:")[0];
                    buttonSelectDate.setText("Date: " + dateDisplay);
                }
            };

    // Register  TimePickerDialog listener
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =

            //Todo: fix time display, e.g. 12.1pm -> 12.01pm
            new TimePickerDialog.OnTimeSetListener() {
                // the callback received when the user "sets" the TimePickerDialog in the dialog
                public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                    hour = hourOfDay;
                    minute = min;
                    //check if date has been filled in
                    if (year != 0 &&  month != 0 && day != 0) {
                        dateSelected = new Date(year, month, day, hour, minute);
                        Log.i(TAG, "Date successfully updated with time");
                        Log.i(TAG, "Date: " + dateSelected);
                    }
                    else {
                        Log.i(TAG, "Date not inputted ");
                    }
                    // Set the Selected Date in Select date Button
                    buttonSelectTime.setText("Time: "+hour+"."+minute);
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

}