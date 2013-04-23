package com.esajuhana.ratemypartner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.esajuhana.ratemypartner.oauth.OAuth;

/**
 * Activity which displays a login screen to the user. TODO: messages,
 * exceptions, errors, login animation etc.
 */
public class LoginActivity extends Activity {

    // TODO: url constants from settings on create?
    private static final String TAG = "LoginActivity";
    private static final int VERIFIER_REQUEST_ID = 1;
    private OAuth OAUTH;
    private String OAUTH_BASE_URI;
    private String OAUTH_REQUEST_TOKEN_URI;
    private String OAUTH_ACCESS_TOKEN_URI;
    private String OAUTH_AUTHORIZE_URI;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String consumerKey = getResources().getString(R.string.oauth_consumer_key);
        String consumerSecret = getResources().getString(R.string.oauth_consumer_secret);

        OAUTH = new OAuth(consumerKey, consumerSecret);
        OAUTH_BASE_URI = getResources().getString(R.string.oauth_uri_base);
        OAUTH_REQUEST_TOKEN_URI = getResources().getString(R.string.oauth_uri_request_token);
        OAUTH_ACCESS_TOKEN_URI = getResources().getString(R.string.oauth_uri_access_token);
        OAUTH_AUTHORIZE_URI = getResources().getString(R.string.oauth_uri_authorize);

        setContentView(R.layout.activity_login);
        setupActionBar();
        
        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
        mLoginStatusMessageView.setText(R.string.login_progress_signing_in);

        findViewById(R.id.login_oauth_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showProgress(true);
                        new OAuthRequestTokenTask().execute();
                    }
                });
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                // TODO: If Settings has multiple levels, Up should navigate up
                // that hierarchy.
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginStatusView.setVisibility(show ? View.VISIBLE
                            : View.GONE);
                }
            });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE
                            : View.VISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    private class OAuthRequestTokenTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String requestToken = "";

            try {
                requestToken = OAUTH.getRequestToken(OAUTH_BASE_URI + OAUTH_REQUEST_TOKEN_URI, null);
            } catch (IllegalStateException ex) {
                Log.v(TAG, "Wrong state: " + ex.getMessage());
                OAUTH.resetState();
            }

            return requestToken;
        }

        @Override
        protected void onPostExecute(String result) {
            OAuthCallbackLogin(result);
        }

        @Override
        protected void onCancelled() {
            OAUTH.resetState();
            showProgress(false);
        }
    }

    private void OAuthCallbackLogin(String result) {
        Log.v(TAG, "OAuthCallbackLogin called!");

        if (!TextUtils.isEmpty(result)) {
            String uriAuthorize = "";

            try {
                uriAuthorize = OAUTH.getAuthorizeRequestUri(OAUTH_BASE_URI + OAUTH_AUTHORIZE_URI);
            } catch (IllegalStateException ex) {
                Log.v(TAG, "Wrong state: " + ex.getMessage());
                OAUTH.resetState();
                // TODO: Show message
                return;
            }

            Log.v(TAG, "uri: " + uriAuthorize);

            Intent intent = new Intent(this, LoginOAuthActivity.class);
            intent.putExtra("uri", uriAuthorize);

            startActivityForResult(intent, VERIFIER_REQUEST_ID);
        }
    }

    private class OAuthAccessTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (params.length < 1) {
                return "";
            }

            String accessToken = "";

            try {
                accessToken = OAUTH.getAccessToken(OAUTH_BASE_URI + OAUTH_ACCESS_TOKEN_URI, params[0]);
            } catch (IllegalStateException ex) {
                Log.v(TAG, "Wrong state: " + ex.getMessage());
                OAUTH.resetState();
                // TODO: Show message
            }

            return accessToken;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v(TAG, "Got access token: " + result);
            OAuthCallbackAuthorized();
        }

        @Override
        protected void onCancelled() {
            OAUTH.resetState();
            showProgress(false);
        }
    }

    protected void OAuthCallbackAuthorized() {
        if (OAUTH.getState() == OAuth.OAuthState.GotAccessToken) {
            showProgress(false);
            Intent oAuthIntent = new Intent(this, MainActivity.class);
            oAuthIntent.putExtra("oauth_object", OAUTH);
            startActivity(oAuthIntent);
        } else {
            // TODO: Show message that something went wrong
            OAUTH.resetState();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode == VERIFIER_REQUEST_ID) {
            if (resultCode == RESULT_OK) {
                String verifier = data.getStringExtra("oauth_verifier");
                Log.v(TAG, "Verifier got here: " + verifier);
                new OAuthAccessTokenTask().execute(verifier);
            }
        }
    }
}
