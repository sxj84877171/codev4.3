package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.MonthlyPayment;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * �԰�����Ŀ���в鿴���˶�
 * 
 * @author ������ 2010-11-20
 * @author ���ӿ�
 * 
 */
public class UnsubscribeActivity extends PviActivity implements Pageable{
	private static final String TAG = "UnsubscribeActivity";
	private Handler mHandler = new H();

	// ��ҳ
	// pager
	private TextView mtv_CurPage = null;
	private TextView mtv_Pages = null;
//	private ImageButton mtv_Prev = null;
//	private ImageButton mtv_Next = null;
	private int total = 0;
	private int perpage = 10;
	private int pages = 0;
	private int curpage = 0;
	private int start = 0;
	private TextView mtvTotal; // ��ʾ�ܼ�¼��

	String selectType = null;

	// �б�
//	private ListView mlvCatasubsList;
	private ArrayList<HashMap<String, String>> catasubsList = new ArrayList<HashMap<String, String>>();// ���һҳ������

	// ����û���Ϣ
	private HashMap<String, Object> userinfo = new HashMap<String, Object>();

	private PviAlertDialog pd;
	private static final int GET_DATA = 101;
	private static final int CLOSE_PD = 102;
	public static final int SHOW_PD_LOADING = 103;
	public static final int SET_UI_DATA = 104;
	public static final int UPDATA_PAGER = 105;

	public static final int ERR_CONNECT_EXP = 201;// ���������쳣
	public static final int ERR_CONNECT_TIMEOUT = 202;// ���ӳ�ʱ
	public static final int ERR_RETCODE_NOT0 = 203; // �ӿڷ������0
	public static final int ERR_CONNECT_FAILED = 204;// ����ʧ��
	public static final int ERR_CHECK_PHONENUM = 205;// �ֻ����������
	public static final int ERR_XML_PARSER = 206; // xml��������
	
	ArrayList<PviUiItem[]> list = new ArrayList<PviUiItem[]>();
	
	
	PviDataList listView = null;

	public void showMe() {

		if ("onResume".equals(selectType)) {
			Intent tmpIntent = new Intent(MainpageActivity.SHOW_ME);
			Bundle bundleToSend = new Bundle();
			bundleToSend.putString("act",
					"com.pvi.ap.reader.activity.UserCenterActivity");
			bundleToSend.putString("actTabName", "�����˶�");
			bundleToSend.putString("sender", UnsubscribeActivity.this
					.getClass().getName());
			tmpIntent.putExtras(bundleToSend);
			sendBroadcast(tmpIntent);
			tmpIntent = null;
			bundleToSend = null;
			selectType = null;
		}
		hideTip();
	}

