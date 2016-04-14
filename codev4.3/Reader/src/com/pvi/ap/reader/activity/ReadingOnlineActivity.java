package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.InactiveFunction;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.SubscribeViewBuilder;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.Bookmark;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;
import com.pvi.ap.reader.data.external.manager.LeafNode;
import com.pvi.ap.reader.data.external.manager.XMLUtil;
import com.pvi.ap.reader.data.external.manager.XmlElement;
import com.pvi.ap.reader.external.txt.AddNote;
import com.pvi.ap.reader.external.txt.ReadSetView;


/**
 * 在线阅读
 * @author 刘奇赞
 * @author rd040 马中庆
 *
 */

public class ReadingOnlineActivity extends PviActivity {

	private String TAG = "ReadingOnlineActivity";
	private Context mContext = ReadingOnlineActivity.this;
	private TextView chapter = null;
	private TextView content = null;
	private float m_size = 16;// 字体大小
	private float lineSpac = 5;// 保存行间距
	private Intent revintent = null;
	private Bundle revBundle = null;

	private String fascicleID = "";
	private String contentID = "";
	private String chapterID = "";
	private String chaptername = "";
	private String position = "";
	private String fontsize = "";

	private String Content = "";
	private String feedescription = "";

	private boolean reflash = false;
	public static final String REFLASH = "CurrentBook";

	public static final int PD1 = 101;
	public static final int PD2 = 102;
	public static final int PD3 = 103;


	public static final int P11 = 1011;
	public static final int P12 = 1012;
	public static final int P13 = 1013;

	// private PopupWindow popmenu;
	// private View popmenuView;

	private boolean isInternalSwitch = false;
	//	SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	private String preChapterID = "";
	private String preChapterName = "";
	private String prefascicleID = "";
	private String nextChapterID = "";
	private String nextChapterName = "";
	private String nextfascicleID = "";

	private int currentPage = 1;
	private int totalPage = 0;

	private PviAlertDialog pd;
	private Handler mHandler = new H();
	private CharSequence charSequence;
	Thread getdatathread = null;

	private String ispagechanging = "";
	private String productID = null;
	private HashMap<String, Object> bookinfo = new HashMap<String, Object>();
	private int chapterIdLength;// 当前一章的长度
	private String fasciclefeeDescription = "";
	private String fascicleProductId = "";
	private String chapterchange = "";
	private boolean issubscribehint = true;
	private Thread thread = null;
	private boolean first = false ;

	private List<Integer> total_pages;
	Spanned spanned;

	private int pageCounter = 0;//翻页计数器

	// add by kizan for search function
	// only search this chapter
	private Button bSearch;
	private Button closeBtn;
	private View serachs;

	private EditText look;
	//	private String mSearchdata = null;
	//	private boolean serchBool = false;
	protected boolean inChapterChanging = false;
	
	private GlobalVar app;


	@Override
	public OnUiItemClickListener getMenuclick() {
		// TODO Auto-generated method stub
		return this.menuclick;
	}

