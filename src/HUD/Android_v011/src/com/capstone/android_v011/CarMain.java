package com.capstone.android_v011;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.LinearLayout;

public class CarMain extends Activity {
	private static CarView cv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		cv = new CarView(this);
		setContentView(cv);
	}
}
