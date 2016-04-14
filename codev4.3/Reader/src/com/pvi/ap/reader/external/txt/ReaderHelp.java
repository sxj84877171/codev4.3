package com.pvi.ap.reader.external.txt;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 
 * @author Elvis
 * @creation 2010-12-3
 */
public class ReaderHelp {
	
	/***
	 * 把对象保存到对应的文件中
	 * @param fileName 文件名
	 * @param obj 保存的对象
	 * @return 是否保存成功
	 */
	public static boolean saveObject(String fileName,Serializable obj){
		FileOutputStream fs = null ; 
		ObjectOutputStream os = null; 
		try {
			fs = new FileOutputStream(fileName);
			os = new ObjectOutputStream(fs); 
			os.writeObject(obj);
			os.flush();
			return true ;
		} catch (Exception e) {
			return false ;
		} finally{
			if(os != null){
				try {
					os.close();
				} catch (IOException e) {
					return false ;
				}
			}
			if(fs != null){
				try {
					fs.close();
				} catch (IOException e) {
					return false ;
				}
			}
		}
	}
	
}
