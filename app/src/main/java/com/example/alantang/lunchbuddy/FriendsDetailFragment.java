package com.example.alantang.lunchbuddy;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.example.alantang.lunchbuddy.DetailListAdapter.customButtonListener;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;


public class FriendsDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Void>, customButtonListener {

    private static final String TAG = "log_message";
    ArrayList<Date> mListDates = new ArrayList<Date>();
    ArrayList<String> mListDatesString = new ArrayList<String>();
    ListView mListViewDates;
    DetailListAdapter mDatesAdaptor;
    FacebookFriend receivedFriend;
    SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy hh:mm aaa");
    RelativeLayout view;

    private FragmentActivity faActivity;

    private static final int friendsDetailLoader = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        faActivity  = (FragmentActivity) super.getActivity();


        view = (RelativeLayout) inflater.inflate(R.layout.fragment_friends_detail, container, false);

        mListViewDates = (ListView) view.findViewById(R.id.listview_friends_detail);
        mDatesAdaptor = new DetailListAdapter(super.getActivity(), mListDatesString);
        mDatesAdaptor.setCustomButtonListner(FriendsDetailFragment.this);
        mListViewDates.setAdapter(mDatesAdaptor);

        if (isNetworkConnected()) {
            getLoaderManager().initLoader(friendsDetailLoader, null, this);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "No internet connection.", Toast.LENGTH_LONG).show();
        }

        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
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
                TextView title = (TextView) view.findViewById(R.id.displayFriendDetail);
//                Intent intent = getActivity().getIntent();
//                receivedFriend = (FacebookFriend) intent.getSerializableExtra("datesDetail");

                Bundle arguments = this.getArguments();
                if (arguments != null) {
                    Log.d(TAG, "arguments not null!");
                    receivedFriend = (FacebookFriend) getArguments().getSerializable("datesDetail");
                    title.setText(receivedFriend.name + "'s Available Dates: ");
                    mListDates = receivedFriend.dates;
                } else {
                    Log.d(TAG, "arguments null :(");
                }

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
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getActivity())
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

                        Toast.makeText(getActivity().getApplicationContext(), "Sending request, please wait...", Toast.LENGTH_LONG).show();
                        appointment.saveInBackground();
                        //todo: check if request actually sent
                        Toast.makeText(getActivity().getApplicationContext(), "Request sent!", Toast.LENGTH_LONG).show();
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

    public boolean isNetworkConnected() {
        final ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED;
    }
}
