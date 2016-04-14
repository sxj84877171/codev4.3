package com.pvi.ap.reader.activity;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.InactiveFunction;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.commentInfo;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.common.beans.BookChapterBean;
import com.pvi.ap.reader.common.beans.BookCommentBean;
import com.pvi.ap.reader.common.beans.BookFunctionBean;
import com.pvi.ap.reader.common.beans.BookRelationshipBean;
import com.pvi.ap.reader.common.beans.BookSummaryBean;
import com.pvi.ap.reader.common.beans.Chapter;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.Bookmark;
import com.pvi.ap.reader.data.content.Favorites;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;
import com.pvi.ap.reader.view.BookSummaryView;

public class BookSummaryActivity extends PviActivity {
	private String TAG = "BookSummaryActivity";

	/*** view ��Ӧ�齨 ***/
	private BookSummaryView bookSummaryView = null;

	/*** View ����Ҫ���� ***/
	private BookSummaryBean bookSunmaryInfo = new BookSummaryBean();
	private BookFunctionBean bookFunction = new BookFunctionBean();
	private BookChapterBean bookChapterBean = new BookChapterBean();
	private BookCommentBean bookCommentBean = new BookCommentBean();
	private BookRelationshipBean bookRelationshipBean = new BookRelationshipBean();
	

