package com.pvi.ap.reader.activity;
import java.io.File;
import java.text.DecimalFormat;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Logger;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.StatFs;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 存储空间
 * @author rd034
 */

public class StorageStatActivity extends PviActivity {

	private final String LT = "StorageStatActivity";

	private LinearLayout tv_insideTotal = null;
	private TextView tv_insideUsed = null;
	private TextView tv_insideUnused = null;
	private LinearLayout tv_outsideTotal = null;
	private TextView tv_outsideUsed = null;
	private TextView tv_outsideUnused = null;

	private TextView tv_insideUsedT = null;
	private TextView tv_insideUnusedT = null;
	private TextView tv_outsideUsedT = null;
	private TextView tv_outsideUnusedT = null;

	private long insideTotal = 0;
	private long insideUsed = 0;
	private long insideUnused = 0;
	private long outsideTotal = 0;
	private long outsideUsed = 0;
	private long outsideUnused = 0;
	//private int ThemeNum = 1;
	private Handler mHandler;
	private ImageView image=null;
	private LinearLayout all=null;
	private int deviceType;
	private static boolean flag=false;//
	
	SDFileListener listener;
	//menu item
	private final BroadcastReceiver broadcastRec = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction()
					.equals(Intent.ACTION_MEDIA_MOUNTED)) {
				Toast.makeText(StorageStatActivity.this, R.string.sdcardin, Toast.LENGTH_LONG).show();
				//EPDRefresh.refreshGCOnceFlash();
				flag=false;
				onResume();
			}
			if (intent.getAction()
					.equals(Intent.ACTION_MEDIA_REMOVED)
					|| intent.getAction().equals(
							Intent.ACTION_MEDIA_UNMOUNTED)
					|| intent.getAction().equals(
							Intent.ACTION_MEDIA_BAD_REMOVAL))

			{
				Toast.makeText(StorageStatActivity.this, R.string.sdcardremoved, Toast.LENGTH_LONG).show();
				//EPDRefresh.refreshGCOnceFlash();
				flag=true;
				onResume();
			}
		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		if(deviceType==1){
//			this.getParent().getWindow().getDecorView().invalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
//		}
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);

		intentFilter.addDataScheme("file");
		registerReceiver(broadcastRec, intentFilter);// 注册监听函数
		Intent intent = new Intent(MainpageActivity.HIDE_TIP);
		sendBroadcast(intent);
		listener = new SDFileListener("/sdcard/MyMusic/1.mp3");
		listener.startWatching();
		mHandler = new Handler();
		setData();

		image=(ImageView)findViewById(R.id.image);
		image.setFocusable(true);
		image.setFocusableInTouchMode(true);
		image.requestFocus();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		GlobalVar appState = ((GlobalVar) getApplicationContext());
		deviceType=appState.deviceType;
		setContentView(R.layout.storagestatstyle2);
		super.onCreate(savedInstanceState);

	}


	private void setData() {
		new Thread() {
			public void run() {
				mHandler.post(doSetData);
			}
		}.start();
	}

	private Runnable doSetData = new Runnable() {
		@Override
		public void run() {
			insideTotal = getTotalInternalMemorySize();
			insideUnused = getAvailableInternalMemorySize();
			insideUsed = insideTotal-insideUnused;

			tv_insideUsedT = (TextView)findViewById(R.id.inside_used_text);
			tv_insideUnusedT = (TextView)findViewById(R.id.inside_unused_text);
			//tv_insideUsed = (TextView)findViewById(R.id.inside_used);
			tv_insideUnused = (TextView)findViewById(R.id.inside_unused);
			tv_insideTotal = (LinearLayout)findViewById(R.id.inside_total);
			if(deviceType==1){
				all=(LinearLayout)findViewById(R.id.mainblock);
				//all.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
//				    			tv_insideUsedT.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//				    			tv_insideUnusedT.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//				    			tv_insideUnused.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//				    			tv_insideTotal.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT |View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);

			}

			String[] temp = fileSize(insideUsed);
			tv_insideUsedT.setText(temp[0]+temp[1]);
			temp = fileSize(insideUnused);
			tv_insideUnusedT.setText(temp[0]+temp[1]);

			int itw = tv_insideTotal.getLayoutParams().width;
			int w = (int)(itw*( (double)insideUnused/insideTotal ));
			//Logger.v("in",w);
			//			 if(w<=0){
			//            	 tv_insideUnused.setBackgroundResource(R.drawable.strorage_size);
			//             }else{
			//            	 tv_insideUnused.setBackgroundResource(R.drawable.strorage_white);
			//             }
			LayoutParams lp = tv_insideUnused.getLayoutParams();
			lp.width = w;
			tv_insideUnused.setLayoutParams(lp);

			//outside

			outsideTotal = getTotalExternalMemorySize();
			outsideUnused = getAvailableExternalMemorySize();
			outsideUsed = outsideTotal-outsideUnused;

			tv_outsideUsedT = (TextView)findViewById(R.id.outside_used_text);
			tv_outsideUnusedT = (TextView)findViewById(R.id.outside_unused_text);
			tv_outsideUnused = (TextView)findViewById(R.id.outside_unused);
			tv_outsideTotal = (LinearLayout)findViewById(R.id.outside_total);


			temp = fileSize(outsideUsed);
			tv_outsideUsedT.setText(temp[0]+temp[1]);
			temp = fileSize(outsideUnused);
			tv_outsideUnusedT.setText(temp[0]+temp[1]);

			int otw = tv_outsideTotal.getLayoutParams().width;
			w = (int)(otw*( (double)outsideUnused/outsideTotal ));
			//Logger.v("sd",w);
			//			 if(w<=0){
			//				 tv_outsideUnused.setBackgroundResource(R.drawable.strorage_size);
			//             }else{
			//            	 tv_outsideUnused.setBackgroundResource(R.drawable.strorage_white);
			//             }
			LayoutParams lp2 = tv_outsideUnused.getLayoutParams();
			if(flag){
				w=450;
			}
			lp2.width = w;
			tv_outsideUnused.setLayoutParams(lp2);	
			Intent tmpIntent = new Intent(MainpageActivity.SHOW_ME);
			Bundle bundleToSend = new Bundle();
			bundleToSend.putString("sender", StorageStatActivity.this.getClass().getName()); //TAB内嵌activity类的全名
			tmpIntent.putExtras(bundleToSend);
			sendBroadcast(tmpIntent);
			tmpIntent = null;
			bundleToSend = null;
			//Logger.v("aa","aa");
		}
	};



	public static long getTotalInternalMemorySize() {   
		File path = Environment.getDataDirectory();   
		StatFs stat = new StatFs(path.getPath());   
		long blockSize = stat.getBlockSize();   
		long totalBlocks = stat.getBlockCount();   
		return totalBlocks * blockSize;   
	}   

	public static long getAvailableInternalMemorySize() {   
		File path = Environment.getDataDirectory();   
		StatFs stat = new StatFs(path.getPath());   
		long blockSize = stat.getBlockSize();   
		long availableBlocks = stat.getAvailableBlocks();   
		return availableBlocks * blockSize;   
	}   

	public static long getAvailableExternalMemorySize() {   
		long availableExternalMemorySize = 0;   
		if (Environment.getExternalStorageState().equals(   
				Environment.MEDIA_MOUNTED)) {   
			File path = Environment.getExternalStorageDirectory();   
			StatFs stat = new StatFs(path.getPath());   
			long blockSize = stat.getBlockSize();   
			long availableBlocks = stat.getAvailableBlocks();   
			availableExternalMemorySize = availableBlocks * blockSize;   
		}else if (Environment.getExternalStorageState().equals(   
				Environment.MEDIA_REMOVED)
				||Environment.getExternalStorageState().equals(Environment.MEDIA_BAD_REMOVAL)
				||Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTABLE)
				||Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {   
			availableExternalMemorySize = 0; 
			flag=true;
		}   

		return availableExternalMemorySize;   
	}   

	public static long getTotalExternalMemorySize() {   
		long totalExternalMemorySize = 0;   
		if (Environment.getExternalStorageState().equals(   
				Environment.MEDIA_MOUNTED)) {   
			File path = Environment.getExternalStorageDirectory();   
			StatFs stat = new StatFs(path.getPath());   
			long blockSize = stat.getBlockSize();   
			long totalBlocks = stat.getBlockCount();   
			totalExternalMemorySize = totalBlocks * blockSize;   
		} else if (Environment.getExternalStorageState().equals(   
				Environment.MEDIA_REMOVED)
				||Environment.getExternalStorageState().equals(Environment.MEDIA_BAD_REMOVAL)
				||Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTABLE)
				||Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {   
			totalExternalMemorySize = 0;
			flag=true;
		}   


		return totalExternalMemorySize;   
	}   

	/* 返回为字符串数组[0]为大小[1]为单位KB或MB */   
	private String[] fileSize(long size) {   
		String str = "";  
		if(size==0){
			str = "MB";
		}else if(size>0&&size<1024){
			str = "B";
		}else if (size >= 1024) {   
			str = "KB";   
			size /= 1024;   
			if (size >= 1024) {   
				str = "MB";   
				size /= 1024;   
			}   
		}   
		DecimalFormat formatter = new DecimalFormat();   
		/* 每3个数字用,分隔如：1,000 */   
		formatter.setGroupingSize(3);   
		String result[] = new String[2];   
		result[0] = formatter.format(size);   
		result[1] = str;   
		return result;   
	}   


	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {

            String vTag = item.id; 
            if(vTag.equals("bookshelf")){
                //我的书架
                final Intent tmpintent = new Intent(MainpageActivity.START_ACTIVITY);
                Bundle sndbundle = new Bundle();
                sndbundle.putString("act","com.pvi.ap.reader.activity.MyBookshelfActivity");

                tmpintent.putExtras(sndbundle);
                sendBroadcast(tmpintent);
            }
            closePopmenu(); 
        
        }};



		public OnUiItemClickListener getMenuclick() {
			return this.menuclick;
		}
		@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			listener.stopWatching();
			if (broadcastRec != null) {
				unregisterReceiver(broadcastRec);// 使用完注销广播监听函数
			}
			super.onPause();
			flag=false;
		}

		public class SDFileListener extends FileObserver {

			public SDFileListener(String path) {
				super(path);
				// TODO Auto-generated constructor stub
			}

			@Override
			public void onEvent(int event, String path) {
				// TODO Auto-generated method stub
				switch (event)
				{
				case FileObserver.ALL_EVENTS:
					//Logger.i("Path",event + path);
					break;
				case FileObserver.DELETE:
					//Logger.i("Path",event + path);
					break;
				case FileObserver.DELETE_SELF:
					//Logger.i("Path",event + path);
					break;
				}
			}
		}
}
