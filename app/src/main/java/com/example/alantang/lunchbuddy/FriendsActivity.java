package com.example.alantang.lunchbuddy;

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
public class FriendsActivity extends ActionBarActivity implements Serializable {

    private static final String TAG = "log_message";

    FacebookListAdapter facebookAdaptor;
    ListView mListViewFacebookIds;
    ArrayList<FacebookFriend> facebookIds = new ArrayList<FacebookFriend>();

    ParseQueries parseQueries = new ParseQueries();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);


        mListViewFacebookIds = (ListView)findViewById(R.id.listView3);
        facebookAdaptor = new FacebookListAdapter(FriendsActivity.this, R.layout.child_friendslistview, facebookIds);
        mListViewFacebookIds.setAdapter(facebookAdaptor);


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
                        for (int i = 0; i < objects.size(); i++) {
                            if (objects.get(i).getString("FacebookId").equals(finalId)) {

//                                Log.d(TAG, "User name: " + objects.get(i).getUsername());
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

                        facebookAdaptor.notifyDataSetChanged();
//                        for (int i = 0; i < facebookIds.size(); i++) {
//                            Log.d(TAG, "Updated Friend 1 ID: " + facebookIds.get(0).id);
//                            Log.d(TAG, "Updated Friend 2 ID: " + facebookIds.get(1).id);
//                            Log.d(TAG, "Updated Friend 1 name: " + facebookIds.get(0).name);
//                            Log.d(TAG, "Updated Friend 2 name: " + facebookIds.get(1).name);
//                            Log.d(TAG, "Updated Friend 1 username: " + facebookIds.get(0).username);
//                            Log.d(TAG, "Updated Friend 2 username: " + facebookIds.get(1).username);
//                            Log.d(TAG, "Updated Friend 1 dates: " + facebookIds.get(0).dates.toString());
//                            Log.d(TAG, "Updated Friend 2 dates: " + facebookIds.get(1).dates.toString());
//                        }

                    } else {
                        Log.d(TAG, "Error: " + e.getMessage());
                    }
                }
            });
            //may have to do subsequent tasks here

        }

    }
}
