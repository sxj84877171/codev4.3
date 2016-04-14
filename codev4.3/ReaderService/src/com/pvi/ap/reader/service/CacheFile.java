package com.pvi.ap.reader.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;

import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

public class CacheFile {
	protected final static String CachePath = "/sdcard/Cache/";
	protected static long MinFreeSpace = 1024*1024; // 1MByte
	protected static long MaxCacheSize = 5 * 1024 * 1024 ;
	protected static File oldest = null;
	public static String GetCachePath() {
		return CachePath;
	}
	public static void setMinFreeSpace(long freeSpace) {
		if (freeSpace > 0)
			CacheFile.MinFreeSpace = freeSpace;
	}
	public static void setMaxCacheSize(long freeSpace) {
		if (freeSpace > 0)
			CacheFile.MaxCacheSize = freeSpace;
	}
	protected static String GetRealName(String filename)
	{
		if (filename.startsWith(CachePath))
			return filename;
		else 
			return CachePath+File.separator+filename;
	}
	protected static String GetPath(String filename)
	{
		return filename.substring(0, filename.lastIndexOf('/'));
	}
	protected static File create(String filename) {
		String realname = GetRealName(filename);
		File file = new File(realname);
		try {
			if (!file.exists()) {
				String pathname = GetPath(realname);
				File path = new File(pathname);
				if (!path.exists())
					if (!path.mkdirs())
						return null;
				if (!file.createNewFile())
					return null;
			}
		} catch (Exception e) {
			return null;
		}
		return file;
	}
	protected static File check(String filename) {
		File file = new File(GetRealName(filename));
		if (!file.exists())
			return null;
		else if (file.lastModified() > System.currentTimeMillis()) {
			return file;
		} else {
			file.delete();
			return null;
		}
	}
	
	public static boolean exist(String filename) {
		return (check(filename)!=null);
	}
	
