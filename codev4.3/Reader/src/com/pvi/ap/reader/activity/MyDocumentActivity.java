package com.pvi.ap.reader.activity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviMenuItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.OnBackListener;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.common.QuitSortComparator;
import com.pvi.ap.reader.data.content.Bookmark;
import com.pvi.ap.reader.activity.pviappframe.PviPopupWindow;

/**
 * 我的文档
 * @author 刘剑雄
 * @since 2011-04-02
 */

public class MyDocumentActivity extends PviActivity implements Pageable{

	private final static String TAG="MyDocumentActivity";
	private ArrayList<HashMap<String, Object>> alldoc = new ArrayList<HashMap<String, Object>>();
    private TextView mydocdir = null;
    private LinearLayout alertpage = null;
	private TextView tishi = null;
	private boolean SelAll = false;
	private int delIndex = 0;
	private String searchkey = "";
	private int currentPage = 1;
	private int itemPerPage = 7;
	private int totalPage = 0;
    //private int ThemeNum = 1;
	private TextView alertmsg = null;
    private String DirPath = "";
	private boolean sdnofile = false;
	private Bundle mbundle = null;
	private Button returnback=null;
	private int deviceType;
	private LinearLayout all=null;
	private int orderType = 1;
	private boolean refresh=false;
	private int id=0;
    protected Context mContext = MyDocumentActivity.this;
	/**
	 * 监听sdcard的广播
	 */
	private final BroadcastReceiver broadcastRec = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
		    
		    //Logger.d("mydoc","onReceive(Context arg0, Intent intent  action:"+intent.getAction());
		    
			
		    final GlobalVar app = (GlobalVar) getApplicationContext();     
	        if (intent.getAction()
					.equals(Intent.ACTION_MEDIA_MOUNTED)) {
	            showPager();
				sdnofile = false ;
				if(!getDocInfo(DirPath, searchkey))
				{  
					showMe();
					return;
				}		
				refresh=true;
				id++;
				setValue();
				showMe();
				listView.requestFocus();
			}
			if (intent.getAction()
					.equals(Intent.ACTION_MEDIA_REMOVED)
					|| intent.getAction().equals(
					Intent.ACTION_MEDIA_UNMOUNTED)
					|| intent.getAction().equals(
					Intent.ACTION_MEDIA_BAD_REMOVAL))

