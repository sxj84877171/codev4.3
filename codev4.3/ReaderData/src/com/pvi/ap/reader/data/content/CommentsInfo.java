package com.pvi.ap.reader.data.content;

import android.net.Uri;
import android.provider.BaseColumns;
import java.sql.Date;


public class CommentsInfo implements BaseColumns
{
	private CommentsInfo(){}
	
	public static final String	AUTHORITY			= "com.pvi.provider.commentsinfocontentprovider";
		

	public static final Uri		CONTENT_URI		    = Uri.parse("content://" + AUTHORITY + "/commentsinfo");

	// �µ�MIME����-���
	public static final String	CONTENT_TYPE		= "vnd.android.cursor.dir/vnd.pvi.commentsinfo";

	// �µ�MIME����-����
	public static final String	CONTENT_ITEM_TYPE	= "vnd.android.cursor.item/vnd.pvi.commentsinfo";

	public static final String	DEFAULT_SORT_ORDER	= "UserID ASC";

	//�ֶ�
	public static final String	UserID		        = "UserID";//�û�ID
	public static final String	CommentId  			= "CommentId";//����ID
	public static final String	Comment 			= "Comment";//��������
	public static final String  CommentTime			= "CommentTime";//����ʱ��
	public static final String  CurrentPage 		= "CurrentPage";//����ҳ
	public static final String  ChapterId			= "ChapterId";//�½�ID
	public static final String 	ChapterName			= "ChapterName";//�½���
	public static final String  ContentId			= "ContentId";//����ID
	public static final String  ContentName			= "ContentName";//������
	public static final String  StartPosition		= "StartPosition";//��ע��ʼλ��
	public static final String  EndPosition 		= "EndPosition";//��ע����λ��
	public static final String  FilePath			= "FilePath";
	public static final String  CertPath			= "CertPath";
}


