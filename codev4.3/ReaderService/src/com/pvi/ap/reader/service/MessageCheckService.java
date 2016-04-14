package com.pvi.ap.reader.service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;

import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * 
 * 检查消息服务
 * @author 马中庆 RD040 
 * 
 */
public class MessageCheckService extends Service {

	private static final String LOG_TAG = "MessageCheckService" + "::";
	private static String unreadRecordCount = "";

	public long mTimeTaskTime = 5*60*1000;//间隔时间 5分钟检查一次  如果没联网 就直接返回

	private Timer mTimer = null;
	private TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			synchMessage();
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		if (mTimer != null) {
			mTimer.cancel();
		} else {
			mTimer = new Timer();
		}
		mTimer.schedule(mTimerTask, new java.util.Date(), mTimeTaskTime);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		setMessage(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		super.onDestroy();

	}

	/**
	 * Outside the control service need more new information
	 * <br>set message to run service
	 * @param intent the Extral to change the class run
	 * @exception RuntimeException
	 */
	private void setMessage(Intent intent) {

		if (intent == null) {
			return;
		}

		Bundle bundle = null;
		bundle = intent.getExtras();
		if (bundle != null) {

			String timeTaskTimeStr = bundle.getString("timeTaskTime");
			if (timeTaskTimeStr != null) {
				mTimeTaskTime = Long.parseLong(timeTaskTimeStr);
			}

		}
	}

	/**
	 * the main method to syschronous information
	 * <br>Synchronous information method
	 * <p>synch message:bookmark,reading optloginfo
	 * @since 2010-9
	 */
	private void synchMessage() {
		Logger.i(LOG_TAG, "datasyn:synchMessage");
		if(networkConnectAvailable()){
			return ;
		}else{
		    checkUserMessage(); 
		}
	}


	private void checkUserMessage() {
        // read data from remote
        HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
        HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

        HashMap responseMap = null;
        try {
            responseMap = CPManager.getMessage(ahmHeaderMap, ahmNamePair);
            if (!responseMap.get("result-code").toString().contains("result-code: 0")) {
                //Logger.e(LOG_TAG, responseMap.get("result-code").toString());
                return ;
            }
        } catch (HttpException e) {
            
            return ;
        }catch (SocketTimeoutException e) {
            
            return ;
           } catch (IOException e) {
            
            return ;
        }catch (Exception e) {
            
            return ;
           }
        byte[] responseBody = (byte[]) responseMap.get("ResponseBody");

        Document dom = null;
        try {
            String xml = new String(responseBody);
            xml = xml.replaceAll(">\\s+?<", "><");
            dom = CPManagerUtil.getDocumentFrombyteArray(xml.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Element root = dom.getDocumentElement();
        
        try{
            //totalRecordCount = root.getElementsByTagName("totalRecordCount").item(0).getFirstChild().getNodeValue();
            unreadRecordCount = root.getElementsByTagName("unreadRecordCount").item(0).getFirstChild().getNodeValue();;
       
            Logger.i(LOG_TAG,"unreadRecordCount:"+unreadRecordCount);
            //如果未读消息>0条，则广播
            if(Integer.parseInt(unreadRecordCount)>0){
                Intent intent = new Intent("");
                intent.putExtra("STATE", 1);
                sendBroadcast(intent);
            }else{
                Intent intent = new Intent("");
                intent.putExtra("STATE", 0);
                sendBroadcast(intent);
            }
        }catch(Exception e){Logger.e(LOG_TAG,e.getMessage());}
}

	/**
	 * 网络是否连接正常
	 * @return
	 */
	public boolean networkConnectAvailable(){
		ConnectivityManager cwjManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if(cwjManager == null){
			return false ;
		}
		if(cwjManager.getActiveNetworkInfo() == null){
			return false ;
		}
		
		return cwjManager.getActiveNetworkInfo().isAvailable();
	}
}
