package com.pvi.ap.reader.data.external.manager;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import com.pvi.ap.reader.data.common.Constants;

/**
 * XML������<br>
 * ������������XML�ַ���
 * @author rd038
 * @since 2010-10-10
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class XMLUtil {
	
	/**
	 * ��xmlElement���ϸ���ǩtagName
	 * @param tagName ����ǩ��
	 * @param xmlElement �ӱ�ǩ
	 * @return XmlElement ���ϸ���ǩ��XML���
	 */
	public static XmlElement getParentXmlElement(String tagName,XmlElement  xmlElement){
		
		List<XmlElement> list = new ArrayList<XmlElement>();
		list.add(xmlElement);
		return new XmlElement(tagName, list);
	}
	
	/**
	 * ����XML�ַ���<br>
	 * ����XML����������ı��ַ���
	 * @param rootElment
	 * @return
	 * @throws ParserConfigurationException
	 */
	public static String getXmlStringFromXmlElement(XmlElement rootElment) throws ParserConfigurationException {	
		
		return Constants.ms_XMLHeader + getXmlBodyStringFromXmlElement(rootElment);
		
	}
	
	/**
	 * �����ַ��������ַ���
	 * �ݹ����
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
	    	if(o instanceof LeafNode){ //Ҷ�ӽ��
	    		LeafNode node = (LeafNode)o;
	    		String nodeName = node.getNodeName();
	    		String nodeValue = node.getNodeValue();
	    		retXmlString.append("<"+nodeName+">");
	    		retXmlString.append(nodeValue);
	    		retXmlString.append("</"+nodeName+">");
	    	} else if (o instanceof XmlElement){ //��Ҷ�ӽ��
	    		//�ݹ���������ַ���
	    		XmlElement element = (XmlElement)o;
	    		retXmlString.append(getXmlBodyStringFromXmlElement(element));
	    	}
	    }
	    retXmlString.append("</"+elmentName+">");
	    
	    return retXmlString.toString();
	}
}
