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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.SelectSpinner;
import com.pvi.ap.reader.activity.pviappframe.PviDataList.OnRowClickListener;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.common.beans.RankBook;
import com.pvi.ap.reader.common.beans.RankType;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * 无线书城 热门排行页
 * 
 * @author rd034
 * 
 */
public class WirelessRankActivity extends PviActivity {
    private static final String TAG = "WirelessRankActivity";

	// the data is set when the wireless store main page hs
	//need directly jump to here with rank type
	private static String defaultRankTypeSent = null;

	// 广播Action常量定义
	public final static String START_ACTIVITY = MainpageActivity.START_ACTIVITY;// 在主要区域启动一个Activity（是否每次都OnCreate？）
	public final static String SET_TITLE = MainpageActivity.SET_TITLE;// 设置标题栏文字
	public final static String BACK = MainpageActivity.BACK;// 返回上一个子Activity

	private int currentPage = 1;
	final private int itemPerPage = 6;

	private int totalPage = 0;

	private Handler retryHandler;
	// private Handler setImageHandler;
	// private Handler getimageHandler;


	private List<RankBook> rankBookList;
	// store the rankTypes name and type value

	private SelectSpinner catalogSelection;
	private SelectSpinner rankTypeSelection;
	private SelectSpinner rankTimeSelection;

	private Button selectBtn;

	private String defaultCatalogId = "-1";
	private String defaultRankType = "2";
	private String defaultRankTime = "1";

	private String blockId = null;
	private String tab = "empty";
	Thread updateImage;

	// add by kizan

	private Handler rankHandler;

	private String blocktype=null;

	public final static String BLOCK_TYPE_RANK_LIST = "3";

	protected static final int RANK_CATALOG_SELECTION_CHANGE = 0x01;
	protected static final int RANK_TYPE_SELECTION_CHANGE = 0x02;
	protected static final int RANK_TIME_SELECTION_CHANGE = 0x03;
	protected final static int RANK_BOOK_LIST_UPDATE = 0x04; 
	protected static final int SETECTION_UPDATE = 0x005;

	protected static final int SHOW_ME = 0x05;
	private static final int FLAG_ERROR = 0;
	private static final int FLAG_OK = 1;

	private static final String FLAG_CATALOG = "1";
	private static final String FLAG_TYPE = "2";
	private static final String FLAG_TIME = "3";

	private static final String PREFS_LABEL_CATALOG_ID = "PREFS_LABEL_CATALOG_ID";

	private static final String PREFS_LABEL_RANK_TYPE_ID = "PREFS_LABEL_RANK_TYPE_ID";

	private static final String PREFS_LABEL_RANK_TIME_ID = "PREFS_LABEL_RANK_TIME_ID";

	private static final String PREFS_LABE_RANKING = "PREFS_LABE_RANKING";

	private static final String REFLASH = "WirelessList";





	private Comparator<RankType> comparator;

	private List<RankType> catalogTypeList;
	private List<RankType> rankTypeList;
	private List<RankType> timeList;

	private LinkedHashMap<String, String> catalogTypeListMap;
	private LinkedHashMap<String, String> rankTypeListMap;
	private LinkedHashMap<String, String> timeListMap;

	protected boolean reflash = false;

	protected boolean onGoing = false;
	
	private GlobalVar app;
	private ArrayList<PviUiItem[]> list = new ArrayList<PviUiItem[]>();
    private PviDataList listView;

	public static void setRankTypeId(String rankTypeId) {
		defaultRankTypeSent = rankTypeId;
	}

	private OnUiItemClickListener menuclick = new OnUiItemClickListener() {

        @Override
        public void onUiItemClick(PviUiItem item) {

            String vTag = item.id;
            if (vTag.equals("reflash")) { //
                reflash = true;
                getPageData();
                closePopmenu();
            } else if (vTag.equals("orderbook")) {
                Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
                Bundle sndBundle1 = new Bundle();
                sndBundle1.putString("act",
                "com.pvi.ap.reader.activity.SubscribeProcess");
                sndBundle1.putString("subscribeMode", "feedback");
                intent1.putExtras(sndBundle1);
                sendBroadcast(intent1);
            }
        
        }
	};

	private OnClickListener paging(int direction) {
		if (direction >= 1) {
			OnClickListener nextListener = new OnClickListener() {
				public void onClick(final View v) {

					closePopmenu();
					// check if the get data is running
					if (onGoing) {
						return;
					}

					if (currentPage == totalPage) {
						return;
					}
					currentPage++;
					getPageData();
					return;
				}
			};
			return nextListener;
		} else {
			OnClickListener prevListener = new OnClickListener() {
				public void onClick(final View v) {

					closePopmenu();
					// check if the get data is running
					if (onGoing) {
						return;
					}
					if (currentPage == 1) {
						return;
					}
					currentPage--;
					getPageData();
					return;
				}
			};
			return prevListener;
		}
	}

