package com.pvi.ap.reader.activity;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.DencryptHelper;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.connection.CPConnection;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * SIM卡鉴权及激活
 * @author Elvis
 * @since 2010-11-25
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class NetAuthenticateSIM extends NetAuthenticate{
	
	public final static String TAG = "NetAuthenticate";
	
	private Handler myHandle = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			hideTip();
			super.handleMessage(msg);
			boolean out = false; 
			PviAlertDialog errorDialog = new PviAlertDialog(activity);
			errorDialog.setCanClose(true);
			errorDialog.setTitle("温馨提示");
			switch (msg.what) {
			case 1:
				Bundle extras= msg.getData();
				String message = extras.getString("message");
				errorDialog.setMessage(message,Gravity.CENTER);
				break;
			case 2:
				errorDialog.setMessage(activity.getResources().getString(R.string.bind_invoke_error),Gravity.CENTER);
				break;
			case 3:
				errorDialog.setMessage(activity.getResources().getString(R.string.net_failuely),Gravity.CENTER);
				break;
			case 4:
				errorDialog.setMessage("XML解析错误",Gravity.CENTER);
				break;
			case 5:
				out = true ;
				break ;

			default:
				errorDialog.setMessage("其他错误",Gravity.CENTER);
				break;
			}
			if(out){
				activity.sendBroadcast(myIntent);
				String serviceReceiver = "com.pvi.ap.reader.service.ServiceReceiver";
        		Intent serviceIntent = new Intent(serviceReceiver);
        		activity.sendBroadcast(serviceIntent);
        		Logger.i(TAG, serviceReceiver);
			}else{
				errorDialog.setButton(DialogInterface.BUTTON_POSITIVE,
						activity.getResources().getString(R.string.alert_dialog_delete_yes),
						(DialogInterface.OnClickListener)null);
				errorDialog.show();
			}
		};
		
	};
	
	/**
	 * 绑定主流程
	 */
	public void mainRun(){
		new Thread(){
			@Override
			public void run() {
				super.run();
				if (handsetAuthenticate()) { 
					drmAInvoke();
					saveOrUpdate();
					myHandle.sendEmptyMessage(5);
				}else{
//					myHandle.sendEmptyMessage(3);
				}
			}
		}.start();
	}
	
	public NetAuthenticateSIM(final Activity activity){
		this.activity = activity;
		 appState = ((GlobalVar)activity.getApplicationContext());
	}
	/**
	 * 鉴权 
	 * @throws Exception
	 */
	private boolean handsetAuthenticate(){
		Logger.i(TAG, "handsetAuthenticate...");
		showTipMessage("正在鉴权...");
		HashMap<String,Object> ahmHeaderMap = new HashMap<String,Object>();
		ahmHeaderMap.put("Accept", Config.getString("Accept"));
		ahmHeaderMap.put("deviceID", Config.getString("DeviceId"));
		ahmHeaderMap.put("Content-type", Config.getString("Content-type"));
		ahmHeaderMap.put("Host", Config.getString("Host"));
		ahmHeaderMap.put("User-Agent", Config.getString("User-Agent"));
		ahmHeaderMap.put("APIVersion", Config.getString("APIVersion"));
		ahmHeaderMap.put("Client-Agent", Config.getString("Client-Agent"));
		ahmHeaderMap.put("proxyIP", Config.getString("proxyIP"));
		ahmHeaderMap.put("port", Config.getString("port"));
		String deviceID = Config.getString("DeviceId");
		String clientVersion = Config.getString("SoftwareVersion");
		String password = Config.getString("ClientPWD");
		String clientHash = DencryptHelper.md5encrypt(clientVersion + deviceID
				+ password);
		HashMap<String,Object> responseMap = new HashMap<String,Object>();
		String requestXMLBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
				+ "<Request>" + "<HandsetAuthenticateReq>" + "<clientHash>"
				+ clientHash + "</clientHash>" + "<UserInfo>" + "<deviceID>"
				+ deviceID + "</deviceID>" + "</UserInfo>"
				+ "</HandsetAuthenticateReq>" + "</Request>";

		responseMap.put("XMLBody", requestXMLBody);
		try {
			responseMap = CPManager.handsetAuthenticate(ahmHeaderMap, responseMap);
		} catch (Exception e) {
			Logger.e(TAG, e);
			myHandle.sendEmptyMessage(3);
			return false ;
		}
		if (responseMap == null) {
			return false;
		}
		if (responseMap != null) {
			String resultCode = responseMap.get("result-code").toString();
			if (resultCode != null && resultCode.contains("result-code: 0")) {
				byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
				Document dom = null;
				try {
					dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
				} catch (Exception e) {
					myHandle.sendEmptyMessage(4);
					Logger.e(TAG, e);
					return false;
				}
				Element root  = dom.getDocumentElement();
				NodeList userInfo = root.getElementsByTagName("UserInfo");
				Element userInfoItem = (Element) userInfo.item(0);
				Element userIDNode = (Element) userInfoItem.getElementsByTagName("userID").item(0);
				userID = userIDNode.getFirstChild().getNodeValue();
				appState.setNeedAuth(false);
				appState.setUserID(userID);
				return true ;
			} else {
				resultCode = resultCode.substring(resultCode.indexOf(' ') + 1,resultCode.indexOf('\r'));
				String message = Error.getErrorDescription(resultCode);
				Bundle extras = new Bundle();
				extras.putString("message", message);
				Message msg = new Message();
				msg.what = 1 ;
				msg.setData(extras); 
				myHandle.sendMessage(msg);
			}
		}
		return false ;
	}
	
	/**
	 * 激活DRM平台
	 */
	private boolean drmAInvoke(){
		Logger.i(TAG, "drmAInvoke");
		showTipMessage("正在获取激活码...");
		HashMap<String,Object> ahmHeaderMap = new HashMap<String,Object>();
		ahmHeaderMap.put("Accept",Config.getString("Accept"));
		ahmHeaderMap.put("Host",Config.getString("DRMHost"));
		ahmHeaderMap.put("User-Agent",Config.getString("User-Agent"));
		String Version = Config.getString("SoftwareVersion") ;
		String password = Config.getString("ClientPWD");
		ahmHeaderMap.put("Version",Version);
		ahmHeaderMap.put("user-id",userID);
		String reqDigest = DencryptHelper.md5encrypt(Version + userID + password);
		Logger.i(TAG, "reqDigest:" + reqDigest);
		ahmHeaderMap.put("ReqDigest",reqDigest);
		ahmHeaderMap.put("proxyIP", Config.getString("proxyIP"));
    	ahmHeaderMap.put("port", Config.getString("port"));
		HashMap<String,Object> retMap = null;
		try {
			retMap = new CPConnection(Config.getString("DRM_URL_ACTIVE")).doGet(ahmHeaderMap, null);
		} catch (Exception e) {
			myHandle.sendEmptyMessage(3);
			Logger.e(TAG, e);
			return false;
		}
		String statusLine = retMap.get("Status").toString();
		System.out.println(retMap.get("Status"));
		hideTip();
		if(statusLine.contains("200 OK")){
			regCode = (String)retMap.get("RegCode");
			appState.setRegCode(regCode);
		}else{
			myHandle.sendEmptyMessage(2);
			return false ;
		}
		return true;
	}

	public Intent getMyIntent() {
		return myIntent;
	}

	public void setMyIntent(Intent myIntent) {
		this.myIntent = myIntent;
	}
	
	
	protected void showTipMessage(String message) {
		Intent tip = new Intent(MainpageActivity.SHOW_TIP);
		Bundle data = new Bundle();
		data.putString("pviapfStatusTip", message);
		tip.putExtras(data);
		activity.sendBroadcast(tip);
	}
	
	private void hideTip(){
		activity.sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
	}
}
