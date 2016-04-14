package com.pvi.ap.reader.data.content;

import android.net.Uri;
import android.provider.BaseColumns;
import java.sql.Date;


public class BookInfo implements BaseColumns
{
	private BookInfo(){}
	
	public static final String	AUTHORITY			= "com.pvi.provider.bookinfocontentprovider";
		

	public static final Uri		CONTENT_URI		    = Uri.parse("content://" + AUTHORITY + "/bookinfo");

	// 新的MIME类型-多个
	public static final String	CONTENT_TYPE		= "vnd.android.cursor.dir/vnd.pvi.bookinfo";

	// 新的MIME类型-单个
	public static final String	CONTENT_ITEM_TYPE	= "vnd.android.cursor.item/vnd.pvi.bookinfo";

	public static final String	DEFAULT_SORT_ORDER	= "Name DESC";

	//字段
	public static final String	ContentID		    = "ContentID";
	public static final String	Name			    = "Name";
	public static final String  Catelog				= "Catelog";
	public static final String	BookType            = "BookType";
	public static final String	PathType            = "PathType";
	public static final String	ProcessPer          = "ProcessPer";
	public static final String  DownloadTime        = "DownloadTime";
	public static final String  DownloadStatus		= "DownloadStatus";
	public static final String	Author              = "Author";
	public static final String	Maker   		    = "Maker";
	public static final String	SaleTime            = "SaleTime";
	public static final String  BookPosition        = "BookPosition";
	public static final String  CertPath			= "CertPath";
	public static final String  URL					= "URL";
	public static final String  ChapterID			= "ChapterID";
	public static final String  BookSize			= "BookSize";
	public static final String  CertStatus			= "CertStatus";
	public static final String  DownloadType         = "DownloadType";
	
}


