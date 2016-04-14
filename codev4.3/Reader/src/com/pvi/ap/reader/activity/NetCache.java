package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpException;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.service.INetCache;

public class NetCache {
	private static String TAG = "NetCache" ;
	private static INetCache m_NetCache = null;
	private static Context holder = null;
	private static ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			if(service != null){
				Logger.i(TAG, service.getClass().getName());
			}
			m_NetCache = INetCache.Stub.asInterface(service);
			if(m_NetCache == null){
				Logger.i(TAG, "bind netCatch service fail");
			}else{
				Logger.i(TAG, "bind netCatch service sucess");
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			m_NetCache = null;
		}
	};

	public static void start(Context holder) {
		NetCache.holder = holder;
		Logger.i(TAG, "bind netCatch service start...");
		Intent serviceIntent = new Intent("com.pvi.ap.reader.service.NetCacheService");
		holder.startService(serviceIntent);
		holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		Logger.i(TAG, "bind netCatch service end...");
	}

	// type 1
	public static android.graphics.Bitmap GetNetImage(String url)
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return null;
		android.graphics.Bitmap ret = null;
		try {
			ret = m_NetCache.GetNetImage(url);
		} catch (Error e) {
			Logger.e(TAG, "hehehheheh");
		} catch(Exception e){
			Logger.e(TAG, e);
		}
		return ret;
	}
	public static String getServerTimeAsString()
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return null;
		return m_NetCache.getServerTimeAsString();
	}
	public static java.util.Date getServerTimeAsDate() 
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return null;
		return m_NetCache.getServerTimeAsDate();
	}
	public static long getServerTimeAsTimes()
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return 0;
		return m_NetCache.getServerTimeAsTimes();// (HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException;
	}

	// type 2
	public static HashMap getUserInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getUserInfo(ahmHeaderMap,ahmNamePair);
		return m_NetCache.getUserInfo(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap modifyUserInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.modifyUserInfo(ahmHeaderMap, ahmNamePair);
		return m_NetCache.modifyUserInfo(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap addFriend(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.addFriend(ahmHeaderMap, ahmNamePair);
		return m_NetCache.addFriend(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getFriendList(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getFriendList(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getFriendList(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getBlockList(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getBlockList(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getBlockList(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getCatalogHomePage(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getCatalogHomePage(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getCatalogHomePage(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getCatalogHomePage2(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getCatalogHomePage(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getCatalogHomePage2(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getCatalogContent(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getCatalogContent(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getCatalogContent(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getCatalogRank(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getCatalogRank(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getCatalogRank(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getSpecifiedRank(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getSpecifiedRank(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getSpecifiedRank(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getContentInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getContentInfo(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getContentInfo(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getChapterList(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getChapterList(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getChapterList(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getChapterInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getChapterInfo(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getChapterInfo(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getChapterImage(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getChapterImage(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getChapterImage(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getAuthorInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getAuthorInfo(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getAuthorInfo(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getBookNewsList(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getBookNewsList(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getBookNewsList(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getBookNewsInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getBookNewsInfo(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getBookNewsInfo(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getRecommendContentList(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getRecommendContentList(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getRecommendContentList(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getBlockContent(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getBlockContent(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getBlockContent(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getRankType(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getRankType(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getRankType(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap downloadContent(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.downloadContent(ahmHeaderMap, ahmNamePair);
		return m_NetCache.downloadContent(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap downloadFreeContent(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.downloadFreeContent(ahmHeaderMap, ahmNamePair);
		return m_NetCache.downloadFreeContent(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getSubscriptionList(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getSubscriptionList(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getSubscriptionList(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getConsumeHistoryList(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getConsumeHistoryList(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getConsumeHistoryList(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getFascicleList(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getFascicleList(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getFascicleList(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getHandsetProperties(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getHandsetProperties(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getHandsetProperties(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap addSystemBookmark(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.addSystemBookmark(ahmHeaderMap, ahmNamePair);
		return m_NetCache.addSystemBookmark(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap addUserBookmark(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.addUserBookmark(ahmHeaderMap, ahmNamePair);
		return m_NetCache.addUserBookmark(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap deleteAllSystemBookmark(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.deleteAllSystemBookmark(ahmHeaderMap, ahmNamePair);
		return m_NetCache.deleteAllSystemBookmark(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap deleteAllUserBookmark(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.deleteAllUserBookmark(ahmHeaderMap, ahmNamePair);
		return m_NetCache.deleteAllUserBookmark(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap deleteBookmark(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.deleteBookmark(ahmHeaderMap, ahmNamePair);
		return m_NetCache.deleteBookmark(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap deleteContentBookmark(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.deleteContentBookmark(ahmHeaderMap, ahmNamePair);
		return m_NetCache.deleteContentBookmark(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap deleteMessage(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.deleteMessage(ahmHeaderMap, ahmNamePair);
		return m_NetCache.deleteMessage(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getBeShelvesBookList(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getBeShelvesBookList(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getBeShelvesBookList(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getHandsetBroadcast(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getHandsetBroadcast(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getHandsetBroadcast(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getMessage(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getMessage(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getMessage(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getResources(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getResources(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getResources(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getSystemParameter(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getSystemParameter(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getSystemParameter(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getUserBookmark(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getUserBookmark(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getUserBookmark(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap searchContent(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.searchContent(ahmHeaderMap, ahmNamePair);
		return m_NetCache.searchContent(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap sendMessage(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.sendMessage(ahmHeaderMap, ahmNamePair);
		return m_NetCache.sendMessage(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getContentBookmark(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getContentBookmark(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getContentBookmark(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getFavorite(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getFavorite(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getFavorite(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap addFavorite(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.addFavorite(ahmHeaderMap, ahmNamePair);
		return m_NetCache.addFavorite(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap deleteFavorite(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.deleteFavorite(ahmHeaderMap, ahmNamePair);
		return m_NetCache.deleteFavorite(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap deleteAllFavorite(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.deleteAllFavorite(ahmHeaderMap, ahmNamePair);
		return m_NetCache.deleteAllFavorite(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getSystemBookmark(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getSystemBookmark(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getSystemBookmark(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap bookUpdate(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.bookUpdate(ahmHeaderMap, ahmNamePair);
		return m_NetCache.bookUpdate(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getAllSerialChapters(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getAllSerialChapters(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getAllSerialChapters(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap subscribeChapters(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.subscribeChapters(ahmHeaderMap, ahmNamePair);
		return m_NetCache.subscribeChapters(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap batchSubscribeNextChapters(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.batchSubscribeNextChapters(ahmHeaderMap, ahmNamePair);
		return m_NetCache.batchSubscribeNextChapters(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getCatalogProductInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getCatalogProductInfo(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getCatalogProductInfo(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap subscribeContent(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.subscribeContent(ahmHeaderMap, ahmNamePair);
		return m_NetCache.subscribeContent(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap subscribeCatalog(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.subscribeCatalog(ahmHeaderMap, ahmNamePair);
		return m_NetCache.subscribeCatalog(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getContentProductInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getContentProductInfo(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getContentProductInfo(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap unbookUpdate(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.unbookUpdate(ahmHeaderMap, ahmNamePair);
		return m_NetCache.unbookUpdate(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getBookUpdateList(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getBookUpdateList(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getBookUpdateList(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getChaptersUrl(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getChaptersUrl(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getChaptersUrl(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap bookUpdateSet(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.bookUpdateSet(ahmHeaderMap, ahmNamePair);
		return m_NetCache.bookUpdateSet(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap unsubscribeCatalog(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.unsubscribeCatalog(ahmHeaderMap, ahmNamePair);
		return m_NetCache.unsubscribeCatalog(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getCatalogSubscriptionList(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getCatalogSubscriptionList(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getCatalogSubscriptionList(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getComment(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getComment(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getComment(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap submitComment(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.submitComment(ahmHeaderMap, ahmNamePair);
		return m_NetCache.submitComment(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap submitVote(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.submitVote(ahmHeaderMap, ahmNamePair);
		return m_NetCache.submitVote(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap recommendedContent(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.recommendedContent(ahmHeaderMap, ahmNamePair);
		return m_NetCache.recommendedContent(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap syncMessageState(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.syncMessageState(ahmHeaderMap, ahmNamePair);
		return m_NetCache.syncMessageState(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap uploadReadRecord(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.uploadReadRecord(ahmHeaderMap, ahmNamePair);
		return m_NetCache.uploadReadRecord(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap uploadClientLog(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.uploadClientLog(ahmHeaderMap, ahmNamePair);
		return m_NetCache.uploadClientLog(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap syncBookmark(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.syncBookmark(ahmHeaderMap, ahmNamePair);
		return m_NetCache.syncBookmark(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap handsetAssociate(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.handsetAssociate(ahmHeaderMap, ahmNamePair);
		return m_NetCache.handsetAssociate(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap handsetAuthenticate(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.handsetAuthenticate(ahmHeaderMap, ahmNamePair);
		return m_NetCache.handsetAuthenticate(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getSMSVerifyCode(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getSMSVerifyCode(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getSMSVerifyCode(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap quit(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.quit(ahmHeaderMap, ahmNamePair);
		return m_NetCache.quit(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap checkUpdate(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.checkUpdate(ahmHeaderMap, ahmNamePair);
		return m_NetCache.checkUpdate(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap register(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.register(ahmHeaderMap, ahmNamePair);
		return m_NetCache.register(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getBindState(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getBindState(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getBindState(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap resend(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.resend(ahmHeaderMap, ahmNamePair);
		return m_NetCache.resend(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getFriendInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getFriendInfo(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getFriendInfo(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap deleteFriend(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.deleteFriend(ahmHeaderMap, ahmNamePair);
		return m_NetCache.deleteFriend(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getUnconfirmedFriendList(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getUnconfirmedFriendList(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getUnconfirmedFriendList(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getHandsetInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getHandsetInfo(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getHandsetInfo(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap unbindHandset(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.unbindHandset(ahmHeaderMap, ahmNamePair);
		return m_NetCache.unbindHandset(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getHandsetUserTicketInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getHandsetUserTicketInfo(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getHandsetUserTicketInfo(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap presentBook(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.presentBook(ahmHeaderMap, ahmNamePair);
		return m_NetCache.presentBook(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getPresentBookInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getPresentBookInfo(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getPresentBookInfo(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap getPresentBookList(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.getPresentBookList(ahmHeaderMap, ahmNamePair);
		return m_NetCache.getPresentBookList(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap downloadContentAck(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return null;
		return m_NetCache.downloadContentAck(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap userOrderBook(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.userOrderBook(ahmHeaderMap, ahmNamePair);
		return m_NetCache.userOrderBook(ahmHeaderMap,ahmNamePair);
	}
	public static HashMap addUserLeaveWord(HashMap ahmHeaderMap, HashMap ahmNamePair)  throws  HttpException, IOException
	{
		if (m_NetCache == null)holder.bindService(new Intent("com.pvi.ap.reader.service.NetCacheService" ), conn, Context.BIND_AUTO_CREATE);
		if (m_NetCache == null) return CPManager.addUserLeaveWord(ahmHeaderMap, ahmNamePair);
		return m_NetCache.addUserLeaveWord(ahmHeaderMap,ahmNamePair);
	}

}
