package com.carcar5talk.surfaceview;

import com.carcar5talk.compute.Vector;
import com.example.carcar5talk.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

// class My Car
public class MyCar {
	/* Member field */
	private int flag;
	private double[] position;
	private double speed;
	private int numOfCars;
	private Bitmap myCar;
	private Vector mVector;
	public MyCar() 
	{ 
		position = new double[2];
		mVector = new Vector();
	}
	
	
	/* Getter */
	public int getFlag()
	{
		return flag;
	}
	
	public double[] getPosition()
	{
		return position;
	}
	
	public double getX()
	{
		return position[0];
	}
	
	public double getY()
	{
		return position[1];
	}
	
	public double getSpeed()
	{
		return speed;
	}
	
	public int getNumOfCars()
	{
		return numOfCars;
	}
	
	public Bitmap getMyCar() 
	{
		return myCar;
	}



	public double getVectorX()
	{
		return mVector.x;
	}

	public double getVectorY()
	{
		return mVector.y;
	}

	public Vector getVector() {
		return this.mVector;
	}


	
	/* Setter */
	public void setFlag(int flag)
	{
		this.flag = flag;
	}
	
	public void setPosition(double[] position)
	{
		this.position[0] = position[0];
		this.position[1] = position[1];
	}
	
	public void setSpeed(double speed)
	{
		this.speed = speed;
	}
	
	public void setNumOfCars(int numOfCars)
	{
		this.numOfCars = numOfCars;
	}
	
	public void setMyCar() 
	{
		this.myCar = BitmapFactory.decodeResource(CarView.mContext.getResources(), R.drawable.car_my);
		this.myCar = Bitmap.createScaledBitmap(myCar, (int)position[0], (int)position[1], false);
	}

	public void setVectorX(double x)
	{
		this.mVector.x = x;
	}

	public void setVectorY(double y)
	{
		this.mVector.y = y;
	}

	public void setVector(double[] vector)
	{
		this.mVector.x = vector[0];
		this.mVector.y = vector[1];
	}

	
}// end class
