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

	/* Extra value */
	private boolean flag;
	
	// Constructor
	public Container() {
		this.flag = false;

		this.mMyCar = new MyCar();
		this.mOtherCars = new ArrayList<OtherCars>();
	}
	
	
	/* Getter */
	public String getRawData()
	{
		return this.rawData;
	}
	
	public MyCar getMyCar()
	{
		return this.mMyCar;
	}
	
	public ArrayList<OtherCars> getOtherCars()
	{
		return this.mOtherCars;
	}

	public boolean getFlag() {
		return this.flag;
	}
	
	/* Setter */
	public void setRawData(String rawData)
	{
		this.rawData = rawData;
		
		Log.d(TAG + " rawData", this.rawData);
	}
	
	public void setMyCar(MyCar mMyCar)
	{
		this.mMyCar = mMyCar; 
	}
	
	public void setOtherCars(ArrayList<OtherCars> mOtherCars)
	{
		this.mOtherCars = mOtherCars;
	}
	
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	/* Convert gps to position */
	private int[] gpsConverter(String gpsStr)
	{
		int[] position = new int[2];
		
		/////////////////////////////////////////////////
		// 수정
		/////////////////////////////////////////////////



		
		return position;
	}
	
	
	/* Parse the raw data */
	public void parseData()
	{
		int i = 0, j = 0, idx = 0, k = 0, mac_idx = 0;
		String tmpStr = "";
		double[] position = new double[2];
		
		/* Set My car */
		// Flag
		mMyCar.setFlag(Integer.valueOf(rawData.charAt(idx++)));

		// GPS(double)
		tmpStr = "";
		for (i = idx; i < 22 + idx; i++)
			tmpStr += rawData.charAt(i);
		position[0] = Double.parseDouble(tmpStr.substring(0, 10));
		position[1] = Double.parseDouble(tmpStr.substring(11, 21));
		mMyCar.setPosition(position);
		idx = i; // idx = 23

		// Speed(double)
		tmpStr = "";
		for (i = idx; i < 6 + idx; i++)
			tmpStr += rawData.charAt(i);
		mMyCar.setSpeed(Double.parseDouble(tmpStr));
		idx = i; // idx = 29

		// # of cars
		mMyCar.setNumOfCars(Integer.valueOf(rawData.charAt(i)));
		idx++; // idx = 30

		
		/* Set Other Car */
		for (i = 0; i < mMyCar.getNumOfCars(); i++) {
			// Create other car object
			OtherCars mCar = new OtherCars();

			// ID(int)
			tmpStr = "";
			for (j = idx; j < 6 + idx; j++) {
				tmpStr = String.format("%X", (rawData.charAt(j) & 0xF0) >> 4) + String.format("%X", rawData.charAt(j) & 0x0F) + tmpStr;
				if(j != 5 + idx)
					tmpStr = ":" + tmpStr;
			}
			Log.d(TAG + "Detected Car's ID", tmpStr);

			mCar.setId(tmpStr);
			idx = j; // idx = 36

			// Flag(int)
			mCar.setFlag(Integer.valueOf(rawData.charAt(j)));
			idx++;

			// GPS(double)
			tmpStr = "";
			for (j = idx; j < 22 + idx; j++)
				tmpStr += rawData.charAt(j);
			position[0] = Double.parseDouble(tmpStr.substring(0, 10));
			position[1] = Double.parseDouble(tmpStr.substring(11, 21));
			mCar.setPosition(position);
			idx = j;

			// Speed(int)
			tmpStr = "";
			for (j = idx; j < 6 + idx; j++)
				tmpStr += rawData.charAt(j);
			mCar.setSpeed(Double.parseDouble(tmpStr));
			idx = j;
			
			mOtherCars.add(i, mCar);
		}
	}

}
