package org.bodtx.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MySmsReceiver extends BroadcastReceiver {
	public MySmsReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("MySmsReceiver", "A new SMS has been received");
		if (intent.getAction()!=null&&intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
			Intent startSmsService = new Intent(context, SoundLevelService.class);
			startSmsService.putExtras(intent);
			startSmsService.setAction(intent.getAction());
			context.startService(startSmsService);
		}
	}
}