	@Override
	public OnUiItemClickListener getMenuclick() {
		return this.menuclick;
	}

	private SharedPreferences mPrefs;

	private String defaultRankTimeLable;

	private String defaultRankTypeLable;

	private String defaultCatalogIdlable;

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		SharedPreferences.Editor ed = mPrefs.edit();
		ed.putString(PREFS_LABEL_CATALOG_ID, catalogTypeListMap
				.get(defaultCatalogId));
		ed.putString(PREFS_LABEL_RANK_TYPE_ID, rankTypeListMap
				.get(defaultRankType));
		ed
		.putString(PREFS_LABEL_RANK_TIME_ID, timeListMap
				.get(defaultRankTime));
		ed.commit();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		this.setContentView(R.layout.wireless_rank);
		app = ((GlobalVar) getApplicationContext());
        listView= (PviDataList)findViewById(R.id.list);

		rankHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case RANK_BOOK_LIST_UPDATE:
					catalogSelection.setSelectKey(defaultCatalogIdlable);
					rankTypeSelection.setSelectKey(defaultRankTypeLable);
					rankTimeSelection.setSelectKey(defaultRankTimeLable);

					list.clear();
					
					int tmpi;
					for (tmpi = 0; tmpi < rankBookList.size(); tmpi++) {
						handRankList(tmpi);					
					}
	                //一页数据已填充完毕
	                listView.setData(list);
                    listView.setOnRowClick(new OnRowClickListener() {

                        @Override
                        public void OnRowClick(View v, int rowIndex) {
                            final RankBook rankBook = rankBookList
                                    .get(rowIndex);
                            if (rankBook != null) {
                                final Intent tmpIntent = new Intent(
                                        MainpageActivity.START_ACTIVITY);
                                final Bundle bundleToSend = new Bundle();
                                bundleToSend
                                        .putString("act",
                                                "com.pvi.ap.reader.activity.BookSummaryActivity");
                                bundleToSend.putString("haveTitleBar", "1");
                                bundleToSend.putString("startType",
                                        "allwaysCreate");
                                bundleToSend.putString("contentID", rankBook
                                        .getContentID());
                                bundleToSend.putString("pviapfStatusTip",
                                        "进入书籍摘要...");
                                tmpIntent.putExtras(bundleToSend);
                                sendBroadcast(tmpIntent);
                            }
                        }
                    });
	                updatePagerinfo(String.valueOf(currentPage)+" / "+String.valueOf(totalPage));
	                showPager();


					onGoing = false;
					showMe();
					break;

