package com.pvi.ap.reader.data.common;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import android.app.Activity;
import android.util.Log;

import com.pvi.ap.reader.data.external.manager.LeafNode;
import com.pvi.ap.reader.data.external.manager.XMLUtil;
import com.pvi.ap.reader.data.external.manager.XmlElement;

/**
 * ��־��ӡ��<br>
 * ��־����<br>
 * @author Elvis
 * @version 1.0.0
 * C)Copyright 2010-2013, by www.pvi.com.tw
 */
public final class Logger {
	 /**
     * ��������VERBOSE����ӡLogger.v
     */
    public static final int VERBOSE = 2;
    
    /**
     * ��������DEBUG����ӡLogger.d
     */
    public static final int DEBUG = 3;
    
    /**
     * ��������INFO����ӡLogger.i
     */
    public static final int INFO = 4;

    /**
     * ��������WARN����ӡLogger.w
     */
    public static final int WARN = 5;

    /**
     * ��������ERROR����ӡLogger.e
     */
    public static final int ERROR = 6;

    /**
     * ��������ASSERT
     */
    public static final int ASSERT = 7;
    
    /**
     * �Ƿ��ӡINFOVIEW�������
     */
    private static boolean INFOVIEW = true ;
    /**
     * �Ƿ��ӡVERBOSEVIEW�������
     */
    private static boolean VERBOSEVIEW = true;
    
    /**
     * �Ƿ��ӡDEBUGVIEW�������
     */
    private static boolean DEBUGVIEW = true;
    
    /**
     * �Ƿ��ӡWARNVIEW�������
     */
    private static boolean WARNVIEW = true;
    
    /**
     * �Ƿ��ӡERRORVIEW�������
     */
    private static boolean ERRORVIEW = true;

    /**
     * ��ӡ�Ƿ���ʾ�ں�̨<br>
     * 0:��ӡ�����̨<br>
     * 1:��ӡ����ļ�<br>
     */
    private static int writeToFile = 0 ;
    
    /**
     * �����ļ�����ӿ�
     * @param writeToFile
     * <br> 0 ��logcat���
     * <br> 1 ���ļ����
     */
	public static void setWriteToFile(int writeToFile) {
		Logger.writeToFile = writeToFile;
	}


	/**
	 * ����xml�ļ�·��
	 * @param logConfigPath
	 * <br>����·��
	 */
	public static void setLogConfigPath(String logConfigPath) {
		Logger.logConfigPath = logConfigPath;
	}

	/**
	 * ����ʱ�������ʽ
	 * @param formatter
	 * <br> ���ʱ���ʽ
	 */
	public static void setFormatter(SimpleDateFormat formatter) {
		Logger.formatter = formatter;
	}


	/**
	 * ����������ļ�������ָ��������κ��ļ�
	 * @param fw
	 * <br>�ļ������
	 */
	public static void setFw(FileWriter fw) {
		Logger.fw = fw;
	}

	/**
     * logconfig·��
     */
    private static String logConfigPath = Constants.CON_FIRST_PAGE_LOCATION + "log.xml" ;

    private Logger() {
    }
    
    private static Properties properties = new Properties();
    
