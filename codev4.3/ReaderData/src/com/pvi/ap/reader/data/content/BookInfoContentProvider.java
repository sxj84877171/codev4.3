
package com.pvi.ap.reader.data.content;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.G3Read_db.DatabaseHelper;

/**
 * BookInfoContentProvider provide some book information to activity and service  
 * by providing some interface for query,insert,delete and update
 * 
 * @author rd026
 *
 */
public class BookInfoContentProvider extends ContentProvider
{
	private static final String				TAG					= "BookInfoContentProvider";

	// 表名
	private static final String				BOOKINFO_TABLE_NAME	= "bookinfo";
	private static HashMap<String, String>	sBookInfoProjectionMap;
	private static final int				BOOKINFO			= 1;	
	private static final int				BOOKINFO_ID			= 2;
	private static final UriMatcher			sUriMatcher;
	private DatabaseHelper	mOpenHelper;

	//创建表SQL语句
	public static final String				CREATETABLE="CREATE TABLE " 
														+ BOOKINFO_TABLE_NAME 
														+ " (" + BookInfo._ID 
														+ " INTEGER PRIMARY KEY," 
														+ BookInfo.ContentID 
														+ " TEXT," 
														+ BookInfo.Name 
														+ " TEXT,"
														+ BookInfo.Catelog
														+ " TEXT,"
														+ BookInfo.BookType
														+ " INTEGER,"
														+ BookInfo.PathType
														+ " INTEGER,"
														+ BookInfo.ProcessPer
														+ " INTEGER,"
														+ BookInfo.DownloadStatus
														+ " INTEGER,"
														+ BookInfo.DownloadTime
														+ " Date,"
														+ BookInfo.Author
														+ " TEXT,"
														+ BookInfo.Maker
														+ " TEXT,"
														+ BookInfo.SaleTime
														+ " TEXT," 
														+ BookInfo.CertPath
														+ " TEXT,"
														+ BookInfo.URL
														+ " TEXT,"
														+ BookInfo.ChapterID
														+ " TEXT,"
														+ BookInfo.BookSize
														+ " TEXT,"
														+ BookInfo.CertStatus
														+ " TEXT,"
														+ BookInfo.BookPosition 
														+ " TEXT,"
														+ BookInfo.DownloadType 
                                                        + " INTEGER"
														+ ");";
	
	//映射字段
	static
	{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(BookInfo.AUTHORITY, "bookinfo", BOOKINFO);
		sUriMatcher.addURI(BookInfo.AUTHORITY, "bookinfo/#", BOOKINFO_ID);

		sBookInfoProjectionMap = new HashMap<String, String>();
		sBookInfoProjectionMap.put(BookInfo._ID, BookInfo._ID);
		sBookInfoProjectionMap.put(BookInfo.ContentID, BookInfo.ContentID);
		sBookInfoProjectionMap.put(BookInfo.Name, BookInfo.Name);
		sBookInfoProjectionMap.put(BookInfo.Catelog, BookInfo.Catelog);
		sBookInfoProjectionMap.put(BookInfo.BookType, BookInfo.BookType);
		sBookInfoProjectionMap.put(BookInfo.PathType, BookInfo.PathType);
		sBookInfoProjectionMap.put(BookInfo.ProcessPer, BookInfo.ProcessPer);
		sBookInfoProjectionMap.put(BookInfo.DownloadTime, BookInfo.DownloadTime);
		sBookInfoProjectionMap.put(BookInfo.Author, BookInfo.Author);
		sBookInfoProjectionMap.put(BookInfo.Maker, BookInfo.Maker);
		sBookInfoProjectionMap.put(BookInfo.SaleTime, BookInfo.SaleTime);
		sBookInfoProjectionMap.put(BookInfo.BookPosition, BookInfo.BookPosition);
		sBookInfoProjectionMap.put(BookInfo.CertPath, BookInfo.CertPath);
		sBookInfoProjectionMap.put(BookInfo.URL, BookInfo.URL);
		sBookInfoProjectionMap.put(BookInfo.ChapterID, BookInfo.ChapterID);
		sBookInfoProjectionMap.put(BookInfo.BookSize, BookInfo.BookSize);
		sBookInfoProjectionMap.put(BookInfo.DownloadStatus, BookInfo.DownloadStatus);
		sBookInfoProjectionMap.put(BookInfo.DownloadType, BookInfo.DownloadType);
	}
	
	@Override
	//实例化一个DatabaseHelper对象
	public boolean onCreate()
	{
		try{
			mOpenHelper = new G3Read_db().new DatabaseHelper(getContext());
			return true;
		}catch(SQLException e){
			return false;
		}
	}
	
