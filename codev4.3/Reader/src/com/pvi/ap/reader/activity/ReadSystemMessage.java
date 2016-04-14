package com.pvi.ap.reader.activity;

import android.app.Activity;
import android.database.Cursor;
import android.telephony.TelephonyManager;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.UserInfo;
import com.pvi.ap.reader.data.external.connection.CPConnection;
import com.pvi.ap.reader.data.external.connection.DRMConnection;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

public class ReadSystemMessage {
	private static String TAG = "ReadSystemMessage" ;
	
	private String cardType = "NOSIM" ;
	
	private String simID = "" ;
	
	private Activity call ;
	
	private GlobalVar appState = null;
	
	public ReadSystemMessage(Activity call){
		this.call = call ;
		appState= ((GlobalVar) call.getApplicationContext());
		this.cardType = appState.getSimType();
	}
	
	public void run(){
		getUSIMMessage();
		updateAppState();
		
//		String serviceReceiver = "com.pvi.ap.reader.service.ServiceReceiver";
//		Intent serviceIntent = new Intent(serviceReceiver);
//		call.sendBroadcast(serviceIntent);
		
	}
	
	private void getUSIMMessage(){
		Logger.i(TAG, "getUSIMMessage:TelephonyManager");
		CPManagerUtil.USER_ID = appState.getUserID();
		CPManagerUtil.X_UP_CALLING_LINE_ID = appState.getLineNum();
		TelephonyManager tm = (TelephonyManager)call.getSystemService(call.TELEPHONY_SERVICE);
		simID = tm.getSimSerialNumber();
        
	    if ((cardType!=null)&&(cardType.equalsIgnoreCase("sim")||cardType.equalsIgnoreCase("usim")))
        {
            CPConnection.sdcard = cardType ;
            DRMConnection.simType = cardType;
        }
		else
        {
            cardType = "WRONGSIM";
//            popWindown();
        }

	}

	private void updateAppState() {
		appState.setSimID(simID);
		String where = UserInfo.SimID + " = '" + simID + "'";
		String columns[] = { UserInfo.UserID, UserInfo.RegCode,
				UserInfo.LineNum, UserInfo.NickName, };
		if (simID != null && !"".equals(simID.trim())) {
			Cursor cur = call.managedQuery(UserInfo.CONTENT_URI, columns,
					where, null, null);
			if (cur == null) {
				return;
			}
			if (!cur.moveToFirst()) {
				return;
			}
			appState.setUserID(cur.getString(cur
					.getColumnIndex(UserInfo.UserID)));
			CPManagerUtil.USER_ID = appState.getUserID();
			appState.setRegCode(cur.getString(cur
					.getColumnIndex(UserInfo.RegCode)));
			appState.setLineNum(cur.getString(cur
					.getColumnIndex(UserInfo.LineNum)));
			CPManagerUtil.X_UP_CALLING_LINE_ID = appState.getLineNum();
			appState.setNickName(cur.getString(cur
					.getColumnIndex(UserInfo.NickName)));
			if (!cur.isClosed()) {
				cur.close();
			}
		}
	}
	
	private void popWindown(){
		popWindown(call.getResources().getString(R.string.nosimorusimmessage));
	}
	
	private void popWindown(String message){
		PviAlertDialog errorDialog =  new PviAlertDialog(call);
		errorDialog.setCanClose(true);
		errorDialog.setTitle(call.getResources().getString(R.string.nosimorusim));
		errorDialog.setMessage(message);
		errorDialog.show();
	}
}
