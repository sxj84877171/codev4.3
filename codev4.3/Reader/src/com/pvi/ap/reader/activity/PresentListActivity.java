package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * 赠送列表
 * @author Fly
 * 
 */

public class PresentListActivity extends PviActivity implements OnClickListener,Pageable {
    private static final String TAG = "PresentListActivity";
    private static int skinID = 1;//皮肤ID
    
    PviBottomBar  pbb;     //引用框架底部工具条
    
    TextView t1,t2,t3,t4,t5,t6;
    
    private RelativeLayout[] presentsLayout = null;
    private TextView[] presents = null;
    private TextView showTitle = null;
    private TextView norecordView;
    
    private String errMsg;
    private int totalCount;
    private ArrayList<String> presentList;
    private ArrayList<String> contentIDList;
    private int selectIndex = -1;
    
    private int pageSize = 10;
    private int pages = 0;
    private int curpage = 0;
    private int type;
    private int oldType;
    
    private Handler mHandler=new H();
    private PviAlertDialog pd = null;
    
    
    public void showMe(){
		
		
		Intent tmpIntent = new Intent(
                MainpageActivity.SHOW_ME);
		Bundle bundleToSend = new Bundle();
        bundleToSend.putString("act", "com.pvi.ap.reader.activity.UserCenterActivity");
        bundleToSend.putString("actTabName", "赠送记录");
        bundleToSend.putString("sender", PresentListActivity.this.getClass().getName());
        tmpIntent.putExtras(bundleToSend);
        sendBroadcast(tmpIntent);
        tmpIntent = null;
        bundleToSend = null;
    
	}
	
    private class H extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
            case 1: //显示数据加载信息
            	//showAlert("正在加载数据");
            	showMessage("正在加载数据...");
                break;
            case 2: //隐藏数据加载信息
            	if(pd != null && pd.isShowing()){
            		pd.dismiss();
            	}
            	hideTip();
                break;
            case 3: //弹出错误框
            	showAlert(errMsg);
                break;
            case 4: //弹出错误框
            	showData();
            	
