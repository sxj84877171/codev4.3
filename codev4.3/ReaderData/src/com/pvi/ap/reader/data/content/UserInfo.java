package com.pvi.ap.reader.data.content;

import android.net.Uri;
import android.provider.BaseColumns;
import java.sql.Date;


public class UserInfo implements BaseColumns
{
	private UserInfo(){}
	
	public static final String	AUTHORITY			= "com.pvi.provider.userinfocontentprovider";
		

	public static final Uri		CONTENT_URI		    = Uri.parse("content://" + AUTHORITY + "/userinfo");

	// 新的MIME类型-多个
	public static final String	CONTENT_TYPE		= "vnd.android.cursor.dir/vnd.pvi.userinfo";

	// 新的MIME类型-单个
	public static final String	CONTENT_ITEM_TYPE	= "vnd.android.cursor.item/vnd.pvi.userinfo";

	public static final String	DEFAULT_SORT_ORDER	= "UserID DESC";

	//字段
	public static final String	UserID		        = "UserID";
	public static final String  SimID        		= "SimID";
	public static final String	UserName			= "UserName";
	public static final String	NickName			= "NickName";
	public static final String  LineNum				= "LineNum";
	public static final String	UserMoney           = "UserMoney";
	public static final String	UserLevel           = "UserLevel";
	public static final String	RegCode             = "RegCode";
	public static final String  HeadID              = "HeadID";
	public static final String	Sex                 = "Sex";
	public static final String	Age   		        = "Age";
	public static final String	Province            = "Province";
	public static final String  City                = "City";
	public static final String  Description         = "Description";
	public static final String  Signature           = "Signature";
	public static final String  BookUpdateType      = "BookUpdateType";
	public static final String  ModifyFlag          = "ModifyFlag";
	public static final String  RegisterTime        = "RegisterTime";
}


