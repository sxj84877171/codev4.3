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
 * OptLogInfoContentProvider save some operation info 
 * Access it by providing some interface for query,insert,delete and update
 * @author rd026
 */
public class OptLogInfoContentProvider extends ContentProvider
{
	private static final String				TAG					= "OptLogInfoContentProvider";

	// 表名
	private static final String				OPTLOGINFO_TABLE_NAME	= "optloginfo";
	private static HashMap<String, String>	sOptLogInfoProjectionMap;
	private static final int				OPTLOGINFO			= 1;	
	private static final int				OPTLOGINFO_ID			= 2;
	private static final UriMatcher			sUriMatcher;
	private DatabaseHelper	mOpenHelper;

	//创建表SQL语句
	public static final String				CREATETABLE="CREATE TABLE " 
														+ OPTLOGINFO_TABLE_NAME 
														+ " (" + OptLogInfo._ID 
														+ " INTEGER PRIMARY KEY," 
														+ OptLogInfo.UserID
														+ " TEXT,"
														+ OptLogInfo.LogType 
														+ " TEXT," 
														+ OptLogInfo.LogConnent 
														+ " TEXT,"
														+ OptLogInfo.CommentTime
														+ " TEXT,"
														+ OptLogInfo.OperationType
														+ " INTEGER,"
														+ OptLogInfo.SynchFlag
														+ " INTEGER" + ");";
	
	static
	{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(OptLogInfo.AUTHORITY, "optloginfo", OPTLOGINFO);
		sUriMatcher.addURI(OptLogInfo.AUTHORITY, "optloginfo/#", OPTLOGINFO_ID);

		sOptLogInfoProjectionMap = new HashMap<String, String>();
		sOptLogInfoProjectionMap.put(OptLogInfo._ID, OptLogInfo._ID);
		sOptLogInfoProjectionMap.put(OptLogInfo.UserID, OptLogInfo.UserID);
		sOptLogInfoProjectionMap.put(OptLogInfo.LogType, OptLogInfo.LogType);
		sOptLogInfoProjectionMap.put(OptLogInfo.LogConnent, OptLogInfo.LogConnent);
		sOptLogInfoProjectionMap.put(OptLogInfo.CommentTime, OptLogInfo.CommentTime);		
		sOptLogInfoProjectionMap.put(OptLogInfo.OperationType, OptLogInfo.OperationType);
		sOptLogInfoProjectionMap.put(OptLogInfo.SynchFlag, OptLogInfo.SynchFlag);

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
			case OPTLOGINFO:
				qb.setTables(OPTLOGINFO_TABLE_NAME);
				qb.setProjectionMap(sOptLogInfoProjectionMap);
				break;

			case OPTLOGINFO_ID:
				qb.setTables(OPTLOGINFO_TABLE_NAME);
				qb.setProjectionMap(sOptLogInfoProjectionMap);
				qb.appendWhere(OptLogInfo._ID + "=" + uri.getPathSegments().get(1));
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder))
		{
			orderBy = OptLogInfo.DEFAULT_SORT_ORDER;
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
			case OPTLOGINFO:
				return OptLogInfo.CONTENT_TYPE;

			case OPTLOGINFO_ID:
				return OptLogInfo.CONTENT_ITEM_TYPE;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	@Override
	//插入数据库
	public Uri insert(Uri uri, ContentValues initialValues)
	{
		if (sUriMatcher.match(uri) != OPTLOGINFO)
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

		if (values.containsKey(OptLogInfo.CommentTime) == false)
		{	
			values.put(OptLogInfo.CommentTime, now);
		}
		if (values.containsKey(OptLogInfo.UserID) == false)
		{	
			values.put(OptLogInfo.UserID, "001");
		}
		if (values.containsKey(OptLogInfo.LogType) == false)
		{
			//Resources r = Resources.getSystem();
			//values.put(RegInfo.Production, r.getString(android.R.string.untitled));
			values.put(OptLogInfo.LogType, "平台");
		}
		if (values.containsKey(OptLogInfo.LogConnent) == false)
		{
			values.put(OptLogInfo.LogConnent, "有新版本，请及时更新");
		}
		if (values.containsKey(OptLogInfo.OperationType) == false)
		{
			values.put(OptLogInfo.OperationType, "1");
		}
		if (values.containsKey(OptLogInfo.SynchFlag) == false)
		{
			values.put(OptLogInfo.SynchFlag, "1");
		}
		
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			String table = db.findEditTable(OPTLOGINFO_TABLE_NAME);
			long rowId = db.insert(OPTLOGINFO_TABLE_NAME, null, values);
			if (rowId > 0)
			{
				Uri bookUri = ContentUris.withAppendedId(OptLogInfo.CONTENT_URI, rowId);
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
				case OPTLOGINFO:
					count = db.delete(OPTLOGINFO_TABLE_NAME, where, whereArgs);
					break;
	
				case OPTLOGINFO_ID:
					String OptLogInfoId = uri.getPathSegments().get(1);
					count = db.delete(OPTLOGINFO_TABLE_NAME, OptLogInfo._ID + "=" + OptLogInfoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
				case OPTLOGINFO:
					count = db.update(OPTLOGINFO_TABLE_NAME, values, where, whereArgs);
					break;
	
				case OPTLOGINFO_ID:
					String OptLogInfoId = uri.getPathSegments().get(1);
					count = db.update(OPTLOGINFO_TABLE_NAME, values, OptLogInfo._ID + "=" + OptLogInfoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