                break;
            default:
            }
        }
    }
    private void showAlert(String message){
    	
    	pd = new PviAlertDialog(getParent());
        pd.setTitle(getResources().getString(R.string.my_friend_hint));
        pd.setMessage(message);
        pd.setCanClose(true);
        pd.show();
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	oldType = type;
    }
    private void gotoBookSummaryActivit(String message,final String contentId){
    	
    	if(message == null || contentId == null){
    		return;
    	}
    	pd = new PviAlertDialog(getParent());
        pd.setTitle(getResources().getString(R.string.my_friend_hint));
        pd.setMessage(message);
        pd.setCanClose(false);
        pd.setButton(DialogInterface.BUTTON_POSITIVE,
				"进入书籍摘要页",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int which) {
				//跳转至书籍摘要
				Intent sndintent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle sndBundle1 = new Bundle();
				sndBundle1.putString("act",
				"com.pvi.ap.reader.activity.BookSummaryActivity");
				sndBundle1.putString("startType", "allwaysCreate");
				sndBundle1.putString("haveTitleBar", "1");
				sndBundle1.putString("contentID", contentId);
				sndintent.putExtras(sndBundle1);
				sendBroadcast(sndintent);
				pd.dismiss();
			}
		});

		pd.setButton(DialogInterface.BUTTON_NEGATIVE,
				"取消",
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	
    	
    	setContentView(R.layout.presentlist);
    	
		pbb = ((GlobalVar)getApplication()).pbb;
		
    	presentList = new ArrayList<String>();
    	
    	contentIDList = new ArrayList<String>();
    	
    	t1 = (TextView) findViewById(R.id.catalogBtn01);
		t2 = (TextView) findViewById(R.id.catalogBtn02);
		t3 = (TextView) findViewById(R.id.catalogBtn03);
		t4 = (TextView) findViewById(R.id.catalogBtn04);
		t5 = (TextView) findViewById(R.id.catalogBtn05);
		t6 = (TextView) findViewById(R.id.catalogBtn06);
		
		t1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID", "ACT14100");
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
				tmpIntent = null;
				bundleToSend = null;
			}
		});
		t2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID", "ACT14200");
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
				tmpIntent = null;
				bundleToSend = null;
			}
		});
		t3.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID", "ACT14300");
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
				tmpIntent = null;
				bundleToSend = null;
			}
		});
		t4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID", "ACT14400");
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
				tmpIntent = null;
				bundleToSend = null;
			}
		});
		t5.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID", "ACT14500");
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
				tmpIntent = null;
				bundleToSend = null;
			}
		});
		t6.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID", "ACT14600");
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
				tmpIntent = null;
				bundleToSend = null;
			}
		});
    	
    	super.onCreate(savedInstanceState);
    
    }
    
    public void initControls(){
    	super.initControls();
    	showTitle = (TextView) findViewById(R.id.showtitle);
        
        norecordView = (TextView)findViewById(R.id.norecord);
        
    	presents = new TextView[pageSize];
    	presents[0] = (TextView)findViewById(R.id.ticketlist01);
    	presents[1] = (TextView)findViewById(R.id.ticketlist02);
    	presents[2] = (TextView)findViewById(R.id.ticketlist03);
    	presents[3] = (TextView)findViewById(R.id.ticketlist04);
    	presents[4] = (TextView)findViewById(R.id.ticketlist05);
    	presents[5] = (TextView)findViewById(R.id.ticketlist06);
    	presents[6] = (TextView)findViewById(R.id.ticketlist07);
    	presents[7] = (TextView)findViewById(R.id.ticketlist08);
    	presents[8] = (TextView)findViewById(R.id.ticketlist09);
    	presents[9] = (TextView)findViewById(R.id.ticketlist10);
    	
    	presentsLayout = new RelativeLayout[pageSize];
    	presentsLayout[0] = (RelativeLayout)findViewById(R.id.r1);
    	presentsLayout[1] = (RelativeLayout)findViewById(R.id.r2);
    	presentsLayout[2] = (RelativeLayout)findViewById(R.id.r3);
    	presentsLayout[3] = (RelativeLayout)findViewById(R.id.r4);
    	presentsLayout[4] = (RelativeLayout)findViewById(R.id.r5);
    	presentsLayout[5] = (RelativeLayout)findViewById(R.id.r6);
    	presentsLayout[6] = (RelativeLayout)findViewById(R.id.r7);
    	presentsLayout[7] = (RelativeLayout)findViewById(R.id.r8);
    	presentsLayout[8] = (RelativeLayout)findViewById(R.id.r9);
    	presentsLayout[9] = (RelativeLayout)findViewById(R.id.r10);
    	
    	 if(deviceType==1){
//    		 t1.invalidate(0, 0, 600,800,UPDATEMODE_4);
		 }
    }
    
    public void bindEvent(){
    	super.bindEvent();
    	
        for(int i=0;i<pageSize;i++){
        	final int index = i;
        	presentsLayout[i].setOnClickListener(this);
        }
        
        
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(R.id.next == v.getId()){
			
			nextPage();
			if(deviceType == 1);
//			t1.invalidate(0, 0, 600,800,UPDATEMODE_4);
			
			
		}else if(R.id.prev == v.getId()){
			
			prePage();
			if(deviceType == 1);
//			t1.invalidate(0, 0, 600,800,UPDATEMODE_4);
			
			
		}else if(R.id.r1 == v.getId()){
			if(presentList.size() > 0){
				gotoBookSummaryActivit(presentList.get(0), contentIDList.get(0));
			}
		}else if(R.id.r2 == v.getId()){
			if(presentList.size() > 1){
				gotoBookSummaryActivit(presentList.get(1), contentIDList.get(1));
			}
			
		}else if(R.id.r3 == v.getId()){
			if(presentList.size() > 2){
				gotoBookSummaryActivit(presentList.get(2), contentIDList.get(2));
			}
			
		}else if(R.id.r4 == v.getId()){
			if(presentList.size() > 3){
				gotoBookSummaryActivit(presentList.get(3), contentIDList.get(3));
			}
			
		}else if(R.id.r5 == v.getId()){
			if(presentList.size() > 4){
				gotoBookSummaryActivit(presentList.get(4), contentIDList.get(4));
			}
			
		}else if(R.id.r6 == v.getId()){
			if(presentList.size() > 5){
				gotoBookSummaryActivit(presentList.get(5), contentIDList.get(5));
			}
			
		}else if(R.id.r7 == v.getId()){
			if(presentList.size() > 6){
				gotoBookSummaryActivit(presentList.get(6), contentIDList.get(6));
			}
			
		}else if(R.id.r8 == v.getId()){
			if(presentList.size() > 7){
				gotoBookSummaryActivit(presentList.get(7), contentIDList.get(7));
			}
			
		}else if(R.id.r9 == v.getId()){
			if(presentList.size() > 8){
				gotoBookSummaryActivit(presentList.get(8), contentIDList.get(8));
			}
			
		}else if(R.id.r10 == v.getId()){
			if(presentList.size() > 9){
				gotoBookSummaryActivit(presentList.get(9), contentIDList.get(9));
			}
			
		}
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Bundle bd = getIntent().getExtras();
		type = bd.getInt("type");
		initPage();
	}
	
	public void showData(){
		
		if(type == 1){
			showTitle.setText("图书赠送列表");
		}else if(type == 2){
			showTitle.setText("图书获赠列表");
		}
		
		
		updatePagerinfo((curpage+1)+" / "+(pages==0?1:pages));

		
        if(presentList!=null && presentList.size()!=0){
        	norecordView.setVisibility(View.GONE);
        	for(int i=0;i<presentList.size();i++){
        		presentsLayout[i].setVisibility(View.VISIBLE);
        		//presents[i].setText(presentList.get(i));
        		//2011年3月10日：13466320958赠送图书《迷失界限的旅途》给我
        		//2011年5月6日：我赠送图书《谜踪之国④幽潜重泉》给15994806532
        		String str = presentList.get(i);
        		str = str.replace("《", " <<");
        		str = str.replace("》", ">> ");
        		presents[i].setText(str);
        	}
        	for(int i=presentList.size();i<pageSize;i++){
        		presentsLayout[i].setVisibility(View.GONE);
        		presents[i].setText("");
        	}
        }else{
        	norecordView.setVisibility(View.VISIBLE);
        	for(int i=0;i<pageSize;i++){
        		presents[i].setText("");
        		presentsLayout[i].setVisibility(View.GONE);
        	}
        }
        
        showMe();

	}
	public void initPage(){
		pages = totalCount%pageSize == 0?totalCount/pageSize:totalCount/pageSize+1;
		curpage = 0;
		getMyticketList(curpage);
	}
	
	public void nextPage(){
		if(curpage + 1 < pages){
			curpage = curpage + 1;
			getMyticketList(curpage);
			
		}
		
	}
	public void prePage(){
		if(curpage - 1 >= 0){
			curpage = curpage - 1;
			getMyticketList(curpage);
	
		}
	}
	
	  //获取我的书券列表的某一页数据
    public void getMyticketList(final int page){
    	
    	mHandler.sendEmptyMessage(1);
    	
    	new Thread(){
    		public void run() {
    			
    	    	if(presentList!=null){
    	    		presentList.clear();
    	    	}
    	    	if(contentIDList!=null){
    	    		contentIDList.clear();
    	    	}
  
    	    	HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
    	        HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
    	        ahmNamePair.put("start", String.valueOf(page * pageSize));
    	        ahmNamePair.put("count", String.valueOf(pageSize));
    	        if(type!=0){
    	        	ahmNamePair.put("type", String.valueOf(type));
    	        }else{
    	        	ahmNamePair.put("type", String.valueOf(oldType));
    	        }
    	        
    	        HashMap responseMap = null;
    	          try {
    	         
    	              responseMap = CPManager.getPresentBookList(ahmHeaderMap,
    	                      ahmNamePair);
    	     
    	              if (responseMap!=null&&responseMap.get("result-code")!=null&&!responseMap.get("result-code").toString().contains(
    	                      "result-code: 0")) {
    	            	  errMsg = Error.getErrorDescriptionForContent(responseMap.get("result-code").toString());
    	            	  if("返回错误码：".equals(errMsg)){
    	            		  errMsg = "返回错误码为空";
    	            	  }
    	            	  mHandler.sendEmptyMessage(3);
    	                  return;
    	              }
    	              mHandler.sendEmptyMessage(2);
    	          } catch (HttpException e) {
    	        	  errMsg = "网络错误";
    	        	  mHandler.sendEmptyMessage(3);
    	        	  Logger.i(TAG, e);
    	              return;
    	          } catch (SocketTimeoutException e) {
    	        	  errMsg = "网络超时";
    	        	  mHandler.sendEmptyMessage(3);
    	        	  Logger.i(TAG, e);
    	              return;
    	          } catch (IOException e) {
    	        	  errMsg = "网络错误";
    	        	  mHandler.sendEmptyMessage(3);
    	        	  Logger.i(TAG, e);
    	              return;
    	          }


    	          byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
    	          try {
    				System.out.println(CPManagerUtil.getStringFrombyteArray(responseBody));
    	          } catch (UnsupportedEncodingException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			}

    	          // 根据返回字节数组构造DOM
    	          Document dom = null;
    	          try {
    	              dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
    	          } catch (ParserConfigurationException e) {
    	        	  errMsg = "解释XML错误";
    	        	  mHandler.sendEmptyMessage(3);
    	        	  Logger.i(TAG, e);
    	              return;
    	          } catch (SAXException e) {
    	        	  errMsg = "解释XML错误";
    	        	  mHandler.sendEmptyMessage(3);
    	        	  Logger.i(TAG, e);
    	              return;
    	          } catch (IOException e) {
    	        	  errMsg = "解释XML错误";
    	        	  mHandler.sendEmptyMessage(3);
    	        	  Logger.i(TAG, e);
    	              return;
    	          }
    	          
    	          Element root = dom.getDocumentElement();
    	         
    	          NodeList countList = root.getElementsByTagName("totalRecordCount");
    	          totalCount = Integer.parseInt(countList.item(0).getFirstChild().getNodeValue());
    	          pages = totalCount%pageSize == 0?totalCount/pageSize:totalCount/pageSize+1;
    	          NodeList nl = root.getElementsByTagName("PresentBookInfo");
    	  		  Element friendtemp = null;
    	  		  NodeList friendinfotemp = null;
    	  		  HashMap<String, Object> map = null;
    	  		  if(nl!=null){
    		  		  for (int i = 0; i < nl.getLength(); i++) {
    		  			//map = new HashMap<String, Object>();
    		  			//friendtemp = (Element) nl.item(i);
    		  			//friendinfotemp = friendtemp.getElementsByTagName("ticketInfo");
    		  			NodeList childNl = nl.item(i).getChildNodes();
    		  			for (int j = 0; j < childNl.getLength(); j++){
  
    		  				
    		  				String tag = childNl.item(j).getNodeName();
    		  				if("presentDesc".equals(tag)){
    		  					String str = "";
    		  					if(childNl.item(j).getFirstChild() != null){
    		  						str = childNl.item(j).getFirstChild().getNodeValue();
    		  					}
    		  					presentList.add(str);
    		  				}else if("contentId".equals(tag)){
    		  					String str = "";
    		  					if(childNl.item(j).getFirstChild() != null){
    		  						str = childNl.item(j).getFirstChild().getNodeValue();
    		  					}
    		  					contentIDList.add(str);
    		  				}
    		  			}
    		  		}
    	  		  }
    	  		  mHandler.sendEmptyMessage(4);
    	  		  
    		};
    	}.start();
    }
    
    
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
        	//按确定键
    		if(presentList.size() > selectIndex){
				gotoBookSummaryActivit(presentList.get(selectIndex), contentIDList.get(selectIndex));
			}
        }
        return super.onKeyUp(keyCode, event);
    }
  
    
}
