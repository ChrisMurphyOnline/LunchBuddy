package com.example.alantang.lunchbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Alan on 8/7/2015.
 */
public class FriendsNowListAdapter extends ArrayAdapter<FacebookFriend> {
    customButtonListener customListner;

    public interface customButtonListener {
        public void onRequestClickListener(int position, String value);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    private static final String TAG = "log_message";
    Context context;
    int resource;
    private ArrayList<FacebookFriend> friends = new ArrayList<FacebookFriend>();

    public FriendsNowListAdapter(Context context, int resource, ArrayList<FacebookFriend> data) {
        super(context, resource, data);
        this.friends = data;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.child_friendsnowlistview, null);
            viewHolder = new ViewHolder();

            viewHolder.text = (TextView) convertView.findViewById(R.id.displayNameNow);
            viewHolder.buttonRequest = (Button) convertView.findViewById(R.id.buttonRequestNow);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.text.setText(friends.get(position).name);

        viewHolder.buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onRequestClickListener(position, friends.get(position).name);
                }
            }
        });


        return convertView;
    }

    public class ViewHolder {
        TextView text;
        Button buttonRequest;
    }
}
