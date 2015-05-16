package com.carcar5talk.surfaceview;

import java.util.ArrayList;

import android.util.Log;


// Class container
public class Container {
	/* Debugging */
	private static final String TAG = "[Container]";
	
	/* Member field */
	private String rawData;
	private MyCar mMyCar;
	private ArrayList<OtherCars> mOtherCars;
	
	// Constructor
	public Container() {
		mMyCar = new MyCar();
		mOtherCars = new ArrayList<OtherCars>();
	}
	
	
	/* Getter */
	public String getRawData()
	{
		return rawData;
	}
	
	public MyCar getMyCar()
	{
		return mMyCar;
	}
	
	public ArrayList<OtherCars> getOtherCars()
	{
		return mOtherCars;
	}
	
	
	/* Setter */
	public void setRawData(String rawData)
	{
		this.rawData = rawData;
		
		Log.d(TAG + " rawData", rawData);
	}
	
	public void setMyCar(MyCar mMyCar)
	{
		this.mMyCar = mMyCar; 
	}
	
	public void setOtherCars(ArrayList<OtherCars> mOtherCars)
	{
		this.mOtherCars = mOtherCars;
	}
	

	/* Convert gps to position */
	private int[] gpsConverter(String gpsStr)
	{
		int[] position = new int[2];
		
		/////////////////////////////////////////////////
		// ¼öÁ¤
		/////////////////////////////////////////////////

		
		
		return position;
	}
	
	
	/* Parse the raw data */
	public void parseData()
	{
		int i, j, idx = 0;
		String tmpStr = "";
		int[] position = new int[2];
		
		/* Set My car */
		// Flag
		mMyCar.setFlag(Integer.valueOf(rawData.charAt(idx++)));
		
		// GPS
		tmpStr = "";
		for (i = idx; i < 22 + idx; i++)
			tmpStr += rawData.charAt(i);
		position = gpsConverter(tmpStr);
		mMyCar.setPosition(position);
		idx = i; // idx = 23
		
		// Speed
		for (i = idx; i < 6 + idx; i++)
			tmpStr += rawData.charAt(i);
		mMyCar.setSpeed(Integer.valueOf(tmpStr));
		idx = i; // idx = 29
		
		// # of cars
		mMyCar.setNumOfCars(Integer.valueOf(rawData.charAt(i)));
		idx++; // idx = 30
		
		Log.d("[Container] NumOfCars", mMyCar.getNumOfCars() + "");

		
		/* Set Other Car */
		for (i = 0; i < mMyCar.getNumOfCars(); i++) {
			// Create other car object
			OtherCars mCar = new OtherCars();

			// ID(char)
			tmpStr = "";
			for (j = idx; j < 6 + idx; j++)
				tmpStr += rawData.charAt(j);
			mCar.setId(tmpStr);
			idx = j; // idx = 36

			// Flag(int)
			mCar.setFlag(Integer.valueOf(rawData.charAt(j)));
			idx++;

			// GPS(int)
			tmpStr = "";
			for (j = idx; j < 22 + idx; j++)
				tmpStr += rawData.charAt(j);
			position = gpsConverter(tmpStr);
			mCar.setPosition(position);
			idx = j;

			// Speed(int)
			tmpStr = "";
			for (j = idx; j < 6 + idx; j++)
				tmpStr += rawData.charAt(j);
			mCar.setSpeed(Integer.valueOf(tmpStr));
			idx = j;
			
			mOtherCars.add(i, mCar);
		}

		Log.d("[+]idx", idx + "");
	}

}
