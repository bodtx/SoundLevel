package org.bodtx.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import org.bodtx.soundLevel.R;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
		startService(new Intent(this, SoundLevelService.class));
		finish();
		
		
		
		
	}


}

