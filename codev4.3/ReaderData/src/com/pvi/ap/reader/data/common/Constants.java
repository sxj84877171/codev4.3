package com.pvi.ap.reader.data.common;

import java.io.File;


import android.os.Environment;

/**
 * �����࣬���Ա���ϵͳ����
 * @author rd038
 *
 */
public class Constants {
	
	/**
	 * POST����XML���������ʾ
	 */
	public static final String ms_PostReqParameter = "XMLBody";
	
	/**
	 * UTF-8�ı�ʾ��ʽ
	 */
	public static final String ms_UTFEncode  = Config.getString("UTFEncode");
	
	/**
	 * �ı���ʽ
	 */
	public static final String ms_TextType = Config.getString("TextType");

	/**
	 * ��Ӧ������
	 */
	public static final String ms_ResponseBody = "ResponseBody";
	
	/**
	 * XML�ļ�ͷ��Ϣ
	 */
	public static final String ms_XMLHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
	
	/**
	 * DRM�����ַ
	 */
	public static final String ms_DRM_URL_ACTIVE = Config.getString("DRM_URL_ACTIVE");
	
	/**
	 * DRM����֤�����ӵ�ַ
	 */
	public static final String DRM_URL_DOWNLOADCERT = Config.getString("DRM_URL_DOWNLOADCERT");
	
    /**
     * ����ƽ̨ ����ַ
     */
    public static final String ms_CPC_BASE_URL = Config.getString("CPC_BASE_URL");  

    /**
     * ����ƽ̨API�ӿ�URL
     */
    public static final String ms_CPC_URL = Config.getString("CPC_BASE_URL")+"/handsetapi";
    
    /**
     * ����������ص�ַ
     */
    public static final String ms_SoftwareUpdate_URL = Config.getString("SoftwareUpdate_URL");  
    
    /**
     * �ͻ�����������
     */
    public static final String ms_ClientPWD = Config.getString("ClientPWD");  
    /**
     * sdcard��MyMusic�ļ���λ��
     */
    public static final String ms_mymusic = Config.getString("MYMUSIC");
    /**
     * sdcard��MyPic�ļ���λ��
     */
    public static final String ms_mypic = Config.getString("ms_mypic");
	/**
	 * sdcard��·��
	 */
	public final static String CON_SDCARD_PATH = "" + Environment.getExternalStorageDirectory();
	/**
	 * ����·��
	 */
	public final static String CON_DOWNLOAD_PATH = CON_SDCARD_PATH + File.separatorChar + "download"+ 
						File.separatorChar;
	/**
	 * MEB�ļ�����·��
	 */
	public static final String CON_MEB_PATH =CON_DOWNLOAD_PATH + "book" + File.separatorChar ;
	/**
	 * ֤������·��
	 */
	public static final String CON_CERT_PATH =CON_DOWNLOAD_PATH + "cert" + File.separatorChar ;
	/**
	 * ֤���׺
	 */
	public static final String CON_CERT_TYPE = ".cert";
	/**
	 * ���ļ����ر�־
	 */
	public static final String CON_DOWNLOAD_TAG = "0";
	/**
	 * �豸ID
	 */
    public static final String ms_DeviceID = Config.getString("DeviceId");
	/**
	 * �ҵ��ĵ���·��
	 */
    public static final String ms_MyDocPath = Config.getString("MyDocPath");
    
    /**
	 * �ҵ��ĵ�
	 */
    public static final String ms_CertPath = Config.getString("ms_CertPath");
    
    /**
     * ��ҳXML�ļ��Ĺ̶���ַ
     */
    public static final String CON_FIRST_PAGE_LOCATION = Environment.getDataDirectory() + "/data/com.pvi.ap.reader/files/" ;
    
    /**
     * ��ҳXML�ļ��Ĺ̶��ļ���
     */
    public static final String CON_FIRST_PAGE_XML_FILE =Config.getString("CON_FIRST_PAGE_XML_FILE");
    
    public static final String configFileName = Config.getString("configFileName");
    
}
