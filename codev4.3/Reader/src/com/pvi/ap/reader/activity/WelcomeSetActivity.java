package com.pvi.ap.reader.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.data.common.Config;


/**
 * 欢迎页设置界面类<br>
 * @author 彭见宝
 * @since 2010-11-5
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 * @since 2011-04-02 @author 刘剑雄
 */
public class WelcomeSetActivity extends PviActivity {


	public final static String LOG_TAG = "WelcomeSetActivity";
	private String [] info = {"打开无线欢迎页","关闭无线欢迎页","无线欢迎页设置",""};
	private setting_view settingview = null;
	private int type = -1;

	/**
	 * 状态
	 */
	public static final String  STATE = "WelcomeSet";

	/**
	 * 打开状态
	 */
	public static final String  OPENSTATE = "open";

	/**
	 * 关闭
	 */
	public static final String  CLOSESTATE = "close";

	public void onResume(){
		super.onResume();
		super.showMe(getClass());
		super.hideTip();
		SharedPreferences settings = getSharedPreferences(Config.getString("configFileName"), 0); 
		String state = settings.getString(STATE, "");
		if(OPENSTATE.equals(state)){
			info[3] = "0";
		}else if(CLOSESTATE.equals(state)){
			info[3] = "1";
		}
		this.settingview.setSettingInfo(info);
		settingview.requestFocus();
	}	
	protected void onPause() {
		super.onPause();
		closePopmenu();
	}

	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.welcomsetstyle2);
		super.onCreate(savedInstanceState);
		settingview = (setting_view) this.findViewById(R.id.settingview);
		settingview.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int focus_idx = settingview.getFocusIndex();
				switch(focus_idx)
				{
				case setting_view.OKBTN:
					type = settingview.getSelIndex();
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

					}else if(type == 0){

						editor.putString(STATE, OPENSTATE);  
						editor.commit(); 
					}else if(type == 1){

						editor.putString(STATE, CLOSESTATE);  
						editor.commit(); 

					}
					sendBroadcast(new Intent(MainpageActivity.BACK));
					break;
				case setting_view.CANCELBTN:
					sendBroadcast(new Intent(MainpageActivity.BACK));
					break;

				case setting_view.ERROR:
					break;
				}
			}
		});
	}
}
