package com.pvi.ap.reader.data.content;

import android.net.Uri;
import android.provider.BaseColumns;
import java.sql.Date;


public class Favorites implements BaseColumns
{
	private Favorites(){}
	
	public static final String	AUTHORITY			= "com.pvi.provider.favoritescontentprovider";
		

	public static final Uri		CONTENT_URI		    = Uri.parse("content://" + AUTHORITY + "/favorites");

	// 新的MIME类型-多个
	public static final String	CONTENT_TYPE		= "vnd.android.cursor.dir/vnd.pvi.favorites";

	// 新的MIME类型-单个
	public static final String	CONTENT_ITEM_TYPE	= "vnd.android.cursor.item/vnd.pvi.favorites";

	public static final String	DEFAULT_SORT_ORDER	= "UserID ASC";

	//字段
	public static final String	UserID		        = "UserID";
	public static final String	ContentId  			= "ContentId";
	public static final String	ContentName 		= "ContentName";
	public static final String  Author  			= "Author";
	public static final String  FavoriteTime 		= "FavoriteTime";
	public static final String  FavoriteURL 		= "FavoriteURL";
	public static final String  ChapterId			= "ChapterId";
	public static final String  ChapterName 		= "ChapterName";
}


