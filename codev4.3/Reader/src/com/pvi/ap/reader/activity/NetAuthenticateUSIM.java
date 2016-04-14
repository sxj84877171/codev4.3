package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.DencryptHelper;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.UserInfo;
import com.pvi.ap.reader.data.external.connection.CPConnection;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * 绑定流程类<br>
 * @author Elvis
 * @since 2010-11-25
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class NetAuthenticateUSIM extends NetAuthenticate{
	
	public final static String TAG = "NetAuthenticate";
	
	public final static String getAuthCodeSuccess = "result-code: 0";
	public final static String bindSuccessProcess = "result-code: 3209";
	public final static String bindSuccessOK = "result-code: 3200" ;
	public final static String bindBeen = "result-code: 3197";
	public final static String bindSuccess = "result-code: 3200";
	public final static String bindFailue = "result-code: 3179";
	public final static String code3198 = "result-code: 3198" ;//手机号码已经绑定其他内置卡
	
	private String simID = null ;
	
	/**
	 * 手机号码输入框
	 */
	private PviAlertDialog  inputNumberDialog = null;
	
	private TextView  indShowTitle = null;
	
	private EditText  indNumber = null;
	
	private Button  indShowGet = null;
	
	private Button  indShowRead = null;

	/**
	 * 验证码输入框
	 */
	private PviAlertDialog  inputAuthcodeDialog = null;
	
	private EditText  iadAuthCode = null;
	
	private Button  iadBind = null;
	
	private Button  iadGetAgain = null;
	
	/**
	 * 手机号码
	 */
	private String photoNum = "" ;
	/**
	 * userID
	 */
	private String userID = "" ;
	/**
	 * 验证码
	 */
	private String reStr = "" ;
	
	public static int curCodeStatus = 0 ;
	
	private Handler myHandle = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			hideTip();
			switch (msg.what) {
			case 1:
				String message = "网络连接失败" ;
				if(msg.getData() != null){
					message = msg.getData().getString("message");
				}
				popWindon(message);
				break ;
			case 2:
				popWindon("网络链接失败");
				break ;
			case 3:
				popWindon(activity.getResources().getString(R.string.bind_getAuthCode_execption));	
				break ;
			case 4:
				inputNumberDialog.dismiss();
				inputAuthcodeDialog.show();
				inputAuthcodeDialog.getWindow().setLayout(400,300);
				break ;
			case 5:
				popWindon(activity.getResources().getString(R.string.bind_timeout));
				break ;
			case 6:
				inputAuthcodeDialog.dismiss();
				popWindon(activity.getResources().getString(R.string.bind_success));
				break ;
			case 7:
				popWindon(activity.getResources().getString(R.string.bind_beenbind));
				break ;
			case 8:
				inputNumberDialog.show();
				inputNumberDialog.getWindow().setLayout(400,300);
				break ;
			case 9:
				popWindon(activity.getResources().getString(R.string.bind_net_error));
				break ;
			case 10:
				inputAuthcodeDialog.dismiss();
				break ;
			case 11:
				if(myIntent != null){
					activity.sendBroadcast(myIntent);
					String serviceReceiver = "com.pvi.ap.reader.service.ServiceReceiver";
	        		Intent serviceIntent = new Intent(serviceReceiver);
	        		activity.sendBroadcast(serviceIntent);
	        		Intent intent = new Intent("com.pvi.ap.reader.service.NetCacheService");
	    			Bundle extras = new Bundle();
	    			extras.putString("userID", userID);
	    			extras.putString("simtype", "USIM");
	    			extras.putString("lineNum", photoNum);
	    			intent.putExtras(extras);
	    			activity.startService(intent);
				}else{
					popWindon(activity.getResources().getString(R.string.bind_beenbind));
				}
			default:
				break;
			}
		};
		
		private void popWindon(String message){
			PviAlertDialog errorDialog =  new PviAlertDialog(activity);
			errorDialog.setCanClose(true);
			errorDialog.setTitle("温馨提示");
			errorDialog.setMessage(message, Gravity.CENTER);
			errorDialog.setButton(DialogInterface.BUTTON_POSITIVE,
					activity.getResources().getString(R.string.alert_dialog_delete_yes),
					(DialogInterface.OnClickListener)null);
			errorDialog.show();
		}
		
	};
	
	/**
	 * 绑定主流程
	 */
	public void mainRun(){
		startAuthenticate();
	}
	
	public NetAuthenticateUSIM(final Activity activity){
		this.activity = activity;
		 appState = ((GlobalVar)activity.getApplicationContext());
		 simID = appState.getSimID();
		LayoutInflater factory = LayoutInflater.from(activity);
		// 得到自定义对话框
		final View numberView = factory.inflate(R.layout.inputnumberdialog, null);
		inputNumberDialog =  new PviAlertDialog(activity);
		inputNumberDialog.setTitle(activity.getString(R.string.bind_getAuthCode_title));
		inputNumberDialog.setCanClose(true);
		inputNumberDialog.setView(numberView);
		indShowTitle = (TextView)numberView.findViewById(R.id.bind_inputnum_showtitle);
		indShowTitle.setTextAppearance(activity, R.style.normal_black_common);
		((TextView)(numberView.findViewById(R.id.noteName))).setTextAppearance(activity, R.style.normal_black_common);
		((TextView)(numberView.findViewById(R.id.bind_inputnum_readagree))).setTextAppearance(activity, R.style.normal_black_common);
		indNumber = (EditText)numberView.findViewById(R.id.bind_number);
		indShowGet = (Button)numberView.findViewById(R.id.bind_get);
		indShowGet.setTextAppearance(activity, R.style.normal_black_common);
		indShowRead = (Button)numberView.findViewById(R.id.bind_read);
		indShowRead.setTextAppearance(activity, R.style.normal_black_common);
		
		final View authCodeView = factory.inflate(R.layout.inputauthcodedialog, null);
		inputAuthcodeDialog =  new PviAlertDialog(activity);
		inputAuthcodeDialog.setTitle("温馨提示");
		inputAuthcodeDialog.setCanClose(true);
		inputAuthcodeDialog.setView(authCodeView);
		iadAuthCode = (EditText)authCodeView.findViewById(R.id.bind_inputAuthcode);
		((TextView)authCodeView.findViewById(R.id.bind_inputAuthcode_input1)).setTextAppearance(activity, R.style.normal_black_common);
		((TextView)authCodeView.findViewById(R.id.bind_inputauthcodetitle)).setTextAppearance(activity, R.style.normal_black_common);
		iadBind =  (Button)authCodeView.findViewById(R.id.bind_bind);
		iadBind.setTextAppearance(activity, R.style.normal_black_common);
		iadGetAgain =  (Button)authCodeView.findViewById(R.id.bind_getagain);
		iadGetAgain.setTextAppearance(activity, R.style.normal_black_common);
		//获取验证码
		indShowGet.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

				new Thread() {
					public void run() {
						HashMap<String, Object> ahmHeaderMap = CPManagerUtil.getHeaderMap();
						HashMap<String, Object> ahmNamePair = CPManagerUtil.getAhmNamePairMap();
						ahmHeaderMap.remove("x-up-calling-line-id");
						ahmHeaderMap.remove("user-id");
						String version = appState.getRegInfo("h_version");
						String password = appState.getRegInfo("client_key");
						version = Config.getString("SoftwareVersion");
						password = Config.getString("ClientPWD");
						String temp = version + password;
						String base64 = DencryptHelper.md5encrypt(temp);
						Logger.i(TAG + "-base64::", base64);
						String msisdn = indNumber.getText().toString();
						String expr = "[1][0-9]{10}";
						if (!msisdn.matches(expr)) {
							String message = "你输入的手机号码" + msisdn + "不正确";
							Bundle extras = new Bundle();
							extras.putString("message", message);
							Message msg = new Message();
							msg.what = 1;
							msg.setData(extras);
							myHandle.sendMessage(msg);
							return;
						}
						photoNum = msisdn;
						String requestXMLBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
								+ "<Request>"
								+ "<GetSMSVerifyCodeReq>"
								+ "<clientHash>"
								+ base64
								+ "</clientHash>"
								+ "<msisdn>"
								+ msisdn
								+ "</msisdn>"
								+ "</GetSMSVerifyCodeReq>" + "</Request>";
						ahmNamePair.put("XMLBody", requestXMLBody);
						HashMap responseMap = null;

						try {
							responseMap = CPManager.getSMSVerifyCode(
									ahmHeaderMap, ahmNamePair);
							if (responseMap == null) {
								myHandle.sendEmptyMessage(2);
								return;
							}
						} catch (Exception e) {
							myHandle.sendEmptyMessage(3);
							curCodeStatus = 0;
							return;
						}

						String result = responseMap.get("result-code")
								.toString();
						Logger.i(TAG, result);
						if (result.contains(getAuthCodeSuccess)) {
							// 获取手机验证码成功
							myHandle.sendEmptyMessage(4);
							curCodeStatus = 3;
						} else {
							if (result.indexOf('\r') == -1) {
								result += result + '\r';
							}
							result = result.substring(result.indexOf(' ') + 1,
									result.indexOf('\r'));
							Logger.i(TAG, "result" + result);
							// 获取手机验证码失败
							String message = Error.getErrorDescription(result);
							Bundle extras = new Bundle();
							extras.putString("message", message);
							Message msg = new Message();
							msg.what = 1;
							msg.setData(extras);
							myHandle.sendMessage(msg);
							curCodeStatus = 0;
						}

					}
				}.start();
			}

		});
		
		//阅读协议
		indShowRead.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v){
				curCodeStatus = 0 ;
				inputNumberDialog.dismiss();
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
			    Bundle bundleToSend = new Bundle();
			    bundleToSend.putInt("type",2);
		        bundleToSend.putString("act","com.pvi.ap.reader.activity.ShowAgreementActivity");
		        bundleToSend.putString("pviapfStatusTip",activity.getResources().getString(R.string.goto_showagreenment));
	            tmpIntent.putExtras(bundleToSend);
		        activity.sendBroadcast(tmpIntent);
			}
		});
		
		//绑定
		iadBind.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread() {
					public void run() {
						HashMap<String, Object> ahmHeaderMap = CPManagerUtil
								.getHeaderMap();
						HashMap<String, Object> ahmNamePair = CPManagerUtil
								.getAhmNamePairMap();
						ahmHeaderMap.remove("x-up-calling-line-id");
						ahmHeaderMap.remove("user-id");
						String deviceID = appState.getDeviceID();
						deviceID = Config.getString("DeviceId"); // "018P801_20100920_001"
																	// ;
						String imei = appState.getRegInfo("imei");
						imei = Config.getString("imei");
						String clientHash = Config.getString("SoftwareVersion")
								+ photoNum + Config.getString("DeviceId")
								+ Config.getString("ClientPWD");
						System.out.println(clientHash);
						clientHash = DencryptHelper.md5encrypt(clientHash);
						reStr = iadAuthCode.getText().toString();
						if (reStr == null || "".equals(reStr.trim())
								|| reStr.length() != 8) {
							String message = activity.getResources().getString(
									R.string.bind_mish);
							sendMessage(message);
							return;
						}
						String requestXMLBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
								+ "<Request>" + "<HandsetAssociateReq>" + "<clientHash>"
								+ clientHash + "</clientHash>" + "<UserInfo>" + "<msisdn>"
								+ photoNum 	+ "</msisdn>" + "<deviceID>" + deviceID
								+ "</deviceID>" + "<imei>" + imei + "</imei>"
								+ "<verifyCode>" + reStr + "</verifyCode>" + "</UserInfo>"
								+ "</HandsetAssociateReq>" 	+ "</Request>";
						ahmNamePair.put("XMLBody", requestXMLBody);
						ahmHeaderMap.put("photoNum", photoNum);
						HashMap<String, Object> responseMap = null;

						try {
							// 以POST的形式连接请求
							System.out.println(ahmHeaderMap);
							System.out.println(ahmNamePair);
							responseMap = CPManager.handsetAssociate(ahmHeaderMap, ahmNamePair);
							if (responseMap == null) {
								return;
							}
						} catch (HttpException e) {
							// 连接异常 ,一般原因为 URL错误
							// 获取手机验证码失败
							Logger.i(TAG, e.toString());
							myHandle.sendEmptyMessage(5);
							return;
						} catch (SocketTimeoutException e) {
							myHandle.sendEmptyMessage(5);
							curCodeStatus = 0;
							return;
						} catch (IOException e) {
							Logger.i(TAG, e);
							myHandle.sendEmptyMessage(5);
							curCodeStatus = 0;
							return;
						}

						String result = responseMap.get("result-code")
								.toString();
						Logger.i(TAG, result);
						if (result.contains(bindSuccessProcess) || result.contains(getAuthCodeSuccess)) {
							appState.setLineNum(photoNum);
							byte[] bytes = (byte[]) responseMap
									.get("ResponseBody");
							Document dom = null;
							try {
								dom = CPManagerUtil
										.getDocumentFrombyteArray(bytes);
								Element root = dom.getDocumentElement();
								NodeList userInfo = root
										.getElementsByTagName("UserInfo");
								Element userInfoItem = (Element) userInfo
										.item(0);
								userID = userInfoItem.getFirstChild()
										.getNodeValue();
								appState.setUserID(userID);
								appState.setNeedAuth(false);
								Logger.i(TAG, "userID:" + userID);
							} catch (Exception e) {
								Logger.e(TAG, e.toString());
							}
							sucessExitThread.start();
							myHandle.sendEmptyMessage(6);
						} else if (result.contains(bindSuccessOK)) {
							appState.setLineNum(photoNum);
							appState.setNeedAuth(false);
							myHandle.sendEmptyMessage(7);

						} else {
							result = result.substring(result.indexOf(' ') + 1,
									result.indexOf('\r'));
							String message = Error.getErrorDescription(result);
							sendMessage(message);
							curCodeStatus = 0;
						}
					}}.start();
			}
		});
		
		//重新获取验证码
		iadGetAgain.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread() {
					public void run() {
						myHandle.sendEmptyMessage(10);
						HashMap<String, Object> ahmHeaderMap = CPManagerUtil.getHeaderMap();
						HashMap<String, Object> ahmNamePair = CPManagerUtil.getAhmNamePairMap();
						ahmHeaderMap.remove("x-up-calling-line-id");
						ahmHeaderMap.remove("user-id");
						String version = appState.getRegInfo("h_version");
						String password = appState.getRegInfo("client_key");
						version = Config.getString("SoftwareVersion");
						password = Config.getString("ClientPWD");
						String temp = version + password;
						String base64 = DencryptHelper.md5encrypt(temp);

						Logger.i(TAG + "-base64::", base64);
						String requestXMLBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
								+ "<Request>" + "<GetSMSVerifyCodeReq>" + "<clientHash>"
								+ base64 + "</clientHash>" + "<msisdn>" + photoNum
								+ "</msisdn>" + "</GetSMSVerifyCodeReq>" + "</Request>";
						ahmNamePair.put("XMLBody", requestXMLBody);
						HashMap responseMap = null;
						try {
							// 以POST的形式连接请求
							responseMap = CPManager.getSMSVerifyCode(
									ahmHeaderMap, ahmNamePair);
							if (responseMap == null) {
								return;
							}
						} catch (Exception e) {
							// 连接异常 ,一般原因为 URL错误
							// 获取手机验证码失败
							Logger.i(TAG, e);
							String message = activity.getResources().getString(
									R.string.bind_getAuthCode_execption);
							sendMessage(message);
							return;
						}

						String resultCode = responseMap.get("result-code")
								.toString();
						Logger.i(TAG, resultCode);
						if (resultCode.contains(getAuthCodeSuccess)) {
							// 获取手机验证码成功
							myHandle.sendEmptyMessage(4);
						} else {
							// 获取手机验证码失败
							resultCode = resultCode
									.substring(resultCode.indexOf(' ') + 1,
											resultCode.indexOf('\r'));
							String message = Error
									.getErrorDescription(resultCode);
							sendMessage(message);
						}

					}}.start();
		}});
	}
	
	/**
	 * 开始绑定
	 */
	public void startAuthenticate(){
		new Thread() {
			@Override
			public void run() {
				if(!appState.isNeedAuth()){
					myHandle.sendEmptyMessage(7);
					return ;
				}
				handsetAuthenticate();
				if (curCodeStatus == 7) {
					drmAInvoke();
					saveOrUpdate(photoNum);
					myHandle.sendEmptyMessage(11);
				}
				if(curCodeStatus == 2){
					myHandle.sendEmptyMessage(8);
				}
				
			}
		}.start();
	}

	private void updateUserInfo() {
		Logger.i(TAG, "updateUserInfo");
		String columns[] = {
				UserInfo.UserID,UserInfo.RegCode,UserInfo.LineNum
		};
		String where = UserInfo.SimID + " = '" + simID + "'" ;//+ UserInfo.RegCode + " = '"+ regCode + "'"
		Cursor cur = activity.managedQuery(UserInfo.CONTENT_URI, columns, where, null, null);
		ContentValues values = new ContentValues();
		if(simID != null  && !"".equals(simID.trim())){
			values.put(UserInfo.SimID, simID.trim());
		}
		if(userID != null && !"".equals(userID.trim())){
			values.put(UserInfo.UserID, userID.trim());
			CPManagerUtil.USER_ID = userID ;
			appState.setUserID(userID);
			Intent intent = new Intent("com.pvi.ap.reader.service.NetCacheService");
			Bundle extras = new Bundle();
			extras.putString("userID", userID);
			extras.putString("lineNum", photoNum);
			extras.putString("simtype", "USIM");
			intent.putExtras(extras);
			activity.startService(intent);
		}
		if(photoNum != null && !"".equals(photoNum.trim())){
			values.put(UserInfo.LineNum, photoNum);
			CPManagerUtil.X_UP_CALLING_LINE_ID = photoNum ;
			appState.setLineNum(photoNum);
		}
		if(regCode != null && !"".equals(regCode.trim())){
			values.put(UserInfo.RegCode, regCode);
			appState.setRegCode(regCode);
		}
		if(cur == null || !cur.moveToFirst()){
			activity.getContentResolver().insert(UserInfo.CONTENT_URI, values);
			if(cur != null && !cur.isClosed()){
				cur.close();
			}
		}else{
			activity.getContentResolver().update(UserInfo.CONTENT_URI, values, where, null);
			if(cur != null && !cur.isClosed()){
				cur.close();
			}
		}
	}
	
	
	private Thread sucessExitThread = new Thread(){
		public void run() {
			int index = 0 ;
			while(true){
				Logger.i(TAG, "run:" + index ++);
				HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
				HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
				ahmHeaderMap.remove("x-up-calling-line-id");
				ahmHeaderMap.remove("user-id");
				String deviceID = appState.getDeviceID();
				deviceID = Config.getString("DeviceId"); // "018P801_20100920_001"
															// ;
				String imei = appState.getRegInfo("imei");
				imei = Config.getString("imei");
				String clientHash = Config.getString("SoftwareVersion") + photoNum
						+ Config.getString("DeviceId")
						+ Config.getString("ClientPWD");
				System.out.println(clientHash);
				clientHash = DencryptHelper.md5encrypt(clientHash);
				String reStr = iadAuthCode.getText().toString();
				String requestXMLBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
						+ "<Request>" + "<HandsetAssociateReq>" 
						+ "<clientHash>" + clientHash + "</clientHash>"
						+ "<UserInfo>" + "<msisdn>" + photoNum + "</msisdn>"
						+ "<deviceID>" + deviceID + "</deviceID>"
						+ "<imei>" + imei + "</imei>"
						+ "<verifyCode>" + reStr + "</verifyCode>"
						+ "</UserInfo>" + "</HandsetAssociateReq>" + "</Request>";
				ahmNamePair.put("XMLBody", requestXMLBody);
				HashMap responseMap = null;
				try {
					responseMap = CPManager.handsetAssociate(ahmHeaderMap,
							ahmNamePair);
					if(responseMap == null){
						return ;
					}
				} catch (Exception e) {
					Logger.e(TAG, e);
					break ;
				}
				String result = responseMap.get("result-code").toString();
				Logger.i(TAG, result);
				if (result.contains(bindSuccess)){
					try {
						drmAInvoke();
						updateUserInfo();
					} catch (Exception e) {
						Logger.e(TAG, e.toString());
					}
					Intent intent = new Intent(MainpageActivity.SHOW_TIP);
	                Bundle sndBundle = new Bundle();
	                sndBundle.putString("pviapfStatusTip",  activity.getResources().getString(R.string.bind_ok));
	                sndBundle.putString("pviapfStatusTipTime",  "5000");
	                intent.putExtras(sndBundle);
	                activity.sendBroadcast(intent);
					curCodeStatus = 4 ; 
					break ;
				}else if(result.contains(bindFailue)){
					break ;
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					break ;
				}
			}
		}
	};
		
	/**
	 * 鉴权 
	 * @throws Exception
	 */
	private void handsetAuthenticate(){
		Logger.i(TAG, "msg:handsetAuthenticate");
		showTipMessage("正在鉴权...");
		HashMap<String,Object> ahmHeaderMap = new HashMap<String,Object>();
		ahmHeaderMap.put("Accept",Config.getString("Accept"));
		ahmHeaderMap.put("deviceID",Config.getString("DeviceId"));
		ahmHeaderMap.put("Content-type",Config.getString("Content-type"));
		ahmHeaderMap.put("Host",Config.getString("Host"));
		ahmHeaderMap.put("User-Agent",Config.getString("User-Agent"));
		ahmHeaderMap.put("APIVersion",Config.getString("APIVersion"));
		ahmHeaderMap.put("Client-Agent",Config.getString("Client-Agent"));
    	if(appState.getLineNum() != null && !"".equals(appState.getLineNum().trim())){
			photoNum = appState.getLineNum();
		}
    	if(photoNum != null && !"".equals(photoNum.trim())){
    		ahmHeaderMap.put("x-up-calling-line-id",photoNum);
    	}
        String deviceID = Config.getString("DeviceId");
		String clientVersion = Config.getString("SoftwareVersion");
		String password = Config.getString("ClientPWD");
        String clientHash = DencryptHelper.md5encrypt(clientVersion + deviceID + password);
        HashMap responseMap = new HashMap();
        String requestXMLBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
    		+ "<Request>"
    		+	"<HandsetAuthenticateReq>"
    		+			"<clientHash>"+clientHash+"</clientHash>"
    		+			"<UserInfo>"
    		+					"<deviceID>" + deviceID+ "</deviceID>"
    		+			"</UserInfo>"
    		+	"</HandsetAuthenticateReq>"
    		+ "</Request>";
        
        responseMap.put("XMLBody", requestXMLBody);
       
			try {
				System.out.println(ahmHeaderMap);
				System.out.println(responseMap);
				responseMap = CPManager.handsetAuthenticate(ahmHeaderMap, responseMap);
				if(responseMap == null){
					return ;
				}
			} catch (Exception e) {
				Logger.e(TAG, e);
				myHandle.sendEmptyMessage(9);
				return ;
			}
		if(responseMap != null){
			String resultCode = responseMap.get("result-code").toString();
			Logger.i(TAG, "resultCode" + resultCode);
			if(resultCode == null || "".equals(resultCode.trim())){
				myHandle.sendEmptyMessage(9);
				curCodeStatus = 2 ;
				return ;
			}
			if(resultCode.contains("result-code: 0")){
				byte[] responseBody = (byte[])responseMap.get("ResponseBody");
				Document dom = null;
				Element root = null ;
				try {
					dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
					root = dom.getDocumentElement();
				} catch (Exception e) {
					myHandle.sendEmptyMessage(4);
					return ;
				}
				NodeList userInfo = root.getElementsByTagName("UserInfo");
				if(userInfo == null){
					String message = activity.getResources().getString(R.string.bind_handsetAut_error) ;
					sendMessage(message);
					curCodeStatus = 2 ;
					return ;
				}
				Element userInfoItem = (Element)userInfo.item(0);
				Element userIDNode = (Element)userInfoItem.getElementsByTagName("userID").item(0);
				userID = userIDNode.getFirstChild().getNodeValue();
				appState.setNeedAuth(false);
				if(userID.equals(appState.getUserID())){
					curCodeStatus = 7 ;
					return ;
				}
				appState.setUserID(userID);
				Logger.i(TAG, "userID:" + userID);
				updateUserInfo();
				curCodeStatus = 7 ;
			}else{
				resultCode = resultCode.substring(resultCode.indexOf(' ')+1, resultCode.indexOf('\r'));
				String message = Error.getErrorDescription(resultCode);
				curCodeStatus = 2 ;
			}
		}
	}
	
	
	/**
	 * 激活DRM平台
	 */
	private boolean drmAInvoke(){
		showTipMessage("正在获取激活码...");
		HashMap ahmHeaderMap = new HashMap();
		ahmHeaderMap.put("Accept",Config.getString("Accept"));
		ahmHeaderMap.put("Host",Config.getString("DRMHost"));
		ahmHeaderMap.put("User-Agent",Config.getString("User-Agent"));
		String Version = Config.getString("SoftwareVersion") ;
		String password = Config.getString("ClientPWD");
		ahmHeaderMap.put("Version",Version);
		if(photoNum == null || "".equals(photoNum.trim())){
			photoNum = appState.getLineNum();
		}
		ahmHeaderMap.put("x-up-calling-line-id",photoNum);
		if(userID == null || "".equals(userID.trim())){
			userID = appState.getUserID();
		}
		ahmHeaderMap.put("user-id",userID);
		String reqDigest = DencryptHelper.md5encrypt(Version + userID + password);
		Logger.i(TAG, "reqDigest:" + reqDigest);
		ahmHeaderMap.put("ReqDigest",reqDigest);
		HashMap retMap = null ;
		try {
			retMap = new CPConnection(Config.getString("DRM_URL_ACTIVE")).doGet(ahmHeaderMap, null);
		} catch (Exception e) {
			curCodeStatus = 9 ;
			Logger.e(TAG, e);
			String message = activity.getResources().getString(R.string.bind_net_error);
			sendMessage(message);
			return false;
		}
		String statusLine = retMap.get("Status").toString();
		System.out.println(retMap.get("Status"));
		hideTip();
		if(statusLine.contains("200 OK")){
			regCode = (String)retMap.get("RegCode");
			if(regCode == null){
				Logger.i(TAG, "platform wrong!");
			}
			appState.setRegCode(regCode);
			curCodeStatus = 10 ;
			return true;
		}else{
			String message = activity.getResources().getString(R.string.bind_invoke_error)  ;
			sendMessage(message) ;
			curCodeStatus = 9 ;
		}
		return false ;
	}
	
	private void getHandsetInfo(){}
	
	private void sendMessage(String message) {
		Bundle extras = new Bundle();
		extras.putString("message", message);
		Message msg = new Message();
		msg.what = 1 ;
		msg.setData(extras);
		myHandle.sendMessage(msg);
	}
	
	private Intent  myIntent = null ;
	
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