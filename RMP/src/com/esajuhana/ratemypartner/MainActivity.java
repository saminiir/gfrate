package com.esajuhana.ratemypartner;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import com.esajuhana.ratemypartner.oauth.OAuth;

public class MainActivity extends Activity {

    private final String TAG = "MainActivity";
    private OAuth OAUTH = null;
    // TODO: base url and endpoint url constants
    // TODO: from settings on activity creation?
    
    public static boolean loggedIn = false;
    private int currentAdjustment = 0;

    public void animateButton(int buttonResourceName) {
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(100); // 100ms
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.ABSOLUTE); // repeat once
        animation.setRepeatMode(Animation.REVERSE); // symmetric animation
        final Button btn = (Button) findViewById(buttonResourceName);
        btn.startAnimation(animation);
    }

    /**
     * TODO Shortcut to reset value back to zero ? TODO	How to use single method
     * i.e. call same method in increase and decrease and get the reference to
     * clicked button from the View object? update send_adjustment button's
     * value according to taps
     *
     * @param change amount to change
     */
    public void updateCurrentAdjustment(int change) {
        currentAdjustment += change;
        animateButton(R.id.send_adjustments);
        Button sendAdjustment = (Button) findViewById(R.id.send_adjustments);
        if (currentAdjustment == 0) {
            sendAdjustment.setText(R.id.send_adjustments);
        } else {
            sendAdjustment.setText("Tap here to compensate rating by (" + currentAdjustment + ")");
        }
    }

    public void increaseValue(View view) {
        updateCurrentAdjustment(1);
    }

    public void decreaseValue(View view) {
        updateCurrentAdjustment(-1);
    }

    public void sendAdjustments(View view) {
        new TestAccessTask().execute(currentAdjustment);    
    }
    
    private class TestAccessTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {
            if (params.length == 0) {
                return "Params are missing!";
            }
            
            String result = "";

            try {
                // params etc.
                result = OAUTH.accessProtectedResource("http://secure-falls-3392.herokuapp.com/api/test", null, OAuth.HttpRequestType.GET);
            } catch (IllegalStateException ex) {
                Log.v(TAG, "Wrong state: " + ex.getMessage());
                OAUTH.resetState();
                // TODO: Show message
                // TODO: callback with finish() (back to login)
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
        Log.v(TAG, "Got response: " + result);
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        OAUTH = (OAuth) intent.getSerializableExtra("oauth_object");

        if (OAUTH == null) {
            finish();
        } else {
            Log.v(TAG, "Got OAuth object with state: " + OAUTH.getState().toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
