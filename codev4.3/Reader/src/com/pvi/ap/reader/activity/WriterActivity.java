/**
 * 4.2.8	作者介绍页
 * author:	马中庆
 */
package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * 
 * @author rd045
 * 
 */
public class WriterActivity extends PviActivity {

	protected static final int GET_DATA = 0x01;

	private final String LOG_TAG = "WriterActivity";

	private String authorID = "";
	private String authorName = "";
	private String authorDetails = "";
	ArrayList<String[]> books = new ArrayList<String[]>(5);

	private Intent revIntent = null;
	private Bundle revBundle = null;

	private TextView tv_authorName = null;
	private TextView tv_authorDetails = null;
	private LinearLayout tv_More = null;

	private Button viewAuthorInfoBtn;

	private ListView dataList = null;
	private WriterBookAdapter adapter = null;

	private Handler retryHandler;


	private Handler writerHandler;

	private Runnable getDataRunnable;

	private GlobalVar appState;
	private Handler mHandler = new Handler();

	private static final String REFLASH = "WirelessList";
	private boolean reflash = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
/*		Intent tmpintent = new Intent(MainpageActivity.SET_TITLE);
		Bundle sndBundle = new Bundle();
		sndBundle.putString("title", "名家名作");
		tmpintent.putExtras(sndBundle);
		sendBroadcast(tmpintent);*/

		appState = ((GlobalVar) getApplicationContext());
		setContentView(R.layout.writer_ui1);
		
		super.onCreate(savedInstanceState);

