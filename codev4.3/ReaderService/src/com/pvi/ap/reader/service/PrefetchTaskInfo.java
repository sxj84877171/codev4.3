package com.pvi.ap.reader.service;

import java.util.HashMap;

/*
 * 预取任务信息
 * */
public class PrefetchTaskInfo {
	public String mName;//数据接口名
	public int mContentType = 1;	//1列表类  2非列表类
	public int mCur;	//当前内容  位置：对于列表类内容，当前页；对于非列表类，当前章节？
	public int mSize;		//对于 “列表”类内容  设置一次预取的页数
	
	public PrefetchTaskInfo(String mName, int mContentType,
			HashMap<String, String> headers, HashMap<String, String> params) {
		super();
		
		headers.put("proxyIP", "10.0.2.2");
		headers.put("port", "8888");
		
		this.mName = mName;
		this.mContentType = mContentType;
		this.headers = headers;
		this.params = params;
		
	}
	//网络接口需要的参数
	public HashMap<String,String> headers;
	public HashMap<String,String> params;
	
}
