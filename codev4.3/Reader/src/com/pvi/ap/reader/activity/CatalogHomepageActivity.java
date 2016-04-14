/**
 * 分类栏目页
 * @author rd029 晏子凯
 * 
 */

package com.pvi.ap.reader.activity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.SelectSpinner;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.activity.pviappframe.PviDataList.OnRowClickListener;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.common.beans.RankBook;
import com.pvi.ap.reader.common.beans.RankType;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.common.data_btn;
import com.pvi.ap.reader.data.common.data_pool;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

//kyle


/**
 * 栏目首页
 */
public class CatalogHomepageActivity extends PviActivity implements Pageable {
	private static final String TAG = "CatalogHomepageActivity";
	private Context mContext = CatalogHomepageActivity.this;
	private GlobalVar appState;

	//	private boolean reflash = false;
	//	int ThemeNum = 0;
	//	private PviAlertDialog dialog;
	public final static String START_ACTIVITY = "com.pvi.ap.reader.mainframe.START_ACTIVITY";// 在主要区域启动一个Activity（是否每次都OnCreate？）
	public final static String SET_TITLE = "com.pvi.ap.reader.mainframe.SET_TITLE";// 设置标题栏文字
	public final static String BACK = "com.pvi.ap.reader.mainframe.BACK";// 返回上一个子Activity
	private int itemPerPage = 7;
	private Handler rankHandler;
	private boolean isshow = false;
	private TextView recommend;
	private TextView hot;
	private TextView total;

	private int pageCounter = 0;//翻页计数器
	private int currentPage = 1;
	private int totalPage_recommend = 0;
	private int totalPage_total = 0;
	private int totalPage_hot = 0;

	private boolean onGoing = false;	



	private SelectSpinner catalogSelection=null;
	private SelectSpinner rankTypeSelection=null;
	private SelectSpinner rankTimeSelection=null;

	private Button selectBtn=null;
	private String defaultRankTimeLable = "";
	private String defaultRankTypeLable = "";
	private String defaultCatalogIdlable = "";

	private String catalogId;

	private String currentTab = "recommend";

	Element eRecommend;
	Element eHot;
	Element eTotal;

	String recommendID;
	String hotID;
	String totalID;

	private boolean pagechange = false;

	private data_pool dataPool_recommend = new data_pool();
	private data_pool dataPool_total = new data_pool();

	private data_btn curDataBtn = new data_btn();

	private Handler retryHandler = new Handler();
	private Handler recommHandler = new Handler();
	private int error;
	private boolean titleOK = false;
	
	
	private ArrayList<PviUiItem[]> list = new ArrayList<PviUiItem[]>();
    private PviDataList listView;
	
