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
	// 수정
	//////////////////////////////////////////////////////
	
	//private ArrayList<OtherCars> mOtherCars;
	
	//////////////////////////////////////////////////////
	// 수정
	//////////////////////////////////////////////////////

	

	public CarView(Context context) {
		super(context);

		mOtherCars = new OtherCars[2];
		position = new int[2];
		
		// 생성시 서피스홀더와 컨텍스트 모두 생성. 그리기부분은 내부클래스로 이것들을 바로 가져다씀
		this.mContext = context;
		this.mHolder = getHolder();
		this.mHolder.addCallback(this);
		
		setFocusable(true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.mThread = new CarThread();
		
		setWillNotDraw(false); // draw에서 그리도록 설정

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
		// 수정
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
		// 수정
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
	public void draw(Canvas canvas) {// 주위 차량이 출력된다고 알리는 문구 출력
		super.draw(canvas);

		Paint paint = new Paint();
		paint.setColor(Color.GRAY);
		paint.setTextSize(30);

		if (mOtherCars[0].isDetected)
			canvas.drawText("Other cars detected.", 10, 20, paint);
		else
			canvas.drawText("Nothing is detected.", 10, 20, paint);
	}

	// 배경 이미지 출력
	public void BackgroundDraw(Canvas canvas) {
		canvas.drawBitmap(background.background, 0, 0, null);
	}

	public void MyCarDraw(Canvas canvas) {
		
		////////////////////////////////////////////////////////
		// myCar 위치 설정
		
		//////////////////////////////////////////////////////
		// 수정
		//////////////////////////////////////////////////////
		
		
		canvas.drawBitmap(mMyCar.getMyCar(), deviceWidth / 3 + 42, deviceHeight / 2, null);
	}

	public void OtherCarsDraw(Canvas canvas) {
		// //////////////////////////////////////////////////////
		// mOtherCars 위치 설정
		
		//////////////////////////////////////////////////////
		// 수정
		//////////////////////////////////////////////////////
		
		
		// 다른 차량 출력조건 만족시 출력
		if (mOtherCars[0].getY() < deviceHeight) {
			canvas.drawBitmap(mOtherCars[0].getOtherCars(), mOtherCars[0].getX(), mOtherCars[0].getY(), null);
			mOtherCars[0].isDetected = true;
		}

		// 화면 위로 올라가면 주위 차량 사라짐
		if (mOtherCars[0].getY() < 0) {
			position[0] = 50; position[1] = deviceHeight;		// 시작 위치 초기화
			mOtherCars[0].setPosition(position);
			mOtherCars[0].isDetected = false; // 출력 안되게 설정
		}
		
		
		// 다른 차량 출력조건 만족시 출력
		if (mOtherCars[1].getY() < deviceHeight) {
			canvas.drawBitmap(mOtherCars[1].getOtherCars(), mOtherCars[1].getX(), mOtherCars[1].getY(), null);
			mOtherCars[1].isDetected = true;
		}

		// 화면 위로 올라가면 주위 차량 사라짐
		if (mOtherCars[1].getY() < 0) {
			position[0] = deviceWidth - 200; position[1] = deviceHeight;
			mOtherCars[1].setPosition(position); // 시작 위치 초기화
			mOtherCars[1].isDetected = false; // 출력 안되게 설정
		}		
	}

	
	
	/** CarThread class */
	class CarThread extends Thread {
		boolean canRun = true;

		// 생성자
		public CarThread() {

		}

		// 이미지 출력시 출력됐다는 구문을 띄우기 위한 핸들러
		// 핸들러가 없으면 외부쓰레드라 invalidate 적용 안됨
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				invalidate(); // draw repaint
			}
		};

		// 출력 Thread 본체
		public void run() {
			Object viewLock = new Object(); // 동기화용
			Canvas canvas = null; 			// canvas 만들기
			
			while (canRun) {		
				canvas = mHolder.lockCanvas(); // canvas잠그고 버퍼 할당
	
				// null체크를 안하면 destroy로 canRun이 false가 될 때 단한번 실행되는순간
				// canvas가 제대로 생성되지않아 널포인트 익셉션 발생
				if (canvas != null) {
					try {
						synchronized (viewLock) { // 동기화 유지
							try {
								BackgroundDraw(canvas); 	// 배경 출력
								MyCarDraw(canvas); 		// 사용자 차량 출력
								OtherCarsDraw(canvas); 	// 주위 차량 출력

								position[0] = 50; position[1] = mOtherCars[0].getY() - 1;
								mOtherCars[0].setPosition(position);
								
								
								position[0] = deviceWidth - 200; position[1] = mOtherCars[1].getY() - 3;
								mOtherCars[1].setPosition(position);
	
								
								//////////////////////////////////////////////////////
								// 수정
								//////////////////////////////////////////////////////
								
								
//								if(Carcar5Talk.mContainer != null)
//									Log.d(TAG + " rawData", Carcar5Talk.mContainer.getRawData());						
								

								//////////////////////////////////////////////////////
								// 수정
								//////////////////////////////////////////////////////
								
							} catch (Exception e) {
								e.printStackTrace();
							}
	
						}
					} finally { // 버퍼작업이 끝나면
						if (canvas != null) // 버퍼 내용을 view에 전송
							mHolder.unlockCanvasAndPost(canvas);
					}
				
				}
			}// while
		}// run
	}
	/** CarThread class end */

	
}// SurfaceView