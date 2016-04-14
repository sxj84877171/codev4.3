/**
 * 4.2.8	书包介绍页
 * author:	马中庆
 */
package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviMenuItem;
import com.pvi.ap.reader.activity.pviappframe.PviPopupWindow;
import com.pvi.ap.reader.activity.pviappframe.PviReaderUI;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.common.beans.BookTypeInfo;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

//author kyle
public class BookPackageInfoActivity extends PviActivity {

	private static final String REFLASH = "WirelessList";

	protected static final int MSG_SET_UI = 0x0001;

	protected static final int MSG_GET_IMAGE = 0x0002;

	protected static final int MSG_SET_IMAGE = 0x0003;

	protected static final int MSG_RETRY = 0x0004;

	private final String LOG_TAG = "BookPackageInfoActivity";

	private String catalogID = "";
	private String catalogName = "";
	private String canSubscribe = "";
	private Intent revIntent = null;
	private Bundle revBundle = null;

	private String details = null;

	private TextView title = null;
	private TextView detailsView = null;
	private ImageView picView = null;

	final int itemPerPage = 4;

	ArrayList<BookTypeInfo> books = new ArrayList<BookTypeInfo>(itemPerPage);

	private RelativeLayout[] primaryBtn = new RelativeLayout[itemPerPage];
	private TextView[] booknametext = new TextView[itemPerPage];
	private TextView[] authortext = new TextView[itemPerPage];
	private ImageView[] coverImage = new ImageView[itemPerPage];

	private Button subscribe = null;

	private Bitmap picBitmap = null;

	String fee = "";

	@Override
	public OnUiItemClickListener getMenuclick() {
		return this.menuclick;
	}

	Thread updateImage;

	private boolean isOrdered = false;

	private boolean reflash = false;



	private Handler myHandler;

	private String picUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setContentView(R.layout.bookpackageinfo_ui1);		

		super.onCreate(savedInstanceState);

		myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int type = msg.what;
				switch (type) {
				case MSG_SET_UI:
					setUIData();
					break;
				case MSG_GET_IMAGE:
					getImages();
					break;
				case MSG_SET_IMAGE:
					setImage();
					break;
				case MSG_RETRY:
					showRetry();
					break;
				}

			}
		};

		registerWirelessSearch();
