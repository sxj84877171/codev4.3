package com.pvi.ap.reader.common.beans;

public class BookChapterBean {
	public BookChapterBean(){
		chapter[0] = new Chapter();
//		chapter[0].chapterName = "第一章 揭开人性的秘密(收费)" ;
		chapter[1] = new Chapter();
//		chapter[1].chapterName = "第二章 女人一生的成长心经(收费)" ;
		chapter[2] = new Chapter();
//		chapter[2].chapterName = "第三章 读懂心情，做对事情(收费)" ;
		chapter[3] = new Chapter();
//		chapter[3].chapterName = "第四章 透析对方的男女读心(收费)" ;
	}
	
	public Chapter[] chapter = new Chapter[4];

	public ChapterListen[] chapterListen = new ChapterListen[4];
	
	public ChapterListen chapterAllListen = null ;
	
	public interface ChapterListen{
		void onClick();
	}

}
