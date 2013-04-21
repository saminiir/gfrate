package com.esajuhana.ratemypartner.oauth;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

/**
 * OAuth request handler
 *
 * @author TODO
 */
public class OAuth {

    private final static String TAG = "OAuth";
    private final int TIMEOUT = 10000;
    private String mConsumerKey;
    private String mConsumerSecret;
    private String mOauthVersion = "1.0";
    private String mOauthSignatureMethod = "HMAC-SHA1";
    private String mToken = "";
    private String mTokenSecret = "";
    private OAuthState mState = OAuthState.Init;
    private static Random sRandom = new Random();

    /**
     * States of this object
     */
    public static enum OAuthState {

        Init, GotRequestToken, GotAccessToken
    }
    
    private static enum HttpRequestType {

        POST, GET
    }
    
    /**
     * Initializes new OAuth-object.
     *
     * @param consumerKey
     * @param consumerSecret
     */
    public OAuth(String consumerKey, String consumerSecret) {
        mConsumerKey = consumerKey;
        mConsumerSecret = consumerSecret;
    }
    
    
    /**
     * Gets the state of OAuth-authentication
     * @return
     */
    public OAuthState getState() {
            return mState;
    }

    /**
     * Gets OAuth 1.0 request token from the URL given as a parameter. Uses
     * HTTP-POST.
     *
     * @param requestTokenUrl
     * @return result body that the server returned
     */
    public String getRequestToken(String requestTokenUrl) {
        
        // Return token, secret and state to initial values
        mToken = "";
        mTokenSecret = "";
        mState = OAuthState.Init;
        
        String timestamp = String.valueOf(getTimestamp());
        String nonce = String.valueOf(getNonce());

        TreeMap<String, String> headerTreeMap = new TreeMap<String, String>();

        headerTreeMap.put("oauth_callback", "oob");
        headerTreeMap.put("oauth_consumer_key", mConsumerKey);
        headerTreeMap.put("oauth_signature_method", mOauthSignatureMethod);
        headerTreeMap.put("oauth_timestamp", timestamp);
        headerTreeMap.put("oauth_version", mOauthVersion);
        headerTreeMap.put("oauth_nonce", nonce);

        String signature = getSignature(headerTreeMap, requestTokenUrl, HttpRequestType.POST);
        headerTreeMap.put("oauth_signature", signature);

        String result = getHttpResult(requestTokenUrl, headerTreeMap, HttpRequestType.POST);

        if (!TextUtils.isEmpty(result)) {
            Uri uri = Uri.parse("http://placeholder.org/?" + result);
            String secret = uri.getQueryParameter("oauth_token_secret");
            String token = uri.getQueryParameter("oauth_token");

            if (secret != null && token != null) {
                mTokenSecret = secret;
                mToken = token;
                
                mState = OAuthState.GotRequestToken;
                
                Log.v(TAG, "Got tokenSecret = " + mTokenSecret);
                Log.v(TAG, "Got token = " + mToken);
            }
        }

        return result;
    }

    /**
     * Gets OAuth 1.0 access token from the URL given as a parameter. Uses
     * HTTP-POST.
     * Throws IllegalStateException if user hasn't got a request token.
     *
     * @param accessTokenUrl
     * @param verifier
     * @param token
     * @return http body
     */
    public String getAccessToken(String accessTokenUrl, String verifier) {
        
        if (mState != OAuthState.GotRequestToken){
              throw new IllegalStateException("Request token hasn't been retrieved.");
        } 

        String timestamp = String.valueOf(getTimestamp());
        String nonce = String.valueOf(getNonce());

        TreeMap<String, String> headerTreeMap = new TreeMap<String, String>();

        headerTreeMap.put("oauth_consumer_key", mConsumerKey);
        headerTreeMap.put("oauth_nonce", nonce);
        headerTreeMap.put("oauth_signature_method", mOauthSignatureMethod);
        headerTreeMap.put("oauth_timestamp", timestamp);
        headerTreeMap.put("oauth_token", mToken);
        headerTreeMap.put("oauth_verifier", verifier);
        headerTreeMap.put("oauth_version", mOauthVersion);

        String signature = getSignature(headerTreeMap, accessTokenUrl, HttpRequestType.POST);
        headerTreeMap.put("oauth_signature", signature);
        
        String result = getHttpResult(accessTokenUrl, headerTreeMap, HttpRequestType.POST);
        
        if (!TextUtils.isEmpty(result)) {
            Uri uri = Uri.parse("http://placeholder.org/?" + result);
            String secret = uri.getQueryParameter("oauth_token_secret");
            String token = uri.getQueryParameter("oauth_token");

            if (secret != null && token != null) {
                mTokenSecret = secret;
                mToken = token;
                
                mState = OAuthState.GotAccessToken;
                
                Log.v(TAG, "Got accesstokenSecret  = " + mTokenSecret);
                Log.v(TAG, "Got accesstoken = " + mToken);
            }
        }
        
        return result;
    }

