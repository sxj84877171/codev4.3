package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviDataList.OnRowClickListener;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

public class SerialSubscribeActivity extends PviActivity {
    private static final String TAG = "SerialSubscribeActivity";
    private Handler mHandler = new H();
    private Context mContext;
    private static int skinID = 1;// 皮肤ID

    // 分页
    // pager
//    private TextView mtv_CurPage = null;
//    private TextView mtv_Pages = null;
//    private ImageButton mtv_Prev = null;
//    private ImageButton mtv_Next = null;
    private int total = 0;
    private int perpage = 7;
    private int pages = 0;
    private int curpage = 1;
    private int start = 0;
    
    private String selectType = null;

    // 列表
    private PviDataList mlvBookupdateList;
    ArrayList<PviUiItem[]> list = new ArrayList<PviUiItem[]>();
    private ArrayList<HashMap<String, String>> bookupdateList = new ArrayList<HashMap<String, String>>();// 存放一页的数据
    private int lastSelect = -1;// save ListView last select
//    private BookupdateListAdapter ba;
    
    // 存放一本书籍的摘要信息
    private HashMap<String, Object> booksummary = new HashMap<String, Object>();
    
    private static boolean isCheck[];

    private PviAlertDialog pd;
    
    private CheckBox lastCheck;

    private static final int GET_DATA = 101;
    private static final int CLOSE_PD = 102;
    public static final int SHOW_PD_LOADING = 103;
    public static final int SET_UI_DATA = 104;
    public static final int UPDATA_PAGER = 105;
    private static final int DO_UNBOOK = 107;//取消预订
    private static final int DO_READBOOK = 108;//读取书籍信息

    public static final int ERR_CONNECT_EXP = 201;// 网络连接异常
    public static final int ERR_CONNECT_TIMEOUT = 202;// 连接超时
    public static final int ERR_RETCODE_NOT0 = 203; // 接口返回码非0
    public static final int ERR_CONNECT_FAILED = 204;// 连接失败
    public static final int ERR_CHECK_PHONENUM = 205;// 手机号码检测出错
    public static final int ERR_XML_PARSER = 206; // xml解析错误
    
    public static final int NOTE_NOSEL = 301; // 未选取
    public static final int NOTE_UNBOOK_SUCCESS = 302; // 取消成功
    
    Button notifyBtn;
    Button readBtn;
    Button cancelBtn;
    
    private Runnable enableButtons = new Runnable(){

        @Override
        public void run() {
        	notifyBtn.setEnabled(true);
        	readBtn.setEnabled(true);
        	cancelBtn.setEnabled(true);
        
        }
        
    };
    
    private Runnable disableButtons = new Runnable(){

        @Override
        public void run() {
        	notifyBtn.setEnabled(false);
        	readBtn.setEnabled(false);
        	cancelBtn.setEnabled(false);
        }
        
    };
    
    
    public void showMe(){
    	if("onResume".equals(selectType)){
    		 Intent tmpIntent = new Intent(
 	                MainpageActivity.SHOW_ME);
 	    Bundle bundleToSend = new Bundle();
 	        bundleToSend.putString("act", "com.pvi.ap.reader.activity.UserCenterActivity");
 	        bundleToSend.putString("actTabName", "连载预订");
 	        bundleToSend.putString("sender", SerialSubscribeActivity.this.getClass().getName());
 	        tmpIntent.putExtras(bundleToSend);
 	        sendBroadcast(tmpIntent);
 	      
 	        tmpIntent = null;
 	        bundleToSend = null;
 	        selectType = null;
 	        
 	       mHandler.post(new Runnable() {
				
				@Override
				public void run() {
//					mlvBookupdateList.requestFocusFromTouch();
//   					mlvBookupdateList.setSelection(0);
				}
			});
    	}
    	
    	hideTip();
    }

    private class H extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
            case GET_DATA:// 执行 获取用户数据
                new Thread() {
                    public void run() {
                    	mHandler.sendEmptyMessage(SHOW_PD_LOADING);
                    	mHandler.post(disableButtons);
                        getBookupdateList.run();
                        mHandler.post(enableButtons);
                        showMe();
                    }
                }.start();
                
                break;
            case DO_UNBOOK:
                new Thread() {
                    public void run() {
                        unbook.run();
                    }
                }.start();
                
                break;
            case DO_READBOOK:
                new Thread() {
                    public void run() {
                        readBook.run();
                    }
                }.start();
                
