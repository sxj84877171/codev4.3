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
 * BookmarkContentProvider provide some book mark information to activity and service
 * by providing some interface for query,insert,delete and update
 * 
 * @author rd026
 */
public class BookmarkContentProvider extends ContentProvider
{
	private static final String				TAG					= "BookmarkContentProvider";

	// 表名
	private static final String				BOOKMARK_TABLE_NAME	= "bookmark";
	private static HashMap<String, String>	sBookmarkProjectionMap;
	private static final int				BOOKMARK			= 1;	
	private static final int				BOOKMARK_ID			= 2;
	private static final UriMatcher			sUriMatcher;
	private DatabaseHelper	mOpenHelper;

	//创建表SQL语句
	public static final String				CREATETABLE="CREATE TABLE " 
														+ BOOKMARK_TABLE_NAME 
														+ " (" + Bookmark._ID 
														+ " INTEGER PRIMARY KEY," 
														+ Bookmark.UserID
														+ " TEXT,"
														+ Bookmark.BookmarkId
														+ " TEXT,"
														+ Bookmark.BookmarkType 
														+ " INTEGER," 
														+ Bookmark.SourceType 
														+ " INTEGER," 
														+ Bookmark.ChapterId 
														+ " TEXT,"
														+ Bookmark.ChapterName
														+ " TEXT,"
														+ Bookmark.ContentId
														+ " TEXT,"
														+ Bookmark.ContentName
														+ " TEXT,"
														+ Bookmark.FilePath
														+ " TEXT,"
														+ Bookmark.CertPath
														+ " TEXT,"
														+ Bookmark.Author
														+ " TEXT,"
														+ Bookmark.LineSpace
														+ " TEXT,"
														+ Bookmark.FontSize
														+ " TEXT,"
														+ Bookmark.OperationType
														+ " INTEGER,"
														+ Bookmark.SynchFlag
														+ " INTEGER,"
														+ Bookmark.Position
														+ " INTEGER,"
														+ Bookmark.Count
														+ " INTEGER,"
														+ Bookmark.CreatedDate
														+ " TEXT,"
														+ Bookmark.DownloadType 
                                                        + " INTEGER"
														+ ");";
	
