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
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.InactiveFunction;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;


/**
 * 分册列表
 * @author rd040
 *
 */
public class FascicleListActivity extends PviActivity {

	protected static final String TAG = "FascicleListActivity";
	private Context mContext = FascicleListActivity.this;
   
    private int[] tvFeeDescriptionIds = {
            R.id.textViewFeeDescription0,
            R.id.textViewFeeDescription1,
            R.id.textViewFeeDescription2,
            R.id.textViewFeeDescription3,
            R.id.textViewFeeDescription4,
            R.id.textViewFeeDescription5,
            R.id.textViewFeeDescription6,
            R.id.textViewFeeDescription7
    };
    
    private int[] tvFascicleDescriptionIds = {
            R.id.textViewFascicleDescription0,
            R.id.textViewFascicleDescription1,
            R.id.textViewFascicleDescription2,
            R.id.textViewFascicleDescription3,
            R.id.textViewFascicleDescription4,
            R.id.textViewFascicleDescription5,
            R.id.textViewFascicleDescription6,
            R.id.textViewFascicleDescription7
    };
    
    private int[] bOpIds = {R.id.buttonOp0,
            R.id.buttonOp1,
            R.id.buttonOp2,
            R.id.buttonOp3,
            R.id.buttonOp4,
            R.id.buttonOp5,
            R.id.buttonOp6,
            R.id.buttonOp7
    };
    
    private TextView[] tvFeeDescription = new TextView[8];
    private TextView[] tvFascicleDescription = new TextView[8];
    private Button[] bOp = new Button[8];

    
	private ArrayList<HashMap<String, Object>> volinfo = new ArrayList<HashMap<String, Object>>();

	private String contentID = "";

	private int currentPage = 1;
	private int itemPerPage =8;
	private int totalPage = 0;



	private int selindex = 0;
	private String ChapterID = "";
	private Handler getbookinfo = null;
	private PviAlertDialog pd= null;
	
	private HashMap<String,Object> readHistory= null;
	
	final static int P1 = 201;         //订购分册的过程


	/**
	 * （同步）过程：订购分册
	 */
	private void p1(){
	    HashMap<String, Object> map1 = null;
        map1 = volinfo.get(selindex);
        String ret = SubscribeProcess.network("subscribeContent", contentID,   null,  map1.get("productID").toString(), map1.get("fascicleID").toString());
        Logger.d(TAG,ret);
        
        //如果订购成功，跳转到阅读界面
        if(ret!=null && ret.length()>18 && (ret.substring(0, 19).contains("0000")||ret.substring(0, 19).contains("3159")) ){
            getbookinfo.sendEmptyMessage(1);
        }
	}
	

