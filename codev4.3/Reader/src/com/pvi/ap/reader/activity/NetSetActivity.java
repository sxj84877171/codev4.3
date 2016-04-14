package com.pvi.ap.reader.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.data.common.Logger;

/**
 * 联网方式设置界面类<br>
 * @author 彭见宝
 * @since 2010-11-5
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 * @since 2011-04-02 @author 刘剑雄
 */
public class NetSetActivity extends PviActivity {
	
	public final static String TAG = "NetSetActivity";
	private Button m_okButton = null;
	private Button m_returnButton = null;
	public final static String  STATE = "NetSet";
	public final static String  AUTO = "AUTO";
	public final static String  TD = "TD";
	public final static String  EDGE = "EDGE";
	private int old = 1 ;
	private int old1 = 1 ;	
	private int type=-1;
	private int deviceType;
	private NetSetView netset=null;
	
	private String newSet= "";
	private Handler mHandler = new Handler();
	private Context mContext = NetSetActivity.this;
	
	private  BroadcastReceiver mbr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            final String ret ;
            if (b != null) {
                ret = b.getString("RETURN") ;
                //Logger.e(TAG,"MODEM 返回值："+ret);
            } else {
                ret = null ;
            }
            
            if(ret == null){
                return ;
            }
/*              SharedPreferences setting= getSharedPreferences(Constants.configFileName, 0); 
            SharedPreferences.Editor edit = setting.edit(); 
            if("AUTO".equals(ret.toUpperCase())){
                selectAutoNet();
                old = type ;
                old1 = type ;
                edit.putString(STATE,AUTO);  
                edit.commit();
                return ;
            }
            if("TD-SCDMA".equals(ret.toUpperCase())){
                selectTdNet();
                old = type ;old1 = type ;
                edit.putString(STATE,TD);  
                edit.commit();
                return ;
            }
            if("GSM".equals(ret.toUpperCase())){
                selectEdgeNet();
                old = type ;old1 = type ;
                edit.putString(STATE,EDGE);  
                edit.commit();
                return ;
            }*/
            
            
            //处理查询返回值
            if("AUTO".equals(ret.toUpperCase())){
                netset.setCur("自动切换");

            }
            else if("TD-SCDMA".equals(ret.toUpperCase())){
                netset.setCur("TD");

            }
            else if("GSM".equals(ret.toUpperCase())){
                netset.setCur("EDGE");

            }
            
            
            
            //处理设置返回值
            
