package com.pvi.ap.reader.data.content;

import android.net.Uri;
import android.provider.BaseColumns;
import java.sql.Date;


public class OptLogInfo implements BaseColumns
{
	private OptLogInfo(){}
	
	public static final String	AUTHORITY			= "com.pvi.provider.optloginfocontentprovider";
		

	public static final Uri		CONTENT_URI		    = Uri.parse("content://" + AUTHORITY + "/optloginfo");

	// 新的MIME类型-多个
	public static final String	CONTENT_TYPE		= "vnd.android.cursor.dir/vnd.pvi.optloginfo";

	// 新的MIME类型-单个
	public static final String	CONTENT_ITEM_TYPE	= "vnd.android.cursor.item/vnd.pvi.optloginfo";

	public static final String	DEFAULT_SORT_ORDER	= "UserID ASC";

	//字段
	public static final String	UserID		        = "UserID";
	public static final String	LogType  			= "LogType";
	public static final String	LogConnent			= "LogConnent";
	public static final String  CommentTime			= "CommentTime";
	public static final String  OperationType		= "OperationType";
	public static final String  SynchFlag			= "SynchFlag";
}


