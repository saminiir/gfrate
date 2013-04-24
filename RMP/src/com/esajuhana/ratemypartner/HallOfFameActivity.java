package com.esajuhana.ratemypartner;

import org.w3c.dom.Text;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class HallOfFameActivity extends Activity {
	
    public static String[] countries = {
    	"Afghanistan", "Albania", "Algeria",
        "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica",
        "Antigua and Barbuda", "Argentina", "Armenia", "Aruba",
        "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain",
        "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin",
        "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegovina",
        "Botswana", "Bouvet Island", "Brazil"
        };
    
    private String mUriBase;
    private String mUriGetHallOfFame;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall_of_fame);
        
        mUriBase = getResources().getString(R.string.uri_base);
        mUriGetHallOfFame = getResources().getString(R.string.uri_get_hall_of_fame);
        
        fillTableLayout();
    }
    
    public void fillTableLayout() {
    	
    	// get a reference for the TableLayout
        TableLayout table = (TableLayout) findViewById(R.id.TableLayout01);
        
        int partnerRanking = 1;
        
        for(String s: countries) {
        	// create a new TableRow
            TableRow row = new TableRow(this);
            // create a new TextView
            TextView rank = new TextView(this);
            rank.setText(partnerRanking + ": ");
            TextView t = new TextView(this);
            t.setText(s);
            row.addView(rank); partnerRanking++;
            row.addView(t);
            table.addView(row, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