    /**
     * Gets protected resource from the URL given as a parameter.
     * GET-query parameters can be specified with params-parameter.
     * Uses HTTP GET. Throws IllegalStateException if user has not authenticated.
     * 
     * @param resourceUrl URL to fetch protected resource
     * @param params HTTP GET query parameters
     * @return HTTP response as a string
     */
    public String getProtectedResource(String resourceUrl, Map<String, String> params) {
        
        if (mState != OAuthState.GotAccessToken){
            throw new IllegalStateException("Access token hasn't been retrieved.");
        }
        
        String timestamp = String.valueOf(getTimestamp());
        String nonce = String.valueOf(getNonce());

        TreeMap<String, String> paramsTreeMap = new TreeMap<String, String>();

        paramsTreeMap.put("oauth_consumer_key", mConsumerKey);
        paramsTreeMap.put("oauth_nonce", nonce);
        paramsTreeMap.put("oauth_signature_method", mOauthSignatureMethod);
        paramsTreeMap.put("oauth_timestamp", timestamp);
        paramsTreeMap.put("oauth_token", mToken);
        paramsTreeMap.put("oauth_version", mOauthVersion);
        
        if (params != null) {
            paramsTreeMap.putAll(params);
        }

        String signature = getSignature(paramsTreeMap, resourceUrl, HttpRequestType.GET);
        paramsTreeMap.put("oauth_signature", signature);
        
        String result = getHttpResult(resourceUrl, paramsTreeMap, HttpRequestType.GET);
        
        return result;
    }   
    
    private static long getNonce() {
        return getTimestamp() + sRandom.nextInt();
    }

    private static long getTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    private String getHttpResult(String url, TreeMap<String, String> params, HttpRequestType requestType) throws ParseException {
        String result = "";

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), TIMEOUT);

            if (requestType == HttpRequestType.POST) {
                HttpPost httpPost = new HttpPost(url);
                String headerContent = getPostHeader(params);

                Log.v(TAG, "Authorization header: " + headerContent);

                httpPost.setHeader("Authorization", headerContent);
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
            } else {
                String queryParams = getGetQueryParameters(params);

                Log.v(TAG, "Query params: " + queryParams);

                HttpGet httpGet = new HttpGet(url + queryParams);
                HttpResponse response = httpClient.execute(httpGet);
                result = EntityUtils.toString(response.getEntity());
            }
        } catch (ClientProtocolException e) {
            Log.v(TAG, e.getMessage());
        } catch (IOException e) {
            Log.v(TAG, e.getMessage());
        } catch (IllegalStateException e) {
            Log.v(TAG, e.getMessage());
        }
        
        if (!TextUtils.isEmpty(result))
        {
            Log.v(TAG, "HTTP-result body was: " + result);
        } else {
            Log.v(TAG, "HTTP-result body was empty");
        }

        return result;
    }

    private String getSignature(TreeMap<String, String> headerTreeMap, String url, HttpRequestType requestType) {

        boolean firstKey = true;
        StringBuilder normalizedParameters = new StringBuilder();

        for (String key : headerTreeMap.keySet()) {
            if (!firstKey) {
                normalizedParameters.append("&");
            } else {
                firstKey = false;
            }

            normalizedParameters.append(key);
            normalizedParameters.append("=");
            normalizedParameters.append(headerTreeMap.get(key));
        }

        StringBuilder ba = new StringBuilder(requestType.toString());
        ba.append("&");
        ba.append(getUrlEncoded(url));
        ba.append("&");
        ba.append(getUrlEncoded(normalizedParameters.toString()));

        String basestring = ba.toString();
        Log.v(TAG, "Basestring: " + basestring);
        String hashkey = mConsumerSecret + "&" + mTokenSecret;
        String signature = getHmacShaSignature(basestring, hashkey);

        return signature;
    }

    private String getPostHeader(TreeMap<String, String> headerTreeMap) {

        boolean firstKey = true;
        StringBuilder header = new StringBuilder();

        header.append("OAuth ");

        for (String key : headerTreeMap.keySet()) {
            if (!firstKey) {
                header.append(", ");
            } else {
                firstKey = false;
            }

            header.append(key);
            header.append("=\"");
            header.append(headerTreeMap.get(key));
            header.append("\"");
        }

        return header.toString();
    }

    private String getGetQueryParameters(TreeMap<String, String> paramsTreeMap) {

        boolean firstKey = true;
        StringBuilder queryStringBuilder = new StringBuilder();

        for (String key : paramsTreeMap.keySet()) {
            if (!firstKey) {
                queryStringBuilder.append("&");
            } else {
                queryStringBuilder.append("?");
                firstKey = false;
            }

            queryStringBuilder.append(key);
            queryStringBuilder.append("=");

            queryStringBuilder.append(getUrlEncoded(paramsTreeMap.get(key)));
        }

        return queryStringBuilder.toString();
    }

    private static String getHmacShaSignature(String basesign, String hashKey) {
        String HMAC_SHA1_ALGORITHM = "HmacSHA1";
        String result = null;

        try {
            Key signingKey = new SecretKeySpec(hashKey.getBytes(), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(basesign.getBytes());
            result = Base64.encodeToString(rawHmac, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.v(TAG, "Something went wrong with HMAC-SHA1 encoding: " + e.getMessage());
        }

        return result;
    }
    
    
    private static String getUrlEncoded(String toEncode) {
        try {
            toEncode = URLEncoder.encode(toEncode, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Log.v(TAG, "Unsupported encoding: " + ex.getMessage());
        }

        return toEncode;
    }
}
