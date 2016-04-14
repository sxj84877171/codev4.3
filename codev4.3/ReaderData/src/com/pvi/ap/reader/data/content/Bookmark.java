package com.pvi.ap.reader.data.content;

import android.net.Uri;
import android.provider.BaseColumns;
import java.sql.Date;


public class Bookmark implements BaseColumns
{
	private Bookmark(){}
	
	public static final String	AUTHORITY			= "com.pvi.provider.bookmarkcontentprovider";
		

	public static final Uri		CONTENT_URI		    = Uri.parse("content://" + AUTHORITY + "/bookmark");

	// 新的MIME类型-多个
	public static final String	CONTENT_TYPE		= "vnd.android.cursor.dir/vnd.pvi.bookmark";

	// 新的MIME类型-单个
	public static final String	CONTENT_ITEM_TYPE	= "vnd.android.cursor.item/vnd.pvi.bookmark";

	public static final String	DEFAULT_SORT_ORDER	= "UserID DESC";

	//字段
	public static final String	UserID		        = "UserID";
	public static final String	BookmarkId			= "BookmarkId";
	public static final String	BookmarkType		= "BookmarkType";
	public static final String	SourceType			= "SourceType";
	public static final String  ChapterId			= "ChapterId";
	public static final String	ChapterName         = "ChapterName";
	public static final String	ContentId           = "ContentId";
	public static final String	ContentName         = "ContentName";
	public static final String  OperationType       = "OperationType";
	public static final String	SynchFlag           = "SynchFlag";
	public static final String	Position   		    = "Position";
	public static final String	Count               = "Count";
	public static final String  CreatedDate         = "CreatedDate";
	public static final String  FilePath			= "FilePath";
	public static final String  CertPath			= "CertPath";
	public static final String  Author				= "Author";
	public static final String  LineSpace			= "LineSpace";
	public static final String  FontSize			= "FontSize";
	public static final String  DownloadType         = "DownloadType";
}


