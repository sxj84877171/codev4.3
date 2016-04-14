package com.pvi.ap.reader.activity;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviMenuItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.activity.pviappframe.PviPopupWindow;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.common.SortUtil;
import com.pvi.ap.reader.data.content.Bookmark;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.*;

/**
 * 最近阅读
 * @author 刘剑雄
 *
 */
public class RecentBookActivity extends PviActivity implements Pageable{
	PviDataList listView;
	ArrayList<PviUiItem[]> datalist;

	final public static String authorPrefix = "作者：";

	private RelativeLayout norecord_layout = null;
	private TextView tishi=null;
	private TextView gotowirelessstore = null;

	private int orderType = 1;  
	private boolean isSearch = false;
	/**
	 * 每页多少记录
	 */
	private int number=7;//每页多少记录
	/**
	 * 页码
	 */
	private int pageNum=1;//页码
	/**
	 * 总页
	 */
	private int count=1;//总页
	/**
	 * 数据条数
	 */
	private int rows=0;//数据行数
	private Bitmap[] coverlogo = new Bitmap[7];
	//	private int delIndex = 0;
	private String searchkey = "";
	private String order=Bookmark.CreatedDate.toLowerCase()+" DESC ";

	private Handler listHandler;
	private boolean isshow = false;
	protected final String TAG = "MyFavoriteActivity";
	private int pageCounter = 0;//翻页计数器
	private void compPageCounter() {
		if (GlobalVar.deviceType == 1) {
			pageCounter++;
			if (pageCounter == 5) {
				pageCounter = 0;
				// gc16 full flash window
				Logger.d(TAG, "gc16 full");
//				getWindow().getDecorView().getRootView().postInvalidate(
//						View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16
//						| View.EINK_UPDATE_MODE_FULL);
			} else {
				Logger.d(TAG, "DU content");
			}
		}
	}


	ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

	public static int containvalue(ArrayList<HashMap<String, Object>> list,String key, String value)
	{
		HashMap<String, Object> map=null;
		for(int i=0;i < list.size();i++)
		{
			map = list.get(i);
			if(map.containsKey(key) &&value.equals(map.get(key)))
			{
				return i;
			}
		}
		return -1;
	}

