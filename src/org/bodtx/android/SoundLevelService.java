package org.bodtx.android;

import java.io.IOException;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.bodtx.soundLevel.R;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SoundLevelService extends Service {
	ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(1);
	boolean isAuBoulot = false;
	private WakeLock wakeLock;
	AudioManager audioManager ;

	@Override
	public void onCreate() {
		audioManager=(AudioManager) this
		.getSystemService(Context.AUDIO_SERVICE);
		
		Notification.Builder notification = new Notification.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher).setContentTitle(
						"Gestion du mode");

		startForeground(1, notification.getNotification());
		PowerManager mgr = (PowerManager) this
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = mgr
				.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
		wakeLock.acquire();
		pool.scheduleWithFixedDelay(new PeriodicTask(), 1, 30, TimeUnit.MINUTES);
	}

	private void setRingMode(int ringMode) {

		audioManager.setRingerMode(ringMode);
		if (ringMode == AudioManager.RINGER_MODE_NORMAL) {
			maxVolume(audioManager, AudioManager.STREAM_NOTIFICATION);
			maxVolume(audioManager, AudioManager.STREAM_VOICE_CALL);
			maxVolume(audioManager, AudioManager.STREAM_RING);
			maxVolume(audioManager, AudioManager.STREAM_SYSTEM);
		}
	}

	private void maxVolume(AudioManager audioManager, int type) {
		// TODO pourquoi 1?
		audioManager.setStreamVolume(type,
				audioManager.getStreamMaxVolume(type), 1);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopForeground(true);
		wakeLock.release();
		pool.shutdown();
		Toast.makeText(this, "Service onDestroy() ", Toast.LENGTH_LONG).show();
	}

	private class PeriodicTask implements Runnable {

		public void run() {
			Log.i("PeriodicTimerService", "Awake");

			int jourSemaine = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
			if (jourSemaine != Calendar.SATURDAY || jourSemaine != Calendar.SUNDAY) {
				if (connectToDevice("30:14:10:09:00:61")) {
					setRingMode(AudioManager.RINGER_MODE_VIBRATE);
					isAuBoulot = true;
				} else if (isAuBoulot && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 16) {
					SmsManager.getDefault().sendTextMessage("+33687419862", null, "Je suis probablement parti du boulot :-)", null, null);
					setRingMode(AudioManager.RINGER_MODE_NORMAL);
					isAuBoulot = false;
				}
			}

			if (!isAuBoulot
					&& audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
				if (connectToDevice("20:13:10:15:38:91")) {
					setRingMode(AudioManager.RINGER_MODE_NORMAL);
				}
			}
		}

		private boolean connectToDevice(String macDevice) {
			boolean success = false;
			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
					.getDefaultAdapter();

			if (!mBluetoothAdapter.isEnabled()) {
				mBluetoothAdapter.enable();
			} else {

				// Set<BluetoothDevice> bondedDevices =
				// mBluetoothAdapter.getBondedDevices();
				// for (BluetoothDevice bondedDevice : bondedDevices) {
				// if(bondedDevice.getName().equals("HC-05")){
				// ;
				// }
				// }
				BluetoothDevice device = mBluetoothAdapter
						.getRemoteDevice(macDevice);
				if (device == null) {
					Log.i("connectToDevice", "Device " + macDevice
							+ " non present\n");
				}
				mBluetoothAdapter.cancelDiscovery();
				BluetoothSocket socket = null;
				try {
					socket = device
							.createInsecureRfcommSocketToServiceRecord(UUID
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
