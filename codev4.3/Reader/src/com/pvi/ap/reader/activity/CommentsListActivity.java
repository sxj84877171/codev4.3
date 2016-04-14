package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.BoldTextView;
import com.pvi.ap.reader.activity.pviappframe.InactiveFunction;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiUtil;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * 评论列表页
 * Author:马中庆
 */

public class CommentsListActivity extends PviActivity {
	private String LOG_TAG = "CommentsListActivity";
	protected final String TAG = "CommentsListActivity";
	private ArrayList<String[]> commentsList = new ArrayList<String[]>();
//	private int skinID = 1;//皮肤ID

	private Intent revIntent = null;
	private Bundle revBundle = null;
	private String contentId = "";
	private Handler mhandler= null;
	private RelativeLayout[] commentlayout = null;
	public final int pageSize = 4;
	private int curPage = 1;
	private int pages = 0;

	BoldTextView btnNew = null;
	private EditText et_content = null;
	private String commentContent = "";
	private int rows=0;//总记录数

	private TextView[] floorstext = null;
	private TextView[] timestext = null;
	private TextView[] fromuserstext = null;
	private TextView[] contentstext = null;
	private BoldTextView[] dingbtn = null;
	private BoldTextView[] bobtn = null;
	private boolean ishow=false;

	private boolean onGoing = false;
	private int pageCounter = 0;//翻页计数器
	private void compPageCounter() {
		if (((GlobalVar) getApplication()).deviceType == 1) {
			pageCounter++;
			if (pageCounter == 5) {
				pageCounter = 0;
				// gc16 full flash window
				Logger.d(TAG, "gc16 full");
//				getWindow().getDecorView().getRootView().postInvalidate(
//						View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16
//						| View.EINK_UPDATE_MODE_FULL);
			} else {
				Logger.d(TAG, "DU content");
			}
		}
	}
	private void init()
	{
		commentlayout = new RelativeLayout[pageSize];
		this.floorstext = new TextView[this.pageSize];
		this.timestext = new TextView[this.pageSize];
		this.fromuserstext = new TextView[this.pageSize];
		this.contentstext = new TextView[this.pageSize];
		this.dingbtn = new BoldTextView[this.pageSize];
		this.bobtn = new BoldTextView[this.pageSize];

		btnNew = (BoldTextView) findViewById(R.id.ButtonNew);
		et_content=(EditText)findViewById(R.id.comment_content);

		this.commentlayout[0] = (RelativeLayout) this.findViewById(R.id.commentlayout01);
		this.commentlayout[1] = (RelativeLayout) this.findViewById(R.id.commentlayout02);
		this.commentlayout[2] = (RelativeLayout) this.findViewById(R.id.commentlayout03);
		this.commentlayout[3] = (RelativeLayout) this.findViewById(R.id.commentlayout04);

		this.contentstext[0] = (TextView) this.findViewById(R.id.tv_content01);
		this.contentstext[1] = (TextView) this.findViewById(R.id.tv_content02);
		this.contentstext[2] = (TextView) this.findViewById(R.id.tv_content03);
		this.contentstext[3] = (TextView) this.findViewById(R.id.tv_content04);

		this.floorstext[0] = (TextView) this.findViewById(R.id.tv_floor01);
		this.floorstext[1] = (TextView) this.findViewById(R.id.tv_floor02);
		this.floorstext[2] = (TextView) this.findViewById(R.id.tv_floor03);
		this.floorstext[3] = (TextView) this.findViewById(R.id.tv_floor04);

		this.timestext[0] = (TextView) this.findViewById(R.id.tv_time01);
		this.timestext[1] = (TextView) this.findViewById(R.id.tv_time02);
		this.timestext[2] = (TextView) this.findViewById(R.id.tv_time03);
		this.timestext[3] = (TextView) this.findViewById(R.id.tv_time04);

		this.fromuserstext[0] = (TextView) this.findViewById(R.id.tv_fromuser01);
		this.fromuserstext[1] = (TextView) this.findViewById(R.id.tv_fromuser02);
		this.fromuserstext[2] = (TextView) this.findViewById(R.id.tv_fromuser03);
		this.fromuserstext[3] = (TextView) this.findViewById(R.id.tv_fromuser04);

		this.dingbtn[0] = (BoldTextView) this.findViewById(R.id.ding01);
		this.dingbtn[1] = (BoldTextView) this.findViewById(R.id.ding02);
		this.dingbtn[2] = (BoldTextView) this.findViewById(R.id.ding03);
		this.dingbtn[3] = (BoldTextView) this.findViewById(R.id.ding04);

		this.bobtn[0] = (BoldTextView) this.findViewById(R.id.bo01);
		this.bobtn[1] = (BoldTextView) this.findViewById(R.id.bo02);
		this.bobtn[2] = (BoldTextView) this.findViewById(R.id.bo03);
		this.bobtn[3] = (BoldTextView) this.findViewById(R.id.bo04);


		this.commentlayout[0].setOnClickListener(commentlayoutlistener);
		this.commentlayout[1].setOnClickListener(commentlayoutlistener);
		this.commentlayout[2].setOnClickListener(commentlayoutlistener);
		this.commentlayout[3].setOnClickListener(commentlayoutlistener);

		this.dingbtn[0].setOnClickListener(dingbolistener);
		this.bobtn[0].setOnClickListener(dingbolistener);
		this.dingbtn[1].setOnClickListener(dingbolistener);
		this.bobtn[1].setOnClickListener(dingbolistener);
		this.dingbtn[2].setOnClickListener(dingbolistener);
		this.bobtn[2].setOnClickListener(dingbolistener);
		this.dingbtn[3].setOnClickListener(dingbolistener);
		this.bobtn[3].setOnClickListener(dingbolistener);

		if(deviceType==1)
		{
			for(int i=0; i<4; i++)
			{
//				commentlayout[i].setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
			}
//			et_content.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			btnNew.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
		}
	}

