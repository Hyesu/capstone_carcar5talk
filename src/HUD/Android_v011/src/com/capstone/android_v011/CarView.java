package com.capstone.android_v011;

import java.util.Random;

import android.app.Activity;
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
	private static CarThread mThread;
	private static SurfaceHolder mHolder;
	public static Context mContext;

	public static int deviceWidth, deviceHeight; // ����� ����,���α���

	private static MyCar mMyCar; 			// ����� ����
	private static OtherCars mOtherCars;	// ���� ����
	private static Background background;	// ���
	
	// �׽�Ʈ�� ����
	public int thread_i = 0;

	int[] otherCarsX = new int[5];
	int[] otherCarsY = new int[5];

	// ������ �ִ� ���� ��½� ���ƴٴ°� ǥ��
	boolean[] otherCarsBoolean = new boolean[5];
	boolean otherCarsStart = false;
	
	
	
	public CarView(Context context) {
		super(context);

		// ������ ���ǽ�Ȧ���� ���ؽ�Ʈ ��� ����. �׸���κ��� ����Ŭ������ �̰͵��� �ٷ� �����پ�
		mHolder = getHolder();
		mHolder.addCallback(this);
		mContext = context;
		mThread = new CarThread();

		setFocusable(true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setWillNotDraw(false); // draw���� �׸����� ����

		deviceWidth = getWidth();
		deviceHeight = getHeight();

		mMyCar = new MyCar(deviceWidth / 2, deviceHeight / 2); // ������� �ڵ��� ��ġ
		mOtherCars = new OtherCars(100, 100); // ���� ���� ����
		background = new Background(); // ��� ����

		try {
			mThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		otherCarsStart = true;
		otherCarsX[0] = deviceWidth / 6;
		otherCarsY[0] = deviceWidth / 2;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mThread.canRun = false;
		((Activity) mContext).finish();
	}

	@Override
	public void draw(Canvas canvas) {// ���� ������ ��µȴٰ� �˸��� ���� ���
		super.draw(canvas);

		Paint paint = new Paint();
		paint.setColor(Color.GRAY);
		paint.setTextSize(30);
		
		
		
		if (otherCarsBoolean[0]) // �� boolean�� true�� �Ǹ� ���
			canvas.drawText("Other cars detected.", 10, 10, paint);
	}

	
	// ��� �̹��� ���
	public void BackgroundDraw(Canvas canvas){
		canvas.drawBitmap(background.background, 0, 0, null);
	}
	
	
	public void MyCarDraw(Canvas canvas) {
		canvas.drawBitmap(mMyCar.myCar, deviceWidth / 2, deviceHeight / 2, null);
	}
	
	
	
	/** CarThread class */
	class CarThread extends Thread {
		boolean canRun = true;

		// ������
		public CarThread() {

		}

		/***************** ���� ���� �̹� ��� **********************/
		public void OtherCarsDraw(Canvas canvas) {
			// �ٸ� ���� ������� ������ ���
			if (otherCarsY[0] < deviceHeight)
				canvas.drawBitmap(mOtherCars.cars, otherCarsX[0], otherCarsY[0], null);

			// ȭ�� �Ʒ��� �������� ���� ���� �����
			if (otherCarsY[0] > deviceHeight) {
				otherCarsY[0] = -mOtherCars.posY; 	// ������ġ�ʱ�ȭ
				otherCarsBoolean[0] = false; 		// ��¾ȵǰ� ����
				otherCarsStart = false;
			}
		}

		/***************** ���� ���� ��³� **********************/

		// ������ ��½� ��µȴٴ� ������ ���� ���� �ڵ鷯
		// �ڵ鷯 ���̴� �ܺξ������ invalidate�� ������ �ȵ�
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				invalidate(); // draw repaint
			}
		};

		// ��� Thread ��ü
		public void run() {
			Object viewLock = new Object(); // ����ȭ��
			Canvas canvas = null; // canvas �����
			
			while (canRun) {
				canvas = mHolder.lockCanvas(); // canvas��װ� ���� �Ҵ�
				
				// nullüũ�� ���ϸ� destroy�� canRun�� false�� �� �� ���ѹ� ����Ǵ¼���
				// canvas�� ����� ���������ʾ� nullpointer�ͼ��� �߻�
				if (canvas != null) {
					try {
						synchronized (viewLock) { // ����ȭ ����
							try {
								/** ���� ���� ��� ���� */
								Log.i("Thread", thread_i++ + " Thread is running.");
								
								BackgroundDraw(canvas);
								MyCarDraw(canvas);
								OtherCarsDraw(canvas);
								/** ���� ���� ��� �� */
								
								//otherCarsX[0]++;
								otherCarsY[0]++;

								// sleep(25); //�ӵ� ������ �Ϸ��� ����

							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					} finally { // �����۾��� ������
						if (canvas != null) // ���� ������ view�� ����
							mHolder.unlockCanvasAndPost(canvas);
					}
				}
			}// while
		}// run
	}
	/** CarThread class end */

}// SurfaceView