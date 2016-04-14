package com.pvi.ap.reader.data.common;

import android.graphics.Bitmap;

public class data_pool {
	final public int itemPerPage = 7;
	private data_btn[] pool_1 = new data_btn[11];
	private int ptr = 1;
	
	public int findIndexByPg(int pPg){
//		if(pPg < 1){
//			return -1;
//		}
//		for(int i = 0;i < pool_1.length;i++){
//			if(pPg == pool_1[i].pgnum){
//				return i;
//			}
//		}
//		return 0;
//		
		return -1;
	}
	
	public data_btn getbyindex(int index){
		//Logger.d("get_data pgnum:"+index, "pgnum of index"+pool_1[index].pgnum);
		return pool_1[index];
	}
	
	public data_pool(){
		for(int i = 0;i < 11; i ++){
			pool_1[i] = new data_btn();
		}
	}
	public int insert_data(data_btn data,int pgnum){
		//Logger.d("insert_data pgnum:"+pgnum, "ptr:"+ptr);
		if(ptr <11){
			for(int i = 0; i < itemPerPage; i ++){
				pool_1[ptr].dataId[i] = data.dataId[i];
				pool_1[ptr].dataPrimary[i] = data.dataPrimary[i];
				pool_1[ptr].dataPrimaryBtn[i] = data.dataPrimaryBtn[i];
				pool_1[ptr].dataSlave[i] = data.dataSlave[i];
				//pool_1[ptr].dataBitmap[i] = data.dataBitmap[i];
			}
			pool_1[ptr].pgnum = pgnum;
		}else{
			ptr = 1;
			for(int i = 0; i < itemPerPage; i ++){
				pool_1[ptr].dataId[i] = data.dataId[i];
				pool_1[ptr].dataPrimary[i] = data.dataPrimary[i];
				pool_1[ptr].dataPrimaryBtn[i] = data.dataPrimaryBtn[i];
				pool_1[ptr].dataSlave[i] = data.dataSlave[i];
				//pool_1[ptr].dataBitmap[i] = data.dataBitmap[i];
			}
			pool_1[ptr].pgnum = pgnum;
		}
		ptr++;
		return 0;
	}
	
	public int insert_image(data_btn data,int pgnum){
				
		for(int i = 0;i < pool_1.length;i++){
			if(pgnum == pool_1[i].pgnum){
				//Logger.d("insert_image pgnum:"+pgnum, "ptr:"+i);
				for(int j = 0; j < itemPerPage; j ++){
					pool_1[i].dataBitmap[j] = data.dataBitmap[j];
					pool_1[i].bitmapOK[j] = true;
				}
			}
		}

		return 0;
	}
	
	public int insert_image(Bitmap data,int pgnum,int itemindex){
		
		for(int ptr = 0;ptr < pool_1.length;ptr++){
			if(pgnum == pool_1[ptr].pgnum){
				//Logger.d("insert_image pgnum:"+pgnum, "ptr:"+ptr);
				pool_1[ptr].dataBitmap[itemindex] = data;	
				pool_1[ptr].bitmapOK[itemindex] = true;
			}
		}

		return 0;
	}
	
}
