package com.pvi.ap.reader.data.external.manager;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.apache.commons.httpclient.HttpException;

import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.data.external.connection.CPConnection;

import android.util.Log;

/**
 * 内容平台接口<br>
 * 该类用来连接移动网络，并向外提供电子书业务接口
 * 
 * @author rd038
 * @since 2010-10-10
 * @version V1.0.0 (C)Copyright 2010-2013, by www.pvi.com.tw
 */

public class CPManager {

	/**
	 * 获取用户信息
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getUserInfo(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {

		ahmHeaderMap.put("Action", "getUserInfo");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Constants.ms_CPC_URL).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 修改用户信息
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap modifyUserInfo(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {

		ahmHeaderMap.put("Action", "modifyUserInfo");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doPost(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;

	}

	/**
	 * 增加好友
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap addFriend(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {

		ahmHeaderMap.put("Action", "addFriend");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;

	}

	/**
	 * 获取用户列表
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getFriendList(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {

		ahmHeaderMap.put("Action", "getFriendList");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;

	}

	/**
	 * 获取专区列表接口
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getBlockList(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getBlockList");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取频道专区首页
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getCatalogHomePage(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getCatalogHomePage");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取专区页面内容列表
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getCatalogContent(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getCatalogContent");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取专区排行榜首页
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getCatalogRank(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getCatalogRank");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取指定的排行榜信息
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getSpecifiedRank(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getSpecifiedRank");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取内容详情
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getContentInfo(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getContentInfo");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取内容目录
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getChapterList(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getChapterList");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取内容章节内容
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getChapterInfo(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getChapterInfo");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取内容章节内插图
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getChapterImage(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getChapterImage");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取书项作者信息
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getAuthorInfo(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getAuthorInfo");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取书讯列表
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getBookNewsList(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getBookNewsList");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取书讯详细信息
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getBookNewsInfo(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getBookNewsInfo");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取相关内容推荐信息
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getRecommendContentList(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getRecommendContentList");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取区块下内容列表
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getBlockContent(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getBlockContent");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取平台支持的排行榜类型信息
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getRankType(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getRankType");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 终端下载内容
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap downloadContent(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "downloadContent");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 下载免费章节
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap downloadFreeContent(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "downloadFreeContent");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doPost(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取按次订购内容列表
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getSubscriptionList(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getSubscriptionList");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取用户的订购的内容消费记录信息，按订购时间倒序排列
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getConsumeHistoryList(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getConsumeHistoryList");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取图书分册信息列表
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getFascicleList(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getFascicleList");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取终端资源内容
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getHandsetProperties(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getHandsetProperties");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		//Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
		//		+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 添加系统书签
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap addSystemBookmark(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "addSystemBookmark");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doPost(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 添加用户书签
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap addUserBookmark(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "addUserBookmark");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doPost(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 删除全部系统书签
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap deleteAllSystemBookmark(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "deleteAllSystemBookmark");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 删除全部用户书签
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap deleteAllUserBookmark(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "deleteAllUserBookmark");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 删除书签
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap deleteBookmark(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "deleteBookmark");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 删除内容全部书签
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap deleteContentBookmark(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "deleteContentBookmark");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 删除站内消息
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap deleteMessage(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "deleteMessage");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doPost(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取所有预上架的书
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getBeShelvesBookList(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getBeShelvesBookList");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取手持终端公告页面
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getHandsetBroadcast(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getHandsetBroadcast");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 终端获取站内消息
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getMessage(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getMessage");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取静态资源内容接口
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getResources(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getResources");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取系统的配置参数的信息。当前的书籍数量等
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getSystemParameter(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getSystemParameter");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 终端向服务器获取内容书签
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getUserBookmark(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getUserBookmark");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 内容搜索接口
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap searchContent(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "searchContent");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doPost(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 发送站内消息接口
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap sendMessage(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {
		ahmHeaderMap.put("Action", "sendMessage");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取内容全部用户书签
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getContentBookmark(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getContentBookmark");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取用户收藏的内容列表
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getFavorite(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getFavorite");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 增加用户收藏
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap addFavorite(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {
		ahmHeaderMap.put("Action", "addFavorite");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 用户删除收藏夹内容
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap deleteFavorite(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "deleteFavorite");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 用户删除收藏夹所有内容
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap deleteAllFavorite(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "deleteAllFavorite");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 终端向服务器获取内容书签。
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getSystemBookmark(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getSystemBookmark");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 预定内容更新
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap bookUpdate(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {
		ahmHeaderMap.put("Action", "bookUpdate");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取所有的未订购连载章节列表和响应的资费信息
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getAllSerialChapters(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getAllSerialChapters");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 多章节订购接口，订购请求的章节列表
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap subscribeChapters(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "subscribeChapters");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doPost(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 多章节订购接口,数目有平台统一配置 现默认为10章
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap batchSubscribeNextChapters(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "batchSubscribeNextChapters");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取包月栏目资费信息
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getCatalogProductInfo(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getCatalogProductInfo");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 订购内容
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap subscribeContent(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "subscribeContent");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 订购包月专区
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap subscribeCatalog(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "subscribeCatalog");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取内容产品信息接口
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getContentProductInfo(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getContentProductInfo");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 取消预定内容更新
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap unbookUpdate(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {
		ahmHeaderMap.put("Action", "unbookUpdate");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取预定内容更新列表
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getBookUpdateList(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getBookUpdateList");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取所有的连载章节的下载URL
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getChaptersUrl(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getChaptersUrl");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 预订更新方式设置
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap bookUpdateSet(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "bookUpdateSet");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 取消订购专区栏目
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap unsubscribeCatalog(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "unsubscribeCatalog");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取消费记录接口
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getCatalogSubscriptionList(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getCatalogSubscriptionList");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取用户评论内容接口，评论结果按照评论时间先后排序
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getComment(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getComment");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 用户对内容发表评论
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap submitComment(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "submitComment");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 用户投票
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap submitVote(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {
		ahmHeaderMap.put("Action", "submitVote");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 向好友推荐进行内容推荐
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap recommendedContent(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "recommendedContent");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doPost(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 阅读站内消息
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap syncMessageState(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "syncMessageState");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doPost(
				ahmHeaderMap, ahmNamePair);
		System.out.println("++++++++++++++++"+retMap);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 上传用户离线阅读记录
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap uploadReadRecord(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "uploadReadRecord");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 日志上传
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap uploadClientLog(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "uploadClientLog");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 同步书签
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap syncBookmark(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {
		ahmHeaderMap.put("Action", "syncBookmark");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 手持终端手机号码关联注册
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap handsetAssociate(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "handsetAssociate");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doPost(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 手持终端登陆鉴权
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap handsetAuthenticate(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "handsetAuthenticate");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doPost(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 手持终端获取短信验证码
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getSMSVerifyCode(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getSMSVerifyCode");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doPost(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 终端退出
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap quit(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {
		ahmHeaderMap.put("Action", "quit");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 终端升级查询
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap checkUpdate(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {
		ahmHeaderMap.put("Action", "checkUpdate");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 手持终端注册
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap register(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {
		ahmHeaderMap.put("Action", "register");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 轮询门户查询当前绑定关系是否已经成功
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getBindState(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getBindState");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 用于绑定处理中时间较长时给用于进行重新请求绑定/解绑的操作
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap resend(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {
		ahmHeaderMap.put("Action", "resend");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 用户获取好友详细信息
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getFriendInfo(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getFriendInfo");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 用户删除好友
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap deleteFriend(HashMap ahmHeaderMap, HashMap ahmNamePair)
	throws IOException, HttpException {
		ahmHeaderMap.put("Action", "deleteFriend");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 向服务器获取待确认好友
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getUnconfirmedFriendList(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getUnconfirmedFriendList");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取用户手持终端绑定关系
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getHandsetInfo(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getHandsetInfo");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 用户解除绑定手持终端
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap unbindHandset(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "unbindHandset");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取用户书券信息
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getHandsetUserTicketInfo(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getHandsetUserTicketInfo");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;

		/*
		HashMap retMap = new HashMap();
		retMap.put("result-code", "result-code: 0");

		String s = (String)ahmNamePair.get("start");
		String xml = "<Response>"+
		"<GetHandsetUserTicketInfoRsp>"+
		"<ticketInfo>您现在有书券50元，其中有20元，将在2011年4月1号失效，请尽快使用。</ticketInfo>"+
		"</GetHandsetUserTicketInfoRsp>"+
		"</Response>";


		retMap.put("ResponseBody", xml.getBytes());
		return retMap;*/
	}
	/**
	 * 获取服务器时间
	 */
	public static Date getServerTime()throws IOException, HttpException, ParseException{
		Date date=null;
		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		HashMap retMap = null;
		retMap = getUserInfo(ahmHeaderMap,ahmNamePair);
		String time=retMap.get("TimeStamp").toString().substring(11);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		return df.parse(time);
	}
	/**
	 * 获取服务器时间
	 */
	public static String getServerTimeAsString(){

		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		HashMap retMap = null;
		DateFormat df = new SimpleDateFormat("yyyy-mm-dd hh:MM:ss");
		String nowDeceiveTime = df.format(new Date());
		try {
			retMap = getUserInfo(ahmHeaderMap,ahmNamePair);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.i("获取服务器时间异常，将返回设备时间",e.toString());
			return nowDeceiveTime;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.i("获取服务器时间异常，将返回设备时间",e.toString());
			return nowDeceiveTime;
		}
		if(retMap == null || retMap.get("TimeStamp")==null){
			return nowDeceiveTime;
		}
		String time = retMap.get("TimeStamp").toString();
		return time.substring(11);
	}

	/**
	 * 获取服务器时间
	 */
	public static Date getServerTimeAsDate(){

		String nowServerTime = getServerTimeAsString();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			return df.parse(nowServerTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.i("获取服务器时间异常，将返回设备时间",e.toString());
			return new Date();
		}
	}

	/**
	 * 获取服务器时间
	 */
	public static long getServerTimeAsTimes(){
		Date nowServerTime = getServerTimeAsDate();
		return nowServerTime.getTime();
	}

	/**
	 * 赠送数据
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap presentBook(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "presentBook");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Constants.ms_CPC_URL).doPost(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取书籍赠送页面描述语
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getPresentBookInfo(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getPresentBookInfo");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Constants.ms_CPC_URL).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取书籍赠送列表
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getPresentBookList(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		ahmHeaderMap.put("Action", "getPresentBookList");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Constants.ms_CPC_URL).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}

	/**
	 * 获取书券列表
	 * 
	 * @param ahmHeaderMap
	 * @param ahmNamePair
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static HashMap getUserTicketList(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {
		
		ahmHeaderMap.put("Action", "getUserTicketList");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Constants.ms_CPC_URL).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
		
		/*
		HashMap retMap = new HashMap();
		retMap.put("result-code", "result-code: 0");

		String s = (String)ahmNamePair.get("start");
		String xml = "";
		if("0".equals(s)){
			xml = "<Response>"+
			"<GetUserTickerListRsp>"+
			"<totalRecordCount>10</totalRecordCount>"+
			"<TicketList>"+
			"<ticketInfo>dsadasda1</ticketInfo>"+
			"<ticketInfo>dsadasda2</ticketInfo>"+
			"<ticketInfo>dsadasda3</ticketInfo>"+
			"<ticketInfo>dsadasda4</ticketInfo>"+
			"<ticketInfo>dsadasda5</ticketInfo>"+
			"<ticketInfo>dsadasda6</ticketInfo>"+
			"<ticketInfo>dsadasda7</ticketInfo>"+
			"</TicketList>"+
			"</GetUserTickerListRsp>"+
			"</Response>";
		}
		if("7".equals(s)){
			xml = "<Response>"+
			"<GetUserTickerListRsp>"+
			"<totalRecordCount>10</totalRecordCount>"+
			"<TicketList>"+
			"<ticketInfo>dsadasda8</ticketInfo>"+
			"<ticketInfo>dsadasda9</ticketInfo>"+
			"<ticketInfo>dsadasda10</ticketInfo>"+
			"</TicketList>"+
			"</GetUserTickerListRsp>"+
			"</Response>";
		}
		retMap.put("ResponseBody", xml.getBytes());
		return retMap;*/
	}
	public static HashMap userOrderBook(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {

		ahmHeaderMap.put("Action", "userOrderBook");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doPost(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;

	}

	public static HashMap addUserLeaveWord(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {

		ahmHeaderMap.put("Action", "addUserLeaveWord");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doPost(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;
	}
	public static HashMap submitCommentVote(HashMap ahmHeaderMap,
			HashMap ahmNamePair) throws IOException, HttpException {

		ahmHeaderMap.put("Action", "submitCommentVote");
		long startTime = System.currentTimeMillis();
		HashMap retMap = new CPConnection(Config.getString("CPC_URL")).doGet(
				ahmHeaderMap, ahmNamePair);
		long endTime = System.currentTimeMillis();
		Log.i("CPManager." + (String) ahmHeaderMap.get("Action")
				+ " spend times(ms)", String.valueOf(endTime - startTime));
		return retMap;

	}
}
