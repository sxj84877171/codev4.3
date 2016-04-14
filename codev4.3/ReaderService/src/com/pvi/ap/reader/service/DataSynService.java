package com.pvi.ap.reader.service;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;

import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.BookInfo;
import com.pvi.ap.reader.data.content.Bookmark;
import com.pvi.ap.reader.data.content.OptLogInfo;
import com.pvi.ap.reader.data.content.Reading;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;
import com.pvi.ap.reader.data.external.manager.LeafNode;
import com.pvi.ap.reader.data.external.manager.XMLUtil;
import com.pvi.ap.reader.data.external.manager.XmlElement;

/**
 * 
 * Data synchronization service
 * <br>the class run background to sunchronization the data
 * @author 孙向锦
 * @since 2010-9-21
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 * 
 */
public class DataSynService extends Service {

	private static final String TAG = "DataSynService::";
	
	 /**
     * 数据同步 0 添加
     */
    public static final String CON_ADD_FLAG = "0";
    /**
     * 数据同步 1删除
     */
	public static final String CON_DELETE_FLAG = "1";
	/**
	 * 数据同步0未同步
	 */
	public static final String CON_NOT_SYNCH_FLAG = "0";
	/**
	 * 数据同步1已同步
	 */
	public static final String CON_YES_SYNCH_FLAG = "1";
	

	public static final String CON_SETFLAG = "YES";

	public String mReadingSet = CON_SETFLAG;// 同步阅读信息
	public String mOptLogInfoSet = CON_SETFLAG;// 同步用户信息
	public String mBookmarkSet = CON_SETFLAG;// 同步日志信息

	public long mTimeTaskTime = 24 * 3600 * 1000;//间隔时间

	private Timer mTimer = null;
	private TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			synchMessage();
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		if (mTimer != null) {
			mTimer.cancel();
		} else {
			mTimer = new Timer();
		}
		mTimer.schedule(mTimerTask, new java.util.Date(), mTimeTaskTime);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		setMessage(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		super.onDestroy();

	}

	/**
	 * Outside the control service need more new information
	 * <br>set message to run service
	 * @param intent the Extral to change the class run
	 * @exception RuntimeException
	 */
	private void setMessage(Intent intent) {
		String methodName = "setMessage";

		if (intent == null) {
			return;
		}

		Bundle bundle = null;
		bundle = intent.getExtras();
		if (bundle != null) {

			String timeTaskTimeStr = bundle.getString("timeTaskTime");
			if (timeTaskTimeStr != null) {
				mTimeTaskTime = Long.parseLong(timeTaskTimeStr);
			}
			//Logger.d(TAG + methodName, "set");
			mReadingSet = bundle.getString("readingSet") == null ? mReadingSet
					: bundle.getString("readingSet");
			mOptLogInfoSet = bundle.getString("optLogInfoSet") == null ? mOptLogInfoSet
					: bundle.getString("optLogInfoSet");
			mBookmarkSet = bundle.getString("BookmarkSet") == null ? mBookmarkSet
					: bundle.getString("BookmarkSet");
		}
	}

	/**
	 * the main method to syschronous information
	 * <br>Synchronous information method
	 * <p>synch message:bookmark,reading optloginfo
	 * @since 2010-9
	 */
	private void synchMessage() {
		Logger.i(TAG, "datasyn:synchMessage");
		if(true) return ;
//		if(networkConnectAvailable()){
//			return ;
//		}

		if (CON_SETFLAG.equals(mBookmarkSet)) {
			synchBookmarkContentProvide();// Synchronous bookmarks information
		}

		if (CON_SETFLAG.equals(mReadingSet)) {
			synchReadingContentProvider(); // Synchronous read information
		}

//		if (CON_SETFLAG.equals(mOptLogInfoSet)) {
//			synchOptLogInfoContentProvider();// Synchronous log information
//		}

		//Logger.d(TAG + methodName, "Data synchronization complete!");

	}

