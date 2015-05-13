package com.carcar5talk.surfaceview;

import com.example.carcar5talk.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

// class My Car
public class MyCar {
	public int posX, posY;
	public Bitmap myCar;
	
	public MyCar(int x, int y) {
		this.posX = x;
		this.posY = y;
		
		myCar = BitmapFactory.decodeResource(CarView.mContext.getResources(), R.drawable.car1);
		myCar = Bitmap.createScaledBitmap(myCar, posX, posY, false);
	}
}// end class
