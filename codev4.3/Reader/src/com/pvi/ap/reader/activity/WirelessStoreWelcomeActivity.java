package com.pvi.ap.reader.activity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/***
 * kizan 
 * @author rd045
 * 
 */
public class WirelessStoreWelcomeActivity extends PviActivity {
	private final int FAIL_NETWORK = 100;
	private final int GET_MSG = 1;
	private final int SHOW_MSG = 2;

	private welcome_view welcomeview = null;

	private ArrayList<HashMap<String, Object>> BroadcastInfo = new ArrayList<HashMap<String, Object>>();

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int msgId = msg.what;
			switch (msgId) {
			case FAIL_NETWORK:
				PviAlertDialog pd = null;
				if(getParent()!=null)
				{
					pd = new PviAlertDialog(
							getParent());
				}
				else
				{
					pd = new PviAlertDialog(
							WirelessStoreWelcomeActivity.this);
				}

				pd.setTitle("网络异常");
				pd.setMessage("数据读取错误!",Gravity.CENTER);
				pd.setCanClose(true);
				if(!WirelessStoreWelcomeActivity.this.isFinishing())
				{
					pd.show();
				}

				break;

			case SHOW_MSG:
				setBroadcastInfo();
				break;

			case GET_MSG:
				new Thread() {
					public void run() {
						if (getBroadcastInfo()) {
							handler.sendEmptyMessage(SHOW_MSG);
						} else {
							handler.sendEmptyMessage(FAIL_NETWORK);
						}
					}
				}.start();

				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.welcomdialog_ui1);
		super.onCreate(savedInstanceState);
		welcomeview = (welcome_view) this.findViewById(R.id.welcomeview);
		handler.sendEmptyMessage(GET_MSG);
		Intent hiddenStatus = new Intent(MainpageActivity.HIDE_TIP);
		sendBroadcast(hiddenStatus);
		long tmpTimeNow = System.currentTimeMillis();
		//		Config.setString("STORE_WELCOME_TIME", Long.toString(tmpTimeNow));
		SharedPreferences settings = getSharedPreferences(
				Constants.configFileName, 0);

