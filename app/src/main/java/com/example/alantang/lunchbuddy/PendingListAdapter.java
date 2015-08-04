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
 * Created by Alan on 8/4/2015.
 */
public class PendingListAdapter extends ArrayAdapter<String> {

    customButtonListener customListner;

    public interface customButtonListener {
        public void onAcceptClickListener(int position, String value);
        public void onRejectClickListener(int position, String value);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    private Context context;
    private ArrayList<String> data = new ArrayList<String>();

    public PendingListAdapter(Context context, ArrayList<String> dataItem) {
        super(context, R.layout.activity_pending, dataItem);
        this.data = dataItem;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.child_pendinglistview, null);
            viewHolder = new ViewHolder();

            viewHolder.text = (TextView) convertView.findViewById(R.id.child_pending_accept);
            viewHolder.buttonAccept = (Button) convertView.findViewById(R.id.buttonAccept);
            viewHolder.buttonReject = (Button) convertView.findViewById(R.id.buttonReject);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String temp = getItem(position);
        viewHolder.text.setText(temp);
        viewHolder.buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onAcceptClickListener(position, temp);
                }
            }
        });

        viewHolder.buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onRejectClickListener(position, temp);
                }

            }
        });

        return convertView;


    }

    public class ViewHolder {
        TextView text;
        Button buttonAccept;
        Button buttonReject;
    }



}
