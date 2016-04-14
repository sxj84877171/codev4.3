package com.pvi.ap.reader.data.content;

import android.net.Uri;
import android.provider.BaseColumns;
import java.sql.Date;


public class SubScribe implements BaseColumns
{
	private SubScribe(){}
	
	public static final String	AUTHORITY			= "com.pvi.provider.subscribecontentprovider";
		

	public static final Uri		CONTENT_URI		    = Uri.parse("content://" + AUTHORITY + "/subscribe");

	// 新的MIME类型-多个
	public static final String	CONTENT_TYPE		= "vnd.android.cursor.dir/vnd.pvi.subscribe";

	// 新的MIME类型-单个
	public static final String	CONTENT_ITEM_TYPE	= "vnd.android.cursor.item/vnd.pvi.subscribe";

	public static final String	DEFAULT_SORT_ORDER	= "Name DESC";

	//字段
	public static final String  UserID				= "UserID";
	public static final String	ContentID		    = "ContentID";
	public static final String	Name			    = "Name";
	public static final String	Author              = "Author";
	public static final String  URL					= "URL";
	public static final String  ChapterID			= "ChapterID";
	public static final String  ChapterName			= "ChapterName";
	public static final String  OrderTime			= "OrderTime";
}


