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
 * UserInfoContentProvider save register user info
 * Access it by providing some interface for query,insert,delete and update
 * @author rd026
 */
public class UserInfoContentProvider extends ContentProvider
{
	private static final String				TAG					= "UserInfoContentProvider";
	
	// 表名
	private static final String				USERINFO_TABLE_NAME	= "userinfo";
	private static HashMap<String, String>	sUserInfoProjectionMap;
	private static final int				USERINFO			= 1;	
	private static final int				USERINFO_ID			= 2;
	private static final UriMatcher			sUriMatcher;
	private DatabaseHelper	mOpenHelper;

	//创建表SQL语句
	public static final String				CREATETABLE="CREATE TABLE " 
														+ USERINFO_TABLE_NAME 
														+ " (" + UserInfo._ID 
														+ " INTEGER PRIMARY KEY," 
														+ UserInfo.UserID
														+ " TEXT,"
														+ UserInfo.SimID
														+ " TEXT,"
														+ UserInfo.UserName 
														+ " TEXT," 
														+ UserInfo.NickName 
														+ " TEXT,"
														+ UserInfo.LineNum
														+ " TEXT,"
														+ UserInfo.UserMoney
														+ " TEXT,"
														+ UserInfo.RegCode
														+ " TEXT,"
														+ UserInfo.Description
														+ " TEXT,"
														+ UserInfo.Signature
														+ " TEXT,"
														+ UserInfo.UserLevel
														+ " INTEGER,"
														+ UserInfo.HeadID
														+ " INTEGER,"
														+ UserInfo.Sex
														+ " INTEGER,"
														+ UserInfo.Age
														+ " INTEGER,"
														+ UserInfo.Province
														+ " TEXT,"
														+ UserInfo.City
														+ " TEXT,"						
														+ UserInfo.BookUpdateType
														+ " INTEGER,"
														+ UserInfo.ModifyFlag
														+ " INTEGER,"
														+ UserInfo.RegisterTime
														+ " Date" + ");";
	
