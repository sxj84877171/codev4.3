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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.InactiveFunction;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;
/**
 * 在线阅读的书籍目录
 * @author rd040 马中庆
 *
 */
public class BookCatalogActivity extends PviActivity {

	private static final String REFLASH = "WirelessList";
	protected static final String TAG = "BookCatalogActivity";
	private Context mContext = BookCatalogActivity.this;

	private String contentID = "";
	private Intent revIntent = null;
	private Bundle revBundle = null;
	private boolean isSerial = false;
	private boolean isFinish = false;
	private String bookname = "";

	private int currentPage = 1;
	private int itemPerPage = 8;
	private int totalPage = 0;

	private TextView lastedreadchapter = null;
	private TextView lastedreadfee = null;
	private TextView updatechapter = null;
	private TextView updatefee = null;

	//	private int volumnNum = 0;
	private int RecentBlockNum = 0;
	private Handler mHandler = null;	
	private LinearLayout recentblock = null;

	private AbsoluteLayout[] chapterlayout = new AbsoluteLayout[8];
	private LinearLayout lastedlayout = null;
	private LinearLayout updatelayout = null;

	private HashMap<String, Object> lastestmap = null;

	private String lastestchapterID = "";
	private String lastestchapter = "";
	private String lastestvolume = "";
	private String lastestchargetype = "";
	private ArrayList<HashMap<String, Object>> bookchapter = new ArrayList<HashMap<String, Object>>();

	private HashMap<String, String> volumnNumforPages = new HashMap<String, String>();
	private TextView[] chapters = new TextView[8];
	private TextView[] fees = new TextView[8];

	private ArrayList<String[]> pageStart = new ArrayList<String[]>();

	private String SelIndex = "";

	private int vol = 0;
	private int chapter = 0;
	private boolean onGoing = false;

	private void getvolchapter(int index)
	{
		int total = 0;
		int pretotal = 0;
		for(int i=0; i<bookchapter.size();i++)
		{
			HashMap<String, Object> map = bookchapter.get(i);

			pretotal = total;
			total = total + Integer.parseInt(map.get("VolumeLen").toString());

			if(map.containsKey("Index0")&&map.get("Index0").equals("1"))
			{		
				total = total + 1;
				if(index == pretotal)
				{
					index ++;
				}
			}
			if(index<=total)
			{
				vol = i;
				chapter = index - pretotal;
				return;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
        revIntent = this.getIntent();
        revBundle = revIntent.getExtras();
        contentID = revBundle.getString("contentID");
        isSerial = revBundle.getBoolean("IsSerial");
        isFinish = revBundle.getBoolean("IsFinish");

        bookname = revBundle.getString("BookName");

        Intent intent = new Intent(MainpageActivity.SET_TITLE);
        Bundle sndBundle = new Bundle();
        sndBundle.putString("title", bookname+"[>>]书籍目录");
        intent.putExtras(sndBundle);
        sendBroadcast(intent);
		
		
		setUIData();
		//for marvell
		//EPDRefresh.refreshGCOnceFlash();
	}

	private View.OnClickListener chapteritemclicklistener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String ChapterID = "";
			String Volumename = "";

			try
			{
				int chapteritemviewid = v.getId();
				switch(chapteritemviewid)
				{
				case R.id.lastedlayout:

					Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle sndBundle = new Bundle();
					sndBundle.putString("act","com.pvi.ap.reader.activity.ReadingOnlineActivity");
					sndBundle.putString("ContentID", contentID);
					sndBundle.putString("haveTitleBar", "1");
					sndBundle.putString("ChapterID", lastestmap.get("chapterid").toString());
					sndBundle.putString("ChapterName", lastestmap.get("chapterName").toString());			
					sndBundle.putString("FontSize", lastestmap.get("FontSize")
							.toString());
					sndBundle.putString("Position", lastestmap.get("readposition")
							.toString());
					sndBundle.putString("LineSpace", lastestmap.get("LineSpace")
							.toString());
					intent.putExtras(sndBundle);
					sendBroadcast(intent);
					return;
				case R.id.updatelayout:
					Intent sndintent = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle sndbundle = new Bundle();
					sndbundle.putString("act","com.pvi.ap.reader.activity.ReadingOnlineActivity");
					sndbundle.putString("startType",  "allwaysCreate");  
					sndbundle.putString("haveTitleBar", "1");
					sndbundle.putString("ContentID", contentID);
					sndbundle.putString("ChapterName", lastestchapter);
					sndbundle.putString("ChapterID", lastestchapterID);
					sndintent.putExtras(sndbundle);
					sendBroadcast(sndintent);
					return;
				case R.id.chapter01layout:
					getvolchapter(0);
					break;
				case R.id.chapter02layout:
					getvolchapter(1);
					break;
				case R.id.chapter03layout:
					getvolchapter(2);
					break;
				case R.id.chapter04layout:
					getvolchapter(3);

					break;
				case R.id.chapter05layout:
					getvolchapter(4);
					break;
				case R.id.chapter06layout:
					getvolchapter(5);
					break;
				case R.id.chapter07layout:
					getvolchapter(6);
					break;
				case R.id.chapter08layout:
					getvolchapter(7);
					break;
				}
				HashMap<String, Object> map = bookchapter.get(vol);
				Volumename = map.get("volumnName").toString();
				ChapterID = map.get("ChapterID" + (chapter-1)).toString();

				Intent sndintent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle sndbundle = new Bundle();
				sndbundle.putString("act","com.pvi.ap.reader.activity.ReadingOnlineActivity");
				sndbundle.putString("startType",  "allwaysCreate");  
				sndbundle.putString("haveTitleBar", "1");
				sndbundle.putString("ContentID", contentID);
				sndbundle.putString("ChapterName", map.get("ChapterName" + (chapter-1)).toString());
				sndbundle.putString("ChapterID", ChapterID);
				sndintent.putExtras(sndbundle);
				sendBroadcast(sndintent);
				return;
			}
			catch(Exception e)
			{
				e.printStackTrace();	
			}
		}
	};


