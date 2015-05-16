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

	/* Extra variable */
	public static int deviceWidth, deviceHeight;
	private int[] position;
	
	
	private static MyCar mMyCar;
	private static OtherCars[] mOtherCars;
	private static Background background;
	private int numOfCars;
	
	//////////////////////////////////////////////////////
	// ����
	//////////////////////////////////////////////////////
	
	//private ArrayList<OtherCars> mOtherCars;
	
	//////////////////////////////////////////////////////
	// ����
	//////////////////////////////////////////////////////

	

	public CarView(Context context) {
		super(context);

		mOtherCars = new OtherCars[2];
		position = new int[2];
		
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

		this.deviceWidth = getWidth();
		this.deviceHeight = getHeight();

		
		mMyCar = new MyCar();
		position[0] = 150; position[1] = 150;
		mMyCar.setPosition(position);
		mMyCar.setMyCar();
		
		mOtherCars[0] = new OtherCars();
		position[0] = 150; position[1] = 150;
		mOtherCars[0].setPosition(position);
		mOtherCars[0].setOtherCars();
		
		mOtherCars[1] = new OtherCars();
		position[0] = 150; position[1] = 150;
		mOtherCars[1].setPosition(position);
		mOtherCars[1].setOtherCars();
		
		background = new Background(); 								

		//////////////////////////////////////////////////////
		// ����
		//////////////////////////////////////////////////////
		
		
		
//		/* Creat my car object */
//		this.mMyCar = mContainer.getMyCar();
//		this.numOfCars = mMyCar.getNumOfCars();
//		
//		/* Creat other cars object */
//		this.mOtherCars = mContainer.getOtherCars();
//		
//		/* Creat background object */
//		this.background = new Background();
//		
//		/* Update information of cars */
//		mContainer.parseData();
		
		
		
		//////////////////////////////////////////////////////
		// ����
		//////////////////////////////////////////////////////
		
		
		
		try {
			mThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		position[0] = 50; position[1] = deviceHeight / 2; 
		mOtherCars[0].setPosition(position);
		
		position[0] = deviceWidth - 200; position[1] = deviceHeight / 4;
		mOtherCars[1].setPosition(position);
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
		
		////////////////////////////////////////////////////////
		// myCar ��ġ ����
		
		//////////////////////////////////////////////////////
		// ����
		//////////////////////////////////////////////////////
		
		
		canvas.drawBitmap(mMyCar.getMyCar(), deviceWidth / 3 + 42, deviceHeight / 2, null);
	}

	public void OtherCarsDraw(Canvas canvas) {
		// //////////////////////////////////////////////////////
		// mOtherCars ��ġ ����
		
		//////////////////////////////////////////////////////
		// ����
		//////////////////////////////////////////////////////
		
		
		// �ٸ� ���� ������� ������ ���
		if (mOtherCars[0].getY() < deviceHeight) {
			canvas.drawBitmap(mOtherCars[0].getOtherCars(), mOtherCars[0].getX(), mOtherCars[0].getY(), null);
			mOtherCars[0].isDetected = true;
		}

		// ȭ�� ���� �ö󰡸� ���� ���� �����
		if (mOtherCars[0].getY() < 0) {
			position[0] = 50; position[1] = deviceHeight;		// ���� ��ġ �ʱ�ȭ
			mOtherCars[0].setPosition(position);
			mOtherCars[0].isDetected = false; // ��� �ȵǰ� ����
		}
		
		
		// �ٸ� ���� ������� ������ ���
		if (mOtherCars[1].getY() < deviceHeight) {
			canvas.drawBitmap(mOtherCars[1].getOtherCars(), mOtherCars[1].getX(), mOtherCars[1].getY(), null);
			mOtherCars[1].isDetected = true;
		}

		// ȭ�� ���� �ö󰡸� ���� ���� �����
		if (mOtherCars[1].getY() < 0) {
			position[0] = deviceWidth - 200; position[1] = deviceHeight;
			mOtherCars[1].setPosition(position); // ���� ��ġ �ʱ�ȭ
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
			Canvas canvas = null; 			// canvas �����
			
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

								position[0] = 50; position[1] = mOtherCars[0].getY() - 1;
								mOtherCars[0].setPosition(position);
								
								
								position[0] = deviceWidth - 200; position[1] = mOtherCars[1].getY() - 3;
								mOtherCars[1].setPosition(position);
	
								
								//////////////////////////////////////////////////////
								// ����
								//////////////////////////////////////////////////////
								
								
//								if(Carcar5Talk.mContainer != null)
//									Log.d(TAG + " rawData", Carcar5Talk.mContainer.getRawData());						
								

								//////////////////////////////////////////////////////
								// ����
								//////////////////////////////////////////////////////
								
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