	static
	{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(UserInfo.AUTHORITY, "userinfo", USERINFO);
		sUriMatcher.addURI(UserInfo.AUTHORITY, "userinfo/#", USERINFO_ID);

		sUserInfoProjectionMap = new HashMap<String, String>();
		sUserInfoProjectionMap.put(UserInfo._ID, UserInfo._ID);
		sUserInfoProjectionMap.put(UserInfo.UserID, UserInfo.UserID);
		sUserInfoProjectionMap.put(UserInfo.UserName, UserInfo.UserName);
		sUserInfoProjectionMap.put(UserInfo.NickName, UserInfo.NickName);
		sUserInfoProjectionMap.put(UserInfo.LineNum, UserInfo.LineNum);		
		sUserInfoProjectionMap.put(UserInfo.RegCode, UserInfo.RegCode);
		sUserInfoProjectionMap.put(UserInfo.UserMoney, UserInfo.UserMoney);
		sUserInfoProjectionMap.put(UserInfo.UserLevel, UserInfo.UserLevel);
		sUserInfoProjectionMap.put(UserInfo.HeadID, UserInfo.HeadID);
		sUserInfoProjectionMap.put(UserInfo.Sex, UserInfo.Sex);
		sUserInfoProjectionMap.put(UserInfo.Age, UserInfo.Age);
		sUserInfoProjectionMap.put(UserInfo.Province, UserInfo.Province);
		sUserInfoProjectionMap.put(UserInfo.City, UserInfo.City);
		sUserInfoProjectionMap.put(UserInfo.BookUpdateType, UserInfo.BookUpdateType);
		sUserInfoProjectionMap.put(UserInfo.Description, UserInfo.Description);
		sUserInfoProjectionMap.put(UserInfo.Signature, UserInfo.Signature);
		sUserInfoProjectionMap.put(UserInfo.ModifyFlag, UserInfo.ModifyFlag);
		sUserInfoProjectionMap.put(UserInfo.RegisterTime, UserInfo.RegisterTime);
		sUserInfoProjectionMap.put(UserInfo.SimID, UserInfo.SimID);
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
			case USERINFO:
				qb.setTables(USERINFO_TABLE_NAME);
				qb.setProjectionMap(sUserInfoProjectionMap);
				break;

			case USERINFO_ID:
				qb.setTables(USERINFO_TABLE_NAME);
				qb.setProjectionMap(sUserInfoProjectionMap);
				qb.appendWhere(UserInfo._ID + "=" + uri.getPathSegments().get(1));
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder))
		{
			orderBy = UserInfo.DEFAULT_SORT_ORDER;
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
			case USERINFO:
				return UserInfo.CONTENT_TYPE;

			case USERINFO_ID:
				return UserInfo.CONTENT_ITEM_TYPE;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	@Override
	//插入数据库
	public Uri insert(Uri uri, ContentValues initialValues)
	{
		if (sUriMatcher.match(uri) != USERINFO)
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

		if (values.containsKey(UserInfo.RegisterTime) == false)
		{	
			values.put(UserInfo.RegisterTime, now);
		}
		if (values.containsKey(UserInfo.UserID) == false)
		{	
			values.put(UserInfo.UserID, "");
		}
		if (values.containsKey(UserInfo.UserName) == false)
		{
			values.put(UserInfo.UserName, "");
		}
		if (values.containsKey(UserInfo.NickName) == false)
		{
			values.put(UserInfo.NickName, "");
		}
		if (values.containsKey(UserInfo.LineNum) == false)
		{
			values.put(UserInfo.LineNum, "");
		}
		if (values.containsKey(UserInfo.UserMoney) == false)
		{
			values.put(UserInfo.UserMoney, "");
		}
		if (values.containsKey(UserInfo.RegCode) == false)
		{
			values.put(UserInfo.RegCode, "");
		}
		if (values.containsKey(UserInfo.Description) == false)
		{
			values.put(UserInfo.Description, "");
		}
		if (values.containsKey(UserInfo.Signature) == false)
		{
			values.put(UserInfo.Signature, "");
		}
		if (values.containsKey(UserInfo.UserLevel) == false)
		{
			values.put(UserInfo.UserLevel, "");
		}
		if (values.containsKey(UserInfo.HeadID) == false)
		{
			values.put(UserInfo.HeadID, "");
		}
		if (values.containsKey(UserInfo.Sex) == false)
		{
			values.put(UserInfo.Sex, "1");
		}
		if (values.containsKey(UserInfo.Age) == false)
		{
			values.put(UserInfo.Age, "");
		}
		if (values.containsKey(UserInfo.Province) == false)
		{
			values.put(UserInfo.Province, "");
		}
		if (values.containsKey(UserInfo.City) == false)
		{
			values.put(UserInfo.City, "");
		}
		if (values.containsKey(UserInfo.BookUpdateType) == false)
		{
			values.put(UserInfo.BookUpdateType, "");
		}
		if (values.containsKey(UserInfo.ModifyFlag) == false)
		{
			values.put(UserInfo.ModifyFlag, "");
		}
		
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			String table = db.findEditTable(USERINFO_TABLE_NAME);
			long rowId = db.insert(USERINFO_TABLE_NAME, null, values);
			if (rowId > 0)
			{
				Uri userUri = ContentUris.withAppendedId(UserInfo.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(userUri, null);
				return userUri;
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
				case USERINFO:
					count = db.delete(USERINFO_TABLE_NAME, where, whereArgs);
					break;
	
				case USERINFO_ID:
					String userinfoId = uri.getPathSegments().get(1);
					count = db.delete(USERINFO_TABLE_NAME, UserInfo._ID + "=" + userinfoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
				case USERINFO:
					count = db.update(USERINFO_TABLE_NAME, values, where, whereArgs);
					break;
	
				case USERINFO_ID:
					String userinfoId = uri.getPathSegments().get(1);
					count = db.update(USERINFO_TABLE_NAME, values, UserInfo._ID + "=" + userinfoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
