//0122 11:32
package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.data.common.EPDRefresh;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * ���ѹ���
 * @author FLY
 *
 */
public class MyFriendListActivity extends PviActivity implements Pageable {
	
	public final String TAG = "MyFriendListActivity";
	
	PviDataList listView;               //viewʵ��
	ArrayList<PviUiItem[]> list;        //����б�ؼ����ڲ���Ϣ��ArrayList���ÿ�У�ÿ��ʹ��һ�����鱣����PviUiItem
	PviBottomBar  pbb;     //���ÿ�ܵײ�������
	
	int pageSize = 7;
	
	int pages;
	
	int curpage;
	
	boolean selectAll = true;
	
	EditText phoneNumEditText;
	
	Button addFriendButton;
	
	Button showButton;
	
	Button selectAllButton;
	
	Button deleteButton;
	
	private String selectType;
	
	boolean[] checkState;
	
	String[] phoneNum;
	
	//private PopupWindow popmenu;
	
    //private View popmenuView;
	private ArrayList<String> msnList = new ArrayList<String>();
	
	private ArrayList<String> msnList1 = new ArrayList<String>();
	
	private ArrayList<String> msnList2 = new ArrayList<String>();
	
	private ArrayList<String> nameList1 = new ArrayList<String>();
	
	 private HashMap<String, Object> userinfo = new HashMap<String, Object>();
	 
	 private Runnable enableButtons = new Runnable(){

	        @Override
	        public void run() {
	        	addFriendButton.setClickable(true);
	        	showButton.setClickable(true);
	        	selectAllButton.setClickable(true);
	        	deleteButton.setClickable(true);
	        
	        }
	        
	    };
	    
	    private Runnable disableButtons = new Runnable(){

	        @Override
	        public void run() {
	        	addFriendButton.setClickable(false);
	        	showButton.setClickable(false);
	        	selectAllButton.setClickable(false);
	        	deleteButton.setClickable(false);
	        }
	        
	    };
	
	
	int count1 = 0;
	
	int count2 = 0;
	
	private String deleteNum;
	
	private static final int GET_DATA = 101;
    private static final int CLOSE_PD = 102;
    public static final int SHOW_PD_LOADING = 103;
    public static final int SET_UI_DATA = 104;

    public static final int ERR_CONNET_EXP = 201;//���������쳣
    public static final int ERR_CONNET_TIMEOUT = 202;//���ӳ�ʱ
    public static final int ERR_RETCODE_NOT0 = 203; //�ӿڷ������0
    public static final int ERR_CONNECT_FAILED = 204;//����ʧ��
    public static final int ERR_CHECK_PHONENUM = 205;//�ֻ����������
    public static final int ERR_XML_PARSER = 206;   //xml��������
    
    private PviAlertDialog pd;
    
    private PviAlertDialog loadPd;
    
    String errMsg = null;
    
    ImageView[] select;
	
	int selectIndex = -1;
  
    private Handler mHandler = new H();
    private OnClickListener clickListen = new MyCLickListen();
    private class H extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
            
            case 1:
            	showAlert("��Ӻ���ʧ�ܣ�"+errMsg);
            	errMsg = null;
    			break;
            case 2:
            	showAlert("��Ӻ��ѳɹ�",false);
    			break; 
            case 3:
            	//showAlert("��Ӻ��������쳣,������",true);
            	showError();
    			break;
    			
            case 4:
            	showAlert("ɾ������"+deleteNum+"ʧ�ܣ�"+errMsg,true);
            	errMsg = null;
    			break;
            case 5:
            	showAlert("ɾ������"+deleteNum+"�ɹ�",true);
    			break;
            case 6:
            	//showAlert("ɾ������"+deleteNum+"�����쳣,������",true);
            	showError();
    			break;
    		
            case 7:
            	showAlert("���غ���ʧ��"+errMsg,true);
            	errMsg = null;
    			break; 
    		case 8:
            	showAlert("���غ��ѳɹ�",true);
            	errMsg = null;
    			break; 
    		case 9:
            	//showAlert("���غ����쳣,������",true);
    			showError();
            	errMsg = null;
    			break; 
    		case 10:
            	showAlert("����û��ѡ��Ҫɾ���ĺ���",true);
            	errMsg = null;
    			break; 
    		case 11:
            	showAlert("����δѡ��Ҫ��ѯ��Ϣ�ĺ���",true);
            	errMsg = null;
    			break; 
            case 1001:
            	
            	 initData();
            	 mHandler.post(enableButtons);
                if(pd!=null && pd.isShowing()){
            		pd.dismiss();
            	}
                hideTip();
                break;
                
