package com.example.alantang.lunchbuddy;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class FriendsDisplayActivity extends FragmentActivity {

    private boolean mTwoPane;
    private static final String TAG = "log_message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_holder);

    if (findViewById(R.id.friends_detail_container) != null) {
        // The detail container view will be present only in the large-screen layouts
        // (res/layout-large). If this view is present, then the activity should be
        // in two-pane mode.
        Log.d(TAG, "in two pane");

        mTwoPane = true;
        // In two-pane mode, show the detail view in this activity by
        // adding or replacing the detail fragment using a
        // fragment transaction.

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.friends_detail_container, new FriendsDetailFragment())
                    .commit();
        }
    } else {
        Log.d(TAG, "in one pane");
    }

    getFragmentManager().findFragmentById(R.id.friendsDisplayFragment);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends_holder, menu);
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

    public boolean getTwoPane() {
        return mTwoPane;
    }

}