    private LinearLayout titleLayout;


	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){
        @Override
        public void onUiItemClick(PviUiItem item) {

            closePopmenu();
            String vTag = item.id;
            if (vTag.equals("feedback")) {
                Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
                Bundle sndBundle1 = new Bundle();
                sndBundle1.putString("act",
                "com.pvi.ap.reader.activity.SubscribeProcess");
                sndBundle1.putString("subscribeMode", "feedback");
                intent1.putExtras(sndBundle1);
                sendBroadcast(intent1);
            }
            else if (vTag.equals("reflash"))
            {
                currentPage = 1;
                showMessage("一键更新中...");
                if(onGoing){
                    return ;
                }

                onGoing = true;
                if(currentTab.equals("recommend")){
                    Logger.d(TAG,"currentTab:"+currentTab);
                    Thread checkUpdate2 = new Thread() {
                        public void run() {
                            if ((error = getRecommend(recommendID, true)) == 0) {
                                Logger.d(TAG,"error:"+error);
                                recommHandler.post(setrecommendvalue);
                            } else {
                                retryHandler.sendEmptyMessage(0);
                                onGoing=false;
                            }

                        }
                    };
                    checkUpdate2.start();
                }
                else if(currentTab.equals("hot")){
                    getPageData();
                    //                  if(ThemeNum == 1){
                    //                      //                      defaultCatalogId = catalogId;
                    itemPerPage = 6;
                    //                  }
                    //                  else if(ThemeNum == 2){
                    //                      hot.setTextColor(android.graphics.Color.BLACK);
                    //                      selector.setImageResource(R.drawable.ranking_2_ui2);
                    //                  }
                }
                else if(currentTab.equals("total")){
                    Thread checkUpdate2 = new Thread() {
                        public void run() {
                            if ((error = getRecommend(totalID,true)) == 0) {
                                recommHandler.post(setrecommendvalue);
                            } else {
                                onGoing = false;
                                retryHandler.sendEmptyMessage(0);
                            }

                        }
                    };
                    checkUpdate2.start();
                }

            }
        
        }};


		private RelativeLayout rankselection = null;

		private String defaultCatalogId = "-1";
		private String defaultRankType = "1";
		private String defaultRankTime = "2";

		private List<RankBook> rankBookList=null;
		List<RankType> catalogTypeList=null;
		List<RankType> rankTypeList=null;
		List<RankType> timeList=null;
		private LinkedHashMap<String, String> catalogTypeListMap=null;
		private LinkedHashMap<String, String> rankTypeListMap=null;
		private LinkedHashMap<String, String> timeListMap=null;

		private static final int FLAG_ERROR = 0;
		private static final int FLAG_OK = 1;

		private static final String FLAG_CATALOG = "1";
		private static final String FLAG_TYPE = "2";
		private static final String FLAG_TIME = "3";
		protected static final int RANK_CATALOG_SELECTION_CHANGE = 0x01;
		protected static final int RANK_TYPE_SELECTION_CHANGE = 0x02;
		protected static final int RANK_TIME_SELECTION_CHANGE = 0x03;
		public final static int RANK_BOOK_LIST_UPDATE = 0x04;


		private Comparator<RankType> comparator;

		private void setSelection() {

			catalogSelection.setSelectKey(defaultCatalogIdlable);
			rankTypeSelection.setSelectKey(defaultRankTypeLable);
			rankTimeSelection.setSelectKey(defaultRankTimeLable);

		}

		/**
		 * 取 排行榜数据
		 * @param reflash
		 * @return
		 */
		private int getDataTypeRank(boolean reflash) {
			String start = String.valueOf((currentPage - 1) * itemPerPage + 1);
			String count = String.valueOf(itemPerPage);

			HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
			HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
			HashMap responseMap = null;
			ahmNamePair.put("catalogId", defaultCatalogId);
			ahmNamePair.put("rankType", defaultRankType);
			ahmNamePair.put("rankTime", defaultRankTime);
			ahmNamePair.put("start", start);
			ahmNamePair.put("count", count);
			ahmNamePair.put("blockId", "6");

			if(reflash)
			{
				ahmNamePair.put("reflash", "WirelessList");
			}

			try {
				responseMap = NetCache.getSpecifiedRank(ahmHeaderMap, ahmNamePair);
			} catch (HttpException e) {
				e.printStackTrace();
				return FLAG_ERROR;
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				return FLAG_ERROR;
			} catch (IOException e) {
				e.printStackTrace();
				return FLAG_ERROR;
			}
			byte[] responseBody = (byte[]) responseMap.get("ResponseBody");

			String rspC = responseMap.get("result-code").toString();

			//Logger.d(TAG,new String(responseBody));

			if (!rspC.contains("result-code: 0")) {
				return FLAG_ERROR;
			}

			Element root = null;

			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			try {
				db = dbfactory.newDocumentBuilder();
				InputStream is = new ByteArrayInputStream(responseBody);
				Document dom;
				dom = db.parse(is);
				root = dom.getDocumentElement();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				return FLAG_ERROR;
			} catch (SAXException e) {
				e.printStackTrace();
				return FLAG_ERROR;
			} catch (IOException e) {
				e.printStackTrace();
				return FLAG_ERROR;
			}
			NodeList totalNl = root.getElementsByTagName("totalRecordCount");
			int totalRecordCount = Integer.parseInt(totalNl.item(0).getFirstChild()
					.getNodeValue());
			totalPage_hot = totalRecordCount / itemPerPage;
			if (totalRecordCount % itemPerPage != 0) {
				totalPage_hot++;
				//				Logger.d("totalPage", totalRecordCount);
			}
			rankBookList.clear();
			NodeList nl = root.getElementsByTagName("RankContent");
			for (int i = 0; i < nl.getLength(); i++) {
				try {
                    Element rankContentEle = (Element) nl.item(i);
                    String contentID = rankContentEle.getElementsByTagName("contentID")
                    .item(0).getFirstChild().getNodeValue();
                    String contentName = rankContentEle.getElementsByTagName(
                    "contentName").item(0).getFirstChild().getNodeValue();
                    String authorID = rankContentEle.getElementsByTagName("authorID")
                    .item(0).getFirstChild().getNodeValue();
                    String authorName = rankContentEle.getElementsByTagName(
                    "authorName").item(0).getFirstChild().getNodeValue();
                    String rankValue = rankContentEle.getElementsByTagName("rankValue")
                    .item(0).getFirstChild().getNodeValue();
                    String current = rankContentEle.getElementsByTagName("current")
                    .item(0).getFirstChild().getNodeValue();
                    //				Logger.d("contentID", contentID);
                    //				Logger.d("contentName", contentName);
                    //				Logger.d("authorID", authorID);
                    //				Logger.d("authorName", authorName);
                    //				Logger.d("rankValue", rankValue);
                    //				Logger.d("current", current);
                    rankBookList.add(new RankBook(contentID, contentName, authorID,
                    		authorName, rankValue, current));
                } catch (DOMException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return FLAG_ERROR;
                }

			}

			catalogTypeList.clear();
			rankTypeList.clear();
			timeList.clear();

			nl = root.getElementsByTagName("Rank");
			String[] rankTypeData;
			for (int i = 0; i < nl.getLength(); i++) {
				try {
                    rankTypeData = new String[4];
                    Element rankEle = (Element) nl.item(i);
                    rankTypeData[0] = rankEle.getElementsByTagName("type").item(0)
                    .getFirstChild().getNodeValue();
                    rankTypeData[1] = rankEle.getElementsByTagName("id").item(0)
                    .getFirstChild().getNodeValue();
                    rankTypeData[2] = rankEle.getElementsByTagName("name").item(0)
                    .getFirstChild().getNodeValue();
                    rankTypeData[3] = rankEle.getElementsByTagName("orderNo").item(0)
                    .getFirstChild().getNodeValue();

                    if (FLAG_CATALOG.equals(rankTypeData[0])) {
                    	catalogTypeList.add(new RankType(rankTypeData[0],
                    			rankTypeData[1], rankTypeData[2], rankTypeData[3]));
                    	//					Logger.d("FLAG_CATALOG" + i, rankTypeData[1]);
                    } else if (FLAG_TYPE.equals(rankTypeData[0])) {
                    	rankTypeList.add(new RankType(rankTypeData[0], rankTypeData[1],
                    			rankTypeData[2]+"榜", rankTypeData[3]));
                    } else if (FLAG_TIME.equals(rankTypeData[0])) {
                    	timeList.add(new RankType(rankTypeData[0], rankTypeData[1],
                    			rankTypeData[2]+"排行", rankTypeData[3]));
                    }
                } catch (DOMException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return FLAG_ERROR;
                }

			}

			//			Logger.d("rankBookList lenght", rankBookList.size());
			//			Logger.d("catalogTypeList lenght", catalogTypeList.size());
			//			Logger.d("rankTypeList lenght", rankTypeList.size());
			//			Logger.d("rankTimeList lenght", timeList.size());

			rankTypeData = null;
			return FLAG_OK;
		}

		/**
		 * 设置本类热榜一页的数据到UI控件上显示
		 */
		private Runnable setdata = new Runnable()
		{
			@Override
			public void run() {

				onGoing = false;
				if(isshow)
				{
					compPageCounter();
				}
				else
				{
					showme();
				}
				updateselection();
				setSelection();


				titleLayout.setBackgroundResource(R.drawable.ranking_2);
				rankselection.setVisibility(View.VISIBLE);

				
				list.clear();
				for (int tmpi = 0; tmpi < rankBookList.size(); tmpi++) {
					handRankList(tmpi);
				}
				//一页数据已填充完毕
            listView.setData(list);
            listView.setOnRowClick(new OnRowClickListener() {

                @Override
                public void OnRowClick(View v, int rowIndex) {
                    final RankBook rankBook = rankBookList.get(rowIndex);
                    if (rankBook != null) {
                        final Intent tmpIntent = new Intent(
                                MainpageActivity.START_ACTIVITY);
                        final Bundle bundleToSend = new Bundle();
                        bundleToSend
                                .putString("act",
                                        "com.pvi.ap.reader.activity.BookSummaryActivity");
                        bundleToSend.putString("haveTitleBar", "1");
                        bundleToSend.putString("startType", "allwaysCreate");
                        bundleToSend.putString("contentID", rankBook
                                .getContentID());
                        bundleToSend.putString("pviapfStatusTip",getResources().getString(R.string.kyleHint01));
                        tmpIntent.putExtras(bundleToSend);
                        sendBroadcast(tmpIntent);
                    }
                }
            });
                showPager();
                updatePagerinfo(String.valueOf(currentPage)+" / "+String.valueOf(getTotalPage()));

				
				
/*				if (getTotalPage() < 1) {
					mNext.setVisibility(View.INVISIBLE);
					mPrev.setVisibility(View.INVISIBLE);
				}
				else
				{
					mNext.setVisibility(View.VISIBLE);
					mPrev.setVisibility(View.VISIBLE);
				}*/
				
				if(isshow)
				{
					hideTip();
				}
				//primaryBtn[1].requestFocus();
			}
		};
		private void updateselection() {
			Collections.sort(catalogTypeList, comparator);
			Collections.sort(rankTypeList, comparator);
			Collections.sort(timeList, comparator);

			catalogTypeListMap.clear();
			rankTypeListMap.clear();
			timeListMap.clear();

			for (RankType rankType : catalogTypeList) {
				catalogTypeListMap.put(rankType.getName(), rankType.getId());
				if(defaultCatalogId.equals(rankType.getId())){
					defaultCatalogIdlable =  rankType.getName();
				}
			}
			if(defaultCatalogIdlable.equals(""))
			{
				defaultCatalogIdlable = catalogTypeList.get(0).getName();
			}
			catalogSelection.setKey_value(catalogTypeListMap);

			for (RankType rankType : rankTypeList) {
				rankTypeListMap.put(rankType.getName(), rankType.getId());
				if(defaultRankType.equals(rankType.getId())){

					defaultRankTypeLable =  rankType.getName();
				}
			}
			if(defaultRankTypeLable.equals(""))
			{
				defaultRankTypeLable = rankTypeList.get(0).getName();
			}
			rankTypeSelection.setKey_value(rankTypeListMap);

			for (RankType rankType : timeList) {
				timeListMap.put(rankType.getName(), rankType.getId());
				if(defaultRankTime.equals(rankType.getId())){
					defaultRankTimeLable =  rankType.getName();
				}
			}
			if(defaultRankTimeLable.equals(""))
			{
				defaultRankTimeLable = timeList.get(0).getName();
			}
			rankTimeSelection.setKey_value(timeListMap);
		}

		@Override
		public OnUiItemClickListener getMenuclick() {
			return this.menuclick;
		}

		private Thread updateImage;

		private Handler setImageHandler = new Handler(){
			public void handleMessage(Message msg) {

				for (int i = 0; i < itemPerPage;i++) {
					if(curDataBtn.dataBitmap[i] != null){
					    if(i<list.size()){
    					    PviUiItem[] items = list.get(i);
    					    if(items!=null){
    					        items[0].pic = curDataBtn.dataBitmap[i];
    					        listView.invalidate();
    					    }
					    }
					}
				}

			};
		};
		
        private void handDataList(final int i) {

            
            PviUiItem[] items = new PviUiItem[]{
                    new PviUiItem("icon"+i, R.drawable.bookcover_5472_ui1, 10, 10, 54, 72, null, false, true, null),
                    new PviUiItem("text1"+i, 0, 90, 30, 250, 50, "我是一列文本", false, true, null),
                    new PviUiItem("text2"+i, 0, 540, 30, 200, 50, "我是又一列文本", false, true, null),
            };
            
            items[0].bgNormal = R.drawable.img_border_normal_ui1;
            items[0].bgFocus = R.drawable.img_border_clicked_ui1;
            items[1].text=curDataBtn.dataPrimary[i];
            items[2].text=curDataBtn.dataSlave[i];
            items[2].textType=PviUiItem.COMMON;
            items[2].textAlign=2;

            list.add(items);

        }
		
		private void handRankList(final int i) {

		    Logger.d(TAG,"handRankList( i:"+i);
		    
		    
			final RankBook rankBook = rankBookList.get(i);

			
			PviUiItem[] items = new PviUiItem[]{
                    new PviUiItem("icon"+i, R.drawable.bookcover_5472_ui1, 10, 10, 54, 72, null, false, true, null),
                    new PviUiItem("text1"+i, 0, 90, 30, 250, 50, "我是一列文本", false, true, null),
                    new PviUiItem("text2"+i, 0, 540, 30, 200, 50, "我是又一列文本", false, true, null),
            };
            
            items[0].bgNormal = R.drawable.img_border_normal_ui1;
            items[0].bgFocus = R.drawable.img_border_clicked_ui1;
            items[1].text=rankBook.getContentName();
            items[2].text="作者："+rankBook.getAuthorName();
            items[2].textType = PviUiItem.COMMON;
            items[2].textAlign=2;

	        list.add(items);

		}
		private Handler getimageHandler = new Handler(){
			public void handleMessage(Message msg) {
				updateImage = new Thread() {
					public void run() {
						Looper.prepare();
						int tmpPage = currentPage;
						for(int i = 0;i < itemPerPage;i++){
							if(curDataBtn.dataPrimary[i] != null && !curDataBtn.bitmapOK[i]){
								try
								{
									curDataBtn.dataBitmap[i] = NetCache.GetNetImage(curDataBtn.dataPrimaryBtn[i]);
									if(curDataBtn.dataBitmap[i] != null){
										if (currentTab.equals("recommend")) {
											dataPool_recommend.insert_image(curDataBtn.dataBitmap[i], tmpPage, i);
										}
										if (currentTab.equals("total")) {
											dataPool_total.insert_image(curDataBtn.dataBitmap[i], tmpPage, i);
										}
									}
								}
								catch(Exception e)
								{
									dataPool_recommend.insert_image(null, tmpPage, i);
								}
								catch(OutOfMemoryError e)
								{
									dataPool_recommend.insert_image(null, tmpPage, i);
								}
							}
						}

						setImageHandler.sendEmptyMessage(0);
					}
				};
				updateImage.start();
			};
		};
		
    /**
     * 设置一页的数据
     */
    Runnable setrecommendvalue = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            onGoing = false;
            if (isshow) {
                compPageCounter();
            } else {
                showme();
            }

            list.clear();
            for (int tmpi = 0; tmpi < itemPerPage
                    && curDataBtn.dataPrimary[tmpi] != null; tmpi++) {
                handDataList(tmpi);
            }
            // 一页数据已填充完毕
            listView.setData(list);
            listView.setOnRowClick(new OnRowClickListener() {

                @Override
                public void OnRowClick(View v, int rowIndex) {
                    final Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
                    final Bundle bundleToSend = new Bundle();
                    bundleToSend.putString("act", "com.pvi.ap.reader.activity.BookSummaryActivity");
                    bundleToSend.putString("haveTitleBar", "1");
                    bundleToSend.putString("startType", "allwaysCreate");
                    bundleToSend.putString("contentID", curDataBtn.dataId[rowIndex]);
                    bundleToSend.putString("pviapfStatusTip", getResources()  .getString(R.string.kyleHint01));
                    tmpIntent.putExtras(bundleToSend);
                    sendBroadcast(tmpIntent);
                }
            });
            showPager();
            updatePagerinfo(String.valueOf(currentPage) + " / "
                    + String.valueOf(getTotalPage()));

            /*
             * 2011-6-28 注释掉 for (int tmpi = 0; tmpi < itemPerPage &&
             * curDataBtn.dataPrimary[tmpi] != null; tmpi++) { final int i =
             * tmpi;
             * 
             * if(curDataBtn.dataBitmap[i] != null){ if(i<list.size()){
             * PviUiItem[] items = list.get(i); if(items!=null){ items[0].pic =
             * curDataBtn.dataBitmap[i]; } } }
             * 
             * }
             */

            rankselection.setVisibility(View.GONE);
            // primaryBtn[0].setVisibility(View.VISIBLE);
            // primaryImage[0].setVisibility(View.VISIBLE);
            // primary[0].setVisibility(View.VISIBLE);
            // slave[0].setVisibility(View.VISIBLE);

            /*
             * if (getTotalPage() < 1) { mNext.setVisibility(View.INVISIBLE);
             * mPrev.setVisibility(View.INVISIBLE); } else {
             * mNext.setVisibility(View.VISIBLE);
             * mPrev.setVisibility(View.VISIBLE); }
             * primaryBtn[0].requestFocus();
             */

            // if(ThemeNum == 1){
            if (currentTab.equals("recommend")) {
                titleLayout.setBackgroundResource(R.drawable.ranking_1);
            } else {
                titleLayout.setBackgroundResource(R.drawable.ranking_3);
            }
            if (isshow) {
                hideTip();
            }
            getimageHandler.sendEmptyMessage(0);
        }

    };
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
		    Logger.d(TAG,"onCreate()");

			appState = ((GlobalVar) getApplicationContext());
			//			ThemeNum = appState.getSkinID();
			//			if (ThemeNum == 1) {
			this.setContentView(R.layout.cataloghomepage);
			
	        listView= (PviDataList)findViewById(R.id.list);

			recommend = (TextView) findViewById(R.id.recommend);
			hot = (TextView) findViewById(R.id.hot);
			total = (TextView) findViewById(R.id.total);

			catalogSelection = (SelectSpinner) findViewById(R.id.catalogSelection);
			rankTypeSelection = (SelectSpinner) findViewById(R.id.rankTypeSelection);
			rankTimeSelection = (SelectSpinner) findViewById(R.id.rankTimeSelection);

			selectBtn = (Button) findViewById(R.id.rankBtn);

			rankselection = (RelativeLayout) findViewById(R.id.rankSelection);

			titleLayout = (LinearLayout) findViewById(R.id.titlelayout);

			catalogTypeList = new ArrayList<RankType>();
			rankTypeList = new ArrayList<RankType>();
			timeList = new ArrayList<RankType>();

			catalogTypeListMap = new LinkedHashMap<String, String>();
			rankTypeListMap = new LinkedHashMap<String, String>();
			timeListMap = new LinkedHashMap<String, String>();

			super.onCreate(savedInstanceState);


			rankHandler = new Handler();
			retryHandler = new Handler() {
				public void handleMessage(Message msg) {

					final PviAlertDialog pd = new PviAlertDialog(getParent());

					pd.setTitle(getResources().getString(R.string.kyleHint02));
					pd.setButton(DialogInterface.BUTTON_POSITIVE,getResources().getString(R.string.my_friend_sure), new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub	                    	
							pd.dismiss();
							if(currentTab.equals("recommend")){
								pagechange = true;
								recommend.performClick();
							}
							if(currentTab.equals("hot")){
								//				switchTab("total");
								pagechange = true;
								hot.performClick();
							}
							if(currentTab.equals("recommend")){
								pagechange = true;
								//				switchTab("hot");
								recommend.performClick();
							}
						}
					});
					pd.setButton(DialogInterface.BUTTON_NEUTRAL,getResources().getString(R.string.my_friend_cancel), new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							pd.dismiss();
							Intent intent = new Intent("com.pvi.ap.reader.mainframe.BACK");
							Bundle bundle = new Bundle();
							bundle.putString("startType", "allwaysCreate");
							intent.putExtras(bundle);
							sendBroadcast(intent);
						}
					});
					pd.show();

				};
			};

			recommHandler = new Handler();

			//TODO: Kizan need move the trigger action to somewhere else! 
			selectBtn.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {

					if(onGoing){
						showOnGoing();
						return ;
					}
					showMessage("正在获取数据...");
					String selectedCatalogId = catalogSelection.getSelectValue();
					if (!defaultCatalogId.equals(selectedCatalogId)) {
						defaultCatalogId = selectedCatalogId;
						currentPage = 1;
					}
					String selectedRankTime = rankTimeSelection.getSelectValue();
					if (!defaultRankTime.equals(selectedRankTime)) {
						defaultRankTime = selectedRankTime;
						currentPage = 1;
					}
					String selectedRankType = rankTypeSelection.getSelectValue();
					if (!defaultRankType.equals(selectedRankType)) {
						defaultRankType = selectedRankType;
						currentPage = 1;
					}
					onGoing = true;
					getPageData();
				}

			});
			registerWirelessSearch();
			recommend.setOnClickListener(new OnClickListener() {
				public void onClick(final View v) {
				    
				    Logger.d(TAG,"recommend clicked!");
					if(onGoing){
						showOnGoing();
						return ;
					}
					//					curDataBtn = new data_btn();
					//					currentTab = "recommend";
					//					currentPage = 1;
					//					onCreate(null);
					isshow=true;
					curDataBtn = new data_btn();
					currentTab = "recommend";
					if(!pagechange)
					{
						showMessage("正在进入...");
						currentPage = 1;
					}
					else
					{
						pagechange=false;
					}
					//					if(ThemeNum == 1){
					itemPerPage = 7;
					titleLayout.setBackgroundResource(R.drawable.ranking_1);
					//					}
					//					else if(ThemeNum == 2){
					//						total.setTextColor(android.graphics.Color.BLACK);
					//						selector.setImageResource(R.drawable.ranking_1_ui2);
					//					}

					int index = dataPool_total.findIndexByPg(currentPage);
					if (index > 0) {
					    Logger.d(TAG,"index:"+index);
						curDataBtn = dataPool_total.getbyindex(index);
						recommHandler.post(setrecommendvalue);
					} else {
					    Logger.d(TAG,"onGoing");
						onGoing = true;
						Thread checkUpdate2 = new Thread() {
							public void run() {
								if ((error = getRecommend(recommendID,false)) == 0) {
									//									recommHandler.sendEmptyMessage(0);
								    Logger.d(TAG,"error:"+error);
									recommHandler.post(setrecommendvalue);
								} else {
									onGoing = false;
									retryHandler.sendEmptyMessage(0);
								}

							}
						};
						checkUpdate2.start();
					}
					//				switchTab("recommend");
					return;
				}
			});
			hot.setOnClickListener(new OnClickListener() {
				public void onClick(final View v) {
				    Logger.d(TAG,"hot clicked!");
					if(onGoing){
						showOnGoing();
						return ;
					}
					isshow=true;
					curDataBtn = new data_btn();
					currentTab = "hot";
					if(!pagechange)
					{
						showMessage("正在进入...");
						currentPage = 1;
					}
					else
					{
						pagechange=false;
					}
					onGoing = true;
					getPageData();
					//					if(ThemeNum == 1){
					//						defaultCatalogId = catalogId;
					itemPerPage = 6;
					//					}
					//					else if(ThemeNum == 2){
					//						hot.setTextColor(android.graphics.Color.BLACK);
					//						selector.setImageResource(R.drawable.ranking_2_ui2);
					//					}
					//				switchTab("hot");
					return;
				}
			});
			total.setOnClickListener(new OnClickListener() {
				public void onClick(final View v) {
				    Logger.d(TAG,"total clicked!");
					if(onGoing){
						showOnGoing();
						return ;
					}
					isshow=true;
					curDataBtn = new data_btn();
					currentTab = "total";
					itemPerPage = 7;
					if(!pagechange)
					{
						showMessage("正在进入...");
						currentPage = 1;
					}
					else
					{
						pagechange=false;
					}

					int index = dataPool_total.findIndexByPg(currentPage);
					if (index > 0) {
						curDataBtn = dataPool_total.getbyindex(index);
						//						recommHandler.sendEmptyMessage(0);
						recommHandler.post(setrecommendvalue);
					} else {
						onGoing = true;

						Thread checkUpdate2 = new Thread() {
							public void run() {
								if ((error = getRecommend(totalID,false)) == 0) {
									recommHandler.post(setrecommendvalue);
								} else {
									onGoing = false;
									retryHandler.sendEmptyMessage(0);
								}

							}
						};
						checkUpdate2.start();
					}
					//				switchTab("total");
					return;
				}
			});


			//		
			//			primaryBtn[0].requestFocus();
		}

		
		/**
		 * 取 精品推荐
		 * @param id
		 * @param reflash
		 * @return
		 */
		public int getRecommend(String id, boolean reflash) {
		    Logger.d(TAG,"getRecommend id:"+id);

			String start = String.valueOf((currentPage - 1) * itemPerPage + 1);
			String count = String.valueOf(itemPerPage);
			HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
			HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
			HashMap responseMap = null;
			ahmNamePair.put("blockId", id);
			ahmNamePair.put("start", start);
			ahmNamePair.put("count", count);
			if(reflash)
			{
				ahmNamePair.put("reflash", "WirelessList");
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
			if((responseMap==null)||(responseMap.get("result-code")==null)||(responseMap.get("ResponseBody")==null))
			{
				return 1;
			}
			String rspC = responseMap.get("result-code").toString();
			if (rspC.contains("result-code: 0")) {
				rspC = "result-code: 0000\r\n";
			}else{
			    return 1;
			}
			byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
			
			//Logger.e(TAG,new String(responseBody));

			try {
				Document dom = null;
				try {
					dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
					return 1;
				} catch (SAXException e) {
					e.printStackTrace();
					return 1;
				}
				catch (IOException e) {
					e.printStackTrace();
					return 1;
				}
				if(dom==null)
				{
					return 1;
				}
				Element rootele2 = dom.getDocumentElement();

				NodeList totalNl = rootele2
				.getElementsByTagName("totalRecordCount");
				int totalRecordCount = Integer.parseInt(totalNl.item(0)
						.getFirstChild().getNodeValue());
				setTotalPage(totalRecordCount / itemPerPage);
				if (totalRecordCount % itemPerPage != 0) {
					setTotalPage(getTotalPage() + 1);
				}

				NodeList nn = rootele2.getElementsByTagName("ContentInfo");
				// loop item:content item
				for (int i= 0; i < nn.getLength(); i++) {
					Element entry = (Element) nn.item(i);
					NodeList nl2 = entry.getElementsByTagName("ContentParam");
					// loop item:param
					for (int j = 0; j < nl2.getLength(); j++) {
						Element eparam1 = (Element) nl2.item(j);
						NodeList nl3 = eparam1.getElementsByTagName("name");
						String name = nl3.item(0).getFirstChild().getNodeValue();
						if ("contentName".equals(name)) {
							NodeList nl4 = eparam1.getElementsByTagName("value");
							if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0).getFirstChild()!=null){					
							    curDataBtn.dataPrimary[i] = nl4.item(0).getFirstChild().getNodeValue();
							}else{
	                            curDataBtn.dataPrimary[i] = "无";
	                        }
						}
						if ("authorName".equals(name)) {
							NodeList nl4 = eparam1.getElementsByTagName("value");
							if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0).getFirstChild()!=null){
							    curDataBtn.dataSlave[i] = "作者:"
								+ nl4.item(0).getFirstChild().getNodeValue();
							}else{
							    curDataBtn.dataSlave[i] = "";
                            }
						}
						if ("contentID".equals(name)) {
							NodeList nl4 = eparam1.getElementsByTagName("value");
							if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0).getFirstChild()!=null){
							    curDataBtn.dataId[i] = nl4.item(0).getFirstChild()
							.getNodeValue();
							}else{
							    curDataBtn.dataId[i] = "";
							}
						}
						if ("smallLogo".equals(name)) {							
							NodeList nl4 = eparam1.getElementsByTagName("value");
							if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0).getFirstChild()!=null){
								    final String tmpStr = nl4.item(0).getFirstChild().getNodeValue();
								curDataBtn.dataPrimaryBtn[i] = Config.getString("CPC_BASE_URL") + tmpStr;
							}else {
								curDataBtn.dataPrimaryBtn[i] = null;
							}
						}
					}// end of param loop

				}// end of content item loop
			} catch (Exception e) {
			    e.printStackTrace();
				Logger.e("CatalogHomapage","recommend XML ERROR");
				return 2;
			}
			if (currentTab.equals("recommend")) {
				dataPool_recommend.insert_data(curDataBtn, currentPage);
			}
			if (currentTab.equals("total")) {
				dataPool_total.insert_data(curDataBtn, currentPage);
			}

			return 0;
		}
		@Override
		public boolean onKeyUp(int keyCode, KeyEvent event) {

			if (keyCode == KeyEvent.KEYCODE_BACK) {
				// 通知框架返回上一个子activty
				Intent intent = new Intent("com.pvi.ap.reader.mainframe.BACK");
				Bundle bundle = new Bundle();
				bundle.putString("startType", "allwaysCreate");
				intent.putExtras(bundle);
				sendBroadcast(intent);
				return false;
			}
			if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
				View focusview = this.getCurrentFocus();
				switch(focusview.getId())
				{
				case R.id.total:
				case R.id.hot:
				case R.id.recommend:
					if(currentTab.equals("hot")){
						//				switchTab("recommend");
						recommend.performClick();
						return true;
					}
					else if(currentTab.equals("total")){
						//				switchTab("hot");
						hot.performClick();
						return true;
					}
					return true;
				}

			}
			if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
				View focusview = this.getCurrentFocus();
				switch(focusview.getId())
				{
				case R.id.total:
				case R.id.hot:
				case R.id.recommend:
					if(currentTab.equals("recommend")){
						//				switchTab("hot");
						hot.performClick();
						return true;
					}
					else if(currentTab.equals("hot")){
						//				switchTab("total");
						total.performClick();
						return true;
					}

					return true;

				}


			}
			return super.onKeyUp(keyCode, event);
		}

		@Override
		protected void onResume() {
		    Logger.d(TAG,"onResume()");
			getSharedPreferences("curmymusicstatus", MODE_PRIVATE);
			super.onResume();
			isshow = false;
			Bundle bundleToRec = new Bundle();
			bundleToRec = this.getIntent().getExtras();
			catalogId = bundleToRec.getString("catalogId");
			{
				if(catalogId == null){
					catalogId = appState.getCatalogId();
				}
				appState.setCatalogId(catalogId);
			}
			defaultCatalogId = catalogId;

			if(currentTab.equals("recommend")){
				//				if(ThemeNum == 1){
				//					itemPerPage = 7;
				//					selector.setImageResource(R.drawable.ranking_1);
				rankselection.setVisibility(View.GONE);

				//				}
				//				if(ThemeNum == 2){
				//					recommend.setTextColor(android.graphics.Color.BLACK);
				//					selector.setImageResource(R.drawable.ranking_1_ui2);
				//				}
			}
			//		if(currentTab.equals("hot")){
			//		
			//		}
			if(currentTab.equals("total")){
				//				if(ThemeNum == 1){
				//					itemPerPage = 7;
				//					selector.setImageResource(R.drawable.ranking_3);
				rankselection.setVisibility(View.GONE);

				//				}
				//				if(ThemeNum == 2){
				//					total.setTextColor(android.graphics.Color.BLACK);
				//					selector.setImageResource(R.drawable.ranking_3_ui2);
				//				}
			}

			if (currentTab.equals("recommend")) {
				int index = dataPool_recommend.findIndexByPg(currentPage);
				if (index > 0) {
					curDataBtn = dataPool_recommend.getbyindex(index);
					//					recommHandler.sendEmptyMessage(0);
					recommHandler.post(setrecommendvalue);
				} else {
					Thread checkUpdate2 = new Thread() {
						public void run() {
							// get three title
							onGoing = true;
							if (!titleOK) {
								if ((error = getThreeTitle()) == 0) {
									titleOK = true;
								}
							}
							if ((error = getRecommend(recommendID, false)) == 0) {
								recommHandler.post(setrecommendvalue);
							} else {
								onGoing = false;
								retryHandler.sendEmptyMessage(0);
							}

						}
					};
					checkUpdate2.start();
				}

			}

			rankBookList = new ArrayList<RankBook>();

			comparator = new Comparator<RankType>() {

				@Override
				public int compare(RankType object1, RankType object2) {
					Integer i1 = Integer.parseInt(object1.getOrderNo());
					Integer i2 = Integer.parseInt(object2.getOrderNo());
					return i1.compareTo(i2);
				}
			};
		}

		/**
		 * 取 栏目首页XML
		 * @return
		 */
		private int getThreeTitle() {
			String strGCH = SubscribeProcess.network("getCatalogHomePage",
					catalogId, null, null, null);
			if (strGCH.substring(0, 10).contains("Exception")) {
				return 1;
			}
			try {
				InputStream is = new ByteArrayInputStream(strGCH.substring(20)
						.getBytes());
				Element rootele = null;

				DocumentBuilderFactory dbfactory = DocumentBuilderFactory
				.newInstance();
				DocumentBuilder db = dbfactory.newDocumentBuilder();
				Document dom = db.parse(is);
				rootele = dom.getDocumentElement();
				NodeList nl = rootele.getElementsByTagName("Block");
				// loop item:content item
				for (int i = 0; i < nl.getLength(); i++) {
					Element entry = (Element) nl.item(i);
					NodeList nl2 = entry.getElementsByTagName("blockName");
					String name = nl2.item(0).getFirstChild().getNodeValue();
					NodeList nl3 = entry.getElementsByTagName("blockID");
					String blockid = nl3.item(0).getFirstChild().getNodeValue();

					if (name.equals("精品推荐")) {
						recommendID = blockid;
						eRecommend = entry;
						continue;
					}
					if (name.equals("本类热榜")) {
						hotID = blockid;
						eHot = entry;
						continue;
					}
					if (name.equals("本类书库")) {
						totalID = blockid;
						eTotal = entry;
						continue;
					}

				}// end of content item loop
			} catch (Exception e) {
				return 2;
			}

			return 0;
		}

		private int getTotalPage() {
			if (currentTab.equals("recommend")) {
				return totalPage_recommend;
			}
			if (currentTab.equals("total")) {
				return totalPage_total;
			}
			if(currentTab.equals("hot"))
			{
				return totalPage_hot;
			}
			return 0;
		}

		private void setTotalPage(int t) {
			if (currentTab.equals("recommend")) {
				totalPage_recommend = t;
			}
			if (currentTab.equals("total")) {
				totalPage_total = t;
			}

		}

		private void getPageData() {
		    Logger.d(TAG,"getPageData()");
			//		showNetWorkProcessing();
			Thread checkUpdate = new Thread() {
				public void run() {
					if ((getDataTypeRank(false)) == FLAG_OK) {

						rankHandler.post(setdata);
					} else {
						//					hiddenDialog();
						onGoing = false;
						retryHandler.sendEmptyMessage(0);
					}
				}
			};
			checkUpdate.start();
		}

		private void showme()
		{
			isshow = true;
			super.showMe(getClass());
			super.hideTip();
		}
		private void compPageCounter() {
			if (((GlobalVar) getApplication()).deviceType == 1) {
				pageCounter++;
				if (pageCounter == 5) {
					pageCounter = 0;
					// gc16 full flash window
					Logger.d("CatalogHomePage", "gc16 full");
//					getWindow().getDecorView().getRootView().postInvalidate(
//							View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16
//							| View.EINK_UPDATE_MODE_FULL);
				} else {
					Logger.d("CatalogHomePage", "DU content");
				}
			}
		}

        @Override
        public void OnNextpage() {
            if(onGoing){
                //                      showOnGoing();
                return ;
            }
            showMessage("正在获取数据...");
            if (currentPage >= getTotalPage()) {
                return;
            }

            curDataBtn = new data_btn();
            currentPage++;
            //              onCreate(null);
            if (currentTab.equals("recommend")) {
                //                      onCreate(null);
                pagechange = true;
                recommend.performClick();
            }
            if (currentTab.equals("hot")) {
                pagechange = true;
                hot.performClick();
            }
            if (currentTab.equals("total")) {
                pagechange = true;
                total.performClick();
            }   
            super.OnNextpage();
        }

        @Override
        public void OnPrevpage() {
            if(onGoing){
                //                      showOnGoing();
                return ;
            }
            if (currentPage <= 1) {
                return;
            }
            showMessage("正在获取数据...");
            curDataBtn = new data_btn();
            currentPage--;
            //              onCreate(null);
            if (currentTab.equals("recommend")) {
                //                      onCreate(null);
                pagechange = true;
                recommend.performClick();
            }
            if (currentTab.equals("hot")) {
                pagechange = true;
                hot.performClick();
            }
            if (currentTab.equals("total")) {
                pagechange = true;
                total.performClick();
            }   
            super.OnPrevpage();
        }
}
