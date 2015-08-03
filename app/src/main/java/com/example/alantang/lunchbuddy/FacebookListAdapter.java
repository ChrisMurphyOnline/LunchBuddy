package com.example.alantang.lunchbuddy;

import android.app.Activity;
import android.hardware.camera2.params.Face;
import android.content.Context;
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
    private Activity activity;
    private ArrayList<FacebookFriend> friends;
    private static LayoutInflater inflater = null;

    public FacebookListAdapter (Activity activity, int textViewResourceId,ArrayList<FacebookFriend> friends) {
        super(activity, textViewResourceId);
        try {
            this.activity = activity;
            this.friends = friends;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.activity_friends, null);
                holder = new ViewHolder();

                holder.displayName = (TextView) vi.findViewById(R.id.displayName);
                holder.displayDates = (TextView) vi.findViewById(R.id.displayDates);


                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }



            holder.displayName.setText(friends.get(position).name);
            holder.displayDates.setText(friends.get(position).numberOfDates);


        } catch (Exception e) {


        }
        return vi;
    }
}