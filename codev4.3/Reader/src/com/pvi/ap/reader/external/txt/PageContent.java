package com.pvi.ap.reader.external.txt;

/**
 * 页面信息内容
 * @author Elvis
 * @creation 2010-12-2
 */
public class PageContent {
	/**
	 * 硬盘读取的起始位置
	 */
	private int startPosition  = 0 ;
	/**
	 * 硬盘读取的结束位置
	 */
	private int endPosition = 0 ;
	/**
	 * 硬盘读取段位置的内容
	 */
	private String content = "" ;
	public int getStartPosition() {
		return startPosition;
	}
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}
	public int getEndPosition() {
		return endPosition;
	}
	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	

}
