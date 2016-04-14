package com.pvi.ap.reader.activity.listener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.GlobalVar;
import com.pvi.ap.reader.activity.MainpageActivity;
import com.pvi.ap.reader.activity.NetAuthenticateUSIM;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;

public class MainPageClickListener implements OnClickListener {
	private PviActivity act;


	public MainPageClickListener(PviActivity act) {
		super();
		this.act = act;
	}

	@Override
	public void onClick(View v) {
		String vTag = "";
//		String subop = "";
		try
		{
			vTag = v.getTag().toString();
		}
		catch(Exception e)
		{
			vTag = "";
		}
		
		act.closePopmenu();
		if (vTag.equals("bind")) { // 通过tag来判断是前面xml中配置的哪个菜单
			GlobalVar appState = (GlobalVar) act.getApplicationContext();
			String usim = appState.getSimType();
			if(usim != null){usim = usim.toUpperCase();}
			if("USIM".equals(usim)){
				new NetAuthenticateUSIM(act).mainRun();
			}else{
				PviAlertDialog errorDialog =  new PviAlertDialog(act);
				errorDialog.setCanClose(true);
				errorDialog.setTimeout(5000);
				errorDialog.setTitle(act.getResources().getString(R.string.nosimorusim));
				if("SIM".equals(usim)){
					errorDialog.setMessage(act.getResources().getString(R.string.unneedbeenblind));
				}else{
					errorDialog.setMessage(act.getResources().getString(R.string.noconnectwork));
				}
				errorDialog.show();
			}
		}

		if (vTag.equals("copyright")) { // 通过tag来判断是前面xml中配置的哪个菜单
			Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
			Bundle bundleToSend = new Bundle();
			bundleToSend.putInt("type", 3);
			bundleToSend.putString("act",
			"com.pvi.ap.reader.activity.ShowAgreementActivity");
			bundleToSend.putString("pviapfStatusTip", "进入版权声明...");
			bundleToSend.putString("mainTitle", "版权声明");
			tmpIntent.putExtras(bundleToSend);
			act.sendBroadcast(tmpIntent);
		}

		if (vTag.equals("privacy")) { // 通过tag来判断是前面xml中配置的哪个菜单
			Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
			Bundle bundleToSend = new Bundle();
			bundleToSend.putInt("type", 4);
			bundleToSend.putString("act",
			"com.pvi.ap.reader.activity.ShowAgreementActivity");
			bundleToSend.putString("pviapfStatusTip", "进入隐私声明...");
			bundleToSend.putString("mainTitle", "隐私声明");
			tmpIntent.putExtras(bundleToSend);
			act.sendBroadcast(tmpIntent);
		}

		if (vTag.equals("protocol")) { // 通过tag来判断是前面xml中配置的哪个菜单
			Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
			Bundle bundleToSend = new Bundle();
			bundleToSend.putInt("type", 2);
			bundleToSend.putString("act",
			"com.pvi.ap.reader.activity.ShowAgreementActivity");
			bundleToSend.putString("pviapfStatusTip", "进入服务协议...");
			bundleToSend.putString("mainTitle", "服务协议");
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
}
