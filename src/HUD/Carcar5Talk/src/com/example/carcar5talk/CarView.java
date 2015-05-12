package com.example.carcar5talk;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class CarView extends SurfaceView implements Callback {
	private CarThread mThread;
	private SurfaceHolder mHolder;
	public static Context mContext;

	public static int deviceWidth, deviceHeight; // ����� ����,���α���

	private static MyCar mMyCar; 			// ����� ����
	private static OtherCars[] mOtherCars;	// ���� ����
	private static Background background; 	// ���

	private int numOtherCar;

	public CarView(Context context, int numCar) {
		super(context);
		
		numOtherCar = numCar;
		mOtherCars = new OtherCars[numOtherCar];

		// ������ ���ǽ�Ȧ���� ���ؽ�Ʈ ��� ����. �׸���κ��� ����Ŭ������ �̰͵��� �ٷ� �����پ�
		this.mContext = context;
		this.mHolder = getHolder();
		this.mHolder.addCallback(this);
		
		setFocusable(true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.mThread = new CarThread();
		
		setWillNotDraw(false); // draw���� �׸����� ����

		deviceWidth = getWidth();
		deviceHeight = getHeight();

		mMyCar = new MyCar(150, 150); 				// ������� �ڵ���
		mOtherCars[0] = new OtherCars(150, 150); 	// ���� ���� ����
		mOtherCars[1] = new OtherCars(150, 150);
		
		background = new Background(); 				// ��� ����

		try {
			mThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		mOtherCars[0].posX = 50;
		mOtherCars[0].posY = deviceHeight / 2;
		
		mOtherCars[1].posX = deviceWidth - 200;
		mOtherCars[1].posY = deviceHeight / 4;
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
	public void draw(Canvas canvas) {// ���� ������ ��µȴٰ� �˸��� ���� ���
		super.draw(canvas);

		Paint paint = new Paint();
		paint.setColor(Color.GRAY);
		paint.setTextSize(30);

		if (mOtherCars[0].isDetected)
			canvas.drawText("Other cars detected.", 10, 20, paint);
		else
			canvas.drawText("Nothing is detected.", 10, 20, paint);
		
		
	}

	// ��� �̹��� ���
	public void BackgroundDraw(Canvas canvas) {
		canvas.drawBitmap(background.background, 0, 0, null);
	}

	public void MyCarDraw(Canvas canvas) {
		canvas.drawBitmap(mMyCar.myCar, deviceWidth / 3 + 42, deviceHeight / 2, null);
	}

	public void OtherCarsDraw(Canvas canvas) {
		// �ٸ� ���� ������� ������ ���
		if (mOtherCars[0].posY < deviceHeight) {
			canvas.drawBitmap(mOtherCars[0].cars, mOtherCars[0].posX, mOtherCars[0].posY, null);
			mOtherCars[0].isDetected = true;
		}

		// ȭ�� ���� �ö󰡸� ���� ���� �����
		if (mOtherCars[0].posY < 0) {
			mOtherCars[0].posY = deviceHeight; // ���� ��ġ �ʱ�ȭ
			mOtherCars[0].isDetected = false; // ��� �ȵǰ� ����
		}
		
		
		// �ٸ� ���� ������� ������ ���
		if (mOtherCars[1].posY < deviceHeight) {
			canvas.drawBitmap(mOtherCars[1].cars, mOtherCars[1].posX, mOtherCars[1].posY, null);
			mOtherCars[1].isDetected = true;
		}

		// ȭ�� ���� �ö󰡸� ���� ���� �����
		if (mOtherCars[1].posY < 0) {
			mOtherCars[1].posY = deviceHeight; // ���� ��ġ �ʱ�ȭ
			mOtherCars[1].isDetected = false; // ��� �ȵǰ� ����
		}		
	}

	/** CarThread class */
	class CarThread extends Thread {
		boolean canRun = true;

		// ������
		public CarThread() {

		}

		// �̹��� ��½� ��µƴٴ� ������ ���� ���� �ڵ鷯
		// �ڵ鷯�� ������ �ܺξ������ invalidate ���� �ȵ�
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
				// canvas�� ����� ���������ʾ� ������Ʈ �ͼ��� �߻�
				if (canvas != null) {
					try {
						synchronized (viewLock) { // ����ȭ ����
							try {
								BackgroundDraw(canvas); 	// ��� ���
								MyCarDraw(canvas); 		// ����� ���� ���
								OtherCarsDraw(canvas); 	// ���� ���� ���
	
								mOtherCars[0].posY--;
								mOtherCars[1].posY -= 3;
								
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