	protected void onResume() {
		// TODO Auto-generated method stub
		this.setOnPmShow(new OnPmShowListener(){

			@Override
			public void OnPmShow(PviPopupWindow popmenu) {               
				if(orderType==1){
					final PviMenuItem vSortByTime = getMenuItem("time");
					if(vSortByTime!=null){
						vSortByTime.isFocuse =true;
					}                    
				}else if(orderType==2){
					final PviMenuItem vSortByBook = getMenuItem("bookname");
					if(vSortByBook!=null){
						vSortByBook.isFocuse = true;
					}
				} else if(orderType==3){
					final PviMenuItem vSortByBook = getMenuItem("author");
					if(vSortByBook!=null){
						vSortByBook.isFocuse = true;
					}
				}        
			}});

		long TimeStart = System.currentTimeMillis();
		Logger.i("Time","RecentBookActivity" + Long.toString(TimeStart));
		showMessage("进入最近阅读...","20000");
		orderType = 1;
		isshow = false;
		listHandler = new Handler()
		{

			@Override
			public void dispatchMessage(Message msg) {
				// TODO Auto-generated method stub
				super.dispatchMessage(msg);
				switch (msg.what)
				{
				case 1:
					final PviAlertDialog pd = new PviAlertDialog(RecentBookActivity.this.getParent());
					pd.setTitle("温馨提示");
					pd.setMessage("选择书籍不存在！",Gravity.CENTER);
					pd.setTimeout(10000); // 可选参数 延时5000ms后自动关闭
					pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							pd.dismiss();
						}
					});
					pd.show();
					break;
				}
			}

		};

		Thread checkUpdate = new Thread() {  
			public void run() {
				try
				{
					getDataFromNetwork();
					getDate();
					if(list!=null)
					{	
						SortUtil sorttime = new SortUtil("readtime");
						sorttime.setDescending(false);
						Collections.sort(list, sorttime);
					}
					listHandler.post(setdata);
				}
				catch(Exception e)
				{
					Logger.e("RecentBook", e); 
				}
			}
		};
		checkUpdate.start();

		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub

		searchkey="";
		this.isSearch=false;
		super.onPause();
	}


	public String getDataFromNetwork(){
		list.clear();
		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

		HashMap responseMap = null;
		try {

			responseMap = NetCache.getSystemBookmark(ahmHeaderMap, ahmNamePair);
			if(responseMap!=null)
			{
				if (!responseMap.get("result-code").toString().contains(
				"result-code: 0")) {
					return responseMap.get("result-code").toString();
				}
			}

		} catch (HttpException e) {
			//
			e.printStackTrace();
			Logger.e("Reader", e.toString());

		} 
		catch (SocketTimeoutException e) {
			e.printStackTrace();
			Logger.e("Reader", e.toString());
		}catch (IOException e) {
			//
			e.printStackTrace();
			Logger.e("Reader", e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			Logger.e("Reader", e.toString());
		}

		Element root = null;
		if((responseMap==null)||(responseMap.get("ResponseBody")==null))
		{
			return "1";
		}
		byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
//		try {
//			System.out.println("返回的XML为：");
//			System.out.println(CPManagerUtil
//					.getStringFrombyteArray(responseBody));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}

		Document dom = null;
		try {
			dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}catch (SAXException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		if(dom==null)
		{
			return "1";
		}
		root = dom.getDocumentElement();
		NodeList nl = null;
		Element temp = null;
		NodeList subnl = null;
		HashMap<String, Object> map = null;
		nl = root.getElementsByTagName("ContentInfo");
		for(int i=0; i < nl.getLength(); i++)
		{
			map = new HashMap<String, Object>();
			temp = (Element) nl.item(i);
			subnl = temp.getElementsByTagName("contentID");
			if(subnl!=null && subnl.item(0)!=null)
			{
				map.put("contentid", subnl.item(0).getFirstChild().getNodeValue());
			}
			else
			{
				map.put("contentid", "");
			}
			subnl = temp.getElementsByTagName("contentName");
			if(subnl!=null && subnl.item(0)!=null)
			{
				map.put("bookname", subnl.item(0).getFirstChild().getNodeValue());
			}
			else
			{
				map.put("bookname", "");
			}
			subnl = temp.getElementsByTagName("bookmarkID");
			if(subnl!=null && subnl.item(0)!=null)
			{
				map.put("ID", subnl.item(0).getFirstChild().getNodeValue());
			}
			else
			{
				map.put("ID", "");
			}
			subnl = temp.getElementsByTagName("chapterID");
			if(subnl!=null && subnl.item(0)!=null)
			{
				map.put("chapterid", subnl.item(0).getFirstChild().getNodeValue());
			}
			else
			{
				map.put("chapterid", "");
			}
			subnl = temp.getElementsByTagName("chapterName");
			if(subnl!=null && subnl.item(0)!=null)
			{
				map.put("chaptername", subnl.item(0).getFirstChild().getNodeValue());
			}
			else
			{
				map.put("chaptername", "");
			}
			subnl = temp.getElementsByTagName("position");
			if(subnl!=null && subnl.item(0)!=null)
			{
				map.put("readposition", subnl.item(0).getFirstChild().getNodeValue());
			}
			else
			{
				map.put("readposition", "");
			}

			subnl = temp.getElementsByTagName("createTime");
			if(subnl!=null && subnl.item(0)!=null)
			{
				map.put("readtime",subnl.item(0).getFirstChild().getNodeValue());
			}
			else
			{
				map.put("readtime", "");
			}
			map.put("type", "0");
			map.put("author", "");
			subnl = temp.getElementsByTagName("smallLogo");
			if(subnl!=null && subnl.item(0)!=null)
			{
				map.put("filepath",subnl.item(0).getFirstChild().getNodeValue());
			}
			else
			{
				map.put("filepath", "");
			}
			map.put("certpath", "");
			map.put("sourcetype", "4");
			map.put("FontSize", "");
			map.put("LineSpace", "");

			int ret = RecentBookActivity.containvalue(list, "contentid", map.get("contentid").toString());

			if(searchkey.equals("")){
				if(ret!=-1)
				{
					list.set(ret, map);
				}
				else
				{
					list.add(map); 
				}
			} 
			else{
				if(map.get("bookname").toString().toLowerCase().contains(searchkey.toLowerCase())){
					//					list.add(map); 
					if(ret!=-1)
					{
						list.set(ret, map);
					}
					else
					{
						list.add(map); 
					}
				}
			}
		}

		return "0";
	}
	/**
	 * 从bookmark表获取数据
	 * 
	 */
	public void getDate(){
		//				list.clear();
		String columns[] = new String[]{ 
				Bookmark.BookmarkType,
				Bookmark.ContentId, 
				Bookmark.ChapterId,
				Bookmark.ChapterName,
				Bookmark.ContentName,
				Bookmark.CertPath,
				Bookmark.FilePath,
				Bookmark.FontSize,
				Bookmark.LineSpace,
				Bookmark.CreatedDate,
				Bookmark.Position,
				Bookmark._ID,
				Bookmark.Author,
				Bookmark.SourceType
		};
		Cursor cur = null;
		String where = "";
		if(list.isEmpty())
		{
			where=Bookmark.BookmarkType+"=0";
		}
		else
		{
			where=Bookmark.BookmarkType+"=0 and " + Bookmark.SourceType + "!='4'";

		}
		cur = managedQuery(Bookmark.CONTENT_URI, columns, where, null, order );

		HashMap<String, Object> map = null;
		try{
			if (cur.moveToFirst()){

				do{
					map = new HashMap<String, Object>();
					String id=cur.getString(cur.getColumnIndex(Bookmark._ID));
					map.put("ID", id);
					String type = cur.getString(cur.getColumnIndex(Bookmark.BookmarkType));
					map.put("type", type);
					String author = cur.getString(cur.getColumnIndex(Bookmark.Author));
					if(author==null){
						author="";
					}
					map.put("author", author);
					String chapterid = cur.getString(cur.getColumnIndex(Bookmark.ChapterId));
					if(chapterid==null){
						chapterid="";
					}
					map.put("chapterid", chapterid);
					String chaptername = cur.getString(cur.getColumnIndex(Bookmark.ChapterName));
					if(chaptername==null){
						chaptername="";
					}
					map.put("chaptername", chaptername);
					String contentid = cur.getString(cur.getColumnIndex(Bookmark.ContentId));
					if(contentid==null){
					    contentid="";
                    }
					
					map.put("contentid", contentid);
					String bookname= cur.getString(cur.getColumnIndex(Bookmark.ContentName));
					if(bookname==null){
						bookname="";
					}
					map.put("bookname", bookname);
					String readtime = cur.getString(cur.getColumnIndex(Bookmark.CreatedDate));
					if(readtime==null){
						readtime="";
					}
					map.put("readtime",GlobalVar.TimeFormat("yyyyMMddHHmmss","MM/dd/yyyy HH:mm:ss", readtime));
					String readposition= cur.getString(cur.getColumnIndex(Bookmark.Position));
					if(readposition==null){
						readposition="";
					}
					map.put("readposition", readposition);
					String filepath=cur.getString(cur.getColumnIndex(Bookmark.FilePath));
					if(filepath==null){
						filepath="";
					}
					map.put("filepath", filepath);
					String certpath=cur.getString(cur.getColumnIndex(Bookmark.CertPath));
					if(certpath==null){
						certpath="";
					}
					map.put("certpath", certpath);
					String sourcetype=cur.getString(cur.getColumnIndex(Bookmark.SourceType));
					if(sourcetype==null){
						sourcetype="";
					}
					map.put("sourcetype", sourcetype);
					String FontSize=cur.getString(cur.getColumnIndex(Bookmark.FontSize));
					if(FontSize==null){
						FontSize="";
					}
					map.put("FontSize", FontSize);
					String LineSpace=cur.getString(cur.getColumnIndex(Bookmark.LineSpace));
					if(LineSpace==null){
						LineSpace="";
					}
					map.put("LineSpace", LineSpace);
					int ret=-1;
					if(sourcetype.equals("2"))
					{
						ret = RecentBookActivity.containvalue(list, "contentid", map.get("contentid").toString());
						for(int i=0;i<list.size();i++)
						{
							if(contentid.equals(list.get(i).get("contentid").toString())&&(map.get("readtime").toString().compareTo(list.get(i).get("readtime").toString()))>0)
							{
								list.remove(i);
							}
							else if(contentid.equals(list.get(i).get("contentid").toString()))
							{
								map = null;
							}
							else
							{
								break;
							}
						}
					}

					if(map!=null)
					{
						if(searchkey.equals("")){
							if(ret!=-1)
							{
								list.set(ret, map);
							}
							else
							{
								list.add(map); 
							}
						} 
						else{
							if(bookname.toLowerCase().contains(searchkey.toLowerCase())){
								if(ret!=-1)
								{
									list.set(ret, map);
								}
								else
								{
									list.add(map); 
								}
							}
						}
					}

				}while (cur.moveToNext());

			}
		}catch(Exception e){
			e.printStackTrace();
			Logger.e(TAG, e);
			return;
		}finally{
			if(cur!=null){
				cur.close();
			}
		}
	}

	/**
	 * 为元素赋值
	 */
	private Runnable setdata = new Runnable() {
		@Override
		public void run() {
			getstaticimagesdata();
			setValue();
		}
	};

	public void setValue(){
		if(isshow){
			compPageCounter();
		}

		rows=list.size();

		double j=(double)rows/number;
		count=(int)Math.ceil(j);
		if(count>0){
			if(pageNum>count){
				pageNum=count;
			}
			showPager();
			updatePagerinfo(pageNum+" / " + count);
		}else{
			hidePager();
		}

		if(rows==0){
			if(searchkey.equals("")){
				norecord_layout.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
				tishi.setText(getResources().getString(R.string.no_recent_read_books_tip));
				gotowirelessstore.setText("转入无线书城");
				gotowirelessstore.requestFocus();
				gotowirelessstore.setOnClickListener( new OnClickListener() {
					public void onClick(final View v) {
						showMessage("进入无线书城...","20000");
						//						showTip("进入无线书城...",20000);
						Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
						Bundle bundleToSend = new Bundle();
						bundleToSend.putString("act",
						"com.pvi.ap.reader.activity.WirelessStoreMainpageActivity");
						bundleToSend.putString("haveTitleBar", "1");
						tmpIntent.putExtras(bundleToSend);
						sendBroadcast(tmpIntent);
					}
				});
			}
			else{
				norecord_layout.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);

				if(!isSearch){
					tishi.setText("相关记录已全部删除！");
				}
				else
				{
					tishi.setText(getResources().getString(R.string.nonesearch));
				}

				gotowirelessstore.setText("返回");
				gotowirelessstore.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						showTip("正在获取数据...");
						searchkey="";
						Thread checkUpdate = new Thread() {  
							public void run() {

								getDataFromNetwork();
								getDate();
								SortUtil sorttime = new SortUtil("readtime");
								sorttime.setDescending(false);
								Collections.sort(list, sorttime);
								listHandler.post(setdata);
							}
						};
						checkUpdate.start();
					}
				});

			}
			hideTip();
			if(!isshow)
			{
				showme();
			}
			return;
		}
		norecord_layout.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		datalist.clear();
		for(int i = 0; i < number; i ++)
		{
			coverlogo[i] = null;
		}
		for(int i = 0; i < this.number; i ++)
		{
			PviUiItem[] items = new PviUiItem[]{
					new PviUiItem("pic"+i, 0, 0, 0, 54, 72, null, true, true, null),
					new PviUiItem("bookname"+i, 0, 100, 10, 235, 30, "", false, true, null),
					new PviUiItem("authorname"+i, 0, 100, 55, 220, 30, "", false, true, null),
					new PviUiItem("readtime"+i, 0, 570, 10, 150, 30, "", false, true, null),
					new PviUiItem("chapter"+i, 0, 570, 55, 190, 30, "", false, true, null),
			};
			if((pageNum-1)*number + i < list.size())
			{
				HashMap<String,Object> map = list.get((pageNum-1)*number+i);
				items[0].res=R.drawable.bookcover_5472_ui1;
				if(map.get("sourcetype").toString().equals("1")){
					
					items[1].text = getResources().getString(R.string.source_type_local) + map.get("bookname").toString();
				}else if(map.get("sourcetype").toString().equals("2")){
					items[1].text = getResources().getString(R.string.source_type_download) + map.get("bookname").toString();
				}else if(map.get("sourcetype").toString().equals("3")){
					items[1].text = getResources().getString(R.string.source_type_SD) + map.get("bookname").toString();

				}else if(map.get("sourcetype").toString().equals("4")){
					items[1].text = getResources().getString(R.string.source_type_online) + map.get("bookname").toString();
					//					items[0].pic = NetCache.GetNetImage(Config.getString("CPC_BASE_URL") +map.get("filepath"));
				}else {
					items[1].text = getResources().getString(R.string.source_type_SD) + map.get("bookname").toString();
				}
				items[0].resDefault = R.drawable.bookcover_5472_ui1;
				Bitmap bit = BitmapFactory.decodeResource(this.getResources(), items[0].res);

				if(items[0].pic!=null)
				{
					bit = items[0].pic;
				}
				items[0].top = (this.listView.lineHeight- bit.getHeight())/2;
				items[0].left= 30 + (60-bit.getWidth())/2;
				items[4].text = map.get("chaptername").toString();
				try {
					items[3].text = GlobalVar.TimeFormat("yyyyMMddHHmmss", map.get("readtime").toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					items[3].text = map.get("readtime").toString();
					e.printStackTrace();
				}
				if(map.get("author").toString().equals(""))
				{
					items[2].text = "";
				}
				else
				{
					items[2].text = authorPrefix + map.get("author").toString();
				}

				if(items[0].res!=0)
				{
					items[0].bgNormal = R.drawable.img_border_normal_ui1;
					items[0].bgFocus = R.drawable.img_border_clicked_ui1;
				}
				else
				{
					items[0].bgNormal = 0;
					items[0].bgFocus = 0;
				}
			
				items[1].textSize=22;
				items[2].textSize=19;
				items[3].textSize=16;
				items[4].textSize=19;
				items[3].textAlign=2;
				items[4].textAlign=2;

				this.datalist.add(items);
			}
			listView.setData(datalist);
		}
		hideTip();
		if(!isshow)
		{
			showme();
		}
	}

	PviBottomBar  pbb;  
	public void onCreate(Bundle savedInstanceState) {
		//控制全屏
		setContentView(R.layout.recentbook);
		super.onCreate(savedInstanceState);
		listView= (PviDataList)findViewById(R.id.list);
		listView.lineHeight = 90;
		listView.setOnRowClick(new PviDataList.OnRowClickListener() {

			@Override
			public void OnRowClick(View v, int rowIndex) {
				// TODO Auto-generated method stub
				setEvent(rowIndex);
			}
		});
		datalist = new ArrayList<PviUiItem[]>();

		norecord_layout = (RelativeLayout) this.findViewById(R.id.norecordlayout);
		tishi = (TextView) this.findViewById(R.id.tishi);
		gotowirelessstore = (TextView) this.findViewById(R.id.gotowirlessstore);
	}

	public void setEvent(int i){

		if((pageNum-1)*number+i >= list.size())
		{
			return;
		}
		showMessage("进入书籍阅读...","20000");
		HashMap<String, Object> map1=list.get((pageNum-1)*number+i);
		if(map1.get("sourcetype").toString().equals("4")){
			Intent intent = new Intent(MainpageActivity.START_ACTIVITY);

			Bundle sndBundle = new Bundle();
			sndBundle.putString("act",  "com.pvi.ap.reader.activity.ReadingOnlineActivity");
			sndBundle.putString("haveTitleBar","1");
			sndBundle.putString("Position",  map1.get("readposition").toString());
			sndBundle.putString("ChapterID", map1.get("chapterid").toString());
			sndBundle.putString("ChapterName", map1.get("chaptername").toString());
			sndBundle.putString("ContentID", map1.get("contentid").toString());
			intent.putExtras(sndBundle);
			sendBroadcast(intent);
		}else{
			HashMap<String, Object> data=new HashMap<String,Object>();
			String FilePath = map1.get("filepath").toString();

			if (!new File(FilePath).exists()) {
				this.listHandler.sendEmptyMessage(1);
				return;
			}
			try {
                data.put("FilePath", map1.get("filepath").toString());
                data.put("ChapterID",map1.get("chapterid").toString() );
                data.put("Offset", map1.get("readposition").toString());
                data.put("SourceType", map1.get("sourcetype").toString());
                data.put("FromPath", "1");
                data.put("CertPath", map1.get("certpath").toString());
                data.put("ContentID",map1.get("contentid").toString() );
                data.put("FontSize",map1.get("FontSize").toString() );
                data.put("LineSpace",map1.get("LineSpace").toString() );
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }
			if(new File(map1.get("filepath").toString()).exists()){
				OpenReader.gotoReader(RecentBookActivity.this, data);
			}else{
				Toast.makeText(RecentBookActivity.this,getResources().getString(R.string.openPrompt), Toast.LENGTH_LONG).show();
			}
		}
	}


	private void showme(){
		isshow = true;
		Logger.d("show me ",this.getClass().getName());
		sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
		Intent tmpIntent = new Intent(MainpageActivity.SHOW_ME);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("act", "com.pvi.ap.reader.activity.MyBookshelfActivity");//TabActivity的类名
		bundleToSend.putString("actTabName", "最近阅读");
		bundleToSend.putString("sender", this.getClass().getName()); //TAB内嵌activity类的全名
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
	}

	private int deletebookmark(String bookmarkid)
	{
		//返回 0成功 1,2失败
		//		Logger.d("delete bookmark ",bookmarkid);
		String StrDB = SubscribeProcess.network("deleteBookmark",bookmarkid,null,null,null);
		if (StrDB.substring(0, 10).contains("Exception")) {
			// retry();
			return 1;
		}
		if(StrDB.substring(0, 19).contains("0000")){
			return 0;
		}else{
			return 2;
		} 
	}
	private int deleteallsystembookmark()
	{
		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		HashMap responseMap = null;
		try {
			responseMap = NetCache.deleteAllSystemBookmark(ahmHeaderMap,
					ahmNamePair);
		} catch (HttpException e) {
			e.printStackTrace();
			return 1;
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			return 1;
		}
		String rspC = responseMap.get("result-code").toString();
		if (rspC.contains("result-code: 0")) {
			return 0;
		}
		else
		{
			return 2;
		}
	}

	@Override
	public void OnNextpage() {
		// TODO Auto-generated method stub
		if (1<count&&pageNum<=(count-1)){
			showMessage("数据加载中...","10000");
			pageNum++;
			this.listView.mCurFoucsRow=0;
			listHandler.post(setdata);
		}
		super.OnNextpage();
	}

	@Override
	public void OnPrevpage() {
		// TODO Auto-generated method stub
		if(pageNum>1&&count>=2){
			showMessage("数据加载中...","10000");
			pageNum--;
			this.listView.mCurFoucsRow=0;
			listHandler.post(setdata);
		}
		super.OnPrevpage();
	}

	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {

            closePopmenu();     
            String vTag = item.id; 
            if(vTag.equals("delete")){

                PviAlertDialog pd = new PviAlertDialog(getParent());
                if(list.size()>0){
                    final int delIndex = listView.mCurFoucsRow;
                    PviUiItem[] items = datalist.get(delIndex);
                    if(items[1].text.equals("")){
                        Toast.makeText(RecentBookActivity.this, getResources().getString(R.string.bookDeletePrompt), Toast.LENGTH_LONG).show();
                    }else{

                        pd.setTitle(getResources().getString(R.string.bookDelete));
                        pd.setMessage(getResources().getString(R.string.bookDelete)+list.get((pageNum-1)*number+delIndex).get("bookname").toString()+getResources().getString(R.string.bookRecentbook),Gravity.CENTER);

                        pd.setButton(getResources().getString(R.string.bookConfirm), new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                isSearch = false;
                                boolean pass = true;
                                if(list.get((pageNum-1)*number+delIndex).get("sourcetype").toString().equals("4"))
                                {
                                    if(deletebookmark(list.get((pageNum-1)*number+delIndex).get("ID").toString())!=0)
                                    {
                                        pass=false;
                                        Toast.makeText(getApplicationContext(), "操作失败！", Toast.LENGTH_LONG).show();
                                    }
                                }
                                //                              else
                                //                              {
                                String where=Bookmark._ID+"='"+list.get((pageNum-1)*number+delIndex).get("ID").toString()+"'";

                                getContentResolver().delete(Bookmark.CONTENT_URI,where,null);
                                //                              }
                                if(pass){
                                    list.remove((pageNum-1)*number+delIndex);
                                }
                                listHandler.post(setdata);
                            }});
                        pd.setButton2(getResources().getString(R.string.bookCancel), new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }});
                        pd.show();
                    }

                }


            }else if(vTag.equals("deleteAll")){

                if(list.size()>0){
                    PviAlertDialog pd = new PviAlertDialog(getParent());
                    pd.setTitle(getResources().getString(R.string.bookDelete));
                    pd.setMessage(getResources().getString(R.string.bookDelete)+getResources().getString(R.string.bookRecentbook),Gravity.CENTER);

                    pd.setButton(getResources().getString(R.string.bookConfirm), new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            boolean pass = true;
                            isSearch = false;
                            if(!searchkey.equals(""))
                            {
                                String where = "";
                                for(int i = 0; i < list.size(); i ++)
                                {
                                    if(list.get((pageNum-1)*number+i).get("sourcetype").toString().equals("4"))
                                    {
                                        if(deletebookmark(list.get((pageNum-1)*number+i).get("ID").toString())!=0)
                                        {
                                            pass=pass&false;
                                        }
                                    }
                                    where=where + Bookmark._ID+"='"+list.get((pageNum-1)*number+i).get("ID").toString()+"' or ";
                                }
                                if(where.endsWith(" or "))
                                {
                                    where = where.substring(0, where.lastIndexOf(" or "));
                                }

                                getContentResolver().delete(Bookmark.CONTENT_URI,where,null);
                                if(pass){
                                    list.clear();
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(), "操作失败！", Toast.LENGTH_LONG).show();
                                    getDate();
                                }
                                listHandler.post(setdata);
                            }
                            else
                            {
                                if(deleteallsystembookmark() != 0)
                                {
                                    pass= false;
                                    Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
                                }
                                String where=Bookmark.BookmarkType+"='"+0+"'";
                                getContentResolver().delete(Bookmark.CONTENT_URI,where,null);
                                pageNum=1;
                                if(!pass)
                                {
                                    getDataFromNetwork();
                                    getDate();
                                    SortUtil sorttime = new SortUtil("readtime");
                                    sorttime.setDescending(false);
                                    Collections.sort(list, sorttime);
                                }
                                else
                                {
                                    list.clear();
                                }
                                listHandler.post(setdata);
                            }

                        }});
                    pd.setButton2(getResources().getString(R.string.bookCancel), new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }});
                    pd.show();
                }
            }
            else if (vTag.equals("search")) {
                isSearch = true;
                if(list.size()>0 || !searchkey.equals("")){
                    LayoutInflater inflater=LayoutInflater.from(getParent());
                    final View view=inflater.inflate(R.layout.search, null);
                    final PviAlertDialog pd = new PviAlertDialog(getParent());
                    pd.setView(view);
                    pd.setCanClose(true);
                    pd.setTitle(getResources().getString(R.string.bookSearch));

                    final TextView tv = (TextView)view.findViewById(R.id.hint);
                    final EditText edt = (EditText)view.findViewById(R.id.keyword);
                    Button search = (Button)view.findViewById(R.id.searchbtn);

                    search.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            searchkey = edt.getText().toString();
                            pageNum=1;
                            //                          getDataFromNetwork();
                            //                          getDate();
                            int bound = list.size();
                            for(int i = 0;i < bound;){
                                if(!searchkey.equals("")){
                                    if(list.get(i).get("bookname").toString().contains(searchkey)){
                                        i++;
                                        continue;
                                    }
                                    list.remove(i);
                                    bound--;
                                }
                                else
                                {
                                    break;
                                }
                            }
                            listHandler.post(setdata);
                            pd.dismiss();
                            //                          searchkey="";

                            //}
                        }});
                    edt.setOnFocusChangeListener(new OnFocusChangeListener(){

                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            // TODO Auto-generated method stub
                            if(hasFocus)
                            {
                                tv.setText("");
                            }
                        }

                    });
                    pd.show();
                }
            }else if (vTag.equals("author")) {
                orderType = 3;
                if(list.size()>0){
                    //order=Bookmark.Author.toLowerCase()+" ASC ";
                    pageNum=1;
                    //                  getDate();
                    Collections.sort(list, new SortUtil("author"));
                    listHandler.post(setdata);
                }
            }else if (vTag.equals("bookname")) {
                orderType = 2;
                if(list.size()>0){
                    pageNum=1;
                    Collections.sort(list, new SortUtil("bookname"));
                    listHandler.post(setdata);
                }
            }else if (vTag.equals("time")) {
                orderType = 1;
                if(list.size()>0){
                    pageNum=1;
                    SortUtil sorttime = new SortUtil("readtime");
                    sorttime.setDescending(false);
                    Collections.sort(list, sorttime);
                    listHandler.post(setdata);
                }
            }
        
        }
	};


	@Override
	public OnUiItemClickListener getMenuclick() {
		return this.menuclick;
	}
	private void getstaticimagesdata()
	{
		new Thread()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				HashMap<String, Object> map = null;
				for(int i = 0; i < 7; i ++)
				{
					if((pageNum-1)*number+ i < list.size())
					{
						map = list.get((pageNum-1)*number + i);
						if(map.get("sourcetype").toString().equals("4"))
						{
							coverlogo[i] = NetCache.GetNetImage(Config.getString("CPC_BASE_URL") + map.get("filepath").toString());
						}
					}

				}

				listHandler.post(setcoverlogo);
			}			
		}.start();
	}

	private Runnable setcoverlogo = new Runnable()
	{

		@Override
		public void run() {
			PviUiItem[] items = null;
			for(int i = 0; i < 7; i ++)
			{
				if((pageNum-1)*number+ i < list.size())
				{
					items = datalist.get(i);
					if(coverlogo[i]!=null)
					{
						items[0].pic = coverlogo[i];
						datalist.set(i, items);
					}
				}
			}
			listView.setData(datalist);
		}
	};
}
