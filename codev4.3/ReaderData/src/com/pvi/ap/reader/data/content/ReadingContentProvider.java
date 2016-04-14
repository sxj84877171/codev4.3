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
 * ReadingContentProvider save some reading info
 * Access it by providing some interface for query,insert,delete and update
 * @author rd026
 */
public class ReadingContentProvider extends ContentProvider
{
	private static final String				TAG					= "ReadingContentProvider";

	// 表名
	private static final String				READING_TABLE_NAME	= "reading";
	private static HashMap<String, String>	sReadingProjectionMap;
	private static final int				READING				= 1;	
	private static final int				READING_ID			= 2;
	private static final UriMatcher			sUriMatcher;
	private DatabaseHelper	mOpenHelper;

	//创建表SQL语句
	public static final String				CREATETABLE="CREATE TABLE " 
														+ READING_TABLE_NAME 
														+ " (" + Reading._ID 
														+ " INTEGER PRIMARY KEY," 
														+ Reading.UserID 
														+ " TEXT," 
														+ Reading.ContentId 
														+ " TEXT,"
														+ Reading.ChapterId
														+ " TEXT,"
														+ Reading.FilePath
														+ " TEXT,"
														+ Reading.ReadName
														+ " TEXT,"
														+ Reading.ReadTime
														+ " TEXT,"
														+ Reading.CertPath
														+ " TEXT,"
														+ Reading.SourceType
    													+ " INTEGER,"
    													+ Reading.OperationType
    													+ " INTEGER,"
    													+ Reading.SynchFlag
    													+ " INTEGER,"
														+ Reading.ReadPosition
														+ " INTEGER" + ");";
	
	static
	{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(Reading.AUTHORITY, "reading", READING);
		sUriMatcher.addURI(Reading.AUTHORITY, "reading/#", READING_ID);

		sReadingProjectionMap = new HashMap<String, String>();
		sReadingProjectionMap.put(Reading._ID, Reading._ID);
		sReadingProjectionMap.put(Reading.UserID, Reading.UserID);
		sReadingProjectionMap.put(Reading.ContentId, Reading.ContentId);
		sReadingProjectionMap.put(Reading.ChapterId, Reading.ChapterId);
		sReadingProjectionMap.put(Reading.FilePath, Reading.FilePath);
		sReadingProjectionMap.put(Reading.ReadName, Reading.ReadName);
		sReadingProjectionMap.put(Reading.ReadTime, Reading.ReadTime);
		sReadingProjectionMap.put(Reading.SourceType, Reading.SourceType);
		sReadingProjectionMap.put(Reading.ReadPosition, Reading.ReadPosition);
		sReadingProjectionMap.put(Reading.OperationType, Reading.OperationType);
		sReadingProjectionMap.put(Reading.SynchFlag, Reading.SynchFlag);
		sReadingProjectionMap.put(Reading.CertPath, Reading.CertPath);
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
			case READING:
				qb.setTables(READING_TABLE_NAME);
				qb.setProjectionMap(sReadingProjectionMap);
				break;

			case READING_ID:
				qb.setTables(READING_TABLE_NAME);
				qb.setProjectionMap(sReadingProjectionMap);
				qb.appendWhere(Reading._ID + "=" + uri.getPathSegments().get(1));
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder))
		{
			orderBy = Reading.DEFAULT_SORT_ORDER;
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
			case READING:
				return Reading.CONTENT_TYPE;

			case READING_ID:
				return Reading.CONTENT_ITEM_TYPE;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	@Override
	//插入数据库
	public Uri insert(Uri uri, ContentValues initialValues)
	{
		if (sUriMatcher.match(uri) != READING)
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
//		Long now = Long.valueOf(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");   
		Date CurTime = new Date(System.currentTimeMillis());
		String now = formatter.format(CurTime);
//		formatter.format(RegInfo.CreatedDate);
		if (values.containsKey(Reading.ReadTime) == false)
		{	
			values.put(Reading.ReadTime, now);
		}
		if (values.containsKey(Reading.UserID) == false)
		{
			//Resources r = Resources.getSystem();
			//values.put(RegInfo.Production, r.getString(android.R.string.untitled));
			values.put(Reading.UserID, "1010");
		}
		if (values.containsKey(Reading.ContentId) == false)
		{
			values.put(Reading.ContentId, "P001");
		}
		if (values.containsKey(Reading.ChapterId) == false)
		{
			values.put(Reading.ChapterId, "P010");
		}
		if (values.containsKey(Reading.FilePath) == false)
		{
			values.put(Reading.FilePath, "data/file");
		}
		if (values.containsKey(Reading.ReadName) == false)
		{
			values.put(Reading.ReadName, "Three gun");
		}
		if (values.containsKey(Reading.SourceType) == false)
		{
			values.put(Reading.SourceType, "1");
		}
		if (values.containsKey(Reading.ReadPosition) == false)
		{
			values.put(Reading.ReadPosition, "1000");
		}
		if (values.containsKey(Reading.OperationType) == false)
		{
			values.put(Reading.OperationType, "0");
		}
		if (values.containsKey(Reading.SynchFlag) == false)
		{
			values.put(Reading.SynchFlag, "1");
		}
		if (values.containsKey(Reading.CertPath) == false)
		{
			values.put(Reading.CertPath, "1");
		}
		
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			String table = db.findEditTable(READING_TABLE_NAME);
			long rowId = db.insert(READING_TABLE_NAME, null, values);
			if (rowId > 0)
			{
				Uri readUri = ContentUris.withAppendedId(Reading.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(readUri, null);
				return readUri;
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
				case READING:
					count = db.delete(READING_TABLE_NAME, where, whereArgs);
					break;
	
				case READING_ID:
					String readId = uri.getPathSegments().get(1);
					count = db.delete(READING_TABLE_NAME, Reading._ID + "=" + readId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
				case READING:
					count = db.update(READING_TABLE_NAME, values, where, whereArgs);
					break;
	
				case READING_ID:
					String readingId = uri.getPathSegments().get(1);
					count = db.update(READING_TABLE_NAME, values, Reading._ID + "=" + readingId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
