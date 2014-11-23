package org.bodtx.android;

import java.io.IOException;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SoundLevelService extends Service {
	// private Handler mHandler = new Handler();
	// public static final int ONE_HOUR = 1000 * 60 * 30;
//	Timer timer = new Timer();
	ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(1);

	// public static final int ONE_HOUR = 5000;

	// Handler handler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	//
	// }
	// };

	@Override
	public void onCreate() {
		// boolean postDelayed = mHandler.postDelayed(periodicTask, ONE_HOUR);
//		timer.scheduleAtFixedRate(new PeriodicTask(), 0, 1000 * 60 * 30);
		pool.scheduleWithFixedDelay(new PeriodicTask(), 1, 30, TimeUnit.MINUTES);
		// Log.i("isDelayed", String.valueOf(postDelayed));

	}

	private void setRingMode(int ringMode) {

		AudioManager audioManager = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setRingerMode(ringMode);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// mHandler.removeCallbacks(periodicTask);
		pool.shutdown();
		Toast.makeText(this, "Service onDestroy() ", Toast.LENGTH_LONG).show();
	}

	private class PeriodicTask implements Runnable {

		public void run() {
			try {
				Log.i("PeriodicTimerService", "Awake");

				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
						.getDefaultAdapter();

				if (!mBluetoothAdapter.isEnabled()) {
					mBluetoothAdapter.enable();
				} else {

					BluetoothDevice device = null;

					// Set<BluetoothDevice> bondedDevices = mBluetoothAdapter
					// .getBondedDevices();
					device = mBluetoothAdapter
							.getRemoteDevice("20:13:10:15:38:91");
					// for (BluetoothDevice bondedDevice : bondedDevices) {
					// if (bondedDevice.getName().equals("HC-05")) {
					// device = bondedDevice;
					// }
					// }
					if (device == null) {
						Log.i("PeriodicTimerService",
								"Erreur HC-05 non present\n");
					}
					mBluetoothAdapter.cancelDiscovery();
					BluetoothSocket socket = null;
					socket = device
							.createInsecureRfcommSocketToServiceRecord(UUID
									.fromString("00001101-0000-1000-8000-00805F9B34FB"));

						try {
							socket.connect();
							Thread.sleep(1000);
							setRingMode(AudioManager.RINGER_MODE_NORMAL);

						} catch (IOException e) {
							// setRingMode(AudioManager.RINGER_MODE_VIBRATE);
							Log.e("error", e.getMessage(), e);
						} catch (InterruptedException e) {
							Log.e("error", e.getMessage(), e);
						} finally {
							try {
								socket.close();
							} catch (IOException e) {
								Log.e("error", "la socket se ferme plus :o");
							}
						}
				}
			} catch (Throwable e) {
				Log.e("erreur", "sortie", e);
			}

			// mHandler.postDelayed(new PeriodicTask(), ONE_HOUR);

		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	};
}
