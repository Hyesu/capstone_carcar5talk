package com.example.android_b_v011;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
 
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
 
public class MainActivity extends Activity {
	
	/*
	 *  TCP 소켓과 유사한 블루투스 소켓에 대한 인터페이스를 나타내며 여기서 제공되는 InputStream과 OutputStream을 통해
	 *  안드로이드 앱이 다른 블루투스 장비와 함께 데이터를 교환할 수 있도록 하는 연결점이다.
	 */
	BluetoothSocket mmSocket;
	
	/*
	 * 원격 블루투스 장비를 나타내며 여기서 제공하는 BluetoothSocket을 이요하여 연결된 장비에게 요청 혹은
	 * name, address, class, bonding 상태 등의 정보를 질의한다.
	 */
	BluetoothDevice mmDevice = null;
	
	final byte delimiter = 33;
	int readBufferPosition = 0;
	
	/*
	 * 
	 * .createRfcommSocketToServiceRecord()
	 * BluetoothDevice에 연결할 수 있도록 BluetoothSocket을 초기화하며 인자로 전달하는 값은 서버에서
	 * BluetoothSocket을 오픈할 때 사용했던 UUID와 일치해야 한다.
	 * 
	 * .connect()
	 * 시스템이 UUID를 매치시키기 위하여 원격 장비의 SDP를 조회한다.
	 * Success : 조회되면 원격 장비는 연결을 수락하고 연결되어 있는 동안 사용할 수 있는 RFCOMM 채널을 공유하고 메소드를 마친다.
	 * Fail or Timeout : 예외 처리(IOException). 
	 */
	public void sendBtMsg(String msg2send){
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Standard SerialPortServiceClass_UUID
        //UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"); //Standard SerialPortService ID
        try {
        	mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        	if (!mmSocket.isConnected()){
        		mmSocket.connect();			// 연결 시도
        	}
			
			String msg = msg2send;
	        //msg += "\n";
	        OutputStream mmOutputStream = mmSocket.getOutputStream();
	        mmOutputStream.write(msg.getBytes());
			
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final Handler handler = new Handler();
		
		final TextView myLabel = (TextView) findViewById(R.id.btResult);
		final Button connectButton = (Button) findViewById(R.id.connectButton);
		
		/*
		 * - BluetoothAdapter
		 * 로컬 블루투스 어댑터(Bluetooth radio)를 나타낸다. 모든 블루투스 블루투스 연동을 위한 entry-point로서
		 * 이것을 사용하여 다른 블루투스 장비들을 찾고, 페어링된 장비들을 질의하고 알고있는 MAC 주소를 이용하여 
		 * BluetoothDevice를 인스턴스화하며 다른 장비로부터 통신을 듣기 위해 BluetoothServerSocket을 생성한다.
		 * 
		 * .getDefaultAdapter()
		 * 장비가 소유하고 있는 블루투스 어댑터에 대한 BluetoothAdapter 객체를 반환한다.
		 * 만약 null이 반환될 경우, 장비는 블루투스를 지원하지 않는 것이다.
		 * 
		 * .read(byte[])
		 * 스트림으로부터 읽기를 마칠 때까지 블로킹된다.
		 * 
		 * .write(byte[])
		 * 보통 블로킹되지 않지만 만약 원격 장비에서 충분히 빠르게 read(byte[]) 메소드를 호출하지 않고
		 * 버퍼가 꽉 찰 경우 흐름에 따라 블로킹될 수 있다.
		 */
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		final class workerThread implements Runnable {
			private String btMsg;
			public workerThread(String msg) {
				btMsg = msg;
			}

			public void run() {
				sendBtMsg(btMsg);
				
				while (!Thread.currentThread().isInterrupted()) {
					int bytesAvailable;
					boolean workDone = false;

					try {
						final InputStream mmInputStream;
						mmInputStream = mmSocket.getInputStream();
						bytesAvailable = mmInputStream.available();
						if (bytesAvailable > 0) {
							byte[] packetBytes = new byte[bytesAvailable];
							Log.e("PacketBytes", "bytes available");
							
							byte[] readBuffer = new byte[1024];
							mmInputStream.read(packetBytes);

							for (int i = 0; i < bytesAvailable; i++) {
								byte b = packetBytes[i];
								if (b == delimiter) {
									byte[] encodedBytes = new byte[readBufferPosition];
									System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
									
									final String data = new String(encodedBytes, "US-ASCII");
									readBufferPosition = 0;

									// The variable data now contains our full command
									handler.post(new Runnable() {
										public void run() {
											myLabel.setText(data);
										}
									});

									workDone = true;
									break;

								} else {
									readBuffer[readBufferPosition++] = b;
								}
							}

							if (workDone == true) {
								mmSocket.close();
								break;
							}

						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
		;
	    
		
		// connect button handler
		connectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click
            	
            	(new Thread(new workerThread("Connecting..."))).start();
            	
            }
        });
		

		
		
		/*
		 * .isEnabled()
		 * 현재 블루투스가 활성화된 상태인지를 확인(true: 활성화 상태)
		 * 
		 * ACTION_REQUEST_ENABLE
		 * 비활성화 상태인 블루투스를 활성화 상태로 변경하기 위한 옵션
		 * 
		 * startActivityForResult()
		 * 앱의 중단없이 시스템의 설정을 통해 블루투스를 활성화시킨다.
		 */
		if(!mBluetoothAdapter.isEnabled()) {
		   Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		   startActivityForResult(enableBluetooth, 0);
		}
		
		
		
		
		/*
		 * .getBondedDevices()
		 * 장비 탐색을 진행하기에 앞서 만약 연결하고자 하는 장비가 이미 페어링된 것인지를 페어링된 장비들의
		 * 집합을 질의할 필요가 있다. 
		 * return value : 페어링된 장비들을 나타내는 BluetoothDevice들의 집합
		 * 
		 *  Set<BluetoothDevice>
		 *  모든 페어링된 장비들을 질의하고 사용자에게 ArrayAdapter를 이용하여 장비의 이름을 보여준다.
		 */
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
       
		if(pairedDevices.size() > 0) {
			for(BluetoothDevice device : pairedDevices) {
				// sudo hciconfig hci0 name 'raspberrypi-0'
				if(device.getName().equals("raspberrypi-0")) {
					mmDevice = device;
					Log.e("Paired Device.", device.getName());
					
					break;
                }
            }
        }
        
        
	}
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
 
}