	private void SubscribeChapter(String contentID, String chapterID,
			String productID) {
		String StrSC = SubscribeProcess.network("subscribeContent", contentID,
				chapterID, productID, null);
		if (StrSC.substring(0, 10).contains("Exception")) {// 订购时网络异常
			Bundle temp = new Bundle();
			temp.putString("chapterID", chapterID);
			temp.putString("chapterName", chaptername);
			temp.putString("productID", productID);
			Message msgtemp = new Message();
			msgtemp.what = 5;
			msgtemp.setData(temp);
			// mHandler.sendEmptyMessage(msg);
			mHandler.sendMessage(msgtemp);
			return;
		}
		if (StrSC.substring(0, 19).contains("0000")
				|| StrSC.substring(0, 19).contains("3159")) {
			// 订购成功的代码

			if (chapterID == this.preChapterID) {
				this.chaptername = preChapterName;
			} else if (chapterID == this.nextChapterID) {
				this.chaptername = this.nextChapterName;
			}
			this.chapterID = chapterID;
			
			
            //重设intent
            revBundle.putString("ChapterID",chapterID);
            revBundle.putString("ContentID",contentID);
            final Intent intent = getIntent();
            intent.putExtras(revBundle);
            setIntent(intent);
			
			onResume();
			return;
		} else {
			PviAlertDialog builder2 = new PviAlertDialog(getParent());
			builder2.setMessage("订购失败");

			builder2.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					;
				}
			});
			builder2.show();
		}
	}

	private class H extends Handler {
		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			super.handleMessage(msg);
			switch (msg.what) {
			case 0:// 执行 获取用户数据
				hiddenDialog();
				break;
			case 2:
				hiddenDialog();
				if (chapterchange.equals("")
						|| (issubscribehint && (chapterchange
								.equals("prechapter") || chapterchange
								.equals("nextchapter")))) {
					final PviAlertDialog builder = new PviAlertDialog(
							ReadingOnlineActivity.this);
					builder.setTitle("温馨提示");
					builder.setMessage(feedescription);
					builder.setButton(DialogInterface.BUTTON_POSITIVE, "确认订购",
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							issubscribehint = false;
							if (chapterchange.equals("")) {
								SubscribeChapter(contentID, chapterID,
										productID);
							} else if (chapterchange
									.equals("prechapter")) {
								SubscribeChapter(contentID,
										preChapterID, productID);
							} else if (chapterchange
									.equals("nextchapter")) {
								SubscribeChapter(contentID,
										nextChapterID, productID);
							}
						}
					});
					builder.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
							new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub

						}
					});
					builder.show();
					return;
				} else if (chapterchange.equals("prechapter")) {
					// 点击上一章
					SubscribeChapter(contentID, preChapterID, productID);
				} else if (chapterchange.equals("nextchapter")) {
					// 点击下一章
					SubscribeChapter(contentID, nextChapterID, productID);
				}

				return;
			case 1:
				hiddenDialog();
				final PviAlertDialog sockitAlertDialog = new PviAlertDialog(
						ReadingOnlineActivity.this);
				sockitAlertDialog.setTitle("温馨提示");
				sockitAlertDialog.setMessage("联网获取章节内容失败！");
				sockitAlertDialog.setButton(getResources().getString(
						R.string.OK), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						dialog.cancel();
						sockitAlertDialog.dismiss();
						;
					}
				});
				sockitAlertDialog.show();
				return;
			case 3:
				// 订购章节
				hiddenDialog();
				if (chapterchange.equals("")) {
					final PviAlertDialog builder = new PviAlertDialog(
							ReadingOnlineActivity.this);
					builder.setTitle("温馨提示");
					builder.setMessage(feedescription);
					builder.setButton(DialogInterface.BUTTON_POSITIVE, "按章订购",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							//

							SubscribeChapter(contentID, chapterID,
									productID);
						}
					});
					builder.setButton(DialogInterface.BUTTON_NEGATIVE, "按分册订购",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							/*
							 * 处理按分册订购
							 */
							Intent intent1 = new Intent(
									MainpageActivity.START_ACTIVITY);
							Bundle sndBundle1 = new Bundle();
							sndBundle1
							.putString("act",
							"com.pvi.ap.reader.activity.SubscribeProcess");
							sndBundle1
							.putString("contentID", contentID);
							sndBundle1.putString("subscribeMode",
							"fascicleList");
							sndBundle1.putString("ProductID",
									fascicleProductId);
							sndBundle1
							.putString("fascicle", fascicleID);
							sndBundle1.putString("chargeTip", "温馨提示");
							sndBundle1.putString("catalogID",
									fasciclefeeDescription);
							intent1.putExtras(sndBundle1);
							sendBroadcast(intent1);
							finish();
							return;
						}
					});
					builder.show();
					return;
				} else if (chapterchange.equals("prechapter")) {
					// 点击上一章
					hiddenDialog();
					SubscribeChapter(contentID, preChapterID, productID);
				} else if (chapterchange.equals("nextchapter")) {
					// 点击下一章
					hiddenDialog();
					SubscribeChapter(contentID, nextChapterID, productID);
				}
				return;
			case 4:
			    Logger.d(TAG,"call OnResume()");
				hiddenDialog();
				//EPDRefresh.refreshGCOnceFlash();
				
				//重设intent
				revBundle.putString("ChapterID",chapterID);
	            revBundle.putString("ContentID",contentID);
	            final Intent intent = getIntent();
	            intent.putExtras(revBundle);
	            setIntent(intent);
				
				onResume();
				return;
			case 5:

				Bundle temp = msg.getData();
				final String tempchapterid = temp.getString("chapterID");
				final String tempchaptername = temp.getString("chapterName");
				final String tempproductid = temp.getString("productID");
				final PviAlertDialog pd = new PviAlertDialog(getParent());
				pd.setTitle(R.string.kyleHint02);
				pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
						new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {

						SubscribeChapter(contentID, tempchapterid,
								productID);
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
				break;

			case PD1://提示框：订购确认
				pd1(msg);
				break;

			case PD2://提示框：通用提示信息，可关闭，一个确定按钮，点确定无操作
				pd2(msg);
				break;

			case PD3://提示框：提示用户到摘要页预订更新
				pd3();
				break;


			case P11://过程：订购本章/书
				new Thread() {
					public void run() {
						p11();
					}
				}.start();
				break;	
			case P12://过程：订购后 连续指定章
				new Thread() {
					public void run() {
						p12();
					}
				}.start();
				break;
			case P13://过程：订购分册
				new Thread() {
					public void run() {
						p13();
					}
				}.start();
				break;
			case 455:
				page_finished();
				break ;

			default:
				;
			}
		}

	}

	private void hiddenDialog() {
		if (pd != null)
			pd.dismiss();

	}

	public void pd1(Message msg) {
		if(msg==null){
			return;
		}
		String chargeMode = "";        //计费类型
		String feeDescription = "";         //本章/书 的 资费信息
		String feeDescription2 = "";        //包月资费信息

		String chaptersFeeDescription = ""; //对于按章订购的，包含连续订购X章的资费信息
		String chaptersNum = "";            //章数

		String fascicleID = "";             //分册ID
		String fascicleProductID = "";      //产品ID
		String fascicleFeeDescription = "";         //资费描述

		String xml = "";

		Bundle bd = msg.getData();
		if(bd!=null){
			Logger.d(TAG,bd.toString());
			/*feeDescription = bd.getString("feeDescription");
            feeDescription2 = bd.getString("feeDescription2");
            chaptersFeeDescription = bd.getString("chaptersFeeDescription");
            chaptersNum = bd.getString("chaptersNum");*/
			xml = bd.getString("xml");
		}

		feeDescription = SubscribeProcess.getFirstValue(xml,"feeDescription");
		//feeDescription2 = bd.getString("feeDescription2");
		chaptersFeeDescription = SubscribeProcess.getFirstValue(xml,"chaptersFeeDescription");
		chaptersNum = SubscribeProcess.getFirstValue(xml,"chaptersNumber");

		chargeMode = SubscribeProcess.getFirstValue(xml,"chargeMode");



		pd = new PviAlertDialog(getParent());
		SubscribeViewBuilder sv = new SubscribeViewBuilder(mContext);
		//订购本章/书       
		sv.tvTop.setText(feeDescription);
		sv.tvTop.setVisibility(View.VISIBLE);
		if("1".equals(chargeMode)){
			sv.bTopLeft.setText("确认订购");
		}else if("2".equals(chargeMode)){
			sv.bTopLeft.setText("确认并订购本章");
		}else{
			sv.bTopLeft.setText("确认订购"); 
		}

		sv.bTopLeft.setVisibility(View.VISIBLE);
		sv.bTopRight.setVisibility(View.VISIBLE);
		sv.bTopLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pd.dismiss();
				//执行过程：订购本章/书
				mHandler.sendEmptyMessage(P11);
			}
		});

		// 如果 设置了 “订购后X章”  显示相应UI控件

		if (chaptersFeeDescription!=null&&!"".equals(chaptersFeeDescription)) {
			sv.tvMiddle.setText(chaptersFeeDescription);
			sv.tvMiddle.setVisibility(View.VISIBLE);
			sv.bMiddleLeft.setText("确认订购");
			sv.bMiddleLeft.setVisibility(View.VISIBLE);
			sv.bTopRight.setVisibility(View.INVISIBLE);
			sv.bMiddleRight.setVisibility(View.VISIBLE);
			sv.bMiddleLeft.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					pd.dismiss();
					//执行过程：订购后X章
					mHandler.sendEmptyMessage(P12);
				}
			});

		} 

		// 如果 有分册  显示相应UI信息（这种情况，将不会出现“连续订购N章”）  
		fascicleID = SubscribeProcess.getFirstValue(xml,"fascicleID");
		if (fascicleID!=null&&!"".equals(fascicleID)) {

			fascicleProductID = SubscribeProcess.getSecondValue(xml,"productID");
			fascicleFeeDescription = SubscribeProcess.getSecondValue(xml,"feeDescription");

			sv.tvMiddle.setText(fascicleFeeDescription);
			sv.tvMiddle.setVisibility(View.VISIBLE);
			sv.bMiddleLeft.setText("确认订购");
			sv.bMiddleLeft.setVisibility(View.VISIBLE);
			sv.bTopRight.setVisibility(View.INVISIBLE);
			sv.bMiddleRight.setVisibility(View.VISIBLE);
			sv.bMiddleLeft.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					pd.dismiss();
					//执行过程：订购本分册
					//mHandler.sendEmptyMessage(P13);
				}
			});

		} 


        // 推荐  原处理在客户端过于麻烦，根据平台提供的推荐信息加以显示即可
        String catalogIdRecom = SubscribeProcess.getFirstValue(xml, "catalogID");
        if (catalogIdRecom != null && !"".equals(catalogIdRecom)){
                recommendCatalog(sv.bBottomLeft,sv.bBottomRight, sv.tvBottom, catalogIdRecom, SubscribeProcess.getFirstValue(xml,
                        "catalogName"),SubscribeProcess.getSecondValue(xml,"feeDescription"));
            
        }else{               
            if(sv.bMiddleLeft.getVisibility()==View.VISIBLE){
                sv.bMiddleRight.setText("取消");
                sv.bMiddleRight.setVisibility(View.VISIBLE);
                sv.bMiddleRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pd.dismiss();
                    }
                });
            }else if(sv.bTopLeft.getVisibility()==View.VISIBLE){
                sv.bTopRight.setText("取消");
                sv.bTopRight.setVisibility(View.VISIBLE);
                sv.bTopRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pd.dismiss();
                    }
                });
            }

        }


		pd.setView(sv.getView());
		pd.setTitle("订购提示");
		pd.setCanClose(true);
		pd.show();

	}

    /**
     * 重写一个 推荐订购
     * 
     * @param btn
     * @param tv
     * @param catalogfee
     */
    private void recommendCatalog(Button btn, Button btnC, TextView tv, String catalogId,String cataName, String catalogfee) {
        Logger.d(TAG,"recom cata: catalogId:"+catalogId+",catalogName:"+cataName+",catalog feeDescribe:"+catalogfee);
        btnC.setText("取消");
        btnC.setVisibility(View.VISIBLE);
        btnC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pd.dismiss();
                    }
                });
        
        final String cataId = catalogId;
        final String cataFee = catalogfee;
        btn.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);
        btn.setText("确认订购");
        //tv.setText("订购" + cataName + "," + cataFee);
        tv.setText(cataFee);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pd!=null){
                    pd.dismiss();
                }
                pd = new PviAlertDialog(getParent());
                pd.setCanClose(true);
                pd.setTitle("确认订购");
                pd.setMessage(cataFee);
                pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                if(pd!=null){
                                    pd.dismiss();
                                }
                                pd = new PviAlertDialog(getParent());

                                new Thread() {
                                    public void run() {

                                        String strSC = SubscribeProcess.network(
                                                "subscribeCatalog", cataId,
                                                null, null, null);
                                        if (strSC != null
                                                && strSC.length() > 9
                                                && strSC.substring(0, 10)
                                                        .contains("Exception")) {
                                            retry();
                                            return;
                                        }
                                        if (strSC != null
                                                && strSC.length() > 18
                                                && strSC.substring(0, 19)
                                                        .contains("0000")) {
                                            if(pd!=null){
                                            pd.setCanClose(true);
                                            pd.setTitle("订购成功");
                                            pd
                                                    .setButton(
                                                            DialogInterface.BUTTON_POSITIVE,
                                                            "确定",
                                                            new android.content.DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int which) {
                                                                    
                                                                    //重设intent
                                                                    revBundle.putString("ChapterID",chapterID);
                                                                    revBundle.putString("ContentID",contentID);
                                                                    final Intent intent = getIntent();
                                                                    intent.putExtras(revBundle);
                                                                    setIntent(intent);
                                                                    
                                                                    onResume();
                                                                }
                                                            });
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pd.show();
                                                }
                                            });
                                            }
                                        } else if (strSC != null
                                                && strSC.length() > 18
                                                && strSC.substring(0, 19)
                                                        .contains("3159")) {

                                            if(pd!=null){
                                            pd.setCanClose(true);
                                            pd.setTitle("已订购过");
                                            pd
                                                    .setButton(
                                                            DialogInterface.BUTTON_POSITIVE,
                                                            "返回",
                                                            new android.content.DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int which) {
                                                                    onResume();
                                                                }
                                                            });
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pd.show();
                                                }
                                            });
                                            }
                                        }

                                    }
                                }.start();

                            }
                        });

                pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                ;
                            }
                        });

                pd.show();
            }
        });
    }

    private void retry() {
        onResume();
    }
	
	private void pd2(Message msg){
		if(msg==null){
		    Logger.e(TAG,"msg is null");
			return;
		}
		String message = "";	        
		Bundle bd = msg.getData();
		if(bd!=null){
			message = bd.getString("message");
		}

		if(message!=null&&!"".equals(message)){	
			pd = new PviAlertDialog(getParent());
			pd.setMessage(message, Gravity.CENTER);
			pd.setTitle("温馨提示");
			pd.setCanClose(true);
			pd.show();        
		}

	}

	private void pd3(){

		pd = new PviAlertDialog(getParent());
		pd.setMessage("该书尚在更新中，您可以进入摘要页预定最新章节", Gravity.CENTER);
		pd.setTitle("温馨提示");
		pd.setButton(DialogInterface.BUTTON_POSITIVE, "确 定", new android.content.DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				pd.dismiss();
				// TODO Auto-generated method stub
				final Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				final Bundle bundleToSend = new Bundle();
				bundleToSend.putString("act","com.pvi.ap.reader.activity.BookSummaryActivity"); 
				bundleToSend.putString("contentID", contentID);
				bundleToSend.putString("pviapfStatusTip",  "正在进入书籍摘要...");
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
			}});
		pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取 消", new android.content.DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}});
		pd.setCanClose(true);
		pd.show();        
	}

	/**
	 * 订购本章/书
	 */
	private void p11(){
		issubscribehint = false;
		if (chapterchange.equals("")) {
			SubscribeChapter(contentID, chapterID,
					productID);
		} else if (chapterchange
				.equals("prechapter")) {
			SubscribeChapter(contentID,
					preChapterID, productID);
		} else if (chapterchange
				.equals("nextchapter")) {
			SubscribeChapter(contentID,
					nextChapterID, productID);
		}
	}

	/**
	 * 订购后N章
	 * 
	 * 需要处理的异常：1、网络连接异常，提示重试  （统一放在网络接口的地方就好了！）
	 *              
	 *              2、返回了错误码   已订购过，认为成功
	 *                              其它错误码，弹出相应提示信息
	 *                              
	 *                              3、其它异常，弹出“系统错误”提示框
	 *              
	 */
	private void p12(){
		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		ahmNamePair.put("contentId", contentID);
		ahmNamePair.put("currentChapterId", chapterID);
		HashMap responseMap = null;
		try {
			responseMap = NetCache.batchSubscribeNextChapters(ahmHeaderMap,
					ahmNamePair);
		} catch (HttpException e) {
			e.printStackTrace();
			return ;
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			return ;
		} catch (IOException e) {
			e.printStackTrace();
			return ;
		}

		//如果订购成功的话，继续之前的操作：获取这一章的内容，显示之


		String resultCodeStr = "";
		if(responseMap!=null){
			final Object temp = responseMap.get("result-code");
			if(temp != null){
				resultCodeStr = temp.toString();
			}            
		}

		String resultCode = "-1";
		if(resultCodeStr.length()>12){

		}
		resultCode = resultCodeStr.substring(13);

		if(resultCode!=null){
			resultCode = resultCode.trim();
		}

		if("0".equals(resultCode)||"3159".equals(resultCode)){
			restartMe();
		}else{
			Message msg = Message.obtain(mHandler);
			msg.what = PD2;
			Bundle bd = new Bundle();
			bd.putString("message", Error.getErrorDescriptionForContent(resultCodeStr));
			msg.setData(bd);            
			mHandler.sendMessage(msg);
		}


	}


	/**
	 * 订购分册
	 */
	private void p13(){
		Intent intent1 = new Intent(
				MainpageActivity.START_ACTIVITY);
		Bundle sndBundle1 = new Bundle();
		sndBundle1
		.putString("act",
		"com.pvi.ap.reader.activity.SubscribeProcess");
		sndBundle1
		.putString("contentID", contentID);
		sndBundle1.putString("subscribeMode",
		"fascicleList");
		sndBundle1.putString("ProductID",
				fascicleProductId);
		sndBundle1
		.putString("fascicle", fascicleID);
		sndBundle1.putString("chargeTip", "温馨提示");
		sndBundle1.putString("catalogID",
				fasciclefeeDescription);
		intent1.putExtras(sndBundle1);
		sendBroadcast(intent1);
		finish();
	}

	private void restartMe(){
		//Logger.d(TAG,"restartMe(...");
		Intent intent1 = new Intent(
				MainpageActivity.START_ACTIVITY);
		intent1.putExtras(getIntent().getExtras());
		sendBroadcast(intent1);
	}

	private String getProductID(String contentID, String ChapterID) {// "0": 已订购
		// "1":未订购
		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		ahmNamePair.put("contentId", contentID);
		ahmNamePair.put("chapterId", ChapterID);
		String retstr = "";
		if (reflash) {
            showTip("一键更新中...");
            ahmNamePair.put("reflash", REFLASH);
            reflash = false;
        }else{
            showTip("数据获取中...");
        }
		HashMap responseMap = null;
		try {

			responseMap = NetCache.getContentProductInfo(ahmHeaderMap,
					ahmNamePair);
			if (responseMap == null) {
				mHandler.sendEmptyMessage(0);
				resumeUi();
				return "2";
			} else if (responseMap.containsKey("result-code")) {
				if (responseMap.get("result-code") != null) {
					retstr = responseMap.get("result-code").toString();
					if (!retstr.contains("result-code: 0")) {
						mHandler.sendEmptyMessage(0);
						resumeUi();
						return retstr;
					}
				} else {
					mHandler.sendEmptyMessage(0);
					resumeUi();
					return "2";
				}
			}

		} catch (HttpException e) {
			//
			e.printStackTrace();
			mHandler.sendEmptyMessage(1);
			resumeUi();
			return "2";
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(1);
			resumeUi();
			return "3";
		} catch (Exception e) {
			//
			e.printStackTrace();
			mHandler.sendEmptyMessage(1);
			// Toast.makeText(this, "联网获取数据失败！", Toast.LENGTH_SHORT).show();
			resumeUi();
			return "2";
		}
		byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
		String temp;
		try {
			temp = CPManagerUtil.getStringFrombyteArray(responseBody);
			if (temp.contains("&quot")) {
				temp = temp.replaceAll("&quot", "");
			}
			responseBody = temp.getBytes();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			temp = "";
		}
/*		try {
			System.out.println("返回的XML为：");
			System.out.println(CPManagerUtil
					.getStringFrombyteArray(responseBody));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/

		Document dom = null;
		try {
			dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(1);
			resumeUi();
			return "2";
		} catch (SAXException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(1);
			resumeUi();
			return "2";
		} catch (IOException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(1);
			resumeUi();
			return "2";
		}
		Element root = dom.getDocumentElement();
		
		resumeUi();

		NodeList nl = root.getElementsByTagName("isOrdered");
		Element isordered = (Element) nl.item(0);
		NodeList tempnl = null;
		Element tempelement = null;

		if (isordered.getFirstChild() != null) {
			if (isordered.getFirstChild().getNodeValue().equals("true")) {
				// 已订购
				return "0";
			} else {
				// 未订购
				tempnl = root.getElementsByTagName("productID");
				tempelement = (Element) tempnl.item(0);
				productID = tempelement.getFirstChild().getNodeValue();

				nl = root.getElementsByTagName("feeDescription");

				feedescription = ((Element) nl.item(0)).getFirstChild()
				.getNodeValue();

				if (bookinfo.containsKey("fascicleFlag")) {
					if (bookinfo.get("fascicleFlag").equals("1")) {
						// 有分册。。。。
						tempnl = root.getElementsByTagName("FascicleInfo");
						tempelement = (Element) tempnl.item(0);
						nl = tempelement.getElementsByTagName("productID");
						fascicleProductId = nl.item(0).getFirstChild()
						.getNodeValue();
						nl = tempelement.getElementsByTagName("feeDescription");
						fasciclefeeDescription = nl.item(0).getFirstChild()
						.getNodeValue();
					}
				}
				return "1";
			}
		} else {
			return "2";
		}

	}

	private boolean getBookInfo(String contentID, String ChapterID) {
		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		ahmNamePair.put("contentId", contentID);
		if (reflash) {
            showTip("一键更新中...");
            ahmNamePair.put("reflash", REFLASH);
            reflash = false;
        }else{
            showTip("数据获取中...");
        }
		HashMap responseMap = null;
		// Log.i("Reader", "getBookSummaryInfo");
		try {

			responseMap = NetCache.getContentInfo(ahmHeaderMap, ahmNamePair);
			if (responseMap == null) {
				mHandler.sendEmptyMessage(1);
				return false;
			} else if (responseMap.containsKey("result-code")) {
				if (responseMap.get("result-code") != null) {
					if (!responseMap.get("result-code").toString().contains(
					"result-code: 0")) {
						mHandler.sendEmptyMessage(1);
						return false;
					}
				} else {
					mHandler.sendEmptyMessage(1);
					return false;
				}
			} else {
				mHandler.sendEmptyMessage(1);
				return false;
			}

		} catch (HttpException e) {
			//
			e.printStackTrace();

			mHandler.sendEmptyMessage(1);
			return false;
		} catch (SocketTimeoutException e) {
			e.printStackTrace();

			mHandler.sendEmptyMessage(1);
			return false;
		} catch (Exception e) {
			//
			e.printStackTrace();

			mHandler.sendEmptyMessage(1);
			// Toast.makeText(this, "联网获取数据失败！", Toast.LENGTH_SHORT).show();
			return false;
		}
		byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
		// try {
		// System.out.println("返回的XML为：");
		// System.out.println(CPManagerUtil
		// .getStringFrombyteArray(responseBody));
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }

		Document dom = null;
		try {
			dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(1);
			return false;
		} catch (SAXException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(1);
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(1);
			return false;
		}
		Element root = dom.getDocumentElement();

		NodeList nl = root.getElementsByTagName("contentName");
		Element contentName = (Element) nl.item(0);

		String bookname = contentName.getFirstChild().getNodeValue();

		nl = root.getElementsByTagName("authorID");
		Element temp = (Element) nl.item(0);
		bookinfo.put("AuthorID", temp.getFirstChild().getNodeValue());

		nl = root.getElementsByTagName("authorName");
		temp = (Element) nl.item(0);
		bookinfo.put("AuthorName", temp.getFirstChild().getNodeValue());

		nl = root.getElementsByTagName("isSerial");
		Element isSerial = (Element) nl.item(0);
		if (isSerial.getFirstChild().getNodeValue().equals("false")) {
			bookinfo.put("IsSerial", "0");
			bookinfo.put("IsFinish", "1");
		} else {
			bookinfo.put("IsSerial", "1");
			NodeList overlist = root.getElementsByTagName("isFinish");
			Element isfinish = (Element) overlist.item(0);
			if (isfinish.getFirstChild().getNodeValue().equals("false")) {
				bookinfo.put("IsFinish", "0");

			} else {
				bookinfo.put("IsFinish", "1");
			}
		}
		bookinfo.put("BookName", bookname);

		nl = root.getElementsByTagName("fascicleFlag");
		temp = (Element) nl.item(0);
		bookinfo.put("fascicleFlag", temp.getFirstChild().getNodeValue());

		nl = root.getElementsByTagName("chargeTip");
		temp = (Element) nl.item(0);
		if(temp!=null && (temp.getFirstChild()!=null))
		{
			bookinfo.put("ChargeTip", temp.getFirstChild().getNodeValue());
		}
		else
		{
			bookinfo.put("ChargeTip", "");
		}

		nl = root.getElementsByTagName("canDownload");
		temp = (Element) nl.item(0);
		bookinfo.put("CanDownload", temp.getFirstChild().getNodeValue());

		nl = root.getElementsByTagName("chargeMode");
		temp = (Element) nl.item(0);
		bookinfo.put("ChargeMode", temp.getFirstChild().getNodeValue());

		nl = root.getElementsByTagName("canPresent");
		temp = (Element) nl.item(0);
		bookinfo.put("canPresent", temp.getFirstChild().getNodeValue());

		nl = root.getElementsByTagName("smallLogo");
		if (nl != null) {
			temp = (Element) nl.item(0);
			if ((temp != null)) {
				if (temp.getFirstChild() != null) {
					bookinfo.put("SmallLogoUrl", temp.getFirstChild()
							.getNodeValue());
				} else {
					bookinfo.put("SmallLogoUrl", "No Small logo URL");
				}
			} else {
				bookinfo.put("SmallLogoUrl", "No Small logo URL");
			}
		} else {
			bookinfo.put("SmallLogoUrl", "No Small logo URL");
		}

		return true;
	}

	@Override
	protected void onResume() {
		Logger.d(TAG,"onResume()");

		super.onResume();
		//		Settings.System.putInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 1);

		//if (!isInternalSwitch) {
		if(true){
			Logger.e(TAG, "isInternalSwitch");
			revintent = this.getIntent();
			if(revintent.getExtras() != null){
				Bundle tmp = revintent.getExtras();
				if(tmp.getString("ContentID") != null){
					revBundle = tmp ;
					Logger.i(TAG, "update intent");
				}
			}

			chapterID = revBundle.getString("ChapterID");
			contentID = revBundle.getString("ContentID");
			Logger.i(TAG, "chapterID" + chapterID);
			Logger.i(TAG, "contentID" + contentID);
			bookinfo.clear();
			Content = "" ;
			if (ispagechanging.equals("")) {
				if(revBundle.containsKey("FontSize"))
				{
					this.fontsize = revBundle.getString("FontSize");
					if (this.fontsize == null) {
						this.fontsize = "16";
					}
				}
				else
				{
					this.fontsize = "16";
				}

				
				m_size = Float.parseFloat(fontsize);
				if(revBundle.containsKey("LineSpace"))
				{
					this.lineSpac = Float.parseFloat(revBundle
							.getString("LineSpace"));
				}
				else
				{
					this.lineSpac = 5;
				}
				if(revBundle.containsKey("Position"))
				{
					position = revBundle.getString("Position");
					if (position == null) {
						position = "0";
					}
				}
				else
				{
					position = "0";
				}
			}
		}
		content.setTextSize(Float.parseFloat(fontsize));
		content.setLineSpacing(this.lineSpac, 1);

		showNetWorkProcessing();

		setUIData();


		long TimeStart = System.currentTimeMillis();
		Logger.i("Time", "ReadingOnlineActivity Enter: "
				+ Long.toString(TimeStart));
		content.requestFocus();
	}

	public void page_finished() {
		first = false ;
		if (content != null) {
			//			Logger.e("page_finished", "999999999999999");
			PageTextView pageView = (PageTextView) content;
			SpannableStringBuilder span_strBuilder = new SpannableStringBuilder(
					spanned.toString().trim());
			total_pages = pageView.all_pages(span_strBuilder, true, false);
			totalPage = total_pages.size();


			int pos = Integer.parseInt(position);
			if (total_pages.contains(pos)) {
				currentPage = total_pages.indexOf(pos) + 1;
			}
			else
			{
				for(int i=0; i < total_pages.size(); i++)
				{
					if(total_pages.get(i)>pos)
					{
						currentPage = i;
						break;
					}
				}
			}
			if(chapterIdLength==0 || currentPage==0)
			{
				//				Logger.e("page_finished", "2222222111111111111111");
				return;
			}
			if (currentPage == totalPage) {
				charSequence = spanned.subSequence(total_pages
						.get(currentPage - 1), chapterIdLength - 1);

			} else {
				charSequence = spanned
				.subSequence(total_pages.get(currentPage - 1),
						total_pages.get(currentPage));
			}
			pageView.setText(charSequence);
			updatePagerinfo(currentPage + "/" + totalPage);


			//			Logger.e("page_finished", "yyyyyyyyyyyyyyyy");

		} else {
			PageTextView pageView = null;
			total_pages = null;
		}
	};

	private void showSearch(boolean b) {
		if (b) {
			serachs.setVisibility(View.VISIBLE);
		} else
		{
			serachs.setVisibility(View.INVISIBLE);
			bSearch.setText(getResources().getString(R.string.Serach));
		}

	}

	private int orientation = 1 ;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// Configuration newConfig = getResources().getConfiguration();
		// orientation = newConfig.orientation ;
		if (orientation == 1) {
			this.setContentView(R.layout.onlinereadpagestyle2);
		} else {
			this.setContentView(R.layout.onlinereadpagestyleoperation);
		}

		app = ((GlobalVar) getApplicationContext());
		this.showChaper = true;
		this.showPager =true;
		first = true ;

		content = (TextView) this.findViewById(R.id.contentview);        

		spanned = Html.fromHtml("");
		super.onCreate(savedInstanceState);
		moveFile();
		if(deviceType==1){
//			content.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
		}

	}
	
	

	public void bindEvent() {
		super.bindEvent();

		serachs = findViewById(R.id.buttonLay);
		closeBtn = (Button) findViewById(R.id.close);
		look = (EditText) findViewById(R.id.serach);
		bSearch = (Button) findViewById(R.id.find);

		if (bSearch != null) {
			bSearch.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {

					txtSerchContent();
				}
			});
		}
		if (closeBtn != null) {
			closeBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					num = 0;
					showSearch(false);
				}
			});
		}

		//左右键翻页
		content.setOnKeyListener(new OnKeyListener(){

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction()==KeyEvent.ACTION_DOWN){
					if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
						return true;
					}
					if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
						return true;
					}
				}

				if(event.getAction()==KeyEvent.ACTION_UP){
					if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
						OnPrevpage();
						content.requestFocus();
						return true;
					}
					if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
						// isFirst= false;
					    OnNextpage();
						content.requestFocus();
						return true;
					}
				}

				return false;
			}});
	}

	private boolean getChapterContent() {
	    
		// Log.i("Menu", "getChapterContent");
		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();

		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

		ahmNamePair.put("contentId", contentID);
		ahmNamePair.put("chapterId", chapterID);

		if (reflash) {
		    showTip("一键更新中...");
			ahmNamePair.put("reflash", REFLASH);
			reflash = false;
		}else{
		    showTip("数据获取中...");
		}

		HashMap responseMap = null;
		try {
			// 以POST的形式连接请求
			responseMap = NetCache.getChapterInfo(ahmHeaderMap, ahmNamePair);
		} catch (HttpException e) {
			// 连接异常 ,一般原因为 URL错误
			e.printStackTrace();
			mHandler.sendEmptyMessage(1);
			return false;
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(1);
			return false;
		} catch (IOException e) {
			// IO异常 ,一般原因为网络问题
			e.printStackTrace();
			mHandler.sendEmptyMessage(1);
			return false;
		}




		String retstr = "";
		if (responseMap.containsKey("result-code")
				&& (responseMap.get("result-code") != null)) {
			retstr = responseMap.get("result-code").toString();
		}

		if (retstr.contains("result-code: 3166")) {
			try {


				byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
				try {
					Logger.d(TAG,"返回的XML为：");
					Logger.d(TAG,CPManagerUtil.getStringFrombyteArray(responseBody));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return false;
				}catch (Exception e) {
					e.printStackTrace();
					return false;
				}


				//去掉特殊符号
				String temp = "";
				try {
					temp = CPManagerUtil.getStringFrombyteArray(responseBody);
					if (temp!=null&&temp.contains("&quot;")) {
						temp = temp.replaceAll("&quot", "\"");
						Logger.d(TAG,temp);
						responseBody = temp.getBytes();
					}
					// System.out.println(temp);

				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					temp = "";
					return false;
				}catch (Exception e) {
					e.printStackTrace();
					return false;
				}

				// 根据返回字节数组构造DOM
				Document dom = null;
				try {
					dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
					mHandler.sendEmptyMessage(1);
					return false;
				} catch (SAXException e) {
					e.printStackTrace();
					mHandler.sendEmptyMessage(1);
					return false;
				} catch (IOException e) {
					e.printStackTrace();
					mHandler.sendEmptyMessage(1);
					return false;
				}
				if(dom==null){
					throw new NullPointerException("dom is null");
				}
				Element root = dom.getDocumentElement();

				NodeList nl = root.getElementsByTagName("productID");
				productID = ((Element) nl.item(0)).getFirstChild()
				.getNodeValue();
				nl = root.getElementsByTagName("feeDescription");

				feedescription = ((Element) nl.item(0)).getFirstChild()
				.getNodeValue();
				//-- ---  这里处理 订购提示

				Message msg = Message.obtain(mHandler);
				msg.what = PD1;
				Bundle bd = new Bundle();
				bd.putString("xml", temp);
				msg.setData(bd);            
				mHandler.sendMessage(msg);

				/*NodeList tempnl = null;
				Element tempelement = null;
				if (bookinfo.containsKey("fascicleFlag")) {
					if (bookinfo.get("fascicleFlag").equals("0")) {
						mHandler.sendEmptyMessage(2);
					} else {
						// 处理分册。。。。
						tempnl = root.getElementsByTagName("FascicleInfo");
						tempelement = (Element) tempnl.item(0);
						nl = tempelement.getElementsByTagName("productID");
						fascicleProductId = nl.item(0).getFirstChild()
						.getNodeValue();
						nl = tempelement.getElementsByTagName("feeDescription");
						fasciclefeeDescription = nl.item(0).getFirstChild()
						.getNodeValue();
						mHandler.sendEmptyMessage(3);
					}
				} else {
					mHandler.sendEmptyMessage(1);
				}*/
			} catch (Exception e) {
				mHandler.sendEmptyMessage(1);
				return false;
			}
			return false;
		} else if (retstr.contains("result-code: 0")) {


			byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
//			try {
//				Logger.d(TAG,"返回的XML为：");
//				Logger.d(TAG,CPManagerUtil.getStringFrombyteArray(responseBody));
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//				return false;
//			}catch (Exception e) {
//				e.printStackTrace();
//				return false;
//			}


			//去掉特殊符号
			String temp = "";
			try {
				temp = CPManagerUtil.getStringFrombyteArray(responseBody);
				if (temp!=null&&temp.contains("&quot;")) {
					temp = temp.replaceAll("&quot", "\"");
					Logger.d(TAG,temp);
					responseBody = temp.getBytes();
				}
				// System.out.println(temp);

			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				temp = "";
				return false;
			}catch (Exception e) {
				e.printStackTrace();
				return false;
			}

			// 根据返回字节数组构造DOM
			Document dom = null;
			try {
				dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(1);
				return false;
			} catch (SAXException e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(1);
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(1);
				return false;
			}
			if(dom==null){
				throw new NullPointerException("dom is null");
			}
			Element root = dom.getDocumentElement();

			NodeList nl = root.getElementsByTagName("content");

			Content = nl.item(0).getFirstChild().getNodeValue();
			Logger.e("GetContentEnd", "Content:" + Content);

			nl = root.getElementsByTagName("chapterName");
			chaptername = nl.item(0).getFirstChild().getNodeValue();

			if (bookinfo.containsKey("fascicleFlag")) {
				if (bookinfo.get("fascicleFlag").equals("1")) {
					NodeList tempnl = root.getElementsByTagName("fascicleID");

					if ((tempnl != null) && tempnl.getLength() > 0) {
						if (tempnl.item(0).getFirstChild() != null) {
							this.fascicleID = tempnl.item(0).getFirstChild()
							.getNodeValue();
						} else {
							fascicleID = "";
						}
					} else {
						fascicleID = "";
					}
				}
			}

			nl = root.getElementsByTagName("PrevChapter");

			Element element = null;
			if ((nl == null) || (nl.getLength() == 0)) {
				this.preChapterID = chapterID;
				this.preChapterName = chaptername;
				this.prefascicleID = fascicleID;
			} else {

				element = (Element) nl.item(0);
				try {
					this.preChapterID = element.getElementsByTagName(
					"chapterID").item(0).getFirstChild().getNodeValue();
					this.preChapterName = element.getElementsByTagName(
					"chapterName").item(0).getFirstChild()
					.getNodeValue();

					if (bookinfo.containsKey("fascicleFlag")) {
						if (bookinfo.get("fascicleFlag").equals("1")) {
							NodeList tempnl = element
							.getElementsByTagName("fascicleID");

							if ((tempnl != null) && tempnl.getLength() > 0) {
								if (tempnl.item(0).getFirstChild() != null) {
									this.prefascicleID = tempnl.item(0)
									.getFirstChild().getNodeValue();
								} else {
									prefascicleID = "";
								}
							} else {
								prefascicleID = "";
							}
						}
					}

				} catch (Exception e) {
					this.preChapterID = chapterID;
					this.preChapterName = chaptername;
					this.prefascicleID = fascicleID;
				}
			}

			nl = root.getElementsByTagName("NextChapter");
			if ((nl == null) || (nl.getLength() == 0)) {
				this.nextChapterID = chapterID;
				this.nextChapterName = chaptername;
				this.nextfascicleID = fascicleID;

			} else {
				element = (Element) nl.item(0);
				try {
					this.nextChapterID = element.getElementsByTagName(
					"chapterID").item(0).getFirstChild().getNodeValue();
					this.nextChapterName = element.getElementsByTagName(
					"chapterName").item(0).getFirstChild()
					.getNodeValue();

					if (bookinfo.containsKey("fascicleFlag")) {
						if (bookinfo.get("fascicleFlag").equals("1")) {
							NodeList tempnl = element
							.getElementsByTagName("fascicleID");

							if ((tempnl != null) && tempnl.getLength() > 0) {
								if (tempnl.item(0).getFirstChild() != null) {
									this.nextfascicleID = tempnl.item(0)
									.getFirstChild().getNodeValue();
								} else {
									nextfascicleID = "";
								}
							} else {
								nextfascicleID = "";
							}
						}
					}

				} catch (Exception e) {
					this.nextChapterID = chapterID;
					this.nextChapterName = chaptername;
					this.nextfascicleID = fascicleID;
					Logger.e("GetContentEnd", "Exception:" + e.toString());
				}
			}
			return true;
		} else if (retstr.contains("result-code: 3130")) {
			mHandler.sendEmptyMessage(1);
			return false;
		} else {
			mHandler.sendEmptyMessage(1);
			return false;
		}
	}

	private void setUIData() {
		getdatathread = new Thread() {
			public void run() {

				if (bookinfo.isEmpty()) {
					if (!getBookInfo(contentID, chapterID)) {
						return;
					}
				}
				if (!getChapterContent())
					return;
				spanned = Html.fromHtml(Content);
				chapterIdLength = spanned.length();
				mHandler.post(getData);
//				mHandler.sendEmptyMessage(455);
				Logger.e("setUIDataEnd", "setUIDataEnd");
			}
		};
		getdatathread.start();
	}

	private Runnable getData = new Runnable() {
		@Override
		public void run() {
			try {
				Logger.d("getData", "getData Start");
				Intent intent = new Intent(MainpageActivity.SET_TITLE);
				Bundle sndBundle = new Bundle();
				
				if (bookinfo.containsKey("BookName")
						&& (bookinfo.get("BookName") != null)) {
					sndBundle.putString("title", bookinfo.get("BookName")
							.toString()
							+ "  [>>]  " + chaptername);
				} else {
					sndBundle.putString("title", chaptername);
				}

				intent.putExtras(sndBundle);
				sendBroadcast(intent);

				SpannableStringBuilder span_strBuilder = new SpannableStringBuilder(
						spanned.toString().trim());
				if (ispagechanging.equals("")) {
					content.setTextSize(Float.parseFloat(fontsize));
					content.setLineSpacing(lineSpac, 1);
				}

				if (!ispagechanging.equals("") || !first) {
					PageTextView pageView = (PageTextView) content;

					total_pages = PageTextView.all_pages(span_strBuilder,getResources().getDisplayMetrics().density , content.getWidth(), content.getHeight(), m_size, 1, ReadingOnlineActivity.this.lineSpac);
					totalPage = total_pages.size();
					if(ispagechanging.equals("prepage")
							&& (currentPage == 1))
					{
						currentPage = totalPage;
					}
					else
					{
						currentPage = 1;
					}

					if (currentPage == totalPage) {
						charSequence = spanned.subSequence(total_pages
								.get(currentPage - 1), chapterIdLength - 1);

					} else {
						charSequence = spanned
						.subSequence(total_pages.get(currentPage - 1),
								total_pages.get(currentPage));
					}

					pageView.setText(charSequence);

					updatePagerinfo(currentPage + "/" + totalPage);


				} 


				hideTip();
				showMe(getClass());
				dataOK = true;

			} catch (Exception e) {
				hideTip();

				mHandler.sendEmptyMessage(0);
				e.printStackTrace();
			}
			inChapterChanging = false;
		}
	};

	private boolean dataOK = false;

	// 下一页
	private void goToNextPage() {
		if(inChapterChanging){
			//			Toast.makeText(ReadingOnlineActivity.this, "上一次操作处理中",
			//					Toast.LENGTH_SHORT).show();
			return;
		}
		long TimeStart = System.currentTimeMillis();
		Logger.i("Time", "Mainpage Pressed" + Long.toString(TimeStart));
		currentPage = currentPage + 1;
		if (this.currentPage > this.totalPage) {
			currentPage = totalPage;
			if ( !inChapterChanging && (nextChapterID!=null&&nextChapterID.equals(chapterID))) {


				if(bookinfo!=null &&
						"1".equals(bookinfo.get("IsSerial")) &&
						"0".equals(bookinfo.get("IsFinish")) ){
					//对于连载中的图书，提示框，提示用户到摘要页预订更新
					mHandler.sendEmptyMessage(PD3);
				}else{
					showMessage("已是本书最后一页","3000");
				}
				return;
			} else {
				OnNextchap();
			}
			return;
		} else if (this.currentPage == this.totalPage) {
			charSequence = spanned.subSequence(total_pages
					.get(this.currentPage - 1), chapterIdLength - 1);


		} else {
			charSequence = spanned.subSequence(total_pages
					.get(this.currentPage - 1), total_pages
					.get(this.currentPage));
		}
		compPageCounter();
		content.setText(charSequence);
		updatePagerinfo(currentPage + "/" + totalPage);


		TimeStart = System.currentTimeMillis();
		Logger.i("Time", "Mainpage Pressed" + Long.toString(TimeStart));

	}

	// 上一页
	private void goToUpPage() {
		if(inChapterChanging){
			//			Toast.makeText(ReadingOnlineActivity.this, "上一次操作处理中",
			//					Toast.LENGTH_SHORT).show();
			return;
		}
		long TimeStart = System.currentTimeMillis();
		Logger.i("Time", "Mainpage Pressed" + Long.toString(TimeStart));
		currentPage = currentPage - 1;
		if (currentPage < 1) {
			currentPage = 1;
			if (!inChapterChanging && (preChapterID!=null&&preChapterID.equals(chapterID))) {
				showMessage("已是本书第一页");
				currentPage = 1;
				return;
			} else {
				OnPrevchap();
			}
			return;
		} else {
			charSequence = spanned.subSequence(
					total_pages.get(currentPage - 1), total_pages
					.get(currentPage));
		}
		compPageCounter();
		content.setText(charSequence);
		updatePagerinfo(currentPage + "/" + totalPage);

		TimeStart = System.currentTimeMillis();
		Logger.i("Time", "Mainpage Pressed" + Long.toString(TimeStart));

	}

	@Override
	public void onPause() {
		if (dataOK) {
			addBookMarkToDB(true);
			new Thread() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					addSystemBookMark(true);
				}
			}.start();
		}
		// if (popmenu != null) {
		// popmenu.dismiss();
		// }
		super.onPause();
	}

	private void addBookMarkToDB(boolean sysbookmark) {
		String columns[] = new String[] { Bookmark.UserID, Bookmark.BookmarkId,
				Bookmark.ContentId, Bookmark.ChapterId, Bookmark.ChapterName,
				Bookmark.ContentName, Bookmark.CertPath, Bookmark.FilePath,
				Bookmark.CreatedDate, Bookmark.Position, Bookmark.BookmarkType };
		Cursor cur = null;
		GlobalVar app = (GlobalVar) getApplicationContext();
		if (sysbookmark) {
			String where = Bookmark.BookmarkType + "='" + 0 + "'" + " and "
			+ Bookmark.ContentId + "='" + contentID + "'";
			cur = managedQuery(Bookmark.CONTENT_URI, columns, where, null, null);
			if (cur.getCount() >= 1) {
				ContentValues values = new ContentValues();
				if (!chaptername.equals("")) {
					values.put(Bookmark.ChapterName, chaptername);
				}
				values.put(Bookmark.ContentId, contentID);
				values.put(Bookmark.UserID, app.getUserID());
				values.put(Bookmark.BookmarkType, 0);
				values.put(Bookmark.FontSize, this.m_size);
				values.put(Bookmark.LineSpace, this.lineSpac);
				values.put(Bookmark.ChapterId, chapterID);
				if (bookinfo.containsKey("BookName")) {
					if (!bookinfo.get("BookName").toString().equals("")) {
						values.put(Bookmark.ContentName, bookinfo.get(
						"BookName").toString());
					}
				}
				if (bookinfo.containsKey("AuthorName")) {
					values.put(Bookmark.Author, bookinfo.get("AuthorName")
							.toString());
				} else {
					values.put(Bookmark.Author, "");
				}
				values.put(Bookmark.OperationType, 0);
				if (currentPage >= totalPage) {
					currentPage = totalPage;
				}
				if (currentPage > 0) {
					if (total_pages != null) {
						values.put(Bookmark.Position, total_pages
								.get(this.currentPage - 1));
					}
				}

				values.put(Bookmark.SynchFlag, 0);
				//				Date CurTime = new Date(System.currentTimeMillis());
				values.put(Bookmark.CreatedDate, GlobalVar.getFormatTime(System.currentTimeMillis()));
				where = Bookmark.BookmarkType + "='" + 0 + "'" + " and "
				+ Bookmark.ContentId + "='" + contentID + "'";
				getContentResolver().update(Bookmark.CONTENT_URI, values,
						where, null);
			} else {
				ContentValues values = new ContentValues();
				values.put(Bookmark.SourceType, 4);
				values.put(Bookmark.ChapterName, chaptername);
				values.put(Bookmark.ContentId, contentID);
				values.put(Bookmark.UserID, app.getUserID());
				values.put(Bookmark.BookmarkType, 0);
				values.put(Bookmark.FontSize, content.getTextSize());
				values.put(Bookmark.LineSpace, this.lineSpac);
				values.put(Bookmark.ChapterId, chapterID);
				if (bookinfo.containsKey("BookName")) {
					values.put(Bookmark.ContentName, bookinfo.get("BookName")
							.toString());
				}
				if (bookinfo.containsKey("AuthorName")) {
					values.put(Bookmark.Author, bookinfo.get("AuthorName")
							.toString());
				} else {
					values.put(Bookmark.Author, "");
				}
				values.put(Bookmark.OperationType, 0);
				if (total_pages != null) {
					values.put(Bookmark.Position, total_pages
							.get(this.currentPage - 1));
				} else {
					values.put(Bookmark.Position, 0);
				}
				values.put(Bookmark.SynchFlag, 0);
				getContentResolver().insert(Bookmark.CONTENT_URI, values);
			}

		} else {
			ContentValues values = new ContentValues();
			values.put(Bookmark.SourceType, 4);
			values.put(Bookmark.ChapterName, chaptername);
			values.put(Bookmark.ContentId, contentID);
			values.put(Bookmark.UserID, app.getUserID());
			values.put(Bookmark.BookmarkType, 1);
			if (bookinfo.containsKey("BookName")) {
				values.put(Bookmark.ContentName, bookinfo.get("BookName")
						.toString());
			}
			if (bookinfo.containsKey("AuthorName")) {
				values.put(Bookmark.Author, bookinfo.get("AuthorName")
						.toString());
			} else {
				values.put(Bookmark.Author, "");
			}
			values.put(Bookmark.FontSize, content.getTextSize());
			values.put(Bookmark.LineSpace, this.lineSpac);
			values.put(Bookmark.ChapterId, chapterID);
			values.put(Bookmark.OperationType, 0);
			if (total_pages != null) {
				values.put(Bookmark.Position, total_pages
						.get(this.currentPage - 1));
			} else {
				values.put(Bookmark.Position, 0);
			}
			values.put(Bookmark.SynchFlag, 0);
			getContentResolver().insert(Bookmark.CONTENT_URI, values);
		}
		if (cur != null) {
			cur.close();
		}
	}

	private void addSystemBookMark(boolean sysbookmark) {
		try {
			HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();

			HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

			LeafNode postemp = new LeafNode("position", total_pages.get(
					this.currentPage - 1).toString());
			LeafNode contenttemp = new LeafNode("contentID", contentID);
			LeafNode chaptertemp = new LeafNode("chapterID", chapterID);
			//LeafNode counttemp = new LeafNode("chapterID", chapterID);
			List param = new ArrayList();
			param.add(postemp);
			param.add(contenttemp);
			param.add(chaptertemp);
			XmlElement temp = new XmlElement("Bookmark", param);
			XmlElement root = null;
			if (sysbookmark) {
				root = XMLUtil
				.getParentXmlElement("AddSystemBookmarkReq", temp);
			} else {
				root = XMLUtil.getParentXmlElement("AddUserBookmarkReq", temp);
			}
			root = XMLUtil.getParentXmlElement("Request", root);

			// System.out.println(XMLUtil
			// .getXmlStringFromXmlElement(root));
			// String temp1 = XMLUtil.getXmlStringFromXmlElement(root);
			// System.out.print(temp1);

			try {
				ahmNamePair.put("XMLBody", XMLUtil
						.getXmlStringFromXmlElement(root));
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}

			HashMap responseMap = null;

			try {
				// 以POST的形式连接请求
				if (sysbookmark) {
					responseMap = NetCache.addSystemBookmark(ahmHeaderMap,
							ahmNamePair);
					Logger.i(TAG, "addSystemBookmark"
							+ responseMap.get("result-code").toString());
				} else {
					responseMap = NetCache.addUserBookmark(ahmHeaderMap,
							ahmNamePair);
					Logger.i(TAG, "addUserBookmark"
							+ responseMap.get("result-code").toString());
				}
			} catch (HttpException e) {
				// 连接异常 ,一般原因为 URL错误
				e.printStackTrace();
				return;
			} catch (IOException e) {
				// IO异常 ,一般原因为网络问题
				e.printStackTrace();
				return;
			}

			//byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
			// try {
			// System.out.println("返回的XML为：");
			// System.out.println(CPManagerUtil
			// .getStringFrombyteArray(responseBody));
			// } catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
			// }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}


	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case 1:
			return buildDialogTypefaces(ReadingOnlineActivity.this);
		}
		return null;
	}

	private Dialog buildDialogTypefaces(Context context) {

		LayoutInflater inflater = LayoutInflater.from(this);

		int layoutID = R.layout.readsetformer;
	
		final View entryView = inflater.inflate(layoutID, null);
		PviAlertDialog dlg = new PviAlertDialog(context);
		// dlg.setTitle(R.string.fontSizeDialogBoxTitle);
		dlg.setView(entryView);
		ReadSetView internalView = (ReadSetView) entryView;
		internalView.setDialog(dlg);
		internalView.setSufangBool(false);
		HashMap<String, String> chooseMap = new HashMap<String, String>();
		chooseMap.put(ReadSetView.CON_FONESIZE, "" + (int) m_size);
		chooseMap.put(ReadSetView.CON_LINESIZE, "" + (int) lineSpac);
		internalView.setChooseMap(chooseMap);
		internalView.setSetListener(new ReadSetView.SetListener() {
			public void chooseDoListener(boolean ok,
					java.util.Map<String, String> chooseButton) {
				if (ok) {
					String foneSize = chooseButton
					.get(ReadSetView.CON_FONESIZE);
					String lineSize = chooseButton
					.get(ReadSetView.CON_LINESIZE);
					m_size = (int) Float.parseFloat(foneSize);
					lineSpac = (int) Float.parseFloat(lineSize);
					content.setTextSize(m_size);
					content.setLineSpacing(lineSpac, 1);
					
					updateIntent();
					
					SpannableStringBuilder span_strBuilder = new SpannableStringBuilder(
							spanned.toString().trim());
					float displayDensity = ReadingOnlineActivity.this.getResources()
					.getDisplayMetrics().density;
					float spacingmult = 1f ;
					float spacingadd =lineSpac ;
					total_pages = PageTextView.all_pages(span_strBuilder,displayDensity , content.getWidth(), content.getHeight(), m_size, spacingmult, spacingadd);
					if (currentPage >= total_pages.size()) {
						currentPage = total_pages.size();
						charSequence = spanned.subSequence(total_pages
								.get(currentPage - 1), chapterIdLength - 1);
					} else {
						charSequence = spanned.subSequence(total_pages
								.get(currentPage - 1), total_pages
								.get(currentPage));
					}
					content.setText(charSequence);
					totalPage = total_pages.size();
					updatePagerinfo(currentPage + " / " + totalPage);

				}
			}
		});

		return dlg;
	}

	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {

            closePopmenu();
            String vTag = item.id;
            if (vTag.equals("bookshelf")) {
                // 我的书架
                InactiveFunction
                .GotoMyBookshelf(ReadingOnlineActivity.this, "",contentID);
            } else if (vTag.equals("search")) {
                showSearch(true);
            } else if (vTag.equals("usercenter")) {
                // 个人中心
                final Intent tmpintent = new Intent(
                        MainpageActivity.START_ACTIVITY);
                Bundle sndbundle = new Bundle();
                sndbundle.putString("actID", "ACT14000");
                sndbundle.putString("actName", "用户信息");
                tmpintent.putExtras(sndbundle);
                sendBroadcast(tmpintent);
            } else if (vTag.equals("favorite")) {
                new Thread(){
                    public void run() {
                     // 收藏
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("contentID", contentID);
                        map.put("BookName", bookinfo.get("BookName").toString());
                        map.put("AuthorName", bookinfo.get("AuthorName").toString());
                        map
                        .put("SmallLogoUrl", bookinfo.get("SmallLogoUrl")
                                .toString());
                        String retstr = InactiveFunction.addFavorite(
                                ReadingOnlineActivity.this, map);

                        if (retstr.equals("0")) {
                            // 提示添加成功
                            Message msg = Message.obtain(mHandler);
                            msg.what = PD2;
                            Bundle bd = new Bundle();
                            bd.putString("message", "收藏成功。");
                            msg.setData(bd);            
                            mHandler.sendMessage(msg);
                        } else if (retstr.equals("1")) {
                            // 提示用户已收藏
                            Message msg = Message.obtain(mHandler);
                            msg.what = PD2;
                            Bundle bd = new Bundle();
                            bd.putString("message", "联网请求超时。");
                            msg.setData(bd);            
                            mHandler.sendMessage(msg);
                        } else if (retstr.equals("2")) {
                            // 提示用户已收藏
                            Message msg = Message.obtain(mHandler);
                            msg.what = PD2;
                            Bundle bd = new Bundle();
                            bd.putString("message", "联网提交请求失败。");
                            msg.setData(bd);            
                            mHandler.sendMessage(msg);
                        } else if (retstr.equals("result-code: 2028\r\n")) {
                            // 提示添加失败
                            Message msg = Message.obtain(mHandler);
                            msg.what = PD2;
                            Bundle bd = new Bundle();
                            bd.putString("message", "收藏已达上限，请到我的收藏中删除收藏记录。");
                            msg.setData(bd);            
                            mHandler.sendMessage(msg);
                        } else {
                            Message msg = Message.obtain(mHandler);
                            msg.what = PD2;
                            Bundle bd = new Bundle();
                            bd.putString("message", Error.getErrorDescriptionForContent(retstr));
                            msg.setData(bd);            
                            mHandler.sendMessage(msg);
                        }
                    }
                }.start();
                
            } else if (vTag.equals("comments")) {
                // 查看评论
                InactiveFunction.comment(ReadingOnlineActivity.this, contentID,
                        true);
            } else if (vTag.equals("flowers")) {
                
                new Thread(){
                    public void run() {
                     // 送鲜花

                        HashMap<String, String> map = InactiveFunction
                        .VoteFlower(contentID);
                        String retstr = map.get("RetCode");

                        if (retstr.equals("0")) {
                            // 提示添加成功
                            Message msg = Message.obtain(mHandler);
                            msg.what = PD2;
                            Bundle bd = new Bundle();
                            bd.putString("message", "送鲜花成功。");
                            msg.setData(bd);            
                            mHandler.sendMessage(msg);
                        } else if (retstr.equals("1")) {
                            // 提示用户已收藏
                            Message msg = Message.obtain(mHandler);
                            msg.what = PD2;
                            Bundle bd = new Bundle();
                            bd.putString("message", "联网请求超时。");
                            msg.setData(bd);            
                            mHandler.sendMessage(msg);

                        } else if (retstr.equals("2")) {
                            // 提示添加失败
                            Message msg = Message.obtain(mHandler);
                            msg.what = PD2;
                            Bundle bd = new Bundle();
                            bd.putString("message", "联网提交请求失败。");
                            msg.setData(bd);            
                            mHandler.sendMessage(msg);
                        } else if (retstr.equals("result-code: 2027\r\n")) {
                            Message msg = Message.obtain(mHandler);
                            msg.what = PD2;
                            Bundle bd = new Bundle();
                            bd.putString("message", "送鲜花已达每日次数限制。");
                            msg.setData(bd);            
                            mHandler.sendMessage(msg);
                        } else {
                            Message msg = Message.obtain(mHandler);
                            msg.what = PD2;
                            Bundle bd = new Bundle();
                            bd.putString("message", Error.getErrorDescriptionForContent(retstr));
                            msg.setData(bd);            
                            mHandler.sendMessage(msg);
                        }
                    }}.start();
                
            } else if (vTag.equals("sendcomments")) {
                // 发表评论
                InactiveFunction.comment(ReadingOnlineActivity.this, contentID,
                        false);
            } else if (vTag.equals("recommendtofriends")) {
                // 推荐给好友
                InactiveFunction.RecommendToFriends(ReadingOnlineActivity.this,
                        contentID, chapterID);

            } else if (vTag.equals("authorinfo")) {
                // 作者信息
                InactiveFunction.AuthorInfo(ReadingOnlineActivity.this,
                        bookinfo.get("AuthorID").toString());
            } else if (vTag.equals("typefaces")) {
                // 字号大小
                showDialog(1);
            } else if (vTag.equals("returncontents")) {
                // 目录页
                Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
                Bundle bundleToSend = new Bundle();
                bundleToSend.putString("act",
                "com.pvi.ap.reader.activity.BookCatalogActivity");
                bundleToSend.putString("contentID", contentID);
                if (bookinfo.containsKey("IsSerial")
                        && (bookinfo.get("IsSerial") != null)) {
                    if (bookinfo.get("IsSerial").toString().equals("0")) {
                        bundleToSend.putBoolean("IsSerial", false);
                        bundleToSend.putBoolean("IsFinish", true);
                    } else {
                        bundleToSend.putBoolean("IsSerial", true);
                        if (bookinfo.get("IsFinish").toString().equals("0")) {
                            bundleToSend.putBoolean("IsFinish", false);

                        } else {
                            bundleToSend.putBoolean("IsFinish", true);
                        }
                    }
                } else {
                    bundleToSend.putBoolean("IsSerial", false);
                    bundleToSend.putBoolean("IsFinish", false);
                }

                bundleToSend.putString("BookName", bookinfo.get("BookName")
                        .toString());

                tmpIntent.putExtras(bundleToSend);
                sendBroadcast(tmpIntent);
            } else if (vTag.equals("bookSummary")) {//跳至书籍摘要
                final Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
                final Bundle bundleToSend = new Bundle();
                bundleToSend.putString("act","com.pvi.ap.reader.activity.BookSummaryActivity"); 
                bundleToSend.putString("contentID", contentID);
                bundleToSend.putString("startType",  "allwaysCreate");
                bundleToSend.putString("pviapfStatusTip",  "进入书籍摘要...");
                tmpIntent.putExtras(bundleToSend);
                sendBroadcast(tmpIntent);
            }else if (vTag.equals("bookmark")) {
                // 添加书签
                Cursor cur = null;

                String where = Bookmark.BookmarkType + "='" + 1 + "'" + " and "
                + Bookmark.ContentId + "='" + contentID + "'" + " and "
                + Bookmark.ChapterId + "='" + chapterID + "'" + " and "
                + Bookmark.Position + "='"
                + total_pages.get(currentPage - 1) + "'";
                cur = managedQuery(Bookmark.CONTENT_URI, null, where, null,
                        null);
                if (cur!=null && cur.getCount() >= 1) {
                    pd = new PviAlertDialog(getParent());
                    pd.setTitle("温馨提示");
                    pd.setMessage("当前页书签已保存...");
                    pd.setCanClose(true);
                    pd.show();
                } else {
                    addSystemBookMark(false);
                    addBookMarkToDB(false);
                    Toast.makeText(ReadingOnlineActivity.this, "添加书签成功！", Toast.LENGTH_LONG).show();

//                  InactiveFunction.showResult(ReadingOnlineActivity.this,
//                          "添加书签", "添加书签成功！");
                }
            } else if (vTag.equals("bookmarks")) {
                // 我的书签
                InactiveFunction.GotoMyBookshelf(ReadingOnlineActivity.this,
                        "我的书签",contentID);
            } else if (vTag.equals("openNote")) {
                openNote(item);
            } else if (vTag.equals("reflash")) { //
                reflash = true;
                onResume();
            } else if (vTag.equals("presentbook")) {

                if (bookinfo.get("canPresent").equals("true")) {
                    Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
                    Bundle sndBundle1 = new Bundle();
                    sndBundle1.putString("act",
                    "com.pvi.ap.reader.activity.PresentToFriend");
                    //sndBundle1.putString("subscribeMode", "orderbook");
                    sndBundle1.putString("contentID", contentID);
                    intent1.putExtras(sndBundle1);
                    sendBroadcast(intent1);
                } else {
                    final PviAlertDialog pd = new PviAlertDialog(getParent());
                    pd.setTitle("温馨提示");
                    pd.setMessage("仅按本收费书籍支持赠送好友！");
                    pd.setCanClose(true);
                    pd.show();
                }
            }
            
        
        }};



		private boolean autopage = true;
		private int lookNum;
		private int num = 0;
		private int page;
		private String lookNext;
		protected int sta;
		protected int end;
		protected boolean addannotation;
		protected int addnote = 1;

		private List<Integer> getFinds(String findFile, int chapterOrPage) {
			int startPos = 0;
			int foundPos = -1; // -1 represents not found.
			List<Integer> foundItems = new ArrayList<Integer>();
			String chapterAndPage = "";
			if (chapterOrPage == 0) {
				// 表示本章查找
				chapterAndPage = spanned.toString().toLowerCase();
			} else {
				// 当前页查找
				chapterAndPage = charSequence.toString().toLowerCase();
			}
			do {
				foundPos = chapterAndPage.indexOf(findFile.toLowerCase());
				if (foundPos == -1) {
					break;
				}
				startPos = startPos + foundPos;
				foundItems.add(startPos);
				chapterAndPage = " " + chapterAndPage.substring(foundPos + 1);

			} while (foundPos > -1);
			return foundItems;
		}

		/**
		 * TXT搜索
		 */
		private void txtSerchContent() {
			if (look.getText() == null || look.getText().toString().equals("")) {
				Toast.makeText(this,
						getResources().getString(R.string.SerchComtent),
						Toast.LENGTH_SHORT).show();
				return;
			}
			String lookup = look.getText().toString();
			autopage = true;
			SpannableStringBuilder style = null;
			List<Integer> finds = getFinds(lookup, 0);
			if (finds.size() == 0) {
				Toast.makeText(this,
						getResources().getString(R.string.NoRelashipComent),
						Toast.LENGTH_SHORT).show();
				return;
			} else if (bSearch.getText().equals(
					getResources().getString(R.string.Serach))) {
				lookNum = finds.get(num);
				if (total_pages.size() == 1) {
					charSequence = spanned;
					page = 1;
				} else if (lookNum < total_pages.get(total_pages.size() - 1)) {
					for (int i = 0; i < total_pages.size() - 1; i++) {
						if (lookNum < total_pages.get(i + 1)) {
							page = i + 1;
							break;
						}
					}
					charSequence = spanned.subSequence(total_pages.get(page - 1),
							total_pages.get(page));
				} else {
					page = total_pages.size();
					charSequence = spanned.subSequence(total_pages.get(page - 1),
							chapterIdLength - 1);
				}

				style = new SpannableStringBuilder(charSequence);
				if (lookNum - total_pages.get(page - 1) == 0) {
					style.setSpan(new StyleSpan(Typeface.BOLD), 0, lookNum
							- total_pages.get(page - 1),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					style.setSpan(new URLSpan(charSequence.toString()), lookNum
							- total_pages.get(page - 1), lookNum
							- total_pages.get(page - 1) + lookup.length(),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				} else if (page < total_pages.size()
						&& (lookNum < total_pages.get(page) && lookNum
								+ lookup.length() > total_pages.get(page))) {
					style.setSpan(new StyleSpan(Typeface.BOLD), 0, lookNum,
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					style
					.setSpan(new URLSpan(charSequence.toString()), lookNum,
							total_pages.get(page),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				} else {
					style.setSpan(new StyleSpan(Typeface.BOLD), 0, lookNum - 1
							- total_pages.get(page - 1),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					style.setSpan(new URLSpan(charSequence.toString()), lookNum
							- total_pages.get(page - 1), lookNum
							- total_pages.get(page - 1) + lookup.length(),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				num++;
				lookNext = lookup;
				bSearch.setText(R.string.Next);
				currentPage = page;
				totalPage = total_pages.size();
				updatePagerinfo(currentPage + "/" + totalPage);
				content.setText(style);
			} else {
				if (lookNext.equals(lookup)) {
					if (num < finds.size()) {
						lookNum = finds.get(num);
						if (total_pages.size() == 1) {
							charSequence = spanned;
							page = 1;
						} else if (lookNum < total_pages
								.get(total_pages.size() - 1)) {
							for (int i = 0; i < total_pages.size() - 1; i++) {
								if (lookNum < total_pages.get(i + 1)) {
									page = i + 1;
									break;
								}
							}
							charSequence = spanned.subSequence(total_pages
									.get(page - 1), total_pages.get(page));
						} else {
							page = total_pages.size();
							charSequence = spanned.subSequence(total_pages
									.get(page - 1), chapterIdLength - 1);
						}

						style = new SpannableStringBuilder(charSequence);
						if (lookNum - total_pages.get(page - 1) == 0) {
							style.setSpan(new StyleSpan(Typeface.BOLD), 0, lookNum
									- total_pages.get(page - 1),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							style.setSpan(new URLSpan(charSequence.toString()),
									lookNum - total_pages.get(page - 1), lookNum
									- total_pages.get(page - 1)
									+ lookup.length(),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						} else if (page < total_pages.size()
								&& (lookNum < total_pages.get(page) && lookNum
										+ lookup.length() > total_pages.get(page))) {
							style.setSpan(new StyleSpan(Typeface.BOLD), 0, lookNum,
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							style.setSpan(new URLSpan(charSequence.toString()),
									lookNum, total_pages.get(page),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						} else {
							style.setSpan(new StyleSpan(Typeface.BOLD), 0, lookNum
									- 1 - total_pages.get(page - 1),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							style.setSpan(new URLSpan(charSequence.toString()),
									lookNum - total_pages.get(page - 1), lookNum
									- total_pages.get(page - 1)
									+ lookup.length(),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						num++;
						currentPage = page;
		                totalPage = total_pages.size();
		                updatePagerinfo(currentPage + "/" + totalPage);
						content.setText(style);
					} else {
						Toast.makeText(this,
								getResources().getString(R.string.LastOne),
								Toast.LENGTH_SHORT).show();
						style = new SpannableStringBuilder(charSequence);
						if (lookNum - total_pages.get(page - 1) == 0) {
							style.setSpan(new StyleSpan(Typeface.BOLD), 0, lookNum
									- total_pages.get(page - 1),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							style.setSpan(new URLSpan(charSequence.toString()),
									lookNum - total_pages.get(page - 1), lookNum
									- total_pages.get(page - 1)
									+ lookup.length(),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						} else if (page < total_pages.size()
								&& (lookNum < total_pages.get(page) && lookNum
										+ lookup.length() > total_pages.get(page))) {
							style.setSpan(new StyleSpan(Typeface.BOLD), 0, lookNum,
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							style.setSpan(new URLSpan(charSequence.toString()),
									lookNum, total_pages.get(page),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						} else {
							style.setSpan(new StyleSpan(Typeface.BOLD), 0, lookNum
									- 1 - total_pages.get(page - 1),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							style.setSpan(new URLSpan(charSequence.toString()),
									lookNum - total_pages.get(page - 1), lookNum
									- total_pages.get(page - 1)
									+ lookup.length(),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						content.setText(style);
					}
				} else {
					finds = getFinds(lookup, 0);
					num = 0;
					if (finds.size() == 0) {
						Toast
						.makeText(
								this,
								getResources().getString(
										R.string.NoRelashipComent),
										Toast.LENGTH_SHORT).show();
						return;
					} else if (num < finds.size()) {
						lookNum = finds.get(num);
						if (total_pages.size() == 1) {
							charSequence = spanned;
							page = 1;
						} else if (lookNum < total_pages
								.get(total_pages.size() - 1)) {
							for (int i = 0; i < total_pages.size() - 1; i++) {
								if (lookNum < total_pages.get(i + 1)) {
									page = i + 1;
									break;
								}
							}
							charSequence = spanned.subSequence(total_pages
									.get(page - 1), total_pages.get(page));
						} else {
							page = total_pages.size();
							charSequence = spanned.subSequence(total_pages
									.get(page - 1), chapterIdLength - 1);
						}
						lookNext = lookup;
						style = new SpannableStringBuilder(charSequence);
						if (lookNum - total_pages.get(page - 1) == 0) {
							style.setSpan(new StyleSpan(Typeface.BOLD), 0, lookNum
									- total_pages.get(page - 1),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							style.setSpan(new URLSpan(charSequence.toString()),
									lookNum - total_pages.get(page - 1), lookNum
									- total_pages.get(page - 1)
									+ lookup.length(),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						} else if (page < total_pages.size()
								&& (lookNum < total_pages.get(page) && lookNum
										+ lookup.length() > total_pages.get(page))) {
							style.setSpan(new StyleSpan(Typeface.BOLD), 0, lookNum,
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							style.setSpan(new URLSpan(charSequence.toString()),
									lookNum, total_pages.get(page),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						} else {
							style.setSpan(new StyleSpan(Typeface.BOLD), 0, lookNum
									- 1 - total_pages.get(page - 1),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							style.setSpan(new URLSpan(charSequence.toString()),
									lookNum - total_pages.get(page - 1), lookNum
									- total_pages.get(page - 1)
									+ lookup.length(),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						num++;
						currentPage = page;
			              totalPage = total_pages.size();
			                updatePagerinfo(currentPage + "/" + totalPage);

						content.setText(style);
					} else {
						Toast.makeText(this,
								getResources().getString(R.string.LastOne),
								Toast.LENGTH_SHORT).show();
						style = new SpannableStringBuilder(charSequence);
						if (lookNum - total_pages.get(page - 1) == 0) {
							style.setSpan(new StyleSpan(Typeface.BOLD), 0, lookNum
									- total_pages.get(page - 1),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							style.setSpan(new URLSpan(charSequence.toString()),
									lookNum - total_pages.get(page - 1), lookNum
									- total_pages.get(page - 1)
									+ lookup.length(),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						} else if (page < total_pages.size()
								&& (lookNum < total_pages.get(page) && lookNum
										+ lookup.length() > total_pages.get(page))) {
							style.setSpan(new StyleSpan(Typeface.BOLD), 0, lookNum,
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							style.setSpan(new URLSpan(charSequence.toString()),
									lookNum, total_pages.get(page),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						} else {
							style.setSpan(new StyleSpan(Typeface.BOLD), 0, lookNum
									- 1 - total_pages.get(page - 1),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							style.setSpan(new URLSpan(charSequence.toString()),
									lookNum - total_pages.get(page - 1), lookNum
									- total_pages.get(page - 1)
									+ lookup.length(),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						content.setText(style);
					}
				}
			}

		}

		private void moveFile() {
			content.setOnTouchListener(new OnTouchListener() {

				private boolean in_annotation_state = false;
				private int annotation_start_pos = 0;
				// StyleSpan selection_span;
				URLSpan selection_span;
				private int startX = 0, startY = 0, endX = 0, endY = 0;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					Layout layout = content.getLayout();
					int line = 0;
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						// \\line=layout.getLineForVertical(tv.getScrollY()+(int)event.getY());
						startX = (int) event.getX();
						startY = (int) event.getY();
						line = layout.getLineForVertical((int) event.getY());
						sta = layout.getOffsetForHorizontal(line, (int) event
								.getX());
						end = sta;
						// try {
						// int Position=total_pages.get(page-1)+sta;
						// for(int i=0;i<commentList.size();i++){
						// CommentInfo comm = commentList.get(i);
						// if(comm.getStartPostion()<=Position&&Position<=comm.getEndPostion()){
						// Comment=comm.getComment();
						// dialogs = buildDialogfile(MebViewFileActivity.this);
						// dialogs.show();
						// }
						// }
						// } catch (Exception e) {
						// // TODO: handle exception
						// return true;
						// }

						if (addannotation || addnote == 2) {
							in_annotation_state = true;
							{
								int offset = 0;
								int h_offset = 0;
								// offset =layout.getLineStart(line);
								h_offset = layout.getOffsetForHorizontal(line,
										(float) event.getX());
								offset = offset + h_offset;
								// \\showDialog(1);
								// tv.setSelection(1, offset);
								annotation_start_pos = offset;
							}
						}
						// \\return true;
						return true;
					case MotionEvent.ACTION_MOVE:
						// \\line=layout.getLineForVertical(tv.getScrollY()+
						// (int)event.getY());
						line = layout.getLineForVertical((int) event.getY());
						end = layout.getOffsetForHorizontal(line, (int) event
								.getX());
						if (addannotation || addnote == 2) {
							if (in_annotation_state) {
								int offset = 0;
								int h_offset = 0;
								// \\offset =layout.getLineStart(line);
								h_offset = layout.getOffsetForHorizontal(line,
										(int) event.getX());
								offset = offset + h_offset;
								// \\showDialog(1);
								CharSequence char_seq = content.getText();
								SpannableStringBuilder span_builder = (SpannableStringBuilder) char_seq;
								if (char_seq != null) {
									if (selection_span == null) {
										// selection_span = new
										// StyleSpan(Typeface.BOLD_ITALIC);
										selection_span = new URLSpan(char_seq
												.toString());
									} else {
										span_builder.removeSpan(selection_span);
										if (offset >= annotation_start_pos) {
											span_builder
											.setSpan(
													selection_span,
													annotation_start_pos,
													offset,
													Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
										} else {
											span_builder
											.setSpan(
													selection_span,
													offset,
													annotation_start_pos,
													Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
										}
									}
								}
								if (offset > annotation_start_pos) {
									// \\
									// tv.setSelection(annotation_start_pos,offset);
								} else {
									// \\
									// tv.setSelection(offset,annotation_start_pos);
								}
							}
						}

						return true;
					case MotionEvent.ACTION_UP:
						endX = (int) event.getX();
						endY = (int) event.getY();
						if (addnote == 2) {
							in_annotation_state = false;
							CharSequence char_seq = content.getText();
							SpannableStringBuilder span_builder = (SpannableStringBuilder) char_seq;
							if (char_seq != null) {
								if (selection_span != null) {
									span_builder.removeSpan(selection_span);
									selection_span = null;
								}
							}
							if (sta > end) {
								int temp = sta;
								sta = end;
								end = temp;
							} else if (sta == end) {
								return true;
							}
							String noteInfoStr = char_seq.toString().substring(sta,
									end);
							final PviAlertDialog pad = new PviAlertDialog(
									ReadingOnlineActivity.this);
							LayoutInflater inflater = LayoutInflater
							.from(ReadingOnlineActivity.this);
							final View entryView = inflater.inflate(
									R.layout.addnewnote, null);
							EditText et = (EditText) entryView
							.findViewById(R.id.notecontent);
							et.setText(noteInfoStr);
							pad.setTitle("添加便笺");
							pad.setView(entryView);
							pad.setCanClose(true);
							pad.setButton(getString(R.string.txtPositiveButton),
									new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText et1 = (EditText) entryView
									.findViewById(R.id.notetile);
									EditText et2 = (EditText) entryView
									.findViewById(R.id.notecontent);
									String str1 = et1.getText().toString();
									String str2 = et2.getText().toString();
									if ("".equals(str1)) {
										PviAlertDialog pad3 = new PviAlertDialog(
												ReadingOnlineActivity.this);
										pad3.setTitle("便笺添加提示");

										pad3.setMessage("添加成功");

										pad3.setMessage("便笺标题不能空");

										pad3.setCanClose(true);
										pad3
										.setButton(
												getString(R.string.txtPositiveButton),
												new DialogInterface.OnClickListener() {

													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														dialog
														.dismiss();
													}
												});
										pad3.show();
										return ;
									}
									Map map = new HashMap();
									map.put("noteName", str1);
									map.put("noteText", str2);

									AddNote an = new AddNote(
											ReadingOnlineActivity.this);
									boolean su = an.addNoteInfo(map);

									PviAlertDialog pad2 = new PviAlertDialog(
											ReadingOnlineActivity.this);
									pad2.setTitle("便笺添加 提示");
									if (su) {
										pad2.setMessage("添加成功");
									} else {
										pad2.setMessage("添加失败");
									}
									pad2.setCanClose(true);
									pad2
									.setButton(
											getString(R.string.txtPositiveButton),
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog
													.dismiss();
												}
											});
									pad2.show();
								}

							});
							pad.setButton2(getString(R.string.txtCancelButton),
									new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									pad.dismiss();
								}

							});
							pad.show();
						} else {
							if (autopage) {
								if (startX - endX > 100
										&& startX - endX >= Math.abs(startY - endY)) {
									OnNextpage();
								}  else if (endX - startX > 100
										&& endX - startX >= Math.abs(startY - endY)) {
									OnPrevpage();
								}else if(startY -endY > 100 && startY - endY >= Math.abs(startX - endX)) {
								    OnNextpage();
								}else if(endY - startY > 100 && endY - startX >= Math.abs(startX - endX)){
								    OnPrevpage();
								}

							}
						}

						return true;
					}

					return true;
				}
			});

		}

		private void openNote(PviUiItem item) {
			if (addnote == 1) {
				item.text="关闭便笺";
				addnote = 2;
			} else if (addnote == 2) {
				item.text="开启便笺";
				addnote = 1;
			}
		}


		@Override
		public void onConfigurationChanged(Configuration newConfig) {
			orientation = newConfig.orientation;
			if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
				setContentView(R.layout.onlinereadpagestyleoperation);
			}else{
				setContentView(R.layout.onlinereadpagestyle2);
			}
			bindEvent();
			moveFile();
			content.setTextSize(Float.parseFloat(fontsize));
			content.setLineSpacing(this.lineSpac,1);
			super.onConfigurationChanged(newConfig);
		}

	private void compPageCounter() {
		if (((GlobalVar) getApplication()).deviceType == 1) {
			pageCounter++;
			if (pageCounter == 6) {
				pageCounter = 0;
				// gc16 full flash window
				Logger.d(TAG, "gc16 full");
//				getWindow().getDecorView().getRootView().invalidate(
//						View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16
//								| View.EINK_UPDATE_MODE_FULL);
			} else {
				Logger.d(TAG, "DU content");
			}
		}
	}
	
	private void resumeUi(){
	    mHandler.post(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                inChapterChanging = false;
                isInternalSwitch = false;
            }});

	}
	
	private void updateIntent(){
        //重设intent
	    
	    revBundle.putString("FontSize",""+m_size);
	    revBundle.putString("LineSpace",""+lineSpac);

	    
        revBundle.putString("ChapterID",chapterID);
        revBundle.putString("ContentID",contentID);
        final Intent intent = getIntent();
        intent.putExtras(revBundle);
        setIntent(intent);
	}
	
	/**
	 * 载入1章新内容
	 * 
	 * 
	 * 改变的成员变量：
	 * content 页面文本
	 * fontsize  字体大小
	 * linespace  行距
	 */
	private void load(){
	    
	}
	
    /**
     * 显示1章新内容
     * 
     * 参数：
     * 
     * 使用的成员变量：
     * content 页面文本
     * fontsize  字体大小
     * linespace  行距
     */
	private void show(){
	    
	}

    @Override
    public void OnNextchap() {
        if(inChapterChanging){
            //                      Toast.makeText(ReadingOnlineActivity.this, "上一次操作处理中",
            //                              Toast.LENGTH_SHORT).show();
            return;
        }
        chapterchange = "nextchapter";
        ispagechanging = "nextchapter";
        if (!inChapterChanging  && (nextChapterID!=null&&nextChapterID.equals(chapterID))) {                    

            if(bookinfo!=null &&
                    "1".equals(bookinfo.get("IsSerial")) &&
                    "0".equals(bookinfo.get("IsFinish")) ){
                //对于连载中图书,提示框
                mHandler.sendEmptyMessage(PD3);
            }else{
                showMessage("本书最后一章","3000");
            }

            return;
        }
        long TimeStart = System.currentTimeMillis();
        Logger.i("Time", "NextChapter Pressed: "
                + Long.toString(TimeStart));
        inChapterChanging = true;
        isInternalSwitch = true;
        thread = new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                String str = getProductID(contentID, nextChapterID);
                Logger.e(TAG,"getProductID(...:"+str);
                if (str.equals("0")) {
                    chapterID = nextChapterID;
                    chaptername = nextChapterName;
                    mHandler.sendEmptyMessage(4);
                    return;
                } else if (str.equals("1")) {

                    if (bookinfo.containsKey("fascicleFlag")) {
                        if (bookinfo.get("fascicleFlag")
                                .equals("0")
                                || (fascicleID!=null && fascicleID.equals(nextfascicleID))
                        ) {
                            mHandler.sendEmptyMessage(2);
                        } else {
                            // 处理分册。。。。
                            mHandler.sendEmptyMessage(3);
                        }
                    } else {
                        mHandler.sendEmptyMessage(2);
                    }
                    return;
                } else {
                    return;
                }
            }

        };
        thread.start();
        super.OnNextchap();
    }

    @Override
    public void OnNextpage() {
        // 下一页
        ispagechanging = "nextpage";
        goToNextPage();
        super.OnNextpage();
    }

    @Override
    public void OnPrevchap() {
        if(inChapterChanging){
            return;
        }
        if (!ispagechanging.equals("prepage")) {
            ispagechanging = "prechapter";
        }

        chapterchange = "prechapter"; // "", "prechapter",
        // "nextchapter"

        if (!inChapterChanging  && (preChapterID!=null && preChapterID.equals(chapterID))) {
            showMessage("已是第一章");
            currentPage = 1;
            return;
        }

        inChapterChanging = true;
        isInternalSwitch = true;

        long TimeStart = System.currentTimeMillis();
        Logger.i("Time", "PreChapter Pressed: "
                + Long.toString(TimeStart));

        thread = new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();

                String str = getProductID(contentID, preChapterID);                     

                
                if (str.equals("0")) {
                    chapterID = preChapterID;
                    chaptername = preChapterName;
                    mHandler.sendEmptyMessage(4);
                    return;
                } else if (str.equals("1")) {

                    if (bookinfo.containsKey("fascicleFlag")) {
                        if (bookinfo.get("fascicleFlag")
                                .equals("0")
                                || (fascicleID!=null && fascicleID.equals(prefascicleID))) {
                            mHandler.sendEmptyMessage(2);
                        } else {
                            // 处理分册。。。。
                            mHandler.sendEmptyMessage(3);
                        }
                    } else {
                        mHandler.sendEmptyMessage(2);
                    }
                    
                    return;
                } else {
                    // Toast.makeText(ReadingOnlineActivity.this,
                    // "联网获取上一章订购信息失败！", Toast.LENGTH_SHORT).show();
                    

                    return;
                }
            }

        };
        thread.start();
        super.OnPrevchap();
    }

    @Override
    public void OnPrevpage() {
        // 上一页
        ispagechanging = "prepage";
        goToUpPage();
        super.OnPrevpage();
    }
}
