package com.pvi.ap.reader.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.data.common.Logger;

/**
 * 系统设置主界面类<br>
 * @author 彭见宝
 * @since 2010-11-5
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 * @since 2011-04-02 @author 刘剑雄
 */
public class SystemConfigActivity extends PviActivity {
	
	//private int themeNum = -1;
	
	private static final String TAG = "SystemConfig";
   SystemView system=null;
   private int deviceType;
   String[] stat=null;
   Context mContext = SystemConfigActivity.this;
   private Handler mHandler = new Handler();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
       
    	super.onCreate(savedInstanceState);
    	GlobalVar appState = ((GlobalVar) getApplicationContext());
     	deviceType=appState.deviceType;
     	// system=new SystemView(SystemConfigActivity.this);
    	this.setContentView(R.layout.systemconfig);
    	system=(SystemView)findViewById(R.id.sysconfig);
    	system.requestFocus();
    	system.setOnKeyListener(new View.OnKeyListener(){

			@Override
			public boolean onKey(View v, int keycode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction()==KeyEvent.ACTION_DOWN){
					int focus_id=system.getFocusIndex();
					if(keycode == KeyEvent.KEYCODE_DPAD_CENTER||keycode == KeyEvent.KEYCODE_ENTER)
					{
						v.performClick();
						return true;
					}
					if(keycode==KeyEvent.KEYCODE_DPAD_LEFT){
						if(focus_id > 0)
						{
							focus_id--;
						}
						else
						{
							focus_id = 0;
						}
						Logger.e(TAG, "focus_id= " + focus_id);
						system.setFocusIndex(focus_id);
						system.refresh();
						return true;
					}
					if(keycode==KeyEvent.KEYCODE_DPAD_RIGHT){
						if(focus_id < system.getItemNum()-1)
						{
							focus_id++;
						}
						else
						{
							focus_id = system.getItemNum()-1;
						}
						Logger.e(TAG, "focus_id= " + focus_id);
						system.setFocusIndex(focus_id);
						system.refresh();
						return true;
					}
					else if(keycode == KeyEvent.KEYCODE_DPAD_UP)
					{
						if(focus_id==4){
							system.setFocusIndex(0);
							system.refresh();
						}else if(focus_id==5){
							system.setFocusIndex(1);
							system.refresh();
						}else if(focus_id==6){
							system.setFocusIndex(2);
							system.refresh();
						}else if(focus_id==7){
							system.setFocusIndex(3);
							system.refresh();
						}else if(focus_id==8){
							system.setFocusIndex(4);
							system.refresh();
						}
						return true;
					}
					else if(keycode == KeyEvent.KEYCODE_DPAD_DOWN)
					{
						if(focus_id==0){
							system.setFocusIndex(4);
							system.refresh();
						}else if(focus_id==1){
							system.setFocusIndex(5);
							system.refresh();
						}else if(focus_id==2){
							system.setFocusIndex(6);
							system.refresh();
						}else if(focus_id==3){
							system.setFocusIndex(7);
							system.refresh();
						}else if(focus_id==4){
							system.setFocusIndex(8);
							system.refresh();
						}else if(focus_id==5){
							system.setFocusIndex(8);
							system.refresh();
						}else if(focus_id==6){
							system.setFocusIndex(8);
							system.refresh();
						}else if(focus_id==7){
							system.setFocusIndex(8);
							system.refresh();
						}
						else if(focus_id==8){
							return false;
						}
						return true;
					}
				}
				return false;
			}
    		
    	});
    	system.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.requestFocus();
				v.invalidate();
				switch(system.getFocusIndex()){
				case 0:{
					Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
					Bundle sndbundle = new Bundle();
					sndbundle.putString("pviapfStatusTip", "进入时间设置");
					msgIntent.putExtras(sndbundle);
					sendBroadcast(msgIntent);
					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				    Bundle bundleToSend = new Bundle();
	                bundleToSend.putString("act","com.pvi.ap.reader.activity.TimeSetActivity");
	                bundleToSend.putString("haveMenuBar","1");    
	               // bundleToSend.putString("mainTitle",  "时间设置");
	                bundleToSend.putString("startType",  "allwaysCreate");
	                tmpIntent.putExtras(bundleToSend);
	                sendBroadcast(tmpIntent);
				};break;
				case 1:{
					Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
					Bundle sndbundle = new Bundle();
					sndbundle.putString("pviapfStatusTip", "进入开机设置");
					msgIntent.putExtras(sndbundle);
					sendBroadcast(msgIntent);
					
			    	Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				    Bundle bundleToSend = new Bundle();
	                bundleToSend.putString("act","com.pvi.ap.reader.activity.StartUpActivity");
	                //bundleToSend.putString("haveTitleBar","1");
	                bundleToSend.putString("haveMenuBar","1");    
	               // bundleToSend.putString("mainTitle",  "时间设置");
	              //  bundleToSend.putString("pviapfStatusTip",getResources().getString(R.string.goto_timeset));
	                bundleToSend.putString("startType",  "allwaysCreate");
	                tmpIntent.putExtras(bundleToSend);
	                sendBroadcast(tmpIntent);
				};break;
				case 2:{
					Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
					Bundle sndbundle = new Bundle();
					sndbundle.putString("pviapfStatusTip", "进入待机设置");
					msgIntent.putExtras(sndbundle);
					sendBroadcast(msgIntent);
					String gotoActivity = "com.pvi.ap.reader.activity.TimeSetActivity";
			    	Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				    Bundle bundleToSend = new Bundle();
	                bundleToSend.putString("act","com.pvi.ap.reader.activity.StandbyActivity");
	                bundleToSend.putString("haveMenuBar","1");    
	                //bundleToSend.putString("mainTitle",  "时间设置");
	                bundleToSend.putString("startType",  "allwaysCreate");
	                tmpIntent.putExtras(bundleToSend);
	                sendBroadcast(tmpIntent);
				};break;
				case 3:{
					Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
					Bundle sndbundle = new Bundle();
					sndbundle.putString("pviapfStatusTip", "进入欢迎页设置");
					msgIntent.putExtras(sndbundle);
					sendBroadcast(msgIntent);
					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				    Bundle bundleToSend = new Bundle();
	                bundleToSend.putString("act","com.pvi.ap.reader.activity.WelcomeSetActivity");
	                bundleToSend.putString("haveMenuBar","1");    
	               // bundleToSend.putString("mainTitle",  "时间设置");
	                bundleToSend.putString("startType",  "allwaysCreate");
	                tmpIntent.putExtras(bundleToSend);
	                sendBroadcast(tmpIntent);
				};break;
				case 4:{
					Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
					Bundle sndbundle = new Bundle();
					sndbundle.putString("pviapfStatusTip", "进入联网设置");
					msgIntent.putExtras(sndbundle);
					sendBroadcast(msgIntent);
					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				    Bundle bundleToSend = new Bundle();
	                bundleToSend.putString("act","com.pvi.ap.reader.activity.NetSetActivity");
	                bundleToSend.putString("haveMenuBar","1");    
	               // bundleToSend.putString("mainTitle",  "时间设置");
	                //bundleToSend.putString("startType",  "allwaysCreate");
	                tmpIntent.putExtras(bundleToSend);
	                sendBroadcast(tmpIntent);
				};break;
				case 5:{
					Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
					Bundle sndbundle = new Bundle();
					sndbundle.putString("pviapfStatusTip", "进入设备信息");
					msgIntent.putExtras(sndbundle);
					sendBroadcast(msgIntent);
					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				    Bundle bundleToSend = new Bundle();
	                bundleToSend.putString("act","com.pvi.ap.reader.activity.ConfigHandsetInfoActivity");
	                bundleToSend.putString("haveMenuBar","1");    
	               // bundleToSend.putString("mainTitle",  "时间设置");
	                bundleToSend.putString("startType",  "allwaysCreate");
	                tmpIntent.putExtras(bundleToSend);
	                sendBroadcast(tmpIntent);
				};break;
				case 6:{
					Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
					Bundle sndbundle = new Bundle();
					sndbundle.putString("pviapfStatusTip", "进入预订提醒");
					msgIntent.putExtras(sndbundle);
					sendBroadcast(msgIntent);
					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				    Bundle bundleToSend = new Bundle();
	                bundleToSend.putString("act","com.pvi.ap.reader.activity.SubscribeRemindActivity");
	                bundleToSend.putString("haveMenuBar","1");    
	               // bundleToSend.putString("mainTitle",  "时间设置");
	                //bundleToSend.putString("startType",  "allwaysCreate");
	                tmpIntent.putExtras(bundleToSend);
	                sendBroadcast(tmpIntent);
				};break;
				case 7:{
					Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
					Bundle sndbundle = new Bundle();
					sndbundle.putString("pviapfStatusTip", "进入出厂设置");
					msgIntent.putExtras(sndbundle);
					sendBroadcast(msgIntent);
					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				    Bundle bundleToSend = new Bundle();
	                bundleToSend.putString("act","com.pvi.ap.reader.activity.RecoveryFactoryActivity");
	                bundleToSend.putString("haveMenuBar","1");    
	               // bundleToSend.putString("mainTitle",  "时间设置");
	                bundleToSend.putString("startType",  "allwaysCreate");
	                tmpIntent.putExtras(bundleToSend);
	                sendBroadcast(tmpIntent);
				};break;
				case 8:{
					Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
					Bundle sndbundle = new Bundle();
					sndbundle.putString("pviapfStatusTip", "进入存储空间");
					msgIntent.putExtras(sndbundle);
					sendBroadcast(msgIntent);
					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				    Bundle bundleToSend = new Bundle();
	                bundleToSend.putString("act","com.pvi.ap.reader.activity.StorageStatActivity");
	                bundleToSend.putString("haveMenuBar","1");    
	               // bundleToSend.putString("mainTitle",  "时间设置");
	                bundleToSend.putString("startType",  "allwaysCreate");
	                tmpIntent.putExtras(bundleToSend);
	                sendBroadcast(tmpIntent);
				};break;
				}
			}
    		
    	});

    	
 	
		
    }
    

    Runnable checkModemReady = new Runnable(){

        @Override
        public void run() {                
            
                if("SIM".equals(((GlobalVar)getApplication()).getSimType()) ||"USIM".equals(((GlobalVar)getApplication()).getSimType())){

                    if(stat!=null){
                    stat[4]="获取中";
                    system.setStat(stat);
                    setUpdates(true);
                    startService("WHAT");                    
                    }

                } else{
                    if(stat!=null){
                        stat[4]="未开启";
                        system.setStat(stat);
                   
                     }
                    mHandler.postDelayed(this, 3000);       //3s后重新检测一次
                }                    

            
        }};
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();   	

    	
    	if(deviceType==1){
			//this.getParent().getWindow().getDecorView().invalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
		}
    	
		Resources resources = getResources();
    	SharedPreferences settings = getSharedPreferences(Constants.configFileName, 0); 
		String welcome = settings.getString(WelcomeSetActivity.STATE, "");
		String startUp = settings.getString(StartUpActivity.STATE, "");
        //String netSet = settings.getString(NetSetActivity.STATE, "");
		String language = settings.getString(LanguageSetActivity.Language, "");
		String curtype=settings.getString(SubscribeRemindActivity.STATE,"");
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		String dataStr = df.format(new Date());
		stat=new String[9];
		
		checkModemReady.run();//检测联网方式		
		
		stat[0]=dataStr;
		if(StartUpActivity.MAINPAGE.equals(startUp)){
			stat[1]=resources.getString(R.string.startup_mainpage);
		}else if(StartUpActivity.LASTREAD.equals(startUp)){
			stat[1]=resources.getString(R.string.startup_lastread);
		}
		int standby_AUTO =Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 0);
		if(standby_AUTO==-1){
			stat[2]=resources.getString(R.string.standby_auto_close);
		}else if(standby_AUTO==10*60*1000){
			stat[2]=resources.getString(R.string.standby_auto_5);
		}else if(standby_AUTO==30*60*1000){
			stat[2]=resources.getString(R.string.standby_auto_20);
		}else if(standby_AUTO==2*60*1000){
			stat[2]="2分钟";
		}else if(standby_AUTO==1*60*1000){
			stat[2]="1分钟";
		}else if(standby_AUTO==30*1000){
			stat[2]="30秒";
		}else{
			stat[2]=resources.getString(R.string.standby_auto_close);
			Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, -1);
		}
		
		Logger.v("standby_AUTO", "standby_AUTO="+standby_AUTO);
		
		if(WelcomeSetActivity.OPENSTATE.equals(welcome)){
			stat[3]=resources.getString(R.string.welcome_open);
		}else if(WelcomeSetActivity.CLOSESTATE.equals(welcome)){
			stat[3]=resources.getString(R.string.welcome_close);
		}
		
