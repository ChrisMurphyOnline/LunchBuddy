package com.example.alantang.lunchbuddy;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.Serializable;


public class FriendsDetailActivity extends FragmentActivity {

    private static final String TAG = "log_message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_detail);


        if (savedInstanceState == null) {
            Log.d(TAG, "in friends detail activity");

            Bundle bundle = new Bundle();
            bundle.putSerializable("datesDetail", getIntent().getSerializableExtra("datesDetail"));

            FriendsDetailFragment friendsDetailFragment = new FriendsDetailFragment();
            friendsDetailFragment.setArguments(bundle);

            getFragmentManager().beginTransaction()
                    .add(R.id.friends_detail_container, friendsDetailFragment)
                    .commit();

        }

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
}
