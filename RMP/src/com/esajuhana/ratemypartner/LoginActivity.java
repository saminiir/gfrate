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
import android.widget.Toast;
import com.esajuhana.ratemypartner.oauth.OAuth;

/**
 * Activity which displays a login screen to the user. TODO: messages,
 * exceptions, errors etc.
 */
public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    
    private static final int VERIFIER_REQUEST_ID = 1;
    
    private OAuth mOAuth;
    private String mOAuthUriBase;
    private String mOAuthUriRequestToken;
    private String mOAuthUriAccessToken;
    private String mOauthUriAuthorize;
    
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String consumerKey = getResources().getString(R.string.oauth_consumer_key);
        String consumerSecret = getResources().getString(R.string.oauth_consumer_secret);

        mOAuth = new OAuth(consumerKey, consumerSecret);
        mOAuthUriBase = getResources().getString(R.string.oauth_uri_base);
        mOAuthUriRequestToken = getResources().getString(R.string.oauth_uri_request_token);
        mOAuthUriAccessToken = getResources().getString(R.string.oauth_uri_access_token);
        mOauthUriAuthorize = getResources().getString(R.string.oauth_uri_authorize);

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
                        if (mOAuth.getState() == OAuth.OAuthState.GotAccessToken) {
                            OAuthCallbackAuthorized();
                        } else {
                            showProgress(true);
                            new OAuthRequestTokenTask().execute();
                        }
                    }
                });
    }

    /**
     * Starts HallOfFameActivity
     * @param view
     */
    public void hallOfFame(View view) {
	    Intent intent = new Intent(this, HallOfFameActivity.class);
	    startActivity(intent);
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

    private class OAuthRequestTokenTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String requestToken;

            try {
                requestToken = mOAuth.getRequestToken(mOAuthUriBase + mOAuthUriRequestToken, null);
            } catch (IllegalStateException ex) {
                Log.v(TAG, "Wrong state: " + ex.getMessage());
                mOAuth.resetState();
                return null;
            }

            return requestToken;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                showToast("Wrong state in OAuth authentication");
                showProgress(false);
            } else {
            OAuthCallbackLogin(result);
            }
        }

        @Override
        protected void onCancelled() {
            mOAuth.resetState();
            showProgress(false);
        }
    }

    private void OAuthCallbackLogin(String result) {
        Log.v(TAG, "OAuthCallbackLogin called!");
        
        showProgress(false);
        
        if (!TextUtils.isEmpty(result)) {
            String uriAuthorize = "";

            try {
                uriAuthorize = mOAuth.getAuthorizeRequestUri(mOAuthUriBase + mOauthUriAuthorize);
            } catch (IllegalStateException ex) {
                Log.v(TAG, "Wrong state: " + ex.getMessage());
                mOAuth.resetState();
                
                showToast("Wrong state in OAuth authentication");
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

            String accessToken;

            try {
                accessToken = mOAuth.getAccessToken(mOAuthUriBase + mOAuthUriAccessToken, params[0]);
            } catch (IllegalStateException ex) {
                Log.v(TAG, "Wrong state: " + ex.getMessage());
                mOAuth.resetState();
                return null;
            }

            return accessToken;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null)
            {
                showToast("Wrong state in OAuth authentication");
                showProgress(false);
            } else {
            Log.v(TAG, "Got access token: " + result);
            OAuthCallbackAuthorized();
            }
        }

        @Override
        protected void onCancelled() {
            mOAuth.resetState();
            showProgress(false);
        }
    }

    protected void OAuthCallbackAuthorized() {
        showProgress(false);
                    
        if (mOAuth.getState() == OAuth.OAuthState.GotAccessToken) {
            Intent oAuthIntent = new Intent(this, MainActivity.class);
            oAuthIntent.putExtra("oauth_object", mOAuth);
            startActivity(oAuthIntent);
        } else {
            showToast("Wrong state in OAuth authentication");
            mOAuth.resetState();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode == VERIFIER_REQUEST_ID) {
            if (resultCode == RESULT_OK) {
                String verifier = data.getStringExtra("oauth_verifier");
                Log.v(TAG, "Verifier got here: " + verifier);
                showProgress(true);
                new OAuthAccessTokenTask().execute(verifier);
            }
        }
    }
    
    protected void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }
}
