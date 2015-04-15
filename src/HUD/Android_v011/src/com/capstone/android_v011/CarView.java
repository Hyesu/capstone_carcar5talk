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

	public static int deviceWidth, deviceHeight; // 장비의 가로,세로길이

	private static MyCar mMyCar; 			// 사용자 차량
	private static OtherCars mOtherCars;	// 주위 차량
	private static Background background;	// 배경

	Random random = new Random();
	
	// 테스트용 변수
	public int thread_i = 0;

	int[] otherCarsX = new int[5];
	int[] otherCarsY = new int[5];

	// 주위에 있는 차량 출력시 사용됐다는걸 표시
	boolean[] otherCarsBoolean = new boolean[5];
	boolean otherCarsStart = false;

	public CarView(Context context) {
		super(context);

		// 생성시 서피스홀더와 컨텍스트 모두 생성. 그리기부분은 내부클래스로 이것들을 바로 가져다씀
		mHolder = getHolder();
		mHolder.addCallback(this);
		mContext = context;
		mThread = new CarThread();

		setFocusable(true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setWillNotDraw(false); // draw에서 그리도록 설정

		deviceWidth = getWidth();
		deviceHeight = getHeight();

		mMyCar = new MyCar(deviceWidth / 2, deviceHeight / 2); // 사용자의 자동차 위치
		mOtherCars = new OtherCars(100, 100); // 주위 차량 생성
		background = new Background(); // 배경 생성

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
	public void draw(Canvas canvas) {// 벌, 뱀이 출력된다고 알리는문구 출력
		super.draw(canvas);

		Paint paint = new Paint();
		paint.setColor(Color.GRAY);
		paint.setTextSize(70);

		if (otherCarsStart) // 각 boolean이 true가 되면 출력
			canvas.drawText("Other cars detected.", 100, 180, paint);
	}

	/** CarThread class */
	class CarThread extends Thread {
		boolean canRun = true;

		// 생성자
		public CarThread() {

		}

		/***************** 주위 차량 이미 출력 **********************/
		public void OtherCarsDraw(Canvas canvas) {
			int a = random.nextInt(100);
			switch (a) {
			case 0:
				otherCarsBoolean[0] = true; // true로 변하면 출력시작
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
				// 다른 차량 출력조건 만족시 출력
				if (otherCarsY[i] < deviceHeight && otherCarsBoolean[i])
					canvas.drawBitmap(mOtherCars.cars, otherCarsX[i],
							otherCarsY[i], null);

				// 화면 아래로 내려가면 주위 차량 사라짐
				if (otherCarsY[i] > deviceHeight) {
					int randomInt = random.nextInt(deviceWidth
							- mOtherCars.posY);
					otherCarsX[i] = randomInt; // x좌표 재설정

					otherCarsY[i] = -mOtherCars.posY; 	// 시작위치초기화
					otherCarsBoolean[i] = false; 		// 출력안되게 설정
				}
			}
		}

		/***************** 주위 차량 출력끝 **********************/

		// 이지미 출력시 출력된다는 구문을 띄우기 위한 핸들러
		// 핸들러 없이는 외부쓰레드라 invalidate가 적용이 안됨
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
				// canvas가 제대로 생성되지않아 nullpointer익셉션 발생
				if (canvas != null) {
					try {
						synchronized (viewLock) { // 동기화 유지
							try {
								/** 주위 차량 출력 구문 */
								Log.i("Thread", thread_i++ + " Thread is running.");
								
								OtherCarsDraw(canvas);
								/** 주위 차량 출력 끝 */

								// sleep(25); //속도 느리게 하려면 조절

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