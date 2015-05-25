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
    //private Vector mVector;

    /* Constructor */
    public ComputePosition(Container container)
    {
        this.mMyCar = container.getMyCar();
        this.mOtherCars = container.getOtherCars();
        this.sle1 = new StraightLineEquation(mMyCar.getPosition(), mMyCar.getVector());
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


    public double calDistance(double myY, double myX, double otherY, double otherX){
        double theta, dist;
        double y1 = myY;
        double x1 = myX;
        double y2 = otherY;
        double x2 = otherX;

        theta = x1 - x2;
        dist = Math.sin(deg2rad(y1)) * Math.sin(deg2rad(y2)) + Math.cos(deg2rad(y1))
                * Math.cos(deg2rad(y2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        return dist;
    }


    // 주어진 도(degree) 값을 라디언으로 변환
    private double deg2rad(double deg){
        return (double)(deg * Math.PI / (double)180d);
    }


    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad){
        return (double)(rad * (double)180d / Math.PI);
    }


    public void gpsToMeter(OtherCars otherCars, double rate)
    {
        double[] meter = new double[2];
        meter[0] = otherCars.getX() * rate;
        meter[1] = otherCars.getY() * rate;

        otherCars.setPosition(meter);
    }

    public void meterToPixel(OtherCars otherCars)
    {
        double[] pixel = new double[2];
        //pixel[0] = (int) ((double) 10 * otherCars.getX());
        pixel[0] = (int) (215 * otherCars.getX());
        pixel[1] = (int) ((double) 10 * otherCars.getY());

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
        double p2pDistance, p2lDistance, offset, dist;

        for(int i = 0; i < mOtherCars.size(); i++) {
            checkDimension(mOtherCars.get(i));
            dist = calDistance(mMyCar.getY(), mMyCar.getX(), mOtherCars.get(i).getY(), mOtherCars.get(i).getX());
            p2pDistance = getStraightDistance(mOtherCars.get(i));
            p2lDistance = getPointToLineDistance(mOtherCars.get(i));
            offset = Math.sqrt(p2pDistance * p2pDistance - p2lDistance * p2lDistance);

            mOtherCars.get(i).setX(p2lDistance);    // 경도
            mOtherCars.get(i).setY(offset);         // 위도

            gpsToMeter(mOtherCars.get(i), dist / p2pDistance);
            meterToPixel(mOtherCars.get(i));

            mOtherCars.get(i).setX(mOtherCars.get(i).getDirection()[0] * mOtherCars.get(i).getPosition()[0]);
            mOtherCars.get(i).setY(mOtherCars.get(i).getDirection()[1] * mOtherCars.get(i).getPosition()[1]);
        }
    }

}// end class
