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
 * 预取处理线程
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
		
		
		//模拟任务执行耗时

/*			for(int i=0;i<1000;i++){
			Log.i(TAG,"i working ..."+i);
			}*/
			
			
			
			//真实处理流程
			

			
//			Logger.i(TAG,"now waiting task:"+app.ptiStack.size());
			if(app.ptiStack.size()>10){
				synchronized(app.ptiStack){
					try {
						app.ptiStack.subList(0,10);//最多处理最近的10个任务
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
					
					
					//针对分页的列表数据
					if(curTaskInfo.mContentType==1){
						
						
						
						int start = 0;
						try {
							start = Integer.parseInt(curTaskInfo.params.get("start"));
						} catch (NumberFormatException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					
		
						//int perpageCount = ContentInfo.getPerpageCount(curTaskInfo.mName);//取参数中的count
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
	
							//不需要异步取本页
							
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
							//批量获取剩余本缓存区块的其它页
							
							
		
							//确定面取多页时需要的变量值
							int startpage = curpage+1; 	//当前页 的下一页 开始
							int endpage =(curpage/cachePageSize+1)*cachePageSize;		
							
							Logger.i(TAG,"try batch fetch : startpage:"+startpage+",endpage:"+endpage);
							
							getMultypage(curTaskInfo, startpage, endpage);
							
						}else{
							Logger.i(TAG,"perpage cache check: iname:"+curTaskInfo.mName+",start:"+start+" cache exist!!!");
						}
						
						
						//多页缓存的最后一页时
						if(curpage%cachePageSize==0){
							Logger.i(TAG,"now reach last page:"+curpage);
							
							//看看下一批的第一页，有没有缓存，没有即视为需要取下一批
							int nextStart = curpage*perpageCount+1;
							Logger.i(TAG,"next 'start' :"+nextStart);
							HashMap<String,String> paramsNextPage = (HashMap<String,String>)curTaskInfo.params.clone();
							paramsNextPage.put("start", String.valueOf(nextStart));
							if(!CacheFile.exist(NetCacheService.GetPathName(curTaskInfo.mName,paramsNextPage))){
								Logger.i(TAG,"first page of multy catch check: iname:"+curTaskInfo.mName+",start"+nextStart+" cache not exist! goto batch fetch...");
								//确定面取多页时需要的变量值
								int startpage = curpage+1; 	//当前页 的下一页 开始
								int endpage =curpage+cachePageSize;	
								getMultypage(curTaskInfo, startpage, endpage);
							}else{
								Logger.i(TAG,"first page of multy catch check: iname:"+curTaskInfo.mName+",start"+nextStart+" cache exist!");
							}
						}else{
							Logger.i(TAG,"not last page, return ");
						}
						
		
						
						
				
						
					}else{//针对章节类数据
						
						//检测下一条数据是否存在缓存文件，如果不存在，取之
						
						
					}
					
					
				}
			}
			

			Logger.i(TAG,"i runnig over !");
	}
	
	
	
	//用于列表类数据，通过 参数start 得到当前页码
	private int calCurpageFromStart(int start,int itemsPerpage){
		int curpage = 1;
		curpage = start/itemsPerpage+1;		
		return curpage;
	}
	
	
	
	
	//用于列表类数据     取多页数据
	private void getMultypage(PrefetchTaskInfo curTaskInfo,int startpage,int endpage){
		

		Logger.i(TAG,"run into get mult page,startpage:"+startpage+",endpage:"+endpage);
		
		ServApp app = ((ServApp) mContext);
		
		//如果 正在处理中的 包含了 这些数据，则 不执行新的远程获取
		if(app.isPrefetching(curTaskInfo.mName, String.valueOf(startpage), String.valueOf(endpage))){
			Logger.i(TAG,"these pages is prefetching ,just wait ...");
			return;
		}
		
		
		String[] doing = new String[4];
		doing[0] = curTaskInfo.mName;
		doing[1] = "1";
			doing[2] = String.valueOf(startpage);
				doing[3] = String.valueOf(endpage);
		
		app.prefetchingList.add(doing);//加入 处理中信息列表。。。
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
			//temp 存放的是平台返回的批量内容
			Object temp = m.invoke(o, curTaskInfo.headers, paramsGetmultypage);
			if (temp instanceof HashMap){
				
				
				ret = (HashMap)temp;//把结果转化回来，并拷贝一份？
				//把xml取出来，本身无需再保留
			
			
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
					
					//把多页数据逐页保存
					int pages = endpage+1-startpage;
					int thepage = startpage;

					for(int i=0;i<pages;i++){
						
						
						
						ret.put("ResponseBody","");	//先置空
						Logger.i(TAG,"save one page :"+thepage);
						//改变原始参数中的start count,改变结果中的xml
						HashMap<String,String> paramsOnepage = (HashMap<String,String>)curTaskInfo.params.clone();
						int thisPageStart = (thepage-1)*perpageCount+1;
						paramsOnepage.put("start", ""+thisPageStart);	//?
						paramsOnepage.put("count", ""+perpageCount);					//?
						
						Logger.i(TAG,"begin to save one page ,start:"+thisPageStart+",count:"+perpageCount+", relative page index i:"+i);
						
						
						
						final String onePageXml = lc.getOnePageFull(i);
						Logger.i(TAG,"save one page ,length:"+onePageXml.length());
						ret.put("ResponseBody",onePageXml.getBytes());//把分页后xml放入ret
						
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
			
			
			
			//从 处理中信息列表 移除
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
			
			//从 处理中信息列表 移除
			app.prefetchingList.remove(doing);
			Logger.i(TAG,"remove doing list : iname:"+curTaskInfo.mName+",startpage:"+startpage+",endpage:"+endpage);
				return;
		} catch (Exception e) {
			e.printStackTrace();
			
			//从 处理中信息列表 移除
			app.prefetchingList.remove(doing);
			Logger.i(TAG,"remove doing list : iname:"+curTaskInfo.mName+",startpage:"+startpage+",endpage:"+endpage);
			
			return;
		}
		
	}


}
