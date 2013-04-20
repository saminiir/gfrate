package com.esajuhana.ratemypartner;

import android.app.Activity;
import android.content.Intent;
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
                if (url.contains("&oauth_verifier=") && url.contains("?oauth_token=")) {
                    Log.v(TAG, "onPageFinished found URL: " + url);
                    
                    // TODO: Transform url-string to Uri-object and get query parameters
                    
                    int tokenIndexStart = url.indexOf("?oauth_token=") + "?oauth_token=".length();
                    int verifierIndexStart = url.indexOf("&oauth_verifier=");
                    
                    String oauth_token = url.substring(tokenIndexStart, verifierIndexStart);
                    String oauth_verifier = url.substring(verifierIndexStart + 16, url.length());
                    
                    Log.v(TAG, "onPageFinished found verifier: " + oauth_verifier);
                    Log.v(TAG, "onPageFinished found token: " + oauth_token);
                    
                    Intent resultData = new Intent();
                    resultData.putExtra("oauth_verifier", oauth_verifier);
                    resultData.putExtra("oauth_token", oauth_token);
                    setResult(Activity.RESULT_OK, resultData);
                    
                    finish();
                }
                super.onPageFinished(view, url);
            }
        });
        webView.loadUrl(uri);
    }
}
