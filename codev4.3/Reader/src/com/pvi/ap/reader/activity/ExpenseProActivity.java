package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.SelectSpinner;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;
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
import android.widget.TextView;

/**
 * ���Ѽ�¼����ȯ��Ϣ�鿴
 * 
 * @author �����
 * @author rd040 ������ 2010-11-17
 * 
 */

public class ExpenseProActivity extends PviActivity{
	
	PviDataList listView;               //viewʵ��
	ArrayList<PviUiItem[]> list;        //����б�ؼ����ڲ���Ϣ��ArrayList���ÿ�У�ÿ��ʹ��һ�����鱣����PviUiItem
	PviBottomBar  pbb;     //���ÿ�ܵײ�������
	
    private static final String TAG = "ExpenseProActivity";
    private static int skinID = 1;//Ƥ��ID

    private HashMap<String, String> ExpenseHistory = new HashMap<String, String>();
    private HashMap<String, Object> userinfo = new HashMap<String, Object>();
    private String ticketInfo = "";
    private ExpenseListAdapter mExpenseListAdapter;
    private ArrayList<HashMap<String, String>> expenseList = new ArrayList<HashMap<String, String>>();
    private double totalFee; // �����ܶ�
    private double totalTicketFee; // ������ȯ�ܶ�

    private TextView phonenum = null; // ��ʾ�ֻ��ŵ�UI�ؼ�
    private TextView totalexpense = null;
    private Button myticket = null; // ���ҵ���ȯ����ť
    private Button select = null; // ���ҵ���ȯ����ť

    private TextView totalConsume = null;
    
    private String selectType;


    private int total = 0;
    private int perpage = 6;
    private int pages = 0;
    private int curpage = 0;
    private int start = 0;
    private TextView mtvTotal; // ��ʾ�ܼ�¼��

    private String queryYear;
    private String queryMonth;

    //private TextView mtvQueryYear;
    //private TextView mtvQueryMonth;
    SelectSpinner yearSelect;
    SelectSpinner monthSelect;

    //private View mvLastMon;
    //private View mvNextMon;

    // �����Ч��ѯ�ꡢ��
    private ArrayList<String[]> validYearMon = new ArrayList<String[]>();
    private int curYearMonIndex; // ��ǰ��ѯ �����µ� index
    private int maxQueryMonthCount = 7; // �ɲ�ѯ���7����

    private static final int GET_DATA = 101;
    private static final int CLOSE_PD = 102;
    public static final int SHOW_PD_LOADING = 103;
    public static final int SET_UI_DATA = 104;
    public static final int DO_DELETE_MESSAGE = 105;
    public static final int SET_UI_DATA_PHONE = 106;
    public static final int SET_UI_DATA_EXPLIST = 107;
    public static final int SET_UI_DATA_TICKET = 108;
    public static final int UPDATA_PAGER = 109;

    public static final int ERR_CONNECT_EXP = 201;// ���������쳣
    public static final int ERR_CONNECT_TIMEOUT = 202;// ���ӳ�ʱ
    public static final int ERR_RETCODE_NOT0 = 203; // �ӿڷ������0
    public static final int ERR_CONNECT_FAILED = 204;// ����ʧ��
    public static final int ERR_CHECK_PHONENUM = 205;// �ֻ����������
    public static final int ERR_XML_PARSER = 206; // xml��������

    private static final int GET_DATA_2 = 1012;
    private static final int GET_DATA_3 = 1013;
    private static final int SHOW_PD_NETOP = 301; // �������
    private static final int SHOW_PD_NO_TICKET_INFO = 302; //û�д�������Ϣ

    private static final int NOTE_INFO1 = 303;//��ֻ�ܲ����7���µ����Ѽ�¼��
    private static final int NOTE_INFO2 = 304;//��ֻ�ܲ鵱ǰʱ����ǰ�����Ѽ�¼��
    
    private static String description = null;
    private static String type = null;
    private static String date = null;
    private static String price = null;
    
    /**
     * �ҵ���ȯ�б� 
     * add by fly @ 2011-3-4
     */
    private List<String> myticketList = null;
    
    /**
     * �ҵ���ȯ�б�����
     * add by fly @ 2011-3-7
     */
    private int totalCount = 0;
    
    private PviAlertDialog pd;
    private Handler mHandler = new H();
    

    
    private boolean loading = false;//��־��������ǰ����������������ʱ��������Ϊtrue
    