				case SETECTION_UPDATE :
					updteSelection();
					break;
				}
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
						Intent tmpIntent = new Intent(
								MainpageActivity.START_ACTIVITY);
						Bundle bundleToSend = new Bundle();
						bundleToSend.putInt("type", 3);
						bundleToSend
						.putString("act",
						"com.pvi.ap.reader.activity.WirelessStoreActivity");
						bundleToSend.putString("pviapfStatusTip",
								getResources().getString(
										R.string.kyleHint03));
						bundleToSend.putString("tab", tab);
						tmpIntent.putExtras(bundleToSend);
						sendBroadcast(tmpIntent);
					}

				});
				pd.setButton(DialogInterface.BUTTON_NEUTRAL, "取消",
						new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						sendBroadcast(new Intent(
								WirelessRankActivity.BACK));
					}

				});
				pd.show();

			};
		};

		// setImageHandler = new Handler() {
		// public void handleMessage(Message msg) {
		//
		// for (int i = 0; i < itemPerPage; i++) {
		// if (curDataBtn.dataBitmap[i] != null) {
		// primaryImage[i]
		// .setImageBitmap(curDataBtn.dataBitmap[i]);
		// } else {
		// primaryImage[i]
		// .setImageResource(R.drawable.default_bookcover_6080_ui2);
		// }
		// }
		// };
		// };

		// getimageHandler = new Handler() {
		// public void handleMessage(Message msg) {
		// updateImage = new Thread() {
		// public void run() {
		// Looper.prepare();
		// int tmpPage = currentPage;
		// for (int i = 0; i < itemPerPage; i++) {
		// if (curDataBtn.dataPrimary[i] != null
		// && !curDataBtn.bitmapOK[i]) {
		// curDataBtn.dataBitmap[i] =
		// GetCoverImage(curDataBtn.dataPrimaryBtn[i]);
		// if (curDataBtn.dataBitmap[i] != null) {
		// dataPool.insert_image(
		// curDataBtn.dataBitmap[i], tmpPage,
		// i);
		// }
		// }
		// }
		// setImageHandler.sendEmptyMessage(0);
		// }
		// };
		// updateImage.start();
		// };
		// };

		rankBookList = new ArrayList<RankBook>();

		catalogTypeList = new ArrayList<RankType>();
		rankTypeList = new ArrayList<RankType>();
		timeList = new ArrayList<RankType>();

		catalogTypeListMap = new LinkedHashMap<String, String>();
		rankTypeListMap = new LinkedHashMap<String, String>();
		timeListMap = new LinkedHashMap<String, String>();

		mPrefs = getSharedPreferences(PREFS_LABE_RANKING, MODE_PRIVATE);

		String storedCatalogId = mPrefs.getString(PREFS_LABEL_CATALOG_ID, "");
		String storedRankTypeId = mPrefs
		.getString(PREFS_LABEL_RANK_TYPE_ID, "");
		String storedRankTimeId = mPrefs
		.getString(PREFS_LABEL_RANK_TIME_ID, "");

		defaultCatalogId = (storedCatalogId == null || ""
				.equals(storedCatalogId)) ? defaultCatalogId : storedCatalogId;
		defaultRankType = (storedRankTypeId == null || ""
				.equals(storedRankTypeId)) ? defaultRankType : storedRankTypeId;
		defaultRankTime = (storedRankTimeId == null || ""
				.equals(storedRankTimeId)) ? defaultRankType : storedRankTimeId;

		catalogSelection = (SelectSpinner) findViewById(R.id.catalogSelection);
		rankTypeSelection = (SelectSpinner) findViewById(R.id.rankTypeSelection);
		rankTimeSelection = (SelectSpinner) findViewById(R.id.rankTimeSelection);

		selectBtn = (Button) findViewById(R.id.rankBtn);

		super.onCreate(icicle);

		comparator = new Comparator<RankType>() {

			@Override
			public int compare(RankType object1, RankType object2) {
				Integer i1 = Integer.parseInt(object1.getOrderNo());
				Integer i2 = Integer.parseInt(object2.getOrderNo());
				return i1.compareTo(i2);
			}
		};

		selectBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(onGoing){
					showOnGoing();
					return ;
				}

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
				getPageData();
			}
		});

		registerWirelessSearch();

	}// end of onCreate()


	private void updteSelection(){

		Collections.sort(catalogTypeList, comparator);
		Collections.sort(rankTypeList, comparator);
		Collections.sort(timeList, comparator);

		catalogTypeListMap.clear();
		rankTypeListMap.clear();
		timeListMap.clear();

		for (RankType rankType : catalogTypeList) {
			catalogTypeListMap.put(rankType.getName(), rankType.getId());
			if (defaultCatalogId.equals(rankType.getId())) {
				defaultCatalogIdlable = rankType.getName();
			}
		}
		catalogSelection.setKey_value(catalogTypeListMap);

		for (RankType rankType : rankTypeList) {
			rankTypeListMap.put(rankType.getName(), rankType.getId());
			if (defaultRankType.equals(rankType.getId())) {
				defaultRankTypeLable = rankType.getName();

			}
		}
		rankTypeSelection.setKey_value(rankTypeListMap);

		for (RankType rankType : timeList) {
			timeListMap.put(rankType.getName(), rankType.getId());
			if (defaultRankTime.equals(rankType.getId())) {
				defaultRankTimeLable = rankType.getName();
			}
		}
		rankTimeSelection.setKey_value(timeListMap);
	}

	private void showMe() {

		sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));

		Intent tmpIntent = new Intent(MainpageActivity.SHOW_ME);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("act",
		"com.pvi.ap.reader.activity.WirelessStoreActivity");// TabActivity的类名
		bundleToSend.putString("actTabName", "热门排行");
		bundleToSend.putString("sender", this.getClass().getName()); // TAB内嵌activity类的全名
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);

	}

	private void getPageData() {

		onGoing = true;
		Thread checkUpdate = new Thread() {
			public void run() {
				if ((getDataTypeRank()) == FLAG_OK) {
					rankHandler.sendEmptyMessage(SETECTION_UPDATE);
					rankHandler.sendEmptyMessage(RANK_BOOK_LIST_UPDATE);

				} else {

					retryHandler.sendEmptyMessage(0);
				}
			}
		};
		checkUpdate.start();
	}

	private void handRankList(final int i) {

		final RankBook rankBook = rankBookList.get(i);
		
		
		
		PviUiItem[] items = new PviUiItem[]{
		        new PviUiItem("text1"+i, 0, 20, 20, 40, 50, "1", false, true, null),
                new PviUiItem("text1"+i, 0, 60, 20, 300, 50, "我是一列文本", false, true, null),
                new PviUiItem("text2"+i, 0, 60, 50, 300, 50, "我是又一列文本", false, true, null),
                new PviUiItem("text3"+i, 0, 500, 30, 200, 50, "我是又一列文本", false, true, null)
        };
        

        items[0].text=String.valueOf((this.currentPage-1)*this.itemPerPage +i+1)+".";
        items[0].textSize=26f;
        items[1].text=rankBook.getContentName();
        items[2].text="作者："+rankBook.getAuthorName();
        items[2].textSize=16f;
        items[3].textAlign=2;
        items[3].text=rankBook.getRankValue();
        items[3].textSize=16f;	

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
		// TODO Auto-generated method stub
		long TimeStart = System.currentTimeMillis();
		Logger.i(TAG, "onResume:" + Long.toString(TimeStart));
		Intent revIntent = this.getIntent();
		Bundle revBundle = revIntent.getExtras();
		if (revBundle != null) {
			tab = revBundle.getString("tab");
		}
		Bundle bundleToRec = this.getIntent().getExtras();
		blockId = bundleToRec.getString("blockid");
		blocktype = bundleToRec.getString("blocktype");
		if (bundleToRec.getString("rankedID") != null) {
			Logger.i(TAG, "get type id from intent:"
					+ bundleToRec.getString("rankedID"));
			defaultRankType = bundleToRec.getString("rankedID");
		}
		// 
		if (defaultRankTypeSent != null) {
			defaultRankType = defaultRankTypeSent;
			defaultRankTypeSent = null;
		}

		getPageData();
		super.onResume();

	}

	// TODO : must fix later :( kizan
	/**
	 * must fix
	 **/
	private int getDataTypeRank() {

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
		ahmNamePair.put("blockId", blockId);

		if (reflash) {
            showNetWorkProcessing2();
            ahmNamePair.put("reflash", REFLASH);
            reflash = false;
        }else{
            showNetWorkProcessing();
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
		
		Logger.d(TAG,new String(responseBody));

		String rspC = responseMap.get("result-code").toString();
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
		totalPage = totalRecordCount / itemPerPage;
		if (totalRecordCount % itemPerPage != 0) {
			totalPage++;
			Logger.d("totalPage", totalRecordCount);
		}
		rankBookList.clear();
		NodeList nl = root.getElementsByTagName("RankContent");
		for (int i = 0; i < nl.getLength(); i++) {
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
			rankBookList.add(new RankBook(contentID, contentName, authorID,
					authorName, rankValue, current));
		}

		catalogTypeList.clear();
		rankTypeList.clear();
		timeList.clear();

		nl = root.getElementsByTagName("Rank");
		String[] rankTypeData;
		for (int i = 0; i < nl.getLength(); i++) {
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
			//			Logger.d("rankTypeData[0]" + i, rankTypeData[1]);
			if (FLAG_CATALOG.equals(rankTypeData[0])) {
				catalogTypeList.add(new RankType(rankTypeData[0],
						rankTypeData[1], rankTypeData[2], rankTypeData[3]));
			} else if (FLAG_TYPE.equals(rankTypeData[0])) {
				rankTypeList.add(new RankType(rankTypeData[0], rankTypeData[1],
						rankTypeData[2]+"榜", rankTypeData[3]));
			} else if (FLAG_TIME.equals(rankTypeData[0])) {
				timeList.add(new RankType(rankTypeData[0], rankTypeData[1],
						rankTypeData[2]+"排行", rankTypeData[3]));
			}
		}
		rankTypeData = null;

		return FLAG_OK;
	}

    @Override
    public void OnNextpage() {
     // check if the get data is running
        if (onGoing) {
            return;
        }

        if (currentPage == totalPage) {
            return;
        }
        currentPage++;
        getPageData();
        super.OnNextpage();
    }

    @Override
    public void OnPrevpage() {
        // check if the get data is running
        if (onGoing) {
            return;
        }
        if (currentPage == 1) {
            return;
        }
        currentPage--;
        getPageData();
        super.OnPrevpage();
    }

	// public static Bitmap GetCoverImage(String ImageUri) {
	// Logger.d("debug", "GetCoverImage: " + ImageUri);
	// URL ImageUrl = null;
	// Bitmap bitmap = null;
	// try {
	// ImageUrl = new URL(ImageUri);
	// } catch (Exception e) {
	// Logger.e("", "Get URL Exception: " + e.toString());
	// return null;
	// }
	// try {
	// HttpURLConnection conn = (HttpURLConnection) ImageUrl
	// .openConnection();
	// conn.setDoInput(true);
	// conn.connect();
	// InputStream is = conn.getInputStream();
	// bitmap = BitmapFactory.decodeStream(is);
	// is.close();
	// conn.disconnect();
	// return bitmap;
	// } catch (IOException e) {
	// Logger.e("", e.toString());
	// }
	// return bitmap;
	// }





}