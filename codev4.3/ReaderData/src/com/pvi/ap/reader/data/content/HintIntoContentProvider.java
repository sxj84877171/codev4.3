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

import com.pvi.ap.reader.data.content.G3Read_db.DatabaseHelper;

/**
 * HintIntoContentProvider save some hint info
 * Access it by providing some interface for query,insert,delete and update
 * @author rd026
 */
public class HintIntoContentProvider extends ContentProvider
{
	private static final String				TAG					= "HintIntoContentProvider";
	
	// 表名
	private static final String				HINTINTO_TABLE_NAME	= "hintinto";
	private static HashMap<String, String>	sHintIntoProjectionMap;
	private static final int				HINTINTO			= 1;	
	private static final int				HINTINTO_ID			= 2;
	private static final UriMatcher			sUriMatcher;
	private DatabaseHelper	mOpenHelper;

	//创建表SQL语句
	public static final String				CREATETABLE="CREATE TABLE " 
														+ HINTINTO_TABLE_NAME 
														+ " (" + HintInto._ID 
														+ " INTEGER PRIMARY KEY," 
														+ HintInto.MessageID
														+ " TEXT,"
														+ HintInto.MessageType 
														+ " TEXT," 
														+ HintInto.MessageContent 
														+ " TEXT,"
														+ HintInto.UpdateTime
														+ " TEXT" + ");";
	
	static
	{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(HintInto.AUTHORITY, "hintinto", HINTINTO);
		sUriMatcher.addURI(HintInto.AUTHORITY, "hintinto/#", HINTINTO_ID);

		sHintIntoProjectionMap = new HashMap<String, String>();
		sHintIntoProjectionMap.put(HintInto._ID, HintInto._ID);
		sHintIntoProjectionMap.put(HintInto.MessageID, HintInto.MessageID);
		sHintIntoProjectionMap.put(HintInto.MessageType, HintInto.MessageType);
		sHintIntoProjectionMap.put(HintInto.MessageContent, HintInto.MessageContent);
		sHintIntoProjectionMap.put(HintInto.UpdateTime, HintInto.UpdateTime);

	}

	@Override
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
			case HINTINTO:
				qb.setTables(HINTINTO_TABLE_NAME);
				qb.setProjectionMap(sHintIntoProjectionMap);
				break;

			case HINTINTO_ID:
				qb.setTables(HINTINTO_TABLE_NAME);
				qb.setProjectionMap(sHintIntoProjectionMap);
				qb.appendWhere(HintInto._ID + "=" + uri.getPathSegments().get(1));
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder))
		{
			orderBy = HintInto.DEFAULT_SORT_ORDER;
		}
		else
		{
			orderBy = sortOrder;
		}
		
		try{
			SQLiteDatabase db = mOpenHelper.getReadableDatabase();
			
			Cursor c = null;
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
			case HINTINTO:
				return HintInto.CONTENT_TYPE;

			case HINTINTO_ID:
				return HintInto.CONTENT_ITEM_TYPE;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	@Override
	//插入数据库
	public Uri insert(Uri uri, ContentValues initialValues)
	{
		if (sUriMatcher.match(uri) != HINTINTO)
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

		if (values.containsKey(HintInto.UpdateTime) == false)
		{	
			values.put(HintInto.UpdateTime, now);
		}
		if (values.containsKey(HintInto.MessageID) == false)
		{	
			values.put(HintInto.MessageID, "001");
		}
		if (values.containsKey(HintInto.MessageType) == false)
		{
			values.put(HintInto.MessageType, "平台");
		}
		if (values.containsKey(HintInto.MessageContent) == false)
		{
			values.put(HintInto.MessageContent, "请升级");
		}
		
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			String table = db.findEditTable(HINTINTO_TABLE_NAME);
			long rowId = db.insert(HINTINTO_TABLE_NAME, null, values);
			if (rowId > 0)
			{
				Uri hintintoUri = ContentUris.withAppendedId(HintInto.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(hintintoUri, null);
				return hintintoUri;
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
				case HINTINTO:
					count = db.delete(HINTINTO_TABLE_NAME, where, whereArgs);
					break;
	
				case HINTINTO_ID:
					String hintintoId = uri.getPathSegments().get(1);
					count = db.delete(HINTINTO_TABLE_NAME, HintInto._ID + "=" + hintintoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			int count;
			switch (sUriMatcher.match(uri))
			{
				case HINTINTO:
					count = db.update(HINTINTO_TABLE_NAME, values, where, whereArgs);
					break;
	
				case HINTINTO_ID:
					String CommentsInfoId = uri.getPathSegments().get(1);
					count = db.update(HINTINTO_TABLE_NAME, values, HintInto._ID + "=" + CommentsInfoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