			{
			    //Logger.d("mydoc","sdcard removed");


//				ap.pbb.setItemVisible("pagerinfo",false);
//				ap.pbb.setItemVisible("prevpage",false);
//				ap.pbb.setItemVisible("nextpage",false);
//				ap.pbb.updateDraw();
				//ap.pbb.hidePager();
				//prepage.setVisibility(View.INVISIBLE);
				//nextpage.setVisibility(View.INVISIBLE);
				alertpage.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
				alertmsg.setText("SD Card不存在！相关资料无法获取");
				sdnofile = true ;
				tishi.setVisibility(View.GONE);
				returnback.requestFocus();				
				hidePager();
				showMe();
				return;
			}
		}
	};
	//显示页面
    public void showMe(){
//    	Intent intent = new Intent(MainpageActivity.HIDE_TIP);
//		sendBroadcast(intent);
		
		Intent tmpIntent = new Intent(
                MainpageActivity.SHOW_ME);
        Bundle bundleToSend = new Bundle();
        bundleToSend.putString("act", "com.pvi.ap.reader.activity.ResCenterActivity");//TabActivity的类名
        bundleToSend.putString("actTabName", "我的文档");
        bundleToSend.putString("sender", MyDocumentActivity.this.getClass().getName()); //TAB内嵌activity类的全名
        tmpIntent.putExtras(bundleToSend);
        sendBroadcast(tmpIntent);
        tmpIntent = null;
        bundleToSend = null;
    }
   
	@Override
	protected void onResume() {
		
        
		// TODO Auto-generated method stub
		
		showTip("进入我的文档，请稍候...",2000);
		
		super.onResume();
		Logger.v(TAG, "onResume"); 
		final GlobalVar app = ((GlobalVar) getApplicationContext());
		long TimeStart = System.currentTimeMillis();
		Logger.i("Time", "MyDocumentActivity enter: " + Long.toString(TimeStart));
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);

		intentFilter.addDataScheme("file");
		registerReceiver(broadcastRec, intentFilter);// 注册监听函数
	     /**
	      * 重写状态栏返回
	      */
		((GlobalVar) getApplicationContext()).pbb.setOnBackListener(new OnBackListener(){

			@Override
			public boolean onBack() {
				// TODO Auto-generated method stub
				Logger.v(TAG, "sdnofile="+sdnofile);
				if(sdnofile){
					sendBroadcast(new Intent(MainpageActivity.BACK));	
					return true;
				}
				return retBackTo();
			}
			 
		 });
		this.setOnPmShow(new OnPmShowListener(){

            @Override
            public void OnPmShow(PviPopupWindow popmenu) {               
              //设置排序子菜单的焦点与orderType一致                
                if(orderType==1){
                    final PviMenuItem vSortByTime = getMenuItem("sortName");
                    if(vSortByTime!=null){
                        vSortByTime.isFocuse = true;
                    }                    
                }else if(orderType==2){
                    final PviMenuItem vSortByBook = getMenuItem("sortRead");
                    if(vSortByBook!=null){
                        vSortByBook.isFocuse = true;
                    }
                }     
                
            }});
		if(!getDocInfo(DirPath, searchkey))
		{   this.showPager = false;
			showMe();
			return;
		}		
		this.showPager = true;
		setValue();
		showMe();

        
     //   this.doclayout[0].requestFocus();
        
	}
	
	@Override
	protected void onStart() {
         super.onStart();
	}
	
	private boolean isSerch = false ;
    /**
     * 设置页面元素的值
     * 
     */
	private void setValue() {
	    
	       if(alldoc==null){
	            Logger.e(TAG,"alldoc is null");
	            return;
	        }
	    
//		if(currentPage%5==0){
//			if(deviceType==1){
//			    if(id==5){
//			     id=0;	
//			  Logger.v("MyDocument", "refresh off");	
//			  getWindow().getDecorView().getRootView().invalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
//			  Logger.v("MyDocument", "refresh on");
//			    }
//			    }
//			}
		if(orderType==1){
		sortUtil = new QuitSortComparator("IsDir","Name","updatetime");
		sortUtil.setDescend2(false);	
		
		}else{
			sortUtil = new QuitSortComparator("IsDir","updatetime","Name");
			sortUtil.setDescend2(true);
		}
		

		
		Collections.sort(alldoc,sortUtil);
		mydocdir.setText(this.DirPath.replace("/", "-->").replace("MyDoc", "我的文档"));
		totalPage = this.alldoc.size() / this.itemPerPage;
		int n = this.alldoc.size() % this.itemPerPage;	
		if (n != 0) {
			totalPage++;
		}
		if(this.totalPage == 0)
		{
			delIndex = -1;

			//prepage.setVisibility(View.INVISIBLE);
			//nextpage.setVisibility(View.INVISIBLE);
			alertpage.setVisibility(View.GONE);
			listView.setVisibility(View.GONE);
			tishi.setVisibility(View.VISIBLE);
			if(searchkey.equals("") || !isSerch)
			{   
				tishi.setText("没有相关文件记录");
				tishi.setFocusable(true);
				tishi.setFocusableInTouchMode(true);
				tishi.requestFocus();
				tishi.setOnKeyListener(onKeyListener);
				//alertmsg.setText(this.DirPath.replace("/", "-->").replace("MyDoc", "我的文档") +"目录下无文件...");
				this.updatePagerinfo("1 / 1");
			}
			else{
					tishi.setText("没有搜索到相关文件");
					tishi.setFocusable(true);
					tishi.setFocusableInTouchMode(true);
					tishi.requestFocus();
					tishi.setOnKeyListener(onKeyListener);
					//alertmsg.setText("没有搜索到相关文件");
					//searchkey = "";
					this.updatePagerinfo("1 / 1");
			}

			return;
		}


		tishi.setVisibility(View.GONE);
		alertpage.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		if(this.totalPage < this.currentPage)
		{
			this.currentPage = this.totalPage;
		}
		
		
		//数据填充

		HashMap<String, Object> map = null;
		String extfilename = "";

		int len = this.currentPage < this.totalPage ? this.itemPerPage
				: (alldoc.size() - (this.currentPage - 1)
						* this.itemPerPage);
		list.clear();
		for (int i = 0; i < itemPerPage; i++) {
			if(i < len)
			{
			    PviUiItem[] items = new PviUiItem[]{
		                new PviUiItem("icon"+i, R.drawable.folder2, 10, 10, 50, 50, null, false, true, null),
		                new PviUiItem("text1"+i, 0, 100, 30, 250, 50, "我是一列文本", false, true, null),
		                new PviUiItem("text2"+i, 0, 540, 30, 200, 50, "我是又一列文本", false, true, null),
		        };
				map = alldoc.get((this.currentPage - 1) * this.itemPerPage + i);

				if(map.get("IsDir").toString().equals("true"))
				{
					
				    items[0].res=R.drawable.folder2;
					
				}
				else
				{
					extfilename = map.get("Ext").toString().toLowerCase();
					if(extfilename.equals("txt"))
					{
						
					    items[0].res=R.drawable.txt2;
						
					}
					else if(extfilename.equals("pdf"))
					{	
						
					    items[0].res=R.drawable.pdf2;
						
						
					}
					else if(extfilename.equals("meb"))
					{
						
					    items[0].res=R.drawable.meb2;
						
					}
					else if(extfilename.equals("doc")||extfilename.equals("docx"))
					{
						
					    items[0].res=R.drawable.word;
						
					}
					else if(extfilename.equals("xls")||extfilename.equals("xlsx"))
					{
						
					    items[0].res=R.drawable.excel;
						
					}
					else if(extfilename.equals("ppt"))
					{
						
					    items[0].res=R.drawable.ppt;
						
					}
					else
					{
						
					    items[0].res=R.drawable.unknownfile;
						
					}
				}
				items[1].text = map.get("Name").toString();
				items[1].textSize=22;
				items[2].text = map.get("Size").toString();
				items[2].textSize=19;
				items[2].textAlign=2;
				final int ii=i;
				
				
//				OnClickListener l = new OnClickListener(){
//
//                    @Override
//                    public void onClick(View arg0) {
//                        // TODO Auto-generated method stub
//                        setEvent(ii);
//                    }
//                    
//                };
                listView.setOnRowClick(new PviDataList.OnRowClickListener() {
					
					@Override
					public void OnRowClick(View v, int rowIndex) {
						// TODO Auto-generated method stub
						 setEvent(ii);
					}
				});
//                items[1].l = l;
//                items[2].l = l;
				
                list.add(items);
				
			}
			
			listView.setData(list);
		}
		if(deviceType==1){
		    if(id==5){
		     id=0;	
		  Logger.v("MyDocument", "refresh off");	
//		  getWindow().getDecorView().getRootView().postInvalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
		  Logger.v("MyDocument", "refresh on");
		    }
		    }
		//this.doclayout[0].requestFocus();
		
		final GlobalVar app = ((GlobalVar) getApplicationContext());        
       updatePagerinfo(currentPage+" / "+this.totalPage);

	}


	private View.OnClickListener docitemclick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			HashMap<String, Object> map = new HashMap<String, Object>();
			File file = null;
			String filename = "";
			
			filename= list.get(listView.mCurFoucsRow)[1].text;
			
			if(filename.equals(""))
			{
				return;
			}
			file = new File(Config.getString("MyDocPath") + DirPath + "/" + filename);
			
			if (file.isFile() && file.exists()) {
				//doclayout[delIndex%itemPerPage].setEnabled(false);			
				
				map.put("FilePath",Config.getString("MyDocPath") + DirPath +"/"+ filename);
				map.put("ChapterID", "");
				map.put("FromPath", "0");
				map.put("Offset", "0");
				map.put("CertPath", "");
				map.put("ContentID", "");
				map.put("SourceType", "3");

				if(OpenReader.gotoReader(MyDocumentActivity.this, map)==-1)
				{   //doclayout[delIndex%itemPerPage].setEnabled(true);
					final PviAlertDialog temp = new PviAlertDialog(getParent());
					temp.setTitle("温馨提示");
					temp.setMessage("不能支持的文件类型！");
					temp.setCanClose(true);
					temp.show();
				}
			} else if (file.isDirectory()) {
				//doclayout[delIndex%itemPerPage].setEnabled(true);
				//doclayout[0].requestFocus();
				DirPath = DirPath + "/"+ filename;
				if(!getDocInfo(DirPath, searchkey))
				{
					return;
				}	
				Intent tmpIntent = new Intent(
						MainpageActivity.SET_TITLE);
				Bundle sndbundle = new Bundle();
				sndbundle.putString("title", "我的文档[>>]"+filename);
				tmpIntent.putExtras(sndbundle);
				sendBroadcast(tmpIntent);
				refresh=true;
				id++;
				setValue();
			} else {
				return;
			}
		}
	};
	public void  setEvent(int i){
		HashMap<String, Object> map = new HashMap<String, Object>();
		File file = null;
		String filename = "";
		if(listView.mCurFoucsRow==-1||listView.mCurFoucsRow>=list.size()){
			return ;
		}
		filename= list.get(listView.mCurFoucsRow)[1].text;
		file = new File(Config.getString("MyDocPath") + DirPath + "/" + filename);
		if (file.isFile() && file.exists()) {
			map.put("FilePath",Config.getString("MyDocPath") + DirPath +"/"+ filename);
			map.put("ChapterID", "");
			map.put("FromPath", "0");
			map.put("Offset", "0");
			map.put("CertPath", "");
			map.put("ContentID", "");
			map.put("SourceType", "3");
//			searchkey = "";
			if(OpenReader.gotoReader(MyDocumentActivity.this, map)==-1)
			{
				final PviAlertDialog temp = new PviAlertDialog(getParent());
				temp.setTitle("温馨提示");
				temp.setMessage("不能支持的文件类型！");
				temp.setCanClose(true);
				temp.show();
			}
		} else if (file.isDirectory()) {
			//doclayout[delIndex%itemPerPage].setEnabled(true);
			//doclayout[0].requestFocus();
			DirPath = DirPath + "/"+ filename;
			if(!getDocInfo(DirPath, searchkey))
			{
				return;
			}	
			Intent tmpIntent = new Intent(
					MainpageActivity.SET_TITLE);
			Bundle sndbundle = new Bundle();
			sndbundle.putString("title", "我的文档[>>]"+filename);
			tmpIntent.putExtras(sndbundle);
			sendBroadcast(tmpIntent);
			refresh=true;
			id++;
			setValue();
		} else {
			return;
		}

	}
	private View.OnKeyListener onKeyListener=new OnKeyListener() {

		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(event.getAction()==KeyEvent.ACTION_UP){
				Logger.v("ACTION_UP", "ACTION_UP");
				if(keyCode == event.KEYCODE_BACK){
					 Logger.v("BACK", "BACK");
					 return retBackTo();
				}
//				if (keyCode == event.KEYCODE_DPAD_LEFT) {
//					Logger.v("KEYCODE_DPAD_LEFT", "KEYCODE_DPAD_LEFT");
//					prevPage();
//					return true;
//				}
//
//				if (keyCode == event.KEYCODE_DPAD_RIGHT) {
//					Logger.v("KEYCODE_DPAD_RIGHT", "KEYCODE_DPAD_RIGHT");
//					nextPage();
//					return true;
//				}
//				if ((keyCode == event.KEYCODE_ENTER)||(keyCode == event.KEYCODE_DPAD_CENTER)) {
//
//					setEvent(listView.mCurFoucsRow);
//					return true;
//				}
				return false;
			}
//			if (event.getAction() == KeyEvent.ACTION_DOWN) {
//				Logger.v("ACTION_DOWN", "ACTION_DOWN");
//				
//			}            

			return false;
		}
	};

	PviDataList listView;
	ArrayList<PviUiItem[]> list;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		GlobalVar appState = ((GlobalVar) getApplicationContext());
		deviceType=appState.deviceType;    
		this.setContentView(R.layout.mydoclist);
		super.onCreate(savedInstanceState);
		Logger.v(TAG, "onCreate");
		listView= (PviDataList)findViewById(R.id.list);
		list = new ArrayList<PviUiItem[]>();
