package com.pvi.ap.reader.activity;

import com.pvi.ap.reader.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MyTicketInfoActivity extends Activity {
	
	private TextView welcome = null;
	private TextView ticketinfo = null;
	private Button okbtn = null;
	
	private Intent revintent = null;
	private Bundle revbundle = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ticketinfo);
		welcome = (TextView)this.findViewById(R.id.welcome);
		this.ticketinfo = (TextView)this.findViewById(R.id.ticketinfo);
		this.okbtn = (Button)this.findViewById(R.id.okbtn);
		
		revintent = this.getIntent();
		this.revbundle = revintent.getExtras();
		
		String nickname = "";
		String infostr = "";
		
		nickname = this.revbundle.getString("NickName");
		if(nickname != null&&(!nickname.equals("")))
		{
			this.welcome.setText(nickname + " ÄãºÃ£º");
		}
		infostr = this.revbundle.getString("TicketInfo");
		this.ticketinfo.setText(infostr);
		
		this.okbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				finish();
				sendBroadcast(new Intent(MainpageActivity.BACK));
				return;
			}
		});
	}
}
