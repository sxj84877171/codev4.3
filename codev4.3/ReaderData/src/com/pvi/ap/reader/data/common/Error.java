package com.pvi.ap.reader.data.common;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import android.os.Environment;

/**
 * 读取配置文件类
 * @author 彭见宝
 *
 */
public class Error {
	
	public static final String TAG = "Config";
	
	public static final String configFileName = Environment.getDataDirectory() + "/data/com.pvi.ap.reader/files/"+"error_zh_cn_net.properties";
	public static final String configFileNameUser = Environment.getDataDirectory() + "/data/com.pvi.ap.reader/files/"+"error_zh_cn_user.properties";

	public static Properties properties = new Properties();
	public static Properties properties_user = new Properties();
	
	public static String pixReplace = "[^0-9]";
	
	static {
		
		 
		try {
			FileInputStream fin = new FileInputStream(configFileName);
			FileInputStream fin_user = new FileInputStream(configFileNameUser);
			 properties.load(fin);
			 properties_user.load(fin_user);
		} catch (FileNotFoundException e) {
			Logger.i(TAG, e.toString());
		} catch (IOException e) {
			Logger.i(TAG, e.toString());
		}
		
	}
	
	public static void init(){
		try {
			FileInputStream fin = new FileInputStream(configFileName);
			 properties.load(fin);
			 FileInputStream fin_user = new FileInputStream(configFileNameUser);
			 properties_user.load(fin_user);
		} catch (FileNotFoundException e) {
			Logger.i(TAG, e.toString());
		} catch (IOException e) {
			Logger.i(TAG, e.toString());
		}
	}
	
	
	public static String getErrorDescription(String filed){
		if(filed == null || "".equals(filed.trim())){
			return "" ;
		}
		if(filed.toLowerCase().contains("error.")){
			if(properties_user != null){
				String retStr = properties_user.getProperty(filed, "");
				String s1 = null;
	    		try {
					s1 = new String(retStr.getBytes("ISO-8859-1"),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return s1;
			}else{
				return "";
			}
		} else {
			if (properties != null) {
				String retStr = properties.getProperty(filed, "");
				String s1 = null;
				try {
					s1 = new String(retStr.getBytes("ISO-8859-1"), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return s1;
			} else {
				return "";
			}
		}
	}
	
	public static String getErrorDescription(String filed,String... pama1){
		if(filed == null || "".equals(filed.trim())){
			return "" ;
		}
		if (properties_user != null) {
			String retStr = properties_user.getProperty(filed, "");
			int index = 0 ;
			for(String tmp:pama1){
				retStr = retStr.replace("{" + index ++ + "}", tmp);
			}
			String s1 = null;
			try {
				s1 = new String(retStr.getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return s1;
		} else {
			return "";
		}
	}
	
	public static String getErrorDescriptionForContent(String filed){
		if(filed == null){
			return "获取网络返回码为空";
		}
		filed = filed.replaceAll("\\r\\n", "");
		if(properties!=null){
			filed = filed.replaceAll(pixReplace, "");
			String retStr = properties.getProperty(filed, "");
			String s1 = null;
    		try {
				s1 = new String(retStr.getBytes("ISO-8859-1"),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				Logger.e(TAG, e);
				return retStr ;
			}
			if("".equals(s1.trim())){
				s1 = "返回错误码："+filed;
			}
			return s1;
			
		}else{
			return "";
		}
	}

	/**
	 * @deprecated
	 * @param filed
	 * @param value
	 * @return
	 */
	public static boolean setString(String filed,String value){
		if(properties!=null){
			properties.put(filed,value);
			return true;
		}else{
			return false;
		}
	}

}
