package com.example.alantang.lunchbuddy;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by Alan on 8/17/2015.
 */
public class Application extends android.app.Application {

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