            else if(ret != null && ret.toUpperCase().equals("OK")){
                
                //显示新值...
                netset.setCur(newSet);
                
                PviAlertDialog pad = new PviAlertDialog(getParent());
                pad.setTitle("温馨提示");
                pad.setMessage("联网方式设置成功！");
                pad.setButton(getString(R.string.OK),new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {/*
                        if(!"ERROR".equals(ret.toUpperCase())){
                            sendBroadcast(new Intent(MainpageActivity.BACK));
                        }else{
                            old = old1 ;
                            if(old1 == 1){selectAutoNet();}
                            if(old1 == 3){selectEdgeNet();}
                            if(old1 == 2){selectTdNet();}
                        }
                    */}
                    
                });
                pad.show();
                /*              
                
                SharedPreferences settings = getSharedPreferences(Constants.configFileName, 0); 
                SharedPreferences.Editor editor = settings.edit(); 

                String msg = "";
                old1 = old ;
                if(old==1){
                    msg = getResources().getString(R.string.net_set_auto_ok);
                    //m_popPrompt_context.setText(msg);
                    
                    //保存数据
                    editor.putString(STATE,AUTO);  
                    editor.commit(); 
                    
                }else if(old==2){
                    msg = getResources().getString(R.string.net_set_td_ok);
                    //m_popPrompt_context.setText(msg);
                    
                    //保存数据
                    editor.putString(STATE, TD);  
                    editor.commit(); 
                }else if(old==3){
                    msg = getResources().getString(R.string.net_set_edge_ok);
                    //m_popPrompt_context.setText(msg);
                    
                    //保存数据
                    editor.putString(STATE, EDGE);  
                    editor.commit(); 
                }else{
                    msg = getResources().getString(R.string.net_set_msg);
                    
                }
            */}
            
            //查询或设置出错！
            
            else if("ERROR".equals(ret.toUpperCase())){
                netset.setCur("查询或设置失败");
                /*PviAlertDialog pad = new PviAlertDialog(getParent());
                pad.setTitle("温馨提示");
                pad.setMessage("联网方式获取或设置失败!");
                pad.setButton(getString(R.string.OK),new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if(!"ERROR".equals(ret.toUpperCase())){
                            sendBroadcast(new Intent(MainpageActivity.BACK));
                        }else{
                            old = old1 ;
                            if(old1 == 1){selectAutoNet();}
                            if(old1 == 3){selectEdgeNet();}
                            if(old1 == 2){selectTdNet();}
                        }
                    }
                    
                });
                pad.show();*/
            }
            
        }

    };
	
	
   /**
    * 选择自动切换网络方式
    */
   public void selectAutoNet(){
	   if(type!=0){
		   type = netset.getFocusIndex();
		   netset.setFocusIndex(0);
		   netset.refresh();
	   
	   }
   }
   /**
    * 选择TD网络方式
    */
   public void selectTdNet(){
	   if(type!=1){
		   type = netset.getFocusIndex();
		   netset.setFocusIndex(1);
		   netset.refresh();
		  
	   }
   }
   /**
    * 选择EDGE网络方式
    */
   public void selectEdgeNet(){
	   if(type!=2){
		   type = netset.getFocusIndex();
		   netset.setFocusIndex(2);
		   netset.refresh();
		  
	   }
   }

	public void onCreate(Bundle savedInstanceState) {
		   setContentView(R.layout.netsetstyle2);
		   GlobalVar appState = ((GlobalVar) getApplicationContext());
			deviceType=appState.deviceType;
	        super.onCreate(savedInstanceState);
	       
	        init();
	    }
	 /**
	  * 初始化
	  */
	    public void init(){
	    	
    	  
	    	netset=(NetSetView)findViewById(R.id.netset);
    		m_okButton = (Button)findViewById(R.id.ok);
    		m_returnButton = (Button)findViewById(R.id.cancel);
    		netset.requestFocus();
    		netset.setOnKeyListener(new View.OnKeyListener() {
    				
    				@Override
    				public boolean onKey(View v, int keycode, KeyEvent event) {
    					// TODO Auto-generated method stub
    					if(event.getAction()==KeyEvent.ACTION_DOWN){
    						int focus_id=netset.getFocusIndex();
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
    							netset.setFocusIndex(focus_id);
    							netset.refresh();
    							type = netset.getFocusIndex();
    							return true;
    						}
    						if(keycode==KeyEvent.KEYCODE_DPAD_RIGHT){
    							if(focus_id < netset.getItemNum()-1)
    							{
    								focus_id++;
    							}
    							else
    							{
    								focus_id = netset.getItemNum()-1;
    							}
    							Logger.v(TAG, "focus_id= " + focus_id);
    							netset.setFocusIndex(focus_id);
    							netset.refresh();
    							type = netset.getFocusIndex();
    							return true;
    						}else if(keycode == KeyEvent.KEYCODE_DPAD_UP)
    						{
    							
    							return true;
    						}else if(keycode == KeyEvent.KEYCODE_DPAD_DOWN)
    						{
    							if(focus_id==0){
    								return false;
    							}else if(focus_id==1){
    								return false;
    							}else if(focus_id==2){
    								return false;
    							}
    							return true;
    						}
    						
    					}
    					return false;
    				}
    			});
    		netset.setOnClickListener(new View.OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					// TODO Auto-generated method stub
    					v.requestFocus();
    					//v.invalidate();
    					switch(netset.getFocusIndex()){
    					case 0:selectAutoNet();;break;
    					case 1:selectTdNet();break;
    					case 2:selectEdgeNet();break;
    					
    					}	
    					
    				}
    			});

    		
	    }
	    

	    Runnable checkModemReady = new Runnable(){

            @Override
            public void run() {                
                
                    if("SIM".equals(((GlobalVar)getApplication()).getSimType()) ||"USIM".equals(((GlobalVar)getApplication()).getSimType())){

                        setUpdates(true);//设置查询结果监听
                        netset.setCur("获取中...");
                        startService("WHAT");//查询
                    } else{

                        //给出提示信息
                        netset.setCur("网络未开启或SIM卡未插好"); 
                        mHandler.postDelayed(this, 3000);       //3s后重新检测一次
                    }                    

                
            }};
	    
	    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		setUI();
		
		netset.setTitle("联网方式");
		String []setTime=new String[]{"自动切换","TD","EDGE"};
		netset.setSetTime(setTime);
		
		checkModemReady.run();
		

		
		
