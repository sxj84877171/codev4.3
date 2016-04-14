package com.pvi.ap.reader.data.external.manager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Constants;


/**
 * 该类用来提供连接移动网络的一些公共方法
 * @author rd038
 * @since 2010-10-10
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class CPManagerUtil{
	
	public static String X_UP_CALLING_LINE_ID = "13466320945" ;
	public static String USER_ID = "1bd9c15b9f35d3186f5325ed97527352" ;
	
	/**
	 * 默认请求头信息
	 * @return
	 */
	public static HashMap getHeaderMap(){
		HashMap ahmHeaderMap = new HashMap();//保存Header信息
	
		//添加Header信息
		
		ahmHeaderMap.put("Accept",Config.getString("Accept"));
		ahmHeaderMap.put("Host",Config.getString("Host"));
		ahmHeaderMap.put("User-Agent",Config.getString("User-Agent"));
		ahmHeaderMap.put("APIVersion",Config.getString("APIVersion"));
		ahmHeaderMap.put("Client-Agent",Config.getString("Client-Agent"));	
		ahmHeaderMap.put("Content-type",Config.getString("Content-type"));
		if(X_UP_CALLING_LINE_ID != null && !"".equals(X_UP_CALLING_LINE_ID.trim())){
			ahmHeaderMap.put("x-up-calling-line-id",X_UP_CALLING_LINE_ID);
		}
		if(USER_ID != null && !"".equals(USER_ID.trim())){
			ahmHeaderMap.put("user-id",USER_ID);
		}
		if(Config.getString("DeviceID")!=null){
			ahmHeaderMap.put("deviceId",Config.getString("DeviceId"));
		}
		//ahmHeaderMap.put("DeviceId","018P801_20100920_001");
		//ahmHeaderMap.put("Accept","*/*");
		/*ahmHeaderMap.put("Host","211.138.125.71:9096");
		ahmHeaderMap.put("User-Agent","EInkStack");
		ahmHeaderMap.put("APIVersion","1.0.0");
		ahmHeaderMap.put("x-up-calling-line-id","13466320945");
		ahmHeaderMap.put("user-id","74e3ac4ff17000dd6ef3785d47696419");
		ahmHeaderMap.put("Client-Agent","PVI_P801_V0.10/800*600/other");	
		ahmHeaderMap.put("Content-type", "text/xml; charset=GBK");*/
		return ahmHeaderMap;
	}
	
	/**
	 * 默认请求参数信息 
	 * @return
	 */
	public static HashMap getAhmNamePairMap(){
		HashMap ahmNamePair = new HashMap();//保存请求参数信息 
		//ahmNamePair.put("start","1"); //默认分页开始
		//ahmNamePair.put("count","10"); //默认分页条数
		return ahmNamePair;
	}
	
	/**
	 * 根据字节数组返回由此构造的DOM文档
	 * @param byteData
	 * @return
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static Document getDocumentFrombyteArray(byte[] byteData) throws ParserConfigurationException, SAXException, IOException{
		
		InputStream in = new ByteArrayInputStream(byteData);
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder db = dbfactory.newDocumentBuilder();
	    Document dom = db.parse(in);
	    return dom;
	    
	}
	
	/**
	 * 根据字节数组返回字符串
	 * @param byteData
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String getStringFrombyteArray(byte[] byteData) throws UnsupportedEncodingException {
		
		return new String(byteData,Config.getString("UTFEncode"));
	    
	}

}
