package com.pvi.ap.reader.data.external.manager;

import java.util.List;

/**
 * XML������ݽṹ<br>
 * ����ɱ�ʾһ��XML�����ṹ�ĵ����Զ����ǩ���Լ��ӽ���б�<br>
 * �ӽ�����2�����ͣ�һΪҶ�ӽ�㣬һ���еݹ麬�����XmlElement
 * @author rd038
 * @since 2010-10-10
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class XmlElement {
	
	/**
	 * ��ǩ��
	 */
	private String ms_TagName ; 
	
	/**
	 * �ӽڵ�
	 */
	private List ml_NodeList ; 
	
	/**
	 * ���캯��
	 * @param tagName
	 * @param nodeList
	 */
	public XmlElement(String tagName,List nodeList){
		this.ms_TagName = tagName;
		this.ml_NodeList = nodeList;
	}
	
	public String getTagName() {
		return ms_TagName;
	}

	public void setTagName(String tagName) {
		this.ms_TagName = tagName;
	}

	public List getNodeList() {
		return ml_NodeList;
	}

	public void setNodeList(List nodeList) {
		this.ml_NodeList = nodeList;
	}

	

}
