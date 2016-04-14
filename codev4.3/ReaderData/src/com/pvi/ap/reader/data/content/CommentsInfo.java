package com.pvi.ap.reader.data.content;

import android.net.Uri;
import android.provider.BaseColumns;
import java.sql.Date;


public class CommentsInfo implements BaseColumns
{
	private CommentsInfo(){}
	
	public static final String	AUTHORITY			= "com.pvi.provider.commentsinfocontentprovider";
		

	public static final Uri		CONTENT_URI		    = Uri.parse("content://" + AUTHORITY + "/commentsinfo");

	// 新的MIME类型-多个
	public static final String	CONTENT_TYPE		= "vnd.android.cursor.dir/vnd.pvi.commentsinfo";

	// 新的MIME类型-单个
	public static final String	CONTENT_ITEM_TYPE	= "vnd.android.cursor.item/vnd.pvi.commentsinfo";

	public static final String	DEFAULT_SORT_ORDER	= "UserID ASC";

	//字段
	public static final String	UserID		        = "UserID";//用户ID
	public static final String	CommentId  			= "CommentId";//评论ID
	public static final String	Comment 			= "Comment";//评论内容
	public static final String  CommentTime			= "CommentTime";//评论时间
	public static final String  CurrentPage 		= "CurrentPage";//所在页
	public static final String  ChapterId			= "ChapterId";//章节ID
	public static final String 	ChapterName			= "ChapterName";//章节名
	public static final String  ContentId			= "ContentId";//书项ID
	public static final String  ContentName			= "ContentName";//书项名
	public static final String  StartPosition		= "StartPosition";//批注开始位置
	public static final String  EndPosition 		= "EndPosition";//批注结束位置
	public static final String  FilePath			= "FilePath";
	public static final String  CertPath			= "CertPath";
}


