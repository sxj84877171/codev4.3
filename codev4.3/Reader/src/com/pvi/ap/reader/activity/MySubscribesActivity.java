package com.pvi.ap.reader.activity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviMenuItem;
import com.pvi.ap.reader.activity.pviappframe.PviPopupWindow;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.common.SortUtil;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.view.View.OnFocusChangeListener;
import android.widget.*;


/**
 * 我的订购
 * @author 刘剑雄
 *
 */
public class MySubscribesActivity extends PviActivity implements Pageable{
	PviDataList listView;
	ArrayList<PviUiItem[]> datalist;
	PviBottomBar  pbb;  
	private Bitmap[] coverlogo = new Bitmap[7];

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
	 * 每页多少记录
	 */
	private int number=7;//每页多少记录

	private int totalNumber = 0;

	private String searchkey = "";

	private Handler listHandler;
	ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, Object>> defaultlist = new ArrayList<HashMap<String, Object>>();
	private int orderType = 1;  
	private boolean isshow=false;
	private boolean hasException = false;
	protected final String TAG = "MySubscribesActivity";
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
		Logger.i("Time","MySubscribesActivity OK" + Long.toString(TimeStart));
		super.onResume();
		isshow=false; 
		showMessage("进入我的订购...");
		orderType = 1;
		hasException = false;
		listHandler = new Handler();
		Thread checkUpdate = new Thread() {  
			public void run() {
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
	}
	/**
	 * 初始化
	 * 元素注册
	 */

	@SuppressWarnings("unchecked")
	public int getDataFromNetwork(String start, String end){

		HashMap<String, Object> map = null;
		String StrGSL = SubscribeProcess.network("getSubscriptionList",start, end,null,null);
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
				totalNumber = Integer.parseInt(totalNl.item(0).getFirstChild().getNodeValue());


				NodeList nl = rootele.getElementsByTagName("ContentInfo");
				for(int i = 0;i < nl.getLength();i++){
					map = new HashMap<String, Object>();
					Element entry = (Element)nl.item(i);
					NodeList nl2 = entry.getElementsByTagName("contentID");
					String tmp = nl2.item(0).getFirstChild().getNodeValue();
					Logger.d("",tmp);
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
					Logger.e("url", map.get("url"));
					NodeList nl5 = entry.getElementsByTagName("chapterName");
					if((nl5 != null)&&(nl5.getLength()>0)&&(nl5.item(0).getFirstChild()!=null))
					{
						map.put("charptername",nl5.item(0).getFirstChild().getNodeValue());
					}
					else
					{
						map.put("charptername","");
					}
					map.put("ordertime"," ");

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
			if(defaultlist.size()<=(pageNum-1)*number)
			{
				return 2;
			}
		}
		defaultlist = (ArrayList<HashMap<String, Object>>) list.clone();
		return 0;
	}

	private void getstaticimagesdata()
	{
		new Thread()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				HashMap map = null;
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
	public void setEvent(int i){
		if((pageNum-1)*number+i < list.size())
		{
			super.showMessage("进入书籍摘要...","20000");
			HashMap<String, Object> map=list.get((pageNum-1)*number+i);
			final String contentID=map.get("contentID").toString();
			Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
			Bundle bundleToSend = new Bundle();
			bundleToSend.putString("act","com.pvi.ap.reader.activity.BookSummaryActivity"); 
			bundleToSend.putString("contentID", contentID);
			bundleToSend.putString("startType",  "allwaysCreate");
			bundleToSend.putString("pviapfStatusTip",  getResources().getString(R.string.booksubscripLoad));
			bundleToSend.putString("pviapfStatusTipTime",  "5000");
			tmpIntent.putExtras(bundleToSend);
			sendBroadcast(tmpIntent);
		}
	}

	private Runnable setdata = new Runnable()
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getstaticimagesdata();
			setValue();
		}

	};
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
		    hidePager();
			pageNum = 1;
			count = 1;
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
				tishi.setText(getResources().getString(R.string.booksubscripnone));
				retbtn.setText("转入无线书城");
				retbtn.requestFocus();
			}else{
				tishi.setText(getResources().getString(R.string.nonesearch));
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
					else if(tishi.getText().toString().equals(getResources().getString(R.string.nonesearch)))
					{
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
		else{
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
							new PviUiItem("pic"+i, R.drawable.bookcover_5472_ui1, 32, 7, 54, 72, null, true, true, null),
							new PviUiItem("bookname"+i, 0, 100, 15, 233, 30, "", false, true, null),
							new PviUiItem("authorname"+i, 0, 100, 50, 230, 30, "", false, true, null),
							new PviUiItem("chapter"+i, 0, 570, 35, 235, 30, "", false, true, null),
					};
					HashMap<String,Object> map=list.get((pageNum-1)*number+i);
					
					items[0].bgNormal = R.drawable.img_border_normal_ui1;
					items[0].bgFocus = R.drawable.img_border_clicked_ui1;
					
					items[1].text = map.get("name").toString();
					items[2].text = RecentBookActivity.authorPrefix + map.get("author").toString();
					items[3].text = map.get("charptername").toString();
					items[1].textSize=22;
					items[2].textSize=19;
					items[3].textSize=19;
					items[3].textAlign=2;
					if(datalist!=null && i < datalist.size())
					{
						this.datalist.set(i, items);
					}
					else
					{
						this.datalist.add(i, items);
					}
				}
			}
			listView.setData(datalist);
		}

		if(!isshow)
		{
			showme();
		}
		super.hideTip();
	}

	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		long TimeStart = System.currentTimeMillis();
		Logger.i("Time","MySubscribesActivity start" + Long.toString(TimeStart));

		setContentView(R.layout.mysubscribes);

		super.onCreate(savedInstanceState);
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
	@Override
	public void OnNextpage() {
		// TODO Auto-generated method stub
		if (1<count&&pageNum<=(count-1)){
			showMessage("数据加载中...","10000");
			pageNum++;
			this.listView.mCurFoucsRow=0;

			Thread thread = new Thread(getdata);
			thread.start();
		}
	}
	@Override
	public void OnPrevpage() {
		// TODO Auto-generated method stub
		if(pageNum>1&&count>=2){
			showMessage("数据加载中...","10000");
			this.listView.mCurFoucsRow=0;
			pageNum--;
			Thread thread = new Thread(getdata);
			thread.start();

		}
	}

	Runnable search = new Runnable()
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
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
					if(!searchkey.equals(""))
					{
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
					listHandler.post(setdata);
					pd.dismiss();
					searchkey="";
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
	};
	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {

            closePopmenu();
            String vTag = item.id; 
            //if(list.size()>0){
            if (vTag.equals("search")) {
                Thread thread = new Thread()
                {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        super.run();
                        if(defaultlist.size() < totalNumber)
                        {
                            if(getDataFromNetwork(String.valueOf(list.size()+1), String.valueOf(totalNumber-list.size()))!=0)
                            {
                                hasException = true;
                                listHandler.post(setdata);
                                return;
                            }
                        }
                        else
                        {
                            list = (ArrayList<HashMap<String, Object>>) defaultlist.clone();
                        }
                        if(list.size()>0 || !searchkey.equals("")){
                            listHandler.post(search);
                        }
                    }

                };
                thread.start();
                //  }
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
                        Collections.sort(list, new SortUtil("author"));
                        pageNum=1;
                        listHandler.post(setdata);
                    }
                }

            }else if (vTag.equals("bookname")) {
                if(orderType!=2)
                {
                    orderType = 2;
                    if(list.size()>0){
                        if(defaultlist.size() < totalNumber)
                        {
                            if(getDataFromNetwork(String.valueOf(list.size()+1), String.valueOf(totalNumber-list.size()))!=0)
                            {
                                hasException = true;
                            }
                        }
                        //                  order=SubScribe.Name+" ASC ";
                        Collections.sort(list, new SortUtil("name"));
                        pageNum=1;
                        listHandler.post(setdata);
                    }
                }

            }else if (vTag.equals("time")) {
                if(orderType!=1)
                {

                    orderType = 1;
                    if(list.size()>0){
                        ArrayList<HashMap<String, Object>> templist = (ArrayList<HashMap<String, Object>>) list.clone();
                        if(defaultlist.size() < totalNumber)
                        {
                            if(getDataFromNetwork(String.valueOf(list.size()+1), String.valueOf(totalNumber-list.size()))!=0)
                            {
                                hasException = true;
                            }
                        }
                        list.clear();
                        for(int j = 0; j<defaultlist.size();j++)
                        {

                            if(templist.contains(defaultlist.get(j)))
                            {
                                list.add(defaultlist.get(j));
                            }
                        }


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
			isshow=true;
			Logger.d("show me ",this.getClass().getName());
			sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
			Intent tmpIntent = new Intent(MainpageActivity.SHOW_ME);
			Bundle bundleToSend = new Bundle();
			bundleToSend.putString("act", "com.pvi.ap.reader.activity.MyBookshelfActivity");//TabActivity的类名
			bundleToSend.putString("actTabName", "我的订购");
			bundleToSend.putString("sender", this.getClass().getName()); //TAB内嵌activity类的全名
			tmpIntent.putExtras(bundleToSend);
			sendBroadcast(tmpIntent);
		}

}