		tv_More = (LinearLayout) findViewById(R.id.moreBtn);
		tv_More.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID",	"ACT20011");
				bundleToSend.putString("listType", "author");
				bundleToSend.putString("mainTitle", authorName+"[>>]作品列表");
				bundleToSend.putString("authorId", authorID);
				bundleToSend.putString("pviapfStatusTip", "数据加载中，请稍候...");
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
			}
		});

		writerHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case GET_DATA:
				    new Thread() {
	                    public void run() {
	                        getDataRunnable.run();
	                    }
	                }.start();
				    //this.post(getDataRunnable);
					break;

				}

			}
		};

		getDataRunnable = new Runnable() {
			@Override
			public void run() {
				if (!setContent(authorID)) {
					retryHandler.sendEmptyMessage(0);
				}
			}

		};

		retryHandler = new Handler() {
			public void handleMessage(Message msg) {

				final PviAlertDialog pd = new PviAlertDialog(getParent());

				pd.setMessage(getResources().getString(R.string.kyleHint02));
				pd.setButton(DialogInterface.BUTTON_POSITIVE, getResources()
						.getString(R.string.my_friend_sure),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								pd.dismiss();
								getPageData();
							}
						});
				pd.setButton(DialogInterface.BUTTON_NEUTRAL, getResources()
						.getString(R.string.my_friend_cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
				pd.show();
			};
		};

		dataList = (ListView) findViewById(R.id.authorBookList);

		tv_authorName = (TextView) findViewById(R.id.authorName);
		tv_authorDetails = (TextView) findViewById(R.id.authorDetails);

		viewAuthorInfoBtn = (Button) findViewById(R.id.viewAuthorInfo);

		viewAuthorInfoBtn.setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if ("".equals(authorName)) {
							return;
						}
						if ("".equals(authorDetails)) {
							return;
						}
						/*Bundle bundleToSend = new Bundle();
						bundleToSend.putString("authorName", authorName);
						bundleToSend.putString("authorDetails", authorDetails);
						final Intent tmpintent = new Intent(getBaseContext(),
								ShowWriterDetailActivity.class);
						tmpintent.putExtras(bundleToSend);
						startActivity(tmpintent);*/
						final PviAlertDialog pd = new PviAlertDialog(getParent());

						pd.setTitle("作者介绍");
		                pd.setMessage(authorDetails);
		                pd.setButton(DialogInterface.BUTTON_POSITIVE, getResources()
		                        .getString(R.string.my_friend_sure),
		                        new DialogInterface.OnClickListener() {
		                            @Override
		                            public void onClick(DialogInterface dialog,
		                                    int which) {
		                                pd.dismiss();
		                            }
		                        });		               
		                pd.show();
					}
				});			
		
		initTab();

		registerWirelessSearch();
	}
	
	/**
	 * 构建tab
	 */
    private void initTab() {
        final int[] blockUires = { R.id.catalogBtn01, R.id.catalogBtn02,
                R.id.catalogBtn03, R.id.catalogBtn04, R.id.catalogBtn05,
                R.id.catalogBtn06 };

        final ArrayList<HashMap<String, String>> blockInfo = appState
                .getBlockInfo();

        final int countBlock = blockInfo.size();
        for (int i = 0; i < countBlock; i++) {
            final HashMap<String, String> hm = blockInfo.get(i);
            if (hm != null) {
                final String blockname = hm.get("blockName");
                if (blockname != null) {
                    final TextView tvBlock = (TextView) findViewById(blockUires[i]);
                    if (tvBlock != null) {
                        Logger.d(LOG_TAG, "ok, find ui:" + blockUires[i]
                                + ",i:" + i + ";set blockname:" + blockname);
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
                    } else {
                        Logger.d(LOG_TAG, "can not find ui:" + blockUires[i]
                                + ",i:" + i);
                    }
                }
            }else{
                Logger.e(LOG_TAG,"hm is null,i:"+i);
            }
        }
    }

	// 获取并显示信息
    private boolean setContent(String catalogID) {

        HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
        HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

        ahmNamePair.put("authorId", authorID);
        ahmNamePair.put("count", "5");
        
        if (reflash) {
            showNetWorkProcessing2();
            ahmNamePair.put("reflash", REFLASH);
            reflash = false;
        }else{
            showNetWorkProcessing();
        }

        HashMap responseMap = null;
        try {

            responseMap = NetCache.getAuthorInfo(ahmHeaderMap, ahmNamePair);
            // add responseMap.get("result-code") == null && by kizan to check
            // if null

            if (responseMap.get("result-code") == null
                    || !responseMap.get("result-code").toString().contains(
                            "result-code: 0")) {
                return false;
            }
        } catch (HttpException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
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
            Logger.e(LOG_TAG, e.getMessage());
            return false;
        } catch (SAXException e) {
            e.printStackTrace();
            Logger.e(LOG_TAG, e.getMessage());
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            Logger.e(LOG_TAG, e.getMessage());
            return false;
        }
        if (dom != null) {

            Element root = dom.getDocumentElement();
            if (root != null) {
                NodeList nl1 = root.getChildNodes();
                if (nl1 != null && nl1.item(0) != null) {
                    nl1 = nl1.item(0).getChildNodes();

                    int nl1Count = nl1.getLength();

                    books.clear();

                    for (int i = 0; i < nl1Count; i++) {
                        Element el = (Element) nl1.item(i);
                        if (el.getNodeName().equals("AuthorInfo")) {
                            NodeList nlAuthorInfo = el.getChildNodes();
                            int nlAuthorInfo_count = nlAuthorInfo.getLength();
                            for (int z = 0; z < nlAuthorInfo_count; z++) {
                                Element el0 = (Element) nlAuthorInfo.item(z);
                                if (el0.getNodeName().equals("ContentInfoList")) {
                                    NodeList nl2 = el0.getChildNodes();
                                    for (int j = 0; j < nl2.getLength(); j++) {
                                        Element el2 = (Element) nl2.item(j);
                                        NodeList nl3 = el2.getChildNodes();
                                        String[] tempContentInfo = new String[2];
                                        for (int k = 0; k < nl3.getLength(); k++) {
                                            Element el3 = (Element) nl3.item(k);
                                            if (el3.getNodeName().equals(
                                                    "contentID")) {
                                                tempContentInfo[0] = el3
                                                        .getFirstChild()
                                                        .getNodeValue();
                                            } else if (el3.getNodeName()
                                                    .equals("contentName")) {
                                                tempContentInfo[1] = el3
                                                        .getFirstChild()
                                                        .getNodeValue();
                                            }
                                        }
                                        books.add(tempContentInfo);
                                    }

                                } else if (el0.getNodeName().equals(
                                        "PropertyList")) {
                                    Logger.d(this.LOG_TAG, "get PropertyList ");
                                    NodeList nl2 = el0.getChildNodes();
                                    for (int j = 0; j < nl2.getLength(); j++) {
                                        Element el2 = (Element) nl2.item(j);
                                        NodeList nl3 = el2.getChildNodes();
                                        for (int k = 0; k < nl3.getLength(); k++) {
                                            Element el3 = (Element) nl3.item(k);
                                            if (el3.getNodeName().equals(
                                                    "propertyValue")) {
                                                authorDetails = el3
                                                        .getFirstChild()
                                                        .getNodeValue();
                                            }
                                        }
                                    }

                                } else if (el0.getNodeName().equals(
                                        "authorName")) {
                                    authorName = el0.getFirstChild()
                                            .getNodeValue();
                                }
                            }

                        }
                    }
                    
                    mHandler.post(new Runnable(){

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            tv_authorName.setText(authorName);
                            tv_authorDetails.setText(authorDetails);

                            if (authorDetails == null || "".equals(authorDetails)) {
                                findViewById(R.id.ImageView03).setVisibility(View.GONE);
                                tv_authorDetails.setVisibility(View.GONE);
                                viewAuthorInfoBtn.setVisibility(View.GONE);

                            } else {
                                findViewById(R.id.ImageView03).setVisibility(
                                        View.VISIBLE);
                                tv_authorDetails.setVisibility(View.VISIBLE);
                                viewAuthorInfoBtn.setVisibility(View.VISIBLE);
                            }

                            adapter = new WriterBookAdapter(WriterActivity.this, books);
                            dataList.setAdapter(adapter);
                            showMe(WriterActivity.class);
                        }});                   

                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

	@Override
	protected void onResume() {
		super.onResume();
	
		//EPDRefresh.refreshGCOnceFlash();
		
		revIntent = this.getIntent();
		
		if(revIntent==null){
		    Logger.e(LOG_TAG,"system err");
		    return;
		}
		
        revBundle = revIntent.getExtras();
        
        String newAuthorID = "";
        if (revBundle != null) {
            newAuthorID = revBundle.getString("authorID");
        }

        //换了作者id才需要重新获取新数据
        if (newAuthorID != null
                &&
                !"".equals(newAuthorID)
                && !newAuthorID.equals(authorID)
           ) {
            authorID=newAuthorID;
            getPageData();
		    
        }else{
            showMe(WriterActivity.class);
        }
		
	}

	private void getPageData() {
		writerHandler.sendEmptyMessage(GET_DATA);
	}
	
	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {

            String vTag = item.id;
               if (vTag.equals("reflash")) { //
                   reflash = true;
                   getPageData();
                   closePopmenu();
               }else if (vTag.equals("orderbook")) {
                   Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
                   Bundle sndBundle1 = new Bundle();
                   sndBundle1.putString("act",
                   "com.pvi.ap.reader.activity.SubscribeProcess");
                   sndBundle1.putString("subscribeMode", "feedback");
                   intent1.putExtras(sndBundle1);
                   sendBroadcast(intent1);
               }
       
        }};

	@Override
	public OnUiItemClickListener getMenuclick() {
		return this.menuclick;
	}

	static class WriterBookAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private List<String[]> list;
		private Context mContext;

		public WriterBookAdapter(Context context, List<String[]> list) {
			super();
			this.inflater = LayoutInflater.from(context);
			mContext = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {

			    int skinID = 1;
			    final GlobalVar appState = ((GlobalVar) mContext.getApplicationContext());
			    convertView = inflater.inflate(
							R.layout.author_book_list_item, null);				

				holder = new ViewHolder();

				holder.tv_title = (TextView) convertView
						.findViewById(R.id.bookname);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			String name = list.get(position)[1];

			holder.tv_title.setText("《" + name + "》 ");

			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent tmpIntent = new Intent(
							MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("act",
							"com.pvi.ap.reader.activity.BookSummaryActivity");
					bundleToSend.putString("contentID", list.get(position)[0]);
					bundleToSend.putString("pviapfStatusTip", "数据加载中，请稍候...");
					tmpIntent.putExtras(bundleToSend);
					v.getContext().sendBroadcast(tmpIntent);
				}
			});
			convertView.setOnTouchListener( new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(event.getAction() == MotionEvent.ACTION_UP){
						v.performClick();
						v.requestFocus();
					}
					return false;
				}
			});

			return convertView;
		}

		static class ViewHolder {
			public TextView tv_title;
		}
	}

}
