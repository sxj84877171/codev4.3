package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiUtil;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;


/**
 * 赠送给好友
 * @author 彭见宝 
 *
 */
public class PresentToFriend extends PviActivity implements Pageable  {
	
	private int themeNum = -1;
	
	PviDataList listView;               //view实例
	ArrayList<PviUiItem[]> list;        //存放列表控件的内部信息：ArrayList存放每行，每行使用一个数组保存多个PviUiItem
	PviBottomBar  pbb;     //引用框架底部工具条
	
	String StrGPBI;
	
	int pageSize = 7;
	
	int pages;
	
	int curpage;
	
	
	Button send;
	
	TextView presentTitle;
	
	String[] phoneNum;
	
	boolean[] checkState;
	
	private String contentID = "";
	private String chapterID = "";
	
	int list1Size;
	int size;
	
	private ArrayList<String> msnList1 = new ArrayList<String>();
	
	private ArrayList<String> msnList2 = new ArrayList<String>();
	
	int count1 = 0;
	
	int count2 = 0;
	
	private ArrayList<String> selectList = new ArrayList<String>();
	
	private EditText friendInput = null;
	
	private EditText friendMessage = null;
	
	
	List allList = new ArrayList();
	
	int selectIndex = -1;

	private PviAlertDialog loadPd;
	
	private Handler mHandler = new H();
	private final int INIT_DATA = 101;
	
	private Runnable enableButtons = new Runnable(){

        @Override
        public void run() {
        	send.setClickable(true);
        }
        
    };
    
    private Runnable disableButtons = new Runnable(){

        @Override
        public void run() {
        	send.setClickable(false);
        }
        
    };
    
    private class H extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
          
