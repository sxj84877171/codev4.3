package com.pvi.ap.reader.data.content;

import android.net.Uri;
import android.provider.BaseColumns;
import java.sql.Date;


public class HintInto implements BaseColumns
{
	private HintInto(){}
	
	public static final String	AUTHORITY			= "com.pvi.provider.hintintocontentprovider";
		

	public static final Uri		CONTENT_URI		    = Uri.parse("content://" + AUTHORITY + "/hintinto");

	// 新的MIME类型-多个
	public static final String	CONTENT_TYPE		= "vnd.android.cursor.dir/vnd.pvi.hintinto";

	// 新的MIME类型-单个
	public static final String	CONTENT_ITEM_TYPE	= "vnd.android.cursor.item/vnd.pvi.hintinto";

	public static final String	DEFAULT_SORT_ORDER	= "MessageID ASC";

	//字段
	public static final String	MessageID	        = "MessageID";
	public static final String	MessageType 		= "MessageType";
	public static final String	MessageContent 		= "MessageContent";
	public static final String  UpdateTime			= "UpdateTime";

}


