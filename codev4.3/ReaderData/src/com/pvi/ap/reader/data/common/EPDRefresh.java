package com.pvi.ap.reader.data.common;

import android.os.SystemProperties;

public class EPDRefresh
{
	//private String 

	//A2 mode   32
	static	public void refreshA2()
	{
		android.os.SystemProperties.set("marvell.ebook.lcd.mode","0");     //GC16  		
	}
	
	static	public void refreshA2Dither()
	{
		android.os.SystemProperties.set("marvell.ebook.lcd.mode","34");     //A2 + Dither  		
	}
	
	//GC16 + once FLASH MODE 
	static	public void refreshGCOnceFlash()
	{
		android.os.SystemProperties.set("marvell.ebook.lcd.mode","0");     
        android.os.SystemProperties.set("marvell.ebook.lcd.flash","2");     
	}

	//GC16 + always FLASH MODE 
	static public void refreshGCAlwaysFlash()
	{
		android.os.SystemProperties.set("marvell.ebook.lcd.mode","0");     
        android.os.SystemProperties.set("marvell.ebook.lcd.flash","1");    
	}	
	
	//GC16 + NOFLASH MODE 
	static public void refreshGCNoFlash()
	{
		android.os.SystemProperties.set("marvell.ebook.lcd.mode","0");   
    //    android.os.SystemProperties.set("marvell.ebook.lcd.flash","0");    
	}
	
	//DU mode
	static public void refreshDU()
	{
		android.os.SystemProperties.set("marvell.ebook.lcd.mode","1");     
		
		
	}
	
	//GC4 mode 
	static public void refreshGC4()
	{
		android.os.SystemProperties.set("marvell.ebook.lcd.mode","16");      
	}
	

	//DU + Dither mode 
	static public void refreshDUDither()
	{
		android.os.SystemProperties.set("marvell.ebook.lcd.mode","0");    
	}
	//DC mode 
	static public void refreshDC()
	{
		android.os.SystemProperties.set("marvell.ebook.lcd.mode","64");    
	}
	//only Flash
	static public void refreshOnceFlash()
	{
        android.os.SystemProperties.set("marvell.ebook.lcd.flash","2");      //flash	
	}
	
	
}
