package com.pvi.ap.reader.data.external.manager;


/**
 * XMLҶ�ӽ�����ݽṹ<br>
 * Ҷ�ӽ�㣬���ݰ�����ǩ���Լ���ֵ
 * @author rd038
 * @since 2010-10-10
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class LeafNode {
	
	/**
	 * �������
	 */
	private String ms_NodeName ; 
	
	/**
	 * ���ֵ
	 */
	private String ms_NodeValue ;
	
	/**
	 * ���캯��
	 * @param nodeName
	 * @param nodeValue
	 */
	
	public LeafNode(String nodeName,String nodeValue){
		this.ms_NodeName = nodeName;
		this.ms_NodeValue = nodeValue;
	}
	
	public String getNodeName() {
		return ms_NodeName;
	}

	public void setNodeName(String nodeName) {
		this.ms_NodeName = nodeName;
	}

	public String getNodeValue() {
		return ms_NodeValue;
	}

	public void setNodeValue(String nodeValue) {
		this.ms_NodeValue = nodeValue;
	}

	
	

}
