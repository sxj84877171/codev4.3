package com.pvi.ap.reader.activity.listener;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.GlobalVar;
import com.pvi.ap.reader.activity.MainpageActivity;
import com.pvi.ap.reader.activity.NetAuthenticate;
import com.pvi.ap.reader.activity.NetAuthenticateSIM;
import com.pvi.ap.reader.activity.NetAuthenticateUSIM;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import android.view.View.OnTouchListener;


public class MainPageTouchListener implements OnTouchListener {
	private PviActivity act;


	public MainPageTouchListener(PviActivity act) {
		super();
		this.act = act;
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
 	   
		if(event.getAction()==MotionEvent.ACTION_UP){

		String vTag = "";
		String subop = "";
		try
		{
			vTag = v.getTag().toString();
		}
		catch(Exception e)
		{
			vTag = "";
		}
		
		act.closePopmenu();
		if (vTag.equals("bind")) { // ͨ��tag���ж���ǰ��xml�����õ��ĸ��˵�
			GlobalVar appState = (GlobalVar) act.getApplicationContext();
			String usim = appState.getSimType();
			if("USIM".equals(usim)){
				new NetAuthenticateUSIM(act).startAuthenticate();
			}else{
				PviAlertDialog errorDialog =  new PviAlertDialog(act);
				errorDialog.setCanClose(true);
				errorDialog.setTimeout(5000);
				errorDialog.setTitle(act.getResources().getString(R.string.nosimorusim));
				errorDialog.setMessage(act.getResources().getString(R.string.unneedbeenblind));
				errorDialog.show();
			}
		}

		if (vTag.equals("copyright")) { // ͨ��tag���ж���ǰ��xml�����õ��ĸ��˵�
			Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
			Bundle bundleToSend = new Bundle();
			bundleToSend.putInt("type", 3);
			bundleToSend.putString("act",
			"com.pvi.ap.reader.activity.ShowAgreementActivity");
			bundleToSend.putString("pviapfStatusTip", "�����Ȩ����...");
			bundleToSend.putString("mainTitle", "��Ȩ����");
			tmpIntent.putExtras(bundleToSend);
			act.sendBroadcast(tmpIntent);
		}

		if (vTag.equals("privacy")) { // ͨ��tag���ж���ǰ��xml�����õ��ĸ��˵�
			Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
			Bundle bundleToSend = new Bundle();
			bundleToSend.putInt("type", 4);
			bundleToSend.putString("act",
			"com.pvi.ap.reader.activity.ShowAgreementActivity");
			bundleToSend.putString("pviapfStatusTip", "������˽����...");
			bundleToSend.putString("mainTitle", "��˽����");
			tmpIntent.putExtras(bundleToSend);
			act.sendBroadcast(tmpIntent);
		}

		if (vTag.equals("protocol")) { // ͨ��tag���ж���ǰ��xml�����õ��ĸ��˵�
			Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
			Bundle bundleToSend = new Bundle();
			bundleToSend.putInt("type", 2);
			bundleToSend.putString("act",
			"com.pvi.ap.reader.activity.ShowAgreementActivity");
			bundleToSend.putString("pviapfStatusTip", "�������Э��...");
			bundleToSend.putString("mainTitle", "����Э��");
			tmpIntent.putExtras(bundleToSend);
			act.sendBroadcast(tmpIntent);
		}
		if (vTag.equals("orderbook")) {
			Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);
			Bundle sndBundle1 = new Bundle();
			sndBundle1.putString("act",
			"com.pvi.ap.reader.activity.SubscribeProcess");
			sndBundle1.putString("subscribeMode", "feedback");
			intent1.putExtras(sndBundle1);
			act.sendBroadcast(intent1);
		}
		
		}
		return true;
	}

}
