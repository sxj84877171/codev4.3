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
 * NotesInfoContentProvider save some note for book file
 * Access it by providing some interface for query,insert,delete and update
 * @author rd026
 */
public class NotesInfoContentProvider extends ContentProvider
{
	private static final String				TAG					= "NotesInfoContentProvider";

	// 表名
	private static final String				NOTESINFO_TABLE_NAME	= "notesinfo";
	private static HashMap<String, String>	sNotesInfoProjectionMap;
	private static final int				NOTESINFO			= 1;	
	private static final int				NOTESINFO_ID			= 2;
	private static final UriMatcher			sUriMatcher;
	private DatabaseHelper	mOpenHelper;

	//创建表SQL语句
	public static final String				CREATETABLE="CREATE TABLE " 
														+ NOTESINFO_TABLE_NAME 
														+ " (" + NotesInfo._ID 
														+ " INTEGER PRIMARY KEY," 
														+ NotesInfo.NoteID
														+ " TEXT,"
														+ NotesInfo.NoteName 
														+ " TEXT," 
														+ NotesInfo.NoteText 
														+ " TEXT,"
														+ NotesInfo.CreateDate
														+ " Date" + ");";
	
	static
	{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(NotesInfo.AUTHORITY, "notesinfo", NOTESINFO);
		sUriMatcher.addURI(NotesInfo.AUTHORITY, "notesinfo/#", NOTESINFO_ID);

		sNotesInfoProjectionMap = new HashMap<String, String>();
		sNotesInfoProjectionMap.put(NotesInfo._ID, NotesInfo._ID);
		sNotesInfoProjectionMap.put(NotesInfo.NoteID, NotesInfo.NoteID);
		sNotesInfoProjectionMap.put(NotesInfo.NoteName, NotesInfo.NoteName);
		sNotesInfoProjectionMap.put(NotesInfo.NoteText, NotesInfo.NoteText);
		sNotesInfoProjectionMap.put(NotesInfo.CreateDate, NotesInfo.CreateDate);		
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
			case NOTESINFO:
				qb.setTables(NOTESINFO_TABLE_NAME);
				qb.setProjectionMap(sNotesInfoProjectionMap);
				break;

			case NOTESINFO_ID:
				qb.setTables(NOTESINFO_TABLE_NAME);
				qb.setProjectionMap(sNotesInfoProjectionMap);
				qb.appendWhere(NotesInfo._ID + "=" + uri.getPathSegments().get(1));
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder))
		{
			orderBy = NotesInfo.DEFAULT_SORT_ORDER;
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
			case NOTESINFO:
				return NotesInfo.CONTENT_TYPE;

			case NOTESINFO_ID:
				return NotesInfo.CONTENT_ITEM_TYPE;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	
	@Override
	//插入数据库
	public Uri insert(Uri uri, ContentValues initialValues)
	{
		if (sUriMatcher.match(uri) != NOTESINFO)
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

		if (values.containsKey(NotesInfo.CreateDate) == false)
		{	
			values.put(NotesInfo.CreateDate, now);
		}
		if (values.containsKey(NotesInfo.NoteID) == false)
		{	
			values.put(NotesInfo.NoteID, "001");
		}
		if (values.containsKey(NotesInfo.NoteName) == false)
		{
			values.put(NotesInfo.NoteName, "第三方");
		}
		if (values.containsKey(NotesInfo.NoteText) == false)
		{
			values.put(NotesInfo.NoteText, "V1.0");
		}
		
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			String table = db.findEditTable(NOTESINFO_TABLE_NAME);
			long rowId = db.insert(NOTESINFO_TABLE_NAME, null, values);
			if (rowId > 0)
			{
				Uri noteUri = ContentUris.withAppendedId(NotesInfo.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(noteUri, null);
				return noteUri;
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
				case NOTESINFO:
					count = db.delete(NOTESINFO_TABLE_NAME, where, whereArgs);
					break;
	
				case NOTESINFO_ID:
					String notesInfoId = uri.getPathSegments().get(1);
					count = db.delete(NOTESINFO_TABLE_NAME, NotesInfo._ID + "=" + notesInfoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
				case NOTESINFO:
					count = db.update(NOTESINFO_TABLE_NAME, values, where, whereArgs);
					break;
	
				case NOTESINFO_ID:
					String NotesInfoId = uri.getPathSegments().get(1);
					count = db.update(NOTESINFO_TABLE_NAME, values, NotesInfo._ID + "=" + NotesInfoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
