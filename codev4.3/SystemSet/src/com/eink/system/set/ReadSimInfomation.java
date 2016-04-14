package com.eink.system.set;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class ReadSimInfomation extends Service {
	
	public static MyBinder ibinder = null ; 
	
	
	public class MyBinder extends Binder{
		ReadSimInfomation getReadSimInfomation(){
			return ReadSimInfomation.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		if(ibinder == null){
			ibinder = new MyBinder();
		}
		return ibinder;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		if(intent != null){
			Bundle extras = intent.getExtras();
			if(extras != null){
				String net = extras.getString("net");
				if("GSM".equals(net) || "AUTO".equals(net) || "TD-SCDMA".equals(net)|| "WHAT".equals(net)){
					getCardType(net);
				}
				
			}
		}
	}
	
	
	private void getCardType() {
		Log.i("ReadSimInfomation", "getCardType()");
		Intent i = new Intent(Intent.ACTION_VIEW);
		Bundle b = new Bundle();
		b.putString("COMMAND", "^CARDMODE");
		i.putExtras(b);
		i.setClassName("com.android.phone","com.android.phone.RilRequestService");
		startService(i);
		
	}
	
	private void getCardType(String net) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		Bundle b = new Bundle();
		b.putString("COMMAND", "^SYSCONFIG");
		b.putString("OPTIONS", net);
		i.putExtras(b);
		i.setClassName("com.android.phone",
				"com.android.phone.RilRequestService");
		startService(i);
	}
}
