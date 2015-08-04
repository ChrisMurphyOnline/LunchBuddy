package com.example.alantang.lunchbuddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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


public class ProfileActivity extends ActionBarActivity implements
    customButtonListener
{

    //Todo: remove / change title bar

    private static final String TAG = "log_message";

    ListView mListViewAppointments, mListViewAvailable;
    ArrayList mListAppointments = new ArrayList<String>();
    ArrayList mListDatesAvailable = new ArrayList<String>();
    ListAdapter mAppointmentsAdapter;
    ListAdapter mAvailableAdapter;

    SimpleDateFormat dateFormat = new SimpleDateFormat("E dd MMM yyyy hh:mm aa z");
    String formattedDate;

    ParseQueries parseQueries = new ParseQueries();
//    public static String objectId = "";
    boolean deleteDialogue = false;
    String value = "";
    Date date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // download list of appointments

        parseQueries.retrieveDatesAvailable();

        for (int i = 0; i < mListAppointments.size(); i++) {
            Log.d(TAG, mListAppointments.get(i).toString());
        }
//        mAppointmentsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListAppointments);
//        mAvailableAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListDatesAvailable);

        mListViewAppointments = (ListView)findViewById(R.id.listview_current_appointments);
        mListViewAvailable = (ListView)findViewById(R.id.listview_dates_available);

        mAvailableAdapter = new ListAdapter(ProfileActivity.this, mListDatesAvailable);
        mAvailableAdapter.setCustomButtonListner(ProfileActivity.this);

//        mAppointmentsAdapter = new ListAdapter(ProfileActivity.this, mListAppointments);
//        mAppointmentsAdapter.setCustomButtonListner(ProfileActivity.this);

        mListViewAvailable.setAdapter(mAvailableAdapter);
//        mListViewAppointments.setAdapter(mAppointmentsAdapter);



//        ListUtils.setDynamicHeight(mListViewAppointments);
//        ListUtils.setDynamicHeight(mListViewAvailable);


    }

//    // added code for listviews
//    public static class ListUtils {
//        public static void setDynamicHeight(ListView mListView) {
//            ListAdapter mListAdapter = mListView.getAdapter();
//            if (mListAdapter == null) {
//                // when adapter is null
//                return;
//            }
//            int height = 0;
//            int desiredWidth = MeasureSpec.makeMeasureSpec(mListView.getWidth(), MeasureSpec.UNSPECIFIED);
//            for (int i = 0; i < mListAdapter.getCount(); i++) {
//                View listItem = mListAdapter.getView(i, null, mListView);
//                listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
//                height += listItem.getMeasuredHeight();
//            }
//            ViewGroup.LayoutParams params = mListView.getLayoutParams();
//            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
//            mListView.setLayoutParams(params);
//            mListView.requestLayout();
//        }
//    }

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


    public class ParseQueries <T extends ParseObject> extends Object {

        public ParseQuery<ParseObject> retrieveDatesAvailable() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("DatesAvailable");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject object = objects.get(i);
                             if (object.getACL().getWriteAccess(ParseUser.getCurrentUser())) {
                                 Date date = (object).getDate("Date");
                                 Log.d(TAG, "Original format: " + date.toString());
                                 formattedDate = dateFormat.format(date);
                                 Log.d(TAG, "Formatted day: " + formattedDate);
                                 mListDatesAvailable.add(formattedDate);
                                 Log.d(TAG, "Reconverted day: " + convertStringToDate(formattedDate));
                                 mAvailableAdapter.notifyDataSetChanged();
                             }
                        }
                        Log.d(TAG, "Retrieved " + objects.size() + " appointments");

                    } else {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Error: " + e.getMessage());
                    }
                }
            });
            return null;
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



}