    /**
     * logconfig.xml�ļ�Ҫ������/sdcard/�����棬�������ļ���ʽҪΪ<br>
     * <?xml version="1.0" encoding="utf-8"?>   <br>
     * <logconfig>  <br>
     * <verbose>true</verbose> <br>
     * <debug>true</debug> <br>
     * <info>true</info> <br>
     * <warn>true</warn> <br>
     * <error>true</error> <br>
     * </logconfig> <br>
     * <p><p>
     */
    public static void initLogConfig(Activity call){
    	FileInputStream fin = null ;
		try {
			fin = new FileInputStream(logConfigPath);
		} catch (Exception e) {
			properties.setProperty("verbose","" + VERBOSEVIEW);
			properties.setProperty("info","" + INFOVIEW);
			properties.setProperty("debug","" + DEBUGVIEW);
			properties.setProperty("warn","" + WARNVIEW);
			properties.setProperty("error","" + ERRORVIEW);
			return ;
		} 
		try {
			properties.load(fin);
		} catch (IOException e) {
			return ;
		}
		VERBOSEVIEW = Boolean.parseBoolean(properties.getProperty("verbose"));
		INFOVIEW = Boolean.parseBoolean(properties.getProperty("info"));
		DEBUGVIEW = Boolean.parseBoolean(properties.getProperty("debug"));
		WARNVIEW = Boolean.parseBoolean(properties.getProperty("warn"));
		ERRORVIEW = Boolean.parseBoolean(properties.getProperty("error"));
		if(fin != null){
			try {
				fin.close();
			} catch (IOException e) {
			}
		}
		if(true){
			return ;
		}
		
    	try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			File logFile = new File(logConfigPath) ;
			if(!logFile.exists()){
				i("logger","logconfig not exit");
				saveLogConfig();
				return ;
			}
			
			/**
			 * DOM����xml�ĵ�
			 */
			Document document = builder.parse(logFile);
			Element root = document.getDocumentElement();
			Node node =  null ;
			String value = null ;
			
			/**
			 * VERBOSE����
			 */
			node = root.getElementsByTagName("verbose").item(0);
			if(node != null){
				value = node.getFirstChild().getNodeValue();
				try {
					VERBOSEVIEW = Boolean.parseBoolean(value);
				} catch (Exception e) {
					i("logger","logconfig has error");
				}
			}
			
			/**
			 * INFO��ȡ
			 */
			node = root.getElementsByTagName("info").item(0);
			if(node != null){
				value = node.getFirstChild().getNodeValue();
				try {
					INFOVIEW = Boolean.parseBoolean(value);
				} catch (Exception e) {
					i("logger","logconfig has error");
				}
			}
			
			/**
			 * DEBUG�����ȡ
			 */
			node = root.getElementsByTagName("debug").item(0);
			if(node != null){
				value = node.getFirstChild().getNodeValue();
				try {
					DEBUGVIEW = Boolean.parseBoolean(value);
				} catch (Exception e) {
					i("logger","logconfig has error");
				}
			}
			/**
			 * WARN�����ȡ
			 */
			node = root.getElementsByTagName("warn").item(0);
			if(node != null){
				value = node.getFirstChild().getNodeValue();
				try {
					WARNVIEW = Boolean.parseBoolean(value);
				} catch (Exception e) {
					i("logger","logconfig has error");
				}
			}
			/**
			 * ERROR�����ȡ
			 */
			node = root.getElementsByTagName("error").item(0);
			if(node != null){
				value = node.getFirstChild().getNodeValue();
				try {
					ERRORVIEW = Boolean.parseBoolean(value);
				} catch (Exception e) {
					i("logger","logconfig has error");
				}
			}
			
		} catch (Exception e) {
			i("logger","logconfig has error");
		}
    }
    
    /**
     * ������־����
     */
    public static void saveLogConfig(){
    	LeafNode l0 = null ;
    	if(INFOVIEW){
    		l0 = new LeafNode("info","true");
    	}else{
    		l0 = new LeafNode("info","false");
    	}
    	LeafNode l1 = null ;
    	if(DEBUGVIEW){
    		l1 = new LeafNode("debug","true");
    	}else{
    		l1 = new LeafNode("debug","false");
    	}
    	LeafNode l2 = null ;
    	if(WARNVIEW){
    		l2 = new LeafNode("warn","true");
    	}else{
    		l2 = new LeafNode("warn","false");
    	}
    	LeafNode l3 = null ;
    	if(ERRORVIEW){
    		l3 = new LeafNode("error","true");
    	}else{
    		l3 = new LeafNode("error","false");
    	}
    	LeafNode l4 = null ;
    	if(VERBOSEVIEW){
    		l4 = new LeafNode("verbose","true");
    	}else{
    		l4 = new LeafNode("verbose","false");
    	}
    	List<LeafNode> listChild = new ArrayList<LeafNode>();
    	listChild.add(l0);
    	listChild.add(l1);
    	listChild.add(l2);
    	listChild.add(l3);
    	listChild.add(l4);
    	XmlElement xmlElement = new XmlElement("logconfig", listChild);
    	String logconfigXMLBody = "";
    	try {
			logconfigXMLBody = XMLUtil.getXmlStringFromXmlElement(xmlElement);
		} catch (ParserConfigurationException e) {
			e("Logger", e.toString());
		}
		File file = new File(logConfigPath);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e("logger", e.toString());
			}
		}
		OutputStream os = null ;
		
		try {
			os = new FileOutputStream(file);
			os.write(logconfigXMLBody.getBytes());
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			e("Logger",e.toString());
		} catch (IOException e) {
			e("Logger",e.toString());
		}
		
    }
    /**
     * I = 19 <br>
     * W = 19 <br>
     * D = 23 <br>
     * E = 20 <br>
     */

    /**
     * ���� {@link #VERBOSE} ��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int v(String tag, String msg) {
        return println(VERBOSE, tag, msg);
    }

    /**
     * ����{@link #VERBOSE} ��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     * @param tr �쳣��־��Ϣ
     */
    public static int v(String tag, String msg, Throwable tr) {
        return println(VERBOSE, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * ����{@link #DEBUG} ��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int d(String tag, String msg) {
        return println(DEBUG, tag, msg);
    }
    
    /**
     * ���� {@link #DEBUG} ��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     * @param tr �쳣��־��Ϣ
     */
    public static int d(String tag, String msg, Throwable tr) {
        return println(DEBUG, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * ���� {@link #INFO} ��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int i(String tag, String msg) {
        return println(INFO, tag, msg);
    }
    /**
     * ���� {@link #INFO} ��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int i(String tag, int msg) {
    	return i(tag, "" + msg);
    }
    
    /**
     * ���� {@link #INFO} ��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int i(String tag, float msg) {
    	return i(tag, "" + msg);
    }
    
    /**
     * ���� {@link #INFO} ��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int i(String tag, double msg) {
    	return i(tag, "" + msg);
    }
    
    /**
     * ���� {@link #INFO} ��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int i(String tag, Object msg) {
    	String message = msg.toString();
    	if(msg instanceof Throwable){
    		message = getStackTraceString((Throwable)msg);
    	}else{
    		message = String.valueOf(msg);
    	}
    	return i(tag, message);
    }
    
    /**
     * ���� {@link #INFO} ��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int i(String tag,long msg){
    	return i(tag,"" + msg);
    }
    
    /**
     * ���� {@link #INFO} ��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int i(String tag,byte msg){
    	return i(tag,"" + msg);
    }
    
    /**
     * ���� {@link #INFO} ��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int e(String tag, int msg) {
    	return e(tag, "" + msg);
    }
    
    
    /**
     * ���� {@link #INFO} ��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int e(String tag, float msg) {
    	return e(tag, "" + msg);
    }
    
    /**
     * ���� {@link #INFO} ��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int e(String tag, double msg) {
    	return e(tag, "" + msg);
    }
    
    /**
     * ���� {@link #ERROR} ��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int e(String tag, Object msg) {
    	String message = msg.toString();
    	if(msg instanceof Throwable){
    		message = getStackTraceString((Throwable)msg);
    	}else{
    		message = String.valueOf(msg);
    	}
    	return e(tag, message);
    }
    
    /**
     * ���� {@link #ERROR} ��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int e(String tag,long msg){
    	return e(tag,"" + msg);
    }
    
    /**
     * ���� {@link #ERROR} ��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int e(String tag,byte msg){
    	return i(tag,"" + msg);
    }
    
    /**
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int w(String tag, int msg) {
    	return w(tag, "" + msg);
    }
    
    /**
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int w(String tag, float msg) {
    	return w(tag, "" + msg);
    }
    
    /**
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int w(String tag, double msg) {
    	return w(tag, "" + msg);
    }
    
    /**
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int w(String tag, Object msg) {
    	String message = msg.toString();
    	if(msg instanceof Throwable){
    		message = getStackTraceString((Throwable)msg);
    	}else{
    		message = String.valueOf(msg);
    	}
    	return w(tag, message);
    }
    
    /**
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int w(String tag,long msg){
    	return w(tag,"" + msg);
    }
    
    /**
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int w(String tag,byte msg){
    	return w(tag,"" + msg);
    }
    
    /**
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int v(String tag, int msg) {
    	return v(tag, "" + msg);
    }
    
    /**
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int v(String tag, float msg) {
    	return v(tag, "" + msg);
    }
    
    /**
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int v(String tag, double msg) {
    	return v(tag, "" + msg);
    }
    
    /**
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int v(String tag, Object msg) {
    	String message = msg.toString();
    	if(msg instanceof Throwable){
    		message = getStackTraceString((Throwable)msg);
    	}else{
    		message = String.valueOf(msg);
    	}
    	return v(tag, message);
    }
    
    /**
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int v(String tag,long msg){
    	return v(tag,"" + msg);
    }
    
    /**
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int v(String tag,byte msg){
    	return v(tag,"" + msg);
    }
    
    /**
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int d(String tag, int msg) {
    	return d(tag, "" + msg);
    }
    
    /**
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int d(String tag, float msg) {
    	return d(tag, "" + msg);
    }
    
    /**
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int d(String tag, double msg) {
    	return d(tag, "" + msg);
    }
    
    /**
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int d(String tag, Object msg) {
    	String message = msg.toString();
    	if(msg instanceof Throwable){
    		message = getStackTraceString((Throwable)msg);
    	}else{
    		message = String.valueOf(msg);
    	}
    	return d(tag, message);
    }
    
    /**
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int d(String tag,long msg){
    	return d(tag,"" + msg);
    }
    
    /**
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int d(String tag,byte msg){
    	return d(tag,"" + msg);
    }
     /**
     * ���� {@link #INFO} �쳣��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     * @param tr An exception to Logger
     */
    public static int i(String tag, String msg, Throwable tr) {
        return println(INFO, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * ���� {@link #WARN}��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     */
    public static int w(String tag, String msg) {
        return println(WARN, tag, msg);
    }
    
    /**
     * ���� {@link #WARN} �쳣��־��Ϣ.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     * @param tr �쳣��Ϣ
     */
    public static int w(String tag, String msg, Throwable tr) {
        return println(WARN, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * ��ѯ�ñ�ǩ�Ƿ���ָ����μ������־��Ϣ
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     * @return �ǻ���
     * @throws ����ǩtag>23ʱ�������׳��쳣
     */
    public static boolean isLoggable(String tag, int level){
    	return Log.isLoggable(tag, level);
    }
        
    /*
     * ����{@link #WARN} �쳣��־��Ϣ
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param tr ������־��Ϣ
     */
    public static int w(String tag, Throwable tr) {
        return println(WARN, tag, getStackTraceString(tr));
    }

    /**
     * Send an {@link #ERROR} Logger message.
     * @param tag Used to identify the source of a Logger message.  It usually identifies
     *        the class or activity where the Logger call occurs.
     * @param msg The message you would like Loggerged.
     */
    public static int e(String tag, String msg) {
        return println(ERROR, tag, msg);
    }

    /**
     * Send a {@link #ERROR} Logger message and Logger the exception.
     * @param tag ����������־��Ϣ�ı�־�����鶨��ʹ��class����
     * @param msg ��־����Ϣ�壬��ϸ��Ϣ
     * @param tr �쳣��Ϣ
     */
    public static int e(String tag, String msg, Throwable tr) {
        return e(tag, msg + "\n"+ getStackTraceString(tr));
    }

    /**
     * �õ��쳣����Ϣ
     * @param tr �쳣��־��Ϣ
     */
    public static String getStackTraceString(Throwable tr) {
    	if (tr == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * �����־��Ϣ
     * @param priority ��־��Ϣ����
     * @param tag ��־��Ϣ��ǩ
     * @param msg ��־����ϸ��Ϣ
     * @return ����д���ֽ���.
     */
    public static int println(int priority, String tag, String msg){
    	if(changeSet){
    		msg = tag + "" + msg ;
    		tag = "Reader" ;
    	}
    	if(isPrintln(priority)){
    		if(writeToFile == 0){
    			if(msg == null || "".equals(msg.trim())){
    				try{
    					throw new java.lang.NullPointerException();
    				}catch(Exception e){
    					msg ="NullPointerException";
    				}
    			}
    			return Log.println(priority, tag, msg);
    		}else{
    			try {
    				if(fw == null){
    					fw = new FileWriter("/sdcard/MyDoc/logfile.txt",true) ;
    				}
					Date CurTime = new Date(System.currentTimeMillis());
					String now = formatter.format(CurTime);
					fw.write(now);
					fw.write("\t");
					fw.write(leverA);
					fw.write("\t");
					fw.write(tag);
					fw.write("\t");
					fw.write(msg);
					fw.write("\r\n");
					fw.flush();
				} catch (IOException e) {
//					Log.e("Logger",e.toString());
				}
    			return 0 ;
    		}
    	}else{
    		return 0;
    	}
    }

    /**
     * ��־�����ʽ
     */
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
    /**
     * �����ӡ��־
     */
    private static String leverA = "E" ;
    /**
     * �����
     */
    private static FileWriter fw = null;
    /**
     * �ر��ļ������
     */
    public static void closeFileWriter(){
    	if(fw != null){
    		try {
				fw.close();
			} catch (IOException e) {
			}
    		fw = null ;
    	}
    }
    /**
     * �Ƿ��ӡ�ü������־
     * @param priority
     * @return �Ƿ��ӡ
     */
    private  static boolean isPrintln(int priority){
    	switch(priority){
	    	case VERBOSE: 
	    		leverA = "V" ;
	    		return VERBOSEVIEW ;
	    	case WARN: 
	    		leverA = "W" ;
	    		return WARNVIEW ;
	    	case INFO: 
	    		leverA = "I" ;
	    		return INFOVIEW ;
	    	case DEBUG: 
	    		leverA = "D" ;
	    		return DEBUGVIEW ;
	    	case ERROR: 
	    		leverA = "E" ;
	    		return ERRORVIEW ;
	    	default:
	    		return false ;
    	}
    }
    
    /** 
     * �ر���־���ر�������־��������
     */
    public static void closeLogger(){
    	closeErrorLogger(true);
    }
    
    /**
     * ������־������������־�������
     */
    public static void openLogger(){
    	openVerboseLogger(true);
    }
    
    /**
     * ����Error������־
     * @param upTo�Ƿ��������ο���
     */
    public static void openErrorLogger(boolean upTo){
    	ERRORVIEW = true ;
    }
    /**
     * ����Error������־
     */
    public static void openErrorLogger(){
    	openErrorLogger(false);
    }
    
    /**
     * �ر�Error������־
     * @param downTo�Ƿ��������ιر�
     */
    public static void closeErrorLogger(boolean downTo){
    	ERRORVIEW = false ;
    	if(downTo){
    		closeWarmLogger(downTo);
    	}
    }
    /**
     * �ر�Error������־
     */
    public static void closeErrorLogger(){
    	closeErrorLogger(false);
    }
    
    /**
     * ����warn��־���
     * @param upTo�Ƿ��������ο���
     */
    public static void openWarmLogger(boolean upTo){
    	WARNVIEW = true ;
    	if(upTo){
    		openErrorLogger(upTo);
    	}
    }
    /**
     * ����warn��־���
     */
    public static void openWarmLogger(){
    	openWarmLogger(false);
    }
    
    /**
     * �ر�warn��־���
     * @param downTo�Ƿ��������ιر�
     */
    public static void closeWarmLogger(boolean downTo){
    	WARNVIEW = false ;
    	if(downTo){
    		closeInfoLogger(downTo);
    	}
    }
    /**
     * �ر�warn��־���
     */
    public static void closeWarmLogger(){
    	closeWarmLogger(false);
    }
    
    /**
     * ����INFO�������Ϣ�����
     * @param upTo�Ƿ��������ο���
     */
    public static void openInfoLogger(boolean upTo){
    	INFOVIEW = true ;
    	if(upTo){
    		openWarmLogger(upTo);
    	}
    }
    /**
     * ����INFO�������Ϣ�����
     */
    public static void openInfoLogger(){
    	openInfoLogger(false);
    }
    
    /**
     * �ر�INFO�������Ϣ�����
     * @param downTo�Ƿ��������ιر�
     */
    public static void closeInfoLogger(boolean downTo){
    	INFOVIEW = false ;
    	if(downTo){
    		closeDebugLogger(downTo);
    	}
    }
    /**
     * �ر�INFO�������Ϣ�����
     */
    public static void closeInfoLogger(){
    	closeInfoLogger(false);
    }
    
    /**
     * ����DEBUG������Ϣ�����
     * @param upTo�Ƿ��������ο���
     */
    public static void openDebugLogger(boolean upTo){
    	DEBUGVIEW = true ;
    	if(upTo){
    		openInfoLogger(upTo);
    	}
    }
    /**
     * ����DEBUG������Ϣ�����
     */
    public static void openDebugLogger(){
    	openDebugLogger(false);
    }
    
    /**
     * �ر�DEBUG������Ϣ�����
     * @param downTo�Ƿ��������ιر�
     */
    public static void closeDebugLogger(boolean downTo){
    	DEBUGVIEW = false ;
    	if(downTo){
    		closeVerboseLogger(downTo);
    	}
    }
    /**
     * �ر�DEBUG������Ϣ�����
     */
    public static void closeDebugLogger(){
    	closeDebugLogger(false);
    }
    
    /**
     * ����VERBOSE�������Ϣ���
     * @param upTo�Ƿ��������ο���
     */
    public static void openVerboseLogger(boolean upTo){
    	VERBOSEVIEW = true ;
    	if(upTo){
    		openDebugLogger(upTo);
    	}
    }
    /**
     * ����VERBOSE�������Ϣ���
     */
    public static void openVerboseLogger(){
    	openVerboseLogger(false);
    }
    
    /**
     * �ر�VERBOSE�������Ϣ���
     * @param downTo�Ƿ��������ιر�
     */
    public static void closeVerboseLogger(boolean downTo){
    	VERBOSEVIEW = false ;
    }
    /**
     * �ر�VERBOSE�������Ϣ���
     */
    public static void closeVerboseLogger(){
    	closeVerboseLogger(false);
    }
    /**
     * �ı����ã�ͳһ��ǩ��
     */
    private static boolean changeSet = false ;
    /**
     * ����
     */
    public static void setChangeSet(boolean changeSet){
    	Logger.changeSet = changeSet ;
    }
    
}
