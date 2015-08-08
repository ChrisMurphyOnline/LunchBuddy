package com.example.alantang.lunchbuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.alantang.lunchbuddy.PendingListAdapter.customButtonListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class PendingActivity extends Activity implements LoaderManager.LoaderCallbacks<Void>, PendingListAdapter.customButtonListener, RejectedListAdapter.customButtonListener
{

    private static final String TAG = "log_message";

    ArrayList<ParseObject> pendingAccepts = new ArrayList<ParseObject>() ;
    ArrayList<ParseObject> rejectedAppts = new ArrayList<ParseObject>() ;
    String displayDate;

    ParseQueries parseQueries = new ParseQueries();

    ListView mListViewRequest, mListViewAccept, mListViewRejected;
    List mListRequest = new ArrayList<String>();
    ArrayList mListAccept = new ArrayList<String>();
    ArrayList mListRejected = new ArrayList<String>();

    PendingListAdapter  mAcceptAdapter;
    RejectedListAdapter mRejectedAdapter;
    ArrayAdapter<String> mRequestAdapter;

    SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy hh:mm aaa");



    private static final int requestsLoader = 0;
    private static final int rejectedLoader = 1;
    private static final int acceptsLoader = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);

        mListViewRequest = (ListView) findViewById(R.id.listview_pending_requests);
        mListViewAccept = (ListView) findViewById(R.id.listview_pending_accepts);
        mListViewRejected = (ListView) findViewById(R.id.listview_rejected_requests);

        mRequestAdapter = new ArrayAdapter<String>(PendingActivity.this, R.layout.child_pendinglist, mListRequest);
        mAcceptAdapter = new PendingListAdapter(PendingActivity.this, mListAccept);
        mAcceptAdapter.setCustomButtonListner(PendingActivity.this);
        mRejectedAdapter = new RejectedListAdapter(PendingActivity.this, mListRejected);
        mRejectedAdapter.setCustomButtonListner(PendingActivity.this);

        mListViewRequest.setAdapter(mRequestAdapter);
        mListViewAccept.setAdapter(mAcceptAdapter);
        mListViewRejected.setAdapter(mRejectedAdapter);



        if (isNetworkConnected()) {
            getLoaderManager().initLoader(requestsLoader, null, this);
            getLoaderManager().initLoader(rejectedLoader, null, this);
            getLoaderManager().initLoader(acceptsLoader, null, this);
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pending, menu);
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
    public Loader<Void> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "in create loader");
        switch (id) {
            case requestsLoader:
                parseQueries.retrievePendingRequests();
                break;
            case rejectedLoader:
                parseQueries.retrieveRejectedRequests();
                break;
            case acceptsLoader:
                parseQueries.retrievePendingAccepts();
                break;
            default:
                return null;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Void> loader, Void params) {
        getLoaderManager().destroyLoader(requestsLoader);
        getLoaderManager().destroyLoader(rejectedLoader);
        getLoaderManager().destroyLoader(acceptsLoader);

    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {
        mListViewRequest.setAdapter(null);
        mListViewRejected.setAdapter(null);
        mListViewAccept.setAdapter(null);
    }


    @Override
    public void onAcceptClickListener(int position, String value) {
        AlertDialog acceptBox = acceptOption(position);
        acceptBox.show();
    }

    @Override
    public void onRejectClickListener(int position, String value) {

        AlertDialog rejectBox = rejectOption(position);
        rejectBox.show();
    }

    @Override
    public void onClearClickListener(int position, String value) {
        AlertDialog clearBox = clearOption(position);
        clearBox.show();
    }

    private AlertDialog acceptOption(int position)
    {
        final int finalPosition = position;
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Accept")
                .setMessage("Accept this appointment?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ParseObject requestorAppointment = new ParseObject("AcceptedAppts");
                        ParseObject posterAppointment = new ParseObject("AcceptedAppts");
                        ParseObject objectMoved = pendingAccepts.get(finalPosition);
                        //retrieve object from Pending accepts
                        try {
                            requestorAppointment.put("Appt", objectMoved.getDate("Appt"));
                        } catch (IllegalArgumentException e) {

                        }
                        requestorAppointment.put("PosterName", objectMoved.get("PosterName"));
                        requestorAppointment.put("PosterId", objectMoved.get("PosterId"));
                        requestorAppointment.put("RequestorName", objectMoved.get("RequestorName"));
                        requestorAppointment.put("RequestorId", objectMoved.get("RequestorId"));
                        requestorAppointment.put("Owner", objectMoved.get("RequestorId"));

                        try {
                            posterAppointment.put("Appt", objectMoved.getDate("Appt"));
                        } catch (IllegalArgumentException e) {

                        }
                        posterAppointment.put("PosterName", objectMoved.get("PosterName"));
                        posterAppointment.put("PosterId", objectMoved.get("PosterId"));
                        posterAppointment.put("RequestorName", objectMoved.get("RequestorName"));
                        posterAppointment.put("RequestorId", objectMoved.get("RequestorId"));
                        posterAppointment.put("Owner", objectMoved.get("PosterId"));

                        //delete from PendingAppts class
                        pendingAccepts.get(finalPosition).deleteInBackground();

                        //clear and refresh data
                        pendingAccepts.clear();
                        Toast.makeText(getApplicationContext(), "Sending request, please wait...", Toast.LENGTH_LONG).show();

                        requestorAppointment.saveInBackground();
                        posterAppointment.saveInBackground();

                        //todo: check if request actually sent
                        Toast.makeText(getApplicationContext(), "Appointment confirmed!", Toast.LENGTH_LONG).show();
                        parseQueries.retrievePendingAccepts();
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

    private AlertDialog rejectOption(int position)
    {
        final int finalPosition = position;
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Reject")
                .setMessage("Reject this appointment?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ParseObject appointment = new ParseObject("RejectedAppts");

                        ParseObject objectMoved = pendingAccepts.get(finalPosition);
                        //retrieve object from Pending accepts
                        try {
                            appointment.put("Appt", objectMoved.getDate("Appt"));
                        } catch (IllegalArgumentException e) {
                        }

                        appointment.put("PosterName", objectMoved.get("PosterName"));
                        appointment.put("PosterId", objectMoved.get("PosterId"));
                        appointment.put("RequestorName", objectMoved.get("RequestorName"));
                        appointment.put("RequestorId", objectMoved.get("RequestorId"));

                        //delete from PendingAppts class
                        pendingAccepts.get(finalPosition).deleteInBackground();

                        //clear and refresh data
                        pendingAccepts.clear();
                        Toast.makeText(getApplicationContext(), "Sending request, please wait...", Toast.LENGTH_LONG).show();

                        appointment.saveInBackground();

                        //todo: check if request actually sent
                        Toast.makeText(getApplicationContext(), "Appointment rejected!", Toast.LENGTH_LONG).show();
                        parseQueries.retrievePendingAccepts();
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
                        rejectedAppts.get(finalPosition).deleteInBackground();
                        rejectedAppts.clear();
                        Toast.makeText(getApplicationContext(), "Clearing appointment, please wait...", Toast.LENGTH_LONG).show();
                        //todo: check if request actually sent
                        Toast.makeText(getApplicationContext(), "Appointment cleared!", Toast.LENGTH_LONG).show();
                        parseQueries.retrieveRejectedRequests();
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
        public void retrievePendingRequests() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("PendingAppts");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        mListRequest.clear();
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject object = objects.get(i);
                            if (object.getString("RequestorId").equals(ParseUser.getCurrentUser().getUsername())) {
                                try {
                                    displayDate = dateFormat.format(object.getDate("Appt"));
                                } catch (NullPointerException f) {
                                    displayDate = "Immediate";
                                }
                                String request = object.getString("PosterName") + ", " + displayDate;
                                mListRequest.add(request);
                            }
                        }
                        mRequestAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Error: " + e.getMessage());
                    }
                }
            });
        }

        public void retrievePendingAccepts() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("PendingAppts");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        mListAccept.clear();
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject object = objects.get(i);
                            if (object.getString("PosterId").equals(ParseUser.getCurrentUser().getUsername())) {
                                pendingAccepts.add(object);
                                try {
                                    displayDate = dateFormat.format(object.getDate("Appt"));
                                } catch (NullPointerException f) {
                                    displayDate = "Immediate";
                                }
                                String request = object.getString("RequestorName") + ", " + displayDate;
                                mListAccept.add(request);
                            }
                        }
                        mAcceptAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Error: " + e.getMessage());
                    }
                }
            });
        }

        public void retrieveRejectedRequests() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("RejectedAppts");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        mListRejected.clear();
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject object = objects.get(i);
                            if (object.getString("RequestorId").equals(ParseUser.getCurrentUser().getUsername())) {
                                rejectedAppts.add(object);
                                try {
                                    displayDate = dateFormat.format(object.getDate("Appt"));
                                } catch (NullPointerException f) {
                                    displayDate = "Immediate";
                                }
                                String request = object.getString("PosterName") + ", " + displayDate;
                                mListRejected.add(request);
                            }
                        }
                        mRejectedAdapter.notifyDataSetChanged();
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


}
