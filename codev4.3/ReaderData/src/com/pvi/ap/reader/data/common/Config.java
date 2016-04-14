package com.pvi.ap.reader.data.common;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import android.os.Environment;
import android.util.Log;

/**
 * 读取配置文件类
 * @author 彭见宝
 *
 */
public class Config{
	
	public static final String TAG = "Config";
	
	public static final String configFileName = Environment.getDataDirectory() + "/data/com.pvi.ap.reader/files/"+"config.properties";
	
	public static final String systemSetFileName = Environment.getDataDirectory() + "/data/com.pvi.ap.reader/shared_prefs/systemset.xml";
	
	public static Properties properties = new Properties();
	
	static {
		
		 
		try {
			FileInputStream fin = new FileInputStream(configFileName);
			properties.load(fin);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.i(TAG, e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.i(TAG, e.toString());
		}
		
	}
	
	public static void init(){
		try {
			FileInputStream fin = new FileInputStream(configFileName);
			 properties.load(fin);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.i(TAG, e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.i(TAG, e.toString());
		}
	}
	
	
	public static String getString(String filed){
		if(properties!=null){
			return properties.getProperty(filed, "");
		}else{
			return "";
		}
	}
	
	public static int getInt(String filed){
		if(properties!=null){
			String numStr = properties.getProperty(filed, "0");
			int num = 0;
			try {
				Integer.parseInt(numStr);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return num;
		}else{
			return 0;
		}
	}
	public static long getLong(String filed){
		if(properties!=null){
			String numStr = properties.getProperty(filed, "0");
			long num = Long.parseLong(numStr);
			return num;
		}else{
			return 0;
		}
	}
	public static float getFloat(String filed){
		if(properties!=null){
			String numStr = properties.getProperty(filed, "0");
			float num = Float.parseFloat(numStr);
			return num;
		}else{
			return 0;
		}
	}
	public static boolean getBoolean(String filed){
		if(properties!=null){
			String numStr = properties.getProperty(filed, "");
			boolean num = Boolean.parseBoolean(numStr);
			return num;
		}else{
			return false;
		}
	}
	public static boolean setString(String filed,String value){
		if(properties!=null){
			//properties.put(filed,value);
			properties.setProperty(filed, value);
			try {
				properties.store(new FileOutputStream(configFileName), "store");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Log.i(TAG, e.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Log.i(TAG, e.toString());
			} 
			return true;
		}else{
			return false;
		}
	}

}
