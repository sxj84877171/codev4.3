package com.pvi.ap.reader.service;

import java.util.HashMap;

/*
 * Ԥȡ������Ϣ
 * */
public class PrefetchTaskInfo {
	public String mName;//���ݽӿ���
	public int mContentType = 1;	//1�б���  2���б���
	public int mCur;	//��ǰ����  λ�ã������б������ݣ���ǰҳ�����ڷ��б��࣬��ǰ�½ڣ�
	public int mSize;		//���� ���б�������  ����һ��Ԥȡ��ҳ��
	
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
	//����ӿ���Ҫ�Ĳ���
	public HashMap<String,String> headers;
	public HashMap<String,String> params;
	
}
