package com.esajuhana.ratemypartner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

public class MainActivity extends Activity {
	
	public static boolean loggedIn = false;
	private int currentAdjustment = 0;
	
	public void animateButton(int buttonResourceName) {
	    final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
	    animation.setDuration(100); // 100ms
	    animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
	    animation.setRepeatCount(Animation.ABSOLUTE); // repeat once
	    animation.setRepeatMode(Animation.REVERSE); // symmetric animation
	    final Button btn = (Button) findViewById(buttonResourceName);
	    btn.startAnimation(animation);
	}
	
	/**
	 * TODO Shortcut to reset value back to zero ? 
	 * TODO	How to use single method i.e. call same method in increase and decrease and get the reference to clicked button from the View object?
	 * update send_adjustment button's value according to taps
	 * @param change amount to change
	 */
	public void updateCurrentAdjustment(int change) {
		currentAdjustment += change;
		animateButton(R.id.send_adjustments);
		Button sendAdjustment = (Button) findViewById(R.id.send_adjustments);
		if( currentAdjustment == 0 )
			sendAdjustment.setText(R.id.send_adjustments);
		else
			sendAdjustment.setText("Tap here to compensate rating by (" + currentAdjustment + ")");
	}
	
	public void increaseValue(View view) {
		updateCurrentAdjustment(1);
	}
	
	public void decreaseValue(View view) {
		updateCurrentAdjustment(-1);
	}
	
	public void sendAdjustments(View view) {
		//TODO: HTTP requests
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if(!loggedIn) {
		    Intent intent = new Intent(this, LoginActivity.class);
		    //EditText editText = (EditText) findViewById(R.id.edit_message);
		    //String message = editText.getText().toString();
		    //intent.putExtra(EXTRA_MESSAGE, message);
		    startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