/*		list.add(new PviUiItem[]{
		        new PviUiItem("icon1", R.drawable.folder2, 10, 10, 50, 50, null, false, true, null),
		        new PviUiItem("text1", 0, 200, 10, 200, 50, "我是一列文本", false, true, null),
		        new PviUiItem("text11", 0, 400, 10, 200, 50, "我是又一列文本", false, true, null),
		});
		list.add(new PviUiItem[]{
                new PviUiItem("icon2", R.drawable.folder2, 10, 10, 50, 50, null, false, true, null),
                new PviUiItem("text2", 0, 200, 10, 200, 50, "我是一列文本", false, true, null),
                new PviUiItem("text22", 0, 400, 10, 200, 50, "我是又一列文本", false, true, null),
        });
		list.add(new PviUiItem[]{
                new PviUiItem("icon3", R.drawable.folder2, 10, 10, 50, 50, null, false, true, null),
                new PviUiItem("text3", 0, 200, 10, 200, 50, "我是一列文本", false, true, null),
                new PviUiItem("text33", 0, 400, 10, 200, 50, "我是又一列文本", false, true, null),
        });
		list.add(new PviUiItem[]{
                new PviUiItem("icon4", R.drawable.folder2, 10, 10, 50, 50, null, false, true, null),
                new PviUiItem("text4", 0, 200, 10, 200, 50, "我是一列文本", false, true, null),
                new PviUiItem("text44", 0, 400, 10, 200, 50, "我是又一列文本", false, true, null),
        });
		list.add(new PviUiItem[]{
                new PviUiItem("icon5", R.drawable.folder2, 10, 10, 50, 50, null, false, true, null),
                new PviUiItem("text5", 0, 200, 10, 200, 50, "我是一列文本", false, true, null),
                new PviUiItem("text55", 0,400, 10, 200, 50, "我是又一列文本", false, true, null),
        });
		listView.setData(list);*/
		
		
		  
		 

		try {
			alertpage = (LinearLayout) this.findViewById(R.id.alertpage);

			returnback = (Button) this.findViewById(R.id.returnback);
			alertmsg = (TextView) this.findViewById(R.id.alertmsg);


			
			mydocdir = (TextView) this.findViewById(R.id.mydocdir);







			tishi = (TextView) this.findViewById(R.id.tishi);
			
			all=(LinearLayout)findViewById(R.id.filelist);
//			if (getRetView() != null) {
//				getRetView().setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View arg0) {
//						Logger.i(TAG, "sdnofile="+sdnofile);
//						if(sdnofile){
//							sendBroadcast(new Intent(MainpageActivity.BACK));	
//							return ;
//						}
//						retBackTo();
//					}
//				});
//			}
		} catch (Exception e) {
			Logger.i("Menu", e.toString());
		}

		
		listView.setOnKeyListener(onKeyListener);

		returnback.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//				System.out.println("DirPath="+DirPath);
				sendBroadcast(new Intent(MainpageActivity.BACK));
