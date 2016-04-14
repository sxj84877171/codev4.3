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
 * RegInfoContentProvider save some register info 
 * Access it by providing some interface for query,insert,delete and update
 * @author rd026
 */
public class RegInfoContentProvider extends ContentProvider
{
	private static final String				TAG					= "RegInfoContentProvider";

	// 表名
	private static final String				REGINFO_TABLE_NAME	= "reginfo";
	private static HashMap<String, String>	sRegInfoProjectionMap;
	private static final int				REGINFO				= 1;	
	private static final int				REGINFO_ID			= 2;
	private static final UriMatcher			sUriMatcher;
	private DatabaseHelper	mOpenHelper;

	//创建表SQL语句
	public static final String				CREATETABLE="CREATE TABLE " 
														+ REGINFO_TABLE_NAME 
														+ " (" + RegInfo._ID 
														+ " INTEGER PRIMARY KEY," 
														+ RegInfo.Production 
														+ " TEXT," 
														+ RegInfo.HardwareVersion 
														+ " TEXT,"
														+ RegInfo.SoftwareVersion
														+ " TEXT,"
														+ RegInfo.DeviceVersion
														+ " TEXT,"
														+ RegInfo.UserAgent
														+ " TEXT,"
														+ RegInfo.APIversion
														+ " TEXT,"
														+ RegInfo.ScreenSize
														+ " TEXT,"
														+ RegInfo.ClientKey
														+ " TEXT,"
														+ RegInfo.IMEI
														+ " TEXT," 
														+ RegInfo.CreatedDate 
														+ " Date" + ");";
	
	static
	{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(RegInfo.AUTHORITY, "reginfo", REGINFO);
		sUriMatcher.addURI(RegInfo.AUTHORITY, "reginfo/#", REGINFO_ID);

		sRegInfoProjectionMap = new HashMap<String, String>();
		sRegInfoProjectionMap.put(RegInfo._ID, RegInfo._ID);
		sRegInfoProjectionMap.put(RegInfo.Production, RegInfo.Production);
		sRegInfoProjectionMap.put(RegInfo.HardwareVersion, RegInfo.HardwareVersion);
		sRegInfoProjectionMap.put(RegInfo.SoftwareVersion, RegInfo.SoftwareVersion);
		sRegInfoProjectionMap.put(RegInfo.DeviceVersion, RegInfo.DeviceVersion);
		sRegInfoProjectionMap.put(RegInfo.UserAgent, RegInfo.UserAgent);
		sRegInfoProjectionMap.put(RegInfo.APIversion, RegInfo.APIversion);
		sRegInfoProjectionMap.put(RegInfo.ScreenSize, RegInfo.ScreenSize);
		sRegInfoProjectionMap.put(RegInfo.ClientKey, RegInfo.ClientKey);
		sRegInfoProjectionMap.put(RegInfo.IMEI, RegInfo.IMEI);
		sRegInfoProjectionMap.put(RegInfo.CreatedDate, RegInfo.CreatedDate);

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
			case REGINFO:
				qb.setTables(REGINFO_TABLE_NAME);
				qb.setProjectionMap(sRegInfoProjectionMap);
				break;

			case REGINFO_ID:
				qb.setTables(REGINFO_TABLE_NAME);
				qb.setProjectionMap(sRegInfoProjectionMap);
				qb.appendWhere(RegInfo._ID + "=" + uri.getPathSegments().get(1));
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder))
		{
			orderBy = RegInfo.DEFAULT_SORT_ORDER;
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
			case REGINFO:
				return RegInfo.CONTENT_TYPE;

			case REGINFO_ID:
				return RegInfo.CONTENT_ITEM_TYPE;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	@Override
	//插入数据库
	public Uri insert(Uri uri, ContentValues initialValues)
	{
		if (sUriMatcher.match(uri) != REGINFO)
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

		if (values.containsKey(RegInfo.CreatedDate) == false)
		{	
			values.put(RegInfo.CreatedDate, now);
		}

		if (values.containsKey(RegInfo.Production) == false)
		{
			values.put(RegInfo.Production, "PVI");
		}
		if (values.containsKey(RegInfo.HardwareVersion) == false)
		{
			values.put(RegInfo.HardwareVersion, "V1.0");
		}
		if (values.containsKey(RegInfo.SoftwareVersion) == false)
		{
			values.put(RegInfo.SoftwareVersion, "PVI_P801_V0.10");
		}
		if (values.containsKey(RegInfo.DeviceVersion) == false)
		{
			values.put(RegInfo.DeviceVersion, "P801");
		}
		if (values.containsKey(RegInfo.UserAgent) == false)
		{
			values.put(RegInfo.UserAgent, "PVI-AGENT");
		}
		if (values.containsKey(RegInfo.APIversion) == false)
		{
			values.put(RegInfo.APIversion, "1.0.0");
		}
		if (values.containsKey(RegInfo.ScreenSize) == false)
		{
			values.put(RegInfo.ScreenSize, "600*800");
		}
		if (values.containsKey(RegInfo.ClientKey) == false)
		{
			values.put(RegInfo.ClientKey, "10");
		}
		if (values.containsKey(RegInfo.IMEI) == false)
		{
			values.put(RegInfo.IMEI, "imei");
		}
		
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			String table = db.findEditTable(REGINFO_TABLE_NAME);
			long rowId = db.insert(REGINFO_TABLE_NAME, null, values);
			if (rowId > 0)
			{
				Uri regUri = ContentUris.withAppendedId(RegInfo.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(regUri, null);
				return regUri;
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
				case REGINFO:
					count = db.delete(REGINFO_TABLE_NAME, where, whereArgs);
					break;
	
				case REGINFO_ID:
					String reginfoId = uri.getPathSegments().get(1);
					count = db.delete(REGINFO_TABLE_NAME, RegInfo._ID + "=" + reginfoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
				case REGINFO:
					count = db.update(REGINFO_TABLE_NAME, values, where, whereArgs);
					break;
	
				case REGINFO_ID:
					String reginfoId = uri.getPathSegments().get(1);
					count = db.update(REGINFO_TABLE_NAME, values, RegInfo._ID + "=" + reginfoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
