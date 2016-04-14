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
 * FavoritesContentProvider save some favorite information 
 * Access it by providing some interface for query,insert,delete and update
 * @author rd026
 */
public class FavoritesContentProvider extends ContentProvider
{
	private static final String				TAG					= "FavoritesContentProvider";

	// 表名
	private static final String				FAVORITES_TABLE_NAME	= "favorites";
	private static HashMap<String, String>	sFavoritesProjectionMap;
	private static final int				FAVORITES			= 1;	
	private static final int				FAVORITES_ID			= 2;
	private static final UriMatcher			sUriMatcher;
	private DatabaseHelper	mOpenHelper;

	//创建表SQL语句
	public static final String				CREATETABLE="CREATE TABLE " 
														+ FAVORITES_TABLE_NAME 
														+ " (" + Favorites._ID 
														+ " INTEGER PRIMARY KEY," 
														+ Favorites.UserID
														+ " TEXT,"
														+ Favorites.ContentId 
														+ " TEXT," 
														+ Favorites.ContentName 
														+ " TEXT,"
														+ Favorites.Author
														+ " TEXT,"
														+ Favorites.FavoriteTime
														+ " TEXT,"
														+ Favorites.ChapterId
														+ " TEXT,"
														+ Favorites.ChapterName
														+ " TEXT,"
														+ Favorites.FavoriteURL
														+ " TEXT" + ");";
	
	static
	{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(Favorites.AUTHORITY, "favorites", FAVORITES);
		sUriMatcher.addURI(Favorites.AUTHORITY, "favorites/#", FAVORITES_ID);

		sFavoritesProjectionMap = new HashMap<String, String>();
		sFavoritesProjectionMap.put(Favorites._ID, Favorites._ID);
		sFavoritesProjectionMap.put(Favorites.UserID, Favorites.UserID);
		sFavoritesProjectionMap.put(Favorites.ContentId, Favorites.ContentId);
		sFavoritesProjectionMap.put(Favorites.ContentName, Favorites.ContentName);
		sFavoritesProjectionMap.put(Favorites.Author, Favorites.Author);		
		sFavoritesProjectionMap.put(Favorites.FavoriteTime, Favorites.FavoriteTime);
		sFavoritesProjectionMap.put(Favorites.FavoriteURL, Favorites.FavoriteURL);
		sFavoritesProjectionMap.put(Favorites.ChapterId, Favorites.ChapterId);
		sFavoritesProjectionMap.put(Favorites.ChapterName, Favorites.ChapterName);
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
			case FAVORITES:
				qb.setTables(FAVORITES_TABLE_NAME);
				qb.setProjectionMap(sFavoritesProjectionMap);
				break;

			case FAVORITES_ID:
				qb.setTables(FAVORITES_TABLE_NAME);
				qb.setProjectionMap(sFavoritesProjectionMap);
				qb.appendWhere(Favorites._ID + "=" + uri.getPathSegments().get(1));
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder))
		{
			orderBy = Favorites.DEFAULT_SORT_ORDER;
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
			case FAVORITES:
				return Favorites.CONTENT_TYPE;

			case FAVORITES_ID:
				return Favorites.CONTENT_ITEM_TYPE;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	@Override
	//插入数据库
	public Uri insert(Uri uri, ContentValues initialValues)
	{
		if (sUriMatcher.match(uri) != FAVORITES)
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

		if (values.containsKey(Favorites.FavoriteTime) == false)
		{	
			values.put(Favorites.FavoriteTime, now);
		}
		if (values.containsKey(Favorites.UserID) == false)
		{	
			values.put(Favorites.UserID, "001");
		}
		if (values.containsKey(Favorites.ContentId) == false)
		{
			values.put(Favorites.ContentId, "111");
		}
		if (values.containsKey(Favorites.ContentName) == false)
		{
			values.put(Favorites.ContentName, "黄帝内经");
		}
		if (values.containsKey(Favorites.Author) == false)
		{
			values.put(Favorites.Author, "神农氏");
		}
		if (values.containsKey(Favorites.FavoriteURL) == false)
		{
			values.put(Favorites.FavoriteURL, "http://www.baidu.com/");
		}
		if (values.containsKey(Favorites.ChapterId) == false)
		{
			values.put(Favorites.ChapterId, "");
		}
		if (values.containsKey(Favorites.ChapterName) == false)
		{
			values.put(Favorites.ChapterName, "");
		}
	
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			String table = db.findEditTable(FAVORITES_TABLE_NAME);
			long rowId = db.insert(FAVORITES_TABLE_NAME, null, values);
			if (rowId > 0)
			{
				Uri favoriteUri = ContentUris.withAppendedId(Favorites.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(favoriteUri, null);
				return favoriteUri;
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
				case FAVORITES:
					count = db.delete(FAVORITES_TABLE_NAME, where, whereArgs);
					break;
	
				case FAVORITES_ID:
					String favoriteInfoId = uri.getPathSegments().get(1);
					count = db.delete(FAVORITES_TABLE_NAME, Favorites._ID + "=" + favoriteInfoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
				case FAVORITES:
					count = db.update(FAVORITES_TABLE_NAME, values, where, whereArgs);
					break;
	
				case FAVORITES_ID:
					String favoriteId = uri.getPathSegments().get(1);
					count = db.update(FAVORITES_TABLE_NAME, values, Favorites._ID + "=" + favoriteId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
