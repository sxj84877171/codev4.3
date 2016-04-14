package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.UserInfo;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * Ԥ���������ý�����<br>
 * 
 * @author �����
 * @since 2010-11-5
 * @version V1.0.0 (C)Copyright 2010-2013, by www.pvi.com.tw
 * @since 2011-04-02 @author ������
 */
public class SubscribeRemindActivity extends PviActivity {
	
	private static final String TAG = "SubscribeRemindActivity";
	  
	public final static String LOG_TAG = "SubscribeRemindActivity";
	
	//private int themeNum = -1;

	public final static String  STATE = "SubscribeRemind";
	private Button m_ontime = null;
	
	private Button m_intime = null;
	
	private Button m_after24hours = null;
	/**
	 * ȷ����ť
	 */
	private Button m_okButton = null;
	/**
	 * ���ذ�ť
	 */
	private Button m_returnButton = null;
	
	
	private int type = -1;
	
	private String retType;
	
	private ImageView image=null;
	private LinearLayout all=null;
	   private int deviceType;
	String errMsg = null;
    private PviAlertDialog pd;
	private Handler mHandler = new H();
	private class H extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
            case 1://�������ȡԤ����Ϣ��������ʾ
            	showAlert("���ڴ������ȡԤ����ʾ��Ϣ�����Ժ�");
            	sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
                break;
            case 2://��ȡʧ��
            	showAlert("��ȡԤ����Ϣʧ�ܣ�"+errMsg);
            	sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
                break;
            case 3://�����쳣
            	showAlert("��ȡԤ����Ϣ���������쳣�������ԣ�");
            	sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
                break;
            case 4://�ر���ʾ��
            	int curtype = -1;
            	if(pd!=null && pd.isShowing()){
    	    		pd.dismiss();
    	    	}
            	 System.out.println("=========="+retType);
                 if("1".equals(retType)){
                 	curtype = 1;
                 }else if("2".equals(retType)){
                 	curtype = 2;
                 }else if("3".equals(retType)){
                 	curtype = 3;
                 }else{
         	     
         			GlobalVar appState = (GlobalVar)getApplicationContext();
         			String useid = appState.getUserID();
         			//String where = UserInfo.UserID + " = '" + useid + "'" ;
         			initselect();
         			
         			String selection = UserInfo.UserID + "=?";
         			String[] selectionArgs = {useid};
         	
         			
         			Cursor cur = managedQuery(UserInfo.CONTENT_URI, null, selection, selectionArgs,
         					null);
         			
         			if (cur.moveToFirst()) {
         				curtype = cur.getInt(cur
         						.getColumnIndex(UserInfo.BookUpdateType));
         			}
         			if(cur!=null){
         				cur.close();
         			}
                 }
         		if (curtype == 1) {
         			selectOntime();
         		} else if (curtype == 2) {
         			selectIntime();
         		} else if (curtype == 3) {
         			selectAfter24hours();
         		}
         		showMe();
         		sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
                break;
            default:
                ;
            }
        }
    }
	 private void showAlert(String message){
	    	if(pd!=null && pd.isShowing()){
	    		pd.dismiss();
	    	}
	        pd = new PviAlertDialog(getParent());
	        pd.setTitle(getResources().getString(R.string.my_friend_hint));
	        pd.setMessage(message);
	        pd.setCanClose(true);
	        //pd.setTimeout(4000);
	        pd.show();
	 }
	
	public void initselect(){
		
		type = -1;
		
		
			m_ontime.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
			m_intime.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
			m_after24hours.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
		
		
	}
	
	public void selectOntime(){
		if(type != 1){
			type = 1;
			
			
				m_ontime.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));	
				m_intime.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
				m_after24hours.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
			
		}
	}
	
	public void selectIntime(){
		if(type != 2){
			type = 2;
			
			
				m_ontime.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
				m_intime.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));
				m_after24hours.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
			
		}
	}

	public void selectAfter24hours(){
		if(type != 3){
			type = 3;
			
			
				m_ontime.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
				m_intime.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
				m_after24hours.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));
			
		}
	}
	public void initUI(){
		m_ontime = (Button) findViewById(R.id.subscriberemind_ontime);
		m_intime = (Button) findViewById(R.id.subscriberemind_intime);
		m_after24hours = (Button) findViewById(R.id.subscriberemind_after24hours);

		m_okButton = (Button) findViewById(R.id.ok);
		m_returnButton = (Button) findViewById(R.id.cancel);
		if(deviceType==1){
	   		all=(LinearLayout)findViewById(R.id.mainblock);
			//all.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
//			m_ontime.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			m_intime.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			m_after24hours.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			m_okButton.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			m_returnButton.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
	   	   }
		m_ontime.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus==true){
					selectOntime();
					}
			}
    		
    	});
		m_ontime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectOntime();
			}
			
		});
		m_intime.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus==true){
					selectIntime();
					}
			}
    		
    	});
		m_intime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectIntime();
			}
			
		});
		m_after24hours.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus==true){
					selectAfter24hours();
					}
			}
    		
    	});
		m_after24hours.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectAfter24hours();
			}
			
		});
		
		// �����¼�
		m_okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				
				PviAlertDialog dialog = new PviAlertDialog(getParent());
        		dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
        		dialog.setCanClose(true);
				ContentValues values = new ContentValues();
				SharedPreferences settings = getSharedPreferences(Constants.configFileName, 0); 
				SharedPreferences.Editor editor = settings.edit(); 
				
				String msg = "";
				String returnMsg = "";
				HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
				HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

				ahmNamePair.put("type", String.valueOf(type));
                Logger.v("curtype", TAG+type);
				HashMap responseMap = null;
				try {
					
					
					
					responseMap = CPManager.bookUpdateSet(ahmHeaderMap,
							ahmNamePair);
					String rescode=responseMap.get("result-code").toString();
					Logger.v("rescode", "rescode="+rescode);
					returnMsg = Error.getErrorDescriptionForContent(rescode);
				
					if (rescode.contains("result-code: 0")) {
						String curtype ="3";
						if (type == 1) {
							// ��������
							
							values.put(UserInfo.BookUpdateType, 1);
							curtype = "1";
							msg = getResources().getString(
									R.string.subscriberemind_set_ontime_ok);
							//m_popPrompt_context.setText(msg);
							editor.putString(STATE,curtype);  
							editor.commit(); 
						} else if (type == 2) {
							// ��������
							values.put(UserInfo.BookUpdateType, 2);
							curtype = "2";
							msg = getResources().getString(
									R.string.subscriberemind_set_intime_ok);
							//m_popPrompt_context.setText(msg);
							editor.putString(STATE,curtype);  
							editor.commit(); 
						} else if (type == 3) {
							// ��������
							values.put(UserInfo.BookUpdateType, 3);
							curtype = "3";
							msg = getResources().getString(
									R.string.subscriberemind_set_after24Hours_ok);
							//m_popPrompt_context.setText(msg);
							editor.putString(STATE,curtype);  
							editor.commit(); 

						} else {
							msg = getResources().getString(
									R.string.subscriberemind_set_msg);
							
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
			        		return;
							

						}
						getContentResolver().update(UserInfo.CONTENT_URI,values, null, null);
					}
				}catch (HttpException e) {
					Logger.e(LOG_TAG, e.getMessage());
					returnMsg = "���µ���������쳣";
					dialog.setMessage(returnMsg);
					dialog.show();
					
					return; 
				}catch (SocketTimeoutException e) {
					returnMsg = "�������糬ʱ";
					Logger.e(LOG_TAG, e.getMessage());
					dialog.setMessage(returnMsg);
					dialog.show();
					
					return;
				}catch (IOException e) {
					returnMsg = "���µ���������쳣";
					Logger.e(LOG_TAG, e.getMessage());
					dialog.setMessage(returnMsg);
					dialog.show();
					
					return;
				}
				
				

				sendBroadcast(new Intent(MainpageActivity.BACK));

			}

		});
		// �����¼�
		m_returnButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			
				sendBroadcast(new Intent(MainpageActivity.BACK));
			}

		});
		
	}
	
	public void onCreate(Bundle savedInstanceState) {
		GlobalVar appState = ((GlobalVar) getApplicationContext());
		deviceType=appState.deviceType;
        setContentView(R.layout.subscriberemindstyle2);
    	super.onCreate(savedInstanceState);
		initUI();
	  }
	
	
	 @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		retType = null;
		//mHandler.sendEmptyMessage(1);
		
		new Thread(){
			public void run() {
				
				HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		        HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		        HashMap responseMap = null;
				try {
					
		            responseMap = CPManager.getUserInfo(ahmHeaderMap, ahmNamePair);
		            
		            if (!responseMap.get("result-code").toString().contains(
		                    "result-code: 0")) {
		            	errMsg = Error.getErrorDescriptionForContent(responseMap.get("result-code").toString());
		            	Logger.v("errMsg", errMsg);
		                mHandler.sendEmptyMessage(2);
		                //Logger.i(TAG, responseMap.get("result-code").toString());
		               // mHandler.post(enableButtons);
		                //loading = false;
		                return;
		            }
		        } catch (HttpException e) {
		            Logger.e(TAG, e.getMessage());
		            mHandler.sendEmptyMessage(3);
		            return;
		        } catch (SocketTimeoutException e) {
		        	Logger.e(TAG, e.getMessage());
		        	mHandler.sendEmptyMessage(3);
		            return;
		        } catch (IOException e) {
		            Logger.e(TAG, e.getMessage());
		            mHandler.sendEmptyMessage(3);
		            return;
		        }
		        
		       
		        byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
		        responseMap = null;//--
		        Document dom = null;
		        try {

		            dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);

		        }catch (Exception e) {
		             return ;
		        }
		        Element root = dom.getDocumentElement();
		        NodeList nl = root.getElementsByTagName("Parameter");
		        String name = "";
		        String value = "";
		        Element temp = null;
		        for (int i = 0; i < nl.getLength(); i++) {
		            temp = (Element) nl.item(i);
		            name = temp.getElementsByTagName("name").item(0)
		                    .getFirstChild().getNodeValue();
		            try {
		                value = temp.getElementsByTagName("value").item(0)
		                        .getFirstChild().getNodeValue();
		            } catch (Exception e) {

		            }
		            if("BookUpdateType".equals(name)){
		            	retType = value;
		            	break;
		            }
		           
		        }
		        mHandler.sendEmptyMessage(4);
			};
		   
		}.start();
		
			
		
	        image=(ImageView)findViewById(R.id.image);
	        image.setFocusable(true);
	        image.setFocusableInTouchMode(true);
	        image.requestFocus();
       
	}
	public void showMe(){
		Intent tmpIntent = new Intent(
                MainpageActivity.SHOW_ME);
        Bundle bundleToSend = new Bundle();
      
        bundleToSend.putString("sender", SubscribeRemindActivity.this.getClass().getName()); //TAB��Ƕactivity���ȫ��
        tmpIntent.putExtras(bundleToSend);
        sendBroadcast(tmpIntent);
        tmpIntent = null;
        bundleToSend = null;
	}



	
}
