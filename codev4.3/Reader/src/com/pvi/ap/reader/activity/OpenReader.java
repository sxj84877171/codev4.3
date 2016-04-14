package com.pvi.ap.reader.activity;

import java.io.File;
import java.util.HashMap;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.Bookmark;
import com.pvi.ap.reader.data.content.CommentsInfo;

public class OpenReader {
	private static Intent sndintent = null;
	private static Bundle sndbundle = null;

	private static String FilePath = "";
	private static String ChapterID = ""; //""：直接Open, or 真正的chapterID
	private static String Offset = "";
	private static String UserID = "";
	private static String CertPath = "";
	private static String sourcetype = "";
	private static String FromPath = "";
	/**
	 * 0 表示成功
	 * -1 文件不存在
	 * @param context
	 * @param map
	 * @return
	 */
	public static int gotoReader(Context context, HashMap<String, Object> map)
	{
		FilePath = map.get("FilePath").toString();
		try
		{
			sourcetype = map.get("SourceType").toString();
		}
		catch(Exception e)
		{
			sourcetype = "";
		}

		String extfile = getFileExt(FilePath).toLowerCase();
		File extF =  new File(FilePath);
		extF.setLastModified(System.currentTimeMillis());
		System.out.println(extF.lastModified());

		sndintent = new Intent(MainpageActivity.START_ACTIVITY);
		sndbundle = new Bundle();


		FromPath = map.get("FromPath").toString();
		sndbundle.putString("FilePath", FilePath);
		sndbundle.putString("ChapterId", map.get("ChapterID").toString());
		if(FromPath.equals("0"))
		{
			Cursor cur = null ;
			//			if(map.get("") == null){
			//				
			//			}
			cur = getBookmarkList((Activity)context, FilePath);
			if(cur != null && cur.moveToFirst()){
				Logger.i("OpenReader", "include read record!");
				map.put("FromPath", "1");
				map.put("FontSize", cur.getString(cur.getColumnIndex(Bookmark.FontSize)));
				Logger.i("OpenReader", "FontSize: " + cur.getColumnIndex(Bookmark.FontSize));
				map.put("LineSpace", cur.getString(cur.getColumnIndex(Bookmark.LineSpace)));
				map.put("Offset", cur.getString(cur.getColumnIndex(Bookmark.Position)));
				map.put("ChapterID", cur.getString(cur.getColumnIndex(Bookmark.ChapterId)));
			}
			if(cur != null && !cur.isClosed()){
				cur.close();
			}
		}

		if(extfile.equals("txt"))
		{ 
			sndbundle.putString("FromPath", map.get("FromPath").toString());
			sndbundle.putString("Offset", map.get("Offset").toString());	
			sndbundle.putString("act","com.pvi.ap.reader.activity.TxtReaderActivity");
			sndbundle.putString("startType",  "allwaysCreate");
			sndbundle.putString("haveStatusBar","1");
			sndbundle.putString("pviapfStatusTip",
					context.getResources().getString(
							R.string.kyleHint01));

			if(!map.get("FromPath").toString().equals("0"))
			{
				sndbundle.putString("LineSpace", map.get("LineSpace").toString());
				sndbundle.putString("FontSize", map.get("FontSize").toString());
			}
			sndintent.putExtras(sndbundle);
			context.sendBroadcast(sndintent);
		}
		else if(extfile.equals("meb"))
		{	
			if(map.containsKey("ContentID") && map.get("ContentID")!=null){
				sndbundle.putString("ContentId", map.get("ContentID").toString());
			}
			if(map.containsKey("FromPath") && map.get("FromPath")!=null){
				sndbundle.putString("FromPath", map.get("FromPath").toString());
			}
			if(map.containsKey("Offset") && map.get("Offset")!=null){
				sndbundle.putString("Offset", map.get("Offset").toString());
			}
			if(map.containsKey("CertPath") && map.get("CertPath")!=null){
				sndbundle.putString("CertPath", map.get("CertPath").toString());
			}
			
			
			
			
			sndbundle.putString("pviapfStatusTip",
					context.getResources().getString(
							R.string.kyleHint01));
			if(map.containsKey("bookType")){
			sndbundle.putString("bookType", map.get("bookType").toString());
			}
			if(map.containsKey("downloadType")
			        &&map.get("downloadType")!=null){
	            sndbundle.putString("downloadType", map.get("downloadType").toString());
	            }
			sndbundle.putString("act","com.pvi.ap.reader.activity.MebViewFileActivity");


			if(!map.get("FromPath").toString().equals("0"))
			{
				sndbundle.putString("ChapterId", map.get("ChapterID").toString());
				Logger.v("ChapterId", map.get("ChapterID").toString());
				sndbundle.putString("LineSpace", map.get("LineSpace").toString());
				sndbundle.putString("FontSize", map.get("FontSize").toString());
			}
			if(map.containsKey("authorName"))
			{
				sndbundle.putString("authorName",  map.get("authorName").toString());
			}
			else
			{
				sndbundle.putString("authorName",  "");
			}

			sndbundle.putString("startType",  "allwaysCreate");
			sndintent.putExtras(sndbundle);
			context.sendBroadcast(sndintent);
		}
		else if(extfile.equals("pdf"))
		{
			Logger.i("pdfReader", "be about to enter PDFReaderActivity");
			sndbundle.putString("FromPath", map.get("FromPath").toString());
			sndbundle.putString(Bookmark.Position, map.get("Offset").toString());
			sndbundle.putString("act","com.pvi.ap.reader.activity.PDFReadActivity");
			sndbundle.putString("startType","reuse");
			if(!map.get("FromPath").toString().equals("0"))
			{
				Logger.i("Open Reader", "into");
				sndbundle.putString("LineSpace", map.get("LineSpace").toString());
				sndbundle.putString("FontSize", map.get("FontSize").toString());
			}
			sndbundle.putString("pviapfStatusTip",
					context.getResources().getString(
							R.string.kyleHint01));
			sndintent.putExtras(sndbundle);
			context.sendBroadcast(sndintent);
		}
		else if(extfile.equals("doc")||extfile.equals("docx"))
		{
			sndintent = new Intent("android.intent.action.VIEW");
			try {
				context.getPackageManager().getApplicationInfo("com.mobisystems.office_registered", PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//Toast.makeText(context, "不能处理的文件类型！", Toast.LENGTH_LONG).show();
				return -1;
			}
			ComponentName comp = new ComponentName("com.mobisystems.office_registered", "com.mobisystems.office.word.WordViewer");
			sndintent.setComponent(comp);
			sndintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			Uri data = Uri.parse("file://" + FilePath);
			sndintent.setData(data);
			context.startActivity(sndintent);
		}
		else if(extfile.equals("xls")||extfile.equals("xlsx"))
		{
			sndintent = new Intent("android.intent.action.VIEW");
			try {
				context.getPackageManager().getApplicationInfo("com.mobisystems.office_registered", PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//				Toast.makeText(context, "不能处理的文件类型！", Toast.LENGTH_LONG).show();
				return -1;
			}

			ComponentName comp = new ComponentName("com.mobisystems.office_registered", "com.mobisystems.office.excel.ExcelViewer");
			sndintent.setComponent(comp);
			sndintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			Uri data = Uri.parse("file://" + FilePath);
			sndintent.setData(data);
			context.startActivity(sndintent);
		}
		else if(extfile.equals("ppt"))
		{
			sndintent = new Intent("android.intent.action.VIEW");
			try {
				context.getPackageManager().getApplicationInfo("com.mobisystems.office_registered", PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				//				Toast.makeText(context, "不能处理的文件类型！", Toast.LENGTH_LONG).show();
				return -1;
			}//12-29 09:37:08.870: INFO/ActivityManager(52): Start proc com.mobisystems.office_registered:powerpoint for activity com.mobisystems.office_registered/com.mobisystems.office.powerpoint.PowerPointViewer: pid=21074 uid=10028 gids={3003, 1015}


			ComponentName comp = new ComponentName("com.mobisystems.office_registered", "com.mobisystems.office.powerpoint.PowerPointViewer");
			sndintent.setComponent(comp);
			sndintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Uri data = Uri.parse("file://" + FilePath);
			sndintent.setData(data);
			context.startActivity(sndintent);
		}
		else
		{
			//			Toast.makeText(context, "不能处理的文件类型！", Toast.LENGTH_LONG).show();
			return -1;
		}
		return 0 ;
	}
	public static String getFileExt(String filename) {
		String s=null;

		s = filename.substring(filename.lastIndexOf(".") + 1);
		return s;
	}

	/**
	 * add 添加对进入上次阅读txt信息的查询
	 * @author Elvis
	 * @param context
	 * @param fileNameStr
	 * @return
	 */
	private static Cursor getBookmarkList(Activity context,String fileNameStr){
		String columns[] = new String[] { Bookmark.UserID, Bookmark.BookmarkId,
				Bookmark.ContentId, Bookmark.ChapterId, Bookmark.ChapterName,
				Bookmark.ContentName, Bookmark.CertPath, Bookmark.FilePath,
				Bookmark.CreatedDate, Bookmark.Position, Bookmark.BookmarkType,
				Bookmark.LineSpace,Bookmark.FontSize};
		String where = Bookmark.BookmarkType + "='" + 0 + "'" + " and " + Bookmark.SourceType+ "='"+ sourcetype +"'" + " and "
		+ Bookmark.FilePath + "='" + fileNameStr + "'"; // filenameString
		return  context.managedQuery(Bookmark.CONTENT_URI, columns, where, null, null) ;
	}

	/**
	 * @author Elvis
	 * 书籍不存在，删除相关书籍
	 * @param context 调用者
	 * @param filePath 文件绝对路径文件名
	 */
	private static void deleteAboutMessage(Activity context,String filePath){
		String where = Bookmark.FilePath + "='" + filePath + "'" ;
		context.getContentResolver().delete(Bookmark.CONTENT_URI, where, null);
		where = CommentsInfo.FilePath + " = '" + filePath + "'";
		context.getContentResolver().delete(CommentsInfo.CONTENT_URI, where, null);
		File file = new File(Constants.CON_FIRST_PAGE_LOCATION + filePath.replace("/", "."));
		file.delete();
	}
}
