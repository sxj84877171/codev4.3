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
 * 日志打印类<br>
 * 日志管理<br>
 * @author Elvis
 * @version 1.0.0
 * C)Copyright 2010-2013, by www.pvi.com.tw
 */
public final class Logger {
	 /**
     * 常量属性VERBOSE，打印Logger.v
     */
    public static final int VERBOSE = 2;
    
    /**
     * 常量属性DEBUG，打印Logger.d
     */
    public static final int DEBUG = 3;
    
    /**
     * 常量属性INFO，打印Logger.i
     */
    public static final int INFO = 4;

    /**
     * 常量属性WARN，打印Logger.w
     */
    public static final int WARN = 5;

    /**
     * 常量属性ERROR，打印Logger.e
     */
    public static final int ERROR = 6;

    /**
     * 常量属性ASSERT
     */
    public static final int ASSERT = 7;
    
    /**
     * 是否打印INFOVIEW级别变量
     */
    private static boolean INFOVIEW = true ;
    /**
     * 是否打印VERBOSEVIEW级别变量
     */
    private static boolean VERBOSEVIEW = true;
    
    /**
     * 是否打印DEBUGVIEW级别变量
     */
    private static boolean DEBUGVIEW = true;
    
    /**
     * 是否打印WARNVIEW级别变量
     */
    private static boolean WARNVIEW = true;
    
    /**
     * 是否打印ERRORVIEW级别变量
     */
    private static boolean ERRORVIEW = true;

    /**
     * 打印是否显示在后台<br>
     * 0:打印输入后台<br>
     * 1:打印输出文件<br>
     */
    private static int writeToFile = 0 ;
    
    /**
     * 设置文件输出接口
     * @param writeToFile
     * <br> 0 用logcat输出
     * <br> 1 用文件输出
     */
	public static void setWriteToFile(int writeToFile) {
		Logger.writeToFile = writeToFile;
	}


	/**
	 * 设置xml文件路径
	 * @param logConfigPath
	 * <br>设置路径
	 */
	public static void setLogConfigPath(String logConfigPath) {
		Logger.logConfigPath = logConfigPath;
	}

	/**
	 * 设置时间输出格式
	 * @param formatter
	 * <br> 输出时间格式
	 */
	public static void setFormatter(SimpleDateFormat formatter) {
		Logger.formatter = formatter;
	}


	/**
	 * 设置输出流文件，可以指定输出到任何文件
	 * @param fw
	 * <br>文件输出流
	 */
	public static void setFw(FileWriter fw) {
		Logger.fw = fw;
	}

	/**
     * logconfig路径
     */
    private static String logConfigPath = Constants.CON_FIRST_PAGE_LOCATION + "log.xml" ;

    private Logger() {
    }
    
    private static Properties properties = new Properties();
    
    /**
     * logconfig.xml文件要保存在/sdcard/卡下面，该配置文件格式要为<br>
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
			 * DOM解析xml文档
			 */
			Document document = builder.parse(logFile);
			Element root = document.getDocumentElement();
			Node node =  null ;
			String value = null ;
			
