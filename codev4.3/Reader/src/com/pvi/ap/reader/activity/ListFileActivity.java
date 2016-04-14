package com.pvi.ap.reader.activity;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.external.meb.Chapters;
import com.pvi.ap.reader.external.meb.MebInterfaceImpl;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;

/**
 * MEB目录页
 * @author rd037
 *
 */
public class ListFileActivity extends PviActivity{
	private final static String TAG="ListFileActivity";
	private String certPath=null;
	private String filePath=null;
	private String contentID=null;
	private static List<Chapters> fileList=null;
	private MebInterfaceImpl mifImpl;
	private Bundle bundle;
	private int page=1;
	private int maxPage=1;
	private PopupWindow popmenu;
	//private TextView[] chapters2 = new TextView[pageNum];
	private Handler mHandler  = null ;
	private PviAlertDialog pd = null ;
	private static int pageNum = 10;
	private List<Chapters> curList = null ;
	private TextView  tView;
	private int skin = 0 ;
	private int skinID = 0 ;
	ListFileActivity m_context = null;
	
	PviDataList m_listView;
	ArrayList<PviUiItem[]> m_list; 
	PviBottomBar  m_pbb;
	
	private String downloadType = "";// 请参照downloadType的说明文档
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			m_context = this;
			GlobalVar appState = (GlobalVar)getApplicationContext();
			skin = 1;// appState.getSkinID();
			switch(skin){
			//case 2:skinID = R.layout.filelist ;pageNum=10;break ;
			case 1:skinID = R.layout.filelist2  ;pageNum=8;break ;
			default:skinID = R.layout.filelist2 ;pageNum=8;break ;
			}
			setContentView(skinID);
			super.onCreate(savedInstanceState);
			
