package com.pvi.ap.reader.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;

public class TimeSetService extends Service {

	private static final String TAG = "TimeSetService";
	
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minuter;
	private int second;
	private String dataFormat;
	Calendar canlendar = Calendar.getInstance();


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {

	}

	@Override
	public void onDestroy() {
		
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {

				year = bundle.getInt("YEAR");
				month = bundle.getInt("MONTH");
				day = bundle.getInt("DATE");
				hour = bundle.getInt("HOUR");
				minuter = bundle.getInt("MINUTER");
				second = bundle.getInt("SECOND");
				dataFormat = bundle.getString("DATAFORMAT");
				set();
			}
		}

	}

	public void set() {
	
		
 	    
			
			System.out.println("======canlendar========"+canlendar);
		    canlendar.set(Calendar.YEAR, year);
		    canlendar.set(Calendar.MONTH, month);
		    canlendar.set(Calendar.DATE, day);
		    canlendar.set(Calendar.HOUR_OF_DAY, hour);
		    canlendar.set(Calendar.MINUTE, minuter);
		    canlendar.set(Calendar.SECOND, second);
		    
		    /*Date date = new Date(year, month, day, hour, minuter,second);
		    
		    DateFormat df = new SimpleDateFormat("yyyy-mm-dd hh:MM:ss");
		    System.out.println(df.format(date));
		    */
		
		    //TimeZone.setDefault(TimeZone.getTimeZone(("Asia/Taipei")));
	        long when = canlendar.getTimeInMillis();
	        //if (when / 1000 < Integer.MAX_VALUE) {
	        SystemClock.setCurrentTimeMillis(when);
	        //}
	        
	        Settings.System.putString(getContentResolver(), Settings.System.TIME_12_24, dataFormat);
	        
	        /*
	        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	        String localTimeZoneID = "Asia/Shanghai"; // "GMT+8"
	        alarm.setTimeZone(localTimeZoneID);
	     
	        
	       
	        String format = "yyyy/MM/dd";
	        Settings.System.putString(getContentResolver(), Settings.System.DATE_FORMAT, format);Settings.System.putString(getContentResolver(), Settings.System.DATE_FORMAT, format);
			*/
	}





}