	private boolean getBookCatalogInfo() {

		bookchapter.clear();
		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();

		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

		ahmNamePair.put("contentId", contentID);

		if(reflash){
		    showNetWorkProcessing2();
			ahmNamePair.put("reflash",REFLASH);
			reflash = false;
		}else{
		    showNetWorkProcessing();
		}

		String start = "";
		String count = "";

		if (currentPage == 1)
		{
			start = "1";
			count = String.valueOf(itemPerPage);
		}
		else
		{
		    if(volumnNumforPages.get(""+(currentPage-1))==null){
		        throw new NullPointerException("system err");
		    }
		    int countVolShowPrevpages = Integer.parseInt(volumnNumforPages.get(""+(currentPage-1)));
		    //Logger.d(TAG,"use:"+(currentPage-1)+"->"+countVolShowPrevpages);
			if(this.RecentBlockNum!=0)
			{
				start = String.valueOf((currentPage-1)*itemPerPage +1 - this.RecentBlockNum - countVolShowPrevpages );
				count = String.valueOf(itemPerPage);
			}
			else
			{
				start = String.valueOf((currentPage-1)*itemPerPage +1 - countVolShowPrevpages );
				count = String.valueOf(itemPerPage);
			}
		}
		ahmNamePair.put("start", start);
		ahmNamePair.put("count", count);

		HashMap responseMap = null;
		try {
			// 以POST的形式连接请求
			responseMap = NetCache.getChapterList(ahmHeaderMap, ahmNamePair);
			if (!responseMap.get("result-code").toString().contains(
			"result-code: 0")) {
				Bundle temp = new Bundle();
				temp.putString("Ret", responseMap.get("result-code").toString());
				Message msg = new Message();
				msg.what=4;
				msg.setData(temp);
				mHandler.sendMessage(msg);

				Intent intent1 = new Intent(MainpageActivity.HIDE_TIP);
				sendBroadcast(intent1);
				return false;
			}
		}
		catch (SocketTimeoutException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(3);
			return false;
		}catch (HttpException e) {
			// 连接异常 ,一般原因为 URL错误
			e.printStackTrace();
			mHandler.sendEmptyMessage(2);
			return false;
		} catch (IOException e) {
			// IO异常 ,一般原因为网络问题
			e.printStackTrace();
			mHandler.sendEmptyMessage(2);
			return false;
		}

		byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
		
/*		try {
			System.out.println("返回的XML为：");
			System.out.println(CPManagerUtil
					.getStringFrombyteArray(responseBody));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}*/
		
		// 根据返回字节数组构造DOM
		Document dom = null;
		try {
			dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(2);
			return false;
		} catch (SAXException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(2);
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(2);
			return false;
		}
		
		if(dom==null){
		    throw new NullPointerException("dom is null");
		}

		Element root = dom.getDocumentElement();
		NodeList nl = null;
		Element element = null;
		Element temp = null;
		nl = root.getElementsByTagName("totalRecordCount");
		element = (Element) nl.item(0);

		int totalrecord = Integer.parseInt(element.getFirstChild().getNodeValue());
		//		totalPage 
		nl = root.getElementsByTagName("totalVolumnCount");
		element = (Element) nl.item(0);

		int totalvolume = Integer.parseInt(element.getFirstChild().getNodeValue());

		if (isSerial && !isFinish) {

			nl = root.getElementsByTagName("LastestChapter");
			element = (Element) nl.item(0);
			temp = (Element) element.getElementsByTagName("chapterName")
			.item(0);
			lastestchapter = temp.getFirstChild().getNodeValue();

			temp = (Element) element.getElementsByTagName("volumnName").item(0);
			lastestvolume = temp.getFirstChild().getNodeValue();

			temp = (Element) element.getElementsByTagName("type").item(0);
			lastestchargetype = temp.getFirstChild().getNodeValue();

			temp = (Element) element.getElementsByTagName("volumnID").item(0);


			temp = (Element) element.getElementsByTagName("chapterID").item(0);
			lastestchapterID = temp.getFirstChild().getNodeValue();


		}
		//		else
		//		{
		//			totalPage = (int) Math.ceil((double)totalrecord/this.itemPerPage);
		//		}
		//		if((lastestmap!=null)&&(!lastestmap.get("chapterid").equals("")))
		//		{
		totalPage = (int) Math.ceil(((double)(totalrecord + totalvolume + RecentBlockNum))/this.itemPerPage);	
		//		}
		//		else
		//		{
		//			totalPage = (int) Math.ceil(((double)(totalrecord + totalvolume + 1))/this.itemPerPage);
		//		}

		nl = root.getElementsByTagName("VolumnInfo");
		Element voltemp = null;
		Element volchapter = null;
		String volname = "";
		
		/*需要统计 前面的页面中，一共有多少个“显示”的 卷;算法：上一页的该值+本页该值；
        是否“显示”，两种判断逻辑 1：判断前面的页中是否已经显示过  2：chapter中是否包含index为0的；这里使用逻辑2
        修改：逻辑2有错误，因为取回的数据并不一定都会被显示出来！
        可以考虑把本逻辑放到show出本页数据的地方
       */   
       int countVolShowThispage = 0;
		
		for (int i = 0; i < nl.getLength(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			voltemp = (Element) nl.item(i);
			volname = ((Element) voltemp.getElementsByTagName(
			"volumnName").item(0)).getFirstChild().getNodeValue();

			map.put("volumnName", volname);
			


			NodeList volnode = voltemp.getElementsByTagName("chapterName");
			map.put("VolumeLen", volnode.getLength());
			for (int j = 0; j < volnode.getLength(); j++) {
				volchapter = (Element) volnode.item(j);
				map.put("ChapterName" + j, volchapter.getFirstChild()
						.getNodeValue());

			}

			NodeList chapterid = voltemp.getElementsByTagName("chapterID");
			for (int j = 0; j < chapterid.getLength(); j++) {
				volchapter = (Element) chapterid.item(j);
				map.put("ChapterID" + j, volchapter.getFirstChild()
						.getNodeValue());

			}

			volnode = voltemp.getElementsByTagName("type");
			for (int j = 0; j < volnode.getLength(); j++) {
				volchapter = (Element) volnode.item(j);
				map.put("ChargeType" + j, volchapter.getFirstChild()
						.getNodeValue());

			}
			
			

			String maxindex = "";
			volnode = voltemp.getElementsByTagName("index");
			for (int j = 0; j < volnode.getLength(); j++) {
				volchapter = (Element) volnode.item(j);
				maxindex = volchapter.getFirstChild()
				.getNodeValue();
				map.put("Index" + j, maxindex);
				
/*				if("1".equals(maxindex)){
				    countVolShowThispage ++;
				}*/

			}

			bookchapter.add(map);
			
			
		}
		
		//保存起来本页的之前所有页（包括本页）总“显示”卷的数目
/*		if(!volumnNumforPages.containsKey(""+currentPage))
		{
		    int countShowPrevpages = 0;
		    if(currentPage>1)
            {
		        countShowPrevpages = Integer.parseInt(volumnNumforPages.get(""+(currentPage-1)))+countVolShowThispage;
            }else{
                countShowPrevpages = countVolShowThispage;
            }
		    volumnNumforPages.put(""+currentPage, ""+countShowPrevpages);
		    Logger.d(TAG,currentPage+"->"+countShowPrevpages);
		}*/

		return true;
	}


	@Override
	public OnUiItemClickListener getMenuclick() {
		// TODO Auto-generated method stub
		return this.menuclick;
	}

	private boolean reflash = false;

	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {
            String vTag = item.id;
            if (vTag.equals("reflash")) { //
                //Logger.d("Click","clicl reflash");
                reflash = true;
                currentPage = 1;
                onResume();
                closePopmenu();
            }
        }};	


