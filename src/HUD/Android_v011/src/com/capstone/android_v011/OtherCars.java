package com.capstone.android_v011;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class OtherCars {
	public int posX, posY; // ����, ����
	public Bitmap cars;
	
	
	public OtherCars(int x, int y) {
		this.posX = x;
		this.posY = y;

		cars = BitmapFactory.decodeResource(CarView.mContext.getResources(), R.drawable.car2);
		//cars = resizeBitmapImage(cars, 100);
		Bitmap.createScaledBitmap(cars, this.posX, this.posY, false);
	}


	/**
	 * Bitmap�̹����� ����, ���� ����� ������¡ �Ѵ�.
	 * 
	 * @param source
	 *            ���� Bitmap ��ü
	 * @param maxResolution
	 *            ���� �ػ�
	 * @return ��������� �̹��� Bitmap ��ü
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
