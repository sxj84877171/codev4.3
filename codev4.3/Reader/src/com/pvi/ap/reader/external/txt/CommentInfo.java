package com.pvi.ap.reader.external.txt;

import java.util.Comparator;

/**
 * 批注信息
 * @author Elvis
 * @creation 2010-12-1
 */
public class CommentInfo implements Comparator{
	
	private int startPostion = 0 ;
	private int endPostion = 0 ;
	private String comment = "" ;
	
	public int getStartPostion() {
		return startPostion;
	}
	
	public void setStartPostion(int startPostion) {
		this.startPostion = startPostion;
	}
	
	public int getEndPostion() {
		return endPostion;
	}
	
	public void setEndPostion(int endPostion) {
		this.endPostion = endPostion;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * 给该对象进行排序
	 */
	@Override
	public int compare(Object object1, Object object2) {
		CommentInfo ci1 = (CommentInfo)object1 ;
		CommentInfo ci2 = (CommentInfo)object2 ;
		return ci1.getStartPostion() - ci2.getStartPostion();
	}

}