            case 1002:
                initData();
                
                clear();
        		//showAlert("���ڼ�������...");
                showMessage("���ڼ�������...");
        	    new Thread(){
        	    	public void run() {
        	    		getData();
        	    		mHandler.sendEmptyMessage(1001);
        	    		showMe();
        	    	};
        	    }.start();
        	    
                break;
            case 1003:
            	showAlert("��ѯ����"+deleteNum+"��Ϣʧ�ܣ�"+errMsg,true);
            	errMsg = null;
    			break;
    			
            case 1004: //��ʾ������ϸ��Ϣ
            	if(pd!=null && pd.isShowing()){
             		pd.dismiss();
             	}
            	PviAlertDialog showDialog = new PviAlertDialog(getParent());
            	showDialog.setTitle("������Ϣ");
          
            	LayoutInflater inflater = getLayoutInflater();   
                //LayoutInflater inflater = (LayoutInflater)    
                //mContext.getSystemService(LAYOUT_INFLATER_SERVICE);   
                View layout = inflater.inflate(R.layout.showfriendinfo,null);  
                
                TextView phonename = (TextView)layout.findViewById(R.id.phonename);
                TextView nickmane = (TextView)layout.findViewById(R.id.nickname);
                TextView age = (TextView)layout.findViewById(R.id.age);
                TextView sex = (TextView)layout.findViewById(R.id.sex);
                TextView address = (TextView)layout.findViewById(R.id.address);
                TextView signature = (TextView)layout.findViewById(R.id.signature);
                
                if(userinfo.get("Mobile")!=null){
                	phonename.setText((String)userinfo.get("Mobile"));
                }
                
                if(userinfo.get("NickName")!=null){
                	nickmane.setText((String)userinfo.get("NickName"));
                }
                
                if(userinfo.get("Age")!=null){
                	age.setText((String)userinfo.get("Age"));
                }
                
                String str = "";
                if(userinfo.get("Province")!=null){
                	str = (String)userinfo.get("Province") + "ʡ";
                	
                }
                if(userinfo.get("City")!=null){
                	str = str + (String)userinfo.get("City") + "��";
                	
                }
                address.setText(str);
                
                if(userinfo.get("Sex")!=null){
                	sex.setText((String)userinfo.get("Sex"));
                }
                
                if(userinfo.get("Description")!=null){
                	signature.setText((String)userinfo.get("Description"));
                }
                
