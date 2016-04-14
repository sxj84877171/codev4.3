package com.pvi.ap.reader.data.common;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author kizan
 * 
 *         2010.12.17
 * 
 *         common date format translate tool .
 * 
 */

public class CommonDateUtils {

	/**
	 * get the date of the date format yy/MM/dd HH:mm
	 * 
	 * @param date
	 * @return
	 */
	public static Date parseDate(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		ParsePosition pos = new ParsePosition(0);
		java.util.Date datetime = formatter.parse(date, pos);
		java.sql.Timestamp ts = null;

		if (datetime != null) {

			ts = new java.sql.Timestamp(datetime.getTime());
		}

		return ts;
	}

	/**
	 * get the date time by date with these format mm/dd hh:mm
	 * 
	 * @param date
	 * @return
	 */

	public static String getMonthHourDate(String s) {
		String dateTime = "";
		String format = "MM/dd HH:mm";
		Date date = parseDate(s);
		dateTime = parseDate2StringWithFormat(date, format);
		return dateTime;
	}

	/**
	 * parse date to string with give date format
	 * 
	 * @param date
	 * @param formate
	 * @return
	 */
	public static String parseDate2StringWithFormat(Date date, String formate) {
		String dateTime = "";
		SimpleDateFormat myFmt = new SimpleDateFormat(formate);
		dateTime = myFmt.format(date);
		return dateTime;
	}
	
	
	public static void main(String s[]){
		
		System.out.println(getMonthHourDate("2010/10/11 11:22:11 221"));
		
	}
}
