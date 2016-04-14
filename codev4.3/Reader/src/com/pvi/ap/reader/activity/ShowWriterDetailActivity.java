package com.pvi.ap.reader.activity;

import com.pvi.ap.reader.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * œ‘ æœ˚œ¢
 * 
 * @author kizan
 * 
 */
public class ShowWriterDetailActivity extends Activity {
	protected static final String LOG_TAG = "ShowWriterDetailActivity";
	private Intent revIntent = null;
	private Bundle revBundle = null;

	private TextView tvDetailContent;
	private TextView tvAuthorName;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.show_writer_info_ui1);

		String detailContent = " ";

		String authorName = " ";

		// set UI display

		tvDetailContent = (TextView) findViewById(R.id.authorDetails);

		tvAuthorName = (TextView) findViewById(R.id.authorName);

		// recieve info

		revIntent = this.getIntent();
		if (revIntent != null) {
			revBundle = revIntent.getExtras();
			if (revBundle != null) {

				if (revBundle.getString("authorDetails") != null) {
					detailContent = revBundle.getString("authorDetails");
				}
				if (revBundle.getString("authorName") != null) {
					authorName = revBundle.getString("authorName");
				}
			}
		}

		tvDetailContent.setText(detailContent);
		tvAuthorName.setText(authorName);

		Button b_return = (Button) findViewById(R.id.b_return);

		b_return.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		super.onCreate(savedInstanceState);
	}

}
