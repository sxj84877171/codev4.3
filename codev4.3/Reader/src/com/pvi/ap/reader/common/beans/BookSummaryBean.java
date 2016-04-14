package com.pvi.ap.reader.common.beans;

import android.graphics.Bitmap;

public class BookSummaryBean {
	
	// 标题
	public String title = "无标题" ;
	// 封面
	public Bitmap bookSurface = null ;
	// 作者
	public String auther = "暂无" ;
	// 字数
	public String codeNum = "0" ;
	// 章节
	public String chapterNum = "未知" ;
	// 大小
	public String bookSize = "0" ;
	// 推荐
	public String recommend = "0" ;
	// 读者数
	public String readerNum = "0" ;
	// 点击
	public String clickNum = "0" ;
	// 收藏
	public String collectionNum = "0" ;
	// 鲜花
	public String flowersNum = "0" ;
	// 资费
	public String charges = "未知" ;
	// 内容简介
	public String contentAbstract = "暂无书籍简介..." ;
	// 星级数
	public int starNum = 0 ;
	
	public ContentAbstractListen contentAbstractListen = null; 
	
	
	
	public boolean isSerial = false ;
	public boolean isFinish = false ;
	public boolean canPresent = false ;
	public String authorID = "" ;
	public String chargeMode = "" ;
	
	public String smallUrl = "" ;
	
	public interface ContentAbstractListen {
		public void onClick();
	}
	
}
