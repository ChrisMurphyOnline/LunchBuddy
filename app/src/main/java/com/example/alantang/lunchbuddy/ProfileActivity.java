package com.example.alantang.lunchbuddy;

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
    ListAdapter mAppointmentsAdaptor;
    ListAdapter mAvailableAdaptor;

    SimpleDateFormat dateFormat = new SimpleDateFormat("E dd MMM yyyy hh:mm aa z");
    String formattedDate;

    ParseQueries parseQueries = new ParseQueries();


    // placeholders before the 2 listviews are populated
    private String [] data1 ={"Hiren", "Pratik", "Dhruv", "Narendra", "Piyush", "Priyank"};
//    private String [] data2 ={"Kirit", "Miral", "Bhushan", "Jiten", "Ajay", "Kamlesh"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // download list of appointments

        parseQueries.retrieveDatesAvailable();

        for (int i = 0; i < mListAppointments.size(); i++) {
            Log.d(TAG, mListAppointments.get(i).toString());
        }
//        mAppointmentsAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListAppointments);
//        mAvailableAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListDatesAvailable);

        mListViewAppointments = (ListView)findViewById(R.id.listView1);
        mListViewAvailable = (ListView)findViewById(R.id.listView2);

        mAvailableAdaptor = new ListAdapter(ProfileActivity.this, mListDatesAvailable);
        mAvailableAdaptor.setCustomButtonListner(ProfileActivity.this);

//        mAppointmentsAdaptor = new ListAdapter(ProfileActivity.this, mListAppointments);
//        mAppointmentsAdaptor.setCustomButtonListner(ProfileActivity.this);

        mListViewAvailable.setAdapter(mAvailableAdaptor);
//        mListViewAppointments.setAdapter(mAppointmentsAdaptor);



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

        Toast.makeText(ProfileActivity.this, "Button click " + value,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClickListener(int position, String value) {
        Date date = convertStringToDate(value);
        parseQueries.deleteObject(date);
        Toast.makeText(ProfileActivity.this, "Deleted!",
                Toast.LENGTH_SHORT).show();
        mListDatesAvailable.clear();
        mAvailableAdaptor.clear();
        parseQueries.retrieveDatesAvailable();
//        mAvailableAdaptor.notifyDataSetChanged();

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


    public class ParseQueries <T extends ParseObject> extends Object {

        public ParseQuery<ParseObject> retrieveDatesAvailable() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("DatesAvailable");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null) {
                        for (int i = 0; i < objects.size(); i++) {
                            Object object = objects.get(i);
                            Date date = ((ParseObject) object).getDate("Date");
                            Log.d(TAG, "Original format: " + date.toString());
                            formattedDate = dateFormat.format(date);
                            Log.d(TAG, "Formatted day: " + formattedDate);
                            mListDatesAvailable.add(formattedDate);
                            Log.d(TAG, "Reconverted day: " + convertStringToDate(formattedDate));
                            mAvailableAdaptor.notifyDataSetChanged();
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
                        } else {
                            Log.d(TAG, "No object found!");
                        }


                    } else {
                        Log.d(TAG, "Error: " + e.getMessage());
                    }
                }
            });

            return null;
        }

    }



}
