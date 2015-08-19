package com.example.alantang.lunchbuddy;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.content.pm.Signature;
import java.util.Arrays;
import java.util.List;
import android.app.Activity;


public class MainActivity extends FragmentActivity {

    private static final String TAG = "log_message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isLoggedIn()) {
            Log.d(TAG, "not logged in");
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        }

//        sets content to be activity_main.xml
        setContentView(R.layout.activity_main);

        Button buttonProfile = (Button) findViewById(R.id.buttonProfile);
        Button buttonCalendar = (Button) findViewById(R.id.buttonCalendar);
        Button buttonFriends = (Button) findViewById(R.id.buttonFriends);
        Button buttonPending = (Button) findViewById(R.id.buttonPending);
        ToggleButton toggle = (ToggleButton) findViewById(R.id.buttonAvailable);

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoggedIn()) {
                    Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Not logged into Facebook", Toast.LENGTH_LONG).show();
                }

            }
        });

        buttonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoggedIn()) {
                    Intent i = new Intent(getApplicationContext(), CalendarActivity.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Not logged into Facebook", Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoggedIn()) {
                    Intent i = new Intent(getApplicationContext(), FriendsDisplayActivity.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Not logged into Facebook", Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoggedIn()) {
                    Intent i = new Intent(getApplicationContext(), PendingActivity.class);
                    startActivity(i);
                }

                else {
                    Toast.makeText(getApplicationContext(), "Not logged into Facebook", Toast.LENGTH_LONG).show();
                }
            }
        });

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isLoggedIn()) {
                    if (isChecked) {
                        ParseUser.getCurrentUser().put("Available", true);
                        Toast.makeText(getApplicationContext(), "Status set to available!", Toast.LENGTH_LONG).show();
                        ParseUser.getCurrentUser().saveInBackground();

                    } else {
                        ParseUser.getCurrentUser().put("Available", false);
                        Toast.makeText(getApplicationContext(), "Status set to unavailable.", Toast.LENGTH_LONG).show();
                        ParseUser.getCurrentUser().saveInBackground();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Not logged into Facebook", Toast.LENGTH_LONG).show();
                }
            }
        });
        Log.i(TAG, "onCreate");
    }




    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

//    private void saveFacebookId() {
//        GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
//            @Override
//            public void onCompleted(JSONObject user, GraphResponse response) {
//                response.getError();
//                Log.e("JSON:", user.toString());
//                try {
//                    String userId = user.getString("id");
//                    String facebookName = user.getString("name");
//                    Log.d(TAG, userId);
//                    ParseUser.getCurrentUser().put("FacebookId", userId);
//                    ParseUser.getCurrentUser().put("FacebookName", facebookName);
//                    ParseUser.getCurrentUser().saveInBackground();
//                    Toast.makeText(getApplicationContext(), "Logged in as " + facebookName, Toast.LENGTH_LONG).show();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).executeAsync();
//    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
}
