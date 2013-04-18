package com.esajuhana.ratemypartner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(uri);
    }
    
    /*
    private class LoginOAuthWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (Uri.parse(url).getHost().equals("10.0.2.2)) {
            // Let load the page
            return false;
        }
        // Otherwise
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
        return true;
    }
    // 
    // AND
    @Override
public void onLoadResource(WebView view, String url)
{
    if (url.equals("http://redirect"))
    {
        //do SOMETHING
    }
    else
    {
        super.onLoadResource(view, url);
    }           
}
}
* */
}