		String state = settings.getString(WelcomeSetActivity.STATE, "");
		if (state.equals("open")) {
			SharedPreferences.Editor ed = settings.edit();
			ed.putString(WirelessStoreMainpageActivity.STORE_WELCOME_TIME,String.valueOf(tmpTimeNow));
			ed.commit();
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//		closebtn.requestFocus();
		welcomeview.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int SelIndex = welcomeview.getFocusIndex();
				if(SelIndex==welcome_view.ERROR){
					return;
				}
				if (SelIndex-1 > BroadcastInfo.size() - 1 || BroadcastInfo.size() - 1 <=0) {
					return;
				}
				if(SelIndex == welcome_view.CLOSEBTN)
				{
					
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					finish();
					return;
				}
				
				HashMap<String, Object> map = BroadcastInfo.get(SelIndex-1);
				String type = map.get("type").toString();

				// the intent only need if the welcome dialog is set open
				// commented it ..kizan
				Intent intent = null;
				Bundle sndbundle = null;

				if (type.equals("1")) {
					// 跳转至 个人中心然后跳到 消息中心
					sndbundle = new Bundle();
					intent = new Intent(MainpageActivity.START_ACTIVITY);
					sndbundle.putString("actID",
					"ACT14600");
					sndbundle.putString("pviapfStatusTip", getResources()
							.getString(R.string.kyleHint01));
					intent.putExtras(sndbundle);

					// add end
				} else if (type.equals("2")) {
					// 跳转至在线阅读
					String contentID = "";
					String chapterID = "";
					String chapterName = "";
					String contentName = "";
					String position = "";
					if (map.containsKey("contentId")) {
						contentID = map.get("contentId").toString();
					} else {
						//
						return;
					}
					if (map.containsKey("contentName")) {
						contentName = map.get("contentName").toString();
					} else {
						return;
					}
					if (map.containsKey("chapterId")) {
						chapterID = map.get("chapterId").toString();
					} else {
						return;
					}
					if (map.containsKey("chapterName")) {
						chapterName = map.get("chapterName").toString();
					} else {
						return;
					}
					if (map.containsKey("position")) {
						position = map.get("position").toString();
					} else {
						return;
					}

					sndbundle = new Bundle();
					intent = new Intent(MainpageActivity.START_ACTIVITY);
					sndbundle.putString("act",
					"com.pvi.ap.reader.activity.ReadingOnlineActivity");
					sndbundle.putString("haveTitleBar", "1");
					sndbundle.putString("ContentID", contentID);
					sndbundle.putString("ChapterName", chapterName);
					sndbundle.putString("ChapterID", chapterID);
					sndbundle.putString("position", position);
					sndbundle.putString("contentName", contentName);
					sndbundle.putString("pviapfStatusTip", getResources()
							.getString(R.string.kyleHint01));
					intent.putExtras(sndbundle);

					//				
				} else if (type.equals("3")) {
					// 跳转至书籍摘要页

					intent = new Intent(MainpageActivity.START_ACTIVITY);
					sndbundle = new Bundle();
					sndbundle.putString("act",
					"com.pvi.ap.reader.activity.BookSummaryActivity");

					sndbundle.putString("contentID", map.get("commentBookID")
							.toString());
					intent.putExtras(sndbundle);

				} else if (type.equals("4") || type.equals("5")) {
					// 跳转至书包摘要页

					intent = new Intent(MainpageActivity.START_ACTIVITY);
					sndbundle = new Bundle();
					sndbundle.putString("catalogID", map.get("sortCatalogID")
							.toString());
					sndbundle.putString("catalogName", map.get("sortCatalogName")
							.toString());
					sndbundle.putString("act",
					"com.pvi.ap.reader.activity.BookPackageInfoActivity");

					intent.putExtras(sndbundle);

				}

				else if (type.equals("6")) {
					// 跳转至书籍资讯页面

					intent = new Intent(MainpageActivity.START_ACTIVITY);
					sndbundle = new Bundle();
					sndbundle.putString("bookNewsID", map.get("bookNewID")
							.toString());
					sndbundle.putString("act",
					"com.pvi.ap.reader.activity.InfoContentActivity");
					intent.putExtras(sndbundle);

				} else if ("7".equals(type)) {
					// show messge
					sndbundle = new Bundle();
					sndbundle.putString("fromuserName", "移动平台");
					sndbundle.putString("time", "");
					sndbundle
					.putString("title", map.get("textMsgTitle").toString());
					sndbundle.putString("content", map.get("textMsg").toString());
					intent = new Intent(getBaseContext(),ShowMessageActivity.class);

					intent.putExtras(sndbundle);
					startActivity(intent);
					return;

				} else {
					return;
				}

				sendBroadcast(intent);

			}
		});
	}

	public boolean getBroadcastInfo() {
		BroadcastInfo.clear();

		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();

		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

		// modified by kizan 2010.12.14 for bug 402
		// we must create the hashmap in the iterator to get the different
		// element
		// I just commented the line below and create the hashmap in every
		// iterator
		HashMap<String, Object> map = null;
		// new HashMap<String, Object>();

		HashMap responseMap = null;

		try {
			// 以POST的形式连接请求
			responseMap = CPManager.getHandsetBroadcast(ahmHeaderMap,
					ahmNamePair);
		} catch (Exception e) {
			// IO异常 ,一般原因为网络问题
			e.printStackTrace();
			return false;
		}

		byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
		//
//		try {
//			System.out.println("返回的XML为：");
//			System.out.println(CPManagerUtil.getStringFrombyteArray(responseBody));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}

		// 根据返回字节数组构造DOM
		Document dom = null;
		try {
			dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		Element root = dom.getDocumentElement();
		// 以下为解释XML的代码
		// ......
		NodeList nl = root.getElementsByTagName("HandsetBroadcastMsg");

		Element temp = null;
		NodeList NLtemp = null;
		String type = "";
		String name = "";
		String value = "";


		for (int i = 0; i < nl.getLength(); i++) {
			temp = (Element) nl.item(i);
			NLtemp = temp.getElementsByTagName("type");
			type = ((Element) NLtemp.item(0)).getFirstChild().getNodeValue();

			// modified by kizan for bug 402
			// here: should create the different HashMap instance
			map = new HashMap<String, Object>();

			map.put("type", type);
			map.put("count", ((Element) temp.getElementsByTagName("count")
					.item(0)).getFirstChild().getNodeValue());

			NLtemp = temp.getElementsByTagName("Parameter");

			for (int j = 0; j < NLtemp.getLength(); j++) {
				temp = (Element) NLtemp.item(j);
				name = ((Element) temp.getElementsByTagName("name").item(0))
				.getFirstChild().getNodeValue();
				try {
					value = ((Element) temp.getElementsByTagName("value").item(
							0)).getFirstChild().getNodeValue();
				} catch (Exception e) {
					Log.d("Reader", "GetBroadcast Val Exception: "
							+ e.toString());
					value = "";
					return false;
				}
				map.put(name, value);
			}

			this.BroadcastInfo.add(map);
		}
		map = new HashMap<String, Object>();
		nl = root.getElementsByTagName("createTime");
		temp = (Element)nl.item(0);
		if(temp!=null)
		{
			map.put("createTime", temp.getFirstChild().getNodeValue());
		}
		this.BroadcastInfo.add(map);
		return true;
	}

	public void setBroadcastInfo() {
		HashMap<String, Object> map = null;
		String type = "";
		String text = "";

		String[] iteminfo = new String[this.BroadcastInfo.size()-1<7?this.BroadcastInfo.size()-1:7 + 1];
		for (int i = 0; i < (this.BroadcastInfo.size()-1<7?this.BroadcastInfo.size()-1:7); i++) {
			text = "   " + (i + 1) + ". ";

			map = this.BroadcastInfo.get(i);
			type = map.get("type").toString();
			if (type.equals("1")) {
				text = text + "最新消息 : 查看消息中心 (共" + map.get("count").toString()
				+ "条)";
			} else if (type.equals("2")) {
				if (map.containsKey("newBookTitle")) {
					text = text + map.get("newBookTitle".toString());
				} else {
					text = text + "最新阅读 ";
				}
			} else if (type.equals("3")) {
				if (map.containsKey("commentBookTitle")) {
					text = text + map.get("commentBookTitle".toString());
				} else {
					text = text + "推荐书籍 ";
				}
			} else if (type.equals("4")) {

				if (map.containsKey("sortCatalogTitle")) {
					text = text + map.get("sortCatalogTitle".toString());
				} else {
					text = text + "包月栏目 ";
				}
			} else if (type.equals("5")) {

				if (map.containsKey("sortCatalogTitle")) {
					text = text + map.get("sortCatalogTitle".toString());
				} else {
					text = text + "包月栏目 ";
				}
			} else if (type.equals("6")) {

				if (map.containsKey("bookNewTitle")) {
					text = text + map.get("bookNewTitle".toString());
				} else {
					text = text + "书籍资讯 ";
				}
			} else if (type.equals("7")) {
				if (map.containsKey("textMsg")) {
					text = text + map.get("textMsg".toString());
				} else {
					text = text + "文字信息";
				}
			}
			iteminfo[i] = text;

		}
		String tempstr = this.BroadcastInfo.get(BroadcastInfo.size()-1).get("createTime").toString();
		try {
			iteminfo[iteminfo.length-1] = "消息更新时间:"+GlobalVar.TimeFormat("yyyyMMddHHmmss", tempstr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			iteminfo[iteminfo.length-1] = "消息更新时间:"+ tempstr;
			e.printStackTrace();
		}
		this.welcomeview.setWelItem(iteminfo);
	}

}
