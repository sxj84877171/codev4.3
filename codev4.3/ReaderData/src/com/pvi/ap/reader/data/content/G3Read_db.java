package com.pvi.ap.reader.data.content;

import java.util.HashMap;

import com.pvi.ap.reader.data.common.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class G3Read_db 
{
	G3Read_db(){};
	
	public static final String				DATABASE_NAME		="G3Read.db";
	public static final int				    DATABASE_VERSION	= 2;
	
	public class DatabaseHelper extends SQLiteOpenHelper
	{
		//���캯��-�������ݿ�
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		//������
		@Override
		public void onCreate(SQLiteDatabase db)
		{	
			Logger.i("G3Read_db", "begin to create db");
			db.execSQL(BookmarkContentProvider.CREATETABLE);
			db.execSQL(CommentsInfoContentProvider.CREATETABLE);
			db.execSQL(APInfoContentProvider.CREATETABLE);
			db.execSQL(BookInfoContentProvider.CREATETABLE);
			db.execSQL(FavoritesContentProvider.CREATETABLE);
			db.execSQL(HintIntoContentProvider.CREATETABLE);
			db.execSQL(NotesInfoContentProvider.CREATETABLE);
			db.execSQL(OptLogInfoContentProvider.CREATETABLE);
			db.execSQL(ReadingContentProvider.CREATETABLE);
			db.execSQL(RegInfoContentProvider.CREATETABLE);
			db.execSQL(UserInfoContentProvider.CREATETABLE);
			db.execSQL(MonthlyPaymentContentProvider.CREATETABLE);
			db.execSQL(SubScribeContentProvider.CREATETABLE);
			db.execSQL(LocalBookContentProvider.CREATETABLE);
			
			Logger.i("G3Read_db", "insert initing data");
			
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(1,'����֮��','2','����̽��','1022K','/mnt/sdcard/localbook/book/����֮�������ߣ�����̽�£�.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(2,'����³Ǽ���','2','','408K','/mnt/sdcard/localbook/book/����³Ǽ���.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(3,'��������','2','','2225K','/mnt/sdcard/localbook/book/��������.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(4,'���ӳɻ�','2','','224K','/mnt/sdcard/localbook/book/���ӳɻ�.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(5,'��ս��ʦ','2','','3032K','/mnt/sdcard/localbook/book/��ս��ʦ.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(6,'����ʵ¼�����쵵��','2','','453K','/mnt/sdcard/localbook/book/����ʵ¼�����쵵��.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(7,'������','2','','509K','/mnt/sdcard/localbook/book/������.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(8,'����溽','2','','369K','/mnt/sdcard/localbook/book/����溽.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(9,'����','2','','455K','/mnt/sdcard/localbook/book/����.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(10,'��ʬĹ��','2','','1949K','/mnt/sdcard/localbook/book/��ʬĹ��.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(11,'ָ��¼','2','','2427K','/mnt/sdcard/localbook/book/ָ��¼.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(12,'�����鼮','2','','2427K','/mnt/sdcard/localbook/book/111.meb','1')");
			Logger.i("G3Read_db", "insert 11 localbooks");
			Logger.i("G3Read_db", "insert initing data sucess");
			Logger.i("G3Read_db", "end create db");
		}
		//�������ݿ�
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("DROP TABLE IF EXISTS notesinfo");
			db.execSQL("DROP TABLE IF EXISTS bookmark");
			db.execSQL("DROP TABLE IF EXISTS commentsinfo");
			db.execSQL("DROP TABLE IF EXISTS apinfo");
			db.execSQL("DROP TABLE IF EXISTS bookinfo");
			db.execSQL("DROP TABLE IF EXISTS favorites");
			db.execSQL("DROP TABLE IF EXISTS hintinto");
			db.execSQL("DROP TABLE IF EXISTS optloginfo");
			db.execSQL("DROP TABLE IF EXISTS reading");
			db.execSQL("DROP TABLE IF EXISTS reginfo");
			db.execSQL("DROP TABLE IF EXISTS userinfo");
			db.execSQL("DROP TABLE IF EXISTS subscribe");
			db.execSQL("DROP TABLE IF EXISTS monthlypayment");
			db.execSQL("DROP TABLE IF EXISTS localbook");
			
			onCreate(db);
		}
	}
	
}