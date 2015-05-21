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
    public ComputePosition(MyCar myCar, ArrayList<OtherCars> mOtherCars)
    {
        this.mMyCar = myCar;
        this.mOtherCars = mOtherCars;
        this.sle1 = new StraightLineEquation(mMyCar.getPosition(), new Vector(-0.00928, 0.0068));
        this.sle2 = sle1.getPerpendicularStraightLineEquation(mMyCar.getPosition());
    }

    /* Point to Point distance*/
    public double getStraightDistance(OtherCars otherCars)
    {
        return Math.sqrt((mMyCar.getX() - otherCars.getX()) * (mMyCar.getX() - otherCars.getX()) + (mMyCar.getY() - otherCars.getY()) * (mMyCar.getY() - otherCars.getY()));
    }

    public double getPointToLineDistance(StraightLineEquation sle, OtherCars otherCars) {
        double denominator = Math.sqrt(sle.gradient * sle.gradient + 1);
        double numerator = Math.abs(sle.gradient * otherCars.getY() - otherCars.getX() + sle.intercept_y);

        return (numerator / denominator);
    }


    public void gpsToMeter(OtherCars otherCars)
    {
        double[] meter = new double[2];

        meter[0] = (3 * (otherCars.getX() / 0.01));
        meter[1] = (3 * (otherCars.getY() / 0.01));

        otherCars.setPosition(meter);
    }

    public void meterToPixel(OtherCars otherCars)
    {
        double[] pixel = new double[2];

        pixel[1] = (int) (215 * otherCars.getY() / 3);
        pixel[0] = (int) (215 * otherCars.getX() / 3);

        otherCars.setPosition(pixel);
    }


    public void checkDimension(StraightLineEquation sle1, StraightLineEquation sle2, OtherCars othercars)
    {
        if(sle1.gradient < 0) {
            if(sle1.compareToStraightLine(othercars.getPosition()) == -1 && sle2.compareToStraightLine(othercars.getPosition()) == 1 ){
                othercars.setX(-1 * othercars.getY());
            }
            else if(sle1.compareToStraightLine(othercars.getPosition()) == 1 && sle2.compareToStraightLine(othercars.getPosition()) == 1) {
                othercars.setX(-1 * othercars.getX());
                othercars.setY(-1 * othercars.getY());
            }
            else if(sle1.compareToStraightLine(othercars.getPosition()) == 1 && sle2.compareToStraightLine(othercars.getPosition()) == -1) {
                othercars.setY(-1 * othercars.getX());
            }
        }
        else {
            if(sle1.compareToStraightLine(othercars.getPosition()) == -1 && sle2.compareToStraightLine(othercars.getPosition()) == -1 ){
                othercars.setX(-1 * othercars.getX());
            }
            else if(sle1.compareToStraightLine(othercars.getPosition()) == 1 && sle2.compareToStraightLine(othercars.getPosition()) == 1) {
                othercars.setY(-1 * othercars.getY());
            }
            else if(sle1.compareToStraightLine(othercars.getPosition()) == 1 && sle2.compareToStraightLine(othercars.getPosition()) == -1) {
                othercars.setX(-1 * othercars.getX());
                othercars.setY(-1 * othercars.getY());
            }
        }
    }


    public void computePosition()
    {
        double p2pDistance, p2lDistance, offset;

        for(int i = 0; i < mOtherCars.size(); i++) {
            OtherCars otherCars = mOtherCars.get(i);

            p2pDistance = getStraightDistance(otherCars);
            p2lDistance = getPointToLineDistance(sle1, otherCars);

            offset = Math.sqrt(p2pDistance * p2pDistance - p2lDistance * p2lDistance);

            otherCars.setX(offset);
            otherCars.setY(p2lDistance);

            gpsToMeter(otherCars);
            meterToPixel(otherCars);
            checkDimension(sle1, sle2, otherCars);

//            Log.d(TAG + i + "'s pixel Y", otherCars.getX() + "");
//            Log.d(TAG + i + "'s pixel X", otherCars.getY() + "");
        }
    }

}// end class
