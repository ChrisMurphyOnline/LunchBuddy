package com.example.alantang.lunchbuddy;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.alantang.lunchbuddy.PendingListAdapter.customButtonListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class PendingActivity extends ActionBarActivity
//    implements customButtonListener
{

    private static final String TAG = "log_message";

    ParseQueries parseQueries = new ParseQueries();

    ListView mListViewRequest, mListViewAccept, mListViewRejected;
    List mListRequest = new ArrayList<String>();
    ArrayList mListAccept = new ArrayList<String>();
    List mListRejected = new ArrayList<String>();

    PendingListAdapter  mAcceptAdapter;
    ArrayAdapter<String> mRequestAdapter, mRejectedAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);

        parseQueries.retrievePendingRequests();

        mListViewRequest = (ListView) findViewById(R.id.listview_pending_requests);

        mRequestAdapter = new ArrayAdapter<String>(PendingActivity.this, R.layout.child_pendinglist, mListRequest);

        mListViewRequest.setAdapter(mRequestAdapter);

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

    public class ParseQueries <T extends ParseObject> extends Object {
        public void retrievePendingRequests() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("PendingAppts");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject object = objects.get(i);
                            if (object.getString("RequestorId").equals(ParseUser.getCurrentUser().getUsername())) {
                                String request = object.getString("PosterName") + ", " + object.getDate("Appt").toString();
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
    }


}
