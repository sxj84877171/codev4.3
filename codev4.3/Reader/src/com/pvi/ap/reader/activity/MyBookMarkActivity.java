package com.pvi.ap.reader.activity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import com.pvi.ap.reader.activity.pviappframe.PviPopupWindow;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.*;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviMenuItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;

import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.common.SortUtil;
import com.pvi.ap.reader.data.content.Bookmark;
/**
 * 用户书签
 * @author 刘剑雄
 *
 */
public class MyBookMarkActivity extends PviActivity implements Pageable {
	PviDataList listView;
	ArrayList<PviUiItem[]> datalist;
	PviBottomBar  pbb;  

	private boolean selected[] = new boolean[7];
	private TextView tvSelectAll = null;
	private TextView tvView = null;
	private TextView tvDelete = null;
	private RelativeLayout norecord_layout = null;
	private TextView tishi=null;
	private TextView retbtn = null;
	/*
	 * 页码
	 */
	private int pageNum=1;//页码
	/*
	 * 总页
	 */
	private int count=1;//总页
	/*
	 * 数据行数
	 */
	private int rows=0;//数据行数
	/*
	 * 每页多少记录
	 */
	private int number=7;//每页多少记录
	private String searchkey = "";
	private int orderType = 1;  
	private boolean isSearch = false;
	private boolean isshow = false;
	private String order=Bookmark.CreatedDate+" DESC ";
	protected final String TAG = "MyBookmarkActivity";
	private Handler listHandler;
	ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();	
	HashMap<String, Object> data=new HashMap<String,Object>();

