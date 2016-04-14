package com.pvi.ap.reader.activity.pviappframe;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

/**
 * ��װ��Service�йصĹ��ú���
 * 
 * @author rd040
 * 
 */
public class PviServiceUtil {
    /**
     * �ж�ĳ���������Ƿ���������: ���硰�������ַ���
     * "com.pvi.ap.reader.service.BackGroundMusicService"
     * 
     * @param context
     * @return
     */
    public static boolean IsRunning(Context context, String serviceClassName) {
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
                .getRunningServices(30);
        myManager = null;
        
        for (RunningServiceInfo service : runningService){
            if (service.service.getClassName().equals(serviceClassName)){
                runningService = null;
                return true;
            }
        }
        runningService = null;
        return false;
    }
    
    /**
     * �ж����ֲ��ŷ����Ƿ��ں�̨����
     * @return
     */
     public static boolean IsBackgroundMusicOn(Context context) {
         ActivityManager myManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
         ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(30);
         for (RunningServiceInfo service:runningService)
             if (service.service.getClassName().equals("com.pvi.ap.reader.service.BackGroundMusicService")) 
                 return true;
         return false;
     }
}