                break;
            case CLOSE_PD:// 关闭提示框
                if (pd != null) {
                    pd.dismiss();
                }
                hideTip();
                break;
            case SHOW_PD_LOADING:// 显示加载中信息框
                //showAlert(getResources().getString(R.string.kyleHint05),false);
            	showMessage("正在加载数据...");
                break;
            case 9999:// 显示加载中信息框
                //showAlert(getResources().getString(R.string.kyleHint05),false);
            	showMessage("正在取消退订...");
                break;
            case SET_UI_DATA:// 把获取的数据填充入UI
                //updateUiData.run();
            	for(int i=0;i<isCheck.length;i++){
            		isCheck[i] = false;
            	}
                updateUiData();
                break;
            case UPDATA_PAGER:// 更新分页条
                updataPager.run();
                break;
            case ERR_CONNECT_EXP:// 网络异常
                //showAlert(getResources().getString(
                //        R.string.my_friend_connecterror));
            	showError();
                break;
            case ERR_CONNECT_TIMEOUT:// 连接超时
                //showAlert("网络连接超时");
            	showError();
                break;
            case ERR_RETCODE_NOT0:// 接口返回错误
            	Bundle b = msg.getData();
            	String error = b.getString("error");
                showAlert(error);
                break;
            case ERR_CONNECT_FAILED:// IO失败
               // showAlert(getResources().getString(
               //         R.string.my_friend_connectfailed));
            	showError();
                break;
            case ERR_CHECK_PHONENUM:// 号码错误
                showAlert(getResources().getString(
                        R.string.my_friend_numchecking));
                break;
            case ERR_XML_PARSER:// XML解析错误
                showAlert("XML解析错误。");
                break;
            case NOTE_NOSEL:
                showAlert("您未选取！");
                break;
            case NOTE_UNBOOK_SUCCESS:
                showAlert("取消成功！");
                break;
            default:
                ;
            }
        }
    }
    
    
    public void showError(){
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

    private  class BookupdateListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private ArrayList<HashMap<String, String>> mList;
        private int index = -1;

        public void setIndex(int index) {
            this.index = index;
        }

        public BookupdateListAdapter(Context context,
                ArrayList<HashMap<String, String>> list) {
            mInflater = LayoutInflater.from(context);
            mList = list;
        }

        public int getCount() {
            return mList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }
        


         class ViewHolder {
        	CheckBox tvCheck;
            TextView tvContentName;
            TextView tvChapterName;
            TextView tvAuthorName;
            TextView tvAddDate;
        }
    
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            if (convertView == null) {
            
                    convertView = mInflater.inflate(
                            R.layout.bookupdatelistitem_ui1, null);
           
                holder = new ViewHolder();
                holder.tvCheck = (CheckBox) convertView
                .findViewById(R.id.checkID);
                holder.tvContentName = (TextView) convertView
                        .findViewById(R.id.contentName);
                holder.tvChapterName = (TextView) convertView
                        .findViewById(R.id.chapterName);
                holder.tvAuthorName = (TextView) convertView
                        .findViewById(R.id.authorName);
                holder.tvAddDate = (TextView) convertView
                        .findViewById(R.id.addDate);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //convertView.setFocusable(true);
            
            try {
                holder.tvContentName.setText(mList.get(position).get(
                        "contentName"));
                holder.tvChapterName.setText(mList.get(position).get(
                        "chapterName"));
                holder.tvAuthorName.setText(mList.get(position).get(
                        "authorName"));
                
                String dataFormat = null;
            	
            	try {
					dataFormat = GlobalVar.TimeFormat("yyyy-MM-dd hh:mm:ss",mList.get(position).get("addDate"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                holder.tvAddDate.setText(dataFormat);
                
	            holder.tvCheck.setChecked(isCheck[position]);   
	            holder.tvCheck.setClickable(false);
	            
            } catch (Exception e) {
            }
            
            if(index==position){
                //convertView.setFocusable(true);
                //convertView.setFocusableInTouchMode(true);
                //convertView.setDuplicateParentStateEnabled(true);
                //convertView.setPressed(true);
                //convertView.setSelected(true);
                
                //convertView.requestFocus();
                //convertView.requestFocusFromTouch();
            }
            
            
            final CheckBox cb = holder.tvCheck;
            final int p = position;
            /*holder.tvCheck.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(holder.tvCheck.isChecked()){
						isCheck[p] = true;
                	}else{
                		isCheck[p] = false;
                	}
				}
			});*/
                        
            convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(isCheck[p] == true){
                		isCheck[p] = false;   
                		cb.setChecked(false);
                	}else{
                		isCheck[p] = true;
                		cb.setChecked(true);
                	}
				}
			
			});
            
           
            /*
             * if (position == index) {
             * 
             * convertView
             * .setBackgroundResource(R.drawable.bookupdatelist_bg_focus);
             * TextView tv0 = (TextView) convertView
             * .findViewById(R.id.contentName); TextView tv1 = (TextView)
             * convertView .findViewById(R.id.chapterName); TextView tv2 =
             * (TextView) convertView .findViewById(R.id.authorName); TextView
             * tv3 = (TextView) convertView .findViewById(R.id.addDate);
             * tv0.setTextColor(Color.WHITE); tv1.setTextColor(Color.WHITE);
             * tv2.setTextColor(Color.WHITE); tv3.setTextColor(Color.WHITE);
             * 
             * } else { convertView
             * .setBackgroundResource(R.drawable.bookupdatelist_bg_normal);
             * TextView tv0 = (TextView) convertView
             * .findViewById(R.id.contentName); TextView tv1 = (TextView)
             * convertView .findViewById(R.id.chapterName); TextView tv2 =
             * (TextView) convertView .findViewById(R.id.authorName); TextView
             * tv3 = (TextView) convertView .findViewById(R.id.addDate);
             * tv0.setTextColor(Color.BLACK); tv1.setTextColor(Color.BLACK);
             * tv2.setTextColor(Color.BLACK); tv3.setTextColor(Color.BLACK); }
             */
            /*
            if(SerialSubscribeActivity.this.deviceType==1){
             	int typeOnClick = View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL;
             	int typeFocus = View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_DU | View.EINK_UPDATE_MODE_PARTIAL;
             	convertView.setUpdateMode(typeFocus);
             }*/
            if(deviceType==1){
//            	convertView.setUpdateMode(UPDATEMODE_5);
            }
            return convertView;
        }

    }

    @Override
    public void onCreate(Bundle icicle) {
        mContext = SerialSubscribeActivity.this;
        final GlobalVar app = ((GlobalVar) getApplicationContext());
        this.showPager = true ;
   
            perpage=9;
            setContentView(R.layout.serialsubscribe_ui1);
     

        super.onCreate(icicle);

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
    	// Message 
        super.onResume();
        
        selectType = "onResume";
        Logger.i(TAG,"onResume()");
        for(int i=0;i<perpage;i++){
    		isCheck[i] = false;
    	}
        
        mHandler.sendEmptyMessage(GET_DATA);
        
        readBtn.requestFocus();
        
       
    }

    private Runnable getBookupdateList = new Runnable() {
        @Override
        public void run() {
            int err = 0;
            
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("start",""+start);
            ahmNamePair.put("count",""+perpage);
            try {                
                responseMap = CPManager.getBookUpdateList(ahmHeaderMap, ahmNamePair);
                mHandler.sendEmptyMessage(CLOSE_PD);
                if (!responseMap.get("result-code").toString().contains(
                        "result-code: 0")) {
                    Logger.i(TAG, responseMap.get("result-code").toString());
                    // 接口返回别的状态码
                    Message msg = new Message();
                    msg.what = ERR_RETCODE_NOT0;
                    Bundle b = new Bundle();
                    b.putString("error", Error.getErrorDescriptionForContent(responseMap.get("result-code").toString()));
                    msg.setData(b);
                    mHandler.sendMessage(msg);
                    return;
                }
            }catch (HttpException e) {
                e.printStackTrace();
                err=1;
            }catch (SocketTimeoutException e){
                e.printStackTrace();
                err=2;
            }catch (IOException e) { 
                e.printStackTrace();
                err=3;
            }
            
            if(err>0){
                mHandler.sendEmptyMessage(CLOSE_PD);
                mHandler.sendEmptyMessage(ERR_CONNECT_FAILED);
                return;
            }
            
            byte[] responseBody = (byte[])responseMap.get("ResponseBody");
/*            System.out.println(new String(responseBody));*/
            
            Element rootele = null;
            try {
                rootele = CPManagerUtil
                        .getDocumentFrombyteArray(responseBody)
                        .getDocumentElement();
                
                
            } catch (Exception e) {
                Logger.e(TAG, "get DOM root failed!"+e.getMessage());
                mHandler.sendEmptyMessage(ERR_XML_PARSER);
                return;
            }

            try {
                mHandler.post(new Runnable(){

                    @Override
                    public void run() {
                        bookupdateList.clear();                        
                    }});
                

                NodeList totalNl = rootele
                        .getElementsByTagName("totalRecordCount");
                total = Integer.parseInt(totalNl.item(0).getFirstChild()
                        .getNodeValue());
                pages = total / perpage;
                if (total % perpage > 0) {
                    pages = pages + 1;
                }

                if (curpage == 0 && total > 0) {
                    curpage = 1;
                }
/*                System.out.println(total);
                System.out.println(pages);*/
                
                mHandler.sendEmptyMessage(UPDATA_PAGER);
                NodeList nl = rootele.getElementsByTagName("ContentInfo");
                int nlCount = nl.getLength();
                // for(int i =
                // (currentPage-1)*itemPerPage;i<nl.getLength()&&i<itemPerPage+(currentPage-1)*itemPerPage;i++){
                if (nlCount > perpage) {
                    nlCount = perpage;
                }
                for (int i = 0; i < nlCount; i++) {
                    final HashMap<String, String> tempHM = new HashMap<String, String>();

                    PviUiItem[] items = new PviUiItem[]{
			                new PviUiItem("contentName"+i, 0, 5, 10, 147,38, "书籍名称 ", false, true, null),
			                new PviUiItem("chapterName"+i, 0, 152, 10, 170,38, "章节名称", false, true, null),
			                new PviUiItem("authorName"+i, 0, 320, 10, 80, 38, "作者名 ", false, true, null),
			                new PviUiItem("addDate"+i, 0, 410, 10, 112, 32, "2010-12-24 12:00:00", false, true, null),
			                new PviUiItem("checbox"+i,R.drawable.notcheck,525, 10, 34, 32, null, false, true, null)
			        };
                    
                    Element e2 = (Element) nl.item(i);
                    tempHM.put("contentID", e2
                            .getElementsByTagName("contentID").item(0)
                            .getFirstChild().getNodeValue());
                    tempHM.put("contentName", e2.getElementsByTagName(
                            "contentName").item(0).getFirstChild()
                            .getNodeValue());
                    items[0].text = e2.getElementsByTagName(
                    "contentName").item(0).getFirstChild()
                    .getNodeValue();
                    tempHM.put("chapterName", e2.getElementsByTagName(
                            "chapterName").item(0).getFirstChild()
                            .getNodeValue());
                    items[1].text = e2.getElementsByTagName(
                    "chapterName").item(0).getFirstChild()
                    .getNodeValue();
                    tempHM.put("chapterID", e2
                            .getElementsByTagName("chapterID").item(0)
                            .getFirstChild().getNodeValue());
                    tempHM.put("authorName", e2.getElementsByTagName(
                            "authorName").item(0).getFirstChild()
                            .getNodeValue());
                    items[2].text = e2.getElementsByTagName(
                    "authorName").item(0).getFirstChild()
                    .getNodeValue();
                    tempHM.put("addDate", e2.getElementsByTagName("addDate")
                            .item(0).getFirstChild().getNodeValue());
                    items[3].text = GlobalVar.TimeFormat("yyyy-MM-dd", e2.getElementsByTagName(
                    "addDate").item(0).getFirstChild()
                    .getNodeValue()).substring(0, 10);

                    list.add(items);

                    mHandler.post(new Runnable(){

                        @Override
                        public void run() {
                            bookupdateList.add(tempHM);  
                        }});
                    
                }
            } catch (Exception e) {
                mHandler.sendEmptyMessage(CLOSE_PD);
                mHandler.sendEmptyMessage(ERR_XML_PARSER);
                e.printStackTrace();
                //Logger.e(TAG, "get data from xml failed!" + e.getMessage());
                return;
            }

            mHandler.sendEmptyMessage(SET_UI_DATA);
        }
    };

    @Override
    public void bindEvent() {
        super.bindEvent();
        // 预订提醒设置
        notifyBtn = (Button) findViewById(R.id.notify);
        notifyBtn.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
                Bundle sndBundle = new Bundle();
                sndBundle.putString("act",
                        "com.pvi.ap.reader.activity.SubscribeRemindActivity");
                sndBundle.putString("haveStatusBar", "1");
                intent.putExtras(sndBundle);
                sendBroadcast(intent);

            }
        });

        // 进入阅读

        // 取消预订
        // 取消预订SubscribeProcess.network("unbookUpdate",contentId[temp],null,null,null);

        // 分页
        // 上一页
