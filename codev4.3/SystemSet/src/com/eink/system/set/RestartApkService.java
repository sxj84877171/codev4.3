package com.eink.system.set;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
/**
 * ����APK����
 * @author �����
 *
 */
public class RestartApkService extends Service {

	private static final String TAG = "RestartApkService";
	
	private String apk_Package;
	
	private String apk_Main_Activity;
	
	private int pid;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		
	}

	@Override
	public void onDestroy() {
		
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				pid = bundle.getInt("pid");
				apk_Package = bundle.getString("apk_Package");
				apk_Main_Activity = bundle.getString("apk_Main_Activity");
		
				restart();
			}
		}

	}

	public void restart() {
	
		//android.os.Process.killProcess(pid); 
		//�رս���
		ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);   
		manager.restartPackage(apk_Package);
		
		//��������
		Intent mIntent = new Intent( ); 
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        ComponentName comp = new ComponentName(apk_Package,apk_Main_Activity);
        mIntent.setComponent(comp); 
        mIntent.setAction("android.intent.action.VIEW"); 
        startActivity(mIntent);
	}





}
