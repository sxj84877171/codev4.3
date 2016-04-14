package com.pvi.ap.reader.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class ServApp extends Application {

	private static final String TAG = "ServApp";
	
	
	//Ԥȡ������Ϣ���ջ  PrefetchTaskInfoStack
	public Stack<PrefetchTaskInfo> ptiStack = new Stack<PrefetchTaskInfo>();
	
	//ִ���е�Ԥȡ����Ĺؼ���Ϣ�б�    [0]iname �ӿ���  ��1�� �������ͣ�1�б�  2�½�      ������б������ݵ�Ԥȡ���񣬡�2��startpage [3]endpage��������½�  [2]�½�id
	public ArrayList<String[]> prefetchingList = new ArrayList<String[]>(); 
	
	
	public int c = 0;
	
	
	
	public String mMyname = "servapp";

	//@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out.close();
		System.err.close();
		Log.i(TAG,"ServApp running ...");
		
/*
 * 
 * 		//�ô������Ԥȡ����
		final String ifname = "getBlockContent";
		final HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		final HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

			
		
		Timer mTimer2 = null;
		
		TimerTask addtask = new TimerTask() {
			@Override
			public void run() {
				Logger.i(TAG,"add task NO."+c+"  ...");
				
				if(c>5){
					Logger.e(TAG,"max task 20 added! not add again!");
					return;
				}
				
				ahmNamePair.put("blockId","1");
				ahmNamePair.put("start",""+(c*7+1));
				ahmNamePair.put("count","7");
				Prefetch.addTask(getApplicationContext(),ifname, 1,ahmHeaderMap, ahmNamePair);
				Logger.i(TAG,"add task info == ifname:"+ifname+",start:"+ahmNamePair.get("start"));
				c++;

			}
		};
		
		
		
		if (mTimer2 != null) {
			mTimer2.cancel();
		} else {
			mTimer2 = new Timer();
		}
		mTimer2.schedule(addtask, new java.util.Date(), 500);	//10�뷭��ҳ
		
*/
		
		//��ʱ�������̳߳صĶ����ŵ�����
		int poolSize = 5;
		final ThreadPoolExecutor pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(poolSize);
		
		
		//��ʱѭ��ִ�У�����ptiStack��Ϊ�գ��ʹ������߳�
		long mTimeTaskTime = 1000;//���ʱ�� 1s  ���û���� ��ֱ�ӷ���

		Timer mTimer = null;
		final Context context = this;
		TimerTask mTimerTask = new TimerTask() {
			@Override
			public void run() {
//				Log.e(TAG,"check is there exist PREFETCH task ...");
				ServApp app = ((ServApp) context);
				Stack<PrefetchTaskInfo> ptiStack;
				ptiStack = app.ptiStack;
				if( ptiStack!=null && ptiStack.size()>0 ){
					Log.i(TAG,"try to start new task ");
					
					//ִ��Ԥȡ����
					pool.execute(new PrefetchThread(context));	
				}
			}
		};
		
		
		if (mTimer != null) {
			mTimer.cancel();
		} else {
			mTimer = new Timer();
		}
		mTimer.schedule(mTimerTask, new java.util.Date(), mTimeTaskTime);

		
	}
	


	public boolean isPrefetching(String iname,String param1,String param2){
		
		Log.i(TAG,"check isPrefetching:"+"iname:"+iname+",param1:"+param1+",param2:"+param2);
		
		boolean result = false;
		final int len = prefetchingList.size();
		for(int i = 0;i<len;i++){
			String[] doing = prefetchingList.get(i);
			Logger.i(TAG,"00");
			if(doing[0].equals(iname)){
				if(doing[1].equals("1")){//��ҳ�б�������
					Logger.i(TAG,"11");
					int startpage =1;
					int endpage = 1;
					try {
						startpage = Integer.parseInt(param1);
						endpage = Integer.parseInt(param2);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					int startpage2 =1;
					int endpage2 = 1;
					try {
						startpage2 = Integer.parseInt(doing[2]);
						endpage2 = Integer.parseInt(doing[3]);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(startpage>=startpage2  && endpage<=endpage2){
						Log.i(TAG,"check isPrefetching: prefeching !");
						result =  true;
					}else{
						Logger.i(TAG,"check isPrefetching: this, no prefeching !");
					}
					
					
				}
			}
		}
		
		Log.i(TAG,"check isPrefetching: all not prefeching");
		return result;
	}

}
