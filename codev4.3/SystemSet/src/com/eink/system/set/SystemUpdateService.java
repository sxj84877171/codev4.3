package com.eink.system.set;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * 系统升级服务<br>
 * 该类升级G3电子书升级服务
 * @since 2010-9
 * @author 孙向锦 
 * @version V1.0.0
 * <p><p><br>
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 *
 */
public class SystemUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private SystemUpdateService self ;
	@Override
	public void onCreate() {
		super.onCreate();
		self = this ;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		updateBySdcard();
		addbroadcastRecFilter();
		super.onStart(intent, startId);
	}
	

	@Override
	public void onDestroy() {
//		unregisterReceiver(broadcastRec);
	}
	
	
	
	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		Intent activity = new Intent(self, UpdateActivity.class);
		activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle extras = new Bundle();
		extras.putString("message", "程序正在安装中...");
		startActivity(activity);
		new Thread() {
			public void run() {
				if (intent != null) {
					Bundle extras = intent.getExtras();
					if (extras != null) {
						String cmd = extras.getString("cmd");
						Log.e("UPDATE", "cmd" + cmd);
						if (cmd != null && !"".equals(cmd)) {
							boolean result = execuseCmd(cmd);
							sendMeesageToActivity(result);
						}
					}
				}
			};
		}.start();
		return 0 ;
	}
	
	private void sendMeesageToActivity(boolean result){
		Intent intent = new Intent("com.eink.system.set.message");
		Bundle extras = new Bundle();
		if(result){
			extras.putString("message", "恭喜您，升级成功。");
		}else{
			extras.putString("message", "抱歉！升级失败，已还原到上一个版本");
		}
		intent.putExtras(extras);
		sendBroadcast(intent);
	}
	
	private final BroadcastReceiver broadcastRec = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        if(intent.getAction().equals("android.intent.action.MEDIA_MOUNTED")){//SD卡已经成功挂载
	        	new Thread(){
	        		public void run() {
	        			updateBySdcard();
	        		}
	        	}.start();

	        }
	    }
	};
	
	private void addbroadcastRecFilter() {
//		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
//		intentFilter.addDataScheme("file");
//		registerReceiver(broadcastRec, intentFilter);// 注册监听函数
	}
	
	
	
	private boolean hasUpdateFile(String path){
		File file = new File(path);
		return file.exists() ;
	}
	
	
	private void install(String path){
		Intent inten = new Intent(Intent.ACTION_VIEW);
		inten.setDataAndType(Uri.parse("file://" + path),
				"application/vnd.android.package-archive");
		inten.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(inten);
	}
	
	private boolean execuseCmd(String cmd){
		Log.i("excuseCmd", "cmd:" + cmd);
		Runtime rt = Runtime.getRuntime();
		Process p = null ;
		try {
			p = rt.exec(cmd);
		} catch (IOException e) {
			Log.e("SystemUpdateServiceEink", Log.getStackTraceString(e));
		}finally{
			if(p != null){
				InputStream is = p.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				StringBuffer sb = new StringBuffer();
				String line = "" ;
				try {
					while((line = br.readLine()) != null){
						sb.append(line);
					}
				} catch (IOException e) {
				} finally{
					try {
						br.close();
						is.close();
					} catch (IOException e) {
					}
				}
				Log.i("result:", "result:" + sb.toString());
				if(sb.toString().contains("Success")){
					return true;
				}else{
					return false;
				}
			}
		}
		
		return false ;
	}
	
	
	public void getAppInfo(String filePath){
		Properties properties = new Properties();
		try {
			FileInputStream fin = new FileInputStream(filePath);
			properties.load(fin);
		} catch (Exception e) {
			return ;
		}
		String apkN = getPropertiesByKey(properties, "apk");
		if(apk != null && "".equals(apkN.trim())){
			String[] tmp = apkN.split(";");
			apk = new String[tmp.length];
			int i = 0 ;
			for(String tp:tmp){
				apk[i++] = getPropertiesByKey(properties, tp);
			}
		}
		String cmdNum = getPropertiesByKey(properties, "cmdNum");
		if(cmdNum != null && !"".equals(cmdNum)){
			int num = 0 ;
			try {
				num = Integer.parseInt(cmdNum);
			} catch (NumberFormatException e) {
				num = 0 ;
			}
			cmd = new String[num];
			for(int i = 0 ; i < num ; i++){
				cmd[i] = getPropertiesByKey(properties, "cmd"+(i+1));
			}
		}
	}
	private String[] cmd = null ;
	private String[] apk = null ;
	private void updateBySdcard() {
		String path =" /sdcard/update/update.properties" ;
		if(hasUpdateFile(path)){
			getAppInfo(path);
		}
		if(cmd != null){
			for(String tmp : cmd){
				if(tmp != null && !"".equals(tmp)){
					 execuseCmd(tmp);
				}
			}
		}
		if(apk != null){
			for(String apkPath:apk){
				File file = new File(apkPath);
				if(!file.exists()) continue ;
				if (getPackageName().equals(readApplicationInfo(apkPath))) {
					install(apkPath);
				}else{
					execuseCmd("pm install -r " + apkPath);
				}
			}
		}
	}
	
	/***
	 * @param path
	 * @return
	 */
	private String readApplicationInfo(String path){
		PackageManager pm = getPackageManager();
		PackageInfo packInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
		if(packInfo != null){
			String version = "" +  packInfo.packageName ;
			return version ;
		}
		return null;
	}
	/**
	 * @param properties
	 * @param key
	 * @return
	 */
	private String getPropertiesByKey(Properties properties,String key){
		if(properties == null) return null;
		if(key == null) return null ;
		String value = properties.getProperty(key);
		if(value == null) value = properties.getProperty(key.toUpperCase());
		if(value == null) value = properties.getProperty(key.toLowerCase());
		if(value == null){
			Object[] otmp = properties.keySet().toArray();
			for(Object o:otmp){
				if(o instanceof String){
					if(key.toLowerCase().equals(o.toString().toLowerCase())){
						value = properties.getProperty(o.toString());
						break ;
					}
				}
			}
		}
		return value ;
	}
}
