package com.pvi.ap.reader.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.data.common.FileHelper;
import com.pvi.ap.reader.data.common.Logger;

/**
 * the system update main thread
 * <br>the class achive the file download
 * <br>the class hava main method:
 * <br>run,downloadFile
 * @author 孙向锦
 * @version V1.0.0
 * <p><p><br>
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class SystemUpdateThread extends Thread {
	private final static String TAG = "SystemUpdateThread" + "::";
	public String mAppName = "Reader.apk" ;
	private Context mContext = null ;
	private Document dom = null;
	public SystemUpdateThread(Context context){
		this.mContext = context ;
	}
	
	public Document getDom() {
		return dom;
	}

	public void setDom(Document dom) {
		this.dom = dom;
	}

	@Override
	public void run() {
		File filePath = new File(Constants.CON_DOWNLOAD_PATH);
		if(!filePath.exists()){
			filePath.mkdirs();
		}
		
		File sourceFile = new File(Constants.CON_DOWNLOAD_PATH + mAppName);
		
		if(sourceFile.exists()){
			FileHelper.cutTo(Constants.CON_DOWNLOAD_PATH+ mAppName, Constants.CON_SDCARD_PATH + mAppName);
		}
		
		File outputFile = new File(Constants.CON_DOWNLOAD_PATH + mAppName);
		
		if (!downloadFile(Config.getString("SoftwareUpdate_URL"), outputFile)){
//			Logger.e(TAG, mContext.getString(R.string.system_soft_download_fa));
			return ;
		}
		// Log.d(TAG + methodName, SystemUpdateService.DOWNLOADNAME + "Reader.apk download OK" );
//		saveUpdateData();
//		Toast.makeText(mContext, "下载成功，程序即将安装", Toast.LENGTH_LONG).show();
//		Intent myIntent = new Intent("com.pvi.ap.reader.service.ReaderUpdatePackage");
//		mContext.sendBroadcast(myIntent);
		Intent inten = new Intent(Intent.ACTION_VIEW);
		inten.setDataAndType(Uri.parse("file://" + Constants.CON_DOWNLOAD_PATH + mAppName),
				"application/vnd.android.package-archive");
		inten.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(inten);

	}
	/**
	 * Download the files
	 * <br>the method is download file;
	 * @param url download url
	 * @param outputFile download the file
	 * @return boolean whether success
	 */
	public boolean downloadFile(String url, File outputFile) {
		if(!outputFile.exists()){
			try {
				outputFile.createNewFile();
			} catch (IOException e) {
				Logger.e(TAG, e.toString());
				return false;
			}
		}
		String methodName = "downloadFile" ;
		int eception = 0 ;
		// Log.d(TAG + methodName, "download apk start...");

		try {
			// 轉換稱URL
			URL u = new URL(url);
			
			URLConnection con = u.openConnection();
			int len = con.getContentLength() ;
			FileDownloadThread fdt = new FileDownloadThread(u,outputFile,0,len);
			fdt.start();
			boolean unFinsh = true ;
			while(unFinsh){
				if(fdt.getRunException() != 0 && eception <= 5){
					eception ++ ;
					fdt = new FileDownloadThread(u,outputFile,0,len);
					fdt.start();
				}
				if(eception > 5){
					return false ;
				}
				if(fdt.isFinished()){
					unFinsh = false ;
				}
				try{
					Thread.sleep(1000);
				}catch(Exception e){
					Log.e(TAG + methodName, e.toString());
					return false ;
				}
			}
			
			return true ;

		} catch (IOException e) {
			Log.e(TAG + methodName, e.toString());
			return false;
		}
	}
	
	/**
	 * Delete the file
	 * @param fileSource the file
	 * @return boolean whether delete success
	 */
	private boolean deleteOnExitFile(String fileSource) {

		File file = new File(fileSource);
		return file.delete();
	}
	
	/**
	 * save the update message
	 * <p>the method is save client version message ;
	 * @param map the message map
	 */
	private void saveUpdateData() {
		
		if(dom == null){
			return ;
		}
		
		Element root = dom.getDocumentElement();

		Node n1 = root.getElementsByTagName("updateVersion").item(0);

		String updateVersion = n1.getFirstChild().getNodeValue();

		Node n2 = root.getElementsByTagName("mustUpdate").item(0);

		String mustUpdate = n2.getFirstChild().getNodeValue();

		Node n3 = root.getElementsByTagName("updateMessage").item(0);

		String updateMessage = n3.getFirstChild().getNodeValue();

		// save to database (updateVersion,mustUpdate,updateMessage)
	}
}