    public void showMe(){
    	
    	if("onResume".equals(selectType)){
	    	Intent tmpIntent = new Intent(
	                MainpageActivity.SHOW_ME);
	    Bundle bundleToSend = new Bundle();
	        bundleToSend.putString("act", "com.pvi.ap.reader.activity.UserCenterActivity");
	        bundleToSend.putString("actTabName", "���Ѽ�¼");
	        bundleToSend.putString("sender", ExpenseProActivity.this.getClass().getName());
	        tmpIntent.putExtras(bundleToSend);
	        sendBroadcast(tmpIntent);
	        
	        //mlvExpenseList.requestFocus();
	        
	        tmpIntent = null;
	        bundleToSend = null;
	        
	        selectType = "";
	        
	        mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mHandler.postAtFrontOfQueue(new Runnable() {
    					public void run() {
    						/*View view = (View)mlvExpenseList.getChildAt(0);
    						//System.out.println("===============1"+view.hasFocus());
    						if(view != null){
    							view.setFocusable(true);
    						}*/
    						select.requestFocus();
    						
    					}
    				});
				}
			},1000);
    	}
    	
    	hideTip();
    }

    private class H extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
            case GET_DATA:// �������ȡ ���Ѽ�¼
                new Thread() {
                    public void run() {
                        getExpense.run();
                        hideTip();
                        mHandler.post(enableButtons);
                        
                    }
                }.start();
                break;
            case 1099:
                new Thread() {
                    public void run() {
                    	getExpense.run();
                        // ��ȡ��Ч�Ĳ�ѯ�ꡢ��
                    	long times = CPManager.getServerTimeAsTimes();
                        for (int i = maxQueryMonthCount; i > 0; i--) {
                           
                        	//Calendar now = Calendar.getInstance();//����Ӧ���Է�����ʱ��Ϊ׼
                            
                            //Edit by fly
                            //ʱ��Ϊ���������ʱ��
                            //2011-3-7
                        	Calendar now = Calendar.getInstance();
                        	now.setTimeInMillis(times);
                        	now.add(Calendar.MONTH, 1 - i);
                            String tempStr[] = new String[2];
                            tempStr[0] = "" + now.get(Calendar.YEAR);
                            tempStr[1] = "" + (now.get(Calendar.MONTH) + 1);
                            validYearMon.add(tempStr);
                        }

                        // ��ѯ���һ����
                        curYearMonIndex = maxQueryMonthCount - 1;
                        getQueryYearMon();
                        getUserInfo.run();
                        
                        showMe();
                    }
                }.start();
                break;
            case GET_DATA_2:// �������ȡ��ȯ��Ϣ
                new Thread() {
                    public void run() {
                        getHandsetUserTicketInfo.run();
                    }
                }.start();
                break;
            case GET_DATA_3:// �������ȡ �û���Ϣ
                new Thread() {
                    public void run() {
                    	 // ��ȡ��Ч�Ĳ�ѯ�ꡢ��
                        for (int i = maxQueryMonthCount; i > 0; i--) {
                           
                        	//Calendar now = Calendar.getInstance();//����Ӧ���Է�����ʱ��Ϊ׼
                            
                            //Edit by fly
                            //ʱ��Ϊ���������ʱ��
                            //2011-3-7
                        	long times = CPManager.getServerTimeAsTimes();
                        	Calendar now = Calendar.getInstance();
                        	now.setTimeInMillis(times);
                        	now.add(Calendar.MONTH, 1 - i);
                            String tempStr[] = new String[2];
                            tempStr[0] = "" + now.get(Calendar.YEAR);
                            tempStr[1] = "" + (now.get(Calendar.MONTH) + 1);
                            validYearMon.add(tempStr);
                        }

                        // ��ѯ���һ����
                        curYearMonIndex = maxQueryMonthCount - 1;
                        getQueryYearMon();
                        getUserInfo.run();
                        
                        showMe();
                    }
                }.start();
                break;
            case UPDATA_PAGER:// ���·�ҳ��
                updataPager();
                break;
            case CLOSE_PD:// �ر���ʾ��
                if (pd != null) {
                    pd.dismiss();
                }
                hideTip();
                break;
            case SHOW_PD_LOADING:// ��ʾ��������Ϣ��
                if (pd != null) {
                    pd.dismiss();
                }
                //showAlert(getResources().getString(R.string.kyleHint05), false);
                break;
            case SET_UI_DATA:// �ѻ�ȡ�����������UI
                break;
            case SET_UI_DATA_TICKET:// 
                if (pd != null) {
                    pd.dismiss();
                }
                pd = new PviAlertDialog(getParent());
                pd.setTitle("�ҵ���ȯ");
                pd.setMessage(ticketInfo);
                pd.setCanClose(true);
                pd.show();
                break;
            case SHOW_PD_NETOP:// �����������
                if (pd != null) {
                    pd.dismiss();
                }
                showAlert("���ڽ����������...", false);
                break;
            case SHOW_PD_NO_TICKET_INFO:// û��
                if (pd != null) {
                    pd.dismiss();
                }
                showAlert(Error.getErrorDescriptionForContent("result-code: 3226"));
                break;
            case ERR_CONNECT_EXP:// �����쳣
                if (pd != null) {
                    pd.dismiss();
                }
                // showAlert(getResources().getString(
                //        R.string.my_friend_connecterror), true);
                showError();
                break;
            case ERR_CONNECT_TIMEOUT:// ���ӳ�ʱ
                if (pd != null) {
                    pd.dismiss();
                }
                //showAlert("�������ӳ�ʱ", true);
                showError();
                break;
            case ERR_RETCODE_NOT0:// �ӿڷ��ش���
                if (pd != null) {
                    pd.dismiss();
                }
                showAlert("����ӿڷ���״̬�벻��ȷ", true);
                break;
            case ERR_CONNECT_FAILED:// IOʧ��
                if (pd != null) {
                    pd.dismiss();
                }
                //showAlert(getResources().getString(
                //        R.string.my_friend_connectfailed), true);
                showError();
                break;
            case ERR_CHECK_PHONENUM:// �������
                if (pd != null) {
                    pd.dismiss();
                }
                showAlert(getResources().getString(
                        R.string.my_friend_numchecking), true);
                break;
            case ERR_XML_PARSER:// XML��������
                if (pd != null) {
                    pd.dismiss();
                }
                showAlert("XML��������", true);
                break;

            case SET_UI_DATA_PHONE:
               /* if (userinfo.containsKey("Mobile")) {
                    phonenum.setText("�ֻ����룺 "
                            + userinfo.get("Mobile").toString());
                }*/
            	phonenum.setText("�ֻ����룺 "
                        + UserInfoActivity.phoneNum);
                break;
            case SET_UI_DATA_EXPLIST:
                setExpenseText();
                list.clear();
            	if(expenseList!=null){
            		for(int i=0;i<expenseList.size();i++){
            				final PviUiItem[] items = new PviUiItem[]{
            						 	new PviUiItem("text1"+i, 0, 10, 10, 190, 50, "�ֻ���", false, true, null),
            						 	new PviUiItem("text2"+i, 0, 190, 10, 80, 50, "", false, true, null),
            			                new PviUiItem("text3"+i, 0, 270, 10, 220, 50, "״̬", false, true, null),
            			                new PviUiItem("text4"+i, 0, 495,10,65,50, null, false, true, null),
            			    };
            		
            				String description1=expenseList.get(i).get("contentName");
            	            final String temp1 = expenseList.get(i).get("chargeMode");
            	            String type1 = "";
            	            if (temp1.equals("1")) {
            	            	type = "����";
            	            } else if (temp1.equals("2")) {
            	            	type = "����";
            	            } else if (temp1.equals("3")) {
            	            	type = "����";
            	            } else if (temp1.equals("4")) {
            	            	type = "����������";
            	            } else if (temp1.equals("15")) {
            	            	type = "����";
            	            } else{
            	            	type = "";
            	            }
            	            
            	            String date1=expenseList.get(i).get("time");
            	            String dateFormat1 = null;
                        	
                        	try {
                        		dateFormat1 = GlobalVar.TimeFormat("yyyy-MM-dd hh:mm:ss",date1);
            				} catch (ParseException e) {
            					// TODO Auto-generated catch block
            					e.printStackTrace();
            				}
            	            Double fee1 = Double.parseDouble(expenseList.get(i).get("fee"));
            	            String price1=String.valueOf(fee1/100) + "Ԫ";
            	            
            	            items[0].text = description1;
            	            items[1].text = type1;
            	            items[2].text = dateFormat1;
            	            items[3].text = price1;
            				final int k = i;
            				OnClickListener l = new OnClickListener(){     //new һ��click�¼�����

            	                @Override
            	                public void onClick(View arg0) {
            	                    // TODO Auto-generated method stub
            	                	description=expenseList.get(k).get("contentName");
                    	            final String temp1 = expenseList.get(k).get("chargeMode");
                    	            if (temp1.equals("1")) {
                    	            	type = "����";
                    	            } else if (temp1.equals("2")) {
                    	            	type = "����";
                    	            } else if (temp1.equals("3")) {
                    	            	type = "����";
                    	            } else if (temp1.equals("4")) {
                    	            	type = "����������";
                    	            } else if (temp1.equals("15")) {
                    	            	type = "����";
                    	            } else{
                    	            	type = "";
                    	            }
                    	            
                    	            date=expenseList.get(k).get("time");
                    	            String dateFormat = null;
                                	
                                	try {
                                		dateFormat = GlobalVar.TimeFormat("yyyy-MM-dd hh:mm:ss",date);
                    				} catch (ParseException e) {
                    					// TODO Auto-generated catch block
                    					e.printStackTrace();
                    				}
                    	            Double fee = Double.parseDouble(expenseList.get(k).get("fee"));
                    	            price=String.valueOf(fee/100) + "Ԫ";
            	                	mHandler.sendEmptyMessage(9999);
            	                }
            	                
            	            };
            	            items[0].l = l;
            	            items[1].l = l;  
            	            items[2].l = l;  
            	            items[3].l = l;  
            				list.add(items);
            			}
            			
            		
            	}
            	if(listView!=null){
            		listView.setData(list);
            	}
                break;
            case NOTE_INFO1:
                if (pd != null) {
                    pd.dismiss();
                }
                showAlert("��ֻ�ܲ鵱ǰʱ����ǰ���7���µ����Ѽ�¼��", true);
                break;
            case NOTE_INFO2:
                if (pd != null) {
                    pd.dismiss();
                }
                showAlert("��ֻ�ܲ鵱ǰʱ����ǰ���7���µ����Ѽ�¼��", true);
                break;
            case 9999: //��ʾ��ϸ������Ϣ
                if (pd != null) {
                    pd.dismiss();
                }
                
                PviAlertDialog showDialog = new PviAlertDialog(getParent());
            	showDialog.setTitle("������Ϣ");
           
            	LayoutInflater inflater = getLayoutInflater(); 
                View layout = inflater.inflate(R.layout.showexpenseproinfo,null);  
                TextView project = (TextView)layout.findViewById(R.id.expenseproject);
                TextView expensetype = (TextView)layout.findViewById(R.id.expensetype);
                TextView expensetime = (TextView)layout.findViewById(R.id.expensetime);
                TextView expenseprice = (TextView)layout.findViewById(R.id.expenseprice);
                /*
                if(deviceType==1){
                	int typeOnClick = View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL;
                	int typeFocus = View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_DU | View.EINK_UPDATE_MODE_PARTIAL;
                	project.setUpdateMode(typeFocus);
                	expensetype.setUpdateMode(typeFocus);
                	expensetime.setUpdateMode(typeFocus);
                	expenseprice.setUpdateMode(typeFocus);
                }*/
                
                if(description!=null){
                	project.setText(description);
                }
                
                if(type!=null){
                	expensetype.setText(type);
                }
                
                if(date!=null){
                	String dataFormat = null;
                	
                	try {
						dataFormat = GlobalVar.TimeFormat("yyyy-MM-dd HH:mm:ss",date);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	
                	expensetime.setText(dataFormat);
                }
                if(price!=null){
                	expenseprice.setText(price);
                }
                
            
            	showDialog.setView(layout);
            	showDialog.setCanClose(true);
                //pd.setTimeout(4000);
            	showDialog.show();
    			break;
                    
            default:
                ;
            }
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

    private  class ExpenseListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private ArrayList<HashMap<String, String>> mList;


        public ExpenseListAdapter(Context context,
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
        

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.expenselistitem_ui1, null);
            
                holder = new ViewHolder();
                holder.tvExpName = (TextView) convertView
                        .findViewById(R.id.expname);
                holder.tvExpType = (TextView) convertView
                        .findViewById(R.id.exptype);
                holder.tvExpTime = (TextView) convertView
                        .findViewById(R.id.exptime);
                holder.tvExpFee = (TextView) convertView
                        .findViewById(R.id.expfee);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
           
            description=mList.get(position).get("contentName");
            final String temp1 = mList.get(position).get("chargeMode");
            if (temp1.equals("1")) {
            	type = "����";
            } else if (temp1.equals("2")) {
            	type = "����";
            } else if (temp1.equals("3")) {
            	type = "����";
            } else if (temp1.equals("4")) {
            	type = "����������";
            } else if (temp1.equals("15")) {
            	type = "����";
            } else{
            	type = "";
            }
            
            date=mList.get(position).get("time");
            Double fee = Double.parseDouble(mList.get(position).get("fee"));
            price=String.valueOf(fee/100) + "Ԫ";
            
            final int p = position;
            convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					 if(p >= mList.size()){
						 return;
					 }
					 description=mList.get(p).get("contentName");
			            final String temp1 = mList.get(p).get("chargeMode");
			            if (temp1.equals("1")) {
			            	type = "����";
			            } else if (temp1.equals("2")) {
			            	type = "����";
			            } else if (temp1.equals("3")) {
			            	type = "����";
			            } else if (temp1.equals("4")) {
			            	type = "����������";
			            } else if (temp1.equals("15")) {
			            	type = "����";
			            } else{
			            	type = "";
			            }
			            
			            date=mList.get(p).get("time");
			            Double fee = Double.parseDouble(mList.get(p).get("fee"));
			            price=String.valueOf(fee/100) + "Ԫ";
			            mHandler.sendEmptyMessage(9999);
					
					/*
					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
	                Bundle bundleToSend = new Bundle();
	                bundleToSend.putString("startType", "allwaysCreate");
	                bundleToSend.putString("actID", "ACT14310");
	                
	                bundleToSend.putString("description",description);
	                bundleToSend.putString("type", type);
	                bundleToSend.putString("date", date);
	                bundleToSend.putString("price", price);
	                tmpIntent.putExtras(bundleToSend);
	                activity.sendBroadcast(tmpIntent);
	                tmpIntent = null;
	                bundleToSend = null;*/
				    
				}
			});

            try {
                holder.tvExpName
                        .setText(description);
                holder.tvExpType.setText(type);
                
                String dataFormat = null;
            	
            	try {
					dataFormat = GlobalVar.TimeFormat("yyyy-MM-dd hh:mm:ss",date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                holder.tvExpTime.setText(dataFormat);
                holder.tvExpFee.setText(price);
            } catch (Exception e) {
            }
            
            if(deviceType==1){
//             	convertView.setUpdateMode(UPDATEMODE_5);
             }
            return convertView;
        }

         class ViewHolder {
            TextView tvExpName;
            TextView tvExpType;
            TextView tvExpTime;
            TextView tvExpFee;
        }
    }
    
    @Override
    public void initControls() {
        super.initControls();
        


        phonenum = (TextView) this.findViewById(R.id.phonenum);
        totalexpense = (TextView) this.findViewById(R.id.totalexpense);
        myticket = (Button) this.findViewById(R.id.myticket);
        select = (Button) this.findViewById(R.id.select);
        select.setFocusable(true);
        select.setFocusableInTouchMode(true);
        totalConsume = (TextView) this.findViewById(R.id.totalConsume);

        // pager
        mtvTotal = (TextView) findViewById(R.id.total);

        //mvLastMon = findViewById(R.id.lastmon);
        //mvNextMon = findViewById(R.id.nextmon);
        //mtvQueryYear = (TextView) findViewById(R.id.queryYear);
        //mtvQueryMonth = (TextView) findViewById(R.id.queryMonth);
        yearSelect = (SelectSpinner) findViewById(R.id.yearselect);
        
        monthSelect = (SelectSpinner) findViewById(R.id.monthselect);
        
        long times = CPManager.getServerTimeAsTimes();
    	Calendar now = Calendar.getInstance();
        now.setTimeInMillis(times);
        
        LinkedHashMap yearMap = new LinkedHashMap();
        yearMap.put(now.get(Calendar.YEAR)+"��",now.get(Calendar.YEAR));
        yearMap.put(now.get(Calendar.YEAR) -1 +"��",now.get(Calendar.YEAR)-1);
        //yearMap.put("2011��",2011);
        //yearMap.put("2010��",2010);
         
        yearSelect.setKey_value(yearMap);
        
        yearSelect.setSelectKey(now.get(Calendar.YEAR)+"��");
        monthSelect.setSelectKey((now.get(Calendar.MONTH)+1)+"��");
		
		LinkedHashMap monthMap = new LinkedHashMap();
		monthMap.put("1��","1");
		monthMap.put("2��","2");
		monthMap.put("3��","3");
		monthMap.put("4��","4");
		monthMap.put("5��","5");
		monthMap.put("6��","6");
		monthMap.put("7��","7");
		monthMap.put("8��","8");
		monthMap.put("9��","9");
		monthMap.put("10��","10");
		monthMap.put("11��","11");
		monthMap.put("12��","12");
		monthSelect.setKey_value(monthMap);
		
		 if(deviceType==1){
//			 findViewById(R.id.yearselect).invalidate(0, 0, 600,800,UPDATEMODE_4);
//			phonenum.setUpdateMode(UPDATEMODE_5);
//            totalexpense.setUpdateMode(UPDATEMODE_5);
//            myticket.setUpdateMode(UPDATEMODE_5);
//            select.setUpdateMode(UPDATEMODE_5);
//            totalConsume.setUpdateMode(UPDATEMODE_5);
//            yearSelect.setUpdateMode(UPDATEMODE_5);
//            monthSelect.setUpdateMode(UPDATEMODE_5);
         }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {


  
        perpage=6;
        setContentView(R.layout.expenselist_ui1);
        listView= (PviDataList)findViewById(R.id.list);
        list = new ArrayList<PviUiItem[]>();
        this.showPager = true;
        pbb = ((GlobalVar)getApplication()).pbb;
        super.onCreate(savedInstanceState);


     

    }

    @Override
    protected void onResume() {
        super.onResume();
        
        selectType = "onResume";
        showMessage("���ڼ�������...");
        
    
        
        // �������ʱ����ȡ�û���Ϣ����ȡ���¼�¼        
        //mHandler.sendEmptyMessage(GET_DATA_3);

        //mtvQueryYear.setText(queryYear);
        //mtvQueryMonth.setText(queryMonth);
        mHandler.sendEmptyMessage(1099);  
    }

    private void getQueryYearMon() {
        String tempStr[] = new String[2];
        //tempStr = validYearMon.get(curYearMonIndex);
        queryYear = yearSelect.getSelectValue();
        queryMonth = monthSelect.getSelectValue();
        //mtvQueryYear.setText(queryYear);
        //mtvQueryMonth.setText(queryMonth);
    }

    private Runnable getExpense = new Runnable() {
        @Override
        public void run() {
            //mHandler.sendEmptyMessage(SHOW_PD_LOADING);
            loading = true;
            Logger.i(TAG, "getExpense(String year=" + queryYear
                    + ", String month=" + queryMonth + "):");       

            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            //ahmNamePair.put("begintime", queryYear + queryMonth + "01");

            Calendar tempCal = Calendar.getInstance();
            
            queryYear = yearSelect.getSelectValue();
            queryMonth = monthSelect.getSelectValue();
            try{
            	tempCal.set(Integer.parseInt(queryYear), Integer
                    .parseInt(queryMonth) - 1, 1);
            }catch (Exception e) {
				// TODO: handle exception
            	//tempCal.set(Integer.parseInt(queryYear), Integer
                //        .parseInt(queryMonth) - 1, 1);
			}
            
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            final String begintime = formatter.format(tempCal.getTime());        
            tempCal.add(Calendar.MONTH, 1);
            tempCal.add(Calendar.DATE, -1);
            final String endtime = formatter.format(tempCal.getTime()); 
            
            //Logger.i(TAG,"begintime:"+begintime+", endtime:"+endtime);
            ahmNamePair.put("begintime", begintime);
            ahmNamePair.put("endtime", endtime);
            

            
            if (start > 0) {
                ahmNamePair.put("start", "" + start);
            }
            ahmNamePair.put("count", "" + perpage);

            HashMap responseMap = null;
            try {
                // ��POST����ʽ��������
                
                responseMap = CPManager.getConsumeHistoryList(ahmHeaderMap,
                        ahmNamePair);
                //mHandler.sendEmptyMessage(CLOSE_PD);
                if(responseMap.get("result-code")!=null){
                    Logger.i(TAG,"result-code:"+responseMap.get("result-code").toString());
                }
                if (!responseMap.get("result-code").toString().contains(
                        "result-code: 0")) {
                    mHandler.sendEmptyMessage(ERR_RETCODE_NOT0);
                    Logger.i(TAG, responseMap.get("result-code").toString());
                    mHandler.post(enableButtons);
                    loading = false;
                    return;
                }
            } catch (HttpException e) {
                Logger.e(TAG, e.getMessage());
                mHandler.sendEmptyMessage(ERR_CONNECT_EXP);
                mHandler.post(enableButtons);
                loading = false;
                return;
            } catch (SocketTimeoutException e) {
                mHandler.sendEmptyMessage(ERR_CONNECT_TIMEOUT);
                mHandler.post(enableButtons);
                loading = false;
                return;
            } catch (IOException e) {
                Logger.e(TAG, e.getMessage());
                mHandler.sendEmptyMessage(ERR_CONNECT_FAILED);
                mHandler.post(enableButtons);
                loading = false;
                return;
            }

            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");

            // ���ݷ����ֽ����鹹��DOM
            Document dom = null;
            try {
                String xml = new String(responseBody);
                xml = xml.replaceAll(">\\s+?<", "><");
                dom = CPManagerUtil.getDocumentFrombyteArray(xml.getBytes());
            } catch (ParserConfigurationException e) {
                Logger.e(TAG, "ParserConfigurationException" + e.getMessage());
                mHandler.sendEmptyMessage(ERR_XML_PARSER);
                mHandler.post(enableButtons);
                loading = false;
                return;
            } catch (SAXException e) {
                Logger.e(TAG, "SAXException" + e.getMessage());
                mHandler.sendEmptyMessage(ERR_XML_PARSER);
                mHandler.post(enableButtons);
                loading = false;                
                return;
            } catch (IOException e) {
                Logger.e(TAG, "IOException" + e.getMessage());
                mHandler.sendEmptyMessage(ERR_XML_PARSER);
                mHandler.post(enableButtons);
                loading = false;
                return;
            }
            
            try {
                //����б�����
                mHandler.post(new Runnable(){
                    @Override
                    public void run() {
                        expenseList.clear();
                    }});

                Element root = dom.getDocumentElement();
                // ����Ϊ����XML�Ĵ���
                NodeList nl1 = root.getChildNodes();
                nl1 = nl1.item(0).getChildNodes();
                int nl1Count = nl1.getLength();
                for (int i = 0; i < nl1Count; i++) {
                    Element el1 = (Element) nl1.item(i);
                    if (el1.getNodeName().equals("totalRecordCount")) {
                        total = Integer.parseInt(el1.getFirstChild()
                                .getNodeValue());
                        pages = total / perpage;
                        if (total % perpage > 0) {
                            pages = pages + 1;
                        }
                        
                        if (curpage == 0 && total > 0) {
                            curpage = 1;
                        }
                        
                        mHandler.sendEmptyMessage(UPDATA_PAGER);

                    } else if (el1.getNodeName().equals("totalFee")) {
                        totalFee = Double.parseDouble(el1.getFirstChild()
                                .getNodeValue());
                    } else if (el1.getNodeName().equals("totalTicketFee")) {
                        totalTicketFee = Double.parseDouble(el1.getFirstChild()
                                .getNodeValue());
                    } else if (el1.getNodeName().equals("ConsumeRecordList")) {
                        NodeList nl2 = el1.getChildNodes();
                        int nl2Count = nl2.getLength();
                        for (int j = 0; j < nl2Count; j++) {
                            Element el2 = (Element) nl2.item(j);
                            if (el2.getNodeName().equals("ConsumeRecord")) {
                                final HashMap<String, String> tempHM = new HashMap<String, String>();
                                NodeList nl3 = el2.getChildNodes();
                                int nl3Count = nl3.getLength();
                                for (int k = 0; k < nl3Count; k++) {
                                    Element el3 = (Element) nl3.item(k);
                                    tempHM.put(el3.getNodeName(), el3
                                            .getFirstChild().getNodeValue());
                                }
                                
                                mHandler.post(new Runnable(){
                                    @Override
                                    public void run() {
                                        expenseList.add(tempHM);
                                }});
                                
                            }
                        }

                    }
                }
                
                
                mHandler.sendEmptyMessage(SET_UI_DATA_EXPLIST);
                mHandler.post(enableButtons);
                

            } catch (Exception e) {
                Logger.e(TAG, "xml parser error:" + e.getMessage());
                mHandler.sendEmptyMessage(ERR_XML_PARSER);
                mHandler.post(enableButtons);
                loading = false;
                return;
            }            

            loading = false;
        }
    };

    private Runnable getUserInfo = new Runnable() {
        @Override
        public void run() {
            loading = true;
           /* Logger.i(TAG, "getUserInfo");
            // Call ContentManager to getUserInfo then set to display
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

            HashMap responseMap = null;
            try {
                //mHandler.sendEmptyMessage(SHOW_PD_LOADING);
                responseMap = CPManager.getUserInfo(ahmHeaderMap, ahmNamePair);
                //mHandler.sendEmptyMessage(CLOSE_PD);
                if (!responseMap.get("result-code").toString().contains(
                        "result-code: 0")) {
                    mHandler.sendEmptyMessage(ERR_RETCODE_NOT0);
                    Logger.i(TAG, responseMap.get("result-code").toString());
                    mHandler.post(enableButtons);
                    loading = false;
                    return;
                }
            } catch (HttpException e) {
                Logger.e(TAG, e.getMessage());
                mHandler.sendEmptyMessage(ERR_CONNECT_EXP);
                mHandler.post(enableButtons);
                loading = false;
                return;
            } catch (SocketTimeoutException e) {
                mHandler.sendEmptyMessage(ERR_CONNECT_TIMEOUT);
                mHandler.post(enableButtons);
                loading = false;
                return;
            } catch (IOException e) {
                Logger.e(TAG, e.getMessage());
                mHandler.sendEmptyMessage(ERR_CONNECT_EXP);
                mHandler.post(enableButtons);
                loading = false;
                return;
            }

            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            Document dom = null;
            try {
                dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);

            } catch (ParserConfigurationException e) {
                Logger.e(TAG, e.getMessage());
                mHandler.sendEmptyMessage(ERR_XML_PARSER);
                mHandler.post(enableButtons);
                loading = false;
                return;
            } catch (SAXException e) {
                Logger.e(TAG, e.getMessage());
                mHandler.sendEmptyMessage(ERR_XML_PARSER);
                mHandler.post(enableButtons);
                loading = false;
                return;
            } catch (IOException e) {
                Logger.e(TAG, e.getMessage());
                mHandler.sendEmptyMessage(ERR_XML_PARSER);
                mHandler.post(enableButtons);
                loading = false;
                return;
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
                    value = "";
                }
                userinfo.put(name, value);
            }
			*/
            mHandler.sendEmptyMessage(SET_UI_DATA_PHONE);
            
            mHandler.post(enableButtons);
            loading = false;

        }
    };
    protected int fetchFlag;

    private Runnable getHandsetUserTicketInfo = new Runnable() {
        @Override
        public void run() {
            loading = true;
             ticketInfo = "";
            fetchFlag = 0;
            getMyticketList();
            
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            try {
                //mHandler.sendEmptyMessage(SHOW_PD_LOADING);
                responseMap = CPManager.getHandsetUserTicketInfo(ahmHeaderMap,
                        ahmNamePair);
                //mHandler.sendEmptyMessage(CLOSE_PD);
                if (responseMap.get("result-code").toString().contains(
                        "result-code: 3226")) {
                    fetchFlag = -2;
                    mHandler.sendEmptyMessage(SHOW_PD_NO_TICKET_INFO);
                    mHandler.post(enableButtons);
                    
                
                    
                    loading = false;
                    return;
                } else if (!responseMap.get("result-code").toString().contains(
                        "result-code: 0")) {
                    // �ӿڷ��ر��״̬��
                    fetchFlag = -1;
                    mHandler.sendEmptyMessage(ERR_RETCODE_NOT0);
                    mHandler.post(enableButtons);
                    loading = false;
                    return;
                }
            } catch (HttpException e) {
                fetchFlag = -1;
                // �����쳣 ,һ��ԭ��Ϊ URL����
                Logger.e(TAG, e.getMessage());
                mHandler.sendEmptyMessage(ERR_CONNECT_EXP);
                mHandler.post(enableButtons);
                loading = false;
                return;
            } catch (SocketTimeoutException e) {
                fetchFlag = -1;
                mHandler.sendEmptyMessage(ERR_CONNECT_TIMEOUT);
                mHandler.post(enableButtons);
                loading = false;
                return;
            } catch (IOException e) {
                fetchFlag = -1;
                // IO�쳣 ,һ��ԭ��Ϊ��������
                Logger.e(TAG, e.getMessage());
                mHandler.sendEmptyMessage(ERR_CONNECT_FAILED);
                mHandler.post(enableButtons);
                loading = false;
                return;
            }


            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");

            // ���ݷ����ֽ����鹹��DOM
            Document dom = null;
            try {
                dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
            } catch (ParserConfigurationException e) {
                fetchFlag = -1;
                Logger.e(TAG, e.getMessage());
                mHandler.sendEmptyMessage(ERR_XML_PARSER);
                mHandler.post(enableButtons);
                loading = false;
                return;
            } catch (SAXException e) {
                fetchFlag = -1;
                Logger.e(TAG, e.getMessage());
                mHandler.sendEmptyMessage(ERR_XML_PARSER);
                mHandler.post(enableButtons);
                loading = false;
                return;
            } catch (IOException e) {
                fetchFlag = -1;
                Logger.e(TAG, e.getMessage());
                mHandler.sendEmptyMessage(ERR_XML_PARSER);
                mHandler.post(enableButtons);
                loading = false;
                return;
            }
            Element root = dom.getDocumentElement();

            // ����Ϊ����XML�Ĵ���
            try {
                NodeList nl = root.getElementsByTagName("ticketInfo");
                if (nl.getLength() != 0) {
                    Element element = (Element) nl.item(0);
                    ticketInfo = element.getFirstChild().getNodeValue();
                    fetchFlag = 1;
                    //mHandler.sendEmptyMessage(SET_UI_DATA_TICKET);
                    //mHandler.post(enableButtons);
                }
            } catch (Exception e) {
                fetchFlag = -1;
                Logger.e(TAG, e.getMessage());
                mHandler.sendEmptyMessage(ERR_XML_PARSER);
                mHandler.post(enableButtons);
                loading = false;
                return;
            }

            loading = false;
            
            //��ת����ȯ�б�ҳ��
            Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
            Bundle bundleToSend = new Bundle();
            bundleToSend.putString("startType", "allwaysCreate");
            bundleToSend.putString("actID", "ACT14320");
            
            bundleToSend.putInt("totalCount",totalCount);
            bundleToSend.putString("ticketInfo",ticketInfo);
            bundleToSend.putStringArrayList("ticketList", (ArrayList<String>)myticketList);
            tmpIntent.putExtras(bundleToSend);
            sendBroadcast(tmpIntent);
            tmpIntent = null;
            bundleToSend = null;
            
            
        }
    };

    // ��������ͳ���ı�
    private void setExpenseText() {
        Logger.i(TAG, "setExpenseText");
        this.totalexpense.setText("�����ܼƣ� " + totalFee / 100 + "Ԫ(������ȯ"
                + totalTicketFee / 100 + "Ԫ)");
        this.totalConsume.setText(totalFee / 100 + " Ԫ");
    }

    @Override
    public void bindEvent() {
        super.bindEvent();
        

        
        
        //�ҵ���ȯ
        this.myticket.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.post(disableButtons);
                final Runnable showTip = new Runnable() {
                    @Override
                    public void run() {
                        final Intent tmpIntent = new Intent(
                                MainpageActivity.SHOW_TIP);
                        final Bundle sndBundle = new Bundle();
                        sndBundle.putString("pviapfStatusTip","���ڽ�����ȯ�б�...");
                        //sndBundle.putString("pviapfStatusTipTime",
                        //        "2000");
                        tmpIntent.putExtras(sndBundle);
                        sendBroadcast(tmpIntent);
                    }
                };
                new Thread() {
                    public void run() {
                        mHandler.post(showTip);
                    }
                }.start();
                getTicketInfo();
            }
        });
        
        this.select.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
            	if (isOutOfDate()) {
            		mHandler.sendEmptyMessage(NOTE_INFO2);
                    mHandler.post(enableButtons);
                    return;
                } 
