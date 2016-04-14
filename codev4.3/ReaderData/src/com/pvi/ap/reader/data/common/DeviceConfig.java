package com.pvi.ap.reader.data.common;

public class DeviceConfig{
	public static String get(String para){
		if(para.equals("device_id")){
			return "018P801_20100920_001";
		}
		return "para error";
	}
}