	/**
	 * Synchronous read information
	 * <p>the main method synch reading database
	 * @return void
	 */
	private void synchReadingContentProvider() {
		String methodName = "synchReadingContentProvider";

		// Get the information need synchronization
		Cursor cursor = getReadingContentProvider();

		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();// head map
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();// sending map
		HashMap responseMap = null;// the result

		// Tectonic XML document
		List listFather = new ArrayList();

		if (!isNotNullFrist(cursor)) {
			return;
		}

		do {
			List listChild = new ArrayList();
			LeafNode l1 = new LeafNode("readTime", cursor.getString(cursor
					.getColumnIndex(Reading.ReadTime)));
			LeafNode l2 = new LeafNode("chapterID", cursor.getString(cursor
					.getColumnIndex(Reading.ChapterId)));
			LeafNode l3 = new LeafNode("contentID", cursor.getString(cursor
					.getColumnIndex(Reading.ContentId)));
			listChild.add(l1);
			listChild.add(l2);
			listChild.add(l3);
			XmlElement xmlElement1 = new XmlElement("GetChapterImageReq",
					listChild);
			listFather.add(xmlElement1);
		} while (cursor.moveToNext());
		XmlElement xmlElementFather = new XmlElement("ReadRecordList",
				listFather);
		List listUploadReadRecordReq = new ArrayList();
		listUploadReadRecordReq.add(xmlElementFather);
		XmlElement uploadReadRecordReq = new XmlElement("UploadReadRecordReq",
				listUploadReadRecordReq);
		List listRequest = new ArrayList();
		listRequest.add(uploadReadRecordReq);
		XmlElement request = new XmlElement("Request", listRequest);

		// An XML string structures
		String requestXMLBody = null;
		try {
			requestXMLBody = XMLUtil.getXmlStringFromXmlElement(request);
			//Logger.d(TAG + methodName, requestXMLBody);
		} catch (ParserConfigurationException e1) {
			Logger.e(TAG + methodName, e1.toString());
			return ;
		}
		ahmNamePair.put("XMLBody", requestXMLBody);

		// Upload network
		try {
			responseMap = CPManager.uploadReadRecord(ahmHeaderMap, ahmNamePair);
		} catch (HttpException e) {
			Logger.e(TAG + methodName, e.toString());
			return ;
		} catch (IOException e) {
			Logger.e(TAG + methodName, e.toString());
			return ;
		}

		if (responseMap == null) {
			//Logger.d(TAG + methodName, "uploadReadRecord no result");
			cursor.close();
			return;
		}
		if (responseMap.get("result-code") == null) {
			//Logger.d(TAG, "ResultMap no result-code");
			cursor.close();
			return;
		}

		//Logger.d(TAG + methodName, responseMap.get("result-code").toString());
		
		if(responseMap.get("result-code").toString().indexOf("0") != -1){
			//Logger.d(TAG + methodName, "data hava synchronization");
			return ;
		}

		// Update mobile database
		updateReadingContentProvider(cursor);

		cursor.close();
	}

	/**
	 * Synchronization operation Logger message
	 * <p>  the main method synch Logger message
	 */
	private void synchOptLogInfoContentProvider() {
		String methodName = "synchOptLogInfoContentProvider";

		// Get the information need synchronization
		Cursor cursor = getOptLogInfoContentProvider();

		if (!isNotNullFrist(cursor)) {
			return;
		}

		List listFather = new ArrayList();
		
		String deviceID = Config.getString("deviceID");

		do {
			List listChild = new ArrayList();
			LeafNode l0 = new LeafNode("deviceID", deviceID);
			LeafNode l1 = new LeafNode("logType", cursor.getString(cursor
					.getColumnIndex(OptLogInfo.LogType)));

			LeafNode l2 = new LeafNode("logTime", cursor.getString(cursor
					.getColumnIndex(OptLogInfo.CommentTime)));
			LeafNode l3 = new LeafNode("content", cursor.getString(cursor
					.getColumnIndex(OptLogInfo.LogConnent)));

			listChild.add(l0);
			listChild.add(l1);
			listChild.add(l2);
			listChild.add(l3);

			XmlElement xmlElement = new XmlElement("ClientLog", listChild);
			listFather.add(xmlElement);
		} while (cursor.moveToNext());

		XmlElement clientLog = new XmlElement("ClientLogList", listFather);
		List clientLogList = new ArrayList();
		clientLogList.add(clientLog);
		XmlElement uploadClientLogReq = new XmlElement("UploadClientLogReq",
				clientLogList);
		List requestList = new ArrayList();
		requestList.add(uploadClientLogReq);
		XmlElement request = new XmlElement("Request", requestList);

		String requestXMLBody = null;
		try {
			requestXMLBody = XMLUtil.getXmlStringFromXmlElement(request);
			//Logger.d(TAG + methodName, requestXMLBody);
		} catch (ParserConfigurationException e1) {
			Logger.e(TAG + methodName, e1.toString());
		}

		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();// 头map
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();// 发送的map
		HashMap responseMap = null;
		ahmNamePair.put("XMLBody", requestXMLBody);
		try {
			responseMap = CPManager.uploadClientLog(ahmHeaderMap, ahmNamePair);
		} catch (HttpException e) {
			Logger.e(TAG + methodName, e.toString());
			return ;
		} catch (IOException e) {
			Logger.e(TAG + methodName, e.toString());
			return ;
		}

		if (responseMap == null) {
			//Logger.d(TAG + methodName, "uploadReadRecord no result");
			cursor.close();
			return;
		}
		if (responseMap.get("result-code") == null) {
			//Logger.d(TAG, "ResultMap no result-code");
			cursor.close();
			return;
		}

		//Logger.d(TAG + methodName, responseMap.get("result-code").toString());
		
		if(responseMap.get("result-code").toString().indexOf("0") == -1){
			//Logger.d(TAG + methodName, "data hava synchronization");
		}

		updateOptLogInfoContentProvider(cursor);

		cursor.close();

	}

