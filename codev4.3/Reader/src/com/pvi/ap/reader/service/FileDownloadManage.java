package com.pvi.ap.reader.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.BookInfo;
/**
 * 文件下载管理<br>
 * 调用下载服务
 * @author Elvis
 * @version 1.1.0
 * C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class FileDownloadManage {
	public static String TAG = "FileDownloadManage" ;
	public static String COMMAND = "command" ;
	
	/**
	 * 调用者
	 */
	private Context mcCall = null ;
	/**
	 * 数据
	 */
	private Bundle mb_extras  = null ;
	/**
	 * 是否有错
	 */
	private boolean mb_error = false ;
	/**
	 * 构造函数
	 */
	public FileDownloadManage(Context call){
		this.mcCall = call ;
		clear();
	}
	/**
	 * 要下载证书
	 * @param certBundle 需要如下参数
	 * <br>Version Version
	 * <br>CID CID
	 * <br>PID PID
	 * <br>Nonce Nonce
	 * <br>requesttype requesttype
	 * <br>user-id user-id
	 * <br>password password
	 * <br>Accept Accept
	 * <br>Host Host
	 * <br>User-Agent User-Agent
	 * <br>x-up-calling-line-id x-up-calling-line-id
	 * <br>contentID contentID
	 * <br>chapterID chapterID可选
	 */
	public void downloadCert(Bundle certBundle){
		Logger.d(TAG + "downloadCert", certBundle.toString());
		mb_extras.putBoolean("downCert", true);
		mb_extras.putAll(certBundle);
	}
	/**
	 * 要下载章节
	 * @param contentBundle 需要如下参数
	 * <br>url 下载meb文件路径
	 * <br>name 下载meb文件名
	 * <br>contentID contentID
	 * <br>cateLog cateLog
	 * <br>bookType bookType
	 * <br>pathType pathType
	 * <br>author author
	 * <br>maker maker
	 * <br>saleTime saleTime
	 */
	public void downloadChapter(Bundle contentBundle){
		Logger.d(TAG + "downloadChapter", contentBundle.toString());
		if(contentBundle.getStringArrayList("idList").size() != contentBundle.getStringArrayList("urlList").size()){
			mb_error = true ;
		}
		mb_extras.putBoolean("downChapter", true);
		mb_extras.putAll(contentBundle);
	}
	/**
	 * 要下载MEB文件
	 * @param mebBundle 需要如下参数
	 * <br>url 下载meb文件路径
	 * <br>name 下载meb文件名
	 * <br>contentID contentID
	 * <br>cateLog cateLog
	 * <br>bookType bookType
	 * <br>pathType pathType
	 * <br>author author
	 * <br>maker maker
	 * <br>saleTime saleTime
	 */
	public void downloadMebBook(Bundle mebBundle){
		Logger.d(TAG + "downloadMebBook", mebBundle.toString());
		mb_extras.putBoolean("downMeb", true);
		mb_extras.putAll(mebBundle);
	}
	
	/**
	 * 批量下载证书
	 * @param chapterCertBundle
	 */
	public void downloadChapterCert(Bundle chapterCertBundle){
		Logger.d(TAG, "downloadChapterCert" + chapterCertBundle.toString());
		mb_extras.putBoolean("downChapterCert", true);
		mb_extras.putAll(chapterCertBundle);
	}
	/**
	 * 服务名
	 */
	private final String serviceName = "com.pvi.ap.reader.service.FileDownloadService.actionName" ;
	/**
	 * 启动服务开始下载
	 */
	private void startService(){
		Intent intent = new Intent(serviceName);
		intent.putExtras(mb_extras);
		this.mcCall.getApplicationContext().startService(intent);
	}
	/**
	 * 关闭服务
	 */
	public void stopService(){
		Intent intent = new Intent(serviceName);
		this.mcCall.stopService(intent);
	}
	/**
	 * 暂停任务
	 * @param bundle 暂停任务的数据
	 * @return 
	 * 0 暂停成功
	 * -1 暂停失败-任务不存在
	 */
	public int pauseDownloadTask(Bundle task){
		Logger.d(TAG + "pauseDownloadTask", task.toString());
		mb_extras.putInt(COMMAND, 1);//暂停
		this.mb_extras.putAll(task) ;
		updateBookInfo("pause", mb_extras.getString("contentID"), null);
		// update 状态
		startService();
		return 0 ;
	}
	/**
	 * 重新下载<br>
	 * 直接调用该方法，设置数据全部外面设置好.
	 * @param task
	 * @return
	 * 0 代表成功
	 * -1 代表失败
	 */
	public int restartDownloadTask(Bundle task){
		Logger.d(TAG + "pauseDownloadTask", task.toString());
		mb_extras.putInt(COMMAND, 2);//重新开始
		updateBookInfo("0", mb_extras.getString("contentID"), null);
		this.mb_extras.putAll(task) ;
		startService();
		return 0 ;
	}
	/**
	 * 重新下载
	 * @param task
	 * @return
	 * 0 代表成功
	 * -1 代表失败
	 */
	public int restartDownloadTask(){
		Logger.d(TAG , "restartDownloadTask");
		mb_extras.putInt(COMMAND, 2);//重新开始
		updateBookInfo("0", mb_extras.getString("contentID"), null);
		startService();
		return 0 ;
	}
	/**
	 * 取消任务
	 * @param task
	 * @return
	 * 0 代表成功
	 * -1 代表失败
	 */
	public int deleteDownloadTask(Bundle task){
		Logger.d(TAG + "deleteDownloadTask", task.toString());
		mb_extras.putInt(COMMAND, 3);//删除下载
		this.mb_extras.putAll(task) ;//
		String contentID = mb_extras.getString("contentID");
		String chapterID = mb_extras.getString("chapterID");
		deleteBookInfo(contentID, chapterID);
		startService();
		return 0 ;
	}
	/**
	 * 删除所有任务
	 * @return
	 * 0 代表成功
	 * -1 代表失败
	 */
	public int deleteAllDownloadTask(){
		Logger.d(TAG ,"deleteAllDownloadTask");
		mb_extras.putInt(COMMAND, 5);//删除下载
		deleteBookInfo(null, null);
		startService();
		return 0 ;
	}
	/**
	 * 继续下载,在外面设置好所有数据
	 * @param task
	 * @return
	 * 0 代表成功
	 * -1 代表失败
	 */
	public int goOnDownloadTask(Bundle task){
		Logger.d(TAG, "goOnDownloadTask");
		mb_extras.putInt(COMMAND, 4);//继续下载
		this.mb_extras.putAll(task) ;
		updateBookInfo("goon", mb_extras.getString("contentID"), null);
		startService();
		return 0 ;
	}
	
	/**
	 * 继续下载
	 * @param task
	 * @return
	 * 0 代表成功
	 * -1 代表失败
	 */
	public int goOnDownloadTask(){
		Logger.d(TAG, "goOnDownloadTask");
		mb_extras.putInt(COMMAND, 4);//继续下载
		updateBookInfo("goon", mb_extras.getString("contentID"), null);
		startService();
		return 0 ;
	}
	/**
	 * 新建任务
	 * @return
	 * 0 代表成功
	 * -1 代表失败
	 */
	public int newDownloadTask(Bundle task) {
		Logger.d(TAG, task.toString());
		if(mb_error){
			return -1 ;
		}
		if(!selectByBookID(mb_extras.getString("contentID") , mb_extras.getString("downloadType"))){
			return -1 ;
		}
		if (this.mb_extras.getStringArrayList("idList") == null) {
			if (hasBeanDownload(this.mb_extras.getString("contentID"),
					this.mb_extras.getString("chapterID"), mb_extras
							.getString("name")) != 0) {
				return -1;
			}
		}
		mb_extras.putInt(COMMAND, 0);
		this.mb_extras.putAll(task);
		insertBookInfo();
		mb_extras.putBoolean("downCert", true);
		mb_extras.putBoolean("downMeb", true);
		startService();
		return 0;
	}
	
	/**
	 * 新建任务
	 * @return
	 * 0 代表成功
	 * -1 代表失败
	 */
	public int newDownloadTask(){
		Logger.d(TAG, "newDownloadTask");
		if(mb_error){
			return -1 ;
		}
		
		if(!selectByBookID(mb_extras.getString("contentID") , mb_extras.getString("downloadType"))){
			return -1 ;
		}
		Logger.i(TAG, "OK1");
		if (this.mb_extras.getStringArrayList("idList") == null) {
			if (hasBeanDownload(this.mb_extras.getString("contentID"),
					this.mb_extras.getString("chapterID"), mb_extras
							.getString("name")) != 0) {
				return -1;
			}
		}
		Logger.i(TAG, "OK2");
		insertBookInfo();
		startService();
		return 0 ;
	}
	
	/**
	 * 清除数据
	 */
	public void clear(){
		mb_extras = new Bundle();
		mb_error = false ;
		mb_extras.putBoolean("downCert", false);
		mb_extras.putBoolean("downMeb", false);
		mb_extras.putBoolean("downChapter", false);
		mb_extras.putBoolean("downChapterCert", false);
		mb_extras.putInt(COMMAND, 0);
	}
	
	/**
	 * 插入记录
	 * @param extras 数据
	 */
	private void insertBookInfo(){
		ArrayList<String> list = mb_extras.getStringArrayList("idList");
		ArrayList<String> urlList = mb_extras.getStringArrayList("urlList");
		int end = 1 ;
		if(list != null){
			end = list.size();
			Logger.i(TAG,"chapter rows:" + end) ;
		}
		for(int i = 0 ; i < end ; i ++){
//			Logger.i(TAG, "insert into " + i);
			Date CurTime = new Date(System.currentTimeMillis());
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = formatter.format(CurTime);
	     	ContentValues values = new ContentValues();
	     	values.put(BookInfo.ContentID, mb_extras.getString("contentID"));
	     	values.put(BookInfo.Name, mb_extras.getString("name"));
	        values.put(BookInfo.Catelog, mb_extras.getString("cateLog"));
	        values.put(BookInfo.BookType, mb_extras.getString("bookType"));
	        values.put(BookInfo.PathType,mb_extras.getString("pathType"));
	        values.put(BookInfo.ProcessPer, "0");
	        values.put(BookInfo.Author, mb_extras.getString("author"));
	        values.put(BookInfo.Maker, mb_extras.getString("maker"));
	        values.put(BookInfo.DownloadTime, now);
	        values.put(BookInfo.SaleTime, mb_extras.getString("saleTime"));
	        if(urlList != null){
	        	values.put(BookInfo.URL, (String)urlList.get(i));
	        }else{
	        	values.put(BookInfo.URL, mb_extras.getString("url"));
	        }
	        if(mb_extras.getBoolean("downCert") || mb_extras.getBoolean("downChapterCert")){
	        	values.put(BookInfo.CertStatus, "1");
	        }else{
	        	values.put(BookInfo.CertStatus, "0");
	        }
	        
	        if(mb_extras.getString("bookSize") != null){
	        	values.put(BookInfo.BookSize, mb_extras.getString("bookSize"));
	        }
	        if (mb_extras.getString("Path") == null) {
				String path = Constants.CON_MEB_PATH
						+ mb_extras.getString("contentID");
				path = path + ".meb";
				mb_extras.putString("Path", path);
				
				if(list != null){
					path = Constants.CON_MEB_PATH + mb_extras.getString("contentID") + File.separator
					+ list.get(i) + ".meb";
					mb_extras.putString("Path", path);
				}
			}
			if (mb_extras.getString("CertPath") == null) {
				mb_extras.putString("CertPath", Constants.CON_CERT_PATH
						+ mb_extras.getString("contentID")
						+ Constants.CON_CERT_TYPE);
				if(list != null){
					mb_extras.putString("CertPath", Constants.CON_MEB_PATH + mb_extras.getString("contentID") + File.separator
					+ list.get(i) + Constants.CON_CERT_TYPE);
				}
			}
	        if (mb_extras.getString("chapterID") != null
					&& !"".equals(mb_extras.getString("chapterID"))) {
	        	values.put(BookInfo.ChapterID, mb_extras.getString("chapterID"));
			}
	        if(list != null){
	        	values.put(BookInfo.ChapterID, (String)list.get(i));
	        }
	        if(mb_extras.getString("DownloadStatus") != null){
	        	values.put(BookInfo.DownloadStatus, mb_extras.getString("DownloadStatus"));
	        }else{
	        	values.put(BookInfo.DownloadStatus,"0");
	        }
	        values.put(BookInfo.CertPath,mb_extras.getString("CertPath"));
	        values.put(BookInfo.BookPosition,mb_extras.getString("Path"));
	        values.put(BookInfo.DownloadType,mb_extras.getString("downloadType"));
//	        Logger.i(TAG, values);
	     	this.mcCall.getContentResolver().insert(BookInfo.CONTENT_URI, values);
		}
    }
	
	/**
	 * 是否已经下载过<br>
	 * 
	 * @return 0 没有下载过<br>
	 *         1 已下载完成<br>
	 *         2 已下载失败<br>
	 *         3 正在下载中<br>
	 *         4 错误数据<br>
	 */
	public int hasBeanDownload(String contentID,String chapterID,String bookName) {
		
		if(contentID == null){
			return 4 ;
		}

		Cursor cur = selectBookInfo(contentID,chapterID,bookName);

		if (cur == null || !cur.moveToFirst()) {
			if(cur != null && !cur.isClosed()){
				cur.close();
			}
			return 0;
		}

		String processBar = cur.getString(cur
				.getColumnIndex(BookInfo.ProcessPer));

		if (processBar.contains("100")) {
			cur.close();
			return 1;
		}

		if ("failure".equals(processBar)) {
			cur.close();
			return 2;
		}
		if(cur.isClosed()){
			cur.close();
		}

		return 3;
	}
	
