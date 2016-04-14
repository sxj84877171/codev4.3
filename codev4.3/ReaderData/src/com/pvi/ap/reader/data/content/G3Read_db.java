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
		//构造函数-创建数据库
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		//创建表
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
			
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(1,'卜王之王','2','竹林探月','1022K','/mnt/sdcard/localbook/book/卜王之王（作者：竹林探月）.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(2,'大地下城纪事','2','','408K','/mnt/sdcard/localbook/book/大地下城纪事.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(3,'地下王朝','2','','2225K','/mnt/sdcard/localbook/book/地下王朝.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(4,'奉子成婚','2','','224K','/mnt/sdcard/localbook/book/奉子成婚.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(5,'近战法师','2','','3032K','/mnt/sdcard/localbook/book/近战法师.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(6,'灵异实录：诡异档案','2','','453K','/mnt/sdcard/localbook/book/灵异实录：诡异档案.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(7,'逆龙道','2','','509K','/mnt/sdcard/localbook/book/逆龙道.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(8,'神鬼奇航','2','','369K','/mnt/sdcard/localbook/book/神鬼奇航.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(9,'巫颂','2','','455K','/mnt/sdcard/localbook/book/巫颂.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(10,'召尸墓响','2','','1949K','/mnt/sdcard/localbook/book/召尸墓响.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(11,'指南录','2','','2427K','/mnt/sdcard/localbook/book/指南录.meb','1')");
			db.execSQL("insert into bookinfo(_id,Name,BookType,Author,BookSize,BookPosition,Catelog)values(12,'测试书籍','2','','2427K','/mnt/sdcard/localbook/book/111.meb','1')");
			Logger.i("G3Read_db", "insert 11 localbooks");
			Logger.i("G3Read_db", "insert initing data sucess");
			Logger.i("G3Read_db", "end create db");
		}
		//更新数据库
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