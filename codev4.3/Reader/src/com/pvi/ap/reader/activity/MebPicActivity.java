package com.pvi.ap.reader.activity;


import java.lang.ref.WeakReference;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.external.meb.MebInterface;
import com.pvi.ap.reader.external.meb.MebInterfaceImpl;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * 封面页
 * @author rd037
 *
 */
public class MebPicActivity extends Activity {
	private ImageView imageView=null;
	private String filePath;
	private String certPath;
	private String contentID;
	Bundle bundle;
	WeakReference<Bitmap> bitmap;


	public void showMe(){
		Intent tmpIntent = new Intent(
                MainpageActivity.SHOW_ME);
        Bundle bundleToSend = new Bundle();
      
        bundleToSend.putString("sender", MebPicActivity.this.getClass().getName()); //TAB内嵌activity类的全名
        tmpIntent.putExtras(bundleToSend);
        sendBroadcast(tmpIntent);
        tmpIntent = null;
        bundleToSend = null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mebpic);
		imageView=(ImageView)findViewById(R.id.meb_showPic);
		imageView.requestFocus();
		bundle = this.getIntent().getExtras();
		filePath=bundle.getString("FilePath");
		certPath=bundle.getString("CertPath");
		contentID=bundle.getString("ContentId");

		MebInterface mebInterfaceImpl=new MebInterfaceImpl();
		
		try {
			
			byte[] mebbit =mebInterfaceImpl.getCover(filePath);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inDither=true;
//			if(mebbit.length>120*1024){
//				opts.inSampleSize=8;
//			}else if(mebbit.length>100*1024&&mebbit.length<=120*1024){
//				opts.inSampleSize = 6;
//			}else if(mebbit.length>80*1024&&mebbit.length<=100*1024){
//				opts.inSampleSize = 5;
//			}else  if(mebbit.length>60*1024&&mebbit.length<=80*1024){
//				opts.inSampleSize = 4;
//			}else if(mebbit.length>40*1024&&mebbit.length<=60*1024){
//				opts.inSampleSize = 3;
//			}else if(mebbit.length>20*1024&&mebbit.length<=40*1024){
//				opts.inSampleSize = 2;
//			}else {
//				opts.inSampleSize=1;
//			}
			bitmap = new WeakReference<Bitmap>(BitmapFactory.decodeByteArray(mebbit, 0, mebbit.length,opts));
			showMe();
		}catch (OutOfMemoryError e) {
			// TODO: handle exception
			Logger.e("exception",e.toString());
			// TODO: handle exception
			Toast.makeText(MebPicActivity.this, getResources().getString(R.string.deadException), Toast.LENGTH_LONG).show();
			Intent intent1 = new Intent(
					MainpageActivity.BACK);
			sendBroadcast(intent1);
			return ;
		} 
		catch (Exception e) {
			Logger.e("exception",e.toString());
			// TODO: handle exception
			Toast.makeText(MebPicActivity.this, getResources().getString(R.string.deadException), Toast.LENGTH_LONG).show();
			Intent intent1 = new Intent(
					MainpageActivity.BACK);
			sendBroadcast(intent1);
			return ;
		}

		imageView.setImageBitmap(bitmap.get());

		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goto_table_of_contents();
			}
		});
	}
	public void goto_table_of_contents() {

		Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
		Bundle sndbundle = new Bundle();
		sndbundle.putString("pviapfStatusTip", "进入书籍目录");
		msgIntent.putExtras(sndbundle);
		sendBroadcast(msgIntent);
		Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle sndbundle1 = new Bundle();
		sndbundle1.putString("act","com.pvi.ap.reader.activity.ListFileActivity");
		sndbundle1.putString("haveStatusBar","1");
		sndbundle1.putString("FilePath",filePath);
		sndbundle1.putString("CertPath", certPath);
		sndbundle1.putString("contentID", contentID);
		//bundle.putString("startType","allwaysCreate");
		intent.putExtras(sndbundle1);
		sendBroadcast(intent);
		
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 通知框架返回上一个子activty
			sendBroadcast(new Intent(MainpageActivity.BACK));
		}else if(keyCode == event.KEYCODE_DPAD_RIGHT) {
			goto_table_of_contents();
		}
		return super.onKeyUp(keyCode, event);
	}

	
}
