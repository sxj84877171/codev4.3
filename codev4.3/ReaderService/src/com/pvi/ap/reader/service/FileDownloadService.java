package com.pvi.ap.reader.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;

import com.pvi.ap.reader.data.common.Book;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.data.common.DencryptHelper;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.BookInfo;
import com.pvi.ap.reader.data.external.connection.CPConnection;
import com.pvi.ap.reader.data.external.connection.DRMConnection;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;
import com.pvi.ap.reader.data.external.manager.DRMManager;
import com.pvi.ap.reader.data.external.manager.LeafNode;
import com.pvi.ap.reader.data.external.manager.XMLUtil;
import com.pvi.ap.reader.data.external.manager.XmlElement;

/**
 * 下载服务类<br>
 * 提供各种下载的服务
 * @author 孙向锦
 * @version 1.0.0
 *
 */
public class FileDownloadService extends Service {
	public static String COMMAND = "command";

	public int deviceType = 1 ;         //1 fsl   2 模拟器   3marvell  4 orther real device    

	/**
	 * 服务类标志
	 */
	private static String TAG = "FileDownloadService" ;

	/**
	 * 当前的任务数
	 */
	private static int taskNum = 0 ;
	/**
	 * 任务箱
	 */
	private Map<String,Map<String,Object>> taskMap = new HashMap<String,Map<String,Object>>();
	/**
	 * 任务查看器
	 */
	private DownloadTaskManage taskManage = new DownloadTaskManage();