/*	private Cursor selectBookInfo(String contentID,String chapter) {
		Logger.d(TAG, "selectBookInfo");
		String columns[] = new String[] { BookInfo._ID, BookInfo.ContentID,
				BookInfo.Name, BookInfo.Catelog, BookInfo.BookType,
				BookInfo.PathType, BookInfo.ProcessPer, BookInfo.Author,
				BookInfo.Maker, BookInfo.BookPosition, BookInfo.DownloadTime,
				BookInfo.SaleTime };
		String where = BookInfo.ContentID + " = '" + contentID + "'" ;
		if(chapter != null && !"".equals(chapter.trim())){
			where += " and " + BookInfo.ChapterID + " = '" + chapter + "'" ;
		}
		return this.mcCall.getContentResolver().query(
				BookInfo.CONTENT_URI, columns,
				BookInfo.ContentID + " = '" + contentID + "'", null, null);
	}*/
	private Cursor selectBookInfo(String contentID,String chapter,String bookName) {
		Logger.d(TAG, "selectBookInfo");
		String columns[] = new String[] { BookInfo._ID, BookInfo.ContentID,
				BookInfo.Name, BookInfo.Catelog, BookInfo.BookType,
				BookInfo.PathType, BookInfo.ProcessPer, BookInfo.Author,
				BookInfo.Maker, BookInfo.BookPosition, BookInfo.DownloadTime,
				BookInfo.SaleTime,BookInfo.DownloadType };
		String where = BookInfo.ContentID + " = '" + contentID + "'" ;
		if(chapter != null && !"".equals(chapter.trim())){
			where += " and " + BookInfo.ChapterID + " = '" + chapter + "'" ;
		}
		where += " and " + BookInfo.Name + " = '" + bookName + "'" ;
		return this.mcCall.getContentResolver().query(
				BookInfo.CONTENT_URI, columns,
				BookInfo.ContentID + " = '" + contentID + "'", null, null);
	}

	/**
	 * 删除任务
	 * @param processPer
	 * @param contentID
	 */
	private void deleteBookInfo(String contentID,String chapterID) {
		Logger.d(TAG, "contentID:" + contentID + " processPer");
		
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
			this.mcCall.getContentResolver().delete(BookInfo.CONTENT_URI, where.toString(), null);
		}else{
			this.mcCall.getContentResolver().delete(BookInfo.CONTENT_URI, null, null);
		}
	}
	
	/**
	 * 修改进度
	 * @param processPer
	 * @param contentID
	 */
	private void updateBookInfo(String processPer, String contentID,String bookSize) {
		Logger.i(TAG, "contentID:" + contentID + " processPer:" + processPer);

		ContentValues values = new ContentValues();
		if ("failure".equals(processPer)) {
			values.put(BookInfo.DownloadStatus, "3");
		} else if ("pause".equals(processPer)) {
			values.put(BookInfo.DownloadStatus, "1");
		} else if ("100".equals(processPer)) {
			values.put(BookInfo.DownloadStatus, "2");
			values.put(BookInfo.ProcessPer, processPer);
		} else if ("goon".equals(processPer)) {
			values.put(BookInfo.DownloadStatus, "0");
		} else {
			values.put(BookInfo.DownloadStatus, "0");
			values.put(BookInfo.ProcessPer, processPer);
		}
		if (bookSize != null && !"".equals(bookSize.trim())) {
			values.put(BookInfo.BookSize, bookSize);
		}
		if(mb_extras.getBoolean("downCert")){
			values.put(BookInfo.CertStatus, "1");
		}
		this.mcCall.getContentResolver().update(BookInfo.CONTENT_URI, values,
				BookInfo.ContentID + "='" + contentID + "' ", null);//and " + BookInfo.ProcessPer + " < '" + processPer + "'
	}
	
	
	private boolean selectByBookID(String contentID,String downloadType){
		String columns[] = new String[] {BookInfo.DownloadType};
		Cursor cur = this.mcCall.getContentResolver().query(BookInfo.CONTENT_URI, columns,BookInfo.ContentID + "=" + contentID ,null,null);
		if(cur == null || !cur.moveToFirst()){
			if(cur != null){
				cur.close();
			}
			return true;
		}
		int count = cur.getCount() ;
		if(downloadType.contains("6")){
			if(count == this.mcCall.getContentResolver().delete(BookInfo.CONTENT_URI, BookInfo.ContentID + "=" + " AND " + contentID+BookInfo.DownloadType + "=5"  , null))
				Logger.i(TAG, "now ID:...");
			if(cur != null){
				cur.close();
			}
			return true ;
		}else if(downloadType.contains("4")){
            if(count == this.mcCall.getContentResolver().delete(BookInfo.CONTENT_URI, BookInfo.ContentID + "=" + " AND " + contentID+BookInfo.DownloadType + "=3"  , null))
                Logger.i(TAG, "now ID:...");
            if(cur != null){
                cur.close();
            }
            return true ;
        }
		String curDownloadType = cur.getString(cur.getColumnIndex(BookInfo.DownloadType));
		cur.close();
		if(curDownloadType == null|| "".equals(curDownloadType)){
			return true;
		}
		if(curDownloadType.contains(downloadType)){//已经存在了相同的任务
			return false;
		}
		return false ;
	}
	
}
