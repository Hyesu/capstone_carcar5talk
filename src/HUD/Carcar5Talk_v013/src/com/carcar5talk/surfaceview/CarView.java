package com.carcar5talk.surfaceview;

import java.util.ArrayList;

import com.carcar5talk.bluetooth.BluetoothChatService;
import com.carcar5talk.bluetooth.Carcar5Talk;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class CarView extends SurfaceView implements Callback {
	// Debugging
	private static final String TAG = "[CarView]";
	private static final boolean D = true;
	
	// Member fields
	private CarThread mThread;
	private SurfaceHolder mHolder;
	public static Context mContext;

	// Extra variable
	public static int deviceWidth, deviceHeight;
	private double[] position;

	private static MyCar mMyCar;
	//private static OtherCars[] mOtherCars;
	private static Background background;
	private int numOfCars;
	
	//////////////////////////////////////////////////////
	// 수정
	//////////////////////////////////////////////////////
	
	private ArrayList<OtherCars> mOtherCars;
	private boolean flag = true;
	private ComputePosition computePosition;
	//////////////////////////////////////////////////////
	// 수
	//////////////////////////////////////////////////////

	

	public CarView(Context context) {
		super(context);

		//mOtherCars = new OtherCars[2];
		position = new double[2];

		this.mContext = context;
		this.mHolder = getHolder();
		this.mHolder.addCallback(this);
		
		setFocusable(true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setWillNotDraw(false);

		this.deviceWidth = getWidth();
		this.deviceHeight = getHeight();
		this.mThread = new CarThread();

		/* Creat my car object */
		this.mMyCar = Carcar5Talk.mContainer.getMyCar();

		/* Creat background object */
		this.background = new Background();

		/* Creat other cars object */
		this.mOtherCars = Carcar5Talk.mContainer.getOtherCars();

		/* Set My Car size */
		position[0] = 150;
		position[1] = 150;
		mMyCar.setPosition(position);
		mMyCar.setMyCar();


		/* deviceWidth / 2 - 80, deviceHeight / 3 : 285, 378 */
		Log.d(TAG + "MyCar position[0]", deviceWidth / 2 - 80 + "");
		Log.d(TAG + "MyCar position[1]", deviceHeight / 3 + "");


		try {
			mThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mThread.canRun = false;
		//((Activity) mContext).finish();
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		Paint paint = new Paint();
		paint.setColor(Color.GRAY);
		paint.setTextSize(30);

//		if (mOtherCars[0].isDetected)
//			canvas.drawText("Other cars detected.", 10, 20, paint);
//		else
//			canvas.drawText("Nothing is detected.", 10, 20, paint);
	}


	public void BackgroundDraw(Canvas canvas) {
		canvas.drawBitmap(background.background, 0, 0, null);
	}

	public void MyCarDraw(Canvas canvas) {
		
		////////////////////////////////////////////////////////
		// myCar 위치 설정
		////////////////////////////////////////////////////////

		/* Center of display */
		canvas.drawBitmap(mMyCar.getMyCar(), deviceWidth / 2 - 80, deviceHeight / 3, null);
		//canvas.drawBitmap(mMyCar.getMyCar(), deviceWidth / 3 + 41, deviceHeight / 3, null);

		//////////////////////////////////////////////////////
		// 수정
		//////////////////////////////////////////////////////
	}



	public void OtherCarsDraw(Canvas canvas) {
		mOtherCars.get(0).setPosition(position);
		mOtherCars.get(0).setOtherCars();

		mOtherCars.get(1).setPosition(position);
		mOtherCars.get(1).setOtherCars();

		position[0] = mMyCar.getX();
		position[1] = mMyCar.getY();

//		Log.d(TAG + "' Y", (int) (mOtherCars.get(0).getX()) + "");
//		Log.d(TAG + "' X", (int) (mOtherCars.get(0).getY()) + "");

//		canvas.drawBitmap(mOtherCars.get(0).getOtherCars(), (int) (position[0] + mOtherCars.get(0).getX()), (int) (position[1] + mOtherCars.get(0).getY()), null);
//		canvas.drawBitmap(mOtherCars.get(1).getOtherCars(), (int) (position[0] + mOtherCars.get(1).getX()), (int) (position[1] + mOtherCars.get(1).getY()), null);

		if(Carcar5Talk.mContainer.getFlag()) {
			for (int i = 0; i < mOtherCars.size(); i++) {
				canvas.drawBitmap(mOtherCars.get(i).getOtherCars(), (int) (position[0] + mOtherCars.get(i).getX()), (int) (position[1] + mOtherCars.get(i).getY()), null);

				Log.d(TAG + i + "'s Y", i + "");
			}
		}
//		else {
//			canvas.drawBitmap(mOtherCars.get(0).getOtherCars(), deviceWidth - 215, deviceHeight - 215, null);
//			canvas.drawBitmap(mOtherCars.get(1).getOtherCars(), deviceWidth - 215, deviceHeight - 215, null);
//		}


		/*
		//////////////////////////////////////////////////////
		mOtherCars 위치 설정
		////////////////////////////////////////////////////
		수정
		////////////////////////////////////////////////////
		if (mOtherCars[0].getY() < deviceHeight) {
		canvas.drawBitmap(mOtherCars[0].getOtherCars(), mOtherCars[0].getX(), mOtherCars[0].getY(), null);
		mOtherCars[0].isDetected = true;
		}

		if (mOtherCars[0].getY() < 0) {
		position[0] = 50; position[1] = deviceHeight;
		mOtherCars[0].setPosition(position);
		mOtherCars[0].isDetected = false;
		}


		if (mOtherCars[1].getY() < deviceHeight) {
		canvas.drawBitmap(mOtherCars[1].getOtherCars(), mOtherCars[1].getX(), mOtherCars[1].getY(), null);
		mOtherCars[1].isDetected = true;
		}

		if (mOtherCars[1].getY() < 0) {
		position[0] = deviceWidth - 200; position[1] = deviceHeight;
		mOtherCars[1].setPosition(position);
		mOtherCars[1].isDetected = false;
		}
		*/
	}


	
	
	/** CarThread class */
	class CarThread extends Thread {
		boolean canRun = true;

		// Thread Constructor
		public CarThread() {

		}

		// Message handler
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				invalidate(); // draw repaint
			}
		};


		public void run() {
			Object viewLock = new Object();
			Canvas canvas = null;
			
			while (canRun) {		
				canvas = mHolder.lockCanvas(); // Lock the canvas and Asign the buffer
	

				if (canvas != null) {
					try {
						synchronized (viewLock) {
							try {
//								OtherCarsDraw(canvas);
//								position[0] = 50; position[1] = mOtherCars[0].getY() - 1;
//								mOtherCars[0].setPosition(position);
//
//
//								position[0] = deviceWidth - 200; position[1] = mOtherCars[1].getY() - 3;
//								mOtherCars[1].setPosition(position);
	
								
								//////////////////////////////////////////////////////
								// 수정
								//////////////////////////////////////////////////////

								if (Carcar5Talk.mContainer.getFlag()) {
									//Log.d(TAG + " Before mContainer Flag", Carcar5Talk.mContainer.getFlag() + "");

									BackgroundDraw(canvas);
									MyCarDraw(canvas);

									computePosition = new ComputePosition(mMyCar, mOtherCars);
									computePosition.computePosition();

									OtherCarsDraw(canvas);

									Carcar5Talk.mContainer.setFlag(false);
									canRun = false;
								}

//								if(Carcar5Talk.mContainer.getFlag()) {
//									Carcar5Talk.mContainer.parseData();
//
//									mMyCar = Carcar5Talk.mContainer.getMyCar();
//									mOtherCars = Carcar5Talk.mContainer.getOtherCars();
//
//
//
//									Log.d(TAG + "M flag", mMyCar.getFlag() + "");
//									Log.d(TAG + "M GPS", mMyCar.getPosition() + "");
//									Log.d(TAG + "M Speed", mMyCar.getSpeed() + "");
//									Log.d(TAG + "M # Of Cars", mMyCar.getNumOfCars() + "");
//
//									//Log.d(TAG + "O ID", "");
//									Carcar5Talk.mContainer.setFlag(false);
//								}

//								if(Carcar5Talk.mContainer != null)
//									Log.d(TAG + " rawData", Carcar5Talk.mContainer.getRawData());						
								

								//////////////////////////////////////////////////////
								// 수정
								//////////////////////////////////////////////////////
								
							} catch (Exception e) {
								e.printStackTrace();
							}
	
						}
					} finally {
						if (canvas != null)
							mHolder.unlockCanvasAndPost(canvas);
					}
				
				}
			}// while
		}// run
	}
	/** CarThread class end */

	
}// SurfaceView