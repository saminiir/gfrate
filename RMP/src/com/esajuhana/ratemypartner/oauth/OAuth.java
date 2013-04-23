package com.esajuhana.ratemypartner.oauth;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import java.io.IOException;
import java.io.Serializable;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * OAuth request handler. Implements Serializable-interface.
 *
 * @author TODO
 */
public class OAuth implements Serializable {

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

    public static enum HttpRequestType {

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
     *
     * @return
     */
    public OAuthState getState() {
        return mState;
    }

    /**
     * Gets OAuth 1.0 request token from the URL given as a parameter. Uses
     * HTTP-POST. If provided callbackUrl is null or empty, "oob" (out of band)
     * will be used.
     *
     * @param requestTokenUrl
     * @param callbackUrl
     * @return result body that the server returned
     */
    public String getRequestToken(String requestTokenUrl, String callbackUrl) {

        // Return token, secret and state to initial values
        mToken = "";
        mTokenSecret = "";
        mState = OAuthState.Init;

        TreeMap<String, String> headerTreeMap = new TreeMap<String, String>(getAlwaysUsedParams());

        if (TextUtils.isEmpty(callbackUrl)) {
            headerTreeMap.put("oauth_callback", "oob");
        } else {
            headerTreeMap.put("oauth_callback", callbackUrl);
        }

        String signature = getSignature(requestTokenUrl, headerTreeMap, HttpRequestType.POST);
        headerTreeMap.put("oauth_signature", signature);

        String result = getHttpResult(requestTokenUrl, headerTreeMap, null, HttpRequestType.POST);

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
     * HTTP-POST. Throws IllegalStateException if user hasn't got a request
     * token.
     *
     * @param accessTokenUrl
     * @param verifier
     * @param token
     * @return http body
     */
    public String getAccessToken(String accessTokenUrl, String verifier) {

        if (mState == OAuthState.Init) {
            throw new IllegalStateException("Request token hasn't been retrieved.");
        } else if (mState == OAuthState.GotAccessToken) {
            throw new IllegalStateException("Access token has already been retrieved.");
        }

        TreeMap<String, String> headerTreeMap = new TreeMap<String, String>(getAlwaysUsedParams());

        headerTreeMap.put("oauth_token", mToken);
        headerTreeMap.put("oauth_verifier", verifier);

        String signature = getSignature(accessTokenUrl, headerTreeMap, HttpRequestType.POST);
        headerTreeMap.put("oauth_signature", signature);

        String result = getHttpResult(accessTokenUrl, headerTreeMap, null, HttpRequestType.POST);

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
     * Gets the authorize get-request URI in OAuth 1.0 form. This URI can be
     * used in outer web browser to authenticate with the service provider.
     *
     * @return URI as a string with query parameters
     */
    public String getAuthorizeRequestUri(String url) {

        if (mState == OAuthState.Init) {
            throw new IllegalStateException("Request token hasn't been retrieved.");
        } else if (mState == OAuthState.GotAccessToken) {
            throw new IllegalStateException("Access token has already been retrieved.");
        }

        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("oauth_token", mToken);

        return url + getGetQueryParameters(params);
    }

    /**
     * Accesses protected resource from the URL given as a parameter. GET/POST
     * parameters can be specified with params-parameter. Throws
     * IllegalStateException if user has not authenticated.
     *
     * @param resourceUrl URL to access protected resource
     * @param params HTTP GET/POST parameters
     * @param requestType HTTP request type as enum
     * @return HTTP response as a string
     */
    public String accessProtectedResource(String resourceUrl, Map<String, String> params, String body, HttpRequestType requestType) {

        if (mState != OAuthState.GotAccessToken) {
            throw new IllegalStateException("Access token hasn't been retrieved.");
        }

        TreeMap<String, String> paramsTreeMap = new TreeMap<String, String>(getAlwaysUsedParams());
        paramsTreeMap.put("oauth_token", mToken);

        if (params != null) {
            paramsTreeMap.putAll(params);
        }

        String signature = getSignature(resourceUrl, paramsTreeMap, requestType);
        paramsTreeMap.put("oauth_signature", signature);

        String result = getHttpResult(resourceUrl, paramsTreeMap, body, requestType);

        return result;
    }

    /**
     * Reset this object's state. Sets received token and token secret to empty.
     * Sets internal state to "Init" (no request or access tokens received)
     */
    public void resetState() {

        mToken = "";
        mTokenSecret = "";
        mState = OAuthState.Init;
    }

    private Map<String, String> getAlwaysUsedParams() {

        String timestamp = String.valueOf(getTimestamp());
        String nonce = String.valueOf(getNonce());

        Map<String, String> params = new TreeMap<String, String>();

        params.put("oauth_consumer_key", mConsumerKey);
        params.put("oauth_nonce", nonce);
        params.put("oauth_signature_method", mOauthSignatureMethod);
        params.put("oauth_timestamp", timestamp);
        params.put("oauth_version", mOauthVersion);

        return params;
    }

    private static long getNonce() {
        return getTimestamp() + sRandom.nextInt();
    }

    private static long getTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    private String getHttpResult(String url, TreeMap<String, String> params, String body, HttpRequestType requestType) throws ParseException {
        String result = "";

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), TIMEOUT);

            if (requestType == HttpRequestType.POST) {
                HttpPost httpPost = new HttpPost(url);
                String headerContent = getPostHeader(params);

                Log.v(TAG, "Authorization header: " + headerContent);

                httpPost.setHeader("Authorization", headerContent);

                // TODO: refactor... little bit too much going on here
                if (body != null) {
                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-Type", "application/json; charset=utf-8");

                    StringEntity se = new StringEntity(body);
                    se.setContentType("text/xml");
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json; charset=utf-8"));
                    httpPost.setEntity(se);
                }

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

        if (!TextUtils.isEmpty(result)) {
            Log.v(TAG, "HTTP-result body was: " + result);
        } else {
            Log.v(TAG, "HTTP-result body was empty");
        }

        return result;
    }

    private String getSignature(String url, TreeMap<String, String> headerTreeMap, HttpRequestType requestType) {

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
