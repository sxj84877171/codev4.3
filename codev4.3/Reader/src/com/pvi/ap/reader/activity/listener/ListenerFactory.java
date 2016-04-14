package com.pvi.ap.reader.activity.listener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.MainpageActivity;
import com.pvi.ap.reader.activity.WirelessRankActivity;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.data.common.Logger;

/***
 * the factory to control the whole intent broadcast while click
 * 
 * @author rd045
 * 
 */
public class ListenerFactory {

	// if we need to store the global click instance we can use singleton
	// pattern

	// private static ListenerFactory _instance = new ListenerFactory();

	// private ListenerFactory() {

	// }

	// public static ListenerFactory getInstance() {
	// return _instance;
	// }

	// go to wireless store
	public static View.OnClickListener getWirelessStoreClickListener(
			final PviActivity act) {
		return new OnClickListener() {
			public void onClick(final View v) {

				//				act.showMessage("进入无线书城...", "20000");

				v.requestFocus();
				//				v.setEnabled(false);
				Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle sndBundle = new Bundle();
				sndBundle.putString("actID", "ACT19000");
				sndBundle.putString("pviapfStatusTip", "进入无线书城...");
				sndBundle.putString("pviapfStatusTipTime",
				"20000");
				intent.putExtras(sndBundle);
				act.sendBroadcast(intent);
				intent = null;
				sndBundle = null;
			}
		};
	}

