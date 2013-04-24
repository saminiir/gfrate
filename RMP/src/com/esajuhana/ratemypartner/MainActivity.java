package com.esajuhana.ratemypartner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;
import com.esajuhana.ratemypartner.helpers.JSONParser;
import com.esajuhana.ratemypartner.oauth.OAuth;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

    private final String TAG = "MainActivity";
    protected final Context context = this;
    private OAuth mOAuth = null;
    private String mOAuthUriBase;
    private String mOAuthUriPostNewPoints;
    private String mOAuthUriGetPoints;
    
    public static boolean loggedIn = false;
    private int currentAdjustment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOAuthUriBase = getResources().getString(R.string.oauth_uri_base);
        mOAuthUriPostNewPoints = getResources().getString(R.string.oauth_uri_post_points);
        mOAuthUriGetPoints = getResources().getString(R.string.oauth_uri_get_points);

        Intent intent = getIntent();
        mOAuth = (OAuth) intent.getSerializableExtra("oauth_object");

        if (mOAuth == null) {
            updateDialog("Authentication error", "Something went wrong with OAuth.");
            finish();
        } else {
            Log.v(TAG, "Got OAuth object with state: " + mOAuth.getState().toString());
        }

        // Get current points for the view
        new TestAccessTask2().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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
            sendAdjustment.setText(getResources().getString(R.string.send_adjustments));
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
        if (currentAdjustment != 0) {
            new TestAccessTask().execute(currentAdjustment);
        }
    }

    /*
     * AsyncTask for testing authenticated POST. Request as JSON and
     * expects JSON return.
     */
    private class TestAccessTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {
            if (params.length == 0) {
                return "Params are missing!";
            }

            String result = "";

            try {
                // params etc.
                JSONObject jsonBody = new JSONObject();

                try {
                    jsonBody.put("to_add", params[0]);
                } catch (JSONException ex) {
                    Log.v(TAG, "Error in JSONObject.put: " + ex.getMessage());
                }

                result = mOAuth.accessProtectedResource(
                        mOAuthUriBase + mOAuthUriPostNewPoints, null,
                        jsonBody.toString(), OAuth.HttpRequestType.POST);

            } catch (IllegalStateException ex) {
                Log.v(TAG, "Wrong state: " + ex.getMessage());
                mOAuth.resetState();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v(TAG, "Got response: " + result);

            final JSONObject jsonResult = JSONParser.parse(result);

            if (jsonResult == null) {
                Log.e(TAG, "Error in JSONParser. Check error in that class.");
                return;
            }

            int points;

            try {
                boolean isOK = jsonResult.getBoolean("isOK");
                int added = jsonResult.getInt("added");
                points = jsonResult.getInt("points");
                Log.v(TAG, "JSON parsing result: " + String.valueOf(isOK) + ", " + added + ", " + points);
            } catch (final JSONException ex) {
                Log.e(TAG, "Error in JSON parsing: " + ex.getMessage());

                updateDialog("Error", ex.getMessage());

                return;
            }

            //updateDialog("JSON-result", jsonResult.toString());
            
            updateTextView(R.id.partner_score, points);
        }
    }

    /*
     * AsyncTask for testing authenticated GET. Expects JSON result.
     */
    private class TestAccessTask2 extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String result;

            try {

                result = mOAuth.accessProtectedResource(
                        mOAuthUriBase + mOAuthUriGetPoints, null,
                        null, OAuth.HttpRequestType.GET);

            } catch (IllegalStateException ex) {
                Log.v(TAG, "Wrong state: " + ex.getMessage());
                mOAuth.resetState();
                return null;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                showToast("Wrong state in OAuth authentication");
                finish();
            } else {

                Log.v(TAG, "Got response: " + result);

                final JSONObject jsonResult = JSONParser.parse(result);

                if (jsonResult == null) {
                    Log.e(TAG, "Error in JSONParser. Check error with tag \"JSONParser\".");
                    return;
                }

                int points;

                try {
                    points = jsonResult.getInt("points");
                    Log.v(TAG, "JSON parsing result: " + points);
                } catch (final JSONException ex) {
                    Log.e(TAG, "Error in JSON parsing: " + ex.getMessage());

                    updateDialog("Error", ex.getMessage());

                    return;
                }

                //updateDialog("JSON-result", jsonResult.toString());
                updateTextView(R.id.partner_score, points);
                showToast("Logged in");
            }
        }
    }

    private void updateTextView(int id, int points) {
        TextView scoreTextView = (TextView) findViewById(id);
        scoreTextView.setText(String.valueOf(points));
    }

    private void updateDialog(String title, String content) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(content);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    protected void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }
}
