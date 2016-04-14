/**
 * 无线书城框架
 * @author rd029 晏子凯
 * 
 */

package com.pvi.ap.reader.activity;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.*;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviReaderUI;
import com.pvi.ap.reader.activity.pviappframe.PviTabActivity;
import com.pvi.ap.reader.activity.pviappframe.PviTabHost;
import com.pvi.ap.reader.activity.pviappframe.PviTabWidget;
import com.pvi.ap.reader.data.common.Logger;


/**
 * 无线书城
 * @author rd038 rd040
 *
 */


public class WirelessStoreActivity extends PviTabActivity {

	private static final int LOADTAB = 101;
	
	final static String blockListLocalFile = Environment.getDataDirectory() + "/data/com.pvi.ap.reader/files/"+"blocklist.dat";

//	private Handler retryHandler = new Handler() {
//		public void handleMessage(Message msg) {
//
//			final PviAlertDialog pd = new PviAlertDialog(getParent());
//			pd.setTitle(R.string.kyleHint02);
//			pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
//					new android.content.DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog,int which) {
//					onCreate(null);
//				}
//			});
//			pd.setButton(DialogInterface.BUTTON_NEUTRAL, "取消",
//					new android.content.DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog,int which) {
//					sendBroadcast(new Intent(
//							WirelessTabActivity.BACK));
//				}
//
//			});
//			pd.show();
//
//		};
//	};
	private static final String TAG = "WirelessStoreActivity";
	private static final String THIS_ACT = "com.pvi.ap.reader.activity.WirelessStoreActivity";
	private Handler mHandler = new H();
	private Context mContext = WirelessStoreActivity.this;
	private String[] blockNames;

	private int error = 0;

	String blockName[] = new String[6];
	String blockId[] = new String[6];
	String blockType[] = new String[6];

//	private Button closebtn = null;
//	private ArrayList<HashMap<String, Object>> BroadcastInfo = new ArrayList<HashMap<String, Object>>();

	PviTabHost mTabHost; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		/*    	 Intent tmpintent = new Intent(MainpageActivity.SET_TITLE);
    	  Bundle sndBundle = new Bundle();
    	  sndBundle.putString("title", "无线书城");
    	  tmpintent.putExtras(sndBundle);
    	  sendBroadcast(tmpintent);*/

		super.onCreate(savedInstanceState);


		/*


		intent[0] = new Intent(this, WirelessTabActivity.class);
		intent[1] = new Intent(this, WirelessTabActivity.class);
		intent[2] = new Intent(this, WirelessTabActivity.class);
		intent[3] = new Intent(this, WirelessTabActivity.class);
		intent[4] = new Intent(this, WirelessTabActivity.class);
		intent[5] = new Intent(this, WirelessTabActivity.class);

		for(int i = 0;i < 6;i ++){
			Bundle tmpBundle = new Bundle();
			tmpBundle.putString("tab",i+"");
			intent[i].putExtras(tmpBundle);
		}
		//get block structure from globalvar

		blockName[0] = appState.getblockName(0);
		if(null == blockName[0] || "" == blockName[0]){
			WirelessStoreMainpageActivity.getStoreBlockList(appState);
		}
		for (int i = 0; i < 6; i++) { 
			blockName[i] = appState.getblockName(i);
			blockId[i] = appState.getblockId(i);
			blockType[i] = appState.getblockType(i);
		}





   	   for (int i = 0; i < 6; i++) { 
	       Bundle bundleToSend = new Bundle();
		   bundleToSend.putString("blockid",blockId[i]);
		   bundleToSend.putString("blocktype",blockType[i]);
		   intent[i].putExtras(bundleToSend);		
   	   }
		 */