	public static View.OnTouchListener getWirelessStoreTouchListener(
			final PviActivity act) {
		return new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
			
				if(event.getAction()==MotionEvent.ACTION_UP)
				{
					v.requestFocus();
					//					act.showMessage("进入无线书城...", "15000");
					Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle sndBundle = new Bundle();
					sndBundle.putString("actID", "ACT19000");
//					sndBundle.putString("pviapfStatusTip", "进入无线书城...");
//					sndBundle.putString("pviapfStatusTipTime",
//					"20000");
					intent.putExtras(sndBundle);
					act.sendBroadcast(intent);
					intent = null;
					sndBundle = null;
				}
				return true;
			}
		};
	}

	/**
	 * go to mybookshelf
	 * 
	 * @param act
	 * @return
	 */
	public static View.OnClickListener getMyBookShelfClickListener(
			final PviActivity act) {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.requestFocus();
				try {
					//					act.showMessage("进入我的书架...", "15000");

					long TimeStart = System.currentTimeMillis();
					Log.i("Time", "MyBookshelf Pressed:"
							+ Long.toString(TimeStart));
					Intent tmpIntent = new Intent(
							MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("actID", "ACT12000");
//					bundleToSend.putString("pviapfStatusTip", "进入我的书架...");
//					bundleToSend.putString("pviapfStatusTipTime",
//					"20000");
					bundleToSend.putString("startType", "allwaysCreate");
					tmpIntent.putExtras(bundleToSend);
					act.sendBroadcast(tmpIntent);
				} catch (Exception e) {
					Log.e("Menu", e.toString());
				}
			}
		};
	}
	public static View.OnTouchListener getMyBookShelfTouchListener(
			final PviActivity act) {
		return new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent arg1) {
				// TODO Auto-generated method stub
				v.requestFocus();
				if(arg1.getAction()== MotionEvent.ACTION_UP)
				{
					try {
						act.showMessage("进入我的书架...", "15000");
						long TimeStart = System.currentTimeMillis();
						Log.i("Time", "MyBookshelf Pressed:"
								+ Long.toString(TimeStart));
						Intent tmpIntent = new Intent(
								MainpageActivity.START_ACTIVITY);
						Bundle bundleToSend = new Bundle();
						bundleToSend.putString("actID", "ACT12000");
						bundleToSend.putString("starttype", "ACT12000");
//						bundleToSend.putString("pviapfStatusTip", "进入我的书架...");
//						bundleToSend.putString("pviapfStatusTipTime",
//						"20000");
						bundleToSend.putString("startType", "allwaysCreate");
						tmpIntent.putExtras(bundleToSend);
						act.sendBroadcast(tmpIntent);

					} catch (Exception e) {
						Log.e("Menu", e.toString());
					}
					return true;
				}
				return false;
			}
		};
	}

	public static View.OnClickListener getMessageCenterClickListener(
			final PviActivity act) {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.requestFocus();

				//				act.showMessage("进入消息中心...", "15000");
				try {
					long TimeStart = System.currentTimeMillis();
					Log.i("Time", "MessageCenter Pressed:"
							+ Long.toString(TimeStart));
					Intent tmpIntent = new Intent(
							MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("actID", "ACT14600");
					bundleToSend.putString("pviapfStatusTip", "进入消息中心...");
					bundleToSend.putString("pviapfStatusTipTime",
					"20000");
					bundleToSend.putString("startType", "allwaysCreate");
					tmpIntent.putExtras(bundleToSend);
					act.sendBroadcast(tmpIntent);
				} catch (Exception e) {
					Log.e("Menu", e.toString());
				}
			}
		};
	}

	public static View.OnClickListener getLocalBookClickListener(
			final PviActivity act) {
		return new OnClickListener() {
			public void onClick(final View v) {

				act.showMessage("进入本地书库...", "15000");
				v.requestFocus();
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID", "ACT13500");
				tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);
			}
		};

	}
	public static View.OnTouchListener getLocalBookTouchListener(
			final PviActivity act) {
		return new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent arg1) {
				// TODO Auto-generated method stub
				v.requestFocus();
				if(arg1.getAction()== MotionEvent.ACTION_UP)
				{
					act.showMessage("进入本地书库...", "15000");

					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("actID", "ACT13500");
					tmpIntent.putExtras(bundleToSend);
					act.sendBroadcast(tmpIntent);
				}
				return true;
			}
		};

	}

	public static View.OnClickListener getResourceCenterClickListener(
			final PviActivity act) {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {

				act.showMessage("进入资源中心...", "15000");
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID", "ACT13000");
				bundleToSend.putString("actTabIndex", "0");
				bundleToSend.putString("startType",  "allwaysCreate"); 
				tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);
			}
		};
	}
	public static View.OnTouchListener getResourceCenterTouchListener(
			final PviActivity act) {
		return new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent arg1) {
				// TODO Auto-generated method stub
				v.requestFocus();
				if(arg1.getAction()== MotionEvent.ACTION_UP)
				{
					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("actID", "ACT13000");
					bundleToSend.putString("actTabIndex", "0");
					bundleToSend.putString("pviapfStatusTip", "进入资源中心...");
					bundleToSend.putString("pviapfStatusTipTime",
					"20000");
					bundleToSend.putString("startType",  "allwaysCreate"); 
					tmpIntent.putExtras(bundleToSend);
					act.sendBroadcast(tmpIntent);
				}
				return true;
			}
		};
	}

	public static View.OnClickListener getMyAnnotationClickListner(
			final PviActivity act) {
		return new OnClickListener() {
			public void onClick(final View v) {

				act.showMessage("进入我的批注...", "15000");
				Bundle bundleToSend = new Bundle();
				Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
				bundleToSend.putString("act",  "com.pvi.ap.reader.activity.ResCenterActivity");
				bundleToSend.putString("haveTitleBar","1");
				bundleToSend.putString("startType",  "allwaysCreate"); 
				bundleToSend.putString("actTabName",  "我的批注");     
				intent.putExtras(bundleToSend);
				act.sendBroadcast(intent);

			}
		};
	}

	public static View.OnClickListener getPersonalClickListener(
			final PviActivity act) {
		return new OnClickListener() {
			public void onClick(final View v) {

				//act.showMessage("进入个人空间，请稍候...", "10000");
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID", "ACT14100");
				bundleToSend.putString("pviapfStatusTip", "进入个人空间...");
				bundleToSend.putString("pviapfStatusTipTime",
				"20000");
				tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);
			}
		};
	}
	public static View.OnTouchListener getPersonalTouchListener(
			final PviActivity act) {
		return new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				arg0.requestFocus();
				if(arg1.getAction()== MotionEvent.ACTION_UP)
				{
					//					act.showMessage("进入个人空间...", "15000");

					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("actID", "ACT14100");
					bundleToSend.putString("pviapfStatusTip", "进入个人空间...");
					bundleToSend.putString("pviapfStatusTipTime",
					"20000");
					tmpIntent.putExtras(bundleToSend);
					act.sendBroadcast(tmpIntent);
				}
				return true;
			}
		};
	}

	public static View.OnClickListener getFp_SettingClickListener(
			final PviActivity act) {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// SystemConfigActivity
				// startActivity(new Intent(MainpageInsideActivity.this,
				// SystemConfigActivity.class));

				act.showMessage("进入系统设置...", "15000");
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("act",
				"com.pvi.ap.reader.activity.SystemConfigActivity");
				bundleToSend.putString("pviapfStatusTip", act.getResources()
						.getString(R.string.goto_setting));
				// bundleToSend.putString("haveTitleBar", "1");
				// bundleToSend.putString("pviapfStatusTipTime", "2000");
				tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);
			}
		};
	}

	public static View.OnClickListener getFp_MusicClickListener(
			final PviActivity act) {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				act.showMessage("进入我的音乐...", "15000");
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				// bundleToSend.putString("act",
				// "com.pvi.ap.reader.activity.ResCenterActivity");
				// bundleToSend.putString("pviapfStatusTip", "数据加载中，请稍候...");
				// bundleToSend.putString("startType", "allwaysCreate");
				// bundleToSend.putString("tab", "MyMusic");
				// bundleToSend.putString("haveTitleBar", "1");
				bundleToSend.putString("actID", "ACT13200");
				tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);
			}
		};
	}

	public static View.OnClickListener getFp_ApplicationClickListener(
			final PviActivity act) {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 显示所有程序

				act.showMessage("进入所有应用...", "15000");
				// 通知框架去启动Activity
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("act",
				"com.pvi.ap.reader.activity.AllAppActivity");
				//				bundleToSend.putString("pviapfStatusTip", "数据加载中，请稍候...");
				bundleToSend.putString("haveTitleBar", "1");
				tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);

			}
		};
	}

	/**
	 * go to the recent read book
	 * 
	 * @param act
	 *            the act put in to send the message
	 * @return the click Listener to go to the recent read book
	 */
	public static View.OnClickListener getGoRecentReadBookListener(
			final PviActivity act) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//		
				//				act.showMessage("进入最近阅读...", "15000");
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();

				bundleToSend.putString("pviapfStatusTip", "进入最近阅读...");
				bundleToSend.putString("pviapfStatusTipTime",
				"20000");
				bundleToSend.putString("startType",  "allwaysCreate"); 
				bundleToSend.putString("actID", "ACT12000");
				tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);

			}
		};
	}
	public static View.OnTouchListener getGoRecentReadBookTouchListener(
			final PviActivity act) {
		return new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent arg1) {
				// TODO Auto-generated method stub
				v.requestFocus();
				if(arg1.getAction()== MotionEvent.ACTION_UP)
				{
					//					act.showMessage("进入最近阅读...", "15000");
					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("pviapfStatusTip", "进入最近阅读...");
					bundleToSend.putString("pviapfStatusTipTime",
					"20000");
					bundleToSend.putString("startType",  "allwaysCreate"); 
					bundleToSend.putString("actID", "ACT12000");
					tmpIntent.putExtras(bundleToSend);
					act.sendBroadcast(tmpIntent);

				}
				return true;
			}
		};
	}

	public static View.OnClickListener getGoAuthorRecommendListener(
			final PviActivity act) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//				v.setEnabled(false);

				//				act.showMessage("进入编辑推荐...", "15000");
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				tmpIntent.putExtras(bundleToSend);
				bundleToSend.putString("act", "com.pvi.ap.reader.activity.WirelessStoreActivity");
				bundleToSend.putString("actTabName", "编辑推荐");
				bundleToSend.putString("pviapfStatusTip", "进入编辑推荐...");
					tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);
			}

		};
	}
	public static View.OnTouchListener getGoAuthorRecommendTouchListener(
			final PviActivity act) {
		return new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent arg1) {
				// TODO Auto-generated method stub
				//				v.setEnabled(false);
				v.requestFocus();
				if(arg1.getAction()== MotionEvent.ACTION_UP)
				{
					//					act.showMessage("进入编辑推荐...", "15000");
					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					tmpIntent.putExtras(bundleToSend);
					bundleToSend.putString("actID", "ACT11000");
					bundleToSend.putString("actTabName", "编辑推荐");
					bundleToSend.putString("pviapfStatusTip", "进入编辑推荐...");
					bundleToSend.putString("pviapfStatusTipTime",
					"20000");
					tmpIntent.putExtras(bundleToSend);
					act.sendBroadcast(tmpIntent);
				}
				return true;
			}

		};
	}

	public static View.OnClickListener getCatalogListener(final PviActivity act) {

		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//		
				act.showMessage("进入分类栏目...", "15000");
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				tmpIntent.putExtras(bundleToSend);
				bundleToSend.putString("actID", "ACT11000");
				bundleToSend.putString("actTabName", "分类栏目");
				tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);
			}

		};
	}

	public static View.OnClickListener getRankingListener(final PviActivity act, final String rankid) {

		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				act.showMessage("进入热门排行...", "15000");
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				tmpIntent.putExtras(bundleToSend);
				bundleToSend.putString("actID", "ACT11000");
				bundleToSend.putString("actTabName", "热门排行");
				if(rankid != null){
					WirelessRankActivity.setRankTypeId(rankid);
				}
				tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);
			}

		};
	}

	public static View.OnTouchListener getRankingTouchListener(final PviActivity act, final String rankid) {

		return new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				v.requestFocus();
				if(event.getAction() == MotionEvent.ACTION_UP)
				{
					act.showMessage("进入热门排行...", "15000");

					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					tmpIntent.putExtras(bundleToSend);
					bundleToSend.putString("actID", "ACT11000");
					bundleToSend.putString("actTabName", "热门排行");
					if(rankid != null){
						WirelessRankActivity.setRankTypeId(rankid);
					}
					tmpIntent.putExtras(bundleToSend);
					act.sendBroadcast(tmpIntent);
				}
				return true;
			}

		};
	}

	public static View.OnClickListener getGoWriterListener(final PviActivity act) {

		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				act.showMessage("进入名家名作...", "15000");
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				tmpIntent.putExtras(bundleToSend);
				bundleToSend.putString("actID", "ACT11000");
				bundleToSend.putString("actTabName", "名家名作");
				tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);
			}

		};
	}

	public static View.OnClickListener getGoodListener(final PviActivity act) {

		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				act.showMessage("进入最新资讯...", "15000");
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();

				bundleToSend.putString("pviapfStatusTip", act.getResources()
						.getString(R.string.kyleHint01));
				tmpIntent.putExtras(bundleToSend);
				bundleToSend.putString("actID", "ACT11000");
				bundleToSend.putString("actTabName", "精品专栏");
				tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);
			}

		};
	}

	public static View.OnClickListener getNewInfoListener(final PviActivity act) {

		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				act.showMessage("进入最新资讯...", "15000");
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				tmpIntent.putExtras(bundleToSend);
				bundleToSend.putString("actID", "ACT11000");
				bundleToSend.putString("actTabName", "最新资讯");
				tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);
			}

		};
	}

	public static View.OnClickListener getBookPackageInfoListener(final PviActivity act) {

		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				act.showMessage("进入包月书包...", "15000");
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				tmpIntent.putExtras(bundleToSend);
				bundleToSend.putString("actID", "ACT11000");
				bundleToSend.putString("actTabName", "包月书包");
				tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);
			}

		};
	}

	public static View.OnClickListener getMyDocListener(final PviActivity act) {

		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				act.showMessage("进入我的文档...", "15000");
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				tmpIntent.putExtras(bundleToSend);
				bundleToSend.putString("actID", "ACT13100");
				tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);
			}

		};
	}

	public static View.OnClickListener getStorePageListener(final PviActivity act) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				act.showMessage("进入存储空间...", "15000");
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				tmpIntent.putExtras(bundleToSend);
				bundleToSend.putString("actID", "ACT13600");
				tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);
			}

		};
	}

	public static View.OnClickListener getPictureListener(final PviActivity act) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				act.showMessage("进入我的图片...", "15000");
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();

				tmpIntent.putExtras(bundleToSend);
				bundleToSend.putString("actID", "ACT13300");
				tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);
			}

		};
	}

	public static View.OnClickListener getMyNoteListener(final PviActivity act) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				act.showMessage("进入我的便笺...", "15000");
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				tmpIntent.putExtras(bundleToSend);
				bundleToSend.putString("actID", "ACT13400");
				tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);
			}

		};
	}

	public static View.OnClickListener systemsettinglistener(final PviActivity act)
	{
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				act.showMessage("进入系统设置...", "15000");
				Intent intent1 = new Intent(
						MainpageActivity.START_ACTIVITY);
				Bundle sndBundle1 = new Bundle();
				sndBundle1
				.putString("act",
				"com.pvi.ap.reader.activity.SystemConfigActivity");

				sndBundle1.putString("startType", "allwaysCreate");
				intent1.putExtras(sndBundle1);
				act.sendBroadcast(intent1);
			}
		};
	}

	public static View.OnClickListener allapplistener(final PviActivity act)
	{
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				act.showMessage("进入所有应用...", "15000");
				Intent intent1 = new Intent(
						MainpageActivity.START_ACTIVITY);
				Bundle sndBundle1 = new Bundle();
				sndBundle1.putString("act",
				"com.pvi.ap.reader.activity.AllAppActivity");
				sndBundle1.putString("startType", "allwaysCreate");
				intent1.putExtras(sndBundle1);
				act.sendBroadcast(intent1);
			}
		};
	}
	public static View.OnClickListener musiclistener(final PviActivity act)
	{
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				act.showMessage("进入我的音乐...", "15000");
				Intent tmpIntent = new Intent(
						MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID", "ACT13000");
				bundleToSend.putString("actTabIndex", "1");
				tmpIntent.putExtras(bundleToSend);
				act.sendBroadcast(tmpIntent);
			}
		};
	}
	public static View.OnClickListener backlistener(final PviActivity act)
	{
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				act.sendBroadcast(new Intent(MainpageActivity.BACK));
			}
		};
	}
}
