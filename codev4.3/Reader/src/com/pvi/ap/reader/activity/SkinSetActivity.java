package com.pvi.ap.reader.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.data.common.Config;


/**
 * 皮肤设置界面类<br>
 * @author 彭见宝
 * @since 2010-11-5
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class SkinSetActivity extends PviActivity {


	public final static String LOG_TAG = "SubscribeRemindActivity";

	/**
	 * 打开选项
	 */
	private Button m_openButton = null;
	/**
	 * 关闭选项
	 */
	private Button m_closeButton = null;

	/**
	 * 确定按钮
	 */
	private Button m_okButton = null;
	/**
	 * 返回按钮
	 */
	private Button m_returnButton = null;

	private int type = -1;

	TextView fp_application = null;
	TextView fp_settings = null;
	TextView fp_music = null;
	TextView fp_back = null;
	TextView fp_mean = null;

	/**
	 * 皮肤状态
	 */
	public final static String  STATE = "SkinSet";

	/** 
	 * 皮肤1状态
	 */
	public final static String  SKIN1 = "SKIN1";

	/**
	 * 皮肤2状态
	 */
	public final static String  SKIN2 = "SKIN2";

	/**
	 * 皮肤2状态
	 */
	public final static String  SKIN3 = "SKIN3";


	public void initselect(){

		type = -1;

		m_openButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
		m_closeButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));


	}

	public void selectOpen(){
		if(type != 1){
			type = 1;


			m_openButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));	
			m_closeButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));

		}
	}

	public void selectClose(){
		if(type != 2){
			type = 2;


			m_openButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
			m_closeButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));

		}
	}


	public void initUI(){
		m_openButton = (Button) findViewById(R.id.skin_skin1);
		m_closeButton = (Button) findViewById(R.id.skin_skin2);

		m_okButton = (Button) findViewById(R.id.ok);
		m_returnButton = (Button) findViewById(R.id.cancel);
		m_openButton.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus==true){
					selectOpen();
				}
			}

		});
		m_openButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectOpen();
			}

		});
		m_closeButton.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus==true){
					selectClose();
				}
			}

		});
		m_closeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectClose();
			}

		});



		// 增加事件
		m_okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				SharedPreferences settings = getSharedPreferences(Config.getString("configFileName"), 0); 
				SharedPreferences.Editor editor = settings.edit(); 
				if(type == -1){
					PviAlertDialog dialog = new PviAlertDialog(getParent());
					dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
					dialog.setCanClose(true);
					dialog.setMessage(getResources().getString(R.string.welcome_set_msg));// 设置自定义对话框的样式
					dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.system_soft_unauthorized),
							new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}

					});
					// 显示对话框
					dialog.show();

				}else if(type == 1){

					editor.putString(STATE, SKIN1);  
					editor.commit(); 
				}else if(type == 2){

					editor.putString(STATE, SKIN2);  
					editor.commit(); 

				}


				Intent i1 = new Intent("com.pvi.ap.reader.service.RestartApkService");
				Bundle b1 = new Bundle();
				b1.putString("apk_Package", "com.pvi.ap.reader");
				b1.putString("apk_Main_Activity", "com.pvi.ap.reader.activity.MainpageActivity");
				i1.putExtras(b1);
				startService(i1);
			}

		});

		// 增加事件
		m_returnButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				sendBroadcast(new Intent(MainpageActivity.BACK));
			}

		});

	}
	public void onResume(){
		super.onResume();
		if(deviceType==1){
//			this.getParent().getWindow().getDecorView().invalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
		}
		Intent tmpIntent = new Intent(
				MainpageActivity.SHOW_ME);
		Bundle bundleToSend = new Bundle();

		bundleToSend.putString("sender", SkinSetActivity.this.getClass().getName()); //TAB内嵌activity类的全名
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
		tmpIntent = null;
		bundleToSend = null;
	}	
	protected void onPause() {
		super.onPause();
		closePopmenu();
	}

	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.skinsetstyle2);
		super.onCreate(savedInstanceState);
		initUI();
	}



	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		initselect();
		SharedPreferences settings = getSharedPreferences(Config.getString("configFileName"), 0); 
		String state = settings.getString(STATE, "");
		if(SKIN1.equals(state)){
			selectOpen();
		}else if(SKIN2.equals(state)){
			selectClose();
		}
		sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));

	}



	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			menupan();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 通知框架返回上一个子activty
			sendBroadcast(new Intent(MainpageActivity.BACK));
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}


	public void setMenuListener(View v){
		LinearLayout ll = (LinearLayout)v;
		LinearLayout ll2 = (LinearLayout)ll.getChildAt(0);
		int itemCount = ll2.getChildCount();
		for(int i=0;i<itemCount;i++){
			TextView menuItem = (TextView)ll2.getChildAt(i);
			if(menuItem.getTag()!=null){
				menuItem.setOnClickListener(menuclick);
			}
		}
	}

	private View.OnClickListener menuclick = new View.OnClickListener() {        
		@Override
		public void onClick(View v) {
			String vTag = v.getTag().toString(); 
			if(vTag.equals("submitBtn")){    //通过tag来判断是前面xml中配置的哪个菜单
				//hideInput();        //红色的片段为你自己在菜单项点击后，需要执行的代码
				// setUIData();
				// popmenu.dismiss();    //点击后，菜单隐藏
			}
			if(vTag.equals("gotoMain")){    
				Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle sndBundle = new Bundle();
				sndBundle.putString("act",
				"com.pvi.ap.reader.activity.MainpageInsideActivity");
				sndBundle.putString("haveStatusBar", "1");
				sndBundle.putString("startType", "reuse");
				intent.putExtras(sndBundle);
				sendBroadcast(intent);
			}

			closePopmenu();
		}
	};


}
