package com.pvi.ap.reader.service;


/**
 * 接收屏幕超时服务类
 * @author 彭见宝
 *
 */



import android.app.KeyguardManager;
import android.app.Service;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;



public class ScreenSaveService extends Service {
	
	public final String tag = "ScreenSaveService";


	public int DELAY = 10000; 
	
	
	
	private int defTimeOut;
	
	KeyguardManager mKeyguardManager=null; 
	private KeyguardLock mKeyguardLock=null;
	
	BroadcastReceiver mMasterResetReciever= new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent){         
           try{
             Intent i = new Intent();
             i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             //i.setClass(context, ScreenSaveActivity.class);
             context.startActivity(i);
             //finish();
             
           }catch(Exception e){
             Log.i("Output:", e.toString());
           }        
        }
      };
	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		//Intent intent = get
		
		 //系统默认屏
        defTimeOut = Settings.System.getInt(getContentResolver(),  
                Settings.System.SCREEN_OFF_TIMEOUT, DELAY); 
        //System.out.println("======defTimeOut====="+defTimeOut);
        Log.i(tag, "======defTimeOut====="+defTimeOut);
        Settings.System.putInt(getContentResolver(),  
                Settings.System.SCREEN_OFF_TIMEOUT, DELAY); 
		
		mKeyguardManager= (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
		mKeyguardLock= mKeyguardManager.newKeyguardLock("");
		mKeyguardLock.disableKeyguard();
		
		//registerReceiver(mMasterResetReciever, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
		registerReceiver(mMasterResetReciever, new IntentFilter(Intent.ACTION_SCREEN_OFF));

	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		DELAY = intent.getExtras().getInt("DELAY");
	}
	
	public void onDestroy() {
		unregisterReceiver(mMasterResetReciever);
		super.onDestroy();

	}

}