		@Override
		public void onCreate(Bundle savedInstanceState) {
			GlobalVar appState = ((GlobalVar) getApplicationContext());
			
			this.setContentView(R.layout.bookcatalog_ui1);
			
			try {



				chapters[0] = (TextView) findViewById(R.id.chapter01);
				chapters[1] = (TextView) findViewById(R.id.chapter02);
				chapters[2] = (TextView) findViewById(R.id.chapter03);
				chapters[3] = (TextView) findViewById(R.id.chapter04);
				chapters[4] = (TextView) findViewById(R.id.chapter05);
				chapters[5] = (TextView) findViewById(R.id.chapter06);
				chapters[6] = (TextView) findViewById(R.id.chapter07);
				chapters[7] = (TextView) findViewById(R.id.chapter08);

				this.chapters[0].requestFocus();

				fees[0] = (TextView) findViewById(R.id.fee01);
				fees[1] = (TextView) findViewById(R.id.fee02);
				fees[2] = (TextView) findViewById(R.id.fee03);
				fees[3] = (TextView) findViewById(R.id.fee04);
				fees[4] = (TextView) findViewById(R.id.fee05);
				fees[5] = (TextView) findViewById(R.id.fee06);
				fees[6] = (TextView) findViewById(R.id.fee07);
				fees[7] = (TextView) findViewById(R.id.fee08);

				this.chapterlayout[0] = (AbsoluteLayout)this.findViewById(R.id.chapter01layout);
				this.chapterlayout[1] = (AbsoluteLayout)this.findViewById(R.id.chapter02layout);
				this.chapterlayout[2] = (AbsoluteLayout)this.findViewById(R.id.chapter03layout);
				this.chapterlayout[3] = (AbsoluteLayout)this.findViewById(R.id.chapter04layout);
				this.chapterlayout[4] = (AbsoluteLayout)this.findViewById(R.id.chapter05layout);
				this.chapterlayout[5] = (AbsoluteLayout)this.findViewById(R.id.chapter06layout);
				this.chapterlayout[6] = (AbsoluteLayout)this.findViewById(R.id.chapter07layout);
				this.chapterlayout[7] = (AbsoluteLayout)this.findViewById(R.id.chapter08layout);

				this.lastedlayout = (LinearLayout)this.findViewById(R.id.lastedlayout);
				this.updatelayout = (LinearLayout)this.findViewById(R.id.updatelayout);

				this.lastedreadchapter = (TextView)this.findViewById(R.id.lastedreadchapter);
				this.lastedreadfee = (TextView)this.findViewById(R.id.lastedreadfee);
				this.updatechapter = (TextView)this.findViewById(R.id.updatechapter);
				this.updatefee = (TextView)this.findViewById(R.id.updatefee);
				this.recentblock = (LinearLayout)this.findViewById(R.id.recentblock);

				recentblock.setVisibility(View.GONE);
				lastedlayout.setVisibility(View.GONE);
				updatelayout.setVisibility(View.GONE);

				mHandler = new Handler()
				{

					@Override
					public void handleMessage(Message msg) {
						// TODO Auto-generated method stub
						switch(msg.what){
						case 0:
							//正常情况下消失对话框，并获得焦点
							if((RecentBlockNum!=0)&&(currentPage == 1))
							{
								if(isSerial && !isFinish)
								{
									updatelayout.requestFocus();
								}

								if((lastestmap!=null)&&(!lastestmap.get("chapterid").equals("")))
								{
									lastedlayout.requestFocus();
								}
							}
							else
							{
								chapterlayout[0].requestFocus();
							}

							break;
						case 1:
							//触屏点击时执行click事件
							if(SelIndex.equals("update"))
							{
								if(RecentBlockNum == 2)
								{
									updatelayout.performClick();
								}
							}
							else
							{
								int sel = Integer.parseInt(SelIndex);
								if(RecentBlockNum != 0)
								{
									chapterlayout[sel].performClick();
								}
								else if(sel != 0)
								{
									chapterlayout[sel].performClick();
								}
							}
							break;
						case 2:
							//联网失败信息提示
							final PviAlertDialog pd1 = new PviAlertDialog(getParent());
							pd1.setTitle("温馨提示");
							pd1.setMessage("联网获取书籍目录信息失败！");
							pd1.setCanClose(true);
							pd1.setButton("确定", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									//sendBroadcast(new Intent(MainpageActivity.BACK));
								}
							});
							pd1.show(); 

							break;
						case 3:
							//联网超时提示

							final PviAlertDialog pd2 = new PviAlertDialog(getParent());
							pd2.setTitle("温馨提示");
							pd2.setMessage("联网超时，获取数籍目录信息失败！");
							//						pd2.setCanClose(true);
							pd2.setButton("确定", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									//sendBroadcast(new Intent(MainpageActivity.BACK));
								}
							});
							pd2.show(); 
							break;
						case 4:

							Bundle temp = msg.getData();
							final PviAlertDialog pd3 = new PviAlertDialog(getParent());
							pd3.setTitle("温馨提示");
							pd3.setMessage(com.pvi.ap.reader.data.common.Error.getErrorDescriptionForContent(temp.getString("Ret")));
							pd3.setButton("确定", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									//sendBroadcast(new Intent(MainpageActivity.BACK));
								}
							});
							pd3.show();
							break;
						}

						super.handleMessage(msg);
					}

				};


			} catch (Exception e) {
				Logger.e(TAG, "Exception: " + e.toString());
			}

			lastedlayout.setOnClickListener(chapteritemclicklistener);
			updatelayout.setOnClickListener(chapteritemclicklistener);
			for(int i=0;i<this.itemPerPage;i++)
			{
				this.chapterlayout[i].setOnClickListener(chapteritemclicklistener);
				this.chapterlayout[i].setOnTouchListener(new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						if(event.getAction() == MotionEvent.ACTION_UP)
						{
							v.requestFocus();
							v.performClick();

						}
						return true;
					}
				});
			}
			lastedlayout.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if(event.getAction() == MotionEvent.ACTION_UP)
					{
						v.requestFocus();
						v.performClick();

					}
					return true;
				}
			});
			updatelayout.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if(event.getAction() == MotionEvent.ACTION_UP)
					{
						v.requestFocus();
						v.performClick();

					}
					return true;
				}
			});

			
			super.onCreate(savedInstanceState);
		}

		private void setUIData() {
			onGoing = true;
			new Thread() {
				public void run() {					
					if(currentPage==1)
					{
						lastestmap = InactiveFunction.getReadHistory(BookCatalogActivity.this, contentID, true);
					}

					RecentBlockNum = 0;
					if (isSerial && !isFinish) 
					{
						RecentBlockNum = RecentBlockNum + 1;
					}
					if((lastestmap!=null)&&(!lastestmap.get("chapterid").equals("")))
					{
						RecentBlockNum = RecentBlockNum + 1;
					}

					if(!getBookCatalogInfo())
					{
						return;
					}

					mHandler.post(getData);
				}
			}.start();
		}


		/**
		 * build一页UI
		 */
		private Runnable getData = new Runnable() {
			@Override
			public void run() {
				try
				{
					emptyLines();

					mHandler.sendEmptyMessage(0);

					if (((isSerial && !isFinish)||((lastestmap!=null)&&(!lastestmap.get("chapterid").equals(""))))&&(currentPage == 1)) 
					{
						//连载&&非完本（有最新章节）  或有最近阅读
						if(RecentBlockNum == 1)
						{
							chapterlayout[7].setVisibility(View.GONE);
						}
						else if(RecentBlockNum == 2)
						{
							chapterlayout[7].setVisibility(View.GONE);
							chapterlayout[6].setVisibility(View.GONE);
						}

						recentblock.setVisibility(View.VISIBLE);

						if(isSerial && !isFinish)
						{					    
							//						if(RecentBlockNum == 2)
							//						{
							//							lastedlayout.setVisibility(View.VISIBLE);
							//						}
							//						else if(RecentBlockNum == 1)
							//						{
							//							lastedlayout.setVisibility(View.GONE);
							//						}
							updatelayout.setVisibility(View.VISIBLE);
							updatechapter.setVisibility(View.VISIBLE);
							updatefee.setVisibility(View.VISIBLE);
							updatechapter.setText("最新章节：" + lastestvolume + "  "
									+ lastestchapter);
							//Logger.d("Reader", "lastestvolume：" + lastestvolume + "  "+ lastestchapter);
							if (lastestchargetype.equals("0")) {
								updatefee.setText("免费");
							}
							else {
								updatefee.setText("");
							}
						}
						else
						{
							updatelayout.setVisibility(View.GONE);
							updatefee.setVisibility(View.GONE);
							updatechapter.setVisibility(View.GONE);
						}

						if((lastestmap!=null)&&(!lastestmap.get("chapterid").equals("")))
						{

							lastedlayout.setVisibility(View.VISIBLE);
							lastedreadfee.setVisibility(View.VISIBLE);
							lastedreadchapter.setVisibility(View.VISIBLE);
							lastedreadchapter.setText("最近阅读："  + lastestmap.get("chapterName").toString());
						}
						else
						{
							lastedlayout.setVisibility(View.GONE);
							lastedreadfee.setVisibility(View.GONE);
							lastedreadchapter.setVisibility(View.GONE);
						}

						int counter = 0;
						int counterVol = 0;//显示卷 计数器

						for (int i1 = 0; i1 < bookchapter.size(); i1++) {
							//循环结束条件

							if (counter >= itemPerPage - RecentBlockNum) {
								break;
							}

							String volname = bookchapter.get(i1).get("volumnName").toString();	

							if(bookchapter.get(i1).containsKey("Index0")&&bookchapter.get(i1).get("Index0").equals("1"))
							{
								chapters[counter].setVisibility(View.VISIBLE);
								chapters[counter].setText(volname);
								counter ++;
								counterVol ++;
							}
							int len = Integer.parseInt(bookchapter.get(i1).get(
							"VolumeLen").toString());
							for (int j = 0; j < len; j++) {
								//Logger.d(TAG,"counter : "+counter);

								if (counter >= itemPerPage - RecentBlockNum) {
									break;
								}

								chapters[counter].setVisibility(View.VISIBLE);
								fees[counter].setVisibility(View.VISIBLE);

								chapters[counter].setText(bookchapter.get(i1).get("ChapterName" + j).toString());//设置行文本
								if (bookchapter.get(i1).get("ChargeType" + j)
										.toString().equals("0")) {
									fees[counter].setText("免费");
								} else {
									fees[counter].setText("");
								}
								chapterlayout[counter].setVisibility(View.VISIBLE);
								counter++;
							}
						}
						
					      /*需要统计 前面的页面中，一共有多少个“显示”的 卷;算法：上一页的该值+本页该值；
				        是否“显示”，两种判断逻辑 1：判断前面的页中是否已经显示过  2：chapter中是否包含index为0的；这里使用逻辑2
				        修改：逻辑2有错误，因为取回的数据并不一定都会被显示出来！
				        可以考虑把本逻辑放到show出本页数据的地方
				       */
						int countVolShowThispage = counterVol;
					      //保存起来本页的之前所有页（包括本页）总“显示”卷的数目
				        if(!volumnNumforPages.containsKey(""+currentPage))
				        {
				            int countShowPrevpages = 0;
				            if(currentPage>1)
				            {
				                countShowPrevpages = Integer.parseInt(volumnNumforPages.get(""+(currentPage-1)))+countVolShowThispage;
				            }else{
				                countShowPrevpages = countVolShowThispage;
				            }
				            volumnNumforPages.put(""+currentPage, ""+countShowPrevpages);
				            //Logger.d(TAG,currentPage+"->"+countShowPrevpages);
				        }
					}
					else {
						chapterlayout[7].setVisibility(View.VISIBLE);
						chapterlayout[6].setVisibility(View.VISIBLE);
						recentblock.setVisibility(View.GONE);
						int total = 0;
						int counterVol = 0;//显示卷 计数
						for (int i = 0; i < bookchapter.size(); i++) {
							if (total >= itemPerPage) {
								break;
							}
							String volname = bookchapter.get(i).get("volumnName")
							.toString();
							if(bookchapter.get(i).containsKey("Index0")&&bookchapter.get(i).get("Index0").equals("1"))
							{
								chapters[total].setVisibility(View.VISIBLE);
								chapters[total].setText(volname);
								total ++;
								counterVol ++;
							}
							int len = Integer.parseInt(bookchapter.get(i).get(
							"VolumeLen").toString());
							for (int j = 0; j < len; j++) {
								if (total >= itemPerPage) {
									break;
								}
								chapters[total].setVisibility(View.VISIBLE);
								fees[total].setVisibility(View.VISIBLE);
								chapters[total].setText(bookchapter.get(i).get("ChapterName" + j)
										.toString());
								if (bookchapter.get(i).get("ChargeType" + j).toString()
										.equals("0")) {
									fees[total].setText("免费");
								} else {
									fees[total].setText("");
								}

								total++;
							}

						}
						
                        /*需要统计 前面的页面中，一共有多少个“显示”的 卷;算法：上一页的该值+本页该值；
                        是否“显示”，两种判断逻辑 1：判断前面的页中是否已经显示过  2：chapter中是否包含index为0的；这里使用逻辑2
                        修改：逻辑2有错误，因为取回的数据并不一定都会被显示出来！
                        可以考虑把本逻辑放到show出本页数据的地方
                       */
                        int countVolShowThispage = counterVol;
                          //保存起来本页的之前所有页（包括本页）总“显示”卷的数目
                        if(!volumnNumforPages.containsKey(""+currentPage))
                        {
                            int countShowPrevpages = 0;
                            if(currentPage>1)
                            {
                                countShowPrevpages = Integer.parseInt(volumnNumforPages.get(""+(currentPage-1)))+countVolShowThispage;
                            }else{
                                countShowPrevpages = countVolShowThispage;
                            }
                            volumnNumforPages.put(""+currentPage, ""+countShowPrevpages);
                            //Logger.d(TAG,currentPage+"->"+countShowPrevpages);
                        }
					}

					GlobalVar app = (GlobalVar)getApplicationContext();
					if (totalPage < 1) {
	                    hidePager();
	                }else{
	                    showPager();
	                    updatePagerinfo(String.valueOf(currentPage)+" / "+String.valueOf(totalPage));   
	                }									
					
					onGoing = false;
					showMe(BookCatalogActivity.class);

				}	
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

		};


		/*
		 * 置空所有UI行
		 */
		private void emptyLines(){
			for(int i=0;i<8;i++){
				chapters[i].setText("");
				fees[i].setText("");
				chapters[i].setVisibility(View.INVISIBLE);
				fees[i].setVisibility(View.INVISIBLE);
			}
		}

        @Override
        public void OnNextpage() {
            try
            {   
                if(onGoing){
                    return;
                }
                if(currentPage >= totalPage){
                    return ;
                }
                currentPage++;
                setUIData();

            }
            catch(Exception e)
            {
                Logger.e(TAG, "next page: " + e.toString());
            }
            super.OnNextpage();
        }

        @Override
        public void OnPrevpage() {
            try
            {   
                if(onGoing){
                    return;
                }

                if(currentPage == 1){
                    return ;
                }
                currentPage--;
                setUIData();

            }
            catch(Exception e)
            {
                Logger.e(TAG, "pre page: " + e.toString());
            }
            super.OnPrevpage();
        }
}