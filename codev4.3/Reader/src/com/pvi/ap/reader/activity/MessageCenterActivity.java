package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviDataList.OnRowClickListener;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;
import com.pvi.ap.reader.data.external.manager.LeafNode;
import com.pvi.ap.reader.data.external.manager.XMLUtil;
import com.pvi.ap.reader.data.external.manager.XmlElement;

/**
 * 
 * 消息中心
 * 
 * @author 马中庆 RD040
 * 
 */
public class MessageCenterActivity extends PviActivity {
	protected static final String LOG_TAG = "MessageCenterActivity";
	private Handler mHandler = new H();
	private Context mContext;
	private Intent mIntent;
	private static int skinID = 1;// 皮肤ID

	// 分页
	// pager
	// private TextView mtv_CurPage = null;
	// private TextView mtv_Pages = null;
	// private View mtv_Prev = null;
	// private View mtv_Next = null;
	private int total = 0;
	private int perpage = 10;
	private int pages = 1;
	private int curpage = 1;
	private int start = 0;

	private String selectType;

	Button selallBtn, delBtn, delallBtn;
	private boolean loading = false;// 标志量，当当前界面正在载入数据时，设置它为true

	// private ListView lv_messageList = null;
	private PviDataList lv_messageList = null;
	ArrayList<PviUiItem[]> list = new ArrayList<PviUiItem[]>();

	private MessageListAdapter messageListAdapter = null;
	private static TextView tv_totalRecordCount = null;

	// add by kizan for bug 80
	private boolean allCheckedSelect = false;

	// add by kizan for bug 511
	private Button showReadedBtn;
	private Button showUnreadBtn;
	private Button showallBtn;

	// data
	private ArrayList<String[]> messageList = new ArrayList<String[]>();
	private ArrayList<String[]> dataList = null;
	private String totalRecordCount = "";
	private static String unreadRecordCount = "";
	private ArrayList<String> deleteMessageIDs = new ArrayList<String>();

	// modified to static for bug 28
	private static int lastSelect = -1;// save ListView last select

	private static final int GET_DATA = 101;
	private static final int CLOSE_PD = 102;
	public static final int SHOW_PD_LOADING = 103;
	public static final int SET_UI_DATA = 104;
	public static final int DO_DELETE_MESSAGE = 105;
	public static final int UPDATA_MESSAGESUMMARY = 106;
	public static final int UPDATA_PAGER = 109;// 更新分页条

	public static final int ERR_CONNECT_EXP = 201;// 网络连接异常
	public static final int ERR_CONNECT_TIMEOUT = 202;// 连接超时
	public static final int ERR_RETCODE_NOT0 = 203; // 接口返回码非0
	public static final int ERR_CONNECT_FAILED = 204;// 连接失败
	public static final int ERR_CHECK_PHONENUM = 205;// 手机号码检测出错
	public static final int ERR_XML_PARSER = 206; // xml解析错误

	public static final int ALERT_DEL_SUCESS = 301; // 删除已成功
	public static final int ALERT_NOSEL = 302; // 未选取

	// add by fly
	// for 赠送记录
	// 2011-3-9
	private Button getButton;
	private Button sendButton;

	private PviAlertDialog pd;

	private class H extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_DATA:// 执行 获取用户数据
				mHandler.post(disableButtons);
				new Thread() {
					public void run() {
						getData.run();
						mHandler.post(enableButtons);
					}
				}.start();
				break;
			case DO_DELETE_MESSAGE:// 执行 删除消息
				mHandler.post(disableButtons);
				new Thread() {
					public void run() {
						// mHandler.post(doDeleteMessage);
						doDeleteMessage.run();
						mHandler.post(enableButtons);
					}
				}.start();
				break;
			case UPDATA_MESSAGESUMMARY:// 更新 消息统计条
				mHandler.post(updataMessageSummary);
				break;
			case CLOSE_PD:// 关闭提示框
				if (pd != null) {
					pd.dismiss();
				}
				hideTip();
				break;
			case SHOW_PD_LOADING:// 显示加载中信息框
				showMessage("正在加载数据...");
				/*
				 * pd = new PviAlertDialog(getParent());
				 * pd.setTitle(getResources().getString(R.string.kyleHint04));
				 * pd.setMessage(getResources().getString(R.string.kyleHint05));
				 * //pd.setHaveProgressBar(true); pd.show();
				 */
				break;
			case SET_UI_DATA:// 把获取的数据填充入UI
				tv_totalRecordCount.setText("   您共有" + totalRecordCount
						+ "条消息(" + unreadRecordCount + "条未读)");

