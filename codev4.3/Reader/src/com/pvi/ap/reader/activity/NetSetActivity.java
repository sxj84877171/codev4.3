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
 * ������ʽ���ý�����<br>
 * @author �����
 * @since 2010-11-5
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 * @since 2011-04-02 @author ������
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
                //Logger.e(TAG,"MODEM ����ֵ��"+ret);
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
            
            
            //�����ѯ����ֵ
            if("AUTO".equals(ret.toUpperCase())){
                netset.setCur("�Զ��л�");

            }
            else if("TD-SCDMA".equals(ret.toUpperCase())){
                netset.setCur("TD");

            }
            else if("GSM".equals(ret.toUpperCase())){
                netset.setCur("EDGE");

            }
            
            
            
            //�������÷���ֵ
            
            else if(ret != null && ret.toUpperCase().equals("OK")){
                
                //��ʾ��ֵ...
                netset.setCur(newSet);
                
                PviAlertDialog pad = new PviAlertDialog(getParent());
                pad.setTitle("��ܰ��ʾ");
                pad.setMessage("������ʽ���óɹ���");
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
                    
                    //��������
                    editor.putString(STATE,AUTO);  
                    editor.commit(); 
                    
                }else if(old==2){
                    msg = getResources().getString(R.string.net_set_td_ok);
                    //m_popPrompt_context.setText(msg);
                    
                    //��������
                    editor.putString(STATE, TD);  
                    editor.commit(); 
                }else if(old==3){
                    msg = getResources().getString(R.string.net_set_edge_ok);
                    //m_popPrompt_context.setText(msg);
                    
                    //��������
                    editor.putString(STATE, EDGE);  
                    editor.commit(); 
                }else{
                    msg = getResources().getString(R.string.net_set_msg);
                    
                }
            */}
            
            //��ѯ�����ó���
            
            else if("ERROR".equals(ret.toUpperCase())){
                netset.setCur("��ѯ������ʧ��");
                /*PviAlertDialog pad = new PviAlertDialog(getParent());
                pad.setTitle("��ܰ��ʾ");
                pad.setMessage("������ʽ��ȡ������ʧ��!");
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
    * ѡ���Զ��л����緽ʽ
    */
   public void selectAutoNet(){
	   if(type!=0){
		   type = netset.getFocusIndex();
		   netset.setFocusIndex(0);
		   netset.refresh();
	   
	   }
   }
   /**
    * ѡ��TD���緽ʽ
    */
   public void selectTdNet(){
	   if(type!=1){
		   type = netset.getFocusIndex();
		   netset.setFocusIndex(1);
		   netset.refresh();
		  
	   }
   }
   /**
    * ѡ��EDGE���緽ʽ
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
	  * ��ʼ��
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

                        setUpdates(true);//���ò�ѯ�������
                        netset.setCur("��ȡ��...");
                        startService("WHAT");//��ѯ
                    } else{

                        //������ʾ��Ϣ
                        netset.setCur("����δ������SIM��δ���"); 
                        mHandler.postDelayed(this, 3000);       //3s�����¼��һ��
                    }                    

                
            }};
	    
	    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		setUI();
		
		netset.setTitle("������ʽ");
		String []setTime=new String[]{"�Զ��л�","TD","EDGE"};
		netset.setSetTime(setTime);
		
		checkModemReady.run();
		

		
		
//		if(deviceType==1){
//			this.getParent().getWindow().getDecorView().invalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
//		}
		
		showMe(this.getClass());
		
	}
	    /**
	     * ����UI
	     */
     public void setUI(){
    		
 		//��ȡ���ݣ����ô򿪻�رհ�ť��״̬
/* 		SharedPreferences settings = getSharedPreferences(Constants.configFileName, 0); 
 		String state = settings.getString(STATE, "");
 		Logger.v("state", state);
 		Intent tmpIntent = new Intent(
                MainpageActivity.SHOW_ME);
        Bundle bundleToSend = new Bundle();
      
        bundleToSend.putString("sender", NetSetActivity.this.getClass().getName()); //TAB��Ƕactivity���ȫ��
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
					newSet = "�Զ��л�";
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
                    pad.setTitle("��ܰ��ʾ");
                    pad.setMessage("����ʧ�ܣ���ѡ��������ʽ��");
                    pad.show();
					return;
				}
				old = type ;
				if("SIM".equals(((GlobalVar)getApplication()).getSimType()) || "USIM".equals(((GlobalVar)getApplication()).getSimType())){
				  //������ʾ��Ϣ
                    showTip("���ڽ�������...", 3000);                    
                    netset.setCur("��������...");
                    startService(net);
				}else{
				    
				    netset.setCur("����δ������SIM��δ���");
					/*Intent intent = new Intent(Intent.ACTION_MAIN);
					Bundle extras = new Bundle();
					extras.putString("RETURN", "ERROR");
					intent.putExtras(extras);
					sendBroadcast(intent);*/
				    //������ʾ������SIM���Ƿ��á�
				    final PviAlertDialog pad = new PviAlertDialog(getParent());
	                pad.setCanClose(true);
				    pad.setTitle("��ܰ��ʾ");
	                pad.setMessage("����ʧ�ܣ����������Ƿ�����SIM���Ƿ��á�");
	                pad.show();
				}
					
				
//					//���ص�ϵͳ���ý���
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
					
					//���ص�ϵͳ���ý���
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
      * ��Ӷ����������
      */
	private void startService(String net) {
		Intent intnet = new Intent("com.eink.system.set.ReadSimInfomation");  
		Bundle extras = new Bundle();
		//Logger.e(TAG,"ȥ��ѯ������������ʽ:"+net);
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
