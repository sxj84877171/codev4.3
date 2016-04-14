package com.pvi.ap.reader.data.content;

import android.net.Uri;
import android.provider.BaseColumns;
import java.sql.Date;


public class Reading implements BaseColumns
{
	private Reading(){}
	
	public static final String	AUTHORITY			= "com.pvi.provider.readingcontentprovider";
		

	public static final Uri		CONTENT_URI		    = Uri.parse("content://" + AUTHORITY + "/reading");

	// 新的MIME类型-多个
	public static final String	CONTENT_TYPE		= "vnd.android.cursor.dir/vnd.pvi.reading";

	// 新的MIME类型-单个
	public static final String	CONTENT_ITEM_TYPE	= "vnd.android.cursor.item/vnd.pvi.reading";

	public static final String	DEFAULT_SORT_ORDER	= "UserID DESC";

	//字段
	public static final String	UserID				= "UserID";
	public static final String	ContentId			= "ContentId";
	public static final String	ChapterId		    = "ChapterId";
	public static final String	SourceType			= "SourceType";
	public static final String  FilePath			= "FilePath";
	public static final String  ReadName 			= "ReadName";
	public static final String  ReadPosition 		= "ReadPosition";
	public static final String  ReadTime 			= "ReadTime";
	public static final String  OperationType		= "OperationType";
	public static final String  SynchFlag			= "SynchFlag";
	public static final String  CertPath			= "CertPath";
}


