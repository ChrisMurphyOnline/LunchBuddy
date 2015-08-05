package com.example.alantang.lunchbuddy;

import android.app.Activity;
import android.content.Intent;
import android.hardware.camera2.params.Face;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.example.alantang.lunchbuddy.FriendsActivity;
import com.example.alantang.lunchbuddy.FacebookFriend;

import java.util.ArrayList;


/**
 * Created by Alan on 8/2/2015.
 */
public class FacebookListAdapter extends ArrayAdapter<FacebookFriend> {

    private static LayoutInflater inflater = null;
    private static final String TAG = "log_message";

    Context context;
    int resource;
    private ArrayList<FacebookFriend> friends = new ArrayList<FacebookFriend>();

    public FacebookListAdapter (Context context, int resource, ArrayList<FacebookFriend> data) {

        super(context, resource, data);
        this.friends = data;
        this.context = context;
        this.resource = resource;
    }

//    public int getCount() {
//        return friends.size();
//    }
//
//    public FacebookFriend getItem(FacebookFriend position) {
//        return position;
//    }
//
//    public long getItemId(int position) {
//        return position;
//    }

    public static class ViewHolder {
        public TextView displayName;
        public TextView displayDates;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);


                vi = inflater.inflate(R.layout.child_friendslistview, null);
                holder = new ViewHolder();

                holder.displayName = (TextView) vi.findViewById(R.id.displayName);
                holder.displayDates = (TextView) vi.findViewById(R.id.displayDates);


                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }
            Log.d(TAG, "FacebookListAdapter Name: " + friends.get(position).name);
            Log.d(TAG, "FacebookListAdapter number of dates: " + friends.get(position).numberOfDates);
            holder.displayName.setText(friends.get(position).name);
            holder.displayDates.setText(String.valueOf(friends.get(position).numberOfDates));



        return vi;
    }
}