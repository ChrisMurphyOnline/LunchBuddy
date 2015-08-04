package com.example.alantang.lunchbuddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import com.example.alantang.lunchbuddy.DetailListAdapter.customButtonListener;
import com.example.alantang.lunchbuddy.DetailListAdapter;


public class FriendsDetailActivity extends ActionBarActivity implements customButtonListener {

    private static final String TAG = "log_message";
    ArrayList<Date> mListDates = new ArrayList<Date>();
    ArrayList<String> mListDatesString = new ArrayList<String>();
    ListView mListViewDates;
    DetailListAdapter mDatesAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_detail);

        TextView title = (TextView) findViewById(R.id.displayFriendDetail);


        Intent intent = getIntent();
        FacebookFriend receivedFriend = (FacebookFriend) intent.getSerializableExtra("datesDetail");

        title.setText(receivedFriend.name + "'s Available Dates: ");

        mListDates = receivedFriend.dates;
        for (int i = 0; i < mListDates.size(); i++) {
            mListDatesString.add(mListDates.get(i).toString());
        }
        Log.d(TAG, mListDatesString.toString());

        mListViewDates = (ListView) findViewById(R.id.listView4);
        mDatesAdaptor = new DetailListAdapter(FriendsDetailActivity.this, mListDatesString);
        mListViewDates.setAdapter(mDatesAdaptor);


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
    public void onRequestClickListener(int position, String value) {
        AlertDialog addBox = requestOption();
        addBox.show();

    }

    public AlertDialog requestOption() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Request")
                .setMessage("Would you like to send this request?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
//
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
