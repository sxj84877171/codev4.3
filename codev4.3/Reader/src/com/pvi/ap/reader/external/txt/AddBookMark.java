package com.pvi.ap.reader.external.txt;

import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.Bookmark;

public class AddBookMark {
	
	public static final int MAXNUM = 5 ;
	
	private Activity mCall = null;
	
	public AddBookMark(Activity mCall){
		this.mCall = mCall ;
	}
	
	
	public boolean insertTxtBookmark(Bundle bunde) {
		String[] columns = { Bookmark.FilePath, Bookmark.ContentName ,Bookmark.CreatedDate};
		StringBuilder sb = new StringBuilder();
		String where = "" ;
		ContentValues values = new ContentValues();
		if(bunde.getString("FilePath") != null){
			values.put(Bookmark.FilePath, bunde.getString("FilePath"));
			values.put(Bookmark.ContentName, bunde.getString("FilePath").substring(
					bunde.getString("FilePath").lastIndexOf("/") + 1));
		}
		values.put(Bookmark.UserID, bunde.getString("UserID"));
		if(bunde.getString("bookmarktype") != null){
			values.put(Bookmark.BookmarkType, bunde.getString("bookmarktype"));
		}
		if(bunde.getString("operationtype") != null){
			values.put(Bookmark.OperationType, bunde.getString("operationtype"));
		}
		if(bunde.getString("sourcetype")!= null){
			values.put(Bookmark.SourceType, bunde.getString("sourcetype"));
		}
		if(bunde.getString("position") != null){
			values.put(Bookmark.Position, bunde.getString("position"));
		}
		if(bunde.getString("chaptername") != null){
			values.put(Bookmark.ChapterName,  bunde.getString("chaptername"));
		}
		if(bunde.getString("codeSize") != null){
			values.put(Bookmark.FontSize,bunde.getString("codeSize"));
		}
		if(bunde.getString("linespace") != null){
			values.put(Bookmark.LineSpace, bunde.getString("linespace") );
		}
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");   
		Date CurTime = new Date(System.currentTimeMillis());
		String now = formatter.format(CurTime);
		values.put(Bookmark.CreatedDate, now);
		sb.append(Bookmark.FilePath + " = '" + bunde.getString("FilePath")).append("'");
		if(bunde.getString("bookmarktype") != null){
			sb.append(" and ").append(Bookmark.BookmarkType + " =  '").append(bunde.getString("bookmarktype")).append("' ");
		}
		if(bunde.getString("sourcetype")!= null){
			sb.append(" and ").append(Bookmark.SourceType + " = '").append(bunde.getString("sourcetype")).append("' ");
		}
		if(bunde.getString("operationtype")!= null){
			sb.append(" and ").append(Bookmark.OperationType + " =  '").append(bunde.getString("operationtype")).append("' ");
		}
		if(bunde.getString("position")!= null){
			sb.append(" and ").append(Bookmark.Position + " = '" +  bunde.getString("position"))
			.append("'");
		}
		String sql =Bookmark.FilePath + " = '" + bunde.getString("FilePath") + "' and " + Bookmark.BookmarkType + "='" + 1 + "'";
		if("0".equals(bunde.getString("bookmarktype"))){
			where = Bookmark.FilePath + " = '" + bunde.getString("FilePath") + "' and " + Bookmark.BookmarkType + "='" + 0 + "'"; ;
		}else{
			where = sb.toString();
		}
		Cursor cur = mCall.managedQuery(Bookmark.CONTENT_URI, columns, where,null, null);
		if (cur != null && cur.getCount() != 0) {
			where = Bookmark.BookmarkType + "='" + 0 + "'" + " and " + Bookmark.FilePath + "='" + bunde.getString("FilePath") + "'";
			if("0".equals(bunde.getString("bookmarktype"))){mCall.getContentResolver().update(Bookmark.CONTENT_URI, values,	where, null);}
			return false;
		}
		if (cur != null && !cur.isClosed()) {
			cur.close();cur = null ;
		}
		cur = mCall.managedQuery(Bookmark.CONTENT_URI, columns, sql,null, Bookmark.CreatedDate + " ASC ");
		if (cur != null && cur.getCount() >= MAXNUM && "1".equals(bunde.getString("bookmarktype"))) {
			if(!cur.moveToFirst()) return false;
			where = cur.getString(cur.getColumnIndex(Bookmark.CreatedDate));
			where = Bookmark.CreatedDate + " = '" + where + "' and " + sql ;
			if (!cur.isClosed()) {
				cur.close();
			}
			try {
				mCall.getContentResolver().update(Bookmark.CONTENT_URI, values,	where, null);
				return true;
			} catch (Exception e) {
				Logger.e(this.getClass().getName(), e);
				return false;
			}
		} 
		if(cur != null && !cur.isClosed()){
			cur.close();cur = null ;
		}
		
		if(bunde.getString("codeSize") != null){
			values.put(Bookmark.FontSize, bunde.getString("codeSize"));
		}
		if(bunde.getString("lineSpacingf") != null){
			values.put(Bookmark.LineSpace, bunde.getString("lineSpacingf"));
		}
		
		try {
			mCall.getContentResolver().insert(Bookmark.CONTENT_URI, values);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
