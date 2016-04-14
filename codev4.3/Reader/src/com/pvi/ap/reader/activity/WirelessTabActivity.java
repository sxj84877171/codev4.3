/**
   * 无线书城TAB页
 * @author rd029 晏子凯
 * 
 */

package com.pvi.ap.reader.activity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiUtil;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.activity.pviappframe.PviDataList.OnRowClickListener;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.common.data_btn;
import com.pvi.ap.reader.data.common.data_pool;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * 无线书城里面的各个TAB列表
 * @author rd040 马中庆
 *
 */
//author:kyle
public class WirelessTabActivity extends PviActivity implements Pageable {
    private static final String TAG = "WirelessTabActivity";
    private Context mContext = WirelessTabActivity.this;

	// 广播Action常量定义
	public final static String START_ACTIVITY = MainpageActivity.START_ACTIVITY;// 在主要区域启动一个Activity（是否每次都OnCreate？）
	public final static String SET_TITLE = MainpageActivity.SET_TITLE;// 设置标题栏文字
	public final static String BACK = MainpageActivity.BACK;// 返回上一个子Activity

	private int currentPage = 1;
	final private int itemPerPage = 7;
	private int totalPage = 0;

	// private Handler newsHandler;

	private Handler retryHandler;
	private Handler setImageHandler;
	private Handler getimageHandler;

	private String blockid = null;
	private String blocktype = null;
	private String[] canSubscribe = new String[itemPerPage];
	private data_pool dataPool = new data_pool();
	private data_btn curDataBtn = new data_btn();

	private String tab = "empty";
	private Thread updateImage;

	private boolean reflash = false;
	public static final String REFLASH = "WirelessList";
	private int pageCounter = 0;
	private int flashPages = 5;
	
