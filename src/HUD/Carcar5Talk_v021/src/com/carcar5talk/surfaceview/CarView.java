package com.carcar5talk.surfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.example.carcar5talk.R;

import java.util.Timer;
import java.util.TimerTask;

public class CarView extends SurfaceView implements Callback {
	// Debugging
	private static final String TAG = "[CarView]";
	private static final boolean D = true;
	
	// Member fields
	private CarThread mThread;
	private SurfaceHolder mHolder;
	public static Context mContext;

	// Car variable
	private static MyCar mMyCar;
	private static OtherCars[] mOtherCars;
	private static Background background;
	public static int[] size;
	private int[] msg_size;
	private double[] pos_mycar;

	// Map variable
	public static double side;
	public static double road;

	// Extra variable
	public static int deviceWidth, deviceHeight;
	private int[] position;
	private int count;
	private int loop;
	private int scenario;
	private boolean isAccident;
	private Bitmap msg;

	public CarView(Context context) {
		super(context);

		this.mContext = context;
		this.mHolder = getHolder();
		this.mHolder.addCallback(this);

		this.position = new int[2];
		this.size = new int[2];
		this.msg_size = new int[2];
		this.pos_mycar = new double[2];

		this.mOtherCars = new OtherCars[3];

		this.count = 0;
		this.loop = 0;
		this.scenario = 1;
		this.isAccident = false;
		this.msg = null;
		
		setFocusable(true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setWillNotDraw(false);

		this.deviceWidth = getWidth();
		this.deviceHeight = getHeight();

		/* Set map variable */
		road = 2 * deviceWidth / 7;
		side = deviceWidth / 7 * 0.3;
		Log.d(TAG + "road", road + "");
		Log.d(TAG + "side", side + "");

		mThread = new CarThread();
		mMyCar = new MyCar();
		background = new Background();

		/* Set my car size */
		this.size[0] = deviceWidth / 7;
		this.size[1] = deviceHeight / 10;
		mMyCar.setPosition(size);

		/* Set message size */
		this.msg_size[0] = deviceWidth - 2 * (int) side;
		this.msg_size[1] = 2 * deviceWidth / 7;

		/* Set my car position */
		pos_mycar[0] = deviceWidth / 2 - size[0] / 2;
		pos_mycar[1] = deviceHeight / 3;


		/* Set other car position */
		for(int i = 0; i < 3; i++) {
			position[0] = deviceWidth / 7;
			position[1] = deviceWidth / 7;

			mOtherCars[i] = new OtherCars();
			mOtherCars[i].setPosition(position);
		}

		position[0] = (int) (side + road / 4);
		position[1] = deviceHeight - 3 * size[1] - 5;
		mOtherCars[1].setPosition(position);


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
	}


	public void BackgroundDraw(Canvas canvas) {
		canvas.drawBitmap(background.background, 0, 0, null);
	}

	public void MyCarDraw(Canvas canvas) {
		mMyCar.setMyCar(size[0], size[1], true);
		canvas.drawBitmap(mMyCar.getMyCar(), (int)pos_mycar[0], (int)pos_mycar[1], null);
	}

	private void drawScenario1(Canvas canvas)
	{
		int[] temp = new int[2];
		boolean isDanger = false;

		// 화면 위로 올라가면 주위 차량 사라짐
		if (mOtherCars[1].getY() < 0) {
			position[0] = (int) (side + road / 4);
			position[1] = deviceHeight;
			mOtherCars[1].setPosition(position);

			// 시나리오2로 변경
			scenario = 2;
		}


		/* 위험 차량 감지 */
		if(mOtherCars[1].getY() > pos_mycar[1] - size[1] - 100 && mOtherCars[1].getY() < pos_mycar[1] + size[1] + 100)
			isDanger = true;

		/* 다른 차량 출력조건 만족시 출력 */
		mOtherCars[0].setOtherCars(size[0], size[1], false);
		canvas.drawBitmap(mOtherCars[0].getOtherCars(), mOtherCars[0].getX(), mOtherCars[0].getY(), null);

		mOtherCars[2].setOtherCars(size[0], size[1], false);
		canvas.drawBitmap(mOtherCars[2].getOtherCars(), mOtherCars[2].getX(), mOtherCars[2].getY(), null);

		if (mOtherCars[1].getY() < deviceHeight) {
			if(isDanger) mOtherCars[1].setOtherCars(size[0], size[1], isDanger);
			else mOtherCars[1].setOtherCars(size[0], size[1], isDanger);

			canvas.drawBitmap(mOtherCars[1].getOtherCars(), mOtherCars[1].getX(), mOtherCars[1].getY(), null);
		}
	}


	// 위험 메시지 전파 시나리오
	private void drawScenario2(Canvas canvas)
	{
		int[] temp = new int[2];
		boolean isDanger = false;

		/* 위험 차량 감지 */
		if(mOtherCars[1].getY() > pos_mycar[1] - size[1] - 100 && mOtherCars[1].getY() < pos_mycar[1] + size[1] + 100)
			isDanger = true;


		/* 다른 차량 출력조건 만족시 출력 */
		/* OtherCar[0] */
		mOtherCars[0].setOtherCars(size[0], size[1], false);
		canvas.drawBitmap(mOtherCars[0].getOtherCars(), mOtherCars[0].getX(), mOtherCars[0].getY(), null);

		/* OtherCar[2] */
		mOtherCars[2].setOtherCars(size[0], size[1], false);
		canvas.drawBitmap(mOtherCars[2].getOtherCars(), mOtherCars[2].getX(), mOtherCars[2].getY(), null);

		/* OtherCar[1] */
		if (mOtherCars[1].getY() < deviceHeight) {
			if(isDanger) {
				mOtherCars[1].setOtherCars(size[0], size[1], isDanger);
				canvas.drawBitmap(mOtherCars[1].getOtherCars(), mOtherCars[1].getX(), mOtherCars[1].getY(), null);
			}
			else {
				mOtherCars[1].setOtherCars(size[0], size[1], isDanger);
				canvas.drawBitmap(mOtherCars[1].getOtherCars(), mOtherCars[1].getX(), mOtherCars[1].getY(), null);
			}
		}

		/* OtherCar[1] */
		if (mOtherCars[1].getX() + size[0] - 30 > pos_mycar[0])
			isAccident = true;
	}

	
	/** CarThread class */
	class CarThread extends Thread {
		boolean canRun = true;

		public CarThread() {

		}

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

								// 위험 차량 감지 시나리오
								if(scenario == 1) {
									drawScenario1(canvas);

									/* OtherCar[1] */
									position[0] = (int) (side + road / 4);;
									position[1] = mOtherCars[1].getY() - 10;
									mOtherCars[1].setPosition(position);

								}
								// 사고 차량 감지 시나리오
								else if(scenario == 2) {
									drawScenario2(canvas);

									// 사고 날 경우
									if (isAccident) {
										if(loop < 3) {
											if (count != 40)
												count++;
											else {
												count = 1;
												loop++;
											}

											if (count > 20 && count < 40) {
												msg = BitmapFactory.decodeResource(CarView.mContext.getResources(), R.drawable.msg_emergency);

												// 좌우반전 이미지 효과 및 Bitmap 만들기
												Matrix sideInversion = new Matrix();
												sideInversion.setScale(-1, 1);
												Bitmap sideInversionImg = Bitmap.createBitmap(msg, 0, 0,  msg.getWidth(), msg.getHeight(), sideInversion, false);
												msg = Bitmap.createScaledBitmap(sideInversionImg, deviceWidth - 2 * (int) side, deviceHeight / 3, false);
												canvas.drawBitmap(msg, (int) side, (int) side, null);
											}

											position[0] = mOtherCars[1].getX();
											position[1] = mOtherCars[1].getY();
										}
										else {
											loop = 0;
											scenario = 1;
											isAccident = false;

											position[0] = (int) (side + road / 4);
											position[1] = deviceHeight;
										}

										mOtherCars[1].setPosition(position);
									}
									else {
										/* OtherCar[1] */
										// 위험 차량일 경우
										if(mOtherCars[1].getY() > pos_mycar[1] - size[1] - 100 && mOtherCars[1].getY() < pos_mycar[1] + size[1] + 100) {
											position[0] = mOtherCars[1].getX() + 10;
											position[1] = mOtherCars[1].getY() - 10;
										}
										else {
											position[0] = (int) (side + road / 4);
											position[1] = mOtherCars[1].getY() - 10;
										}

										mOtherCars[1].setPosition(position);
									}
								}

								/* OtherCar[0] */
								position[0] = (int) (deviceWidth - side - 3 * size[0] / 2);
								position[1] = (int) pos_mycar[1];
								mOtherCars[0].setPosition(position);


								/* OtherCar[2] */
								position[0] = (int) pos_mycar[0];
								position[1] = deviceHeight - 2 * size[1];
								mOtherCars[2].setPosition(position);

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