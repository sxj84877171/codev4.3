package com.pvi.ap.reader.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.pvi.ap.reader.data.common.Constants;

/**
 * ��תģʽ���ý�����<br>
 * @author �����
 * @since 2010-11-5
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class RotatingActivity extends PviActivity {
	
	/**
	 * ��ѡ��
	 */
	private RadioButton m_openButton = null;
	/**
	 * �ر�ѡ��
	 */
	private RadioButton m_closeButton = null;
	/**
	 * ȷ����ť
	 */
	private Button m_okButton = null;
	/**
	 * ���ذ�ť
	 */
	private Button m_returnButton = null;
	/**
	 * ������ʾ��رհ�ť
	 */
	private ImageView m_popPrompt_close = null;
	/**
	 * ������ʾ��
	 */
	private LinearLayout m_popPrompt = null;
	
	/**
	 * ������ʾ������
	 */
	private TextView m_popPrompt_context = null;
	
	/**
	 * ������ʾ���ʱ��
	 */
	private int m_popPrompt_openTime = 2000;
	
	/**
	 * ������ʾ��ر�ʱ��
	 */
	private int m_popPrompt_closeTime = 2000;
	
	
	/**
	 * ״̬
	 */
	public final static String  STATE = "RotatingSet";
	
	/**
	 * ��״̬
	 */
	public final static String  OPENSTATE = "open";
	
	/**
	 * �ر�
	 */
	public final static String  CLOSESTATE = "close";
	

	
	public void onCreate(Bundle savedInstanceState) {
    	
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.rotatingset);
	        
	        	
	    }
	    
	    class MyThread implements Runnable{
	    	
	    
	    	public void run(){
	    		
	    	
	    		//��ʼ��UI
	    		m_openButton = (RadioButton)findViewById(R.id.rotating_open);
	    		m_closeButton = (RadioButton)findViewById(R.id.rotating_close);
	    		m_okButton = (Button)findViewById(R.id.rotating_ok);
	    		m_returnButton = (Button)findViewById(R.id.rotating_return);
	    		m_popPrompt = (LinearLayout)findViewById(R.id.rotating_popPrompt);
	    		m_popPrompt_close = (ImageView)findViewById(R.id.rotating_popPrompt_close);
	    		m_popPrompt_context = (TextView)findViewById(R.id.rotating_popPrompt_context);
	    		
	    		//��ȡ���ݣ����ô򿪻�رհ�ť��״̬
	    		SharedPreferences settings = getSharedPreferences(Config.getString("configFileName"), 0); 
	    		String state = settings.getString(STATE, "");
	    		//System.out.println("STATE:======="+state);
	    		m_openButton.setChecked(false);
	    		m_closeButton.setChecked(false);
	    		if(OPENSTATE.equals(state)){
	    			m_openButton.setChecked(true);
	    		}else if(CLOSESTATE.equals(state)){
	    			m_closeButton.setChecked(true);
	    		}
	    		
	    		//�����¼�
	    		m_okButton.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						
						SharedPreferences settings = getSharedPreferences(Config.getString("configFileName"), 0); 
						SharedPreferences.Editor editor = settings.edit(); 
						//AlphaAnimation myAnimation_Alpha = new AlphaAnimation(0.1f, 1.0f); 
						//myAnimation_Alpha.setDuration(m_popPrompt_openTime); 
						String msg = "";
						if(m_openButton.isChecked()){
							msg = getResources().getString(R.string.rotating_set_open_ok);
							//ѡ�����ô�
							//m_popPrompt_context.setText(msg);
							
							//��������
							editor.putString(STATE,OPENSTATE);  
							editor.commit(); 
							
						}else if(m_closeButton.isChecked()){
							msg = getResources().getString(R.string.rotating_set_close_ok);
							//ѡ�����ùر�
							//m_popPrompt_context.setText(msg);
							
							//��������
							editor.putString(STATE, CLOSESTATE);  
							editor.commit(); 
						}else{
							
							msg = getResources().getString(R.string.rotating_set_msg);
							//ѡ�����ùر�
							//m_popPrompt_context.setText(msg);
							
						}
						PviAlertDialog dialog = new PviAlertDialog(getParent());
		        		dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
		        		dialog.setCanClose(true);
		        		dialog.setMessage(msg);// �����Զ���Ի������ʽ
		        		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.system_soft_unauthorized),
		                         new android.content.DialogInterface.OnClickListener() {

		                             @Override
		                             public void onClick(DialogInterface dialog,
		                                     int which) {
		                                 // TODO Auto-generated method stub
		                            	 dialog.dismiss();
		                             }

		                         });
		        		// ��ʾ�Ի���
		        		dialog.show();
						//m_popPrompt.startAnimation(myAnimation_Alpha);
						//m_popPrompt.setVisibility(View.VISIBLE);
						
						
					}
	    			
	    		});
	    		//�����¼�
	    		m_returnButton.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						
						//���ص�ϵͳ���ý���
						//Intent intent = new Intent();
	         			//intent.setClass(WelcomeSetActivity.this, SystemConfigActivity.class);
	         			//startActivity(intent);
						//���ص�ϵͳ���ý���
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
	    		//�����¼�
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
	    		//��ʼ��UI��ɺ󣬷�����Ϣ
	    		Message msg=new Message();

	    		Bundle b=new Bundle();
	    		
	    		b.putString("msg", "over");

	    		msg.setData(b);

	    		myHandler.sendMessage(msg);//ͨ��sendMessage��Handler���͸���UI����Ϣ
				*/
	    	}
	    }

	    @Override
	    public boolean onKeyUp(int keyCode, KeyEvent event) {
	        // TODO Auto-generated method stub
	       if(keyCode == KeyEvent.KEYCODE_BACK){
	           //֪ͨ��ܷ�����һ����activty
	           sendBroadcast(new Intent(MainpageActivity.BACK));
	             return false;
	       }
	        return super.onKeyUp(keyCode, event);
	    };
	    
	    @Override
	    protected void onStart() {
	    	super.onStart();
	    	//���������߳�
	    	Thread uiLoadDataThread = new Thread(new MyThread());
	        uiLoadDataThread.start();
	    }
}