            	showDialog.setView(layout);
            	showDialog.setCanClose(true);
                //pd.setTimeout(4000);
            	showDialog.show();
    			break;
            default:
                ;
            }

            super.handleMessage(msg);
        }
    }
    
    public void showError(){
    	PviAlertDialog pd2 = new PviAlertDialog(getParent());
		pd2.setTitle("��ܰ��ʾ");
		pd2.setMessage("���������������",Gravity.CENTER);
		pd2.setCanClose(false);
		pd2.setButton(DialogInterface.BUTTON_POSITIVE, "ȷ��",
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
	
    class MyCLickListen implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			if(v.getId() == R.id.showinfo){
				
				//showAlert("���ڲ�ѯ������ϸ��Ϣ...");
				showMessage("���ڲ�ѯ������ϸ��Ϣ...");
				 new Thread(){
        	    	public void run() {
        	    		
        	    		String num = null;
	   					for(int i=0;i<pageSize;i++){
	   						
	   						if(checkState[i] == true){
	   							num = phoneNum[i];
	   							break;
	   				
	   						}
	   					}
	   					
	   					if(num == null){
	   						//showAlert("��δѡȡҪ��ѯ���ѣ�");
	   						mHandler.sendEmptyMessage(11);
	   						return;
	   					}else{
	   						selectFriend(num);
	   						System.out.println("===="+userinfo);
	   					}
	   					
	        	    		
	        	    	};
				 }.start();
				
				
				
				
				
			}
			if(v.getId() == R.id.addfriend){

				String phone = phoneNum.toString();
				String msisdn = phoneNumEditText.getText().toString();
				String expr = "[1][0-9]{10}";
				if(!msisdn.matches(expr)){
					showAlert("������ĺ���"+msisdn+"����ȷ��");
					return;
				}else if(msnList!=null && msnList.contains(msisdn)){
					showAlert("������ĺ���"+msisdn+"����ӣ�");
					return;
				}else{
					final String temp = msisdn;
					final int page = curpage;
			     	for(int i=0;i<pageSize;i++){
			     		checkState[i] = false;
					}
					 showMessage("������Ӻ���...");   
					 mHandler.post(disableButtons);
	            	    new Thread(){
	            	    	public void run() {
	            	    		addFriend(temp);
	        			     	getData();
	        			     	mHandler.post(enableButtons);
	        			     	curpage = page;
	            	    		mHandler.sendEmptyMessage(1002);
	            	    	};
	            	  }.start();
			     	if(phoneNumEditText != null){
						phoneNumEditText.setText("");
					}
				}
			}else if(v.getId() == R.id.select){
				
				if(selectAll == true){				
					for(int i=0;i<pageSize;i++){
						PviUiItem[] p = (PviUiItem[])listView.mUiList.get(i);
						checkState[i] = true;
						p[3].res = R.drawable.check;
					}
					selectAll = false;
				}else{
					for(int i=0;i<pageSize;i++){
						PviUiItem[] p = (PviUiItem[])listView.mUiList.get(i);
						checkState[i] = false;
						p[3].res = R.drawable.notcheck;
					}
					selectAll = true;
				}
				listView.invalidate();
			}else if(v.getId() == R.id.delete){
		
				 
				 new Thread(){
         	    	public void run() {
         	    		boolean isdelete = false;
         	    		//System.out.println("=========delete3=============");
    					for(int i=0;i<pageSize;i++){
    						//System.out.println("=========checkState[i]============="+checkState[i]);
    						if(checkState[i] == true){
    							deleteNum = phoneNum[i];
    							deleteFriend(phoneNum[i]);
    							isdelete = true;
    				
    						}
    					}
    					//System.out.println("=========delete4=============");
    					if(isdelete == true){
    						showMessage("����ɾ������...");
    						int page = curpage;
    						getData();
    						if(page > pages - 1){
    							page = pages - 1;
    						}
    						mHandler.sendEmptyMessage(1002);
    					}else{
    						mHandler.sendEmptyMessage(10);
    					}
    					//System.out.println("=========delete5=============");
         	    		
         	    	};
				 }.start();
			
				
			}
			
		}
    	
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.friendliststyle2);
    
    	
		pbb = ((GlobalVar)getApplication()).pbb;
    	super.onCreate(savedInstanceState);	
    	
	}
	
	public void clear(){
		pages = -1;
		curpage = -1;
		for(int i=0;i<pageSize;i++){
			checkState[i] = false;
			phoneNum[i] = null;
		}
	}
	
	public void getData(){
				  msnList.clear();
	    		  GetFriendList("1","10000");
	    		  getUnconfirmedFriendList("1","10000");
	    		  msnList.addAll(msnList1);
	    		  msnList.addAll(msnList2);
	    		  //msnList1.addAll(msnList2);
	    		  if((count1 + count2) % pageSize == 0){
	    				pages = (count1 + count2) / pageSize;
	    		  }else{
	    				pages = (count1 + count2) / pageSize + 1;
	    		  }
	    		  curpage = 0;
	 }
	
	public void initData(){
		
		
		//======================���·�ҳ
		updatePagerinfo((curpage+1)+" / "+(pages==0?1:pages));

		//======================��������
		TextView norecord = (TextView)findViewById(R.id.norecord);
    	int list1Size = msnList1.size();
		int size = (1+curpage)*pageSize<msnList.size()?(1+curpage)*pageSize:msnList.size();
		if(size == 0){
			norecord.setVisibility(View.VISIBLE);
		}else{
			norecord.setVisibility(View.GONE);
		}
		
		list.clear();
		for(int i=curpage * pageSize;i<size;i++){
			final PviUiItem[] items = new PviUiItem[]{
					 	new PviUiItem("text1"+i, 0, 10, 10, 280, 50, "�ֻ���", false, true, null),
					 	new PviUiItem("text2"+i, 0, 280, 10, 170, 50, "", false, true, null),
		                new PviUiItem("text3"+i, 0, 450, 10, 85, 50, "״̬", false, true, null),
		                new PviUiItem("icon"+i, R.drawable.notcheck, 535,10,25,50, null, false, true, null),
		    };
			items[0].text = msnList.get(i);
			 
			phoneNum[i % pageSize] = msnList.get(i);
			
			if(i<list1Size){
				items[2].text = "��ȷ��";
				if(nameList1!=null && (i % pageSize)<nameList1.size() && nameList1.get(i % pageSize)!=null){
					items[1].text = nameList1.get(i % pageSize);
				}else{
					items[1].text = "";
				}
			}else{
				items[2].text = "����֤";
			}
			
			final int k = i;
			OnClickListener l = new OnClickListener(){     //new һ��click�¼�����

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                	if(checkState[k % pageSize] == false){
                		checkState[k % pageSize] = true;
                		items[3].res = R.drawable.check;
                		listView.invalidate();
                	}else{
                		checkState[k % pageSize] = false;
                		items[3].res = R.drawable.notcheck;
                		listView.invalidate();
                	}
                }
                
            };
            items[0].l = l;
            items[1].l = l;  
            items[2].l = l;  
            items[3].l = l;  
			list.add(items);
		}
		listView.setData(list);
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		selectType = "onResume";
		clear();
		//showAlert("���ڼ�������...");
		showMessage("���ڼ�������...");
	    new Thread(){
	    	public void run() {
	    		mHandler.post(disableButtons);
	    		getData();
	    		mHandler.sendEmptyMessage(1001);
	    		mHandler.post(enableButtons);
	    		showMe();
	    	};
	    }.start();
	    
	    
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//hideInput();
	}
	
	public void showMe(){
		
		if("onResume".equals(selectType)){
			Intent tmpIntent = new Intent(
	                MainpageActivity.SHOW_ME);
			Bundle bundleToSend = new Bundle();
	        bundleToSend.putString("act", "com.pvi.ap.reader.activity.UserCenterActivity");
	        bundleToSend.putString("actTabName", "���ѹ���");
	        bundleToSend.putString("sender", MyFriendListActivity.this.getClass().getName());
	        tmpIntent.putExtras(bundleToSend);
	        sendBroadcast(tmpIntent);
	        tmpIntent = null;
	        bundleToSend = null;
	        selectType = "";
		}
		
		//���ؼ���������Ϣ
		hideTip();
	}
	
	private boolean GetFriendList(String start, String count) {
	   // mHandler.sendEmptyMessage(SHOW_PD_LOADING);  
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
		nameList1.clear();
		try {
			// ��POST����ʽ��������
			responseMap = CPManager.getFriendList(ahmHeaderMap, ahmNamePair);
			//mHandler.sendEmptyMessage(CLOSE_PD);
			if (!responseMap.get("result-code").toString().contains(
			"result-code: 0")) {
			    //mHandler.sendEmptyMessage(203); 
				//pd.getWindow().setLayout(300, 200);
				errMsg = Error.getErrorDescriptionForContent(responseMap.get("result-code").toString());
				mHandler.sendEmptyMessage(7); 
				//showAlert("��ȡ����ʧ�ܣ�"+Error.getErrorDescriptionForContent(responseMap.get("result-code").toString()));
	            Logger.i("GetFriendList Error Code: ",responseMap.get("result-code").toString());
				return false;
			}
		} catch (HttpException e) {
			// �����쳣 ,һ��ԭ��Ϊ URL����
		    //mHandler.sendEmptyMessage(201); 
			//showAlert("���������쳣");
			mHandler.post(enableButtons);
			mHandler.sendEmptyMessage(9); 
            //pd.getWindow().setLayout(300, 200);
            Logger.e("GetFriendList http Exception: ",e.toString());
			return false;
		} catch (IOException e) {
			// IO�쳣 ,һ��ԭ��Ϊ��������
		    //mHandler.sendEmptyMessage(204); 
			//showAlert("���������쳣");
			mHandler.post(enableButtons);
			mHandler.sendEmptyMessage(9); 
           // pd.getWindow().setLayout(300, 200);
            Logger.e("GetFriendList IO Excepmtion: ",e.toString());
			return false;
		}


		byte[] responseBody = (byte[]) responseMap.get("ResponseBody");

		// ���ݷ����ֽ����鹹��DOM
		Document dom = null;
		try {
			dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
		} catch (ParserConfigurationException e) {
			mHandler.post(enableButtons);
		    mHandler.sendEmptyMessage(206); 
			Logger.e("Menu", "Dom addFavorite Exception: "+ e.toString());
		} catch (SAXException e) {
			mHandler.post(enableButtons);
		    mHandler.sendEmptyMessage(206);
			Logger.e("Menu", "Dom addFavorite Exception: "+ e.toString());
			//			return false;
		} catch (SocketTimeoutException e) {
			mHandler.post(enableButtons);
		    mHandler.sendEmptyMessage(202);
            //��ʾ��ȡ�û���Ϣʧ����ʾ��
            return false;
           }
		catch (IOException e) {
			mHandler.post(enableButtons);
		    mHandler.sendEmptyMessage(204);
			Logger.e("Menu", "Dom GetFriendList Exception: "+ e.toString());
			//			return false;
		}
		Element root = dom.getDocumentElement();
		// ����Ϊ����XML�Ĵ���
		// ......

		NodeList nl = root.getElementsByTagName("Friend");
		Element friendtemp = null;
		NodeList nametemp = null;
		NodeList friendinfotemp = null;
		HashMap<String, Object> map = null;
		for (int i = 0; i < nl.getLength(); i++) {
			map = new HashMap<String, Object>();
			friendtemp = (Element) nl.item(i);
			friendinfotemp = friendtemp
			.getElementsByTagName("msisdn");
			nametemp = friendtemp
			.getElementsByTagName("nickName");
			msnList1.add( friendinfotemp.item(0).getFirstChild().getNodeValue());
			if(nametemp!=null && nametemp.item(0)!=null &&  nametemp.item(0).getFirstChild()!=null){
				nameList1.add( nametemp.item(0).getFirstChild().getNodeValue());
			}
		}
		nl = root.getElementsByTagName("totalRecordCount");
		this.count1 = Integer.parseInt(nl.item(0).getFirstChild().getNodeValue());
		//		this.totalPage = this.totalPage + Integer.parseInt(nl.item(0).getFirstChild().getNodeValue());
		
		return true;
	}

	private boolean getUnconfirmedFriendList(String start, String count) {
		this.msnList2.clear();
		//mHandler.sendEmptyMessage(SHOW_PD_LOADING); 
		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

		ahmNamePair.put("start", start);
		ahmNamePair.put("count", count);

		HashMap responseMap = null;

		try {
			// ��POST����ʽ��������
			responseMap = CPManager.getUnconfirmedFriendList(ahmHeaderMap,
					ahmNamePair);
			mHandler.sendEmptyMessage(CLOSE_PD); 
			if (!responseMap.get("result-code").toString().contains(
			"result-code: 0")) {
			    //mHandler.sendEmptyMessage(203);
	            Logger.i("getUnconfirmedFriendList Error Code: ",responseMap.get("result-code").toString());
				
	            //showAlert("��ȡ����ʧ�ܣ�"+Error.getErrorDescriptionForContent(responseMap.get("result-code").toString()));
	            errMsg = Error.getErrorDescriptionForContent(responseMap.get("result-code").toString());
				mHandler.sendEmptyMessage(7); 
				
	            return false;
			}
		} catch (HttpException e) {
			// �����쳣 ,һ��ԭ��Ϊ URL����
		    //mHandler.sendEmptyMessage(201);
			//showAlert("���������쳣");
			mHandler.post(enableButtons);
			mHandler.sendEmptyMessage(9);
            Logger.e("getUnconfirmedFriendList http Exception: ",e.toString());
			return false;
		}  catch (SocketTimeoutException e) {
			//showAlert("���������쳣");
			mHandler.post(enableButtons);
			mHandler.sendEmptyMessage(9);
		    //mHandler.sendEmptyMessage(202);
            //��ʾ��ȡ�û���Ϣʧ����ʾ��
            return false;
           }catch (IOException e) {
			// IO�쳣 ,һ��ԭ��Ϊ��������
            //mHandler.sendEmptyMessage(ERR_CONNECT_FAILED);
        	//showAlert("���������쳣");
        	mHandler.post(enableButtons);
        	mHandler.sendEmptyMessage(9);
            Logger.e("getUnconfirmedFriendList IO Exception: ",e.toString());
			return false;
		}
		byte[] responseBody = (byte[]) responseMap.get("ResponseBody");


		// ���ݷ����ֽ����鹹��DOM
		Document dom = null;
		try {
			dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
		} catch (ParserConfigurationException e) {
			mHandler.post(enableButtons);
		    mHandler.sendEmptyMessage(ERR_XML_PARSER);
			Logger.e("Menu", "Dom getUnconfirmedFriendList Exception1: "+ e.toString());
			//			return false;
		}  catch (SocketTimeoutException e) {
			mHandler.post(enableButtons);
		    mHandler.sendEmptyMessage(ERR_CONNET_TIMEOUT);
            //��ʾ��ȡ�û���Ϣʧ����ʾ��
            return false;
           }catch (SAXException e) {
        	   mHandler.post(enableButtons);
             mHandler.sendEmptyMessage(ERR_XML_PARSER);
			Logger.e("Menu", "Dom getUnconfirmedFriendList Exception2: "+ e.toString());
			//			return false;
		} catch (IOException e) {
			mHandler.post(enableButtons);
		    mHandler.sendEmptyMessage(ERR_CONNECT_FAILED);
			Logger.e("Menu", "Dom getUnconfirmedFriendList Exception3: "+ e.toString());
			//			return false;
		}

		Element root = dom.getDocumentElement();
		// ����Ϊ����XML�Ĵ���
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
			mHandler.post(enableButtons);
			Logger.e("Menu", "11111" + e.toString());	
			return false;
		}

		return true;
	}

	private boolean addFriend(String msisdn)
	{ 
	    //mHandler.sendEmptyMessage(SHOW_PD_LOADING); 
		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

		ahmNamePair.put("msisdn", msisdn);

		HashMap responseMap = null;
		try {
			//��POST����ʽ��������
			responseMap = CPManager.addFriend(ahmHeaderMap, ahmNamePair);
			//mHandler.sendEmptyMessage(CLOSE_PD); 
			if (!responseMap.get("result-code").toString().contains(
			"result-code: 0")) {
				
				if(responseMap.get("result-code").toString().contains(
					"result-code: 3232")){
					errMsg = "������ĺ�����ƶ����룡";
					
				}else{
					errMsg = Error.getErrorDescriptionForContent(responseMap.get("result-code").toString());
				}
				mHandler.sendEmptyMessage(1); 
				//mHandler.
			}else{
				//showAlert("��Ӻ��ѳɹ�");
				mHandler.sendEmptyMessage(2); 
				
			}
		} catch (HttpException e) {
			mHandler.sendEmptyMessage(3); 
			//�����쳣 ,һ��ԭ��Ϊ URL����
		   // mHandler.sendEmptyMessage(ERR_CONNET_EXP);
			//showAlert("���������쳣");
            Logger.e("addFriend http Exception: ",e);
			return false;
		}  catch (SocketTimeoutException e) {
			mHandler.sendEmptyMessage(3); 
		    //mHandler.sendEmptyMessage(ERR_CONNET_TIMEOUT);
			//showAlert("���������쳣");
			  Logger.e("addFriend SocketTimeoutException Exception: ",e);
            return false;
           }catch (IOException e) {
        	  mHandler.sendEmptyMessage(3); 
			  //IO�쳣 ,һ��ԭ��Ϊ��������
        	  //mHandler.sendEmptyMessage(ERR_CONNECT_FAILED);
        	  //  showAlert("���������쳣");
            Logger.e("addFriend IO Exception: ",e);
			return false;
		}
		byte[] responseBody = (byte[])responseMap.get("ResponseBody");

		return true;
	}

	private boolean deleteFriend(String msisdn)
	{
	    //mHandler.sendEmptyMessage(SHOW_PD_LOADING); 
		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		ahmNamePair.put("msisdn", msisdn);

		HashMap responseMap = null;
		try {
			//��POST����ʽ��������
			responseMap = CPManager.deleteFriend(ahmHeaderMap, ahmNamePair);
			//mHandler.sendEmptyMessage(CLOSE_PD); 
			if (!responseMap.get("result-code").toString().contains(
			"result-code: 0")) {
				
				errMsg = Error.getErrorDescriptionForContent(responseMap.get("result-code").toString());
				mHandler.sendEmptyMessage(4); 
				
			    //mHandler.sendEmptyMessage(ERR_RETCODE_NOT0);
				//showAlert("ɾ������ʧ�ܣ�"+Error.getErrorDescriptionForContent(responseMap.get("result-code").toString()));
	            Logger.e("Delete Error Code: ",responseMap.get("result-code").toString());
				return false;
			}else{
				mHandler.sendEmptyMessage(5); 
				//showAlert("ɾ�����ѳɹ�");
	        }
		} catch (HttpException e) {
			//�����쳣 ,һ��ԭ��Ϊ URL����
		    //mHandler.sendEmptyMessage(ERR_CONNET_EXP);
            Logger.e("Http deleteFriend Exception: ",e.toString());
            //showAlert("���������쳣");
            mHandler.sendEmptyMessage(6); 
			return false;
		}  catch (SocketTimeoutException e) {
		    //mHandler.sendEmptyMessage(ERR_CONNET_TIMEOUT);
			//showAlert("���������쳣");
			mHandler.sendEmptyMessage(6); 
            //��ʾ��ȡ�û���Ϣʧ����ʾ��
            return false;
           }catch (IOException e) {
			//IO�쳣 ,һ��ԭ��Ϊ��������
            //mHandler.sendEmptyMessage(ERR_CONNECT_FAILED);
        	//showAlert("���������쳣");
        	mHandler.sendEmptyMessage(6); 
            Logger.e("IO deleteFriend Exception: ",e.toString());
            
			return false;
		}
		byte[] responseBody = (byte[])responseMap.get("ResponseBody");


		//���ݷ����ֽ����鹹��DOM
		Document dom = null;
		try {
			dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
            Logger.e("Dom configuration deleteFriend Exception: ",e.toString());
			return false;
		} catch (SAXException e) {
			e.printStackTrace();
            Logger.e("SAX deleteFriend Exception: ",e.toString());
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Logger.e("Dom IO deleteFriend Exception: ",e.toString());
			return false;
		}
		Element root = dom.getDocumentElement();
		return true;
	}
	
	
	private boolean selectFriend(String msisdn)
	{
	    //mHandler.sendEmptyMessage(SHOW_PD_LOADING); 
		userinfo.clear();
		
		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		ahmNamePair.put("msisdn", msisdn);

		HashMap responseMap = null;
		try {
			//��POST����ʽ��������
			responseMap = CPManager.getFriendInfo(ahmHeaderMap, ahmNamePair);
			//mHandler.sendEmptyMessage(CLOSE_PD); 
			if (!responseMap.get("result-code").toString().contains(
			"result-code: 0")) {
				
				errMsg = Error.getErrorDescriptionForContent(responseMap.get("result-code").toString());
				mHandler.sendEmptyMessage(4); 
				
			    //mHandler.sendEmptyMessage(ERR_RETCODE_NOT0);
				//showAlert("ɾ������ʧ�ܣ�"+Error.getErrorDescriptionForContent(responseMap.get("result-code").toString()));
	            Logger.e("Delete Error Code: ",responseMap.get("result-code").toString());
				return false;
			}
		} catch (HttpException e) {
			//�����쳣 ,һ��ԭ��Ϊ URL����
		    //mHandler.sendEmptyMessage(ERR_CONNET_EXP);
            Logger.e("Http deleteFriend Exception: ",e.toString());
            //showAlert("���������쳣");
            mHandler.sendEmptyMessage(6); 
			return false;
		}  catch (SocketTimeoutException e) {
		    //mHandler.sendEmptyMessage(ERR_CONNET_TIMEOUT);
			//showAlert("���������쳣");
			mHandler.sendEmptyMessage(6); 
            //��ʾ��ȡ�û���Ϣʧ����ʾ��
            return false;
           }catch (IOException e) {
			//IO�쳣 ,һ��ԭ��Ϊ��������
            //mHandler.sendEmptyMessage(ERR_CONNECT_FAILED);
        	//showAlert("���������쳣");
        	mHandler.sendEmptyMessage(6); 
            Logger.e("IO deleteFriend Exception: ",e.toString());
            
			return false;
		}
		byte[] responseBody = (byte[])responseMap.get("ResponseBody");


		//���ݷ����ֽ����鹹��DOM
		Document dom = null;
		try {
			dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
            Logger.e("Dom configuration deleteFriend Exception: ",e.toString());
			return false;
		} catch (SAXException e) {
			e.printStackTrace();
            Logger.e("SAX deleteFriend Exception: ",e.toString());
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Logger.e("Dom IO deleteFriend Exception: ",e.toString());
			return false;
		}
	
        Element root = dom.getDocumentElement();

        NodeList nl = root.getElementsByTagName("Parameter");
        String name = "";
        String value = "";
        Element temp = null;
        for (int i = 0; i < nl.getLength(); i++) {
            temp = (Element) nl.item(i);
            name = temp.getElementsByTagName("name").item(0)
                    .getFirstChild().getNodeValue();
            try {
                value = temp.getElementsByTagName("value").item(0)
                        .getFirstChild().getNodeValue();
            } catch (Exception e) {

            }
            userinfo.put(name, value);
        }

        
        //�ͷŶ���
        responseBody=null;
        dom = null;
        nl = null;            
        mHandler.sendEmptyMessage(1004); 
		return true;
	}

    /* (non-Javadoc)
     * @see com.pvi.ap.reader.activity.PviActivity#bindEvent()
     */
    @Override
    public void bindEvent() {
        // TODO Auto-generated method stub
        super.bindEvent();
        addFriendButton.setOnClickListener(clickListen);
        selectAllButton.setOnClickListener(clickListen);
        showButton.setOnClickListener(clickListen);
        deleteButton.setOnClickListener(clickListen);
     }

    /* (non-Javadoc)
     * @see com.pvi.ap.reader.activity.PviActivity#initControls()
     */
    @Override
    public void initControls() {
        // TODO Auto-generated method stub
        super.initControls();
        
        listView= (PviDataList)findViewById(R.id.list);
        list = new ArrayList<PviUiItem[]>();
        this.showPager = true;
        //pbb.setPageable(this);//���ܵײ��Ĺ�����������ϵ
        phoneNumEditText = (EditText)findViewById(R.id.phonenum);
        addFriendButton = (Button)findViewById(R.id.addfriend);
        selectAllButton = (Button)findViewById(R.id.select);
        showButton = (Button)findViewById(R.id.showinfo);
        deleteButton = (Button)findViewById(R.id.delete);
        
        phoneNum = new String[pageSize];
        checkState = new boolean[pageSize];
        if(deviceType==1){
			
//			getWindow().
//            getDecorView()
//            .getRootView().
//            invalidate(UPDATEMODE_4);
//			phoneNumEditText.setUpdateMode(UPDATEMODE_5);
//            addFriendButton.setUpdateMode(UPDATEMODE_5);
//            selectAllButton.setUpdateMode(UPDATEMODE_5);
//            showButton.setUpdateMode(UPDATEMODE_5);
//            deleteButton.setUpdateMode(UPDATEMODE_5);
//            listView.setUpdateMode(UPDATEMODE_1);
		}
        


    }
	
    public void showAlert(String message,boolean canClose){ 
    	if(pd!=null && pd.isShowing()){
    		pd.dismiss();
    	}
        pd = new PviAlertDialog(getParent());
        pd.setTitle(getResources().getString(R.string.my_friend_hint));
        //pd.setMessage(message);
        TextView tv = new TextView(MyFriendListActivity.this);
        tv.setText(message);
        tv.setTextSize(21);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.BLACK);
        
        pd.setView(tv);
        pd.setCanClose(canClose);
        pd.show();
    }
    
    public void showAlert(String message){ 
    	
    	final PviAlertDialog pd = new PviAlertDialog(getParent());
        pd.setTitle(getResources().getString(R.string.my_friend_hint));
        //pd.setMessage(message);
        TextView tv = new TextView(MyFriendListActivity.this);
        tv.setText(message);
        tv.setTextSize(21);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.BLACK);
        
        pd.setView(tv);
        
        pd.setButton(DialogInterface.BUTTON_POSITIVE,
				"ȷ��",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						if(phoneNumEditText != null){
							phoneNumEditText.setText("");
						}
						pd.dismiss();
					}
		});
        pd.show();
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
    	 if (keyCode == KeyEvent.KEYCODE_MENU) {
             menupan();
             return true;
         }
         if (keyCode == KeyEvent.KEYCODE_BACK) {
             sendBroadcast(new Intent(MainpageActivity.BACK));
             return true;
         }
         
         
         if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
             // ����ҳ
         	boolean hasFocus = listView.hasFocus();

         	if(hasFocus){
         		nextPage();
         	}
         	return false;
         }
         
         if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
        	 boolean hasFocus = listView.hasFocus();
             // ���ҷ�ҳ
         	 if( hasFocus){
         		 prevPage();
             }
         	 return false;
         }
         
         
         if(keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
         	if(selectIndex == 2){
         		clickListen.onClick(addFriendButton);
         	}else if(selectIndex == 10){
         		clickListen.onClick(selectAllButton);
         		//selectAllButton.setPressed(true);
         	}else if(selectIndex == 11){
         		clickListen.onClick(deleteButton);
         		//deleteButton.setPressed(true);
         	}
         }
     
		return super.onKeyDown(keyCode, event);
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
	
	void prevPage(){
		if(curpage > 0){
     		if(deviceType==1){
//     			Logger.i(TAG, "=========full refresh========");
//                getWindow().
//                getDecorView()
//                .getRootView()
//                .invalidate(UPDATEMODE_4);
     		}
      	  
             //final int page = curpage;
     		curpage  --;
             for(int i=0;i<pageSize;i++){
             
         		checkState[i] = false;
     		
         	
             }
             
            //showAlert("���ڼ�������...");
             showMessage("���ڼ�������...");
             mHandler.post(disableButtons);
     	    new Thread(){
     	    	public void run() {
     	    		//getData();
                     //curpage = page;
                     //curpage  --;
     	    		mHandler.sendEmptyMessage(1001);
     	    	};
     	    }.start();
     	}
     	    
	}
	
	void nextPage(){
		 if(curpage < pages - 1){
      		if(deviceType==1){
//      			 Logger.i(TAG, "=========full refresh========");
//                 getWindow().
//                 getDecorView()
//                 .getRootView()
//                 .invalidate(UPDATEMODE_4);
      		}
       	  
              // final int page = curpage;
      		 curpage  ++;
               for(int i=0;i<pageSize;i++){
          
           			checkState[i] = false;
               }
               
               
              //showAlert("���ڼ�������...");
              showMessage("���ڼ�������...");
              mHandler.post(disableButtons);
       	    new Thread(){
       	    	public void run() {
       	    		//getData();
                      // curpage = page;
                      // curpage  ++;
       	    		mHandler.sendEmptyMessage(1001);
       	    	};
       	    }.start();
               //initData();
          }
	}

}
