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
 * CommentsInfoContentProvider provide some comment information for book 
 * by providing some interface for query,insert,delete and update
 * @author rd026
 */
public class CommentsInfoContentProvider extends ContentProvider
{
	private static final String				TAG					= "CommentsInfoContentProvider";
	
	// 表名
	private static final String				COMMENTSINFO_TABLE_NAME	= "commentsinfo";
	private static HashMap<String, String>	sCommentsInfoProjectionMap;
	private static final int				COMMENTSINFO			= 1;	
	private static final int				COMMENTSINFO_ID			= 2;
	private static final UriMatcher			sUriMatcher;
	private DatabaseHelper	mOpenHelper;

	//创建表SQL语句
	public static final String				CREATETABLE="CREATE TABLE " 
														+ COMMENTSINFO_TABLE_NAME 
														+ " (" + CommentsInfo._ID 
														+ " INTEGER PRIMARY KEY," 
														+ CommentsInfo.UserID
														+ " TEXT,"
														+ CommentsInfo.CommentId 
														+ " TEXT," 
														+ CommentsInfo.Comment 
														+ " TEXT,"
														+ CommentsInfo.CommentTime
														+ " TEXT,"
														+ CommentsInfo.ChapterId
														+ " TEXT,"
														+ CommentsInfo.ChapterName 
														+ " TEXT," 
														+ CommentsInfo.ContentId 
														+ " TEXT,"
														+ CommentsInfo.ContentName
														+ " TEXT," 
														+ CommentsInfo.StartPosition 
														+ " TEXT,"
														+ CommentsInfo.EndPosition
														+ " TEXT,"
														+ CommentsInfo.FilePath
														+ " TEXT,"
														+ CommentsInfo.CertPath
														+ " TEXT,"
														+ CommentsInfo.CurrentPage
														+ " INTEGER" + ");";
	
	static
	{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(CommentsInfo.AUTHORITY, "commentsinfo", COMMENTSINFO);
		sUriMatcher.addURI(CommentsInfo.AUTHORITY, "commentsinfo/#", COMMENTSINFO_ID);

		sCommentsInfoProjectionMap = new HashMap<String, String>();
		sCommentsInfoProjectionMap.put(CommentsInfo._ID, CommentsInfo._ID);
		sCommentsInfoProjectionMap.put(CommentsInfo.UserID, CommentsInfo.UserID);
		sCommentsInfoProjectionMap.put(CommentsInfo.CommentId, CommentsInfo.CommentId);
		sCommentsInfoProjectionMap.put(CommentsInfo.Comment, CommentsInfo.Comment);
		sCommentsInfoProjectionMap.put(CommentsInfo.CommentTime, CommentsInfo.CommentTime);		
		sCommentsInfoProjectionMap.put(CommentsInfo.CurrentPage, CommentsInfo.CurrentPage);
		sCommentsInfoProjectionMap.put(CommentsInfo.ChapterId, CommentsInfo.ChapterId);		
		sCommentsInfoProjectionMap.put(CommentsInfo.ChapterName, CommentsInfo.ChapterName);
		sCommentsInfoProjectionMap.put(CommentsInfo.ContentId, CommentsInfo.ContentId);		
		sCommentsInfoProjectionMap.put(CommentsInfo.ContentName, CommentsInfo.ContentName);
		sCommentsInfoProjectionMap.put(CommentsInfo.StartPosition, CommentsInfo.StartPosition);		
		sCommentsInfoProjectionMap.put(CommentsInfo.EndPosition, CommentsInfo.EndPosition);
		sCommentsInfoProjectionMap.put(CommentsInfo.FilePath, CommentsInfo.FilePath);
		sCommentsInfoProjectionMap.put(CommentsInfo.CertPath, CommentsInfo.CertPath);
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
			case COMMENTSINFO:
				qb.setTables(COMMENTSINFO_TABLE_NAME);
				qb.setProjectionMap(sCommentsInfoProjectionMap);
				break;

			case COMMENTSINFO_ID:
				qb.setTables(COMMENTSINFO_TABLE_NAME);
				qb.setProjectionMap(sCommentsInfoProjectionMap);
				qb.appendWhere(CommentsInfo._ID + "=" + uri.getPathSegments().get(1));
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder))
		{
			orderBy = CommentsInfo.DEFAULT_SORT_ORDER;
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
			case COMMENTSINFO:
				return CommentsInfo.CONTENT_TYPE;

			case COMMENTSINFO_ID:
				return CommentsInfo.CONTENT_ITEM_TYPE;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	@Override
	//插入数据库
	public Uri insert(Uri uri, ContentValues initialValues)
	{
		if (sUriMatcher.match(uri) != COMMENTSINFO)
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

		if (values.containsKey(CommentsInfo.CommentTime) == false)
		{	
			values.put(CommentsInfo.CommentTime, now);
		}
		if (values.containsKey(CommentsInfo.UserID) == false)
		{	
			values.put(CommentsInfo.UserID, "001");
		}
		if (values.containsKey(CommentsInfo.CommentId) == false)
		{
			values.put(CommentsInfo.CommentId, "111");
		}
		if (values.containsKey(CommentsInfo.Comment) == false)
		{
			values.put(CommentsInfo.Comment, "此处表达手法甚妙！");
		}
		if (values.containsKey(CommentsInfo.CurrentPage) == false)
		{
			values.put(CommentsInfo.CurrentPage, "1000");
		}
		if (values.containsKey(CommentsInfo.ChapterId) == false)
		{
			values.put(CommentsInfo.ChapterId, "1000");
		}
		if (values.containsKey(CommentsInfo.ChapterName) == false)
		{
			values.put(CommentsInfo.ChapterName, "1000");
		}
		if (values.containsKey(CommentsInfo.ContentId) == false)
		{
			values.put(CommentsInfo.ContentId, "1000");
		}
		if (values.containsKey(CommentsInfo.ContentName) == false)
		{
			values.put(CommentsInfo.ContentName, "1000");
		}
		if (values.containsKey(CommentsInfo.StartPosition) == false)
		{
			values.put(CommentsInfo.StartPosition, "1000");
		}
		if (values.containsKey(CommentsInfo.EndPosition) == false)
		{
			values.put(CommentsInfo.EndPosition, "1000");
		}
		if (values.containsKey(CommentsInfo.FilePath) == false)
		{
			values.put(CommentsInfo.FilePath, "1000");
		}
		if (values.containsKey(CommentsInfo.EndPosition) == false)
		{
			values.put(CommentsInfo.EndPosition, "1000");
		}
		
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			String table = db.findEditTable(COMMENTSINFO_TABLE_NAME);
			long rowId = db.insert(COMMENTSINFO_TABLE_NAME, null, values);
			if (rowId > 0)
			{
				Uri commentUri = ContentUris.withAppendedId(CommentsInfo.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(commentUri, null);
				return commentUri;
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
				case COMMENTSINFO:
					count = db.delete(COMMENTSINFO_TABLE_NAME, where, whereArgs);
					break;

				case COMMENTSINFO_ID:
					String commentInfoId = uri.getPathSegments().get(1);
					count = db.delete(COMMENTSINFO_TABLE_NAME, CommentsInfo._ID + "=" + commentInfoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
				case COMMENTSINFO:
					count = db.update(COMMENTSINFO_TABLE_NAME, values, where, whereArgs);
					break;

				case COMMENTSINFO_ID:
					String CommentsInfoId = uri.getPathSegments().get(1);
					count = db.update(COMMENTSINFO_TABLE_NAME, values, CommentsInfo._ID + "=" + CommentsInfoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
