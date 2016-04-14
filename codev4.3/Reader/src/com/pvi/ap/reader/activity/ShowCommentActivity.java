package com.pvi.ap.reader.activity;

import java.text.ParseException;
import java.util.HashMap;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.BoldTextView;
import com.pvi.ap.reader.activity.pviappframe.InactiveFunction;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * �鿴����
 *
 * @author ������
 * @author rd040
 *
 */
public class ShowCommentActivity extends PviActivity {

	String fromuser = "";
	String content = "";
	String time = "";
	String uptotal = "";
	String commentID = "";
	String downtotal = "";
	String floor = "";
	private RelativeLayout relativeLayout = null;
	private TextView floor_txt = null;
	private TextView fromuser_txt = null;
	private TextView time_txt = null;
	private BoldTextView up_txt = null;
	private BoldTextView down_txt = null;
	private TextView content_txt = null;
	private Button closebtn = null;

	//	private BoldTextView title = null;

	private Handler mhandler= null;

	private RelativeLayout uplayout = null;
	private RelativeLayout downlayout = null;

	private static final String LOG_TAG = "ShowCommentActivity";
	private Intent revIntent = null;
	private Bundle revBundle = null;
	//	private int themeNum=1;//��������
	private TextView []CatButton = new TextView[6];//tab
	private Button [] button=new Button[2];
	public void init(){

		uplayout = (RelativeLayout) this.findViewById(R.id.uplayout);
		downlayout = (RelativeLayout) this.findViewById(R.id.downlayout);
		floor_txt = (TextView) this.findViewById(R.id.floor);
		fromuser_txt = (TextView) this.findViewById(R.id.fromuser);
		time_txt = (TextView) this.findViewById(R.id.time);
		up_txt = (BoldTextView) this.findViewById(R.id.upbtn);
		down_txt = (BoldTextView) this.findViewById(R.id.downbtn);
		//			precomment_txt = (TextView) this.findViewById(R.id.precomment);
		//			nextcomment_txt = (TextView) this.findViewById(R.id.nextcomment);
		content_txt = (TextView) this.findViewById(R.id.content);
		closebtn = (Button) this.findViewById(R.id.b_return);
		//			if(appState.deviceType==1)
		//			{
		//				this.uplayout.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_DU | View.EINK_UPDATE_MODE_PARTIAL);
		//				this.downlayout.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_DU | View.EINK_UPDATE_MODE_PARTIAL);
		//				this.closebtn.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_DU | View.EINK_UPDATE_MODE_PARTIAL);
		//			}
		showme();
	}
	//	GlobalVar appState = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {

