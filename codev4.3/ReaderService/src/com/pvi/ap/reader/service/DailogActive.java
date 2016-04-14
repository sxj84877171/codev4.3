package com.pvi.ap.reader.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * System upgrade display dialog box class<br>
 * <p>Generally, use the onCreate openDailog method
 * 
 * <p>Generally, use the class DailogActive to show dialog box
 * @version 1.0
 * @since 2010-9-15
 * @author ËïÏò½õ
 *
 */
public class DailogActive extends Activity {
	public static final String messageFlag = "positiveNegative" ;//positive negative information

	private String path = null ;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	/**
	 * @param savedInstanceState 
	 * savedInstanceState
	 * @exception no exception
	 * @return no return
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String message = bundle.getString("message");
		String title = bundle.getString("title");
		path = bundle.getString("path");
		if(title == null || "".equals(title.trim())){
			title = getString(R.string.system_soft_update);
		}
		if(message == null || "".equals(message.trim())){
			message = getString(R.string.system_soft_update_message);
		}
		openDailog(title,message);
	}
	/**
	 * Popup whether update dialog
	 * @param
	 * @exception no exception
	 * @return no return
	 */
	public void openDailog(String title,String message){
		Dialog dialog = new AlertDialog.Builder(DailogActive.this)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(R.string.system_soft_unauthorized,
						new DialogInterface.OnClickListener() {
						
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String message = "ok" ;
								postiveToMethod(message);
								dialog.dismiss();
								finish();
							}
						}).setNegativeButton(R.string.system_soft_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss();
								finish();
							}
						}).create();
		dialog.show();
	}
	private void postiveToMethod(String message) {
		Intent myIntent = new Intent(SystemUpdateService.DAILOGACTIVENAME);
		Bundle bundle = new Bundle();
		bundle.putString("path", path);
		bundle.putString("message", message);
		myIntent.putExtras(bundle);
		sendBroadcast(myIntent);
	}
}
