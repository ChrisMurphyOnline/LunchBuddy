package com.example.alantang.lunchbuddy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Alan on 8/15/2015.
 */
public class DatesAvailCursorAdapter extends CursorAdapter {

    SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy hh:mm aaa");
    private static final String TAG = "log_message";
    Date date;

    public DatesAvailCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.child_listview, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(final View view, Context context, final Cursor cursor) {

        final ViewHolder viewHolder = new ViewHolder();

        // Find fields to populate in inflated template
        viewHolder.text = (TextView) view.findViewById(R.id.childTextView);
        // Extract properties from cursor
        String body = cursor.getString(cursor.getColumnIndexOrThrow(DatesAvailDatabase.COL_DATE));
        // Populate fields with extracted properties
        viewHolder.text.setText(body);



        viewHolder.buttonEdit = (Button) view.findViewById(R.id.buttonEdit);
        viewHolder.buttonDelete = (Button) view.findViewById(R.id.buttonDelete);

        viewHolder.buttonEdit.setTag(cursor.getPosition());
        viewHolder.buttonDelete.setTag(cursor.getPosition());

        viewHolder.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int offset = (Integer) viewHolder.buttonEdit.getTag();
                Log.d(TAG, "Click Cursor position: " + String.valueOf(offset));
                String value = "";
                if (cursor != null) {
                    cursor.moveToPosition(offset);
                    int columnIndex = cursor.getColumnIndexOrThrow("date");
                    Log.d(TAG, "Click Cursor Index: " + String.valueOf(columnIndex));
                    value = cursor.getString(columnIndex);
                    Log.d(TAG, "Click Cursor value: " + value);
                }
                Date date = convertStringToDate(value);
                setDate(date);
                AlertDialog editBox = editOption(view, date);
                editBox.show();
            }
        });

        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int offset = (Integer) viewHolder.buttonDelete.getTag();
                String value = "";
                if (cursor != null) {
                    cursor.moveToPosition(offset);
                    int columnIndex = cursor.getColumnIndexOrThrow("date");
                    Log.d(TAG, "Click Cursor Index: " + String.valueOf(columnIndex));
                    value = cursor.getString(columnIndex);
                    Log.d(TAG, "Click Cursor value: " + value);
                }

                AlertDialog deleteBox = deleteOption(view, value);
                deleteBox.show();
            }
        });

    }

    public class ViewHolder {
        TextView text;
        Button buttonEdit;
        Button buttonDelete;
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

    public void setDate(Date val) {
        date = val;
    }

    private AlertDialog editOption(final View view, final Date date)
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(view.getContext())
                //set message, title, and icon
                .setTitle("Edit")
                .setMessage("Would you like to edit the item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
//                        Date tempDate  = date;
                        Log.i(TAG, "Date in editDialogue: " + date.toString());
                        editObject(date, view);
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

    // opens up Calendar dialogue to edit date
    private void editObject(Date date, final View view) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("DatesAvailable");
        query.whereEqualTo("Date", date);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Retrieved " + objects.size() + " objects");
                    if (objects.size() > 0) {
                        String objectId = objects.get(0).getObjectId();
                        String date = objects.get(0).getDate("Date").toString();
                        Log.d(TAG, "Date: " + objectId);
                        Log.d(TAG, "Object ID: " + objectId);
                        Intent intent = new Intent(view.getContext(), CalendarActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("DATE_FROM_PROFILE", date);
                        extras.putString("OBJECTID_FROM_PROFILE", objectId);
                        intent.putExtras(extras);
                        view.getContext().startActivity(intent);
                    } else {
                        Log.d(TAG, "No object found!");
                    }
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });
    }

    private AlertDialog deleteOption(final View view, final String value)
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(view.getContext())
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String val = value;
                        Date date = convertStringToDate(value);
                        deleteObject(date, view);
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

    public void deleteObject (Date date, final View view) {
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
                        Toast.makeText(view.getContext(), "Item deleted!", Toast.LENGTH_SHORT).show();


                    } else {
                        Log.d(TAG, "No object found!");
                        Toast.makeText(view.getContext(), "Oops! Item does not exist. Please refresh the page.", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(view.getContext(), "Oops! Item does not exist. Please refresh the page.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });
    }


}