/*		findViewById(R.id.catalogBtn01).setOnClickListener(
				ListenerFactory.getGoAuthorRecommendListener(this));
		findViewById(R.id.catalogBtn02).setOnClickListener(
				ListenerFactory.getCatalogListener(this));
		findViewById(R.id.catalogBtn03).setOnClickListener(
				ListenerFactory.getRankingListener(this, null));
		findViewById(R.id.catalogBtn04).setOnClickListener(
				ListenerFactory.getGoWriterListener(this));
		findViewById(R.id.catalogBtn05).setOnClickListener(
				ListenerFactory.getGoodListener(this));
		findViewById(R.id.catalogBtn06).setOnClickListener(
				ListenerFactory.getBookPackageInfoListener(this));*/



		initTab();
		initControls();
		

	}

	private void showRetry() {
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

	}

	private void getImages() {
		new Thread() {
			public void run() {
				picBitmap = getCoverImage(picUrl);
				for (BookTypeInfo bookInfo : books) {
					bookInfo.setImage(getCoverImage(bookInfo.getSmallLogo()));
				}
				myHandler.sendEmptyMessage(MSG_SET_IMAGE);
			}
		}.start();

	}

	private void setImage() {

		if (picBitmap != null) {
			picView.setImageBitmap(picBitmap);
		}
		int i = 0;
		for (BookTypeInfo bookInfo : books) {
			if (bookInfo.getImage() != null) {
				coverImage[i].setImageBitmap(bookInfo.getImage());
			}
			i++;
		}
	}

	private void setUIData() {
		title.setText(catalogName);
		if ("false".equals(canSubscribe)) {
			subscribe.setVisibility(View.INVISIBLE);
		}
		detailsView.setText("简介：" + details);

		if (isOrdered) {
			subscribe.setText("您已订购");
			subscribe.setEnabled(false);
		}

		int i = 0;
		for (final BookTypeInfo bookInfo : books) {

			booknametext[i].setText(bookInfo.getContentName());
			authortext[i].setText("作者：" + bookInfo.getAuthorName());

			primaryBtn[i].setOnClickListener(new OnClickListener() {
				public void onClick(final View v) {
					Intent tmpIntent = new Intent(
							MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("act",
							"com.pvi.ap.reader.activity.BookSummaryActivity");
					bundleToSend.putString("haveTitleBar", "1");
					bundleToSend.putString("startType", "allwaysCreate");
					bundleToSend
							.putString("contentID", bookInfo.getContentID());
					bundleToSend.putString("pviapfStatusTip", "数据加载中，请稍候...");
					if (!isOrdered && canSubscribe.equals("true")) {
						bundleToSend.putString("catalogOrdered", "false");
						bundleToSend.putString("fee", fee);
						bundleToSend.putString("catalogName", catalogName);
						bundleToSend.putString("catalogID", catalogID);
					}
					tmpIntent.putExtras(bundleToSend);
					sendBroadcast(tmpIntent);
				}
			});
			i++;
		}
	}

	public void initControls() {
		// bind UI controls
		title = (TextView) findViewById(R.id.title);

		detailsView = (TextView) findViewById(R.id.details);
		picView = (ImageView) findViewById(R.id.image);

		subscribe = (Button) findViewById(R.id.subscribe);

		primaryBtn[0] = (RelativeLayout) findViewById(R.id.primaryBtn01);
		primaryBtn[1] = (RelativeLayout) findViewById(R.id.primaryBtn02);
		primaryBtn[2] = (RelativeLayout) findViewById(R.id.primaryBtn03);
		primaryBtn[3] = (RelativeLayout) findViewById(R.id.primaryBtn04);

		booknametext[0] = (TextView) findViewById(R.id.bookname01);
		booknametext[1] = (TextView) findViewById(R.id.bookname02);
		booknametext[2] = (TextView) findViewById(R.id.bookname03);
		booknametext[3] = (TextView) findViewById(R.id.bookname04);

		authortext[0] = (TextView) findViewById(R.id.author01);
		authortext[1] = (TextView) findViewById(R.id.author02);
		authortext[2] = (TextView) findViewById(R.id.author03);
		authortext[3] = (TextView) findViewById(R.id.author04);

		coverImage[0] = (ImageView) findViewById(R.id.image01);
		coverImage[1] = (ImageView) findViewById(R.id.image02);
		coverImage[2] = (ImageView) findViewById(R.id.image03);
		coverImage[3] = (ImageView) findViewById(R.id.image04);
		
		//设置eink屏刷
        /*if(deviceType==1){
              for(int i=0;i<4;i++){
                  primaryBtn[i].setUpdateMode(UPDATEMODE_1);
                  coverImage[i].setUpdateMode(UPDATEMODE_1);
              }
              picView.setUpdateMode(UPDATEMODE_1);
              subscribe.setUpdateMode(UPDATEMODE_1);
              final TextView tvMore = (TextView) findViewById(R.id.more);
              tvMore.setUpdateMode(UPDATEMODE_1);
          }*/

		bindEvent();

	}

	private void subscribe() {
		Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("act",
				"com.pvi.ap.reader.activity.SubscribeProcess");
		bundleToSend.putString("subscribeMode", "catalog");
		bundleToSend.putString("catalogID", catalogID);
		bundleToSend.putString("catalogName", catalogName);
		bundleToSend.putString("chargeTip", fee);
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
	}

	// 获取专区信息
	private boolean getCatalogContent(String catalogID) {

		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

		ahmNamePair.put("catalogId", catalogID);
		ahmNamePair.put("count", "4");
		if (reflash) {
		    showNetWorkProcessing2();
			ahmNamePair.put("reflash", REFLASH);
			reflash = false;
		}else{
		    showNetWorkProcessing();
		}
		HashMap responseMap = null;
		try {

			responseMap = NetCache.getCatalogContent(ahmHeaderMap, ahmNamePair);

			if (!responseMap.get("result-code").toString().contains(
					"result-code: 0")) {
				return false;
			}
		} catch (HttpException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		byte[] responseBody = (byte[]) responseMap.get("ResponseBody");

		Document dom = null;
		try {
			String xml = new String(responseBody);
			xml = xml.replaceAll("\\s", "");
			dom = CPManagerUtil.getDocumentFrombyteArray(xml.getBytes());

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		} catch (SAXException e) {
		    e.printStackTrace();
			return false;
		} catch (IOException e) {
		    e.printStackTrace();
			return false;
		}

		Element root = dom.getDocumentElement();
		NodeList nl1 = root.getChildNodes();
		nl1 = nl1.item(0).getChildNodes();

		books.clear();

		try {
            for (int i = 0; i < nl1.getLength(); i++) {
            	Element el = (Element) nl1.item(i);
            	if (el.getNodeName().equals("ContentInfoList")) {
            		NodeList nl2 = el.getChildNodes();
            		for (int j = 0; j < nl2.getLength() && j < itemPerPage; j++) {
            			Element el2 = (Element) nl2.item(j);
            			NodeList nl3 = el2.getChildNodes();
            			String contentID = "";
            			String contentName = "";
            			String authorName = "";
            			String smallLogo = "";

            			for (int k = 0; k < nl3.getLength(); k++) {
            				final Element el3 = (Element) nl3.item(k);
            				if (el3.getNodeName().equals("contentID")) {
            					contentID = el3.getFirstChild().getNodeValue();
            				} else if (el3.getNodeName().equals("contentName")) {
            					contentName = el3.getFirstChild().getNodeValue();
            				} else if (el3.getNodeName().equals("authorName")) {
            					authorName = el3.getFirstChild().getNodeValue();
            				} else if (el3.getNodeName().equals("smallLogo")) {
            					smallLogo = el3.getFirstChild().getNodeValue();
            				}
            			}

            			books.add(new BookTypeInfo(contentID, contentName,
            					authorName, smallLogo));

            		}

            	} else if (el.getNodeName().equals("description")) {
            		fee = el.getFirstChild().getNodeValue();
            		details = el.getFirstChild().getNodeValue();

            	} else if (el.getNodeName().equals("picUrl")) {
            		picUrl = el.getFirstChild().getNodeValue();

            	} else if (el.getNodeName().equals("isOrdered")) {
            		isOrdered = true;

            	}
            }
        } catch (DOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }catch (NullPointerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
		return true;
	}

	private Bitmap getCoverImage(String ImageUri) {
		return NetCache
				.GetNetImage(Config.getString("CPC_BASE_URL") + ImageUri);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 通知框架返回上一个子activty
			sendBroadcast(new Intent(WirelessTabActivity.BACK));
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			menupan();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	private OnUiItemClickListener menuclick = new OnUiItemClickListener() {

	    @Override
        public void onUiItemClick(PviUiItem item) {
            String vTag = item.id;
            if (vTag.equals("subscribe")) { // 通过tag来判断是前面xml中配置的哪个菜单
                subscribe();
            } else if (vTag.equals("reflash")) { //
                reflash = true;
                getPageData();

            } else if (vTag.equals("orderbook")) {
                Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
                Bundle sndBundle1 = new Bundle();
                sndBundle1.putString("act",
                        "com.pvi.ap.reader.activity.SubscribeProcess");
                sndBundle1.putString("subscribeMode", "feedback");
                intent1.putExtras(sndBundle1);
                sendBroadcast(intent1);
            }
            closePopmenu();
        }
	};


	public void bindEvent() {
		// 订购按钮
		subscribe.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 去订购提示页面
				subscribe();
			}
		});

		// 更多
		LinearLayout tv_More = (LinearLayout) findViewById(R.id.more);
		tv_More.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("act",
						"com.pvi.ap.reader.activity.BookListActivity");
				bundleToSend.putString("listType", "catalog");
				//bundleToSend.putString("blockId", blockId);
				bundleToSend.putString("mainTitle", catalogName);
				bundleToSend.putString("haveTitleBar", "1");
				bundleToSend.putString("catalogId", catalogID);
				bundleToSend.putString("pviapfStatusTip", "数据加载中，请稍候...");
				if (!isOrdered && canSubscribe!=null && canSubscribe.equals("true")) {
					bundleToSend.putString("catalogOrdered", "false");
					bundleToSend.putString("fee", fee);
					bundleToSend.putString("catalogName", catalogName);
				}

				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
			}
		});
		super.bindEvent();
	}

	protected void getPageData() {

		new Thread() {
			public void run() {
				if (!"".equals(catalogID)) {
					if (!getCatalogContent(catalogID)) {
						myHandler.sendEmptyMessage(MSG_RETRY);
					} else {
						myHandler.sendEmptyMessage(MSG_SET_UI);
						myHandler.sendEmptyMessage(MSG_GET_IMAGE);
						showMe(BookPackageInfoActivity.class);
					}
				}
			}
		}.start();

	}



	@Override
	protected void onResume() {
	    
	    
		//EPDRefresh.refreshGCOnceFlash();
		// TODO Auto-generated method stub

		// TODO : need fix ?
		// if (catalogID == null) {
		// catalogID = appState.getCatalogID();
		// }
		// if (catalogName == null) {
		// catalogName = appState.getCatalogName();
		// }
		// if (canSubscribe == null) {
		// canSubscribe = appState.getCanSubscribe();
		// }
		// appState.setCanSubscribe(canSubscribe);
		// appState.setCatalogID(catalogID);
		// appState.setCatalogName(catalogName);
	    
	    String blockName = "精品专栏";
	    final Intent intent = this.getIntent();
	    if(intent!=null){
	        final Bundle bd = intent.getExtras();
	        if(bd!=null){
	            blockName = bd.getString("blockName");
	        }
	    }       
	    setTabBg(blockName);
	    
	    //取数据
	       revIntent = this.getIntent();
	        revBundle = revIntent.getExtras();
	        if (revBundle != null) {
	            catalogID = revBundle.getString("catalogID");
	            catalogName = revBundle.getString("catalogName");
	            canSubscribe = revBundle.getString("canSubscribe");
	            //blockId = revBundle.getString("blockid");
	        }
	    getPageData();
	    
		super.onResume();
		
		//设置菜单
		 this.setOnPmShow(new OnPmShowListener(){
             @Override
             public void OnPmShow(PviPopupWindow w) {
                 final PviMenuItem item = (PviMenuItem)getMenuItem("subscribe");
                 if (isOrdered || !"true".equals(canSubscribe)) {
                     item.enable = false;
                 } else {
                     item.enable = true;                     
                 }
             }
		 });
	}

	
/*	private void initTab(String blockId2) {
		TextView catalogBtn05 = (TextView) findViewById(R.id.catalogBtn05);
		TextView catalogBtn06 = (TextView) findViewById(R.id.catalogBtn06);

		if ("3".equals(blockId2)) {
			catalogBtn05.setBackgroundResource(R.drawable.tab_normal_ui1);
			catalogBtn06.setBackgroundResource(R.drawable.tab_selected_ui1);
		} else {
			catalogBtn06.setBackgroundResource(R.drawable.tab_normal_ui1);
			catalogBtn05.setBackgroundResource(R.drawable.tab_selected_ui1);
		}
	}*/
	
	
	
	private void initTab() {

        final int[] blockUires = { R.id.catalogBtn01, R.id.catalogBtn02,
                R.id.catalogBtn03, R.id.catalogBtn04, R.id.catalogBtn05,
                R.id.catalogBtn06 };

        final GlobalVar app = ((GlobalVar) getApplicationContext());
        final ArrayList<HashMap<String, String>> blockInfo = app.getBlockInfo();
        if(blockInfo==null){
            Logger.e(LOG_TAG,"system err");
            return;
        }
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

	

}