//				if("MyDoc".equals(DirPath)){
//					sendBroadcast(new Intent(MainpageActivity.BACK));	
//				}else{
//					int lastindex = DirPath.lastIndexOf("/");	
//					DirPath = DirPath.substring(0, lastindex);
//					Intent sndintent = new Intent(MainpageActivity.START_ACTIVITY);
//					Bundle sndBundle = new Bundle();
//					sndBundle.putString("act","com.pvi.ap.reader.activity.ResCenterActivity");
//					sndBundle.putString("startType",  "allwaysCreate");  
//					sndBundle.putString("haveTitleBar", "1");
//					sndBundle.putString("DirPath", DirPath);
//					sndBundle.putString("actTabIndex", "0");
//					sndintent.putExtras(sndBundle);
//					sendBroadcast(sndintent);
//				}
			}
		});


		
		}
	

	private void nextPage(){
	    try {
            if (currentPage == totalPage) {
                return;
            }
            currentPage++;
//          onResume();
            if(!getDocInfo(DirPath, searchkey))
            {
                return;
            }   
//          if(currentPage%5==0){
//              if(deviceType==1){
//              
//              all.setUpdateMode(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);   
//              }
//              }
            //doclayout[0].requestFocus();
            listView.requestFocus();
            refresh=true;
            id++;
            Logger.v("id", id);
            setValue();

        } catch (Exception e) {
            Logger.i("Reader", "next page: " + e.toString());
        }
	}
	
	private void prevPage(){
	    try {
            if (currentPage == 1) {
                return;
            }
            currentPage--;
            if(!getDocInfo(DirPath, searchkey))
            {
                return;
            }
            refresh=true;
            id++;
            Logger.v("id", id);
            setValue();

            //doclayout[0].requestFocus();
            listView.requestFocus();
        } catch (Exception e) {
            Logger.i("Reader", "pre page: " + e.toString());
        }
	}
	
	
   /**
    * 查询sdcard文件列表
    * @param dirpath
    * @param searchkey
    * @return
    */
	private boolean getDocInfo(String dirpath, String searchkey) {

		alldoc.clear();	
		String dir = "";
		if (dirpath.equals("")) {
			this.DirPath = "MyDoc";		
		} else {
			this.DirPath = dirpath;
		}

		dir= android.os.Environment.getExternalStorageDirectory()
		.getPath() + "/" + this.DirPath;
		
		if (!(android.os.Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED))) {
//			ap.pbb.setItemVisible("pagerinfo",false);
//			ap.pbb.setItemVisible("prevpage",false);
//			ap.pbb.setItemVisible("nextpage",false);
//			ap.pbb.updateDraw();
			//ap.pbb.hidePager();
			//prepage.setVisibility(View.INVISIBLE);
			//nextpage.setVisibility(View.INVISIBLE);
			alertpage.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			alertmsg.setText("SD Card不存在！相关资料无法获取...");
			tishi.setVisibility(View.GONE);
			sdnofile = true;
			return false;
		}

		//this.dashtxt.setVisibility(View.VISIBLE);
		//prepage.setVisibility(View.VISIBLE);
		//nextpage.setVisibility(View.VISIBLE);
