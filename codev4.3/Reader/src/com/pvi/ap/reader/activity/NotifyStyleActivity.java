package com.pvi.ap.reader.activity;

import com.pvi.ap.reader.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class NotifyStyleActivity extends Activity{
	private TextView info;
	private TextView[] style = new TextView[3];
	private TextView result;
	private String[] type = new String[3];
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.setContentView(R.layout.notifystyle);
		info = (TextView) findViewById(R.id.text01);
		style[0] = (TextView) findViewById(R.id.text02);
		style[1] = (TextView) findViewById(R.id.text03);
		style[2] = (TextView) findViewById(R.id.text04);
		result = (TextView) findViewById(R.id.result);
		type[0] = "1";
		type[1] = "2";
		type[2] = "3";
		
		for(int i=1;i<3;i++){
			final int tmp = i;
			style[tmp].setOnClickListener(new OnClickListener() {
				public void onClick(final View v) {
					SubscribeProcess.network("bookUpdateSet",type[tmp],null,null,null);
					result.setText("ÉèÖÃÔ¤¶©³É¹¦");
					result.setVisibility(View.VISIBLE);
			           }
			       });
		}
		result.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				finish();
		        }
		 });
		
		
		
	}
	
}
