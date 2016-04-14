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
	 * �Ѷ��󱣴浽��Ӧ���ļ���
	 * @param fileName �ļ���
	 * @param obj ����Ķ���
	 * @return �Ƿ񱣴�ɹ�
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
