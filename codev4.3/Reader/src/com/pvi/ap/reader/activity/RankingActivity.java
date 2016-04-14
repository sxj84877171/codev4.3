
/**
 * 热门排行
 * @author rd029 晏子凯
 * 
 */
package com.pvi.ap.reader.activity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;

import com.pvi.ap.reader.data.common.Logger;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

//kyle
public class RankingActivity extends PviActivity {
	public static final String TAG = "RankingActivity";

	final private int itemPerPage = 7;
	
    public final static String START_ACTIVITY = "com.pvi.ap.reader.mainframe.START_ACTIVITY";//在主要区域启动一个Activity（是否每次都OnCreate？）
    public final static String SET_TITLE = "com.pvi.ap.reader.mainframe.SET_TITLE";//设置标题栏文字
    public final static String BACK = "com.pvi.ap.reader.mainframe.BACK";//返回上一个子Activity
	private String catalogId = null;
	private String rankType = null;
	private String rankTime = "周";


	
	private TextView week;
	private TextView month;
	private TextView total;
	
    private ImageButton mPrev;
    private ImageButton mNext;
    private View[] primaryBtn = new Button[itemPerPage];
    private ImageView[] primaryImage = new ImageView[itemPerPage];
	private TextView[] primary = new TextView[itemPerPage];
	private TextView[] slave = new TextView[itemPerPage];
	private TextView tvPage;
	private TextView tvPages;


	private String[] dataPrimary = new String[itemPerPage];
	private String[] dataSlave = new String[itemPerPage];
	private String[] dataId = new String[itemPerPage];
	private String[] dataImageURL = new String[itemPerPage];
	//private Bitmap[] dataBitmap = new Bitmap[itemPerPage];
	
	private Intent revIntent = null;
	private Bundle revBundle = null;

