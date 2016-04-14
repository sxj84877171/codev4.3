
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
public class SubScribeContentProvider extends ContentProvider
{
	private static final String				TAG					= "SubScribeContentProvider";

	// 表名
	private static final String				SUBSCRIBE_TABLE_NAME	= "subscribe";
	private static HashMap<String, String>	sSubScribeProjectionMap;
	private static final int				SUBSCRIBE			= 1;	
	private static final int				SUBSCRIBE_ID			= 2;
	private static final UriMatcher			sUriMatcher;
	private DatabaseHelper	mOpenHelper;

	//创建表SQL语句
	public static final String				CREATETABLE="CREATE TABLE " 
														+ SUBSCRIBE_TABLE_NAME 
														+ " (" + SubScribe._ID 
														+ " INTEGER PRIMARY KEY," 
														+ SubScribe.ContentID 
														+ " TEXT," 
														+ SubScribe.Name 
														+ " TEXT,"
														+ SubScribe.UserID
														+ " TEXT,"
														+ SubScribe.Author
														+ " TEXT,"
														+ SubScribe.URL
														+ " TEXT,"
														+ SubScribe.ChapterID
														+ " TEXT,"
														+ SubScribe.ChapterName
														+ " TEXT,"
														+ SubScribe.OrderTime 
														+ " TEXT" + ");";
	
	//映射字段
	static
	{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(SubScribe.AUTHORITY, "subscribe", SUBSCRIBE);
		sUriMatcher.addURI(SubScribe.AUTHORITY, "subscribe/#", SUBSCRIBE_ID);

		sSubScribeProjectionMap = new HashMap<String, String>();
		sSubScribeProjectionMap.put(SubScribe._ID, SubScribe._ID);
		sSubScribeProjectionMap.put(SubScribe.ContentID, SubScribe.ContentID);
		sSubScribeProjectionMap.put(SubScribe.Name, SubScribe.Name);
		sSubScribeProjectionMap.put(SubScribe.UserID, SubScribe.UserID);
		sSubScribeProjectionMap.put(SubScribe.Author, SubScribe.Author);
		sSubScribeProjectionMap.put(SubScribe.URL, SubScribe.URL);
		sSubScribeProjectionMap.put(SubScribe.ChapterID, SubScribe.ChapterID);
		sSubScribeProjectionMap.put(SubScribe.ChapterName, SubScribe.ChapterName);
		sSubScribeProjectionMap.put(SubScribe.OrderTime, SubScribe.OrderTime);
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
			case SUBSCRIBE:
				qb.setTables(SUBSCRIBE_TABLE_NAME);
				qb.setProjectionMap(sSubScribeProjectionMap);
				break;

			case SUBSCRIBE_ID:
				qb.setTables(SUBSCRIBE_TABLE_NAME);
				qb.setProjectionMap(sSubScribeProjectionMap);
				qb.appendWhere(SubScribe._ID + "=" + uri.getPathSegments().get(1));
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder))
		{
			orderBy = SubScribe.DEFAULT_SORT_ORDER;
		}
		else
		{
			orderBy = sortOrder;
		}

		Cursor c = null;
		try{
			SQLiteDatabase db = mOpenHelper.getReadableDatabase();
			c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);	
			c.setNotificationUri(getContext().getContentResolver(), uri);
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
			case SUBSCRIBE:
				return SubScribe.CONTENT_TYPE;

			case SUBSCRIBE_ID:
				return SubScribe.CONTENT_ITEM_TYPE;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	@Override
	//插入数据库
	public Uri insert(Uri uri, ContentValues initialValues)
	{
		if (sUriMatcher.match(uri) != SUBSCRIBE)
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

		if (values.containsKey(SubScribe.OrderTime) == false)
		{	
			values.put(SubScribe.OrderTime, now);
		}
		if (values.containsKey(SubScribe.ContentID) == false)
		{
			values.put(SubScribe.ContentID, "");
		}
		if (values.containsKey(SubScribe.Name) == false)
		{
			values.put(SubScribe.Name, "");
		}
		if (values.containsKey(SubScribe.Author) == false)
		{
			values.put(SubScribe.Author, "");
		}
		if (values.containsKey(SubScribe.URL) == false)
		{
			values.put(SubScribe.URL, "");
		}
		if (values.containsKey(SubScribe.ChapterID) == false)
		{
			values.put(SubScribe.ChapterID, "");
		}
		if (values.containsKey(SubScribe.ChapterName) == false)
		{
			values.put(SubScribe.ChapterName, "");
		}
		if (values.containsKey(SubScribe.UserID) == false)
		{
			values.put(SubScribe.UserID, "");
		}
		
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			String table = db.findEditTable(SUBSCRIBE_TABLE_NAME);
			long rowId = db.insert(SUBSCRIBE_TABLE_NAME, null, values);
			if (rowId > 0)
			{
				Uri SubUri = ContentUris.withAppendedId(SubScribe.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(SubUri, null);
				return SubUri;
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
				case SUBSCRIBE:
					count = db.delete(SUBSCRIBE_TABLE_NAME, where, whereArgs);
					break;

				case SUBSCRIBE_ID:
					String SubId = uri.getPathSegments().get(1);
					count = db.delete(SUBSCRIBE_TABLE_NAME, SubScribe._ID + "=" + SubId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
				case SUBSCRIBE:
					count = db.update(SUBSCRIBE_TABLE_NAME, values, where, whereArgs);
					break;

				case SUBSCRIBE_ID:
					String subId = uri.getPathSegments().get(1);
					count = db.update(SUBSCRIBE_TABLE_NAME, values, SubScribe._ID + "=" + subId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
