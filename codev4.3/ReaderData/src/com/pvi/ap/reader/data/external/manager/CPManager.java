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
 * ����ƽ̨�ӿ�<br>
 * �������������ƶ����磬�������ṩ������ҵ��ӿ�
 * 
 * @author rd038
 * @since 2010-10-10
 * @version V1.0.0 (C)Copyright 2010-2013, by www.pvi.com.tw
 */

public class CPManager {

	/**
	 * ��ȡ�û���Ϣ
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
	 * �޸��û���Ϣ
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
	 * ���Ӻ���
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
	 * ��ȡ�û��б�
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
	 * ��ȡר���б�ӿ�
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
	 * ��ȡƵ��ר����ҳ
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
	 * ��ȡר��ҳ�������б�
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
	 * ��ȡר�����а���ҳ
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
	 * ��ȡָ�������а���Ϣ
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
	 * ��ȡ��������
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
	 * ��ȡ����Ŀ¼
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
	 * ��ȡ�����½�����
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
	 * ��ȡ�����½��ڲ�ͼ
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
	 * ��ȡ����������Ϣ
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
	 * ��ȡ��Ѷ�б�
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
	 * ��ȡ��Ѷ��ϸ��Ϣ
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
	 * ��ȡ��������Ƽ���Ϣ
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
	 * ��ȡ�����������б�
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
	 * ��ȡƽ̨֧�ֵ����а�������Ϣ
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
	 * �ն���������
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
	 * ��������½�
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
	 * ��ȡ���ζ��������б�
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
	 * ��ȡ�û��Ķ������������Ѽ�¼��Ϣ��������ʱ�䵹������
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
	 * ��ȡͼ��ֲ���Ϣ�б�
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
	 * ��ȡ�ն���Դ����
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
	 * ���ϵͳ��ǩ
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
	 * ����û���ǩ
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
	 * ɾ��ȫ��ϵͳ��ǩ
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
	 * ɾ��ȫ���û���ǩ
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
	 * ɾ����ǩ
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
	 * ɾ������ȫ����ǩ
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
	 * ɾ��վ����Ϣ
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
	 * ��ȡ����Ԥ�ϼܵ���
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
	 * ��ȡ�ֳ��ն˹���ҳ��
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
	 * �ն˻�ȡվ����Ϣ
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
	 * ��ȡ��̬��Դ���ݽӿ�
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
	 * ��ȡϵͳ�����ò�������Ϣ����ǰ���鼮������
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
	 * �ն����������ȡ������ǩ
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
	 * ���������ӿ�
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
	 * ����վ����Ϣ�ӿ�
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
	 * ��ȡ����ȫ���û���ǩ
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
	 * ��ȡ�û��ղص������б�
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
	 * �����û��ղ�
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
	 * �û�ɾ���ղؼ�����
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
	 * �û�ɾ���ղؼ���������
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
	 * �ն����������ȡ������ǩ��
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
	 * Ԥ�����ݸ���
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
	 * ��ȡ���е�δ���������½��б����Ӧ���ʷ���Ϣ
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
	 * ���½ڶ����ӿڣ�����������½��б�
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
	 * ���½ڶ����ӿ�,��Ŀ��ƽ̨ͳһ���� ��Ĭ��Ϊ10��
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
	 * ��ȡ������Ŀ�ʷ���Ϣ
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
	 * ��������
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
	 * ��������ר��
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
	 * ��ȡ���ݲ�Ʒ��Ϣ�ӿ�
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
	 * ȡ��Ԥ�����ݸ���
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
	 * ��ȡԤ�����ݸ����б�
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
	 * ��ȡ���е������½ڵ�����URL
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
	 * Ԥ�����·�ʽ����
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
	 * ȡ������ר����Ŀ
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
	 * ��ȡ���Ѽ�¼�ӿ�
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
	 * ��ȡ�û��������ݽӿڣ����۽����������ʱ���Ⱥ�����
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
	 * �û������ݷ�������
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
	 * �û�ͶƱ
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
	 * ������Ƽ����������Ƽ�
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
	 * �Ķ�վ����Ϣ
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
	 * �ϴ��û������Ķ���¼
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
	 * ��־�ϴ�
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
	 * ͬ����ǩ
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
	 * �ֳ��ն��ֻ��������ע��
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
	 * �ֳ��ն˵�½��Ȩ
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
	 * �ֳ��ն˻�ȡ������֤��
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
	 * �ն��˳�
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
	 * �ն�������ѯ
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
	 * �ֳ��ն�ע��
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
	 * ��ѯ�Ż���ѯ��ǰ�󶨹�ϵ�Ƿ��Ѿ��ɹ�
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
	 * ���ڰ󶨴�����ʱ��ϳ�ʱ�����ڽ������������/���Ĳ���
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
	 * �û���ȡ������ϸ��Ϣ
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
	 * �û�ɾ������
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
	 * ���������ȡ��ȷ�Ϻ���
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
	 * ��ȡ�û��ֳ��ն˰󶨹�ϵ
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
	 * �û�������ֳ��ն�
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
	 * ��ȡ�û���ȯ��Ϣ
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
		"<ticketInfo>����������ȯ50Ԫ��������20Ԫ������2011��4��1��ʧЧ���뾡��ʹ�á�</ticketInfo>"+
		"</GetHandsetUserTicketInfoRsp>"+
		"</Response>";


		retMap.put("ResponseBody", xml.getBytes());
		return retMap;*/
	}
	/**
	 * ��ȡ������ʱ��
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
	 * ��ȡ������ʱ��
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
			Log.i("��ȡ������ʱ���쳣���������豸ʱ��",e.toString());
			return nowDeceiveTime;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.i("��ȡ������ʱ���쳣���������豸ʱ��",e.toString());
			return nowDeceiveTime;
		}
		if(retMap == null || retMap.get("TimeStamp")==null){
			return nowDeceiveTime;
		}
		String time = retMap.get("TimeStamp").toString();
		return time.substring(11);
	}

	/**
	 * ��ȡ������ʱ��
	 */
	public static Date getServerTimeAsDate(){

		String nowServerTime = getServerTimeAsString();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			return df.parse(nowServerTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.i("��ȡ������ʱ���쳣���������豸ʱ��",e.toString());
			return new Date();
		}
	}

	/**
	 * ��ȡ������ʱ��
	 */
	public static long getServerTimeAsTimes(){
		Date nowServerTime = getServerTimeAsDate();
		return nowServerTime.getTime();
	}

	/**
	 * ��������
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
	 * ��ȡ�鼮����ҳ��������
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
	 * ��ȡ�鼮�����б�
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
	 * ��ȡ��ȯ�б�
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