	/**
	 * Synchronous bookmarks information
	 * <p> the main method synch bookmark
	 * @return void
	 */
	private void synchBookmarkContentProvide() {
		String methodName = "synchBookmarkContentProvide";

		// Get the information need synchronization
		Cursor cursor = getBookmarkContentProvide();
		if (!isNotNullFrist(cursor)) {

			return;
		}
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		do {
			Map<String, String> map = new HashMap<String, String>();
			map.put("contentID", cursor.getString(cursor
					.getColumnIndex(Bookmark.ContentId)));
			map.put("chapterID", cursor.getString(cursor
					.getColumnIndex(Bookmark.ChapterId)));
			map.put("position", cursor.getString(cursor
					.getColumnIndex(Bookmark.Position)));
			map.put("type", cursor.getString(cursor
					.getColumnIndex(Bookmark.BookmarkType)));
			map.put("operation", cursor.getString(cursor
					.getColumnIndex(Bookmark.OperationType)));
			map.put("createTime", cursor.getString(cursor
					.getColumnIndex(Bookmark.CreatedDate)));
			mapList.add(map);

		} while (cursor.moveToNext());

		for (int i = 0; i < mapList.size(); i++) {
			Map<String, String> map = mapList.get(i);

			List listFather = new ArrayList();

			LeafNode l0 = new LeafNode("contentID", map.get("contentID"));
			LeafNode l1 = new LeafNode("chapterID", map.get("chapterID"));
			LeafNode l2 = new LeafNode("position", map.get("position"));
			LeafNode l3 = new LeafNode("type", map.get("type"));
			LeafNode l4 = new LeafNode("operation", map.get("operation"));
			LeafNode l5 = new LeafNode("createTime", map.get("createTime"));

			List listChild = new ArrayList();
			listChild.add(l0);
			listChild.add(l1);
			listChild.add(l2);
			listChild.add(l3);
			listChild.add(l4);
			listChild.add(l5);

			XmlElement leaf = new XmlElement("Bookmark", listChild);
			List bookmarkList = new ArrayList();
			bookmarkList.add(leaf);
			XmlElement bookmarkListXml = new XmlElement("BookmarkList",
					bookmarkList);
			LeafNode l6 = new LeafNode("totalRecordCount", "1");
			List addNewList = new ArrayList();
			addNewList.add(l6);
			addNewList.add(bookmarkListXml);
			XmlElement syncBookmarkReq = new XmlElement("SyncBookmarkReq",
					addNewList);

			List syncBookmarkReqList = new ArrayList();

			syncBookmarkReqList.add(syncBookmarkReq);

			XmlElement request = new XmlElement("Request", syncBookmarkReqList);

			String requestXMLBody = null;
			try {
				requestXMLBody = XMLUtil.getXmlStringFromXmlElement(request);

				//Logger.d(TAG + methodName, requestXMLBody);
			} catch (ParserConfigurationException e1) {
				Logger.e(TAG + methodName, e1.toString());
			}

			HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();// 头map
			HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();// 发送的map
			HashMap responseMap = null;
			ahmNamePair.put("XMLBody", requestXMLBody);

			try {
				responseMap = CPManager.syncBookmark(ahmHeaderMap, ahmNamePair);
			} catch (HttpException e) {
				Logger.e(TAG + methodName, e.toString());
				return ;
			} catch (IOException e) {
				Logger.e(TAG + methodName, e.toString());
				return ;
			}
			
			if (responseMap == null) {
				//Logger.d(TAG + methodName, "uploadReadRecord no result");
				continue ;
			}
			if (responseMap.get("result-code") == null) {
				//Logger.d(TAG, "ResultMap no result-code");
				continue ;
			}

			//Logger.d(TAG + methodName, responseMap.get("result-code").toString());
			
			if(responseMap.get("result-code").toString().indexOf("0") == -1){
				//Logger.d(TAG + methodName, "data hava synchronization");
			}

			byte[] responseBody = (byte[])responseMap.get("ResponseBody");
			getResultXml(responseBody);
		}


		updateBookmarkContentProvide(cursor);
		if(!cursor.isClosed()){
			cursor.close();
		}
	}