			/**
			 * VERBOSE级别
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
			 * INFO读取
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
			 * DEBUG级别读取
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
			 * WARN级别读取
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
			 * ERROR级别读取
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
     * 保存日志设置
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
     * 发送 {@link #VERBOSE} 日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int v(String tag, String msg) {
        return println(VERBOSE, tag, msg);
    }

    /**
     * 发送{@link #VERBOSE} 日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     * @param tr 异常日志信息
     */
    public static int v(String tag, String msg, Throwable tr) {
        return println(VERBOSE, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * 发送{@link #DEBUG} 日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int d(String tag, String msg) {
        return println(DEBUG, tag, msg);
    }
    
    /**
     * 发送 {@link #DEBUG} 日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     * @param tr 异常日志信息
     */
    public static int d(String tag, String msg, Throwable tr) {
        return println(DEBUG, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * 发送 {@link #INFO} 日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int i(String tag, String msg) {
        return println(INFO, tag, msg);
    }
    /**
     * 发送 {@link #INFO} 日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int i(String tag, int msg) {
    	return i(tag, "" + msg);
    }
    
    /**
     * 发送 {@link #INFO} 日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int i(String tag, float msg) {
    	return i(tag, "" + msg);
    }
    
    /**
     * 发送 {@link #INFO} 日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int i(String tag, double msg) {
    	return i(tag, "" + msg);
    }
    
    /**
     * 发送 {@link #INFO} 日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
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
     * 发送 {@link #INFO} 日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int i(String tag,long msg){
    	return i(tag,"" + msg);
    }
    
    /**
     * 发送 {@link #INFO} 日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int i(String tag,byte msg){
    	return i(tag,"" + msg);
    }
    
    /**
     * 发送 {@link #INFO} 日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int e(String tag, int msg) {
    	return e(tag, "" + msg);
    }
    
    
    /**
     * 发送 {@link #INFO} 日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int e(String tag, float msg) {
    	return e(tag, "" + msg);
    }
    
    /**
     * 发送 {@link #INFO} 日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int e(String tag, double msg) {
    	return e(tag, "" + msg);
    }
    
    /**
     * 发送 {@link #ERROR} 日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
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
     * 发送 {@link #ERROR} 日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int e(String tag,long msg){
    	return e(tag,"" + msg);
    }
    
    /**
     * 发送 {@link #ERROR} 日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int e(String tag,byte msg){
    	return i(tag,"" + msg);
    }
    
    /**
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int w(String tag, int msg) {
    	return w(tag, "" + msg);
    }
    
    /**
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int w(String tag, float msg) {
    	return w(tag, "" + msg);
    }
    
    /**
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int w(String tag, double msg) {
    	return w(tag, "" + msg);
    }
    
    /**
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
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
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int w(String tag,long msg){
    	return w(tag,"" + msg);
    }
    
    /**
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int w(String tag,byte msg){
    	return w(tag,"" + msg);
    }
    
    /**
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int v(String tag, int msg) {
    	return v(tag, "" + msg);
    }
    
    /**
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int v(String tag, float msg) {
    	return v(tag, "" + msg);
    }
    
    /**
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int v(String tag, double msg) {
    	return v(tag, "" + msg);
    }
    
    /**
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
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
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int v(String tag,long msg){
    	return v(tag,"" + msg);
    }
    
    /**
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int v(String tag,byte msg){
    	return v(tag,"" + msg);
    }
    
    /**
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int d(String tag, int msg) {
    	return d(tag, "" + msg);
    }
    
    /**
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int d(String tag, float msg) {
    	return d(tag, "" + msg);
    }
    
    /**
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int d(String tag, double msg) {
    	return d(tag, "" + msg);
    }
    
    /**
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
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
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int d(String tag,long msg){
    	return d(tag,"" + msg);
    }
    
    /**
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int d(String tag,byte msg){
    	return d(tag,"" + msg);
    }
     /**
     * 发送 {@link #INFO} 异常日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     * @param tr An exception to Logger
     */
    public static int i(String tag, String msg, Throwable tr) {
        return println(INFO, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * 发送 {@link #WARN}日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     */
    public static int w(String tag, String msg) {
        return println(WARN, tag, msg);
    }
    
    /**
     * 发送 {@link #WARN} 异常日志信息.
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     * @param tr 异常信息
     */
    public static int w(String tag, String msg, Throwable tr) {
        return println(WARN, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * 查询该标签是否是指定层次级别的日志信息
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     * @return 是或不是
     * @throws 当标签tag>23时，向外抛出异常
     */
    public static boolean isLoggable(String tag, int level){
    	return Log.isLoggable(tag, level);
    }
        
    /*
     * 发送{@link #WARN} 异常日志信息
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param tr 错误日志信息
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
     * @param tag 用来定义日志信息的标志，建议定义使用class名。
     * @param msg 日志的信息体，详细信息
     * @param tr 异常信息
     */
    public static int e(String tag, String msg, Throwable tr) {
        return e(tag, msg + "\n"+ getStackTraceString(tr));
    }

    /**
     * 得到异常的信息
     * @param tr 异常日志信息
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
     * 输出日志信息
     * @param priority 日志信息级别
     * @param tag 日志信息标签
     * @param msg 日志的详细信息
     * @return 返回写的字节数.
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
     * 日志输出格式
     */
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
    /**
     * 级别打印标志
     */
    private static String leverA = "E" ;
    /**
     * 输出流
     */
    private static FileWriter fw = null;
    /**
     * 关闭文件输出流
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
     * 是否打印该级别的日志
     * @param priority
     * @return 是否打印
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
     * 关闭日志，关闭所有日志级别的输出
     */
    public static void closeLogger(){
    	closeErrorLogger(true);
    }
    
    /**
     * 开启日志，开启所有日志级别输出
     */
    public static void openLogger(){
    	openVerboseLogger(true);
    }
    
    /**
     * 开启Error类别的日志
     * @param upTo是否向上依次开启
     */
    public static void openErrorLogger(boolean upTo){
    	ERRORVIEW = true ;
    }
    /**
     * 开启Error类别的日志
     */
    public static void openErrorLogger(){
    	openErrorLogger(false);
    }
    
    /**
     * 关闭Error类别的日志
     * @param downTo是否向下依次关闭
     */
    public static void closeErrorLogger(boolean downTo){
    	ERRORVIEW = false ;
    	if(downTo){
    		closeWarmLogger(downTo);
    	}
    }
    /**
     * 关闭Error类别的日志
     */
    public static void closeErrorLogger(){
    	closeErrorLogger(false);
    }
    
    /**
     * 开启warn日志类别
     * @param upTo是否向上依次开启
     */
    public static void openWarmLogger(boolean upTo){
    	WARNVIEW = true ;
    	if(upTo){
    		openErrorLogger(upTo);
    	}
    }
    /**
     * 开启warn日志类别
     */
    public static void openWarmLogger(){
    	openWarmLogger(false);
    }
    
    /**
     * 关闭warn日志类别
     * @param downTo是否向下依次关闭
     */
    public static void closeWarmLogger(boolean downTo){
    	WARNVIEW = false ;
    	if(downTo){
    		closeInfoLogger(downTo);
    	}
    }
    /**
     * 关闭warn日志类别
     */
    public static void closeWarmLogger(){
    	closeWarmLogger(false);
    }
    
    /**
     * 开启INFO级别的信息的输出
     * @param upTo是否向上依次开启
     */
    public static void openInfoLogger(boolean upTo){
    	INFOVIEW = true ;
    	if(upTo){
    		openWarmLogger(upTo);
    	}
    }
    /**
     * 开启INFO级别的信息的输出
     */
    public static void openInfoLogger(){
    	openInfoLogger(false);
    }
    
    /**
     * 关闭INFO级别的信息的输出
     * @param downTo是否向下依次关闭
     */
    public static void closeInfoLogger(boolean downTo){
    	INFOVIEW = false ;
    	if(downTo){
    		closeDebugLogger(downTo);
    	}
    }
    /**
     * 关闭INFO级别的信息的输出
     */
    public static void closeInfoLogger(){
    	closeInfoLogger(false);
    }
    
    /**
     * 开启DEBUG级别信息的输出
     * @param upTo是否向上依次开启
     */
    public static void openDebugLogger(boolean upTo){
    	DEBUGVIEW = true ;
    	if(upTo){
    		openInfoLogger(upTo);
    	}
    }
    /**
     * 开启DEBUG级别信息的输出
     */
    public static void openDebugLogger(){
    	openDebugLogger(false);
    }
    
    /**
     * 关闭DEBUG级别信息的输出
     * @param downTo是否向下依次关闭
     */
    public static void closeDebugLogger(boolean downTo){
    	DEBUGVIEW = false ;
    	if(downTo){
    		closeVerboseLogger(downTo);
    	}
    }
    /**
     * 关闭DEBUG级别信息的输出
     */
    public static void closeDebugLogger(){
    	closeDebugLogger(false);
    }
    
    /**
     * 开启VERBOSE级别的信息输出
     * @param upTo是否向上依次开启
     */
    public static void openVerboseLogger(boolean upTo){
    	VERBOSEVIEW = true ;
    	if(upTo){
    		openDebugLogger(upTo);
    	}
    }
    /**
     * 开启VERBOSE级别的信息输出
     */
    public static void openVerboseLogger(){
    	openVerboseLogger(false);
    }
    
    /**
     * 关闭VERBOSE级别的信息输出
     * @param downTo是否向下依次关闭
     */
    public static void closeVerboseLogger(boolean downTo){
    	VERBOSEVIEW = false ;
    }
    /**
     * 关闭VERBOSE级别的信息输出
     */
    public static void closeVerboseLogger(){
    	closeVerboseLogger(false);
    }
    /**
     * 改变设置，统一标签。
     */
    private static boolean changeSet = false ;
    /**
     * 设置
     */
    public static void setChangeSet(boolean changeSet){
    	Logger.changeSet = changeSet ;
    }
    
}
