package com.example.alantang.lunchbuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.camera2.params.Face;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.example.alantang.lunchbuddy.FacebookListAdapter;
import com.example.alantang.lunchbuddy.FacebookFriend;

import android.content.Intent;

@SuppressWarnings("serial")
public class FriendsActivity extends Activity implements Serializable, FriendsNowListAdapter.customButtonListener {

    private static final String TAG = "log_message";

    FacebookListAdapter facebookAdapter;
    FriendsNowListAdapter friendsNowAdapter;
    ListView mListViewFacebookIds, mListViewFriendsAvailableNow;

    ArrayList<FacebookFriend> facebookIds = new ArrayList<FacebookFriend>();
    ArrayList<FacebookFriend> friendsNowArray = new ArrayList<FacebookFriend>();

    ParseQueries parseQueries = new ParseQueries();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);


        mListViewFacebookIds = (ListView)findViewById(R.id.listview_friends);
        mListViewFriendsAvailableNow = (ListView) findViewById(R.id.listview_friends_now);

        facebookAdapter = new FacebookListAdapter(FriendsActivity.this, R.layout.child_friendslistview, facebookIds);
        friendsNowAdapter = new FriendsNowListAdapter(FriendsActivity.this, R.layout.child_friendsnowlistview, friendsNowArray);
        friendsNowAdapter.setCustomButtonListner(FriendsActivity.this);

        mListViewFacebookIds.setAdapter(facebookAdapter);
        mListViewFriendsAvailableNow.setAdapter(friendsNowAdapter);


//        downloads list of Facebook friends
        new DownloadFriendsList().execute();

        mListViewFacebookIds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = mListViewFacebookIds.getItemAtPosition(position);
                Intent intent = new Intent(view.getContext(), FriendsDetailActivity.class);
                intent.putExtra("datesDetail", (Serializable) listItem);
                startActivity(intent);
            }
        });
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

                        Log.d(TAG, "Friend name: " + friendsNowArray.get(finalPosition));

                        ParseObject appointment = new ParseObject("PendingAppts");
                        ParseACL postACL = new ParseACL(ParseUser.getCurrentUser());
                        postACL.setPublicReadAccess(true);
                        postACL.setPublicWriteAccess(true);
                        appointment.setACL(postACL);
                        appointment.put("PosterName", friendsNowArray.get(finalPosition).name);
                        appointment.put("PosterId", friendsNowArray.get(finalPosition).username);

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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
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

    private class DownloadFriendsList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
                @Override
                public void onCompleted(JSONArray objects, GraphResponse graphResponse) {
                    Log.d(TAG, "Graph friends: " + objects.toString());

                    for (int i = 0; i < objects.length(); i++) {
                        try {
                            FacebookFriend friend = new FacebookFriend(objects.getJSONObject(i).getString("id"),
                                    objects.getJSONObject(i).getString("name"));
                            facebookIds.add(friend);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }}
                ).executeAndWait();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            for (int i = 0; i < facebookIds.size(); i++) {
                Log.d(TAG, "Id: " + facebookIds.get(i).id + ", Name: " + facebookIds.get(i).name);
                parseQueries.retrieveUsername(facebookIds.get(i).id, facebookIds.get(i));
            }
        }
    }

    public class ParseQueries <T extends ParseObject> extends Object {

        public FacebookFriend retrieveUsername(String id, final FacebookFriend friend) {
            ParseQuery query = ParseUser.getQuery();
            final String finalId = id;
//            final FacebookFriend finalFriend = friend;
            query.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        friendsNowArray.clear();
                        for (int i = 0; i < objects.size(); i++) {
                            if (objects.get(i).getString("FacebookId").equals(finalId)) {
                                // check for friends available now
                                if (objects.get(i).getBoolean("Available")) {
                                    FacebookFriend friend = new FacebookFriend(objects.get(i).getString("FacebookId"), objects.get(i).getString("FacebookName"));
                                    friend.username = objects.get(i).getString("username");
                                    friendsNowArray.add(friend);
                                }
                                friend.setUsername(objects.get(i).getUsername());
                                parseQueries.retrieveDatesAvailable(objects.get(i).getUsername(), friend);
                            }
                        }
                    } else {
                        // Something went wrong. Look at the ParseException to see what's up.
                        Log.d(TAG, "Error: " + e.getMessage());
                    }
                }
            });
            return friend;
        }

        public void retrieveDatesAvailable(String username, final FacebookFriend friend) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("DatesAvailable");
            final String finalUsername = username;
//            final FacebookFriend finalFriend = friend;
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        for (int i = 0; i < objects.size(); i++) {
//                            Log.d(TAG, "Creator: " + objects.get(i).getString("Creator"));
                            if (objects.get(i).get("Creator").equals(finalUsername)) {
                                Log.d(TAG, "Date: " + objects.get(i).getDate("Date").toString());
                                friend.addDate(objects.get(i).getDate("Date"));
                                friend.updateNumberOfDates();
                            }
                        }
                        ///// do code here... because somehow onPostExecute doesn't wait for ParseQueries to complete :(
                        facebookAdapter.notifyDataSetChanged();
                        friendsNowAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Error: " + e.getMessage());
                    }
                }
            });
            //may have to do subsequent tasks here

        }

    }
}
