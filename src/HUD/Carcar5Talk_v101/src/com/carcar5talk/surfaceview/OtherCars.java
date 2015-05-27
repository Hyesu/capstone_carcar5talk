package com.carcar5talk.surfaceview;	

import com.example.carcar5talk.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

// class Other Cars
public class OtherCars 
{
	/* Member field */
	private String id;
	private int flag;
	private int[] direction;
	private double[] position;
	private double speed;
	private Bitmap otherCars;
	
	public boolean isDetected;
	
	/* Constructor */
	public OtherCars() { 
		position = new double[2];
		direction = new int[2];

		direction[0] = 1; direction[1] = 1;
	}

	/* Getter */
	public String getId()
	{
		return id;
	}
	
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

	public int[] getDirection()
	{
		return direction;
	}

	public double getSpeed()
	{
		return speed;
	}
	
	public Bitmap getOtherCars()
	{
		return otherCars;
	}

	
	/* Setter */
	public void setId(String id)
	{
		this.id = id;
	}
	
	public void setFlag(int flag)
	{
		this.flag = flag;
	}
	
	public void setPosition(double[] position)
	{
		this.position[0] = position[0];
		this.position[1] = position[1];
	}

	public void setX(double x)
	{
		this.position[0] = x;
	}

	public void setY(double y)
	{
		this.position[1] = y;
	}

	public void setDirectionX(int directionX)
	{
		this.direction[0] = directionX;
	}

	public void setDirectionY(int directionY)
	{
		this.direction[1] = directionY;
	}

	public void setSpeed(double speed)
	{
		this.speed = speed;
	}
	
	public void setOtherCars(int sizeX, int sizeY, boolean isDanger)
	{
		if (isDanger) this.otherCars = BitmapFactory.decodeResource(CarView.mContext.getResources(), R.drawable.car_danger);
		else this.otherCars = BitmapFactory.decodeResource(CarView.mContext.getResources(), R.drawable.car_other);

		this.otherCars = Bitmap.createScaledBitmap(otherCars, sizeX, sizeY, false);
	}
	

	public Bitmap resizeBitmapImage(Bitmap source, int maxResolution) 
	{
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
