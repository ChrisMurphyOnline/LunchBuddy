package com.example.alantang.lunchbuddy;

import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Alan on 8/17/2015.
 */
public class Application extends android.app.Application {

    private static final String TAG = "log_message";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "cRs6iJZlt7MHBAqP8ch1SKLREZ6yADDtgl66Cf82", "NVjTb0Y3aMVWHrpEUCjxiIWQPEuj1aOIjBCmkD8Z");
        ParseFacebookUtils.initialize(this);


        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.
        FacebookSdk.sdkInitialize(getApplicationContext());

    }

}
