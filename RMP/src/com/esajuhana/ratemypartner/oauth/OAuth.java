package com.esajuhana.ratemypartner.oauth;

import android.util.Base64;
import android.util.Log;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Random;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author TODO
 */
public class OAuth {

    private final String TAG = "OAuth";
    private final int TIMEOUT = 10000;
    private String mConsumerKey;
    private String mConsumerSecret;
    private String mOauthVersion = "1.0";
    private String mOauthSignatureMethod = "HMAC-SHA1";
    private String mTokenSecret = "";
    private static Random sRandom = new Random();

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
     * Gets OAuth 1.0 request token from the URL given as a parameter. Uses
     * HTTP-POST.
     *
     * @param mApiRequestTokenUrl
     * @return result body that the server returned
     */
    public String getRequestToken(String requestTokenUrl) {

        String timestamp = ((Long) getTimestamp()).toString();
        String nonce = ((Long) getNonce()).toString();

        StringBuilder ba = new StringBuilder("POST&");

        try {
            ba.append(URLEncoder.encode(requestTokenUrl, "UTF-8"));
            ba.append("&");
            ba.append(URLEncoder.encode("oauth_callback=oob"
                    + "&oauth_consumer_key=" + mConsumerKey
                    + "&oauth_nonce=" + nonce
                    + "&oauth_signature_method=" + mOauthSignatureMethod
                    + "&oauth_timestamp=" + timestamp
                    + "&oauth_version=" + mOauthVersion, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Log.v(TAG, "UnsupportedEncodingException: " + ex.getMessage());
        }

        String basestring = ba.toString();
        String hashkey = mConsumerSecret + "&";
        String signature = getHmacShaSignature(basestring, hashkey);

        String headerAuth = "OAuth oauth_callback=\"oob"
                + "\", oauth_consumer_key=\"" + mConsumerKey
                + "\", oauth_nonce=\"" + nonce
                + "\", oauth_signature=\"" + signature
                + "\", oauth_signature_method=\"" + mOauthSignatureMethod
                + "\", oauth_timestamp=\"" + timestamp
                + "\", oauth_version=\"" + mOauthVersion + "\"";

        Log.v(TAG, "headerAuth: " + headerAuth);

        String result = getHttpResult(requestTokenUrl, headerAuth, "Authorization");
        
        // TODO: better method for token and token secret extraction
        // TODO: save both as private fields
        if (result != null) {
            String[] resultSplit = result.split("&");
            
            if (resultSplit.length < 3) {
                Log.v(TAG, "resultSplit is too short!");
                return result;
            }
            
            String[] tokenSecretSplit = resultSplit[1].split("=");
            
            if (tokenSecretSplit.length < 2) {
                Log.v(TAG, "tokenSecretSplit is too short!");
                return result;
            }
            
            mTokenSecret = tokenSecretSplit[1];
            Log.v(TAG, "Got tokenSecret = " + mTokenSecret);
        }
        
        return result;
    }

    public String getAccessToken(String accessTokenUrl, String verifier, String token) {
        String timestamp = ((Long) getTimestamp()).toString();
        String nonce = ((Long) getNonce()).toString();

        StringBuilder ba = new StringBuilder("POST&");

        try {
            ba.append(URLEncoder.encode(accessTokenUrl, "UTF-8"));
            ba.append("&");
            ba.append(URLEncoder.encode("oauth_consumer_key=" + mConsumerKey
                    + "&oauth_nonce=" + nonce
                    + "&oauth_signature_method=" + mOauthSignatureMethod
                    + "&oauth_timestamp=" + timestamp
                    + "&oauth_token=" + token
                    + "&oauth_verifier=" + verifier
                    + "&oauth_version=" + mOauthVersion, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Log.v(TAG, "UnsupportedEncodingException: " + ex.getMessage());
        }

        String basestring = ba.toString();
        String hashkey = mConsumerSecret + "&" + mTokenSecret;
        String signature = getHmacShaSignature(basestring, hashkey);

        String headerAuth = "OAuth oauth_consumer_key=\"" + mConsumerKey
                + "\", oauth_nonce=\"" + nonce
                + "\", oauth_signature_method=\"" + mOauthSignatureMethod
                + "\", oauth_signature=\"" + signature
                + "\", oauth_timestamp=\"" + timestamp
                + "\", oauth_token=\"" + token
                + "\", oauth_verifier=\"" + verifier
                + "\", oauth_version=\"" + mOauthVersion + "\"";

        Log.v(TAG, "headerAuth: " + headerAuth);

        return getHttpResult(accessTokenUrl, headerAuth, "Authorization");
    }

    private static long getNonce() {
        return getTimestamp() + sRandom.nextInt();
    }

    private static long getTimestamp() {
        return System.currentTimeMillis() / 1000;
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
            // TODO 
        }

        return result;
    }

    private String getHttpResult(String url, String headerContent, String headerName) throws ParseException {
        String result = null;

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), TIMEOUT);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader(headerName, headerContent);
            HttpResponse response = httpClient.execute(httpPost);

            result = EntityUtils.toString(response.getEntity());

            Log.v(TAG, result == null ? "" : result);
        } catch (ClientProtocolException e) {
            Log.v(TAG, e.getMessage());
        } catch (IOException e) {
            Log.v(TAG, e.getMessage());
        } catch (IllegalStateException e) {
            Log.v(TAG, e.getMessage());
        }

        return result;
    }
}