	/**
	 * Get in the reading record synchronous information
	 * <p> the method is get the reading message from database;
	 * @return cursor
	 */
	private Cursor getReadingContentProvider() {
		String methodName = "getReadingContentProvider";

		String columns[] = new String[] { Reading._ID, Reading.UserID,
				Reading.ContentId, Reading.ChapterId, Reading.FilePath,
				Reading.ReadName, Reading.ReadTime, Reading.SourceType,
				Reading.ReadPosition };
		String where = Reading.SynchFlag + " = " + CON_NOT_SYNCH_FLAG + " and "
				+ Reading.OperationType + "=" + CON_ADD_FLAG;
		//Logger.d(TAG + methodName, columns.toString() + where);
		Cursor cursor = getContentResolver().query(Reading.CONTENT_URI,
				columns, where, null, null);
		return cursor;
	}

	/**
	 * Operate without synchronization of Logger information table
	 * <p>the method is get the optLoggerinfo message form database;
	 * @return cursor the result database
	 */
	private Cursor getOptLogInfoContentProvider() {
		String methodName = "getOptLogInfoContentProvider";

		String columns[] = new String[] { OptLogInfo._ID, OptLogInfo.UserID,
				OptLogInfo.LogType, OptLogInfo.LogConnent,
				OptLogInfo.CommentTime, OptLogInfo.OperationType,
				OptLogInfo.SynchFlag };
		String where = OptLogInfo.SynchFlag + " = " + CON_NOT_SYNCH_FLAG + " and "
				+ OptLogInfo.OperationType + " = " + CON_ADD_FLAG;
		//Log.d(TAG + methodName, columns.toString() + where);
		Cursor cur = getContentResolver().query(OptLogInfo.CONTENT_URI,
				columns, where, null, OptLogInfo._ID + " ASC");

		return cur;
	}

	/**
	 * Bookmarks not synchronous information table get information
	 * <p>the method is get the bookmark message form database;
	 * @return cursor the bookmark cursor;
	 */
	private Cursor getBookmarkContentProvide() {
		String methodName = "getBookmarkContentProvide";

		String columns[] = new String[] { Bookmark._ID, Bookmark.UserID,
				Bookmark.BookmarkId, Bookmark.BookmarkType, Bookmark.ChapterId,
				Bookmark.ChapterName, Bookmark.ContentId, Bookmark.ContentName,
				Bookmark.OperationType, Bookmark.SynchFlag, Bookmark.Position,
				Bookmark.Count, Bookmark.CreatedDate };
		String where = Bookmark.SynchFlag + " = " + CON_NOT_SYNCH_FLAG + " and "
				+ Bookmark.OperationType + " = " + CON_ADD_FLAG;
		//Logger.d(TAG + methodName, columns.toString() + where);
		return getContentResolver().query(Bookmark.CONTENT_URI, columns, where,
				null, Bookmark._ID + " ASC");
	}

	/**
	 * Read the record synchronous information changes
	 * <p> the method to update the reading message
	 * @param cursor
	 * @return boolean
	 */
	private boolean updateReadingContentProvider(Cursor cursor) {
		String methodName = "updateReadingContentProvider";

		if (!isNotNullFrist(cursor)) {
			return true;
		}

		// Literally modified has been updated
		do {
			ContentValues values = new ContentValues();
			values.put(Reading.SynchFlag, CON_YES_SYNCH_FLAG);
			getContentResolver().update(
					Reading.CONTENT_URI,
					values,
					Reading._ID
							+ "="
							+ cursor.getString(cursor
									.getColumnIndex(Reading._ID)), null);

		} while (cursor.moveToNext());

		return true;
	}

