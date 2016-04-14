package com.pvi.ap.reader.data.content;

import android.net.Uri;
import android.provider.BaseColumns;
import java.sql.Date;


public class APInfo implements BaseColumns
{
	private APInfo(){}
	
	public static final String	AUTHORITY			= "com.pvi.provider.apinfocontentprovider";
		

	public static final Uri		CONTENT_URI		    = Uri.parse("content://" + AUTHORITY + "/apinfo");

	// �µ�MIME����-���
	public static final String	CONTENT_TYPE		= "vnd.android.cursor.dir/vnd.pvi.apinfo";

	// �µ�MIME����-����
	public static final String	CONTENT_ITEM_TYPE	= "vnd.android.cursor.item/vnd.pvi.apinfo";

	public static final String	DEFAULT_SORT_ORDER	= "ApName ASC";

	//�ֶ�
	public static final String	ApName		        = "ApName";
	public static final String	ApDeveloper 		= "ApDeveloper";
	public static final String	ApVersion 			= "ApVersion";
	public static final String  ApUpdateTime  		= "ApUpdateTime";
	public static final String  ApSize      		= "ApSize";

}


