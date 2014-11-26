package org.bodtx.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	public BootReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("boot", "boot Time!");
		Intent startSoundService = new Intent(context, SoundLevelService.class);
		context.startService(startSoundService);
	}
}
