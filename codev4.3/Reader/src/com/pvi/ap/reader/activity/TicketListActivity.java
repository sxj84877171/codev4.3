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

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * 书券列表
 * @author Fly
 * 
 */

public class TicketListActivity extends PviActivity implements OnClickListener {
    private static final String TAG = "TicketListActivity";
    private static int skinID = 1;//皮肤ID
    
    
    TextView t1,t2,t3,t4,t5,t6;
    
    private TextView norecordView;
    private TextView ticketInfoTextView;
    private TextView[] tickets = new TextView[7];
    private RelativeLayout[] rLayout = new RelativeLayout[7];
    
    private Button returnButton;
    
    private TextView mtv_CurPage = null;
    private TextView mtv_Pages = null;
    private View mtv_Prev = null;
    private View mtv_Next = null;
    
    private String errMsg;
    private int totalCount;
    private String ticketInfo;
    private ArrayList<String> ticketList;
    
    private int pageSize = 7;
    private int pages = 0;
    private int curpage = 0;
    
    private Handler mHandler=new H();
    private PviAlertDialog pd = null;
	
    private class H extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
            case 1: //显示数据加载信息
            	//showAlert("正在加载数据");
            	showMessage("正在加载数据");
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
                
            case 4: //加载完数据后显示数据
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
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	
    	
    	setContentView(R.layout.ticketlist);
    	
    	super.onCreate(savedInstanceState);
    	
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
    	