				try {
					// messageListAdapter = new MessageListAdapter(
					// getBaseContext(), messageList);
					lv_messageList.setData(list);
					updatePagerinfo(curpage + "/" + pages);
					// lv_messageList.setAdapter(messageListAdapter);

					// lv_messageList
					// .setOnItemClickListener(new OnItemClickListener() {
					//
					// public void onItemClick(AdapterView parent,
					// View v, int position, long id) {
					// // show
					// Bundle bundleToSend = new Bundle();
					// bundleToSend.putString("messageID",
					// messageList.get(position)[0]);
					// bundleToSend.putString("fromuserName",
					// messageList.get(position)[4]);
					// bundleToSend.putString("time", messageList
					// .get(position)[5]);
					// bundleToSend.putString("title", messageList
					// .get(position)[6]);
					// bundleToSend.putString("content",
					// messageList.get(position)[7]);
					// final Intent tmpintent = new Intent(
					// getBaseContext(),
					// ShowMessageActivity.class);
					// tmpintent.putExtras(bundleToSend);
					// startActivity(tmpintent);
					// }
					// });

					// commented those below lines 298~311 by kizan for bug 28
					// on 2011.12.17 13:30
					// the click event is caught by ListAdapter the event will
					// never reach here
					// lv_messageList.setOnItemSelectedListener(new
					// AdapterView.OnItemSelectedListener(){
					//
					// @Override
					// public void onItemSelected(AdapterView<?> arg0, View
					// arg1,
					// int position, long arg3) {
					// lastSelect = position;
					// arg1.setPressed(true);
					// }
					//
					// @Override
					// public void onNothingSelected(AdapterView<?> arg0) {
					//                          
					// }});

				} catch (Exception e) {
					Logger.e(LOG_TAG, e.getMessage());
				}
				break;
			case UPDATA_PAGER:// 更新分页条
				mHandler.post(updataPager);
				break;
			case ERR_CONNECT_EXP:// 网络异常
				// PviUiUtil.Redo(pd, mContext,mIntent);
				// showAlert(getResources().getString(R.string.my_friend_connecterror));
				showError();
				break;
			case ERR_CONNECT_TIMEOUT:// 连接超时
				// PviUiUtil.Redo(pd, mContext,mIntent);
				// showAlert("网络连接超时");
				showError();
				break;
			case ERR_RETCODE_NOT0:// 接口返回错误
				showAlert("网络接口返回状态码不正确");
				break;
			case ERR_CONNECT_FAILED:// IO失败
				// PviUiUtil.Redo(pd, mContext, mIntent);
				// showAlert(getResources().getString(R.string.my_friend_connectfailed));
				showError();
				break;
			case ERR_CHECK_PHONENUM:// 号码错误
				showAlert(getResources().getString(
						R.string.my_friend_numchecking));
				break;
			case ERR_XML_PARSER:// XML解析错误
				showAlert("XML解析错误。");
				break;
			case ALERT_DEL_SUCESS:
				showAlert("删除已成功。");
				break;
			case ALERT_NOSEL:
				showAlert("您未选取。");
				break;

