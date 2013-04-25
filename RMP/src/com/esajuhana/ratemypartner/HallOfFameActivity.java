package com.esajuhana.ratemypartner;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import com.esajuhana.ratemypartner.helpers.JSONParser;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HallOfFameActivity extends Activity {

    private final String TAG = "HallOfFameActivity";
    private String mUriBase;
    private String mUriGetHallOfFame;
    private JSONObject mJSONtopList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall_of_fame);

        mUriBase = getResources().getString(R.string.uri_base);
        mUriGetHallOfFame = getResources().getString(R.string.uri_get_hall_of_fame);
        
        new HallOfFameActivity.GetHallOfFameTask().execute();
    }

    public void fillTableLayout() {

        if (mJSONtopList == null) {
            return;
        }
        
        // get a reference for the TableLayout
        TableLayout table = (TableLayout) findViewById(R.id.TableLayout01);
        
        JSONArray array;
        
        try {
            array = mJSONtopList.getJSONArray("topPoints");
        } catch (JSONException ex) {
            Log.e(TAG, ex.getMessage());
            return;
        }
        
        Log.v(TAG, array.toString());
        
        if (array != null && array.length() == 0) {
            Log.v(TAG, "JSONArray was empty.");
            return;
        }
        
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject rowObject = array.getJSONObject(i);
                // create a new TableRow
                TableRow row = new TableRow(this);
                // create a new TextView
                TextView rank = new TextView(this);
                rank.setText(rowObject.getInt("points") + ": ");
                TextView t = new TextView(this);
                t.setText(rowObject.getString("name"));
                row.addView(rank);
                row.addView(t);
                
                table.addView(row, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            } catch (JSONException ex) {
                Log.e(TAG, ex.getMessage());
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
     * AsyncTask for getting hall of fame from public end point.
     */
    private class GetHallOfFameTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
 
            String result = "";

            HttpClient httpClient = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);

            HttpGet httpGet = new HttpGet(mUriBase + mUriGetHallOfFame);
            
            try {
                HttpResponse response = httpClient.execute(httpGet);
                result = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                Log.v(TAG, e.getMessage());
            } catch (IOException e) {
                Log.v(TAG, e.getMessage());
            } catch (IllegalStateException e) {
                Log.v(TAG, e.getMessage());
            }
            
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v(TAG, "Got response: " + result);
            
            if (!TextUtils.isEmpty(result)) {
                mJSONtopList = JSONParser.parse(result);
                fillTableLayout();
            }
        }
    }
}