            case 301:// 关闭提示框
                if(loadPd!=null){loadPd.dismiss();}
                break;
            case 1001:
            	showInfo();
                if(loadPd!=null && loadPd.isShowing()){
                	loadPd.dismiss();
            	}
                break;
            case INIT_DATA:
                initData();
                break;
            case 999:
            	String error = msg.getData().getString("error");
            	final PviAlertDialog pd = new PviAlertDialog(getParent());
                pd.setTitle(getResources().getString(R.string.my_friend_hint));
                pd.setMessage(error,Gravity.CENTER);
                pd.setTimeout(4000);
                pd.show();
                pd.getWindow().setLayout(300, 200);
                break;
            case 9991:
            	showError1();
                break;
            case 9992:
            	showError2();
                break;
            case 9993:
            	Bundle b = msg.getData();
            	showError3(b.getString("error"));
                break;
            default:
                ;
            }

            super.handleMessage(msg);
        }
    }
  
    public void showError1(){
    	PviAlertDialog pd2 = new PviAlertDialog(getParent());
		pd2.setTitle("温馨提示");
		pd2.setMessage("操作出现网络错误！",Gravity.CENTER);
		pd2.setCanClose(false);
		pd2.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method
						// stub
						sendBroadcast(new Intent(MainpageActivity.BACK));
						return;
					}
				});
		pd2.show();
    }
    
    public void showError2(){
    	PviAlertDialog pd2 = new PviAlertDialog(getParent());
		pd2.setTitle("温馨提示");
		pd2.setMessage("操作出现网络错误！",Gravity.CENTER);
		pd2.setCanClose(true);
		pd2.show();
    }
    
    public void showError3(String msg){
    	PviAlertDialog pd2 = new PviAlertDialog(getParent());
		pd2.setTitle("温馨提示");
		pd2.setMessage(msg,Gravity.CENTER);
		pd2.setCanClose(true);
		pd2.show();
    }
    private OnClickListener clickListen = new MyCLickListen();

	
    class MyCLickListen implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			if(v.getId() == R.id.send){
				String msg = friendMessage.getText().toString();
				msg = msg.trim();
				String [] friends = friendInput.getText().toString().split(";");
				String expr = "[1][0-9]{10}";
				allList = new ArrayList();
				List inputList = new ArrayList();
				
				for(int i=0;i<friends.length;i++){
					String msisdn = friends[i];
					if(!msisdn.matches(expr)){
						if(friends.length==1 && "".equals(msisdn)){
							break;
						}
						final PviAlertDialog errorDialog =  new PviAlertDialog(getParent());
						errorDialog.setCanClose(true);
						errorDialog.setTitle(getResources().getString(R.string.bind_getAuthCode_title));
						
						errorDialog.setMessage("您输入的号码"+msisdn+"不正确");
						errorDialog.setButton(DialogInterface.BUTTON_POSITIVE,
								getResources().getString(R.string.alert_dialog_delete_yes),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										errorDialog.dismiss();
									}
						});
						errorDialog.show();
						return;
					}
					inputList.add(friends[i]);
				}
				//allList.addAll(selectList);
				allList.addAll(inputList);
				
				if(allList.size() == 0)
				{
					showAlert(getResources().getString(R.string.friendsel));
					return;
				}
			
				else if("".equals(msg))
				{
					showAlert(getResources().getString(R.string.msgnull));
					return;
				}
				else if(msg.length() > 40)
				{
					showAlert(getResources().getString(R.string.msglengthlong));
					return;
				}
				
				if(allList.size()!=1){
					showAlert("一次只能赠送给一个好友！");
					return;
				}
				
				
				mHandler.post(disableButtons);
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						recommend(allList);
						mHandler.post(enableButtons);
					}
				});
		
				//mHandler.sendEmptyMessage(301);
				
	        
				
			}
			
		}
    	
    }

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		//				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		
		//setContentView(R.layout.);
		pbb = ((GlobalVar)getApplication()).pbb;
    	setContentView(R.layout.recommendfriendliststyle2);
		 setContentView(R.layout.presenttofriend);
    
    	
		super.onCreate(savedInstanceState);
		
		Intent revintent = this.getIntent();
		Bundle revbundle = revintent.getExtras();
		this.contentID = revbundle.getString("contentID");
		this.chapterID = revbundle.getString("chapterID");
		
		send = (Button)findViewById(R.id.send);
		
		friendInput = (EditText)findViewById(R.id.friendinput);
		
		friendMessage = (EditText)findViewById(R.id.friendmessage);
		
		phoneNum = new String[pageSize];
		
		checkState = new boolean[pageSize];

		presentTitle = (TextView)findViewById(R.id.presenttitle);
		

		listView= (PviDataList)findViewById(R.id.list);
	     list = new ArrayList<PviUiItem[]>();
	      //pbb.setPageable(this);//与框架底部的工具条建立联系
	     this.showPager = true;
    	
		if(deviceType==1){
//			 findViewById(R.id.presenttitle).invalidate(0, 0, 600,800,UPDATEMODE_4);
//			 send.setUpdateMode(UPDATEMODE_5);
//			 friendInput.setUpdateMode(UPDATEMODE_5);
//			 friendMessage.setUpdateMode(UPDATEMODE_5);
       }
		
    	send.setOnClickListener(clickListen);
    }
	
	public void clear(){
		selectIndex = -1;
		pages = -1;
		curpage = -1;
		for(int i=0;i<pageSize;i++){
			phoneNum[i] = null;
			checkState[i] = false;
		}
	}
	
	public void getData(){
		StrGPBI = "";
		StrGPBI = SubscribeProcess.network("getPresentBookInfo", contentID, null, null,
                null);
        if (StrGPBI!=null&&StrGPBI.length()>10&&StrGPBI.substring(0, 10).contains("Exception")) {
        	
        	final PviAlertDialog pd = new PviAlertDialog(getParent());
            pd.setTitle(getResources().getString(R.string.my_friend_hint));
            pd.setMessage("获取书籍赠送信息出错！");
            pd.setTimeout(4000);
            pd.show();
            return;
            //pd.getWindow().setLayout(300, 200);
        }
        
		if(GetFriendList("1","10000")){
		    getUnconfirmedFriendList("1","10000");
		}
		
		
		if((count1 + count2) % pageSize == 0){
			pages = (count1 + count2) / pageSize;
		}else{
			pages = (count1 + count2) / pageSize + 1;
		}
		curpage = 0;
	}
	
	public void showInfo(){
		Thread thread = new Thread()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
			    mHandler.sendEmptyMessage(INIT_DATA);
				showme();
				super.run();
			}
			
		};
		thread.start();
	}
	
	public void initData(){
		
		String fee1 = SubscribeProcess.getvalue(StrGPBI.substring(20), "description");
	    String fee2 = SubscribeProcess.getvalue(StrGPBI.substring(20), "presentDepict");
	    String fee = fee1 + "\n" + fee2;
	        
		presentTitle.setText(fee);
		
		//======================更新分页
		updatePagerinfo((curpage+1)+" / "+(pages==0?1:pages));

		
		//======================更新数据
		list1Size = msnList1.size();
		msnList1.addAll(msnList2);
		size = (1+curpage)*pageSize<msnList1.size()?(1+curpage)*pageSize:msnList1.size();
		list.clear();
		
		for(int i=curpage * pageSize;i<size;i++){
			
			
			final PviUiItem[] items = new PviUiItem[]{
				 	new PviUiItem("text1"+i, 0, 10, 10, 440, 50, "手机号", false, true, null),
	                new PviUiItem("text2"+i, 0, 450, 10, 85, 50, "状态", false, true, null),
	                new PviUiItem("icon"+i, R.drawable.notcheck, 535,10,25,50, null, false, true, null),
			};
			phoneNum[i % pageSize] = msnList1.get(i);
			items[0].text = msnList1.get(i);
			if(i<list1Size){
				items[1].text = "已确认";
			}else{
				items[1].text = "待验证";
			}
			
			if(selectList.contains(msnList1.get(i))){
				items[2].res = R.drawable.check;
			}
			
			final int k = i;
			OnClickListener l = new OnClickListener(){     //new 一个click事件监听

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                	
                	String str = friendInput.getText().toString();
    				
                	if(checkState[k % pageSize] == false){
                		checkState[k % pageSize] = true;
                		items[2].res = R.drawable.check;
                		listView.invalidate();
                		if(str != null && !str.contains(phoneNum[k % pageSize])){
    						if("".equals(str) || ";".equals(str.substring(str.length()-1, str.length()))){
    							str = str +phoneNum[k % pageSize]  + ";";
    						}else{
    							str = str + ";" + phoneNum[k % pageSize]  + ";";
    						}
    						friendInput.setText(str);
    					}
                		selectList.add(msnList1.get(k));
                	}else{
                		checkState[k % pageSize] = false;
                		items[2].res = R.drawable.notcheck;
                		listView.invalidate();
                		selectList.remove(msnList1.get(k));
                		if(str != null && str.contains(phoneNum[k % pageSize])){
    						str = str.replace(phoneNum[k % pageSize] + ";","");
    						friendInput.setText(str);
    					}
    					
                		
                	}
                }
                
            };
            items[0].l = l;
            items[1].l = l;  
            items[2].l = l;  

            
			list.add(items);
			
		}
		listView.setData(list);
	}
	public void initData1(){
		
		
		String fee1 = SubscribeProcess.getvalue(StrGPBI.substring(20), "description");
	    String fee2 = SubscribeProcess.getvalue(StrGPBI.substring(20), "presentDepict");
	    String fee = fee1 + "\n" + fee2;
	        
		presentTitle.setText(fee);
		
		
		//======================更新分页
		updatePagerinfo((curpage+1)+" / "+(pages==0?1:pages));

	
		//======================更新数据
		list.clear();
		//int list1Size = msnList1.size();
		int size = (1+curpage)*pageSize<msnList1.size()?(1+curpage)*pageSize:msnList1.size();
		for(int i=curpage * pageSize;i<size;i++){
			
			final PviUiItem[] items = new PviUiItem[]{
				 	new PviUiItem("text1"+i, 0, 10, 10, 440, 50, "手机号", false, true, null),
	                new PviUiItem("text2"+i, 0, 450, 10, 85, 50, "状态", false, true, null),
	                new PviUiItem("icon"+i, R.drawable.notcheck, 535,10,25,50, null, false, true, null),
			};
			
			phoneNum[i % pageSize] = msnList1.get(i);
			items[0].text = msnList1.get(i);
			if(i<list1Size){
				items[1].text = "已确认";
			}else{
				items[1].text = "待验证";
			}
			
			if(selectList.contains(msnList1.get(i))){
				items[2].res = R.drawable.check;
			}
			final int k = i;
			OnClickListener l = new OnClickListener(){     //new 一个click事件监听

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                	
                	String str = friendInput.getText().toString();
    				
                	if(checkState[k % pageSize] == false){
                		checkState[k % pageSize] = true;
                		items[2].res = R.drawable.check;
                		listView.invalidate();
                		if(str != null && !str.contains(phoneNum[k % pageSize])){
    						if("".equals(str) || ";".equals(str.substring(str.length()-1, str.length()))){
    							str = str +phoneNum[k % pageSize]  + ";";
    						}else{
    							str = str + ";" + phoneNum[k % pageSize]  + ";";
    						}
    						friendInput.setText(str);
    					}
                		selectList.add(msnList1.get(k));
                		
                	}else{
                		checkState[k % pageSize] = false;
                		items[2].res = R.drawable.notcheck;
                		listView.invalidate();
                		selectList.remove(msnList1.get(k));
                		if(str != null && str.contains(phoneNum[k % pageSize])){
    						str = str.replace(phoneNum[k % pageSize] + ";","");
    						friendInput.setText(str);
    					}
                	}
                }
                
            };
            items[0].l = l;
            items[1].l = l;  
            items[2].l = l;  

            
			list.add(items);
		}
		listView.setData(list);
		
	}
	private void showAlert(String message){
    	if(loadPd!=null && loadPd.isShowing()){
    		loadPd.dismiss();
    	}
    	loadPd = new PviAlertDialog(getParent());
    	loadPd.setTitle(getResources().getString(R.string.my_friend_hint));
    	loadPd.setMessage(message);
    	loadPd.setCanClose(true);
        //pd.setTimeout(4000);
    	loadPd.show();
    }
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		clear();
		showTip("正在加载数据...");
	    new Thread(){
	    	public void run() {
	    		getData();
	    		mHandler.sendEmptyMessage(1001);
	    	};
	    }.start();
		
		/*
		loadPd = new PviAlertDialog(getParent());
		loadPd.setTitle(getResources().getString(R.string.my_friend_hint));
		loadPd.setMessage(getResources().getString(R.string.kyleHint05));
		//loadPd.setTimeout(4000);
		loadPd.show();
		
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				clear();
				getData();
				initData();
			}
		});

		mHandler.sendEmptyMessage(301);*/
		
		/*
		clear();
		getData();
		initData();
		*/
	}
	
	private boolean GetFriendList(String start, String count) {
		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		//		String start = String.valueOf((currentPage-1)*itemPerPage+1);
		//		String count = String.valueOf(itemPerPage);
		//		Log.d("Reader", start);
		//		Log.d("Reader", count);
		ahmNamePair.put("start", start);
		ahmNamePair.put("count", count);

		HashMap responseMap = null;

		msnList1.clear();
		try {
			// 以POST的形式连接请求
			responseMap = CPManager.getFriendList(ahmHeaderMap, ahmNamePair);
			if (!responseMap.get("result-code").toString().contains(
			"result-code: 0")) {
				Message msg = new Message();
				msg.what = 9993;
				Bundle b = new Bundle();
				b.putString("error", Error.getErrorDescriptionForContent(responseMap.get("result-code").toString()));
				msg.setData(b);
				mHandler.sendMessage(msg);
				//pd.getWindow().setLayout(300, 200);
				Logger.i("GetFriendList Error Code: ",responseMap.get("result-code").toString());
				return false;
			}
		} catch (HttpException e) {
			// 连接异常 ,一般原因为 URL错误
			e.printStackTrace();
			mHandler.sendEmptyMessage(9991);
            //pd.getWindow().setLayout(300, 200);
            Logger.e("GetFriendList http Exception: ",e.toString());
			return false;
		} catch (IOException e) {
			// IO异常 ,一般原因为网络问题
			e.printStackTrace();
			mHandler.sendEmptyMessage(9991);
           // pd.getWindow().setLayout(300, 200);
            Logger.e("GetFriendList IO Exception: ",e.toString());
			return false;
		}


		byte[] responseBody = (byte[]) responseMap.get("ResponseBody");

//		try {
//
//			
//			Log.d("Reader", "返回的XML为：");
//			
//			Log.d("Reader", CPManagerUtil
//					.getStringFrombyteArray(responseBody));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//			Toast.makeText(this, "GetFriendList Exception: "
//					+ e.toString(),	Toast.LENGTH_SHORT).show();
//			//			return false;
//		}

		// 根据返回字节数组构造DOM
		Document dom = null;
		try {
			dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(9992);
			Logger.e("Menu", "Dom addFavorite Exception: "+ e.toString());
			//			return false;
		} catch (SAXException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(9992);
			Logger.e("Menu", "Dom addFavorite Exception: "+ e.toString());
			//			return false;
		} catch (SocketTimeoutException e) {
        	
			mHandler.sendEmptyMessage(9992);
			//handler2.sendEmptyMessage(0);
            //显示获取用户信息失败提示框
            return false;
           }
		catch (IOException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(9992);
			Logger.e("Menu", "Dom GetFriendList Exception: "+ e.toString());
			//			return false;
		}
		Element root = dom.getDocumentElement();
		// 以下为解释XML的代码
		// ......

		NodeList nl = root.getElementsByTagName("Friend");
		Element friendtemp = null;
		NodeList friendinfotemp = null;
		HashMap<String, Object> map = null;
		for (int i = 0; i < nl.getLength(); i++) {
			map = new HashMap<String, Object>();
			friendtemp = (Element) nl.item(i);
			friendinfotemp = friendtemp
			.getElementsByTagName("msisdn");
			/*map.put("msisdn", friendinfotemp.item(0).getFirstChild().getNodeValue());
			// Log.i("Menu", friendinfotemp.getFirstChild().getNodeValue());
			friendinfotemp =  friendtemp.getElementsByTagName("nickName");
			map.put("nickName", friendinfotemp.item(0).getFirstChild().getNodeValue());

			friendlist.add(map);*/
			msnList1.add( friendinfotemp.item(0).getFirstChild().getNodeValue());
		}
		nl = root.getElementsByTagName("totalRecordCount");
		this.count1 = Integer.parseInt(nl.item(0).getFirstChild().getNodeValue());
		//		this.totalPage = this.totalPage + Integer.parseInt(nl.item(0).getFirstChild().getNodeValue());
		return true;
	}

	private boolean getUnconfirmedFriendList(String start, String count) {
		this.msnList2.clear();
		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

		ahmNamePair.put("start", start);
		ahmNamePair.put("count", count);

		HashMap responseMap = null;

		try {
			// 以POST的形式连接请求
			responseMap = CPManager.getUnconfirmedFriendList(ahmHeaderMap,
					ahmNamePair);
			if (!responseMap.get("result-code").toString().contains(
			"result-code: 0")) {
				Message msg = new Message();
				msg.what = 999;
				Bundle b = new Bundle();
				b.putString("error", Error.getErrorDescriptionForContent(responseMap.get("result-code").toString()));
				msg.setData(b);
				mHandler.sendMessage(msg);
				/*
				final PviAlertDialog pd = new PviAlertDialog(getParent());
	            pd.setTitle(getResources().getString(R.string.my_friend_hint));
	            pd.setMessage(getResources().getString(R.string.my_friend_connecting));
	            pd.setTimeout(4000);
	            pd.show();
	            pd.getWindow().setLayout(300, 200);
	            Logger.i("getUnconfirmedFriendList Error Code: ",responseMap.get("result-code").toString());
	            */
				return false;
			}
		} catch (HttpException e) {
			// 连接异常 ,一般原因为 URL错误
			e.printStackTrace();
			mHandler.sendEmptyMessage(9991);
			return false;
		}  catch (SocketTimeoutException e) {
        	
			mHandler.sendEmptyMessage(9991);
            //显示获取用户信息失败提示框
            return false;
           }catch (IOException e) {
			// IO异常 ,一般原因为网络问题
			e.printStackTrace();
			mHandler.sendEmptyMessage(9991);
			return false;
		}
		byte[] responseBody = (byte[]) responseMap.get("ResponseBody");

//		try {
//			System.out.println("返回的XML为：");
//			System.out.println(CPManagerUtil
//					.getStringFrombyteArray(responseBody));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//			Toast.makeText(this, "getUnconfirmedFriendList Exception: "
//					+ e.toString(),	Toast.LENGTH_SHORT).show();
//			Log.i("sssss", "222" + e.toString());
//			//			return false;
//		}
		// 根据返回字节数组构造DOM
		Document dom = null;
		try {
			dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(9992);
			Logger.e("Menu", "Dom getUnconfirmedFriendList Exception1: "+ e.toString());
			//			return false;
		}  catch (SocketTimeoutException e) {
        	
			mHandler.sendEmptyMessage(9992);
			//handler2.sendEmptyMessage(0);
            //显示获取用户信息失败提示框
            return false;
           }catch (SAXException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(9992);
			Logger.e("Menu", "Dom getUnconfirmedFriendList Exception2: "+ e.toString());
			//			return false;
		} catch (IOException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(9992);
			Logger.e("Menu", "Dom getUnconfirmedFriendList Exception3: "+ e.toString());
			//			return false;
		}

		Element root = dom.getDocumentElement();
		// 以下为解释XML的代码
		// ......

		try
		{
			//			
			NodeList nl = root.getElementsByTagName("Friend");
			if(nl == null)
			{
				return true;
			}
			Element friendtemp = null;
			NodeList friendinfotemp = null;
			for (int i = 0; i < nl.getLength(); i++) {
				friendtemp = (Element) nl.item(i);
				friendinfotemp = (NodeList) friendtemp
				.getElementsByTagName("msisdn");
				msnList2.add(friendinfotemp.item(0).getFirstChild()
						.getNodeValue());
			}

			nl = root.getElementsByTagName("totalRecordCount");
			this.count2 = Integer.parseInt(nl.item(0).getFirstChild().getNodeValue());
		}
		catch(Exception e)
		{
			Logger.e("Menu", "11111" + e.toString());	
			return false;
		}

		return true;
	}

	
	