//		if(deviceType==1){
//			this.getParent().getWindow().getDecorView().invalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
//		}
		
		showMe(this.getClass());
		
	}
	    /**
	     * 设置UI
	     */
     public void setUI(){
    		
 		//获取数据，设置打开或关闭按钮的状态
/* 		SharedPreferences settings = getSharedPreferences(Constants.configFileName, 0); 
 		String state = settings.getString(STATE, "");
 		Logger.v("state", state);
 		Intent tmpIntent = new Intent(
                MainpageActivity.SHOW_ME);
        Bundle bundleToSend = new Bundle();
      
        bundleToSend.putString("sender", NetSetActivity.this.getClass().getName()); //TAB内嵌activity类的全名
        tmpIntent.putExtras(bundleToSend);
        sendBroadcast(tmpIntent);
        tmpIntent = null;
        bundleToSend = null;
// 		m_autoButton.setChecked(false);
// 		m_tdButton.setChecked(false);
 		
// 		m_edgecloseButton.setChecked(false);
 		if(AUTO.equals(state)){
 			selectAutoNet();
 		}else if(TD.equals(state)){
 			selectTdNet();
 		}else if(EDGE.equals(state)){
 			selectEdgeNet();
 		}*/
 		
 		
 		m_okButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					String net = "" ;
				switch (type) {
				case 0:
					net = "AUTO";
					newSet = "自动切换";
					break;
				case 1:
					net = "TD-SCDMA";
					newSet = "TD";
					break;
				case 2:
					net = "GSM";
					newSet = "EDGE";
					break;
				default:
					net = null;
					final PviAlertDialog pad = new PviAlertDialog(getParent());
                    pad.setCanClose(true);
                    pad.setTitle("温馨提示");
                    pad.setMessage("设置失败：请选择联网方式。");
                    pad.show();
					return;
				}
				old = type ;
				if("SIM".equals(((GlobalVar)getApplication()).getSimType()) || "USIM".equals(((GlobalVar)getApplication()).getSimType())){
				  //给出提示信息
                    showTip("正在进行设置...", 3000);                    
                    netset.setCur("保存设置...");
                    startService(net);
				}else{
				    
				    netset.setCur("网络未开启或SIM卡未插好");
					/*Intent intent = new Intent(Intent.ACTION_MAIN);
					Bundle extras = new Bundle();
					extras.putString("RETURN", "ERROR");
					intent.putExtras(extras);
					sendBroadcast(intent);*/
				    //弹出提示框：请检查SIM卡是否插好。
				    final PviAlertDialog pad = new PviAlertDialog(getParent());
	                pad.setCanClose(true);
				    pad.setTitle("温馨提示");
	                pad.setMessage("设置失败：请检查网络是否开启或SIM卡是否插好。");
	                pad.show();
				}
					
				
//					//返回到系统设置界面
//					sendBroadcast(new Intent(MainpageActivity.BACK));
//      			Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
//				    Bundle bundleToSend = new Bundle();
//	                bundleToSend.putString("act","com.pvi.ap.reader.activity.SystemConfigActivity");
//	                //bundleToSend.putString("haveTitleBar","1");
//	                bundleToSend.putString("haveMenuBar","1"); 
//	                bundleToSend.putString("startType",  "allwaysCreate");
//	                tmpIntent.putExtras(bundleToSend);
//	                sendBroadcast(tmpIntent);

					
					
				}
 			
 		});
 		
 		m_returnButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					back();
					
					//返回到系统设置界面
      			/*Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				    Bundle bundleToSend = new Bundle();
	                bundleToSend.putString("act","com.pvi.ap.reader.activity.SystemConfigActivity");
	                //bundleToSend.putString("haveTitleBar","1");
	                bundleToSend.putString("haveMenuBar","1"); 
	                bundleToSend.putString("startType",  "allwaysCreate");
	                tmpIntent.putExtras(bundleToSend);
	                sendBroadcast(tmpIntent);*/
					
				}
 			
 		});
     }


     /***
      * Add  by Elvis at 2011-5-20 start
      */
     @Override
    protected void onPause() {
        setUpdates(false);
    	super.onPause();    	
    }

     /***
      * Add by Elvis at 2011-5-20 end
      */
     
     
     
     
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
     
     /***
      * Add by Elvis at 2011-5-20 end
      */
	
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
	
}