	private Handler retryHandler = new Handler() {
		public void handleMessage(Message msg) {
			final PviAlertDialog pd = new PviAlertDialog(getParent());
			pd.setTitle(R.string.kyleHint02);
			pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,int which) {
							onCreate(null);
						}

					});
			pd.setButton(DialogInterface.BUTTON_NEUTRAL, "取消",
					new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							sendBroadcast(new Intent(
									WirelessTabActivity.BACK));
						}

					});
			pd.show();

		};
	};
	
	private int currentPage = 1;
	private int totalPage = 0;

	private Handler mHandler;

	private ImageView selector;
	
	protected static final String LOG_TAG = "RankingActivity";

	private int getData(String catalog,String rankType){	
		String start = String.valueOf((currentPage-1)*itemPerPage+1);
		String count = String.valueOf(itemPerPage);
		String rt = null;
		if(rankTime.equals("日")){
			rt = "1";
		}else if(rankTime.equals("周")){
			rt = "2";
		}else if(rankTime.equals("月")){
			rt = "3";
			
		}else if(rankTime.equals("总")){
			rt = "4";
			
		}else{
			rt = "2";
		}
		String strGSR = SubscribeProcess.network2("getSpecifiedRank",catalogId,rankType,rt,start,count);
		if (strGSR.substring(0, 10).contains("Exception")) {
			return 1;
		}
		try{
			InputStream is = new ByteArrayInputStream(strGSR.substring(20).getBytes());	
			Element rootele = null;
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance(); 
			DocumentBuilder db = dbfactory.newDocumentBuilder();	
			Document dom = db.parse(is);
			rootele = dom.getDocumentElement();
		
			NodeList totalNl = rootele.getElementsByTagName("totalRecordCount");
			int totalRecordCount = Integer.parseInt(totalNl.item(0).getFirstChild().getNodeValue());
			totalPage = totalRecordCount/itemPerPage;
			if(totalRecordCount%itemPerPage != 0){
				totalPage++;
			}

			mHandler.post(new Runnable(){

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    tvPage.setText(String.valueOf(currentPage));
                    tvPages.setText(String.valueOf(totalPage));
                }});
		
			NodeList nl = rootele.getElementsByTagName("RankContent");
			for(int i = 0;i< nl.getLength();i ++){
				Element entry = (Element)nl.item(i);
				NodeList nl2 = entry.getElementsByTagName("contentName");
				dataPrimary[i] = nl2.item(0).getFirstChild().getNodeValue();
				
				NodeList nl3 = entry.getElementsByTagName("authorName");
				dataSlave[i] = nl3.item(0).getFirstChild().getNodeValue();
				
				NodeList nl4 = entry.getElementsByTagName("contentID");
				final String tmpStr = nl4.item(0).getFirstChild().getNodeValue();
				dataId[i] = tmpStr;
	
				
			}
		}catch(Exception e){
		    e.printStackTrace();
			return 2;
		}
		return 0;
	}
	
	private void reBindUI(){
		
		for(int i = 0; i < itemPerPage;i ++){
			primaryBtn[i] = null;
			primary[i] = null;
			slave[i] = null;
		}
		
		
		week = (TextView) findViewById(R.id.week);
		month = (TextView) findViewById(R.id.month);
		total = (TextView) findViewById(R.id.total);
		
		selector = (ImageView) findViewById(R.id.selector);
		
		mPrev = (ImageButton) findViewById(R.id.prev);
		mNext = (ImageButton) findViewById(R.id.next);
		tvPage = (TextView) findViewById(R.id.curpage);
		tvPages = (TextView) findViewById(R.id.pages);
		
		primaryBtn[0] = (Button) findViewById(R.id.primaryBtn01);
		primaryBtn[1] = (Button) findViewById(R.id.primaryBtn02);
		primaryBtn[2] = (Button) findViewById(R.id.primaryBtn03);
		primaryBtn[3] = (Button) findViewById(R.id.primaryBtn04);
		primaryBtn[4] = (Button) findViewById(R.id.primaryBtn05);
		primaryBtn[5] = (Button) findViewById(R.id.primaryBtn06);
		primaryBtn[6] = (Button) findViewById(R.id.primaryBtn07);
		
		primaryImage[0] = (ImageView) findViewById(R.id.primaryImage01);
		primaryImage[1] = (ImageView) findViewById(R.id.primaryImage02);
		primaryImage[2] = (ImageView) findViewById(R.id.primaryImage03);
		primaryImage[3] = (ImageView) findViewById(R.id.primaryImage04);
		primaryImage[4] = (ImageView) findViewById(R.id.primaryImage05);
		primaryImage[5] = (ImageView) findViewById(R.id.primaryImage06);
		primaryImage[6] = (ImageView) findViewById(R.id.primaryImage07);
		
		primary[0] = (TextView) findViewById(R.id.primary01);
		primary[1] = (TextView) findViewById(R.id.primary02);
		primary[2] = (TextView) findViewById(R.id.primary03);
		primary[3] = (TextView) findViewById(R.id.primary04);
		primary[4] = (TextView) findViewById(R.id.primary05);
		primary[5] = (TextView) findViewById(R.id.primary06);
		primary[6] = (TextView) findViewById(R.id.primary07);

		slave[0] = (TextView) findViewById(R.id.slave01);
		slave[1] = (TextView) findViewById(R.id.slave02);
		slave[2] = (TextView) findViewById(R.id.slave03);
		slave[3] = (TextView) findViewById(R.id.slave04);
		slave[4] = (TextView) findViewById(R.id.slave05);
		slave[5] = (TextView) findViewById(R.id.slave06);
		slave[6] = (TextView) findViewById(R.id.slave07);
		
		//setting up item onFocusChangeListener
//		for(int i = 0;i < itemPerPage;i++){
//			final int tmp = i;
//			primaryBtn[tmp].setOnFocusChangeListener(new OnFocusChangeListener(){
//				public void onFocusChange(View V, boolean isFocused){
//					if (isFocused==true){
//						primary[tmp].setTextColor(android.gra.Color.);
//						slave[tmp].setTextColor(android.graphics.Color.);
//					}
//					else{
//						primary[tmp].setTextColor(android.graphics.Color.BLACK);
//						slave[tmp].setTextColor(android.graphics.Color.BLACK);
//					}
//				}
//			});
//		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Logger.d(TAG, "onCreate");
		// TODO Auto-generated method stub
		GlobalVar appState = ((GlobalVar) getApplicationContext());
		this.setContentView(R.layout.ranking);
		
		Intent tmpintent = new Intent(MainpageActivity.SET_TITLE);
		Bundle sndBundle = new Bundle();
		sndBundle.putString("title", "热门排行");
		tmpintent.putExtras(sndBundle);
		sendBroadcast(tmpintent);
		try {
			revIntent = this.getIntent();
			revBundle = revIntent.getExtras();
			if (revBundle != null) {
				rankType = revBundle.getString("rankType");
				catalogId = revBundle.getString("catalogId");	
				Logger.d("rankingactivity rankType", rankType);
				Logger.d("rankingactivity catalogId", catalogId);
			}
		} catch (Exception e) {
			Log.d(LOG_TAG, e.toString());
		}

		{
			if(rankType == null){
				rankType = appState.getrankType();
				catalogId = appState.getrankCatalog();
				
			}
			appState.setrankType(rankType);
			appState.setrankCatalog(catalogId);
		}
		
		if (catalogId == null) {
			catalogId = "0";
		}

		
		mHandler = new Handler(){       
	        public void handleMessage(Message msg) {  

	        	for(int tmpi = 0;tmpi < itemPerPage&&dataPrimary[tmpi] != null;tmpi ++){
	        		final int i = tmpi;
	        		primary[i].setText(dataPrimary[i]);
		        	slave[i].setText(dataSlave[i]);
		        	primaryImage[i].setImageResource(R.drawable.default_bookcover_6080);
		        	primaryBtn[i].setOnClickListener(new OnClickListener() {
					public void onClick(final View v) {
					    Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
					    Bundle bundleToSend = new Bundle();
		                bundleToSend.putString("act","com.pvi.ap.reader.activity.BookSummaryActivity");
		                bundleToSend.putString("haveTitleBar","1");
		                bundleToSend.putString("startType",  "allwaysCreate");
		                bundleToSend.putString("contentID",dataId[i]);
		                bundleToSend.putString("pviapfStatusTip",  "数据加载中，请稍候...");
		                tmpIntent.putExtras(bundleToSend);
		                sendBroadcast(tmpIntent);
				        }
					});				        	
	        	}
	    		if(totalPage <= 1){
	    		    mNext.setVisibility(View.INVISIBLE);
	    		    mPrev.setVisibility(View.INVISIBLE);
	    		}
	        };  
	    };  

		
		this.reBindUI();
		
		super.onCreate(savedInstanceState);
		
		if(rankTime.equals("周")){
			selector.setImageResource(R.drawable.ranking_1);
			
		}
		if(rankTime.equals("月")){
			
			selector.setImageResource(R.drawable.ranking_2);
			
		}
		if(rankTime.equals("总")){
			
			selector.setImageResource(R.drawable.ranking_3);
			
		}
		
		final PviAlertDialog dialog = new PviAlertDialog(getParent());
		dialog.setTitle(getResources().getString(R.string.kyleHint04));
		dialog
				.setMessage(getResources().getString(
						R.string.kyleHint05));
		dialog.show();
		final Handler handler = new Handler() {
		   public void handleMessage(Message msg) {
		      dialog.dismiss();
		      }
		   };
		Thread checkUpdate = new Thread() {
		   public void run() {
			   if(0 ==getData(catalogId, rankType)){
				   mHandler.sendEmptyMessage(0);
				   handler.sendEmptyMessage(0);
			   }else {
					handler.sendEmptyMessage(0);
					retryHandler.sendEmptyMessage(0);
			   }
			
		   }
		};
		checkUpdate.start();
		
		
		
		mPrev.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				if(currentPage == 1){
					return;
				}
				currentPage--;
				onCreate(null);
				return;	
			}
		});
		mNext.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				if(currentPage == totalPage){
					return;
				}
				currentPage++;
				onCreate(null);
				return;
			}
		});
		
		week.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				switchTab("周");
				return;
			}
		});
		
		month.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				switchTab("月");
				return;	
			}
		});		
		
		total.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				switchTab("总");
				return;	
			}
		});	

		//primaryBtn[0].requestFocus();
	}






	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
            //通知框架返回上一个子activty
            sendBroadcast(new Intent(BACK));
            return false;
		}
		if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
			if(rankTime.equals("周")){
				switchTab("总");
				return true;
			}
			if(rankTime.equals("月")){
				switchTab("周");
				return true;
			}
			if(rankTime.equals("总")){
				switchTab("月");
				return true;
			}
		}
		if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
			if(rankTime.equals("周")){
				switchTab("月");
				return true;
			}
			if(rankTime.equals("月")){
				switchTab("总");
				return true;
			}
			if(rankTime.equals("总")){
				switchTab("周");
				return true;
			}
			
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onResume() {
		Logger.d(TAG, "onResume");
		for(int i = 0;i < itemPerPage;i++){
			primary[i].setTextColor(android.graphics.Color.BLACK);
			slave[i].setTextColor(android.graphics.Color.BLACK);
		}
		sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
		//primaryBtn[0].requestFocus();
		super.onResume();	
	}

	private void switchTab(String time){
		rankTime = time;
		currentPage = 1;
		onCreate(null);
	}


	
}
