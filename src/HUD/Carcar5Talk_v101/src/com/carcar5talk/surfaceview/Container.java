package com.carcar5talk.surfaceview;

import java.util.ArrayList;

import android.util.Log;

import com.carcar5talk.compute.Vector;


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


	public void parseDataStr()
	{
		int idx = 0, signX = 1, signY = 1;
		String tmpStr = "";
		double[] position = new double[2];

		/* Set My car */
		// Flag(3)
		mMyCar.setFlag(Integer.parseInt(rawData.substring(0, 3)));
		Log.d(TAG + "My flag", mMyCar.getFlag() + "");

		// GPS(22)
		tmpStr = rawData.substring(3, 25);
		position[1] = Double.parseDouble(tmpStr.substring(0, 10)) / (double) 100;	// 위도
		position[0] = Double.parseDouble(tmpStr.substring(11, 21)) / (double) 100;	// 경도
		mMyCar.setPosition(position);
		Log.d(TAG + "My GPS", tmpStr + "");

		// Speed(6)
		tmpStr = rawData.substring(25, 31);
		mMyCar.setSpeed(Double.parseDouble(tmpStr));
		Log.d(TAG + "My Speed", tmpStr + "");

		// Vector(22)
		tmpStr = rawData.substring(31, 53);
		if(tmpStr.charAt(0) == '-') {	// 위도 벡터 부호
			signX = -1;
		}
		if(tmpStr.charAt(11) == '-'){	// 경도 벡터 부호
			signY = -1;
		}
		position[1] = (double) signX * Double.parseDouble(tmpStr.substring(1, 11));		// 위도
		position[0] = (double) signY * Double.parseDouble(tmpStr.substring(12, 22));	// 경도
		mMyCar.setVector(position);
		Log.d(TAG + "My vector", tmpStr);

		// # of cars(3)
		tmpStr = rawData.substring(53, 56);
		mMyCar.setNumOfCars(Integer.parseInt(tmpStr));
		idx = 56;
		Log.d(TAG + "# of cars", Integer.parseInt(tmpStr) + "");

		mOtherCars.clear();
		/* Set Other Car */
		for (int i = 0; i < mMyCar.getNumOfCars(); i++) {
			// Create other car object
			OtherCars mCar = new OtherCars();

			// ID(int)
			tmpStr = rawData.substring(idx, idx + 17);
			mCar.setId(tmpStr);
			idx += 17;
			Log.d(TAG + "Detected Car's ID", tmpStr);

			// Flag(int)
			tmpStr = rawData.substring(idx, idx + 3);
			mCar.setFlag(Integer.parseInt(tmpStr));
			idx += 3;
			Log.d(TAG + i + "'s Car's Flag", mCar.getFlag() + "");

			// GPS
			tmpStr = rawData.substring(idx, idx + 22);
			position[1] = Double.parseDouble(tmpStr.substring(0, 10)) / (double) 100;
			position[0] = Double.parseDouble(tmpStr.substring(11, 21)) / (double) 100;
			mCar.setPosition(position);
			idx += 22;
			Log.d(TAG + i + "'s Car's GPS", tmpStr);

			// Speed
			tmpStr = rawData.substring(idx, idx + 6);
			mCar.setSpeed(Double.parseDouble(tmpStr));
			idx += 6;
			Log.d(TAG + i + "'s Car's Speed", tmpStr);

			mOtherCars.add(i, mCar);
		}
	}


	/* Parse the raw data */
	public void parseData()
	{
		int i = 0, j = 0, idx = 0, k = 0, mac_idx = 0, signX = 1, signY = 1;
		String tmpStr = "";
		double[] position = new double[2];
		
		/* Set My car */
		// Flag
		mMyCar.setFlag(Integer.valueOf(rawData.charAt(idx++)));

		// GPS(double)
		tmpStr = "";
		for (i = idx; i < 22 + idx; i++)
			tmpStr += rawData.charAt(i);
		position[1] = Double.parseDouble(tmpStr.substring(0, 10)) / (double) 100;
		position[0] = Double.parseDouble(tmpStr.substring(11, 21)) / (double) 100;

		mMyCar.setPosition(position);
		idx = i; // idx = 23

		// Speed(double)
		tmpStr = "";
		for (i = idx; i < 6 + idx; i++)
			tmpStr += rawData.charAt(i);
		mMyCar.setSpeed(Double.parseDouble(tmpStr));
		idx = i; // idx = 29

		// Vector
		tmpStr = "";
		for (i = idx; i < 22 + idx; i++)
			tmpStr += rawData.charAt(i);
		if(tmpStr.charAt(0) == '-') {
			signX = -1;
		}
		if(tmpStr.charAt(11) == '-'){
			signY = -1;
		}
		position[1] = (double) signX * Double.parseDouble(tmpStr.substring(1, 11));
		position[0] = (double) signY * Double.parseDouble(tmpStr.substring(12, 22));
		mMyCar.setVector(position);
		idx = i; // idx = 51



		//진행 방향은 22자의 문자열로 표현된다. “sddmm.mmmmmsdddmm.mmmm”의 형식을 따라 위도의 차이를 표현하기 위해 부호 1자,
		//degree 차이 2자, minute 차이 8자, 경도의 차이를 표현하기 위해 부호 1자, degree 차이 3자, minute 차이 7자를 사용한다.


		// # of cars
		mMyCar.setNumOfCars(Integer.valueOf(rawData.charAt(i)));
		idx++; // idx = 30

		mOtherCars.clear();
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
			position[1] = Double.parseDouble(tmpStr.substring(0, 10)) / (double) 100;
			position[0] = Double.parseDouble(tmpStr.substring(11, 21)) / (double) 100;
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
