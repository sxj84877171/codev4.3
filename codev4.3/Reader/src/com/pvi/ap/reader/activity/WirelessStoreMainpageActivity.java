package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
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
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.common.data_btn;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;
import com.pvi.ap.reader.view.WSMainView;

public class WirelessStoreMainpageActivity extends PviActivity {
	
	WSMainView mainView;
	private String[] tag = new String[6];
	private int catalogItem = 10;
	private int recommendItem = 5;
	private int rankingItem = 6;
	private int authorItem = 3;
	private int goodsItem = 6;
	private int newsItem = 5;
	Thread updateImage;

	private Handler setImageHandler = new Handler();

	private Runnable showImage= new Runnable()
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			for (int i = 0; i < recommendItem; i++) {
				if (recommendDataBtn.dataBitmap[i] != null) {
					//recommendImage[i].setImageBitmap(recommendDataBtn.dataBitmap[i]);
				} 
			}
		}

	};

	private Handler getimageHandler = new Handler() {
		public void handleMessage(Message msg) {
			updateImage = new Thread() {
				public void run() {
					Looper.prepare();
					for (int i = 0; i < recommendItem; i++) {
						if (recommendDataBtn.dataPrimary[i] != null
								&& !recommendDataBtn.bitmapOK[i]) {
							try
							{
								recommendDataBtn.dataBitmap[i] = NetCache
								.GetNetImage(recommendDataBtn.dataPrimaryBtn[i]);
							}
							catch(Exception e)
							{
								recommendDataBtn.dataBitmap[i]=null;
							}
							catch(OutOfMemoryError e)
							{
								recommendDataBtn.dataBitmap[i]=null;
							}
						}
					}
					setImageHandler.post(showImage);
					return;
				}
			};
			updateImage.start();
		};
	};
	//	private String[] imageURL = new String[7];
	private data_btn GoodsDataBtn = new data_btn();     //"精品专栏"信息
	private data_btn recommendDataBtn = new data_btn(); //编辑推荐信息
	private data_btn authorDataBtn = new data_btn();    //名家名作
	private data_btn rankingDataBtn = new data_btn();   //热门排行
	private data_btn catalogDataBtn = new data_btn();   //分类栏目
	private data_btn newsDataBtn = new data_btn();     //包月书包或最新资讯


	private TextView[] fakeTab = new TextView[6];
	private TextView[] title = new TextView[6];
	private String[] dataTitle = new String[6];


	private Handler retryHandler;
	private Handler uiHandler;

	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {

            String vTag = "";
            vTag = item.id;
            closePopmenu();
            if (vTag.equals("orderbook")) {
                Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
                Bundle sndBundle1 = new Bundle();
                sndBundle1.putString("act",
                "com.pvi.ap.reader.activity.SubscribeProcess");
                sndBundle1.putString("subscribeMode", "feedback");
                intent1.putExtras(sndBundle1);
                sendBroadcast(intent1);
            }
            else if(vTag.equals("reflash"))
            {
                showMessage("一键更新中...");
                Thread checkUpdate = new Thread() {
                    public void run() {

                        if (getData(true) == 0) {
                            refrashMainView();
                            return;
                        } else {
                            retryHandler.sendEmptyMessage(0);
                            //                          Log.i(" retryHandler", "retryHandler is op");
                            return;
                        }
                    }
                };
                checkUpdate.start();
            }
        
        }};




		private void bindUI() {


			if(deviceType==1)
			{
				

			}
		}

		@Override
		public OnUiItemClickListener getMenuclick() {

			return this.menuclick;
		}

		private void setWidgetNum() {
			catalogItem = 10;
			recommendItem = 5;
			rankingItem = 6;
			authorItem = 3;
			goodsItem = 4;
			newsItem = 4;
		}
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			setContentView(R.layout.wireless_store_mainpage);
			mainView = (WSMainView)findViewById(R.id.mainview);
			setWidgetNum();
			bindUI();
			super.onCreate(savedInstanceState);

			uiHandler = new Handler();

			retryHandler = new Handler() {
				public void handleMessage(Message msg) {
					
					final PviAlertDialog pd = new PviAlertDialog(getParent());
					pd.setTitle(R.string.kyleHint02);
					pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
							new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							pd.dismiss();
							Thread checkUpdate = new Thread() {
								public void run() {
									Looper.prepare();
									onResume();
								}
							};
							checkUpdate.start();
						}
					});
					pd.setButton(DialogInterface.BUTTON_NEUTRAL, "取消",
							new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							pd.dismiss();
							Intent intent = new Intent(WirelessTabActivity.BACK);
							Bundle bundle = new Bundle();
							bundle.putString("startType", "allwaysCreate");
							intent.putExtras(bundle);
							sendBroadcast(intent);
						}

					});
					pd.show();

				};
			};
			super.registerWirelessSearch();

		}

		protected int getData(boolean reflash) {
			try
			{
				HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
				HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
				HashMap responseMap = null;
				ahmNamePair.put("catalogId", "1");
				if(reflash)
				{
					ahmNamePair.put("reflash", "OnlyIF");
				}
				try {
					responseMap = NetCache.getCatalogHomePage(ahmHeaderMap,
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
				String rspC = responseMap.get("result-code").toString();
				if (!rspC.contains("result-code: 0")) {
					return 3;
				}
				byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
				//				if (strGCH.substring(0, 10).contains("Exception")) {
				//					return 1;
				//				}
				//				if (!strGCH.substring(0, 19).contains("0000")) {
				//					return 3;
				//				}
				//				InputStream is = new ByteArrayInputStream(strGCH.substring(20)
				//						.getBytes());

				//				DocumentBuilderFactory dbfactory = DocumentBuilderFactory
				//				.newInstance();
				//				DocumentBuilder db = dbfactory.newDocumentBuilder();
				//				Document dom = db.parse(is);
				//				try {
				//					System.out.println("返回的XML为：");
				//					System.out.println(CPManagerUtil
				//							.getStringFrombyteArray(responseBody));
				//				} catch (UnsupportedEncodingException e) {
				//					e.printStackTrace();
				//				}

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
				Element rootele = null;
				rootele = dom.getDocumentElement();
				NodeList totalNl = rootele.getElementsByTagName("Block");

				int temp = 0;
				for (int i = 0; i < totalNl.getLength(); i++) {
					Element entry = (Element) totalNl.item(i);
					NodeList blockNamel = entry.getElementsByTagName("blockName");
					String blockname = blockNamel.item(0).getFirstChild()
					.getNodeValue();
					tag[temp] = blockname;
					temp ++;
					if ("精品专栏".equals(blockname)) {
						dataTitle[3] = "精品专栏";
						resolveGoods(entry);
						//						Logger.d("new store mainpage", "resolve goods ok");
					}
					else if ("包月书包".equals(blockname)) {
						dataTitle[5] = "包月书包";
						resolveNews2(entry);
						//						Logger.d("new store mainpage", "resolve goods ok");
					}
					else if ("最新资讯".equals(blockname)) {
						dataTitle[5] = "最新资讯";
						resolveNews(entry);
						//						Logger.d("new store mainpage", "resolve news ok");
					}
					else if ("名家名作".equals(blockname)) {
						dataTitle[4] = "名家名作";
						resolveAuthor(entry);
						//						Logger.d("new store mainpage", "resolve author ok");
					}
					else if ("编辑推荐".equals(blockname)) {
						dataTitle[1] = "编辑推荐";
						resolveRecommend(entry);
						//						Logger.d("new store mainpage", "resolve recommend ok");
					}
					else if ("热门排行".equals(blockname)) {
						dataTitle[2] = "热门排行";
						resolveRanking(entry);
						//						Logger.d("new store mainpage", "resolve rank ok");
					}
					else if ("分类栏目".equals(blockname)) {
						dataTitle[0] = "分类栏目";
						resolveCatalog(entry);
						//						Logger.d("new store mainpage", "resolve catalog ok");
					}
					else
					{
						temp--;
					}
				}
			} catch (Exception e) {
			    e.printStackTrace();
				Logger.e("get data in new store error", e.toString());
				return 2;
			}
			catch(OutOfMemoryError e)
			{
			    e.printStackTrace();
				Intent intent = new Intent(WirelessTabActivity.BACK);
				Bundle bundle = new Bundle();
				intent.putExtras(bundle);
				sendBroadcast(intent);
				return 3;
			}
			return 0;
		}

		private void resolveRanking(Element paraentry) {
			NodeList nl = paraentry.getElementsByTagName("BlockContentInfo");
			// loop item:content item
			for (int i = 0; i < nl.getLength() && i < rankingItem; i++) {
				Element entry = (Element) nl.item(i);

				NodeList nl0 = entry.getElementsByTagName("blockContentID");
				rankingDataBtn.dataId[i] = nl0.item(0).getFirstChild()
				.getNodeValue();

				NodeList nl2 = entry.getElementsByTagName("BlockContentParam");
				// loop item:param
				for (int j = 0; j < nl2.getLength(); j++) {
					Element eparam1 = (Element) nl2.item(j);
					NodeList nl3 = eparam1.getElementsByTagName("name");
					String name = nl3.item(0).getFirstChild().getNodeValue();
					if (name.equals("rankType")) {
						NodeList nl4 = eparam1.getElementsByTagName("value");
						rankingDataBtn.dataPrimary[i] = nl4.item(0).getFirstChild()
						.getNodeValue();
						// primary[i].setText(nl4.item(0).getFirstChild().getNodeValue());
					}

				}// end of param loop

			}// end of content item loop

		}

		private void resolveRecommend(Element paraentry) {
			NodeList nl = paraentry.getElementsByTagName("BlockContentInfo");
			// loop item:content item
			for (int i = 0; i < nl.getLength() && i < recommendItem; i++) {
				Element entry = (Element) nl.item(i);
				NodeList nl2 = entry.getElementsByTagName("BlockContentParam");
				// loop item:param
				for (int j = 0; j < nl2.getLength(); j++) {
					Element eparam1 = (Element) nl2.item(j);
					NodeList nl3 = eparam1.getElementsByTagName("name");
					String name = nl3.item(0).getFirstChild().getNodeValue();
					if (name.equals("contentName")) {
						NodeList nl4 = eparam1.getElementsByTagName("value");
						if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
		                        .getFirstChild()!=null)	{
						
						    recommendDataBtn.dataPrimary[i] = nl4.item(0).getFirstChild().getNodeValue();
						}else{
						    recommendDataBtn.dataPrimary[i] = "";
						}
					}
					//				if (name.equals("authorName")) {
					//					NodeList nl4 = eparam1.getElementsByTagName("value");
					//					recommendDataBtn.dataSlave[i] = getResources().getString(
					//							R.string.kyleHint15)
					//							+ nl4.item(0).getFirstChild().getNodeValue();
					//					// slave[i].setText(getResources().getString(R.string.kyleHint15)
					//					// + nl4.item(0).getFirstChild().getNodeValue());
					//					continue;
					//				}
					if (name.equals("contentID")) {
						NodeList nl4 = eparam1.getElementsByTagName("value");
						if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
                                .getFirstChild()!=null) {
						final String tmpStr = nl4.item(0).getFirstChild()
						.getNodeValue();
						recommendDataBtn.dataId[i] = tmpStr;
						}else{
						    recommendDataBtn.dataId[i] = "";
						}
					}
					if (name.equals("smallLogo")) {

							NodeList nl4 = eparam1.getElementsByTagName("value");
							if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
	                                .getFirstChild()!=null) {
							final String tmpStr = nl4.item(0).getFirstChild()
							.getNodeValue();
							recommendDataBtn.dataPrimaryBtn[i] = Config
							.getString("CPC_BASE_URL")
							+ tmpStr;
							}else{
							    recommendDataBtn.dataPrimaryBtn[i] = null;
							}
					}
				}// end of param loop

			}// end of content item loop

		}

		private void resolveAuthor(Element paraentry) {
			NodeList nl = paraentry.getElementsByTagName("BlockContentInfo");
			// loop item:content item
			for (int i = 0; i < nl.getLength() && i < authorItem; i++) {
				Element entry = (Element) nl.item(i);
				NodeList forAuthorID = entry.getElementsByTagName("blockContentID");
				final String tmpStr = forAuthorID.item(0).getFirstChild()
				.getNodeValue();
				authorDataBtn.dataId[i] = tmpStr;
				NodeList nl2 = entry.getElementsByTagName("BlockContentParam");
				// loop item:param
				for (int j = 0; j < nl2.getLength(); j++) {
					Element eparam1 = (Element) nl2.item(j);
					NodeList nl3 = eparam1.getElementsByTagName("name");
					String name = nl3.item(0).getFirstChild().getNodeValue();
					if (name.equals("contentName")) {
						NodeList nl4 = eparam1.getElementsByTagName("value");
						if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
                                .getFirstChild()!=null) {
						authorDataBtn.dataSlave[i] = nl4.item(0).getFirstChild().getNodeValue();
						}else{
						    authorDataBtn.dataSlave[i] = "";
						}
					}
					if (name.equals("authorName")) {
						NodeList nl4 = eparam1.getElementsByTagName("value");
						if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
                                .getFirstChild()!=null) {
						authorDataBtn.dataPrimary[i] = nl4.item(0).getFirstChild()
						.getNodeValue();
						}else{
						    authorDataBtn.dataPrimary[i] = "";
						}
					}
					if (name.equals("contentId")) {
						NodeList nl4 = eparam1.getElementsByTagName("value");
						if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
                                .getFirstChild()!=null) {
						authorDataBtn.dataPrimaryBtn[i] = nl4.item(0).getFirstChild()
						.getNodeValue();
						}else{
						    authorDataBtn.dataPrimaryBtn[i] = "";
						}
					}
				}// end of param loop
			}// end of content item loop

		}

		private void resolveNews(Element paraentry) {
			NodeList nl = paraentry.getElementsByTagName("BlockContentInfo");
			// loop item:content item
			for (int i = 0; i < nl.getLength() && i < newsItem; i++) {
				Element entry = (Element) nl.item(i);

				NodeList nl0 = entry.getElementsByTagName("blockContentID");
				newsDataBtn.dataId[i] = nl0.item(0).getFirstChild().getNodeValue();

				NodeList nl2 = entry.getElementsByTagName("BlockContentParam");
				// loop item:param
				for (int j = 0; j < nl2.getLength(); j++) {
					Element eparam1 = (Element) nl2.item(j);
					NodeList nl3 = eparam1.getElementsByTagName("name");
					String name = nl3.item(0).getFirstChild().getNodeValue();
					if (name.equals("bookNewsName")) {
						NodeList nl4 = eparam1.getElementsByTagName("value");
						if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
                                .getFirstChild()!=null) {
						newsDataBtn.dataPrimary[i] = nl4.item(0).getFirstChild()
						.getNodeValue();
						}else{
						    newsDataBtn.dataPrimary[i] = "";
						}
					}
					if (name.equals("publishTime")) {
						NodeList nl4 = eparam1.getElementsByTagName("value");
						if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
                                .getFirstChild()!=null) {
						    newsDataBtn.dataSlave[i] = nl4.item(0).getFirstChild().getNodeValue();
						    if(newsDataBtn.dataSlave[i].length()>9){
						    newsDataBtn.dataSlave[i] = newsDataBtn.dataSlave[i].substring(0, 10);
						    }
						}else{
						    newsDataBtn.dataSlave[i] = "";
						}
					}
				}// end of param loop

			}// end of content item loop

		}

		private void resolveNews2(Element paraentry) {
			NodeList nl1 = paraentry.getElementsByTagName("BlockContentInfo");

			// loop item:content item
			for (int i = 0; i < goodsItem && i < nl1.getLength(); i++) {
				Element entry = (Element) nl1.item(i);
				if (entry != null) {
					NodeList nl0 = entry.getElementsByTagName("blockContentID");
					newsDataBtn.dataId[i] = nl0.item(0).getFirstChild()
					.getNodeValue();

					NodeList nl2 = entry.getElementsByTagName("BlockContentParam");
					int nl2Count = nl2.getLength();
					// loop item:param
					for (int j = 0; j < nl2Count; j++) {
						Element eparam1 = (Element) nl2.item(j);
						NodeList nl3 = eparam1.getElementsByTagName("name");
						String name = nl3.item(0).getFirstChild().getNodeValue();
						if (name.equals("catalogName")) {
							NodeList nl4 = eparam1.getElementsByTagName("value");
							if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
                                    .getFirstChild()!=null) {
							newsDataBtn.dataPrimary[i] = nl4.item(0)
							.getFirstChild().getNodeValue();
							}else{
							    newsDataBtn.dataPrimary[i] = "";
							}
						}
						if (name.equals("bookNumber")) {
							NodeList nl4 = eparam1.getElementsByTagName("value");
							if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
                                    .getFirstChild()!=null) {
							    newsDataBtn.dataSlave[i] = nl4.item(0).getFirstChild()
							.getNodeValue()
							+ getResources().getString(R.string.kyleHint14);
							}else{
							    newsDataBtn.dataSlave[i] = "";
							}
						}
						if (name.equals("catalogType")) {
							NodeList nl4 = eparam1.getElementsByTagName("value");
							if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
                                    .getFirstChild()!=null) {
							String tmp = nl4.item(0).getFirstChild().getNodeValue();
							if (tmp.equals("3") || tmp.equals("4")
									|| tmp.equals("5")) {
								newsDataBtn.dataPrimaryBtn[i] = "true";
							} else {
								newsDataBtn.dataPrimaryBtn[i] = "false";
							}

							}else{
							    newsDataBtn.dataPrimaryBtn[i] = "false";
							}
						}
					}// end of param loop
				}

			}// end of content item loop

		}

		private void resolveGoods(Element paraentry) {

			NodeList nl1 = paraentry.getElementsByTagName("BlockContentInfo");

			// loop item:content item
			for (int i = 0; i < (goodsItem< nl1.getLength()?goodsItem:nl1.getLength()); i++) {
				Element entry = (Element) nl1.item(i);
				if (entry != null) {
					NodeList nl0 = entry.getElementsByTagName("blockContentID");
					GoodsDataBtn.dataId[i] = nl0.item(0).getFirstChild()
					.getNodeValue();

					NodeList nl2 = entry.getElementsByTagName("BlockContentParam");
					int nl2Count = nl2.getLength();
					// loop item:param
					for (int j = 0; j < nl2Count; j++) {
						Element eparam1 = (Element) nl2.item(j);
						NodeList nl3 = eparam1.getElementsByTagName("name");
						String name = nl3.item(0).getFirstChild().getNodeValue();
						if (name.equals("catalogName")) {
							NodeList nl4 = eparam1.getElementsByTagName("value");
							if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
                                    .getFirstChild()!=null) {
							GoodsDataBtn.dataPrimary[i] = nl4.item(0)
							.getFirstChild().getNodeValue();
							}else{
							    GoodsDataBtn.dataPrimary[i] = "";
							}
						}
						if (name.equals("bookNumber")) {
							NodeList nl4 = eparam1.getElementsByTagName("value");
							if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
                                    .getFirstChild()!=null) {
							GoodsDataBtn.dataSlave[i] = nl4.item(0).getFirstChild()
							.getNodeValue()
							+ getResources().getString(R.string.kyleHint14);
							}else{
							    GoodsDataBtn.dataSlave[i] = "";
							}
						}
						if (name.equals("catalogType")) {
							NodeList nl4 = eparam1.getElementsByTagName("value");
							if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
                                    .getFirstChild()!=null) {
							String tmp = nl4.item(0).getFirstChild().getNodeValue();
							if (tmp.equals("3") || tmp.equals("4")
									|| tmp.equals("5")) {
								GoodsDataBtn.dataPrimaryBtn[i] = "true";
							} else {
								GoodsDataBtn.dataPrimaryBtn[i] = "false";
							}
							}else{
							    GoodsDataBtn.dataPrimaryBtn[i] = "false";
							}

						}
					}// end of param loop
				}

			}// end of content item loop

		}

		private void resolveCatalog(Element paraentry) {
			NodeList nl1 = paraentry.getElementsByTagName("BlockContentInfo");

			//			Log.i("Reader", "resolveCatalog BlockContentInfo num : " + nl1.getLength());
			// loop item:content item
			for (int i = 0; i < ((catalogItem < nl1.getLength())?catalogItem:nl1.getLength()); i++) {
				Element entry = (Element) nl1.item(i);
				if (entry != null) {
					NodeList nl0 = entry.getElementsByTagName("blockContentID");
					catalogDataBtn.dataId[i] = nl0.item(0).getFirstChild()                  //栏目的ContentID
					.getNodeValue();

					NodeList nl2 = entry.getElementsByTagName("BlockContentParam");
					int nl2Count = nl2.getLength();
					// loop item:param
					for (int j = 0; j < nl2Count; j++) {
						Element eparam1 = (Element) nl2.item(j);
						NodeList nl3 = eparam1.getElementsByTagName("name");
						String name = nl3.item(0).getFirstChild().getNodeValue();
						if (name.equals("catalogName")) {
							NodeList nl4 = eparam1.getElementsByTagName("value");
							if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
                                    .getFirstChild()!=null) {
							catalogDataBtn.dataPrimary[i] = nl4.item(0)                           //栏目名称
							.getFirstChild().getNodeValue();
							}else{
							    catalogDataBtn.dataPrimary[i] = "";
							}
						}
						if (name.equals("bookNumber")) {
							NodeList nl4 = eparam1.getElementsByTagName("value");
							if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
                                    .getFirstChild()!=null) {
							catalogDataBtn.dataSlave[i] = nl4.item(0)                             //书籍数量
							.getFirstChild().getNodeValue()
							+ getResources().getString(R.string.kyleHint14);                       
							}else{
							    catalogDataBtn.dataSlave[i] = "";
							}
						}
						if (name.equals("catalogType")) {
							NodeList nl4 = eparam1.getElementsByTagName("value");
							if(nl4!=null&&nl4.item(0)!=null&&nl4.item(0)
                                    .getFirstChild()!=null) {
							String tmp = nl4.item(0).getFirstChild().getNodeValue();
							catalogDataBtn.dataPrimaryBtn[i] = tmp;                                //栏目类型
							}else{
							    catalogDataBtn.dataPrimaryBtn[i] = "";
							}
						}
					}// end of param loop
				}

			}// end of content item loop

		}
		
		
		

		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();

			Thread checkUpdate = new Thread() {
				public void run() {

					if (getData(false) == 0) {
						refrashMainView();
						return;
					} else {
						retryHandler.sendEmptyMessage(0);
						return;
					}
				}
			};
			checkUpdate.start();

		}
		private void showme()
		{
			super.showMe(getClass());
			super.hideTip();
			if(deviceType==1){
//				mainView.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//				this.getParent().getWindow().getDecorView().getRootView().postInvalidate(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
			}
			SharedPreferences settings = getSharedPreferences(
					Constants.configFileName, 0);
			String state = settings.getString(WelcomeSetActivity.STATE, "");
			if (state.equals("open")) {
				long tmpTimeNow = System.currentTimeMillis();
				long tmpTimeLast = Long.parseLong(settings.getString("STORE_WELCOME_TIME","0"));
				Logger.e("WirelessStoreMainPage", "tmpTimeLast:" + tmpTimeLast +" Sub:"+ String.valueOf(tmpTimeNow - tmpTimeLast));
				if ((tmpTimeLast==0)||((tmpTimeNow - tmpTimeLast) > 12 * 60 * 60 * 1000)) {
					Bundle bundleToSend1 = new Bundle();
					final Intent tmpintent = new Intent(getBaseContext(),
							WirelessStoreWelcomeActivity.class);
					tmpintent.putExtras(bundleToSend1);
					startActivity(tmpintent);
				}
			}
		}
		
		public void refrashMainView(){
			mainView.setDataTitle(dataTitle);
			mainView.setGoodsDataBtn(GoodsDataBtn);
			mainView.setRecommendDataBtn(recommendDataBtn);
			mainView.setAuthorDataBtn(authorDataBtn);
			mainView.setRankingDataBtn(rankingDataBtn);
			mainView.setCatalogDataBtn(catalogDataBtn);
			mainView.setNewsDataBtn(newsDataBtn);
			mainView.setData();
			mainView.postInvalidate();
			showme();
			
		}
		public static String STORE_WELCOME_TIME = "STORE_WELCOME_TIME";
}
