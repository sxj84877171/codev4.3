package com.pvi.ap.reader.external.txt;

/**
 * ҳ����Ϣ����
 * @author Elvis
 * @creation 2010-12-2
 */
public class PageContent {
	/**
	 * Ӳ�̶�ȡ����ʼλ��
	 */
	private int startPosition  = 0 ;
	/**
	 * Ӳ�̶�ȡ�Ľ���λ��
	 */
	private int endPosition = 0 ;
	/**
	 * Ӳ�̶�ȡ��λ�õ�����
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
