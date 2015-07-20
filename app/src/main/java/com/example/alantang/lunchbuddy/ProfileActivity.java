package com.example.alantang.lunchbuddy;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class ProfileActivity extends ActionBarActivity {

    //Todo: remove / change title bar

    private static final String TAG = "log_message";

    ListView mListViewAppointments, mListViewAvailable;
    ArrayList mListAppointments = new ArrayList<String>();
    ArrayList mListDatesAvailable = new ArrayList<String>();
    ArrayAdapter<String> mAppointmentsAdaptor;
    ArrayAdapter<String> mAvailableAdaptor;

// placeholders before the 2 listviews are populated
    private String [] data1 ={"Hiren", "Pratik", "Dhruv", "Narendra", "Piyush", "Priyank"};
//    private String [] data2 ={"Kirit", "Miral", "Bhushan", "Jiten", "Ajay", "Kamlesh"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // download list of appointments
        ParseQueryAppointments parseQueryAppointments = new ParseQueryAppointments();
        parseQueryAppointments.getQuery();

        for (int i = 0; i < mListAppointments.size(); i++) {
            Log.d(TAG, mListAppointments.get(i).toString());
        }
        mAppointmentsAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListAppointments);
        mAvailableAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListDatesAvailable);

        mListViewAppointments = (ListView)findViewById(R.id.listView1);
        mListViewAvailable = (ListView)findViewById(R.id.listView2);
        mListViewAppointments.setAdapter(mAppointmentsAdaptor);
        mListViewAvailable.setAdapter(mAvailableAdaptor);

        ListUtils.setDynamicHeight(mListViewAppointments);
        ListUtils.setDynamicHeight(mListViewAvailable);

    }

    // added code for listviews
    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = MeasureSpec.makeMeasureSpec(mListView.getWidth(), MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }

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

    public class ParseQueryAppointments <T extends ParseObject> extends Object {

        public ParseQuery<ParseObject> getQuery() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("DatesAvailable");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null) {
                        for (int i = 0; i < objects.size(); i++) {
                            Object object = objects.get(i);
                            String date = ((ParseObject) object).getDate("Date").toString();
                            mListDatesAvailable.add(date);


// mListAppointments.add(objects.get(i).getDate("Date").toString());
                            Log.d(TAG, objects.get(i).getDate("Date").toString());

                        }
                        mAvailableAdaptor.notifyDataSetChanged();
                        Log.d(TAG, "Retrieved " + objects.size() + " appointments");

                    } else {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Error: " + e.getMessage());
                    }
                }
            });
            return null;
        }
    }



}