	private class H extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_DATA:// ִ�� ��ȡ�û�����
				new Thread() {
					public void run() {
						getCataSubsList.run();
						getUserInfo.run();
						showMe();

					}
				}.start();
				break;
			case CLOSE_PD:// �ر���ʾ��
				if (pd != null) {
					pd.dismiss();
				}
				hideTip();
				break;
			case SHOW_PD_LOADING:// ��ʾ��������Ϣ��
				/*
				 * pd = new PviAlertDialog(getParent());
				 * pd.setTitle(getResources().getString(R.string.kyleHint04));
				 * pd.setMessage(getResources().getString(R.string.kyleHint05));
				 * //pd.setHaveProgressBar(true); pd.show();
				 */
				showMessage("���ڼ�������...");
				break;
			case SET_UI_DATA:// �ѻ�ȡ�����������UI
				updateUiCatasubList.run();
				break;
			case UPDATA_PAGER:// ���·�ҳ��
				mHandler.post(updataPager);
				break;
			case ERR_CONNECT_EXP:// �����쳣
				// showAlert(getResources().getString(R.string.my_friend_connecterror));
				showError();
				break;
			case ERR_CONNECT_TIMEOUT:// ���ӳ�ʱ
				// showAlert("�������ӳ�ʱ");
				showError();
				break;
			case ERR_RETCODE_NOT0:// �ӿڷ��ش���
				showAlert("����ӿڷ���״̬�벻��ȷ");
				break;
			case ERR_CONNECT_FAILED:// IOʧ��
				// showAlert(getResources().getString(R.string.my_friend_connectfailed));
				showError();
				break;
			case ERR_CHECK_PHONENUM:// �������
				showAlert(getResources().getString(
						R.string.my_friend_numchecking));
				break;
			case ERR_XML_PARSER:// XML��������
				showAlert("XML��������");
				break;
			case 1001:
				final String catalogID = msg.getData().getString("catalogID");
				final String result = SubscribeProcess.network(
						"unsubscribeCatalog", catalogID, null, null, null);
				if (result != null && result.contains("result-code: 0000")) {
					pd.dismiss();
					PviAlertDialog pd = new PviAlertDialog(getParent());
					pd.setTitle("��ܰ��ʾ");
					pd.setMessage("�˶��ɹ� !", Gravity.CENTER);

					pd.setButton(
							getResources().getString(R.string.bookConfirm),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Logger.i(TAG, "pppp" + result);
									// TODO Auto-generated method stub
									Logger.i(TAG,
											"SubscribeProcess.network(unsubscribeCatalog,v.getTag():"
													+ catalogID);
									final String where = MonthlyPayment.CatalogID
											+ "='" + catalogID + "'";
									getContentResolver().delete(
											MonthlyPayment.CONTENT_URI, where,
											null);
									mHandler.sendEmptyMessage(SHOW_PD_LOADING);
									mHandler.sendEmptyMessage(GET_DATA);
								}

							});
					pd.show();

				} else if (result != null && result.contains("Exception")) {

					// showAlert("�˶����������쳣�����Ժ����� ��");
					showError();

				} else {

					showAlert(Error.getErrorDescriptionForContent(result));
				}
				hideTip();
				return;
			default:
				;
			}
		}
	}

	public void showError() {
		PviAlertDialog pd2 = new PviAlertDialog(getParent());
		pd2.setTitle("��ܰ��ʾ");
		pd2.setMessage("���������������", Gravity.CENTER);
		pd2.setCanClose(false);
		pd2.setButton(DialogInterface.BUTTON_POSITIVE, "ȷ��",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method
						// stub
						sendBroadcast(new Intent(MainpageActivity.BACK));
						return;
					}
				});
		pd2.show();
	}

	public void showAlert(String message) {
		if (pd != null && pd.isShowing()) {
			pd.dismiss();
		}
		pd = new PviAlertDialog(getParent());
		pd.setTitle(getResources().getString(R.string.my_friend_hint));
		// pd.setMessage(message);
		TextView tv = new TextView(UnsubscribeActivity.this);
		tv.setText(message);
		tv.setTextSize(21);
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(Color.BLACK);

		pd.setView(tv);
		pd.setCanClose(true);
		pd.show();
	}

	@Override
	public void initControls() {
//		mlvCatasubsList = (ListView) findViewById(R.id.catasubsList);

		// pager
//		mtv_CurPage = (TextView) findViewById(R.id.curpage);
//		mtv_Pages = (TextView) findViewById(R.id.pages);
//		mtv_Prev = (ImageButton) findViewById(R.id.prev);
//		mtv_Next = (ImageButton) findViewById(R.id.next);
		mtvTotal = (TextView) findViewById(R.id.total);

//		if (deviceType == 1) {
//			getWindow().getDecorView().getRootView().invalidate(UPDATEMODE_4);
//			mtv_CurPage.setUpdateMode(UPDATEMODE_5);
//			mtv_Pages.setUpdateMode(UPDATEMODE_5);
//			mtv_Prev.setUpdateMode(UPDATEMODE_5);
//			mtv_Next.setUpdateMode(UPDATEMODE_5);
//			mtvTotal.setUpdateMode(UPDATEMODE_5);
//		}

	}

	@Override
	public void onCreate(Bundle icicle) {

		setContentView(R.layout.unsubscribe_ui1);
		
		final GlobalVar app = ((GlobalVar) getApplicationContext());
		this.showPager = true;

		super.onCreate(icicle);
	}

	@Override
	protected void onResume() {
		super.onResume();
		selectType = "onResume";
		mHandler.sendEmptyMessage(SHOW_PD_LOADING);
		mHandler.sendEmptyMessage(GET_DATA);
		// mlvCatasubsList.requestFocus();
	}

	/**
	 * ȡ������
	 */
	public boolean cancelSub() {
		boolean flag = false;
		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();

		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

		ahmNamePair.put("catalogId", "32");

		HashMap responseMap = null;
		try {
			// ��POST����ʽ��������
			responseMap = CPManager.unsubscribeCatalog(ahmHeaderMap,
					ahmNamePair);
			mHandler.sendEmptyMessage(CLOSE_PD);
			flag = true;
		} catch (HttpException e) {
			// �����쳣 ,һ��ԭ��Ϊ URL����
			mHandler.sendEmptyMessage(ERR_CONNECT_EXP);
			flag = false;
		} catch (SocketTimeoutException e) {

			mHandler.sendEmptyMessage(ERR_CONNECT_TIMEOUT);
			flag = false;
		} catch (IOException e) {
			// IO�쳣 ,һ��ԭ��Ϊ��������
			mHandler.sendEmptyMessage(ERR_CONNECT_FAILED);
			flag = false;
		}

		return flag;
	}

	private Runnable getCataSubsList = new Runnable() {
		@Override
		public void run() {

			String xml = SubscribeProcess.network("getCatalogSubscriptionList",
					null, "" + start, "" + perpage, null);
			mHandler.sendEmptyMessage(CLOSE_PD);
			if (xml.contains("Exception")) {
				mHandler.sendEmptyMessage(ERR_CONNECT_FAILED);
				return;
			}
			Element rootele = null;
			try {
				xml = xml.substring(20);
				xml = xml.replaceAll(">\\s+?<", "><");
				rootele = CPManagerUtil
						.getDocumentFrombyteArray(xml.getBytes())
						.getDocumentElement();
			} catch (Exception e) {
				mHandler.sendEmptyMessage(CLOSE_PD);
				mHandler.sendEmptyMessage(ERR_XML_PARSER);
				return;
			}

			try {
				catasubsList.clear();

				

			  
				NodeList totalNl = rootele
						.getElementsByTagName("totalRecordCount");
				total = Integer.parseInt(totalNl.item(0).getFirstChild()
						.getNodeValue());
				pages = total / perpage;
				if (total % perpage > 0) {
					pages = pages + 1;
				}
				if (curpage == 0 && total > 0) {
					curpage = 1;
				}

				mHandler.sendEmptyMessage(UPDATA_PAGER);

				NodeList nl = rootele.getElementsByTagName("CatalogInfo");
				int nlCount = nl.getLength();
				// for(int i =
				// (currentPage-1)*itemPerPage;i<nl.getLength()&&i<itemPerPage+(currentPage-1)*itemPerPage;i++){
				list.clear();
				for (int i = 0; i < nlCount; i++) {
					
					  PviUiItem[] items = new PviUiItem[]{
				                new PviUiItem("cataname"+i, 0, 21, 10, 160,42, "5Ԫ������Ŀ", false, true, null),
				                new PviUiItem("substime"+i, 0, 175, 10, 150,42, "2010/12/22 14:00", false, true, null),
				                new PviUiItem("subsfee"+i, 0, 345, 10, 90, 42, "5Ԫ", false, true, null),
				                new PviUiItem("unsubsbtn"+i, R.drawable.booksummarybtn, 456, 10, 104, 32, "�� ��", false, true, null),
				        };
					
					
					HashMap<String, String> tempHM = new HashMap<String, String>();
					Element e2 = (Element) nl.item(i);
					tempHM.put("catalogID", e2
							.getElementsByTagName("catalogID").item(0)
							.getFirstChild().getNodeValue());
					tempHM.put("catalogName", e2.getElementsByTagName(
							"catalogName").item(0).getFirstChild()
							.getNodeValue());
					items[0].text = e2.getElementsByTagName("catalogName").item(0).getFirstChild().getNodeValue();
					tempHM.put("startTime", e2
							.getElementsByTagName("startTime").item(0)
							.getFirstChild().getNodeValue());
					items[1].text = GlobalVar.TimeFormat("yyyy-MM-dd",e2.getElementsByTagName("startTime").item(0).getFirstChild().getNodeValue()).substring(0, 10);
					tempHM.put("fee", e2.getElementsByTagName("fee").item(0)
							.getFirstChild().getNodeValue());
					items[2].text = e2.getElementsByTagName("fee").item(0).getFirstChild().getNodeValue() + "Ԫ";
					catasubsList.add(tempHM);
					list.add(items);
				}
			} catch (Exception e) {
				mHandler.sendEmptyMessage(CLOSE_PD);
				mHandler.sendEmptyMessage(ERR_XML_PARSER);
				Logger.e(TAG, "get data from xml failed!" + e.getMessage());
				return;
			}
			mHandler.sendEmptyMessage(SET_UI_DATA);
		}
	};

	private Runnable updateUiCatasubList = new Runnable() {
		@Override
		public void run() {
			listView.setData(list);
			final GlobalVar app = ((GlobalVar) getApplicationContext());        
			showPager();
	        updatePagerinfo((curpage == 0 ? "1" : curpage) +" / "+ ((pages == 0 ? "1":pages)));
	        mtvTotal.setText("��" + total + "��");

		}
	};

	private Runnable getUserInfo = new Runnable() {
		@Override
		public void run() {
			Logger.i(TAG, "getUserInfo");
			// Call ContentManager to getUserInfo then set to display
			HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
			HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

			HashMap responseMap = null;
			try {

				responseMap = CPManager.getUserInfo(ahmHeaderMap, ahmNamePair);
				mHandler.sendEmptyMessage(CLOSE_PD);
				if (!responseMap.get("result-code").toString().contains(
						"result-code: 0")) {
					mHandler.sendEmptyMessage(ERR_RETCODE_NOT0);
					Logger.i(TAG, responseMap.get("result-code").toString());
					return;
				}
			} catch (HttpException e) {
				mHandler.sendEmptyMessage(CLOSE_PD);
				mHandler.sendEmptyMessage(ERR_CONNECT_EXP);
				return;
			} catch (SocketTimeoutException e) {
				mHandler.sendEmptyMessage(CLOSE_PD);
				mHandler.sendEmptyMessage(ERR_CONNECT_TIMEOUT);
				return;
			} catch (IOException e) {
				mHandler.sendEmptyMessage(CLOSE_PD);
				mHandler.sendEmptyMessage(ERR_CONNECT_FAILED);
				return;
			}

			byte[] responseBody = (byte[]) responseMap.get("ResponseBody");

			Document dom = null;
			try {
				dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);

			} catch (ParserConfigurationException e) {
				mHandler.sendEmptyMessage(CLOSE_PD);
				mHandler.sendEmptyMessage(ERR_XML_PARSER);
				Logger.e(TAG, e.getMessage());
				return;
			} catch (SAXException e) {
				mHandler.sendEmptyMessage(CLOSE_PD);
				mHandler.sendEmptyMessage(ERR_XML_PARSER);
				Logger.e(TAG, e.getMessage());
				return;
			} catch (IOException e) {
				mHandler.sendEmptyMessage(CLOSE_PD);
				mHandler.sendEmptyMessage(ERR_XML_PARSER);
				Logger.e(TAG, e.getMessage());
				return;
			}
			Element root = dom.getDocumentElement();

			NodeList nl = root.getElementsByTagName("Parameter");
			String name = "";
			String value = "";
			Element temp = null;
			for (int i = 0; i < nl.getLength(); i++) {
				temp = (Element) nl.item(i);
				name = temp.getElementsByTagName("name").item(0)
						.getFirstChild().getNodeValue();
				try {
					value = temp.getElementsByTagName("value").item(0)
							.getFirstChild().getNodeValue();
				} catch (Exception e) {
					value = "";
				}
				userinfo.put(name, value);
			}

			if (userinfo.containsKey("Mobile")) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
					}
				});
			}

		}
	};

	@Override
	public void bindEvent() {

		super.bindEvent();
		
		
		listView = (PviDataList)findViewById(R.id.catasubsList);
		listView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listView.mCurFoucsRow <0 || listView.mCurFoucsRow >= catasubsList.size())
					return ;
				pd = new PviAlertDialog(getParent());
				pd.setTitle("��ܰ��ʾ");
				pd.setMessage("ȷ��ȡ�����¶�����");

				final String catalogID = catasubsList.get(listView.mCurFoucsRow)
						.get("catalogID");
				pd.setButton(getResources().getString(R.string.bookConfirm),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								showMessage("�����˶�����...");
								new Thread() {
									public void run() {
										Message msg = new Message();
										msg.what = 1001;
										Bundle b = new Bundle();
										b.putString("catalogID", catalogID);
										msg.setData(b);
										mHandler.sendMessage(msg);
									};
								}.start();

								pd.dismiss();
							}
						});
				pd.setButton2(getResources().getString(R.string.bookCancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
				pd.show();
			}
		});


	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	private void prevPage() {
		if (total > 0 && curpage > 1) {
			curpage = curpage - 1;
			start = (curpage - 1) * perpage + 1;
			mHandler.post(getCataSubsList);
		}
	}

	private void nextPage() {
		if (total > 0 && curpage < pages) {
			curpage = curpage + 1;
			start = (curpage - 1) * perpage + 1;
			mHandler.post(getCataSubsList);
		}
	}

	private Runnable updataPager = new Runnable() {
		@Override
		public void run() {
		}
	};

	@Override
	public void OnNextpage() {
		nextPage();
	}

	@Override
	public void OnPrevpage() {
		prevPage();
	}

}