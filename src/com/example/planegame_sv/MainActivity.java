package com.example.planegame_sv;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	MySurfaceView mySV;
	static Button btnDown, btnUp, btnLeft, btnRight;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mySV = (MySurfaceView)findViewById(R.id.surfaceView1);
		btnUp = (Button)findViewById(R.id.btnUp);
		btnDown = (Button)findViewById(R.id.btnDown);
		btnLeft = (Button)findViewById(R.id.btnLeft);
		btnRight = (Button)findViewById(R.id.btnRight);
		btnUp.setOnTouchListener(new ButtonListener());
		btnDown.setOnTouchListener(new ButtonListener());
		btnLeft.setOnTouchListener(new ButtonListener());
		btnRight.setOnTouchListener(new ButtonListener());
		btnVisible(false);
	}
	
	
	class ButtonListener implements OnClickListener, OnTouchListener {		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {				
				mySV.buttonDown(v);
				
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				mySV.buttonUp(v);
			}
			return false;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
		}
		
	}
	
	public static void btnVisible(boolean s) {
		if (s) {			
			btnUp.setVisibility(View.VISIBLE);
			btnDown.setVisibility(View.VISIBLE);
			btnLeft.setVisibility(View.VISIBLE);
			btnRight.setVisibility(View.VISIBLE);
		} else {
			btnUp.setVisibility(View.INVISIBLE);
			btnDown.setVisibility(View.INVISIBLE);
			btnLeft.setVisibility(View.INVISIBLE);
			btnRight.setVisibility(View.INVISIBLE);
		}
	}
	
}
