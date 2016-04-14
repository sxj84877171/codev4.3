package com.pvi.ap.reader.activity.pviappframe;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.pvi.ap.reader.activity.GlobalVar;
import com.pvi.ap.reader.activity.MainpageActivity;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.Bookmark;
import com.pvi.ap.reader.data.content.Favorites;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;



public class InactiveFunction {
	public static HashMap<String, String> VoteFlower(String contentID)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		ahmNamePair.put("contentId", contentID);
		ahmNamePair.put("vote", "1");

		HashMap responseMap = null;

		try {
			// 以POST的形式连接请求
			responseMap = CPManager.submitVote(ahmHeaderMap, ahmNamePair);
			if (responseMap.get("result-code").toString().contains(
			"result-code: 0")) {
				byte[] responseBody = (byte[]) responseMap.get("ResponseBody");

				Document dom = null;
				try {
					dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);

				} catch (ParserConfigurationException e) {
					e.printStackTrace();
					map.put("RetCode", "1");
					map.put("Exception", e.toString());
					return map;
				} catch (SAXException e) {
					e.printStackTrace();
					map.put("RetCode", "1");
					map.put("Exception", e.toString());
					return map;
				} catch (IOException e) {
					e.printStackTrace();
					map.put("RetCode", "1");
					map.put("Exception", e.toString());
					return map;
				}


				Element root = dom.getDocumentElement();

				NodeList nl = root.getElementsByTagName("flowerValue");
				Element element = (Element) nl.item(0);
				if(element==null)
				{
					map.put("RetCode", "3");
					return map;
				}
				else if(element.getFirstChild() == null)
				{
					map.put("RetCode", "3");
					return map;
				}
				else
				{
					map.put("FlowerNum", element.getFirstChild().getNodeValue());
					map.put("RetCode", "0");
					return map;
				}
			}
		}catch (SocketTimeoutException e) {
			e.printStackTrace();
			map.put("RetCode", "1");
			map.put("Exception", e.toString());
			return map;
		}catch (HttpException e) {
			// 连接异常 ,一般原因为 URL错误
			e.printStackTrace();
			map.put("RetCode", "2");
			map.put("Exception", e.toString());
			return map;
		} catch (IOException e) {
			// IO异常 ,一般原因为网络问题
			e.printStackTrace();
			map.put("RetCode", "2");
			map.put("Exception", e.toString());
			return map;
		}
		map.put("RetCode", responseMap.get("result-code").toString());
		return map;
	}
	public static String addFavorite(Activity act,HashMap<String, String> map) {

		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		ahmNamePair.put("contentId", map.get("contentID"));
		HashMap responseMap = null;
		String retstr = "";
		try {
			// 以POST的形式连接请求
			responseMap = CPManager.addFavorite(ahmHeaderMap, ahmNamePair);
			retstr = responseMap.get("result-code").toString();
			if (!responseMap.get("result-code").toString().contains(
			"result-code: 0")) {
				return retstr;
			}
		} catch (HttpException e) {
			// 连接异常 ,一般原因为 URL错误
			e.printStackTrace();

			return "2";

		}catch (SocketTimeoutException e) {
			e.printStackTrace();
			return "1";
		} 
		catch (IOException e) {
			e.printStackTrace();
			return "2";
		}

		GlobalVar app = (GlobalVar)act.getApplicationContext() ;
		ContentValues values = new ContentValues();
		values.put(Favorites.UserID,  app.getUserID());
		values.put(Favorites.ContentId, map.get("contentID"));

		values.put(Favorites.ContentName, map.get("BookName").toString());
		values.put(Favorites.Author, map.get("AuthorName").toString());

		values.put(Favorites.FavoriteTime, GlobalVar.getFormatTime(System.currentTimeMillis()));

		values.put(Favorites.FavoriteURL,  map.get("SmallLogoUrl").toString());
		values.put(Favorites.ChapterId, "");
		values.put(Favorites.ChapterName, "");
		act.getContentResolver().insert(Favorites.CONTENT_URI, values);
		return "0";
	}

	public static void RecommendToFriends(Activity act,String contentID,String chapterID)
	{
		Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle bundle = new Bundle();
		bundle.putString("contentID",contentID);
		bundle.putString("chapterID",chapterID);
		bundle.putString("act",
		"com.pvi.ap.reader.activity.RecommendToFriend");
		bundle.putString("startType", "allwaysCreate");
		bundle.putBoolean("RequestMsisdn", true);
		intent.putExtras(bundle);
		act.sendBroadcast(intent);
	}

	public static void comment(Activity act, String contentID, boolean browse)
	{
		if(browse)
		{
			//查看评论
			Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
			Bundle sndBundle = new Bundle();
			sndBundle.putString("act",
			"com.pvi.ap.reader.activity.CommentsListActivity");
			sndBundle.putString("haveTitleBar", "1");
			sndBundle.putString("startType", "allwaysCreate");
			sndBundle.putString("contentID", contentID);
			intent.putExtras(sndBundle);
			act.sendBroadcast(intent);
		}
		else
		{
			//发表评论
			Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
			Bundle bundleToSend = new Bundle();
			bundleToSend.putString("contentID", contentID);
			bundleToSend.putString("act",
			"com.pvi.ap.reader.activity.NewCommentActivity");
			bundleToSend.putString("startType", "allwaysCreate");
			tmpIntent.putExtras(bundleToSend);
			act.sendBroadcast(tmpIntent);
		}
	}
	public static void AuthorInfo(Activity act,String authorID)
	{
		Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("act",
		"com.pvi.ap.reader.activity.WriterActivity");
		bundleToSend.putString("authorID",authorID);
		tmpIntent.putExtras(bundleToSend);
		act.sendBroadcast(tmpIntent);
	}

	public static void showResult(Activity act,String title, String msg) {
		final PviAlertDialog pd = new PviAlertDialog(act.getParent());
		pd.setTitle(title);
		pd.setMessage(msg,Gravity.CENTER);
		pd.setCanClose(true);
		pd.show();
	}
	public static void GotoMyBookshelf(Activity act,String tabname,String contentID)
	{
		//		tabname ={我的书签，最近阅读，... }
		Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle bunde = new Bundle();
		bunde.putString("act",
		"com.pvi.ap.reader.activity.MyBookshelfActivity");
		bunde.putString("haveTitleBar", "1");
		bunde.putString("startType", "allwaysCreate");
		bunde.putString("SourceType", "4");
		bunde.putString("FilePath", contentID);
		if(!tabname.equals(""))
		{
			bunde.putString("actTabName", tabname); // 跳转到我的书签 ，如果去掉这语句，就会跳到
		}
		intent.putExtras(bunde);
		act.sendBroadcast(intent);
	}

	public static Bitmap GetCoverImage(String ImageUri) {
		URL ImageUrl = null;
		Bitmap bitmap = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			ImageUrl = new URL(ImageUri);
		} catch (Exception e) {
			// Toast.makeText(this, "Exception:" + e.toString(),
			// Toast.LENGTH_SHORT);
			return null;
		}
		try {
			conn = (HttpURLConnection) ImageUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
			conn.disconnect();
		}
		catch(OutOfMemoryError o)
		{
			Logger.i("Reader", "Out of Menory!");
			return null;
		}
		catch (Exception e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.e("Reader", e.toString());
			return null;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return bitmap;
	}

	public static HashMap<String, Object> getReadHistory(Activity act, String s,boolean flag) {//flag==true 代表在线，反之
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.clear();
		String columns[] = new String[]{ 
				Bookmark.BookmarkType,
				Bookmark.ContentId, 
				Bookmark.ChapterId,
				Bookmark.ChapterName,
				Bookmark.ContentName,
				Bookmark.CertPath,
				Bookmark.FilePath,
				Bookmark.FontSize,
				Bookmark.LineSpace,
				Bookmark.CreatedDate,
				Bookmark.Position,
				Bookmark._ID,
				Bookmark.Author,
				Bookmark.SourceType
		};
		Cursor cur = null;
		String where="";
		if(flag){
			where=Bookmark.BookmarkType+"='0' and "+ Bookmark.ContentId + "='" + s+ "'";
		}else{
			where=Bookmark.BookmarkType+"='0' and "+ Bookmark.ContentName + "='" + s + "'";
		}
		Logger.i("Reader", "where: " + where);
		cur = act.managedQuery(Bookmark.CONTENT_URI, columns, where, null, null);
		if(cur==null)
		{
			map.put("chapterid", "");
			return map;
		}
		try{
			if (cur.moveToFirst()){

				do{
					map = new HashMap<String, Object>();
					String chapterid = cur.getString(cur.getColumnIndex(Bookmark.ChapterId));
					if(chapterid==null){
						chapterid="";
					}
					map.put("chapterid", chapterid);
					String temp="";
					//					
					temp = cur.getString(cur.getColumnIndex(Bookmark.ContentId));
					map.put("contentid", temp);
					temp= cur.getString(cur.getColumnIndex(Bookmark.Position));
					map.put("readposition", temp);
					String FontSize=cur.getString(cur.getColumnIndex(Bookmark.FontSize));
					map.put("FontSize", FontSize);
					temp=cur.getString(cur.getColumnIndex(Bookmark.LineSpace));
					map.put("LineSpace", temp);
					temp=cur.getString(cur.getColumnIndex(Bookmark.ChapterName));
					map.put("chapterName", temp);
				}while (cur.moveToNext());
			}
			else
			{
				map.put("chapterid", "");
				return map;
			}
		}catch(Exception e){
			return null;
		}finally{
			if(cur!=null){
				cur.close();
			}
		}
		return map;
	}
	public static HashMap<String, Object> getReadHistory(Activity act, String contentID) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.clear();
		Date lastesttemp = null;
		Date curtemp = null;
		SimpleDateFormat sdf = new SimpleDateFormat();

		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		HashMap responseMap = null;
		
		String columns[] = new String[] { 
		        Bookmark.FontSize,
		        Bookmark.BookmarkId, 
		        Bookmark.BookmarkType,
		        Bookmark.ChapterName,
		        Bookmark.LineSpace,
		        Bookmark.CreatedDate,
		        Bookmark.ChapterId,
		        Bookmark.Position
		};
		String where = Bookmark.BookmarkType + "=0 and "+Bookmark.ContentId + "='" + contentID + "'";

		Cursor cur = act.managedQuery(Bookmark.CONTENT_URI, columns, where, null,null);
		if(cur != null){
			if(cur.moveToFirst()){
			    //Logger.d("M","找到了本地记录！");
				String temp = "";
                try {
                    temp = cur.getString(cur.getColumnIndex(Bookmark.CreatedDate));
                    if( temp != null){
                    	try {
                    		lastesttemp = new Date(temp);
                    	} catch (Exception e) {
                    		e.printStackTrace();
                    	}
                    }
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
				map.put("createTime", temp);
				try {
                    temp = cur.getString(cur.getColumnIndex(Bookmark.ChapterId));
                    map.put("chapterid", temp);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
				try {
                    temp = cur.getString(cur.getColumnIndex(Bookmark.ChapterName));
                    map.put("chapterName", temp);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
				try {
                    temp = cur.getString(cur.getColumnIndex(Bookmark.Position));
                    map.put("readposition", temp);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
			}
			cur.close();
		}

		try {
			responseMap = CPManager
			.getSystemBookmark(ahmHeaderMap, ahmNamePair);
			if (!responseMap.get("result-code").toString().contains(
					"result-code: 0")) {
			    //Logger.d("M","未取得网络书签！");
				return map;
			}else{
			    //Logger.d("M"," 取网络书签");
			}
		} catch (HttpException e) {
			e.printStackTrace();
			return map;
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			//Logger.e("Reader", e.toString());
			return map;
		} catch (IOException e) {
			e.printStackTrace();
			//Logger.e("Reader", e.toString());
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			//Logger.e("Reader", e.toString());
			return map;
		}

		byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
		Document dom = null;
		try {
			dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return map;
		} catch (SAXException e) {
			e.printStackTrace();
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return map;
		}
		Element root = dom.getDocumentElement();

		NodeList nl = root.getElementsByTagName("ContentInfo");
		Element element = null;
		Element temp = null;

		for (int i = 0; i < nl.getLength(); i++) {
			element = (Element) nl.item(i);
			temp = (Element) element.getElementsByTagName("contentID").item(0);
			if (temp!=null&&temp.getFirstChild()!=null&&temp.getFirstChild().getNodeValue()!=null&&temp.getFirstChild().getNodeValue().equals(contentID)) {

				temp = (Element) element.getElementsByTagName("createTime")
				.item(0);
				curtemp = sdf.parse(temp.getFirstChild().getNodeValue(),
						new ParsePosition(0));
				if(curtemp!=null){
				if ((lastesttemp == null) || (curtemp.after(lastesttemp))) {
					lastesttemp = curtemp;
					map.put("createTime", temp.getFirstChild().getNodeValue());
					temp = (Element) element.getElementsByTagName("chapterID")
					.item(0);
					map.put("chapterid", temp.getFirstChild().getNodeValue());
					temp = (Element) element
					.getElementsByTagName("chapterName").item(0);
					map.put("chapterName", temp.getFirstChild().getNodeValue());
					temp = (Element) element.getElementsByTagName("position")
					.item(0);
					map.put("readposition", temp.getFirstChild().getNodeValue());
					return map;
				}
				}
			}
		}
		return map;
	}
	public static HashMap<String, String> submitCommentVote(boolean up, String commentID)
	{
		String flowerValue = "";
		String eggValue = "";
		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		ahmNamePair.put("commentId", commentID);
		HashMap<String, String> retmap = new HashMap<String, String>();

		if(up)
		{
			ahmNamePair.put("vote", "1");
		}
		else
		{
			ahmNamePair.put("vote", "0");
		}

		HashMap responseMap = null;

		try {
			// 以POST的形式连接请求
			responseMap = CPManager.submitCommentVote(ahmHeaderMap, ahmNamePair);
			if (responseMap.get("result-code").toString().contains(
			"result-code: 0")) {
				byte[] responseBody = (byte[]) responseMap.get("ResponseBody");

				Document dom = null;
				try {
					dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
					retmap.put("RetCode", "1");
					return retmap;
				} catch (SAXException e) {
					e.printStackTrace();
					retmap.put("RetCode", "1");
					return retmap;
				} catch (IOException e) {
					e.printStackTrace();
					retmap.put("RetCode", "1");
					return retmap;
				}


				Element root = dom.getDocumentElement();

				NodeList nl = root.getElementsByTagName("flowerValue");
				Element element = (Element) nl.item(0);
				if(element==null)
				{
					retmap.put("RetCode", "3");
					return retmap;
				}
				else if(element.getFirstChild() == null)
				{
					retmap.put("RetCode", "3");
					return retmap;
				}
				else
				{
					flowerValue = element.getFirstChild().getNodeValue();
				}

				nl = root.getElementsByTagName("eggValue");
				element = (Element) nl.item(0);
				if(element==null)
				{
					retmap.put("RetCode", "3");
					return retmap;
				}
				else if(element.getFirstChild() == null)
				{
					retmap.put("RetCode", "3");
					return retmap;
				}
				else
				{
					eggValue = element.getFirstChild().getNodeValue();
				}

				retmap.put("flowerValue", flowerValue);
				retmap.put("eggValue", eggValue);
				retmap.put("RetCode", "0");
				return retmap;

			}
		}catch (SocketTimeoutException e) {
			e.printStackTrace();
			retmap.put("RetCode", "1");
			return retmap;
		}catch (HttpException e) {
			// 连接异常 ,一般原因为 URL错误
			e.printStackTrace();
			retmap.put("RetCode", "2");
			return retmap;
		} catch (IOException e) {
			// IO异常 ,一般原因为网络问题
			e.printStackTrace();
			retmap.put("RetCode", "1");
			return retmap;
		}

		retmap.put("RetCode", responseMap.get("result-code").toString());
		return retmap;
	}
}
