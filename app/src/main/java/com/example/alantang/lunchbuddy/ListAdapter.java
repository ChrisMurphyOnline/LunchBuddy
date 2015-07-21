package com.example.alantang.lunchbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Alan Tang on 7/20/2015.
 */
public class ListAdapter extends ArrayAdapter<String> {

    customButtonListener customListner;

    public interface customButtonListener {
        public void onEditClickListener(int position, String value);
        public void onDeleteClickListener(int position, String value);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    private Context context;
    private ArrayList<String> data = new ArrayList<String>();

    public ListAdapter(Context context, ArrayList<String> dataItem) {
        super(context, R.layout.activity_profile, dataItem);
        this.data = dataItem;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.child_listview, null);
            viewHolder = new ViewHolder();

            viewHolder.text = (TextView) convertView.findViewById(R.id.childTextView);
            viewHolder.buttonEdit = (Button) convertView.findViewById(R.id.buttonEdit);
            viewHolder.buttonDelete = (Button) convertView.findViewById(R.id.buttonDelete);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String temp = getItem(position);
        viewHolder.text.setText(temp);
        viewHolder.buttonEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onEditClickListener(position, temp);
                }
            }
        });

        viewHolder.buttonDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onDeleteClickListener(position, temp);
                }

            }
        });

        return convertView;


    }

    public class ViewHolder {
        TextView text;
        Button buttonEdit;
        Button buttonDelete;
    }


}
