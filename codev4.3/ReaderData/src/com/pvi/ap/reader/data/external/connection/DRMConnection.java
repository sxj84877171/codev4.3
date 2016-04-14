package com.pvi.ap.reader.data.external.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Constants;

import android.util.Log;


/**
 * DRM连接接口<br>
 * 该类分别实现以Post和Get连接DRM<br>
 * @author rd038
 * @since 2010-10-12
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class DRMConnection extends CMGWConnection {
	
    public static String simType = "" ;//SIM卡类型
    
	/**
	 * DRM连接地址
	 */
	private String ms_DRMURL = null;
	
	/**
	 * 构造函数
	 * @param ms_DRMURL
	 */
	public DRMConnection(String ms_DRMURL) {
		
		this.ms_DRMURL = ms_DRMURL;
		
	}


	/**
	 * 以POST方式连接DRM
	 * @param ahm_HeaderMap HTTP头参数
	 * @param ahm_NamePair  HTTP请求参数
	 * @return	请求结果信息 
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public HashMap doPost(HashMap ahmHeaderMap, HashMap ahmNamePair) throws HttpException, IOException {
		//Log.d("DRMConnection.doPost()", "Get into doPost Method");
		
		HashMap retHashMap = new HashMap(); //返回结果信息Map 
		
		//创建PostMethod对象
		PostMethod mhm_PostMethod = new PostMethod(ms_DRMURL);
		
		//将ahmHeaderMap的参数信息设置到mhm_PostMethod Head
		if(ahmHeaderMap != null) {
			Iterator ahmHeaderMapI = ahmHeaderMap.keySet().iterator();
			while(ahmHeaderMapI.hasNext()) {
				Object o = ahmHeaderMapI.next();
				String key = o.toString();
				String value = (String)ahmHeaderMap.get(key);
				mhm_PostMethod.setRequestHeader(key, value);
			}
		}
		
		
		//将ahmNamePair的参数信息设置到mhm_PostMethod Body
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
		mhm_PostMethod.setQueryString(nameValuePairs);
		
		HttpClient hClient = new HttpClient();//HttpClient对象 
        if("SIM".equalsIgnoreCase(simType)){
            hClient.getHostConfiguration().setProxy(Config.getString("proxyIP"), Integer.parseInt(Config.getString("port")));
        }
		
		long startTimes = System.currentTimeMillis();
		Log.i("DRMConnection.doPost() start times", String.valueOf(startTimes));
		
		hClient.executeMethod(mhm_PostMethod);//请求
	
		long endTimes = System.currentTimeMillis();
		Log.i("DRMConnection.doPost() end times", String.valueOf(endTimes));
		Log.i("DRMConnection.doPost() lost times(ms)", String.valueOf(endTimes - startTimes));
	    
		
		//封装返回Response Header参数和Response Body, 存入返回HashMap对象中
		retHashMap.put("Status", mhm_PostMethod.getStatusLine());
		retHashMap.put("TimeStamp", mhm_PostMethod.getResponseHeader("TimeStamp"));
		retHashMap.put("Encoding-Type", mhm_PostMethod.getResponseHeader("Encoding-Type"));
		retHashMap.put("result-code", mhm_PostMethod.getResponseHeader("result-code"));
		retHashMap.put("Content-Type", mhm_PostMethod.getResponseHeader("Content-Type"));
		retHashMap.put("Content-Length", mhm_PostMethod.getResponseHeader("Content-Length"));
		retHashMap.put("Set-Cookie", mhm_PostMethod.getResponseHeader("Set-Cookie"));
		retHashMap.put("APIVersion", mhm_PostMethod.getResponseHeader("APIVersion"));
		retHashMap.put("Cache-Control", mhm_PostMethod.getResponseHeader("Cache-Control"));
		retHashMap.put("RegCode", mhm_PostMethod.getResponseHeader("RegCode"));
		retHashMap.put("RspDigest", mhm_PostMethod.getResponseHeader("RspDigest"));
		byte[] responseBody =  mhm_PostMethod.getResponseBody();
		retHashMap.put(Constants.ms_ResponseBody, responseBody);
		/*
		Log.d("DRMConnection.doPost()", "Return Status:"+mhm_PostMethod.getStatusLine());
		Log.d("DRMConnection.doPost()", "TimeStamp:"+mhm_PostMethod.getResponseHeader("TimeStamp"));
		Log.d("DRMConnection.doPost()", "Encoding-Type:"+mhm_PostMethod.getResponseHeader("Encoding-Type"));
		Log.d("DRMConnection.doPost()", "result-code:"+mhm_PostMethod.getResponseHeader("result-code"));
		Log.d("DRMConnection.doPost()", "Content-Type:"+mhm_PostMethod.getResponseHeader("Content-Type"));
		Log.d("DRMConnection.doPost()", "Content-Length:"+mhm_PostMethod.getResponseHeader("Content-Length"));
		Log.d("DRMConnection.doPost()", "Set-Cookie:"+mhm_PostMethod.getResponseHeader("Set-Cookie"));
		Log.d("DRMConnection.doPost()", "APIVersion:"+mhm_PostMethod.getResponseHeader("APIVersion"));
		Log.d("DRMConnection.doPost()", "Cache-Control:"+mhm_PostMethod.getResponseHeader("Cache-Control"));
		Log.d("DRMConnection.doPost()", "RegCode:"+mhm_PostMethod.getResponseHeader("RegCode"));
		Log.d("DRMConnection.doPost()", "RspDigest:"+mhm_PostMethod.getResponseHeader("RspDigest"));
		Log.d("DRMConnection.doPost()", "ResponseBody:"+new String(responseBody,"UTF-8"));
	    */
	    
	    mhm_PostMethod.releaseConnection();//关闭连接 
	    
		//Log.d("DRMConnection.doPost()", "Get out doPost Method");
		//返回
		return retHashMap;
		
	}

	/**
	 * 以GET方式连接DRM 
	 * @param ahm_HeaderMap HTTP头参数
	 * @param ahm_NamePair  HTTP请求参数
	 * @return	请求结果信息 
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public HashMap doGet(HashMap ahmHeaderMap, HashMap ahmNamePair) throws IOException,HttpException {
		
		
		//Log.d("DRMConnection.doGet()", "Get into doGet Method");
		
		HashMap retHashMap = new HashMap(); //返回结果信息Map 
		
		//创建GetMethod对象
		//Log.d("DRMConnection.doGet", ms_DRMURL);
		GetMethod mhm_GetMethod = new GetMethod(ms_DRMURL);
		
		//将ahmHeaderMap的参数信息设置到mhm_PostMethod Head
		if(ahmHeaderMap != null) {
			Iterator ahmHeaderMapI = ahmHeaderMap.keySet().iterator();
			while(ahmHeaderMapI.hasNext()) {
				Object o = ahmHeaderMapI.next();
				String key = o.toString();
				String value = (String)ahmHeaderMap.get(key);
				//Log.d("DRMConnection.doGet", key+"->"+value);
				mhm_GetMethod.setRequestHeader(key, value);
			}
		}
		
		
		//将ahmNamePair的参数信息设置到mhm_PostMethod Body
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
		
		HttpClient hClient = new HttpClient();//HttpClient对象 
        if("SIM".equalsIgnoreCase(simType)){
            System.out.println("simType:"+simType+",user proxy ");
	        hClient.getHostConfiguration().setProxy(Config.getString("proxyIP"), Integer.parseInt(Config.getString("port")));
        }else{
            System.out.println("simType:"+simType);
        }
		
		long startTimes = System.currentTimeMillis();
		Log.i("DRMConnection.doGet() start times", String.valueOf(startTimes));
		
		hClient.executeMethod(mhm_GetMethod);//请求
	
		long endTimes = System.currentTimeMillis();
		Log.i("DRMConnection.doGet() end times", String.valueOf(endTimes));
		Log.i("DRMConnection.doGet() lost times(ms)", String.valueOf(endTimes - startTimes));
			 
		  
		
		//封装返回Response Header参数和Response Body, 存入返回HashMap对象中
		retHashMap.put("Status", mhm_GetMethod.getStatusLine());
		retHashMap.put("TimeStamp", mhm_GetMethod.getResponseHeader("TimeStamp"));
		retHashMap.put("Encoding-Type", mhm_GetMethod.getResponseHeader("Encoding-Type"));
		retHashMap.put("result-code", mhm_GetMethod.getResponseHeader("result-code"));
		retHashMap.put("Content-Type", mhm_GetMethod.getResponseHeader("Content-Type"));
		retHashMap.put("Content-Length", mhm_GetMethod.getResponseHeader("Content-Length"));
		retHashMap.put("Set-Cookie", mhm_GetMethod.getResponseHeader("Set-Cookie"));
		retHashMap.put("APIVersion", mhm_GetMethod.getResponseHeader("APIVersion"));
		retHashMap.put("Cache-Control", mhm_GetMethod.getResponseHeader("Cache-Control"));
		retHashMap.put("RegCode", mhm_GetMethod.getResponseHeader("RegCode"));
		retHashMap.put("RspDigest", mhm_GetMethod.getResponseHeader("RspDigest"));
		byte[] responseBody =  mhm_GetMethod.getResponseBody();
		retHashMap.put(Constants.ms_ResponseBody, responseBody);

/*		Log.d("DRMConnection.doGet()", "Return Status:"+mhm_GetMethod.getStatusLine());
		Log.d("DRMConnection.doGet()", "TimeStamp:"+mhm_GetMethod.getResponseHeader("TimeStamp"));
		Log.d("DRMConnection.doGet()", "Encoding-Type:"+mhm_GetMethod.getResponseHeader("Encoding-Type"));
		Log.d("DRMConnection.doGet()", "result-code:"+mhm_GetMethod.getResponseHeader("result-code"));
		Log.d("DRMConnection.doGet()", "Content-Type:"+mhm_GetMethod.getResponseHeader("Content-Type"));
		Log.d("DRMConnection.doGet()", "Content-Length:"+mhm_GetMethod.getResponseHeader("Content-Length"));
		Log.d("DRMConnection.doGet()", "Set-Cookie:"+mhm_GetMethod.getResponseHeader("Set-Cookie"));
		Log.d("DRMConnection.doGet()", "APIVersion:"+mhm_GetMethod.getResponseHeader("APIVersion"));
		Log.d("DRMConnection.doGet()", "Cache-Control:"+mhm_GetMethod.getResponseHeader("Cache-Control"));
		Log.d("DRMConnection.doGet()", "RegCode:"+mhm_GetMethod.getResponseHeader("RegCode"));
		Log.d("DRMConnection.doGet()", "RspDigest:"+mhm_GetMethod.getResponseHeader("RspDigest"));
		Log.d("DRMConnection.doGet()", "ResponseBody:"+new String(responseBody,"UTF-8"));*/

	    mhm_GetMethod.releaseConnection();//关闭连接 
	    
	    //Log.d("DRMConnection.doGet()", "Get out doGet Method");
		
	    
	    //返回
		return retHashMap;
	}

}
