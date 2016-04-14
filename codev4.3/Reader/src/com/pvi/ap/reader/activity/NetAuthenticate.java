package com.pvi.ap.reader.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.Bookmark;
import com.pvi.ap.reader.data.content.UserInfo;

/**
 * 绑定流程类<br>
 * @author Elvis
 * @since 2010-11-25
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public abstract class NetAuthenticate {
	
	protected  Activity activity = null;
	/**
	 * userID
	 */
	protected String userID = "" ;
	/**
	 * 激活码
	 */
	protected String regCode = "" ;
	
    protected GlobalVar appState = null;
	
	protected Intent myIntent = null ;
	 
	public abstract void mainRun();
	public abstract void setMyIntent(Intent myIntent);

	
	public static NetAuthenticate getNetAuthenticate(Activity activity){
		final String cardType = ((GlobalVar)activity.getApplication()).getSimType();
		Logger.i("NetAuthenticate", "cardType" + cardType);
		if (cardType != null) {
			if (cardType.equalsIgnoreCase("usim")) {
				return new NetAuthenticateUSIM(activity);  
			} else if (cardType.equalsIgnoreCase("sim")) {
				return new NetAuthenticateSIM(activity);
			}
		}
		return null;
	}
	
	protected void saveOrUpdate(){
		saveOrUpdate(null);
	}
	
	protected void saveOrUpdate(String photo){
		String simID = appState.getSimID();
		String where = UserInfo.SimID + " = '" + simID + "'";
		String columns[] = { UserInfo.UserID, UserInfo.RegCode,
				UserInfo.LineNum, UserInfo.NickName, };
		if (simID != null && !"".equals(simID.trim())) {
			Cursor cur = activity.managedQuery(UserInfo.CONTENT_URI, columns,
					where, null, null);
			if (cur == null) {
				saveInfo(photo);
				return;
			}
			if (!cur.moveToFirst()) {
				saveInfo(photo);
				return;
			}
			if(!cur.isClosed()){
				cur.close();
			}
		}
		ContentValues values = new ContentValues();
		values.put(UserInfo.SimID, appState.getSimID());
		values.put(UserInfo.UserID, userID);
		values.put(UserInfo.RegCode, regCode);
		if(photo != null && !"".equals(photo)){
			values.put(UserInfo.LineNum, photo);
		}
		activity.getContentResolver().update(Bookmark.CONTENT_URI, values,	where, null);
	}
	
	private void saveInfo(String photo){
		ContentValues values = new ContentValues();
		values.put(UserInfo.SimID, appState.getSimID());
		values.put(UserInfo.UserID, userID);
		values.put(UserInfo.RegCode, regCode);
		if(photo != null && !"".equals(photo)){
			values.put(UserInfo.LineNum, photo);
		}
		activity.getContentResolver().insert(UserInfo.CONTENT_URI, values);
	}
	
}
