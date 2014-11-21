package org.bodtx.android;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SoundLevelService extends Service {
	Bundle messageBundle = new Bundle();
	private Handler mHandler = new Handler();
	public static final int ONE_HOUR = 5000;

	// Handler handler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	//
	// }
	// };

	private Runnable periodicTask = new Runnable() {
		public void run() {
			Log.i("PeriodicTimerService", "Awake");

			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
					.getDefaultAdapter();

			if (!mBluetoothAdapter.isEnabled()) {
				mBluetoothAdapter.enable();
			} else {

				BluetoothDevice device = null;

				Set<BluetoothDevice> bondedDevices = mBluetoothAdapter
						.getBondedDevices();
				for (BluetoothDevice bondedDevice : bondedDevices) {
					if (bondedDevice.getName().equals("HC-05")) {
						device = bondedDevice;
					}
				}
				if (device == null) {
					Log.i("PeriodicTimerService", "Erreur HC-05 non appairé\n");
				}
				 mBluetoothAdapter.cancelDiscovery();
				 BluetoothSocket socket=null;
				try {
					socket = device
							.createInsecureRfcommSocketToServiceRecord(UUID
									.fromString("00001101-0000-1000-8000-00805F9B34FB"));
					
					socket.connect();
					Thread.sleep(1000);
					setRingMode(AudioManager.RINGER_MODE_NORMAL);
					
				} catch (IOException e) {
					setRingMode(AudioManager.RINGER_MODE_VIBRATE);
					Log.e("error", e.getMessage(),e);
				} catch (InterruptedException e) {
					Log.e("error", e.getMessage(),e);
				}
				finally{
					try {
						socket.close();
					} catch (IOException e) {
						Log.e("error", "la socket se ferme plus :o");
					}
				}
			}

			mHandler.postDelayed(periodicTask, ONE_HOUR);
			// messageBundle.putString("bip", "bip");
			// Message obtainMessage = handler.obtainMessage();
			// obtainMessage.setData(messageBundle);
			// handler.sendMessage(obtainMessage);

		}

	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		mHandler.postDelayed(periodicTask, ONE_HOUR);

	}

	private void setRingMode(int ringMode) {

		AudioManager audioManager = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setRingerMode(ringMode);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(periodicTask);
		Toast.makeText(this, "Service onDestroy() ", Toast.LENGTH_LONG).show();
	}
}
