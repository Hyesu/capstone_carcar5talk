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

		/* Creat background object */
		this.background = new Background();

		/* Creat my car object and Set my car size */
		this.mMyCar = Carcar5Talk.mContainer.getMyCar();
		position[0] = 150; position[1] = 150;
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
		/* Center of display */
		canvas.drawBitmap(mMyCar.getMyCar(), deviceWidth / 2 - 80, deviceHeight / 3, null);
	}


	public void OtherCarsDraw(Canvas canvas) {
		/* Creat other cars object and Set other cars size */
		this.mOtherCars = Carcar5Talk.mContainer.getOtherCars();
		position[0] = 150; position[1] = 150;
		for(int i = 0; i < mOtherCars.size(); i++) {
			mOtherCars.get(i).setPosition(position);
			mOtherCars.get(i).setOtherCars();
		}

		position[0] = deviceWidth / 2 - 80;
		position[1] = deviceHeight / 3;

		for (int i = 0; i < mOtherCars.size(); i++) {
			canvas.drawBitmap(mOtherCars.get(i).getOtherCars(), (int) (position[0] + (int) mOtherCars.get(i).getX()), (int) (position[1] + (int) mOtherCars.get(i).getY()), null);

//				Log.d(TAG + i + "'s X", (int) (position[0] + mOtherCars.get(i).getX()) + "");
//				Log.d(TAG + i + "'s Y", (int) (position[1] + mOtherCars.get(i).getY()) + "");

			Log.d(TAG + i + "'s Y", (int) mOtherCars.get(i).getX() + "");
			Log.d(TAG + i + "'s X", (int) mOtherCars.get(i).getY() + "");
		}



//		05-21 21:28:03.047  15789-16265/com.example.carcar5talk D/[ComputePosition] 0's Y﹕ -22.0
//		05-21 21:28:03.047  15789-16265/com.example.carcar5talk D/[ComputePosition] 0's X﹕ 22.0
//		05-21 21:28:03.047  15789-16265/com.example.carcar5talk D/[ComputePosition] 1's Y﹕ -187.0
//		05-21 21:28:03.047  15789-16265/com.example.carcar5talk D/[ComputePosition] 1's X﹕ 187.0
//		05-21 21:28:03.047  15789-16265/com.example.carcar5talk D/[CarView]0's X﹕ 258
//		05-21 21:28:03.047  15789-16265/com.example.carcar5talk D/[CarView]0's Y﹕ 400
//		05-21 21:28:03.047  15789-16265/com.example.carcar5talk D/[CarView]1's X﹕ 93
//		05-21 21:28:03.047  15789-16265/com.example.carcar5talk D/[CarView]1's Y﹕ 565



//		else {
//			canvas.drawBitmap(mOtherCars.get(0).getOtherCars(), deviceWidth - 215, deviceHeight - 215, null);
//			canvas.drawBitmap(mOtherCars.get(1).getOtherCars(), deviceWidth - 215, deviceHeight - 215, null);
//		}
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
									Log.d(TAG + " Before mContainer Flag", Carcar5Talk.mContainer.getFlag() + "");

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