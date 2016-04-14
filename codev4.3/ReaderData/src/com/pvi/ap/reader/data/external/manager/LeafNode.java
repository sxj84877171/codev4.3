package com.pvi.ap.reader.data.external.manager;


/**
 * XML叶子结点数据结构<br>
 * 叶子结点，内容包含标签名以及其值
 * @author rd038
 * @since 2010-10-10
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class LeafNode {
	
	/**
	 * 结点名称
	 */
	private String ms_NodeName ; 
	
	/**
	 * 结点值
	 */
	private String ms_NodeValue ;
	
	/**
	 * 构造函数
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