	/**
	 * Modify the following information Logger message operation simultaneously
	 * <p>the method is update the optLoggerinfo message ;
	 * @param cursor it is need to update message cursor;
	 * @return whether update ok
	 */
	private boolean updateOptLogInfoContentProvider(Cursor cursor) {
		if (!isNotNullFrist(cursor)) {
			return true;
		}

		do {
			ContentValues values = new ContentValues();
			values.put(OptLogInfo.SynchFlag, CON_YES_SYNCH_FLAG);
			getContentResolver().update(
					OptLogInfo.CONTENT_URI,
					values,
					OptLogInfo._ID
							+ "="
							+ cursor.getString(cursor
									.getColumnIndex(OptLogInfo._ID)), null);
		} while (cursor.moveToNext());

		return true;
	}

	/**
	 * Changes in the information table bookmarks information simultaneously
	 * <p>the method is update the bookmark message;
	 * @param cursor it is need to update message cursor; 
	 * @return whether update ok
	 */
	private boolean updateBookmarkContentProvide(Cursor cursor) {

		if (!isNotNullFrist(cursor)) {
			return true;
		}

		do {
			ContentValues values = new ContentValues();
			values.put(Bookmark.SynchFlag, CON_YES_SYNCH_FLAG);
			getContentResolver().update(
					Bookmark.CONTENT_URI,values,Bookmark._ID + " = "
					+ cursor.getString(cursor.getColumnIndex(Bookmark._ID)), null);
		} while (cursor.moveToNext());

		return true;
	}

	/**
	 * Judge whether the cursor is empty
	 * @param cursor the database cursor
	 * @return boolean whether null?
	 */
	private boolean isNotNullFrist(Cursor cursor) {
		String methodName = "isNotNullFrist";

		if (cursor == null || cursor.getCount() <= 0 || !cursor.moveToFirst()
				|| cursor.getPosition() < 0) {
			if (cursor != null) {
				//Log.d(TAG + methodName, "cursor is null");
				cursor.close();
			}
			return false;
		} else {
			return true;
		}
	}

	/**
	 * save the message
	 * @param responseBody xml document
	 * @return no return ;
	 */
	private void getResultXml(byte[] responseBody) {
		String methodName = "getResultXml";

		Document dom = null;

		try {
			System.out.println(new String(responseBody));
			dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
		} catch (ParserConfigurationException e) {
			Logger.e(TAG + methodName, e.toString());
			return ;
		} catch (SAXException e) {
			Logger.e(TAG + methodName, e.toString());
			return ;
		} catch (IOException e) {
			Logger.e(TAG + methodName, e.toString());
			return ;
		}

		Element rootele = dom.getDocumentElement();
		NodeList contentInfoList = rootele.getElementsByTagName("ContentInfoList");
		if(contentInfoList == null || contentInfoList.getLength() == 0){
			return ;
		}
		
		NodeList contentInfo = rootele.getElementsByTagName("ContentInfo");
		
		int size = contentInfo.getLength();
		
		for(int i = 0 ; i< size ; i ++){
			Element contentInfoItem = (Element)contentInfo.item(i);
			Node contentIDNode = contentInfoItem.getElementsByTagName("contentID").item(0);
			String contentID = contentIDNode.getFirstChild().getNodeValue();
			Node contentNameNode = contentInfoItem.getElementsByTagName("contentName").item(0);
			String contentName = contentNameNode.getFirstChild().getNodeValue();
			Node authorNameNode = contentInfoItem.getElementsByTagName("authorName").item(0);
			String authorName = authorNameNode.getFirstChild().getNodeValue();
			Node chapterIDNode = contentInfoItem.getElementsByTagName("").item(0);
			String chapterID = chapterIDNode.getFirstChild().getNodeValue();
			NodeList bookmarkList = contentInfoItem.getElementsByTagName("BookmarkList");
			int insize = bookmarkList.getLength();
			for(int j = 0 ; j < insize ; j ++){
				Element bookmarkItem = (Element)bookmarkList.item(j);
				Node bookmarkIDNode = bookmarkItem.getElementsByTagName("bookmarkID").item(0);
				String bookmarkID = bookmarkIDNode.getFirstChild().getNodeValue();
				Node inchapterIDNode = bookmarkItem.getElementsByTagName("chapterID").item(0);
				String inchapterID = inchapterIDNode.getFirstChild().getNodeValue();
				Node chapterNameNode = bookmarkItem.getElementsByTagName("chapterName").item(0);
				String chapterName = chapterNameNode.getFirstChild().getNodeValue();
				Node positionNode = bookmarkItem.getElementsByTagName("position").item(0);
				String position = positionNode.getFirstChild().getNodeValue();
				Node createTimeNode = bookmarkItem.getElementsByTagName("createTime").item(0);
				String createTime = createTimeNode.getFirstChild().getNodeValue();
				Node typeNode = bookmarkItem.getElementsByTagName("type").item(0);
				String type = typeNode.getFirstChild().getNodeValue();
				
			}
			
		}
	}
	
