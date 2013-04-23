package com.esajuhana.ratemypartner.helpers;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Offers method for parsing a string containing JSON-information to JSOBObject.
 *
 * @author TODO
 */
public class JSONParser {
    
    private static final String TAG = "JSONParser";
    
    /**
     * Parses the string given as a parameter to JSONObject.
     * If result could not be parsed returns new (empty) JSONObject.
     * 
     * @param jsonString
     * @return JSONObject
     */
    public static JSONObject parse(String jsonString) {

        JSONObject json = null;
        
        try {
            json = new JSONObject(jsonString);
        } catch (JSONException ex) {
            Log.e(TAG, "Error parsing data " + ex.toString());
        }
        
        return json != null ? json : new JSONObject();
    }    
}
