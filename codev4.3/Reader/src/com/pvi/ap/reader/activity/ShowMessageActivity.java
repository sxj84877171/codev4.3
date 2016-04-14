package com.pvi.ap.reader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pvi.ap.reader.R;

/**
 * 
 * 显示消息
 * 
 * @author 马中庆 RD040
 * 
 */
public class ShowMessageActivity extends Activity {
	protected static final String LOG_TAG = "ShowMessageActivity";
	private Intent revIntent = null;
	private Bundle revBundle = null;

	private TextView tvFromUser;
	private TextView tvMessageContent;
	private TextView tvMessageTitle;
	private TextView tvTime;

	String fromuserName = " ";
	String content = " ";
	String time = " ";
	String title = " ";
	String type = "";
	String contentId = null;
	String messageID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.showmessage_ui1);
		// set UI display
		tvFromUser = (TextView) findViewById(R.id.fromuserName);
		tvMessageContent = (TextView) findViewById(R.id.messageContent);
		tvTime = (TextView) findViewById(R.id.time);
		tvMessageTitle = (TextView) findViewById(R.id.title);
		// recieve info

		revIntent = this.getIntent();
		if (revIntent != null) {
			revBundle = revIntent.getExtras();
			if (revBundle != null) {
				if (revBundle.getString("fromuserName") != null) {
					fromuserName = revBundle.getString("fromuserName");
				}
				if (revBundle.getString("content") != null) {
					content = revBundle.getString("content");
				}
				if (revBundle.getString("time") != null) {
					time = revBundle.getString("time");
				}
				if (revBundle.getString("title") != null) {
					title = revBundle.getString("title");
				}
				if (revBundle.getString("type") != null) {
					type = revBundle.getString("type");
				}
				if (revBundle.getString("contentId") != null) {
					contentId = revBundle.getString("contentId");
				}
				messageID = revBundle.getString("messageID");
			}
		}

		tvFromUser.setText(fromuserName);
		tvMessageContent.setText(content);
		tvTime.setText(time);
		tvMessageTitle.setText(title);

		Button b_return = (Button) findViewById(R.id.b_return);
		final String temp = contentId;
		b_return.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		Button b_goto = (Button) findViewById(R.id.b_goto);
		b_goto.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent sndintent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle sndBundle1 = new Bundle();
				sndBundle1.putString("act",
						"com.pvi.ap.reader.activity.BookSummaryActivity");
				sndBundle1.putString("startType", "allwaysCreate");
				sndBundle1.putString("haveTitleBar", "1");
				sndBundle1.putString("contentID", temp);
				sndintent.putExtras(sndBundle1);
				sendBroadcast(sndintent);
				finish();
			}
		});
		if ("2".equals(type)) {
			b_goto.setClickable(true);
			b_goto.setEnabled(true);
		} else {
			b_goto.setClickable(false);
			b_goto.setEnabled(false);
		}

		super.onCreate(savedInstanceState);
	}

}
