package com.example.carcar5talk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

	public static int deviceWidth, deviceHeight; // 장비의 가로,세로길이

	private static MyCar mMyCar; 			// 사용자 차량
	private static OtherCars[] mOtherCars;	// 주위 차량
	private static Background background; 	// 배경

	private int numOtherCar;

	public CarView(Context context, int numCar) {
		super(context);
		
		numOtherCar = numCar;
		mOtherCars = new OtherCars[numOtherCar];

		// 생성시 서피스홀더와 컨텍스트 모두 생성. 그리기부분은 내부클래스로 이것들을 바로 가져다씀
		this.mContext = context;
		this.mHolder = getHolder();
		this.mHolder.addCallback(this);
		this.mThread = new CarThread();
		
		
		setFocusable(true);
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setWillNotDraw(false); // draw에서 그리도록 설정

		deviceWidth = getWidth();
		deviceHeight = getHeight();

		mMyCar = new MyCar(150, 150); 				// 사용자의 자동차
		mOtherCars[0] = new OtherCars(150, 150); 	// 주위 차량 생성
		mOtherCars[1] = new OtherCars(150, 150);
		
		background = new Background(); 				// 배경 생성

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
		((Activity) mContext).finish();
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
		canvas.drawBitmap(mMyCar.myCar, deviceWidth / 3 + 42, deviceHeight / 2, null);
	}

	public void OtherCarsDraw(Canvas canvas) {
		// 다른 차량 출력조건 만족시 출력
		if (mOtherCars[0].posY < deviceHeight) {
			canvas.drawBitmap(mOtherCars[0].cars, mOtherCars[0].posX, mOtherCars[0].posY, null);
			mOtherCars[0].isDetected = true;
		}

		// 화면 위로 올라가면 주위 차량 사라짐
		if (mOtherCars[0].posY < 0) {
			mOtherCars[0].posY = deviceHeight; // 시작 위치 초기화
			mOtherCars[0].isDetected = false; // 출력 안되게 설정
		}
		
		
		// 다른 차량 출력조건 만족시 출력
		if (mOtherCars[1].posY < deviceHeight) {
			canvas.drawBitmap(mOtherCars[1].cars, mOtherCars[1].posX, mOtherCars[1].posY, null);
			mOtherCars[1].isDetected = true;
		}

		// 화면 위로 올라가면 주위 차량 사라짐
		if (mOtherCars[1].posY < 0) {
			mOtherCars[1].posY = deviceHeight; // 시작 위치 초기화
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
			Canvas canvas = null; // canvas 만들기

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

								mOtherCars[0].posY--;
								mOtherCars[1].posY -= 3;
								
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