package com.pvi.ap.reader.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 后台服务启动接收广播
 * @since 2010-9
 * @author 孙向锦
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class ServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		/**
		 * 启动系统升级服务
		 */
		Intent systemUpdateIntent = new Intent(context, SystemUpdateService.class);
		context.startService(systemUpdateIntent);
		
		/**
		 * 
		 */
//		Intent systemUpdate = new Intent("com.eink.system.set.SystemUpdateService");
//		context.startService(systemUpdate);
		
		/**
		 * 启动数据同步服务
		 */
//		Intent dataSynIntent = new Intent(context, DataSynService.class);
//		context.startService(dataSynIntent);
		
		/**
		 * 启动文件下载服务
		 */
		Intent fileDownloadIntent = new Intent(context,FileDownloadService.class);
		context.startService(fileDownloadIntent);
		
		//启动检查书城消息服务
		Intent messageCheckService = new Intent(context, MessageCheckService.class);
		context.startService(messageCheckService);

	}

}
