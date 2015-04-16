package com.capstone.android_v011;

import android.app.Activity;
import android.os.Bundle;

public class CarMain extends Activity {
	private static CarView cv;
	private static int numCar = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		cv = new CarView(this, numCar);
		setContentView(cv);
	}
	
	
	
}
