
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
public class LocalBookContentProvider extends ContentProvider
{
	private static final String				TAG					= "LocalBookContentProvider";

	// 表名
	private static final String				LOCALBOOK_TABLE_NAME	= "localbook";
	private static HashMap<String, String>	sLocalBookProjectionMap;
	private static final int				LOCALBOOK			= 1;	
	private static final int				LOCALBOOK_ID		= 2;
	private static final UriMatcher			sUriMatcher;
	private DatabaseHelper	mOpenHelper;

	//创建表SQL语句
	public static final String				CREATETABLE="CREATE TABLE " 
														+ LOCALBOOK_TABLE_NAME 
														+ " (" + LocalBook._ID 
														+ " INTEGER PRIMARY KEY," 
														+ LocalBook.ContentID 
														+ " TEXT," 
														+ LocalBook.Name 
														+ " TEXT,"
														+ LocalBook.Catelog
														+ " TEXT,"
														+ LocalBook.BookType
														+ " INTEGER,"
														+ LocalBook.PathType
														+ " INTEGER,"
														+ LocalBook.Author
														+ " TEXT,"
														+ LocalBook.Maker
														+ " TEXT,"
														+ LocalBook.SaleTime
														+ " TEXT," 
														+ LocalBook.ChapterID
														+ " TEXT,"
														+ LocalBook.BookSize
														+ " TEXT,"
														+ LocalBook.BookPosition 
														+ " TEXT" + ");";
	
	//映射字段
	static
	{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(LocalBook.AUTHORITY, "localbook", LOCALBOOK);
		sUriMatcher.addURI(LocalBook.AUTHORITY, "localbook/#", LOCALBOOK_ID);

		sLocalBookProjectionMap = new HashMap<String, String>();
		sLocalBookProjectionMap.put(LocalBook._ID, LocalBook._ID);
		sLocalBookProjectionMap.put(LocalBook.ContentID, LocalBook.ContentID);
		sLocalBookProjectionMap.put(LocalBook.Name, LocalBook.Name);
		sLocalBookProjectionMap.put(LocalBook.Catelog, LocalBook.Catelog);
		sLocalBookProjectionMap.put(LocalBook.BookType, LocalBook.BookType);
		sLocalBookProjectionMap.put(LocalBook.PathType, LocalBook.PathType);
		sLocalBookProjectionMap.put(LocalBook.Author, LocalBook.Author);
		sLocalBookProjectionMap.put(LocalBook.Maker, LocalBook.Maker);
		sLocalBookProjectionMap.put(LocalBook.SaleTime,LocalBook.SaleTime);
		sLocalBookProjectionMap.put(LocalBook.BookPosition, LocalBook.BookPosition);
		sLocalBookProjectionMap.put(LocalBook.ChapterID, LocalBook.ChapterID);
		sLocalBookProjectionMap.put(LocalBook.BookSize, LocalBook.BookSize);
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
			case LOCALBOOK:
				qb.setTables(LOCALBOOK_TABLE_NAME);
				qb.setProjectionMap(sLocalBookProjectionMap);
				break;

			case LOCALBOOK_ID:
				qb.setTables(LOCALBOOK_TABLE_NAME);
				qb.setProjectionMap(sLocalBookProjectionMap);
				qb.appendWhere(LocalBook._ID + "=" + uri.getPathSegments().get(1));
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder))
		{
			orderBy = LocalBook.DEFAULT_SORT_ORDER;
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
			case LOCALBOOK:
				return LocalBook.CONTENT_TYPE;

			case LOCALBOOK_ID:
				return LocalBook.CONTENT_ITEM_TYPE;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	@Override
	//插入数据库
	public Uri insert(Uri uri, ContentValues initialValues)
	{
		if (sUriMatcher.match(uri) != LOCALBOOK)
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

		if (values.containsKey(LocalBook.SaleTime) == false)
		{	
			values.put(LocalBook.SaleTime, now);
		}
		if (values.containsKey(LocalBook.ContentID) == false)
		{
			values.put(LocalBook.ContentID, "");
		}
		if (values.containsKey(LocalBook.Name) == false)
		{
			values.put(LocalBook.Name, "");
		}
		if (values.containsKey(LocalBook.Catelog) == false)
		{
			values.put(LocalBook.Catelog, "");
		}
		if (values.containsKey(LocalBook.BookType) == false)
		{
			values.put(LocalBook.BookType, "");
		}
		if (values.containsKey(LocalBook.PathType) == false)
		{
			values.put(LocalBook.PathType, "");
		}
		if (values.containsKey(LocalBook.Author) == false)
		{
			values.put(LocalBook.Author, "");
		}
		if (values.containsKey(LocalBook.Maker) == false)
		{
			values.put(LocalBook.Maker, "CMCC");
		}
		if (values.containsKey(LocalBook.BookPosition) == false)
		{
			values.put(LocalBook.BookPosition, "");
		}
		if (values.containsKey(LocalBook.ChapterID) == false)
		{
			values.put(LocalBook.ChapterID, "");
		}
		if (values.containsKey(LocalBook.BookSize) == false)
		{
			values.put(LocalBook.BookSize, "");
		}
		
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			String table = db.findEditTable(LOCALBOOK_TABLE_NAME);
			long rowId = db.insert(LOCALBOOK_TABLE_NAME, null, values);
			if (rowId > 0)
			{
				Uri bookUri = ContentUris.withAppendedId(LocalBook.CONTENT_URI, rowId);
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
				case LOCALBOOK:
					count = db.delete(LOCALBOOK_TABLE_NAME, where, whereArgs);
					break;

				case LOCALBOOK_ID:
					String bookId = uri.getPathSegments().get(1);
					count = db.delete(LOCALBOOK_TABLE_NAME, LocalBook._ID + "=" + bookId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
				case LOCALBOOK:
					count = db.update(LOCALBOOK_TABLE_NAME, values, where, whereArgs);
					break;

				case LOCALBOOK_ID:
					String bookId = uri.getPathSegments().get(1);
					count = db.update(LOCALBOOK_TABLE_NAME, values, LocalBook._ID + "=" + bookId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
