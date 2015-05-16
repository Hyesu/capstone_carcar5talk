package com.example.carcar5talk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class OtherCars {
	public int posX, posY; // 가로, 세로
	public Bitmap cars;
	public boolean isDetected;
	
	
	public OtherCars(int x, int y) {
		this.posX = x;
		this.posY = y;

		cars = BitmapFactory.decodeResource(CarView.mContext.getResources(), R.drawable.car2);
		cars = Bitmap.createScaledBitmap(cars, this.posX, this.posY, false);
	}


	/**
	 * Bitmap이미지의 가로, 세로 사이즈를 리사이징 한다.
	 * 
	 * @param source
	 *            원본 Bitmap 객체
	 * @param maxResolution
	 *            제한 해상도
	 * @return 리사이즈된 이미지 Bitmap 객체
	 */
	public Bitmap resizeBitmapImage(Bitmap source, int maxResolution) {
		int width = source.getWidth();
		int height = source.getHeight();
		int newWidth = width;
		int newHeight = height;
		float rate = 0.0f;

		if (width > height) {
			if (maxResolution < width) {
				rate = maxResolution / (float) width;
				newHeight = (int) (height * rate);
				newWidth = maxResolution;
			}
		} else {
			if (maxResolution < height) {
				rate = maxResolution / (float) height;
				newWidth = (int) (width * rate);
				newHeight = maxResolution;
			}
		}

		return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
	}
	
}