			m_listView= (PviDataList)findViewById(R.id.list);
			m_list = new ArrayList<PviUiItem[]>();
			m_listView.setOnKeyListener(onKeyListener);
	        m_pbb = appState.pbb;
//			m_pbb.setPageable(this);
//			m_pbb.setItemVisible("prevpage", true);
//			m_pbb.setItemVisible("pagerinfo", true);
//			m_pbb.setItemVisible("nextpage", true);
	        this.showPager = true;
	        //bindEvent();
			mHandler = new Handler(){
				public void handleMessage(Message msg) {
					if(msg.what == 1) {
						if(fileList == null || fileList.size() == 0){
							pd.dismiss();
							Intent intent1 = new Intent(
									MainpageActivity.BACK);
							sendBroadcast(intent1);
							return ;
						}
						getFileList(page);
//						if(skinID==R.layout.filelist){
//			
//							tView.setText( page+"/"+maxPage);
//							chapters[0].requestFocus();
//						}else {
							updatePagerinfo(page+" / "+maxPage);

							
//						}
					//	pd.dismiss();
						mHandler.sendEmptyMessageDelayed(2,500);
					}else if(msg.what == 2) {
						//\\chapterlayout[0].requestFocus();
					}
				}
			};
			pd = new PviAlertDialog(getParent());
			pd.setTitle("温馨提示");
			pd.setMessage("正在获取书籍目录信息，请稍候...");
			pd.setHaveProgressBar(true);
			//pd.show();
				
//			}
			
		}  catch (OutOfMemoryError e) {
			Logger.d("List", e.getMessage());
			Toast.makeText(ListFileActivity.this, getResources().getString(R.string.deadException), Toast.LENGTH_LONG).show();
			Intent intent1 = new Intent(
					MainpageActivity.BACK);
			sendBroadcast(intent1);
			return ;
		}
		catch (Exception e) {
			Logger.d("List", e.getMessage());
			Toast.makeText(ListFileActivity.this, getResources().getString(R.string.deadException), Toast.LENGTH_LONG).show();
			Intent intent1 = new Intent(
					MainpageActivity.BACK);
			sendBroadcast(intent1);
			return ;
		}
		
	}
	@Override
    public void OnNextpage() {
     //   nextPage();   //你的“下一页”逻辑
		page=page+1;
		if(page>=maxPage){
			page=maxPage;
		}
		for(int i=0;i<pageNum;i++){
			//\\chapters[i].setText("");
		}
		getFileList(page);
		updatePagerinfo(page+" / "+maxPage);
    }

    @Override
    public void OnPrevpage() {
     //   prevPage();
		page=page-1;
		if(page<=1){
			page=1;
		}
		getFileList(page);
		updatePagerinfo(page+" / "+maxPage);

    }
	
	private void setUIData() {
		new Thread() {
			public void run() {
				initFileList();
				
				if(fileList != null && fileList.size() > 0){
					setTitile(fileList.get(0).getMebName());
				}
				mHandler. sendEmptyMessage (1);
			}
		}.start();
				
		
	}
	
	private void setTitile(String bookname){
		Intent intent = new Intent(MainpageActivity.SET_TITLE);
		Bundle sndBundle = new Bundle();
		sndBundle.putString("title", bookname);
		intent.putExtras(sndBundle);
		sendBroadcast(intent);
	}
	
	private void initFileList()
	{
			bundle=this.getIntent().getExtras();
			filePath = bundle.getString("FilePath");
			certPath=bundle.getString("CertPath");
			contentID=bundle.getString("ContentId");
			long TimeStart = System.currentTimeMillis();
			Logger.i("Time","Mainpage Pressed" + Long.toString(TimeStart));
			mifImpl=new MebInterfaceImpl();
			try {
				fileList=mifImpl.openMebFile(filePath);
			}catch (OutOfMemoryError e) {
				// TODO: handle exception
				Logger.d("List", "OutOfMemoryError");
				Intent intent1 = new Intent(
						MainpageActivity.BACK);
				sendBroadcast(intent1);
				return ;
			} 
			catch (Exception e) {
				// TODO: handle exception
				Logger.d("List", e.getMessage());
				Intent intent1 = new Intent(
						MainpageActivity.BACK);
				sendBroadcast(intent1);
				return ;
			}
			
			TimeStart = System.currentTimeMillis();
			Logger.i("Time","Mainpage Pressed" + Long.toString(TimeStart));
			if(fileList.size()%pageNum==0){
				maxPage=fileList.size()/pageNum;
			}else {
				maxPage=fileList.size()/pageNum+1;
			}
	}
	boolean m_turn_off_drawing = false;
	OnPreDrawListener m_predraw_listener = new OnPreDrawListener() {
		   @Override
		   public boolean onPreDraw() {
		    // TODO Auto-generated method stub
			   if(m_turn_off_drawing) {
				//   Log.e(TAG,"onPreDraw called, drawing canceled");
				   return false;
			   }else {
				//   Log.e(TAG,"onPreDraw called, drawing confirmed");
				   return true;
			   }
		   }
		  };
	private Handler  screen_update_handle = new Handler() {
              @Override
              public void handleMessage(Message msg) {
            	  if(msg.what == 1) {
            		 // ((ViewRoot)(m_context.getWindow().getDecorView().getRootView())).scheduleTraversals();
            		  //如果没有下面一行,将不会刷屏,因此,可以暂时关闭刷屏
            		//  m_context.getWindow().getDecorView().getRootView().getViewTreeObserver().removeOnPreDrawListener(m_predraw_listener);
            		  m_turn_off_drawing = false;
            		  GlobalVar appState = (GlobalVar)getApplicationContext();
            		  if(appState.deviceType==1){
//            			  m_context.getWindow().getDecorView().getRootView().invalidate(UPDATEMODE_4);
            		  }else {
            			  m_context.getWindow().getDecorView().getRootView().invalidate();
            		  }
            		  //Log.e(TAG,"invalidate(UPDATEMODE_4)");
            	  }
              }
          };

    private void fill_list() {
		m_list.clear();
		for (int i = 0; i < curList.size(); i++) {
			int x = 2; int y = 10; int width = 596; int height = 60;
			PviUiItem[] items = new PviUiItem[]{
					new PviUiItem("item_"+i, 0, x, y, width, height, curList.get(i).getFileUrl(), true, true, null),
				//	new PviUiItem("item_"+i + 1000, 0, 0, y, 1, height, null, false, false, null),
			};
			final int ii=i;
			OnClickListener l = new OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                	if(ii< curList.size()){
						//Logger.v("string", chapters[ii].getText());
						Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
						Bundle sndbundle = new Bundle();
						sndbundle.putString("pviapfStatusTip", "进入书籍");
						msgIntent.putExtras(sndbundle);
						sendBroadcast(msgIntent);
						setEvent(ii);
					}
                }
                
            };				
            items[0].l = l;
			m_list.add(items);
		}
		m_listView.setData(m_list);
    	
    }
	private void getFileList(int page){
		List<Chapters> list=null;
		if(page*pageNum<fileList.size()){
			list=fileList.subList((page-1)*pageNum, page*pageNum);
		}else{
			list=fileList.subList((page-1)*pageNum,fileList.size());
		}
		curList = list ;
		
		Logger.i(TAG,"turn off drawing");
		m_turn_off_drawing = true;
	//	this.getWindow().getDecorView().getRootView().getViewTreeObserver().addOnPreDrawListener(m_predraw_listener); 
		
		fill_list();
		Logger.i(TAG,"turn on drawing");

		
		//screen_update_handle.sendEmptyMessage(1);
		
		showMe();
		sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
	}
   /**
    * 单击事件
    * @param k
    */
	public void setEvent(int k){
		if(k< curList.size()){
		Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
    	bundle.putString("pviapfStatusTip", getResources().getString(R.string.waitting));
    	bundle.putString("act","com.pvi.ap.reader.activity.MebViewFileActivity");
    	bundle.putString("haveStatusBar","1");
    	bundle.putString("startType",  "allwaysCreate");
		bundle.putString("FilePath",filePath);
		bundle.putString("ChapterId",((page-1)*pageNum+k+1)+"");
		bundle.putString("downloadType", downloadType);
		//Logger.v("k", k);
		//Logger.v("size",curList.size());
		bundle.putString("flag", curList.get(k).getFlag());
		
		bundle.putString("ContentId", contentID);
//		bundle.putString("fileUrl", curList.get(k).getFileUrl());
//		bundle.putString("mebName", curList.get(k).getMebName());
//		bundle.putInt("startFile", curList.get(k).getStartFile());
//		bundle.putInt("fileLength", curList.get(k).getFileLength());
//		bundle.putInt("billingflag", curList.get(k).getBillingflag());
		bundle.putString("FromPath", "0");
		bundle.putString("CertPath", certPath);
		
		tmpIntent.putExtras(bundle);
		sendBroadcast(tmpIntent);
		}
	}
	public void showMe(){
		Intent tmpIntent = new Intent(
                MainpageActivity.SHOW_ME);
        Bundle bundleToSend = new Bundle();
      
        bundleToSend.putString("sender", ListFileActivity.this.getClass().getName()); //TAB内嵌activity类的全名
        tmpIntent.putExtras(bundleToSend);
        sendBroadcast(tmpIntent);
        tmpIntent = null;
        bundleToSend = null;
	}
	@Override
	protected void onResume() {
		long TimeStart = System.currentTimeMillis();
		Logger.i("Time","Mainpage Pressed" + Long.toString(TimeStart));
		super.onResume();
		//EPDRefresh.refreshGCOnceFlash();
		
		final Bundle bunde = getIntent().getExtras();
		if(bunde!=null){
		    Logger.d(TAG,"downloadType:"+downloadType);
		    downloadType=bunde.getString("downloadType");
		}else{
		    Logger.d(TAG,"downloadType is empty:"+downloadType);
		}
		
		try{
			setUIData();
			//showMe();
		}catch (OutOfMemoryError e) {
			// TODO: handle exception
			Logger.d("List", "OutOfMemoryError");
			Intent intent1 = new Intent(
					MainpageActivity.BACK);
			sendBroadcast(intent1);
			return ;
		} 
		
	}
	public void bindEvent(){	
		super.bindEvent();
		if(skinID==R.layout.filelist2){

		}else {
		}
		
	}
	
	 
    private View.OnClickListener chapteritemclicklistener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		try {
			int	chapteritemviewid = v.getId();
			String mebString = new String("");
			int id = 0;
			if(skinID==R.layout.filelist2){
				
				switch(chapteritemviewid)
				{
					case R.id.chapter01layout:
						id=1;
						break;
					case R.id.chapter02layout:
						id=2;
						break;
					case R.id.chapter03layout:
						id=3;
						break;
					case R.id.chapter04layout:
						id=4;
						break;
					case R.id.chapter05layout:
						id=5;
						break;
					case R.id.chapter06layout:
						id=6;
						break;
					case R.id.chapter07layout:
						id=7;
						break;
					case R.id.chapter08layout:
						id=8;
						break;	
				}
				//mebString=chapters[id-1].getText().toString().trim();
				if(mebString!=null&&!mebString.equals("")){
					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
		        	bundle.putString("pviapfStatusTip", getResources().getString(R.string.waitting));
		        	bundle.putString("act","com.pvi.ap.reader.activity.MebViewFileActivity");
		        	bundle.putString("haveStatusBar","1");
		        	bundle.putString("startType",  "allwaysCreate");
		    		bundle.putString("FilePath",filePath);
		    		
		    		bundle.putString("ChapterId", String.valueOf((page-1)*7+id));
		    		bundle.putString("FromPath", "0");
		    		bundle.putString("CertPath", certPath);
		    		bundle.putString("ContentId", bundle.getString("ContentId"));
		    		
		    		bundle.putString("downloadType", downloadType);
		    		
		    		tmpIntent.putExtras(bundle);
		    		sendBroadcast(tmpIntent);
				}else {
					return;
				}
			}else {
				switch(chapteritemviewid)
				{
					case R.id.chapter01:
						id=1;
						break;
					case R.id.chapter02:
						id=2;
						break;
					case R.id.chapter03:
						id=3;
						break;
					case R.id.chapter04:
						id=4;
						break;
					case R.id.chapter05:
						id=5;
						break;
					case R.id.chapter06:
						id=6;
						break;
					case R.id.chapter07:
						id=7;
						break;
					case R.id.chapter08:
						id=8;
						break;
				}
				//mebString=chapters[id-1].getText().toString().trim();
				if(mebString!=null&&!mebString.equals("")){
					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
		        	bundle.putString("pviapfStatusTip", getResources().getString(R.string.waitting));
		        	bundle.putString("act","com.pvi.ap.reader.activity.MebViewFileActivity");
		        	bundle.putString("haveStatusBar","1");
		        	bundle.putString("startType",  "allwaysCreate");
		    		bundle.putString("FilePath",filePath);
		    		bundle.putString("ChapterId", String.valueOf((page-1)*pageNum+id));
		    		bundle.putString("FromPath", "0");
		    		bundle.putString("CertPath", certPath);
		    		bundle.putString("ContentId", bundle.getString("ContentId"));
		    		
		    		bundle.putString("downloadType", downloadType);
		    		
		    		tmpIntent.putExtras(bundle);
		    		sendBroadcast(tmpIntent);
				}else {
					return;
				}
			}
		
			
			} catch (Exception e) {
				Logger.e("ListFileActivity", e.toString());
			}
		}
    };
    
    private View.OnKeyListener onKeyListener=new OnKeyListener() {    
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
                sendBroadcast(new Intent(MainpageActivity.BACK));
                return true;
            }
		}
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
            
			if ((keyCode == event.KEYCODE_ENTER)||(keyCode == event.KEYCODE_DPAD_CENTER)) {

				setEvent(m_listView.mCurFoucsRow);
				return true;
			}
			
			if (keyCode == event.KEYCODE_DPAD_LEFT) {

				OnPrevpage();
				return true;
			}

			if (keyCode == event.KEYCODE_DPAD_RIGHT) {

				OnNextpage();
				return true;
			}
			
			
		}            

		return false;
	}
    };

  private View.OnKeyListener onKeyListener_not_used=new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if(event.getAction()==KeyEvent.ACTION_DOWN)
			{
				
				if (keyCode == event.KEYCODE_ENTER ) {
						if(!v.isClickable()) {
							return true;
						}

						int idx = 0;
						switch (v.getId()) {
						case R.id.chapter01layout:
							idx = 0;
							bundle.putString("ChapterId", String.valueOf((page-1)*pageNum+1));
							break;
						case R.id.chapter02layout:
							idx = 1;
							bundle.putString("ChapterId", String.valueOf((page-1)*pageNum+2));
							break;
						case R.id.chapter03layout:
							idx = 2;
							bundle.putString("ChapterId", String.valueOf((page-1)*pageNum+3));
							break;
						case R.id.chapter04layout:
							idx = 3;
							bundle.putString("ChapterId", String.valueOf((page-1)*pageNum+4));
							break;
						case R.id.chapter05layout:
							idx = 4;
							bundle.putString("ChapterId", String.valueOf((page-1)*pageNum+5));
							break;
						case R.id.chapter06layout:
							idx = 5;
							bundle.putString("ChapterId", String.valueOf((page-1)*pageNum+6));
							break;
						case R.id.chapter07layout:
							idx = 6;
							bundle.putString("ChapterId", String.valueOf((page-1)*pageNum+7));
							break;
						case R.id.chapter08layout:
							idx = 7;
							bundle.putString("ChapterId", String.valueOf((page-1)*pageNum+8));
							break;	
					}
					if(idx >= curList.size()) {
						return true;
					}
					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
					bundle.putString("pviapfStatusTip", getResources().getString(R.string.waitting));
		        	bundle.putString("act","com.pvi.ap.reader.activity.MebViewFileActivity");
		        	bundle.putString("haveStatusBar","1");
		        	bundle.putString("startType",  "allwaysCreate");
		    		bundle.putString("FilePath",filePath);
		    		bundle.putString("FromPath", "0");
		    		bundle.putString("CertPath", certPath);
		    		bundle.putString("ContentId", bundle.getString("ContentId"));
		    		bundle.putString("downloadType", downloadType);
		    		tmpIntent.putExtras(bundle);
		    		sendBroadcast(tmpIntent);
					return true ;
					
					
				}
				if (keyCode == event.KEYCODE_DPAD_LEFT) {
					page=page-1;
					if(page<=1){
						page=1;
					}
					for(int i=0;i<pageNum;i++){
						//\\chapters[i].setText("");
					}
					if(skinID==R.layout.filelist2){
						updatePagerinfo(page+" / "+maxPage);

					}else {
						tView.setText( page+"/"+maxPage);
					}
					getFileList(page);
					//totalpagetxt.setText("" + maxPage);
					return true;
				}
				if (keyCode == event.KEYCODE_DPAD_RIGHT) {
					page=page+1;
					if(page>=maxPage){
						page=maxPage;
					}
					for(int i=0;i<pageNum;i++){
						//\\chapters[i].setText("");
					}
					if(skinID==R.layout.filelist2){
						updatePagerinfo(page+" / "+maxPage);
					}else {
						tView.setText( page+"/"+maxPage);
					}
					getFileList(page);
					//totalpagetxt.setText("" + maxPage);
					return true;
				}
				if(keyCode==event.KEYCODE_DPAD_UP)
				{
					if(skinID==R.layout.filelist2){
						switch (v.getId()) {
					  }
					}else {
						switch (v.getId()) {
					
					  }
					}
					
					
				}
				if(keyCode==event.KEYCODE_DPAD_DOWN)
				{
					if(skinID==R.layout.filelist2){
						switch (v.getId()) {
							//break;	
					  }
					}else {
						switch (v.getId()) {
					  }
					}
					
				}
			}
			if (event.getAction() == KeyEvent.ACTION_UP) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
	                sendBroadcast(new Intent(MainpageActivity.BACK));
	            }
			}
			
			
			return true;
		}
  };
  	@Override
	protected void onPause() {
	// TODO Auto-generated method stub
  	if (popmenu != null && popmenu.isShowing()) {
			popmenu.dismiss();
		}
	super.onPause();
  	}

}