	public void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.fasciclelist_ui1);
		
		String bookname = "";

		try
		{
			contentID = this.getIntent().getExtras().getString("contentID");
			bookname = this.getIntent().getExtras().getString("BookName");
		}
		catch(Exception e)
		{
			contentID="";
			bookname= "未知";
		}
		Intent intent = new Intent(MainpageActivity.SET_TITLE);
		Bundle sndBundle1 = new Bundle();
		sndBundle1.putString("title", bookname+"[>>]分册列表");
		intent.putExtras(sndBundle1);
		sendBroadcast(intent);		

		getbookinfo = new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch(msg.what)
				{
				case 0:
				    if(pd!=null){
					pd.dismiss();
				    }
					break;
				case 1:

					HashMap<String, Object> map = null;
					map = volinfo.get(selindex);
					ChapterID = map.get("startChapterID").toString();
					Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle sndBundle = new Bundle();
					sndBundle.putString("act",
					"com.pvi.ap.reader.activity.ReadingOnlineActivity");
					sndBundle.putString("ContentID", contentID);
					sndBundle.putString("ChapterID", ChapterID);
					//sndBundle.putString("startType", "allwaysCreate");
					intent.putExtras(sndBundle);
					sendBroadcast(intent);
					break;
				case 2:
					//提示是否进入阅读
					pd = new PviAlertDialog(FascicleListActivity.this);
					pd.setTitle("温馨提示");
					pd.setMessage("该分册已经订购，请点击进入阅读");
					pd.setButton(DialogInterface.BUTTON_POSITIVE,
							"进入阅读",
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							getbookinfo.sendEmptyMessage(1);
						}
					});
					pd.setButton(DialogInterface.BUTTON_NEGATIVE,
							"取消",
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							return;
						}
					});
					pd.show();
					break;
				case 3:
					//提示是否订购
					pd = new PviAlertDialog(FascicleListActivity.this);
					pd.setTitle("温馨提示");
					pd.setMessage("该分册未订购，请确认订购");
					pd.setButton(DialogInterface.BUTTON_POSITIVE,
							"确认订购",
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							getbookinfo.sendEmptyMessage(4);
							pd.dismiss();
						}
					});
					pd.setButton(DialogInterface.BUTTON_NEGATIVE,
							"取消",
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							pd.dismiss();
							return;
						}
					});
					pd.show();
					break;
				case 4:
					//订购流程
					HashMap<String, Object> map1 = null;
					Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle sndBundle1 = new Bundle();
					sndBundle1.putString("act",
					"com.pvi.ap.reader.activity.SubscribeProcess");
					sndBundle1.putString("contentID", contentID);

					sndBundle1.putString("subscribeMode", "fascicleList");
					map1 = volinfo.get(selindex);
					//					if (booksummary.get("FascicleFlag").toString().equals("1")) {
					//						sndBundle1.putString("fascicle", "1");
					//					} 
					//					if (booksummary.get("CanDownload").toString().equals("false")) {
					if(map1.containsKey("productID"))
					{
						sndBundle1.putString("ProductID", map1.get("productID").toString());
					}
					else
					{
						sndBundle1.putString("ProductID", "");
					}

					if(map1.containsKey("fascicleID"))
					{
						sndBundle1.putString("fascicle", map1.get("fascicleID").toString());
					}
					else
					{
						sndBundle1.putString("fascicle", "");
					}
					if(map1.containsKey("feeDescription"))
					{
						sndBundle1.putString("chargeTip", map1.get("feeDescription").toString());
					}
					else
					{
						sndBundle1.putString("chargeTip", "");
					}
					if(map1.containsKey("fascicleDescription"))
					{
						sndBundle1.putString("catalogID", map1.get("fascicleDescription").toString());
					}
					else
					{
						sndBundle1.putString("catalogID", "");
					}
					intent1.putExtras(sndBundle1);
					sendBroadcast(intent1);
			
				case P1:
				    new Thread() {
	                    public void run() {
	                        p1();
	                    }
	                }.start();	                    
	                break;
	
					
				}
			}
		};
		
		
