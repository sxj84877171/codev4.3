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
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.data.common.EPDRefresh;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;
import com.pvi.ap.reader.data.external.manager.LeafNode;
import com.pvi.ap.reader.data.external.manager.XMLUtil;
import com.pvi.ap.reader.data.external.manager.XmlElement;


/**
 * 赠送给好友
 * @author 彭见宝 
 *
 */
//public class PresentToFriend extends PviActivity {
//	
//	private int themeNum = -1;
//	
//	String StrGPBI;
//	
//	int pageSize = 7;
//	
//	int pages;
//	
//	int curpage;
//	
//	Button[] checkButton;
//	
//	Button send;
//	
//	TextView presentTitle;
//	
//	TextView[] phoneNum;
//	
//	TextView[] state;
//	
//	boolean[] checkState;
//	
//	private String contentID = "";
//	private String chapterID = "";
//	
//	int list1Size;
//	int size;
//	
//	private ArrayList<String> msnList1 = new ArrayList<String>();
//	
//	private ArrayList<String> msnList2 = new ArrayList<String>();
//	
//	int count1 = 0;
//	
//	int count2 = 0;
//	
//	private TextView mtv_CurPage = null;//当前页码显示框
//	
//	private TextView mtv_Pages = null; //总页码显示框
//	
//	private ImageButton mtv_Prev = null;//上一页码显示框
//	
//	private ImageButton mtv_Next = null;//下一页码显示框
//	
//	//private PopupWindow popmenu;
//	//private View popmenuView;
//    private ImageButton home = null;
//    private ImageButton allapp = null;
//	private ImageButton syssetting = null;
//	private ImageButton music = null;
//	private ImageButton back = null;
//	
//	private ArrayList<String> selectList = new ArrayList<String>();
//	
//	private EditText friendInput = null;
//	
//	private EditText friendMessage = null;
//	
//	ImageView[] select;
//	
//	List allList = new ArrayList();
//	
//	int selectIndex = -1;
//	RelativeLayout[] r;
//	
//	private PviAlertDialog loadPd;
//	
//	private Handler mHandler = new H();
//	private final int INIT_DATA = 101;
//    private class H extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            // TODO Auto-generated method stub
//            switch (msg.what) {
//          
//            case 301:// 关闭提示框
//                if(loadPd!=null){loadPd.dismiss();}
//                break;
//            case 1001:
//            	showInfo();
//                if(loadPd!=null && loadPd.isShowing()){
//                	loadPd.dismiss();
//            	}
//                break;
//            case INIT_DATA:
//                initData();
//                break;
//            case 999:
//            	String error = msg.getData().getString("error");
//            	final PviAlertDialog pd = new PviAlertDialog(getParent());
//                pd.setTitle(getResources().getString(R.string.my_friend_hint));
//                pd.setMessage(error,Gravity.CENTER);
//                pd.setTimeout(4000);
//                pd.show();
//                pd.getWindow().setLayout(300, 200);
//                break;
//            case 9991:
//            	showError1();
//                break;
//            case 9992:
//            	showError2();
//                break;
//            case 9993:
//            	Bundle b = msg.getData();
//            	showError3(b.getString("error"));
//                break;
//            default:
//                ;
//            }
//
//            super.handleMessage(msg);
//        }
//    }
//  
//    public void showError1(){
//    	PviAlertDialog pd2 = new PviAlertDialog(getParent());
//		pd2.setTitle("温馨提示");
//		pd2.setMessage("操作出现网络错误！",Gravity.CENTER);
//		pd2.setCanClose(false);
//		pd2.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
//				new android.content.DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog,
//							int which) {
//						// TODO Auto-generated method
//						// stub
//						sendBroadcast(new Intent(MainpageActivity.BACK));
//						return;
//					}
//				});
//		pd2.show();
//    }
//    
//    public void showError2(){
//    	PviAlertDialog pd2 = new PviAlertDialog(getParent());
//		pd2.setTitle("温馨提示");
//		pd2.setMessage("操作出现网络错误！",Gravity.CENTER);
//		pd2.setCanClose(true);
//		pd2.show();
//    }
//    
//    public void showError3(String msg){
//    	PviAlertDialog pd2 = new PviAlertDialog(getParent());
//		pd2.setTitle("温馨提示");
//		pd2.setMessage(msg,Gravity.CENTER);
//		pd2.setCanClose(true);
//		pd2.show();
//    }
//    private OnClickListener clickListen = new MyCLickListen();
//
//	
//    class MyCLickListen implements OnClickListener{
//
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			
//			if(v.getId() == R.id.send){
//				String msg = friendMessage.getText().toString();
//				msg = msg.trim();
//				String [] friends = friendInput.getText().toString().split(";");
//				String expr = "[1][0-9]{10}";
//				allList = new ArrayList();
//				List inputList = new ArrayList();
//				
//				for(int i=0;i<friends.length;i++){
//					String msisdn = friends[i];
//					if(!msisdn.matches(expr)){
//						if(friends.length==1 && "".equals(msisdn)){
//							break;
//						}
//						final PviAlertDialog errorDialog =  new PviAlertDialog(getParent());
//						errorDialog.setCanClose(true);
//						errorDialog.setTitle(getResources().getString(R.string.bind_getAuthCode_title));
//						
//						errorDialog.setMessage("您输入的号码"+msisdn+"不正确");
//						errorDialog.setButton(DialogInterface.BUTTON_POSITIVE,
//								getResources().getString(R.string.alert_dialog_delete_yes),
//								new DialogInterface.OnClickListener() {
//
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										errorDialog.dismiss();
//									}
//						});
//						errorDialog.show();
//						return;
//					}
//					inputList.add(friends[i]);
//				}
//				//allList.addAll(selectList);
//				allList.addAll(inputList);
//				
//				if(allList.size() == 0)
//				{
//					showAlert(getResources().getString(R.string.friendsel));
//					return;
//				}
//			
//				else if("".equals(msg))
//				{
//					showAlert(getResources().getString(R.string.msgnull));
//					return;
//				}
//				else if(msg.length() > 40)
//				{
//					showAlert(getResources().getString(R.string.msglengthlong));
//					return;
//				}
//				
//				if(allList.size()!=1){
//					showAlert("一次只能赠送给一个好友！");
//					return;
//				}
//				
//				mHandler.post(new Runnable() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						recommend(allList);
//					}
//				});
//		
//				//mHandler.sendEmptyMessage(301);
//				
//	        
//				
//			}else if(v.getId() == R.id.select1 || v.getId() == R.id.r1){
//				String str = friendInput.getText().toString();
//				if(checkState[0] == false){
//					checkState[0] = true;
//					selectList.add(phoneNum[0].getText().toString());
//			
//						checkButton[0].setBackgroundResource(R.drawable.check);
//				
//					if(str != null && !str.contains(phoneNum[0].getText().toString())){
//						if("".equals(str) || ";".equals(str.substring(str.length()-1, str.length()))){
//							str = str +phoneNum[0].getText().toString()  + ";";
//						}else{
//							str = str + ";" + phoneNum[0].getText().toString()  + ";";
//						}friendInput.setText(str);
//					}
//					
//				}else{
//					checkState[0] = false;
//					selectList.remove(phoneNum[0].getText().toString());
//				
//						checkButton[0].setBackgroundResource(R.drawable.notcheck);
//			
//					if(str != null && str.contains(phoneNum[0].getText().toString())){
//						str = str.replace(phoneNum[0].getText().toString()  + ";","");
//						friendInput.setText(str);
//					}
//					
//				}
//			}else if(v.getId() == R.id.select2 || v.getId() == R.id.r2){
//				String str = friendInput.getText().toString();
//				if(checkState[1] == false){
//					checkState[1] = true;
//					selectList.add(phoneNum[1].getText().toString());
//				
//						checkButton[1].setBackgroundResource(R.drawable.check);
//				
//					if(str != null && !str.contains(phoneNum[1].getText().toString())){
//						if("".equals(str) || ";".equals(str.substring(str.length()-1, str.length()))){
//							str = str +phoneNum[1].getText().toString()  + ";";
//						}else{
//							str = str + ";" + phoneNum[1].getText().toString()  + ";";
//						}
//						friendInput.setText(str);
//					}
//					
//				}else{
//					checkState[1] = false;
//					selectList.remove(phoneNum[1].getText().toString());
//					
//						checkButton[1].setBackgroundResource(R.drawable.notcheck);
//				
//					if(str != null && str.contains(phoneNum[1].getText().toString())){
//						str = str.replace(phoneNum[1].getText().toString()  + ";","");
//						friendInput.setText(str);
//					}
//				}
//			}else if(v.getId() == R.id.select3 || v.getId() == R.id.r3){
//				String str = friendInput.getText().toString();
//				if(checkState[2] == false){
//					checkState[2] = true;
//					selectList.add(phoneNum[2].getText().toString());
//					
//						checkButton[2].setBackgroundResource(R.drawable.check);
//					
//					if(str != null && !str.contains(phoneNum[2].getText().toString())){
//						if("".equals(str) || ";".equals(str.substring(str.length()-1, str.length()))){
//							str = str +phoneNum[2].getText().toString()  + ";";
//						}else{
//							str = str + ";" + phoneNum[2].getText().toString()  + ";";
//						}
//						friendInput.setText(str);
//					}
//					
//				}else{
//					checkState[2] = false;
//					selectList.remove(phoneNum[2].getText().toString());
//					
//						checkButton[2].setBackgroundResource(R.drawable.notcheck);
//				
//					if(str != null && str.contains(phoneNum[2].getText().toString())){
//						str = str.replace(phoneNum[2].getText().toString()  + ";","");
//						friendInput.setText(str);
//					}
//				}
//			}else if(v.getId() == R.id.select4 || v.getId() == R.id.r4){
//				String str = friendInput.getText().toString();
//				if(checkState[3] == false){
//					checkState[3] = true;
//					selectList.add(phoneNum[3].getText().toString());
//					
//						checkButton[3].setBackgroundResource(R.drawable.check);
//				
//					if(str != null && !str.contains(phoneNum[3].getText().toString())){
//						if("".equals(str) || ";".equals(str.substring(str.length()-1, str.length()))){
//							str = str +phoneNum[3].getText().toString()  + ";";
//						}else{
//							str = str + ";" + phoneNum[3].getText().toString()  + ";";
//						}
//						friendInput.setText(str);
//					}
//					
//				}else{
//					checkState[3] = false;
//					selectList.remove(phoneNum[3].getText().toString());
//				
//						checkButton[3].setBackgroundResource(R.drawable.notcheck);
//					
//					if(str != null && str.contains(phoneNum[3].getText().toString())){
//						str = str.replace(phoneNum[3].getText().toString()  + ";","");
//						friendInput.setText(str);
//					}
//				}
//			}else if(v.getId() == R.id.select5 || v.getId() == R.id.r5){
//				String str = friendInput.getText().toString();
//				if(checkState[4] == false){
//					checkState[4] = true;
//					selectList.add(phoneNum[4].getText().toString());
//					
//						checkButton[4].setBackgroundResource(R.drawable.check);
//					
//					if(str != null && !str.contains(phoneNum[4].getText().toString())){
//						if("".equals(str) || ";".equals(str.substring(str.length()-1, str.length()))){
//							str = str +phoneNum[4].getText().toString()  + ";";
//						}else{
//							str = str + ";" + phoneNum[4].getText().toString()  + ";";
//						}
//						friendInput.setText(str);
//					}
//					
//				}else{
//					checkState[4] = false;
//					selectList.remove(phoneNum[4].getText().toString());
//			
//						checkButton[4].setBackgroundResource(R.drawable.notcheck);
//				
//					if(str != null && str.contains(phoneNum[4].getText().toString())){
//						str = str.replace(phoneNum[4].getText().toString()  + ";","");
//						friendInput.setText(str);
//					}
//				}
//			}else if(v.getId() == R.id.select6 || v.getId() == R.id.r6){
//				String str = friendInput.getText().toString();
//				if(checkState[5] == false){
//					checkState[5] = true;
//					selectList.add(phoneNum[5].getText().toString());
//			
//						checkButton[5].setBackgroundResource(R.drawable.check);
//				
//					if(str != null && !str.contains(phoneNum[5].getText().toString())){
//						if("".equals(str) || ";".equals(str.substring(str.length()-1, str.length()))){
//							str = str +phoneNum[5].getText().toString()  + ";";
//						}else{
//							str = str + ";" + phoneNum[5].getText().toString()  + ";";
//						}
//						friendInput.setText(str);
//					}
//					
//				}else{
//					checkState[5] = false;
//					selectList.remove(phoneNum[5].getText().toString());
//					
//						checkButton[5].setBackgroundResource(R.drawable.notcheck);
//				
//					if(str != null && str.contains(phoneNum[5].getText().toString())){
//						str = str.replace(phoneNum[5].getText().toString()  + ";","");
//						friendInput.setText(str);
//					}
//				}
//			}else if(v.getId() == R.id.select7 || v.getId() == R.id.r7){
//				String str = friendInput.getText().toString();
//				if(checkState[6] == false){
//					checkState[6] = true;
//					selectList.add(phoneNum[6].getText().toString());
//			
//						checkButton[6].setBackgroundResource(R.drawable.check);
//					
//					if(str != null && !str.contains(phoneNum[6].getText().toString())){
//						if("".equals(str) || ";".equals(str.substring(str.length()-1, str.length()))){
//							str = str +phoneNum[6].getText().toString()  + ";";
//						}else{
//							str = str + ";" + phoneNum[6].getText().toString()  + ";";
//						}
//						friendInput.setText(str);
//					}
//				}else{
//					checkState[6] = false;
//					selectList.remove(phoneNum[6].getText().toString());
//			
//						checkButton[6].setBackgroundResource(R.drawable.notcheck);
//				
//					if(str != null && str.contains(phoneNum[6].getText().toString())){
//						str = str.replace(phoneNum[6].getText().toString()  + ";","");
//						friendInput.setText(str);
//					}
//				}
//			}
//			
//		}
//    	
//    }
//
//	
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//		
//		//setContentView(R.layout.);
//		
//    	    setContentView(R.layout.presenttofriend);
//    
//    	
//		super.onCreate(savedInstanceState);
//		
//		Intent revintent = this.getIntent();
//		Bundle revbundle = revintent.getExtras();
//		this.contentID = revbundle.getString("contentID");
//		this.chapterID = revbundle.getString("chapterID");
//		
//		send = (Button)findViewById(R.id.send);
//		
//		friendInput = (EditText)findViewById(R.id.friendinput);
//		
//		friendMessage = (EditText)findViewById(R.id.friendmessage);
//		
//		checkButton = new Button[pageSize];
//		
//		phoneNum = new TextView[pageSize];
//		
//		state = new TextView[pageSize];
//		
//		checkState = new boolean[pageSize];
//		r = new RelativeLayout[pageSize];
//		
//		checkButton[0] = (Button)findViewById(R.id.select1);
//		checkButton[1] = (Button)findViewById(R.id.select2);
//		checkButton[2] = (Button)findViewById(R.id.select3);
//		checkButton[3] = (Button)findViewById(R.id.select4);
//		checkButton[4] = (Button)findViewById(R.id.select5);
//		checkButton[5] = (Button)findViewById(R.id.select6);
//		checkButton[6] = (Button)findViewById(R.id.select7);
//		
//		phoneNum[0] = (TextView)findViewById(R.id.phone01);
//		phoneNum[1] = (TextView)findViewById(R.id.phone02);
//		phoneNum[2] = (TextView)findViewById(R.id.phone03);
//		phoneNum[3] = (TextView)findViewById(R.id.phone04);
//		phoneNum[4] = (TextView)findViewById(R.id.phone05);
//		phoneNum[5] = (TextView)findViewById(R.id.phone06);
//		phoneNum[6] = (TextView)findViewById(R.id.phone07);
//		
//		state[0] = (TextView)findViewById(R.id.status01);
//		state[1] = (TextView)findViewById(R.id.status02);
//		state[2] = (TextView)findViewById(R.id.status03);
//		state[3] = (TextView)findViewById(R.id.status04);
//		state[4] = (TextView)findViewById(R.id.status05);
//		state[5] = (TextView)findViewById(R.id.status06);
//		state[6] = (TextView)findViewById(R.id.status07);
//		
//		r[0] = (RelativeLayout)findViewById(R.id.r1);
//		r[1] = (RelativeLayout)findViewById(R.id.r2);
//		r[2] = (RelativeLayout)findViewById(R.id.r3);
//		r[3] = (RelativeLayout)findViewById(R.id.r4);
//		r[4] = (RelativeLayout)findViewById(R.id.r5);
//		r[5] = (RelativeLayout)findViewById(R.id.r6);
//		r[6] = (RelativeLayout)findViewById(R.id.r7);
//		
//		presentTitle = (TextView)findViewById(R.id.presenttitle);
//		
//	    	mtv_CurPage = (TextView)findViewById(R.id.curpage);
//	    	
//	    	mtv_Pages = (TextView)findViewById(R.id.pages);
//	    	
//	    	mtv_Prev = (ImageButton)findViewById(R.id.prev);
//	    	
//	    	mtv_Next = (ImageButton)findViewById(R.id.next);
//    	
//    	
//		if(deviceType==1){
//			 findViewById(R.id.presenttitle).invalidate(0, 0, 600,800,UPDATEMODE_4);
//			 r[0].setUpdateMode(UPDATEMODE_5);
//			 r[1].setUpdateMode(UPDATEMODE_5);
//			 r[2].setUpdateMode(UPDATEMODE_5);
//			 r[3].setUpdateMode(UPDATEMODE_5);
//			 r[4].setUpdateMode(UPDATEMODE_5);
//			 r[5].setUpdateMode(UPDATEMODE_5);
//			 r[6].setUpdateMode(UPDATEMODE_5);
//			 
//			 send.setUpdateMode(UPDATEMODE_5);
//			 friendInput.setUpdateMode(UPDATEMODE_5);
//			 friendMessage.setUpdateMode(UPDATEMODE_5);
//       }
//		
//    	send.setOnClickListener(clickListen);
//    	
//    	//绑定向上分页事件
//    	mtv_Prev.setOnClickListener(new OnClickListener() {
//	     	public void onClick(final View v) {
//	     		
//	     		if(curpage > 0){
//	     			if(deviceType == 1){
//	     				
//	     				findViewById(R.id.presenttitle).invalidate(0, 0, 600,800,UPDATEMODE_4);
//	     			
//	     			}
//	     			curpage--;
//	        	    initData1();
//	     		}
//	     		
//	        }
//	    });
//    	
//    	//绑定向下分页事件
//    	mtv_Next.setOnClickListener(new OnClickListener() {
//	     	public void onClick(final View v) {
//	     		
//	     		if(curpage < pages - 1){
//	     			
//	     			if(deviceType == 1){
//	     				
//	     				findViewById(R.id.presenttitle).invalidate(0, 0, 600,800,UPDATEMODE_4);
//	     			
//	     			}
//	     			curpage++;
//	        	    initData1();
//	     		}
//	     		
//	        }
//	    });
//		
//		
//		checkButton[0].setOnClickListener(clickListen);
//		r[0].setOnClickListener(clickListen);
//		checkButton[1].setOnClickListener(clickListen);
//		r[1].setOnClickListener(clickListen);
//		checkButton[2].setOnClickListener(clickListen);
//		r[2].setOnClickListener(clickListen);
//		checkButton[3].setOnClickListener(clickListen);
//		r[3].setOnClickListener(clickListen);
//		checkButton[4].setOnClickListener(clickListen);
//		r[4].setOnClickListener(clickListen);
//		checkButton[5].setOnClickListener(clickListen);
//		r[5].setOnClickListener(clickListen);
//		checkButton[6].setOnClickListener(clickListen);
//		r[6].setOnClickListener(clickListen);
//		
//		if(themeNum == 1){
//			   //底部工具栏事件处理
//		       home = (ImageButton) findViewById(R.id.fp_application);
//		        if(home!=null){
//		        home.setOnClickListener(new View.OnClickListener() {
//		
//		            @Override
//		            public void onClick(View v) {
//		                // 显示所有程序
//		               // 通知框架去启动Activity
//		            	Intent intent1 = new Intent(
//								MainpageActivity.START_ACTIVITY);
//						Bundle sndBundle1 = new Bundle();
//						sndBundle1.putString("act",
//						"com.pvi.ap.reader.activity.AllAppActivity");
//						sndBundle1.putString("startType", "allwaysCreate");
//						intent1.putExtras(sndBundle1);
//						sendBroadcast(intent1);
//		            }
//		        });
//		        }
//				this.syssetting = (ImageButton) findViewById(R.id.fp_settings);
//				//this.allapp = (TextView) this.findViewById(R.id.allapp);
//				this.back = (ImageButton) this.findViewById(R.id.back);
//				this.music = (ImageButton) this.findViewById(R.id.fp_music);
//				this.syssetting.setOnClickListener(new View.OnClickListener() {
//		
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						Intent intent1 = new Intent(
//								MainpageActivity.START_ACTIVITY);
//						Bundle sndBundle1 = new Bundle();
//						sndBundle1
//						.putString("act",
//						"com.pvi.ap.reader.activity.SystemConfigActivity");
//		
//						sndBundle1.putString("startType", "allwaysCreate");
//						intent1.putExtras(sndBundle1);
//						sendBroadcast(intent1);
//					}
//				});
//				/*
//				this.allapp.setOnClickListener(new View.OnClickListener() {
//		
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						Intent intent1 = new Intent(
//								MainpageActivity.START_ACTIVITY);
//						Bundle sndBundle1 = new Bundle();
//						sndBundle1.putString("act",
//						"com.pvi.ap.reader.activity.AllAppActivity");
//						sndBundle1.putString("startType", "allwaysCreate");
//						intent1.putExtras(sndBundle1);
//						sendBroadcast(intent1);
//					}
//				});*/
//		
//				this.music.setOnClickListener(new View.OnClickListener() {
//		
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						Intent tmpIntent = new Intent(
//								MainpageActivity.START_ACTIVITY);
//						Bundle bundleToSend = new Bundle();
//						bundleToSend.putString("actID", "ACT13200");
//						tmpIntent.putExtras(bundleToSend);
//						sendBroadcast(tmpIntent);
//					}
//				});
//				this.back.setOnClickListener(new View.OnClickListener() {
//		
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						sendBroadcast(new Intent(MainpageActivity.BACK));
//					}
//				});
//		}else if(themeNum == 2){
//			TextView tv_menuBtn = (TextView) findViewById(R.id.menubtn);
//		    tv_menuBtn.setOnClickListener(new TextView.OnClickListener() {
//		            @Override
//		            public void onClick(View v) {
//		                menupan();
//		            }
//		    });
//		}
//		
//		select = new ImageView[pageSize];
//		select[0] = (ImageView)findViewById(R.id.selectbg0);
//		select[1] = (ImageView)findViewById(R.id.selectbg1);
//		select[2] = (ImageView)findViewById(R.id.selectbg2);
//		select[3] = (ImageView)findViewById(R.id.selectbg3);
//		select[4] = (ImageView)findViewById(R.id.selectbg4);
//		select[5] = (ImageView)findViewById(R.id.selectbg5);
//		select[6] = (ImageView)findViewById(R.id.selectbg6);
//		/*select[0].setOnFocusChangeListener(new OnFocusChangeListener(){
//
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				// TODO Auto-generated method stub
//				//System.out.println("=======hasFocus========="+hasFocus);
//				EPDRefresh.refreshDUDither(); 
//				if(hasFocus == true){
//					selectIndex = 2;
//					if(themeNum == 1){
//						select[0].setImageResource(R.drawable.friendlistfoucs);
//					}else if(themeNum == 2){
//						select[0].setImageResource(R.drawable.friendlistfoucs_ui2);
//					}
//					
//				}else{
//					if(themeNum == 1){
//						select[0].setImageResource(R.drawable.friendlist);
//					}else if(themeNum == 2){
//						select[0].setImageResource(R.drawable.friendlist_ui2);
//					}
//					
//				}
//			}
//			
//		});
//		
//		select[1].setOnFocusChangeListener(new OnFocusChangeListener(){
//
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				// TODO Auto-generated method stub
//				//System.out.println("=======hasFocus========="+hasFocus);
//				EPDRefresh.refreshDUDither(); 
//				if(hasFocus == true){
//					selectIndex = 3;
//					if(themeNum == 1){
//						select[1].setImageResource(R.drawable.friendlistfoucs);
//					}else if(themeNum == 2){
//						select[1].setImageResource(R.drawable.friendlistfoucs_ui2);
//					}
//					
//				}else{
//					if(themeNum == 1){
//						select[1].setImageResource(R.drawable.friendlist);
//					}else if(themeNum == 2){
//						select[1].setImageResource(R.drawable.friendlist_ui2);
//					}
//					
//				}
//			}
//			
//		});
//		
//		select[2].setOnFocusChangeListener(new OnFocusChangeListener(){
//
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				// TODO Auto-generated method stub
//				//System.out.println("=======hasFocus========="+hasFocus);
//				EPDRefresh.refreshDUDither(); 
//				if(hasFocus == true){
//					selectIndex = 4;
//					if(themeNum == 1){
//						select[2].setImageResource(R.drawable.friendlistfoucs);
//					}else if(themeNum == 2){
//						select[2].setImageResource(R.drawable.friendlistfoucs_ui2);
//					}
//					
//				}else{
//					if(themeNum == 1){
//						select[2].setImageResource(R.drawable.friendlist);
//					}else if(themeNum == 2){
//						select[2].setImageResource(R.drawable.friendlist_ui2);
//					}
//					
//				}
//			}
//			
//		});
//		
//		select[3].setOnFocusChangeListener(new OnFocusChangeListener(){
//
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				// TODO Auto-generated method stub
//				//System.out.println("=======hasFocus========="+hasFocus);
//				EPDRefresh.refreshDUDither(); 
//				if(hasFocus == true){
//					selectIndex = 5;
//					if(themeNum == 1){
//						select[3].setImageResource(R.drawable.friendlistfoucs);
//					}else if(themeNum == 2){
//						select[3].setImageResource(R.drawable.friendlistfoucs_ui2);
//					}
//					
//				}else{
//					if(themeNum == 1){
//						select[3].setImageResource(R.drawable.friendlist);
//					}else if(themeNum == 2){
//						select[3].setImageResource(R.drawable.friendlist_ui2);
//					}
//					
//				}
//			}
//			
//		});
//		
//		select[4].setOnFocusChangeListener(new OnFocusChangeListener(){
//
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				// TODO Auto-generated method stub
//				//System.out.println("=======hasFocus========="+hasFocus);
//				EPDRefresh.refreshDUDither(); 
//				if(hasFocus == true){
//					selectIndex = 6;
//					if(themeNum == 1){
//						select[4].setImageResource(R.drawable.friendlistfoucs);
//					}else if(themeNum == 2){
//						select[4].setImageResource(R.drawable.friendlistfoucs_ui2);
//					}
//					
//				}else{
//					if(themeNum == 1){
//						select[4].setImageResource(R.drawable.friendlist);
//					}else if(themeNum == 2){
//						select[4].setImageResource(R.drawable.friendlist_ui2);
//					}
//					
//				}
//			}
//			
//		});
//		
//		select[5].setOnFocusChangeListener(new OnFocusChangeListener(){
//
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				// TODO Auto-generated method stub
//				//System.out.println("=======hasFocus========="+hasFocus);
//				EPDRefresh.refreshDUDither(); 
//				if(hasFocus == true){
//					selectIndex = 7;
//					if(themeNum == 1){
//						select[5].setImageResource(R.drawable.friendlistfoucs);
//					}else if(themeNum == 2){
//						select[5].setImageResource(R.drawable.friendlistfoucs_ui2);
//					}
//					
//				}else{
//					if(themeNum == 1){
//						select[5].setImageResource(R.drawable.friendlist);
//					}else if(themeNum == 2){
//						select[5].setImageResource(R.drawable.friendlist_ui2);
//					}
//					
//				}
//			}
//			
//		});
//		
//		select[6].setOnFocusChangeListener(new OnFocusChangeListener(){
//
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				// TODO Auto-generated method stub
//				//System.out.println("=======hasFocus========="+hasFocus);
//				EPDRefresh.refreshDUDither(); 
//				if(hasFocus == true){
//					selectIndex = 8;
//					if(themeNum == 1){
//						select[6].setImageResource(R.drawable.friendlistfoucs);
//					}else if(themeNum == 2){
//						select[6].setImageResource(R.drawable.friendlistfoucs_ui2);
//					}
//					
//				}else{
//					if(themeNum == 1){
//						select[6].setImageResource(R.drawable.friendlist);
//					}else if(themeNum == 2){
//						select[6].setImageResource(R.drawable.friendlist_ui2);
//					}
//					
//				}
//			}
//			
//		});*/
//		/*
//		send.setOnFocusChangeListener(new OnFocusChangeListener() {
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				// TODO Auto-generated method stub
//				//System.out.println("=======hasFocus========="+hasFocus);
//				EPDRefresh.refreshDUDither(); 
//				if(hasFocus == true){
//					selectIndex = 1;
//					if(themeNum == 1){
//						send.setBackgroundResource(R.drawable.buttonstyle2_2);
//					}
//				}else{
//					if(themeNum == 1){
//						send.setBackgroundResource(R.drawable.buttonstyle2_1);
//					}
//				}
//			}
//		});*/
//		
//	
//	
//	}
//	
//	public void clear(){
//		selectIndex = -1;
//		pages = -1;
//		curpage = -1;
//		for(int i=0;i<pageSize;i++){
//			checkButton[i].setVisibility(View.INVISIBLE);
//			phoneNum[i].setVisibility(View.INVISIBLE);
//			state[i].setVisibility(View.INVISIBLE);
//			checkState[i] = false;
//		
//				checkButton[i].setBackgroundResource(R.drawable.notcheck);
//		
//		}
//	}
//	
//	public void getData(){
//		StrGPBI = "";
//		StrGPBI = SubscribeProcess.network("getPresentBookInfo", contentID, null, null,
//                null);
//        if (StrGPBI.substring(0, 10).contains("Exception")) {
//        	
//        	final PviAlertDialog pd = new PviAlertDialog(getParent());
//            pd.setTitle(getResources().getString(R.string.my_friend_hint));
//            pd.setMessage("获取书籍赠送信息出错！");
//            pd.setTimeout(4000);
//            pd.show();
//            return;
//            //pd.getWindow().setLayout(300, 200);
//        }
//        
//		GetFriendList("1","10000");
//		getUnconfirmedFriendList("1","10000");
//		
//		if((count1 + count2) % pageSize == 0){
//			pages = (count1 + count2) / pageSize;
//		}else{
//			pages = (count1 + count2) / pageSize + 1;
//		}
//		curpage = 0;
//	}
//	
//	public void showInfo(){
//		Thread thread = new Thread()
//		{
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//			    mHandler.sendEmptyMessage(INIT_DATA);
//				showme();
//				super.run();
//			}
//			
//		};
//		thread.start();
//	}
//	
//	public void initData(){
//		
//		for(int i=0;i<pageSize;i++){
//			r[i].setVisibility(View.GONE);
//		
//				checkButton[i].setBackgroundResource(R.drawable.notcheck);
//		
//		}
//		
//		String fee1 = SubscribeProcess.getvalue(StrGPBI.substring(20), "description");
//	    String fee2 = SubscribeProcess.getvalue(StrGPBI.substring(20), "presentDepict");
//	    String fee = fee1 + "\n" + fee2;
//	        
//		presentTitle.setText(fee);
//		mtv_CurPage.setText(String.valueOf(curpage+1));
//		
//		if(pages==0){
//			mtv_Pages.setText("1");
//		}else{
//			mtv_Pages.setText(String.valueOf(pages));
//		}
//		
//		
//		list1Size = msnList1.size();
//		
//		msnList1.addAll(msnList2);
//		
//		size = (1+curpage)*pageSize<msnList1.size()?(1+curpage)*pageSize:msnList1.size();
//		for(int i=curpage * pageSize;i<size;i++){
//			r[i % pageSize].setVisibility(View.VISIBLE);
//			
//			checkButton[i % pageSize].setVisibility(View.VISIBLE);
//			phoneNum[i % pageSize].setVisibility(View.VISIBLE);
//			state[i % pageSize].setVisibility(View.VISIBLE);
//			
//			phoneNum[i % pageSize].setText(msnList1.get(i));
//			
//			if(i<list1Size){
//				state[i % pageSize].setText("已确认");
//			}else{
//				state[i % pageSize].setText("待验证");
//			}
//			
//			if(selectList.contains(msnList1.get(i))){
//				
//		
//					checkButton[i % pageSize].setBackgroundResource(R.drawable.check);
// 				
//			}
//		}
//		
//	}
//	public void initData1(){
//		
//		for(int i=0;i<pageSize;i++){
//			r[i].setVisibility(View.GONE);
//
//				checkButton[i].setBackgroundResource(R.drawable.notcheck);
//	
//		}
//		
//		String fee1 = SubscribeProcess.getvalue(StrGPBI.substring(20), "description");
//	    String fee2 = SubscribeProcess.getvalue(StrGPBI.substring(20), "presentDepict");
//	    String fee = fee1 + "\n" + fee2;
//	        
//		presentTitle.setText(fee);
//		mtv_CurPage.setText(String.valueOf(curpage+1));
//		
//		if(pages==0){
//			mtv_Pages.setText("1");
//		}else{
//			mtv_Pages.setText(String.valueOf(pages));
//		}
//		
//		size = (1+curpage)*pageSize<msnList1.size()?(1+curpage)*pageSize:msnList1.size();
//		for(int i=curpage * pageSize;i<size;i++){
//			r[i % pageSize].setVisibility(View.VISIBLE);
//			
//			checkButton[i % pageSize].setVisibility(View.VISIBLE);
//			phoneNum[i % pageSize].setVisibility(View.VISIBLE);
//			state[i % pageSize].setVisibility(View.VISIBLE);
//			
//			phoneNum[i % pageSize].setText(msnList1.get(i));
//			
//			if(i<list1Size){
//				state[i % pageSize].setText("已确认");
//			}else{
//				state[i % pageSize].setText("待验证");
//			}
//			
//			if(selectList.contains(msnList1.get(i))){
//				
//			
//					checkButton[i % pageSize].setBackgroundResource(R.drawable.check);
// 			
//			}
//		}
//		
//	}
//	private void showAlert(String message){
//    	if(loadPd!=null && loadPd.isShowing()){
//    		loadPd.dismiss();
//    	}
//    	loadPd = new PviAlertDialog(getParent());
//    	loadPd.setTitle(getResources().getString(R.string.my_friend_hint));
//    	loadPd.setMessage(message);
//    	loadPd.setCanClose(true);
//        //pd.setTimeout(4000);
//    	loadPd.show();
//    }
//	
//	@Override
//	protected void onStart() {
//		// TODO Auto-generated method stub
//		super.onStart();
//		
//		clear();
//		showTip("正在加载数据...");
//	    new Thread(){
//	    	public void run() {
//	    		getData();
//	    		mHandler.sendEmptyMessage(1001);
//	    	};
//	    }.start();
//		
//		/*
//		loadPd = new PviAlertDialog(getParent());
//		loadPd.setTitle(getResources().getString(R.string.my_friend_hint));
//		loadPd.setMessage(getResources().getString(R.string.kyleHint05));
//		//loadPd.setTimeout(4000);
//		loadPd.show();
//		
//		mHandler.post(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				clear();
//				getData();
//				initData();
//			}
//		});
//
//		mHandler.sendEmptyMessage(301);*/
//		
//		/*
//		clear();
//		getData();
//		initData();
//		*/
//	}
//	
//	private boolean GetFriendList(String start, String count) {
//		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
//		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
//		//		String start = String.valueOf((currentPage-1)*itemPerPage+1);
//		//		String count = String.valueOf(itemPerPage);
//		//		Log.d("Reader", start);
//		//		Log.d("Reader", count);
//		ahmNamePair.put("start", start);
//		ahmNamePair.put("count", count);
//
//		HashMap responseMap = null;
//
//		msnList1.clear();
//		try {
//			// 以POST的形式连接请求
//			responseMap = CPManager.getFriendList(ahmHeaderMap, ahmNamePair);
//			if (!responseMap.get("result-code").toString().contains(
//			"result-code: 0")) {
//				Message msg = new Message();
//				msg.what = 9993;
//				Bundle b = new Bundle();
//				b.putString("error", Error.getErrorDescriptionForContent(responseMap.get("result-code").toString()));
//				msg.setData(b);
//				mHandler.sendMessage(msg);
//				//pd.getWindow().setLayout(300, 200);
//				Logger.i("GetFriendList Error Code: ",responseMap.get("result-code").toString());
//				return false;
//			}
//		} catch (HttpException e) {
//			// 连接异常 ,一般原因为 URL错误
//			e.printStackTrace();
//			mHandler.sendEmptyMessage(9991);
//            //pd.getWindow().setLayout(300, 200);
//            Logger.e("GetFriendList http Exception: ",e.toString());
//			return false;
//		} catch (IOException e) {
//			// IO异常 ,一般原因为网络问题
//			e.printStackTrace();
//			mHandler.sendEmptyMessage(9991);
//           // pd.getWindow().setLayout(300, 200);
//            Logger.e("GetFriendList IO Exception: ",e.toString());
//			return false;
//		}
//
//
//		byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
//
////		try {
////
////			
////			Log.d("Reader", "返回的XML为：");
////			
////			Log.d("Reader", CPManagerUtil
////					.getStringFrombyteArray(responseBody));
////		} catch (UnsupportedEncodingException e) {
////			e.printStackTrace();
////			Toast.makeText(this, "GetFriendList Exception: "
////					+ e.toString(),	Toast.LENGTH_SHORT).show();
////			//			return false;
////		}
//
//		// 根据返回字节数组构造DOM
//		Document dom = null;
//		try {
//			dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
//		} catch (ParserConfigurationException e) {
//			e.printStackTrace();
//			mHandler.sendEmptyMessage(9992);
//			Logger.e("Menu", "Dom addFavorite Exception: "+ e.toString());
//			//			return false;
//		} catch (SAXException e) {
//			e.printStackTrace();
//			mHandler.sendEmptyMessage(9992);
//			Logger.e("Menu", "Dom addFavorite Exception: "+ e.toString());
//			//			return false;
//		} catch (SocketTimeoutException e) {
//        	
//			mHandler.sendEmptyMessage(9992);
//			//handler2.sendEmptyMessage(0);
//            //显示获取用户信息失败提示框
//            return false;
//           }
//		catch (IOException e) {
//			e.printStackTrace();
//			mHandler.sendEmptyMessage(9992);
//			Logger.e("Menu", "Dom GetFriendList Exception: "+ e.toString());
//			//			return false;
//		}
//		Element root = dom.getDocumentElement();
//		// 以下为解释XML的代码
//		// ......
//
//		NodeList nl = root.getElementsByTagName("Friend");
//		Element friendtemp = null;
//		NodeList friendinfotemp = null;
//		HashMap<String, Object> map = null;
//		for (int i = 0; i < nl.getLength(); i++) {
//			map = new HashMap<String, Object>();
//			friendtemp = (Element) nl.item(i);
//			friendinfotemp = friendtemp
//			.getElementsByTagName("msisdn");
//			/*map.put("msisdn", friendinfotemp.item(0).getFirstChild().getNodeValue());
//			// Log.i("Menu", friendinfotemp.getFirstChild().getNodeValue());
//			friendinfotemp =  friendtemp.getElementsByTagName("nickName");
//			map.put("nickName", friendinfotemp.item(0).getFirstChild().getNodeValue());
//
//			friendlist.add(map);*/
//			msnList1.add( friendinfotemp.item(0).getFirstChild().getNodeValue());
//		}
//		nl = root.getElementsByTagName("totalRecordCount");
//		this.count1 = Integer.parseInt(nl.item(0).getFirstChild().getNodeValue());
//		//		this.totalPage = this.totalPage + Integer.parseInt(nl.item(0).getFirstChild().getNodeValue());
//		return true;
//	}
//
//	private boolean getUnconfirmedFriendList(String start, String count) {
//		this.msnList2.clear();
//		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
//		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
//
//		ahmNamePair.put("start", start);
//		ahmNamePair.put("count", count);
//
//		HashMap responseMap = null;
//
//		try {
//			// 以POST的形式连接请求
//			responseMap = CPManager.getUnconfirmedFriendList(ahmHeaderMap,
//					ahmNamePair);
//			if (!responseMap.get("result-code").toString().contains(
//			"result-code: 0")) {
//				Message msg = new Message();
//				msg.what = 999;
//				Bundle b = new Bundle();
//				b.putString("error", Error.getErrorDescriptionForContent(responseMap.get("result-code").toString()));
//				msg.setData(b);
//				mHandler.sendMessage(msg);
//				/*
//				final PviAlertDialog pd = new PviAlertDialog(getParent());
//	            pd.setTitle(getResources().getString(R.string.my_friend_hint));
//	            pd.setMessage(getResources().getString(R.string.my_friend_connecting));
//	            pd.setTimeout(4000);
//	            pd.show();
//	            pd.getWindow().setLayout(300, 200);
//	            Logger.i("getUnconfirmedFriendList Error Code: ",responseMap.get("result-code").toString());
//	            */
//				return false;
//			}
//		} catch (HttpException e) {
//			// 连接异常 ,一般原因为 URL错误
//			e.printStackTrace();
//			mHandler.sendEmptyMessage(9991);
//			return false;
//		}  catch (SocketTimeoutException e) {
//        	
//			mHandler.sendEmptyMessage(9991);
//            //显示获取用户信息失败提示框
//            return false;
//           }catch (IOException e) {
//			// IO异常 ,一般原因为网络问题
//			e.printStackTrace();
//			mHandler.sendEmptyMessage(9991);
//			return false;
//		}
//		byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
//
////		try {
////			System.out.println("返回的XML为：");
////			System.out.println(CPManagerUtil
////					.getStringFrombyteArray(responseBody));
////		} catch (UnsupportedEncodingException e) {
////			e.printStackTrace();
////			Toast.makeText(this, "getUnconfirmedFriendList Exception: "
////					+ e.toString(),	Toast.LENGTH_SHORT).show();
////			Log.i("sssss", "222" + e.toString());
////			//			return false;
////		}
//		// 根据返回字节数组构造DOM
//		Document dom = null;
//		try {
//			dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
//		} catch (ParserConfigurationException e) {
//			e.printStackTrace();
//			mHandler.sendEmptyMessage(9992);
//			Logger.e("Menu", "Dom getUnconfirmedFriendList Exception1: "+ e.toString());
//			//			return false;
//		}  catch (SocketTimeoutException e) {
//        	
//			mHandler.sendEmptyMessage(9992);
//			//handler2.sendEmptyMessage(0);
//            //显示获取用户信息失败提示框
//            return false;
//           }catch (SAXException e) {
//			e.printStackTrace();
//			mHandler.sendEmptyMessage(9992);
//			Logger.e("Menu", "Dom getUnconfirmedFriendList Exception2: "+ e.toString());
//			//			return false;
//		} catch (IOException e) {
//			e.printStackTrace();
//			mHandler.sendEmptyMessage(9992);
//			Logger.e("Menu", "Dom getUnconfirmedFriendList Exception3: "+ e.toString());
//			//			return false;
//		}
//
//		Element root = dom.getDocumentElement();
//		// 以下为解释XML的代码
//		// ......
//
//		try
//		{
//			//			
//			NodeList nl = root.getElementsByTagName("Friend");
//			if(nl == null)
//			{
//				return true;
//			}
//			Element friendtemp = null;
//			NodeList friendinfotemp = null;
//			for (int i = 0; i < nl.getLength(); i++) {
//				friendtemp = (Element) nl.item(i);
//				friendinfotemp = (NodeList) friendtemp
//				.getElementsByTagName("msisdn");
//				msnList2.add(friendinfotemp.item(0).getFirstChild()
//						.getNodeValue());
//			}
//
//			nl = root.getElementsByTagName("totalRecordCount");
//			this.count2 = Integer.parseInt(nl.item(0).getFirstChild().getNodeValue());
//		}
//		catch(Exception e)
//		{
//			Logger.e("Menu", "11111" + e.toString());	
//			return false;
//		}
//
//		return true;
//	}
//
//	
//	
//
//public boolean onKeyDown(int keyCode, KeyEvent event) {
//    	
//    	
//        if (keyCode == KeyEvent.KEYCODE_MENU) {
//            menupan();
//            return true;
//        }
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            // 通知框架返回上一个子activty
//            sendBroadcast(new Intent(MainpageActivity.BACK));
//            return true;
//        }
//        
//        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
//        	boolean hasFocus = r[0].hasFocus() || r[1].hasFocus() || r[2].hasFocus()
//			   || r[3].hasFocus() || r[4].hasFocus() || r[5].hasFocus()
//			   || r[6].hasFocus();
//            // 向左翻页
//        	if(curpage > 0 && hasFocus){
//        		if(deviceType == 1){
//     				
//     				findViewById(R.id.presenttitle).invalidate(0, 0, 600,800,UPDATEMODE_4);
//     			
//     			}
//        		curpage  --;
//        	    initData1();
//        	    return true;
//     		}else{
//        		return false;
//        	}
//        	
//        	
//        }
//        
//        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//        	boolean hasFocus = r[0].hasFocus() || r[1].hasFocus() || r[2].hasFocus()
//			   || r[3].hasFocus() || r[4].hasFocus() || r[5].hasFocus()
//			   || r[6].hasFocus();
//             // 向右翻页
//        	if(curpage < pages - 1 && hasFocus){
//        		if(deviceType == 1){
//     				
//     				findViewById(R.id.presenttitle).invalidate(0, 0, 600,800,UPDATEMODE_4);
//     			
//     			}
//        		curpage++;
//        	    initData1();
//         	   return true;
//     		}else{
//        		return false;
//        	}
//        	
//        	 
//        }
//        
//        if(keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
//        	if(selectIndex == 1){
//        		clickListen.onClick(send);
//        	}
//        	else if(selectIndex>=2 && selectIndex<=8 && checkButton[selectIndex-2]!=null){
//        		clickListen.onClick(checkButton[selectIndex-2]);
//        	}
//        }
//       
//        return super.onKeyDown(keyCode, event);
//    }
//  
//    
//    @Override
//    protected void onPause() {
//        // TODO Auto-generated method stub
//        closePopmenu();
//        super.onPause();
//    }
//    
//	private boolean recommend(List friends)
//	{
//	
//		String StrAULW = SubscribeProcess.network("presentBook", contentID,
//                friendInput.getText().toString().substring(0,11), friendMessage.getText()
//                        .toString(), null);
//		hideInput();
//        if (StrAULW.substring(0, 10).contains("Exception")) {
//        	PviAlertDialog pd = new PviAlertDialog(
//                    getParent());
//            pd.setCanClose(true);
//            pd.setTitle("温馨提示");
//            pd.setMessage("书籍赠送失败");
//            pd.show();
//            return false;
//        } else if (StrAULW.substring(0, 19)
//                .contains("0000")) {
//            PviAlertDialog pd = new PviAlertDialog(
//                    getParent());
//            pd.setCanClose(true);
//            pd.setTitle("温馨提示");
//            pd.setMessage("书籍赠送成功");
//            pd.show();
//            return true;
//        }else{
//        	 PviAlertDialog pd = new	 PviAlertDialog(
//                     getParent());
//             pd.setCanClose(true);
//             pd.setTitle("温馨提示");
//             pd.setMessage(Error.getErrorDescriptionForContent(StrAULW.substring(0, 19)));
//             pd.show();
//             return true;
//        }
//	}
//	private void showme()
//	{
//		Intent tmpIntent = new Intent(
//				MainpageActivity.SHOW_ME);
//		Bundle bundleToSend = new Bundle();
//		bundleToSend.putString("sender",this.getClass().getName()); //TAB内嵌activity类的全名
//		tmpIntent.putExtras(bundleToSend);
//		sendBroadcast(tmpIntent);
//		tmpIntent = null;
//		bundleToSend = null;
//		Intent intent1 = new Intent(MainpageActivity.HIDE_TIP);
//		sendBroadcast(intent1);
//
//	}
//}