//        this.mtv_Prev.setOnClickListener(new OnClickListener() {
//            public void onClick(final View v) {
//                prevPage();
//            }
//        });

        // 下一页
//        this.mtv_Next.setOnClickListener(new OnClickListener() {
//            public void onClick(final View v) {
//                nextPage();
//            }
//        });

        // 进入阅读
        readBtn = (Button) findViewById(R.id.readBtn);
        readBtn.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.sendEmptyMessage(DO_READBOOK);
            }
        });
        
        //readBtn.setFocusable(true);
        //readBtn.setFocusableInTouchMode(true);

        // 取消预订
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
            	mHandler.post(disableButtons);
                mHandler.sendEmptyMessage(DO_UNBOOK);
            }
        });
        //mlvBookupdateList.setFocusable(true);
        //mlvBookupdateList.setFocusableInTouchMode(true);
//        mlvBookupdateList.setOnFocusChangeListener(new View.OnFocusChangeListener(){
//
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				Logger.i("=========================", hasFocus);
//				if (hasFocus) {
//					mHandler.postAtFrontOfQueue(new Runnable() {
//    					public void run() {
////    						mlvBookupdateList.setSelection(0);
//    					}
//    				});
//    			}
//			}
//        	
//        });

    }

    @Override
    public void initControls() {
        super.initControls();
        mlvBookupdateList = (PviDataList) findViewById(R.id.bookupdateList);
        mlvBookupdateList.setData(list);
        mlvBookupdateList.setOnRowClick(new OnRowClickListener() {
			@Override
			public void OnRowClick(View v, int rowIndex) {
				PviUiItem[] tvp = list.get(rowIndex);
				if(tvp[4].res == R.drawable.check){
					tvp[4].res = R.drawable.notcheck ;
					isCheck[rowIndex] = false;
				}else{
					tvp[4].res = R.drawable.check ;
					isCheck[rowIndex] = true;
				}
				mlvBookupdateList.invalidate();
			}
		});
        
		if (deviceType == 1) {
//			getWindow().getDecorView().getRootView().invalidate(UPDATEMODE_4);
		}
        
        pd = new PviAlertDialog(getParent());
        
        isCheck = new boolean[perpage];

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && mlvBookupdateList!=null && mlvBookupdateList.hasFocus()) {
            prevPage();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && mlvBookupdateList!=null && mlvBookupdateList.hasFocus()) {
            nextPage();
            return true;
        }
        
        /*
        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if(lastCheck!=null && lastSelect>-1 && lastSelect<perpage){
            	if(isCheck[lastSelect] == true){
            		isCheck[lastSelect] = false;
            		lastCheck.setChecked(false);
            		lastCheck.invalidate();
            		return false;
            	}else{
            		isCheck[lastSelect] = true;
            		lastCheck.setChecked(true);
            		lastCheck.invalidate();
            		return false;
            	}
            }
            return false;
        }*/
        return super.onKeyDown(keyCode, event);
    }

    private void prevPage() {
        if (total > 0 && curpage > 1) {
        	for(int i=0;i<perpage;i++){
        		isCheck[i] = false;
        	}
            curpage = curpage - 1;
            start = (curpage - 1) * perpage + 1;
            mHandler.sendEmptyMessage(GET_DATA);
        }
    }

	private void nextPage() {
		if (total > 0 && curpage < pages) {
			for (int i = 0; i < perpage; i++) {
				isCheck[i] = false;
			}
			curpage = curpage + 1;
			start = (curpage - 1) * perpage + 1;
			mHandler.sendEmptyMessage(GET_DATA);
		}
	}



    // 取消预订
    private Runnable unbook = new Runnable() {
        @Override
        public void run() {
        	
        	String contentID = null;
        	for(int i=0;i<perpage;i++){
        		if(isCheck[i] == true && bookupdateList!=null && i<bookupdateList.size()){
        			contentID = bookupdateList.get(i).get(
                    "contentID");
        			 HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
                     HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
                     
                     ahmNamePair.put("contentId",contentID);

                     HashMap responseMap = null;
                     try {
                         mHandler.sendEmptyMessage(9999);
                         responseMap = CPManager.unbookUpdate(ahmHeaderMap,
                                 ahmNamePair);
                         if (!responseMap.get("result-code").toString().contains(
                                 "result-code: 0")) {
                             mHandler.sendEmptyMessage(ERR_RETCODE_NOT0);
                             Logger
                                     .i(TAG, responseMap.get("result-code")
                                             .toString());
                             mHandler.post(enableButtons);
                             return;
                         } else {
                             mHandler.sendEmptyMessage(CLOSE_PD);
                             mHandler.sendEmptyMessage(NOTE_UNBOOK_SUCCESS);
                             
                             lastSelect = -1;

                             /*if(bookupdateList!=null&&bookupdateList.size()==1
                                     &&curpage>1){
                                 prevPage();
                             }else{*/    
                             if(start>0 && (bookupdateList==null || bookupdateList.size()==1) && start - perpage >= 0){
                            	 if(curpage - 1>=0){
                            		 curpage--;
                            	 }
                            	 start = start - perpage;
                             }
                                 mHandler.sendEmptyMessage(GET_DATA);
                             //}
                             mHandler.post(enableButtons);     
                             return;
                         }
                     } catch (HttpException e) {
                         Logger.e(TAG, e.getMessage());

                         mHandler.sendEmptyMessage(ERR_CONNECT_EXP);
                         mHandler.post(enableButtons);
                         return;

                     } catch (SocketTimeoutException e) {

                         mHandler.sendEmptyMessage(ERR_CONNECT_TIMEOUT);
                         mHandler.post(enableButtons);
                         return;
                     }

                     catch (IOException e) {
                         Logger.e(TAG, e.getMessage());
                         mHandler.post(enableButtons);
                         mHandler.sendEmptyMessage(ERR_CONNECT_EXP);

                         return;
                     }
        		}
        	}
        	mHandler.post(enableButtons);
            if (contentID == null) {
                mHandler.sendEmptyMessage(CLOSE_PD);
                mHandler.sendEmptyMessage(NOTE_NOSEL);
            }
        }
    };

    // 阅读图书
    private Runnable readBook = new Runnable() {
        @Override
        public void run() {

            // 取选择的图书的类型
        	String contentID = null;
        	int i=0;
        	for(i=0;i<perpage;i++){
        		if(isCheck[i] == true){
        			contentID = bookupdateList.get(i).get(
                    "contentID");
        			break;
        		}
        	}
            if (contentID!=null) {

               
                HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
                HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

                ahmNamePair.put("contentId", contentID);

                HashMap responseMap = null;
                try {
                    mHandler.sendEmptyMessage(SHOW_PD_LOADING);
                    responseMap = CPManager.getContentInfo(ahmHeaderMap,
                            ahmNamePair);
                    mHandler.sendEmptyMessage(CLOSE_PD);
                    if (!responseMap.get("result-code").toString().contains(
                            "result-code: 0")) {
                        mHandler.sendEmptyMessage(ERR_RETCODE_NOT0);
                        return;
                    }
                } catch (HttpException e) {
                    mHandler.sendEmptyMessage(ERR_CONNECT_EXP);
                    return;

                } catch (SocketTimeoutException e) {
                    mHandler.sendEmptyMessage(ERR_CONNECT_TIMEOUT);

                    return;
                }

                catch (IOException e) {
                    mHandler.sendEmptyMessage(ERR_CONNECT_FAILED);
                    return;
                }
                byte[] responseBody = (byte[]) responseMap.get("ResponseBody");

                Document dom = null;
                try {
                    dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);

                } catch (ParserConfigurationException e) {
                    mHandler.sendEmptyMessage(ERR_XML_PARSER);
                    return;
                } catch (SAXException e) {
                    mHandler.sendEmptyMessage(ERR_XML_PARSER);
                    return;
                } catch (IOException e) {
                    mHandler.sendEmptyMessage(ERR_XML_PARSER);
                    return;
                }
                Element root = dom.getDocumentElement();

                NodeList nl = root.getElementsByTagName("contentName");
                Element contentName = (Element) nl.item(0);

                String bookname = contentName.getFirstChild().getNodeValue();
                booksummary.put("BookName", bookname);

                nl = root.getElementsByTagName("isSerial");
                Element isSerial = (Element) nl.item(0);
                if (isSerial.getFirstChild().getNodeValue().equals("false")) {
                    booksummary.put("IsSerial", "false");
                } else {
                    booksummary.put("IsSerial", "true");
                    NodeList overlist = root.getElementsByTagName("isFinish");
                    Element isfinish = (Element) overlist.item(0);
                    if (isfinish.getFirstChild().getNodeValue().equals("false")) {
                        booksummary.put("IsFinish", "false");

                    } else {
                        booksummary.put("IsFinish", "true");
                    }
                }

                nl = root.getElementsByTagName("authorID");
                Element temp = (Element) nl.item(0);
                booksummary
                        .put("AuthorID", temp.getFirstChild().getNodeValue());

                nl = root.getElementsByTagName("authorName");
                temp = (Element) nl.item(0);
                booksummary.put("AuthorName", temp.getFirstChild()
                        .getNodeValue());

                nl = root.getElementsByTagName("mark");
                temp = (Element) nl.item(0);
                booksummary.put("Mark", temp.getFirstChild().getNodeValue());

                // Log.i("Menu", "Mark: " +
                // temp.getFirstChild().getNodeValue());

                nl = root.getElementsByTagName("chargeTip");
                temp = (Element) nl.item(0);
                booksummary.put("ChargeTip", temp.getFirstChild()
                        .getNodeValue());

                nl = root.getElementsByTagName("canDownload");
                temp = (Element) nl.item(0);
                booksummary.put("CanDownload", temp.getFirstChild()
                        .getNodeValue());

                nl = root.getElementsByTagName("canBookUpdate");
                temp = (Element) nl.item(0);
                booksummary.put("CanBookUpdate", temp.getFirstChild()
                        .getNodeValue());

                nl = root.getElementsByTagName("chargeMode");
                temp = (Element) nl.item(0);
                booksummary.put("ChargeMode", temp.getFirstChild()
                        .getNodeValue());

                nl = root.getElementsByTagName("fascicleFlag");
                temp = (Element) nl.item(0);
                booksummary.put("FascicleFlag", temp.getFirstChild()
                        .getNodeValue());

                /*
                 * try{
                 * Logger.i(TAG,",BookName:"+booksummary.get("BookName").toString
                 * () +",CanDownload:"+booksummary.get("CanDownload").toString()
                 * +",ChargeMode:"+booksummary.get("ChargeMode").toString()
                 * +",CanBookUpdate:"
                 * +booksummary.get("CanBookUpdate").toString()
                 * +",IsSerial:"+booksummary.get("IsSerial").toString()
                 * +",IsFinish:"+booksummary.get("IsFinish").toString()
                 * +",ChargeTip:"+booksummary.get("ChargeTip").toString()
                 * +",FascicleFlag:"+booksummary.get("FascicleFlag").toString()
                 * 
                 * 
                 * ); }catch(Exception e){ Logger.i(TAG,e.getMessage()); }
                 */

                // 进入阅读
                // 如果可以下载，启动下载，跳转到“我的下载”；其它，跳到在线阅读
                if (booksummary.get("CanDownload").toString().equals("true")) {
                    // copy 自booksummaryactivity 的“下载阅读”代码片段
                    Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
                    Bundle sndBundle = new Bundle();
                    sndBundle.putString("act",
                            "com.pvi.ap.reader.activity.SubscribeProcess");
                    sndBundle.putString("contentID", contentID);
                    if (booksummary.get("IsSerial").toString()
                            .contains("false")) {
                        sndBundle.putString("isSerial", "0");
                        sndBundle.putString("isFinish", "0");
                    } else {
                        sndBundle.putString("isSerial", "1");
                        if (booksummary.get("IsFinish").toString().contains(
                                "false")) {
                            sndBundle.putString("isFinish", "0");
                        } else {
                            sndBundle.putString("isFinish", "1");
                        }
                    }
                    sndBundle.putString("subscribeMode", "download");// 注明是下载
                    if (booksummary.get("CanDownload").toString().equals(
                            "false")) {
                        sndBundle.putString("canDownload", "0");
                    } else {
                        sndBundle.putString("canDownload", "1");
                    }
                    sndBundle.putString("chargeMode", booksummary.get(
                            "ChargeMode").toString());
                    sndBundle.putString("chargeTip", booksummary.get(
                            "ChargeTip").toString());
                    sndBundle.putString("bookName", booksummary.get("BookName")
                            .toString());
                    sndBundle.putString("authorName", booksummary.get(
                            "AuthorName").toString());
                    intent.putExtras(sndBundle);
                    sendBroadcast(intent);

                    /*
                     * Intent sndintent = new
                     * Intent(MainpageActivity.START_ACTIVITY); Bundle sndbundle
                     * = new Bundle();sndbundle.putString("act",
                     * "com.pvi.ap.reader.activity.MyDownloadsActivity");
                     * sndbundle.putString("startType", "reuse");
                     * sndintent.putExtras(sndbundle); sendBroadcast(sndintent);
                     */

                } else {
                    // Logger.i(TAG,"contentID:"+contentID+",chapterName:"+bookupdateList.get(lastSelect).get("chapterName")+",chapterID:"+bookupdateList.get(lastSelect).get("chapterID"));
                    Intent sndintent = new Intent(
                            MainpageActivity.START_ACTIVITY);
                    Bundle sndbundle = new Bundle();
                    sndbundle.putString("act",
                            "com.pvi.ap.reader.activity.ReadingOnlineActivity");
                    sndbundle.putString("startType", "allwaysCreate");
                    sndbundle.putString("ContentID", contentID);
                    sndbundle.putString("ChapterName", bookupdateList.get(
                            i).get("chapterName"));
                    sndbundle.putString("ChapterID", bookupdateList.get(
                            i).get("chapterID"));
                    sndintent.putExtras(sndbundle);
                    sendBroadcast(sndintent);
                }

            } else {// 未选择
                
                mHandler.sendEmptyMessage(NOTE_NOSEL);
            }

        }
    };
    
    /*private Runnable updateUiData = new Runnable() {

        @Override
        public void run() {*/
    private void updateUiData() {
    	
    	mlvBookupdateList.setData(list);
    	final GlobalVar app = ((GlobalVar) getApplicationContext());        
        updatePagerinfo(curpage == 0 ? "1" : curpage +" / "+ (pages == 0 ? "1":pages));
//            ba = new BookupdateListAdapter(
//                    mContext, bookupdateList);
//            mlvBookupdateList.setAdapter(ba);
            //mlvBookupdateList.setItemsCanFocus(true);
            //mlvBookupdateList.setFocusable(true);
            //mlvBookupdateList.setFocusableInTouchMode(true);
           // mlvBookupdateList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);// 单选模式

//            mlvBookupdateList
//                    .setOnItemSelectedListener(new ListView.OnItemSelectedListener() {
//
//                        @Override
//                        public void onItemSelected(AdapterView<?> arg0, View arg1,
//                                int arg2, long arg3) {
//                            // TODO Auto-generated method stub
//                        	
//                        	
//                            /*lastSelect = arg2;
//                            ViewGroup view = (ViewGroup)ba.getView(arg2, null, null);
//                            lastCheck = (CheckBox)view.getChildAt(4);
//                            ba.setIndex(arg2);
//                            view.setFocusable(true);
//                            view.setFocusableInTouchMode(true);
//                            view.requestFocus();
//                            view.requestFocusFromTouch();
//                            //ba.notifyDataSetChanged();*/
//
//                            // mlvBookupdateList.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
//                            // mlvBookupdateList.requestFocus();
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> arg0) {
//                            // TODO Auto-generated method stub
//
//                        }
//                    });
//
//            mlvBookupdateList.setOnItemClickListener(new OnItemClickListener() {
//
//                @Override
//                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//                        long arg3) {
//                    // TODO Auto-generated method stub
//                	
//                	//System.out.println("1111111111111111");
//                	
//                	ViewGroup view = (ViewGroup)ba.getView(arg2, null, null);
//                	view.performClick();
//                	CheckBox checkbox = (CheckBox)view.getChildAt(4);
//                	System.out.println("==="+checkbox.isChecked());
//                	checkbox.setChecked(checkbox.isChecked());
//                	isCheck[arg2] = checkbox.isChecked();
//                	/*if(isCheck[arg2] == true){
//                		isCheck[arg2] = false;   
//                		checkbox.setChecked(false);
//                	}else{
//                		isCheck[arg2] = true;
//                		checkbox.setChecked(true);
//                	}
//                    lastSelect = arg2;
//                    ba.setIndex(arg2);*/
//                    ba.notifyDataSetChanged();
//                }
//
//            });
           
           
     	}
        //}};

    private void showAlert(String message,boolean canClose) {
        if (pd != null) {
            pd.dismiss();
        }
        pd = new PviAlertDialog(getParent());
        pd.setTitle(getResources().getString(R.string.my_friend_hint));
        pd.setMessage(message);
        pd.setCanClose(canClose);
        pd.setTimeout(2000);
        pd.show();
    }
    
    public void showAlert(String message){ 
    	if(pd!=null && pd.isShowing()){
    		pd.dismiss();
    	}
        pd = new PviAlertDialog(getParent());
        pd.setTitle(getResources().getString(R.string.my_friend_hint));
        //pd.setMessage(message);
        TextView tv = new TextView(SerialSubscribeActivity.this);
        tv.setText(message);
        tv.setTextSize(21);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.BLACK);
        
        pd.setView(tv);
        pd.setCanClose(true);
        pd.show();
    }

    private Runnable updataPager = new Runnable() {
        @Override
        public void run() {
//        	if(pages == 0){
//        		mtv_Pages.setText("1");
//        	}else{
//        		mtv_Pages.setText("" + pages);
//        	}
//            
//        	if(curpage == 0){
//        		mtv_CurPage.setText("1");
//        	}else{
//        		mtv_CurPage.setText("" + curpage);
//        	}
//            
//        	
//             mtvTotal.setText("" + total);
             
        }
    };
    
    

	@Override
	public void OnNextpage() {
		nextPage();
	}

	@Override
	public void OnPrevpage() {
		prevPage();
	}
    	

}