package com.pvi.ap.reader.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Logger;

/**
 * 屏保设置类<br>
 * @author 彭见宝
 * @since 2010-11-5
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 * @since 2011-04-02 @author 刘剑雄
 */
public class StandbyActivity extends PviActivity {
	
	
	public final static String TAG = "StandbyActivity";

	//private int themeNum = -1;
    private Button m_okButton = null;
	private Button m_returnButton = null;
    private int type = -1;
	private int deviceType;
	private StandbyView stand=null;
	/**
	 * 自动屏保状态
	 */
	public static final String  AUTO_STATE = "auto_state";
	
	/**
	 * 自动屏保关闭状态
	 */
	public static final String  AUTO_STATE_CLOSE = "close";
	/**
	 * 自动屏保状态--5分钟
	 */
	public static final String  AUTO_STATE_5 = "5";
	/**
	 * 自动屏保状态--20分钟
	 */
	public static final String  AUTO_STATE_20 = "20";
	
	
	/**
	 * 图片切换时间
	 */
	public static final String  CHANGE_STATE = "change_state";
	
	/**
	 *  图片切换时间--3分钟
	 */
	public static final String  CHANGE_STATE_3 = "3";
	/**
	 *  图片切换时间--15分钟
	 */
	public static final String  CHANGE_STATE_15 = "15";
	/**
	 *  图片切换时间--30分钟
	 */
	public static final String  CHANGE_STATE_30 = "30";
	

	
	public void initselect(){
	    type = -1;
	    
		}
	
	public void selectAutoClose(){
		if(type != 0){
			type = stand.getFocusIndex();
			 stand.setFocusIndex(0);
				stand.refresh();  
			}
	}
	
	public void selectAuto5(){
		if(type != 1){
			type = stand.getFocusIndex();
			 stand.setFocusIndex(1);
				stand.refresh();
			}
	}
	public void selectAuto20(){
		if(type != 2){
			type = stand.getFocusIndex();
			 stand.setFocusIndex(2);
				stand.refresh();
			}
	}
	
	public void selectchange3(){
		if(type != 3){
			type = stand.getFocusIndex();
			 stand.setFocusIndex(3);
				stand.refresh();
			}
	}
	
	public void selectchange15(){
		if(type != 4){
			type = stand.getFocusIndex();
			 stand.setFocusIndex(4);
				stand.refresh();
			
			
			}
	}
	public void selectchange30(){
		if(type != 5){
			type = stand.getFocusIndex();
			 stand.setFocusIndex(5);
				stand.refresh();
	    }
	}

	
	public void initUI(){
		stand=(StandbyView)findViewById(R.id.stand);
		m_okButton = (Button) findViewById(R.id.ok);
		m_returnButton = (Button) findViewById(R.id.cancel);
        stand.requestFocus();
        stand.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keycode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction()==KeyEvent.ACTION_DOWN){
					int focus_id=stand.getFocusIndex();
					if(keycode==KeyEvent.KEYCODE_DPAD_LEFT){
						if(focus_id > 0)
						{
							focus_id--;
						}
						else
						{
							focus_id = 0;
						}
						Logger.v(TAG, "focus_id= " + focus_id);
						stand.setFocusIndex(focus_id);
						stand.refresh();
						type = stand.getFocusIndex();
						return true;
					}
					if(keycode==KeyEvent.KEYCODE_DPAD_RIGHT){
						if(focus_id < stand.getItemNum()-1)
						{
							focus_id++;
						}
						else
						{
							focus_id = stand.getItemNum()-1;
						}
						Logger.v(TAG, "focus_id= " + focus_id);
						stand.setFocusIndex(focus_id);
						stand.refresh();
						type = stand.getFocusIndex();
						return true;
					}else if(keycode == KeyEvent.KEYCODE_DPAD_UP)
					{
						if(focus_id==3){
							stand.setFocusIndex(0);
							stand.refresh();
							type = stand.getFocusIndex();
						}else if(focus_id==4){
							stand.setFocusIndex(1);
							stand.refresh();
							type = stand.getFocusIndex();
						}else if(focus_id==5){
							stand.setFocusIndex(2);
							stand.refresh();
							type = stand.getFocusIndex();
						}
						return true;
					}else if(keycode == KeyEvent.KEYCODE_DPAD_DOWN)
					{
						if(focus_id==0){
							stand.setFocusIndex(3);
							stand.refresh();
							type = stand.getFocusIndex();
						}else if(focus_id==1){
							stand.setFocusIndex(4);
							stand.refresh();
							type = stand.getFocusIndex();
						}else if(focus_id==2){
							stand.setFocusIndex(5);
							stand.refresh();
							type = stand.getFocusIndex();
						}else if(focus_id==3){
							return false;
						}else if(focus_id==4){
							return false;
						}else if(focus_id==5){
							return false;
						}
						return true;
					}
					
				}
				return false;
			}
		});
        stand.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.requestFocus();
				//v.invalidate();
				switch(stand.getFocusIndex()){
				case 0:selectAutoClose();break;
				case 1:selectAuto5();break;
				case 2:selectAuto20();break;
				case 3:selectchange3();break;
				case 4:selectchange15();break;
				case 5:selectchange30();break;
				}	
				
			}
		});