		/*2011-5-2注释掉！*/
		/*		   if(appState.getblockName(5) == null ){ 
		       Logger.d(TAG,"非得要在这里取数据吗？？？");
	    	if(0 != WirelessStoreMainpageActivity.getStoreBlockList(appState)){
	    		return;
	    	}
		 }*/  	   

	}

    @Override
    public void onResume() {

        if (error != 0) {
            sendBroadcast(new Intent(WirelessTabActivity.BACK));
        }

        // mHandler.sendEmptyMessage(LOADTAB); //异步生成界面，会有问题

        if (blockNames == null) {
            //Logger.d(TAG,"blockNames is not loaded");
            //if(getStoreBlockList()==0){
                //Logger.d(TAG,"get right xml");
                mTabHost = getTabHost();
                if (mTabHost.getTabWidget() != null
                        && mTabHost.getTabWidget().getTabCount() != 6) {
                    //Logger.d(TAG,"build tab");
                    addTab.run();
                    //Logger.d(TAG,"resume a tab");
                    resumeTab.run();
                }
            /*}else{ 
                final Intent tip = new Intent(MainpageActivity.SHOW_TIP);
                final Bundle data = new Bundle();
                data.putString("pviapfStatusTip", "系统错误请重试。");
                data.putString("pviapfStatusTipTime", "3000");
                tip.putExtras(data);
                sendBroadcast(tip);
                Logger.e(TAG,"system err");
            }*/
        } else {
            resumeTab.run();
            //Logger.d(TAG,"resume a tab");
        }

        super.onResume();

    }

 

	private class H extends Handler {


		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case LOADTAB:
				new Thread() {
					public void run() {
						loadTab();
						//resumeTab();
					}
				}.start();
				break;

			default:
				;
			}

			super.handleMessage(msg);
		}
	}

	
	/**
	 * 根据application中保存的无线书城的主栏目数据，动态初始化TabActivity
	 */
	private Runnable addTab = new Runnable(){

		@Override
		public void run() {
		    //Logger.d(TAG,"addTab(...");

			final GlobalVar appState = ((GlobalVar) getApplicationContext());

			mTabHost = getTabHost(); 

			//          LinearLayout ll = (LinearLayout)mTabHost.getChildAt(0);
			//          TabWidget tw = (TabWidget)ll.getChildAt(0);  
			//          TODO: KIZAN MODIFIED  get the TabWidget directly
			PviTabWidget tw = mTabHost.getTabWidget();


			int tabLayoutXml = 0;
			tabLayoutXml = R.layout.tab_indicator_ui1;
			

			final HashMap<String, String[]> BlockNameList =PviReaderUI.buildBlockNameList(mContext);
			blockNames = BlockNameList.get(getPackageName()+"."+getLocalClassName());		
			setBlockNames(blockNames);
			final HashMap<String, Class> classes =  PviReaderUI.getClasses();
			final ArrayList<HashMap<String, String>> blockInfo = appState.getBlockInfo();
			if (blockNames != null) {
			    //Logger.e(TAG,"blockNames.length:"+blockNames.length);
				for (int i = 0; i < blockNames.length; i++) {
					try {
					    final HashMap<String, String> hm = blockInfo.get(i);
					    
					    if(hm!=null){
					    
						blockName[i] = hm.get("blockName");
						blockId[i] = hm.get("blockID");
						blockType[i] = hm.get("blockType");;

						//Logger.e(TAG,"name:"+blockName[i]+",id:"+blockId[i]+",type:"+blockType[i]);
						
						final Intent tmpIntent = new Intent(mContext,
								classes.get(blockName[i]));

						Bundle tmpBundle = new Bundle();
						tmpBundle.putString("tab",i+"");
						tmpBundle.putString("blockid",blockId[i]);
						tmpBundle.putString("blocktype",blockType[i]);  
						tmpBundle.putString("blockName",blockName[i]);
						//Logger.d(TAG,"初始化tab，参数："+tmpBundle);
						tmpIntent.putExtras(tmpBundle);

						
						mTabHost.addTab(mTabHost.newTabSpec(blockName[i])
								.setContent(
										tmpIntent));
					    }else{
					        Logger.e(TAG,"hm is null,i:"+i);
					    }
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} else {
				Logger.e(TAG, "blockName is null");                
			}



			/*      RelativeLayout tabIndicator1 = (RelativeLayout) LayoutInflater.from(this).inflate(tabLayoutXml, tw, false);   
                    RelativeLayout tabIndicator2 = (RelativeLayout) LayoutInflater.from(this).inflate(tabLayoutXml, tw, false);   
                    RelativeLayout tabIndicator3 = (RelativeLayout) LayoutInflater.from(this).inflate(tabLayoutXml, tw, false);   
                    RelativeLayout tabIndicator4 = (RelativeLayout) LayoutInflater.from(this).inflate(tabLayoutXml, tw, false);   
                    RelativeLayout tabIndicator5 = (RelativeLayout) LayoutInflater.from(this).inflate(tabLayoutXml, tw, false);   
                    RelativeLayout tabIndicator6 = (RelativeLayout) LayoutInflater.from(this).inflate(tabLayoutXml, tw, false);   

                    TextView tvTab1 = (TextView)tabIndicator1.getChildAt(0);   
                    tvTab1.setText(blockName[0]); 
                    mTabHost.addTab( mTabHost.newTabSpec(blockName[0])   
                            .setIndicator(tabIndicator1)   
                            .setContent(intent[0]) );


                    TextView tvTab2 = (TextView)tabIndicator2.getChildAt(0);   
                    tvTab2.setText(blockName[1]); 
                    mTabHost.addTab( mTabHost.newTabSpec(blockName[1])   
                            .setIndicator(tabIndicator2)   
                            .setContent(intent[1]) );


                    TextView tvTab3 = (TextView)tabIndicator3.getChildAt(0);   
                    tvTab3.setText(blockName[2]); 
                    mTabHost.addTab( mTabHost.newTabSpec(blockName[2])   
                            .setIndicator(tabIndicator3)   
                            .setContent(intent[2]) );

                    TextView tvTab4 = (TextView)tabIndicator4.getChildAt(0);   
                    tvTab4.setText(blockName[3]); 
                    mTabHost.addTab( mTabHost.newTabSpec(blockName[3])   
                            .setIndicator(tabIndicator4)   
                            .setContent(intent[3]) );

                    TextView tvTab5 = (TextView)tabIndicator5.getChildAt(0);   
                    tvTab5.setText(blockName[4]); 
                    mTabHost.addTab( mTabHost.newTabSpec(blockName[4])   
                            .setIndicator(tabIndicator5)   
                            .setContent(intent[4]) );

                    TextView tvTab6 = (TextView)tabIndicator6.getChildAt(0);   
                    tvTab6.setText(blockName[5]); 
                    mTabHost.addTab( mTabHost.newTabSpec(blockName[5])   
                            .setIndicator(tabIndicator6)   
                            .setContent(intent[5]) );*/


/*			try{
				ViewGroup vg = (ViewGroup)mTabHost.getTabContentView();
				int vgCount = vg.getChildCount();
				for(int i=0;i<vgCount;i++){
					((FrameLayout)((ViewGroup)vg.getChildAt(i)).getChildAt(0)).setForeground(null);
				}
			}catch(Exception e){
			    e.printStackTrace();
			}*/

			/*mTabHost.setOnTabChangedListener(new PviTabHost.OnTabChangeListener() {

				@Override
				public void onTabChanged(String tabId) {
					//修改刷新模式
					//EPDRefresh.refreshDUDither();

					//通知框架界面发生了跳转///////////
					Intent tmpIntent = new Intent(
							MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("act", THIS_ACT);
					bundleToSend.putString("actTabName", blockNames[mTabHost.getCurrentTab()]);
					bundleToSend.putString("switchInTab", "1");//在tab中通过操作控件发生界面跳转
					tmpIntent.putExtras(bundleToSend);
					sendBroadcast(tmpIntent);
					tmpIntent = null;
					bundleToSend = null;
					//////////////////////////
					try{
						ViewGroup vg = (ViewGroup)getTabHost().getTabContentView();
						int vgCount = vg.getChildCount();
						for(int i=0;i<vgCount;i++){
							((FrameLayout)((ViewGroup)vg.getChildAt(i)).getChildAt(0)).setForeground(null);
						}
					}catch(Exception e){

					}
				}
			});*/

			// 切换TAB时（开始切换时）的提示信息
			final int tabCount = tw.getChildCount();
			for (int i = 0; i < tabCount; i++) {
				final int curTabIndex = i;
				final TextView tv = (TextView) ((RelativeLayout) tw
						.getChildAt(i)).getChildAt(0);

				//                            tv.setOnTouchListener(new OnTouchListener(){
				//
				//                              @Override
				//                              public boolean onTouch(View arg0, MotionEvent arg1) {
				//                                  // TODO Auto-generated method stub
				//                                  Logger.i(TAG," action:"+arg1.getAction());
				//                                  if(arg1.getAction()==MotionEvent.ACTION_UP){
				//                                      //通知框架跳转
				//                                      Intent tmpIntent = new Intent(
				//                                              MainpageActivity.START_ACTIVITY);
				//                                      Bundle bundleToSend = new Bundle();
				//                                      bundleToSend.putString("act", THIS_ACT);
				//                                      bundleToSend.putString("startType","allwaysCreate");
				//                                      //Logger.i(TAG,"blockName:"+blockNames[mTabHost.getCurrentTab()]);
				//                                      bundleToSend.putString("actTabName", ((TextView)arg0).getText().toString());
				//                                      Logger.i(TAG,((TextView)arg0).getText());
				//                                      //bundleToSend.putString("switchInTab", "1");//在tab中通过操作控件发生界面跳转
				//                                      tmpIntent.putExtras(bundleToSend);
				//                                      sendBroadcast(tmpIntent);
				//                                      tmpIntent = null;
				//                                      bundleToSend = null;
				//                                      //////////////////////////
				//                                  }
				//                                  return true;
				//                              }
				//                              
				//                            });

/*2011-6-23暂注释
 * 				tv.setOnClickListener(new RelativeLayout.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub   

						//修改刷新模式
						//EPDRefresh.refreshDUDither();

						final Intent intent = getIntent();
						if (intent != null) {
							final Bundle b = intent.getExtras();
							if (b != null) {
								b.putString("actTabName",blockNames[curTabIndex]);
								intent.putExtras(b);
							}
						}

						//切换TAB时自动提示信息  （废弃）

						                        final TextView tv = (TextView) v;
                                    // TODO: MODIFIED KIZAN sendBroadcast call is asynchronous
                                    // why need another Runnable ?
                                    // comment these runnable code 
//                                    final Runnable showTip = new Runnable() {
//                                        @Override
//                                        public void run() {
                                            final Intent tmpIntent = new Intent(
                                                    MainpageActivity.SHOW_TIP);
                                            final Bundle sndBundle = new Bundle();
                                            sndBundle.putString("pviapfStatusTip", getResources()
                                                    .getString(R.string.entering)
                                                    + tv.getText() + "...");
                                            sndBundle.putString("pviapfStatusTipTime",
                                                    "2000");
                                            tmpIntent.putExtras(sndBundle);
                                            sendBroadcast(tmpIntent);
//                                        }
//                                    };
//                                    new Thread() {
//                                        public void run() {
//                                            mHandler.post(showTip);
//                                        }
//                                    }.start();
						 
						final RelativeLayout rl = ((RelativeLayout) v
								.getParent());
						final Runnable rlClick = new Runnable() {
							@Override
							public void run() {
								rl.performClick();
							}
						};
						new Thread() {
							public void run() {
								mHandler.post(rlClick);
							}
						}.start();
					}
				});*/

			}


		}};


		private void loadTab(){
		    //Logger.d(TAG,"loadTab(...");

			//只在blockName是null的时候需要初始化tab
			if(blockNames==null){			    
	            //getStoreBlockList();
	            mTabHost = getTabHost();
                if(mTabHost.getTabWidget()!=null && mTabHost.getTabWidget().getTabCount()!=6){
                    addTab.run();
                    //Logger.d(TAG,"build tabs");
                }
			}
			
			//移到UI线程去执行这些代码
			mHandler.post(resumeTab);
		}

		
		/**
		 * 恢复tab
		 */
    private Runnable resumeTab = new Runnable() {

        @Override
        public void run() {
            //Logger.d(TAG, "resume tab");
            mTabHost = getTabHost();

            String actTabName = blockNames[0];

            final Intent it = getIntent();
            if (it != null) {
                Bundle bd = it.getExtras();
                if (bd != null) {

                    actTabName = bd.getString("actTabName");
                    if (actTabName == null) {
                        actTabName = blockNames[0];
                    }

                } else {
                    //Logger.i(TAG, "getExtras() is null");
                }
            } else {
                //Logger.i(TAG, "getIntent() is null");
            }

            if (actTabName == null) {
                actTabName = blockNames[0];
            }

            //Logger.d(TAG, "actTabName:" + actTabName);

            try {
                final int curTabIndex = getBlockIndex(actTabName);
                //Logger.d(TAG, "curTabIndex:" + curTabIndex);
                mTabHost.setCurrentTab(curTabIndex);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }};
			
			
/**
 * 从网络读取无线书城主栏目数据 
 * @return
 *//*
    private int getStoreBlockList() {
        //Logger.d(TAG, "getStoreBlockList(...");
        // TODO Auto-generated method stub
        //String strGBL = SubscribeProcess.network("getBlockList", "1", null,            null, null);
        final String strGBL = this.getBlockListXml();        

        if (strGBL != null && strGBL.length() > 9
                && strGBL.substring(0, 10).contains("Exception")) {
            //Logger.d(TAG, "1 getBlocklist Network error");
            return 1;
        }
        if (strGBL != null && strGBL.length() > 18
                && strGBL.substring(0, 19).contains("0000")) {
            //Logger.d(TAG, "get right xml from net");

            InputStream is = new ByteArrayInputStream(strGBL.substring(20)
                    .getBytes());
            Element rootele = null;
            try {
                DocumentBuilderFactory dbfactory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db = dbfactory.newDocumentBuilder();
                Document dom = db.parse(is);
                rootele = dom.getDocumentElement();
                NodeList nl1 = rootele.getElementsByTagName("BlockInfo");

                final GlobalVar appstate = ((GlobalVar) getApplicationContext());
                for (int i = 0; i < nl1.getLength(); i++) {
                    Element entry = (Element) nl1.item(i);
                    try {
                        Element eblockposition = (Element) entry
                                .getElementsByTagName("blockPosition").item(0);
                        String index = eblockposition.getFirstChild()
                                .getNodeValue();
                        int indexi = Integer.parseInt(index) - 1;
                        appstate.setblockPosition(indexi, eblockposition
                                .getFirstChild().getNodeValue());
                        Element eTitle = (Element) entry.getElementsByTagName(
                                "blockName").item(0);
                        String title = eTitle.getFirstChild().getNodeValue();
                        appstate.setblockName(indexi, title);
                        Element eblockid = (Element) entry
                                .getElementsByTagName("blockID").item(0);
                        appstate.setblockId(indexi, eblockid.getFirstChild()
                                .getNodeValue());
                        Element eblocktype = (Element) entry
                                .getElementsByTagName("blockType").item(0);
                        appstate.setblockType(indexi, eblocktype
                                .getFirstChild().getNodeValue());

                    } catch (Exception e) {

                        Element eTitle = (Element) entry.getElementsByTagName(
                                "blockName").item(0);
                        String title = eTitle.getFirstChild().getNodeValue();
                        appstate.setblockName(i, title);
                        Element eblockid = (Element) entry
                                .getElementsByTagName("blockID").item(0);
                        appstate.setblockId(i, eblockid.getFirstChild()
                                .getNodeValue());
                        Element eblocktype = (Element) entry
                                .getElementsByTagName("blockType").item(0);
                        appstate.setblockType(i, eblocktype.getFirstChild()
                                .getNodeValue());
                        Element eblockposition = (Element) entry
                                .getElementsByTagName("blockPosition").item(0);
                        appstate.setblockPosition(i, eblockposition
                                .getFirstChild().getNodeValue());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("error", "2 get wiress store Blocklist error");
                return 2;
            }

        } else {
            Logger.e("error", "3 get wiress store Blocklist error");
            return 3;
        }
        
        return 0;
    }
    */

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        setIntent(intent);
        super.onNewIntent(intent);
    } 
}

