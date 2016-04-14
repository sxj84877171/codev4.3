package com.pvi.ap.reader.data.external.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpException;

import android.util.Log;

import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.data.common.DencryptHelper;
import com.pvi.ap.reader.data.external.connection.DRMConnection;

/**
 * DRMƽ̨�ӿ�<br>
 * �������������ƶ�DRM���������ṩ���֤�����أ���ȡ��Կ�ӿ�
 * @author rd038
 * @since 2010-10-10
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */

public class DRMManager {
	

	/**
	 * DRM����
	 * @param ahm
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static HashMap active(HashMap ahm) throws HttpException, IOException {
		
		Log.d("DRMManager.active()", "Get into active Method");
		
		HashMap retHashMap = new HashMap(); //����HashMap
		
		//��ahm�����л�ȡ�������ֵ
		String version = (String)ahm.get("Version"); //�汾
		String userid = (String)ahm.get("user-id");	//�û�ID
		String password = (String)ahm.get("password");	//����
		
		//��version��userid��password���ֵȡMD5 Hashֵ����Base64����
		String temp = version + userid + password;
		//Log.d("DRMManager.active()", "ԭ�ַ���"+temp);

		String base64str = DencryptHelper.md5encrypt(temp);
		//Log.d("DRMManager.active()", "Base64���ܺ�ֵ"+base64str);
		
		//����DRMConnection doGet��������DRM
		//ȡ��doGet����ֵ���ŵ�HashMap�����з���
		DRMConnection dRMConnection = new DRMConnection(Config.getString("DRM_URL_ACTIVE"));
		HashMap ahmHeaderMap = new HashMap();
		ahmHeaderMap.put("Accept", (String)ahm.get("Accept"));
		ahmHeaderMap.put("Host", (String)ahm.get("Host"));
		ahmHeaderMap.put("User-Agent", (String)ahm.get("User-Agent"));
		ahmHeaderMap.put("Version", (String)ahm.get("Version"));
		if(ahm.get("x-up-calling-line-id") != null){
			ahmHeaderMap.put("x-up-calling-line-id", (String)ahm.get("x-up-calling-line-id"));
		}
		ahmHeaderMap.put("user-id", (String)ahm.get("user-id"));
		ahmHeaderMap.put("ReqDigest", base64str);
		
		//���Դ�retHashMap�õ�������Ϣ
		//������
		//Status  HTTP/1.1 200 OK
		//RegCode
		//RspDigest
		retHashMap = dRMConnection.doGet(ahmHeaderMap, null);
		
		//Log.d("DRMManager.active()", "Get out active Method");
		
		return retHashMap;
	}

	/**
	 * ����֤��ӿ�
	 * @param ahm
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static HashMap downloadCert(HashMap ahm) throws HttpException, IOException {
		
		Log.d("DRMManager.downloadCert()", "Get into downloadCert Method");
		
		
		HashMap retHashMap = new HashMap(); //����HashMap
		
		//��ahm�����л�ȡ�������ֵ
		String requestType = (String)ahm.get("requesttype");
		String nonce = (String)ahm.get("Nonce");
		String version = (String)ahm.get("Version");
		String userid = (String)ahm.get("user-id");
		String pid = (String)ahm.get("PID");
		String password = (String)ahm.get("password");
		
		//��requesttype��nonce��version��userid��pid��password���ֵȡMD5 Hashֵ����Base64����
		String temp = requestType + nonce + version + userid + pid + password;
		//Log.d("DRMManager.downloadCert()", "ԭ�ַ���"+temp);
	
		String base64str = DencryptHelper.md5encrypt(temp);
		//Log.d("DRMManager.downloadCert()", "Base64���ܺ�ֵ"+base64str);
		
		//����DRMConnection doGet��������DRM
		//ȡ��doGet����ֵ���ŵ�HashMap�����з���
		//Log.d("DRMManager.downloadCert()", "connect to server:"+Constants.DRM_URL_DOWNLOADCERT);
		DRMConnection dRMConnection = new DRMConnection(Constants.DRM_URL_DOWNLOADCERT);
		//Log.d("DRMManager.downloadCert()", "connect to server succed!");
		HashMap ahmHeaderMap = new HashMap();
		
		if(ahm.get("CID")!=null){
		    ahmHeaderMap.put("CID",(String)ahm.get("CID"));
		}
		if(ahm.get("PID")!=null){
		    ahmHeaderMap.put("PID",(String)ahm.get("PID"));
		}
		ahmHeaderMap.put("Nonce",(String)ahm.get("Nonce"));
		ahmHeaderMap.put("Accept",(String)ahm.get("Accept"));
		ahmHeaderMap.put("Host",(String)ahm.get("Host"));
		ahmHeaderMap.put("User-Agent",(String)ahm.get("User-Agent"));
		ahmHeaderMap.put("requesttype",(String)ahm.get("requesttype"));
		ahmHeaderMap.put("Version",(String)ahm.get("Version"));
		if(ahm.get("x-up-calling-line-id")!=null){
		    ahmHeaderMap.put("x-up-calling-line-id",(String)ahm.get("x-up-calling-line-id"));
		}
		ahmHeaderMap.put("user-id",(String)ahm.get("user-id"));
		ahmHeaderMap.put("ReqDigest",base64str);
		
		//Log.d("DRMManager.downloadCert()",ahmHeaderMap.toString());
		
		//���Դ�retHashMap�õ�������Ϣ
		//������
		//Status  HTTP/1.1 200 OK
		//֤�������ֽ�����

        retHashMap = dRMConnection.doGet(ahmHeaderMap, null);

		
		//Log.d("DRMManager.downloadCert()", "Get out downloadCert Method");
		
		return retHashMap;
	}
	
	
	/**
	 * ��ȡ��Կ
	 * @param fileName
	 * @param regcode
	 * @param password
	 * @param userid
	 * @return
	 * @throws Exception
	 */
	public static String getKey(String fileName,String regcode,String password,String userid) throws Exception {	
		
		//Log.d("DRMManager.getKey()", "Get into getKey Method");
		
		InputStream is = new FileInputStream(fileName);
		int size = is.available();
		//Log.d("DRMManager.getKey()", "size:"+size);
		byte[] resbt = new byte[size];
		is.read(resbt);
		
	    byte[] param1 = new byte[16];				//ǰ16λ
	    byte[] cert = new byte[resbt.length-16];	//��17λ��ʼ�����
	    
	    //Log.d("DRMManager.getKey()", new String(resbt));
	    System.arraycopy(resbt, 0, param1, 0, 16);
	    System.arraycopy(resbt, 16, cert, 0, resbt.length-16);
	    
	    String str = regcode + password + userid;
		String rek = DencryptHelper.md5encrypt(str);
	     
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");   
		messageDigest.update(str.getBytes());  
		byte rek1[] = messageDigest.digest();
		
		String retKeyXml = DencryptHelper.aesdecrypt(cert, rek1, param1);
		//Log.d("DRMManager.getKey()", "���ص���ԿXML�ļ�"+retKeyXml);
		
		//Log.d("DRMManager.getKey()", "Get out getKey Method");
		
		return retKeyXml;
	}

}
