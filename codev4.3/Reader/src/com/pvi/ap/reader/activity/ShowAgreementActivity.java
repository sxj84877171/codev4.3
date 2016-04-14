package com.pvi.ap.reader.activity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * 显示协议，隐私，版权<br>
 * @author 彭见宝
 * @since 2010-11-24
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 */
public class ShowAgreementActivity extends PviActivity implements Pageable {

	protected static final String TAG = "ShowAgreementActivity";

	private Spanned sp ;

	private String msgcContext;

	private int pages;

	private int pageSize = 500;

	private int curPage = 0;

	private int type = 0;

	private TextView contextView;

//	private TextView titleView;

	public void getData(){
		handler.sendEmptyMessage(4);

		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();

		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

		ahmNamePair.put("type", String.valueOf(type));

		HashMap responseMap = null;
		try {
			responseMap = CPManager.getHandsetProperties(ahmHeaderMap, ahmNamePair);
		} catch (Exception e) {
			handler.sendEmptyMessage(2);
			return ;
		} 
		result = responseMap.get("result-code").toString();
		if (result.contains("result-code: 0")){
			byte[] responseBody = (byte[])responseMap.get("ResponseBody");
			try {
				String context = CPManagerUtil.getStringFrombyteArray(responseBody);
				sp = Html.fromHtml(context);
				msgcContext = sp.subSequence(0, sp.length()).toString();
				if(msgcContext==null){
					msgcContext="";
				}
				String regex = "<!--(.*)-->|[*].*[{](.*)[}]"; 
				msgcContext = msgcContext.replaceAll(regex, "");
				handler.sendEmptyMessage(1);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else{
			handler.sendEmptyMessage(3);
		}

	}

	public void reflushUI(){
		pages = msgcContext.length() % pageSize == 0?(msgcContext.length()/pageSize):(msgcContext.length()/pageSize+1);
		updatePagerinfo(String.valueOf(curPage + 1) + "/" + String.valueOf(pages));
		String charSequence;
		if(msgcContext.length()<curPage * pageSize + pageSize){
			charSequence = msgcContext.substring(curPage * pageSize, msgcContext.length());
		}else{
			charSequence = msgcContext.substring(curPage * pageSize, curPage * pageSize + pageSize);
		}
		contextView.setText(charSequence);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.showagreement);

		super.onCreate(savedInstanceState);
		final GlobalVar app = ((GlobalVar) getApplicationContext());    
		pbb = app.pbb;
		this.showPager=true;
	}

	/* (non-Javadoc)
	 * @see com.pvi.ap.reader.activity.PviActivity#onResume()
	 */
	PviBottomBar  pbb;  
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		type = intent.getExtras().getInt("type");

		contextView = (TextView)findViewById(R.id.showagreement_context);
		contextView.setFocusable(true);
		contextView.setFocusableInTouchMode(true);
		contextView.requestFocus();
		curPage = 0 ;
		new ThreadChild().start();
		sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
		super.onResume();
	}


	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				reflushUI();

				showMe(ShowAgreementActivity.this.getClass());
				break;
			case 2:
				hideTip();
				final PviAlertDialog pd = new PviAlertDialog(getParent());
				pd.setTitle(R.string.kyleHint02);
				pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
						new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						new ThreadChild().start();
					}
				});
				pd.setButton(DialogInterface.BUTTON_NEUTRAL, "取消",
						new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						sendBroadcast(new Intent(MainpageActivity.BACK));
					}
				});
				pd.show();

				break;
			case 3:
				hideTip();
				result = result.substring(result.indexOf(' ') + 1, result.indexOf('\r'));
				//获取手机验证码失败
				PviAlertDialog errorDialog =  new PviAlertDialog(getParent());
				errorDialog.setCanClose(true);
				errorDialog.setTitle("错误提示");
				errorDialog.setMessage(Error.getErrorDescriptionForContent(result));
				errorDialog.show();
				sendBroadcast(new Intent(MainpageActivity.BACK));

				break ;
			case 4:
				showNetWorkProcessing();
				break ;
			default:
				break;
			}
		};
	};

	private class ThreadChild extends Thread{
		public void run() {
			getData(); 		

		};
	};

	private String result = "" ;
	@Override
	public void OnNextpage() {
		// TODO Auto-generated method stub
		if(curPage < pages-1){
			curPage ++ ;
			reflushUI();
		}
		super.OnNextpage();
	}

	@Override
	public void OnPrevpage() {
		// TODO Auto-generated method stub
		
		if(curPage > 0){
			curPage -- ;
			reflushUI();
		}
		super.OnPrevpage();
	}
}
