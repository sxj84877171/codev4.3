package com.pvi.ap.reader.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;

import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.connection.CPConnection;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;


public class NetCacheService extends Service {
	
	public final static String cacheConfig = CacheFile.GetCachePath() + File.separator  + "cache.properties";
	
	private String TAG = "NetCacheService" ;
	public int deviceType = 1 ;         //1 fsl   2 模拟器   3marvell  4 orther real device    
    
	private Prefetch prefetch = null;
	protected  static NetCacheServicer m_hNetCache = null;

	public NetCacheService() {
		if (m_hNetCache == null)
			m_hNetCache = new NetCacheServicer();
	} 
	
	@Override
	public void onCreate() {
		Logger.i(TAG, "NetCacheService start...");
		CacheFile.checkCatchFile(30*60*1000, 10 * 60 * 1000); // 30分一次 10分钟后开始
		CacheFile.setMinFreeSpace((long)GetNetCacheTime("MinFreeSpace"));
		CacheFile.setMaxCacheSize((long)GetNetCacheTime("MaxCacheSize"));
		registBroadcaseReceiver();
		prefetch = new Prefetch();
		new Thread(){
			public void run() {delete(CacheFile.GetCachePath());};
		}.start();
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Logger.i(TAG, "onBind:" + arg0);
		if (m_hNetCache == null){
			Logger.i(TAG, "m_hNetCache is null");
			m_hNetCache = new NetCacheServicer();
		}
		Logger.i(TAG, "m_hNetCache:" + m_hNetCache);
		return m_hNetCache;
	}
	
    /**
     * 取得要缓存的时间 
	 * @param IFName 是指通信的接口名,可据此返回不同的时间
	 * @return 以分为计算单位,值为零表示不缓存
     */
    public static  int GetNetCacheTime(String IFName) {
    	/**
    	 * 当不存在时，返回默认值。
    	 */
    	int time = 0 ;
    	
    	File file = new File(cacheConfig);
    	if(!file.exists()){
    		File path = new File(CacheFile.GetCachePath());
    		if(!path.exists())
    			if(!path.mkdirs())
    				return time ;
    		writeDefualtFile(cacheConfig);
    	}
    	FileInputStream fis = null ;
    	try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			return time ;
		}
    	Properties properties = new Properties();
    	if(fis != null){
    		try {
				properties.load(fis);
			} catch (IOException e) {
				return time ;
			}
    	}
    	
    	String ret = properties.getProperty(IFName);
    	
    	int retInt = time ;
    	try {
			retInt = Integer.parseInt(ret);
		} catch (NumberFormatException e) {
			return time;
		}
    	
