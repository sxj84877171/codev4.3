package com.pvi.ap.reader.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.listener.MainPageUiItemClickListener;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Book;
import com.pvi.ap.reader.data.common.CommonDateUtils;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.Bookmark;

//class description place here
//author:kyle
public class MainpageInsideActivity extends PviActivity {

	private int recommendnum = 3;
	private int recordsLineSize = 5;
	Book[] recommend;
	String TAG = "MyViewTest";
	String[] linespace;
	String[] fontsize;
	String[] certpath;
	String[] userid;
	String[] bookmarkid;
	String[] chapterid;
	String[] chaptername;
	String[] readtime;
	String[] filepath;
	String[] contentid;
	String[] bookname;
	String[] readposition;
	String[] readSourceType;
	String[] booktype;
	String[] downloadType;
	DefView myview=null;
	recommend_main recommendview = null;
	recent_main recentview  = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.test);
		myview = (DefView) this.findViewById(R.id.myview);
		myview.requestFocus();
		myview.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keycode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == KeyEvent.ACTION_DOWN)
				{
					int focus_idx = myview.getFocusIndex();
					if(keycode == KeyEvent.KEYCODE_DPAD_CENTER)
					{
						v.performClick();
						return true;
					}
					else if(keycode == KeyEvent.KEYCODE_DPAD_LEFT)
					{
						if(focus_idx > 0)
						{
							focus_idx--;
						}
						else
						{
							focus_idx = 0;
						}
						myview.setFocusIndex(focus_idx);
						myview.refresh();
						return true;
					}
					else if(keycode == KeyEvent.KEYCODE_DPAD_RIGHT)
					{
						if(focus_idx < myview.getItemNum()-1)
						{
							focus_idx++;
						}
						else
						{
							focus_idx = myview.getItemNum()-1;
						}
						myview.setFocusIndex(focus_idx);
						myview.refresh();
						return true;
					}
					else if(keycode == KeyEvent.KEYCODE_DPAD_UP)
					{
						return true;
					}
					else if(keycode == KeyEvent.KEYCODE_DPAD_DOWN)
					{
						if(focus_idx==0||focus_idx==1||focus_idx==2)
						{
							recommendview.setFocusIndex(0);
							recommendview.requestFocus();
						}
						else
						{
							recentview.setFocusIndex(0);
							recentview.requestFocus();
						}
						return true;
					}
				}
				return false;
			}
		});
		myview.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.requestFocus();
				v.invalidate();
				switch(myview.getFocusIndex())
				{
				case 1:
					Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle sndBundle = new Bundle();
					sndBundle.putString("actID", "ACT19000");
					sndBundle.putString("pviapfStatusTip", "进入无线书城...");
/*					sndBundle.putString("pviapfStatusTipTime",
					"20000");*/
					intent.putExtras(sndBundle);
					sendBroadcast(intent);
					intent = null;
					sndBundle = null;
					break;
				case 0:
					if(bookname==null)
					{
						gotoWirelessStore();
					}
					else
					{
						if ("4".equals(readSourceType[0])) {
							//							activity.showMessage("进入书籍阅读...","20000");
							Intent intent1 = new Intent(
									MainpageActivity.START_ACTIVITY);
							Bundle sndBundle1 = new Bundle();
							sndBundle1
							.putString("act",
							"com.pvi.ap.reader.activity.ReadingOnlineActivity");
							sndBundle1.putString("haveTitleBar", "1");
							/*sndBundle1.putString("startType", "allwaysCreate"); // 每次都create一个新实例，不设置此参数时，默认为“复用”已有的
							*/sndBundle1.putString("Position", readposition[0]);
							sndBundle1.putString("ChapterID", chapterid[0]);
							sndBundle1.putString("ChapterName", chaptername[0]);
							sndBundle1.putString("ContentID", contentid[0]);
							sndBundle1.putString("FontSize", fontsize[0]);
							sndBundle1.putString("pviapfStatusTip", "进入书籍阅读...");
/*							sndBundle1.putString("pviapfStatusTipTime",
							"20000");*/
							intent1.putExtras(sndBundle1);

							sendBroadcast(intent1);
						} else {
							goToRecentRead(0);
						}
					}
					break;
				case 2:
					long TimeStart = System.currentTimeMillis();
					Logger.i("Time", "MyBookshelf Pressed:"
							+ Long.toString(TimeStart));
					Intent tmpIntent = new Intent(
							MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("actID", "ACT12000");
					bundleToSend.putString("pviapfStatusTip", "进入我的书架...");
/*					bundleToSend.putString("pviapfStatusTipTime",
					"20000");*/
/*					bundleToSend.putString("startType", "allwaysCreate");*/
					tmpIntent.putExtras(bundleToSend);
					sendBroadcast(tmpIntent);

					break;
				case 3:
					//					showMessage("进入本地书库...", "15000");
					v.requestFocus();
					Intent tmpIntent1 = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend1 = new Bundle();
					bundleToSend1.putString("actID", "ACT13500");
					tmpIntent1.putExtras(bundleToSend1);
					sendBroadcast(tmpIntent1);

					break;
				case 4:
					Intent tmpIntent2 = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend2 = new Bundle();
					bundleToSend2.putString("actID", "ACT13000");
					bundleToSend2.putString("actTabIndex", "0");
					tmpIntent2.putExtras(bundleToSend2);
					sendBroadcast(tmpIntent2);
					break;
				case 5:
					Intent tmpIntent3 = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend3 = new Bundle();
					bundleToSend3.putString("actID", "ACT14100");
					bundleToSend3.putString("pviapfStatusTip", "进入个人空间...");
/*					bundleToSend3.putString("pviapfStatusTipTime",
					"20000");*/
					tmpIntent3.putExtras(bundleToSend3);
					sendBroadcast(tmpIntent3);
					break;
				}
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		recommendview = (recommend_main) this.findViewById(R.id.recommendview);
		recommend = new Book[this.recommendnum];
		for(int i = 0; i < this.recommendnum; i ++)
		{
			recommend[i] = new Book();
		}
		getRecommendBook(recommend);

		ArrayList<HashMap<String, String>> bookinfo = new ArrayList<HashMap<String, String>> ();
		HashMap<String, String> map = null;
		for(int i = 0; i < this.recommendnum; i ++)
		{
			map = new HashMap<String, String>();
			map.put("Author", recommend[i].author);
			map.put("Name", recommend[i].name);
			map.put("Url", recommend[i].url);
			map.put("Detail", recommend[i].details);
			bookinfo.add(map);
		}
		recommendview.setRecommendBook(bookinfo);
		recommendview.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent tmpIntent = new Intent(
						MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				switch(recommendview.getFocusIndex())
				{
				case 0:
				case 1:
					bundleToSend.putString("act", "com.pvi.ap.reader.activity.WirelessStoreActivity");
					bundleToSend.putString("actTabName", "编辑推荐");
					bundleToSend.putString("pviapfStatusTip", "进入编辑推荐...");
					bundleToSend.putString("pviapfStatusTipTime",
					"20000");
					break;
				case 2:
					bundleToSend.putString("act",
					"com.pvi.ap.reader.activity.BookSummaryActivity");
					bundleToSend.putString("haveTitleBar", "1");
					bundleToSend.putString("startType", "allwaysCreate");
					bundleToSend.putString("contentID", recommend[0].id);
					bundleToSend.putString("pviapfStatusTip", "进入书籍摘要...");
					bundleToSend.putString("pviapfStatusTipTime",
					"20000");

					break;
				case 3:
					bundleToSend.putString("act",
					"com.pvi.ap.reader.activity.BookSummaryActivity");
					bundleToSend.putString("haveTitleBar", "1");
					bundleToSend.putString("startType", "allwaysCreate");
					bundleToSend.putString("contentID", recommend[1].id);
					bundleToSend.putString("pviapfStatusTip", "进入书籍摘要...");
					bundleToSend.putString("pviapfStatusTipTime",
					"20000");
					break;
				case 4:
					bundleToSend.putString("act",
					"com.pvi.ap.reader.activity.BookSummaryActivity");
					bundleToSend.putString("haveTitleBar", "1");
					bundleToSend.putString("startType", "allwaysCreate");
					bundleToSend.putString("contentID", recommend[2].id);
					bundleToSend.putString("pviapfStatusTip", "进入书籍摘要...");
					bundleToSend.putString("pviapfStatusTipTime",
					"20000");
					break;
				}
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
			}
		});
		recommendview.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keycode, KeyEvent event) {
				// TODO Auto-generated method stub
				int focus_idx = recommendview.getFocusIndex();
				if(event.getAction() == KeyEvent.ACTION_DOWN)
				{
					if(keycode == KeyEvent.KEYCODE_DPAD_CENTER)
					{
						recommendview.setIsClick(true);
						v.performClick();
						return true;
					}
					else if(keycode == KeyEvent.KEYCODE_DPAD_LEFT)
					{
						if(focus_idx == recommend_main.RECOMMEND_MORE)
						{
							recommendview.setFocusIndex(recommend_main.RECOMMEND_TITLE);
							recommendview.refresh();
						}
						return true;
					}
					else if(keycode == KeyEvent.KEYCODE_DPAD_RIGHT)
					{
						if(focus_idx == recommend_main.RECOMMEND_TITLE)
						{
							recommendview.setFocusIndex(recommend_main.RECOMMEND_MORE);
							recommendview.refresh();
							return true;
						}
						else
						{
							if(focus_idx == recommend_main.RECOMMEND_BOOK1)
							{
								recentview.setFocusIndex(recent_main.RECENT_RECORD1);
							}
							else if(focus_idx == recommend_main.RECOMMEND_BOOK2)
							{
								recentview.setFocusIndex(recent_main.RECENT_RECORD3);
							}
							else if(focus_idx == recommend_main.RECOMMEND_BOOK3)
							{
								recentview.setFocusIndex(recent_main.RECENT_RECORD5);
							}
							recentview.requestFocus();
							return true;
						}
				
					}
					else if(keycode == KeyEvent.KEYCODE_DPAD_DOWN)
					{
						if(focus_idx == recommend_main.RECOMMEND_TITLE || focus_idx == recommend_main.RECOMMEND_MORE)
						{
							focus_idx = recommend_main.RECOMMEND_BOOK1;
						}
						else if(focus_idx < recommendview.getItemNum()+1)
						{
							focus_idx ++;
						}
						else
						{
							return false;
						}
						recommendview.setFocusIndex(focus_idx);
						recommendview.refresh();
						return true;
					}
					else if(keycode == KeyEvent.KEYCODE_DPAD_UP)
					{
						if(focus_idx > 2)
						{
							focus_idx --;
						}
						else if(focus_idx == 2)
						{
							focus_idx = recommend_main.RECOMMEND_TITLE;
						}
						else
						{
							myview.setFocusIndex(DefView.CONTINUEREAD);
							myview.requestFocus();
							return true;
						}
						recommendview.setFocusIndex(focus_idx);
						recommendview.refresh();
						return true;
					}
					return false;
				}

				return false;
			}
		});
		recentview = (recent_main) this.findViewById(R.id.recentview);
		getRecentRecord();
		bookinfo = new ArrayList<HashMap<String, String>> ();
		map = null;
		for(int i = 0; i < this.recordsLineSize; i ++)
		{
			map = new HashMap<String, String>();
			if(bookname[i]!=null)
			{
				map.put("Name", bookname[i]);
				if(chaptername[i]==null)
				{
					map.put("Chapter", "");
				}
				else
				{
					map.put("Chapter", chaptername[i]);
				}	
				if(readtime[i]==null)
				{
					map.put("ReadTime", "");
				}
				else
				{
					map.put("ReadTime", CommonDateUtils.getMonthHourDate(readtime[i]));
				}
				if(map!=null&&map.size()>0)
				{
					bookinfo.add(map);
				}
			}

		}
		recentview.setRecentBook(bookinfo);
		recentview.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				int index = 0;
				switch(recentview.getFocusIndex())
				{
				case 0:
				case 1:
					Intent tmpIntent = new Intent(
							MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("pviapfStatusTip", "进入最近阅读...");
					bundleToSend.putString("pviapfStatusTipTime",
					"20000");
					bundleToSend.putString("startType",  "allwaysCreate"); 
					bundleToSend.putString("actID", "ACT12000");
					tmpIntent.putExtras(bundleToSend);
					sendBroadcast(tmpIntent);
					return;
				case 2:
					index = 0;
					break;
				case 3:
					index = 1;
					break;
				case 4:
					index = 2;
					break;
				case 5:
					index = 3;
					break;
				case 6:
					index = 4;
					break;
				case 7:
					index = 5;
					break;
				}
				if(index!=5)
				{
					if ("4".equals(readSourceType[index])) {
						//							activity.showMessage("进入书籍阅读...","20000");
						Intent intent = new Intent(
								MainpageActivity.START_ACTIVITY);
						Bundle sndBundle = new Bundle();
						sndBundle
						.putString("act",
						"com.pvi.ap.reader.activity.ReadingOnlineActivity");
						sndBundle.putString("haveTitleBar", "1");
						sndBundle.putString("startType", "allwaysCreate"); // 每次都create一个新实例，不设置此参数时，默认为“复用”已有的
						sndBundle.putString("Position", readposition[index]);
						sndBundle.putString("ChapterID", chapterid[index]);
						sndBundle.putString("ChapterName", chaptername[index]);
						sndBundle.putString("ContentID", contentid[index]);
						sndBundle.putString("FontSize", fontsize[index]);
						sndBundle.putString("pviapfStatusTip", "进入书籍阅读...");
						sndBundle.putString("pviapfStatusTipTime",
						"20000");
						intent.putExtras(sndBundle);

						sendBroadcast(intent);
					} else {
						goToRecentRead(index);
					}
				}
				else
				{
					gotoWirelessStore();
				}
			}
		});
		recentview.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keycode, KeyEvent event) {
				// TODO Auto-generated method stub
				int focus_idx = recentview.getFocusIndex();
				if(event.getAction() == KeyEvent.ACTION_DOWN)
				{
					if(keycode == KeyEvent.KEYCODE_DPAD_CENTER)
					{
						v.performClick();
						return true;
					}
					else if(keycode == KeyEvent.KEYCODE_DPAD_LEFT)
					{
						if(focus_idx == recent_main.RECENT_MORE)
						{
							recentview.setFocusIndex(recent_main.RECENT_TITLE);
							recentview.refresh();
							return true;
						}
						else 
						{
//							return false;
							if(focus_idx == recent_main.RECENT_TITLE)
							{
								recommendview.setFocusIndex(recommend_main.RECOMMEND_MORE);
							}
							else if(focus_idx == recent_main.RECENT_RECORD1)
							{
								recommendview.setFocusIndex(recommend_main.RECOMMEND_BOOK1);
							}
							else if(focus_idx == recent_main.RECENT_NORECORD || focus_idx == recent_main.RECENT_RECORD2 || focus_idx == recent_main.RECENT_RECORD3)
							{
								recommendview.setFocusIndex(recommend_main.RECOMMEND_BOOK2);
							}
							else if(focus_idx == recent_main.RECENT_RECORD4 || focus_idx == recent_main.RECENT_RECORD5)
							{
								recommendview.setFocusIndex(recommend_main.RECOMMEND_BOOK3);
							}
							recommendview.requestFocus();
							return true;
						}
						
					}
					else if(keycode == KeyEvent.KEYCODE_DPAD_RIGHT)
					{
						if(focus_idx == 0)
						{
							recentview.setFocusIndex(recent_main.RECENT_MORE);
							recentview.refresh();
						}
						return true;
					}
					else if(keycode == KeyEvent.KEYCODE_DPAD_DOWN)
					{
						if(focus_idx == recent_main.RECENT_MORE || focus_idx == recent_main.RECENT_TITLE)
						{
							if(recentview.getItemNum()>=1)
							{
								focus_idx = recent_main.RECENT_RECORD1;
							}
							else
							{
								focus_idx = recent_main.RECENT_NORECORD;
							}
						}
						else if(focus_idx < recentview.getItemNum()+1)
						{
							focus_idx ++;
						}
						else
						{
							return false;
						}
						recentview.setFocusIndex(focus_idx);
						recentview.refresh();
						return true;
					}
					else if(keycode == KeyEvent.KEYCODE_DPAD_UP)
					{
						if(focus_idx > 2 && focus_idx <= recentview.getItemNum()+1)
						{
							focus_idx --;
						}
						else if(focus_idx == 2 || (focus_idx == 7))
						{
							focus_idx = 0;
						}
						else if(focus_idx == 1 || (focus_idx == 0))
						{
							myview.setFocusIndex(DefView.LOCALBOOK);
							myview.requestFocus();
							return true;
						}
						recentview.setFocusIndex(focus_idx);
						recentview.refresh();
						return true;
					}
					return false;
				}
				return false;
			}
		});
	}
	public void gotoWirelessStore() {

		Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("act",
		"com.pvi.ap.reader.activity.WirelessStoreMainpageActivity");
		bundleToSend.putString("haveTitleBar", "1");
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);

	}
	private void goToRecentRead(int i) {

		String FilePath = filepath[i];
		if(FilePath!=null)
		{
			Logger.e(TAG, FilePath);
		}
		else
		{
			Logger.e(TAG, "filepath is null");
			return;
		}
		if (!new File(FilePath).exists()) {
			final PviAlertDialog pd = new PviAlertDialog(this);
			pd.setTitle("温馨提示");
			pd.setMessage("选择书籍不存在！");
			pd.setTimeout(10000); // 可选参数 延时5000ms后自动关闭
			pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					pd.dismiss();
				}
			});
			pd.show();
			return;
		}
		HashMap<String, Object> data = new HashMap<String, Object>();;
		String Contentid = contentid[i];
		String ChapterId = chapterid[i];
		String Offset = readposition[i];
		String UserID = userid[i];
		String CertPath = certpath[i];
		String LineSpace = linespace[i];
		String FontSize = fontsize[i];
		//		String BookType = booktype[i];
		String DownloadType = downloadType[i];
		
		data.put("FilePath", FilePath);
		data.put("ChapterID", ChapterId);
		data.put("Offset", Offset);
		// data.put("UserID", UserID);
		data.put("FromPath", "1");
		data.put("CertPath", CertPath);
		data.put("ContentID", Contentid);
		data.put("LineSpace", LineSpace);
		data.put("FontSize", FontSize);
		data.put("downloadType", DownloadType);

		// add by kizan to show tips for open the document 2011.01.19

		Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
		Bundle sndbundle = new Bundle();
		//		sndbundle.putString("pviapfStatusTip", activity.getResources()
		//				.getString(R.string.kyleHint01));
		msgIntent.putExtras(sndbundle);
		sendBroadcast(msgIntent);

		OpenReader.gotoReader(this, data);

	}
	private void getRecentRecord()
	{
		linespace = new String[recordsLineSize];
		fontsize = new String[recordsLineSize];
		certpath = new String[recordsLineSize];
		userid = new String[this.recordsLineSize];
		bookmarkid = new String[recordsLineSize];
		chapterid = new String[recordsLineSize];
		chaptername = new String[recordsLineSize];
		readtime = new String[recordsLineSize];
		filepath = new String[recordsLineSize];
		contentid = new String[recordsLineSize];
		bookname = new String[recordsLineSize];
		readposition = new String[recordsLineSize];
		readSourceType = new String[recordsLineSize];
		downloadType = new String[recordsLineSize];

		String columns[] = new String[] { Bookmark.UserID, Bookmark.BookmarkId,
				Bookmark.ContentId, Bookmark.ChapterId, Bookmark.ChapterName,
				Bookmark.ContentName, Bookmark.CertPath, Bookmark.FilePath,
				Bookmark.LineSpace, Bookmark.FontSize, Bookmark.CreatedDate,
				Bookmark.Position, Bookmark.SourceType ,Bookmark.DownloadType};
		Cursor cur = null;
		String where = Bookmark.BookmarkType + "='0'";
		String order = Bookmark.CreatedDate + " DESC ";

		cur = managedQuery(Bookmark.CONTENT_URI, columns, where, null, order);
		if(cur==null)
		{
			return;
		}
		int tmp = cur.getCount();

		// 
		if (tmp >= recordsLineSize) {
			tmp = recordsLineSize;
		}

		if (cur.moveToFirst()) {
			this.bookname=new String[recordsLineSize];

			for (int tmpi = 0; tmpi < tmp; tmpi++) {

				final int i = tmpi;
				userid[i] = cur.getString(cur.getColumnIndex(Bookmark.UserID));
				bookmarkid[i] = cur.getString(cur
						.getColumnIndex(Bookmark.BookmarkId));
				chapterid[i] = cur.getString(cur
						.getColumnIndex(Bookmark.ChapterId));
				chaptername[i] = cur.getString(cur
						.getColumnIndex(Bookmark.ChapterName));
				contentid[i] = cur.getString(cur
						.getColumnIndex(Bookmark.ContentId));
				bookname[i] = cur.getString(cur
						.getColumnIndex(Bookmark.ContentName));
				readtime[i] = cur.getString(cur
						.getColumnIndex(Bookmark.CreatedDate));
				readposition[i] = cur.getString(cur
						.getColumnIndex(Bookmark.Position));
				filepath[i] = cur.getString(cur
						.getColumnIndex(Bookmark.FilePath));
				certpath[i] = cur.getString(cur
						.getColumnIndex(Bookmark.CertPath));
				linespace[i] = cur.getString(cur
						.getColumnIndex(Bookmark.LineSpace));
				fontsize[i] = cur.getString(cur
						.getColumnIndex(Bookmark.FontSize));

				readSourceType[i] = cur.getString(cur
						.getColumnIndex(Bookmark.SourceType));
				downloadType[i] = cur.getString(cur
                        .getColumnIndex(Bookmark.DownloadType));

				if (!cur.moveToNext()) {

					break;
				}
			}
		}
		if(cur!=null)
		{
			cur.close();
		}
	}

	private void getRecommendBook(Book[] book) {

		book[0].id = "346548972";
		book[0].name = "廿岁跟对人:卅岁做对事";
		book[0].details = "一本可以随时随地拿出来翻阅的女人心灵成长经典，继《7天女学馆》火热畅销之后，苏芩又一部新女学力作，书中以冷静";
		book[0].author = "苏芩";
		book[0].url = "";

		book[1].id = "346597461";
		book[1].name = "缅怀逝去者:唐山大地震";
		book[1].details = " 纪念唐山大地震,即是缅怀灾难中逝去的同胞,更是鼓励生者的我们。这是我们共有的家园,地震可以破坏它,但摧毁不了我们万众一心众志成城的凝聚力";
		book[1].author = "关仁山,王家惠";
		book[1].url = "";

		if(this.recommendnum >2)
		{
			book[2].id = "346599757";
			book[2].name = "单纯:鲸鱼女孩池塘男孩";
			book[2].details = "这一刻她的眼神，对我而言就是永恒。一个是聪明大方、总是有莫名预感的大眼美女；一个是体贴诚实、偶尔讲冷笑话的腼腆男孩；二人在一次校园十大美女选拔赛中结识。";
			book[2].author = "蔡智恒";
			book[2].url = "";
		}

		FileInputStream fs = null;
		ObjectInputStream os = null;
		for (int i = 0; i < this.recommendnum; i++) {
			String fileName = "/data/data/com.pvi.ap.reader/book" + i + ".dat";
			File file = new File(fileName);
			if (!file.exists()) {
				continue;
			}
			try {
				fs = new FileInputStream(file);
				os = new ObjectInputStream(fs);
				Book btmp = (Book) os.readObject();
				for(Book tp :book){
					if((btmp.id).equals((tp.id))){
						continue ;
					}
				}
				book[i] = btmp;
			} catch (FileNotFoundException e) {
			} catch (Exception e) {
			} finally {
				try {
					if (fs != null) {
						fs.close();
						fs = null;
					}
					if (os != null) {
						os.close();
						os = null;
					}
				} catch (IOException e) {
				}
			}
		}

	}

    @Override
    public OnUiItemClickListener getMenuclick() {
        // TODO Auto-generated method stub
        return new MainPageUiItemClickListener(this);
    }
}
