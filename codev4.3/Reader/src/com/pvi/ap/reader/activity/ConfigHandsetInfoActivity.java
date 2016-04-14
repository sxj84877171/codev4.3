/**
 * 设备信息      
 * @author 马中庆
 */
package com.pvi.ap.reader.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.RegInfo;

/**
 * 设备信息界面类<br>
 * 
 * @author 彭见宝
 * @since 2010-11-5
 * @version V1.0.0 (C)Copyright 2010-2013, by www.pvi.com.tw
 * @since 2011-04-02 @author 刘剑雄
 */
public class ConfigHandsetInfoActivity extends PviActivity {
	
	public static String VISION = "V2011-07-05 版本";


	//private int themeNum = -1;
	
	/**
	 * 产品型号
	 */
	private TextView productView = null;
	
	/**
	 * 设备ID
	 */
	private TextView equIdView = null;
	
	/**
	 * IMEI
	 */
	private TextView imeiView = null;
	
	/**
	 * IMEI
	 */
	private TextView softvision = null;

	
	
	private ImageView image=null;
	private LinearLayout all=null;
	   private int deviceType;

	public void onResume(){
		super.onResume();
		setValue();
		sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
		Intent tmpIntent = new Intent(
                MainpageActivity.SHOW_ME);
        Bundle bundleToSend = new Bundle();
      
        bundleToSend.putString("sender", ConfigHandsetInfoActivity.this.getClass().getName()); //TAB内嵌activity类的全名
        tmpIntent.putExtras(bundleToSend);
        sendBroadcast(tmpIntent);
        tmpIntent = null;
        bundleToSend = null;
        image=(ImageView)findViewById(R.id.image);
        image.setFocusable(true);
        image.setFocusableInTouchMode(true);
        image.requestFocus();
	}	
 
	public void onCreate(Bundle savedInstanceState) {
		GlobalVar appState = ((GlobalVar) getApplicationContext());
		deviceType=appState.deviceType;
        setContentView(R.layout.confighandsetinfostyle2);
    	super.onCreate(savedInstanceState);
		if(deviceType==1){
	   		all=(LinearLayout)findViewById(R.id.mainblock);
			//all.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
			
	   	   }
	

		
	}
    public void setValue(){
    	productView = (TextView) findViewById(R.id.confighandsetinfo_product);
		equIdView = (TextView) findViewById(R.id.confighandsetinfo_equ);
		imeiView = (TextView) findViewById(R.id.confighandsetinfo_imei);
		softvision = (TextView) findViewById(R.id.softvision);
		
		
		// 获取数据，设置打开或关闭按钮的状态
		String productID = "PVI20101105";
		String deviceID = "PVI20101105";
		String imei = "PVI20101105-H";
		Cursor cur = managedQuery(RegInfo.CONTENT_URI, null, null, null, null);
		if (cur.moveToFirst()) {
			productID = cur.getString(cur.getColumnIndex(RegInfo.Production));
			deviceID = cur.getString(cur.getColumnIndex(RegInfo.DeviceVersion));
			imei = cur.getString(cur.getColumnIndex(RegInfo.IMEI));
		}			
		cur.close();
		productView.setText(productID);
		equIdView.setText(deviceID);
		imeiView.setText(imei);
		Date now = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Logger.v("time", df.format(now));
		
		softvision.setText(VISION);

		
		
    }
	class MyThread implements Runnable {

		public void run() {
			
			// 初始化UI
			productView = (TextView) findViewById(R.id.confighandsetinfo_product);
			equIdView = (TextView) findViewById(R.id.confighandsetinfo_equ);
			imeiView = (TextView) findViewById(R.id.confighandsetinfo_imei);
			softvision = (TextView) findViewById(R.id.softvision);
			
			
			// 获取数据，设置打开或关闭按钮的状态
			String productID = "PVI20101105";
			String deviceID = "PVI20101105";
			String imei = "PVI20101105-H";
			Cursor cur = managedQuery(RegInfo.CONTENT_URI, null, null, null, null);
			if (cur.moveToFirst()) {
				productID = cur.getString(cur.getColumnIndex(RegInfo.Production));
				deviceID = cur.getString(cur.getColumnIndex(RegInfo.DeviceVersion));
				imei = cur.getString(cur.getColumnIndex(RegInfo.IMEI));
			}			
			cur.close();
			productView.setText(productID);
			equIdView.setText(deviceID);
			imeiView.setText(imei);
			softvision.setText(VISION);

			
		
			sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
			
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 加载数据线程
//		Thread uiLoadDataThread = new Thread(new MyThread());
//		uiLoadDataThread.start();
	}



}