	static
	{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(Bookmark.AUTHORITY, "bookmark", BOOKMARK);
		sUriMatcher.addURI(Bookmark.AUTHORITY, "bookmark/#", BOOKMARK_ID);

		sBookmarkProjectionMap = new HashMap<String, String>();
		sBookmarkProjectionMap.put(Bookmark._ID, Bookmark._ID);
		sBookmarkProjectionMap.put(Bookmark.UserID, Bookmark.UserID);
		sBookmarkProjectionMap.put(Bookmark.BookmarkId, Bookmark.BookmarkId);
		sBookmarkProjectionMap.put(Bookmark.BookmarkType, Bookmark.BookmarkType);
		sBookmarkProjectionMap.put(Bookmark.ChapterId, Bookmark.ChapterId);
		sBookmarkProjectionMap.put(Bookmark.ChapterName, Bookmark.ChapterName);
		sBookmarkProjectionMap.put(Bookmark.ContentId, Bookmark.ContentId);
		sBookmarkProjectionMap.put(Bookmark.ContentName, Bookmark.ContentName);
		sBookmarkProjectionMap.put(Bookmark.OperationType, Bookmark.OperationType);
		sBookmarkProjectionMap.put(Bookmark.SynchFlag, Bookmark.SynchFlag);
		sBookmarkProjectionMap.put(Bookmark.Position, Bookmark.Position);
		sBookmarkProjectionMap.put(Bookmark.Count, Bookmark.Count);		
		sBookmarkProjectionMap.put(Bookmark.CreatedDate, Bookmark.CreatedDate);
		sBookmarkProjectionMap.put(Bookmark.FilePath, Bookmark.FilePath);
		sBookmarkProjectionMap.put(Bookmark.CertPath, Bookmark.CertPath);
		sBookmarkProjectionMap.put(Bookmark.Author, Bookmark.Author);
		sBookmarkProjectionMap.put(Bookmark.SourceType, Bookmark.SourceType);
		sBookmarkProjectionMap.put(Bookmark.FontSize, Bookmark.FontSize);
		sBookmarkProjectionMap.put(Bookmark.LineSpace, Bookmark.LineSpace);
		sBookmarkProjectionMap.put(Bookmark.DownloadType, Bookmark.DownloadType);
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
			case BOOKMARK:
				qb.setTables(BOOKMARK_TABLE_NAME);
				qb.setProjectionMap(sBookmarkProjectionMap);
				break;

			case BOOKMARK_ID:
				qb.setTables(BOOKMARK_TABLE_NAME);
				qb.setProjectionMap(sBookmarkProjectionMap);
				qb.appendWhere(Bookmark._ID + "=" + uri.getPathSegments().get(1));
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder))
		{
			orderBy = Bookmark.DEFAULT_SORT_ORDER;
		}
		else
		{
			orderBy = sortOrder;
		}

		try{
			Cursor c = null;
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
			case BOOKMARK:
				return Bookmark.CONTENT_TYPE;

			case BOOKMARK_ID:
				return Bookmark.CONTENT_ITEM_TYPE;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	@Override
	//插入数据库
	public Uri insert(Uri uri, ContentValues initialValues)
	{
		if (sUriMatcher.match(uri) != BOOKMARK)
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

		if (values.containsKey(Bookmark.CreatedDate) == false)
		{	
			values.put(Bookmark.CreatedDate, now);
		}
		if (values.containsKey(Bookmark.UserID) == false)
		{	
			values.put(Bookmark.UserID, "001");
		}
		if (values.containsKey(Bookmark.BookmarkId) == false)
		{
			values.put(Bookmark.BookmarkId, "hello");
		}
		if (values.containsKey(Bookmark.BookmarkType) == false)
		{
			values.put(Bookmark.BookmarkType, "0");
		}
		if (values.containsKey(Bookmark.ChapterId) == false)
		{
			values.put(Bookmark.ChapterId, "");
		}
		if (values.containsKey(Bookmark.ChapterName) == false)
		{
			values.put(Bookmark.ChapterName, "");
		}
		if (values.containsKey(Bookmark.ContentId) == false)
		{
			values.put(Bookmark.ContentId, "7788414");
		}
		if (values.containsKey(Bookmark.ContentName) == false)
		{
			values.put(Bookmark.ContentName, "");
		}
		if (values.containsKey(Bookmark.OperationType) == false)
		{
			values.put(Bookmark.OperationType, "1");
		}
		if (values.containsKey(Bookmark.SynchFlag) == false)
		{
			values.put(Bookmark.SynchFlag, "1");
		}
		if (values.containsKey(Bookmark.Position) == false)
		{
			values.put(Bookmark.Position, "128");
		}
		if (values.containsKey(Bookmark.Count) == false)
		{
			values.put(Bookmark.Count, "1");
		}
		if (values.containsKey(Bookmark.FilePath) == false)
		{
			values.put(Bookmark.FilePath, "1");
		}
		if (values.containsKey(Bookmark.CertPath) == false)
		{
			values.put(Bookmark.CertPath, "1");
		}
		if (values.containsKey(Bookmark.Author) == false)
		{
			values.put(Bookmark.Author, "");
		}
		if (values.containsKey(Bookmark.SourceType) == false)
		{
			values.put(Bookmark.SourceType, "");
		}
		if (values.containsKey(Bookmark.LineSpace) == false)
		{
			values.put(Bookmark.LineSpace, "");
		}
		if (values.containsKey(Bookmark.FontSize) == false)
		{
			values.put(Bookmark.FontSize, "");
		}
		if (values.containsKey(Bookmark.DownloadType) == false)
        {
            values.put(Bookmark.DownloadType, "");
        }

		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			String table = db.findEditTable(BOOKMARK_TABLE_NAME);
			long rowId = db.insert(BOOKMARK_TABLE_NAME, null, values);
			if (rowId > 0)
			{
				Uri notesUri = ContentUris.withAppendedId(Bookmark.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(notesUri, null);
				return notesUri;
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
				case BOOKMARK:
					count = db.delete(BOOKMARK_TABLE_NAME, where, whereArgs);
					break;

				case BOOKMARK_ID:
					String userinfoId = uri.getPathSegments().get(1);
					count = db.delete(BOOKMARK_TABLE_NAME, Bookmark._ID + "=" + userinfoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
				case BOOKMARK:
					count = db.update(BOOKMARK_TABLE_NAME, values, where, whereArgs);
					break;

				case BOOKMARK_ID:
					String notesinfoId = uri.getPathSegments().get(1);
					count = db.update(BOOKMARK_TABLE_NAME, values, Bookmark._ID + "=" + notesinfoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
