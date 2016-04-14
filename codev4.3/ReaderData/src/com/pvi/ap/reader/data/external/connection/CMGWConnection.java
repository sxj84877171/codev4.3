package com.pvi.ap.reader.data.external.connection;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpException;
/**
 * 移动网络连接接口<br>
 * 该类为连接移动网络的基类，为子类提供Post连接方式和Get连接方式<br>
 * 具体子类有CPConnection 和 DRMConnection
 * @author rd038
 * @since 2010-10-10
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public abstract class CMGWConnection {
	
	/**
	 * 以POST方式连接 
	 * @param ahm_HeaderMap HTTP头参数
	 * @param ahm_NamePair  HTTP请求参数
	 * @return	请求结果信息 
	 * @throws HttpException
	 * @throws IOException
	 */
	public abstract HashMap doPost(HashMap ahm_HeaderMap,HashMap ahm_NamePair) throws HttpException, IOException ;
	
	/**
	 * 以GET方式连接 
	 * @param ahm_HeaderMap HTTP头参数
	 * @param ahm_NamePair  HTTP请求参数
	 * @return	请求结果信息 
	 * @throws HttpException
	 * @throws IOException
	 */
	public abstract HashMap doGet(HashMap ahm_HeaderMap,HashMap ahm_NamePair) throws HttpException, IOException ;

}