	/*** �� ���� ****/
	private String contentID = null;
	private String catalogOrdered = null;
	private String fee = null;
	private String catalogID = null;
	private String catalogName = null;
	private boolean resumeShow = false ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.booksummarystyle2);
		super.onCreate(savedInstanceState);

		Intent revIntent = getIntent();
		Bundle revBundle = null;
		revBundle = revIntent.getExtras();
		contentID = revBundle.getString("contentID");

		if (revBundle.containsKey("catalogOrdered")) {
			catalogOrdered = revBundle.getString("catalogOrdered");
		}
		if (revBundle.containsKey("fee")) {
			fee = revBundle.getString("fee");
		}
		if (revBundle.containsKey("catalogID")) {
			catalogID = revBundle.getString("catalogID");
		}
		if (revBundle.containsKey("catalogName")) {
			catalogName = revBundle.getString("catalogName");
		}

		Logger.i(TAG, "contentID:" + contentID);

		dataThread.start();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	/***
	 * �÷�����Ҫ���齨���󶨼�����
	 */
	@Override
	public void bindEvent() {
		bookSummaryView = (BookSummaryView) findViewById(R.id.booksummaryView);
		
		// ���ݼ�������
		bookSunmaryInfo.contentAbstractListen = new BookSummaryBean.ContentAbstractListen() {
			@Override
			public void onClick() {
				if (bookSunmaryInfo.contentAbstract != null) {
					InactiveFunction.showResult(BookSummaryActivity.this,
							"���ݼ��", bookSunmaryInfo.contentAbstract);
				}
			}
		};
		
		// �����Ķ�������
		bookFunction.readonlineListen = new BookFunctionBean.ReadonlineListen() {
			
			@Override
			public void onClick() {
				if (bookFunction.canreadonline) {
					Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle sndBundle1 = new Bundle();
					sndBundle1.putString("act",
							"com.pvi.ap.reader.activity.ReadingOnlineActivity");
					sndBundle1.putString("startType", "allwaysCreate");
					sndBundle1.putString("haveTitleBar", "1");
					sndBundle1.putString("ContentID", contentID);
					sndBundle1.putString("ChapterName",
							bookChapterBean.chapter[0].chapterName);
					sndBundle1.putString("ChapterID",
							bookChapterBean.chapter[0].chapterID);
					sndBundle1.putString("ChargeType",
							bookChapterBean.chapter[0].chargeType);
					intent1.putExtras(sndBundle1);
					sendBroadcast(intent1);
				} else {
					Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle sndBundle1 = new Bundle();
					sndBundle1.putString("act",
					"com.pvi.ap.reader.activity.ReadingOnlineActivity");
					sndBundle1.putString("ContentID", contentID);
					sndBundle1.putString("haveTitleBar", "1");
					sndBundle1.putString("ChapterID", bookFunction.map.get("chapterid")
							.toString());
					if(bookFunction.map.containsKey("FontSize"))
					{
						sndBundle1.putString("FontSize", bookFunction.map.get("FontSize")
								.toString());
					}
					if(bookFunction.map.containsKey("readposition"))
					{
						sndBundle1.putString("Position", bookFunction.map.get("readposition")
								.toString());
					}
					if(bookFunction.map.containsKey("LineSpace"))
					{
						sndBundle1.putString("LineSpace", bookFunction.map.get("LineSpace")
								.toString());
					}
					intent1.putExtras(sndBundle1);
					sendBroadcast(intent1);
					return;
				}
			}
		};
		bookCommentBean.commentAllListen = new BookCommentBean.CommentListen() {
			
			@Override
			public void onClick() {
				showMessage("���������б�...","20000");
				InactiveFunction.comment(BookSummaryActivity.this, contentID, true);
			}
		};
		
		bookCommentBean.commentListen[0] = new BookCommentBean.CommentListen() {
			@Override
			public void onClick() {
				showMessage("���������б�...","20000");
				InactiveFunction.comment(BookSummaryActivity.this, contentID, true);
			}
		};
		
		
		// ���ؼ�����
		bookFunction.downreadListen = new BookFunctionBean.DownreadListen() {
			@Override
			public void onClick() {
				Logger.i(TAG, "downreadListen");

				Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle sndBundle1 = new Bundle();
				sndBundle1.putString("act",
						"com.pvi.ap.reader.activity.SubscribeProcess");
				sndBundle1.putString("contentID", contentID);

				if (!bookSunmaryInfo.isSerial) {
					sndBundle1.putString("isSerial", "0");
					sndBundle1.putString("isFinish", "0");
				} else {
					sndBundle1.putString("isSerial", "1");
					if (!bookSunmaryInfo.isFinish) {
						sndBundle1.putString("isFinish", "0");
					} else {
						sndBundle1.putString("isFinish", "1");
					}
				}
				if (!"".equals(catalogOrdered)) {
					sndBundle1.putString("catalogOrdered", catalogOrdered);
				}
				if (!"".equals(fee)) {
					sndBundle1.putString("fee", fee);
				}
				if (!"".equals(catalogID)) {
					sndBundle1.putString("catalogID", catalogID);
				}
				if (!"".equals(catalogName)) {
					sndBundle1.putString("catalogName", catalogName);
				}
				sndBundle1.putString("subscribeMode", "download");
				if (bookFunction.canbookupdate) {
					sndBundle1.putString("fascicle", "1");
				} else {
					sndBundle1.putString("fascicle", "0");
				}
				sndBundle1.putString("chargeMode", bookSunmaryInfo.chargeMode);
				sndBundle1.putString("chargeTip", bookSunmaryInfo.charges);
				sndBundle1.putString("bookName", bookSunmaryInfo.title);
				sndBundle1.putString("authorName", bookSunmaryInfo.auther);
				intent1.putExtras(sndBundle1);
				sendBroadcast(intent1);
			}
		};
		// Ԥ�����¼�����
		bookFunction.reservationUpdateListen = new BookFunctionBean.ReservationUpdateListen() {
			@Override
			public void onClick() {
				if (bookFunction.canbookupdate) {
					showMessage("����Ԥ��...");
					new Thread() {
						@Override
						public void run() {
							super.run();
							HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
							HashMap ahmNamePair = CPManagerUtil
									.getAhmNamePairMap();
							ahmNamePair.put("contentId", contentID);
							HashMap responseMap = null;
							try {
								// ��POST����ʽ��������
								responseMap = CPManager.bookUpdate(
										ahmHeaderMap, ahmNamePair);
								if (!responseMap.get("result-code").toString()
										.trim().contains("result-code: 0")) {
									Bundle temp = new Bundle();
									temp.putString("Ret", responseMap.get(
											"result-code").toString());
									Message msg = new Message();
									msg.what = 6;
									msg.setData(temp);
									handler.sendMessage(msg);
									Intent intent1 = new Intent(
											MainpageActivity.HIDE_TIP);
									sendBroadcast(intent1);
									return;
								}
							} catch (SocketTimeoutException e) {
								Logger.e("Reader", e);
								handler.sendEmptyMessage(5);
								return;
							} catch (Exception e) {
								Logger.e("Reader", e);
								handler.sendEmptyMessage(4);
								return;
							}
							byte[] responseBody = (byte[]) responseMap
									.get("ResponseBody");
							// ���ݷ����ֽ����鹹��DOM
							Document dom = null;
							try {
								dom = CPManagerUtil
										.getDocumentFrombyteArray(responseBody);
							} catch (Exception e) {
								Logger.e("Reader", e);
								handler.sendEmptyMessage(4);
								return;
							}
							Element root = dom.getDocumentElement();
							String HintStr = "";
							NodeList nl = root
									.getElementsByTagName("bookUpdateMessage");
							Element temp = (Element) nl.item(0);
							HintStr = temp.getFirstChild().getNodeValue();
							Bundle tempbundle = new Bundle();

							tempbundle.putString("HintStr", HintStr);
							Message msg = new Message();
							msg.what = 7;
							msg.setData(tempbundle);
							handler.sendMessage(msg);
						}
					}.start();
					bookFunction.canbookupdate = false ;
				}
				}
		};
		bookFunction.collectionreadListen = new BookFunctionBean.CollectionreadListen() {
			@Override
			public void onClick() {
				if (!"�ղر���".equals(bookFunction.collectionread)) {
					showMessage("�����鼮�ֲ��б�...");
					Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle sndBundle1 = new Bundle();
					sndBundle1.putString("act",
							"com.pvi.ap.reader.activity.FascicleListActivity");
					sndBundle1.putString("haveTitleBar", "1");
					sndBundle1.putString("startType", "allwaysCreate");
					sndBundle1.putString("contentID", contentID);
					sndBundle1.putString("BookName", bookSunmaryInfo.title);
					intent1.putExtras(sndBundle1);
					sendBroadcast(intent1);
				}else{
					if (bookFunction.cancollectionread) {
						showMessage("�����ղ�...");
						final HashMap<String, String> map = new HashMap<String, String>();
						map.put("contentID", contentID);
						map.put("BookName", bookSunmaryInfo.title);
						map.put("AuthorName", bookSunmaryInfo.auther);
						map.put("SmallLogoUrl", bookSunmaryInfo.smallUrl);
						new Thread() {
							@Override
							public void run() {
								String retstr = InactiveFunction.addFavorite(
										BookSummaryActivity.this, map);
								if (retstr.equals("0")) {
									// ��ʾ��ӳɹ�
									handler.sendEmptyMessage(8);
								} else if (retstr.equals("1")) {
									// ��ʾ�û����ղ�
									handler.sendEmptyMessage(9);
								} else if (retstr.equals("2")) {
									// ��ʾ�û����ղ�
									handler.sendEmptyMessage(10);
								} else if (retstr
										.equals("result-code: 2028\r\n")) {
									// ��ʾ���ʧ��
									handler.sendEmptyMessage(11);
								} else {
									Bundle tempbundle = new Bundle();
									tempbundle.putString("RetCode", retstr);
									Message msg = new Message();
									msg.what = 12;
									msg.setData(tempbundle);
									handler.sendMessage(msg);
									bookFunction.cancollectionread = false;
								}
								super.run();
							}
						}.start();
					}
				}
			}
		};
		
		// Ŀ¼������ļ�����
		bookChapterBean.chapterAllListen = new BookChapterBean.ChapterListen() {
			@Override
			public void onClick() {
				showMessage("�����鼮Ŀ¼...");
				Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle sndBundle1 = new Bundle();
				sndBundle1.putString("act",
						"com.pvi.ap.reader.activity.BookCatalogActivity");
				sndBundle1.putString("startType", "allwaysCreate");
				sndBundle1.putString("contentID", contentID);
				sndBundle1.putBoolean("IsSerial", bookSunmaryInfo.isSerial);
				sndBundle1.putBoolean("IsFinish", bookSunmaryInfo.isFinish);
				sndBundle1.putString("BookName", bookSunmaryInfo.title);
				intent1.putExtras(sndBundle1);
				sendBroadcast(intent1);
			}
		};
		// ����������˻�ϲ��..  ������
		bookRelationshipBean.bookRelationshipListen[0] = new BookRelationshipBean.BookRelationshipListen() {
			@Override
			public void onClick() {
				showMessage("����������˻�ϲ��...");
				Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle sndBundle1 = new Bundle();
				sndBundle1.putString("haveTitleBar", "1");
				sndBundle1.putString("act",
				"com.pvi.ap.reader.activity.BookListActivity");
				sndBundle1.putString("startType", "allwaysCreate");
				sndBundle1.putString("contentId", contentID);
				sndBundle1.putString("listType", "othersread");
				intent1.putExtras(sndBundle1);
				sendBroadcast(intent1);
			}
		};
		// ͬ���Ƽ�... ������
		bookRelationshipBean.bookRelationshipListen[1] = new BookRelationshipBean.BookRelationshipListen() {
			@Override
			public void onClick() {
				showMessage("����ͬ���Ƽ�...");
				Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle sndBundle1 = new Bundle();
				sndBundle1.putString("haveTitleBar", "1");
				sndBundle1.putString("act",
				"com.pvi.ap.reader.activity.BookListActivity");
				sndBundle1.putString("startType", "allwaysCreate");
				sndBundle1.putString("contentId", contentID);
				sndBundle1.putString("listType", "similar");
				intent1.putExtras(sndBundle1);
				sendBroadcast(intent1);
			}
		};

		for (int i = 0; i < 4; i++) {
			final int index = i;
			bookChapterBean.chapterListen[i] = new BookChapterBean.ChapterListen() {
				@Override
				public void onClick() {
					Logger.i(TAG, "ChapterListen");
					showMessage("�����鼮�Ķ�...");
					Intent sndintent = new Intent(
							MainpageActivity.START_ACTIVITY);
					Bundle sndbundle = new Bundle();
					sndbundle.putString("act",
							"com.pvi.ap.reader.activity.ReadingOnlineActivity");
					sndbundle.putString("startType", "allwaysCreate");
					sndbundle.putString("haveTitleBar", "1");
					sndbundle.putString("ContentID", contentID);
					sndbundle.putString("ChargeType",
							bookChapterBean.chapter[index].chargeType);
					sndbundle.putString("ChapterID",
							bookChapterBean.chapter[index].chapterID);
					sndintent.putExtras(sndbundle);
					sendBroadcast(sndintent);
				}
			};
		}

		/// ����ͬ���Ƽ��Ͷ���������˻� ϲ��   ������
		for (int i = 0; i < 6; i++) {
			final int index = i ;
			bookRelationshipBean.bookListen[index] = new BookRelationshipBean.BookListen() {
				@Override
				public void onClick() {
					showMessage("����ͼ��ժҪ...");
					Intent sndintent = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle sndBundle1 = new Bundle();
					sndBundle1.putString("act","com.pvi.ap.reader.activity.BookSummaryActivity");
					sndBundle1.putString("startType", "allwaysCreate");
					sndBundle1.putString("haveTitleBar", "1");
					sndBundle1.putString("contentID", bookRelationshipBean.book1ContentID[index]);
					sndintent.putExtras(sndBundle1);
					sendBroadcast(sndintent);
				}
			};

		}
		registerWirelessSearch();
		super.bindEvent();
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Logger.i(TAG, "message");
			switch (msg.what) {
			case 0:
				final PviAlertDialog pd1 = new PviAlertDialog(getParent());
				pd1.setTitle("��ܰ��ʾ");
				pd1.setMessage("������ȡ�鼮ժҪ��Ϣʧ�ܣ�", Gravity.CENTER);
				pd1.setButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent("com.pvi.ap.reader.mainframe.BACK");
						Bundle bundle = new Bundle();
						bundle.putString("startType", "allwaysCreate");
						intent.putExtras(bundle);
						sendBroadcast(intent);
					}
				});
				pd1.show();
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				// Ԥ��ʹ�����쳣
				Intent intent1 = new Intent(MainpageActivity.HIDE_TIP);
				sendBroadcast(intent1);
				final PviAlertDialog pd = new PviAlertDialog(getParent());
				pd.setTitle("��ܰ��ʾ");
				pd.setMessage("�����쳣����Ԥ��ʧ�ܣ�", Gravity.CENTER);
				pd.setCanClose(true);
				pd.show();
				break;
			case 5:
				// Ԥ��ʱ���糬ʱ
				Intent intent2 = new Intent(MainpageActivity.HIDE_TIP);
				sendBroadcast(intent2);
				final PviAlertDialog pd11 = new PviAlertDialog(getParent());
				pd11.setTitle("��ܰ��ʾ");
				pd11.setMessage("���糬ʱ����Ԥ��ʧ�ܣ�", Gravity.CENTER);
				pd11.setCanClose(true);
				pd11.show();
				break;
			case 6:
				// Ԥ�������벻Ϊ0����ʾ
				Bundle temp = msg.getData();
				final PviAlertDialog pd2 = new PviAlertDialog(getParent());
				pd2.setTitle("��ܰ��ʾ");
				pd2.setMessage(Error.getErrorDescriptionForContent(temp
						.getString("Ret")), Gravity.CENTER);
				pd2.setCanClose(true);
				pd2.show();
				break;
			case 7:
				Bundle temp1 = msg.getData();
				PviAlertDialog builder = new PviAlertDialog(
						BookSummaryActivity.this);
				builder.setTitle("��ܰ��ʾ");
				builder.setMessage(temp1.getString("HintStr"), Gravity.CENTER);
				builder.setButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					    hideTip();
						return;
					}
				});
				builder.show();
				break;
			case 8:
				InactiveFunction.showResult(BookSummaryActivity.this, "��ܰ��ʾ",
						"�ղسɹ���");
				hideTip();
				break;
			case 9:
				InactiveFunction.showResult(BookSummaryActivity.this, "��ܰ��ʾ",
						"�����ղ�����ʱ!");
				hideTip();
				break;
			case 10:
				InactiveFunction.showResult(BookSummaryActivity.this, "��ܰ��ʾ",
						"�����ύ�ղ�����ʧ��!");
				hideTip();
				break;
			case 11:
				InactiveFunction.showResult(BookSummaryActivity.this, "��ܰ��ʾ",
						"�ղ��Ѵ����ޣ��뵽�ҵ��ղ���ɾ���ղؼ�¼��");
				hideTip();
				break;
			case 12:
				Bundle tempbundle = msg.getData();

				String retstr = tempbundle.getString("RetCode");
				InactiveFunction.showResult(BookSummaryActivity.this, "��ܰ��ʾ",
						Error.getErrorDescriptionForContent(retstr));
				hideTip();
				break;
			case 100:
				bookSummaryView.setDataSource(bookSunmaryInfo, bookFunction,
						bookChapterBean, bookCommentBean, bookRelationshipBean);
				showMe(this.getClass());
				break;
			}
			super.handleMessage(msg);
		}
	};

	private Thread dataThread = new Thread() {
		public void run() {
			bookFunction.map = InactiveFunction.getReadHistory(BookSummaryActivity.this,contentID);

			if(bookFunction.map != null && (bookFunction.map.get("chapterid")!=null) &&(!bookFunction.map.get("chapterid").toString().equals(""))){
			    Logger.e(TAG,"�����Ķ���¼");
			    bookFunction.readonline = "�����Ķ�";
				bookFunction.canreadonline = false ;
			}else{
			    Logger.e(TAG,"û�� �����Ķ���¼");
				bookFunction.readonline = "�����Ķ�" ;
				bookFunction.canreadonline = true ;
			}
			if(getBookSummaryInfo(contentID, 0)){
			    Logger.e(TAG,"ȡ�����鼮��Ϣ");
				handler.sendEmptyMessage(100);
			}else{
			    Logger.e(TAG,"ȡ�鼮��Ϣʧ��");
				handler.sendEmptyMessage(0);
			}
			//Logger.i(TAG, "thread stop...");
		};
	};
	
	private void getReadHistoryFromDB(String contentID){
		String columns[] = new String[] { Bookmark.FontSize,Bookmark.BookmarkId, Bookmark.LineSpace,Bookmark.CreatedDate,Bookmark.ChapterId };
		String where = Bookmark.BookmarkType + "='" + 0 + "'" + " and "	+ Bookmark.ContentId + "='" + contentID + "'";
		Cursor cur = managedQuery(Bookmark.CONTENT_URI, columns, where, null,null);
		if(cur != null){
			if(cur.moveToFirst()){
				bookFunction.map.put("", "");
			}
		}
		
	}


	protected void onResume() {
		super.onResume();
		if(resumeShow){
			showMe(this.getClass());
		}
	};

	public String getComment(String contentId) {
		Logger.i(TAG, "getComment:" + contentId);
		HashMap<String, Object> ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap<String, Object> ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		ahmNamePair.put("contentId", contentId);
		ahmNamePair.put("start", "1");
		ahmNamePair.put("count", "2");

		HashMap<String, Object> responseMap = null;
		try {
			responseMap = NetCache.getComment(ahmHeaderMap, ahmNamePair);
			if ((responseMap != null)
					&& (responseMap.get("result-code") != null)
					&& (!responseMap.get("result-code").toString().contains(
							"result-code: 0"))) {
				Logger.d(TAG, responseMap.get("result-code").toString());
				return responseMap.get("result-code").toString();
			}
		} catch (SocketTimeoutException e) {
			Logger.e(TAG, e);
			return "2";
		} catch (Exception e) {
			Logger.e(TAG, e);
			return "1";
		}
		if (responseMap == null) {
			return "1";
		}
		byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
		Document dom = null;
		try {
			String xml = new String(responseBody);
			xml = xml.replaceAll(">\\s+?<", "><");
			dom = CPManagerUtil.getDocumentFrombyteArray(xml.getBytes());
		} catch (Exception e) {
			Logger.e(TAG, e.getMessage());
			return "1";
		}

		Element root = dom.getDocumentElement();
		Element tempchild = null;
		NodeList nl = root.getElementsByTagName("totalRecordCount");

		if (nl != null) {
			tempchild = (Element) nl.item(0);
			if ((tempchild != null)) {
				if ((tempchild.getFirstChild() != null)) {
					bookCommentBean.totleCount = "("
							+ tempchild.getFirstChild().getNodeValue() + "��)";
				}
			}
		}

		nl = root.getElementsByTagName("Comment");

		NodeList tempnl = null;
		commentInfo tempcomment = null;

		for (int i = 0; i < 2; i++) {
			if (i < nl.getLength()) {
				tempcomment = new commentInfo();
				tempchild = (Element) nl.item(i);
				tempnl = tempchild.getElementsByTagName("commentID");
				if ((tempnl != null)) {
					tempcomment.setCommentID(tempnl.item(0).getFirstChild()
							.getNodeValue());
				}
				tempnl = tempchild.getElementsByTagName("fromUser");
				if ((tempnl != null)) {
					tempcomment.setFromUser(tempnl.item(0).getFirstChild()
							.getNodeValue());
				}
				tempnl = tempchild.getElementsByTagName("time");
				if ((tempnl != null)) {
					tempcomment.setTime(tempnl.item(0).getFirstChild()
							.getNodeValue());
				}
				tempnl = tempchild.getElementsByTagName("content");
				if ((tempnl != null)) {
					tempcomment.setComment(tempnl.item(0).getFirstChild()
							.getNodeValue());
				}
				tempnl = tempchild.getElementsByTagName("floorCount");
				if ((tempnl != null)) {
					tempcomment.setFloorCount(tempnl.item(0).getFirstChild()
							.getNodeValue());
				}
				tempnl = tempchild.getElementsByTagName("upTotal");
				if ((tempnl != null)) {
					tempcomment.setUpTotal(tempnl.item(0).getFirstChild()
							.getNodeValue());
				}
				tempnl = tempchild.getElementsByTagName("downTotal");
				if ((tempnl != null)) {
					tempcomment.setDownTotal(tempnl.item(0).getFirstChild()
							.getNodeValue());
				}
				bookCommentBean.cinfo[i] = tempcomment;
			}
		}
		return "0";
	}

	private boolean getBookSummaryInfo(String contentID, int update) {
		Logger.i(TAG, "getBookSummaryInfo:" + "contentID :" + contentID
				+ "  update:" + update);
		boolean netfail = false;
		Logger.i("Reader", "contentId: " + contentID);
		HashMap<String, Object> ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap<String, Object> ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		ahmNamePair.put("contentId", contentID);
		if (update == 1) {
			ahmNamePair.put("reflash", "CurrentBook");
		} else if (update == 2) {
			ahmNamePair.put("reflash", "OnlyIF");
		}
		HashMap<String, Object> responseMap = null;
		try {
			responseMap = NetCache.getContentInfo(ahmHeaderMap, ahmNamePair);
			if (responseMap != null) {
				if (!responseMap.get("result-code").toString().contains(
						"result-code: 0")) {
					showMessage("����ʧ�ܣ�", "2000");
					return false;
				}
			} else {
				if (update == 0) {
					showMessage("����ʧ�ܣ�", "2000");
					return false;
				} else {
					netfail = true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			showMessage("����ʧ�ܣ�", "2000");
			Logger.e("Reader", e.toString());
			if (update == 0) {
				return false;
			} else {
				netfail = true;
			}

		}

		Element root = null;
		if ((update == 2 && !netfail) || update == 0) {
			byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
			try {
				System.out.println("���ص�XMLΪ��");
				System.out.println(CPManagerUtil
						.getStringFrombyteArray(responseBody));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			Document dom = null;
			try {
				dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);

			} catch (Exception e) {
				e.printStackTrace();
				showMessage("����ʧ�ܣ�", "2000");
				if (update == 0) {
					return false;
				}
			}
			if (dom == null) {
				showMessage("����ʧ�ܣ�", "2000");
				return false;
			}
			root = dom.getDocumentElement();
			resolveStaticInfo(root);
			resolveDynamicInfo(root);
			getComment(contentID);
		}
		resumeShow = true ;
		return resumeShow;
	}

	private void resolveStaticInfo(Element root) {
		Logger.i(TAG, "resolveStaticInfo");
		NodeList nl = null;
		Element temp = null;
		nl = root.getElementsByTagName("contentName");
		if (nl != null) {
			Element contentName = (Element) nl.item(0);
			if ((contentName != null) && (contentName.getFirstChild() != null)) {
				bookSunmaryInfo.title = contentName.getFirstChild()
						.getNodeValue();

			}
		}
		nl = root.getElementsByTagName("isSerial");
		Element isSerial = (Element) nl.item(0);

		if (isSerial.getFirstChild().getNodeValue().equals("false")) {
			bookSunmaryInfo.isSerial = false;
		} else {
			bookSunmaryInfo.isSerial = true;
			NodeList overlist = root.getElementsByTagName("isFinish");
			Element isfinish = (Element) overlist.item(0);
			if (isfinish.getFirstChild().getNodeValue().equals("false")) {
				bookSunmaryInfo.isFinish = false;
			} else {
				bookSunmaryInfo.isFinish = true;
			}
		}

		nl = root.getElementsByTagName("authorID");
		if (nl != null) {
			temp = (Element) nl.item(0);
			if ((temp != null) && (temp.getFirstChild() != null)) {
				bookSunmaryInfo.authorID = temp.getFirstChild().getNodeValue();
			}
		}

		nl = root.getElementsByTagName("authorName");
		if (nl != null) {
			temp = (Element) nl.item(0);
			if ((temp != null) && (temp.getFirstChild() != null)) {
				bookSunmaryInfo.auther = temp.getFirstChild().getNodeValue();
			}
		}
		nl = root.getElementsByTagName("description");
		if (nl != null) {
			temp = (Element) nl.item(0);
			if ((temp != null) && (temp.getFirstChild() != null)) {
				bookSunmaryInfo.contentAbstract = temp.getFirstChild()
						.getNodeValue();
			}
		}

		nl = root.getElementsByTagName("coverLogo");
		if (nl != null) {
			temp = (Element) nl.item(0);
			if ((temp != null)) {
				if (temp.getFirstChild() != null) {
					String url = temp.getFirstChild().getNodeValue();
					bookSunmaryInfo.bookSurface = NetCache.GetNetImage(Config
							.getString("CPC_BASE_URL")
							+ url);
				}
			}
		}
		
		nl = root.getElementsByTagName("smallLogo");
		if (nl != null) {
			temp = (Element) nl.item(0);
			if ((temp != null)) {
				if (temp.getFirstChild() != null) {
					String url = temp.getFirstChild().getNodeValue();
					bookSunmaryInfo.smallUrl = url;
				}
			}
		}

		nl = root.getElementsByTagName("canPresent");
		if (nl != null) {
			temp = (Element) nl.item(0);
			if ((temp != null)) {
				if ((temp.getFirstChild() != null)) {
					bookSunmaryInfo.canPresent = Boolean.parseBoolean(temp
							.getFirstChild().getNodeValue());
				}
			}
		}

		nl = root.getElementsByTagName("canBookUpdate");
		if (nl != null) {
			temp = (Element) nl.item(0);
			if ((temp != null) && (temp.getFirstChild() != null)) {
				bookFunction.canbookupdate = Boolean.parseBoolean(temp
						.getFirstChild().getNodeValue());
			}
		}

		nl = root.getElementsByTagName("chargeMode");
		if (nl != null) {
			temp = (Element) nl.item(0);
			bookSunmaryInfo.chargeMode = temp.getFirstChild().getNodeValue();
		} else {
			bookSunmaryInfo.chargeMode = "4";
		}

		nl = root.getElementsByTagName("fascicleFlag");
		if (nl != null) {
			temp = (Element) nl.item(0);
			if ("0".equals(temp.getFirstChild().getNodeValue())) {
				bookFunction.collectionread = "�ղر���";
				bookFunction.cancollectionread = true;
			} else {
				bookFunction.collectionread = "�鿴�ֲ�";
				bookFunction.cancollectionread = true;
			}
		}

		nl = root.getElementsByTagName("VolumnInfo");
		Element voltemp = null;
		Element volchapter = null;

		NodeList volnode = null;
		NodeList chapterinfonode = null;
		NodeList chapterid = null;
		NodeList typenode = null;
		Element chapterinfoelement = null;
		int index = 0;
		for (int i = 0; i < nl.getLength(); i++) {
			voltemp = (Element) nl.item(i);
			chapterinfonode = voltemp.getElementsByTagName("ChapterInfo");
			for (int j = 0; j < chapterinfonode.getLength(); j++) {
				Chapter chapter = new Chapter();
				chapterinfoelement = (Element) chapterinfonode.item(j);
				volnode = chapterinfoelement
						.getElementsByTagName("chapterName");
				volchapter = (Element) volnode.item(0);
				chapter.chapterName = volchapter.getFirstChild().getNodeValue();
				chapterid = chapterinfoelement
						.getElementsByTagName("chapterID");
				volchapter = (Element) chapterid.item(0);
				chapter.chapterID = volchapter.getFirstChild().getNodeValue();

				typenode = chapterinfoelement.getElementsByTagName("type");
				volchapter = (Element) typenode.item(0);
				chapter.chargeType = volchapter.getFirstChild().getNodeValue();
				if("0".equals(chapter.chargeType)){
					chapter.chapterName += "(���)";
				}
				bookChapterBean.chapter[index++] = chapter;
				if (index >= 4)
					return;
			}
		}

	}

	private void resolveDynamicInfo(Element root) {
		Logger.i(TAG, "resolveDynamicInfo");
		NodeList nl;
		Element temp;
		nl = root.getElementsByTagName("wordCount");
		if (nl != null) {
			temp = (Element) nl.item(0);
			if ((temp != null)) {
				if ((temp.getFirstChild() != null)) {
					bookSunmaryInfo.codeNum = temp.getFirstChild()
							.getNodeValue();
				}
			}
		}
		nl = root.getElementsByTagName("readerValue");
		if (nl != null) {
			temp = (Element) nl.item(0);
			if ((temp != null)) {
				if ((temp.getFirstChild() != null)) {
					bookSunmaryInfo.readerNum = temp.getFirstChild()
							.getNodeValue();
				}
			}
		}

		nl = root.getElementsByTagName("size");
		if (nl != null) {
			temp = (Element) nl.item(0);
			if ((temp != null)) {
				if ((temp.getFirstChild() != null)) {
					bookSunmaryInfo.bookSize = temp.getFirstChild()
							.getNodeValue();
				}
			}
		}

		nl = root.getElementsByTagName("mark");
		if (nl != null) {
			temp = (Element) nl.item(0);
			if ((temp != null) || (temp.getFirstChild() != null)) {
				bookSunmaryInfo.starNum = Integer.parseInt(temp.getFirstChild()
						.getNodeValue());
			}
		}
		nl = root.getElementsByTagName("totalChapterCount");
		if (nl != null) {
			temp = (Element) nl.item(0);
			if ((temp != null) || (temp.getFirstChild() != null)) {
				bookSunmaryInfo.chapterNum = temp.getFirstChild()
						.getNodeValue();
			}
		}

		nl = root.getElementsByTagName("clickValue");
		if (nl != null) {
			temp = (Element) nl.item(0);
			if ((temp != null) || (temp.getFirstChild() != null)) {
				bookSunmaryInfo.clickNum = temp.getFirstChild().getNodeValue();
			}
		}

		nl = root.getElementsByTagName("favoriteValue");
		temp = (Element) nl.item(0);
		if (nl != null) {
			if ((temp != null) || (temp.getFirstChild() != null)) {
				bookSunmaryInfo.collectionNum = temp.getFirstChild()
						.getNodeValue();
			}
		}

		nl = root.getElementsByTagName("flowerValue");
		if (nl != null) {
			temp = (Element) nl.item(0);
			if (temp.getFirstChild() != null) {
				bookSunmaryInfo.flowersNum = temp.getFirstChild()
						.getNodeValue();
			}
		}

		nl = root.getElementsByTagName("recommendedValue");
		if (nl != null) {
			temp = (Element) nl.item(0);
			if ((temp != null) || (temp.getFirstChild() != null)) {
				bookSunmaryInfo.recommend = temp.getFirstChild().getNodeValue();
			}
		}

		nl = root.getElementsByTagName("chargeTip");
		if (nl != null) {
			temp = (Element) nl.item(0);
			if ((temp != null) && (temp.getFirstChild() != null)) {
				bookSunmaryInfo.charges = temp.getFirstChild().getNodeValue();
			}
		}

		nl = root.getElementsByTagName("isSerial");
		Element isSerial = (Element) nl.item(0);
		if (isSerial.getFirstChild().getNodeValue().equals("false")) {
			bookSunmaryInfo.isSerial = false;
			bookSunmaryInfo.title += "�������ء�";
		} else {
			NodeList updatechapternl = root
					.getElementsByTagName("LastestChapter");
			Element updatechapterelement = (Element) updatechapternl.item(0);
			Element tempelement = (Element) updatechapterelement
					.getElementsByTagName("chapterID").item(0);
			Chapter chapter = new Chapter();
			chapter.chapterID = tempelement.getFirstChild().getNodeValue();

			tempelement = (Element) updatechapterelement.getElementsByTagName(
					"chapterName").item(0);
			chapter.chapterName = tempelement.getFirstChild().getNodeValue();
			tempelement = (Element) updatechapterelement.getElementsByTagName(
					"type").item(0);
			chapter.chargeType = tempelement.getFirstChild().getNodeValue();
			bookSunmaryInfo.isSerial = true;
			NodeList overlist = root.getElementsByTagName("isFinish");
			Element isfinish = (Element) overlist.item(0);
			if (isfinish.getFirstChild().getNodeValue().equals("false")) {
				bookSunmaryInfo.isFinish = false;
				bookSunmaryInfo.title += "�������С�";
			} else {
				bookSunmaryInfo.isFinish = true;
				bookSunmaryInfo.title += "�������걾��";
			}
		}
		NodeList volnode = null;
		nl = root.getElementsByTagName("AssociateContent");
		Element associateroot = null;
		Element associatecontent = null;
		NodeList tempnl = null;
		Element tempelement = null;

		for (int i = 0; i < nl.getLength(); i++) {
			associateroot = (Element) nl.item(i);
			String type = ((Element) associateroot.getElementsByTagName("type")
					.item(0)).getFirstChild().getNodeValue();
			tempnl = associateroot.getElementsByTagName("ContentInfo");
			int in1 = 0, in2 = 3;
			for (int j = 0; j < tempnl.getLength(); j++) {
				tempelement = (Element) tempnl.item(j);
				volnode = tempelement.getElementsByTagName("contentName");
				associatecontent = (Element) volnode.item(0);

				if (type.equals("1")) {
					if (in1 < 3) {
						bookRelationshipBean.book1Name[in1] = associatecontent
								.getFirstChild().getNodeValue();
					}
				} else if (type.equals("3")) {
					if (in2 < 6) {
						bookRelationshipBean.book1Name[in2] = associatecontent
								.getFirstChild().getNodeValue();
					}
				}

				volnode = tempelement.getElementsByTagName("contentID");
				associatecontent = (Element) volnode.item(0);
				if (type.equals("1")) {
					if (in1 < 3) {
						bookRelationshipBean.book1ContentID[in1] = associatecontent
								.getFirstChild().getNodeValue();
					}
				} else if (type.equals("3")) {
					if (in2 < 6) {
						bookRelationshipBean.book1ContentID[in2] = associatecontent
								.getFirstChild().getNodeValue();
					}
				}

				volnode = tempelement.getElementsByTagName("smallLogo");
				associatecontent = (Element) volnode.item(0);
				if (type.equals("1")) {
					if (associatecontent == null) {
					} else if (associatecontent.getFirstChild() == null) {
					} else {
						if (in1 < 3) {
							String url = associatecontent.getFirstChild()
									.getNodeValue();
							Bitmap bit = NetCache.GetNetImage(Config
									.getString("CPC_BASE_URL")
									+ url);
							if(bit == null){
								bit = BitmapFactory.decodeResource(getResources(), R.drawable.bookcover_5472_ui1);
							}
							bookRelationshipBean.book1Map[in1++] = Bitmap.createScaledBitmap(bit,54,72,true);
						}
					}

				} else if (type.equals("3")) {
					if (associatecontent == null) {
					} else if (associatecontent.getFirstChild() == null) {
					} else {
						if (in2 < 6) {
							String url = associatecontent.getFirstChild()
									.getNodeValue();
							Bitmap bit = NetCache.GetNetImage(Config
									.getString("CPC_BASE_URL")
									+ url);
							if(bit == null){
								bit = BitmapFactory.decodeResource(getResources(), R.drawable.bookcover_5472_ui1);
							}
							if(bit.getHeight() > 72){
								bit = Bitmap.createScaledBitmap(bit,54,72,true);
							}
							bookRelationshipBean.book1Map[in2++] = bit ;
						}
					}
				}
			}
		}
	}

	private OnUiItemClickListener menuclick = new OnUiItemClickListener() {


        @Override
        public void onUiItemClick(PviUiItem item) {
            String vTag = "";
            vTag = item.id;
            closePopmenu();
            if (vTag.equals("bookshelf")) {
                // �ҵ����
                InactiveFunction.GotoMyBookshelf(BookSummaryActivity.this, "",
                        contentID);
            } else if (vTag.equals("usercenter")) {
                // ��������
                final Intent tmpintent = new Intent(
                        MainpageActivity.START_ACTIVITY);
                Bundle sndbundle = new Bundle();
                sndbundle.putString("act",
                        "com.pvi.ap.reader.activity.UserCenterActivity");
                tmpintent.putExtras(sndbundle);
                sendBroadcast(tmpintent);
            } else if (vTag.equals("favorite")) {
                // �ղ�
                showMessage("�����ղ�...");
                final HashMap<String, String> map = new HashMap<String, String>();
                map.put("contentID", contentID);
                map.put("BookName", bookSunmaryInfo.title);
                map.put("AuthorName", bookSunmaryInfo.auther);
                map.put(Favorites.FavoriteURL,  "no small image ");

                new Thread() {
                    @Override
                    public void run() {
                        String retstr = InactiveFunction.addFavorite(
                                BookSummaryActivity.this, map);
                        if (retstr.equals("0")) {
                            // ��ʾ��ӳɹ�
                            handler.sendEmptyMessage(8);

                        } else if (retstr.equals("1")) {
                            // ��ʾ�û����ղ�
                            handler.sendEmptyMessage(9);
                        } else if (retstr.equals("2")) {
                            // ��ʾ�û����ղ�
                            handler.sendEmptyMessage(10);
                        } else if (retstr.equals("result-code: 2028\r\n")) {
                            // ��ʾ���ʧ��
                            handler.sendEmptyMessage(11);
                        } else {
                            Bundle tempbundle = new Bundle();
                            tempbundle.putString("RetCode", retstr);
                            Message msg = new Message();
                            msg.what = 12;
                            msg.setData(tempbundle);
                            handler.sendMessage(msg);
                        }
                        super.run();
                    }
                }.start();
            } else if (vTag.equals("comments")) {
                // �鿴����
                InactiveFunction.comment(BookSummaryActivity.this, contentID,
                        true);
            } else if (vTag.equals("flowers")) {
                // ���ʻ�
                HashMap<String, String> map = InactiveFunction
                        .VoteFlower(contentID);
                String retstr = map.get("RetCode");
                Logger.i("Reader", "retstr: " + retstr);
                if (retstr.equals("0")) {
                    // ��ʾ��ӳɹ�
                    InactiveFunction.showResult(BookSummaryActivity.this,
                            "��ܰ��ʾ", "���ʻ��ɹ���");
                } else if (retstr.equals("1")) {
                    // ��ʾ�û������ʻ�
                    InactiveFunction.showResult(BookSummaryActivity.this,
                            "��ܰ��ʾ", "��������ʱ!");
                } else if (retstr.equals("2")) {
                    // ��ʾ���ʧ��
                    InactiveFunction.showResult(BookSummaryActivity.this,
                            "��ܰ��ʾ", "�����ύ����ʧ��!");
                } else if (retstr.equals("result-code: 2027\r\n")) {
                    InactiveFunction.showResult(BookSummaryActivity.this,
                            "��ܰ��ʾ", "���ʻ��Ѵ�ÿ�մ�������!");
                } else {
                    InactiveFunction
                            .showResult(BookSummaryActivity.this, "��ܰ��ʾ", Error
                                    .getErrorDescriptionForContent(retstr));
                }
            } else if (vTag.equals("sendcomments")) {
                // ��������
            } else if (vTag.equals("recommendtofriends")) {
                // �Ƽ�������
                InactiveFunction.RecommendToFriends(BookSummaryActivity.this,
                        contentID, "");
            } else if (vTag.equals("authorinfo")) {
                // �鿴������Ϣ
                InactiveFunction.AuthorInfo(BookSummaryActivity.this,
                        bookSunmaryInfo.authorID);
            } else if (vTag.equals("orderbook")) {
                Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
                Bundle sndBundle1 = new Bundle();
                sndBundle1.putString("act",
                        "com.pvi.ap.reader.activity.SubscribeProcess");
                sndBundle1.putString("subscribeMode", "feedback");
                intent1.putExtras(sndBundle1);
                sendBroadcast(intent1);
            } else if (vTag.equals("reflash")) {
                showMessage("һ��������...");
            } else if (vTag.equals("comments")) {
                InactiveFunction.comment(BookSummaryActivity.this, contentID,
                        true);
            } else if (vTag.equals("flowers")) {
                HashMap<String, String> map = InactiveFunction
                        .VoteFlower(contentID);
                String retstr = map.get("RetCode");
                Logger.i("Reader", "retstr: " + retstr);
                if (retstr.equals("0")) {
                    // ��ʾ��ӳɹ�
                    InactiveFunction.showResult(BookSummaryActivity.this,
                            "��ܰ��ʾ", "���ʻ��ɹ���");
                } else if (retstr.equals("1")) {
                    // ��ʾ�û������ʻ�
                    InactiveFunction.showResult(BookSummaryActivity.this,
                            "��ܰ��ʾ", "��������ʱ!");
                } else if (retstr.equals("2")) {
                    // ��ʾ���ʧ��
                    InactiveFunction.showResult(BookSummaryActivity.this,
                            "��ܰ��ʾ", "�����ύ����ʧ��!");
                } else if (retstr.equals("result-code: 2027\r\n")) {
                    InactiveFunction.showResult(BookSummaryActivity.this,
                            "��ܰ��ʾ", "���ʻ��Ѵ�ÿ�մ�������!");
                } else {
                    InactiveFunction
                            .showResult(BookSummaryActivity.this, "��ܰ��ʾ", Error
                                    .getErrorDescriptionForContent(retstr));
                }
            } else if (vTag.equals("recommendtofriends")) {
                // �Ƽ�������

                InactiveFunction.RecommendToFriends(BookSummaryActivity.this,
                        contentID, "");
            } else if (vTag.equals("presentbook")) {

                if (bookSunmaryInfo.canPresent) {
                    Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
                    Bundle sndBundle1 = new Bundle();
                    sndBundle1.putString("act",
                            "com.pvi.ap.reader.activity.PresentToFriend");
                    sndBundle1.putString("startType", "allwaysCreate");
                    sndBundle1.putString("contentID", contentID);
                    intent1.putExtras(sndBundle1);
                    sendBroadcast(intent1);
                } else {
                    final PviAlertDialog pd = new PviAlertDialog(getParent());
                    pd.setTitle("��ܰ��ʾ");
                    pd.setMessage("�������շ��鼮֧�����ͺ��ѣ�", Gravity.CENTER);
                    pd.setCanClose(true);
                    pd.show();
                }
            }
        
        }
	};

	@Override
	public OnUiItemClickListener getMenuclick() {
		return this.menuclick;
	}
}
