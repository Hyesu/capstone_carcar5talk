package com.capstone.android_v011;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class OtherCars {
	public int posX, posY;			// 가로, 세로
	public Bitmap cars;
	
	public OtherCars(int x, int y) {
		this.posX = x;
		this.posY = y;
		
		cars = BitmapFactory.decodeResource(CarView.mContext.getResources(), R.drawable.car2);
		Bitmap.createScaledBitmap(cars, this.posX, this.posY, false);
	}
}
