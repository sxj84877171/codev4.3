package com.pvi.ap.reader.data.external.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import android.util.Log;

import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.data.common.Logger;

/**
 * ����ƽ̨���ӽӿ�<br>
 * ����ֱ�ʵ����Post��Get�����ƶ����磬�����������<br>
 * @author rd038
 * @since 2010-10-10
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class CPConnection extends CMGWConnection {
	
	public static String sdcard = "" ;
	
	/**
	 * �ƶ��������ӵ�ַ
	 */
	private String ms_CPURL = null;
	
	/**
	 * ���캯��
	 * @param ms_CPURL
	 */
	public CPConnection(String ms_CPURL) {//���캯��
		
		this.ms_CPURL = ms_CPURL;
		
	}


	/**
	 * ��POST��ʽ�����ƶ�����
	 * @param ahm_HeaderMap HTTPͷ����
	 * @param ahm_NamePair  HTTP�������
	 * @return	��������Ϣ 
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public HashMap doPost(HashMap ahmHeaderMap, HashMap ahmNamePair) throws HttpException, IOException {
		
		//Log.d("CPConnection.doPost()", "Get into doPost Method");
		
		HashMap retHashMap = new HashMap(); //���ؽ����ϢMap 
		
		//����PostMethod����
		PostMethod mhm_PostMethod = new PostMethod(ms_CPURL);
		System.out.println(ms_CPURL);
		
		//��ahmHeaderMap�Ĳ�����Ϣ���ε������õ�mhm_PostMethod Head
		if(ahmHeaderMap != null) {
			Iterator ahmHeaderMapI = ahmHeaderMap.keySet().iterator();
			while(ahmHeaderMapI.hasNext()) {
				Object o = ahmHeaderMapI.next();
				String key = o.toString();
				String value = (String)ahmHeaderMap.get(key);
				mhm_PostMethod.setRequestHeader(key, value);
			}
		}
		
		
		//��ahmNamePair�Ĳ�����Ϣ���õ�mhm_PostMethod Body
		List nameValuePairList = new ArrayList();
		if(ahmNamePair != null) {
			Iterator ahmNamePairI = ahmNamePair.keySet().iterator();
			while(ahmNamePairI.hasNext()) {
				Object o = ahmNamePairI.next();
				String key = o.toString();
				if(Constants.ms_PostReqParameter.equals(key)){
					continue;
				}
				String value = (String)ahmNamePair.get(key);
				NameValuePair nameValuePair = new NameValuePair(key,value);
				nameValuePairList.add(nameValuePair);
			}
		}
		NameValuePair[] nameValuePairs = new NameValuePair[nameValuePairList.size()];
		for(int i=0;i<nameValuePairList.size();i++) {
			nameValuePairs[i] = (NameValuePair)nameValuePairList.get(i);
		}
		mhm_PostMethod.setQueryString(nameValuePairs);
		
		if(ahmNamePair!=null && (String)ahmNamePair.get(Constants.ms_PostReqParameter)!=null ) {
			RequestEntity entity = new StringRequestEntity((String)ahmNamePair.get(Constants.ms_PostReqParameter), Config.getString("TextType"),   
					Config.getString("UTFEncode"));   
			mhm_PostMethod.setRequestEntity(entity);
		}
		
		HttpClient hClient = new HttpClient();//HttpClient���� 
		//if(cardType.equals("SIM")){
		try {
			if(sdcard.equals("SIM")){
				hClient.getHostConfiguration().setProxy(Config.getString("proxyIP"), Integer.parseInt(Config.getString("port")));
				System.out.println("Config.getString(\"proxyIP\")" + Config.getString("proxyIP"));
				System.out.println("Config.getString(\"port\")" + Config.getString("port"));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		//System.out.println("=====TIME_OUT======"+Config.getInt("TIME_OUT"));
		HttpConnectionManagerParams managerParams = hClient 
	    .getHttpConnectionManager().getParams(); 
			// �������ӳ�ʱʱ��(��λ����) 
			managerParams.setConnectionTimeout(15000); 
			// ���ö����ݳ�ʱʱ��(��λ����) 
			managerParams.setSoTimeout(15000);
		long startTimes = System.currentTimeMillis();
		Log.i("CPConnection.doPost() start times", String.valueOf(startTimes));
		//hClient.setConnectionTimeout(5000);
		System.out.println(mhm_PostMethod);
		hClient.executeMethod(mhm_PostMethod);//����
		long endTimes = System.currentTimeMillis();
		Log.i("CPConnection.doPost() end times", String.valueOf(endTimes));
		Log.i("CPConnection.doPost() lost times(ms)", String.valueOf(endTimes - startTimes));
		
		byte[] responseBody =  mhm_PostMethod.getResponseBody();
		retHashMap.put(Constants.ms_ResponseBody, responseBody);
		
		//��װ����Response Header������Response Body, ���뷵��HashMap������
	      retHashMap.put("Status", mhm_PostMethod.getStatusLine()==null?"":mhm_PostMethod.getStatusLine().toString());
	      retHashMap.put("TimeStamp", mhm_PostMethod.getResponseHeader("TimeStamp")==null?"":mhm_PostMethod.getResponseHeader("TimeStamp").toString());
	      retHashMap.put("Encoding-Type", mhm_PostMethod.getResponseHeader("Encoding-Type")==null?"":mhm_PostMethod.getResponseHeader("Encoding-Type").toString());
	      retHashMap.put("result-code", mhm_PostMethod.getResponseHeader("result-code")==null?"":mhm_PostMethod.getResponseHeader("result-code").toString());
	      retHashMap.put("Content-Type", mhm_PostMethod.getResponseHeader("Content-Type")==null?"":mhm_PostMethod.getResponseHeader("Content-Type").toString());
	      retHashMap.put("Content-Length", mhm_PostMethod.getResponseHeader("Content-Length")==null?"":mhm_PostMethod.getResponseHeader("Content-Length").toString());
	      retHashMap.put("Set-Cookie", mhm_PostMethod.getResponseHeader("Set-Cookie")==null?"":mhm_PostMethod.getResponseHeader("Set-Cookie").toString());
	      retHashMap.put("APIVersion", mhm_PostMethod.getResponseHeader("APIVersion")==null?"":mhm_PostMethod.getResponseHeader("APIVersion").toString());
	      retHashMap.put("Cache-Control", mhm_PostMethod.getResponseHeader("Cache-Control")==null?"":mhm_PostMethod.getResponseHeader("Cache-Control").toString());
	        
		
		/*
		Log.d("CPConnection.doPost()", "Return Status:"+mhm_PostMethod.getStatusLine());
		Log.d("CPConnection.doPost()", "TimeStamp:"+mhm_PostMethod.getResponseHeader("TimeStamp"));
		Log.d("CPConnection.doPost()", "Encoding-Type:"+mhm_PostMethod.getResponseHeader("Encoding-Type"));
		Log.d("CPConnection.doPost()", "result-code:"+mhm_PostMethod.getResponseHeader("result-code"));
		Log.d("CPConnection.doPost()", "Content-Type:"+mhm_PostMethod.getResponseHeader("Content-Type"));
		Log.d("CPConnection.doPost()", "Content-Length:"+mhm_PostMethod.getResponseHeader("Content-Length"));
		Log.d("CPConnection.doPost()", "Set-Cookie:"+mhm_PostMethod.getResponseHeader("Set-Cookie"));
		Log.d("CPConnection.doPost()", "APIVersion:"+mhm_PostMethod.getResponseHeader("APIVersion"));
		Log.d("CPConnection.doPost()", "Cache-Control:"+mhm_PostMethod.getResponseHeader("Cache-Control"));
		Log.d("CPConnection.doPost()", "ResponseBody:"+new String(responseBody,Config.getString("UTFEncode"));
		*/
	    mhm_PostMethod.releaseConnection();//�ر����� 
	    
	    //Log.d("CPConnection.doPost()", "Get out doPost Method");
	    
	    //����
		return retHashMap;
		
	}

	/**
	 * ��GET��ʽ�����ƶ�����
	 * @param ahm_HeaderMap HTTPͷ����
	 * @param ahm_NamePair  HTTP�������
	 * @return	��������Ϣ 
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public HashMap doGet(HashMap ahmHeaderMap, HashMap ahmNamePair) throws IOException,HttpException {
		
		//Log.d("CPConnection.doGet()", "Get into doGet Method");
		
		HashMap retHashMap = new HashMap(); //���ؽ����ϢMap 
		
		//����GetMethod����
		GetMethod mhm_GetMethod = new GetMethod(ms_CPURL);
		//System.out.println(ms_CPURL);
		
		//��ahmHeaderMap�Ĳ�����Ϣ���ε������õ�mhm_PostMethod Head
		if(ahmHeaderMap != null) {
			Iterator ahmHeaderMapI = ahmHeaderMap.keySet().iterator();
			while(ahmHeaderMapI.hasNext()) {
				Object o = ahmHeaderMapI.next();
				String key = o.toString();
				String value = (String)ahmHeaderMap.get(key);
				mhm_GetMethod.setRequestHeader(key, value);
			}
		}
		
		
		//��ahmNamePair�Ĳ�����Ϣ���õ�mhm_PostMethod Body
		List nameValuePairList = new ArrayList();
		if(ahmNamePair != null) {
			Iterator ahmNamePairI = ahmNamePair.keySet().iterator();
			while(ahmNamePairI.hasNext()) {
				Object o = ahmNamePairI.next();
				String key = o.toString();
				String value = (String)ahmNamePair.get(key);
				NameValuePair nameValuePair = new NameValuePair(key,value);
				nameValuePairList.add(nameValuePair);
			}
		}
		NameValuePair[] nameValuePairs = new NameValuePair[nameValuePairList.size()];
		for(int i=0;i<nameValuePairList.size();i++) {
			nameValuePairs[i] = (NameValuePair)nameValuePairList.get(i);
		}
		mhm_GetMethod.setQueryString(nameValuePairs);
		
		HttpClient hClient = new HttpClient();//HttpClient���� 
		try {
			if(sdcard.equals("SIM")){
				hClient.getHostConfiguration().setProxy(Config.getString("proxyIP"), Integer.parseInt(Config.getString("port")));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		//System.out.println("=====TIME_OUT======"+Config.getInt("TIME_OUT"));
		HttpConnectionManagerParams managerParams = hClient 
	    .getHttpConnectionManager().getParams(); 
			// �������ӳ�ʱʱ��(��λ����) 
			managerParams.setConnectionTimeout(15000); 
			// ���ö����ݳ�ʱʱ��(��λ����) 
			managerParams.setSoTimeout(15000);
		
		long startTimes = System.currentTimeMillis();
		Log.i("CPConnection.doGet() start times", String.valueOf(startTimes));
		//hClient.setConnectionTimeout(5000);
		hClient.executeMethod(mhm_GetMethod);//����
		long endTimes = System.currentTimeMillis();
		Log.i("CPConnection.doGet() end times", String.valueOf(endTimes));
		Log.i("CPConnection.doGet() lost times(ms)", String.valueOf(endTimes - startTimes));
		
		//��װ����Response Header������Response Body, ���뷵��HashMap������
		retHashMap.put("Status", mhm_GetMethod.getStatusLine()==null?"":mhm_GetMethod.getStatusLine().toString());
		retHashMap.put("TimeStamp", mhm_GetMethod.getResponseHeader("TimeStamp")==null?"":mhm_GetMethod.getResponseHeader("TimeStamp").toString());
		retHashMap.put("Encoding-Type", mhm_GetMethod.getResponseHeader("Encoding-Type")==null?"":mhm_GetMethod.getResponseHeader("Encoding-Type").toString());
		retHashMap.put("result-code", mhm_GetMethod.getResponseHeader("result-code")==null?"":mhm_GetMethod.getResponseHeader("result-code").toString());
		retHashMap.put("Content-Type", mhm_GetMethod.getResponseHeader("Content-Type")==null?"":mhm_GetMethod.getResponseHeader("Content-Type").toString());
		retHashMap.put("Content-Length", mhm_GetMethod.getResponseHeader("Content-Length")==null?"":mhm_GetMethod.getResponseHeader("Content-Length").toString());
		retHashMap.put("Set-Cookie", mhm_GetMethod.getResponseHeader("Set-Cookie")==null?"":mhm_GetMethod.getResponseHeader("Set-Cookie").toString());
		retHashMap.put("APIVersion", mhm_GetMethod.getResponseHeader("APIVersion")==null?"":mhm_GetMethod.getResponseHeader("APIVersion").toString());
		retHashMap.put("Cache-Control", mhm_GetMethod.getResponseHeader("Cache-Control")==null?"":mhm_GetMethod.getResponseHeader("Cache-Control").toString());
		Header h1 = mhm_GetMethod.getResponseHeader("RegCode");
		if(h1 != null){
			retHashMap.put("RegCode", h1.getValue());
		}
		retHashMap.put("RspDigest", mhm_GetMethod.getResponseHeader("RspDigest"));
		
		byte[] responseBody =  mhm_GetMethod.getResponseBody();
		retHashMap.put(Constants.ms_ResponseBody, responseBody);
		/*
		Log.d("CPConnection.doGet()", "Return Status:"+mhm_GetMethod.getStatusLine());
		Log.d("CPConnection.doGet()", "TimeStamp:"+mhm_GetMethod.getResponseHeader("TimeStamp"));
		Log.d("CPConnection.doGet()", "Encoding-Type:"+mhm_GetMethod.getResponseHeader("Encoding-Type"));
		Log.d("CPConnection.doGet()", "result-code:"+mhm_GetMethod.getResponseHeader("result-code"));
		Log.d("CPConnection.doGet()", "Content-Type:"+mhm_GetMethod.getResponseHeader("Content-Type"));
		Log.d("CPConnection.doGet()", "Content-Length:"+mhm_GetMethod.getResponseHeader("Content-Length"));
		Log.d("CPConnection.doGet()", "Set-Cookie:"+mhm_GetMethod.getResponseHeader("Set-Cookie"));
		Log.d("CPConnection.doGet()", "APIVersion:"+mhm_GetMethod.getResponseHeader("APIVersion"));
		Log.d("CPConnection.doGet()", "Cache-Control:"+mhm_GetMethod.getResponseHeader("Cache-Control"));
		Log.d("CPConnection.doGet()", "ResponseBody:"+new String(responseBody,Constants.ms_UTFEncode));
		*/
		mhm_GetMethod.releaseConnection();//�ر����� 
	    
	    //Log.d("CPConnection.doGet()", "Get out doGet Method");
		
		    
	    //����
		return retHashMap;
	}
	
	/**
	 * ��POST��ʽ�����ƶ�����(�������)
	 * @author rd038
	 * @param ahmHeaderMap ����ͷ
	 * @param action �������ÿ�����ӵĽӿ�����
	 * @param ahmNamePairList �������ÿ�����ӵĽӿڲ���
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 * @since 2010-10-25~2010-10-26
	 */
	public List<HashMap> doPost(HashMap ahmHeaderMap,List<String> action, List<HashMap> ahmNamePairList) throws HttpException, IOException {
	
		//Log.d("CPConnection.doPost()", "Get into doPost Method");
		List retList = new ArrayList();
		 
		//����PostMethod����
		PostMethod mhm_PostMethod = new PostMethod(ms_CPURL);
		System.out.println(ms_CPURL);
		
		//��ahmHeaderMap�Ĳ�����Ϣ���ε������õ�mhm_PostMethod Head
		if(ahmHeaderMap != null) {
			Iterator ahmHeaderMapI = ahmHeaderMap.keySet().iterator();
			while(ahmHeaderMapI.hasNext()) {
				Object o = ahmHeaderMapI.next();
				String key = o.toString();
				String value = (String)ahmHeaderMap.get(key);
				mhm_PostMethod.setRequestHeader(key, value);
			}
		}
		
		HttpClient hClient = new HttpClient();//HttpClient���� 
		try {
			if(sdcard.equals("SIM")){
				hClient.getHostConfiguration().setProxy(Config.getString("proxyIP"), Integer.parseInt(Config.getString("port")));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		HttpConnectionManagerParams managerParams = hClient 
	    .getHttpConnectionManager().getParams(); 
			// �������ӳ�ʱʱ��(��λ����) 
			managerParams.setConnectionTimeout(Config.getInt("TIME_OUT")); 
			// ���ö����ݳ�ʱʱ��(��λ����) 
			managerParams.setSoTimeout(Config.getInt("TIME_OUT"));
		//��ahmNamePair�Ĳ�����Ϣ���õ�mhm_PostMethod Body
		for(int i=0;i<ahmNamePairList.size();i++){
			mhm_PostMethod.setRequestHeader("Action", action.get(i));
			List nameValuePairList = new ArrayList();
			HashMap ahmNamePair = ahmNamePairList.get(i);
			if(ahmNamePair != null) {
				Iterator ahmNamePairI = ahmNamePair.keySet().iterator();
				while(ahmNamePairI.hasNext()) {
					Object o = ahmNamePairI.next();
					String key = o.toString();
					if(Constants.ms_PostReqParameter.equals(key)){
						continue;
					}
					String value = (String)ahmNamePair.get(key);
					NameValuePair nameValuePair = new NameValuePair(key,value);
					nameValuePairList.add(nameValuePair);
				}
			}
			NameValuePair[] nameValuePairs = new NameValuePair[nameValuePairList.size()];
			for(int j=0;j<nameValuePairList.size();j++) {
				nameValuePairs[j] = (NameValuePair)nameValuePairList.get(j);
			}
			mhm_PostMethod.setQueryString(nameValuePairs);
			
			long startTimes = System.currentTimeMillis();
			Log.i("CPConnection.doPost() start times" , String.valueOf(startTimes));
			hClient.executeMethod(mhm_PostMethod);//����
			long endTimes = System.currentTimeMillis();
			Log.i("CPConnection.doPost() end times" , String.valueOf(endTimes));
			Log.i("CPConnection.doPost() lost times(ms)" , String.valueOf(endTimes - startTimes));
			
			HashMap retHashMap = new HashMap(); //���ؽ����ϢMap
			byte[] responseBody =  mhm_PostMethod.getResponseBody();
			//System.out.println("ttttttttttttttt"+new String(responseBody));
			retHashMap.put(Constants.ms_ResponseBody, responseBody);
			//��װ����Response Header������Response Body, ���뷵��HashMap������
			retHashMap.put("Status", mhm_PostMethod.getStatusLine());
			retHashMap.put("TimeStamp", mhm_PostMethod.getResponseHeader("TimeStamp"));
			retHashMap.put("Encoding-Type", mhm_PostMethod.getResponseHeader("Encoding-Type"));
			retHashMap.put("result-code", mhm_PostMethod.getResponseHeader("result-code"));
			retHashMap.put("Content-Type", mhm_PostMethod.getResponseHeader("Content-Type"));
			retHashMap.put("Content-Length", mhm_PostMethod.getResponseHeader("Content-Length"));
			retHashMap.put("Set-Cookie", mhm_PostMethod.getResponseHeader("Set-Cookie"));
			retHashMap.put("APIVersion", mhm_PostMethod.getResponseHeader("APIVersion"));
			retHashMap.put("Cache-Control", mhm_PostMethod.getResponseHeader("Cache-Control"));
			retList.add(retHashMap);
		}
	
	    mhm_PostMethod.releaseConnection();//�ر����� 
	    
	    //Log.d("CPConnection.doPost()", "Get out doPost Method");
	    
	    //����
		return retList;
		
	}
	
	/**
	 * ��Get��ʽ�����ƶ�����(�������)
	 * @author rd038
	 * @param ahmHeaderMap ����ͷ
	 * @param action �������ÿ�����ӵĽӿ�����
	 * @param ahmNamePairList �������ÿ�����ӵĽӿڲ���
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 * @since 2010-10-25~2010-10-26
	 */
	public List<HashMap> doGet(HashMap ahmHeaderMap,List<String> action, List<HashMap> ahmNamePairList) throws HttpException, IOException {
	
		//Log.d("CPConnection.doPost()", "Get into doPost Method");
		List retList = new ArrayList();
		 
		//����PostMethod����
		GetMethod mhm_GetMethod = new GetMethod(ms_CPURL);
		System.out.println(ms_CPURL);
		
		//��ahmHeaderMap�Ĳ�����Ϣ���ε������õ�mhm_PostMethod Head
		if(ahmHeaderMap != null) {
			Iterator ahmHeaderMapI = ahmHeaderMap.keySet().iterator();
			while(ahmHeaderMapI.hasNext()) {
				Object o = ahmHeaderMapI.next();
				String key = o.toString();
				String value = (String)ahmHeaderMap.get(key);
				mhm_GetMethod.setRequestHeader(key, value);
			}
		}
		
		HttpClient hClient = new HttpClient();//HttpClient���� 
		try {
			if(sdcard.toLowerCase().equals("SIM")){
				hClient.getHostConfiguration().setProxy(Config.getString("proxyIP"), Integer.parseInt(Config.getString("port")));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		HttpConnectionManagerParams managerParams = hClient 
	    .getHttpConnectionManager().getParams(); 
			// �������ӳ�ʱʱ��(��λ����) 
			managerParams.setConnectionTimeout(Config.getInt("TIME_OUT")); 
			// ���ö����ݳ�ʱʱ��(��λ����) 
			managerParams.setSoTimeout(Config.getInt("TIME_OUT"));
		//��ahmNamePair�Ĳ�����Ϣ���õ�mhm_PostMethod Body
		for(int i=0;i<ahmNamePairList.size();i++){
			mhm_GetMethod.setRequestHeader("Action", action.get(i));
			List nameValuePairList = new ArrayList();
			HashMap ahmNamePair = ahmNamePairList.get(i);
			if(ahmNamePair != null) {
				Iterator ahmNamePairI = ahmNamePair.keySet().iterator();
				while(ahmNamePairI.hasNext()) {
					Object o = ahmNamePairI.next();
					String key = o.toString();
					if(Constants.ms_PostReqParameter.equals(key)){
						continue;
					}
					String value = (String)ahmNamePair.get(key);
					NameValuePair nameValuePair = new NameValuePair(key,value);
					nameValuePairList.add(nameValuePair);
				}
			}
			NameValuePair[] nameValuePairs = new NameValuePair[nameValuePairList.size()];
			for(int j=0;j<nameValuePairList.size();j++) {
				nameValuePairs[j] = (NameValuePair)nameValuePairList.get(j);
			}
			mhm_GetMethod.setQueryString(nameValuePairs);
			
			long startTimes = System.currentTimeMillis();
			Log.i("CPConnection.doPost() start times" , String.valueOf(startTimes));
			hClient.executeMethod(mhm_GetMethod);//����
			long endTimes = System.currentTimeMillis();
			Log.i("CPConnection.doPost() end times" , String.valueOf(endTimes));
			Log.i("CPConnection.doPost() lost times(ms)" , String.valueOf(endTimes - startTimes));
			
			HashMap retHashMap = new HashMap(); //���ؽ����ϢMap
			byte[] responseBody =  mhm_GetMethod.getResponseBody();
			//System.out.println("ttttttttttttttt"+new String(responseBody));
			retHashMap.put(Constants.ms_ResponseBody, responseBody);
			//��װ����Response Header������Response Body, ���뷵��HashMap������
			retHashMap.put("Status", mhm_GetMethod.getStatusLine());
			retHashMap.put("TimeStamp", mhm_GetMethod.getResponseHeader("TimeStamp"));
			retHashMap.put("Encoding-Type", mhm_GetMethod.getResponseHeader("Encoding-Type"));
			retHashMap.put("result-code", mhm_GetMethod.getResponseHeader("result-code"));
			retHashMap.put("Content-Type", mhm_GetMethod.getResponseHeader("Content-Type"));
			retHashMap.put("Content-Length", mhm_GetMethod.getResponseHeader("Content-Length"));
			retHashMap.put("Set-Cookie", mhm_GetMethod.getResponseHeader("Set-Cookie"));
			retHashMap.put("APIVersion", mhm_GetMethod.getResponseHeader("APIVersion"));
			retHashMap.put("Cache-Control", mhm_GetMethod.getResponseHeader("Cache-Control"));
			retList.add(retHashMap);
		}
	
		
	    mhm_GetMethod.releaseConnection();//�ر����� 
	    
	     //����
		return retList;
		
	}

}
