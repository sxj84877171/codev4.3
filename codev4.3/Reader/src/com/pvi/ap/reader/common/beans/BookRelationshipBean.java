package com.pvi.ap.reader.common.beans;

import android.graphics.Bitmap;

public class BookRelationshipBean {
	public String book1Name[] =
		new String[6];
//		           {"星晨变","修真研究","修真研究","修真研究","修真研究","修真研究"};
	public String[] book1ContentID = new String[6];
	public Bitmap book1Map[] = new Bitmap[6];
	public BookListen[] bookListen = new BookListen[6];
	public BookRelationshipListen[] bookRelationshipListen = new BookRelationshipListen[2];
	
	public interface BookListen{
		public void onClick();
	}
	
	public interface BookRelationshipListen{
		void onClick();
	}
}
