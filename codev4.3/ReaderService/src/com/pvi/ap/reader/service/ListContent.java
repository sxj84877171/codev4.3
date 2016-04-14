package com.pvi.ap.reader.service;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pvi.ap.reader.data.common.Logger;


/*列表内容处理类*/
public class ListContent {

	private static final String TAG = "ListContent";
	public String xml;
	public String mName;//接口名称 即 数据名称
	public int itemPerpage;	//每页条数
	private ArrayList<String> pages = new ArrayList<String>();

	public ListContent(String xml, String mName, int itemPerpage) {
		super();
		this.xml = xml;
		this.mName = mName;
		this.itemPerpage = itemPerpage;
		
		Logger.i(TAG,"xml length:"+xml.length());
		
		if(!setTag(mName)){
			return ;
		}
		Logger.i(TAG,"begin to page XML to pages ...");
		page();
		
	}
	
	
	private void setTag(String listTag,String itemTag){
		setListTag(listTag);
		setItemTag(itemTag);
	}
	
	private String listTag;//列表标签
	private String itemTag;//单条数据标签


	

	public void setListTag(String listTag) {
		this.listTag = listTag;
	}

	public void setItemTag(String itemTag) {
		this.itemTag = itemTag;
	}

	/*
	 * 取xml头尾
	 * */
	public String getHaderFooter(){		
		final String regEx="<"+this.listTag+">"+"(.*?)"+"</"+this.listTag+">";
		Pattern p=Pattern.compile(regEx,Pattern.DOTALL);
		Matcher m=p.matcher(this.xml);
		if (m.find()) {
			String temp = m.group();
			int start = m.start();
			int end = m.end();
			String ret = xml.substring(0,start)+"#-#-#"+xml.substring(end);
			return ret;
		}
		else 
			return this.xml;
	}
	
	public void page(){
		Logger.i(TAG,"i am in functin page");
		String regEx="<"+this.itemTag+">"+"(.*?)"+"</"+this.itemTag+">";
		Pattern p=Pattern.compile(regEx,Pattern.DOTALL);
		Matcher m=p.matcher(this.xml);
		String thisPageXml = "";
		int fi = 0;
		while (m.find()) {
			thisPageXml += m.group()+"\n";
			if((fi+1)%this.itemPerpage==0){	//最后不完整页的处理待添加
				pages.add(thisPageXml);
				thisPageXml = "";
			}
			fi++;
		}
		if (fi%this.itemPerpage != 0) {
			pages.add(thisPageXml);
		}
	}
	public String getOnePage(int page){
		if(page<pages.size()){
			return this.pages.get(page);
		}
		
		else{
			Logger.d(TAG,"no this page :"+page);
			return null;
		}
		
	}
	public String getOnePageFull(int page){
		String hf = getHaderFooter();
		String onepage = getOnePage(page);
		if (onepage == null) return null;
		onepage = "<"+this.listTag+">\n"+onepage+"</"+this.listTag+">\n";
		return hf.replace("#-#-#",onepage);		
	}

	
	private boolean  setTag(String iFrame){
		if("getBlockContent".equals(iFrame)){
			setTag("ContentInfoList","ContentInfo");
		}else if("getRecommendContentList".equals(iFrame)){
			setTag("ContentInfoList", "ContentInfo");
		}else if("getCatalogContent".equals(iFrame)){
			setTag("ContentInfoList", "ContentInfo");
		}else if("getUserInfo".equals(iFrame)){
			setTag("ParameterList", "Parameter");
		}else if("getFriendInfo".equals(iFrame)){
			setTag("ParameterList", "Parameter");
		}else if("deleteFriend".equals(iFrame)){
			setTag("FriendList", "Friend");
		}else if("getFriendList".equals(iFrame)){
			setTag("FriendList", "Friend");
		}else if("getUnconfirmedFriendList".equals(iFrame)){
			setTag("FriendList", "Friend");
		}else if("getUserTicketList".equals(iFrame)){
			setTag("TicketList", "Ticket");
		}else if("getBlockList".equals(iFrame)){
			setTag("BlockInfoList", "BlockInfo");
		}else if("getCatalogHomePage".equals(iFrame)){
			setTag("BlockList", "Block");
		}else if("getCatalogHomePage2".equals(iFrame)){
			setTag("BlockList", "Block");
		}else if("getCatalogContent".equals(iFrame)){
			setTag("ContentInfoList", "ContentInfo");
		}else if("getSpecifiedRank".equals(iFrame)){
			setTag("RankContentList", "RankContent");
		}else if("getContentInfo".equals(iFrame)){
			setTag("ContentInfoList", "ContentInfo");
		}else if("getChapterList".equals(iFrame)){
			setTag("VolumnInfoList", "VolumnInfo");
		}else if("getAuthorInfo".equals(iFrame)){
			setTag("PropertyList", "Property");
		}else if("getBookNewsList".equals(iFrame)){
			setTag("BookNewsList", "BookNews");
		}else if("getRecommendContentList".equals(iFrame)){
			setTag("ContentInfoList", "ContentInfo");
		}else if("getFascicleList".equals(iFrame)){
			setTag("FascicleInfoList", "FascicleInfo");
		}else if("addUserBookmark".equals(iFrame)){
			setTag("BookmarkList", "Bookmark");
		}else if("deleteContentBookmark".equals(iFrame)){
			setTag("ContentInfoList", "ContentInfo");
		}else if("getUserBookmark".equals(iFrame)){
			setTag("ContentInfoList", "ContentInfo");
		}else if("getContentBookmark".equals(iFrame)){
			setTag("BookmarkList", "Bookmark");
		}else if("getFavorite".equals(iFrame)){
			setTag("ContentInfoList", "ContentInfo");
		}else if("deleteFavorite".equals(iFrame)){
			setTag("ContentInfoList", "ContentInfo");
		}else if("getBookUpdateList".equals(iFrame)){
			setTag("ContentInfoList", "ContentInfo");
		}else if("getChaptersUrl".equals(iFrame)){
			setTag("ChapterInfoList", "ChapterInfo");
		}else if("getSubscriptionList".equals(iFrame)){
			setTag("ContentInfoList", "ContentInfo");
		}else if("getConsumeHistoryList".equals(iFrame)){
			setTag("ConsumeRecordList", "ConsumeRecord");
		}else if("getBeShelvesBookList".equals(iFrame)){
			setTag("ContentInfoList", "ContentInfo");
		}else if("getCatalogSubscriptionList".equals(iFrame)){
			setTag("CatalogInfoList", "CatalogInfo");
		}else if("getAllSerialChapters".equals(iFrame)){
			setTag("ChapterInfoList", "ChapterInfo");
		}else if("getMessage".equals(iFrame)){
			setTag("MessageList", "Message");
		}else if("getComment".equals(iFrame)){
			setTag("CommentList", "Comment");
		}else if("submitComment".equals(iFrame)){
			setTag("CommentList", "Comment");
		}else if("getPresentBookList".equals(iFrame)){
			setTag("PresentBookList", "PresentBook");
		}else{
			return false ;
		}
		return true ;
	}
}
