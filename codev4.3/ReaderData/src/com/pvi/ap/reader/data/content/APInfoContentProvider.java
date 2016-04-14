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

import com.pvi.ap.reader.data.content.G3Read_db.DatabaseHelper;

/**
 * APInfoContentProvider provide some AP Info to activity and service
 * by providing some interface for query,insert,delete and update
 * @author rd026
 *
 */
public class APInfoContentProvider extends ContentProvider
{
	private static final String				TAG					= "APInfoContentProvider";

	// 表名
	private static final String				APINFO_TABLE_NAME	= "apinfo";
	private static HashMap<String, String>	sAPInfoProjectionMap;
	private static final int				APINFO			= 1;	
	private static final int				APINFO_ID			= 2;
	private static final UriMatcher			sUriMatcher;
	private DatabaseHelper	mOpenHelper;

	//创建表SQL语句
	public static final String				CREATETABLE="CREATE TABLE " 
														+ APINFO_TABLE_NAME 
														+ " (" + APInfo._ID 
														+ " INTEGER PRIMARY KEY," 
														+ APInfo.ApName
														+ " TEXT,"
														+ APInfo.ApDeveloper 
														+ " TEXT," 
														+ APInfo.ApVersion 
														+ " TEXT,"
														+ APInfo.ApSize
														+ " TEXT,"
														+ APInfo.ApUpdateTime
														+ " TEXT" + ");";
	
	static
	{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(APInfo.AUTHORITY, "apinfo", APINFO);
		sUriMatcher.addURI(APInfo.AUTHORITY, "apinfo/#", APINFO_ID);

		sAPInfoProjectionMap = new HashMap<String, String>();
		sAPInfoProjectionMap.put(APInfo._ID, APInfo._ID);
		sAPInfoProjectionMap.put(APInfo.ApName, APInfo.ApName);
		sAPInfoProjectionMap.put(APInfo.ApDeveloper, APInfo.ApDeveloper);
		sAPInfoProjectionMap.put(APInfo.ApVersion, APInfo.ApVersion);
		sAPInfoProjectionMap.put(APInfo.ApSize, APInfo.ApSize);		
		sAPInfoProjectionMap.put(APInfo.ApUpdateTime, APInfo.ApUpdateTime);
	}
		
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
			case APINFO:
				qb.setTables(APINFO_TABLE_NAME);
				qb.setProjectionMap(sAPInfoProjectionMap);
				break;

			case APINFO_ID:
				qb.setTables(APINFO_TABLE_NAME);
				qb.setProjectionMap(sAPInfoProjectionMap);
				qb.appendWhere(APInfo._ID + "=" + uri.getPathSegments().get(1));
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder))
		{
			orderBy = APInfo.DEFAULT_SORT_ORDER;
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
			return c;
		}catch(SQLException e){
			return null;
		}
	}
	@Override
	// 如果有自定义类型，必须实现该方法
	public String getType(Uri uri)
	{
		switch (sUriMatcher.match(uri))
		{
			case APINFO:
				return APInfo.CONTENT_TYPE;

			case APINFO_ID:
				return APInfo.CONTENT_ITEM_TYPE;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	@Override
	//插入数据库
	public Uri insert(Uri uri, ContentValues initialValues)
	{
		if (sUriMatcher.match(uri) != APINFO)
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
		
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");   
		Date CurTime = new Date(System.currentTimeMillis());
		String now = formatter.format(CurTime);

		if (values.containsKey(APInfo.ApUpdateTime) == false)
		{	
			values.put(APInfo.ApUpdateTime, now);
		}
		if (values.containsKey(APInfo.ApName) == false)
		{	
			values.put(APInfo.ApName, "股票");
		}
		if (values.containsKey(APInfo.ApDeveloper) == false)
		{
			values.put(APInfo.ApDeveloper, "第三方");
		}
		if (values.containsKey(APInfo.ApVersion) == false)
		{
			values.put(APInfo.ApVersion, "V1.0");
		}
		if (values.containsKey(APInfo.ApSize) == false)
		{
			values.put(APInfo.ApSize, "596K");
		}
		
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			String table = db.findEditTable(APINFO_TABLE_NAME);
			long rowId = db.insert(APINFO_TABLE_NAME, null, values);
			if (rowId > 0)
			{
				Uri apUri = ContentUris.withAppendedId(APInfo.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(apUri, null);
				return apUri;
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
				case APINFO:
					count = db.delete(APINFO_TABLE_NAME, where, whereArgs);
					break;

				case APINFO_ID:
					String apInfoId = uri.getPathSegments().get(1);
					count = db.delete(APINFO_TABLE_NAME, APInfo._ID + "=" + apInfoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
	@Override
	//更新数据
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs)
	{
		int count = 0 ;
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			switch (sUriMatcher.match(uri))
			{
				case APINFO:
					count = db.update(APINFO_TABLE_NAME, values, where, whereArgs);
					break;

				case APINFO_ID:
					String ApInfoId = uri.getPathSegments().get(1);
					count = db.update(APINFO_TABLE_NAME, values, APInfo._ID + "=" + ApInfoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
					break;

				default:
					throw new IllegalArgumentException("Unknown URI " + uri);
			}
			getContext().getContentResolver().notifyChange(uri, null);
		}catch(SQLException e){
			Log.e(TAG, Log.getStackTraceString(e));
		}
		return count;
	}

}
