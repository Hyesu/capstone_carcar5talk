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

	Random random = new Random();
	
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

		for (int i = 0; i < 5; i++) {
			int randomInt = random.nextInt(deviceWidth - mOtherCars.posX);

			otherCarsX[i] = randomInt;
			otherCarsBoolean[i] = false;
			otherCarsY[i] = -mOtherCars.posY;

		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mThread.canRun = false;
		((Activity) mContext).finish();
	}

	@Override
	public void draw(Canvas canvas) {// ��, ���� ��µȴٰ� �˸��¹��� ���
		super.draw(canvas);

		Paint paint = new Paint();
		paint.setColor(Color.GRAY);
		paint.setTextSize(70);

		if (otherCarsStart) // �� boolean�� true�� �Ǹ� ���
			canvas.drawText("Other cars detected.", 100, 180, paint);
	}

	/** CarThread class */
	class CarThread extends Thread {
		boolean canRun = true;

		// ������
		public CarThread() {

		}

		/***************** ���� ���� �̹� ��� **********************/
		public void OtherCarsDraw(Canvas canvas) {
			int a = random.nextInt(100);
			switch (a) {
			case 0:
				otherCarsBoolean[0] = true; // true�� ���ϸ� ��½���
				break;
			case 1:
				otherCarsBoolean[1] = true;
				break;
			case 2:
				otherCarsBoolean[2] = true;
				break;
			case 3:
				otherCarsBoolean[3] = true;
				break;
			case 4:
				otherCarsBoolean[4] = true;
				break;
			}

			for (int i = 0; i < 5; i++) {
				// �ٸ� ���� ������� ������ ���
				if (otherCarsY[i] < deviceHeight && otherCarsBoolean[i])
					canvas.drawBitmap(mOtherCars.cars, otherCarsX[i],
							otherCarsY[i], null);

				// ȭ�� �Ʒ��� �������� ���� ���� �����
				if (otherCarsY[i] > deviceHeight) {
					int randomInt = random.nextInt(deviceWidth
							- mOtherCars.posY);
					otherCarsX[i] = randomInt; // x��ǥ �缳��

					otherCarsY[i] = -mOtherCars.posY; 	// ������ġ�ʱ�ȭ
					otherCarsBoolean[i] = false; 		// ��¾ȵǰ� ����
				}
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
								
								OtherCarsDraw(canvas);
								/** ���� ���� ��� �� */

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