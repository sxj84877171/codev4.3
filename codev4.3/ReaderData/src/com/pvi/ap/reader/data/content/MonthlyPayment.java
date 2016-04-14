package com.pvi.ap.reader.data.content;

import android.net.Uri;
import android.provider.BaseColumns;
import java.sql.Date;


public class MonthlyPayment implements BaseColumns
{
	private MonthlyPayment(){}
	
	public static final String	AUTHORITY			= "com.pvi.provider.monthlypaymentcontentprovider";
		

	public static final Uri		CONTENT_URI		    = Uri.parse("content://" + AUTHORITY + "/monthlypayment");

	// �µ�MIME����-���
	public static final String	CONTENT_TYPE		= "vnd.android.cursor.dir/vnd.pvi.monthlypayment";

	// �µ�MIME����-����
	public static final String	CONTENT_ITEM_TYPE	= "vnd.android.cursor.item/vnd.pvi.monthlypayment";

	public static final String	DEFAULT_SORT_ORDER	= "UserID DESC";

	//�ֶ�
	public static final String  UserID				= "UserID";
	public static final String  ParentCatalogID     = "ParentCatalogID";   //Ƶ��ID�� 1��ԭ�� 2���鼮  3������ 4����־��
	public static final String  ParentCatalogName   = "ParentCatalogName";
	public static final String  CatalogID    		= "CatalogID";
	public static final String  CatalogName         = "CatalogName";     //ר������    
	public static final String  URL					= "URL";			//ר��ͼƬURL
	public static final String  Fee					= "Fee"; 			//���·���
	public static final String  NextChargeTime		= "NextChargeTime";  //�´μƷ�ʱ��
	public static final String  StartTime			= "StartTime";      //��ʼ����ʱ��
}


