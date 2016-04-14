package com.pvi.ap.reader.activity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.pvi.ap.reader.activity.pviappframe.PviPopupWindow;
import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviMenuItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.EPDRefresh;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;
import com.pvi.ap.reader.data.common.SortUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.*;


/**
 * 我的收藏
 * @author 田青红
 * 
 */
public class MyFavoriteActivity extends PviActivity implements Pageable{

	PviDataList listView;
	ArrayList<PviUiItem[]> datalist;
	PviBottomBar  pbb;  

	private int number=7;//每页多少记录
	private Bitmap[] coverlogo = new Bitmap[7];
	private RelativeLayout norecord_layout = null;
	private TextView tishi=null;
	private TextView retbtn = null;

	private int pageNum=1;//页码
	private int count=1;//总页

	private int delIndex = 0;
	private Handler listHandler;

	private boolean isSearch=false;
	private String searchkey = "";
	ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, Object>> defaultlist = new ArrayList<HashMap<String, Object>>();

	//	private String order=Favorites.FavoriteTime+" DESC ";
	private int totalNumber = 0;
	private int orderType = 1;

	private boolean hasException = false;
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
	protected void onResume() {
		// TODO Auto-generated method stub
		long TimeStart = System.currentTimeMillis();
		Logger.i("Time","MyFavoriteActivity" + Long.toString(TimeStart));

		super.onResume();
		showMessage("进入我的收藏...");
		EPDRefresh.refreshGCOnceFlash();
		orderType = 1;
		isshow = false;
		hasException = false;
		Thread checkUpdate = new Thread() {  
			public void run() {
				//				if(list.size()<=0)
				//				{
				if(getDataFromNetwork(String.valueOf(number*(pageNum-1) + 1),String.valueOf(number))!=0)
				{
					hasException = true;
				}
				listHandler.post(setdata);
			}
		};
		checkUpdate.start();

	}
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		searchkey="";
		isSearch = false;
	}

	/**
	 * 从Favorites表获取数据
	 * 
	 */
	//	public void getDataFromDB(){
	//		list.clear();
	//		String columns[] = new String[]{ 
	//				Favorites._ID,
	//				Favorites.UserID, 
	//				Favorites.ContentId,
	//				Favorites.ContentName,
	//				Favorites.Author,
	//				Favorites.FavoriteTime,
	//				Favorites.FavoriteURL,
	//				Favorites.ChapterId,
	//				Favorites.ChapterName
	//		};
	//		Cursor cur = null;
	//
	//		cur = managedQuery(Favorites.CONTENT_URI, columns, null, null, order );
	//
	//		HashMap<String, Object> map = null;
	//		try{
	//			if (cur.moveToFirst()){
	//
	//				do{
	//					map = new HashMap<String, Object>();
	//					String id=cur.getString(cur.getColumnIndex(Favorites._ID));
	//					map.put("ID", id);
	//					String userID = cur.getString(cur.getColumnIndex(Favorites.UserID));
	//					map.put("userID", userID);
	//					String contentID = cur.getString(cur.getColumnIndex(Favorites.ContentId));
	//					if(contentID==null){
	//						contentID="";
	//					}
	//					map.put("contentID", contentID);
	//					String name = cur.getString(cur.getColumnIndex(Favorites.ContentName));
	//
	//					map.put("name", name);
	//					String author = cur.getString(cur.getColumnIndex(Favorites.Author));
	//
	//					map.put("author", author);
	//					String favoritetime= cur.getString(cur.getColumnIndex(Favorites.FavoriteTime));
	//					map.put("favoritetime", favoritetime);
	//					String url = cur.getString(cur.getColumnIndex(Favorites.FavoriteURL));
	//
	//					map.put("url", url);
	//					String chapterID= cur.getString(cur.getColumnIndex(Favorites.ChapterId));
	//					if(chapterID==null){
	//						chapterID="";
	//					}
	//					map.put("chapterID", chapterID);
	//					String charptername = cur.getString(cur.getColumnIndex(Favorites.ChapterName));
	//					if(charptername==null){
	//						charptername="";
	//					}
	//					map.put("charptername", charptername);
	//
	//
	//					if(searchkey.equals("")){
	//						list.add(map); 
	//					} 
	//					else{
	//
	//						if(name.toLowerCase().contains(searchkey.toLowerCase())){
	//							list.add(map); 
	//						}
	//
	//					}
	//				}while (cur.moveToNext());
	//
	//			}
	//		}catch(Exception e){
	//			return;
	//		}finally{
	//			if(cur!=null){
	//				cur.close();
	//			}
	//		}
	//	}

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
						coverlogo[i] = NetCache.GetNetImage(Config.getString("CPC_BASE_URL") + map.get("url").toString());
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

	public int getDataFromNetwork(String start, String count){
		HashMap<String, Object> map = null;
		String StrGSL = SubscribeProcess.network("getFavorite",start,count,null,null);
		int num = Integer.parseInt(start);
		if (StrGSL.substring(0, 10).contains("Exception")) {
			// retry();
			if(num > defaultlist.size())
			{
				return 1;
			}
		}

		num--;
		try {
			if(StrGSL.substring(0, 19).contains("0000")){
				InputStream is = new ByteArrayInputStream(StrGSL.substring(20).getBytes());	
				Element rootele = null;
				DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance(); 
				DocumentBuilder db = dbfactory.newDocumentBuilder();	
				Document dom = db.parse(is);
				rootele = dom.getDocumentElement();
				NodeList totalNl = rootele.getElementsByTagName("totalRecordCount");
				totalNumber  = Integer.parseInt(totalNl.item(0).getFirstChild().getNodeValue());
				NodeList nl = rootele.getElementsByTagName("ContentInfo");
				for(int i = 0;i < nl.getLength();i++){
					map = new HashMap<String, Object>();
					Element entry = (Element)nl.item(i);
					NodeList nl2 = entry.getElementsByTagName("contentID");
					String tmp = nl2.item(0).getFirstChild().getNodeValue();
					//					Logger.d("",tmp);
					map.put("contentID",tmp);

					NodeList nl3 = entry.getElementsByTagName("contentName");
					map.put("name", nl3.item(0).getFirstChild().getNodeValue());

					NodeList nl4 = entry.getElementsByTagName("authorName");
					map.put("author", nl4.item(0).getFirstChild().getNodeValue());

					try {
						NodeList nl5 = entry.getElementsByTagName("smallLogo");
						map.put("url", nl5.item(0).getFirstChild().getNodeValue());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						map.put("url","null");
					}

					NodeList nl5 = entry.getElementsByTagName("addDate");

					map.put("favoritetime", nl5.item(0).getFirstChild().getNodeValue());

					nl5 = entry.getElementsByTagName("chapterName");
					if((nl5 != null)&&(nl5.getLength()>0)&&(nl5.item(0).getFirstChild()!=null))
					{
						map.put("charptername",nl5.item(0).getFirstChild().getNodeValue());
					}
					else
					{
						map.put("charptername","");
					}

					map.put("chapterID"," ");

					int ret = RecentBookActivity.containvalue(list, "contentID", map.get("contentID").toString());
					if(ret!=-1)
					{
						list.set(num, map);
					}
					else
					{
						list.add(num,map); 
					}
					num++;
				}//end of content item loop
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.e("Reader", e.toString());
			if(defaultlist.size()<=(pageNum-1)*number)
			{
				return 2;
			}
		}
		defaultlist = (ArrayList<HashMap<String, Object>>) list.clone();
		return 0;
	}

	public void setEvent(int i){
		super.showMessage("进入书籍摘要","20000");
		HashMap<String, Object> map=list.get((pageNum-1)*number+i);
		final String contentID=map.get("contentID").toString();
		Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("act","com.pvi.ap.reader.activity.BookSummaryActivity"); 
		bundleToSend.putString("contentID", contentID);
		bundleToSend.putString("startType",  "allwaysCreate");
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
	}
	public void setValue(){
		if(isshow)
		{
			compPageCounter();
		}


		double j=(double)totalNumber/number;
		if(!searchkey.equals(""))
		{
			j=(double)list.size()/number;
		}
		count=(int)Math.ceil(j);
		if(count>0){
			if(pageNum>count){
				pageNum=count;
			}
			showPager();
			updatePagerinfo(pageNum+" / " + count);
		}else{
			pageNum = 1;
			count = 1;
			hidePager();
		}

		if((pageNum-1)*number>=list.size()){
			listView.setVisibility(View.GONE);
			norecord_layout.setVisibility(View.VISIBLE);
			if(hasException)
			{
				tishi.setText("联网获取数据失败，点击OK键返回！"); 
				retbtn.setText("返回");
				retbtn.requestFocus();
			}
			else if(searchkey.equals("")){
				tishi.setText(getResources().getString(R.string.bookfavoritenone)); 
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
					else if(tishi.getText().toString().equals("相关记录已全部删除！"))
					{
						//						norecord_layout.setVisibility(View.INVISIBLE);
						searchkey="";
						Thread checkUpdate = new Thread() {  
							public void run() {
								list = (ArrayList<HashMap<String, Object>>) defaultlist.clone();
								listHandler.post(setdata);
							}
						};
						checkUpdate.start();
					}
					else
					{
						Intent intent = new Intent(WirelessTabActivity.BACK);
						Bundle bundle = new Bundle();
						bundle.putString("startType", "allwaysCreate");
						intent.putExtras(bundle);
						sendBroadcast(intent);
					}
				}
			});
		}
		else
		{
			listView.setVisibility(View.VISIBLE);
			norecord_layout.setVisibility(View.GONE);
			PviUiItem[] items = null;
			datalist.clear();
			for(int i = 0; i < this.number; i ++)
			{
				items = null;
				if((pageNum-1)*number + i < list.size())
				{
					items = new PviUiItem[]{
							new PviUiItem("pic"+i, R.drawable.bookcover_5472_ui1, 30, 7, 54, 72, null, true, true, null),
							new PviUiItem("bookname"+i, 0, 100, 10, 235, 30, "", false, true, null),
							new PviUiItem("authorname"+i, 0, 100, 55, 220, 30, "", false, true, null),
							new PviUiItem("readtime"+i, 0, 570, 10, 150, 30, "", false, true, null),
							new PviUiItem("chapter"+i, 0, 570, 55, 190, 30, "", false, true, null),
					};
					HashMap<String,Object> map=new HashMap<String,Object>();

					map=list.get((pageNum-1)*number+i);
					items[0].bgNormal = R.drawable.img_border_normal_ui1;
					items[0].bgFocus = R.drawable.img_border_clicked_ui1;
					items[1].text = map.get("name").toString();
					items[4].text = map.get("charptername").toString();
					items[2].text = RecentBookActivity.authorPrefix + map.get("author").toString();
					try {
						items[3].text = GlobalVar.TimeFormat("yyyy-MM-dd HH:mm:ss",map.get("favoritetime").toString());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						items[3].text = map.get("favoritetime").toString();
					}
					
					if(datalist!=null && i < datalist.size())
					{
						this.datalist.set(i, items);
					}
					else
					{
						this.datalist.add(i, items);
					}
					items[4].textAlign=2;
					items[3].textAlign=2;
					items[2].textSize=19;
					items[3].textSize=16;
					items[4].textSize=19;
				}
			}
			listView.setData(datalist);
		}
		if(!isshow)
		{
			showme();
		}
	}
	private Runnable setdata = new Runnable() {
		@Override
		public void run() {
			try {
				setValue();
				getstaticimagesdata();
			}
			catch (Exception e) {
				Logger.e("Reader", e.toString());
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//控制全屏
		long TimeStart = System.currentTimeMillis();
		Logger.i("Time","MyFavoriteActivity Starting" + Long.toString(TimeStart));

		setContentView(R.layout.myfavorite);

		super.onCreate(savedInstanceState);
		list.clear();
		listHandler = new Handler();
		listView= (PviDataList)findViewById(R.id.list);
		listView.lineHeight = 90;
		datalist = new ArrayList<PviUiItem[]>();
		final GlobalVar app = ((GlobalVar) getApplicationContext());    
		pbb = app.pbb;
		this.showPager = true;
		norecord_layout = (RelativeLayout) this.findViewById(R.id.norecordlayout);
		tishi = (TextView) this.findViewById(R.id.tishi);
		retbtn = (TextView) this.findViewById(R.id.gotowirlessstore);
		listView.setOnRowClick(new PviDataList.OnRowClickListener()
		{
			@Override
			public void OnRowClick(View v, int rowIndex) {
				// TODO Auto-generated method stub
				setEvent(rowIndex);
			}
		});
		this.setOnPmShow(new OnPmShowListener(){

			@Override
			public void OnPmShow(PviPopupWindow popmenu) {               
				//设置排序子菜单的焦点与orderType一致                
				if(orderType==1){
					final PviMenuItem vSortByTime = getMenuItem("time");
					if(vSortByTime!=null){
						vSortByTime.isFocuse = true;
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




	public boolean deleteFav(){
		boolean flag=false;
		try{
			HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
			HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

			ahmNamePair.put("contentId", list.get((pageNum-1)*number+delIndex).get("contentID").toString());
			HashMap responseMap = null;

			try {
				responseMap = CPManager.deleteFavorite(ahmHeaderMap, ahmNamePair);
				//Logger.v(tag, msg)("map="+responseMap.get("result-code").toString());
				flag=true;
			}catch (HttpException e) {
				//连接异常 ,一般原因为 URL错误
				Logger.e("MyFavoriteActivity http", e.toString());
				flag=false;


			} catch (SocketTimeoutException e) {
				//连接异常 ,超时
				Logger.e("MyFavoriteActivity socker", e.toString());
				flag=false;

			} catch (IOException e) {
				//IO异常 ,一般原因为网络问题
				flag=false;

			}
		}catch(Exception e){
			Logger.e("MyFavoriteActivity Exception", e.toString());
			flag=false;
		}
		return flag;
	}
	public boolean deleteAllFav(){
		boolean flag=false;

		try{
			HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
			HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
			HashMap responseMap = null;
			try {
				responseMap = CPManager.deleteAllFavorite(ahmHeaderMap, ahmNamePair);
				flag=true;
			}catch (HttpException e) {
				//连接异常 ,一般原因为 URL错误
				Logger.e("MyFavoriteActivity http", e.toString());
				flag=false;
			} catch (SocketTimeoutException e) {
				//连接异常 ,超时
				Logger.e("MyFavoriteActivity socker", e.toString());
				flag=false;

			} catch (IOException e) {
				//IO异常 ,一般原因为网络问题
				flag=false;

			}
		}catch(Exception e){
			Logger.e("MyFavoriteActivity Exception", e.toString());
			flag=false;
		}
		return flag;
	}

	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {

            closePopmenu();
            delIndex = listView.mCurFoucsRow;
            PviUiItem[] items = null;
            items = datalist.get(delIndex);
            
            String vTag = item.id; 
            if(vTag.equals("delete")){

                if(list.size()>0){
                    if(items[1].text.equals("")){
                        Toast.makeText(MyFavoriteActivity.this, getResources().getString(R.string.bookDeletePrompt), Toast.LENGTH_LONG).show();
                    }
                    else{
                        PviAlertDialog pd = new PviAlertDialog(getParent());
                        pd.setTitle(getResources().getString(R.string.bookDelete));
                        pd.setMessage(getResources().getString(R.string.bookDelete)+list.get((pageNum-1)*number+delIndex).get("name").toString(),Gravity.CENTER);

                        pd.setButton(getResources().getString(R.string.bookConfirm), new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                isSearch = false;
                                if(deleteFav()){    
                                    totalNumber--;
                                    defaultlist.remove(list.get((pageNum-1)*number+delIndex));
                                    list.remove((pageNum-1)*number+delIndex);
                                    getstaticimagesdata();
                                    setValue();
                                }else{
                                    Toast.makeText(MyFavoriteActivity.this, "操作失败", Toast.LENGTH_LONG).show();  
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


            }if(vTag.equals("deleteAll")){

                if(list.size()>0){

                    PviAlertDialog pd = new PviAlertDialog(getParent());
                    pd.setTitle(getResources().getString(R.string.bookDelete));
                    pd.setMessage(getResources().getString(R.string.bookDelete)+"全部收藏书籍",Gravity.CENTER);

                    pd.setButton(getResources().getString(R.string.bookConfirm), new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            isSearch = false;
                            if(searchkey.equals(""))
                            {
                                if(deleteAllFav()){
                                    pageNum=1;
                                    totalNumber = 0;
                                    defaultlist.clear();
                                    list.clear();
                                    setValue();
                                }else{
                                    Toast.makeText(MyFavoriteActivity.this, "操作失败", Toast.LENGTH_LONG).show();  
                                }
                            }
                            else
                            {
                                boolean pass = true;
                                for(int i=0; i<list.size(); i++)
                                {
                                    if(deleteFav()){    
                                        totalNumber--;
                                        list.remove((pageNum-1)*number+delIndex);
                                    }
                                    else
                                    {
                                        pass = false;
                                    }
                                }
                                if(!pass)
                                {
                                    Toast.makeText(MyFavoriteActivity.this, "操作失败", Toast.LENGTH_LONG).show();  
                                    defaultlist.clear();
                                    list.clear();
                                    if(getDataFromNetwork("1", String.valueOf(totalNumber))!=0)
                                    {
                                        hasException = true;
                                    }
                                }
                                else
                                {
                                    totalNumber=0;
                                    defaultlist.clear();
                                    list.clear();
                                }
                                getstaticimagesdata();
                                setValue();
                            }
                        }});
                    pd.setButton2(getResources().getString(R.string.bookCancel), new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }});
                    pd.show();
                }
            }else if (vTag.equals("search")) {
                if(defaultlist.size() < totalNumber)
                {
                    if(getDataFromNetwork(String.valueOf(list.size()+1), String.valueOf(totalNumber-list.size()))!=0)
                    {
                        hasException = true;
                    }
                }
                isSearch = true;
                if(list.size()>0 || !searchkey.equals("")){
                    list = (ArrayList<HashMap<String, Object>>) defaultlist.clone();
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
                            if(!searchkey.equals("")){
                                for(int i = 0;i < bound;){
                                    if(list.get(i).get("name").toString().contains(searchkey)){
                                        i++;
                                        continue;
                                    }
                                    list.remove(i);
                                    bound--;
                                }
                            }
                            else
                            {
                                list = (ArrayList<HashMap<String, Object>>) defaultlist.clone();
                            }
                            pageNum=1;
                            getstaticimagesdata();
                            setValue();
                            pd.dismiss();
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
                if(orderType!=3)
                {
                    orderType = 3;
                    if(list.size()>0){
                        if(defaultlist.size() < totalNumber)
                        {
                            if(getDataFromNetwork(String.valueOf(list.size()+1), String.valueOf(totalNumber-list.size()))!=0)
                            {
                                hasException = true;
                            }
                        }
                        //                  order=Favorites.Author+" ASC ";
                        Collections.sort(list, new SortUtil("author"));
                        pageNum=1;
                        getstaticimagesdata();
                        setValue();
                    }
                }

            }else if (vTag.equals("bookname")) {
                if(orderType!=2)
                {
                    orderType=2;
                    if(list.size()>0){
                        //                  order=Favorites.ContentName+" ASC ";
                        if(defaultlist.size() < totalNumber)
                        {
                            if(getDataFromNetwork(String.valueOf(list.size()+1), String.valueOf(totalNumber-list.size()))!=0)
                            {
                                hasException = true;
                            }

                        }
                        Collections.sort(list, new SortUtil("name"));
                        pageNum=1;
                        listHandler.post(setdata);
                    }
                }

            }else if (vTag.equals("time")) {

                if(orderType!=1)
                {
                    orderType=1;
                    if(list.size()>0){
                        if(defaultlist.size() < totalNumber)
                        {
                            if(getDataFromNetwork(String.valueOf(list.size()+1), String.valueOf(totalNumber-list.size()))!=0)
                            {
                                hasException = true;
                            }
                        }
                        SortUtil sort = new SortUtil("favoritetime");
                        sort.setDescending(false);
                        Collections.sort(list, sort);
                        pageNum=1;
                        listHandler.post(setdata);
                    }
                }

            }

        
        }};


		@Override
		public OnUiItemClickListener getMenuclick() {
			return this.menuclick;
		}

	private void showme(){
		isshow = true;
		sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
		Intent tmpIntent = new Intent(MainpageActivity.SHOW_ME);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("act", "com.pvi.ap.reader.activity.MyBookshelfActivity");//TabActivity的类名
		bundleToSend.putString("actTabName", "我的收藏");
		bundleToSend.putString("sender", this.getClass().getName()); //TAB内嵌activity类的全名
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
	}
	@Override
	public void OnNextpage() {
		// TODO Auto-generated method stub
		if (pageNum == count) {
			return;
		}
		showMessage("数据加载中...","10000");
		pageNum++;
		this.listView.mCurFoucsRow=0;
		Thread thread = new Thread(getdata);
		thread.start();
		super.OnNextpage();
	}
	@Override
	public void OnPrevpage() {
		// TODO Auto-generated method stub
		if (pageNum == 1) {
			return;
		}
		showMessage("数据加载中...","10000");
		pageNum--;
		this.listView.mCurFoucsRow=0;
		Thread thread = new Thread(getdata);
		thread.start();
		super.OnPrevpage();
	}
	Runnable getdata = new Runnable()
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(searchkey.equals("")){ 
				if(defaultlist.size() < totalNumber)
				{
					String start = String.valueOf((pageNum-1)*number +1);
					String count = String.valueOf(number);
					if(getDataFromNetwork(start, count)!=0)
					{
						hasException = true;
					}
				}
				listHandler.post(setdata);
			}else{
				listHandler.post(setdata);
			}	
		}
	};
}