	private String SourceType = null;
	private String FilePath = null;
	/*
	 * 翻页计数器
	 */
	private int pageCounter = 0;//翻页计数器
	private void compPageCounter() {
		if (GlobalVar.deviceType == 1) {
			pageCounter++;
			if (pageCounter == 3) {
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
	protected void onResume() {
		// TODO Auto-generated method stub
		long TimeStart = System.currentTimeMillis();
		Logger.i("Time","MyBookMarkActivity" + Long.toString(TimeStart));
		super.onResume();
		super.showMessage("进入我的书签...","20000");

		tvSelectAll.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				selectAll();
			}
		});  
		tvView.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				//				tvView.setEnabled(false);
				for(int i = 0;i < selected.length;i++){
					if(selected[i]){
						showMessage("进入书籍阅读...", "20000");
						setEvent(i);
						return;
					}
				}

			}
		});  
		tvDelete.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {

				boolean hadselected = false;
				for(int tmpi = 0; tmpi < selected.length; tmpi ++){
					if(selected[tmpi]){
						hadselected = true;
						Logger.i("Selected Index:", "Selected Index:"+tmpi);
					}
				}
				if(!hadselected){
					Logger.i("Selected Index:", "No Selected Index");
					return;
				}

				final PviAlertDialog pd = new PviAlertDialog(getParent());
				pd.setTitle(getResources().getString(R.string.bookDelete));
				pd.setMessage("确定要删除书签吗？",Gravity.CENTER);
				pd.setButton(DialogInterface.BUTTON_POSITIVE,getResources().getString(R.string.bookConfirm), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						isSearch = false;
						showMessage("正在删除...");
						Thread deletebookmark = new Thread()
						{

							@Override
							public void run() {
								// TODO Auto-generated method stub
								for(int i = 0;i < selected.length;i ++){
									if(selected[i]){
										if(!list.get(pagei(i)).get("sourcetype").toString().equals("4"))
										{
											String where=Bookmark._ID+"='"+list.get(pagei(i)).get("id").toString()+"'";				
											getContentResolver().delete(Bookmark.CONTENT_URI,where,null);
										}
										else
										{
											try {
												String id = list.get(pagei(i)).get("bookmarkID").toString();
												if(deleteNetworkBookmark(id) != 0)
												{
													Toast.makeText(getBaseContext(), "删除"+list.get(pagei(i)).get("bookname").toString()+"失败！", Toast.LENGTH_LONG).show();
												}
											} catch (Exception e) {
												e.printStackTrace();
											}
										}

									}

								}
								if(SourceType==null)
								{
									list.clear();
									getFromDB();
									getDataFromNetwork();
								}
								else if(SourceType.equals("4"))
								{
									list.clear();
									getDataFromNetwork();
								}
								else
								{
									list.clear();
									getFromDB();
								}
								SortUtil sorttime = new SortUtil("readtime");
								sorttime.setDescending(false);
								Collections.sort(list, sorttime);
								listHandler.post(setdata);
								super.run();
							}

						};
						deletebookmark.start();


					}});
				pd.setButton2(getResources().getString(R.string.bookCancel), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}});
				pd.show();
			}
		});  

		orderType = 1;
		isshow = false;
		listHandler = new Handler()
		{

			@Override
			public void dispatchMessage(Message msg) {
				// TODO Auto-generated method stub
				super.dispatchMessage(msg);
				switch(msg.what)
				{
				case 0:
					clearChoose();
					setValue();
					break;
				}
			}

		};

		Thread checkUpdate = new Thread() {  
			public void run() {

				if(SourceType==null)
				{
					list.clear();
					getFromDB();
					getDataFromNetwork();
				}
				else if(SourceType.equals("4"))
				{
					list.clear();
					getDataFromNetwork();
				}
				else
				{
					list.clear();
					getFromDB();
				}

				SortUtil sorttime = new SortUtil("readtime");
				sorttime.setDescending(false);
				Collections.sort(list, sorttime);
				listHandler.post(setdata);
			}
		};
		checkUpdate.start();

	}
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		searchkey="";
		this.isSearch=false;
	}
	/**
	 * 初始化
	 */
	public void init(){

		tvSelectAll = (TextView)findViewById(R.id.selectAllButton);
		tvView = (TextView)findViewById(R.id.viewButton);
		tvDelete = (TextView)findViewById(R.id.deleteButton);
		norecord_layout = (RelativeLayout) this.findViewById(R.id.norecordlayout);
		this.tishi=(TextView)findViewById(R.id.tishi);
		this.retbtn = (TextView)this.findViewById(R.id.gotowirlessstore);

		this.setOnPmShow(new OnPmShowListener(){

			@Override
			public void OnPmShow(PviPopupWindow popmenu) {               
				//设置排序子菜单的焦点与orderType一致                
				if(orderType==1){
					final PviMenuItem vSortByTime = getMenuItem("time");
					if(vSortByTime!=null){
						vSortByTime.isFocuse=true;
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
	}	

	/**
	 * 从bookmark表获取数据
	 * 
	 */

	public void getFromDB(){
		//		list.clear();
		for(int i = 0;i < selected.length;i++){
			selected[i] = false;
		}

		String columns[] = new String[]{ 
				Bookmark.BookmarkType,
				Bookmark.ContentId, 
				Bookmark.ChapterId,
				Bookmark.ChapterName,
				Bookmark.ContentName,
				Bookmark.CertPath,
				Bookmark.FilePath,
				Bookmark.CreatedDate,
				Bookmark.Position,
				Bookmark.LineSpace,
				Bookmark._ID,
				Bookmark.FontSize,
				Bookmark.Author,
				Bookmark.SourceType
		};
		Cursor cur = null;
		String where = "";
		if(this.SourceType!=null&&this.FilePath!=null&&!this.FilePath.equals(""))
		{
			where=Bookmark.BookmarkType+"='"+1+"' and " + Bookmark.SourceType + "=" + "'"+SourceType+"' and " + Bookmark.FilePath + "='"+this.FilePath+"'";
		}
		else
		{
			where=Bookmark.BookmarkType+"='"+1+"' and " + Bookmark.SourceType + "!=" + "'4'";
		}
		cur = managedQuery(Bookmark.CONTENT_URI, columns, where, null, order );
		HashMap<String, Object> map = new HashMap<String, Object> ();
		if (cur.moveToFirst()){
			do{
				map = new HashMap<String, Object>();
				String id = cur.getString(cur.getColumnIndex(Bookmark._ID));
				map.put("id", id);

				String type = cur.getString(cur.getColumnIndex(Bookmark.BookmarkType));
				map.put("type", type);
				String author = cur.getString(cur.getColumnIndex(Bookmark.Author));
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

				map.put("contentid", contentid);
				String bookname= cur.getString(cur.getColumnIndex(Bookmark.ContentName));
				map.put("bookname", bookname);
				String readtime = cur.getString(cur.getColumnIndex(Bookmark.CreatedDate));
				if(readtime==null){
					readtime="";
				}
				try {
					map.put("readtime", GlobalVar.TimeFormat("yyyyMMddHHmmss","MM/dd/yyyy HH:mm:ss", readtime));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String readposition= cur.getString(cur.getColumnIndex(Bookmark.Position));
				map.put("readposition", readposition);
				String filepath=cur.getString(cur.getColumnIndex(Bookmark.FilePath));
				map.put("filepath", filepath);
				String certpath=cur.getString(cur.getColumnIndex(Bookmark.CertPath));
				map.put("certpath", certpath);
				String sourcetype=cur.getString(cur.getColumnIndex(Bookmark.SourceType));
				map.put("sourcetype", sourcetype);
				String FontSize=cur.getString(cur.getColumnIndex(Bookmark.FontSize));
				map.put("FontSize", FontSize);
				String LineSpace=cur.getString(cur.getColumnIndex(Bookmark.LineSpace));
				map.put("LineSpace", LineSpace);
				if(searchkey.equals("")){
					list.add(map); 
				} 
				else{
					if(bookname.toLowerCase().contains(searchkey.toLowerCase())){
						list.add(map); 
					}
				}
			}
			while (cur.moveToNext());
		}
		if(cur!=null)
		{
			cur.close();
		}
	}

	public int getDataFromNetwork(){

		HashMap<String, Object> map = null;
		String StrGSL = SubscribeProcess.network("getUserBookmark","1","500",null,null);
		if (StrGSL.substring(0, 10).contains("Exception")) {
			return 1;
		}

		try {
			if(StrGSL.substring(0, 19).contains("0000")){
				InputStream is = new ByteArrayInputStream(StrGSL.substring(20).getBytes());	
				Element rootele = null;
				DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance(); 
				DocumentBuilder db = dbfactory.newDocumentBuilder();	
				Document dom = db.parse(is);
				rootele = dom.getDocumentElement();
				NodeList nl = rootele.getElementsByTagName("ContentInfo");
				for(int i = 0;i < nl.getLength();i++){
					String contentid;
					String bookname;
					String author;

					Element entry = (Element)nl.item(i);
					NodeList nl2 = entry.getElementsByTagName("contentID");
					String tmp = nl2.item(0).getFirstChild().getNodeValue();
					Logger.d("Reader",tmp);
					contentid = tmp;

					NodeList nl3 = entry.getElementsByTagName("contentName");
					bookname = nl3.item(0).getFirstChild().getNodeValue();

					NodeList nl4 = entry.getElementsByTagName("authorName");
					author = nl4.item(0).getFirstChild().getNodeValue();

					NodeList nl5 = entry.getElementsByTagName("Bookmark");
					for(int j = 0;j < nl5.getLength();j++){
						Element itementry = (Element)nl5.item(j);
						map = new HashMap<String, Object>();
						NodeList n21 = itementry.getElementsByTagName("bookmarkID");
						map.put("bookmarkID", n21.item(0).getFirstChild().getNodeValue());
						Logger.d("bookmark ID",n21.item(0).getFirstChild().getNodeValue());
						NodeList n22 = itementry.getElementsByTagName("chapterID");
						map.put("chapterid", n22.item(0).getFirstChild().getNodeValue());
						Logger.d("chapterid at network",n22.item(0).getFirstChild().getNodeValue());

						NodeList n23 = itementry.getElementsByTagName("position");
						map.put("readposition", n23.item(0).getFirstChild().getNodeValue());

						NodeList n24 = itementry.getElementsByTagName("chapterName");
						try {
							map.put("chaptername", n24.item(0).getFirstChild().getNodeValue());
						} catch (Exception e) {
							map.put("chaptername"," ");
							e.printStackTrace();
						}

						NodeList n25 = itementry.getElementsByTagName("createTime");
						map.put("readtime",  n25.item(0).getFirstChild().getNodeValue());
						map.put("type", "1");
						map.put("bookname",bookname);
						map.put("contentid",contentid);
						map.put("author",author);
						map.put("FontSize","15");
						map.put("sourcetype", "4");
						map.put("filepath", "");
						map.put("certpath", "");
						if(this.SourceType!=null&&SourceType.equals("4")&&this.FilePath!=null&&!this.FilePath.equals(""))
						{
							if(contentid.equals(this.FilePath)){
								list.add(map); 
							} 
						}
						else
						{
							if(searchkey.equals("")){
								list.add(map); 
							} 
							else{
								if(bookname.toLowerCase().contains(searchkey.toLowerCase())){
									list.add(map); 
								}
							}
						}
					
						Logger.d("my book mark count",""+list.size());
					}
				}//end of content item loop
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 2;
		}

		return 0;

	}

	/**
	 * 为元素赋值
	 */
	private Runnable setdata = new Runnable()
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			clearChoose();
			setValue();
		}

	};
	public void setValue(){
		if(isshow)
		{
			compPageCounter();
		}
		tvView.setEnabled(true);
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
			this.tvDelete.setVisibility(View.GONE);
			this.tvSelectAll.setVisibility(View.GONE);
			this.tvView.setVisibility(View.GONE);
			norecord_layout.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			if(searchkey.equals("")){

				tishi.setText(getResources().getString(R.string.bookmarknone));
				retbtn.setText("转入无线书城");
				retbtn.requestFocus();
			}else{
				if(!isSearch){
					tishi.setText("相关记录已全部删除！");
				}
				else
				{
					tishi.setText(getResources().getString(R.string.nonesearch));
				}
				retbtn.setText("返回");
				retbtn.requestFocus();
			}
			this.retbtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
//					retbtn.setEnabled(false);
					if(retbtn.getText().toString().equals("转入无线书城")){
						Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
						Bundle bundleToSend = new Bundle();
						bundleToSend.putString("act",
						"com.pvi.ap.reader.activity.WirelessStoreMainpageActivity");
						bundleToSend.putString("haveTitleBar", "1");
						tmpIntent.putExtras(bundleToSend);
						sendBroadcast(tmpIntent);
					}
					else
					{
						searchkey="";
						showTip("正在获取数据");
						Thread checkUpdate = new Thread() {  
							public void run() {
								list.clear();
								if(SourceType==null)
								{
									getFromDB();
									getDataFromNetwork();
								}
								else if(SourceType.equals("4"))
								{
									getDataFromNetwork();
								}
								else
								{
									getFromDB();
								}
								SortUtil sorttime = new SortUtil("readtime");
								sorttime.setDescending(false);
								Collections.sort(list, sorttime);
								listHandler.post(setdata);
							}
						};
						checkUpdate.start();
					}
				}
			});
			hideTip();
			if(!isshow)
			{
				showme();
			}
			return;
		}

		this.tvDelete.setVisibility(View.VISIBLE);
		this.tvSelectAll.setVisibility(View.VISIBLE);
		this.tvView.setVisibility(View.VISIBLE);
		norecord_layout.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		PviUiItem[] items = null;
		for(int i = 0; i < this.number; i ++)
		{
			items = null;
			if(datalist!=null && i < datalist.size())
			{
				items = datalist.get(i);
			}
			if((pageNum-1)*number + i < list.size())
			{
				if(items==null)
				{
					items = new PviUiItem[]{
							new PviUiItem("pic"+i, R.drawable.bookmark_bg, 31, 0, 60, 80, null, true, true, null),
							new PviUiItem("bookname"+i, 0, 100, 15, 233, 30, "", false, true, null),
							new PviUiItem("authorname"+i, 0, 100, 50, 200, 30, "", false, true, null),
							new PviUiItem("time"+i, 0, 550, 15, 150, 30, "", false, true, null),
							new PviUiItem("chapter"+i, 0, 550, 50, 160, 30, "", false, true, null),
							new PviUiItem("pic"+i, R.drawable.notcheck, 554, 29, 26, 25, null, true, true, null),
					};
				}
				HashMap<String,Object> map=list.get((pageNum-1)*number + i);

				if(map.get("sourcetype").toString().equals("1")){
					items[1].text = getResources().getString(R.string.source_type_local) + map.get("bookname").toString();
				}else if(map.get("sourcetype").toString().equals("2")){
					items[1].text = getResources().getString(R.string.source_type_download) + map.get("bookname").toString();
				}else if(map.get("sourcetype").toString().equals("3")){
					items[1].text = getResources().getString(R.string.source_type_SD) + map.get("bookname").toString();
				}else if(map.get("sourcetype").toString().equals("4")){
					items[1].text = getResources().getString(R.string.source_type_online) + map.get("bookname").toString();
				}else {
					items[1].text = getResources().getString(R.string.source_type_SD) + map.get("bookname").toString();
				}
				items[4].text = map.get("chaptername").toString();
				try {
					items[3].text = GlobalVar.TimeFormat("yyyyMMddHHmmss", map.get("readtime").toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					items[3].text = map.get("readtime").toString();
				}
				if(map.get("author")==null || ("".equals(map.get("author"))))
				{
					items[2].text = "";
				}
				else
				{
					items[2].text = RecentBookActivity.authorPrefix + map.get("author").toString();
				}

				items[1].textSize=22;
				items[2].textSize=19;
				items[3].textSize=16;
				items[4].textSize=19;
				items[3].textAlign=2;
				items[4].textAlign=2;
				if(datalist!=null && i < datalist.size())
				{
					this.datalist.set(i, items);
				}
				else
				{
					this.datalist.add(i, items);
				}

			}

			listView.setData(datalist);
		}
		hideTip();
		if(!isshow)
		{
			showme();
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		setContentView(R.layout.mybookmark);
		super.onCreate(savedInstanceState);
		Intent intent = this.getIntent();
		try
		{
			Bundle bundle = intent.getExtras();
			if(bundle.containsKey("SourceType"))
			{
				this.SourceType = bundle.getString("SourceType");
			}
			if(bundle.containsKey("FilePath"))
			{
				this.FilePath = bundle.getString("FilePath");
			}
		}
		catch (Exception e)
		{
			this.SourceType = null;
			this.FilePath = null;
		}

		listView= (PviDataList)findViewById(R.id.list);
		datalist = new ArrayList<PviUiItem[]>();
		final GlobalVar app = ((GlobalVar) getApplicationContext());    
		pbb = app.pbb;

		this.showPager = true;
		init();
		listView.setOnRowClick(itemclick);
	}

	
	PviDataList.OnRowClickListener itemclick= new PviDataList.OnRowClickListener() {

		@Override
		public void OnRowClick(View v, int rowIndex) {
			// TODO Auto-generated method stub
			PviUiItem[] items = null;
			items = datalist.get(rowIndex);
			Logger.e(TAG, "listView.mCurFoucsRow: " + rowIndex);
			if(items[5].res == R.drawable.check)
			{
				items[5].res = R.drawable.notcheck;
				selected[rowIndex] = false;
			}
			else
			{
				items[5].res = R.drawable.check;
				selected[rowIndex] = true;
			}
			datalist.set(rowIndex, items);
			listView.setData(datalist);
		}
	};

	public void setEvent(int i){
		super.showTip("进入阅读页...", 20000);
		HashMap<String, Object> map1=list.get((pageNum-1)*number+i);
		if(map1.get("sourcetype").toString().equals("4")){
			Intent intent = new Intent(MainpageActivity.START_ACTIVITY);

			Bundle sndBundle = new Bundle();
			sndBundle.putString("act",  "com.pvi.ap.reader.activity.ReadingOnlineActivity");
			sndBundle.putString("haveTitleBar","1");
			sndBundle.putString("startType",  "allwaysCreate");    //每次都create一个新实例，不设置此参数时，默认为“复用”已有的
			sndBundle.putString("Position",  map1.get("readposition").toString());
			sndBundle.putString("ChapterID", map1.get("chapterid").toString());
			sndBundle.putString("ChapterName", map1.get("chaptername").toString());
			sndBundle.putString("ContentID", map1.get("contentid").toString());
			sndBundle.putString("FontSize", map1.get("FontSize").toString());
			intent.putExtras(sndBundle);
			sendBroadcast(intent);
		}else{
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
				OpenReader.gotoReader(MyBookMarkActivity.this, data);
			}else{

				Toast.makeText(MyBookMarkActivity.this,getResources().getString(R.string.openPrompt), Toast.LENGTH_LONG).show();

			}
		}
	}

	//检验输入字符
	public boolean  checkInput(String s){
		boolean flag=false;

		for(int i=0;i<s.length();i++){
			s.charAt(i);
		}
		return flag;
	}

	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {

            closePopmenu();
            String vTag = item.id; 
            if(vTag.equals("delete")){
                boolean hadselected = false;
                for(int tmpi = 0; tmpi < selected.length; tmpi ++){
                    if(selected[tmpi]){
                        hadselected = true;
                    }
                }
                if(!hadselected){
                    return;
                }

                tvDelete.performClick();

            }else if(vTag.equals("deleteAll")){
                if(list.size()>0){
                    PviAlertDialog pd = new PviAlertDialog(getParent());
                    pd.setTitle(getResources().getString(R.string.bookDelete));
                    pd.setMessage("");
                    pd.setMessage(getResources().getString(R.string.bookDelete)+"所有书签",Gravity.CENTER);

                    pd.setButton(getResources().getString(R.string.bookConfirm), new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                            isSearch = false;

                            if(searchkey.equals(""))
                            {
                                deleteAllNetworkBookmark();

                                String where=Bookmark.BookmarkType+"='"+1+"'";
                                getContentResolver().delete(Bookmark.CONTENT_URI,where,null);
                                pageNum=1;
                                list.clear();
                            }
                            else
                            {
                                boolean pass=true;
                                for(int i = 0;i < list.size();i ++){
                                    if(list.get(pagei(i)).get("sourcetype").toString().equals("4"))
                                    {
                                        try {
                                            String id = list.get(pagei(i)).get("bookmarkID").toString();
                                            if(deleteNetworkBookmark(id) != 0)
                                            {
                                                pass = pass&false;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    //                                  else
                                    //                                  {
                                    String where=Bookmark._ID+"='"+list.get(pagei(i)).get("id").toString()+"'";             
                                    getContentResolver().delete(Bookmark.CONTENT_URI,where,null);
                                    //                                  }
                                }
                                if(pass)
                                {
                                    list.clear();
                                }
                                else
                                {
                                    if(SourceType==null)
                                    {
                                        list.clear();
                                        getFromDB();
                                        getDataFromNetwork();
                                    }
                                    else if(SourceType.equals("4"))
                                    {
                                        list.clear();
                                        getDataFromNetwork();
                                    }
                                    else
                                    {
                                        list.clear();
                                        getFromDB();
                                    }
                                    SortUtil sorttime = new SortUtil("readtime");
                                    sorttime.setDescending(false);
                                    Collections.sort(list, sorttime);
                                }

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
                            pageNum=1;
                            //getDataFromNetwork();
                            pd.dismiss();
                            listHandler.post(setdata);
                            //                          searchkey="";
                        }
                    });
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
                    //order=Bookmark.Author+" ASC ";
                    pageNum=1;
                    //getDataFromNetwork();
                    Collections.sort(list, new SortUtil("author"));
                    listHandler.post(setdata);
                }
            }else if (vTag.equals("bookname")) {
                orderType = 2;
                if(list.size()>0){
                    //order=Bookmark.ContentName+" ASC ";
                    pageNum=1;
                    //getDataFromNetwork();
                    Collections.sort(list, new SortUtil("bookname"));
                    listHandler.post(setdata);
                }
            }else if (vTag.equals("time")) {
                orderType = 1;
                if(list.size()>0){
                    order=Bookmark.CreatedDate+" DESC ";
                    SortUtil sort =  new SortUtil("readtime");
                    sort.setDescending(false);
                    Collections.sort(list,sort);
                    pageNum=1;
                    //                  onResume();
                    listHandler.post(setdata);
                }
            }
        
        }};



		public OnUiItemClickListener getMenuclick() {
			return this.menuclick;
		}
		private int deleteNetworkBookmark(String bookmarkId){
			//返回 0成功 1,2失败
			Logger.d("delete bookmark ",bookmarkId);
			String StrDB = SubscribeProcess.network("deleteBookmark",bookmarkId,null,null,null);
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
		private int deleteAllNetworkBookmark(){
			//返回 0成功 1,2失败
			Logger.d("delete all bookmark "," ");
			String StrDB = SubscribeProcess.network("deleteAllUserBookmark",null,null,null,null);
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
		private void selectAll(){
			Logger.e(TAG, "come to select all");	
			int itemNum = 0;
			boolean isselected = true;
			if(pageNum == count){
				if(rows==number){
					itemNum = number;
				}
				else
				{
					itemNum = rows%number;
				}
			}else{
				itemNum = number;
			}
			for(int i = 0; i < itemNum;i ++){
				isselected = isselected&&selected[i];
			}
			if(isselected){
				for(int i = 0; i < itemNum;i ++){
					PviUiItem[] items = null;
					items = datalist.get(i);
					items[5].res = R.drawable.notcheck;
					datalist.set(i, items);
					selected[i] = false;
				} 
			}else{
				for(int i = 0; i < itemNum;i ++){
					PviUiItem[] items = null;
					items = datalist.get(i);

					items[5].res = R.drawable.check;
					datalist.set(i, items);
					selected[i] = true;
				}
			}
			listView.setData(datalist);
		}

		private void clearChoose(){
			this.datalist.clear();
			int itemNum = 0;
			if(pageNum == count){
				itemNum = rows%number;
			}else{
				itemNum = number;
			}
			for(int i = 0; i < itemNum;i ++){
				selected[i] = false;
			} 
			this.listView.setData(datalist);
		}
		private void showme(){
			isshow = true;
			Logger.d("show me ",this.getClass().getName());
			sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
			Intent tmpIntent = new Intent(MainpageActivity.SHOW_ME);
			Bundle bundleToSend = new Bundle();
			bundleToSend.putString("act", "com.pvi.ap.reader.activity.MyBookshelfActivity");//TabActivity的类名
			bundleToSend.putString("actTabName", "我的书签");
			bundleToSend.putString("sender", this.getClass().getName()); //TAB内嵌activity类的全名
			tmpIntent.putExtras(bundleToSend);
			sendBroadcast(tmpIntent);
		}

		private int pagei(int i){
			return number*(pageNum - 1) + i;
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
		}
		@Override
		public void OnPrevpage() {
			// TODO Auto-generated method stub
			if(pageNum>1&&count>=2){
				showMessage("数据加载中...","10000");
				pageNum--;
				listView.mCurFoucsRow = 0;
				listHandler.post(setdata);
			} 
		}
}
