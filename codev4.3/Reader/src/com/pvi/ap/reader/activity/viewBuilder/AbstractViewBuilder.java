package com.pvi.ap.reader.activity.viewBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public abstract class AbstractViewBuilder implements ActivityListener {
	protected PviActivity activity = null;

	public AbstractViewBuilder(PviActivity activity, int layoutResID) {
		this.activity = activity;
		this.activity.setContentView(layoutResID);
	}

	public abstract void initUI();

	public abstract void bindEvent();

	public View findViewById(int id) {
		return activity.getWindow().findViewById(id);
	}

	public void sendBroadcast(Intent intent) {
		activity.sendBroadcast(intent);
	}

	public Resources getResources() {
		return activity.getResources();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public abstract void onPause();

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPostResume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRestart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	public abstract void onResume();

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub

	}

	public Context getApplicationContext() {
		return activity.getApplicationContext();
	}

	public final Cursor managedQuery(Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		return activity.managedQuery(uri, projection, selection, selectionArgs,
				sortOrder);
	}

	public final String getString(int resId) {
		return activity.getString(resId);
	}

	public abstract boolean onKeyUp(int keyCode, KeyEvent event);

	public boolean onKeyDown(int keyCode, KeyEvent event){
		return false;
	}

	public ContentResolver getContentResolver() {
		return activity.getContentResolver();
	}

	public FileInputStream openFileInput(String name)
	throws FileNotFoundException {
		return activity.openFileInput(name);
	}
	public Intent getIntent() {
		return activity.getIntent();
	}
}
