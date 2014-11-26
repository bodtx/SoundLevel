package org.bodtx.android;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SoundLevelService extends Service {
	// private Handler mHandler = new Handler();
	// public static final int ONE_HOUR = 1000 * 60 * 30;
	// Timer timer = new Timer();
	ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(1);

	boolean isAuBoulot = false;

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
		// timer.scheduleAtFixedRate(new PeriodicTask(), 0, 1000 * 60 * 30);
		pool.scheduleWithFixedDelay(new PeriodicTask(this), 1, 30, TimeUnit.MINUTES);
		// Log.i("isDelayed", String.valueOf(postDelayed));

	}

	private void setRingMode(int ringMode) {

		AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setRingerMode(ringMode);
		if (ringMode == AudioManager.RINGER_MODE_NORMAL) {
			// TODO pourquoi 1?
			audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
					audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL), 1);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// mHandler.removeCallbacks(periodicTask);
		pool.shutdown();
		Toast.makeText(this, "Service onDestroy() ", Toast.LENGTH_LONG).show();
	}

	private class PeriodicTask implements Runnable {
		Context serviceContext;

		public PeriodicTask(Context serviceContext) {
			super();
			this.serviceContext = serviceContext;
		}

		public void run() {
			Log.i("PeriodicTimerService", "Awake");
			
			int jourSemaine = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
			if (jourSemaine != Calendar.SATURDAY || jourSemaine != Calendar.SUNDAY) {
				if (false/*connectToDevice("boulot")*/) {
					setRingMode(AudioManager.RINGER_MODE_VIBRATE);
					isAuBoulot = true;
				} else if (isAuBoulot && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 16) {
					Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:0687419862"));
					smsIntent.putExtra("sms_body", "Je probablement déjà parti du boulot :-)");
					startActivity(smsIntent);
					setRingMode(AudioManager.RINGER_MODE_NORMAL);
					isAuBoulot = false;
				}
			}
			
			if (!isAuBoulot) {
				if (connectToDevice("20:13:10:15:38:91")) {
					setRingMode(AudioManager.RINGER_MODE_NORMAL);
				}
			}
		}

		private boolean connectToDevice(String macDevice) {
			boolean success = false;
			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

			if (!mBluetoothAdapter.isEnabled()) {
				mBluetoothAdapter.enable();
			} else {

				BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macDevice);
				if (device == null) {
					Log.i("connectToDevice", "Device " + macDevice + " non present\n");
				}
				mBluetoothAdapter.cancelDiscovery();
				BluetoothSocket socket = null;
				try {
					socket = device.createInsecureRfcommSocketToServiceRecord(UUID
							.fromString("00001101-0000-1000-8000-00805F9B34FB"));

					socket.connect();
					Thread.sleep(1000);
					success = true;
				} catch (Exception e) {
					Log.e("error", e.getMessage(), e);
				} finally {
					try {
						socket.close();
					} catch (IOException e) {
						Log.e("error", "la socket se ferme plus :o");
					}
				}
			}
			return success;
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	};
}
