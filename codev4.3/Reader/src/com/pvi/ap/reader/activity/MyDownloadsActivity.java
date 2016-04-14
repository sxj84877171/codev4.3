package com.pvi.ap.reader.activity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.common.SortUtil;
import com.pvi.ap.reader.data.content.BookInfo;
import com.pvi.ap.reader.service.FileDownloadManage;
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.pvi.ap.reader.activity.pviappframe.PviPopupWindow;

/**
 * 我的下载
 * @author 刘剑雄
 *
 */
public class MyDownloadsActivity extends PviActivity implements Pageable{

	PviDataList listView;
	ArrayList<PviUiItem[]> datalist;
	PviBottomBar  pbb;  
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

	private boolean isSearch = false;
	private int delIndex = 0;
	private String searchkey = "";
	String order=BookInfo.DownloadTime+ " DESC ";
	ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	HashMap<String, Object> data=new HashMap<String,Object>();

	private ArrayList<String> chapterurllist = new ArrayList<String> ();
	private ArrayList<String> chapteridlist = new ArrayList<String> ();

	private BroadcastReceiver dataReceiver=null;

	private Handler listHandler;
	private int orderType = 1;  
	/*
	 * 更新进度
	 */
	protected final String TAG = "MyDownloadsActivity";
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
	/*
	 * 更新进度
	 */
	public void updatePre(){
		if (dataReceiver == null) {
			dataReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action
                            .equals("com.pvi.ap.reader.FileDownloadService.data.update")) {
                        String data = intent.getExtras().getString("data");
                        String contentID = intent.getExtras().getString(
                                "contentID");
                        HashMap<String, Object> map = new HashMap<String, Object>();

                        for (int i = 0; i < list.size(); i++) {
                            map = list.get(i);
                            if (contentID.equals(map.get("contentid")
                                    .toString())) {
                                PviUiItem[] items = null;

                                if (datalist.size() > i) {

                                    items = datalist.get(i);

                                    if (Integer.parseInt(data) > 0) {
                                        if (Integer.parseInt(data) > Integer
                                                .parseInt(map.get("processper")
                                                        .toString())) {
                                            list.get(i).put("processper", data);

                                            if ((i >= (pageNum - 1) * number)
                                                    && (i < pageNum * number)
                                                    && map.get("downStat")
                                                            .toString().equals(
                                                                    "0")) {
                                                items[4].text = getResources()
                                                        .getString(
                                                                R.string.bookdownloadsjindu)
                                                        + data + "%";
                                                datalist.set(i, items);

                                            }

                                            if (data.equals("100")) {
                                                if ((i >= (pageNum - 1)
                                                        * number)
                                                        && (i < pageNum
                                                                * number)
                                                        && map.get("downStat")
                                                                .toString()
                                                                .equals("0")) {
                                                    items[3].text = getResources()
                                                            .getString(
                                                                    R.string.bookdownloadsfish);
                                                    datalist.set(i, items);
                                                }
                                                list.get(i)
                                                        .put("downStat", "2");
                                            }
                                            // Logger.e("data=", data);
                                            listView.setData(datalist);
                                        }
                                    } else {
                                        if ((i >= (pageNum - 1) * number)
                                                && (i < pageNum * number)
                                                && map.get("downStat")
                                                        .toString().equals("0")) {
                                            items[3].text = getResources()
                                                    .getString(
                                                            R.string.bookdownloadsfail);
                                            datalist.set(i, items);
                                        }
                                        list.get(i).put("downStat", "3");
                                        listView.setData(datalist);
                                    }

                                }
                            }
                        }
                    }
                }
			};
			IntentFilter iFilter = new IntentFilter();
			iFilter.addAction("com.pvi.ap.reader.FileDownloadService.data.update");
			registerReceiver(dataReceiver, iFilter);
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		this.setOnPmShow(new OnPmShowListener(){

			@Override
			public void OnPmShow(PviPopupWindow popmenu) {               
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
		long TimeStart = System.currentTimeMillis();
		Logger.i("Time","MyDownloadsActivity ok" + Long.toString(TimeStart));

		super.onResume();
		showMessage("进入我的下载...","20000");
		orderType = 1;
		listHandler = new Handler();
		Thread checkUpdate = new Thread() {  
			public void run() {
				getDate();
				listHandler.post(setdata);
			}
		};
		checkUpdate.start();
	}
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		if(dataReceiver!=null){
			unregisterReceiver(dataReceiver); 
			dataReceiver = null ;
		}
		isSearch = false;
		searchkey="";

	}


	public void getDate(){
		list.clear();
		String columns[] = new String[]{BookInfo._ID, 
				BookInfo.ContentID,
				BookInfo.Name, 
				BookInfo.Catelog, 
				BookInfo.BookType, 
				BookInfo.PathType,
				BookInfo.ProcessPer,
				BookInfo.DownloadTime,
				BookInfo.Author,
				BookInfo.Maker,
				BookInfo.SaleTime,
				BookInfo.CertPath,
				BookInfo.BookPosition,
				BookInfo.BookSize,
				BookInfo.ChapterID,
				BookInfo.DownloadStatus,
				BookInfo.URL,
				BookInfo.DownloadType
		};
		Cursor cur = null;
		String where =BookInfo.PathType+"='"+1+"'";
		HashMap<String, Object> map = null;
		cur = managedQuery(BookInfo.CONTENT_URI, columns, where, null, order);
		String precontentid = "";
		try{
			if (cur.moveToFirst()){
				do{
					map = new HashMap<String, Object>();
					String id = cur.getString(cur.getColumnIndex(BookInfo._ID));
					map.put("ID", id);
					String  contentid=cur.getString(cur.getColumnIndex(BookInfo.ContentID));
					if(contentid.equals(precontentid))
					{
						precontentid = contentid;
						continue;
					}
					map.put("contentid", contentid);
					String  name= cur.getString(cur.getColumnIndex(BookInfo.Name));
					map.put("name", name);

					String catelog = cur.getString(cur.getColumnIndex(BookInfo.Catelog));
					map.put("catelog", catelog);
					String  bookType = cur.getString(cur.getColumnIndex(BookInfo.BookType));
					map.put("bookType", bookType);
					String pathType = cur.getString(cur.getColumnIndex(BookInfo.PathType));
					map.put("pathType", pathType);
					String processper = cur.getString(cur.getColumnIndex(BookInfo.ProcessPer));
					map.put("processper", processper);
					String downloadTime = cur.getString(cur.getColumnIndex(BookInfo.DownloadTime));
					map.put("downloadTime", downloadTime); 
					String author = cur.getString(cur.getColumnIndex(BookInfo.Author));
					map.put("author", author); 
					String  booksize=cur.getString(cur.getColumnIndex(BookInfo.BookSize));
					if(booksize==null){
						booksize="";
					}
					map.put("booksize", booksize);
					String bookposition = cur.getString(cur.getColumnIndex(BookInfo.BookPosition));
					map.put("bookposition", bookposition);
					String certpath=cur.getString(cur.getColumnIndex(BookInfo.CertPath));
					map.put("certpath", certpath);
					String maker= cur.getString(cur.getColumnIndex(BookInfo.Maker));
					map.put("maker", maker);
					String saleTime= cur.getString(cur.getColumnIndex(BookInfo.SaleTime));
					map.put("saleTime", saleTime);
					String url=cur.getString(cur.getColumnIndex(BookInfo.URL));
					map.put("url", url);
					String downStat=cur.getString(cur.getColumnIndex(BookInfo.DownloadStatus));
					map.put("downStat", downStat);

					String chapterid=cur.getString(cur.getColumnIndex(BookInfo.ChapterID));
					map.put("chapterid", chapterid);

					String downloadType=cur.getString(cur.getColumnIndex(BookInfo.DownloadType));
					map.put("downloadType", downloadType);


					if(searchkey.equals("")){
						list.add(map); 
					} 
					else{
						if(name.toLowerCase().contains(searchkey.toLowerCase())){
							list.add(map); 
						}
					}
					precontentid = contentid;
				}
				while (cur.moveToNext());
			}
		}catch(Exception e){
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
			try {
				setValue();
					
			}
			catch (Exception e) {
				Logger.e("Reader", e.toString());
			}
		}
	};
	public void setValue(){
		if(isShown())
		{
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
			norecord_layout.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			if(searchkey.equals("")){

				tishi.setText(getResources().getString(R.string.bookdownloadsnone));
				retbtn.setText("转入无线书城");

			}else{

				if(isSearch)
				{
					tishi.setText("相关记录已全部删除！");
				}
				else
				{
					tishi.setText(getResources().getString(R.string.nonesearch));
				}

				retbtn.setText("返回");
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
						showTip("正在获取数据...");
						searchkey="";
						Thread checkUpdate = new Thread() {  
							public void run() {
								getDate();
								listHandler.post(setdata);
							}
						};
						checkUpdate.start();
					}
				}
			});
			hideTip();
			showme();
			return;
		}
		datalist.clear();
		for(int i = 0; i < this.number; i ++)
		{
			PviUiItem[] items = new PviUiItem[]{
					new PviUiItem("pic"+i, R.drawable.bookcover_5472_ui1, 32, 7, 60, 80, null, true, true, null),
					new PviUiItem("bookname"+i, 0, 100, 10, 235, 30, "", false, true, null),
					new PviUiItem("authorname"+i, 0, 100, 55, 170, 30, "", false, true, null),
					new PviUiItem("status"+i, 0, 570, 10, 70, 30, "", false, true, null),
					new PviUiItem("percent"+i, 0, 570, 55, 150, 30, "", false, true, null),
					new PviUiItem("size"+i, 0, 280, 55, 130, 30, "", false, true, null),
			};
			if((pageNum-1)*number + i < list.size())
			{
				HashMap<String,Object> map=new HashMap<String,Object>();

				map=list.get((pageNum-1)*number+i);

				if(map.get("downStat").toString().equals("0")){
					items[3].text=getResources().getString(R.string.bookdownloadsdoing);
				}
				if(map.get("downStat").toString().equals("1")){
					items[3].text=getResources().getString(R.string.bookdownloadspause);
				}
				if(map.get("downStat").toString().equals("2")){
					items[3].text=getResources().getString(R.string.bookdownloadsfish);
				}
				if(map.get("downStat").toString().equals("3")){
					items[3].text=getResources().getString(R.string.bookdownloadsfail);
				}
				items[1].text=map.get("name").toString();
				items[4].text=getResources().getString(R.string.bookdownloadsjindu)+map.get("processper").toString()+"%";
				if(map.get("booksize").toString()==null||map.get("booksize").toString().equals("")){
					items[5].text="";
				}else{
					try{	

						double tempsize = 0.0;
						DecimalFormat df = new DecimalFormat("#.##");
						tempsize=Double.parseDouble(map.get("booksize").toString());
						if(tempsize > 0)
						{
							if(tempsize<1024*1024 ){
								items[5].text=getResources().getString(R.string.bookdownloadssize)+df.format(tempsize/1024).toString()+"KB";
							}else{
								items[5].text=getResources().getString(R.string.bookdownloadssize)+df.format(tempsize/1024/1024).toString()+"MB";
							}
						}


					}catch(Exception e){
						Logger.e("MyDownloadsActivity", e.toString());
					}
				}
				items[2].text = RecentBookActivity.authorPrefix + map.get("author").toString();
				items[0].bgNormal = R.drawable.img_border_normal_ui1;
				items[0].bgFocus = R.drawable.img_border_clicked_ui1;
				items[1].textSize=22;
				items[2].textSize=19;
				items[3].textSize=16;
				items[4].textSize=19;
				items[3].textAlign=2;
				items[4].textAlign=2;
				items[5].textSize=19;
				this.datalist.add(items);
			}
			listView.setData(datalist);
			
			updatePre();
		}
		hideTip();
		showme();

	}
	//	GlobalVar appState=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//控制全屏
		long TimeStart = System.currentTimeMillis();
		Logger.i("Time","MyDownloadsActivity start" + Long.toString(TimeStart));

		setContentView(R.layout.mydownloads);

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
		final GlobalVar app = ((GlobalVar) getApplicationContext());    
		pbb = app.pbb;
		this.showPager = true;
		norecord_layout = (RelativeLayout) this.findViewById(R.id.norecordlayout);
		tishi = (TextView) this.findViewById(R.id.tishi);
		this.retbtn = (TextView)this.findViewById(R.id.retbtn);
	}
	public void setEvent(int i){
		HashMap<String,Object> map=list.get((pageNum-1)*number+i);
		if(map.get("processper").toString().equals("100")){
			super.showMessage("进入书籍阅读...");
			data.put("FilePath", map.get("bookposition").toString());
			data.put("ChapterID", "");
			data.put("Offset","");

			data.put("FromPath", "0");
			data.put("CertPath",  map.get("certpath").toString());
			data.put("ContentID", map.get("contentid").toString());
			data.put("SourceType", "2");
			data.put("authorName", map.get("author").toString());
			if(map.get("bookType")!=null){
				data.put("bookType", map.get("bookType").toString());
			}
			if(map.get("downloadType")!=null){
				//Logger.d("mydoc","downloadType:"+map.get("downloadType").toString());
				data.put("downloadType", map.get("downloadType").toString());
			}else{
				//Logger.d("mydoc","downloadType is empty");
			}

			if(map.get("bookposition").toString()!=null){

				if(new File(map.get("bookposition").toString()).isFile()&&new File(map.get("bookposition").toString()).length()>0){

					OpenReader.gotoReader(MyDownloadsActivity.this, data);
				}else{
					Toast.makeText(MyDownloadsActivity.this, getResources().getString(R.string.openPrompt), Toast.LENGTH_LONG).show();
				}
			}
		} 

	}

	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {

            closePopmenu(); 


            String vTag = item.id; 
            if(vTag.equals("delete")){

                if(list.size()>0){
                    delIndex = listView.mCurFoucsRow;
                    PviUiItem[] items = null;
                    items = datalist.get(delIndex);
                    
                    if(items[1].text.equals("")){
                        Toast.makeText(MyDownloadsActivity.this, getResources().getString(R.string.bookDeletePrompt), Toast.LENGTH_LONG).show();

                    }else{
                        final PviAlertDialog pd = new PviAlertDialog(getParent());
                        pd.setTitle(getResources().getString(R.string.bookDelete));
                        pd.setMessage(getResources().getString(R.string.bookDelete)+list.get((pageNum-1)*number+delIndex).get("name").toString(),Gravity.CENTER);
                        pd.setButton(DialogInterface.BUTTON_POSITIVE,getResources().getString(R.string.bookConfirm), new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                isSearch = false;
                                FileDownloadManage fdm = new FileDownloadManage(MyDownloadsActivity.this);
                                Bundle task = new Bundle();
                                task.putString("contentID", list.get((pageNum-1)*number+delIndex).get("contentid").toString());
                                task.putString("Path", list.get((pageNum-1)*number+delIndex).get("bookposition").toString());
                                task.putString("CertPath", list.get((pageNum-1)*number+delIndex).get("certpath").toString());
                                fdm.deleteDownloadTask(task);
                                getDate();
                                setValue();

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

                    
                    final PviAlertDialog pd = new PviAlertDialog(getParent());
                    pd.setTitle(getResources().getString(R.string.bookDelete));
                    pd.setMessage(getResources().getString(R.string.bookDelete)+"所有下载",Gravity.CENTER);
                    pd.setButton(DialogInterface.BUTTON_POSITIVE,getResources().getString(R.string.bookConfirm), new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isSearch = false;
                            if(searchkey.equals(""))
                            {
                                FileDownloadManage fdm = new FileDownloadManage(MyDownloadsActivity.this);
                                fdm.deleteAllDownloadTask();
                                pageNum=1;

                            }
                            else
                            {
                                for(int i = 0; i < list.size(); i ++)
                                {
                                    FileDownloadManage fdm = new FileDownloadManage(MyDownloadsActivity.this);
                                    Bundle task = new Bundle();
                                    task.putString("contentID", list.get((pageNum-1)*number+i).get("contentid").toString());
                                    task.putString("Path", list.get((pageNum-1)*number+i).get("bookposition").toString());
                                    task.putString("CertPath", list.get((pageNum-1)*number+i).get("certpath").toString());
                                    fdm.deleteDownloadTask(task);
                                }
                            }
                            getDate();
                            setValue();
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
                            getDate();
                            setValue();
                            pd.dismiss();
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
                if(list.size()>0){
                    orderType = 3;
                    //order=BookInfo.Author+" ASC ";
                    pageNum=1;
                    getDate();
                    Collections.sort(list, new SortUtil("author"));
                    setValue();
                }
            }else if (vTag.equals("bookname")) {
                if(list.size()>0){
                    //order=BookInfo.Name+" ASC ";
                    pageNum=1;
                    orderType = 2;
                    getDate();
                    Collections.sort(list, new SortUtil("name"));
                    setValue();
                }
            }else if (vTag.equals("time")) {
                orderType = 1;
                if(list.size()>0){
                    order=BookInfo.DownloadTime+" DESC ";
                    Collections.sort(list, new SortUtil("downloadTime"));
                    pageNum=1;
                    pageNum=1;
                    getDate();
                    setValue();
                }
            }else if(vTag.equals("xin")){

                if(list.size()>0){
                    delIndex = listView.mCurFoucsRow;
                    PviUiItem[] items = null;
                    items = datalist.get(delIndex);
                    
                    if(items[1].text.equals("")){
                        Toast.makeText(MyDownloadsActivity.this, getResources().getString(R.string.bookDeletePrompt), Toast.LENGTH_LONG).show(); 

                    }else{

                        if(list.get((pageNum-1)*number+delIndex).get("chapterid").toString().equals(""))
                        {
                            FileDownloadManage mFileDownloadManage = new FileDownloadManage(MyDownloadsActivity.this);
                            final Bundle certExtras = new Bundle();
                            final GlobalVar gv=(GlobalVar)getApplicationContext();

                            certExtras.putString("Version",Config.getString("SoftwareVersion"));
                            certExtras.putString("CID",list.get((pageNum-1)*number+delIndex).get("contentid").toString());
                            certExtras.putString("PID",list.get((pageNum-1)*number+delIndex).get("contentid").toString());
                            certExtras.putString("Nonce",Config.getString("Nonce"));
                            certExtras.putString("requesttype","2");
                            certExtras.putString("user-id",gv.getUserID());
                            certExtras.putString("password",Config.getString("ClientPWD"));
                            ;
                            certExtras.putString("Accept","*/*");
                            certExtras.putString("Host",Config.getString("DRM_URL_DOWNLOADHOST"));
                            certExtras.putString("User-Agent","EInkStack");

                            if(!"SIM".equalsIgnoreCase(gv.getSimType())){
                                certExtras.putString("x-up-calling-line-id",gv.getLineNum());
                            }

                            mFileDownloadManage.downloadCert(certExtras);
                            Bundle bookExtras= new Bundle();
                            bookExtras.putString("Path", list.get((pageNum-1)*number+delIndex).get("bookposition").toString());
                            bookExtras.putString("url",list.get((pageNum-1)*number+delIndex).get("url").toString());
                            bookExtras.putString("name",list.get((pageNum-1)*number+delIndex).get("name").toString());
                            bookExtras.putString("contentID",list.get((pageNum-1)*number+delIndex).get("contentid").toString());
                            bookExtras.putString("cateLog","0");
                            bookExtras.putString("downloadType",list.get((pageNum-1)*number+delIndex).get("downloadType").toString());
                            bookExtras.putString("pathType","1");
                            bookExtras.putString("author",list.get((pageNum-1)*number+delIndex).get("author").toString());
                            bookExtras.putString("maker",list.get((pageNum-1)*number+delIndex).get("maker").toString());
                            bookExtras.putString("saleTime",list.get((pageNum-1)*number+delIndex).get("saleTime").toString());
                            mFileDownloadManage.downloadMebBook(bookExtras);

                            mFileDownloadManage.restartDownloadTask();

                        }
                        else
                        {
                            String downstat = list.get((pageNum-1)*number+delIndex).get("downStat").toString();
                            if(downstat.equals("0"))
                            {
                                Toast.makeText(MyDownloadsActivity.this, "正在下载！", Toast.LENGTH_LONG).show(); 
                                return;
                            }

                            getSearialBookInfo(list.get((pageNum-1)*number+delIndex).get("contentid").toString());
                            Logger.d("subscribeMode download chapters:","");
                            final Bundle extras = new Bundle();
                            final GlobalVar appState = (GlobalVar)getApplicationContext();

                            extras.putString("fileName","");
                            extras.putString("name",list.get((pageNum-1)*number+delIndex).get("name").toString());
                            extras.putString("contentID",list.get((pageNum-1)*number+delIndex).get("contentid").toString());

                            //      extras.putString("downloadType","");//book:单个 chapter:调用getResources接口下载
                            //  extras.putString("url",url);
                            //insert to db
                            extras.putStringArrayList("urlList",chapterurllist);
                            extras.putStringArrayList("idList",chapteridlist);


                            extras.putString("cateLog","0");
                            extras.putString("downloadType",list.get((pageNum-1)*number+delIndex).get("downloadType").toString());
                            extras.putString("pathType","1");
                            extras.putString("processPer","");
                            extras.putString("author",list.get((pageNum-1)*number+delIndex).get("author").toString());
                            extras.putString("maker","0");
                            extras.putString("saleTime","0");

                            extras.putString("Version",Config.getString("SoftwareVersion"));
                            extras.putString("CID",list.get((pageNum-1)*number+delIndex).get("contentid").toString());
                            extras.putString("PID",list.get((pageNum-1)*number+delIndex).get("contentid").toString());
                            extras.putString("Nonce",Config.getString("Nonce"));
                            extras.putString("requesttype","2");
                            extras.putString("user-id",appState.getUserID());
                            extras.putString("password",Config.getString("ClientPWD"));
                            extras.putString("Accept","*/*");
                            extras.putString("Host",Config.getString("DRM_URL_DOWNLOADHOST"));
                            extras.putString("User-Agent","EInkStack");
                            if(!"SIM".equalsIgnoreCase(appState.getSimType())){
                                extras.putString("x-up-calling-line-id",appState.getLineNum());
                            }

                            FileDownloadManage mFileDownloadManage = new FileDownloadManage(MyDownloadsActivity.this);
                            mFileDownloadManage.downloadChapter(extras);
                            mFileDownloadManage.restartDownloadTask();
                        }
                        list.get((pageNum-1)*number+delIndex).put("downStat", 0);
                        list.get((pageNum-1)*number+delIndex).put("processper", 0);
                        items[3].text = getResources().getString(R.string.bookdownloadsdoing);
                        items[4].text = getResources().getString(R.string.bookdownloadsjindu)+list.get((pageNum-1)*number+delIndex).get("processper").toString()+"%";
                        datalist.set(delIndex, items);
                        listView.setData(datalist);
                    }
                }
            }else if(vTag.equals("jixu")){
                
                
                if(list.size()>0){
                    delIndex = listView.mCurFoucsRow;
                    PviUiItem[] items = null;
                    items = datalist.get(delIndex);
                    if(items[1].text.equals("")){
                        Toast.makeText(MyDownloadsActivity.this, getResources().getString(R.string.bookDeletePrompt), Toast.LENGTH_LONG).show();    

                    }else{

                        if(list.get((pageNum-1)*number+delIndex).get("downStat").toString().equals("1")){
                            if(list.get((pageNum-1)*number+delIndex).get("chapterid").toString().equals(""))
                            {

                                FileDownloadManage mFileDownloadManage = new FileDownloadManage(MyDownloadsActivity.this);
                                final Bundle certExtras = new Bundle();
                                final GlobalVar gv=new GlobalVar();

                                certExtras.putString("Version",Config.getString("SoftwareVersion"));
                                certExtras.putString("CID",list.get((pageNum-1)*number+delIndex).get("contentid").toString());
                                certExtras.putString("PID",list.get((pageNum-1)*number+delIndex).get("contentid").toString());
                                certExtras.putString("Nonce",Config.getString("Nonce"));
                                certExtras.putString("requesttype","2");
                                certExtras.putString("user-id",gv.getUserID());
                                certExtras.putString("password",gv.getInnerPassword());
                                certExtras.putString("Accept","*/*");
                                certExtras.putString("Host",Config.getString("DRM_URL_DOWNLOADHOST"));
                                certExtras.putString("User-Agent","EInkStack");
                                if(!"SIM".equalsIgnoreCase(gv.getSimType())){
                                    certExtras.putString("x-up-calling-line-id",gv.getLineNum());
                                }

                                mFileDownloadManage.downloadCert(certExtras);
                                Bundle bookExtras= new Bundle();
                                bookExtras.putString("Path", list.get((pageNum-1)*number+delIndex).get("bookposition").toString());
                                bookExtras.putString("url",list.get((pageNum-1)*number+delIndex).get("url").toString());
                                bookExtras.putString("name",list.get((pageNum-1)*number+delIndex).get("name").toString());
                                bookExtras.putString("contentID",list.get((pageNum-1)*number+delIndex).get("contentid").toString());
                                bookExtras.putString("cateLog","0");
                                bookExtras.putString("downloadType",list.get((pageNum-1)*number+delIndex).get("downloadType").toString());
                                bookExtras.putString("pathType","1");
                                bookExtras.putString("author",list.get((pageNum-1)*number+delIndex).get("author").toString());
                                bookExtras.putString("maker",list.get((pageNum-1)*number+delIndex).get("maker").toString());
                                bookExtras.putString("saleTime",list.get((pageNum-1)*number+delIndex).get("saleTime").toString());
                                mFileDownloadManage.downloadMebBook(bookExtras);

                                mFileDownloadManage.goOnDownloadTask();

                                list.get((pageNum-1)*number+delIndex).put("downStat", "0");
                                items[3].text = getResources().getString(R.string.bookdownloadsdoing);
                                datalist.set(delIndex, items);
                                listView.setData(datalist);
                            }
                            else
                            {
                                Toast.makeText(MyDownloadsActivity.this, "连载书籍不支持暂停操作！", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        else if(list.get((pageNum-1)*number+delIndex).get("downStat").toString().equals("2"))
                        {
                            items[3].text = getResources().getString(R.string.bookdownloadsfish);
                            items[4].text = getResources().getString(R.string.bookdownloadsfish)+list.get((pageNum-1)*number+delIndex).get("processper").toString()+"%";
                            datalist.set(delIndex, items);
                            listView.setData(datalist);
                            Toast.makeText(MyDownloadsActivity.this, getResources().getString(R.string.bookdownloadsfinidhed), Toast.LENGTH_LONG).show();
                        }else{

                            Toast.makeText(MyDownloadsActivity.this, getResources().getString(R.string.bookdownloadsnopause), Toast.LENGTH_LONG).show(); 
                        }
                    }
                }   
            }else if(vTag.equals("pause")){

                if(list.size()>0){
                    delIndex = listView.mCurFoucsRow;
                    PviUiItem[] items = null;
                    items = datalist.get(delIndex);
                    if(items[1].text.equals("")){
                        Toast.makeText(MyDownloadsActivity.this, getResources().getString(R.string.bookDeletePrompt), Toast.LENGTH_LONG).show(); 

                    }else{
                        if(list.get((pageNum-1)*number+delIndex).get("downStat").toString().equals("0")){
                            if(list.get((pageNum-1)*number+delIndex).get("chapterid").toString().equals(""))
                            {
                                FileDownloadManage fdm = new FileDownloadManage(MyDownloadsActivity.this);
                                Bundle task = new Bundle();
                                //                              

                                task.putString("contentID", list.get((pageNum-1)*number+delIndex).get("contentid").toString());
                                fdm.pauseDownloadTask(task);
                                getDate();
                                list.get((pageNum-1)*number+delIndex).put("downStat", "1");
                                items[3].text = getResources().getString(R.string.bookdownloadspause);
                                datalist.set(delIndex, items);
                                listView.setData(datalist);
                            }
                            else
                            {
                                Toast.makeText(MyDownloadsActivity.this, "连载书籍不支持暂停操作！", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(MyDownloadsActivity.this, getResources().getString(R.string.bookdownloadsnodoing), Toast.LENGTH_LONG).show(); 
                        }
                    }
                }
            }

        
        }};



		@Override
		public OnUiItemClickListener getMenuclick() {
			return this.menuclick;
		}

		private void showme(){
			if(!isShown()){
			Logger.d("show me ",this.getClass().getName());
			sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
			Intent tmpIntent = new Intent(MainpageActivity.SHOW_ME);
			Bundle bundleToSend = new Bundle();
			bundleToSend.putString("act", "com.pvi.ap.reader.activity.MyBookshelfActivity");//TabActivity的类名
			bundleToSend.putString("actTabName", "我的下载");
			bundleToSend.putString("sender", this.getClass().getName()); //TAB内嵌activity类的全名
			tmpIntent.putExtras(bundleToSend);
			sendBroadcast(tmpIntent);
			}
		}


		private String getSearialBookInfo(String contentid)
		{
			chapterurllist.clear();
			chapteridlist.clear();
			String columns[] = new String[]{BookInfo._ID, 
					BookInfo.ChapterID,
					BookInfo.URL
			};
			Cursor cur = null;
			String where =BookInfo.PathType+"='"+1+"' and " + BookInfo.ContentID + "='"+contentid+"'";

			cur = managedQuery(BookInfo.CONTENT_URI, columns, where, null, order);
			//			String precontentid = "";
			try{
				if (cur.moveToFirst()){
					do{

						String url=cur.getString(cur.getColumnIndex(BookInfo.URL));
						chapterurllist.add(url);
						String chapterid=cur.getString(cur.getColumnIndex(BookInfo.ChapterID));
						chapteridlist.add(chapterid);
					}
					while (cur.moveToNext());
				}
			}catch(Exception e){
				return "1";
			}finally{
				if(cur!=null){
					cur.close();
				}
			}
			return "0";
		}
		@Override
		public void OnNextpage() {
			// TODO Auto-generated method stub
			if (1<count&&pageNum<=(count-1)){
				//showMessage("数据加载中...","10000");
				pageNum++;
				this.listView.mCurFoucsRow=0;
				setValue();
				super.hideTip();
			}
			super.OnNextpage();
		}
		@Override
		public void OnPrevpage() {
			// TODO Auto-generated method stub
			if(pageNum>1&&count>=2){
				//showMessage("数据加载中...","10000");
				pageNum--;
				this.listView.mCurFoucsRow=0;
				setValue();
				super.hideTip();
			}
			super.OnPrevpage();
		}

}

