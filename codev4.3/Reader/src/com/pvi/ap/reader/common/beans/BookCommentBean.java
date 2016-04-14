package com.pvi.ap.reader.common.beans;

import com.pvi.ap.reader.activity.pviappframe.commentInfo;

public class BookCommentBean {
	
	public String totleCount = "(0条)" ;
	
	public BookCommentBean(){
		cinfo[0] = new commentInfo();
		cinfo[0].setComment("该书暂无评论，请写下您的观点与其他用户分享吧！");
		cinfo[1] = new commentInfo();
//		cinfo[1].setComment("该书赞无评论。请写下您的观点与其他用户分享吧！");
	}
	
	public commentInfo[] cinfo = new commentInfo[2];
	
	public CommentListen[] commentListen = new CommentListen[2] ;
	
	public CommentListen commentAllListen = null ;
	
	
	public interface CommentListen{
		void onClick();
	}
}