	private ArrayList<PviUiItem[]> list = new ArrayList<PviUiItem[]>();
	private PviDataList listView;

	
	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){


        @Override
        public void onUiItemClick(PviUiItem item) {

            String vTag = item.id;
            if (vTag.equals("reflash")) { //
                closePopmenu();
                //Logger.d("Click", "clicl reflash");
                reflash = true;
                currentPage = 1;
                getPageData();
                
            }else if (vTag.equals("orderbook")) {
                closePopmenu();
                final Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
                final Bundle sndBundle1 = new Bundle();
                sndBundle1.putString("act",
                "com.pvi.ap.reader.activity.SubscribeProcess");
                sndBundle1.putString("subscribeMode", "feedback");
                intent1.putExtras(sndBundle1);
                sendBroadcast(intent1);
            }
        
        }};
	
	private Handler blockTypeHandler;

	private String showName;

	public final static String BLOCK_TYPE_CATALOG_LIST = "1";
	public final static String BLOCK_TYPE_CONTENT_LIST = "2";

	public final static String BLOCK_TYPE_AUTHORS_LIST = "4";
	public final static String BLOCK_TYPE_BOOK_INFO_LIST = "5";

	public final static int BLOCK_TYPE_HANDLER_CATALOG_LIST = 0x01;
	public final static int BLOCK_TYPE_HANDLER_CONTENT_LIST = 0x02;

	public final static int BLOCK_TYPE_HANDLER_AUTHOUS_LIST = 0x04;
	public final static int BLOCK_TYPE_HANDLER_BOOK_INFO_LIST = 0x05;

    

	private GlobalVar appState;

	protected boolean onGoing = false;

	@Override
	public OnUiItemClickListener getMenuclick() {
		return this.menuclick;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
	    
	    //Logger.d(TAG,"onCreate() ");
	    
		appState = ((GlobalVar) getApplicationContext());

		this.setContentView(R.layout.wirelesstab);

		// the blocks' type handler
		// there are five kinds of block list type
		// a : catalog list
		// b : content list
		// c : rank list
		// d : author list
		// e : book info list
		//this.showPager = true;
		listView= (PviDataList)findViewById(R.id.list);
		
		
		blockTypeHandler = new Handler() {
			public void handleMessage(Message msg) {

               
                list.clear();
                
				int tmpi;
				for (tmpi = 0; tmpi < itemPerPage
						&& curDataBtn.dataPrimary[tmpi] != null; tmpi++) {
					final int i = tmpi;
					switch (msg.what) {

					case BLOCK_TYPE_HANDLER_CATALOG_LIST:
						handCatalogList(i);
						break;
					case BLOCK_TYPE_HANDLER_CONTENT_LIST:
						handContentList(i);
						getimageHandler.sendEmptyMessage(0);
						break;

					case BLOCK_TYPE_HANDLER_AUTHOUS_LIST:
						handAuthorsList(i);
						break;
					case BLOCK_TYPE_HANDLER_BOOK_INFO_LIST:
						handInfoList(i);
						break;
					}


				}
				
                //如果是0条，则弹出提示“暂无数据”
				if(tmpi==0){
				    showTip("暂无数据，请重试",2000);
				    return;
				}
				
				//一页数据已填充完毕
				listView.setData(list);
				
				switch (msg.what) {

                case BLOCK_TYPE_HANDLER_CATALOG_LIST:
                    if ("2".equals(blockid)) {
                        listView.setOnRowClick(new OnRowClickListener() {

                            @Override
                            public void OnRowClick(View v, int rowIndex) {
                              //0本的情况，提示
                                if(curDataBtn!=null
                                        &&curDataBtn.dataSlave[rowIndex]!=null
                                        &&curDataBtn.dataSlave[rowIndex].equals("0"+getResources().getString(R.string.kyleHint14))){
                                    showTip("该栏目没有书籍。",3000);
                                    return;
                                }else{
                                    handCatalogListClick(rowIndex);
                                }
                            }
                        });
                    }

                    else if ("3".equals(blockid) ) {
                        listView.setOnRowClick(new OnRowClickListener() {

                            @Override
                            public void OnRowClick(View v, int rowIndex) {
                              //0本的情况，提示
                                if(curDataBtn!=null
                                        &&curDataBtn.dataSlave[rowIndex]!=null
                                        &&curDataBtn.dataSlave[rowIndex].equals("0"+getResources().getString(R.string.kyleHint14))){
                                    showTip("该栏目没有书籍。",3000);
                                    return;
                                }else{
                                    handBookPackageInfoListClick(rowIndex,"3");
                                }
                            }
                        });
                    }
                    
                    else if ("4".equals(blockid) ) {
                        listView.setOnRowClick(new OnRowClickListener() {

                            @Override
                            public void OnRowClick(View v, int rowIndex) {
                              //0本的情况，提示
                                if(curDataBtn!=null
                                        &&curDataBtn.dataSlave[rowIndex]!=null
                                        &&curDataBtn.dataSlave[rowIndex].equals("0"+getResources().getString(R.string.kyleHint14))){
                                    showTip("该栏目没有书籍。",3000);
                                    return;
                                }else{
                                    handBookPackageInfoListClick(rowIndex,blockid);
                                }
                            }
                        });
                    }
                    break;
                case BLOCK_TYPE_HANDLER_CONTENT_LIST:
                    listView.setOnRowClick(new OnRowClickListener() {

                        @Override
                        public void OnRowClick(View v, int rowIndex) {
                            handContentListClick(rowIndex);
                        }
                    });
                    break;

                case BLOCK_TYPE_HANDLER_AUTHOUS_LIST:
                    listView.setOnRowClick(new OnRowClickListener() {

                        @Override
                        public void OnRowClick(View v, int rowIndex) {
                            handAuthorsListTouchOrClick(rowIndex);
                        }
                    });
                    break;
                case BLOCK_TYPE_HANDLER_BOOK_INFO_LIST:
                    listView.setOnRowClick(new OnRowClickListener() {

                        @Override
                        public void OnRowClick(View v, int rowIndex) {
                            handInfoTouchOrClick(rowIndex);
                        }
                    });
                    break;
                }

				updatePagerinfo(String.valueOf(currentPage)+" / "+String.valueOf(totalPage));

				if (totalPage < 1) {
					hidePager();
				}else{
				    showPager();
				}
				onGoing = false;
				
				//在这里刷新一下全屏 
		        if(deviceType==1){

		            if( pageCounter>0 && pageCounter%flashPages==0 ){
		                Logger.d(TAG,"GC16 FULL");
//                        getWindow().getDecorView().getRootView()
//                                .postInvalidate(
//                                        View.EINK_WAIT_MODE_WAIT
//                                                | View.EINK_WAVEFORM_MODE_GC16
//                                                | View.EINK_UPDATE_MODE_FULL);
		            }
		        }
		        pageCounter++;

				showMe();


			}

		};

		retryHandler = new Handler() {
			public void handleMessage(Message msg) {

				final PviAlertDialog pd = new PviAlertDialog(getParent());
				pd.setTitle(R.string.kyleHint02);
				pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								getPageData();
							}

						});
				pd.setButton(DialogInterface.BUTTON_NEUTRAL, "取消",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								
							}

						});
				pd.show();

			};
		};

		setImageHandler = new Handler() {
			public void handleMessage(Message msg) {

				for (int i = 0; i < itemPerPage && i<list.size(); i++) {
					if (curDataBtn.dataBitmap[i] != null) {
					    PviUiItem[] items = list.get(i);
						items[0].pic = curDataBtn.dataBitmap[i];
					}
				}
				listView.setData(list);
			};
		};

		getimageHandler = new Handler() {
			public void handleMessage(Message msg) {
				updateImage = new Thread() {
					public void run() {
						Looper.prepare();
						int tmpPage = currentPage;
						for (int i = 0; i < itemPerPage&&i<list.size(); i++) {
							if (curDataBtn.dataPrimary[i] != null
							        && curDataBtn.dataPrimaryBtn[i]!=null
							        && !"".equals(curDataBtn.dataPrimaryBtn[i])							        
									&& !curDataBtn.bitmapOK[i]) {
								curDataBtn.dataBitmap[i] = getCoverImage(curDataBtn.dataPrimaryBtn[i]);
								if (curDataBtn.dataBitmap[i] != null) {
									dataPool.insert_image(
											curDataBtn.dataBitmap[i], tmpPage,
											i);
								}
							}
						}
						setImageHandler.sendEmptyMessage(0);
					}
				};
				updateImage.start();
			};
		};

		super.onCreate(icicle);
		

		registerWirelessSearch();


	}// end of onCreate()

    private void getPageData() {
        //Logger.d(TAG,"getPageData()");
        
        onGoing = true;

        Intent revIntent = getIntent();

        if (revIntent != null) {

            Bundle revBundle = revIntent.getExtras();
            if (revBundle != null) {
                //Logger.d(TAG,revBundle);

                tab = revBundle.getString("tab");
                blockid = revBundle.getString("blockid");
                blocktype = revBundle.getString("blocktype");
                showName = revBundle.getString("blockName");

                // style catalog
                if (BLOCK_TYPE_CATALOG_LIST.equals(blocktype)) {
                    // if (index > 0) {
                    // curDataBtn = dataPool.getbyindex(index);
                    // blockTypeHandler
                    // .sendEmptyMessage(BLOCK_TYPE_HANDLER_CATALOG_LIST);
                    // } else {
                    Thread checkUpdate = new Thread() {
                        public void run() {
                            Looper.prepare();
                            if ((getDataTypeList()) == 0) {
                                blockTypeHandler
                                        .sendEmptyMessage(BLOCK_TYPE_HANDLER_CATALOG_LIST);

                            } else {

                                retryHandler.sendEmptyMessage(0);
                            }
                            // Looper.loop();
                            reflash = false;
                            hideTip();
                        }
                    };
                    checkUpdate.start();
                    // }
                }
                // style book
                else if (BLOCK_TYPE_CONTENT_LIST.equals(blocktype)) {
                    // if (index > 0) {
                    // curDataBtn = dataPool.getbyindex(index);
                    // blockTypeHandler
                    // .sendEmptyMessage(BLOCK_TYPE_HANDLER_CONTENT_LIST);
                    // } else {

                    Thread checkUpdate = new Thread() {
                        public void run() {
                            if ((getDataTypeBook()) == 0) {
                                blockTypeHandler
                                        .sendEmptyMessage(BLOCK_TYPE_HANDLER_CONTENT_LIST);

                            } else {

                                retryHandler.sendEmptyMessage(0);
                            }
                            reflash = false;
                            hideTip();
                        }
                    };
                    checkUpdate.start();
                    // }
                }
                // style news
                else if (BLOCK_TYPE_BOOK_INFO_LIST.equals(blocktype)) {

                    Thread checkUpdate = new Thread() {
                        public void run() {
                            if ((getDataTypeNews()) == 0) {
                                blockTypeHandler
                                        .sendEmptyMessage(BLOCK_TYPE_HANDLER_BOOK_INFO_LIST);

                            } else {

                                retryHandler.sendEmptyMessage(0);
                            }
                            reflash = false;
                            hideTip();
                        }
                    };
                    checkUpdate.start();
                }

                // style author
                else if (BLOCK_TYPE_AUTHORS_LIST.equals(blocktype)) {

                    Thread checkUpdate = new Thread() {
                        public void run() {
                            if ((getDataTypeAuthor()) == 0) {
                                blockTypeHandler
                                        .sendEmptyMessage(BLOCK_TYPE_HANDLER_AUTHOUS_LIST);

                            } else {

                                retryHandler.sendEmptyMessage(0);
                            }
                            reflash = false;
                            hideTip();
                        }
                    };
                    checkUpdate.start();
                } else {
                    Logger.e(TAG, "blocktype:" + blocktype);
                }
            }
        }

        
    }

	private void handInfoTouchOrClick(int i) {
		// if (ThemeNum == 2) {
		// primaryBtn[i]
		// .setBackgroundResource(R.drawable.list_pressed_ui2);
		// primary[i].setTextColor(android.graphics.Color.WHITE);
		// slave[i].setTextColor(android.graphics.Color.WHITE);
		// }
		final Bundle bundleToSend = new Bundle();
		bundleToSend.putString("bookNewsID", curDataBtn.dataId[i]);
		final Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
		bundleToSend.putString("act",
				"com.pvi.ap.reader.activity.InfoContentActivity");
		bundleToSend.putString("pviapfStatusTip", getResources().getString(
				R.string.kyleHint01));
		bundleToSend.putString("haveTitleBar", "1");
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
	}

    private void handInfoList(final int i) {
        PviUiItem[] items = new PviUiItem[] {
                new PviUiItem("icon" + i, R.drawable.defaultinfo_small, 10, 10,
                        50, 50, null, false, true, null),
                new PviUiItem("text1" + i, 0, 90, 30, 250, 50, "我是一列文本",
                        false, true, null),
                new PviUiItem("text2" + i, 0, 540, 30, 200, 50, "我是又一列文本",
                        false, true, null), };

        items[1].text = curDataBtn.dataPrimary[i];
        items[2].text = curDataBtn.dataSlave[i];
        items[2].textType = PviUiItem.COMMON;
        items[2].textAlign = 2;

        list.add(items);
    }

	private void handCatalogListClick(int i) {
		final String tmpCataInfo = curDataBtn.dataId[i];
		// if (ThemeNum == 2) {
		// primaryBtn[i]
		// .setBackgroundResource(R.drawable.list_pressed_ui2);
		// primary[i].setTextColor(android.graphics.Color.WHITE);
		// slave[i].setTextColor(android.graphics.Color.WHITE);
		// }
		Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("act",
				"com.pvi.ap.reader.activity.CatalogHomepageActivity");
		bundleToSend.putString("pviapfStatusTip", getResources().getString(
				R.string.kyleHint01));
		bundleToSend.putString("haveTitleBar", "1");
		bundleToSend.putString("catalogId", tmpCataInfo);
		bundleToSend.putString("startType", "allwaysCreate");
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
	}

	private void handBookPackageInfoListClick(int i, String blockid2) {
		final String tmpStr1 = curDataBtn.dataId[i];
		final String tmpStr2 = curDataBtn.dataPrimary[i];
		// if (ThemeNum == 2) {
		// primaryBtn[i]
		// .setBackgroundResource(R.drawable.list_pressed_ui2);
		// primary[i].setTextColor(android.graphics.Color.WHITE);
		// slave[i].setTextColor(android.graphics.Color.WHITE);
		// }
		Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("act",
				"com.pvi.ap.reader.activity.BookPackageInfoActivity");
		bundleToSend.putString("startType", "allwaysCreate");
		bundleToSend.putString("catalogID", tmpStr1);
		bundleToSend.putString("catalogName", tmpStr2);
		bundleToSend.putString("canSubscribe", canSubscribe[i]);
		bundleToSend.putString("pviapfStatusTip", getResources().getString(
				R.string.kyleHint01));
		bundleToSend.putString("haveTitleBar", "1");
		bundleToSend.putString("blockid", blockid2);
		if ("3".equals(blockid2)) {
			bundleToSend.putString("mainTitle", tmpStr2 );
		}else{
			bundleToSend.putString("mainTitle", tmpStr2);
		}
		
		tmpIntent.putExtras(bundleToSend);
		
		
		sendBroadcast(tmpIntent);
	}

	private void handCatalogList(final int i) {
	    
	    PviUiItem[] items = new PviUiItem[]{
                new PviUiItem("icon"+i, R.drawable.defaultcatalog_small_1, 10, 10, 50, 50, null, false, true, null),
                new PviUiItem("text1"+i, 0, 90, 30, 250, 50, "我是一列文本", false, true, null),
                new PviUiItem("text2"+i, 0, 540, 30, 200, 50, "我是又一列文本", false, true, null),
        };
	    

		items[1].text=curDataBtn.dataPrimary[i];
		items[2].text=curDataBtn.dataSlave[i];
		items[2].textType = PviUiItem.COMMON;
		items[2].textAlign =2;
		
		if ("2".equals(blockid)) {

			items[0].res = R.drawable.defaultcatalog_small_1;			

		}

		else if ("3".equals(blockid) ) {
			
		    items[0].res = R.drawable.defaultbookpack_small;

		}
		
		else if ("4".equals(blockid) ) {
			
		    items[0].res = R.drawable.defaultbookpack_small;

		}
		
		
		list.add(items);
	}

	private void handContentListClick(int i) {
		// if (ThemeNum == 2) {
		// primaryBtn[i]
		// .setBackgroundResource(R.drawable.list_pressed_ui2);
		// primary[i].setTextColor(android.graphics.Color.WHITE);
		// slave[i].setTextColor(android.graphics.Color.WHITE);
		// }
		Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("act",
				"com.pvi.ap.reader.activity.BookSummaryActivity");
		bundleToSend.putString("startType", "allwaysCreate");
		bundleToSend.putString("haveTitleBar", "1");
		bundleToSend.putString("contentID", curDataBtn.dataId[i]);
		bundleToSend.putString("pviapfStatusTip", getResources().getString(
				R.string.kyleHint01));
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
	}

	private void handContentList(final int i) {

	      PviUiItem[] items = new PviUiItem[]{
	                new PviUiItem("icon"+i, R.drawable.bookcover_5472_ui1, 10, 10, 54, 72, null, false, true, null),
	                new PviUiItem("text1"+i, 0, 90, 30, 250, 50, "我是一列文本", false, true, null),
	                new PviUiItem("text2"+i, 0, 540, 30, 200, 50, "我是又一列文本", false, true, null),
	        };
	        
	        items[0].bgNormal = R.drawable.img_border_normal_ui1;
	        items[0].bgFocus = R.drawable.img_border_clicked_ui1;
	        items[1].text=curDataBtn.dataPrimary[i];
	        items[2].text=curDataBtn.dataSlave[i];
	        items[2].textType = PviUiItem.COMMON;
	        items[2].textAlign = 2;

		if (curDataBtn.dataBitmap[i] != null) {
			items[0].pic = curDataBtn.dataBitmap[i];
		} 

		list.add(items);
	}

	private void handAuthorsListTouchOrClick(int i) {
		// if (ThemeNum == 2) {
		// primaryBtn[i]
		// .setBackgroundResource(R.drawable.list_pressed_ui2);
		// primary[i].setTextColor(android.graphics.Color.WHITE);
		// slave[i].setTextColor(android.graphics.Color.WHITE);
		// }
		Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("act",
				"com.pvi.ap.reader.activity.WriterActivity");
		bundleToSend.putString("authorID", curDataBtn.dataId[i]);
		bundleToSend.putString("pviapfStatusTip", getResources().getString(
				R.string.kyleHint01));
		bundleToSend.putString("haveTitleBar", "1");
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
	}

    private void handAuthorsList(final int i) {

        PviUiItem[] items = new PviUiItem[] {
                new PviUiItem("icon" + i, R.drawable.defaultauthor_small, 10,
                        10, 50, 50, null, false, true, null),
                new PviUiItem("text1" + i, 0, 90, 30, 200, 50, "我是一列文本",
                        false, true, null),
                new PviUiItem("text2" + i, 0, 540, 30, 200, 50, "我是又一列文本",
                        false, true, null), };

        items[1].text = curDataBtn.dataPrimary[i];
        items[2].text = "《" + curDataBtn.dataSlave[i] + "》";
        items[2].textType = PviUiItem.COMMON;
        items[2].textAlign = 2;

        list.add(items);

    }

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {	

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 通知框架返回上一个子activty
			Bundle tmpBundle = new Bundle();
			tmpBundle.putString("retry", "false");
			Intent tmpIntent = new Intent(BACK);
			tmpIntent.putExtras(tmpBundle);
			sendBroadcast(tmpIntent);
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}



	@Override
	protected void onResume() {
		long TimeStart = System.currentTimeMillis();
		Logger.i(TAG, "onResume:" + Long.toString(TimeStart));
		getPageData();
		super.onResume();

	}

	private int getDataTypeList() {
	    //Logger.d(TAG,"getDataTypeList()");

		String start = String.valueOf((currentPage - 1) * itemPerPage + 1);
		String count = String.valueOf(itemPerPage);
		String strGBC;

		if (reflash) {
		    showNetWorkProcessing2();
			strGBC = SubscribeProcess.network("getBlockContent", blockid,
					start, count, REFLASH);
			reflash = false;
		} else {
		    showNetWorkProcessing();
			strGBC = SubscribeProcess.network("getBlockContent", blockid,
					start, count, null);
		}
		
		//Logger.d(TAG,"getDataTypeList() :"+strGBC);

		if (strGBC.substring(0, 10).contains("Exception")) {
			return 1;
		}

		try {
			InputStream is = new ByteArrayInputStream(strGBC.substring(20)
					.getBytes());
			Element rootele = null;
			// try {
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder db = dbfactory.newDocumentBuilder();
			Document dom = db.parse(is);
			rootele = dom.getDocumentElement();
			NodeList totalNl = rootele.getElementsByTagName("totalRecordCount");
			int totalRecordCount = Integer.parseInt(totalNl.item(0)
					.getFirstChild().getNodeValue());
			totalPage = totalRecordCount / itemPerPage;
			if (totalRecordCount % itemPerPage != 0) {
				totalPage++;
			}

			NodeList nl0 = rootele.getElementsByTagName("contentID");
			for (int i = 0; i < nl0.getLength(); i++) {
				curDataBtn.dataId[i] = nl0.item(i).getFirstChild()
						.getNodeValue();
			}
			NodeList nl1 = rootele.getElementsByTagName("ContentInfo");
			// loop item:content item
			for (int i = 0; i < itemPerPage; i++) {
				Element entry = (Element) nl1.item(i);
				if (entry != null) {
					NodeList nl2 = entry.getElementsByTagName("ContentParam");
					int nl2Count = nl2.getLength();
					// loop item:param
					for (int j = 0; j < nl2Count; j++) {
						Element eparam1 = (Element) nl2.item(j);
						NodeList nl3 = eparam1.getElementsByTagName("name");
						String name = nl3.item(0).getFirstChild()
								.getNodeValue();
						if (name.equals("catalogName")) {
							NodeList nl4 = eparam1
									.getElementsByTagName("value");
							if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
                                    .getFirstChild()!=null) {
							curDataBtn.dataPrimary[i] = nl4.item(0)
									.getFirstChild().getNodeValue();
							}else{
							    curDataBtn.dataPrimary[i] = "";
							}
						}
						if (name.equals("bookNumber")) {
							NodeList nl4 = eparam1
									.getElementsByTagName("value");
							if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
                                    .getFirstChild()!=null) {
							curDataBtn.dataSlave[i] = nl4.item(0)
									.getFirstChild().getNodeValue()
									+ getResources().getString(
											R.string.kyleHint14);
							}else{
							    curDataBtn.dataSlave[i] = "";
							}
						}
						if (name.equals("catalogType")) {
							NodeList nl4 = eparam1
									.getElementsByTagName("value");
							if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
                                    .getFirstChild()!=null) {
							String tmp = nl4.item(0).getFirstChild()
									.getNodeValue();
							if (tmp.equals("3") || tmp.equals("4")
									|| tmp.equals("5")) {
								canSubscribe[i] = "true";
							} else {
								canSubscribe[i] = "false";
							}
							}else{
							    canSubscribe[i] = "false";
							}
						}
					}// end of param loop
				}
			}// end of content item loop
			dataPool.insert_data(curDataBtn, currentPage);

		} catch (Exception e) {
		    e.printStackTrace();
			return 2;
		}
		return 0;
	}

    private void showMe() {

        if (!isShown()) {
            //Logger.d(TAG, "send 'show me'");
            //sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));

            final Intent tmpIntent = new Intent(MainpageActivity.SHOW_ME);
            final Bundle bundleToSend = new Bundle();
            bundleToSend.putString("act","com.pvi.ap.reader.activity.WirelessStoreActivity");// TabActivity的类名
            bundleToSend.putString("actTabName", showName);
            bundleToSend.putString("sender", this.getClass().getName()); // TAB内嵌activity类的全名
            tmpIntent.putExtras(bundleToSend);
            sendBroadcast(tmpIntent);

        }else{
            //Logger.d(TAG, "noneed to send 'show me'");
        }
    }

    /**
     * 取 编辑推荐
     * @return
     */
	private int getDataTypeBook() {
	    //Logger.d(TAG,"getDataTypeBook() ");
	    
		String start = String.valueOf((currentPage - 1) * itemPerPage + 1);
		String count = String.valueOf(itemPerPage);

		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
        HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
        HashMap responseMap = null;
        
        //Logger.d(TAG,"参数：blockid="+blockid+",start="+start+",count="+count);
        
        ahmNamePair.put("blockId", blockid);
        ahmNamePair.put("start", start);
        ahmNamePair.put("count", count);
        


		if (reflash) {
		    //Logger.d(TAG,"一键更新");
		    showNetWorkProcessing2();
		    ahmNamePair.put("reflash", REFLASH);
			reflash = false;
		} else {
		    //Logger.d(TAG,"普通数据获取");
		    showNetWorkProcessing();
		}
		
        try {
            responseMap = NetCache.getBlockContent(ahmHeaderMap,
                    ahmNamePair);
        } catch (HttpException e) {
            e.printStackTrace();
            return 1;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
		


        try {
            //Logger.d(TAG, new String((byte[]) responseMap.get("ResponseBody")));

            InputStream is = new ByteArrayInputStream((byte[]) responseMap
                    .get("ResponseBody"));
            Element rootele = null;
            // try {
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder db = dbfactory.newDocumentBuilder();
            Document dom = db.parse(is);
            rootele = dom.getDocumentElement();
            NodeList totalNl = rootele.getElementsByTagName("totalRecordCount");
            final int totalRecordCount = Integer.parseInt(totalNl.item(0)
                    .getFirstChild().getNodeValue());

            // Logger.d(TAG,"总条数："+totalRecordCount);

            totalPage = totalRecordCount / itemPerPage;
            if (totalRecordCount % itemPerPage != 0) {
                totalPage++;
            }

            NodeList nl = rootele.getElementsByTagName("ContentInfo");
            // loop item:content item
            for (int i = 0; i < nl.getLength(); i++) {
                Element entry = (Element) nl.item(i);
                NodeList nl2 = entry.getElementsByTagName("ContentParam");
                // loop item:param
                for (int j = 0; j < nl2.getLength(); j++) {
                    Element eparam1 = (Element) nl2.item(j);
                    final NodeList nl3 = eparam1.getElementsByTagName("name");

                    if (nl3 != null && nl3.item(0) != null
                            && nl3.item(0).getFirstChild() != null) {

                        String name = nl3.item(0).getFirstChild()
                                .getNodeValue();
                        if ("contentName".equals(name)) {
                            NodeList nl4 = eparam1
                                    .getElementsByTagName("value");
                            if (nl4 != null && nl4.item(0) != null
                                    && nl4.item(0).getFirstChild() != null) {
                                curDataBtn.dataPrimary[i] = nl4.item(0)
                                        .getFirstChild().getNodeValue();
                            }else{
                                curDataBtn.dataPrimary[i] = "无";
                            }
                        }
                        if ("authorName".equals(name)) {
                            NodeList nl4 = eparam1
                                    .getElementsByTagName("value");
                            if (nl4 != null && nl4.item(0) != null
                                    && nl4.item(0).getFirstChild() != null) {
                                curDataBtn.dataSlave[i] = getResources()
                                        .getString(R.string.kyleHint15)
                                        + nl4.item(0).getFirstChild()
                                                .getNodeValue();
                            }else{
                                curDataBtn.dataSlave[i] = "无";
                            }
                        }
                        if ("contentID".equals(name)) {
                            NodeList nl4 = eparam1
                                    .getElementsByTagName("value");
                            if (nl4 != null && nl4.item(0) != null
                                    && nl4.item(0).getFirstChild() != null) {
                                final String tmpStr = nl4.item(0)
                                        .getFirstChild().getNodeValue();
                                curDataBtn.dataId[i] = tmpStr;
                            }else{
                                curDataBtn.dataId[i] = "";
                            }

                        }

                        if ("smallLogo".equals(name)) {
                            try {
                                NodeList nl4 = eparam1
                                        .getElementsByTagName("value");
                                final String tmpStr = nl4.item(0)
                                        .getFirstChild().getNodeValue();
                                curDataBtn.dataPrimaryBtn[i] = tmpStr;

                            } catch (Exception e) {
                                curDataBtn.dataPrimaryBtn[i] = null;
                            }
                        }
                    }
                }// end of param loop
            }// end of content item loop

            dataPool.insert_data(curDataBtn, currentPage);
        } catch (Exception e) {
		    e.printStackTrace();
			return 2;
		}

		return 0;
	}

	private int getDataTypeNews() {
	    //Logger.d(TAG,"getDataTypeNews() ");
	    
		String start = String.valueOf((currentPage - 1) * itemPerPage + 1);
		String count = String.valueOf(itemPerPage);

		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		ahmNamePair.put("catalogId", "1");
		ahmNamePair.put("start", start);
		ahmNamePair.put("count", count);

		if (reflash) {
		    showNetWorkProcessing2();
			ahmNamePair.put("reflash", REFLASH);
			reflash = false;
		}else{
		    showNetWorkProcessing();
		}

		HashMap responseMap = null;
		try {
			responseMap = NetCache.getBookNewsList(ahmHeaderMap, ahmNamePair);
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}

		// System.out.println("返回zhuangtai："+responseMap.get("result-code"));
		byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
		
		//Logger.d(TAG,"getDataTypeNews() :"+new String(responseBody));

		// 根据返回字节数组构造DOM
		Document dom = null;
		try {
			dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		}

		Element root = dom.getDocumentElement();

		NodeList totalNl = root.getElementsByTagName("totalRecordCount");
		int totalRecordCount = Integer.parseInt(totalNl.item(0).getFirstChild()
				.getNodeValue());
		totalPage = totalRecordCount / itemPerPage;
		if (totalRecordCount % itemPerPage != 0) {
			totalPage++;
		}

		NodeList n1 = root.getElementsByTagName("BookNews");
		for (int i = 0; i < itemPerPage; i++) {
			Element entry = (Element) n1.item(i);

			NodeList childrens = entry.getChildNodes();
			for (int j = 0; j < childrens.getLength(); j++) {
				Node node = childrens.item(j);
				if(node!=null){
				if ("title".equals(node.getNodeName())) {
				    if(node.getFirstChild()!=null){
				        curDataBtn.dataPrimary[i] = node.getFirstChild()
							.getNodeValue();
				    }else{
				        curDataBtn.dataPrimary[i] = "";
				    }
					
				}
				if ("publishTime".equals(node.getNodeName())) {
					if(node.getFirstChild()!=null&&node.getFirstChild().getNodeValue()!=null){
					    curDataBtn.dataSlave[i]=PviUiUtil.FormatTime(node.getFirstChild().getNodeValue(),"yyyy-MM-dd HH:mm:ss","MM/dd/yyyy HH:mm");
					}else{
					    curDataBtn.dataSlave[i]= "";
					}
				}
				if ("bookNewsID".equals(node.getNodeName())) {
				    if(node.getFirstChild()!=null){
				        curDataBtn.dataId[i] = node.getFirstChild().getNodeValue();
				    }else{
				        curDataBtn.dataId[i] = "";
				    }
				}
				}
			}

		}
		return 0;
	}

	private int getDataTypeAuthor() {
	    //Logger.d(TAG,"getDataTypeAuthor() ");
	    
		String start = String.valueOf((currentPage - 1) * itemPerPage + 1);
		String count = String.valueOf(itemPerPage);
		String strGBC;

		if (reflash) {
		    showNetWorkProcessing2();
			strGBC = SubscribeProcess.network("getBlockContent", blockid,
					start, count, REFLASH);
			reflash = false;
		} else {
		    showNetWorkProcessing();
			strGBC = SubscribeProcess.network("getBlockContent", blockid,
					start, count, null);
		}
		//Logger.d(TAG,"getDataTypeAuthor() :"+strGBC);

		if (strGBC.substring(0, 10).contains("Exception")) {
			return 1;
		}
		try {
			InputStream is = new ByteArrayInputStream(strGBC.substring(20)
					.getBytes());
			Element rootele = null;
			// try {
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder db = dbfactory.newDocumentBuilder();
			Document dom = db.parse(is);
			rootele = dom.getDocumentElement();
			NodeList totalNl = rootele.getElementsByTagName("totalRecordCount");
			int totalRecordCount = Integer.parseInt(totalNl.item(0)
					.getFirstChild().getNodeValue());
			totalPage = totalRecordCount / itemPerPage;
			if (totalRecordCount % itemPerPage != 0) {
				totalPage++;
			}

			NodeList nl = rootele.getElementsByTagName("ContentInfo");
			// loop item:content item
			for (int i = 0; i < nl.getLength(); i++) {
				Element entry = (Element) nl.item(i);
				NodeList forAuthorID = entry.getElementsByTagName("contentID");
				final String tmpStr = forAuthorID.item(0).getFirstChild()
						.getNodeValue();
				curDataBtn.dataId[i] = tmpStr;
				final int tmp = i;

				NodeList nl2 = entry.getElementsByTagName("ContentParam");
				// loop item:param
				for (int j = 0; j < nl2.getLength(); j++) {
					Element eparam1 = (Element) nl2.item(j);
					NodeList nl3 = eparam1.getElementsByTagName("name");
					String name = nl3.item(0).getFirstChild().getNodeValue();
					if (name.equals("contentName")) {
						NodeList nl4 = eparam1.getElementsByTagName("value");
						if(nl4.item(0)!=null&&nl4.item(0).getFirstChild()!=null){
						curDataBtn.dataSlave[i] = nl4.item(0).getFirstChild()
								.getNodeValue();
						}else{
						    curDataBtn.dataSlave[i] = "";
						}
					}
					if (name.equals("authorName")) {
						NodeList nl4 = eparam1.getElementsByTagName("value");
						if(nl4.item(0)!=null&&nl4.item(0).getFirstChild()!=null){
						curDataBtn.dataPrimary[i] = nl4.item(0).getFirstChild()
								.getNodeValue();
						}else{
						    curDataBtn.dataPrimary[i] = "";
						}
					}
				}// end of param loop

			}// end of content item loop
		} catch (Exception e) {
		    e.printStackTrace();
			return 2;
		}
		return 0;
	}

	public static Bitmap getCoverImage(String ImageUri) {
	    if(ImageUri==null||"".equals(ImageUri)){
	        Logger.e(TAG,"ImageUri is null or empty");
	        return null;
	    }

		Bitmap bitmap = null;
		try {
		    //Logger.d(LOG_TAG,"getCoverImage:"+Config.getString("CPC_BASE_URL")+ ImageUri);
			bitmap = NetCache.GetNetImage(Config.getString("CPC_BASE_URL")+ ImageUri);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

    @Override
    public void OnNextpage() {
        if (onGoing) {
            return;
        }
        curDataBtn = new data_btn();
        if (currentPage == totalPage) {
            return;
        }
        currentPage++;
        getPageData();
        super.OnNextpage();
    }

    @Override
    public void OnPrevpage() {
        if (onGoing) {
            return;
        }
        curDataBtn = new data_btn();
        if (currentPage == 1) {
            return;
        }
        currentPage--;
        getPageData();
        super.OnPrevpage();
    }

}