/*		this.subscribebtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//
				boolean issubscribe = false;
				HashMap<String, Object> map = null;
				map = volinfo.get(selindex);
				if(map.get("isSubscribe").toString().equals("0"))
				{
					//未订购  goto dinggou
					getbookinfo.sendEmptyMessage(3);
				}
				else
				{
					//已订购   goto readingonline
					getbookinfo.sendEmptyMessage(2);

				}
			}
		});*/
	
		
		super.onCreate(savedInstanceState);
	}


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {

            boolean isinList = false;

            for (int i = 0; i < 8; i++) {
                if (bOp[i] != null && bOp[i].hasFocus()) {
                    isinList = true;
                    break;
                }
            }

            if (isinList) {

                try {
                    if (currentPage == 1) {
                        return true;
                    }
                    currentPage--;

                    setUIData();
                    // onCreate(null);
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("Reader", "pre page: " + e.toString());
                }
                return true;
            } 

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {


            boolean isinList = false;

            for (int i = 0; i < 8; i++) {
                if (bOp[i] != null && bOp[i].hasFocus()) {
                    isinList = true;
                    break;
                }
            }

            if (isinList) {
                try {
                    if (currentPage == totalPage) {
                        return true;
                    }
                    currentPage++;
                    setUIData();
                    // onCreate(null);
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("Reader", "next page: " + e.toString());
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }   

	private void setUIData() {
		new Thread() {
			public void run() {
			    showTip();

				String start = String.valueOf((currentPage-1)*itemPerPage+1);
				String count = String.valueOf(itemPerPage);
				String ret = getFascicleInfo(start, count);
				if(!ret.equals("0"))
				{
					getbookinfo.sendEmptyMessage(0);
					return;
				}
				getbookinfo.post(getData);
			}
		}.start();
	}
	private Runnable getData = new Runnable() {
		@Override
		public void run() {
			try {
			    Logger.d(TAG,"getData run(...");

				Intent intent11 = new Intent(MainpageActivity.HIDE_TIP);	
				sendBroadcast(intent11);
				
				getbookinfo.sendEmptyMessage(0);
				
				totalPage = (int) Math.ceil((double)totalPage/itemPerPage);

				PviBottomBar pbb = ((GlobalVar)getApplication()).pbb;
		        if(totalPage>0){
		            showPager();
		            updatePagerinfo(currentPage+" / "+totalPage);
		        }else{
		            hidePager();
		        }

				HashMap<String, Object> map = new HashMap<String, Object>();
				for(int i = 0; i < itemPerPage ; i++)
				{
					if(i < volinfo.size())
					{
					    //给一行UI填充数据
						map = volinfo.get(i);
						
						tvFascicleDescription[i].setText(map.get("fascicleDescription").toString());
										

						tvFeeDescription[i].setVisibility(View.VISIBLE);
						tvFascicleDescription[i].setVisibility(View.VISIBLE);
						
						//如果已订购改分册，显示“开始阅读”；否则显示“确认订购”
						if(map.get("isSubscribe")!=null&&"1".equals(map.get("isSubscribe").toString())){
						    tvFeeDescription[i].setText("您已订购该分册。");
						    
						    //增加判断，取出这本书的最近阅读记录，如果存在章节id且该章节位于该分册内，则“继续阅读”
						    boolean isContinue = false;
						    try {
						        
						        String recentChapterId = "";
						        if(readHistory!=null){
						            final Object obj = readHistory.get("chapterid");
						            if(obj!=null){
						                recentChapterId = obj.toString();
						            }     
						        }
						        
						        
                                if(recentChapterId!=null&&!"".equals(recentChapterId)
                                &&    Integer.parseInt(recentChapterId)>=Integer.parseInt(map.get("startChapterID").toString())    
                                &&    Integer.parseInt(recentChapterId)<=Integer.parseInt(map.get("endChapterID").toString())    
                                
                                ){
                                    isContinue = true;
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            
                            if(!isContinue){						    
    						    bOp[i].setText("开始阅读");
    						    
    						    final int cur = i;
    						    bOp[i].setOnClickListener(new OnClickListener(){
    
                                    @Override
                                    public void onClick(View v) {
                                        selindex = cur;
                                        getbookinfo.sendEmptyMessage(1);//进入阅读
                                    }});						    
                            }else{
                                bOp[i].setText("继续阅读");
                                
                                final int cur = i;
                                bOp[i].setOnClickListener(new OnClickListener(){
    
                                    @Override
                                    public void onClick(View v) {
                                        selindex = cur;
                                        //继续阅读
                                        
                                        final Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
                                        final Bundle sndBundle1 = new Bundle();
                                        sndBundle1.putString("act", "com.pvi.ap.reader.activity.ReadingOnlineActivity");
                                        sndBundle1.putString("ContentID", contentID);
                                        sndBundle1.putString("haveTitleBar", "1");
                                        sndBundle1.putString("ChapterID", readHistory.get("chapterid")
                                                .toString());
                                        if(readHistory.containsKey("FontSize"))
                                        {
                                            sndBundle1.putString("FontSize", readHistory.get("FontSize")
                                                    .toString());
                                        }
                                        if(readHistory.containsKey("readposition"))
                                        {
                                            sndBundle1.putString("Position", readHistory.get("readposition")
                                                    .toString());
                                        }
                                        if(readHistory.containsKey("LineSpace"))
                                        {
                                            sndBundle1.putString("LineSpace", readHistory.get("LineSpace")
                                                    .toString());
                                        }
                                        intent1.putExtras(sndBundle1);
                                        sendBroadcast(intent1);
                                        
                                        
                                    }});
                            }
						    
						}else{
						    tvFeeDescription[i].setText(map.get("feeDescription").toString());
						    
						    bOp[i].setText("确认订购");
						    
						    final int cur = i;
						    bOp[i].setOnClickListener(new OnClickListener(){

                                @Override
                                public void onClick(View v) {
                                    selindex = cur;
                                    getbookinfo.sendEmptyMessage(P1);//去订购本分册
                                }});
						    
						}

						bOp[i].setOnTouchListener(new OnTouchListener(){
                            @Override
                            public boolean onTouch(View v,
                                    MotionEvent arg1) {
                                if(arg1.getAction()==MotionEvent.ACTION_UP){
                                    if(!v.hasFocus()){
                                        v.performClick();
                                    }
                                }
                                return false;
                            }});
						bOp[i].setVisibility(View.VISIBLE);		
					}
					else
					{
						//隐藏一行UI控件
					    tvFeeDescription[i].setVisibility(View.INVISIBLE);
                        tvFascicleDescription[i].setVisibility(View.INVISIBLE);
                        bOp[i].setVisibility(View.INVISIBLE);
					}
				}
				
				if(totalPage==0){
				    tvFeeDescription[0].setText("该书籍没有分册。");
				    tvFeeDescription[0].setVisibility(View.VISIBLE);
				}
				
				showMe(FascicleListActivity.class);
				
			} catch (Exception e) {
				Logger.e(TAG, e.toString());
			}
		}
	};

	private String getFascicleInfo(String start, String end) 
	{
		volinfo.clear();
		try{

			HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
			HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
			ahmNamePair.put("contentId", contentID);
			ahmNamePair.put("start", start);
			ahmNamePair.put("count", end);
			HashMap responseMap = null;

			responseMap = CPManager.getFascicleList(ahmHeaderMap, ahmNamePair);
			byte[] responseBody = (byte[])responseMap.get("ResponseBody");
			try {
				System.out.println("返回的XML为：");
				System.out.println(CPManagerUtil
						.getStringFrombyteArray(responseBody));
			} catch (Exception e) {
				e.printStackTrace();
			}
			Document dom=CPManagerUtil.getDocumentFrombyteArray(responseBody);
			NodeList nl = null;
			Element rootele = dom.getDocumentElement();
			nl = rootele.getElementsByTagName("totalRecordCount");
			this.totalPage = Integer.parseInt(nl.item(0).getFirstChild().getNodeValue());

			nl = rootele.getElementsByTagName("FascicleInfo"); 
			HashMap<String, Object> map = null;
			Element entry = null;
			Element temp = null;
			for(int i=0;i<nl.getLength();i++){
				map = new HashMap<String, Object>();
				entry = (Element)nl.item(i); 
				temp = (Element)entry.getElementsByTagName("fascicleDescription").item(0);
				map.put("fascicleDescription", temp.getFirstChild().getNodeValue());

				temp = (Element)entry.getElementsByTagName("fascicleID").item(0);
				map.put("fascicleID", temp.getFirstChild().getNodeValue());

				temp=(Element)entry.getElementsByTagName("startChapterID").item(0);
				map.put("startChapterID", temp.getFirstChild().getNodeValue());

				temp=(Element)entry.getElementsByTagName("endChapterID").item(0);
				map.put("endChapterID", temp.getFirstChild().getNodeValue());

				temp=(Element)entry.getElementsByTagName("feeDescription").item(0);
				map.put("feeDescription", temp.getFirstChild().getNodeValue());

				temp = (Element)entry.getElementsByTagName("isSubscribe").item(0);
				map.put("isSubscribe", temp.getFirstChild().getNodeValue());

				temp = (Element)entry.getElementsByTagName("productID").item(0);
				map.put("productID", temp.getFirstChild().getNodeValue());

				this.volinfo.add(map);
			}

		} catch (HttpException e) {

			e.printStackTrace();
			Logger.e("Reader", e.toString());

			return "1";
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			Logger.e("Reader", e.toString());

			return "2";
		}catch (IOException e) {
			//
			e.printStackTrace();
			Logger.e("Reader", e.toString());

			return "1";
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "3";
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "3";
		} 
		//showMe(FascicleListActivity.class);
		return "0";
	}


    @Override
    public void initControls() {
        for(int i=0;i<itemPerPage;i++){
            tvFeeDescription[i] = (TextView)findViewById(tvFeeDescriptionIds[i]);
            tvFascicleDescription[i] = (TextView)findViewById(tvFascicleDescriptionIds[i]);
            bOp[i] = (Button)findViewById(bOpIds[i]);
        } 

        super.initControls();
    }


    @Override
    protected void onResume() {
        readHistory = InactiveFunction.getReadHistory(this, contentID, true);
        setUIData();
        super.onResume();
    }


    @Override
    public void OnNextpage() {
        try
        {
            if(currentPage == totalPage){
                return ;
            }
            currentPage++;
            setUIData();
        }
        catch(Exception e)
        {
            Logger.e("Reader", "next page: " + e.toString());
        }
        super.OnNextpage();
    }


    @Override
    public void OnPrevpage() {
        try
        {
            if(currentPage == 1){
                return;
            }
            currentPage--;
            setUIData();
        }
        catch(Exception e)
        {
            Logger.e("Reader", "pre page: " + e.toString());
        }
        super.OnPrevpage();
    }


}
