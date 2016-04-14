
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
public class MonthlyPaymentContentProvider extends ContentProvider
{
	private static final String				TAG					= "MonthlyPaymentContentProvider";

	// 表名
	private static final String				MONTHLYPAYMENT_TABLE_NAME	= "monthlypayment";
	private static HashMap<String, String>	sMonthlyPaymentProjectionMap;
	private static final int				MONTHLYPAYMENT			= 1;	
	private static final int				MONTHLYPAYMENT_ID			= 2;
	private static final UriMatcher			sUriMatcher;
	private DatabaseHelper	mOpenHelper;

	//创建表SQL语句
	public static final String				CREATETABLE="CREATE TABLE " 
														+ MONTHLYPAYMENT_TABLE_NAME 
														+ " (" + MonthlyPayment._ID 
														+ " INTEGER PRIMARY KEY," 
														+ MonthlyPayment.UserID 
														+ " TEXT," 
														+ MonthlyPayment.ParentCatalogName 
														+ " TEXT,"
														+ MonthlyPayment.ParentCatalogID 
														+ " INTEGER,"
														+ MonthlyPayment.CatalogID
														+ " TEXT,"
														+ MonthlyPayment.CatalogName
														+ " TEXT,"
														+ MonthlyPayment.URL
														+ " TEXT,"
														+ MonthlyPayment.Fee
														+ " TEXT,"
														+ MonthlyPayment.NextChargeTime
														+ " TEXT,"
														+ MonthlyPayment.StartTime
														+ " TEXT" + ");";
	
	//映射字段
	static
	{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(MonthlyPayment.AUTHORITY, "monthlypayment", MONTHLYPAYMENT);
		sUriMatcher.addURI(MonthlyPayment.AUTHORITY, "monthlypayment/#", MONTHLYPAYMENT_ID);

		sMonthlyPaymentProjectionMap = new HashMap<String, String>();
		sMonthlyPaymentProjectionMap.put(MonthlyPayment._ID, MonthlyPayment._ID);
		sMonthlyPaymentProjectionMap.put(MonthlyPayment.UserID, MonthlyPayment.UserID);
		sMonthlyPaymentProjectionMap.put(MonthlyPayment.ParentCatalogName, MonthlyPayment.ParentCatalogName);
		sMonthlyPaymentProjectionMap.put(MonthlyPayment.ParentCatalogID, MonthlyPayment.ParentCatalogID);
		sMonthlyPaymentProjectionMap.put(MonthlyPayment.Fee, MonthlyPayment.Fee);
		sMonthlyPaymentProjectionMap.put(MonthlyPayment.URL, MonthlyPayment.URL);
		sMonthlyPaymentProjectionMap.put(MonthlyPayment.CatalogID, MonthlyPayment.CatalogID);
		sMonthlyPaymentProjectionMap.put(MonthlyPayment.CatalogName, MonthlyPayment.CatalogName);
		sMonthlyPaymentProjectionMap.put(MonthlyPayment.NextChargeTime, MonthlyPayment.NextChargeTime);
		sMonthlyPaymentProjectionMap.put(MonthlyPayment.StartTime, MonthlyPayment.StartTime);
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
			case MONTHLYPAYMENT:
				qb.setTables(MONTHLYPAYMENT_TABLE_NAME);
				qb.setProjectionMap(sMonthlyPaymentProjectionMap);
				break;

			case MONTHLYPAYMENT_ID:
				qb.setTables(MONTHLYPAYMENT_TABLE_NAME);
				qb.setProjectionMap(sMonthlyPaymentProjectionMap);
				qb.appendWhere(MonthlyPayment._ID + "=" + uri.getPathSegments().get(1));
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder))
		{
			orderBy = MonthlyPayment.DEFAULT_SORT_ORDER;
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
			case MONTHLYPAYMENT:
				return MonthlyPayment.CONTENT_TYPE;

			case MONTHLYPAYMENT_ID:
				return MonthlyPayment.CONTENT_ITEM_TYPE;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	@Override
	//插入数据库
	public Uri insert(Uri uri, ContentValues initialValues)
	{
		if (sUriMatcher.match(uri) != MONTHLYPAYMENT)
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

		if (values.containsKey(MonthlyPayment.NextChargeTime) == false)
		{	
			values.put(MonthlyPayment.NextChargeTime, now);
		}
		if (values.containsKey(MonthlyPayment.ParentCatalogID) == false)
		{
			values.put(MonthlyPayment.ParentCatalogID, "");
		}
		if (values.containsKey(MonthlyPayment.ParentCatalogName) == false)
		{
			values.put(MonthlyPayment.ParentCatalogName, "");
		}
		if (values.containsKey(MonthlyPayment.Fee) == false)
		{
			values.put(MonthlyPayment.Fee, "");
		}
		if (values.containsKey(MonthlyPayment.URL) == false)
		{
			values.put(MonthlyPayment.URL, "");
		}
		if (values.containsKey(MonthlyPayment.CatalogID) == false)
		{
			values.put(MonthlyPayment.CatalogID, "");
		}
		if (values.containsKey(MonthlyPayment.CatalogName) == false)
		{
			values.put(MonthlyPayment.CatalogName, "");
		}
		if (values.containsKey(MonthlyPayment.UserID) == false)
		{
			values.put(MonthlyPayment.UserID, "");
		}
		if (values.containsKey(MonthlyPayment.StartTime) == false)
		{
			values.put(MonthlyPayment.StartTime, "");
		}
		
		try{
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			String table = db.findEditTable(MONTHLYPAYMENT_TABLE_NAME);
			long rowId = db.insert(MONTHLYPAYMENT_TABLE_NAME, null, values);
			if (rowId > 0)
			{
				Uri Uri = ContentUris.withAppendedId(MonthlyPayment.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(Uri, null);
				return Uri;
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
				case MONTHLYPAYMENT:
					count = db.delete(MONTHLYPAYMENT_TABLE_NAME, where, whereArgs);
					break;

				case MONTHLYPAYMENT_ID:
					String Id = uri.getPathSegments().get(1);
					count = db.delete(MONTHLYPAYMENT_TABLE_NAME, MonthlyPayment._ID + "=" + Id + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
				case MONTHLYPAYMENT:
					count = db.update(MONTHLYPAYMENT_TABLE_NAME, values, where, whereArgs);
					break;

				case MONTHLYPAYMENT_ID:
					String Id = uri.getPathSegments().get(1);
					count = db.update(MONTHLYPAYMENT_TABLE_NAME, values, MonthlyPayment._ID + "=" + Id + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
