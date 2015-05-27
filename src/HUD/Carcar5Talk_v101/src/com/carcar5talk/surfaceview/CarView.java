package com.carcar5talk.surfaceview;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.carcar5talk.bluetooth.BluetoothChatService;
import com.carcar5talk.bluetooth.Carcar5Talk;
import com.example.carcar5talk.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

	// Size variable
	private double side;
	private double road;
	private double[] size;
	private double[] pos_mycar;

	// Car variable
	private MyCar mMyCar;
	private ArrayList<OtherCars> mOtherCars;
	private Background background;
	private ComputePosition computePosition;

	private static OtherCars[] mArrOtherCars;

	// Extra variable
	public static int deviceWidth, deviceHeight;
	private int count = 1;
	public static boolean flag = false;
	private double[] position;


	public CarView(Context context) {
		super(context);

		this.size = new double[2];
		this.pos_mycar = new double[2];
		this.position = new double[2];

		this.mContext = context;
		this.mHolder = getHolder();
		this.mHolder.addCallback(this);

		this.mArrOtherCars = new OtherCars[3];

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
		this.size[1] = deviceHeight / 10;
		Log.d(TAG + "size[0]", size[0] + "");		// 154.285714286
		Log.d(TAG + "size[1]", size[1] + "");		// 170.1
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


		/* Set other car position */
		for(int i = 0; i < 3; i++) {
			position[0] = deviceWidth / 7;
			position[1] = deviceWidth / 7;

			mArrOtherCars[i] = new OtherCars();
			mArrOtherCars[i].setPosition(position);
		}

		position[0] = (int) (deviceWidth - side - 3 * size[0] / 2);
		position[1] = (int) (deviceHeight - 3 * size[1] - 5);
		mArrOtherCars[1].setPosition(position);

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
	}


	public void BackgroundDraw(Canvas canvas) {
		canvas.drawBitmap(background.background, 0, 0, null);
	}


	public void MyCarDraw(Canvas canvas) {
		canvas.drawBitmap(mMyCar.getMyCar(), (int)pos_mycar[0], (int)pos_mycar[1], null);
	}


	/**
	 * 주변 차량, 긴급메시지를 Draw해주는 메소드
	 *
	 * @author Sungjung Kim
	 * @since 2015.05.13
	 *
	 */
	public void OtherCarsDraw(Canvas canvas)
	{
		int[] temp = new int[2];
		boolean isDanger = false;
		Bitmap msg;


		/* Creat other cars object and Set other cars size */
		mOtherCars = Carcar5Talk.mContainer.getOtherCars();
		for(int i = 0; i < mOtherCars.size(); i++) {
			// true: RedCar
			if (mOtherCars.get(i).getY() > 100) isDanger = true;
			else isDanger = false;

			mOtherCars.get(i).setOtherCars((int) size[0], (int) size[1], isDanger);

//			// true: Accident
//			if((mOtherCars.get(i).getFlag() & 1) == 1) {
//				Log.d(TAG + i + "'s accident", "accident@@@@@@@@@@@@@");
//
//				if(count != 100) count++;
//				else count = 1;
//
//				if(count > 50 && count < 100){
//					msg = BitmapFactory.decodeResource(CarView.mContext.getResources(), R.drawable.msg_emergency);
//					msg = Bitmap.createScaledBitmap(msg, deviceWidth - 2 * (int) side, deviceHeight / 3, false);
//
//					canvas.drawBitmap(msg, (int) side, (int) side, null);
//				}
//			}

			Log.d(TAG + i + "'s X", mOtherCars.get(i).getX() + "");
			Log.d(TAG + i + "'s Y", mOtherCars.get(i).getY() + "");
		}


		for (int i = 0; i < mOtherCars.size(); i++) {
			temp[0] = (int) (pos_mycar[0] + (int) mOtherCars.get(i).getX());
			temp[1] = (int) (pos_mycar[1] + (int) mOtherCars.get(i).getY());

			if(mOtherCars.get(i).getX() < -15 && mOtherCars.get(i).getX() > -45) {
				// Left side
				Log.d(TAG + "case 0", "");
				canvas.drawBitmap(mOtherCars.get(i).getOtherCars(), (int) (side + road / 4), temp[1], null);
			}
			else if(mOtherCars.get(i).getX() > 15 && mOtherCars.get(i).getX() < 45){
				// Right side
				Log.d(TAG + "case 1", "");
				canvas.drawBitmap(mOtherCars.get(i).getOtherCars(), (int) (deviceWidth - side - 3 * size[0] / 2), temp[1], null);
			}
			else if(mOtherCars.get(i).getX() > -15 && mOtherCars.get(i).getX() < 15){
				// My side
				Log.d(TAG + "case 2", "");
				canvas.drawBitmap(mOtherCars.get(i).getOtherCars(), (int) pos_mycar[0], temp[1], null);
			}
			else {
				Log.d(TAG + "case 3", "");
				continue;
			}
		}
	}

	private void defaultDraw(Canvas canvas)
	{
		int[] temp = new int[2];
		boolean isDanger = false;


		/* 화면 위로 올라가면 주위 차량 사라짐 */
		if (mArrOtherCars[0].getY() < 0) {
			position[0] = (int) (side + road / 4);
			position[1] = deviceHeight;
			mArrOtherCars[0].setPosition(position);
		}

		if (mArrOtherCars[1].getY() < 0) {
			position[0] = (int) (deviceWidth - side - 3 * size[0] / 2);
			position[1] = deviceHeight;
			mArrOtherCars[1].setPosition(position);
		}


		if (mArrOtherCars[2].getY() < 0) {
			position[0] = (int) pos_mycar[0];
			position[1] = deviceHeight;
			mArrOtherCars[2].setPosition(position);
		}

		/* 위험 차량 감지 */
		if(mArrOtherCars[1].getY() > pos_mycar[1] - size[1] - 100 && mArrOtherCars[1].getY() < pos_mycar[1] + size[1] + 100)
			isDanger = true;

		/* 다른 차량 출력조건 만족시 출력 */
		if (mArrOtherCars[0].getY() < deviceHeight) {
			mArrOtherCars[0].setOtherCars((int) size[0], (int) size[1], false);
			canvas.drawBitmap(mArrOtherCars[0].getOtherCars(), (int) mArrOtherCars[0].getX(), (int) mArrOtherCars[0].getY(), null);
		}

		if (mArrOtherCars[1].getY() < deviceHeight) {
			if(isDanger) {
				mArrOtherCars[1].setOtherCars((int) size[0], (int) size[1], isDanger);
				canvas.drawBitmap(mArrOtherCars[1].getOtherCars(), (int) mArrOtherCars[1].getX(), (int) mArrOtherCars[1].getY(), null);
			}
			else {
				mArrOtherCars[1].setOtherCars((int) size[0], (int) size[1], isDanger);
				canvas.drawBitmap(mArrOtherCars[1].getOtherCars(), (int) mArrOtherCars[1].getX(), (int) mArrOtherCars[1].getY(), null);
			}
		}

		if (mArrOtherCars[2].getY() < deviceHeight) {
			mArrOtherCars[2].setOtherCars((int) size[0], (int) size[1], false);
			canvas.drawBitmap(mArrOtherCars[2].getOtherCars(), (int) mArrOtherCars[2].getX(), (int) mArrOtherCars[2].getY(), null);
		}
	}


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
				canvas = mHolder.lockCanvas();

				if (canvas != null) {
					try {
						synchronized (viewLock) {
							try {
								BackgroundDraw(canvas);
								MyCarDraw(canvas);

								// Bluetooth ON
								if(flag) {
									if (Carcar5Talk.mContainer.getFlag()) {
										computePosition = new ComputePosition(Carcar5Talk.mContainer);
										computePosition.computePosition();
										OtherCarsDraw(canvas);

										Carcar5Talk.mContainer.setFlag(false);
									}
									else {
										OtherCarsDraw(canvas);
									}
								}
								else {
									defaultDraw(canvas);

									/* OtherCar[0] */
									position[0] = (int) (side + road / 4);
									position[1] = (int) pos_mycar[1];
									mArrOtherCars[0].setPosition(position);

									/* OtherCar[1] */
										position[0] = (int) (deviceWidth - side - 3 * size[0] / 2);
										position[1] = mArrOtherCars[1].getY() - 10;
										mArrOtherCars[1].setPosition(position);

									/* OtherCar[2] */
										position[0] = (int) pos_mycar[0];
										position[1] = deviceHeight - 2 * size[1];
										mArrOtherCars[2].setPosition(position);
								}


//								if (Carcar5Talk.mContainer.getFlag()) {
//									computePosition = new ComputePosition(Carcar5Talk.mContainer);
//									computePosition.computePosition();
//
//									Log.d(TAG, "flag=true @@@@@@@@2@@@@@@@@@@@@@ OtherCarsDraw()");
//
//									OtherCarsDraw(canvas);
//									Carcar5Talk.mContainer.setFlag(false);
//								}
//								else {
//									Log.d(TAG, "flag=false ##################3 OtherCarsDraw()");
//									OtherCarsDraw(canvas);
//								}

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