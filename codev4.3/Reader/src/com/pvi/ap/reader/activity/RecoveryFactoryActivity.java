package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import org.apache.commons.httpclient.HttpException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.UserInfo;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * 恢复出厂设置界面类<br>
 * @author 彭见宝
 * @since 2010-11-5
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 * @since 2011-04-02 @author 刘剑雄
 */
public class RecoveryFactoryActivity extends PviActivity {


	public final static String LOG_TAG = "RecoveryFactoryActivity";

	private setting_view settingview = null;

	private String [] info = {"恢复出厂设置","保持现有设置","恢复出厂设置",""};


	private int type=1;
	
	private Handler handler = null;
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.recoveryfactorystyle2);
		super.onCreate(savedInstanceState);
		handler= new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what == 0)
				{
					Bundle temp = msg.getData();
					String returnMsg = temp.get("MSG").toString();
					PviAlertDialog dialog = new PviAlertDialog(getParent());
					dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
					dialog.setCanClose(true);

					dialog.setMessage(returnMsg);
					dialog.show();
				}
			}

		};
		settingview = (setting_view) this.findViewById(R.id.settingview);
		settingview.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int focus_idx = settingview.getFocusIndex();
				switch(focus_idx)
				{
				case setting_view.OKBTN:
					Thread thread = new Thread(new MyThread());
					thread.start();
					break;
				case setting_view.CANCELBTN:
					//					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
					//					Bundle bundleToSend = new Bundle();
					//					bundleToSend.putString("act","com.pvi.ap.reader.activity.SystemConfigActivity");
					//					//bundleToSend.putString("haveTitleBar","1");
					//					bundleToSend.putString("haveMenuBar","1");      
					//					bundleToSend.putString("startType",  "allwaysCreate");
					//					tmpIntent.putExtras(bundleToSend);
					//					sendBroadcast(tmpIntent);
					sendBroadcast(new Intent(MainpageActivity.BACK));
					break;

				case setting_view.ERROR:
					break;
				}
			}
		});
	}

	class MyThread implements Runnable{


		public void run(){
			type = settingview.getSelIndex();
			if(type==0){
				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, -1);
				SharedPreferences settings = getSharedPreferences(Config.getString("configFileName"), 0); 
				SharedPreferences.Editor editor = settings.edit(); 
				editor.putString(WelcomeSetActivity.STATE, WelcomeSetActivity.CLOSESTATE);
				editor.putString(StartUpActivity.STATE, StartUpActivity.MAINPAGE);
				//editor.putString(StandbyActivity.AUTO_STATE, StandbyActivity.AUTO_STATE_CLOSE);
				editor.putString(StandbyActivity.CHANGE_STATE, StandbyActivity.CHANGE_STATE_3);
				editor.putString(TimeSetActivity.DATE_FORMAT, TimeSetActivity.DATE_FORMAT_24);
				editor.putString(NetSetActivity.STATE, NetSetActivity.AUTO);
				editor.putString(RefreshSetActivity.STATE, RefreshSetActivity.GC16_FLASH);
				editor.putString(RotatingActivity.STATE, RotatingActivity.CLOSESTATE);
				editor.putString(LanguageSetActivity.Language, LanguageSetActivity.ENGLISH);
				editor.putString(SoundSetActivity.STATE, SoundSetActivity.CLOSESTATE);
				editor.putString(SkinSetActivity.STATE, SkinSetActivity.SKIN1);


				//预订提醒设置
				ContentValues values = new ContentValues();
				// 保存数据
				values.put(UserInfo.BookUpdateType, 3);



				String msg = "";
				String returnMsg = "";
				HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
				HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

				ahmNamePair.put("type", String.valueOf(3));

				HashMap responseMap = null;
				try {
					responseMap = CPManager.bookUpdateSet(ahmHeaderMap,
							ahmNamePair);
					String rescode=responseMap.get("result-code").toString();
					Logger.v("rescode", "rescode="+rescode);
					if (rescode.contains("result-code: 0")) {
						editor.putString(SubscribeRemindActivity.STATE,"3");
						getContentResolver().update(UserInfo.CONTENT_URI,values, null, null);
						returnMsg = null;
					}
					else
					{
						returnMsg = Error.getErrorDescriptionForContent(rescode);
					}
				}catch (HttpException e) {
					Logger.e(LOG_TAG, e.getMessage());
					//					return; 
				}catch (SocketTimeoutException e) {
					returnMsg = "预订提醒更新到网络出现异常";
					Logger.e(LOG_TAG, e.getMessage());
					//					return;
				}catch (IOException e) {
					returnMsg = "预订提醒更新到网络出现异常";
					Logger.e(LOG_TAG, e.getMessage());
					//					return;
				}
				editor.commit();
				if(returnMsg!=null&&!returnMsg.equals(""))
				{
					Message message = new Message();
					message.what = 0;
					Bundle bundle = new Bundle();
					bundle.putString("MSG", returnMsg);
					message.setData(bundle);
				}
			}
			//返回到系统设置界面
			sendBroadcast(new Intent(MainpageActivity.BACK));
		}

	}
	public void onResume(){
		super.onResume();
		super.showMe(getClass());
		super.hideTip();	
		info[3] = "1";
		this.settingview.setSettingInfo(info);
		settingview.requestFocus();
	}	
}
