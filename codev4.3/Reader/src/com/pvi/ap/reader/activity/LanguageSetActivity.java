package com.pvi.ap.reader.activity;

import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.data.common.Logger;

/**
 * 语言设置界面类<br>
 * @author 彭见宝
 * @since 2010-11-5
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class LanguageSetActivity extends PviActivity {


	public final static String LOG_TAG = "StandbyActivity";

	//private int themeNum = -1;

	private Button m_english = null;

	private Button m_chinese_s = null;

	private Button m_chinese_t = null;


	/**
	 * 确定按钮
	 */
	private Button m_okButton = null;
	/**
	 * 返回按钮
	 */
	private Button m_returnButton = null;

	//private PopupWindow popmenu;

	//private View popmenuView;

	private int type = -1;


	TextView fp_application = null;
	TextView fp_settings = null;
	TextView fp_music = null;
	TextView fp_back = null;
	TextView fp_mean = null;


	/**
	 * 状态
	 */
	public static final String  Language = "Language";

	/**
	 * 简体中文状态
	 */
	public static final String  S_CHINESE = "S_CHINESE";

	/**
	 * 繁体中文状态
	 */
	public static final String  T_CHINESE = "T_CHINESE";

	/**
	 * 英语状态
	 */
	public static final String  ENGLISH = "ENGLISH";


	public void initselect(){

		type = -1;

		
			m_english.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
			m_chinese_s.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
			m_chinese_t.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
		

	}

	public void selectEnglish(){
		if(type != 1){
			type = 1;

			
				m_english.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));	
				m_chinese_s.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
				m_chinese_t.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
			
		}

	}

	public void selectChinese_s(){
		if(type != 2){
			type = 2;

			
				m_chinese_s.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));	
				m_english.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
				m_chinese_t.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
			
		}

	}
	public void selectChinese_t(){
		if(type != 3){
			type = 3;

			
				m_chinese_t.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));	
				m_chinese_s.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
				m_english.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
			
		}

	}



	public void initUI(){
		m_chinese_t = (Button) findViewById(R.id.language_chanese_t);
		m_chinese_s = (Button) findViewById(R.id.language_chanese_s);
		m_english = (Button) findViewById(R.id.language_english);


		m_okButton = (Button) findViewById(R.id.ok);
		m_returnButton = (Button) findViewById(R.id.cancel);
		m_chinese_t.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus==true){
					selectChinese_t();
				}
			}

		});
		m_chinese_s.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus==true){
					selectChinese_s();
				}
			}

		});
		m_english.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus==true){
					selectEnglish();
				}
			}

		});
		m_chinese_t.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectChinese_t();
			}

		});
		m_chinese_s.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectChinese_s();
			}

		});
		m_english.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectEnglish();
			}

		});

		// 增加事件
		m_okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				SharedPreferences settings = getSharedPreferences(Constants.configFileName, 0); 
				SharedPreferences.Editor editor = settings.edit(); 
				//AlphaAnimation myAnimation_Alpha = new AlphaAnimation(0.1f, 1.0f); 
				//myAnimation_Alpha.setDuration(m_popPrompt_openTime); 
				String msg = "";
				Locale locale = Locale.ENGLISH;
				if(type == 2){
					msg = getResources().getString(R.string.language_set_s_chinese_ok);
					//m_popPrompt_context.setText(msg);
					locale = Locale.SIMPLIFIED_CHINESE;
					//保存数据
					editor.putString(Language,S_CHINESE);  
					editor.commit(); 

				}else if(type == 3){
					msg = getResources().getString(R.string.language_set_t_chinese_ok);
					//m_popPrompt_context.setText(msg);
					locale = Locale.TRADITIONAL_CHINESE;
					//保存数据
					editor.putString(Language,T_CHINESE); 
					editor.commit(); 
				}else if(type == 1){
					msg = getResources().getString(R.string.language_set_english_ok);
					//m_popPrompt_context.setText(msg);
					locale = Locale.ENGLISH;
					//保存数据
					editor.putString(Language,ENGLISH);  
					editor.commit(); 
				}else{
					msg = getResources().getString(R.string.language_set_msg);
					//m_popPrompt_context.setText(msg);

				}
				Class<?> activityManagerNative;
				try {
					activityManagerNative = Class.forName("android.app.ActivityManagerNative");
					// Log.i("amnType", activityManagerNative.toString());   

					Object am=activityManagerNative.getMethod("getDefault").invoke(activityManagerNative);   
					//  Log.i("amType", am.getClass().toString());   

					Object config=am.getClass().getMethod("getConfiguration").invoke(am);   
					//  Log.i("configType", config.getClass().toString());   
					config.getClass().getDeclaredField("locale").set(config, locale);   
					config.getClass().getDeclaredField("userSetLocale").setBoolean(config, true);   
					am.getClass().getMethod("updateConfiguration",android.content.res.Configuration.class).invoke(am,config);  

					Intent i1 = new Intent("com.pvi.ap.reader.service.RestartApkService");
					Bundle b1 = new Bundle();
					//b1.putInt("pid",android.os.Process.myPid());
					b1.putString("apk_Package", "com.pvi.ap.reader");
					b1.putString("apk_Main_Activity", "com.pvi.ap.reader.activity.MainpageActivity");
					i1.putExtras(b1);
					startService(i1);
					return;


				}catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					Logger.i("LanguageSetActivity", "国际化配置异常");
				}  
			}


		});

		// 增加事件
		m_returnButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				//	m_okButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonstyle2_1));
				//	m_returnButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonstyle2_2));
				
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
		bundleToSend.putString("sender", LanguageSetActivity.this.getClass().getName()); //TAB内嵌activity类的全名
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
		tmpIntent = null;
		bundleToSend = null;
	}	

	public void onCreate(Bundle savedInstanceState) {
		GlobalVar appState = ((GlobalVar) getApplicationContext());
		
		
			setContentView(R.layout.languagesetstyle2);
		
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.languagesetstyle2);




		initUI();

		


	}



	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		initselect();

		SharedPreferences settings = getSharedPreferences(Constants.configFileName, 0); 
		String state = settings.getString(Language, "");

		if(S_CHINESE.equals(state)){
			selectChinese_s();
		}else if(T_CHINESE.equals(state)){
			selectChinese_t();
		}else if(ENGLISH.equals(state)){
			selectEnglish();
		}

		sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));

	}


}