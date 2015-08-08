package com.example.alantang.lunchbuddy;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import android.app.Activity;


public class MainActivity extends Activity {

    private static final String TAG = "log_message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.
        FacebookSdk.sdkInitialize(getApplicationContext());
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "cRs6iJZlt7MHBAqP8ch1SKLREZ6yADDtgl66Cf82", "NVjTb0Y3aMVWHrpEUCjxiIWQPEuj1aOIjBCmkD8Z");

        ParseFacebookUtils.initialize(getApplicationContext());

        final List<String> permissions = Arrays.asList("public_profile", "email");


//         get Development Key Hash
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("com.example.alantang.lunchbuddy",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.i("KeyHash: ", Base64.encodeToString(md.digest(), 0));
//            }
//        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
//            Log.e("Test", e.getMessage());
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("Test", e.getMessage());
//        }

        ParseUser.logOut();

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    saveFacebookId();
                } else {
                    saveFacebookId();
                }
            }
        });


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
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i);
            }
        });

        buttonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(i);
            }
        });

        buttonFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), FriendsActivity.class);
                startActivity(i);
            }
        });

        buttonPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PendingActivity.class);
                startActivity(i);
            }
        });

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
        });

        // Intent for User to be accessible from other activities
//        Intent i = new Intent(getApplicationContext(), MainActivity.class);
//        i.putExtra("new_variable_name","value");
//        startActivity(i);


        Log.i(TAG, "onCreate");
    }





    // Facebook calls this feature "Single sign-on" (SSO), and requires you to override onActivityResult() in your calling Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
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

    private void saveFacebookId() {
        GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject user, GraphResponse response) {
                response.getError();
                Log.e("JSON:", user.toString());
                try {
                    String userId = user.getString("id");
                    String facebookName = user.getString("name");
                    Log.d(TAG, userId);
                    ParseUser.getCurrentUser().put("FacebookId", userId);
                    ParseUser.getCurrentUser().put("FacebookName", facebookName);
                    ParseUser.getCurrentUser().saveInBackground();
                    Toast.makeText(getApplicationContext(), "Logged in as " + facebookName, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeAsync();
    }
}