	private void updateBookMark()
    {   
    	ContentValues values = new ContentValues();
    	values.put(BookInfo.ProcessPer, "70%");
    	
    	getContentResolver().update(BookInfo.CONTENT_URI,values,BookInfo.ContentID + "=" + "'BK-001'",null);
    }
	
	private void insertUserInfo() {
		String methodName = "insertUserInfo";

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date CurTime = new Date(System.currentTimeMillis());
		String now = formatter.format(CurTime);

		ContentValues values = new ContentValues();
		values.put(OptLogInfo.UserID, "002");
		values.put(OptLogInfo.LogType, "平台");
		values.put(OptLogInfo.LogConnent, "软件版本升级，已更新");
		values.put(OptLogInfo.OperationType, "0");
		values.put(OptLogInfo.SynchFlag, "0");
		values.put(OptLogInfo.CommentTime, now);

		getContentResolver().insert(OptLogInfo.CONTENT_URI, values);
		////Log.d(TAG + methodName, OptLogInfo.CONTENT_URI + values.toString());

	}

	private void deleteUserInfo(String where, String[] selectionArgs) {

		getContentResolver().delete(OptLogInfo.CONTENT_URI, where,
				selectionArgs);
	}

	private void insertReading() {

		ContentValues values = new ContentValues();
		values.put(Reading.UserID, "U011");
		values.put(Reading.ContentId, "P103");
		values.put(Reading.ChapterId, "P002");
		values.put(Reading.FilePath, "data/file2");
		values.put(Reading.ReadName, "Small_women");
		values.put(Reading.ReadTime, "2010-09-14");
		values.put(Reading.SourceType, "2");
		values.put(Reading.ReadPosition, "1000");
		values.put(Reading.SynchFlag, CON_NOT_SYNCH_FLAG);
		values.put(Reading.OperationType, CON_ADD_FLAG);
		getContentResolver().insert(Reading.CONTENT_URI, values);
	}

	private void deleteReading(String where, String[] selectionArgs) {

		getContentResolver().delete(Reading.CONTENT_URI, where, selectionArgs);
	}

	private void insertBookmark() {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date CurTime = new Date(System.currentTimeMillis());
		String now = formatter.format(CurTime);

		ContentValues values = new ContentValues();
		values.put(Bookmark.UserID, "002");
		values.put(Bookmark.BookmarkId, "11010");
		values.put(Bookmark.BookmarkType, "1");
		values.put(Bookmark.ChapterId, "1000");
		values.put(Bookmark.ChapterName, "1000");
		values.put(Bookmark.ContentId, "L001");
		values.put(Bookmark.ContentName, "");
		values.put(Bookmark.OperationType, "0");
		values.put(Bookmark.SynchFlag, "0");
		values.put(Bookmark.Position, "42");
		values.put(Bookmark.Count, "湖北");
		values.put(Bookmark.CreatedDate, now);

		getContentResolver().insert(Bookmark.CONTENT_URI, values);

	}

	private void deleteBookmark(String where, String[] selectionArgs) {

		getContentResolver().delete(Bookmark.CONTENT_URI, where, selectionArgs);
	}

	/**
	 * 网络是否连接正常
	 * @return
	 */
	public boolean networkConnectAvailable(){
		ConnectivityManager cwjManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if(cwjManager == null){
			return false ;
		}
		if(cwjManager.getActiveNetworkInfo() == null){
			return false ;
		}
		
		return cwjManager.getActiveNetworkInfo().isAvailable();
	}
}
