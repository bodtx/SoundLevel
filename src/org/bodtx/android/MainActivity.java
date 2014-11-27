package org.bodtx.android;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
		Intent startSoundService = new Intent(this, SoundLevelService.class);
		this.startService(startSoundService);
		finish();
		
	}


}