public boolean onKeyDown(int keyCode, KeyEvent event) {
    	
    	
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            menupan();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 通知框架返回上一个子activty
            sendBroadcast(new Intent(MainpageActivity.BACK));
            return true;
        }
        
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
        	// 向左翻页
        	if(listView.hasFocus()){
        		prevPage();
     		}
        	return false;
        }
        
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
        	// 向右翻页
        	if(listView.hasFocus()){
        		nextPage();
     		}
        	return false;
        	 
        }
        
       
        return super.onKeyDown(keyCode, event);
    }
  
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        closePopmenu();
        super.onPause();
    }
    
	private boolean recommend(List friends)
	{
	
		String StrAULW = SubscribeProcess.network("presentBook", contentID,
                friendInput.getText().toString().substring(0,11), friendMessage.getText()
                        .toString(), null);
		
		PviUiUtil.hideInput(getWindow().getDecorView());
		
        if (StrAULW.substring(0, 10).contains("Exception")) {
        	PviAlertDialog pd = new PviAlertDialog(
                    getParent());
            pd.setCanClose(true);
            pd.setTitle("温馨提示");
            pd.setMessage("书籍赠送失败");
            pd.show();
            return false;
        } else if (StrAULW.substring(0, 19)
                .contains("0000")) {
            PviAlertDialog pd = new PviAlertDialog(
                    getParent());
            pd.setCanClose(true);
            pd.setTitle("温馨提示");
            pd.setMessage("书籍赠送成功");
            pd.show();
            return true;
        }else{
        	 PviAlertDialog pd = new	 PviAlertDialog(
                     getParent());
             pd.setCanClose(true);
             pd.setTitle("温馨提示");
             pd.setMessage(Error.getErrorDescriptionForContent(StrAULW.substring(0, 19)));
             pd.show();
             return true;
        }
	}
	private void showme()
	{
		Intent tmpIntent = new Intent(
				MainpageActivity.SHOW_ME);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("sender",this.getClass().getName()); //TAB内嵌activity类的全名
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
		tmpIntent = null;
		bundleToSend = null;
		Intent intent1 = new Intent(MainpageActivity.HIDE_TIP);
		sendBroadcast(intent1);

	}

	@Override
	public void OnNextpage() {
		// TODO Auto-generated method stub
		nextPage();
	}

	@Override
	public void OnPrevpage() {
		// TODO Auto-generated method stub
		prevPage();
	}
	
	public void nextPage(){
		
		if(curpage < pages - 1){
    		curpage++;
   	        initData1();
 		}

	}
	
	public void prevPage(){
		if(curpage > 0){
        	
            curpage  --;
    	    initData1();

 		}

	}
}
