package com.pvi.ap.reader.data.common;

import java.io.File;


import android.os.Environment;

/**
 * 常量类，用以保存系统常量
 * @author rd038
 *
 */
public class Constants {
	
	/**
	 * POST请求，XML请求参数表示
	 */
	public static final String ms_PostReqParameter = "XMLBody";
	
	/**
	 * UTF-8的表示方式
	 */
	public static final String ms_UTFEncode  = Config.getString("UTFEncode");
	
	/**
	 * 文本格式
	 */
	public static final String ms_TextType = Config.getString("TextType");

	/**
	 * 响应主参数
	 */
	public static final String ms_ResponseBody = "ResponseBody";
	
	/**
	 * XML文件头信息
	 */
	public static final String ms_XMLHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
	
	/**
	 * DRM激活地址
	 */
	public static final String ms_DRM_URL_ACTIVE = Config.getString("DRM_URL_ACTIVE");
	
	/**
	 * DRM下载证书连接地址
	 */
	public static final String DRM_URL_DOWNLOADCERT = Config.getString("DRM_URL_DOWNLOADCERT");
	
    /**
     * 内容平台 基地址
     */
    public static final String ms_CPC_BASE_URL = Config.getString("CPC_BASE_URL");  

    /**
     * 内容平台API接口URL
     */
    public static final String ms_CPC_URL = Config.getString("CPC_BASE_URL")+"/handsetapi";
    
    /**
     * 软件更新下载地址
     */
    public static final String ms_SoftwareUpdate_URL = Config.getString("SoftwareUpdate_URL");  
    
    /**
     * 客户端内置密码
     */
    public static final String ms_ClientPWD = Config.getString("ClientPWD");  
    /**
     * sdcard中MyMusic文件夹位置
     */
    public static final String ms_mymusic = Config.getString("MYMUSIC");
    /**
     * sdcard中MyPic文件夹位置
     */
    public static final String ms_mypic = Config.getString("ms_mypic");
	/**
	 * sdcard卡路径
	 */
	public final static String CON_SDCARD_PATH = "" + Environment.getExternalStorageDirectory();
	/**
	 * 下载路径
	 */
	public final static String CON_DOWNLOAD_PATH = CON_SDCARD_PATH + File.separatorChar + "download"+ 
						File.separatorChar;
	/**
	 * MEB文件下载路径
	 */
	public static final String CON_MEB_PATH =CON_DOWNLOAD_PATH + "book" + File.separatorChar ;
	/**
	 * 证书下载路径
	 */
	public static final String CON_CERT_PATH =CON_DOWNLOAD_PATH + "cert" + File.separatorChar ;
	/**
	 * 证书后缀
	 */
	public static final String CON_CERT_TYPE = ".cert";
	/**
	 * 该文件下载标志
	 */
	public static final String CON_DOWNLOAD_TAG = "0";
	/**
	 * 设备ID
	 */
    public static final String ms_DeviceID = Config.getString("DeviceId");
	/**
	 * 我的文档的路径
	 */
    public static final String ms_MyDocPath = Config.getString("MyDocPath");
    
    /**
	 * 我的文档
	 */
    public static final String ms_CertPath = Config.getString("ms_CertPath");
    
    /**
     * 首页XML文件的固定地址
     */
    public static final String CON_FIRST_PAGE_LOCATION = Environment.getDataDirectory() + "/data/com.pvi.ap.reader/files/" ;
    
    /**
     * 首页XML文件的固定文件名
     */
    public static final String CON_FIRST_PAGE_XML_FILE =Config.getString("CON_FIRST_PAGE_XML_FILE");
    
    public static final String configFileName = Config.getString("configFileName");
    
}
