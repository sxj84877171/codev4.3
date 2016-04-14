package com.pvi.ap.reader.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.data.common.Config;

/**
 * 刷新模式设置界面类<br>
 * @author 彭见宝
 * @since 2010-11-9
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class RefreshSetActivity extends PviActivity {

	/**
	 * GC16_NOFLASH选项
	 */
	private RadioButton m_GC16_NOFLASH = null;
	/**
	 * m_GC16_FLASH选项
	 */
	private RadioButton m_GC16_FLASH = null;
	/**
	 * 确定按钮
	 */
	private Button m_okButton = null;
	/**
	 * 返回按钮
	 */
	private Button m_returnButton = null;
	/**
	 * 操作提示框关闭按钮
	 */
	private ImageView m_popPrompt_close = null;
	/**
	 * 操作提示框
	 */
	private LinearLayout m_popPrompt = null;

	/**
	 * 操作提示框内容
	 */
	private TextView m_popPrompt_context = null;

	/**
	 * 操作提示框打开时间
	 */
	private int m_popPrompt_openTime = 2000;

	/**
	 * 操作提示框关闭时间
	 */
	private int m_popPrompt_closeTime = 2000;


	/**
	 * 状态
	 */
	public final static String  STATE = "RefreshSet";

	/**
	 * 打开状态
	 */
	public final static String  GC16_NOFLASH = "GC16_NOFLASH";

	/**
	 * 关闭
	 */
	public final static String  GC16_FLASH = "GC16_FLASH";



	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.refreshset);


	}

	class MyThread implements Runnable{


		public void run(){


			//初始化UI
			m_GC16_NOFLASH = (RadioButton)findViewById(R.id.refresh_noflash);
			m_GC16_FLASH = (RadioButton)findViewById(R.id.refresh_flash);
			m_okButton = (Button)findViewById(R.id.refresh_ok);
			m_returnButton = (Button)findViewById(R.id.refresh_return);
			m_popPrompt = (LinearLayout)findViewById(R.id.refresh_popPrompt);
			m_popPrompt_close = (ImageView)findViewById(R.id.refresh_popPrompt_close);
			m_popPrompt_context = (TextView)findViewById(R.id.refresh_popPrompt_context);

			//获取数据，设置当前状态
			SharedPreferences settings = getSharedPreferences(Config.getString("configFileName"), 0); 
			String state = settings.getString(STATE, "");
			//System.out.println("STATE:======="+state);
			m_GC16_NOFLASH.setChecked(false);
			m_GC16_FLASH.setChecked(false);
			if(GC16_NOFLASH.equals(state)){
				m_GC16_NOFLASH.setChecked(true);
			}else if(GC16_FLASH.equals(state)){
				m_GC16_FLASH.setChecked(true);
			}

			//增加事件
			m_okButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {

					SharedPreferences settings = getSharedPreferences(Config.getString("configFileName"), 0); 
					SharedPreferences.Editor editor = settings.edit(); 
					//AlphaAnimation myAnimation_Alpha = new AlphaAnimation(0.1f, 1.0f); 
					//myAnimation_Alpha.setDuration(m_popPrompt_openTime); 
					String msg = "";
					if(m_GC16_NOFLASH.isChecked()){
						msg = getResources().getString(R.string.refresh_set_noflash_ok);
						//选择设置打开
						//m_popPrompt_context.setText(msg);

						//保存数据
						editor.putString(STATE,GC16_NOFLASH);  
						editor.commit(); 

					}else if(m_GC16_FLASH.isChecked()){
						msg = getResources().getString(R.string.refresh_set_flash_ok);
						//选择设置关闭
						//m_popPrompt_context.setText(msg);

						//保存数据
						editor.putString(STATE, GC16_FLASH);  
						editor.commit(); 
					}else{

						msg = getResources().getString(R.string.refresh_set_msg);
						//选择设置关闭
						//m_popPrompt_context.setText(msg);

					}

					PviAlertDialog dialog = new PviAlertDialog(getParent());
					dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
					dialog.setCanClose(true);
					dialog.setMessage(msg);// 设置自定义对话框的样式
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

					//m_popPrompt.startAnimation(myAnimation_Alpha);
					//m_popPrompt.setVisibility(View.VISIBLE);


				}

			});
			//增加事件
			m_returnButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {

					//返回到系统设置界面
					//Intent intent = new Intent();
					//intent.setClass(WelcomeSetActivity.this, SystemConfigActivity.class);
					//startActivity(intent);
					//返回到系统设置界面
					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("act","com.pvi.ap.reader.activity.SystemConfigActivity");
					//bundleToSend.putString("haveTitleBar","1");
					bundleToSend.putString("haveMenuBar","1");  
					bundleToSend.putString("startType",  "allwaysCreate");
					tmpIntent.putExtras(bundleToSend);
					sendBroadcast(tmpIntent);

				}

			});
			//增加事件
			m_popPrompt_close.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {

					AlphaAnimation myAnimation_Alpha = new AlphaAnimation(1.0f, 0.1f); 
					myAnimation_Alpha.setDuration(m_popPrompt_closeTime);
					m_popPrompt.startAnimation(myAnimation_Alpha);
					m_popPrompt.setVisibility(View.INVISIBLE);

				}

			});
			sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
			/*
	    		//初始化UI完成后，发送消息
	    		Message msg=new Message();

	    		Bundle b=new Bundle();

	    		b.putString("msg", "over");

	    		msg.setData(b);

	    		myHandler.sendMessage(msg);//通过sendMessage向Handler发送更新UI的消息
			 */
		}
	}


	@Override
	protected void onStart() {
		super.onStart();
		//加载数据线程
		if(deviceType==1){
//			this.getParent().getWindow().getDecorView().invalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
		}
		Thread uiLoadDataThread = new Thread(new MyThread());
		uiLoadDataThread.start();
	}
}
