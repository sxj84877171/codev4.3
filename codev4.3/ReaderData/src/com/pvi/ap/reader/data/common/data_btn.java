package com.pvi.ap.reader.data.common;

import android.graphics.Bitmap;

public class data_btn {
	public int pgnum;
	final public int itemPerPage = 11;
	public String[] dataPrimaryBtn ;
	public String[] dataPrimary ;
	public String[] dataSlave ;
	public String[] dataId ;
	public Bitmap[] dataBitmap;
	public boolean[] bitmapOK;
	
	public int clear(String[] str){
		str = new String[itemPerPage];
		return 0;
	}
	public int clear(Bitmap[] bitmap){
		bitmap = new Bitmap[itemPerPage];
		return 0;
	}
	public int flushall(){
		dataPrimaryBtn = new String[itemPerPage];
		dataPrimary = new String[itemPerPage];
		dataSlave = new String[itemPerPage];
		dataId = new String[itemPerPage];
		dataBitmap = new Bitmap[itemPerPage];
		bitmapOK = new boolean[itemPerPage];
		return 0;	
	}
	public data_btn(){
		dataPrimaryBtn = new String[itemPerPage];
		dataPrimary = new String[itemPerPage];
		dataSlave = new String[itemPerPage];
		dataId = new String[itemPerPage];
		dataBitmap = new Bitmap[itemPerPage];
		bitmapOK = new boolean[itemPerPage];
	}
}