/*		Logger.v("netSet", netSet);
		if(NetSetActivity.AUTO.equals(netSet)){
			stat[4]=resources.getString(R.string.net_auto);
		}else if(NetSetActivity.TD.equals(netSet)){
			stat[4]=resources.getString(R.string.net_td);
		}else if(NetSetActivity.EDGE.equals(netSet)){
			stat[4]=resources.getString(R.string.net_edge);
		}*/
	
//		if(LanguageSetActivity.ENGLISH.equals(language)){
//			propertis[9].setText(resources.getString(R.string.language_english));
//		}else if(LanguageSetActivity.S_CHINESE.equals(language)){
//			propertis[9].setText(resources.getString(R.string.language_s_chinese));
//		}else if(LanguageSetActivity.T_CHINESE.equals(language)){
//			propertis[9].setText(resources.getString(R.string.language_t_chinese));
//		}
		

		Logger.v("curtype", "curtype="+curtype);
		if (curtype.equals("1")) {
			stat[6]=resources.getString(R.string.subscriberemind_ontime);
		} else if (curtype.equals("2")) {
			stat[6]=resources.getString(R.string.subscriberemind_intime);
		} else if (curtype.equals("3")) {
			stat[6]=resources.getString(R.string.subscriberemind_after24hours);
		}
		stat[5]="";stat[7]="";stat[8]="";
		system.setStat(stat);
		   Intent tmpIntent = new Intent(
	                MainpageActivity.SHOW_ME);
	        Bundle bundleToSend = new Bundle();
	      
	        bundleToSend.putString("sender", SystemConfigActivity.this.getClass().getName()); //TAB内嵌activity类的全名
	        tmpIntent.putExtras(bundleToSend);
	        sendBroadcast(tmpIntent);
	        tmpIntent = null;
	        bundleToSend = null;
		sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
