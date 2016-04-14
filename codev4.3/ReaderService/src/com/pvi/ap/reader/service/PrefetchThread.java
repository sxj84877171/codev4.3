package com.pvi.ap.reader.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.pvi.ap.reader.data.common.Logger;

import android.content.Context;
import android.util.Xml.Encoding;


/*
 * Ԥȡ�����߳�
 * */
public class PrefetchThread extends Thread {

	private static final String TAG = "PrefetchThread";
	private Context mContext;
	

	public PrefetchThread(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		final ServApp app = ((ServApp) mContext);
		
		Logger.e(TAG,app.mMyname+" new thread! i begin running ");
		
		
		//ģ������ִ�к�ʱ

/*			for(int i=0;i<1000;i++){
			Log.i(TAG,"i working ..."+i);
			}*/
			
			
			
			//��ʵ��������
			

			
//			Logger.i(TAG,"now waiting task:"+app.ptiStack.size());
			if(app.ptiStack.size()>10){
				synchronized(app.ptiStack){
					try {
						app.ptiStack.subList(0,10);//��ദ�������10������
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			synchronized(app.ptiStack){
				if(app.ptiStack!=null&&app.ptiStack.size()>0){					
				
					final PrefetchTaskInfo curTaskInfo = app.ptiStack.pop();
					Logger.i(TAG,"task info == iname:"+curTaskInfo.mName+",start:"+curTaskInfo.params.get("start"));
					
					
					//��Է�ҳ���б�����
					if(curTaskInfo.mContentType==1){
						
						
						
						int start = 0;
						try {
							start = Integer.parseInt(curTaskInfo.params.get("start"));
						} catch (NumberFormatException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					
		
						//int perpageCount = ContentInfo.getPerpageCount(curTaskInfo.mName);//ȡ�����е�count
						int perpageCount = 0;
						try {
							perpageCount = Integer.parseInt(curTaskInfo.params.get("count"));
						} catch (NumberFormatException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						int cachePageSize = NetCacheService.GetNetCachePage(curTaskInfo.mName);			
						
						int curpage = calCurpageFromStart(start,perpageCount);
						
						
						
						Logger.i(TAG,"perpage check : cache file :"+NetCacheService.GetPathName(curTaskInfo.mName,curTaskInfo.params));
						
						if(!CacheFile.exist(NetCacheService.GetPathName(curTaskInfo.mName,curTaskInfo.params))){
	
							//����Ҫ�첽ȡ��ҳ
							
/*							HashMap ret = null;
							try {
								Class c = Class.forName("com.pvi.ap.reader.data.external.manager.CPManager");
								Object o = c.newInstance();
								Method m = c.getMethod(curTaskInfo.mName, new Class[]{Class.forName("java.util.HashMap"),Class.forName("java.util.HashMap")});
								Object temp = m.invoke(o, curTaskInfo.headers, curTaskInfo.params);
								if (temp instanceof HashMap)
									ret = (HashMap)temp;
									if (NetCacheService.ResponseCode(ret) == 0) {
										CacheFile.write(NetCacheService.GetPathName(curTaskInfo.mName, curTaskInfo.params), ret, NetCacheService.GetNetCacheTime(curTaskInfo.mName));
									}
								else
									return;
							} catch (InvocationTargetException e) {
								Throwable err = e.getTargetException();
								if (err instanceof HttpException)
									throw (HttpException)err;
								else if (err instanceof IOException) 
									throw (IOException)err;
								else
									return;
							} catch (Exception e) {
								return;
							}*/
							
							
		
							Logger.i(TAG,"perpage cache check: iname:"+curTaskInfo.mName+",start:"+start+" cache not exist ");
							//������ȡʣ�౾�������������ҳ
							
							
		
							//ȷ����ȡ��ҳʱ��Ҫ�ı���ֵ
							int startpage = curpage+1; 	//��ǰҳ ����һҳ ��ʼ
							int endpage =(curpage/cachePageSize+1)*cachePageSize;		
							
							Logger.i(TAG,"try batch fetch : startpage:"+startpage+",endpage:"+endpage);
							
							getMultypage(curTaskInfo, startpage, endpage);
							
						}else{
							Logger.i(TAG,"perpage cache check: iname:"+curTaskInfo.mName+",start:"+start+" cache exist!!!");
						}
						
						
						//��ҳ��������һҳʱ
						if(curpage%cachePageSize==0){
							Logger.i(TAG,"now reach last page:"+curpage);
							
							//������һ���ĵ�һҳ����û�л��棬û�м���Ϊ��Ҫȡ��һ��
							int nextStart = curpage*perpageCount+1;
							Logger.i(TAG,"next 'start' :"+nextStart);
							HashMap<String,String> paramsNextPage = (HashMap<String,String>)curTaskInfo.params.clone();
							paramsNextPage.put("start", String.valueOf(nextStart));
							if(!CacheFile.exist(NetCacheService.GetPathName(curTaskInfo.mName,paramsNextPage))){
								Logger.i(TAG,"first page of multy catch check: iname:"+curTaskInfo.mName+",start"+nextStart+" cache not exist! goto batch fetch...");
								//ȷ����ȡ��ҳʱ��Ҫ�ı���ֵ
								int startpage = curpage+1; 	//��ǰҳ ����һҳ ��ʼ
								int endpage =curpage+cachePageSize;	
								getMultypage(curTaskInfo, startpage, endpage);
							}else{
								Logger.i(TAG,"first page of multy catch check: iname:"+curTaskInfo.mName+",start"+nextStart+" cache exist!");
							}
						}else{
							Logger.i(TAG,"not last page, return ");
						}
						
		
						
						
				
						
					}else{//����½�������
						
						//�����һ�������Ƿ���ڻ����ļ�����������ڣ�ȡ֮
						
						
					}
					
					
				}
			}
			

			Logger.i(TAG,"i runnig over !");
	}
	
	
	
	//�����б������ݣ�ͨ�� ����start �õ���ǰҳ��
	private int calCurpageFromStart(int start,int itemsPerpage){
		int curpage = 1;
		curpage = start/itemsPerpage+1;		
		return curpage;
	}
	
	
	
	
	//�����б�������     ȡ��ҳ����
	private void getMultypage(PrefetchTaskInfo curTaskInfo,int startpage,int endpage){
		

		Logger.i(TAG,"run into get mult page,startpage:"+startpage+",endpage:"+endpage);
		
		ServApp app = ((ServApp) mContext);
		
		//��� ���ڴ����е� ������ ��Щ���ݣ��� ��ִ���µ�Զ�̻�ȡ
		if(app.isPrefetching(curTaskInfo.mName, String.valueOf(startpage), String.valueOf(endpage))){
			Logger.i(TAG,"these pages is prefetching ,just wait ...");
			return;
		}
		
		
		String[] doing = new String[4];
		doing[0] = curTaskInfo.mName;
		doing[1] = "1";
			doing[2] = String.valueOf(startpage);
				doing[3] = String.valueOf(endpage);
		
		app.prefetchingList.add(doing);//���� ��������Ϣ�б�����
		Logger.i(TAG,"add doing list : iname:"+curTaskInfo.mName+",startpage:"+startpage+",endpage:"+endpage);
		
		Logger.i(TAG,"get mult page,startpage:"+startpage+",endpage:"+endpage);
		
		//int perpageCount = ContentInfo.getPerpageCount(curTaskInfo.mName);
		int perpageCount = 0;
		try {
			perpageCount = Integer.parseInt(curTaskInfo.params.get("count"));
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		int cachePageSize = NetCacheService.GetNetCachePage(curTaskInfo.mName);	

		
		
		HashMap<String,String> paramsGetmultypage = (HashMap<String,String>)curTaskInfo.params.clone();
		
		int thisBatchStart = (startpage-1)*perpageCount+1;
		int thisBatchCount = cachePageSize*perpageCount;
		paramsGetmultypage.put("start", ""+thisBatchStart);
		paramsGetmultypage.put("count", ""+thisBatchCount);
		
		Logger.i(TAG,"batch get data, send interface params    start:"+thisBatchStart+",count:"+thisBatchCount);

		HashMap<String,Object> ret = null;
		try {
			Class c = Class.forName("com.pvi.ap.reader.data.external.manager.CPManager");
			Object o = c.newInstance();
			Method m = c.getMethod(curTaskInfo.mName, new Class[]{Class.forName("java.util.HashMap"),Class.forName("java.util.HashMap")});
			//temp ��ŵ���ƽ̨���ص���������
			Object temp = m.invoke(o, curTaskInfo.headers, paramsGetmultypage);
			if (temp instanceof HashMap){
				
				
				ret = (HashMap)temp;//�ѽ��ת��������������һ�ݣ�
				//��xmlȡ���������������ٱ���
			
			
			String xml = "";
			try {
				xml = new String((byte[])ret.get("ResponseBody"));
				if(xml != null && xml instanceof String){
					xml = xml.replaceAll("\\s", "");
					Logger.i(TAG,"save one page ,total xml length:"+xml.length());
				}else{
					Logger.i(TAG,"save one page total xml is null");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Logger.e(TAG,"save one page , err!");
				e.printStackTrace();
			}
				
				if (NetCacheService.ResponseCode(ret) == 0) {
					
					ListContent lc = new ListContent(xml,curTaskInfo.mName,perpageCount);
					
					//�Ѷ�ҳ������ҳ����
					int pages = endpage+1-startpage;
					int thepage = startpage;

					for(int i=0;i<pages;i++){
						
						
						
						ret.put("ResponseBody","");	//���ÿ�
						Logger.i(TAG,"save one page :"+thepage);
						//�ı�ԭʼ�����е�start count,�ı����е�xml
						HashMap<String,String> paramsOnepage = (HashMap<String,String>)curTaskInfo.params.clone();
						int thisPageStart = (thepage-1)*perpageCount+1;
						paramsOnepage.put("start", ""+thisPageStart);	//?
						paramsOnepage.put("count", ""+perpageCount);					//?
						
						Logger.i(TAG,"begin to save one page ,start:"+thisPageStart+",count:"+perpageCount+", relative page index i:"+i);
						
						
						
						final String onePageXml = lc.getOnePageFull(i);
						Logger.i(TAG,"save one page ,length:"+onePageXml.length());
						ret.put("ResponseBody",onePageXml.getBytes());//�ѷ�ҳ��xml����ret
						
						Logger.i(TAG,"!!save one page ,write cache file:"+NetCacheService.GetPathName(curTaskInfo.mName, paramsOnepage));
						CacheFile.write(NetCacheService.GetPathName(curTaskInfo.mName, paramsOnepage), ret, NetCacheService.GetNetCacheTime(curTaskInfo.mName));
					
						thepage++;
					}
					
					//CacheFile.write(NetCacheService.GetPathName(curTaskInfo.mName, curTaskInfo.params), ret, NetCacheService.GetNetCacheTime(curTaskInfo.mName));
				}else{
					
					Logger.i(TAG,"!!no need cache,preftch !");
				}
				
				
			}else{	
				
				Logger.e(TAG,"temp is not a HashMap");
				return;
			}
			
			
			
			//�� ��������Ϣ�б� �Ƴ�
			Logger.i(TAG,"batch fetch finished!!!!!!");
			app.prefetchingList.remove(doing);
			Logger.i(TAG,"remove doing list : iname:"+curTaskInfo.mName+",startpage:"+startpage+",endpage:"+endpage);
			
		} catch (InvocationTargetException e) {
			/*Throwable err = e.getTargetException();
			if (err instanceof HttpException)
				throw (HttpException)err;
			else if (err instanceof IOException) 
				throw (IOException)err;
			else*/
			
			e.printStackTrace();
			
			//�� ��������Ϣ�б� �Ƴ�
			app.prefetchingList.remove(doing);
			Logger.i(TAG,"remove doing list : iname:"+curTaskInfo.mName+",startpage:"+startpage+",endpage:"+endpage);
				return;
		} catch (Exception e) {
			e.printStackTrace();
			
			//�� ��������Ϣ�б� �Ƴ�
			app.prefetchingList.remove(doing);
			Logger.i(TAG,"remove doing list : iname:"+curTaskInfo.mName+",startpage:"+startpage+",endpage:"+endpage);
			
			return;
		}
		
	}


}
