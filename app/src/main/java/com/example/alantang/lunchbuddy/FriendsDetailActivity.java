package com.example.alantang.lunchbuddy;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.example.alantang.lunchbuddy.DetailListAdapter.customButtonListener;
import com.example.alantang.lunchbuddy.DetailListAdapter;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;


public class FriendsDetailActivity extends Activity implements LoaderManager.LoaderCallbacks<Void>, customButtonListener {

    private static final String TAG = "log_message";
    ArrayList<Date> mListDates = new ArrayList<Date>();
    ArrayList<String> mListDatesString = new ArrayList<String>();
    ListView mListViewDates;
    DetailListAdapter mDatesAdaptor;
    FacebookFriend receivedFriend;
    SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy hh:mm aaa");

    private static final int friendsDetailLoader = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_detail);

        mListViewDates = (ListView) findViewById(R.id.listview_friends_detail);
        mDatesAdaptor = new DetailListAdapter(FriendsDetailActivity.this, mListDatesString);
        mDatesAdaptor.setCustomButtonListner(FriendsDetailActivity.this);
        mListViewDates.setAdapter(mDatesAdaptor);

        getLoaderManager().initLoader(friendsDetailLoader, null, this);

    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "friends detail on resume");
        getLoaderManager().initLoader(friendsDetailLoader, null, this);
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "friends detail on pause");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "friends detail on stop");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "friends detail on destroy");
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Log.d(TAG, "friends detail on restart");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends_detail, menu);
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
        switch (id) {
            case friendsDetailLoader:
                Log.d(TAG, "in friend detail loader");
                TextView title = (TextView) findViewById(R.id.displayFriendDetail);
                Intent intent = getIntent();
                receivedFriend = (FacebookFriend) intent.getSerializableExtra("datesDetail");

                title.setText(receivedFriend.name + "'s Available Dates: ");

                mListDates = receivedFriend.dates;
                mListDatesString.clear();
                for (int i = 0; i < mListDates.size(); i++) {
                    mListDatesString.add(dateFormat.format(mListDates.get(i)));
                }
                mDatesAdaptor.notifyDataSetChanged();
                break;
            default:
                return null;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Void> loader, Void params) {
        getLoaderManager().destroyLoader(friendsDetailLoader);
    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {
        mListViewDates.setAdapter(null);
    }


    @Override
    public void onRequestClickListener(int position, String value) {
        AlertDialog requestBox = requestOption(position);
        requestBox.show();
    }

    private AlertDialog requestOption(int position) {
        final int finalPosition = position;
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Request")
                .setMessage("Would you like to send this request?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        Log.d(TAG, "Date requested: " + mListDates.get(finalPosition).toString());
                        Log.d(TAG, "Friend name: " + receivedFriend.name);


                        ParseObject appointment = new ParseObject("PendingAppts");
                        ParseACL postACL = new ParseACL(ParseUser.getCurrentUser());
                        postACL.setPublicReadAccess(true);
                        postACL.setPublicWriteAccess(true);
                        appointment.setACL(postACL);
                        appointment.put("Appt", mListDates.get(finalPosition));
                        appointment.put("PosterName", receivedFriend.name);
                        appointment.put("PosterId", receivedFriend.username);

                        appointment.put("RequestorName", ParseUser.getCurrentUser().get("FacebookName"));
                        appointment.put("RequestorId", ParseUser.getCurrentUser().getUsername());

                        Toast.makeText(getApplicationContext(), "Sending request, please wait...", Toast.LENGTH_LONG).show();
                        appointment.saveInBackground();
                        //todo: check if request actually sent
                        Toast.makeText(getApplicationContext(), "Request sent!", Toast.LENGTH_LONG).show();
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
}