    	initControls();
    	bindEvent();
    }
    
    public void initControls(){
    	super.initControls();
    	ticketInfoTextView = (TextView)findViewById(R.id.ticketinfo);
    	norecordView = (TextView)findViewById(R.id.norecord);
    	returnButton = (Button)findViewById(R.id.returnB);
    	mtv_CurPage = (TextView) findViewById(R.id.curpage);
        mtv_Pages = (TextView) findViewById(R.id.pages);
        mtv_Prev = findViewById(R.id.prev);
        mtv_Next = findViewById(R.id.next);
    	tickets[0] = (TextView)findViewById(R.id.ticketlist01);
    	tickets[1] = (TextView)findViewById(R.id.ticketlist02);
    	tickets[2] = (TextView)findViewById(R.id.ticketlist03);
    	tickets[3] = (TextView)findViewById(R.id.ticketlist04);
    	tickets[4] = (TextView)findViewById(R.id.ticketlist05);
    	tickets[5] = (TextView)findViewById(R.id.ticketlist06);
    	tickets[6] = (TextView)findViewById(R.id.ticketlist07);
    	
    	rLayout[0] = (RelativeLayout)findViewById(R.id.r1);
    	rLayout[1] = (RelativeLayout)findViewById(R.id.r2);
    	rLayout[2] = (RelativeLayout)findViewById(R.id.r3);
    	rLayout[3] = (RelativeLayout)findViewById(R.id.r4);
    	rLayout[4] = (RelativeLayout)findViewById(R.id.r5);
    	rLayout[5] = (RelativeLayout)findViewById(R.id.r6);
    	rLayout[6] = (RelativeLayout)findViewById(R.id.r7);
    	
    	if(deviceType==1){
//    		t1.invalidate(0, 0, 600,800,UPDATEMODE_4);
		}
    }
    
    public void bindEvent(){
    	super.bindEvent();
    	returnButton.setOnClickListener(this);
        mtv_Prev.setOnClickListener(this);
        mtv_Next.setOnClickListener(this);
    	
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(R.id.returnB == v.getId()){
			
			//sendBroadcast(new Intent(MainpageActivity.BACK));
			
			Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
			Bundle bundleToSend = new Bundle();
			bundleToSend.putString("actID", "ACT14300");
			bundleToSend.putString("pviapfStatusTip", "正在进入消费记录...");
			bundleToSend.putString("startType", "allwaysCreate");
			tmpIntent.putExtras(bundleToSend);
			sendBroadcast(tmpIntent);
			
		}else if(R.id.next == v.getId()){
			
			nextPage();
			
		}else if(R.id.prev == v.getId()){
			
			prePage();
			
		}
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Bundle bd = getIntent().getExtras();
		totalCount = bd.getInt("totalCount");
		ticketInfo = bd.getString("ticketInfo");
		ticketList = bd.getStringArrayList("ticketList");
		
		initPage();
	}
	
	public void showMe(){
		
		
		Intent tmpIntent = new Intent(
                MainpageActivity.SHOW_ME);
		Bundle bundleToSend = new Bundle();
        bundleToSend.putString("act", "com.pvi.ap.reader.activity.UserCenterActivity");
        bundleToSend.putString("actTabName", "书券列表");
        bundleToSend.putString("sender", TicketListActivity.this.getClass().getName());
        tmpIntent.putExtras(bundleToSend);
        sendBroadcast(tmpIntent);
        tmpIntent = null;
        bundleToSend = null;
        
        sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
    
	}

	public void showData(){
		ticketInfoTextView.setText(ticketInfo);
		mtv_CurPage.setText(String.valueOf(curpage+1));
        mtv_Pages.setText(String.valueOf(pages));
        if(ticketList!=null && ticketList.size()!=0){
        	norecordView.setVisibility(View.GONE);
        	for(int i=0;i<ticketList.size();i++){
        		rLayout[i].setVisibility(View.VISIBLE);
        		tickets[i].setText(ticketList.get(i));
        	}
        	for(int i=ticketList.size();i<pageSize;i++){
        		rLayout[i].setVisibility(View.GONE);
        		tickets[i].setText("");
        	}
        }else{
        	norecordView.setVisibility(View.VISIBLE);
        	for(int i=0;i<pageSize;i++){
        		rLayout[i].setVisibility(View.GONE);
        		tickets[i].setText("");
        	}
        }
        showMe();

	}
	public void initPage(){
		pages = totalCount%pageSize == 0?totalCount/pageSize:totalCount/pageSize+1;
		curpage = 0;
		showData();
	}
	
	public void nextPage(){
		if(curpage + 1 < pages){
			curpage = curpage + 1;
			getMyticketList(curpage);
			if(deviceType==1){
//	    		t1.invalidate(0, 0, 600,800,UPDATEMODE_4);
			}
			//showData();
		}
		
	}
	public void prePage(){
		if(curpage - 1 >= 0){
			curpage = curpage - 1;
			getMyticketList(curpage);
			if(deviceType==1){
//	    		t1.invalidate(0, 0, 600,800,UPDATEMODE_4);
			}
			//showData();
		}
	}
	
	  //获取我的书券列表的某一页数据
    public void getMyticketList(final int page){
    	
    	mHandler.sendEmptyMessage(1);
    	
    	new Thread(){
    		public void run() {
    			
    			try {
					Thread.sleep(2000);
				} catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
    	    	if(ticketList!=null){
    	    		ticketList.clear();
    	    	}
    	    	System.out.println("=======page========"+page);
    	    	HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
    	        HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
    	        ahmNamePair.put("start", String.valueOf(page * pageSize));
    	        ahmNamePair.put("count", String.valueOf(pageSize));
    	        HashMap responseMap = null;
    	          try {
    	         
    	              responseMap = CPManager.getUserTicketList(ahmHeaderMap,
    	                      ahmNamePair);
    	     
    	              if (!responseMap.get("result-code").toString().contains(
    	                      "result-code: 0")) {
    	            	  errMsg = Error.getErrorDescriptionForContent(responseMap.get("result-code").toString());
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
    	          System.out.println("=========totalCount========="+totalCount);
    	          
    	          NodeList nl = root.getElementsByTagName("ticketInfo");
    	  		  Element friendtemp = null;
    	  		  NodeList friendinfotemp = null;
    	  		  HashMap<String, Object> map = null;
    	  		  if(nl!=null){
    		  		  for (int i = 0; i < nl.getLength(); i++) {
    		  			//map = new HashMap<String, Object>();
    		  			//friendtemp = (Element) nl.item(i);
    		  			//friendinfotemp = friendtemp.getElementsByTagName("ticketInfo");
    		  			ticketList.add(nl.item(i).getFirstChild().getNodeValue());
    		  		  }
    	  		  }
    	  		  System.out.println("====myticketList==="+ticketList);
    	  		  
    	  		  //发送显示数据消息
    	  		  mHandler.sendEmptyMessage(4);
    		};
    	}.start();
    }
  
}