//		
//    	//imageButton[0].requestFocus();
    }
    
    private boolean mUpdating =false;
    
    void setUpdates(boolean update) {
        if (update != mUpdating) {
            mUpdating = update;
            if (update) {
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_MAIN);
                mContext.registerReceiver(mbr, filter);
                
            }else{
                mContext.unregisterReceiver(mbr);
    }}}
    
    private BroadcastReceiver mbr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            final String ret ;
            if (b != null) {
                ret = b.getString("RETURN") ;
                //Logger.e(TAG,"在系统设置界面，接收到返回值:"+ret);
            } else {
                ret = null ;
            }
            
            if(ret == null){
                return ;
            }
       
            
            //处理查询返回值
            if("AUTO".equals(ret.toUpperCase())){
                stat[4]="自动切换";

            }
            else if("TD-SCDMA".equals(ret.toUpperCase())){
                stat[4]="TD";

            }
            else if("GSM".equals(ret.toUpperCase())){
                stat[4]="EDGE";

            }        
            
            //查询或设置出错！
            
            else if("ERROR".equals(ret.toUpperCase())){
                stat[4]="未知";
                
            }
            system.setStat(stat);
            
        }

    };

    @Override
    protected void onPause() {
        setUpdates(false);
        super.onPause();
    }
   
    
    /***
     * Add  by Elvis at 2011-5-20 start
     */
    /**
     * 添加对网络的设置
     */
   private void startService(String net) {
       Intent intnet = new Intent("com.eink.system.set.ReadSimInfomation");  
       Bundle extras = new Bundle();
       //Logger.e(TAG,"去查询或设置联网方式:"+net);
       extras.putString("net", net);
       intnet.putExtras(extras);
       startService(intnet);
   }
    
}
