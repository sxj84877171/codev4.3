package com.pvi.ap.reader.activity;


import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;

import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.data.common.EPDRefresh;


/**
 * 具有加载动画显示的Activity基类<br>
 * 只要继承该基类，在进入该Activity时候可以有加载动画<br>
 * @author 彭见宝
 * @since 2010-11-5
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class AnimationLoadActivity extends Activity {
	
 
		protected Handler myHandler = null;
	
		String dialogMsg = "";	
		
		//int dialogViewId = R.layout.dialog;
		
		protected Dialog dialog = null;
		
		public Dialog getDialog() {
			return dialog;
		}

		public void setDialog(Dialog dialog) {
			this.dialog = dialog;
		}

		public Handler getMyHandler() {
			return myHandler;
		}

		public void setMyHandler(Handler myHandler) {
			this.myHandler = myHandler;
		}

		
		
		public AnimationLoadActivity() {
			
		}
		
		public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        
		      
		        
		        /*
		        LinearLayout layout = new LinearLayout(this);   
		        layout.setOrientation(LinearLayout.VERTICAL);  
		        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		      
		        layout.setLayoutParams(param);
		        
		       
		        dialogMsg = getResources().getString(R.string.systemconfig_load_message);
		        TextView tv = new TextView(this);
		        tv.setText(dialogMsg);
		        tv.setGravity(Gravity.CENTER);
		        layout.addView(tv);
		 
		        
		        GIFView view = new GIFView(this);
		        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		        param1.gravity = Gravity.CENTER;
		        param1.width=126;
		        param1.height=22;
		        param1.topMargin = 15;
		        param1.bottomMargin = 20;
		        view.setLayoutParams(param1);
		        view.setBackgroundColor(Color.RED);
		        layout.addView(view);
		   
		        
		      
				dialog = new AlertDialog.Builder(AnimationLoadActivity.this).setTitle(
		        "温馨提示").setView(layout)
				.create();
				// 显示对话框
		        dialog.show();
		        dialog.getWindow().setLayout(180,200);//300 长，200宽
				myHandler = new MyHandler();*/
		      
		}
		
		class MyHandler extends Handler{//继承Handler类时，必须重写handleMessage方法

		
			public MyHandler(){
				super();
			}

			public MyHandler(Looper l){

				super(l);

			}

			public void handleMessage(Message msg) {//执行接收到的通知，此时执行的顺序是按照队列进行，即先进先出
				
				super.handleMessage(msg);

				Bundle b=msg.getData();

				String msgStr =b.getString("msg");
				
				if("over".equals(msgStr)){

					dialog.dismiss();
					
				}
			}
		}
		
		
		@Override
		public Resources getResources() {
			
			Resources resources = super.getResources();
			Configuration config = resources.getConfiguration();
			SharedPreferences settings = getSharedPreferences(Constants.configFileName, 0); 
    		String state = settings.getString(LanguageSetActivity.Language, "");
    		//Log.i("now language:", state);
    		if(LanguageSetActivity.S_CHINESE.equals(state)){
    			config.locale = Locale.SIMPLIFIED_CHINESE; //简体中文()
    		}else if(LanguageSetActivity.T_CHINESE.equals(state)){
    			config.locale = Locale.TRADITIONAL_CHINESE ; //繁体中文()
    		}else if(LanguageSetActivity.ENGLISH.equals(state)){
    			config.locale = Locale.ENGLISH; //英语()
    		}else{
    			config.locale = Locale.ENGLISH; //英语()
    		}
			

			DisplayMetrics dm = resources.getDisplayMetrics();
			resources.updateConfiguration(config, dm);
			return resources;
		}
		
		
		@Override
		protected void onResume() {
		     // TODO Auto-generated method stub
		     EPDRefresh.refreshGCOnceFlash();
		     super.onResume();
		}
}