			default:
				;
			}

			super.handleMessage(msg);
		}

	}

	public void showError() {
		PviAlertDialog pd2 = new PviAlertDialog(getParent());
		pd2.setTitle("温馨提示");
		pd2.setMessage("操作出现网络错误！", Gravity.CENTER);
		pd2.setCanClose(false);
		pd2.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sendBroadcast(new Intent(MainpageActivity.BACK));
						return;
					}
				});
		pd2.show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pvi.ap.reader.activity.PviActivity#initControls()
	 */
	@Override
	public void initControls() {
		super.initControls();
		lv_messageList = (PviDataList) findViewById(R.id.messageList);
		tv_totalRecordCount = (TextView) findViewById(R.id.totalRecordCount);
		selallBtn = (Button) findViewById(R.id.selallBtn);
		delBtn = (Button) findViewById(R.id.delBtn);
		delallBtn = (Button) findViewById(R.id.delallBtn);
		showReadedBtn = (Button) findViewById(R.id.showReadedBtn);
		showUnreadBtn = (Button) findViewById(R.id.showUnreadBtn);
		showallBtn = (Button) findViewById(R.id.showallBtn);

		// pager
		// mtv_CurPage = (TextView) findViewById(R.id.curpage);
		// mtv_Pages = (TextView) findViewById(R.id.pages);
		// mtv_Prev = findViewById(R.id.prev);
		// mtv_Next = findViewById(R.id.next);

		sendButton = (Button) findViewById(R.id.showSendBtn);
		getButton = (Button) findViewById(R.id.showGetBtn);

		if (deviceType == 1) {
//			getWindow().getDecorView().getRootView().invalidate(UPDATEMODE_4);

			// findViewById(R.id.mainblock).invalidate(UPDATEMODE_5);
			// findViewById(R.id.selallBtn).invalidate(0, 0,
			// 600,800,UPDATEMODE_4);
			// int UPDATEMODE =
//			tv_totalRecordCount.setUpdateMode(UPDATEMODE_9);
//
//			selallBtn.setUpdateMode(UPDATEMODE_9);
//			delBtn.setUpdateMode(UPDATEMODE_9);
//			delallBtn.setUpdateMode(UPDATEMODE_9);
//			showReadedBtn.setUpdateMode(UPDATEMODE_9);
//			showUnreadBtn.setUpdateMode(UPDATEMODE_9);
//			showallBtn.setUpdateMode(UPDATEMODE_9);

			// pager
			// mtv_CurPage.setUpdateMode(UPDATEMODE_9);
			// mtv_Pages.setUpdateMode(UPDATEMODE_9);
			// mtv_Prev.setUpdateMode(UPDATEMODE_9);
			// mtv_Next.setUpdateMode(UPDATEMODE_9);

//			sendButton.setUpdateMode(UPDATEMODE_9);
//			getButton.setUpdateMode(UPDATEMODE_9);

		}

	}

	private GlobalVar appState = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		appState = ((GlobalVar) getApplicationContext());

		perpage = 10;
		setContentView(R.layout.messagecenter_ui1);

		mContext = MessageCenterActivity.this;
		mIntent = getIntent();

		this.showPager = true;

		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onResume() {
		super.onResume();
		selectType = "onResume";
		setUIData();
		// showallBtn.setBackgroundResource(R.drawable.btn_bg_3_focus);
	}

	private class MessageListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private ArrayList<String[]> messageList;

		public MessageListAdapter(Context context, ArrayList<String[]> list) {
			mInflater = LayoutInflater.from(context);
			messageList = list;
		}

		public int getCount() {
			return messageList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.messagelistitem_ui1,
						null);

				holder = new ViewHolder();
				holder.cb_messageID = (CheckBox) convertView
						.findViewById(R.id.messageID);

				holder.cb_messageID
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								lastSelect = position;
							}

						});

				holder.tv_title = (TextView) convertView
						.findViewById(R.id.tv_title);
				holder.tv_time = (TextView) convertView
						.findViewById(R.id.tv_time);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.cb_messageID.setTag(messageList.get(position)[0]);
			String title = messageList.get(position)[6];
			if (title != null && !title.equals("")) {
				;
			} else {
				title = "无标题消息";
			}
			holder.tv_title.setText(title);
			// add click event:
			final String f_title = title;
			final String f_content = messageList.get(position)[7];
			final String f_fromusername = messageList.get(position)[4];
			final String f_time = messageList.get(position)[5];
			final String f_type = messageList.get(position)[1];
			String f_contentId = null;
			if (messageList.get(position).length > 8) {
				f_contentId = messageList.get(position)[8];
			}
			final String temp = f_contentId;
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (loading == false) {
						Logger.i(LOG_TAG, "start to read message,messageID:"
								+ messageList.get(position)[0]);
						new Thread() {
							public void run() {

								HashMap ahmHeaderMap = CPManagerUtil
										.getHeaderMap();
								HashMap ahmNamePair = CPManagerUtil
										.getAhmNamePairMap();
								String xMLBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Request><SyncMessageReq><MessageIDList></messageID>"
										+ messageList.get(position)[0]
										+ "</messageID></MessageIDList></SyncMessageReq></Request>";
								ahmNamePair.put("XMLBody", xMLBody);
								HashMap responseMap = null;

								try {
									responseMap = CPManager.syncMessageState(
											ahmHeaderMap, ahmNamePair);
								} catch (HttpException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}

								Logger
										.i(LOG_TAG, "read message:"
												+ responseMap);

							};
						}.start();
						Logger.i(LOG_TAG, "the end of read message");

						lastSelect = position;
						// show messge
						Bundle bundleToSend = new Bundle();
						bundleToSend.putString("messageID", messageList
								.get(position)[0]);
						bundleToSend.putString("fromuserName", f_fromusername);
						bundleToSend.putString("time", f_time);
						bundleToSend.putString("title", f_title);
						bundleToSend.putString("content", f_content);
						bundleToSend.putString("type", f_type);
						bundleToSend.putString("contentId", temp);
						final Intent tmpintent = new Intent(v.getContext(),
								ShowMessageActivity.class);
						tmpintent.putExtras(bundleToSend);
						// add by kizan for bug 511
						// set the message status to read status

						/*
						 * messageList.get(lastSelect)[2] = "1" ; int count = 0
						 * ; for(String s [] : messageList){
						 * if(!"1".equals(s[2])){ count++; } }
						 * tv_unreadRecordCount.setText(""+count);
						 */

						v.getContext().startActivity(tmpintent);
					}
				}
			});

			String dataFormat = null;

			try {
				dataFormat = GlobalVar.TimeFormat("yyyy-MM-dd hh:mm:ss",
						messageList.get(position)[5]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			holder.tv_time.setText(dataFormat);

			if (deviceType == 1) {
//				convertView.setUpdateMode(UPDATEMODE_9);
			}
			return convertView;
		}

		class ViewHolder {
			CheckBox cb_messageID;
			TextView tv_title;
			TextView tv_time;
		}
	}

	private void setUIData() {
		mHandler.sendEmptyMessage(SHOW_PD_LOADING);
		mHandler.sendEmptyMessage(GET_DATA);
	}

	private Runnable getData = new Runnable() {
		@Override
		public void run() {

			loading = true;
			messageList.clear();
			// read data from remote
			HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
			// HashMap ahmHeaderMap = new HashMap();//保存Header信息
			// 添加Header信息
			// ahmHeaderMap.put("Accept","*/*");
			/*
			 * ahmHeaderMap.put("Host"," 211.140.7.144:9096");
			 * ahmHeaderMap.put("User-Agent","Mozilla/4.0");
			 * ahmHeaderMap.put("APIVersion","1.0.0");
			 * ahmHeaderMap.put("x-up-calling-line-id","13466320946");
			 * ahmHeaderMap.put("user-id","be7a85b5c8445b3061e39275bd950297");
			 * ahmHeaderMap.put("Client-Agent","PVI_P801_V1.0/800*600/other");
			 * ahmHeaderMap.put("Content-type", "application/xml");
			 * ahmHeaderMap.put("DeviceId", "018P801_20100920_001");
			 */

			HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

			if (start > 0) {
				ahmNamePair.put("start", "" + start);
			}
			ahmNamePair.put("count", "" + perpage);

			HashMap responseMap = null;
			try {
				responseMap = CPManager.getMessage(ahmHeaderMap, ahmNamePair);
				mHandler.sendEmptyMessage(CLOSE_PD);
				loading = false;
				if (!responseMap.get("result-code").toString().contains(
						"result-code: 0")) {
					mHandler.sendEmptyMessage(ERR_RETCODE_NOT0);
					Logger
							.e(LOG_TAG, responseMap.get("result-code")
									.toString());
					return;
				} else {

				}
			} catch (HttpException e) {
				mHandler.sendEmptyMessage(CLOSE_PD);
				Logger.e(LOG_TAG, e);
				mHandler.sendEmptyMessage(ERR_CONNECT_EXP);
				loading = false;
				return;
			} catch (SocketTimeoutException e) {
				mHandler.sendEmptyMessage(CLOSE_PD);
				Logger.e(LOG_TAG, e);
				mHandler.sendEmptyMessage(ERR_CONNECT_TIMEOUT);
				// 显示获取用户信息失败提示框
				loading = false;
				return;
			} catch (IOException e) {
				mHandler.sendEmptyMessage(CLOSE_PD);
				Logger.e(LOG_TAG, "3-" + e.getMessage());
				mHandler.sendEmptyMessage(ERR_CONNECT_FAILED);
				loading = false;
				return;
			}
			byte[] responseBody = (byte[]) responseMap.get("ResponseBody");

			Document dom = null;
			try {
				String xml = new String(responseBody);
				xml = xml.replaceAll(">\\s+?<", "><");
				dom = CPManagerUtil.getDocumentFrombyteArray(xml.getBytes());
			} catch (Exception e) {
				mHandler.sendEmptyMessage(CLOSE_PD);
				Logger.e(LOG_TAG, e);
				mHandler.sendEmptyMessage(ERR_XML_PARSER);
				return;
			}

			Element root = dom.getDocumentElement();

			try {
				totalRecordCount = root
						.getElementsByTagName("totalRecordCount").item(0)
						.getFirstChild().getNodeValue();

				// pager
				total = Integer.parseInt(totalRecordCount);
				pages = total / perpage;
				if (total % perpage > 0) {
					pages = pages + 1;
				}

				if (curpage == 0 && total > 0) {
					curpage = 1;
				}

				if (pages == 0) {
					pages = 1;
				}

				mHandler.sendEmptyMessage(UPDATA_PAGER);

				unreadRecordCount = root.getElementsByTagName(
						"unreadRecordCount").item(0).getFirstChild()
						.getNodeValue();
				;
			} catch (Exception e) {
				mHandler.sendEmptyMessage(CLOSE_PD);
				Logger.e(LOG_TAG, e);
				mHandler.sendEmptyMessage(ERR_XML_PARSER);
				return;
			}

			NodeList nl1 = root.getElementsByTagName("MessageList");
			int idn = 0;
			if (nl1 != null) {
				Element el1 = (Element) nl1.item(0);
				if (el1 != null) {
					NodeList nl2 = el1.getElementsByTagName("Message");
					if (nl2 != null) {
						int nl2Count = nl2.getLength();
						for (int i = 0; i < nl2Count; i++) {
							Element el2 = (Element) nl2.item(i);
							NodeList nl3 = el2.getChildNodes();
							if (nl3 != null) {
								int nl3Count = nl3.getLength();
								String[] message = new String[nl3Count];
								for (int j = 0; j < nl3Count; j++) {
									Element el3 = (Element) nl3.item(j);
									try {
										message[j] = el3.getFirstChild()
												.getNodeValue();
									} catch (Exception e) {
										;
									}
								}
								messageList.add(message);
								idn++;
							}
						}
					}
				}
			}
			setList(messageList);

			mHandler.sendEmptyMessage(SET_UI_DATA);

			showMe();

		}
	};

	private void setList(ArrayList<String[]> messageList) {
		list = new ArrayList<PviUiItem[]>();
		for (int idn = 0; idn < messageList.size(); idn++) {
			String[] message = new String[6];
			message = messageList.get(idn);
			final int indx = idn;
			PviUiItem[] items = new PviUiItem[] {
					new PviUiItem(message[0], 0, 21, 10, 349, 42, "无标题消息",
							false, true, null),
					new PviUiItem("tv_time" + idn, 0, 380, 10, 150, 42,
							"2010/12/22", false, true, null),
					new PviUiItem(message[2], R.drawable.notcheck, 530, 10, 60,
							42, null, false, true, null), };
			final PviUiItem pui = items[2];
			final String messageId = message[3];
			pui.l = new OnClickListener() {
				public void onClick(View v) {
					lastSelect = indx;
					if (pui.res == R.drawable.notcheck) {
						pui.res = R.drawable.check;
						deleteMessageIDs.add(messageId);
					} else {
						pui.res = R.drawable.notcheck;
						deleteMessageIDs.remove(messageId);
					}
					// v.invalidate();
				}
			};
			items[0].text = message[6];
			items[1].text = message[5].substring(0, 10);
			list.add(items);
		}
	}

	public void showMe() {
		if ("onResume".equals(selectType)) {
			Intent tmpIntent = new Intent(MainpageActivity.SHOW_ME);
			Bundle bundleToSend = new Bundle();
			bundleToSend.putString("act",
					"com.pvi.ap.reader.activity.UserCenterActivity");
			bundleToSend.putString("actTabName", "消息中心");
			bundleToSend.putString("sender", MessageCenterActivity.this
					.getClass().getName());
			tmpIntent.putExtras(bundleToSend);
			sendBroadcast(tmpIntent);
			tmpIntent = null;
			bundleToSend = null;
			selectType = null;
		}

		hideTip();
	}

	private void deleteMessage() {
		mHandler.sendEmptyMessage(DO_DELETE_MESSAGE);
	}

	private Runnable doDeleteMessage = new Runnable() {
		@Override
		public void run() {
			// 从UI中得到需要删除的messageID
			// final int childCount = lv_messageList.getChildCount();

			for (int i = 0; i < list.size(); i++) {
				PviUiItem[] pui = list.get(i);
				if (pui[2].res == R.drawable.check) {
					deleteMessageIDs.add(pui[0].id);
				}
			}

			if (deleteMessageIDs != null && deleteMessageIDs.size() > 0) {

				// build post xml
				HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
				HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

				ArrayList<LeafNode> MessageIDList = new ArrayList<LeafNode>();
				int delCount = deleteMessageIDs.size();
				for (int i = 0; i < delCount; i++) {
					MessageIDList.add(new LeafNode("messageID",
							deleteMessageIDs.get(i)));
				}

				XmlElement xmlMessageIDList = new XmlElement("MessageIDList",
						MessageIDList);
				XmlElement xmlDeleteMessageReq = XMLUtil.getParentXmlElement(
						"DeleteMessageReq", xmlMessageIDList);
				XmlElement xmlRequest = XMLUtil.getParentXmlElement("Request",
						xmlDeleteMessageReq);
				String requestXMLBody = "";
				try {
					requestXMLBody = XMLUtil
							.getXmlStringFromXmlElement(xmlRequest);
				} catch (ParserConfigurationException e1) {
					e1.printStackTrace();
					mHandler.sendEmptyMessage(ERR_XML_PARSER);
					return;
				}
				System.out.println(requestXMLBody);
				ahmNamePair.put("XMLBody", requestXMLBody);

				// post data
				HashMap responseMap = null;
				try {
					mHandler.sendEmptyMessage(SHOW_PD_LOADING);
					responseMap = CPManager.deleteMessage(ahmHeaderMap,
							ahmNamePair);
					System.out.println(responseMap);
					mHandler.sendEmptyMessage(CLOSE_PD);
					if (!responseMap.get("result-code").toString().contains(
							"result-code: 0")) {
						Logger.d(LOG_TAG, responseMap.get("result-code")
								.toString());
						mHandler.sendEmptyMessage(ERR_RETCODE_NOT0);
						return;
					} else {
						ArrayList<String[]> tempList = new ArrayList<String[]>();
						for (String[] message : messageList) {
							if (!deleteMessageIDs.contains(message[0])) {
								tempList.add(message);
							}
						}

						messageList.clear();
						for (String[] message : tempList) {
							messageList.add(message);
						}

						mHandler.post(new Runnable() {
							@Override
							public void run() {
								// messageListAdapter.notifyDataSetChanged();
								lv_messageList.setData(list);
							}
						});

						deleteMessageIDs.clear();
						// add by kizan for bug 72

						mHandler.post(new Runnable() {
							@Override
							public void run() {
							}
						});

						// 更新总条数
						totalRecordCount = "" + tempList.size();
						mHandler.sendEmptyMessage(UPDATA_MESSAGESUMMARY);
						mHandler.sendEmptyMessage(ALERT_DEL_SUCESS);
//						mHandler.sendEmptyMessage(GET_DATA);

						lastSelect = -1;
					}
				} catch (HttpException e) {
					mHandler.sendEmptyMessage(CLOSE_PD);
					Logger.e(LOG_TAG, e.getMessage());
					mHandler.sendEmptyMessage(ERR_CONNECT_EXP);
					return;
				} catch (SocketTimeoutException e) {
					mHandler.sendEmptyMessage(CLOSE_PD);
					mHandler.sendEmptyMessage(ERR_CONNECT_TIMEOUT);
					return;
				} catch (IOException e) {
					mHandler.sendEmptyMessage(CLOSE_PD);
					Logger.e(LOG_TAG, e.getMessage());
					mHandler.sendEmptyMessage(ERR_CONNECT_EXP);
					return;
				}

			} else {
				mHandler.sendEmptyMessage(ALERT_NOSEL);
			}
		}
	};

	@Override
	public void bindEvent() {
		super.bindEvent();

		lv_messageList.setOnRowClick(new OnRowClickListener() {

			@Override
			public void OnRowClick(View v, int rowIndex) {
				if (rowIndex >= messageList.size()) {
					return;
				}
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("messageID",
						messageList.get(rowIndex)[0]);
				bundleToSend.putString("fromuserName", messageList
						.get(rowIndex)[4]);
				bundleToSend.putString("time", messageList.get(rowIndex)[5]);
				bundleToSend.putString("title", messageList.get(rowIndex)[6]);
				bundleToSend.putString("content", messageList.get(rowIndex)[7]);
				final Intent tmpintent = new Intent(getBaseContext(),
						ShowMessageActivity.class);
				tmpintent.putExtras(bundleToSend);
				startActivity(tmpintent);
				lastSelect = rowIndex ;
				readMessage();

			}
		});

		sendButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("startType", "allwaysCreate");
				bundleToSend.putString("actID", "ACT14610");

				bundleToSend.putInt("type", 1);
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
				tmpIntent = null;
				bundleToSend = null;
			}
		});

		getButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("startType", "allwaysCreate");
				bundleToSend.putString("actID", "ACT14610");

				bundleToSend.putInt("type", 2);
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
				tmpIntent = null;
				bundleToSend = null;
			}
		});

		// 全选

		selallBtn.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectOrDeselectAllMessage();
			}
		});

		// 删除

		delBtn.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteMessage();
			}
		});

		// 删除所有

		delallBtn.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				// int childCount = lv_messageList.getChildCount();
				// for (int i = 0; i < list.size(); i++) {
				// PviUiItem[] pui = list.get(i);
				// pui[2].res = R.drawable.check ;
				// }
				for (int i = 0; i < messageList.size(); i++) {
					deleteMessageIDs.add(messageList.get(i)[0]);
				}
				deleteMessage();
				

			}
		});

		// 显示已读

		// showReadedBtn.setPressed(true);
		showReadedBtn.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {

				// setBtnPressedBg(v);
				ArrayList<String[]> tempList = new ArrayList<String[]>();
				int c = messageList.size();
				for (int i = 0; i < c; i++) {
					if (messageList.get(i)[2] != null
							&& messageList.get(i)[2].equals("1")) {
						// list.remove(i);
						tempList.add(messageList.get(i));
					}
				}
				setList(tempList);
				// messageListAdapter = new MessageListAdapter(getBaseContext(),
				// tempList);
				// lv_messageList.setAdapter(messageListAdapter);
				lv_messageList.setData(list);
			}
		});

		// 显示未读

		// showUnreadBtn.setPressed(true);
		showUnreadBtn.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				// setBtnPressedBg(v);
				ArrayList<String[]> tempList = new ArrayList<String[]>();
				int c = messageList.size();
				for (int i = 0; i < c; i++) {
					if (messageList.get(i)[2] != null
							&& messageList.get(i)[2].equals("0")) {
						tempList.add(messageList.get(i));
						// list.remove(i);
					}
				}
				// messageListAdapter = new MessageListAdapter(getBaseContext(),
				// tempList);
				// lv_messageList.setAdapter(messageListAdapter);
				setList(tempList);
				lv_messageList.setData(list);
			}
		});

		// 显示全部

		// showallBtn.setPressed(true);
		showallBtn.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				// setBtnPressedBg(v);
				// messageListAdapter = new MessageListAdapter(getBaseContext(),
				// messageList);
				// lv_messageList.setAdapter(messageListAdapter);
				setList(messageList);
				lv_messageList.setData(list);
			}
		});

		// 分页
		// 上一页
		// this.mtv_Prev.setOnClickListener(new OnClickListener() {
		// public void onClick(final View v) {
		// mHandler.post(disableButtons);
		// prevPage();
		// //if(deviceType == 1)
		// // findViewById(R.id.selallBtn).invalidate(0, 0,
		// 600,800,UPDATEMODE_4);
		// }
		// });
		//
		// // 下一页
		// this.mtv_Next.setOnClickListener(new OnClickListener() {
		// public void onClick(final View v) {
		// mHandler.post(disableButtons);
		// nextPage();
		// //if(deviceType == 1)
		// // findViewById(R.id.selallBtn).invalidate(0, 0,
		// 600,800,UPDATEMODE_4);
		// }
		// });

	}

	private OnUiItemClickListener menuclick = new OnUiItemClickListener() {

		@Override
		public void onUiItemClick(PviUiItem item) {

			String vTag = item.id;
			if (vTag.equals("selall")) {
				selectOrDeselectAllMessage();
			} else if (vTag.equals("view")) {// 查看消息

				getPopmenu().dismiss();
				if (lastSelect > -1 && lastSelect < messageList.size()) {
					readMessage();
					messageList.get(lastSelect)[2] = "1";
					int count = 0;
					for (String s[] : messageList) {
						if (!"1".equals(s[2])) {
							count++;
						}
					}
					tv_totalRecordCount.setText("   您共有" + totalRecordCount
							+ "条消息(" + count + "条未读)");
				}

			} else if (vTag.equals("del")) {// 删除
				getPopmenu().dismiss();
				deleteMessage();
			} else if (vTag.equals("delall")) {// 清空消息
				getPopmenu().dismiss();
				// int childCount = lv_messageList.getChildCount();
				for (int i = 0; i < list.size(); i++) {
					PviUiItem[] pui = list.get(i);
					pui[2].res = R.drawable.check;
				}
				deleteMessage();
			}

		}

	};

	@Override
	public OnUiItemClickListener getMenuclick() {
		return this.menuclick;
	}

	private void readMessage() {
		if (messageList != null) {
			if (-1 < lastSelect && lastSelect < messageList.size()) {
				final String[] message = messageList.get(lastSelect);
				if (message != null) {

					Logger.i(LOG_TAG, "start to read message,messageID:"
							+ message[0]);
					new Thread() {
						public void run() {

							HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
							HashMap ahmNamePair = CPManagerUtil
									.getAhmNamePairMap();
							String xMLBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Request><SyncMessageReq><MessageIDList></messageID>"
									+ message[0]
									+ "</messageID></MessageIDList></SyncMessageReq></Request>";
							ahmNamePair.put("XMLBody", xMLBody);
							HashMap responseMap = null;

							try {
								responseMap = CPManager.syncMessageState(
										ahmHeaderMap, ahmNamePair);
							} catch (HttpException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}

							Logger.i(LOG_TAG, "read message:" + responseMap);

						};
					}.start();
					Logger.i(LOG_TAG, "the end of read message");

					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("fromuserName", message[4]);
					bundleToSend.putString("time", message[5]);
					bundleToSend.putString("title", message[6]);
					bundleToSend.putString("content", message[7]);
					final Intent tmpintent = new Intent(getBaseContext(),
							ShowMessageActivity.class);
					tmpintent.putExtras(bundleToSend);
					// add by kizan for bug 511
					// set the message status to read status
					unreadRecordCount = ""
							+ (Integer.valueOf(unreadRecordCount) - 1);
					message[2] = "1";
					startActivity(tmpintent);
				} else {
					mHandler.sendEmptyMessage(ALERT_NOSEL);
				}
			} else {
				mHandler.sendEmptyMessage(ALERT_NOSEL);
			}
		} else {
			mHandler.sendEmptyMessage(ALERT_NOSEL);
		}

	}

	// add by kizan for bug 90
	private void selectOrDeselectAllMessage() {
		closePopmenu();
		// 全选
		// int childCount = lv_messageList.getChildCount();
		// for (int i = 0; i < childCount; i++) {
		// ViewGroup itemview = (ViewGroup) lv_messageList.getChildAt(i);
		// CheckBox cb = (CheckBox) itemview.getChildAt(3);
		//
		// // cb.setChecked(!cb.isChecked());
		// if (allCheckedSelect) {
		// if (cb.isChecked()) {
		// cb.setChecked(false);
		// }
		//
		// } else {
		// if (!cb.isChecked()) {
		// cb.setChecked(true);
		// }
		//
		// }
		// }
		for (PviUiItem[] pvi : list) {
			if (allCheckedSelect) {
				pvi[2].res = R.drawable.notcheck;
			} else {
				pvi[2].res = R.drawable.check;
			}

		}
		lv_messageList.setData(list);
		lv_messageList.invalidate();
		allCheckedSelect = !allCheckedSelect;

	}

	private int getMessageCount(String readFlag) {
		int count = 0;
		for (String[] message : messageList) {
			if (message[2].equals(readFlag)) {
				count++;
			}
		}
		return count;
	}

	public void showAlert(String message) {
		if (pd != null && pd.isShowing()) {
			pd.dismiss();
		}
		pd = new PviAlertDialog(getParent());
		pd.setTitle(getResources().getString(R.string.my_friend_hint));
		// pd.setMessage(message);
		TextView tv = new TextView(MessageCenterActivity.this);
		tv.setText(message);
		tv.setTextSize(21);
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(Color.BLACK);

		pd.setView(tv);
		pd.setCanClose(true);
		pd.show();
	}

	private Runnable updataMessageSummary = new Runnable() {
		@Override
		public void run() {
			tv_totalRecordCount.setText("    您共有" + totalRecordCount + "条消息("
					+ getMessageCount("0") + "条未读)");
			list = new ArrayList<PviUiItem[]>();
			lv_messageList.setData(list);
			lv_messageList.invalidate();
			curpage = 1 ;
			pages = 1 ;
			updatePagerinfo(curpage + "/" + pages);
		
		}
	};

	private Runnable updataPager = new Runnable() {
		@Override
		public void run() {

			// if(pages == 0){
			// mtv_Pages.setText("1");
			// }else{
			// mtv_Pages.setText("" + pages);
			// }
			//            
			// if(curpage == 0){
			// mtv_CurPage.setText("1");
			// }else{
			// mtv_CurPage.setText("" + curpage);
			// }

		}
	};

	public void OnPrevpage() {
		prevPage();
		super.OnPrevpage();
	}

	private void prevPage() {
		if (!loading) {
			mHandler.post(disableButtons);
			if (total > 0 && curpage > 1) {
				curpage = curpage - 1;
				start = (curpage - 1) * perpage + 1;
				mHandler.sendEmptyMessage(GET_DATA);
			}
		}
	}

	@Override
	public void OnNextpage() {
		nextPage();
		super.OnNextpage();
	}

	private void nextPage() {
		if (!loading) {
			mHandler.post(disableButtons);
			if (total > 0 && curpage < pages) {
				curpage = curpage + 1;
				start = (curpage - 1) * perpage + 1;
				mHandler.sendEmptyMessage(GET_DATA);
			}
		}
	}

	private Runnable enableButtons = new Runnable() {

		@Override
		public void run() {
			getPopmenu();
			showReadedBtn.setClickable(true);
			showUnreadBtn.setClickable(true);
			showallBtn.setClickable(true);
			getButton.setClickable(true);
			sendButton.setClickable(true);
			selallBtn.setClickable(true);
			delBtn.setClickable(true);
			delallBtn.setClickable(true);
			lv_messageList.setEnabled(true);
			lv_messageList.setClickable(true);
			lv_messageList.postInvalidate();

		}

	};

	private Runnable disableButtons = new Runnable() {

		@Override
		public void run() {
			showReadedBtn.setClickable(false);
			showUnreadBtn.setClickable(false);
			showallBtn.setClickable(false);
			getButton.setClickable(false);
			sendButton.setClickable(false);
			selallBtn.setClickable(false);
			delBtn.setClickable(false);
			delallBtn.setClickable(false);
			lv_messageList.setEnabled(false);
			lv_messageList.setClickable(false);
		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && lv_messageList != null
				&& lv_messageList.hasFocus()) {
			mHandler.post(disableButtons);
			prevPage();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && lv_messageList != null
				&& lv_messageList.hasFocus()) {
			mHandler.post(disableButtons);
			nextPage();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}
