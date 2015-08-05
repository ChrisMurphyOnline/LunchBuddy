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
 * Created by Alan on 8/5/2015.
 */
public class ConfirmedListAdapter extends ArrayAdapter<String> {
    customButtonListener customListner;

    public interface customButtonListener {
        public void onClearClickListener(int position, String value);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    private Context context;
    private ArrayList<String> data = new ArrayList<String>();

    public ConfirmedListAdapter(Context context, ArrayList<String> dataItem) {
        super(context, R.layout.activity_profile, dataItem);
        this.data = dataItem;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.child_rejectedlistview, null);
            viewHolder = new ViewHolder();

            viewHolder.text = (TextView) convertView.findViewById(R.id.displayRejectedDates);
            viewHolder.buttonClear = (Button) convertView.findViewById(R.id.buttonClear);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String temp = getItem(position);
        viewHolder.text.setText(temp);

        viewHolder.buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onClearClickListener(position, temp);
                }
            }
        });


        return convertView;
    }

    public class ViewHolder {
        TextView text;
        Button buttonClear;
    }
}