//		ap.pbb.setItemVisible("pagerinfo",true);
//		ap.pbb.setItemVisible("prevpage",true);
//		ap.pbb.setItemVisible("nextpage",true);
//		ap.pbb.updateDraw();
		//ap.pbb.showPager();
		alertpage.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		tishi.setVisibility(View.GONE);
		Logger.i("Reader", this.DirPath);
		File path = new File(dir);
		boolean a = path.mkdirs();

		File[] alldocfile = null;
		alldocfile = path.listFiles();

		if (alldocfile == null) {
             
			//prepage.setVisibility(View.INVISIBLE);
			//nextpage.setVisibility(View.INVISIBLE);
			alertpage.setVisibility(View.GONE);
			listView.setVisibility(View.GONE);
			tishi.setVisibility(View.VISIBLE);
			tishi.setText("没有相关文件记录");
			this.updatePagerinfo("1 / 1");
			return false;

		}

		sdnofile = false;

		String temp = "";
		String extfilename = "";
		for (int i = 0; i < alldocfile.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			temp = alldocfile[i].getName();
			double tempsize = 0.0;
			DecimalFormat df = new DecimalFormat("#.##");
			map.put("updatetime", "" + alldocfile[i].lastModified());

			if (searchkey.equals("")) {
				map.put("Name", temp);
				if (alldocfile[i].isDirectory()) {
					map.put("IsDir", "true");
					map.put("Size", "");
				} else if (alldocfile[i].isFile()) {
					if(!toVisble(temp)){
						continue ;
					}
					extfilename = getFileExt(alldocfile[i].getName()).toLowerCase();
					//					if(!(extfilename.equals("txt") || extfilename.equals("meb") ||extfilename.equals("pdf")))
					//					{
					//						continue;
					//					}
					map.put("Ext", extfilename);
					map.put("IsDir", "false");
					if (alldocfile[i].length() / 1024 == 0) {
						tempsize = (double) alldocfile[i].length();
						map.put("Size", df.format(tempsize) + "B");
					} else if (alldocfile[i].length() / (1024 * 1024) == 0) {
						tempsize = ((double) alldocfile[i].length()) / 1024;
						map.put("Size", df.format(tempsize) + "KB");
					} else {
						tempsize = ((double) alldocfile[i].length())
						/ (1024 * 1024);
						map.put("Size", df.format(tempsize) + "MB");
					}
				}

				alldoc.add(map);
			} else {
				if (temp.toLowerCase().contains(searchkey.toLowerCase())) {
					map.put("Name", temp);
					if (alldocfile[i].isDirectory()) {
						map.put("IsDir", "true");
						map.put("Size", "");
					} else if (alldocfile[i].isFile()) {
						if(!toVisble(temp)){
							continue ;
						}
						extfilename = getFileExt(alldocfile[i].getName()).toLowerCase();
						//						if(!(extfilename.equals("txt") || extfilename.equals("meb") ||extfilename.equals("pdf")))
						//						{
						//							continue;
						//						}
						map.put("Ext", extfilename);
						map.put("IsDir", "false");
						if (alldocfile[i].length() / 1024 == 0) {
							tempsize = (double) alldocfile[i].length();
							map.put("Size", df.format(tempsize) + "B");
						} else if (alldocfile[i].length() / (1024 * 1024) == 0) {
							tempsize = ((double) alldocfile[i].length()) / 1024;
							map.put("Size", df.format(tempsize) + "KB");
						} else {
							tempsize = ((double) alldocfile[i].length())
							/ (1024 * 1024);
							map.put("Size", df.format(tempsize) + "MB");
						}
					}
					alldoc.add(map);
				}
			}
		}
		
		return true;
	}

	private String getFileExt(String filename) {
		filename = filename.substring(filename.lastIndexOf(".") + 1);
		return filename;
	}

	@Override
	protected void onPause() {

		if (broadcastRec != null) {
			unregisterReceiver(broadcastRec);// 使用完注销广播监听函数
		}
		super.onPause();
		refresh=false;
		id=0;
		searchkey = "";
		//DirPath = "";
		//currentPage = 1;
		isSerch = false ;
		if(delIndex>0){
		//doclayout[delIndex%itemPerPage].setEnabled(true);
		}
	}

	
	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){
        @Override
        public void onUiItemClick(PviUiItem item) {

            final PviAlertDialog dialog = new PviAlertDialog(getParent());
            dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
            dialog.setCanClose(true);
            String vTag = item.id;
            if(vTag.equals("back1")){
            	if(sdnofile){
					sendBroadcast(new Intent(MainpageActivity.BACK));	
					return ;
				}
                retBackTo();
            }
            if(sdnofile)
            {
//              Toast.makeText(MyDocumentActivity.this, "SD Card不存在！",
//                      Toast.LENGTH_LONG).show();
                dialog.setMessage("SD Card不存在");
                dialog.show();
                closePopmenu();
                return;
            }
            
            
             
                if (vTag.equals("delete")) {
                    if(listView.mCurFoucsRow>=list.size()||listView.mCurFoucsRow==-1){
                        dialog.setMessage("请选择要操作的文件");
                        dialog.show();
                        closePopmenu();
                        return ;
                        }
                    delete();
                } else if (vTag.equals("search")) {
                    // 搜索
                    isSerch = true ;
//                  if(sdnofile)
//                  {
//                      Toast.makeText(MyDocumentActivity.this, "SD Card不存在！",
//                              Toast.LENGTH_LONG).show();
//                      return;
//                  }
                    LayoutInflater inflater=LayoutInflater.from(getParent());

                    final View view=inflater.inflate(R.layout.search, null);
                    final PviAlertDialog pd = new PviAlertDialog(getParent());
                    pd.setView(view);
                    pd.setCanClose(true);
                    pd.setTitle("搜  索");
                    final TextView tv = (TextView)view.findViewById(R.id.hint);
                    final EditText edt = (EditText)view.findViewById(R.id.keyword);
                    Button search = (Button)view.findViewById(R.id.searchbtn);
                    
                    search.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            searchkey = edt.getText().toString();
                            searchkey = searchkey.toLowerCase();
                            if (searchkey.equals("")) {
                                tv.setText("搜索关键字为空，将列举全部文件！");
                            } 
                            if(!getDocInfo(DirPath, searchkey))
                            {
                                pd.dismiss();
                                return;
                            }       
                            pd.dismiss();
                            refresh=true;
                            id++;
                            setValue();
                            
                            //searchkey="";
                        }
                    });
                    edt.setOnFocusChangeListener(new OnFocusChangeListener() {

                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            // TODO Auto-generated method stub
                            if (hasFocus) {
                                tv.setText("");
                            }
                        }

                    });
                    pd.show();
                }
                else if (vTag.equals("retparentdir")) 
                {
                    int lastindex = DirPath.lastIndexOf("/");
                    if(lastindex == -1)
                    {
                        final PviAlertDialog pd = new PviAlertDialog(getParent());
                        pd.setTitle("温馨提示");
                        pd.setMessage("当前目录已经是我的文档的根目录");
                        pd.setCanClose(true);
                        pd.show();
                        return ;
                    }
                    DirPath = DirPath.substring(0, lastindex);

                    Logger.d("Reader", DirPath);
                    Intent sndintent = new Intent(MainpageActivity.START_ACTIVITY);
                    Bundle sndBundle = new Bundle();
                    sndBundle.putString("act","com.pvi.ap.reader.activity.ResCenterActivity");
                    sndBundle.putString("startType",  "allwaysCreate");  
                    sndBundle.putString("haveTitleBar", "1");
                    sndBundle.putString("DirPath", DirPath);
                    sndintent.putExtras(sndBundle);
                    sendBroadcast(sndintent);           
                }else if(vTag.equals("deleteAll")){
                    deleteAll();
                }else if(vTag.equals("sortName")){
                    sortChoose = 0 ;
                    sort();
                }else if(vTag.equals("sortRead")){
                    sortChoose = 1 ;
                    sort();
                }else if(vTag.equals("rename")){
                    if(listView.mCurFoucsRow>=list.size()||listView.mCurFoucsRow==-1){
                        dialog.setMessage("请选择要操作的文件");
                        dialog.show();
                        closePopmenu();
                        return ;
                        }
                    rename();
                }
                closePopmenu();
        
        }};

	
	private int sortChoose = 0 ;
	private QuitSortComparator sortUtil = new QuitSortComparator("IsDir","Name","updatetime");


	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		int lastindex = DirPath.lastIndexOf("/");
		if(lastindex == -1)
		{
			return;
		}
		DirPath = DirPath.substring(0, lastindex);

		this.mbundle.putString("DirPath", DirPath);
		intent.putExtras(this.mbundle);
		setIntent(intent);
		super.onNewIntent(intent);
	}	
	@Override
	public OnUiItemClickListener getMenuclick() {
		return this.menuclick;
	}

	private void rename() {
		final PviAlertDialog dia = new PviAlertDialog(getParent());
		dia.setTitle(getResources().getString(R.string.systemconfig_pop_message));
		dia.setCanClose(true);
		if (delIndex == -1) {
//			Toast.makeText(MyDocumentActivity.this, "请选择要重命名的文件！",
//					Toast.LENGTH_LONG).show();
			dia.setMessage("请选择要重命名的文件");
			dia.show();
			return;
		}
		final String Path = Config.getString("MyDocPath") +  DirPath + "/";
		//final String namePath = docname[delIndex%itemPerPage].getText().toString();
		final String namePath = list.get(listView.mCurFoucsRow)[1].text;
		final PviAlertDialog pd2 = new PviAlertDialog(getParent());
		pd2.setTitle("重命名");
		LayoutInflater inflater = LayoutInflater.from(MyDocumentActivity.this);
		final View entryView = inflater.inflate(R.layout.rename, null);
		pd2.setView(entryView);
		pd2.setCanClose(true);
		final EditText et = (EditText)entryView.findViewById(R.id.rename_name);
		File file = new File(Path + namePath);
		final String name ;
		if(!file.isDirectory()){
			name = namePath.substring(namePath.lastIndexOf('.'));
		et.setText("" + namePath.substring(0, namePath.lastIndexOf('.')));
		}else{
			name = "" ;
			et.setText(namePath);
		}
		pd2.setButton(DialogInterface.BUTTON_POSITIVE,getString(R.string.bookConfirm),new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				File file = new File(Path + namePath);
				File newFile = new File(Path + et.getText().toString() + name);
				Logger.i(MyDocumentActivity.this.getClass().getName(), namePath);
				Logger.i(MyDocumentActivity.this.getClass().getName(), et.getText().toString().trim());
				if((!newFile.exists()) && (!namePath.equals(et.getText().toString().trim()))&&file.renameTo(newFile)){
					PviAlertDialog pd3 = new PviAlertDialog(getParent());
					pd3.setTitle("温馨提示");
					pd3.setMessage("重命名成功");
					pd3.setCanClose(true);
					//pd3.show();
					if(!getDocInfo(DirPath, searchkey))
					{
						return;
					}
					refresh=true;
					id++;
					setValue();
					
				}else{
					PviAlertDialog pd3 = new PviAlertDialog(getParent());
					pd3.setTitle("温馨提示");
					String message = "" ;
					if(newFile.exists()){
						message = "重命名失败，该文件名已存在！" ;
					}else if("".equals(et.getText().toString().trim())){
						message = "重命名失败，文件名不能为空！" ;
					}else if(namePath.equals(et.getText().toString().trim())){
						message = "重命名失败，文件名没有变化！" ;
					}
					pd3.setMessage(message);
					pd3.setCanClose(true);
					pd3.show();
				}
			}
			
		});
		pd2.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.Cancel),
		 new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				pd2.dismiss();
			}
		});
		pd2.show();
		refresh=true;
		id++;
		setValue();
	}

	private void sort() {
		if(sortChoose == 0 ){
			orderType = 1;
			sortUtil = new QuitSortComparator("IsDir","Name","updatetime");
			sortUtil.setDescend2(false);
			refresh=true;
			id++;
			setValue();
		}else if(sortChoose == 1){
			orderType = 2;
			sortUtil = new QuitSortComparator("IsDir","updatetime","Name");
			sortUtil.setDescend2(true);
			refresh=true;
			id++;
			setValue();
		}
	}
	/**
	 * 删除文件对应的书签
	 * @param name
	 * @return
	 */
	public boolean delFileBookmark(String name){
		boolean flag=false;
		if(getFileExt(name).toLowerCase().contains("meb")){
			return flag;
		}else{
		String where=Bookmark.ContentName+"='"+name+"'";
        getContentResolver().delete(Bookmark.CONTENT_URI,where,null);
        flag=true;
		return flag;
		}
	}
	
	private void delete() {
		isSerch = false ;
		final PviAlertDialog dl = new PviAlertDialog(getParent());
		dl.setTitle(getResources().getString(R.string.systemconfig_pop_message));
		dl.setCanClose(true);
		if (delIndex == -1) {
//			Toast.makeText(MyDocumentActivity.this, "请选择要删除的文件！",
//					Toast.LENGTH_LONG).show();
			dl.setMessage("请选择要删除的文件");
			dl.show();
			closePopmenu();
			return;
		}
		final PviAlertDialog pd = new PviAlertDialog(getParent());
		pd.setTitle("删  除");
		pd.setCanClose(true);
		pd.setMessage(getString(R.string.alert_dialog_delete_message));
		pd.setTimeout(5000); // 可选参数 延时5000ms后自动关闭

		pd.setButton(DialogInterface.BUTTON_POSITIVE,
				getString(R.string.alert_dialog_delete_yes),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int which) {


				File myfile = null;
				String Path1 = Config.getString("MyDocPath") +  DirPath + "/";
				String Path="";
				if (SelAll) {
					for (int i = 0; i < itemPerPage; i++) {
						//Path = Config.getString("MyDocPath") +  DirPath + "/";
						//Path = Path1 + MyDocumentActivity.this.docname[i].getText().toString();
					    Path = Path1 + list.get(listView.mCurFoucsRow)[1].text;
					    
					    myfile = new File(Path);
						if (!myfile.exists()||Path.equals("")) {
							return;
						}
						final PviAlertDialog temp = new PviAlertDialog(getParent());
						temp.setTitle("温馨提示");
//						if(delete(myfile))
//						{	
//							temp.setMessage("文件已删除！");
//						}
//						else
//						{
							temp.setMessage("文件删除失败！");
//						}
						temp.setCanClose(true);
						temp.show();
						myfile = new File(Constants.CON_FIRST_PAGE_LOCATION + Path.replace('/', '.'));
						myfile.delete();
					}
					SelAll = false;
				} else {

					//Path = Path1 + MyDocumentActivity.this.docname[delIndex%itemPerPage].getText().toString();
				    Path = Path1 +list.get(listView.mCurFoucsRow)[1].text;
					
					myfile = new File(Path);
					final PviAlertDialog temp = new PviAlertDialog(getParent());
					temp.setTitle("温馨提示");
					temp.setCanClose(true);
					if(delete(myfile))
					{	
						//temp.setMessage("文件已删除！");
					}
					else
					{
						temp.setMessage("文件删除失败！");
						temp.show();
					}
					
					myfile = new File(Constants.CON_FIRST_PAGE_LOCATION + Path.replace('/', '.'));
					myfile.delete();
				}
				if(!getDocInfo(DirPath, searchkey))
				{
					return;
				}		
				refresh=true;
				id++;
				setValue();
			}
		});

		pd.setButton(DialogInterface.BUTTON_NEGATIVE,
				getString(R.string.alert_dialog_delete_no),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int which) {
				// TODO Auto-generated method stub
				pd.dismiss();
			}
		});

		pd.show();
	}
	
	
	private void deleteAll() {
		final PviAlertDialog pd = new PviAlertDialog(getParent());
		pd.setTitle("删除全部");
		pd.setCanClose(true);
		pd.setMessage(getString(R.string.playingmusicdeleteallmsg));
		pd.setTimeout(5000); // 可选参数 延时5000ms后自动关闭
		if (delIndex == -1) {
//			Toast.makeText(MyDocumentActivity.this, "请选择要删除的文件！",
//					Toast.LENGTH_LONG).show();
			
			closePopmenu();
			return;
		}
		pd.setButton(DialogInterface.BUTTON_POSITIVE,
				getString(R.string.alert_dialog_delete_yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						isSerch = false ;
						final PviAlertDialog temp = new PviAlertDialog(
								getParent());
						temp.setTitle("温馨提示");
						boolean ok = true ;
						for(HashMap<String, Object> tmpMap :alldoc){
							ok = ok && delete(android.os.Environment.getExternalStorageDirectory()
									.getPath() + File.separator + DirPath + File.separator+ tmpMap.get("Name").toString());
						}
						if (ok) {
							//temp.setMessage("文件已删除！");
						} else {
							temp.setMessage("文件删除失败！");
							temp.setCanClose(true);
							temp.show();
						}
						
						if (!getDocInfo(DirPath, searchkey)) {
							return;
						}
						refresh=true;
						id++;
						setValue();
					}
				});

		pd.setButton(DialogInterface.BUTTON_NEGATIVE,
				getString(R.string.alert_dialog_delete_no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						pd.dismiss();
					}
				});

		pd.show();

	}
	
	
	private boolean delete(String fileName){
		File file = new File(fileName);
		return delete(file);
	}
	
	private boolean delete(File fileName){
		if(fileName.isFile()){
			delFileBookmark(fileName.getName());
			return fileName.delete();
			
		}
		if(fileName.isDirectory()){
			for(File file:fileName.listFiles()){
				delete(file);
				delFileBookmark(fileName.getName());
			}
			return fileName.delete();
		}
		
		return false ;
	}
	/**
	 * 过滤文件
	 * @param fileName
	 * @return
	 */
	private boolean toVisble(String fileName){
		if(fileName.toLowerCase().endsWith(".txt")){
			return true ;
		}
		if(fileName.toLowerCase().endsWith(".meb")){
			return true ;
		}
		if(fileName.toLowerCase().endsWith(".pdf")){
			return true ;
		}
		if(fileName.toLowerCase().endsWith(".doc")){
			return true ;
		}
		if(fileName.toLowerCase().endsWith(".docx")){
			return true ;
		}
		if(fileName.toLowerCase().endsWith(".xlsx")){
			return true ;
		}
		if(fileName.toLowerCase().endsWith(".xls")){
			return true ;
		}
		if(fileName.toLowerCase().endsWith(".ppt")){
			return true ;
		}
		return false;
	}
    /**
     * 重写返回
     * @return
     */
	private boolean retBackTo() {
		Logger.i("TAG->retBackTo->", "retBackTo") ;
		
		if(!"".equals(searchkey)){
			searchkey = "" ;
			if(!getDocInfo(DirPath, searchkey))
			{
				return true ;
			}
			Intent tmpIntent = new Intent(
					MainpageActivity.SET_TITLE);
			Bundle sndbundle = new Bundle();
			sndbundle.putString("title", "我的文档");
			tmpIntent.putExtras(sndbundle);
			sendBroadcast(tmpIntent);
			refresh=true;
			id++;
			setValue();
			return  true ;
		}
		
		int lastindex = DirPath.lastIndexOf("/");
		Logger.v("lastindex", "lastindex="+lastindex);
		if(lastindex != -1)
		{
			DirPath = DirPath.substring(0, lastindex);
			Logger.d("Reader", DirPath);
			if(!getDocInfo(DirPath, searchkey))
			{
				return true ;
			}	
			Intent tmpIntent = new Intent(
					MainpageActivity.SET_TITLE);
			Bundle sndbundle = new Bundle();
			sndbundle.putString("title", "我的文档");
			tmpIntent.putExtras(sndbundle);
			sendBroadcast(tmpIntent);
			refresh=true;
			id++;
			setValue();
			return  true ;
		}
		sendBroadcast(new Intent(MainpageActivity.BACK));
		return true ;
	}

    @Override
    public void OnNextpage() {
        // TODO Auto-generated method stub
        Logger.d("MyDoc","nextpage ！");
        nextPage();
    }

    @Override
    public void OnPrevpage() {
        // TODO Auto-generated method stub
        Logger.d("MyDoc","prevpage ！");
        prevPage();
    }
	
	
	
}
