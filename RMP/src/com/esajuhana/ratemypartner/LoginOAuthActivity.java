package com.esajuhana.ratemypartner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Activity which displays the webpage from the URL given as an intent
 * String-parameter. Finishes when WebView gets to a page, which has a
 * query parameter "oauth_verifier". Puts this verifier to an intent and sets
 * it as a result and finishes.
 * 
 * This is done in a WebView to keep consumer out of the authentication.
 * 
 * @author TODO
 */
public class LoginOAuthActivity extends Activity {

    private static final String TAG = "LoginOAuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_oauth);

        // Get login URI from intent
        Intent intent = getIntent();
        String uri = intent.getStringExtra("uri");
        
        final WebView webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            
            @Override
            public void onPageFinished(WebView view, String url) {
                
                if (url.contains("oauth_verifier=")) {
                    Log.v(TAG, "onPageFinished found URL: " + url);
                    
                    webView.setVisibility(View.GONE);

                    Uri uri = Uri.parse(url);
                    String verifier = uri.getQueryParameter("oauth_verifier");

                    if (verifier != null) {
                        Log.v(TAG, "onPageFinished found verifier: " + verifier);

                        Intent resultData = new Intent();
                        resultData.putExtra("oauth_verifier", verifier);
                        setResult(Activity.RESULT_OK, resultData);

                        finish();
                    }
                }

                super.onPageFinished(view, url);
            }
        });
        
        webView.loadUrl(uri);
    }
}