	@Override
	//查询操作
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (sUriMatcher.match(uri))
		{
			case BOOKINFO:
				qb.setTables(BOOKINFO_TABLE_NAME);
				qb.setProjectionMap(sBookInfoProjectionMap);
				break;

			case BOOKINFO_ID:
				qb.setTables(BOOKINFO_TABLE_NAME);
				qb.setProjectionMap(sBookInfoProjectionMap);
				qb.appendWhere(BookInfo._ID + "=" + uri.getPathSegments().get(1));
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder))
		{
			orderBy = BookInfo.DEFAULT_SORT_ORDER;
		}
		else
		{
			orderBy = sortOrder;
		}

		Cursor c = null;
		try{
			SQLiteDatabase db = mOpenHelper.getReadableDatabase();
			db.beginTransaction();
			c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);	
			c.setNotificationUri(getContext().getContentResolver(), uri);
			db.endTransaction();
		}catch(SQLException e){	
			return null;
		}
		return c;
	}
	@Override
	// 如果有自定义类型，必须实现该方法
	public String getType(Uri uri)
	{
		switch (sUriMatcher.match(uri))
		{
			case BOOKINFO:
				return BookInfo.CONTENT_TYPE;

			case BOOKINFO_ID:
				return BookInfo.CONTENT_ITEM_TYPE;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	@Override
	//插入数据库
	public Uri insert(Uri uri, ContentValues initialValues)
	{
		if (sUriMatcher.match(uri) != BOOKINFO)
		{
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		ContentValues values;
		if (initialValues != null)
		{
			values = new ContentValues(initialValues);
		}
		else
		{
			values = new ContentValues();
		}

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");   
		Date CurTime = new Date(System.currentTimeMillis());
		String now = formatter.format(CurTime);

		if (values.containsKey(BookInfo.DownloadTime) == false)
		{	
			values.put(BookInfo.DownloadTime, now);
		}
		if (values.containsKey(BookInfo.SaleTime) == false)
		{	
			values.put(BookInfo.SaleTime, now);
		}
		if (values.containsKey(BookInfo.ContentID) == false)
		{
			//Resources r = Resources.getSystem();
			//values.put(RegInfo.Production, r.getString(android.R.string.untitled));
			values.put(BookInfo.ContentID, "");
		}
		if (values.containsKey(BookInfo.Name) == false)
		{
			values.put(BookInfo.Name, "");
		}
		if (values.containsKey(BookInfo.Catelog) == false)
		{
			values.put(BookInfo.Catelog, "");
		}
		if (values.containsKey(BookInfo.BookType) == false)
		{
			values.put(BookInfo.BookType, "");
		}
		if (values.containsKey(BookInfo.PathType) == false)
		{
			values.put(BookInfo.PathType, "");
		}
		if (values.containsKey(BookInfo.ProcessPer) == false)
		{
			values.put(BookInfo.ProcessPer, "");
		}
		if (values.containsKey(BookInfo.Author) == false)
		{
			values.put(BookInfo.Author, "");
		}
		if (values.containsKey(BookInfo.Maker) == false)
		{
			values.put(BookInfo.Maker, "CMCC");
		}
		if (values.containsKey(BookInfo.BookPosition) == false)
		{
			values.put(BookInfo.BookPosition, "");
		}
		if (values.containsKey(BookInfo.CertPath) == false)
		{
			values.put(BookInfo.CertPath, "");
		}
		if (values.containsKey(BookInfo.URL) == false)
		{
			values.put(BookInfo.URL, "");
		}
		if (values.containsKey(BookInfo.ChapterID) == false)
		{
			values.put(BookInfo.ChapterID, "");
		}
		if (values.containsKey(BookInfo.BookSize) == false)
		{
			values.put(BookInfo.BookSize, "");
		}
		if (values.containsKey(BookInfo.DownloadStatus) == false)
		{
			values.put(BookInfo.DownloadStatus, "");
		}
		if (values.containsKey(BookInfo.DownloadType) == false)
        {
            values.put(BookInfo.DownloadType, "");
        }
		
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			String table = db.findEditTable(BOOKINFO_TABLE_NAME);
			long rowId = db.insert(BOOKINFO_TABLE_NAME, null, values);
			if (rowId > 0)
			{
				Uri bookUri = ContentUris.withAppendedId(BookInfo.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(bookUri, null);
				return bookUri;
			}
		}catch(SQLException e){
			return null;
		}
		
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	//删除数据
	public int delete(Uri uri, String where, String[] whereArgs)
	{
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			int count;
			switch (sUriMatcher.match(uri))
			{
				case BOOKINFO:
					count = db.delete(BOOKINFO_TABLE_NAME, where, whereArgs);
					break;

				case BOOKINFO_ID:
					String reginfoId = uri.getPathSegments().get(1);
					count = db.delete(BOOKINFO_TABLE_NAME, BookInfo._ID + "=" + reginfoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
					break;

				default:
					throw new IllegalArgumentException("Unknown URI " + uri);
			}
			getContext().getContentResolver().notifyChange(uri, null);
			return count;
		}catch(SQLException e){
			return  -1;
		}	
	}
	@Override
	//更新数据
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs)
	{
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			int count;
			switch (sUriMatcher.match(uri))
			{
				case BOOKINFO:
					count = db.update(BOOKINFO_TABLE_NAME, values, where, whereArgs);
					break;

				case BOOKINFO_ID:
					String bookinfoId = uri.getPathSegments().get(1);
					count = db.update(BOOKINFO_TABLE_NAME, values, BookInfo._ID + "=" + bookinfoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
					break;

				default:
					throw new IllegalArgumentException("Unknown URI " + uri);
			}
			getContext().getContentResolver().notifyChange(uri, null);
			return count;
		}catch(SQLException e){
			return -1;
		}	
	}

}
