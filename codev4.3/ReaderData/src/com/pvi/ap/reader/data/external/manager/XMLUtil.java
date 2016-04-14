package com.pvi.ap.reader.data.external.manager;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import com.pvi.ap.reader.data.common.Constants;

/**
 * XML帮助类<br>
 * 该类用来生成XML字符串
 * @author rd038
 * @since 2010-10-10
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class XMLUtil {
	
	/**
	 * 给xmlElement加上父标签tagName
	 * @param tagName 父标签名
	 * @param xmlElement 子标签
	 * @return XmlElement 加上父标签的XML结点
	 */
	public static XmlElement getParentXmlElement(String tagName,XmlElement  xmlElement){
		
		List<XmlElement> list = new ArrayList<XmlElement>();
		list.add(xmlElement);
		return new XmlElement(tagName, list);
	}
	
	/**
	 * 生成XML字符串<br>
	 * 根据XML结点生成其文本字符串
	 * @param rootElment
	 * @return
	 * @throws ParserConfigurationException
	 */
	public static String getXmlStringFromXmlElement(XmlElement rootElment) throws ParserConfigurationException {	
		
		return Constants.ms_XMLHeader + getXmlBodyStringFromXmlElement(rootElment);
		
	}
	
	/**
	 * 生成字符串主体字符串
	 * 递归调用
	 * @param rootElment
	 * @return
	 * @throws ParserConfigurationException
	 */
	public static String getXmlBodyStringFromXmlElement(XmlElement rootElment) throws ParserConfigurationException {	
		
		if(rootElment == null){
			return "";
		}
		StringBuffer retXmlString = new StringBuffer("");
	    
	    String elmentName = rootElment.getTagName();
	    
	    retXmlString.append("<"+elmentName+">");
	    List nodeList = rootElment.getNodeList();
	    for(int i=0;i<nodeList.size();i++){
	    	Object o = nodeList.get(i);
	    	if(o instanceof LeafNode){ //叶子结点
	    		LeafNode node = (LeafNode)o;
	    		String nodeName = node.getNodeName();
	    		String nodeValue = node.getNodeValue();
	    		retXmlString.append("<"+nodeName+">");
	    		retXmlString.append(nodeValue);
	    		retXmlString.append("</"+nodeName+">");
	    	} else if (o instanceof XmlElement){ //非叶子结点
	    		//递归调用生成字符串
	    		XmlElement element = (XmlElement)o;
	    		retXmlString.append(getXmlBodyStringFromXmlElement(element));
	    	}
	    }
	    retXmlString.append("</"+elmentName+">");
	    
	    return retXmlString.toString();
	}
}