    	return retInt ; 
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	if(intent != null){
    		Bundle extars = intent.getExtras();
			if (extars != null) {
				String userID = extars.getString("userID");
				Logger.i(TAG, "userID:" + userID);
				if (userID != null && !"".equals(userID.trim())) {
					CPManagerUtil.USER_ID = userID;
				}
				String lineNum = extars.getString("lineNum");
				Logger.i(TAG, "lineNum:" + lineNum);
				if (lineNum != null && !"".equals(lineNum.trim())) {
					CPManagerUtil.X_UP_CALLING_LINE_ID = lineNum;
				}
			}
    	}
    	return super.onStartCommand(intent, flags, startId);
    }
    /**
     * 取得要预取的内容页数 
	 * @param IFName 是指通信的接口名,可据此返回不同的页数
	 * @return 页数,值为零表示不预取
	 * 注意:对列表,实现中假定当前页的显示行数就是以后各页的显示行数
     */
    public static int GetNetCachePage(String IFName) {
    	return GetNetCacheTime(IFName + "Page");  //先假定是3页吧
    }
    
    
    public static String GetPath(String IFName, HashMap ahmNamePair) {
    	String path = IFName ; 
    	if (IFName.equals("getCatalogContent")){
    		path = IFName+File.separator+(String)ahmNamePair.get("catalogId");
    	}else if (IFName.equals("getBlockContent")){
    		path =  IFName+File.separator+(String)ahmNamePair.get("blockId");
    	}else if (IFName.equals("getCatalogRank")){
    		path =  IFName +File.separator + (String)ahmNamePair.get("catalogId") + File.separator + (String)ahmNamePair.get("rankType");
    	}else if (IFName.equals("getSpecifiedRank")){
    		path =  IFName +File.separator + (String)ahmNamePair.get("catalogId") + File.separator + (String)ahmNamePair.get("rankType");
    	}else if (IFName.equals("getContentInfo")){
    		path =  IFName +File.separator + (String)ahmNamePair.get("contentId");  // 必要的识别关键
    	}else if (IFName.equals("getChapterList")){
    		path =  IFName +File.separator + (String)ahmNamePair.get("contentId");  // 必要的识别关键
    	}else if (IFName.equals("getChapterInfo")){
    		path =  IFName +File.separator + (String)ahmNamePair.get("contentId");   // 必要的识别关键
    	}else if (IFName.equals("getChapterImage")){
    		path =  IFName +File.separator + (String)ahmNamePair.get("contentId");    // 必要的识别关键
    	}else if (IFName.equals("getAuthorInfo")){
    		path =  IFName +File.separator + (String)ahmNamePair.get("authorId");
    	}else if (IFName.equals("getBookNewsList")){
    		path =  IFName +File.separator + (String)ahmNamePair.get("catalogId");
    	}else if (IFName.equals("getRecommendContentList")){
    		path =  IFName +File.separator + (String)ahmNamePair.get("contentId") + File.separator + (String)ahmNamePair.get("type");
    	}else if (IFName.equals("getBlockContent")){
    		path =  IFName +File.separator + (String)ahmNamePair.get("blockId");
    	}else if (IFName.equals("downloadContent")){
    		path =  IFName +File.separator + (String)ahmNamePair.get("catalogId");
    	}else if (IFName.equals("downloadFreeContent")){
    		path =  IFName +File.separator + (String)ahmNamePair.get("catalogId");
    	}else if (IFName.equals("getSubscriptionList")){
    		path =  IFName +File.separator + (String)ahmNamePair.get("catalogId");
    	}
    	
    	return path ;
    }
    public static String GetSingleName(String IFName, HashMap ahmNamePair) {
    	String singleName = "" ;
    	if (IFName.equals("getCatalogContent")){
    		singleName = (String)ahmNamePair.get("start")+"_"+(String)ahmNamePair.get("count");
    	}else if (IFName.equals("getBlockContent")){
    		singleName = (String)ahmNamePair.get("start")+"_"+(String)ahmNamePair.get("count");
    	}else if (IFName.equals("getFriendList")){
    		singleName = (String)ahmNamePair.get("catalogId");
    	}else if (IFName.equals("getBlockList")){
    		singleName = (String)ahmNamePair.get("catalogId");
    	}else if (IFName.equals("getCatalogHomePage")){
    		singleName = (String)ahmNamePair.get("catalogId");
    	}else if (IFName.equals("getCatalogRank")){
    		singleName = (String)ahmNamePair.get("start")+"_"+(String)ahmNamePair.get("count");
    	}else if (IFName.equals("getSpecifiedRank")){
    		singleName = (String)ahmNamePair.get("rankTime") +"_" +  (String)ahmNamePair.get("start")+"_"+(String)ahmNamePair.get("count") +"_" + (String)ahmNamePair.get("blockId");
    	}else if (IFName.equals("getContentInfo")){
    		singleName = (String)ahmNamePair.get("catalogId") + "_" + (String)ahmNamePair.get("count"); // 这两参数都是可选的
    		if(ahmNamePair.get("catalogId") == null) singleName = (String)ahmNamePair.get("contentId");
    	}else if (IFName.equals("getChapterList")){
    		singleName = (String)ahmNamePair.get("start") + "_" + (String)ahmNamePair.get("count") + "_" + (String)ahmNamePair.get("sortType");// 已经确定书籍了
    	}else if (IFName.equals("getChapterInfo")){
    		singleName = (String)ahmNamePair.get("catalogId") + "_" + (String)ahmNamePair.get("chapterId"); // 确定了书本了.
    	}else if (IFName.equals("getChapterImage")){
    		singleName = (String)ahmNamePair.get("catalogId") + "_" + (String)ahmNamePair.get("chapterId") + "_" + (String)ahmNamePair.get("src"); //已经确定书籍了
    	}else if (IFName.equals("getAuthorInfo")){
    		singleName = (String)ahmNamePair.get("start") + "_" + (String)ahmNamePair.get("count");
    	}else if (IFName.equals("getBookNewsList")){
    		singleName = (String)ahmNamePair.get("start") + "_" + (String)ahmNamePair.get("count");
    	}else if (IFName.equals("getBookNewsInfo")){
    		singleName = (String)ahmNamePair.get("bookNewsID");
    	}else if (IFName.equals("getRecommendContentList")){
    		singleName = (String)ahmNamePair.get("start") + "_" + (String)ahmNamePair.get("count");
    	}else if (IFName.equals("getBlockContent")){
    		singleName = (String)ahmNamePair.get("start") + "_" + (String)ahmNamePair.get("count");
    	}else if (IFName.equals("downloadContent")){
    		singleName = (String)ahmNamePair.get("contentId");
    	}else if (IFName.equals("downloadFreeContent")){
    		singleName = (String)ahmNamePair.get("contentId");
    	}else if (IFName.equals("getSubscriptionList")){
    		singleName = (String)ahmNamePair.get("start") + "_" + (String)ahmNamePair.get("count");
    	}else if (IFName.equals("getConsumeHistoryList")){
    		singleName = (String)ahmNamePair.get("begintime") + "_" + (String)ahmNamePair.get("endtime") 
    		+ "_" +  (String)ahmNamePair.get("start") + "_" + (String)ahmNamePair.get("end");
    	}else if (IFName.equals("getHandsetProperties")){
    		singleName = (String)ahmNamePair.get("type") ;
    	}else if (IFName.equals("getBeShelvesBookList")){
    		singleName = (String)ahmNamePair.get("start") + "_" + (String)ahmNamePair.get("count");
    	}else if (IFName.equals("getMessage")){
    		singleName = (String)ahmNamePair.get("isDelete") + "_" + (String)ahmNamePair.get("isRead") 
    		+ "_" +  (String)ahmNamePair.get("start") + "_" + (String)ahmNamePair.get("count");
    	}else if (IFName.equals("getUserBookmark")){
    		singleName = (String)ahmNamePair.get("start") + "_" + (String)ahmNamePair.get("end");
    	}else if (IFName.equals("getContentBookmark")){
    		singleName = (String)ahmNamePair.get("contentId");
    	}else if (IFName.equals("getFavorite")){
    		singleName = (String)ahmNamePair.get("start") + "_" + (String)ahmNamePair.get("end");
    	}else if (IFName.equals("bookUpdate")){
    		singleName = (String)ahmNamePair.get("contentId") ;
    	}else if (IFName.equals("getAllSerialChapters")){
    		singleName = (String)ahmNamePair.get("contentId") + "_" + 
    		(String)ahmNamePair.get("start") + "_" + (String)ahmNamePair.get("end");
    	}else if (IFName.equals("subscribeChapters")){
    		singleName = (String)ahmNamePair.get("contentId") + "_" + 
    		(String)ahmNamePair.get("ChapterIdList") + "_" + (String)ahmNamePair.get("chapterId")+ 
    		"_" + (String)ahmNamePair.get("transactionId");
    	}else if (IFName.equals("batchSubscribeNextChapters")){
    		singleName = (String)ahmNamePair.get("contentId") + "_" + 
    		(String)ahmNamePair.get("currentChapterId");
    	}else if (IFName.equals("getCatalogProductInfo")){
    		singleName = (String)ahmNamePair.get("contentId") + "_" + 
    		(String)ahmNamePair.get("currentChapterId");
    	}else if (IFName.equals("subscribeContent")){
    		singleName = (String)ahmNamePair.get("productId") + "_" + 
    		(String)ahmNamePair.get("contentId") + "_" + (String)ahmNamePair.get("chapterId") 
    		+ "_" + (String)ahmNamePair.get("catalogId")+ "_" + (String)ahmNamePair.get("fascicleId")  ;
    	}else if (IFName.equals("subscribeCatalog")){
    		singleName = (String)ahmNamePair.get("catalogId")  ;
    	}else if (IFName.equals("getContentProductInfo")){
    		singleName = (String)ahmNamePair.get("catalogId") + "_" + (String)ahmNamePair.get("chapterId")  ;
    	}else if (IFName.equals("unbookUpdate")){
    		singleName = (String)ahmNamePair.get("contentId");
    	}else if (IFName.equals("getBookUpdateList")){
    		singleName = (String)ahmNamePair.get("start") + "_" + (String)ahmNamePair.get("count");
    	}else if (IFName.equals("getChaptersUrl")){
    		singleName = (String)ahmNamePair.get("contentId") + "_" + (String)ahmNamePair.get("start") 
    		+ "_"+ (String)ahmNamePair.get("count") + "_" + (String)ahmNamePair.get("startChapterId");
    	}else if (IFName.equals("bookUpdateSet")){
    		singleName = (String)ahmNamePair.get("type");
    	}else if (IFName.equals("unsubscribeCatalog")){
    		singleName = (String)ahmNamePair.get("catalogId");
    	}else if (IFName.equals("getComment")){
    		singleName = (String)ahmNamePair.get("contentId") + "_" +
    		(String)ahmNamePair.get("start") + "_" + (String)ahmNamePair.get("count");
    	}else if (IFName.equals("submitComment")){
    		singleName = (String)ahmNamePair.get("contentId") + "_" +
    		(String)ahmNamePair.get("count") + "_" + (String)ahmNamePair.get("comment");
    	}else if (IFName.equals("submitVote")){
    		singleName = (String)ahmNamePair.get("contentId") + "_" +
    		(String)ahmNamePair.get("vote");
    	}else if (IFName.equals("recommendedContent")){
    		singleName = (String)ahmNamePair.get("contentID") + "_" +
    		(String)ahmNamePair.get("chapterID") + "_" + 
    		(String)ahmNamePair.get("msisdn") + "_" +(String)ahmNamePair.get("message") ;
    	}else if (IFName.equals("getFriendInfo")){
    		singleName = (String)ahmNamePair.get("msisdn")  ;
    	}else{
    		singleName = "" + System.currentTimeMillis();
    	}
    	
    	return singleName + ".tmp";
    }
    public static String GetPathName(String IFName, HashMap ahmNamePair) {
    	String path = GetPath(IFName,ahmNamePair);
    	String name = GetSingleName(IFName,ahmNamePair);
    	if ((path != null) && (name != null))
    		return path+File.separator+name;
    	else
    		return null;
    }
	public static String PathName4url(String url) {
		if (url.startsWith("http://"))
			url = url.substring("http://".length());
		int index = url.indexOf(':');
		if (index != -1)
			url = url.substring(0, index) + File.separator
					+ url.substring(index + 1);
		return url;
	}
	
     protected static int ResponseCode(HashMap response) {
    	 if(response == null || response.get("result-code") == null){
    		 return -1;
    	 }
		String resp = response.get("result-code").toString();
		int code = -1;
		int i = "result-code:".length();
		char c = resp.charAt(i);
		while (c == ' ') c = resp.charAt(++i);
		if ((c >= '0') && (c <= '9')) {
			code = 0;
			do {
				code = code*10+(c-'0');
				c = resp.charAt(++i);
			} while (c != '\r');
		}
		return code;
    }

     protected void deleteWirelessList() {
 		delete(CacheFile.GetCachePath() + File.separator + "getBlockList");
		delete(CacheFile.GetCachePath() + File.separator + "getCatalogHomePage");
		delete(CacheFile.GetCachePath() + File.separator + "getCatalogHomePage2");
		delete(CacheFile.GetCachePath() + File.separator + "getCatalogContent");
		delete(CacheFile.GetCachePath() + File.separator + "getCatalogRank");
		delete(CacheFile.GetCachePath() + File.separator + "getSpecifiedRank");
		delete(CacheFile.GetCachePath() + File.separator + "getRecommendContentList");
		delete(CacheFile.GetCachePath() + File.separator + "getBookNewsList");
		delete(CacheFile.GetCachePath() + File.separator + "getBookNewsInfo");
		delete(CacheFile.GetCachePath() + File.separator + "getBlockContent");
		delete(CacheFile.GetCachePath() + File.separator + "getRankType");
		delete(CacheFile.GetCachePath() + File.separator + "getHandsetBroadcast");
		delete(CacheFile.GetCachePath() + File.separator + "getAuthorInfo");
     }
 	private void deleteWirelessCache(){
		deleteWirelessList();
		delete(CacheFile.GetCachePath() + File.separator + "getContentInfo");
		delete(CacheFile.GetCachePath() + File.separator + "getChapterList");
		delete(CacheFile.GetCachePath() + File.separator + "getChapterInfo");
		delete(CacheFile.GetCachePath() + File.separator + "getChapterImage");
	}
     protected void deleteWirelessBook(String IFName, HashMap ahmNamePair) {
			CacheFile.delete(GetPath("getContentInfo", ahmNamePair));
			CacheFile.delete(GetPath("getChapterList", ahmNamePair));
			CacheFile.delete(GetPath("getChapterInfo", ahmNamePair));
			CacheFile.delete(GetPath("getChapterImage", ahmNamePair));
     }
	protected HashMap normal(String IFName, HashMap ahmHeaderMap,
			HashMap ahmNamePair){
		Logger.i(TAG + ":" +  IFName,"ahmHeaderMap" + ahmHeaderMap);
		Logger.i(TAG + ":" + IFName, "ahmNamePair" + ahmNamePair);
		final String cardType = this.getSimType();
		if ((cardType != null)
				&& (cardType.equalsIgnoreCase("sim") || cardType
						.equalsIgnoreCase("usim"))) {
			CPConnection.sdcard = cardType;
			FileDownloadThread.simType = cardType ;
		}
		HashMap ret = null;
		int cachetime = GetNetCacheTime(IFName);
		if (cachetime > 0) {
			if (ahmNamePair.containsKey("reflash")) {
				String reflash = (String)ahmNamePair.get("reflash");
				ahmNamePair.remove("reflash");
				if (reflash.equals("OnlyIF"));
//					CacheFile.delete(GetPath(IFName, ahmNamePair));
				else if (reflash.equals("WirelessList"))
					deleteWirelessList();
				else if (reflash.equals("CurrentBook"))
					deleteWirelessBook(IFName, ahmNamePair);
			} else {
				ret = CacheFile.read(GetPathName(IFName, ahmNamePair));
			}


		}
		if (ret == null) {
			ret = reflectionMethod(IFName, ahmHeaderMap, ahmNamePair);
			if(ResponseCode(ret) == 0){
//				ret.remove("DataFrom");
				CacheFile.write(GetPathName(IFName, ahmNamePair), ret,cachetime);
			}
			
		}
		else{
			Logger.i(TAG + ":" + IFName, "return netcatch" + ret);
			ret.put("DataFrom", "file");
		}
		if ((ResponseCode(ret) == 0) && (cachetime > 0)) {
			Logger.i(TAG + IFName, IFName + ":prefetch:"+cachetime);
			// Prefetch.addTask(getApplicationContext(),IFName, 1,ahmHeaderMap, ahmNamePair);
			if (prefetch != null){
				if(preset(IFName, ahmHeaderMap, ahmNamePair)){
					prefetch.addTask(IFName, ahmHeaderMap, ahmNamePair);
				}else{
					Logger.i(TAG, "preset fail");
				}
			}
		}
		return ret;
	}

	public HashMap reflectionMethod(String IFName, HashMap ahmHeaderMap,
			HashMap ahmNamePair) {
		HashMap ret = null ;
		Logger.i(TAG, IFName + "invoke");
		try {
			Class c = Class
					.forName("com.pvi.ap.reader.data.external.manager.CPManager");
			Object o = c.newInstance();
			Method m = c.getMethod(IFName,new Class[] { Class.forName("java.util.HashMap"),
							Class.forName("java.util.HashMap") });
			Object temp = m.invoke(o, ahmHeaderMap, ahmNamePair);
			System.out.println(temp);
			if (temp instanceof HashMap) {
				ret = (HashMap) temp;
			}else{
				ret = new HashMap();
			}
		} catch (InvocationTargetException e) {
			Logger.e(TAG, Logger.getStackTraceString(e));
			Throwable err = e.getTargetException();
			ret = new HashMap();
			if (err instanceof Exception) {
				ret.put("Exception", (Exception)err);
				return ret;
			}
		} catch (Exception e) {
			Logger.e(TAG, Logger.getStackTraceString(e));
			ret = new HashMap();
			ret.put("Exception", e);
		}
		ret.put("DataFrom", "net");
		return ret;
	}

	public class NetCacheServicer extends INetCache.Stub {
		
		public NetCacheServicer(){
			Logger.i(TAG, "NetCacheServicer init");
		}

		@Override
		public Bitmap GetNetImage(String url){
			Logger.i(TAG, url);
			Bitmap bmp = CacheFile.readbmp(PathName4url(url));
			if(url != null){
				url = FileDownloadService.encodeURI(url);
			}
			Logger.i(TAG, url);
			if (bmp == null) {
				try {
					URL ImageUrl = new URL(url);
					URLConnection conn = null;
					if("sim".equalsIgnoreCase(getSimType())){
						SocketAddress addr = new InetSocketAddress(Config.getString("proxyIP"),Integer.parseInt(Config.getString("port")));
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
					CacheFile.write(PathName4url(url), bmp, GetNetCacheTime("GetNetImage"));
				} catch (Exception e) {
					Logger.e(TAG, e);
					return null;
				}
			}
			return bmp;
		}

		@Override
		public String getServerTimeAsString() {
			return CPManager.getServerTimeAsString();
		}

		public	java.util.Date getServerTimeAsDate() {
			return null;
		}

		public	long getServerTimeAsTimes() {
			return 0;
		}
		
		@Override
		public HashMap getUserInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)
		{
			if ("".equals(CPConnection.sdcard.trim())) {
				CPConnection.sdcard = (String) ahmHeaderMap.get("sdcard");
				FileDownloadThread.simType = CPConnection.sdcard;
			}
			return normal("getUserInfo", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap modifyUserInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			HashMap ret = normal("modifyUserInfo", ahmHeaderMap, ahmNamePair);
			if (ResponseCode(ret) == 0)
				CacheFile.delete("getUserInfo");
			return ret;
		}

		@Override
		public HashMap addFriend(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			HashMap ret = normal("addFriend", ahmHeaderMap, ahmNamePair);
			if (ResponseCode(ret) == 0)
				CacheFile.delete("getFriendList");
			return ret;
		}

		@Override
		public HashMap getFriendList(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getFriendList", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getBlockList(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getBlockList", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getCatalogHomePage(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getCatalogHomePage", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getCatalogHomePage2(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getCatalogHomePage2", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getCatalogContent(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getCatalogContent", ahmHeaderMap, ahmNamePair);



		}

		@Override
		public HashMap getCatalogRank(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getCatalogRank", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getSpecifiedRank(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getSpecifiedRank", ahmHeaderMap, ahmNamePair);
		}



		@Override
		public HashMap getContentInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getContentInfo", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getChapterList(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getChapterList", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getChapterInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getChapterInfo", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getChapterImage(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getChapterImage", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getAuthorInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getAuthorInfo", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getBookNewsList(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getBookNewsList", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getBookNewsInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getBookNewsInfo", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getRecommendContentList(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getRecommendContentList", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getBlockContent(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getBlockContent", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getRankType(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getRankType", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap downloadContent(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("downloadContent", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap downloadFreeContent(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("downloadFreeContent", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getSubscriptionList(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getSubscriptionList", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getConsumeHistoryList(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getConsumeHistoryList", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getFascicleList(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getFascicleList", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getHandsetProperties(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getHandsetProperties", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap addSystemBookmark(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("addSystemBookmark", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap addUserBookmark(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("addUserBookmark", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap deleteAllSystemBookmark(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("deleteAllSystemBookmark", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap deleteAllUserBookmark(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("deleteAllUserBookmark", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap deleteBookmark(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("deleteBookmark", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap deleteContentBookmark(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("deleteContentBookmark", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap deleteMessage(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			CacheFile.delete("getMessage");
			return normal("deleteMessage", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getBeShelvesBookList(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getBeShelvesBookList", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getHandsetBroadcast(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getHandsetBroadcast", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getMessage(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getMessage", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getResources(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getResources", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getSystemParameter(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getSystemParameter", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getUserBookmark(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getUserBookmark", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap searchContent(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("searchContent", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap sendMessage(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			CacheFile.delete("getMessage");
			return normal("sendMessage", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getContentBookmark(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getContentBookmark", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getFavorite(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getFavorite", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap addFavorite(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("addFavorite", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap deleteFavorite(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("deleteFavorite", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap deleteAllFavorite(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("deleteAllFavorite", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getSystemBookmark(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getSystemBookmark", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap bookUpdate(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("bookUpdate", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getAllSerialChapters(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getAllSerialChapters", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap subscribeChapters(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("subscribeChapters", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap batchSubscribeNextChapters(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("batchSubscribeNextChapters", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getCatalogProductInfo(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getCatalogProductInfo", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap subscribeContent(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("subscribeContent", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap subscribeCatalog(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("subscribeCatalog", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getContentProductInfo(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getContentProductInfo", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap unbookUpdate(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("unbookUpdate", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getBookUpdateList(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getBookUpdateList", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getChaptersUrl(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getChaptersUrl", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap bookUpdateSet(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("bookUpdateSet", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap unsubscribeCatalog(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("unsubscribeCatalog", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getCatalogSubscriptionList(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getCatalogSubscriptionList", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getComment(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getComment", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap submitComment(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("submitComment", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap submitVote(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("submitVote", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap recommendedContent(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("recommendedContent", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap syncMessageState(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("syncMessageState", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap uploadReadRecord(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("uploadReadRecord", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap uploadClientLog(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("uploadClientLog", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap syncBookmark(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("syncBookmark", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap handsetAssociate(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			String iFrame = "handsetAssociate" ;
			int bindSucess = 3200 ;
			String photo = (String)ahmHeaderMap.get("photoNum");
			ahmHeaderMap.remove("photoNum");
			HashMap ret = normal(iFrame, ahmHeaderMap, ahmNamePair) ; 
			if(bindSucess == ResponseCode(ret)){
				File path = new File(CacheFile.GetCachePath());
				if(!path.exists()){
					path.mkdirs();
				}
				File file = new File(CacheFile.GetCachePath() + File.separator + iFrame);
				delete(CacheFile.GetCachePath());
//				if(!file.exists()){
					writePhoneFile(photo, file);
//				}else{
//					BufferedReader br = null ;
//					try{
//					 br = new BufferedReader(new FileReader(file));
//					String  oldPhoto = br.readLine();
//					if(!photo.equals(oldPhoto.trim())){
//						writePhotoFile(photo, file);
//						delete(CacheFile.GetCachePath());
//					}
//					}catch(Exception e){
//						e.printStackTrace();
//					}finally{
//						if(br != null){
//							try {
//								br.close();
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//					}
//				}
			}
			return ret;
		}

		private void writePhoneFile(String phone, File file) {
			FileWriter fw = null;
			try {
				fw = new FileWriter(file);
				fw.write(phone);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(fw != null){
					try{
						fw.close();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public HashMap handsetAuthenticate(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			CPConnection.sdcard = (String)ahmHeaderMap.get("sdcard");
			FileDownloadThread.simType = CPConnection.sdcard ;
			return normal("handsetAuthenticate", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getSMSVerifyCode(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getSMSVerifyCode", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap quit(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("quit", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap checkUpdate(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("checkUpdate", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap register(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("register", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getBindState(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getBindState", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap resend(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("resend", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getFriendInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getFriendInfo", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap deleteFriend(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("deleteFriend", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getUnconfirmedFriendList(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getUnconfirmedFriendList", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getHandsetInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("getHandsetInfo", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap unbindHandset(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("unbindHandset", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getHandsetUserTicketInfo(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getHandsetUserTicketInfo", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap presentBook(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("presentBook", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getPresentBookInfo(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getPresentBookInfo", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap getPresentBookList(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("getPresentBookList", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap downloadContentAck(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("downloadContentAck", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap userOrderBook(HashMap ahmHeaderMap, HashMap ahmNamePair)
				{
			return normal("userOrderBook", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap addUserLeaveWord(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("addUserLeaveWord", ahmHeaderMap, ahmNamePair);
		}

		@Override
		public HashMap submitCommentVote(HashMap ahmHeaderMap,
				HashMap ahmNamePair) {
			return normal("submitCommentVote", ahmHeaderMap, ahmNamePair);
		}
	}

	
	private static void writeDefualtFile(String path) {
		defualtValue = new ArrayList<String>();
		/**** 添加默认值 ****/
		defualtValue.add("GetNetImage=10080\n");
		defualtValue.add("getBlockList=10080\n");
		defualtValue.add("getCatalogHomePage=10080\n");
		defualtValue.add("getCatalogHomePage2=10080\n");
		defualtValue.add("getCatalogContent=10080\n");
		defualtValue.add("getCatalogRank=10080\n");
		defualtValue.add("getSpecifiedRank=10080\n");
		defualtValue.add("getBlockContent=10080\n");
		defualtValue.add("getRankType=10080\n");
		defualtValue.add("getHandsetBroadcast=10080\n");
		defualtValue.add("getChapterInfo=10080\n");
		defualtValue.add("getChapterList=10080\n");
		defualtValue.add("getAuthorInfo=10080\n");
		defualtValue.add("getContentInfo=10080\n");
//		defualtValue.add("getBlockListPage=0\n");
//		defualtValue.add("getCatalogHomePagePage=0\n");
//		defualtValue.add("getCatalogHomePage2Page=0\n");
		defualtValue.add("getCatalogContentPage=5\n");
//		defualtValue.add("getCatalogRankPage=0\n");
		defualtValue.add("getSpecifiedRankPage=5\n");
		defualtValue.add("getBlockContentPage=5\n");
//		defualtValue.add("getRankTypePage=0\n");
//		defualtValue.add("getHandsetBroadcastPage=0\n");
		defualtValue.add("getChapterInfoPage=1\n");
		File file = new File(path);
		FileWriter fw = null;
		try {

			fw = new FileWriter(file);
			for (int i = 0; i < defualtValue.size(); i++) {
				String value = defualtValue.get(i);
				fw.write(value);
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private static List<String> defualtValue = null; 
	
	
	
	@Override
	public void onDestroy() {
		unregisterReceiver(sdr);
		sdr = null ;
		super.onDestroy();
	}
	
	
	class ShutDownReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(Intent.ACTION_SHUTDOWN) 
					|| action.equals(Intent.ACTION_SCREEN_OFF)
					|| action.equals("com.pvi.ap.reader.unblind.interface")){
				System.out.println("divice is" + action);
				deleteWirelessCache();
				
			}
			
		}
		
	}
	
	private ShutDownReceiver sdr = null ;
	
	private void registBroadcaseReceiver(){
		if(sdr == null)
		{
			sdr = new ShutDownReceiver();
			IntentFilter iFilter = new IntentFilter();
			iFilter.addAction(Intent.ACTION_SHUTDOWN);
			iFilter.addAction(Intent.ACTION_SCREEN_OFF);
			iFilter.addAction("com.pvi.ap.reader.unblind.interface");
			registerReceiver(sdr, iFilter);
		}
	}
	
	
	/**
	 * 删除绝对路径名的文件或文件夹
	 * @param fileName 文件名或文件夹路径
	 * @return 是否成功完全删除
	 */
	private boolean delete(String fileName){
		File file = new File(fileName);
		return delete(file);
	}
	
	/**
	 * 删除指定的文件或文件夹
	 * @param fileName 文件
	 * @return 是否成功完全删除
	 */
	private boolean delete(File fileName){
		if(!fileName.exists()){
			return false ;
		}
		if(fileName.isFile()){
			return fileName.delete();
			
		}
		if(fileName.isDirectory()){
			for(File file:fileName.listFiles()){
				delete(file);
			}
			return fileName.delete();
		}
		
		return false ;
	}
	
	public boolean preset(String IFName, HashMap ahmHeaderMap,HashMap ahmNamePair){
		Logger.i(TAG, "preset");
		List<String> list = new ArrayList<String>();
		readPresetInterface(list, IFName);
		for(int i = list.size() - 1 ; i>= 0; i--){
			String iname = list.get(i);
			HashMap ret  = reflectionMethod(iname, ahmHeaderMap, ahmNamePair);
			if(ResponseCode(ret) != 0){
				return false ;
			}
		}
		return true ;
	}
	
	/**
	 * 读取xml文件，判断传入的接口是否有预制接口，有的话，保存到list中。
	 * 直到读取到最后没有预制接口为止。
	 */
	public void readPresetInterface(List<String>  list,String IFName){
		
		String preset = "Preset" ;
		File file = new File(cacheConfig);
    	if(!file.exists()){
    		return ;
    	}
    	FileInputStream fis = null ;
    	try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
		}
    	Properties properties = new Properties();
    	if(fis != null){
    		try {
				properties.load(fis);
			} catch (IOException e) {
			}
    	}
    	
    	String ret = "";
    	while(ret != null){
    		ret = properties.getProperty(IFName + preset);
    		/**
    		 * 防止成环形链,当为环形链时，取一个最小环
    		 */
    		if(list.contains(ret)){
    			break ;
    		}
    		if(ret != null && !"".equals(ret)){
    			list.add(ret);
    			IFName = ret ;
    		}else{
    			ret = null ;
    		}
    	}
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
}



