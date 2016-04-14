package com.pvi.ap.reader.data.content;

import android.net.Uri;
import android.provider.BaseColumns;
import java.sql.Date;


public class RegInfo implements BaseColumns
{
	private RegInfo(){}
	
	public static final String	AUTHORITY			= "com.pvi.provider.reginfocontentprovider";
		

	public static final Uri		CONTENT_URI		    = Uri.parse("content://" + AUTHORITY + "/reginfo");

	// 新的MIME类型-多个
	public static final String	CONTENT_TYPE		= "vnd.android.cursor.dir/vnd.pvi.reginfo";

	// 新的MIME类型-单个
	public static final String	CONTENT_ITEM_TYPE	= "vnd.android.cursor.item/vnd.pvi.reginfo";

	public static final String	DEFAULT_SORT_ORDER	= "IMEI DESC";

	//字段
	public static final String	CreatedDate		    = "CreatedDate";
	public static final String	Production			= "Production";
	public static final String  DeviceVersion		= "DeviceVersion";
	public static final String	HardwareVersion     = "HardwareVersion";
	public static final String	SoftwareVersion     = "SoftwareVersion";
	public static final String	UserAgent           = "UserAgent";
	public static final String  APIversion          = "APIversion";
	public static final String	ScreenSize          = "Screensize";
	public static final String	ClientKey		    = "ClientKey";
	public static final String	IMEI                = "IMEI";

}


