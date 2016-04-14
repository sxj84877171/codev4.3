package com.pvi.ap.reader.data.external.manager;

import java.util.List;

/**
 * XML结点数据结构<br>
 * 该类可表示一个XML的树结构文档：自定义标签名以及子结点列表<br>
 * 子结点可有2种类型，一为叶子结点，一具有递归含义的子XmlElement
 * @author rd038
 * @since 2010-10-10
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class XmlElement {
	
	/**
	 * 标签名
	 */
	private String ms_TagName ; 
	
	/**
	 * 子节点
	 */
	private List ml_NodeList ; 
	
	/**
	 * 构造函数
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
