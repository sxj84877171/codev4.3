package com.pvi.ap.reader.data.content;

import android.net.Uri;
import android.provider.BaseColumns;
import java.sql.Date;


public class NotesInfo implements BaseColumns
{
	private NotesInfo(){}
	
	public static final String	AUTHORITY			= "com.pvi.provider.notesinfocontentprovider";
		

	public static final Uri		CONTENT_URI		    = Uri.parse("content://" + AUTHORITY + "/notesinfo");

	// 新的MIME类型-多个
	public static final String	CONTENT_TYPE		= "vnd.android.cursor.dir/vnd.pvi.notesinfo";

	// 新的MIME类型-单个
	public static final String	CONTENT_ITEM_TYPE	= "vnd.android.cursor.item/vnd.pvi.notesinfo";

	public static final String	DEFAULT_SORT_ORDER	= "NoteID ASC";

	//字段
	public static final String	NoteID		        = "NoteID";
	public static final String	NoteName 		    = "NoteName";
	public static final String	NoteText 			= "NoteText";
	public static final String  CreateDate  		= "CreateDate";

}


