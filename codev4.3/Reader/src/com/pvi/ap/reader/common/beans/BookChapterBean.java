package com.pvi.ap.reader.common.beans;

public class BookChapterBean {
	public BookChapterBean(){
		chapter[0] = new Chapter();
//		chapter[0].chapterName = "��һ�� �ҿ����Ե�����(�շ�)" ;
		chapter[1] = new Chapter();
//		chapter[1].chapterName = "�ڶ��� Ů��һ���ĳɳ��ľ�(�շ�)" ;
		chapter[2] = new Chapter();
//		chapter[2].chapterName = "������ �������飬��������(�շ�)" ;
		chapter[3] = new Chapter();
//		chapter[3].chapterName = "������ ͸���Է�����Ů����(�շ�)" ;
	}
	
	public Chapter[] chapter = new Chapter[4];

	public ChapterListen[] chapterListen = new ChapterListen[4];
	
	public ChapterListen chapterAllListen = null ;
	
	public interface ChapterListen{
		void onClick();
	}

}
