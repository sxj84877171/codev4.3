package com.pvi.ap.reader.data.external.connection;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpException;
/**
 * �ƶ��������ӽӿ�<br>
 * ����Ϊ�����ƶ�����Ļ��࣬Ϊ�����ṩPost���ӷ�ʽ��Get���ӷ�ʽ<br>
 * ����������CPConnection �� DRMConnection
 * @author rd038
 * @since 2010-10-10
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public abstract class CMGWConnection {
	
	/**
	 * ��POST��ʽ���� 
	 * @param ahm_HeaderMap HTTPͷ����
	 * @param ahm_NamePair  HTTP�������
	 * @return	��������Ϣ 
	 * @throws HttpException
	 * @throws IOException
	 */
	public abstract HashMap doPost(HashMap ahm_HeaderMap,HashMap ahm_NamePair) throws HttpException, IOException ;
	
	/**
	 * ��GET��ʽ���� 
	 * @param ahm_HeaderMap HTTPͷ����
	 * @param ahm_NamePair  HTTP�������
	 * @return	��������Ϣ 
	 * @throws HttpException
	 * @throws IOException
	 */
	public abstract HashMap doGet(HashMap ahm_HeaderMap,HashMap ahm_NamePair) throws HttpException, IOException ;

}