//            	if(deviceType==1){
//                    getWindow().
//                    getDecorView()
//                    .getRootView()
//                    .setUpdateMode(View.EINK_WAIT_MODE_WAIT |
//                            View.EINK_WAVEFORM_MODE_GC16 | 
//                            View.EINK_UPDATE_MODE_FULL); 
//            	}
            	showMessage("���ڼ�������...");
            	mHandler.post(disableButtons);
                nextMon();
            }
        });
    }

    /*
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        
        return super.onKeyUp(keyCode, event);
    }*/

    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
        	//System.out.println(mlvExpenseList.hasFocus());
			if(!listView.hasFocus()){
				return false;
			}
        	if(curpage > 1){
        		mHandler.post(disableButtons);
        		prevPage();
        		return true;
        	}else{
        		return true;
        	}

        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
        	//System.out.println(mlvExpenseList.hasFocus());
        	if(!listView.hasFocus()){
				return false;
			}
        	if(curpage < pages){
        		mHandler.post(disableButtons);
        		nextPage();
        		return true;
        	}else{
        		return true;
        	}
        	
        }
		return super.onKeyDown(keyCode, event);
	}

	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {
            closePopmenu();
            String vTag = item.id;
            if (vTag.equals("myticket")) {
                getTicketInfo();
            } else if (vTag.equals("lastmon")) {// ��������
                prevMon();
            } else if (vTag.equals("nextmon")) {// ��������
                nextMon();
            }
        }};
    
    



    private void getTicketInfo() {
        if(!loading){
            mHandler.post(disableButtons);
            mHandler.sendEmptyMessage(GET_DATA_2);
        }
    }

  
    public OnUiItemClickListener getMenuclick() {
		return this.menuclick;
	}
    
 /*   private Runnable showTicket = new Runnable() {
        @Override
        public void run() {
            
        }
    };

    private Thread showTicket = new Thread() {
        @Override
        public void run() {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                ;
            }
            int i = 0;
            while (i < 50) {
                if (fetchFlag != 0) {
                    if (fetchFlag == -2) {
                        ticketInfo = "û��������ȯ��Ϣ��";
                    } else if (fetchFlag == -1) {
                        ticketInfo = "�Բ�����ʱδ�ܻ��������ȯ��Ϣ��������Ժ����ԡ�";
                    }
                    pd = new PviAlertDialog(getParent());
                    pd.setTitle("�ҵ���ȯ");
                    pd.setMessage(ticketInfo);
                    pd.setCanClose(true);
                    pd.show();
                    return;
                }
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    ;
                }
                i++;
            }
            Logger.i(TAG, "showTicket timeout");
        }
    };*/

    private void prevPage() {        
        if(!loading){
        	if(isOutOfDate()){
        		mHandler.sendEmptyMessage(NOTE_INFO2);
                mHandler.post(enableButtons);
                return;
        	}
            if (total > 0 && curpage > 1) {
            	showMessage("���ڼ�������...");
            	mHandler.post(disableButtons);
                curpage = curpage - 1;
                start = (curpage - 1) * perpage + 1;
                mHandler.sendEmptyMessage(GET_DATA);
            }
            
        }
    }

    private void nextPage() {
        if(!loading){
        	if(isOutOfDate()){
        		mHandler.sendEmptyMessage(NOTE_INFO2);
                mHandler.post(enableButtons);
                return;
        	}
            if (total > 0 && curpage < pages) {
            	showMessage("���ڼ�������...");
            	mHandler.post(disableButtons);
                curpage = curpage + 1;
                start = (curpage - 1) * perpage + 1;
                mHandler.sendEmptyMessage(GET_DATA);
            }
            //mHandler.post(enableButtons);
        }
    }

    private void prevMon() {   
        if(!loading){
            mHandler.post(disableButtons);
            Logger.i(TAG,"+prevMon");
            // ��ҳ����ʼ��
            start = 0;
            curpage = 0;
            // �ꡢ�¸���
            if (curYearMonIndex > 0) {
                curYearMonIndex--;
                getQueryYearMon();
                // �������ݲ�ѯ
                mHandler.sendEmptyMessage(GET_DATA);
            } else {
                // ��ʾ�û�ֻ�ܲ���ô��
                mHandler.sendEmptyMessage(NOTE_INFO1);
                mHandler.post(enableButtons);
                return;
            }
        }else{
            Logger.i(TAG,"-prevMon");
        }

    }
    
    
    public boolean isOutOfDate(){
    	boolean isOutOfDate = true;
    	String year = yearSelect.getSelectValue();
    	String month = monthSelect.getSelectValue();
    	for(int i = 0;i<validYearMon.size();i++){
    		String[] str = validYearMon.get(i);
    		if(year.equals(str[0]) && month.equals(str[1])){
    			isOutOfDate = false;
    			return isOutOfDate;
    		}
    	}
    	return isOutOfDate;
    }

    private void nextMon() {
        if(!loading){
            mHandler.post(disableButtons);
            Logger.i(TAG,"+nextMon");
            // ��ҳ����ʼ��
            if (!isOutOfDate()) {
            	start = 0;
                curpage = 0;
                curYearMonIndex++;
                getQueryYearMon();
                // �������ݲ�ѯ
                
                mHandler.sendEmptyMessage(GET_DATA);
            } else {
                // ��ʾ�û�ֻ�ܲ���ô��
                mHandler.sendEmptyMessage(NOTE_INFO2);
                mHandler.post(enableButtons);
                return;
            }
            
            // �ꡢ�¸���
            /*
            if (curYearMonIndex < maxQueryMonthCount - 1) {
                curYearMonIndex++;
                getQueryYearMon();
                // �������ݲ�ѯ
                
                mHandler.sendEmptyMessage(GET_DATA);
            } else {
                // ��ʾ�û�ֻ�ܲ���ô��
                mHandler.sendEmptyMessage(NOTE_INFO2);
                mHandler.post(enableButtons);
                return;
            }*/
        }else{
            Logger.i(TAG,"-nextMon");
        }
    }

    private void showAlert(String message, boolean canClose) {
        pd = new PviAlertDialog(getParent());
        pd.setTitle(getResources().getString(R.string.my_friend_hint));
        pd.setMessage(message);
        pd.setCanClose(canClose);
        pd.setTimeout(4000);
        pd.show();
    }
    
    public void showAlert(String message){ 
    	if(pd!=null && pd.isShowing()){
    		pd.dismiss();
    	}
        pd = new PviAlertDialog(getParent());
        pd.setTitle(getResources().getString(R.string.my_friend_hint));
        //pd.setMessage(message);
        TextView tv = new TextView(ExpenseProActivity.this);
        tv.setText(message);
        tv.setTextSize(21);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.BLACK);
        
        pd.setView(tv);
        pd.setCanClose(true);
        pd.show();
    }
    
    private void updataPager() {
    	int a = curpage;
    	int b = (pages==0?1:pages);
    	if(a > b){
    		a = b;
    	}
    	updatePagerinfo(a+" / "+b);
    }
    
    private Runnable enableButtons = new Runnable(){

        @Override
        public void run() {
          
            myticket.setClickable(true);
            select.setClickable(true);
        
        }
        
    };
    
    private Runnable disableButtons = new Runnable(){

        @Override
        public void run() {

        	 myticket.setClickable(false);
             select.setClickable(false);
       
        }
        
    };
    
    //��ȡ�ҵ���ȯ�б�ĵ�һҳ����
    public void getMyticketList(){
    	
    	if(myticketList != null){
    		return;
    	}
    	myticketList = new ArrayList<String>();
    	  HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
          HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
          ahmNamePair.put("start", "0");
          ahmNamePair.put("count", "7");
          HashMap responseMap = null;
          try {
              //mHandler.sendEmptyMessage(SHOW_PD_LOADING);
              responseMap = CPManager.getUserTicketList(ahmHeaderMap,
                      ahmNamePair);
              //mHandler.sendEmptyMessage(CLOSE_PD);
              if (responseMap.get("result-code").toString().contains(
                      "result-code: 3226")) {
                  fetchFlag = -2;
                  mHandler.sendEmptyMessage(SHOW_PD_NO_TICKET_INFO);
                  mHandler.post(enableButtons);
                  loading = false;
                  return;
              } else if (!responseMap.get("result-code").toString().contains(
                      "result-code: 0")) {
                  // �ӿڷ��ر��״̬��
                  fetchFlag = -1;
                  mHandler.sendEmptyMessage(ERR_RETCODE_NOT0);
                  mHandler.post(enableButtons);
                  loading = false;
                  return;
              }
          } catch (HttpException e) {
              fetchFlag = -1;
              // �����쳣 ,һ��ԭ��Ϊ URL����
              Logger.e(TAG, e.getMessage());
              mHandler.sendEmptyMessage(ERR_CONNECT_EXP);
              mHandler.post(enableButtons);
              loading = false;
              return;
          } catch (SocketTimeoutException e) {
              fetchFlag = -1;
              mHandler.sendEmptyMessage(ERR_CONNECT_TIMEOUT);
              mHandler.post(enableButtons);
              loading = false;
              return;
          } catch (IOException e) {
              fetchFlag = -1;
              // IO�쳣 ,һ��ԭ��Ϊ��������
              Logger.e(TAG, e.getMessage());
              mHandler.sendEmptyMessage(ERR_CONNECT_FAILED);
              mHandler.post(enableButtons);
              loading = false;
              return;
          }


          byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
          try {
			System.out.println(CPManagerUtil.getStringFrombyteArray(responseBody));
          } catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

          // ���ݷ����ֽ����鹹��DOM
          Document dom = null;
          try {
              dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
          } catch (ParserConfigurationException e) {
              fetchFlag = -1;
              Logger.e(TAG, e.getMessage());
              mHandler.sendEmptyMessage(ERR_XML_PARSER);
              mHandler.post(enableButtons);
              loading = false;
              return;
          } catch (SAXException e) {
              fetchFlag = -1;
              Logger.e(TAG, e.getMessage());
              mHandler.sendEmptyMessage(ERR_XML_PARSER);
              mHandler.post(enableButtons);
              loading = false;
              return;
          } catch (IOException e) {
              fetchFlag = -1;
              Logger.e(TAG, e.getMessage());
              mHandler.sendEmptyMessage(ERR_XML_PARSER);
              mHandler.post(enableButtons);
              loading = false;
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
	  			myticketList.add(nl.item(i).getFirstChild().getNodeValue());
	  		  }
  		  }
  		  System.out.println("====myticketList==="+myticketList);
  		  loading = false;
    }

    
    @Override
	public void OnNextpage() {
		// TODO Auto-generated method stub
    	if(curpage < pages){
    		mHandler.post(disableButtons);
            nextPage();
    	}
	}

	@Override
	public void OnPrevpage() {
		// TODO Auto-generated method stub
		if(curpage > 1){
            mHandler.post(disableButtons);
            prevPage();
    	}
	}

}
