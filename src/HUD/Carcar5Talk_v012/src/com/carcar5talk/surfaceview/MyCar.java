package com.carcar5talk.surfaceview;

import com.example.carcar5talk.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

// class My Car
public class MyCar {
	/* Member field */
	private int flag;
	private int[] position;
	private double speed;
	private int numOfCars;
	private Bitmap myCar;
	
	public MyCar() 
	{ 
		position = new int[2];
	}
	
	
	/* Getter */
	public int getFlag()
	{
		return flag;
	}
	
	public int[] getPosition() 
	{
		return position;
	}
	
	public int getX()
	{
		return position[0];
	}
	
	public int getY()
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
	
	
	
	/* Setter */
	public void setFlag(int flag)
	{
		this.flag = flag;
	}
	
	public void setPosition(int[] position) 
	{
		this.position[0] = position[0];
		this.position[1] = position[1];
	}
	
	public void setSpeed(int speed)
	{
		this.speed = speed;
	}
	
	public void setNumOfCars(int numOfCars)
	{
		this.numOfCars = numOfCars;
	}
	
	public void setMyCar() 
	{
		this.myCar = BitmapFactory.decodeResource(CarView.mContext.getResources(), R.drawable.car1);
		this.myCar = Bitmap.createScaledBitmap(myCar, position[0], position[1], false);
	}
	
}// end class
