package com.carcar5talk.bluetooth;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.carcar5talk.surfaceview.CarView;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for incoming
 * connections, a thread for connecting with a device, and a thread for
 * performing data transmissions when connected.
 */
public class BluetoothChatService {
	// Debugging
	private static final String TAG = "[BluetoothChatService]";
	private static final boolean D = true;

	// Name for the SDP record when creating server socket
	private static final String NAME_SECURE = "BluetoothChatSecure";

	// SerialPortServiceClass_UUID
	private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

	// Member fields
	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private AcceptThread mSecureAcceptThread;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private int mState;
//	private Timer mTimer;
//	private TimerTask mTask;

	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0; // we're doing nothing
	public static final int STATE_LISTEN = 1; // now listening for incoming connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
	public static final int STATE_CONNECTED = 3; // now connected to a remote device

	// Extra variable



	/**
	 * Constructor. Prepares a new BluetoothChat session.
	 * 
	 * @param context
	 *            The UI Activity Context
	 * @param handler
	 *            A Handler to send messages back to the UI Activity
	 */
	public BluetoothChatService(Context context, Handler handler) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		mHandler = handler;
	}

	/**
	 * Set the current state of the chat connection
	 *
	 * @param state
	 *            An integer defining the current connection state
	 */
	private synchronized void setState(int state) {
		if (D)
			Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;

		// Give the new state to the Handler so the UI Activity can update
		mHandler.obtainMessage(Carcar5Talk.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
	}

	/**
	 * Return the current connection state.
	 */
	public synchronized int getState() {
		return mState;
	}

	/**
	 * Start the chat service. Specifically start AcceptThread to begin a
	 * session in listening (server) mode. Called by the Activity onResume()
	 */
	public synchronized void start() {
		if (D)
			Log.d(TAG, "start");

		// Cancel any thread attempting to make a connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		setState(STATE_LISTEN);

		// Start the thread to listen on a BluetoothServerSocket
		if (mSecureAcceptThread == null) {
			mSecureAcceptThread = new AcceptThread(true);
			mSecureAcceptThread.start();
		}
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 * 
	 * @param device
	 *            The BluetoothDevice to connect
	 * @param secure
	 *            Socket Security type - Secure (true) , Insecure (false)
	 */
	public synchronized void connect(BluetoothDevice device, boolean secure) {
		if (D)
			Log.d(TAG, "connect to: " + device);

		// Cancel any thread attempting to make a connection
		if (mState == STATE_CONNECTING) {
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(device, secure);
		mConnectThread.start();

		setState(STATE_CONNECTING);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * 
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {
		if (D)
			Log.d(TAG, "connected, Socket Type:" + socketType);

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Cancel the accept thread because we only want to connect to one
		// device
		if (mSecureAcceptThread != null) {
			mSecureAcceptThread.cancel();
			mSecureAcceptThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket, socketType);
		mConnectedThread.start();

		// Send the name of the connected device back to the UI Activity
		Message msg = mHandler.obtainMessage(Carcar5Talk.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(Carcar5Talk.DEVICE_NAME, device.getName());
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		setState(STATE_CONNECTED);

		// Send 'SYN' packet
		//write("SYN".getBytes());
	}

	/**
	 * Stop all threads
	 */
	public synchronized void stop() {
		if (D)
			Log.d(TAG, "stop");

		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		if (mSecureAcceptThread != null) {
			mSecureAcceptThread.cancel();
			mSecureAcceptThread = null;
		}

		setState(STATE_NONE);
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 * 
	 * @param out
	 *            The bytes to write
	 * @see ConnectedThread#write(byte[])
	 */
	public void write(byte[] out) {
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (mState != STATE_CONNECTED)
				return;
			r = mConnectedThread;
		}
		// Perform the write unsynchronized
		r.write(out);
	}

	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	private void connectionFailed() {
		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(Carcar5Talk.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(Carcar5Talk.TOAST, "Unable to connect device");
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		// Start the service over to restart listening mode
		BluetoothChatService.this.start();
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	private void connectionLost() {
		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(Carcar5Talk.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(Carcar5Talk.TOAST, "Device connection was lost");
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		// Start the service over to restart listening mode
		BluetoothChatService.this.start();
	}

	/**
	 * This thread runs while listening for incoming connections. It behaves
	 * like a server-side client. It runs until a connection is accepted (or
	 * until cancelled).
	 */
	private class AcceptThread extends Thread {
		// The local server socket
		private final BluetoothServerSocket mmServerSocket;
		private String mSocketType;

		public AcceptThread(boolean secure) {
			BluetoothServerSocket tmp = null;
			mSocketType = "Secure";

			// Create a new listening server socket
			try {
				if (secure)
					tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
			} catch (IOException e) {
				Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
			}
			mmServerSocket = tmp;
		}

		@Override
		public void run() {
			if (D)
				Log.d(TAG, "Socket Type: " + mSocketType
						+ "BEGIN mAcceptThread" + this);
			setName("AcceptThread" + mSocketType);

			BluetoothSocket socket = null;

			// Listen to the server socket if we're not connected
			while (mState != STATE_CONNECTED) {
				try {
					// This is a blocking call and will only return on a
					// successful connection or an exception
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					Log.e(TAG, "Socket Type: " + mSocketType
							+ "accept() failed", e);
					break;
				}

				// If a connection was accepted
				if (socket != null) {
					synchronized (BluetoothChatService.this) {
						switch (mState) {
						case STATE_LISTEN:
						case STATE_CONNECTING:
							// Situation normal. Start the connected thread.
							connected(socket, socket.getRemoteDevice(), mSocketType);
							break;
						case STATE_NONE:
						case STATE_CONNECTED:
							// Either not ready or already connected. Terminate new socket.
							try {
								Log.d(TAG, "socket close");

								socket.close();
							} catch (IOException e) {
								Log.e(TAG, "Could not close unwanted socket", e);
							}
							break;
						}
					}
				}
			}

			if (D)
				Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);
		}

		public void cancel() {
			if (D)
				Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
			try {
				mmServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "Socket Type" + mSocketType
						+ "close() of server failed", e);
			}
		}
	}

	/**
	 * This thread runs while attempting to make an outgoing connection with a
	 * device. It runs straight through; the connection either succeeds or
	 * fails.
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		private String mSocketType;

		public ConnectThread(BluetoothDevice device, boolean secure) {
			mmDevice = device;
			BluetoothSocket tmp = null;
			mSocketType = "Secure";

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				if (secure) {
					tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
				}
			} catch (IOException e) {
				Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
			}
			mmSocket = tmp;
		}

		@Override
		public void run() {
			Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
			setName("ConnectThread" + mSocketType);

			// Always cancel discovery because it will slow down a connection
			mAdapter.cancelDiscovery();

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a successful
				// connection or an exception
				mmSocket.connect();
			} catch (IOException e) {
				// Close the socket
				try {
					mmSocket.close();
				} catch (IOException e2) {
					Log.e(TAG, "unable to close() " + mSocketType + " socket during connection failure", e2);
				}
				connectionFailed();
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (BluetoothChatService.this) {
				mConnectThread = null;
			}

			// Start the connected thread
			connected(mmSocket, mmDevice, mSocketType);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect " + mSocketType
						+ " socket failed", e);
			}
		}
	}


	/**
	 * This thread runs during a connection with a remote device. It handles all
	 * incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket, String socketType) {
			Log.d(TAG, "create ConnectedThread: " + socketType);
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			}
			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}


		public void run() {
			int ret;
			byte[] bytes;
			Log.i(TAG, "BEGIN mConnectedThread");

			// Keep listening to the InputStream while connected
			while (true) {
				try {
					Log.d(TAG, "************************ ConnectThread is Running ************************");
					bytes = new byte[1000];

//					mTask = new TimerTask() {
//						public void run() {
//							try {

					ret = mmInStream.read(bytes);
					Log.d(TAG + " ret", ret + "");
//							} catch (IOException e) {
//								Log.e(TAG, "disconnected", e);
//								e.printStackTrace();
//								connectionLost();
//
//								// Start the service over to restart listening mode
//								BluetoothChatService.this.start();
//							}
//						}
//					};
//					mTimer = new Timer();
//					mTimer.schedule(mTask, 0, 1000);	// 0초후 첫실행, 3초마다 계속실행

					//String rawData = minimizer(new String(bytes, 0, 1000));
					String rawData = minimizerStr(new String(bytes, 0, 1000));
					Log.d(TAG + " rawData", rawData);

					Log.d(TAG + " Before mContainer Flag", Carcar5Talk.mContainer.getFlag() + "");

					/* Init container class && Set raw data */
					Carcar5Talk.mContainer.setRawData(rawData);
					//Carcar5Talk.mContainer.parseData();
					Carcar5Talk.mContainer.parseDataStr();
					Carcar5Talk.mContainer.setFlag(true);
					CarView.flag = true;

					Log.d(TAG + " After mContainer Flag", Carcar5Talk.mContainer.getFlag() + "");

					mHandler.obtainMessage(Carcar5Talk.MESSAGE_READ, ret, -1, rawData).sendToTarget();
					bytes = null;
					Log.d(TAG, "************************************************************************");
				} catch (IOException e) {
					Log.e(TAG, "disconnected", e);
					connectionLost();

					// Start the service over to restart listening mode
					BluetoothChatService.this.start();
					break;
				}
			}
		}

		
		/**
		 * Write to the connected OutStream.
		 * 
		 * @param buffer
		 *            The bytes to write
		 */
		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);
			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
		
	} // end ConnectedThread


	/**
	 * binary data를 축소화 시켜주는 메소드.
	 *
	 * @author Sungjung Kim
	 * @since 2015.05.15
	 *
	 */
	private String minimizerStr(String inStr)
	{
		int idx = 0;
		String outStr = "";

		/* Set My car */
		// Flag(3)
		outStr += inStr.substring(0, 3);

		// GPS(22)
		outStr += inStr.substring(3, 25);

		// Speed(6)
		outStr += inStr.substring(25, 31);

		// Vector(22)
		outStr += inStr.substring(31, 53);

		// # of cars(3)
		outStr += inStr.substring(53, 56);
		idx = 56;

		/* Set Other cars */
		for(int i = 0; i < Integer.parseInt(outStr.substring(53, 56)); i++) {
			// ID
			outStr += inStr.substring(idx, idx + 17);
			idx += 17;

			// Flag
			outStr += inStr.substring(idx, idx + 3);
			idx += 3;

			// GPS
			outStr += inStr.substring(idx, idx + 22);
			idx += 22;

			// Speed
			outStr += inStr.substring(idx, idx + 6);
			idx += 6;
		}

		return outStr;
	}



	/**
	 * binary data를 축소화 시켜주는 메소드.
	 *
	 * @author Sungjung Kim
	 * @since 2015.05.13
	 *
	 */
	private String minimizer(String inStr) {
		int i = 0, j = 0, idx = 0, rt = 0;
		String outStr = "";

		/* Set My car */
		// Flag
		outStr += inStr.charAt(idx++);

		// GPS
		for(i = idx; i < 22 + idx; i++)
			outStr += inStr.charAt(i);
		idx = i; 	// idx = 23

		// Speed
		for(i = idx; i < 6 + idx; i++)
			outStr += inStr.charAt(i);
		idx = i; 	// idx = 29

		// Vector
		for(i = idx; i < 22 + idx; i++)
			outStr += inStr.charAt(i);
		idx = i;	// idx = 51


		// # of cars
		outStr += inStr.charAt(i);
		idx++; 	// idx = 30
        rt = idx;

		/* Set Other Car */
		for(i = 0; i < Integer.valueOf(outStr.charAt(rt - 1)); i++) {
			// ID
			for(j = idx; j < 6 + idx; j++)
                outStr += inStr.charAt(j);
			idx = j;	// idx = 36
            //Log.d(TAG + i + "'s ID", Integer.parseInt(outStr.substring(idx - 6, 36)) + "");
            //Log.d(TAG + i + "'s ID", outStr.substring(idx - 6, idx));

			// Flag
			outStr += inStr.charAt(j);
			idx++;

			// GPS
			for(j = idx; j < 22 + idx; j++)
				outStr += inStr.charAt(j);
			idx = j;

			// Speed
			for(j = idx; j < 6 + idx; j++)
				outStr += inStr.charAt(j);
			idx = j;
		}

		return outStr;
	}
	
}