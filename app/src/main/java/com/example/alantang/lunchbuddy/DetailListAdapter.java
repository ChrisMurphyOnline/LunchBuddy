package com.example.alantang.lunchbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.util.ArrayList;

/**
 * Created by Alan on 8/3/2015.
 */
public class DetailListAdapter extends ArrayAdapter<String> {

    customButtonListener customListner;

    public interface customButtonListener {
        public void onRequestClickListener(int position, String value);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    private Context context;
    private ArrayList<String> data = new ArrayList<String>();

    public DetailListAdapter(Context context, ArrayList<String> dataItem) {
        super(context, R.layout.fragment_friends_detail, dataItem);
        this.data = dataItem;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.child_detaillistview, null);
            viewHolder = new ViewHolder();

            viewHolder.text = (TextView) convertView.findViewById(R.id.displayDetailDates);
            viewHolder.buttonRequest = (Button) convertView.findViewById(R.id.buttonRequest);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String temp = getItem(position);
        viewHolder.text.setText(temp);

        viewHolder.buttonRequest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onRequestClickListener(position, temp);
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

