package com.pvi.ap.reader.activity.viewBuilder;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View.OnClickListener;

public interface ActivityListener {
//	public void onCreate(Bundle savedInstanceState);
	public void onRestoreInstanceState(Bundle savedInstanceState);
	public void onPostCreate(Bundle savedInstanceState);
	public void onStart();
	public void onRestart();
	public void onResume() ;
	public void onPostResume();
	public void onSaveInstanceState(Bundle outState);
	public void onPause();
	public void onStop();
	public void onDestroy();
	
	public boolean onKeyUp(int keyCode, KeyEvent event);
	public boolean onKeyDown(int keyCode, KeyEvent event);
	public void onConfigurationChanged(Configuration newConfig);	
}