		//		appState = ((GlobalVar) getApplicationContext());
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.showcomment);

		init();

		try {
			revIntent = this.getIntent();
			revBundle = revIntent.getExtras();
			if (revBundle != null) {
				fromuser = revBundle.getString("fromuser");
				content = revBundle.getString("content");
				time = revBundle.getString("time");
				this.uptotal = revBundle.getString("dingCount");
				this.downtotal= revBundle.getString("boCount");
				this.floor = revBundle.getString("floor");
				this.commentID = revBundle.getString("commentID");
			}
		} catch (Exception e) {
			Log.d(LOG_TAG, e.getMessage());
		}


		this.floor_txt.setText(this.floor + "¥");
		this.fromuser_txt.setText(this.fromuser);
		try {
			this.time_txt.setText(GlobalVar.TimeFormat("yyyy-MM-dd HH:mm:ss",this.time));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.e("ShowComment", e.toString());
			this.time_txt.setText(this.time);
		}
		this.up_txt.setText("��(" + this.uptotal +")");
		this.down_txt.setText("��(" + this.downtotal+")");
		this.content_txt.setText(this.content);

		this.uplayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Thread upthread = new Thread()
				{

					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						HashMap<String,String> retmap = InactiveFunction.submitCommentVote(true, commentID);
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
						return;
					}

				};
				upthread.start();
			}
		});
		this.downlayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Thread downthread = new Thread()
				{

					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						HashMap<String,String> retmap = InactiveFunction.submitCommentVote(false, commentID);
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
						return;
					}

				};
				downthread.start();
			}
		});
		closebtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//					Intent intent = new Intent(WirelessTabActivity.BACK);
				//					Bundle bundle = new Bundle();
				//					bundle.putString("startType", "allwaysCreate");
				//					intent.putExtras(bundle);
				//					sendBroadcast(intent);
				finish();
			}
		});

		mhandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);

				switch(msg.what)
				{
				case 4:   //���ݷ�������ʾ��Ϣ
					PviAlertDialog pd2 = null;
					Bundle temp = msg.getData();

					if(getParent()!=null)
					{
						pd2 = new PviAlertDialog(getParent());
					}
					else
					{
						pd2 = new PviAlertDialog(ShowCommentActivity.this);
					}

					pd2.setTitle("��ܰ��ʾ");
					pd2.setMessage(Error.getErrorDescriptionForContent(temp.getString("Ret")),Gravity.CENTER);
					pd2.setCanClose(false);
					pd2.setButton("ȷ��", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//							finish();
						}
					});
					pd2.show();
					break;
				case 0:     
					PviAlertDialog pd = null;
					Bundle temp1 = msg.getData();
					if(getParent()!=null)
					{
						pd = new PviAlertDialog(getParent());
					}
					else
					{
						pd = new PviAlertDialog(ShowCommentActivity.this);
					}

					pd.setTitle("��ܰ��ʾ");

					pd.setCanClose(false);
					if(temp1.getBoolean("up"))
					{	
						pd.setMessage("֧�ָ����۳ɹ���",Gravity.CENTER);
					}
					else
					{
						pd.setMessage("���������۳ɹ���",Gravity.CENTER);
					}

					pd.setButton("ȷ��", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
						}
					});
					pd.show();
					up_txt.setText("��(" + temp1.getString("flowerValue") +")");
					down_txt.setText("��(" + temp1.getString("eggValue")+")");

					break;
				case 1:
					final PviAlertDialog pd1 = new PviAlertDialog(ShowCommentActivity.this);
					pd1.setTitle("��ܰ��ʾ");
					pd1.setMessage("�����쳣����������ʧ�ܣ�",Gravity.CENTER);
					pd1.setCanClose(false);
					pd1.setButton("ȷ��", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							pd1.dismiss();
						}
					});
					pd1.show();
					break;
				case 2:
					final PviAlertDialog pd3 = new PviAlertDialog(getParent()!=null?getParent():ShowCommentActivity.this);
					pd3.setTitle("��ܰ��ʾ");
					pd3.setMessage("������ʱ����������ʧ�ܣ�",Gravity.CENTER);
					pd3.setCanClose(false);
					pd3.setButton("ȷ��", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							pd3.dismiss();
						}
					});
					pd3.show();
					break;
				case 3:
					final PviAlertDialog pd4 = new PviAlertDialog((getParent()!=null?getParent():ShowCommentActivity.this));
					pd4.setTitle("��ܰ��ʾ");
					pd4.setMessage("�ƶ����������쳣��������������ʧ�ܣ�",Gravity.CENTER);
					pd4.setCanClose(false);
					pd4.setButton("ȷ��", new DialogInterface.OnClickListener() {

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
	}



	//	private String submitCommentVote(boolean up)
	//	{
	//		String flowerValue = "";
	//		String eggValue = "";
	//		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
	//		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
	//		ahmNamePair.put("commentId", this.commentID);
	//		Log.i("Reader", "commentID" + commentID);
	//		if(up)
	//		{
	//			ahmNamePair.put("vote", "1");
	//		}
	//		else
	//		{
	//			ahmNamePair.put("vote", "0");
	//		}
	//
	//		HashMap responseMap = null;
	//
	//		try {
	//			// ��POST����ʽ��������
	//			responseMap = CPManager.submitCommentVote(ahmHeaderMap, ahmNamePair);
	//			if (responseMap.get("result-code").toString().contains(
	//			"result-code: 0")) {
	//				byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
	//
	//				Document dom = null;
	//				try {
	//					dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
	//				} catch (ParserConfigurationException e) {
	//					e.printStackTrace();
	//					mhandler.sendEmptyMessage(1);
	//					return "1";
	//				} catch (SAXException e) {
	//					e.printStackTrace();
	//					mhandler.sendEmptyMessage(1);
	//					return "1";
	//				} catch (IOException e) {
	//					e.printStackTrace();
	//					mhandler.sendEmptyMessage(1);
	//					return "1";
	//				}
	//
	//
	//				Element root = dom.getDocumentElement();
	//
	//				NodeList nl = root.getElementsByTagName("flowerValue");
	//				Element element = (Element) nl.item(0);
	//				if(element==null)
	//				{
	//					mhandler.sendEmptyMessage(3);
	//					return "3";
	//				}
	//				else if(element.getFirstChild() == null)
	//				{
	//					mhandler.sendEmptyMessage(3);
	//					return "3";
	//				}
	//				else
	//				{
	//					flowerValue = element.getFirstChild().getNodeValue();
	//				}
	//
	//				nl = root.getElementsByTagName("eggValue");
	//				element = (Element) nl.item(0);
	//				if(element==null)
	//				{
	//					mhandler.sendEmptyMessage(3);
	//					return "3";
	//				}
	//				else if(element.getFirstChild() == null)
	//				{
	//					mhandler.sendEmptyMessage(3);
	//					return "3";
	//				}
	//				else
	//				{
	//					eggValue = element.getFirstChild().getNodeValue();
	//				}
	//
	//				Bundle temp = new Bundle();
	//				temp.putBoolean("up", up);
	//				temp.putString("flowerValue", flowerValue);
	//				temp.putString("eggValue", eggValue);
	//				Message msg = new Message();
	//				msg.what=0;
	//				msg.setData(temp);
	//				mhandler.sendMessage(msg);
	//
	//				return "0";
	//			}
	//		}catch (SocketTimeoutException e) {
	//			e.printStackTrace();
	//			mhandler.sendEmptyMessage(1);
	//			return "1";
	//		}catch (HttpException e) {
	//			// �����쳣 ,һ��ԭ��Ϊ URL����
	//			e.printStackTrace();
	//			mhandler.sendEmptyMessage(2);
	//			return "2";
	//		} catch (IOException e) {
	//			// IO�쳣 ,һ��ԭ��Ϊ��������
	//			e.printStackTrace();
	//			mhandler.sendEmptyMessage(1);
	//			return "1";
	//		}
	//		Bundle temp = new Bundle();
	//		temp.putString("Ret", responseMap.get("result-code").toString());
	//		Message msg = new Message();
	//		msg.what=4;
	//		msg.setData(temp);
	//		mhandler.sendMessage(msg);
	//		Log.i("", responseMap.get("result-code").toString());
	//		return responseMap.get("result-code").toString();
	//	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);

		if(deviceType==1){
			if(relativeLayout!=null)
			{
//				relativeLayout.invalidate(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
			}
		}
	}
	private void showme()
	{
		Intent intent1 = new Intent(MainpageActivity.HIDE_TIP);
		sendBroadcast(intent1);
		Intent tmpIntent = new Intent(
				MainpageActivity.SHOW_ME);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("sender",this.getClass().getName()); //TAB��Ƕactivity���ȫ��
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
		tmpIntent = null;
		bundleToSend = null;
	}
}