	private View.OnClickListener commentlayoutlistener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(onGoing)
			{
				return;
			}
			int idx = 0;
			switch(v.getId())
			{
			case R.id.commentlayout01:
				idx = 0;
				break;
			case R.id.commentlayout02:
				idx = 1;
				break;
			case R.id.commentlayout03:
				idx = 2;
				break;
			case R.id.commentlayout04:
				idx = 3;
				break;
			}
			int index = curPage ;
			Bundle bundleToSend = new Bundle();
			bundleToSend.putString("commentID",commentsList.get((index-1)*pageSize + idx)[0]);
			bundleToSend.putString("fromuser",commentsList.get((index-1)*pageSize+ idx)[1]);
			bundleToSend.putString("content",commentsList.get((index-1)*pageSize+ idx)[6]);
			bundleToSend.putString("floor",commentsList.get((index-1)*pageSize+ idx)[3]);
			bundleToSend.putString("dingCount",commentsList.get((index-1)*pageSize+ idx)[4]);
			bundleToSend.putString("boCount",commentsList.get((index-1)*pageSize+ idx)[5]);
			bundleToSend.putString("time",commentsList.get((index-1)*pageSize+ idx)[2]);
			final Intent tmpintent = new Intent(v.getContext(), ShowCommentActivity.class);
			tmpintent.putExtras(bundleToSend);
			startActivity(tmpintent);   
		}
	};

	private View.OnClickListener dingbolistener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(onGoing)
			{
				return;
			}
			onGoing = true;
			int idx = 0;
			switch(v.getId())
			{
			case R.id.ding01:
				idx = 0;
				break;
			case R.id.bo01:
				idx = 1;
				break;
			case R.id.ding02:
				idx = 2;
				break;
			case R.id.bo02:
				idx = 3;
				break;
			case R.id.ding03:
				idx = 4;
				break;
			case R.id.bo03:
				idx = 5;
				break;
			case R.id.ding04:
				idx = 6;
				break;
			case R.id.bo04:
				idx = 7;
				break;
			}
			final int temp = idx;
			if(idx%2 == 0)
			{
				//ding
				Thread upthread = new Thread()
				{

					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						HashMap<String,String> retmap = InactiveFunction.submitCommentVote(true, commentsList.get((curPage-1)*pageSize + temp/2)[0]);

						String retcode = retmap.get("RetCode");
						if(retcode.equals("1"))
						{
							mhandler.sendEmptyMessage(1);
						}
						else if(retcode.equals("2"))
						{
							mhandler.sendEmptyMessage(2);
						}
						else if(retcode.equals("3"))
						{
							mhandler.sendEmptyMessage(3);
						}
						else if(retcode.equals("0"))
						{
							Bundle temp = new Bundle();
							temp.putBoolean("up", true);
							temp.putString("flowerValue", retmap.get("flowerValue"));
							temp.putString("eggValue",retmap.get("eggValue"));
							Message msg = new Message();
							msg.what=0;
							msg.setData(temp);
							mhandler.sendMessage(msg);
						}
						else
						{
							Bundle temp = new Bundle();
							temp.putString("Ret", retmap.get("RetCode"));
							Message msg = new Message();
							msg.what=4;
							msg.setData(temp);
							mhandler.sendMessage(msg);
						}
						onGoing=false;
						return;
					}

				};
				upthread.start();
			}
			else
			{
				//bo
				Thread downthread = new Thread()
				{

					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						HashMap<String,String> retmap = InactiveFunction.submitCommentVote(false, commentsList.get((curPage-1)*pageSize + temp/2)[0]);

						String retcode = retmap.get("RetCode");
						if(retcode.equals("1"))
						{
							mhandler.sendEmptyMessage(1);
						}
						else if(retcode.equals("2"))
						{
							mhandler.sendEmptyMessage(2);
						}
						else if(retcode.equals("3"))
						{
							mhandler.sendEmptyMessage(3);
						}
						else if(retcode.equals("0"))
						{
							Bundle temp = new Bundle();
							temp.putBoolean("up", false);
							temp.putString("flowerValue", retmap.get("flowerValue"));
							temp.putString("eggValue",retmap.get("eggValue"));
							Message msg = new Message();
							msg.what=0;
							msg.setData(temp);
							mhandler.sendMessage(msg);
						}
						else
						{
							Bundle temp = new Bundle();
							temp.putString("Ret", retmap.get("RetCode"));
							Message msg = new Message();
							msg.what=4;
							msg.setData(temp);
							mhandler.sendMessage(msg);
						}
						onGoing=false;
						return;
					}

				};
				downthread.start();

			}
		}
	};
	GlobalVar appState = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {


		appState = ((GlobalVar) getApplicationContext());
//		skinID = appState.getSkinID();
//		if (skinID == 1) {
			setContentView(R.layout.commentslist_ui1);
//		} else if (skinID == 2) {
//			setContentView(R.layout.commentslist_ui2);
//		}
			
		this.showPager = true ;

		init();

		mhandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				PviAlertDialog pd2 = null;
				switch(msg.what)
				{
				case 4:   //根据返回码提示信息
					Bundle temp = msg.getData();

					if(getParent()!=null)
					{
						pd2 = new PviAlertDialog(getParent());
					}
					else
					{
						pd2 = new PviAlertDialog(CommentsListActivity.this);
					}

					pd2.setTitle("温馨提示");
					pd2.setMessage(Error.getErrorDescriptionForContent(temp.getString("Ret")),Gravity.CENTER);
					pd2.setCanClose(false);
					pd2.setButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//							finish();
						}
					});
					pd2.show();
					break;
				case 0:       // 提交成功时
					Bundle temp1 = msg.getData();
					if(getParent()!=null)
					{
						pd2 = new PviAlertDialog(getParent());
					}
					else
					{
						pd2 = new PviAlertDialog(CommentsListActivity.this);
					}

					pd2.setTitle("温馨提示");

					pd2.setCanClose(false);
					if(temp1.getBoolean("up"))
					{	
						pd2.setMessage("支持该评论成功！",Gravity.CENTER);
					}
					else
					{
						pd2.setMessage("反驳该评论成功！",Gravity.CENTER);
					}

					pd2.setButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							finish();
						}
					});
					pd2.show();
					setUIData();
					break;
				case 1:
					hideTip();
					final PviAlertDialog pd = new PviAlertDialog(getParent());
					pd.setTitle("温馨提示");
					pd.setMessage("联网异常，导致请求失败！",Gravity.CENTER);
					pd.setCanClose(false);
					pd.setButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							pd.dismiss();
						}
					});
					pd.show();
					break;
				case 2:
					hideTip();
					final PviAlertDialog pd3 = new PviAlertDialog(getParent()!=null?getParent():CommentsListActivity.this);
					pd3.setTitle("温馨提示");
					pd3.setMessage("联网超时，导致请求失败！",Gravity.CENTER);
					pd3.setCanClose(false);
					pd3.setButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							pd3.dismiss();
						}
					});
					pd3.show();
					break;
				case 3:
					final PviAlertDialog pd4 = new PviAlertDialog((getParent()!=null?getParent():CommentsListActivity.this));
					pd4.setTitle("温馨提示");
					pd4.setMessage("移动返回数据异常，解析顶驳次数失败！",Gravity.CENTER);
					pd4.setCanClose(false);
					pd4.setButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							pd4.dismiss();
						}
					});
					pd4.show();
					break;
				default:
					break;
				}
			}

		};
		super.onCreate(savedInstanceState);

		try {
			revIntent = getIntent();
			revBundle = revIntent.getExtras();
			if (revBundle != null) {
				contentId = revBundle.getString("contentID");
			}
		} catch (Exception e) {
			Logger.e(TAG, e.toString());
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ishow=false;
		setUIData();

	}

	//	private static class CommentsListAdapter extends BaseAdapter {
	//		private LayoutInflater mInflater;
	//		private ArrayList<String[]> commentsList;
	//
	//		public void setCommentsList(ArrayList<String[]> commentsList){
	//			this.commentsList = commentsList;
	//		}
	//
	//		public CommentsListAdapter(Context context, ArrayList<String[]> list) {
	//			mInflater = LayoutInflater.from(context);
	//			commentsList = list;
	//		}
	//
	//		public int getCount() {
	//			return commentsList.size();
	//		}
	//
	//		public Object getItem(int position) {
	//			return position;
	//		}
	//
	//		public long getItemId(int position) {
	//			return position;
	//		}
	//
	//		public View getView(int position, View convertView, ViewGroup parent) {
	//			ViewHolder holder = null;
	//			if (convertView == null) {
	//				if(skinID==1){
	//					convertView = mInflater
	//					.inflate(R.layout.commentslistitem_ui1, null);
	//				}else if(skinID==2){
	//					convertView = mInflater
	//					.inflate(R.layout.commentslistitem_ui2, null);
	//				} 
	//				holder = new ViewHolder();
	//				holder.tv_fromuser = (TextView) convertView
	//				.findViewById(R.id.tv_fromuser);
	//				holder.tv_content = (TextView) convertView
	//				.findViewById(R.id.tv_content);
	//				holder.tv_time = (TextView) convertView
	//				.findViewById(R.id.tv_time);
	//				holder.tv_floor=(TextView) convertView
	//				.findViewById(R.id.tv_floor);
	//				holder.tv_ding=(BoldTextView) convertView
	//				.findViewById(R.id.ding);
	//				holder.tv_bo=(BoldTextView) convertView
	//				.findViewById(R.id.bo);
	//				convertView.setTag(holder);
	//			} else {
	//				holder = (ViewHolder) convertView.getTag();
	//			}
	//
	//			holder.tv_fromuser.setText(commentsList.get(position)[1]);
	//			holder.tv_content.setText(commentsList.get(position)[6]);
	//			holder.tv_time.setText(commentsList.get(position)[2]);
	//			holder.tv_floor.setText(commentsList.get(position)[3]+"楼");
	//			holder.tv_ding.setText("顶(" + commentsList.get(position)[4] +")");
	//			holder.tv_bo.setText("驳("+commentsList.get(position)[5]+")");
	//			final int p = position;
	//			holder.tv_ding.setOnClickListener(new View.OnClickListener() {
	//
	//				@Override
	//				public void onClick(View v) {
	//					// TODO Auto-generated method stub
	//					Thread upthread = new Thread()
	//					{
	//
	//						@Override
	//						public void run() {
	//							// TODO Auto-generated method stub
	//							super.run();
	//							HashMap<String,String> retmap = InactiveFunction.submitCommentVote(true, commentsList.get(p)[0]);
	//
	//							String retcode = retmap.get("RetCode");
	//							if(retcode.equals("1"))
	//							{
	//								mhandler.sendEmptyMessage(1);
	//							}
	//							else if(retcode.equals("2"))
	//							{
	//								mhandler.sendEmptyMessage(2);
	//							}
	//							else if(retcode.equals("3"))
	//							{
	//								mhandler.sendEmptyMessage(3);
	//							}
	//							else if(retcode.equals("0"))
	//							{
	//								Bundle temp = new Bundle();
	//								temp.putBoolean("up", true);
	//								temp.putString("flowerValue", retmap.get("flowerValue"));
	//								temp.putString("eggValue",retmap.get("eggValue"));
	//								Message msg = new Message();
	//								msg.what=0;
	//								msg.setData(temp);
	//								mhandler.sendMessage(msg);
	//							}
	//							else
	//							{
	//								Bundle temp = new Bundle();
	//								temp.putString("Ret", retmap.get("RetCode"));
	//								Message msg = new Message();
	//								msg.what=4;
	//								msg.setData(temp);
	//								mhandler.sendMessage(msg);
	//							}
	//							return;
	//						}
	//
	//					};
	//					upthread.start();
	//				}
	//			});
	//			holder.tv_bo.setOnClickListener(new View.OnClickListener() {
	//
	//				@Override
	//				public void onClick(View arg0) {
	//					// TODO Auto-generated method stub
	//					Thread downthread = new Thread()
	//					{
	//
	//						@Override
	//						public void run() {
	//							// TODO Auto-generated method stub
	//							super.run();
	//							HashMap<String,String> retmap = InactiveFunction.submitCommentVote(false, commentsList.get(p)[0]);
	//							String retcode = retmap.get("RetCode");
	//							if(retcode.equals("1"))
	//							{
	//								mhandler.sendEmptyMessage(1);
	//							}
	//							else if(retcode.equals("2"))
	//							{
	//								mhandler.sendEmptyMessage(2);
	//							}
	//							else if(retcode.equals("3"))
	//							{
	//								mhandler.sendEmptyMessage(3);
	//							}
	//							else if(retcode.equals("0"))
	//							{
	//								Bundle temp = new Bundle();
	//								temp.putBoolean("up", false);
	//								temp.putString("flowerValue", retmap.get("flowerValue"));
	//								temp.putString("eggValue",retmap.get("eggValue"));
	//								Message msg = new Message();
	//								msg.what=0;
	//								msg.setData(temp);
	//								mhandler.sendMessage(msg);
	//							}
	//							else
	//							{
	//								Bundle temp = new Bundle();
	//								temp.putString("Ret", retmap.get("RetCode"));
	//								Message msg = new Message();
	//								msg.what=4;
	//								msg.setData(temp);
	//								mhandler.sendMessage(msg);
	//							}
	//							return;
	//						}
	//
	//					};
	//					downthread.start();
	//				}
	//			});
	//			convertView.setOnClickListener(new View.OnClickListener(){
	//				public void onClick(View v) {
	//
	//					Logger.i(TAG,"comment item clicked!");
	//					//show this comment
	//					Bundle bundleToSend = new Bundle();
	//					bundleToSend.putString("commentID",commentsList.get(p)[0]);
	//					bundleToSend.putString("fromuser",commentsList.get(p)[1]);
	//					bundleToSend.putString("content",commentsList.get(p)[6]);
	//					bundleToSend.putString("floor",commentsList.get(p)[3]);
	//					bundleToSend.putString("dingCount",commentsList.get(p)[4]);
	//					bundleToSend.putString("boCount",commentsList.get(p)[5]);
	//					bundleToSend.putString("time",commentsList.get(p)[2]);
	//					final Intent tmpintent = new Intent(v.getContext(), ShowCommentActivity.class);
	//					tmpintent.putExtras(bundleToSend);
	//					v.getContext().startActivity(tmpintent);   
	//				}});
	//
	//			return convertView;
	//		}
	//
	//		static class ViewHolder {
	//			TextView tv_floor;//楼
	//			TextView tv_fromuser;
	//			TextView tv_time;
	//			TextView tv_content;
	//			BoldTextView tv_ding;//支持
	//			BoldTextView tv_bo;//反对
	//		}
	//	}

	private Runnable getData = new Runnable() {
		@Override
		public void run() {
			//contentId = "13667427";
			if(ishow)
			{
				compPageCounter();
			}

			if(pages == 0){
				pages = rows % pageSize == 0?(rows / pageSize) : (rows / pageSize + 1);
			}
			if(pages == 0){
				pages = 1;
			}

			if(curPage > pages){
				curPage = pages;
			}
			updatePagerinfo(curPage + "/" + pages) ;


			String[] temparray = new String[7];
			for(int i=0; i < pageSize; i++)
			{
				if((curPage-1)*pageSize+i < commentsList.size())
				{
					temparray = commentsList.get((curPage-1)*pageSize+i);
					commentlayout[i].setVisibility(View.VISIBLE);
					floorstext[i].setText(temparray[3]+"楼");
					fromuserstext[i].setText(temparray[1]);
					try {
						timestext[i].setText(GlobalVar.TimeFormat("yyyy-MM-dd HH:mm:ss", temparray[2]));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						timestext[i].setText( temparray[2]);
						e.printStackTrace();
						Logger.e(TAG, e.toString());
					}
					contentstext[i].setText(temparray[6]);
					dingbtn[i].setText("顶(" +temparray[4] +")");
					bobtn[i].setText("驳(" +temparray[5] +")");
				}
				else
				{
					commentlayout[i].setVisibility(View.INVISIBLE);
				}
			}
			hideTip();
			if(!ishow)
			{
				showme();
			}
			onGoing = false;
		}
	};

	private void setUIData() {

		new Thread() {
			public void run() {
				onGoing = true;

				if(contentId!=null&&!contentId.equals("")){
					Logger.e("Reader", (curPage-1)*pageSize);
					Logger.e("commentsList", commentsList.size());

					// read data from remote
					HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
					HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
					ahmNamePair.put("start", String.valueOf((curPage-1)*pageSize+1));
					ahmNamePair.put("count", String.valueOf(pageSize));
					ahmNamePair.put("contentId", contentId);

					HashMap responseMap = null;
					try {
						responseMap = NetCache.getComment(ahmHeaderMap, ahmNamePair);
						if (responseMap!=null &&responseMap.get("result-code")!=null&&!responseMap.get("result-code").toString().contains(
						"result-code: 0")) {
							Logger.d(TAG, responseMap.get("result-code").toString());
							// return false;
							onGoing = false;
							mhandler.sendEmptyMessage(1);
							return;
						}
					} catch (HttpException e) {
						onGoing = false;
						mhandler.sendEmptyMessage(1);
						return;
					}catch (SocketTimeoutException e) {
						onGoing = false;
						mhandler.sendEmptyMessage(2);
						return;
					}catch (IOException e) {
						// return false;
						onGoing = false;
						mhandler.sendEmptyMessage(1);
						return;
					}
					byte[] responseBody=null;
					if(responseMap!=null)
					{
						responseBody = (byte[]) responseMap.get("ResponseBody");
					}
					else
					{
						mhandler.sendEmptyMessage(1);
						return;
					}

					Document dom = null;
					try {
						String xml = new String(responseBody);
						xml = xml.replaceAll(">\\s+?<", "><");
						dom = CPManagerUtil.getDocumentFrombyteArray(xml.getBytes());
					} catch (Exception e) {
						Logger.e(TAG, e.getMessage());
						onGoing = false;
						mhandler.sendEmptyMessage(1);
						return;
					}

					Element root = dom.getDocumentElement();
					Element erows=(Element)root.getElementsByTagName("totalRecordCount").item(0);
					String srows=erows.getFirstChild().getNodeValue();
					rows=Integer.parseInt(srows);
					NodeList nl1 = root.getChildNodes();
					nl1 = nl1.item(0).getChildNodes();

					int nl1Count = nl1.getLength();
					for (int i = 0; i < nl1Count; i++) {
						Element el1 = (Element) nl1.item(i);
						if (el1.getNodeName().equals("CommentList")) {
							NodeList nl2 = el1.getChildNodes();
							int nl2Count = nl2.getLength();
							for (int j = 0; j < nl2Count; j++) {
								Element el2 = (Element) nl2.item(j);
								if (el2.getNodeName().equals("Comment")) {
									NodeList nl3 = el2.getChildNodes();
									int nl3Count = nl3.getLength();
									String[] commentInfo = new String[nl3Count];
									for (int k = 0; k < nl3Count; k++) {
										Element el3 = (Element) nl3.item(k);
										commentInfo[k] = el3.getFirstChild()
										.getNodeValue();	
										Logger.i("reader", "CommentList" + k+":  " + commentInfo[k]);
									}
									if((curPage-1)*pageSize + j< commentsList.size())
									{
										commentsList.set((curPage-1)*pageSize + j, commentInfo);
									}
									else
									{
										commentsList.add(commentInfo);
									}
								}

							}
						}
					}
				}
				mhandler.post(getData);
			}
		}.start();
	}

	@Override
	public void bindEvent(){
		super.bindEvent();

		//新增
		if(btnNew!=null){
			btnNew.setOnClickListener(new TextView.OnClickListener() {
				@Override
				public void onClick(View v) {
				    PviUiUtil.hideInput(v);
					newComment();
				}
			});
		}
	}
	
	@Override
	public void OnPrevpage() {
		if(onGoing)
		{
			return;
		}

		if(curPage > 1){
			showMessage("正在获取数据...","20000");
			curPage -- ;
			setUIData();
		}
	}
	
	@Override
	public void OnNextpage() {
		if(onGoing)
		{
			return;
		}
		if(curPage < pages){
			showMessage("正在获取数据...","20000");
			curPage ++ ;
			setUIData();
		}
	}

	private OnUiItemClickListener menuclick = new OnUiItemClickListener() {
		@Override
		public void onUiItemClick(PviUiItem item){
			String vTag = item.id;
			if (vTag.equals("newComment")) {
				getPopmenu().dismiss();
				/*
                //添加评论
                Bundle bundleToSend = new Bundle();
                bundleToSend.putString("contentID",contentId);
                final Intent tmpintent = new Intent(CommentsListActivity.this, NewCommentActivity.class);
                tmpintent.putExtras(bundleToSend);
                startActivity(tmpintent);  
				 */

				// 发表评论

				newComment();
			}
		}
	};


	private void newComment(){
		// 发表评论
		//评论内容检测
		if(et_content.getText()!=null){

			commentContent = et_content.getText().toString();
			if(commentContent.equals("")){
				Activity a = getParent();
				PviAlertDialog pd;
				if(a!=null){
					pd = new PviAlertDialog(a);
				}else{
					pd = new PviAlertDialog(CommentsListActivity.this);
				}
				pd.setCanClose(true);
				pd.setTitle("温馨提示");
				pd.setMessage("请检查您的输入。",Gravity.CENTER);   
				et_content.setText("");
				pd.show(); 
				return ;
			}

			//提交评论
			HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
			HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
			ahmNamePair.put("contentID", contentId);
			ahmNamePair.put("comment", commentContent);

			HashMap responseMap = null;
			try {
				responseMap = NetCache.submitComment(ahmHeaderMap, ahmNamePair);
				if (!responseMap.get("result-code").toString().contains(
				"result-code: 0")) {
					Logger.d(LOG_TAG,responseMap.get("result-code").toString());
				}
			}  catch (Exception e) {Logger.d(LOG_TAG,e.getMessage());}
			Activity a = getParent();
			PviAlertDialog pd;
			if(a!=null){
				pd = new PviAlertDialog(a);
			}else{
				pd = new PviAlertDialog(CommentsListActivity.this);
			}
			pd.setCanClose(true);
			pd.setTitle("温馨提示");
			pd.setMessage("您的评论已经提交，请等待管理员审核。",Gravity.CENTER);  
			et_content.setText("");
			pd.show(); 
		}

	}

	private void showme()
	{

		ishow=true;
		super.showMe(getClass());
		super.hideTip();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub

		if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_CENTER)
		{
			return super.dispatchKeyEvent(event);
		}
		else if(event.getAction() == KeyEvent.ACTION_UP)
		{
			if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT)
			{
				View v = this.getCurrentFocus();
				switch(v.getId())
				{
				case R.id.comment_content:
				case R.id.ButtonNew:
				case R.id.commentlayout01:
					//				case R.id.ding01:
					//				case R.id.bo01:
				case R.id.commentlayout02:
					//				case R.id.ding02:
					//				case R.id.bo02:
				case R.id.commentlayout03:
					//				case R.id.ding03:
					//				case R.id.bo03:
				case R.id.commentlayout04:
					//				case R.id.ding04:
					//				case R.id.bo04:
					return true;
				case R.id.bo01:
					dingbtn[0].requestFocus();
					return true;
				case R.id.bo02:
					dingbtn[1].requestFocus();
					return true;
				case R.id.bo03:
					dingbtn[2].requestFocus();
					return true;
				case R.id.bo04:
					dingbtn[3].requestFocus();
					return true;
				case R.id.fp_settings:
					View v1 = this.findViewById(R.id.next);
					v1.requestFocus();
					return true;
				case R.id.fp_music:
					View v2 = this.findViewById(R.id.fp_settings);
					v2.requestFocus();
					return true;
				case R.id.back:
					View v3 = this.findViewById(R.id.fp_music);
					v3.requestFocus();
					return true;
				case R.id.prev:
					View v4 = this.findViewById(R.id.menubtn);
					v4.requestFocus();
					return true;
				case R.id.next:
					View v5 = this.findViewById(R.id.prev);
					v5.requestFocus();
					return true;
				default:
					return super.dispatchKeyEvent(event);
				}

			}
			else if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT)
			{

				View v = this.getCurrentFocus();
				switch(v.getId())
				{
				case R.id.comment_content:
				case R.id.ButtonNew:
				case R.id.commentlayout01:
					//				case R.id.ding01:
					//				case R.id.bo01:
				case R.id.commentlayout02:
					//				case R.id.ding02:
					//				case R.id.bo02:
				case R.id.commentlayout03:
					//				case R.id.ding03:
					//				case R.id.bo03:
				case R.id.commentlayout04:
					//				case R.id.ding04:
					//				case R.id.bo04:
					return true;
				case R.id.ding01:
					bobtn[0].requestFocus();
					return true;
				case R.id.ding02: 
					bobtn[1].requestFocus();
					return true;
				case R.id.ding03:
					bobtn[2].requestFocus();
					return true;
				case R.id.ding04:
					bobtn[3].requestFocus();
					return true;
//				case R.id.fp_settings:
//					View v1 = this.findViewById(R.id.fp_music);
//					v1.requestFocus();
//					return true;
//				case R.id.fp_music:
//					View v2 = this.findViewById(R.id.back);
//					v2.requestFocus();
//					return true;
//				case R.id.menubtn:
//					View v3 = this.findViewById(R.id.prev);
//					v3.requestFocus();
//					return true;
//				case R.id.prev:
//					View v4 = this.findViewById(R.id.next);
//					v4.requestFocus();
//					return true;
//				case R.id.next:
//					View v5 = this.findViewById(R.id.fp_settings);
//					v5.requestFocus();
//					return true;
				default:
					return super.dispatchKeyEvent(event);
				}

			}
			else if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_DOWN)
			{
				View view = findViewById(R.id.fp_settings);
				View v = this.getCurrentFocus();
				switch(v.getId())
				{
				case R.id.comment_content:
					btnNew.requestFocus();
					return true;
				case R.id.ButtonNew:
					if(this.commentlayout[0].getVisibility()==View.VISIBLE)
					{
						this.dingbtn[0].requestFocus();
						return true;
					}
					else
					{
						view.requestFocus();
						return true;
					}
				case R.id.bo01:

					this.commentlayout[0].requestFocus();
					return true;
				case R.id.ding01:
					this.commentlayout[0].requestFocus();
					return true;
				case R.id.commentlayout01:
					if(this.commentlayout[1].getVisibility()==View.VISIBLE)
					{
						this.dingbtn[1].requestFocus();
						return true;
					}
					else
					{
						view.requestFocus();
						return true;
					}
				case R.id.ding02:
					this.commentlayout[1].requestFocus();
					return true;
				case R.id.bo02:
					this.commentlayout[1].requestFocus();
					return true;
				case R.id.commentlayout02:
					if(this.commentlayout[2].getVisibility()==View.VISIBLE)
					{
						this.dingbtn[2].requestFocus();
						return true;
					}
					else
					{
						view.requestFocus();
						return true;
					}
				case R.id.ding03:
					this.commentlayout[2].requestFocus();
					return true;
				case R.id.bo03:
					this.commentlayout[2].requestFocus();
					return true;
				case R.id.commentlayout03:
					if(this.commentlayout[3].getVisibility()==View.VISIBLE)
					{
						this.dingbtn[3].requestFocus();
						return true;
					}
					else
					{
						view.requestFocus();
						return true;
					}
				case R.id.ding04:
					this.commentlayout[3].requestFocus();
					return true;
				case R.id.bo04:
					this.commentlayout[3].requestFocus();
					return true;
				case R.id.commentlayout04:
					view.requestFocus();
					return true;
				}
			}
			else if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_UP)
			{

				View v = this.getCurrentFocus();
				switch(v.getId())
				{
				case R.id.ButtonNew:
					this.et_content.requestFocus();
					break;
				case R.id.bo01:
					btnNew.requestFocus();
					return true;
				case R.id.ding01:
					btnNew.requestFocus();
					return true;
				case R.id.commentlayout01:
					dingbtn[0].requestFocus();
					break;
				case R.id.ding02:
					this.commentlayout[0].requestFocus();
					return true;
				case R.id.bo02:
					this.commentlayout[0].requestFocus();
					return true;
				case R.id.commentlayout02:
					dingbtn[1].requestFocus();
					break;
				case R.id.ding03:
					this.commentlayout[1].requestFocus();
					return true;
				case R.id.bo03:
					this.commentlayout[1].requestFocus();
					return true;
				case R.id.commentlayout03:
					this.dingbtn[2].requestFocus();
					return true;
				case R.id.ding04:
					this.commentlayout[2].requestFocus();
					return true;
				case R.id.bo04:
					this.commentlayout[2].requestFocus();
					return true;
				case R.id.commentlayout04:
					this.dingbtn[3].requestFocus();
					return true;
				case R.id.fp_settings:
				case R.id.fp_music:
				case R.id.back:
				case R.id.prev:
				case R.id.next:
				case R.id.menubtn:
					if(this.commentlayout[3].getVisibility()==View.VISIBLE)
					{
						this.commentlayout[3].requestFocus();
					}
					else if(this.commentlayout[2].getVisibility()==View.VISIBLE)
					{
						this.commentlayout[2].requestFocus();
					}
					else if(this.commentlayout[1].getVisibility()==View.VISIBLE)
					{
						this.commentlayout[1].requestFocus();
					}
					else if(this.commentlayout[0].getVisibility()==View.VISIBLE)
					{
						this.commentlayout[0].requestFocus();
					}
					else
					{
						this.btnNew.requestFocus();
					}

					return true;
				}
			}
		}
		else
		{
			return true;
		}

		return super.dispatchKeyEvent(event);
	}

	//	@Override
	//	public boolean dispatchTouchEvent(MotionEvent ev) {
	//		// TODO Auto-generated method stub
	//		View v = this.getCurrentFocus();
	//		if(v!=null&&v.getId()==R.id.comment_content)
	//		{
	//			((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(v, InputMethodManager.SHOW_FORCED);
	//		}
	//		return super.dispatchTouchEvent(ev);
	//	}
	
	@Override
	public OnUiItemClickListener getMenuclick() {
		return this.menuclick;
	}
	

}
