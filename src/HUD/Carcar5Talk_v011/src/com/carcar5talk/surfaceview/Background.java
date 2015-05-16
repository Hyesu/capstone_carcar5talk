package com.carcar5talk.surfaceview;

import com.example.carcar5talk.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Background {
	public static Bitmap background;
	public static int width, height;
	
	// ������
	public Background() {
		background = BitmapFactory.decodeResource(CarView.mContext.getResources(), R.drawable.road);
		background = Bitmap.createScaledBitmap(background, CarView.deviceWidth, CarView.deviceHeight, false);

		width = background.getWidth();
		height = background.getHeight();
	}
}
