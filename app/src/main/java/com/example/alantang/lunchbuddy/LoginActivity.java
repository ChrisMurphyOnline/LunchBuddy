package com.example.alantang.lunchbuddy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.List;


public class LoginActivity extends Activity {

    private static final String TAG = "log_message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //         get Development Key Hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.alantang.lunchbuddy",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash: ", Base64.encodeToString(md.digest(), 0));
            }
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            Log.e("Test", e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.e("Test", e.getMessage());
        }

        Button buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    // Enable Local Datastore.
                    final List<String> permissions = Arrays.asList("public_profile", "email");
                    ParseUser.logOut();
                    ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException err) {
                            if (user == null) {
                                Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
                                Log.d(TAG, err.toString());
                            } else if (user.isNew()) {
                                saveFacebookId();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                saveFacebookId();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "No internet access", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public boolean isNetworkConnected() {
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED;
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

    // Facebook calls this feature "Single sign-on" (SSO), and requires you to override onActivityResult() in your calling Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
