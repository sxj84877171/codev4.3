package com.pvi.ap.reader.data.content;

import android.net.Uri;
import android.provider.BaseColumns;
import java.sql.Date;


public class MonthlyPayment implements BaseColumns
{
	private MonthlyPayment(){}
	
	public static final String	AUTHORITY			= "com.pvi.provider.monthlypaymentcontentprovider";
		

	public static final Uri		CONTENT_URI		    = Uri.parse("content://" + AUTHORITY + "/monthlypayment");

	// 新的MIME类型-多个
	public static final String	CONTENT_TYPE		= "vnd.android.cursor.dir/vnd.pvi.monthlypayment";

	// 新的MIME类型-单个
	public static final String	CONTENT_ITEM_TYPE	= "vnd.android.cursor.item/vnd.pvi.monthlypayment";

	public static final String	DEFAULT_SORT_ORDER	= "UserID DESC";

	//字段
	public static final String  UserID				= "UserID";
	public static final String  ParentCatalogID     = "ParentCatalogID";   //频道ID（ 1：原创 2：书籍  3：漫画 4：杂志）
	public static final String  ParentCatalogName   = "ParentCatalogName";
	public static final String  CatalogID    		= "CatalogID";
	public static final String  CatalogName         = "CatalogName";     //专区名称    
	public static final String  URL					= "URL";			//专区图片URL
	public static final String  Fee					= "Fee"; 			//包月费用
	public static final String  NextChargeTime		= "NextChargeTime";  //下次计费时间
	public static final String  StartTime			= "StartTime";      //开始订购时间
}