	/**
	 * 取得当前的任务数
	 */
	public static int getDownTaskNum(){
		return taskNum ;
	}
	/**
	 * 下载首页任务
	 */
	private TimerTask mTimerTask = new TimerTask(){
		public void run() {
			downFristPageMessage();
//			downFristPageMessage1();
		}
	};
	/**
	 * 初始化服务，下载首页xml文件，启动上次未下载完成的任务
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Thread thread = new Thread(){
			public void run() {
				try {
					if (!networkConnectAvailable()) {
						return;
					}
					Timer mTimer = new Timer();
					mTimer.schedule(mTimerTask, new java.util.Date(), 24*3600*1000);
					initDownloadTask();
					if (taskManage != null && !taskManage.isAlive()) {
						taskManage.start();
					}
				} catch (Exception e) {
					Logger.e(TAG, e);
				}
			}
		};
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	/***
	 * 接收外面提交的任务处理
	 */
	@Override
	public void onStart(Intent intent, int startId) {

		Logger.i(TAG, "FiledownloadService.onStart()");
		super.onStart(intent, startId);
		/**
		 * 上层调用不能给空数据
		 */
		if(intent == null){
			return ;
		}
		final Bundle extrasGet = intent.getExtras() ;
		if(extrasGet == null){
			return ;
		}
		new Thread() {
			public void run() {
				try {

					Logger.i(TAG, extrasGet.toString());
					Logger.i(TAG, "command:" + extrasGet.getInt(COMMAND));
					/**
					 * 判断上层的目的 暂停下载
					 */
					if (extrasGet.getInt(COMMAND) == 1) {
						updateBookInfo("pause", extrasGet.getString("contentID"), null);
						pauseDownloadTask(extrasGet);
						return ;
					}

					/**
					 * 重新下载
					 */
					if (extrasGet.getInt(COMMAND) == 2) {
						restartDownloadTask(extrasGet);
					}

					/**
					 * 删除任务
					 */
					if (extrasGet.getInt(COMMAND) == 3) {
						deleteDownloadTask(extrasGet);
						return ;
					}

					/**
					 * 继续下载
					 */
					if (extrasGet.getInt(COMMAND) == 4) {
						updateBookInfo("goon", extrasGet.getString("contentID"), null);
					}

					/**
					 * 全部删除
					 */
					if(extrasGet.getInt(COMMAND) == 5){
						deleteDownloadTask();
					}
					if(extrasGet.getString("bookType") != null && "6".equals(extrasGet.getString("bookType"))){deleteDownloadTask(extrasGet);}

					Logger.i(TAG, "downCert? " +extrasGet.getBoolean("downCert"));
					Logger.i(TAG, "downMeb? " + extrasGet.getBoolean("downMeb"));
					Logger.i(TAG, "downChapter? "+ extrasGet.getBoolean("downChapter"));
					/**
					 * 需要下载证书
					 */
					if (extrasGet.getBoolean("downCert")) {
						try {
							downloadCert(extrasGet);
						} catch (Exception e) {
							updateBookInfo("certfailure", extrasGet.getString("contentID"),null);
							Logger.e(TAG, e);
						}
					}

					//                  /**
					//                   * 批量下载证书
					//                   */
					//                  if (extrasGet.getBoolean("downChapterCert")) {
					//                      try {
					//                          downloadChapterCert(extrasGet);
					//                      } catch (Exception e) {
					//                          updateBookInfo("certfailure", extrasGet.getString("contentID"),extrasGet.getString("chapterID"));
					//                          Logger.e(TAG, e.toString());
					//                      }
					//                  }

					/**
					 * 需要下载MEB文件
					 */
					if (extrasGet.getBoolean("downMeb")) {
						try {
							if(taskMap.get(extrasGet.getString("contentID"))== null ){
								downloadMeb(extrasGet);
							}
						} catch (Exception e) {
							sendmessAndUpdateDate(extrasGet.getString("contentID"),"-1");
							Logger.e(TAG, e);
							return ;
						}
					}

					/**
					 * 需要下载章节
					 */
					if (extrasGet.getBoolean("downChapter")) {
						try {
							downloadChapter(extrasGet,true);
							updateBookInfo("100", extrasGet.getString("contentID"),null);
						} catch (Exception e) {
							// update ...
							sendmessAndUpdateDate(extrasGet.getString("contentID"), "-1");
							return ;
						}
					}
					/**
					 * 启动跟踪器.
					 */
					if(taskManage == null || taskNum <= 0 || !taskManage.isAlive()){
						Logger.i(TAG, "taskManage init");
						taskManage = new DownloadTaskManage();
						taskManage.start();
					}
				}catch(Exception e){
					Logger.e(TAG, e);
				}
			};
		}.start();

	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 初始化上次下载的任务，提交到下载任务当中去。
	 */
	private void initDownloadTask(){

		Cursor cur = selectBookInfo();
		if(cur == null || !cur.moveToFirst()){
			if(cur != null){
				cur.close();
			}
			return ;
		}

		Bundle extras = null ;
		String contentID = null ;
		do{
			extras = new Bundle();
			String name = cur.getString(cur.getColumnIndex(BookInfo.BookPosition));
			String url = cur.getString(cur.getColumnIndex(BookInfo.URL));
			contentID =  cur.getString(cur.getColumnIndex(BookInfo.ContentID)) ;

			extras.putString("contentID",contentID);
			extras.putString("url", url);
			extras.putString("Path", name);
			try {
				downloadMeb(extras);
			} catch (Exception e) {
				sendmessAndUpdateDate(contentID,"-1");
			}
		}while(cur.moveToNext());
		cur.close();
	}

	/**
	 * 应需求，为首页界面下载一个xml文件
	 */
	public void downFristPageMessage() {
		HashMap<String, Object> ahmHeaderMap = CPManagerUtil.getHeaderMap();

		HashMap<String, Object> ahmNamePair = CPManagerUtil.getAhmNamePairMap();

		ahmNamePair.put("blockId", "1");
		ahmNamePair.put("start", "1");
		ahmNamePair.put("count", "10");

		HashMap<String, Object> responseMap = null;
		try {
			// 以POST的形式连接请求
			responseMap = CPManager.getBlockContent(ahmHeaderMap, ahmNamePair);
			if(responseMap == null){
				return ;
			}
		} catch (Exception e) {
			// IO异常 ,一般原因为网络问题
			Logger.e(TAG, e);
			return;
		}
		if(responseMap.get("result-code") == null){
			return ;
		}
		String resultCode = responseMap.get("result-code").toString();
		if (!resultCode.contains("result-code: 0")) {
			return;
		}

		byte[] resultBody = (byte[]) responseMap.get("ResponseBody");

		Document dom = null;
		try {
			dom = CPManagerUtil.getDocumentFrombyteArray(resultBody);
		} catch (ParserConfigurationException e) {
			Logger.e(TAG, e);
			return;
		} catch (SAXException e) {
			Logger.e(TAG, e);
			return;
		} catch (IOException e) {
			Logger.e(TAG, e);
			return;
		}
		ahmNamePair.remove("blockId");
		ahmNamePair.remove("start");
		ahmNamePair.remove("count");
		Element root = dom.getDocumentElement();
		String contentId = "";
		NodeList nodeList1 = root.getElementsByTagName("ContentInfo");
		int su = 0 ;
		for (int i = 0; i < nodeList1.getLength() && i < 10; i++) {
			if(su >= 3){break;}
			Element node1 = (Element) nodeList1.item(i);
			contentId = node1.getElementsByTagName("contentID").item(0)
			.getFirstChild().getNodeValue();
			ahmNamePair.put("contentId", contentId);
			try {
				responseMap = CPManager.getContentInfo(ahmHeaderMap,
						ahmNamePair);
				if(responseMap == null){
					return ;
				}
			} catch (HttpException e) {
				Logger.e(TAG, e);
				return;
			} catch (IOException e) {
				Logger.e(TAG, e);
				return;
			}
			if( responseMap.get("result-code") == null){
				return ;
			}

			resultCode = responseMap.get("result-code").toString();
			if (!resultCode.contains("result-code: 0")) {
				return;
			}
			String temp = new String((byte[]) responseMap.get("ResponseBody"));
			Book book = new Book();
			book.id = contentId;
			book.author = "authorName";
			book.details = "description";
			book.name = "contentName";
			book.url = "coverLogo" ;
			book = getvalue(temp, book);
			if(book == null){
				Logger.i(TAG, "book is null");
				continue ;
			}
			String url = Config.getString("CPC_BASE_URL") + book.url ;
			book.url = "/data/data/com.pvi.ap.reader/" + "book" + su + ".jpg";
			Logger.i(TAG, book.url);
			Bitmap bitmap = getNetImage(url,book.url);
			Logger.i(TAG, "download bitmap");
			if(bitmap == null){
				continue ;
			}
			FileOutputStream fs = null;
			ObjectOutputStream os = null;
			try {
				String fileName = "/data/data/com.pvi.ap.reader/book" + (su++)
				+ ".dat";
				fs = new FileOutputStream(fileName);
				os = new ObjectOutputStream(fs);
				os.writeObject(book);
				Logger.i(TAG, fileName);
			} catch (Exception e) {
				Logger.e(TAG, e);
				Logger.i(TAG, "fail");
			} finally {
				Logger.i(TAG, ".................");
				if (fs != null) {
					try {
						fs.close();
					} catch (IOException e) {
					}
				}
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
					}
				}
			}
			Logger.i(TAG, "OK0");
			if(su >= 3){
				break ;
			}
		}
	}
	public void downFristPageMessage1(){
		HashMap<String,Object> ahmHeaderMap = CPManagerUtil.getHeaderMap();

		HashMap<String,Object> ahmNamePair = CPManagerUtil.getAhmNamePairMap();

		ahmNamePair.put("blockId", "1");
		ahmNamePair.put("start", "1");
		ahmNamePair.put("count", "1");

		HashMap<String,Object> responseMap = null;
		try {
			//以POST的形式连接请求
			responseMap = CPManager.getBlockContent(ahmHeaderMap, ahmNamePair);
		} catch (Exception e) {
			//IO异常 ,一般原因为网络问题
			Logger.e(TAG, e);
			return ;
		}
		String resultCode = responseMap.get("result-code").toString();
		if(!resultCode.contains("result-code: 0")){
			return ;
		}

		byte[] resultBody = (byte[])responseMap.get("ResponseBody");

		Document dom = null;
		try {
			dom = CPManagerUtil.getDocumentFrombyteArray(resultBody);
		} catch (ParserConfigurationException e) {
			Logger.e(TAG, e);
			return ;
		} catch (SAXException e) {
			Logger.e(TAG, e);
			return ;
		} catch (IOException e) {
			Logger.e(TAG, e);
			return ;
		}
		Element root = dom.getDocumentElement();
		String contentId = "";
		Node node = root.getElementsByTagName("ContentParam").item(0);
		if(node == null){
			return ;
		}
		NodeList nodeList = node.getChildNodes();
		if (nodeList != null) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node1 = nodeList.item(i);
				String tagName = node1.getNodeName();
				if ("value".equals(tagName)) {
					contentId = node1.getFirstChild().getNodeValue();
					if(contentId!=null && !"".equals(contentId)){
						break;
					}

				}
			}
		}


		ahmNamePair.remove("blockId");
		ahmNamePair.remove("start");
		ahmNamePair.remove("count");
		ahmNamePair.put("contentId", contentId);
		try {
			responseMap = CPManager.getContentInfo(ahmHeaderMap, ahmNamePair);
		} catch (HttpException e) {
			Logger.e(TAG, e);
			return ;
		} catch (IOException e) {
			Logger.e(TAG, e);
			return ;
		}
		if(responseMap.get("result-code") == null){
			return ;
		}
		resultCode = responseMap.get("result-code").toString();
		if(!resultCode.contains("result-code: 0")){
			return ;
		}

		resultBody = (byte[])responseMap.get("ResponseBody");

		ahmNamePair.remove("contentId");
		try{
			responseMap = CPManager.getSystemParameter(ahmHeaderMap, ahmNamePair);
		}catch (HttpException e) {
			Logger.e(TAG, e);
			return ;
		} catch (IOException e) {
			Logger.e(TAG, e);
			return ;
		}
		byte[] totleBytes = (byte[])responseMap.get("ResponseBody");
		String strTemp = "8888" ;
		try {
			dom = CPManagerUtil.getDocumentFrombyteArray(totleBytes);
		} catch (ParserConfigurationException e) {
			Logger.e(TAG, e);
			return ;
		} catch (SAXException e) {
			Logger.e(TAG, e);
			return ;
		} catch (IOException e) {
			Logger.e(TAG, e);
			return ;
		}
		root = dom.getDocumentElement();
		node = null ;
		NodeList nl = root.getElementsByTagName("SystemParameter");
		if(nl == null){
			return ;
		}
		for(int i = 0 ; i < nl.getLength(); i++ ){
			Element e = (Element)nl.item(i);
			Node n2 = e.getElementsByTagName("type").item(0); 
			if("online_books_count".equals(n2.getFirstChild().getNodeValue())){
				Node n3 = e.getElementsByTagName("value").item(0);
				strTemp = n3.getFirstChild().getNodeValue();
				break ;
			}
		}

		String totleBooksCount = "<TotalBooksCount>"  + strTemp + "</TotalBooksCount>";
		String result = new String(resultBody);
		int index = result.toLowerCase().lastIndexOf("Response".toLowerCase());
		StringBuilder sb = new StringBuilder();
		sb.append(result.subSequence(0, index-2)).append(totleBooksCount).append("</Response>");

		File firstPageLocationPath = new File(Constants.CON_FIRST_PAGE_LOCATION);
		if(!firstPageLocationPath.exists()){
			firstPageLocationPath.mkdirs();
		}

		File xmlFile = new File(Constants.CON_FIRST_PAGE_LOCATION + Constants.CON_FIRST_PAGE_XML_FILE);

		OutputStream os = null ;
		try {
			os = new FileOutputStream(xmlFile);
			os.write(sb.toString().getBytes());
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			Logger.e(TAG, e);
		} catch (IOException e) {
			Logger.e(TAG, e);
		}
	}

	/**
	 * 删除任务
	 * @param task
	 * @return
	 */
	public void deleteDownloadTask(Bundle task){
		Logger.i(TAG, "deleteDownloadTask");
		pauseDownloadTask(task);
		String fileName = task.getString("Path");
		String certName = task.getString("CertPath");
		if(fileName != null && !"".equals(fileName)){
			Logger.i(TAG, fileName);
			File file = new File(fileName);
			Logger.i(TAG, "delete file:" + delete(file));
		}
		if(certName != null && !"".equals(certName)){
			Logger.i(TAG, certName);
			File certFile = new File(certName);
			Logger.i(TAG, "delete cert:" + delete(certFile));
		}
	}
	/**
	 * 删除任务
	 * @param task
	 * @return
	 */
	public void deleteDownloadTask(){
		Logger.i(TAG, "deleteDownloadTask");
		Object[] obj = taskMap.keySet().toArray();
		Bundle task = null ;
		for(Object tempObj :obj){
			task =  new Bundle();
			task.putString("contentID", tempObj.toString());
			pauseDownloadTask(task);
		}
		File fileList = new File(Constants.CON_CERT_PATH);
		for(File tempFile:fileList.listFiles()){
			if(tempFile.getAbsolutePath().endsWith(Constants.CON_CERT_TYPE)){
				if(tempFile.isFile()){
					tempFile.delete();
				}
			}else if(tempFile.isDirectory()){
				delete(tempFile);
			}
		}
		fileList = new File(Constants.CON_MEB_PATH);
		for(File tempFile:fileList.listFiles()){
			if(tempFile.getAbsolutePath().endsWith(".meb")){
				if(tempFile.isFile()){
					tempFile.delete();
				}
			}

			if(tempFile.isDirectory()){
				delete(tempFile);
			}
		}
	}

	private boolean delete(File file){
		boolean sucess = true ;
		if(file.isDirectory()){
			for(File tmp:file.listFiles()){
				if(tmp.getName().endsWith(".meb") || tmp.getName().endsWith(".cert"))
					sucess = sucess && delete(tmp);
			}
			if(sucess){
				file.delete();
			}
		}else{
			sucess = sucess && file.delete();
		}

		return sucess ;
	}

	/**
	 * 重新下载
	 * @param task
	 * @throws Exception 
	 */
	public void restartDownloadTask(Bundle task) {
		Logger.i(TAG, "restartDownloadTask");
		String contentID = task.getString("contendID");
		Object oldTask = taskMap.get(contentID);
		if (oldTask != null) {
			deleteDownloadTask(task);
		}else{
			deleteDownloadTask(task);
		}
	}

	/**
	 * 暂停任务
	 * @param bundle 暂停任务的数据
	 * @return 
	 */
	public void pauseDownloadTask(Bundle task){
		Logger.i(TAG, "pauseDownloadTask");
		String contentID = task.getString("contentID");
		if (contentID != null) {
			Map<String, Object> map = taskMap.get(contentID);
			if(map == null){
				return ;
			}
			FileDownloadThread fdt = (FileDownloadThread) map
			.get(contentID);
			synchronized (taskMap) {
				if (fdt != null) {
					if(fdt.isAlive()){
						fdt.interrupt();
					}
					fdt = null;
				}
				taskMap.remove(contentID);
			}
			taskNum--;
		}
	}

	/**
	 * 下载MEB文件<br>
	 * 继续下载
	 * @param mebBundle
	 * @throws Exception
	 */
	private void downloadMeb(Bundle mebBundle) throws Exception{

		Logger.i(TAG, "downloadMeb start ");

		String urlStr = mebBundle.getString("url");
		String contentID = mebBundle.getString("contentID");
		String fileName = mebBundle.getString("Path") ;
		File mebPath = new File(Constants.CON_MEB_PATH);
		if(!mebPath.exists()){
			mebPath.mkdirs();
		}
		File mebFile = new File(fileName);
		if(!mebFile.exists()){
			mebFile.createNewFile();
		}
		URL url = new URL(encodeURI(urlStr));
		int lenth = 87878 ;

		if("sim".equalsIgnoreCase(getSimType())){
			//Logger.i(TAG, "SIM SocketAddress" + Config.getString("proxyIP") + Config.getString("port"));
			CPConnection.sdcard = "SIM" ;
			SocketAddress addr = new InetSocketAddress(Config.getString("proxyIP"),Integer.parseInt(Config.getString("port")));
			Proxy typeProxy = new Proxy(Proxy.Type.HTTP, addr);  
			URLConnection conn = url.openConnection(typeProxy);
			conn.setConnectTimeout(60000);
			lenth = conn.getContentLength();
		}else{
			CPConnection.sdcard = "USIM" ;
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(60000);
			lenth = conn.getContentLength();
		}
		//      lenth = mebBundle.getString("");
		if(lenth <= 0){
			throw new Exception("the net file is not exists");
		}
		if(getAvailableExternalMemorySize() <= lenth){
			Intent intent = new Intent("com.pvi.ap.reader.mainframe.SHOW_TIP");
			Bundle sndBundle = new Bundle();
			sndBundle.putString("pviapfStatusTip",  getString(R.string.fileLowMerroy));
			sndBundle.putString("pviapfStatusTipTime",  "5000");
			intent.putExtras(sndBundle);
			sendBroadcast(intent);
			throw new Exception("sdcard low memory");
		}
		mebBundle.putString("beenLength", "0");
		mebBundle.putInt("FileLength", lenth);
		updateBookInfo("0", contentID, null);
		FileDownloadThread fdt = new FileDownloadThread(url,mebFile,0,lenth);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(contentID, fdt);
		map.put("value", mebBundle);
		synchronized(taskMap){
			taskMap.put(contentID, map);
		}
		if(taskNum < 4){
			fdt.start();
		}
		taskNum ++ ;
		Logger.i(TAG, "downloadMeb end... ");
	}

	/**
	 * 要下载证书
	 * @param certBundle
	 * @throws Exception 
	 */
	private void downloadCert(Bundle certBundle) throws Exception{

		Logger.e(TAG, "downloadCert start...");
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("Version", certBundle.getString("Version"));
		map.put("CID", certBundle.getString("CID"));
		map.put("PID", certBundle.getString("PID"));
		map.put("Nonce", certBundle.getString("Nonce"));
		map.put("requesttype", certBundle.getString("requesttype"));
		map.put("user-id", certBundle.getString("user-id"));
		map.put("password", certBundle.getString("password"));
		map.put("Accept", certBundle.getString("Accept"));
		map.put("Host", certBundle.getString("Host"));
		map.put("User-Agent", certBundle.getString("User-Agent"));
		//      if(certBundle.getString("x-up-calling-line-id") != null){
		final String cardType = getSimType();
		if( !"sim".equalsIgnoreCase(cardType)){
			map.put("x-up-calling-line-id", certBundle.getString("x-up-calling-line-id"));
		}

		DRMConnection.simType = cardType;
		HashMap hashMap = DRMManager.downloadCert(map);
//		if (hashMap == null || hashMap.get("result-code") == null) {
//			throw new Exception();
//		}
//		if (!hashMap.get("result-code").equals("result-code: 0")) {
//			Logger.e(TAG, "download cert filure because result-code="
//					+ hashMap.get("result-code"));
//			throw new Exception("download cert filure because result-code="
//					+ hashMap.get("result-code"));
//		}
		Logger.i(TAG, hashMap.toString());
		byte[] fileContext = (byte[]) hashMap.get("ResponseBody");
		if(fileContext == null || "".equals(new String(fileContext).trim()) || hashMap.get("Content-Length").toString().contains("Content-Length: 0")){
			throw new Exception();
		}
		String contentID = certBundle.getString("contentID");
		File certPath = new File(Constants.CON_CERT_PATH);
		if(!certPath.exists()){
			certPath.mkdirs();
		}
		File file = new File(Constants.CON_CERT_PATH + contentID + Constants.CON_CERT_TYPE);
		OutputStream os = new FileOutputStream(file);
		os.write(fileContext);
		os.flush();
		os.close();
		Logger.i(TAG, "downloadCert end...");
	}

	private void downloadChapterCert(Bundle certBundle) throws Exception{
		//      android.os.Debug.waitForDebugger();
		//      Logger.i(TAG + "downloadChapterCert", "test start...");
		//      certBundle = new Bundle();
		//      certBundle.putString("requesttype", "4");
		//      certBundle.putString("Nonce", "0123456789123456");
		//      certBundle.putString("user-id", "1bd9c15b9f35d3186f5325ed97527352");
		//      certBundle.putString("password", "PVI80120100922");
		//      certBundle.putString("Accept", "*/*");
		//      certBundle.putString("Host", "211.140.7.144:9080");
		//      certBundle.putString("User-Agent", "EInkStack");
		//      certBundle.putString("Version", "PVI_P801_V0.10");
		//      certBundle.putString("x-up-calling-line-id", "13466320945");
		//      certBundle.putString("Content-Type", "application/xml");
		//      ArrayList<String> arr = new ArrayList<String>();
		//      arr.add("66510682");
		//      arr.add("66510683");
		//      arr.add("66510684");
		//      arr.add("66510685");
		//      arr.add("66510686");
		//      arr.add("66510687");
		//      arr.add("66510688");
		//      arr.add("66510689");
		//      arr.add("66510690");
		//      arr.add("66510691");
		//      arr.add("66510692");
		//      arr.add("66510693");
		//      arr.add("66510694");
		//      certBundle.putStringArrayList("idList", arr);
		//      Logger.i(TAG + "downloadChapterCert", "test end...");
		String ranName = certBundle.getString("chapterID");
		String contentID = certBundle.getString("contentID");
		String fileName = Constants.CON_CERT_PATH + contentID +File.separator+  ranName + Constants.CON_CERT_TYPE;
		File pathFile = new File(Constants.CON_CERT_PATH + contentID);
		File file = new File(fileName);
		if(file.exists()){
			return ;
		}


		Logger.i(TAG  + "downloadChapterCert", "downloadChapterCert start...");
		ArrayList<String> chapterIdList = certBundle.getStringArrayList("idList");
		if(chapterIdList == null || chapterIdList.size() <= 0){
			Logger.i(TAG + "downloadChapterCert", "downloadChapterCert is not need download,because chapterlist is empty");
			return ;
		}
		HashMap<String,String> ahm = new HashMap<String,String>();
		ahm.put("requesttype", "4");
		String key = "Nonce" ;
		ahm.put(key, certBundle.getString(key));
		key = "user-id" ;
		ahm.put(key, certBundle.getString(key));
		key = "password" ;
		ahm.put(key, certBundle.getString(key));
		key = "Accept" ;
		ahm.put(key, certBundle.getString(key));
		key = "Host" ;
		ahm.put(key, certBundle.getString(key));
		key = "User-Agent" ;
		ahm.put(key, certBundle.getString(key));
		key = "Version" ;
		ahm.put(key, certBundle.getString(key));
		key = "x-up-calling-line-id" ;
		ahm.put(key, certBundle.getString(key));
		ahm.put("Content-Type", "application/xml");
		HashMap<String,String> body = new HashMap<String,String>();

		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<CIDList>");
		for(int i=0;i<chapterIdList.size(); i ++){
			sb.append("<CID>").append(chapterIdList.get(i)).append("</CID>");
		}
		sb.append("</CIDList>");
		Logger.i(TAG, sb);
		body.put("XMLBody", sb.toString());
		String requestType = (String)ahm.get("requesttype");
		String nonce = (String)ahm.get("Nonce");
		String version = (String)ahm.get("Version");
		String userid = (String)ahm.get("user-id");
		String password = (String)ahm.get("password");

		String temp = requestType + nonce + version + userid + password;
		Logger.i(TAG + "downloadChapterCert", temp);
		String base64str = DencryptHelper.md5encrypt(temp);
		Logger.i(TAG + "downloadChapterCert", base64str);

		CPConnection dRMConnection = new CPConnection(Constants.DRM_URL_DOWNLOADCERT);

		HashMap ahmHeaderMap = new HashMap();


		final String cardType = this.getSimType();
		if(cardType != null && "sim".equalsIgnoreCase(cardType.toLowerCase())){
			dRMConnection.sdcard = "SIM";

		}else if(cardType != null && "usim".equalsIgnoreCase(cardType.toLowerCase())){
			dRMConnection.sdcard = "USIM";
			ahmHeaderMap.put("x-up-calling-line-id",(String)ahm.get("x-up-calling-line-id"));
		}

		ahmHeaderMap.put("Nonce",(String)ahm.get("Nonce"));
		ahmHeaderMap.put("Accept",(String)ahm.get("Accept"));
		ahmHeaderMap.put("Host",(String)ahm.get("Host"));
		ahmHeaderMap.put("User-Agent",(String)ahm.get("User-Agent"));
		ahmHeaderMap.put("requesttype",(String)ahm.get("requesttype"));
		ahmHeaderMap.put("Version",(String)ahm.get("Version"));     
		ahmHeaderMap.put("user-id",(String)ahm.get("user-id"));
		ahmHeaderMap.put("Content-Type",(String)ahm.get("Content-Type"));
		ahmHeaderMap.put("ReqDigest",base64str);
		HashMap retHashMap = null; 
		try {
			//          System.out.println(ahmHeaderMap);
			//          System.out.println(body);
			retHashMap = dRMConnection.doPost(ahmHeaderMap, body);
		} catch (HttpException e) {
			throw e ;
		} catch (IOException e) {
			throw e ;
		}
		//      if(!((StatusLine)retHashMap.get("Status")).startsWithHTTP("")){
		//          return ;
		//      }
		//      Logger.i(TAG + "downloadChapterCert", retHashMap);
		//      System.out.println(retHashMap);
		byte[] message = (byte[])retHashMap.get("ResponseBody");
		//      System.out.println(new String(message));

		if(!pathFile.exists()){
			pathFile.mkdirs();
		}

		if (!file.exists()) {
			try {
				file.createNewFile();
				OutputStream os = new FileOutputStream(file);
				os.write(message);
				os.flush();
				os.close();
			} catch (IOException e) {
			}
		}

		// 写入文件

		//      Logger.i(TAG, message);

		Logger.i(TAG + "downloadChapterCert", "downloadChapterCert end...");
	}

	/**
	 * 下载章节
	 * @param chapterID 章节数据
	 * @throws Exception
	 */
	private void downloadChapter(Bundle chapter,boolean flag) throws Exception{
		List<String> urlArray = chapter.getStringArrayList("urlList");
		ArrayList<String> chapterIdList = chapter.getStringArrayList("idList");
		String contentID = chapter.getString("contentID");
		ArrayList<String> tmpArray = null ;
		ArrayList<String> tmpChapterIdList = null ;
		String url = null ;
		String chapterID = null ;
		Bundle tmpDown = null ;
		for(int i = 0 ; i < urlArray.size() ; i ++){
			updateDownStateView(1);
			url = urlArray.get(i);
			chapterID = chapterIdList.get(i);

			tmpArray = new ArrayList<String>(1);
			tmpChapterIdList = new ArrayList<String>(1);

			tmpArray.add(url);
			tmpChapterIdList.add(chapterID);

			tmpDown = (Bundle)chapter.clone();
			tmpDown.putStringArrayList("urlList", tmpArray);
			tmpDown.putStringArrayList("idList", tmpChapterIdList);
			tmpDown.putString("chapterID", chapterID);
			tmpDown.putString("contentID", contentID);

			try {
				downloadChapter(tmpDown);
				downloadChapterCert(tmpDown);
			} catch (Exception e) {
				Logger.e(TAG, e);
				sendmessAndUpdateDate(contentID, "-1");
				break ;
			}

			int data = (int)((i + 1) * 100 / urlArray.size()) ;
			sendmessAndUpdateDate(contentID, "" + data);
		}
		if (taskNum <= 0) {
			updateDownStateView(2);
		}
	}

	/**
	 * 下载章节
	 * @param chapterID 章节数据
	 * @throws Exception
	 */
	private void downloadChapter(Bundle chapter) throws Exception {

		Logger.i(TAG + "->downloadChapter", chapter);
		List<String> urlArray = chapter.getStringArrayList("urlList");
		String contentID = chapter.getString("contentID");
		String ranName = chapter.getString("chapterID");
		String fileName = ranName + ".meb";
		File file = new File(Constants.CON_MEB_PATH + File.separator + contentID + File.separator + fileName);
		if(file.exists()){
			return ;
		}
		LeafNode lf = null;
		List<LeafNode> listChild = null;
		int index = -1, end = -1, start = -1;
		String body = null;
		String requestXMLBody = null;
		XmlElement resourceList = null;
		List<XmlElement> list = null;
		XmlElement getResourcesReq = null;
		List<XmlElement> listRequest = null;
		XmlElement request = null;
		HashMap<String, Object> ahmHeaderMap = null;
		HashMap<String, Object> ahmNamePair = null;
		HashMap<String, Object> responseMap = null;
		String contentBody = null;
		listChild = new ArrayList<LeafNode>();
		for (String url : urlArray) {
			Logger.i(TAG, url);
			lf = new LeafNode("url", url);
			listChild.add(lf);
		}
		// 组织成xml文件
		resourceList = new XmlElement("ResourceList", listChild);
		list = new ArrayList<XmlElement>();
		list.add(resourceList);
		getResourcesReq = new XmlElement("GetResourcesReq", list);
		listRequest = new ArrayList<XmlElement>();
		listRequest.add(getResourcesReq);
		request = new XmlElement("Request", listRequest);
		try {
			requestXMLBody = XMLUtil.getXmlStringFromXmlElement(request);
		} catch (Exception e) {
			Logger.e(TAG + "::downloadChapter", e);
			throw e;
		}
		ahmHeaderMap = CPManagerUtil.getHeaderMap();
		ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		ahmNamePair.put("XMLBody", requestXMLBody);
		// 提交到平台进行下载
		try {
			ahmHeaderMap.put("Action", "getResources");
			responseMap = new CPConnection(Config.getString("CPC_URL")).doPost(
					ahmHeaderMap, ahmNamePair);
		} catch (Exception e) {
			Logger.e(TAG + "::downloadChapter", e);
			throw e;
		}
		//      System.out.println(responseMap);
		requestXMLBody = null;
		resourceList = null;
		list = null;
		getResourcesReq = null;
		listRequest = null;
		request = null;
		ahmHeaderMap = null;
		ahmNamePair = null;
		// 得到返回结果
		String resultCode = responseMap.get("result-code").toString();
		// 是否正确返回
		if (!resultCode.contains("result-code: 0")) {
			Logger.i(TAG, resultCode);
			throw new Exception();
		}
		byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
		responseMap = null;
		contentBody = new String(responseBody);
		File pathFile = new File(Constants.CON_MEB_PATH + File.separator + contentID);
		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
		// 写入文件
		FileWriter os = new FileWriter(file, true);
		Logger.i(TAG, "contentBody.length()=" + contentBody.length());
		start = contentBody.indexOf("resourceboundary");
		while (true) {
			System.gc();
			//          System.out.println("resourceboundary start--------------------"
			//                  + start);
			index = contentBody.indexOf("XMEB", start);
			//          System.out.println("resourceboundary index--------------------"
			//                  + index);
			if (index == -1 || start == -1) {
				break;
			}
			end = contentBody.indexOf("--resourceboundary", start + 1);
			//          System.out
			//                  .println("resourceboundary end--------------------" + end);
			body = contentBody.substring(index, end);
			os.write(body);
			start = end;
		}
		os.flush();
		os.close();
		Logger.i(TAG, "downloadChapter end...");
	}

	//  /**
	//   * 下载章节
	//   * @param chapterID 章节数据
	//   * @throws Exception
	//   */
	//  private void downloadChapter(Bundle chapter) throws Exception{
	//      
	//      final int once = 10 ;
	//      Logger.i(TAG + "->downloadChapter", chapter);
	//      updateDownStateView(1);
	//      List<String> urlArray = chapter.getStringArrayList("urlList");
	//      System.out.println("urlArray" + urlArray.size());
	//      LeafNode lf = null ;
	//      List<LeafNode> listChild = null;
	//      String contentID = chapter.getString("contentID");
	//      String ranName = chapter.getString("ranName");
	//      int index = -1, end = -1,start = -1;
	//      String body = null ;
	//      float percen =  (float)once / (float)urlArray.size() ;
	//      float sum = percen ;
	//      List<String> tpList = null ;
	//      String requestXMLBody = null;
	//      String sumStr = null ;
	//      XmlElement resourceList = null ;
	//      List<XmlElement> list = null ;
	//      XmlElement getResourcesReq = null ;
	//      List<XmlElement> listRequest = null ;
	//      XmlElement request = null ;
	//      HashMap<String, Object> ahmHeaderMap = null ;
	//      HashMap<String, Object> ahmNamePair = null ;
	//      HashMap<String, Object> responseMap = null;
	//      String contentBody = null ;
	//      while (true) {
	//          if(urlArray == null){
	//              break ;
	//          }
	//          if(urlArray.size() > once){
	//              tpList =  urlArray.subList(0,once);
	//              urlArray = urlArray.subList(once, urlArray.size());
	//          }else{
	//              tpList = urlArray ;
	//              urlArray = null ;
	//          }
	//          System.out.println("tpList" + tpList.size());
	//          listChild = new ArrayList<LeafNode>() ;
	//          for (String url : tpList) {
	//              Logger.i(TAG, url);
	//              lf = new LeafNode("url", url);
	//              listChild.add(lf);
	//          }
	//          // 组织成xml文件
	//          resourceList = new XmlElement("ResourceList", listChild);
	//          list = new ArrayList<XmlElement>();
	//          list.add(resourceList);
	//          getResourcesReq = new XmlElement("GetResourcesReq", list);
	//          listRequest = new ArrayList<XmlElement>();
	//          listRequest.add(getResourcesReq);
	//          request = new XmlElement("Request", listRequest);
	//          try {
	//              requestXMLBody = XMLUtil.getXmlStringFromXmlElement(request);
	//          } catch (Exception e) {
	//              Logger.e(TAG + "::downloadChapter", e);
	//              return;
	//          }
	//          ahmHeaderMap = CPManagerUtil.getHeaderMap();
	//          ahmNamePair = CPManagerUtil.getAhmNamePairMap();
	//          ahmNamePair.put("XMLBody", requestXMLBody);
	//          // 提交到平台进行下载
	//          try {
	//              ahmHeaderMap.put("Action", "getResources");
	//              responseMap = new CPConnection(Config.getString("CPC_URL"))
	//                      .doPost(ahmHeaderMap, ahmNamePair);
	//          } catch (Exception e) {
	//              Logger.e(TAG + "::downloadChapter", e);
	//              throw new Exception();
	//          }
	//          System.out.println(responseMap);
	//          
	//          tpList = null ;
	//          requestXMLBody = null;
	//          sumStr = null ;
	//          resourceList = null ;
	//          list = null ;
	//          getResourcesReq = null ;
	//          listRequest = null ;
	//          request = null ;
	//          ahmHeaderMap = null ;
	//          ahmNamePair = null ;
	//          
	//          // 得到返回结果
	//          String resultCode = responseMap.get("result-code").toString();
	//
	//          // 是否正确返回
	//          if (!resultCode.contains("result-code: 0")) {
	//              Logger.i(TAG, resultCode);
	//              throw new Exception();
	//          }
	//          byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
	//          responseMap = null;
	//          contentBody = new String(responseBody);
	//          File pathFile = new File(Constants.CON_MEB_PATH);
	//          if (!pathFile.exists()) {
	//              pathFile.mkdirs();
	//          }
	//
	//          String fileName = contentID + "_" + ranName + ".meb";
	//          File file = new File(Constants.CON_MEB_PATH + fileName);
	//
	//          // 写入文件
	//          FileWriter os = new FileWriter(file, true);
	//          Logger.i(TAG, "contentBody.length()=" + contentBody.length());
	//          start = contentBody.indexOf("resourceboundary");
	//          while (true) {
	//              System.gc();
	//              System.out.println("resourceboundary start--------------------" + start);
	//              index = contentBody.indexOf("XMEB", start);
	//              System.out.println("resourceboundary index--------------------" + index);
	//              if (index == -1 || start == -1) {
	//                  break;
	//              }
	//              end = contentBody.indexOf("--resourceboundary", start + 1);
	//              System.out.println("resourceboundary end--------------------" + end);
	//              body = contentBody.substring(index, end);
	//              os.write(body);
	//              start = end;
	//          }
	//          Logger.i(TAG,"while(trye) break");
	//          os.flush();
	//          os.close();
	//          sumStr = "" + (int)(100 * sum) ;
	//          updateBookInfo(sumStr, contentID,null);
	//          Intent myIntent = new Intent("com.pvi.ap.reader.FileDownloadService.data.update");
	//          myIntent.putExtra("data",sumStr);
	//          myIntent.putExtra("contentID", contentID);
	//          sendBroadcast(myIntent);
	//          sum += percen ;
	//
	//      }
	//      if (taskNum <= 0) {
	//          updateDownStateView(2);
	//      }
	//      updateBookInfo("100", contentID,null);
	//      Intent myIntent = new Intent("com.pvi.ap.reader.FileDownloadService.data.update");
	//      myIntent.putExtra("data", "100");
	//      myIntent.putExtra("contentID", contentID);
	//      sendBroadcast(myIntent);
	//      Intent intent = new Intent("com.pvi.ap.reader.mainframe.SHOW_TIP");
	//        Bundle sndBundle = new Bundle();
	//        sndBundle.putString("pviapfStatusTip",  getString(R.string.fileBeenDownTip));
	//        sndBundle.putString("pviapfStatusTipTime",  "5000");
	//        intent.putExtras(sndBundle);
	//        sendBroadcast(intent);
	//      Logger.i(TAG, "downloadChapter end...");
	//  }
	/**
	 * 绑定服务
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	/**
	 * 退出服务
	 */
	@Override
	public void onDestroy() {

		if(taskManage != null){
			if(taskManage.isAlive()){
				taskManage.interrupt();
			}
			taskManage = null ;
		}

		super.onDestroy();
	}

	/**
	 * 查询未下载完成的任务
	 * @return
	 * 数据库游标
	 */
	private Cursor selectBookInfo() {
		String columns[] = new String[] { BookInfo._ID, BookInfo.ContentID,
				BookInfo.Name,
				BookInfo.PathType, BookInfo.ProcessPer, 
				BookInfo.BookPosition,
				BookInfo.URL};
		return getContentResolver().query(BookInfo.CONTENT_URI, columns,
				BookInfo.ProcessPer + " != \"100\" and " + BookInfo.ProcessPer + " != \"failure\" and  " + BookInfo.ChapterID + " = \"\"", null, null);
	}
	/**
	 * 修改进度
	 * @param processPer
	 * @param contentID
	 */
	private void updateBookInfo(String processPer, String contentID,String bookSize) {
		Logger.i(TAG, "contentID:" + contentID + " processPer:" + processPer);

		ContentValues values = new ContentValues();
		if("failure".equals(processPer)){
			values.put(BookInfo.DownloadStatus, "3");
		}else if("certfailure".equals(processPer)){
			values.put(BookInfo.CertStatus, "2");
		}else if("pause".equals(processPer)){
			values.put(BookInfo.DownloadStatus, "1");
		}else if("100".equals(processPer)){
			values.put(BookInfo.DownloadStatus, "2");
			values.put(BookInfo.ProcessPer, processPer);
		}else if("goon".equals(processPer)){
			values.put(BookInfo.DownloadStatus, "0");
		}else{
			values.put(BookInfo.DownloadStatus, "0");
			values.put(BookInfo.ProcessPer, processPer);
		}
		if(bookSize != null && !"".equals(bookSize.trim())){
			values.put(BookInfo.BookSize, bookSize);
		}
		getContentResolver().update(BookInfo.CONTENT_URI, values,
				BookInfo.ContentID + "='" + contentID + "' ", null);//and " + BookInfo.ProcessPer + " < '" + processPer + "'
	}
	/**
	 * 删除任务
	 * @param processPer
	 * @param contentID
	 */
	private void deleteBookInfo(String contentID,String chapterID) {
		Logger.i(TAG, "contentID:" + contentID + " processPer");

		StringBuffer where = new StringBuffer() ;
		boolean flag = false ;
		if(contentID != null){
			flag = true ;
			where.append(BookInfo.ContentID + "='" + contentID + "' ");
		}
		if(chapterID != null && !"".equals(chapterID.trim())){
			if(flag){
				where.append(" and ");
			}
			where.append(BookInfo.ChapterID + "='" + chapterID + "' ");
		}
		if(flag){
			getContentResolver().delete(BookInfo.CONTENT_URI, where.toString(), null);
		}
	}

	/*  *//**
	 * 插入记录
	 * @param extras 数据
	 *//*
    private void insertBookInfo(Bundle extras){

        Date CurTime = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = formatter.format(CurTime);
        ContentValues values = new ContentValues();
        values.put(BookInfo.ContentID, extras.getString("contentID"));
        values.put(BookInfo.Name, extras.getString("name"));
        values.put(BookInfo.Catelog, extras.getString("cateLog"));
        values.put(BookInfo.BookType, extras.getString("bookType"));
        values.put(BookInfo.PathType,extras.getString("pathType"));
        values.put(BookInfo.ProcessPer, "0");
        values.put(BookInfo.Author, extras.getString("author"));
        values.put(BookInfo.Maker, extras.getString("maker"));
        if(extras.getString("chapterID") != null){
            values.put(BookInfo.ChapterID, extras.getString("chapterID"));
        }
        if(extras.getString("Path") == null){
            String path = Constants.CON_MEB_PATH + extras.getString("contentID");
            if(extras.getString("chapterID") != null){
                path += "." +  extras.getString("chapterID") ;
            }
            path = path + ".meb" ;
            extras.putString("Path", path);
        }
        if(extras.getString("CertPath") == null){
            extras.putString("CertPath", Constants.CON_CERT_PATH + extras.getString("contentID") + Constants.CON_CERT_TYPE);
        }
        values.put(BookInfo.CertPath,extras.getString("CertPath"));
        values.put(BookInfo.BookPosition,extras.getString("Path"));
        values.put(BookInfo.DownloadTime, now);
        values.put(BookInfo.SaleTime, extras.getString("saleTime"));
        values.put(BookInfo.URL, extras.getString("url"));
        values.put(BookInfo.DownloadType, extras.getString("downloadType"));
        getContentResolver().insert(BookInfo.CONTENT_URI, values);
    }*/

	/**
	 * 查询各个线程下载进度
	 * @author rd036
	 */
	private class DownloadTaskManage extends Thread{
		@Override
		public void run() {
			while (true) {
				Logger.i(TAG, "Current task:" + taskNum);
				if (taskNum <= 0) {
					updateDownStateView(2);
					Logger.i(TAG, "no task and the download is exit");
					return;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					updateDownStateView(2);
					Logger.i(TAG, "receiver command to stop download task and be exit");
					return ;
				}
				if (taskMap.isEmpty()) {
					continue;
				}
				updateDownStateView(1);
				Object[] obj = taskMap.keySet().toArray();

				if(taskNum != obj.length){
					taskNum = obj.length ;
				}
				if(obj.length == 0){
					updateDownStateView(2);
					return ;
				}
				for (int i = 0 ; i< obj.length ; i ++) {
					final String contentID = obj[i].toString();
					Map<String, Object> taskValueMap = taskMap.get(contentID);
					//                  Logger.i(TAG, "contentID:" + contentID);
					if(taskValueMap == null){
						continue ;
					}
					FileDownloadThread fdt = (FileDownloadThread) taskValueMap
					.get(contentID);
					Bundle extrasInner = (Bundle) taskValueMap.get("value");
					int lenth = extrasInner.getInt("FileLength");
					//                  Logger.i(TAG, "FileLength:" + lenth);
					if(lenth <= 0){
						synchronized (taskMap) {
							taskMap.remove(contentID);
						}
						taskNum -- ;
						sendmessAndUpdateDate(contentID, "-1");
					}
					if (fdt != null) {

						if (fdt.getRunException() == 1 // 网络异常，断点续传
								|| !(fdt.isFinished() ||fdt.isAlive())) { // 任务过多，启动新的任务
							String urlStr = extrasInner.getString("url");
							File file = new File(extrasInner.getString("Path"));
							if(!file.exists()){
								try {
									file.createNewFile();
								} catch (IOException e) {
									Logger.e(TAG, e);
									continue;
								}
							}
							URL url = null;
							try {
								url = new URL(encodeURI(urlStr));
							} catch (Exception e) {
								Logger.e(TAG, e);
								continue;
							}
							fdt = new FileDownloadThread(url, file, 0, lenth);
							fdt.start();
							//                          Logger.i(TAG, "re start");
						}else if(fdt.getRunException() == 2){
							synchronized (taskMap) {
								taskMap.remove(contentID);
							}
							taskNum -- ;
							sendmessAndUpdateDate(contentID,"-1");
						}
					}else{
						continue ;
					}
					String beenLength = extrasInner.getString("beenLength");
					double dbeenLiength = 0 ;
					if(beenLength != null){
						dbeenLiength = Double.parseDouble(beenLength);
					}
					int size = fdt.getDownloadSize();
					//                  Logger.i(TAG, "Been downSize:" + size);
					double percent = ((double) size) / (double) lenth * 100;
					if(percent - dbeenLiength >= 1){
						final String result = String.valueOf((int) percent);
						extrasInner.putString("beenLength", result);
						if(percent <= 100){
							sendmessAndUpdateDate(contentID, result);
						}
						//                      Logger.i(TAG, "result:" + result);
					}
					/**
					 * 任务结束，移出任务队列
					 */
					if (fdt != null) {
						if (fdt.isFinished()) {
							fdt = null;
							synchronized (taskMap) {
								taskMap.remove(contentID);
							}
							Logger.i(TAG, "taskNum:" + taskNum);
							sendmessAndUpdateDate(contentID, "100");
							taskNum--;
							Logger.i(TAG, "result:" + 100);
							if(taskNum <= 0){
								updateDownStateView(2);
								return ;
							}
						}
					}

				}
			}
		}

		/**
		 * 退出线程
		 */
		@Override
		public void interrupt() {
			Object[] obj = taskMap.keySet().toArray();
			for(int i = 0 ; i< obj.length ; i++){
				String taskKey = obj[i].toString();
				Map<String,Object> taskValueMap = taskMap.get(taskKey);
				FileDownloadThread fdt = (FileDownloadThread)taskValueMap.get(taskKey);
				if(fdt != null){
					if(fdt.isAlive()){
						fdt.interrupt();
					}
					fdt = null ;
				}
			}
			super.interrupt();
		}
	}


	private void updateDownStateView(int send) {
		Bundle bundleToSend2 = new Bundle();
		bundleToSend2.putInt("STATE", send); // 1有下载任务进行中，显示gif
		// 2无下载任务进行，图标隐藏
		final Intent tmpintent2 = new Intent(
		"com.pvi.ap.reader.DownloadStateView.DOWNLOAD_STATE_UPDATE");
		tmpintent2.putExtras(bundleToSend2);
		sendBroadcast(tmpintent2);
	}

	private void sendmessAndUpdateDate(final String contentID,String data) {
		if("-1".equals(data)){
			updateBookInfo("failure", contentID,null);
			Logger.i(TAG, "the tast content(" + contentID + ") is failure");
		}else{
			updateBookInfo(data, contentID,null);
		}
		Intent myIntent = new Intent("com.pvi.ap.reader.FileDownloadService.data.update");
		myIntent.putExtra("data", data);
		myIntent.putExtra("contentID", contentID);
		sendBroadcast(myIntent);
		if("100".equals(data)){
			Intent intent = new Intent("com.pvi.ap.reader.mainframe.SHOW_TIP");
			Bundle sndBundle = new Bundle();
			sndBundle.putString("pviapfStatusTip",  getString(R.string.fileBeenDownTip));
			sndBundle.putString("pviapfStatusTipTime",  "5000");
			intent.putExtras(sndBundle);
			sendBroadcast(intent);
		}
	}

	/**
	 * 网络是否连接正常
	 * @return
	 */
	public boolean networkConnectAvailable(){
		//      ConnectivityManager cwjManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		//      
		//      if(cwjManager == null){
		//          return false ;
		//      }
		//      if(cwjManager.getActiveNetworkInfo() == null){
		//          return false ;
		//      }
		//      
		//      return cwjManager.getActiveNetworkInfo().isAvailable();
		
		return true ;
	}

	public static String getvalue(String source,String pattern){
		try{
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance(); 
			DocumentBuilder db = dbfactory.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(source.getBytes());
			Document dom = db.parse(is); 
			Element rootele = dom.getDocumentElement();
			NodeList nl = rootele.getElementsByTagName(pattern);
			String tmp = nl.item(0).getFirstChild().getNodeValue();
			return tmp; 
		}catch (Exception e) {
			Logger.e(TAG, e);
			return "error";
		}   
	}
	
	public Book getvalue(String source,Book book){
		try{
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance(); 
			DocumentBuilder db = dbfactory.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(source.getBytes());
			Document dom = db.parse(is); 
			Element rootele = dom.getDocumentElement();
			String pattern = book.author;
			NodeList nl = rootele.getElementsByTagName(pattern);
			book.author = nl.item(0).getFirstChild().getNodeValue();
			pattern = book.details;
			nl = rootele.getElementsByTagName(pattern);
			book.details = nl.item(0).getFirstChild().getNodeValue();
			pattern = book.url;
			nl = rootele.getElementsByTagName(pattern);
			book.url = nl.item(0).getFirstChild().getNodeValue();
			pattern = book.name;
			nl = rootele.getElementsByTagName(pattern);
			book.name = nl.item(0).getFirstChild().getNodeValue();
			return book;
		}catch (Exception e) {
			Logger.e(TAG, e);
			return null;
		}  
	}


	public static long getAvailableExternalMemorySize() {   
		long availableExternalMemorySize = 0;   
		if (Environment.getExternalStorageState().equals(   
				Environment.MEDIA_MOUNTED)) {   
			File path = Environment.getExternalStorageDirectory();   
			StatFs stat = new StatFs(path.getPath());   
			long blockSize = stat.getBlockSize();   
			long availableBlocks = stat.getAvailableBlocks();   
			availableExternalMemorySize = availableBlocks * blockSize;   
		}else if (Environment.getExternalStorageState().equals(   
				Environment.MEDIA_REMOVED)
				||Environment.getExternalStorageState().equals(Environment.MEDIA_BAD_REMOVAL)
				||Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTABLE)
				||Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {   
			availableExternalMemorySize = 0;   
		}   

		return availableExternalMemorySize;   
	} 

	private String getSimType(){
		if(this.deviceType==1){//非思卡尔
			return android.os.SystemProperties.get("gsm.sim.card.type");
		}else if(this.deviceType==2){//模拟器
			return "USIM";
		}else if(this.deviceType==3){//马威尔
			return "";
		}else if(this.deviceType==4){//普通手机
			return "SIM";//默认有卡且是SIM卡
		}else{
			return "WRONGSIM";
		}
	}
	
	
	public Bitmap getNetImage(String url,String savePath){
		Bitmap bmp = null ;
		url = encodeURI(url);
		String tmpName = savePath.substring(0, savePath.lastIndexOf('/'));
		File tmpFile = new File(tmpName);
		if(!tmpFile.exists()){
			tmpFile.mkdirs();
		}
		File file = new File(savePath);
		if (bmp == null) {
			try {
				file.createNewFile();
				URL ImageUrl = new URL(url);
				URLConnection conn = null;
				if ("sim".equalsIgnoreCase(getSimType())) {
					SocketAddress addr = new InetSocketAddress(Config
							.getString("proxyIP"), Integer.parseInt(Config
							.getString("port")));
					Proxy typeProxy = new Proxy(Proxy.Type.HTTP, addr);
					conn = ImageUrl.openConnection(typeProxy);
				}else{
					conn = ImageUrl.openConnection();
				}
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				bmp = BitmapFactory.decodeStream(is);
				is.close();
				FileOutputStream fout = new FileOutputStream(file);
				bmp.compress(Bitmap.CompressFormat.PNG, 100, fout);
			} catch (Exception e) {
				Logger.e(TAG, e);
				return null;
			}
		}
		return bmp;
	}
	
	public static String encodeURI(String url){
		
		
		String ret = url ;
		Logger.i(TAG + "encodeURI", ret);
		if(ret == null) return url ;
		StringBuffer sb = new StringBuffer();
		try {
			for(int i = 0 ; i < url.length();i++){
				if(url.charAt(i) > 128){
					sb.append(URLEncoder.encode("" + url.charAt(i),"UTF-8"));
				}else{
					sb.append(url.charAt(i));
				}
			}
//		ret = URLEncoder.encode(url,"GBK");
//			ret =  new String(url.getBytes("ISO-8859-1"),"UTF-8");
		} catch (Exception e) {
			Logger.e(TAG, e);
		}
		Logger.i(TAG, sb.toString());
		return sb.toString();
	}
}
