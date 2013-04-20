package com.esajuhana.ratemypartner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 *
 * @author TODO
 */
public class LoginOAuthActivity extends Activity {

    private static final String TAG = "LoginOAuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_oauth);

        Intent intent = getIntent();
        String uri = intent.getStringExtra("uri");

        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("oauth_verifier=") && url.contains("oauth_token=")) {
                    Log.v(TAG, "onPageFinished found URL: " + url);

                    Uri uri = Uri.parse(url);
                    String token = uri.getQueryParameter("oauth_token");
                    String verifier = uri.getQueryParameter("oauth_verifier");

                    if (token != null && verifier != null) {
                        Log.v(TAG, "onPageFinished found verifier: " + verifier);
                        Log.v(TAG, "onPageFinished found token: " + token);

                        Intent resultData = new Intent();
                        resultData.putExtra("oauth_verifier", verifier);
                        resultData.putExtra("oauth_token", token);
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