	public static void  write(String filename, Bitmap content, int period) {
		if (period <= 0) return;
		File file = create(filename);
		if (file != null) {
			try {
				FileOutputStream fout = new FileOutputStream(file);
				content.compress(Bitmap.CompressFormat.PNG, 100, fout);
				long time = System.currentTimeMillis() + (long)period*60000;
				if (file.setLastModified(time)) {
					fout.flush();
					fout.close();
				}
				else
					file.delete();
			} catch (Exception e) {
				file.delete();
			}
		}
	}
	public static void  write(String filename, HashMap content, int period) {
		if (period <= 0) return;
		File file = create(filename);
		if (file != null) {


			try {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
				SaveObjectIntoFile soif = new SaveObjectIntoFile();
				Iterator contentI = content.keySet().iterator();
				soif.message = new HashMap() ;
				while(contentI.hasNext()){
					String key = contentI.next().toString();
					Object o = content.get(key);
					if(o instanceof Serializable){
						soif.message.put(key, o);
					}else{
						/***
						 * 
						 */
					}
				}
				out.writeObject(soif);
				long time = System.currentTimeMillis() + (long)period*60000;
				if (file.setLastModified(time)) {
					out.flush();
					out.close();
				}
				else
					file.delete();
			} catch (Exception e) {
				file.delete();
			}
		}
	}
	public static Bitmap readbmp(String filename){
		File file = check(filename);
		if (file == null)
			return null;
		else {
			try {
				Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(file));
				return bmp;
			} catch (Exception e) {
				return null;
			}
		}
	}
	public static HashMap read(String filename){
		File file = check(filename);
		if (file == null)
			return null;
		else {
			try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
				SaveObjectIntoFile soif = (SaveObjectIntoFile)in.readObject();
				HashMap content = soif.message;
				in.close();
				return content;
			} catch (Exception e) {
				return null;
			}
		}
	}
	public static void delete(String filename) {
		File file = new File(GetRealName(filename));
		if (file.isDirectory()) {
			File[] list = file.listFiles();
			if (list != null)
				for (int i=0; i<list.length; i++) {
					delete(list[i].getAbsolutePath());
				}
			file.delete();
		}
		else
			file.delete();
	}

	public static long getFreeSpace() {
		String state = Environment.getExternalStorageState(); 
		if(Environment.MEDIA_MOUNTED.equals(state)) { 
			File sdcardDir = Environment.getExternalStorageDirectory(); 
			StatFs sf = new StatFs(sdcardDir.getPath()); 
			long blockSize = sf.getBlockSize(); 
//			long blockCount = sf.getBlockCount(); 
			long availCount = sf.getAvailableBlocks(); 
			return availCount*blockSize;
		} else
			return 0;
	}
	/**
	 * @param every 每次任务的间隔时间
	 * @param delay 延迟运行时间
	 */
	public static void checkCatchFile(long period,long delay){
		if(period <= 0 || delay < 0){
			return ;
		}
		new Timer().schedule(new TimerTask(){
			public void run() {
				OuttimeThread ot = new OuttimeThread();
				ot.setPriority(Thread.MIN_PRIORITY);
				ot.start();
			};
		}, delay, period);
	}
	
	/**
	 * 优先级比较低的线程
	 * 当前时间和文件最后修改时间对比，过期则删除
	 * 不过期不处理
	 */
	static class OuttimeThread extends Thread {
		private boolean interruptFlag = false;
		private long cacheFileLenth = 0l ;

		@Override
		public void run() {
			Logger.i("delete netCacheFile", "start...");
			super.run();
			File file = new File(CachePath);
			if(!file.exists()){
				return; // file.mkdirs();
			}
			do {
				oldest = null;
				for(File dirFile:file.listFiles()){
					if(dirFile != null)
					if(dirFile.isDirectory()){
						delete(dirFile);
					}
				}
				if (interruptFlag || (oldest == null))
					break;
				else if (getFreeSpace() < MinFreeSpace) {
					delete(oldest);
					oldest = null;
				}
			} while (getFreeSpace() < MinFreeSpace || cacheFileLenth > MaxCacheSize);
			Logger.i("delete netCacheFile", "end...");
		}

		@Override
		public void interrupt() {
			interruptFlag = true ;
			super.interrupt();
		}
		
		private boolean delete(File file){
			if(interruptFlag || file==null){
				return false;
			}
			boolean isAll = true ;
			if(file.isDirectory()){
				File[] listFile = file.listFiles();
				for(File lFile:listFile){
					if(lFile != null && lFile.isFile()){
						cacheFileLenth += lFile.length() ;
						if(lFile.lastModified() <= System.currentTimeMillis()){
							lFile.delete();
							Logger.i("delete out of time file mode", lFile.getAbsolutePath());
						}else{
							if (oldest == null)
								oldest = lFile;
							else if (lFile.lastModified() < oldest.lastModified())
								oldest = lFile;
							isAll = false ;
						}
					}else{
						if(delete(lFile)){
							lFile.delete();
						}else{
							isAll = false ;
						}
					}
				}
				
			}else if(file.lastModified() <= System.currentTimeMillis()){
				cacheFileLenth += file.length() / 4096 * 4096 + 4096 ;
				file.delete();
			}else{
				cacheFileLenth += file.length() ;
				if (oldest == null)
					oldest = file;
				else if (file.lastModified() < oldest.lastModified())
					oldest = file;
				isAll = false ;
			}
			return isAll ;
		}
	}
	
	
	public static void  getErrorDocutemt(long when){
		Logger.i("getErrorDocutemt", "getErrorDocutemt:"  + when);
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
				HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
				ahmNamePair.put("type", "1");
				HashMap retMap = null;
				try {
					retMap = CPManager.getHandsetProperties(ahmHeaderMap,
							ahmNamePair);
					if (retMap == null) {
						return;
					}
				} catch (Exception e) {
					Logger.e("CacheFile",e);
					return ;
				}
				System.out.println(retMap);
				String resultCode = (String) retMap.get("result-code")
						.toString();
				if (resultCode == null
						|| !resultCode.contains("result-code: 0")) {
					return;
				}
				byte[] responseBody = (byte[]) retMap.get("ResponseBody");
				String retStr = null;
				try {
					retStr = CPManagerUtil.getStringFrombyteArray(responseBody);
				} catch (Exception e) {
				}
				try {
					FileWriter fw = new FileWriter(new File(Environment
							.getDataDirectory()
							+ "/data/com.pvi.ap.reader/files/"
							+ "error_zh_cn_net.properties"), false);
					fw.write(retStr);
					fw.flush();
					fw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, when);
		
	}
}
