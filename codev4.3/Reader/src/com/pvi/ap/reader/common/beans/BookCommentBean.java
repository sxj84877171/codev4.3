package com.pvi.ap.reader.common.beans;

import com.pvi.ap.reader.activity.pviappframe.commentInfo;

public class BookCommentBean {
	
	public String totleCount = "(0��)" ;
	
	public BookCommentBean(){
		cinfo[0] = new commentInfo();
		cinfo[0].setComment("�����������ۣ���д�����Ĺ۵��������û�����ɣ�");
		cinfo[1] = new commentInfo();
//		cinfo[1].setComment("�����������ۡ���д�����Ĺ۵��������û�����ɣ�");
	}
	
	public commentInfo[] cinfo = new commentInfo[2];
	
	public CommentListen[] commentListen = new CommentListen[2] ;
	
	public CommentListen commentAllListen = null ;
	
	
	public interface CommentListen{
		void onClick();
	}
}
