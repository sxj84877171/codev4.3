package com.pvi.ap.reader.data.content;

import android.net.Uri;
import android.provider.BaseColumns;
import java.sql.Date;


public class LocalBook implements BaseColumns
{
	private LocalBook(){}
	
	public static final String	AUTHORITY			= "com.pvi.provider.localbookcontentprovider";
		

	public static final Uri		CONTENT_URI		    = Uri.parse("content://" + AUTHORITY + "/localbook");

	// �µ�MIME����-���
	public static final String	CONTENT_TYPE		= "vnd.android.cursor.dir/vnd.pvi.localbook";

	// �µ�MIME����-����
	public static final String	CONTENT_ITEM_TYPE	= "vnd.android.cursor.item/vnd.pvi.localbook";

	public static final String	DEFAULT_SORT_ORDER	= "Name DESC";

	//�ֶ�
	public static final String	ContentID		    = "ContentID";
	public static final String	Name			    = "Name";
	public static final String  Catelog				= "Catelog";
	public static final String	BookType            = "BookType";
	public static final String	PathType            = "PathType";
	public static final String	Author              = "Author";
	public static final String	Maker   		    = "Maker";
	public static final String	SaleTime            = "SaleTime";
	public static final String  BookPosition        = "BookPosition";
	public static final String  ChapterID			= "ChapterID";
	public static final String  BookSize			= "BookSize";
	
}


