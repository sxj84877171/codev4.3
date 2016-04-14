package com.eink.system.set;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class UpdateActivity extends Activity {
	private TextView messageView  ;
	private Button button ;
	
	private BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if("com.eink.system.set.message".equals(action)){
				Bundle extras = intent.getExtras();
				String message = extras.getString("message");
				Log.e("SystemUpdateServiceEink", "message:" + message);
				messageView.setText(message);
				button.setVisibility(View.VISIBLE);
//				getWindow().getDecorView().getRootView().invalidate(0, 0, 600, 800, View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		messageView = (TextView)findViewById(R.id.message);
		messageView.setBackgroundColor(Color.WHITE);
		messageView.setText("程序正在升级中...");
		messageView.setTextSize(30);
		button = (Button)findViewById(R.id.complete);
		button.setVisibility(View.GONE);
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		getWindow().getDecorView().getRootView().invalidate(0, 0, 600, 800, View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
		IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction("com.eink.system.set.message");
		registerReceiver(br, intentfilter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(br);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return true;
	}
}
