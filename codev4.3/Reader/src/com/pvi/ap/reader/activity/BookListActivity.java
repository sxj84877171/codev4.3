/**
 * 书籍列表页
 * @author rd029 晏子凯
 * parameters : author,catalog,similar,othersread
 */

package com.pvi.ap.reader.activity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviReaderUI;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviDataList.OnRowClickListener;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.common.beans.BookTypeInfo;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * 
 * refactor by kizan 2011.03.31
 * 
 */
public class BookListActivity extends PviActivity {

	protected static final String TAG = "BookListActivity";
	private static final String ERROR = null;
	protected static final int FLAG_OK = 0x0001;
	private static final int FLAG_ERROR = 0x0002;
	final private int itemPerPage = 7;
	EditText searchinput=null;
	Button searchbtn = null;
	// 传入参数
	private String catalogId = null;// 栏目id
	private String catalogName = null;
	private String listType = null; // 列表类型 catalog： catalog里的书籍列表；

	// block里书籍列表;author 某作者的全部书籍列表
	private String authorId = null; // 作者id
	private String contentId = null;
	private Intent revIntent = null;
	private Bundle revBundle = null;

	private String catalogOrdered = null;
	private String fee = null;

	private List<BookTypeInfo> books = new ArrayList<BookTypeInfo>();

	private int currentPage = 1;
	private int totalPage = 0;

	private final String REFLASH = "WirelessList";

	private boolean reflash = false;

	protected boolean onGoing = false;

	private boolean onImageWork = false;
	
	private ArrayList<PviUiItem[]> list = new ArrayList<PviUiItem[]>();
	private PviDataList listView;
	private GlobalVar app;

