package com.pvi.ap.reader.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Logger;

/**
 * �ϵ��ļ������߳���<br>
 * ֧�ֶ��߳�����
 * @author rd038
 * @since 2010-10-10
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class FileDownloadThread extends Thread {
	/**
	 * �����С���ֽ�����
	 */
	private static final int mi_BUFFER_SIZE=1024; 
	/**
	 * ���������ַURL
	 */
	private URL mu_Url;  
	/**
	 * ���ر����ļ�
	 */
	private File mf_File; 
	/**
	 * �ϵ����ؿ�ʼλ��
	 */
	private int mi_StartPosition; 
	/**
	 * �ϵ����ؽ���λ��
	 */
	private int mi_EndPosition; 
	/**
	 * �ϵ����ص�ǰλ��
	 */
	private int mi_CurPosition; 
	/**
	 * ���ڱ�ʶ��ǰ�߳��Ƿ��������
	 */
	private boolean mb_Finished=false;
	/**
	 * �����ش�С
	 */
	private int mi_DownloadSize=0;
	/**
	 * �߳��Ƿ��쳣
	 */
	private int mi_RunException = 0 ; 
	
	/**
     * sim������
     */
    public static String simType = "" ;
	
	public int getRunException() {
		return mi_RunException;
	}


	public void setRunException(int runException) {
		this.mi_RunException = runException;
	}


	/**
	 * ���췽��
	 * @param url
	 * @param file
	 * @param startPosition
	 * @param endPosition
	 */
	public FileDownloadThread(URL url,File file,int startPosition,int endPosition){
		this.mu_Url=url;
		this.mf_File=file;
		this.mi_StartPosition=startPosition;
		this.mi_CurPosition=startPosition;
		this.mi_EndPosition=endPosition;
	}
	
	
	/**
	 * �����߳�ִ�����巽��<br>
	 * ��Ҫ�������Ǵ������ϵ����Զϵ����ʽ���أ�������������
	 */
	public void run() {
		

           
        BufferedInputStream bis = null;
        RandomAccessFile fos = null;                                               
        byte[] buf = new byte[mi_BUFFER_SIZE];
        URLConnection con = null;
        FileInputStream fin = null;
        FileOutputStream fout = null;
        try {
        	if("sim".equalsIgnoreCase(simType)){
        		SocketAddress addr = new InetSocketAddress(Config.getString("proxyIP"),Integer.parseInt(Config.getString("port")));
        		Proxy typeProxy = new Proxy(Proxy.Type.HTTP, addr);  
        		con = mu_Url.openConnection(typeProxy);
        	}else{
        		con = mu_Url.openConnection();
        	}
        	con.setConnectTimeout(600000);
        	con.setReadTimeout(600000);
            con.setAllowUserInteraction(true);
            fin = new FileInputStream(mf_File);
            int fLen = fin.available();
            if(fLen>mi_StartPosition){
            	mi_StartPosition = fLen;
            }
            fos = new RandomAccessFile(mf_File, "rw");
            fos.seek(mi_StartPosition);
            Logger.i("FiledownloadThread", "filebeenlen:" + fLen);
            fout = new FileOutputStream(mf_File,true);
            mi_DownloadSize += mi_StartPosition ;
            mi_CurPosition = mi_StartPosition;
            if(mi_CurPosition != mi_EndPosition)
            con.setRequestProperty("RANGE","bytes=" + mi_CurPosition + "-" + mi_EndPosition); 
            bis = new BufferedInputStream(con.getInputStream(),8 * 1024); 
            //��ʼѭ����������ʽ��д�ļ�
            while (mi_CurPosition < mi_EndPosition && !interrupBool) {
            	//System.out.println(mi_CurPosition +" "+mi_EndPosition);
                int len = bis.read(buf, 0, mi_BUFFER_SIZE);                
                if (len == -1) {
                   throw new IOException("the net file can not read");
                }
                fout.write(buf, 0, len);
                fout.flush();
                //fin.read(buf, 0, len);
                /*fos.write(buf, 0, len);*/
                mi_CurPosition = mi_CurPosition + len;
                if (mi_CurPosition > mi_EndPosition) {
                	mi_DownloadSize+=len - (mi_CurPosition - mi_EndPosition) + 1;
                } else {
                	mi_DownloadSize+=len;
                }
            }
            //���������Ϊtrue
            this.mb_Finished = true;
           
            //fos.close();
        }catch(FileNotFoundException e){
        	mi_RunException = 2 ;
//        	Logger.e("FileDownloadThread",e);
        }catch(SocketTimeoutException e){
        	mi_RunException = 1 ;
//        	Logger.e("FileDownloadThread", e);
        }catch (IOException e) {
        	mi_RunException = 1 ;
//        	Logger.e("FileDownloadThread",e);
        } catch(Exception e){
        	mi_RunException = 2 ;
        	Logger.e("FileDownloadThread",e);
        }
        finally {
			try {
				if (bis != null)
					bis.close();
			} catch (IOException e) {
				Logger.e("FileDownloadThread", e);
			}
			try {
				if (fin != null)
					fin.close();
			} catch (IOException e) {
				Logger.e("FileDownloadThread", e);
			}
			try {
				if (fout != null)
					fout.close();
			} catch (IOException e) {
				Logger.e("FileDownloadThread", e);
			}
		}
	}
 
	public boolean isFinished(){
		return mb_Finished;
	}
 
	public int getDownloadSize() {
		return mi_DownloadSize;
	}

	@Override
	public void interrupt() {
		interrupBool = true ;
		super.interrupt();
	}
	
	private boolean interrupBool = false ;

}
