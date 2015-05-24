package com.carcar5talk.surfaceview;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
	private final String TAG = "[CarView]";
	private final boolean D = true;
	
	// Member fields
	private CarThread mThread;
	private SurfaceHolder mHolder;
	public static Context mContext;

	// Extra variable
	public static int deviceWidth, deviceHeight;
	private double[] position;
	private int count = 0;
	private TimerTask mTask;
	private Timer mTimer;

	// Map variable
	private double side;
	public static double road;

	// Car variable
	private double[] size;
	private double[] pos_mycar;
	private MyCar mMyCar;
	private ArrayList<OtherCars> mOtherCars;
	private Background background;
	private ComputePosition computePosition;
	

	public CarView(Context context) {
		super(context);

		this.position = new double[2];
		this.size = new double[2];
		this.pos_mycar = new double[2];

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

		/* Creat background object */
		this.background = new Background();

		/* Creat my car object and Set my car size */
		this.mMyCar = Carcar5Talk.mContainer.getMyCar();
		this.size[0] = deviceWidth / 7;
		this.size[1] = deviceWidth / 7;
		Log.d(TAG + "size[0]", size[0] + "");
		Log.d(TAG + "size[1]", size[1] + "");
		mMyCar.setPosition(size);
		mMyCar.setMyCar();

		/*
		* Device Width & Height
		* Note3 : 1080, 1701
		* */
		Log.d(TAG + "Device Width", deviceWidth + "");
		Log.d(TAG + "Device Height", deviceHeight + "");

		/* Set map variable */
		road = 2 * deviceWidth / 7;
		side = deviceWidth / 7 * 0.3;
		Log.d(TAG + "road", road + "");
		Log.d(TAG + "side", side + "");


		/* Set my car position */
		pos_mycar[0] = deviceWidth / 2 - size[0] / 2;
		pos_mycar[1] = deviceHeight / 3;
		Log.d(TAG + "pos_mycar[0]", pos_mycar[0] + "");
		Log.d(TAG + "pos_mycar[1]", pos_mycar[1] + "");

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
		canvas.drawBitmap(mMyCar.getMyCar(), (int)pos_mycar[0], (int)pos_mycar[1], null);
	}


	public void OtherCarsDraw(Canvas canvas) {
		int[] temp = new int[2];
		boolean isDanger = false;

		/* Creat other cars object and Set other cars size */
		mOtherCars = Carcar5Talk.mContainer.getOtherCars();
		for(int i = 0; i < mOtherCars.size(); i++) {


			mOtherCars.get(i).setOtherCars((int)size[0], (int)size[1], isDanger);


		}

		for (int i = 0; i < mOtherCars.size(); i++) {
			temp[0] = (int) (pos_mycar[0] + (int) mOtherCars.get(i).getX());
			temp[1] = (int) (pos_mycar[1] + (int) mOtherCars.get(i).getY());

			if(pos_mycar[0] > temp[0]) {
				// Left side
				canvas.drawBitmap(mOtherCars.get(i).getOtherCars(), (int) (side + road / 4), temp[1], null);
			}
			else {
				// Right side
				canvas.drawBitmap(mOtherCars.get(i).getOtherCars(), (int) (deviceWidth - side - 3 * size[0] / 2), temp[1], null);
			}
		}
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

								
								//////////////////////////////////////////////////////
								// 수정
								//////////////////////////////////////////////////////

								BackgroundDraw(canvas);
								MyCarDraw(canvas);

								if (Carcar5Talk.mContainer.getFlag()) {

									computePosition = new ComputePosition(Carcar5Talk.mContainer);
									computePosition.computePosition();

									OtherCarsDraw(canvas);

									Carcar5Talk.mContainer.setFlag(false);
								}
								else {
									OtherCarsDraw(canvas);
								}


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