//        stand.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//			
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				// TODO Auto-generated method stub
//				//if(hasFocus){
//				
//					switch(stand.getFocusIndex()){
//					case 0:selectAutoClose();Logger.v("0", "0="+0);break;
//					case 1:selectAuto5();Logger.v("1", "1="+1);break;
//					case 2:selectAuto20();Logger.v("2", "2="+2);break;
//					case 3:selectchange3();Logger.v("3", "3="+3);break;
//					case 4:selectchange15();Logger.v("4", "4="+4);break;
//					case 5:selectchange30();Logger.v("5", "5="+5);break;
//					}	
//				//}
//			}
//		});
		// 增加事件
		m_okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				SharedPreferences settings = getSharedPreferences(Config.getString("configFileName"), 0); 
				SharedPreferences.Editor editor = settings.edit(); 
		
				String msg = "";
				int lateTime = 0;
				
				if(type == -1 ){
					msg = getResources().getString(R.string.standby_set_msg);
				}else{
					if(stand.getFocusIndex() == 0){
//						editor.putString(AUTO_STATE, AUTO_STATE_CLOSE);
//						editor.commit();
						Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, -1); //set the screent time out.
					int s=Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 0); //get the current system setting.
					Logger.v("s", "s="+s);
					}else if(stand.getFocusIndex() == 1){
//						editor.putString(AUTO_STATE, AUTO_STATE_5);
//						editor.commit();
						lateTime = 30 * 1000;
						Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, lateTime);
						int s=Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 0); //get the current system setting.
						Logger.v("s1", "s1="+s);
					}else if(stand.getFocusIndex() == 2){
//						editor.putString(AUTO_STATE, AUTO_STATE_20);
//						editor.commit();
						lateTime = 1 *60*1000;
						Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, lateTime);
						int s=Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 0); //get the current system setting.
						Logger.v("s2", "s2="+s);
					}else if(stand.getFocusIndex() == 3){
//						editor.putString(CHANGE_STATE, CHANGE_STATE_3);
//						editor.commit();
						lateTime = 2 *60*1000;
						Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, lateTime);
						int s=Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 0); //get the current system setting.
						Logger.v("s3", "s3="+s);
					}else if(stand.getFocusIndex() ==4){
						lateTime = 10 *60*1000;
						Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, lateTime);
						int s=Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 0); //get the current system setting.
						Logger.v("s4", "s4="+s);
					}else if(stand.getFocusIndex() == 5){
						lateTime = 30 *60*1000;
						Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, lateTime);
						int s=Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 0); //get the current system setting.
						Logger.v("s5", "s5="+s);
					}
					msg = getResources().getString(R.string.standby_set_ok);
		
				}
				
//				String serviceName = "com.pvi.ap.reader.service.ScreenSaveService" ;
//				Intent intent = new Intent(serviceName);
//				//先关闭屏保服务
//				stopService(intent);
//				
//				Bundle extra = new Bundle();
//				extra.putInt("DELAY", lateTime);
//				intent.putExtra("DELAY",extra );
//				//分别设置参数
//				ScreenSaveActivity.changeTime = changeTime;
//				if(lateTime!=0){
//					//startService(intent);
//				}
				sendBroadcast(new Intent(MainpageActivity.BACK));

        		
			}

		});
		
		// 增加事件
		m_returnButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
					//m_okButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonstyle2_1));
					//m_returnButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonstyle2_2));
				
				sendBroadcast(new Intent(MainpageActivity.BACK));
			}

		});
		
	}
	public void onResume(){
		super.onResume();
//		if(deviceType==1){
//			this.getParent().getWindow().getDecorView().invalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
//		}
		int time=Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 0); //get the current system setting.
		Logger.v("time", "time="+time);
		Intent tmpIntent = new Intent(
                MainpageActivity.SHOW_ME);
        Bundle bundleToSend = new Bundle();
      
        bundleToSend.putString("sender", StandbyActivity.this.getClass().getName()); //TAB内嵌activity类的全名
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
		GlobalVar appState = ((GlobalVar) getApplicationContext());
		deviceType=appState.deviceType;
    	setContentView(R.layout.standbysetstyle2);
    	super.onCreate(savedInstanceState);
    	
		initUI();
       }
	
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		initselect();
//		SharedPreferences settings = getSharedPreferences(Config.getString("configFileName"), 0); 
//		String state = settings.getString(AUTO_STATE, "0");
         int time=Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 0);
		if(time==-1){
			selectAutoClose();
		}else if(time==30*1000){
			selectAuto5();
		}else if(time==1*60*1000){
			selectAuto20();
		}if(time==2*60*1000){
			selectchange3();
		}else if(time==10*60*1000){
			selectchange15();
		}else if(time==30*60*1000){
			selectchange30();
		}
	
		sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
		
	}

	
}