	private Handler retryHandler = new Handler() {
		public void handleMessage(Message msg) {

			final PviAlertDialog pd = new PviAlertDialog(getParent());
			pd.setTitle(R.string.kyleHint02);
			pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
					new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					getPageData();
				}
			});
			pd.setButton(DialogInterface.BUTTON_NEUTRAL, "取消",
					new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			pd.show();

		};
	};
	
	/**
	 * final BookTypeInfo book = books.get(i);
                    if (book!=null&&book.getImage() != null&&i<itemPerPage) {
                        try {
                            PviUiItem[] items = list.get(i);
                            items[0].pic = book.getImage();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
	 */

    private Handler getimageHandler = new Handler() {
        public void handleMessage(Message msg) {
            new Thread() {
                public void run() {

                    synchronized (books) {
                        onImageWork = true;
                        final int count = books.size();
                        for (int i = 0; i < count; i++) {
                            final BookTypeInfo bookInfo = books.get(i);
                            final String logo = bookInfo.getSmallLogo();
                            if (logo != null && !"".equals(logo)) {
                                bookInfo.setImage(NetCache.GetNetImage(Config
                                        .getString("CPC_BASE_URL")
                                        + bookInfo.getSmallLogo()));
                                if (bookInfo.getImage() != null
                                        && i < itemPerPage) {
                                    try {
                                        PviUiItem[] items = list.get(i);
                                        items[0].pic = bookInfo.getImage();
                                        listView.postInvalidate();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }
                        onImageWork = false;
                        onGoing = false;
                        // setImageHandler.sendEmptyMessage(0);
                    }

                }

            }.start();
        };
    };



	private Handler bookHandler = new Handler() {
		public void handleMessage(Message msg) {
			searchinput=(EditText) findViewById(R.id.searchInput);
			

			searchbtn=(Button) findViewById(R.id.searchButton);
			
//			if(appState.deviceType == 1)
//			{
//				searchinput.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_DU | View.EINK_UPDATE_MODE_PARTIAL);
//				searchbtn.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_DU | View.EINK_UPDATE_MODE_PARTIAL);
//			}
			searchinput.setVisibility(View.VISIBLE);
			searchbtn.setVisibility(View.VISIBLE);
			if(totalPage == 0)
			{
				totalPage = 1;
				searchinput.setVisibility(View.INVISIBLE);
				searchbtn.setVisibility(View.INVISIBLE);
			}
			if(currentPage>totalPage)
			{
				currentPage = totalPage;
			}

			updatePagerinfo(String.valueOf(currentPage)+" / "+String.valueOf(totalPage));
			
			
			if(books==null){
			    Logger.e(TAG,"system is null");
			    return;
			}
			
			list.clear();
			
			synchronized (books) {
                final int countBooks = books.size();
                int i = 0;
                for (; i < countBooks; i++) {
                    final BookTypeInfo bookInfo = books.get(i);
                    if (i >= itemPerPage) {
                        break;
                    }
                    if (bookInfo == null) {
                        break;
                    }
                    
                    
                    PviUiItem[] items = new PviUiItem[]{
                            new PviUiItem("icon"+i, R.drawable.bookcover_5472_ui1, 10, 10, 54, 72, null, false, true, null),
                            new PviUiItem("text1"+i, 0, 90, 30, 200, 50, "我是一列文本", false, true, null),
                            new PviUiItem("text2"+i, 0, 530, 30, 200, 50, "我是又一列文本", false, true, null),
                    };
                    
                    items[0].bgNormal = R.drawable.img_border_normal_ui1;
                    items[0].bgFocus = R.drawable.img_border_clicked_ui1;
                    items[1].text=bookInfo.getContentName();
                    items[2].text="作者:" + bookInfo.getAuthorName();
                    items[2].textAlign = 2;
                    items[2].textType=PviUiItem.COMMON;

                    list.add(items);
                }
                
                //一页数据已填充完毕
                listView.setData(list);
                listView.setOnRowClick(new OnRowClickListener(){

                    @Override
                    public void OnRowClick(View v, int rowIndex) {
                        if(rowIndex<0||rowIndex>books.size()-1){
                            return;
                        }
                        final BookTypeInfo bookInfo = books.get(rowIndex);
                        Intent tmpIntent = new Intent(
                                MainpageActivity.START_ACTIVITY);
                        Bundle bundleToSend = new Bundle();
                        bundleToSend
                                .putString("act",
                                        "com.pvi.ap.reader.activity.BookSummaryActivity");
                        bundleToSend.putString("haveTitleBar", "1");
                        bundleToSend
                                .putString("startType", "allwaysCreate");
                        bundleToSend.putString("contentID", bookInfo
                                .getContentID());
                        bundleToSend.putString("pviapfStatusTip",
                                getResources().getString(
                                        R.string.kyleHint01));
                        if (listType.equals("catalog")) {
                            if (null != catalogOrdered) {
                                bundleToSend.putString("catalogOrdered",
                                        "false");
                                bundleToSend.putString("fee", fee);
                                bundleToSend.putString("catalogID",
                                        catalogId);
                                bundleToSend.putString("catalogName",
                                        catalogName);
                            }
                        }

                        tmpIntent.putExtras(bundleToSend);
                        sendBroadcast(tmpIntent);
                    
                    }});
                
                showPager();
                updatePagerinfo(String.valueOf(currentPage)+" / "+String.valueOf(totalPage));
                

                //show出界面
                hideTip();
                showMe(BookListActivity.class);
            
                getimageHandler.sendEmptyMessage(0);
			}
		};
	};

	private Handler setImageHandler = new Handler() {
		public void handleMessage(Message msg) {
			synchronized (books) {
			    final int countBooks = books.size();
                for (int i = 0; i < countBooks; i++) {
                    final BookTypeInfo book = books.get(i);
                    if (book!=null&&book.getImage() != null&&i<itemPerPage) {
                        try {
                            PviUiItem[] items = list.get(i);
                            items[0].pic = book.getImage();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                onImageWork = false;
                onGoing = false;
            }            
		};
	};

	@Override
	public OnUiItemClickListener getMenuclick() {
		return this.menuclick;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.booklist);		
		super.onCreate(savedInstanceState);
		
		app = ((GlobalVar) getApplicationContext());		
		listView= (PviDataList)findViewById(R.id.list);
		
		initTab();
		registerWirelessSearch();
	}
	
	
    @Override
    protected void onResume() {
        Logger.i("Time", "BookListActivity onResume():"
                + Long.toString(System.currentTimeMillis()));
        super.onResume();
        
        revIntent = this.getIntent();
        if(revIntent==null){
            Logger.e(TAG,"system err:revIntent is null");
            return;
        }
        revBundle = revIntent.getExtras();
        Logger.d(TAG,revBundle);
        if (revBundle != null) {
            listType = revBundle.getString("listType");
            if(revBundle.containsKey("catalogId"))
            {
                catalogId = revBundle.getString("catalogId");
            }
            if(revBundle.containsKey(catalogName))
            {
                catalogName = revBundle.getString("catalogName");
            }
            if(revBundle.containsKey("blockId"))
            {
                revBundle.getString("blockId");
            }
            if(revBundle.containsKey("authorId")){
                authorId = revBundle.getString("authorId");
            }
            if(revBundle.containsKey("contentId")){
                contentId = revBundle.getString("contentId");
            }
            if(revBundle.containsKey("catalogOrdered")){
                catalogOrdered = revBundle.getString("catalogOrdered");
            }
            if (null != catalogOrdered) {
                fee = revBundle.getString("fee");
            }

        }
        final LinearLayout tabs = (LinearLayout) findViewById(R.id.tabs);
        if(tabs==null){
            Logger.e(TAG,"system err");
            return;
        }
        if("othersread".equals(listType)||"similar".equals(listType))
        {
            tabs.setVisibility(View.GONE);
        }
        else
        {
            tabs.setVisibility(View.VISIBLE);
        }
        
        if ("author".equals(listType)) {
            setTabBg("名家名作");
        } else if ("catalog".equals(listType)) {
            setTabBg("精品专栏");
            
        }
        
        getPageData();

    }

	private int getData() {
		synchronized (books) {
            // clear the books data first
            books.clear();
        }
        String start = String.valueOf((currentPage - 1) * itemPerPage + 1);
		String count = String.valueOf(itemPerPage);
		// get the data with list type
		String strGAI = getXmlData(start, count);
		if(strGAI==null||"".equals(strGAI))
		{
			return FLAG_ERROR;
		}

		InputStream is = new ByteArrayInputStream(strGAI.substring(20)
				.getBytes());
		Element rootele = null;
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbfactory.newDocumentBuilder();

			Document dom = db.parse(is);

			rootele = dom.getDocumentElement();
			NodeList totalNl = rootele.getElementsByTagName("totalRecordCount");
			int totalRecordCount = Integer.parseInt(totalNl.item(0)
					.getFirstChild().getNodeValue());
			totalPage = totalRecordCount / itemPerPage;
			if (totalRecordCount % itemPerPage != 0) {
				totalPage++;
			}
			String contentID = "";
			String contentName = "";
			String authorName = "";
			String smallLogo = "";

			if(listType.equals("author")){
				NodeList authorNl = rootele.getElementsByTagName("authorName");
				if(authorNl!=null&&authorNl.item(0)!=null){
					authorName = authorNl.item(0)
					.getFirstChild().getNodeValue();
				}
			}

			NodeList nl = rootele.getElementsByTagName("ContentInfo");
			for (int i = 0; i < nl.getLength(); i++) {
				Element entry = (Element) nl.item(i);
				NodeList nl2 = entry.getElementsByTagName("contentName");

				contentName = nl2.item(0).getFirstChild().getNodeValue();

				nl2 = entry.getElementsByTagName("smallLogo");
				if(nl2.getLength()!=0){
					smallLogo = nl2.item(0).getFirstChild().getNodeValue();
				}

				nl2 = entry.getElementsByTagName("contentID");
				contentID = nl2.item(0).getFirstChild().getNodeValue();

				nl2 = entry.getElementsByTagName("authorName");
				if(nl2.getLength()!=0){
					authorName = nl2.item(0).getFirstChild().getNodeValue();
				}


				synchronized (books) {
                    books.add(new BookTypeInfo(contentID, contentName,
                            authorName, smallLogo));
                }
			}
		} catch (ParserConfigurationException e) {

			e.printStackTrace();
			return FLAG_ERROR;
		} catch (SAXException e) {

			e.printStackTrace();
			return FLAG_ERROR;
		} catch (IOException e) {

			e.printStackTrace();
			return FLAG_ERROR;
		}catch (Exception e) {

            e.printStackTrace();
            return FLAG_ERROR;
        }
		return FLAG_OK;
	}

	private void getPageData() {
		onGoing = true;
		new Thread() {
			public void run() {
				if (getData() == FLAG_OK) {
					bookHandler.sendEmptyMessage(0);
				} else {
					retryHandler.sendEmptyMessage(0);
				}
			}
		}.start();

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


    private void initTab() {

        final int[] blockUires = { R.id.catalogBtn01, R.id.catalogBtn02,
                R.id.catalogBtn03, R.id.catalogBtn04, R.id.catalogBtn05,
                R.id.catalogBtn06 };

        final GlobalVar app = ((GlobalVar) getApplicationContext());
        final ArrayList<HashMap<String, String>> blockInfo = app.getBlockInfo();
        final int countBlock = blockInfo.size();
        for (int i = 0; i < countBlock; i++) {
            final HashMap<String, String> hm = blockInfo.get(i);
            if (hm != null) {
                final String blockname = hm.get("blockName");
                final TextView tvBlock = (TextView) findViewById(blockUires[i]);
                if (tvBlock != null) {
                    tvBlock.setText(blockname);
                    tvBlock.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            final Intent tmpIntent = new Intent(
                                    MainpageActivity.START_ACTIVITY);
                            final Bundle bundleToSend = new Bundle();
                            tmpIntent.putExtras(bundleToSend);
                            bundleToSend
                                    .putString("act",
                                            "com.pvi.ap.reader.activity.WirelessStoreActivity");
                            bundleToSend.putString("actTabName", blockname);
                            bundleToSend.putString("pviapfStatusTip", "进入"
                                    + blockname + "...");
                            tmpIntent.putExtras(bundleToSend);
                            sendBroadcast(tmpIntent);
                        }
                    });
                    tvBlock.setOnTouchListener(new OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            // TODO Auto-generated method stub
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                if (!v.hasFocus()) {
                                    v.performClick();
                                }
                            }
                            return false;
                        }
                    });
                }
            }
        }

    }
	
	private void setTabBg(String blockName){

	    final LinearLayout llTabs = (LinearLayout)findViewById(R.id.tabs);
	    if(llTabs!=null){
	    final int countTab = llTabs.getChildCount();
	    for(int i=0;i<countTab;i++){
	        final TextView tvBlock = (TextView)llTabs.getChildAt(i);
            if(tvBlock!=null){
                final String text = tvBlock.getText().toString();
                if(text!=null&&text.equals(blockName)){
                    llTabs.setBackgroundResource(PviReaderUI.tabBgs[i]);
                    break;
                }
            }

	    }
	}

	}

	private String getXmlData(String start, String count) {

		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		HashMap responseMap = null;
		if (reflash) {
		    showNetWorkProcessing2();
			ahmNamePair.put("reflash", REFLASH);
			reflash = false;
		}else{
		    showNetWorkProcessing();
		}
		ahmNamePair.put("start", start);
		ahmNamePair.put("count", count);
		try {
			// with different list types have different network methods 
			if ("author".equals(listType)) {
				ahmNamePair.put("authorId", authorId);
				responseMap = NetCache.getAuthorInfo(ahmHeaderMap, ahmNamePair);
			} else if ("similar".equals(listType)
					|| "othersread".equals(listType)) {
				ahmNamePair.put("contentId", contentId);
				// 1读过此书的还读过 2订购过此书的还订购过 3同类型书 4同系列的书
				if ("othersread".equals(listType)) {
					ahmNamePair.put("type", "1");
				} else {
					ahmNamePair.put("type", "3");
				}
				responseMap = NetCache.getRecommendContentList(ahmHeaderMap,
						ahmNamePair);
			} else if ("catalog".equals(listType)) {
				ahmNamePair.put("catalogId", catalogId);
				responseMap = NetCache.getCatalogContent(ahmHeaderMap,
						ahmNamePair);
			}else{
			    Logger.e(TAG,"unkwon listType or is null");
			    return "";
			}

		} catch (HttpException e) {
			e.printStackTrace();
			return "";
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			return "";

		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		
		
		if(responseMap!=null&&responseMap.get("ResponseBody")!=null){
		    try {
                byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
                String rspC = responseMap.get("result-code").toString();
                //Logger.d(LOG_TAG,new String(responseBody));
                //TODO add result-code support here !
                if (!rspC.contains("result-code: 0")) {
                    return ERROR;
                }

                return new String(responseBody);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "";
            }
		}else{
		    return "";
		}
		

		
	}

    @Override
    public void OnNextpage() {
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

}
