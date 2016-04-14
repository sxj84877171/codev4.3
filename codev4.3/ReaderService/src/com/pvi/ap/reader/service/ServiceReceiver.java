package com.pvi.ap.reader.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * ��̨�����������չ㲥
 * @since 2010-9
 * @author �����
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class ServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		/**
		 * ����ϵͳ��������
		 */
		Intent systemUpdateIntent = new Intent(context, SystemUpdateService.class);
		context.startService(systemUpdateIntent);
		
		/**
		 * 
		 */
//		Intent systemUpdate = new Intent("com.eink.system.set.SystemUpdateService");
//		context.startService(systemUpdate);
		
		/**
		 * ��������ͬ������
		 */
//		Intent dataSynIntent = new Intent(context, DataSynService.class);
//		context.startService(dataSynIntent);
		
		/**
		 * �����ļ����ط���
		 */
		Intent fileDownloadIntent = new Intent(context,FileDownloadService.class);
		context.startService(fileDownloadIntent);
		
		//������������Ϣ����
		Intent messageCheckService = new Intent(context, MessageCheckService.class);
		context.startService(messageCheckService);

	}

}
