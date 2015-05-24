package com.carcar5talk.surfaceview;


import android.util.Log;
import com.carcar5talk.compute.StraightLineEquation;
import com.carcar5talk.compute.Vector;

import java.util.ArrayList;


/* ComputePosition class */
public class ComputePosition {
    /* Debugging */
    private String TAG = "[ComputePosition] ";

    private MyCar mMyCar;
    private ArrayList<OtherCars> mOtherCars;
    private StraightLineEquation sle1, sle2;

    /* */
    public ComputePosition(Container container)//MyCar myCar, ArrayList<OtherCars> mOtherCars)
    {
        this.mMyCar = container.getMyCar(); //myCar;
        this.mOtherCars = container.getOtherCars(); //mOtherCars;
        this.sle1 = new StraightLineEquation(mMyCar.getPosition(), new Vector(-0.00928, 0.0068));
        this.sle2 = sle1.getPerpendicularStraightLineEquation(mMyCar.getPosition());
    }

    /* Point to Point distance*/
    public double getStraightDistance(OtherCars otherCars)
    {
        return Math.sqrt((mMyCar.getX() - otherCars.getX()) * (mMyCar.getX() - otherCars.getX()) + (mMyCar.getY() - otherCars.getY()) * (mMyCar.getY() - otherCars.getY()));
    }

    public double getPointToLineDistance(OtherCars otherCars) {
        double denominator = Math.sqrt(sle1.gradient * sle1.gradient + 1);
        double numerator = Math.abs(sle1.gradient * otherCars.getX() - otherCars.getY() + sle1.intercept_y);

        return (numerator / denominator);
    }


    public void gpsToMeter(OtherCars otherCars)
    {
        double[] meter = new double[2];

        meter[0] = (3 * (otherCars.getX() / 0.005));
        meter[1] = (3 * (otherCars.getY() / 0.005));

        otherCars.setPosition(meter);
    }

    public void meterToPixel(OtherCars otherCars)
    {
        double[] pixel = new double[2];

        pixel[0] = (int) (CarView.road * otherCars.getX() / 3);
        pixel[1] = (int) (CarView.road * otherCars.getY() / 3);

        otherCars.setPosition(pixel);
    }


    public void checkDimension(OtherCars othercars)
    {
        if(sle1.gradient < 0) {
            if(sle1.compareToStraightLine(othercars.getPosition()) == -1 && sle2.compareToStraightLine(othercars.getPosition()) == -1 ){
                othercars.setDirectionY(-1);
            }
            else if(sle1.compareToStraightLine(othercars.getPosition()) == 1 && sle2.compareToStraightLine(othercars.getPosition()) == -1) {
                othercars.setDirectionX(-1);
                othercars.setDirectionY(-1);
            }
            else if(sle1.compareToStraightLine(othercars.getPosition()) == 1 && sle2.compareToStraightLine(othercars.getPosition()) == 1) {
                othercars.setDirectionX(-1);
            }
        }
        else {
            if(sle1.compareToStraightLine(othercars.getPosition()) == -1 && sle2.compareToStraightLine(othercars.getPosition()) == -1 ){
                othercars.setDirectionY(-1);
            }
            else if(sle1.compareToStraightLine(othercars.getPosition()) == -1 && sle2.compareToStraightLine(othercars.getPosition()) == 1) {
                othercars.setDirectionX(-1);
                othercars.setDirectionY(-1);
            }
            else if(sle1.compareToStraightLine(othercars.getPosition()) == 1 && sle2.compareToStraightLine(othercars.getPosition()) == 1) {
                othercars.setDirectionX(-1);
            }
        }
    }


    public void computePosition()
    {
        double p2pDistance, p2lDistance, offset;

        for(int i = 0; i < mOtherCars.size(); i++) {
            checkDimension(mOtherCars.get(i));
            p2pDistance = getStraightDistance(mOtherCars.get(i));
            p2lDistance = getPointToLineDistance(mOtherCars.get(i));
            offset = Math.sqrt(p2pDistance * p2pDistance - p2lDistance * p2lDistance);

            mOtherCars.get(i).setX(p2lDistance);    // 경도
            mOtherCars.get(i).setY(offset);         // 위도

            gpsToMeter(mOtherCars.get(i));
            meterToPixel(mOtherCars.get(i));

            mOtherCars.get(i).setX(mOtherCars.get(i).getDirection()[0] * mOtherCars.get(i).getPosition()[0]);
            mOtherCars.get(i).setY(mOtherCars.get(i).getDirection()[1] * mOtherCars.get(i).getPosition()[1]);

            Log.d(TAG + i + "'s X", mOtherCars.get(i).getX() + "");
            Log.d(TAG + i + "'s Y", mOtherCars.get(i).getY() + "");
        }
    }

}// end class
