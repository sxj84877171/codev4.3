package com.pvi.ap.reader.activity;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import com.pvi.ap.reader.activity.pviappframe.PviPopupWindow;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviMenuItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiUtil;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.OnBackListener;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.common.QuitSortComparator;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.*;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * 本地音乐
 * 
 * @author rd034 
 * @since 2011-04-02 @author 刘剑雄
 */
public class MyMusicActivity extends PviActivity implements Pageable{
    
	private final static String TAG="MyMusicActivity";
	public static final String service = "com.pvi.ap.reader.service.BackGroundMusicService";
	private static String bkaction = "com.pvi.reader.activity.MyMusicActivity.listener";
	private boolean isPause = false;
	private boolean isStop = false;
	private boolean isPlaying = false;
	private int currentPage = 1;
	private int itemPerPage = 6;
	private int totalPage = 0;
	private int ret=0;
	ArrayList<HashMap<String, Object>> musicInfo = new ArrayList<HashMap<String, Object>>();
	private boolean isBackgrounMusic = false;
	private String DirPath = "";
	//private int ThemeNum = 1;
	private String searchkey = "";
	private int selIndex = -1;
	private QuitSortComparator sortUtil = new QuitSortComparator("IsDir","Name","time");
	private boolean IsSearch = false;
    private TextView curmusicname = null;
	private TextView musicprocess = null;
	private SeekBar curplaypos = null;
    private ImageButton premusic = null;
	private ImageButton pause = null;
	private ImageButton stop = null;
	private ImageButton nextmusic = null;
    private TextView[] soundlevel = new TextView[8];
    private Handler mhandler = null;
    private boolean isChanging = false;// 互斥变量，防止定时器与SeekBar拖动时进度冲突
	private String musicnameplaying = "";
	private int musicindexplaying = 0;
    private boolean isbackgroundon = false;
    private LinearLayout alertpage = null;
	private LinearLayout filelist = null;
	private TextView alertmsg = null;
	private TextView tishi = null;
	ArrayList<String> musiclist = new ArrayList<String>();//背景音乐列表
	ArrayList<String> musicplays = new ArrayList<String>();//我的音乐当前播放列表
	private int isoundlevel = 0;
	private ImageButton soundsub = null;
	private ImageButton soundadd = null;
	private boolean pagechange = false;
	private boolean sdnofile = false;
	private String bkstatus = "";
	private String curmusicfile = "";
	private int curmusicprocess = 0;
	private int curmusiclen = 0;
	private ImageView closeButton;
	private RelativeLayout playmuiscLayout;
	private Button returnback=null;
	private RelativeLayout all=null;
	private int deviceType;
	private int orderType = 1;
	private boolean refresh=false;
	private int id=0;
	PviDataList listView;               //view实例
    ArrayList<PviUiItem[]> list;  
    private Context mContext = MyMusicActivity.this;
	/**
	 * 接收音乐播放状态的广播
	 */
	private final BroadcastReceiver palyMusic=new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction()
					.equals(bkaction))
			{
				if (intent.getExtras().containsKey("status")) {
					String status = intent.getExtras().getString("status");
					if (status.equals("play")) {
						isPlaying = true;
						isPause = false;
						isStop = false;
						bkstatus = "play";
					}
					else {
						bkstatus = "pause";
						isPlaying = false;
						isPause = true;
						isStop = false;
						pause.setImageResource(R.drawable.play);
					} 
				}else
				{
					
					curmusicfile = intent.getExtras().getString("file");
					Logger.v("curmusicfile", "curmusicfile="+curmusicfile);
					curmusicprocess = intent.getExtras().getInt("position");
					curmusiclen = intent.getExtras().getInt("duration");
					musicindexplaying = musicplays.indexOf(curmusicfile);
					curplaypos.setMax(curmusiclen);
					curplaypos.setProgress((int) curmusicprocess);
					musicprocess.setText(ConventTime(curmusicprocess));
					curmusicname.setText(getbkmusicname(curmusicfile));
					
//					for(int i=0;i<itemPerPage;i++){
//						if(musicname[i].getText().toString().equals(getbkmusicname(curmusicfile))){
//							Logger.v("i", "i="+i);
//							//selIndex=i;
//							musiclayout[i].requestFocus();
//						}
//					}
				}
				
			}
		}
		
	};
	/**
	 * 监测sdcard的广播
	 */
	private final BroadcastReceiver broadcastRec = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			Logger.d("mymusic","onReceive(Context arg0, Intent intent  action:"+intent.getAction());
			// TODO Auto-generated method stub
			final GlobalVar app = (GlobalVar) getApplicationContext();
			if (intent.getAction()
					.equals(Intent.ACTION_MEDIA_MOUNTED)) {

				getBKMusicList();
				int ret = getMusicInfo(DirPath, searchkey);
				refresh=true;
				id++;
				playmuiscLayout.setVisibility(View.GONE);
				showPager();
				setValue(ret);
				//musiclayout[0].requestFocus();
				sdnofile = false;
				listView.requestFocus();
				showme();
			}
			if (intent.getAction()
					.equals(Intent.ACTION_MEDIA_REMOVED)
					|| intent.getAction().equals(
							Intent.ACTION_MEDIA_UNMOUNTED)
					|| intent.getAction().equals(
							Intent.ACTION_MEDIA_BAD_REMOVAL))

			{   Logger.d("mymusic","sdcard removed");
			   
				alertpage.setVisibility(View.VISIBLE);
				filelist.setVisibility(View.GONE);
				alertmsg.setText("SD Card不存在！相关资料无法获取...");
				tishi.setVisibility(View.GONE);
				returnback.requestFocus();
				hidePager();
				sdnofile = true;
				return;
			}
			
		}
	};

	private View.OnClickListener musicsound = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int res = v.getId();
			switch(res)
			{
			case R.id.soundlevel0:
				isoundlevel = 0;
				break;
			case R.id.soundlevel1:
				isoundlevel = 1;
				break;
			case R.id.soundlevel2:
				isoundlevel = 2;
				break;
			case R.id.soundlevel3:
				isoundlevel = 3;
				break;
//			case R.id.soundlevel4:
//				isoundlevel = 4;
//				break;
//			case R.id.soundlevel5:
//				isoundlevel = 5;
//				break;
//			case R.id.soundlevel6:
//				isoundlevel = 6;
//				break;
//			case R.id.soundlevel7:
//				isoundlevel = 7;
//				break;
			default:
				break;
			}
			setVolume();
			Intent sndintent = new Intent(MainpageActivity.VOLUME_CHANGED);
			sendBroadcast(sndintent);
		}
	};
	protected void onResume() {
		// TODO Auto-generated method stub
		
		showTip("进入我的音乐，请稍候...",2000);
		
		super.onResume();
		
		
		Logger.v(TAG, "onResume");
//		try {
//			init_screenoff = Settings.System.getInt(this.getContentResolver(),
//					Settings.System.SCREEN_OFF_TIMEOUT);
//			Settings.System.putInt(this.getContentResolver(),
//					Settings.System.SCREEN_OFF_TIMEOUT, -1);
//		} catch (Exception e) {
//			Log.i("FSDS", e.toString());
//			Toast.makeText(this, "Set Screenoff Exception:" + e.toString(),
//					Toast.LENGTH_LONG).show();
//		}
		long TimeStart = System.currentTimeMillis();

		Logger.i("Time", "MyMusicActivity" + Long.toString(TimeStart));
		
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		intentFilter.addDataScheme("file");
		//intentFilter.addAction(bkaction);
		registerReceiver(broadcastRec,intentFilter);// 注册监听函数
		IntentFilter in = new IntentFilter();
		in.addAction(bkaction);
		registerReceiver(palyMusic,in);// 注册播放音乐监听函数
		float m_iVolume = 0;
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
		int i=mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
		mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
		m_iVolume = (float)2*max/3;
		m_iVolume =i;
		Logger.i("Reader", "m_iVolume : " + m_iVolume);
		if (m_iVolume < 0) 
			m_iVolume = 0;
		else if (m_iVolume > max) 
			m_iVolume = max;
		//		mplayer.setVolume(m_iVolume, m_iVolume);	

		boolean getsoundlevel = true;
		for(int i1 = 3; i1 >= 1; i1--)
		{
			if(m_iVolume >=(max/3)*i1)
			{
				if(getsoundlevel)
				{
					isoundlevel = i1;
					getsoundlevel = false;
				}

				soundlevel[i1].setBackgroundResource(R.drawable.musicsound2);
			}
			else
			{
				soundlevel[i1].setBackgroundResource(R.drawable.musicsound3);
			}
		}

		if(IsBackgroundMusicOn())
		{
			Intent intent = new Intent(service);
			intent.putExtra("operate", "IsPause");
			intent.putExtra("tracer", bkaction);
			sendBroadcast(intent); 
		}

		mPrefs = getSharedPreferences("curmymusicstatus", MODE_PRIVATE);
		curmusicname.setText(mPrefs.getString("FileName", ""));
		curplaypos.setMax(mPrefs.getInt("TotalPos", 100));
		curplaypos.setProgress(mPrefs.getInt("PlayPos", 0));
		musicprocess.setText(mPrefs.getString("CurTime", "0:00"));
		File file = new File(mPrefs.getString("FilePath", "/sdcard/"));
		String filepath = mPrefs.getString("FilePath", "/sdcard/");
		String path = filepath.substring(0, filepath.lastIndexOf("/"));
		HashMap <String, Object> temp = null;

		getBKMusicList();
		
		 ret = getMusicInfo(DirPath, searchkey);

		setValue(ret);
		if(path.contentEquals(Config.getString("MyDocPath") + DirPath ))
		{
			for(int i1 = 0; i1<musicInfo.size();i1++)
			{
				temp = musicInfo.get(i1);
				if(temp.get("FilePath").toString().equals(filepath))
				{
					this.musicindexplaying = i1;
					break;
				}
			}
		}
		if(file.exists()&&file.isFile())
		{
			this.isPause=true;
			this.isPlaying=false;
			this.isStop=false;
			stop.setImageResource(R.drawable.stop_mymusic);
			pause.setImageResource(R.drawable.play_mymusic);
			this.playmusic(mPrefs.getString("FilePath", "/sdcard/"), mPrefs.getInt("PlayPos", 0));
		}

//		if(isPlaying){
//			playmuiscLayout.setVisibility(View.VISIBLE);
//		}else{
//			playmuiscLayout.setVisibility(View.GONE);
//		}
		/**
	      * 重写状态栏返回
	      */
		((GlobalVar) getApplicationContext()).pbb.setOnBackListener(new OnBackListener(){

			@Override
			public boolean onBack() {
				// TODO Auto-generated method stub
				if(sdnofile){
					sendBroadcast(new Intent(MainpageActivity.BACK));	
					return true;
				}
				return retBackTo();
			}
			 
		 });
		this.setOnPmShow(new OnPmShowListener(){

            @Override
            public void OnPmShow(PviPopupWindow popmenu) {               
              //设置排序子菜单的焦点与orderType一致                
                if(orderType==1){
                    final PviMenuItem vSortByTime = getMenuItem("filecompositor");
                    if(vSortByTime!=null){
                        vSortByTime.isFocuse = true;
                    }                    
                }else if(orderType==2){
                    final PviMenuItem vSortByBook = getMenuItem("timecompositor");
                    if(vSortByBook!=null){
                        vSortByBook.isFocuse = true;
                    }
                }     
                
            }});
		showme();
	}
	
	private void setValue(int ret) {

		if(orderType==1){
			sortUtil = new QuitSortComparator("IsDir","Name","time");
			sortUtil.setDescend2(false);	
			
			}else{
				sortUtil = new QuitSortComparator("IsDir","time","Name");
				sortUtil.setDescend2(true);
			}
		Collections.sort(musicInfo,sortUtil);
		if((ret != 0)||(musicInfo.size()==0))
		{
			selIndex = -1;
			
			if(ret == 1)
			{
				alertpage.setVisibility(View.VISIBLE);
				filelist.setVisibility(View.GONE);
				tishi.setVisibility(View.GONE);
				alertmsg.setText("SD Card不存在！相关资料无法获取...");
				hidePager();
			}
			else
			{
				alertpage.setVisibility(View.GONE);
				filelist.setVisibility(View.GONE);
				tishi.setVisibility(View.VISIBLE);
				if(!IsSearch)
				{   
					tishi.setText("没有相关音乐记录");
					tishi.setFocusable(true);
					tishi.setFocusableInTouchMode(true);
					tishi.requestFocus();
					tishi.setOnKeyListener(onKeyListener);
				}
				else
				{ 
					tishi.setText("没有搜索到符合条件的音乐");
					//searchkey = "";
					IsSearch = false;
					tishi.setFocusable(true);
					tishi.setFocusableInTouchMode(true);
					tishi.requestFocus();
					tishi.setOnKeyListener(onKeyListener);
				}
				this.updatePagerinfo("1 / 1");
			}
			return;
		}
		
		tishi.setVisibility(View.GONE);
		alertpage.setVisibility(View.GONE);
		filelist.setVisibility(View.VISIBLE);
		this.totalPage = (int) Math.ceil((double) musicInfo.size()
				/ this.itemPerPage);
		if(totalPage == 0)
		{
			totalPage = 1;
		}
		if(this.totalPage < this.currentPage)
		{
			this.currentPage=this.totalPage;
		}
		
		HashMap<String, Object> map = null;
		int len = (this.currentPage < this.totalPage ? this.itemPerPage
				: (musicInfo.size() - (this.currentPage - 1)* this.itemPerPage));
		list.clear();
		for (int i = 0; i < itemPerPage; i++) {
			if(i < len)
			{   
				PviUiItem[] items = new PviUiItem[]{
		                new PviUiItem("icon"+i, R.drawable.folder2, 10, 10, 50, 50, null, false, true, null),
		                new PviUiItem("text1"+i, 0, 100, 30, 400, 50, "", false, true, null),
		                new PviUiItem("icon"+i, R.drawable.playbtn, 510, 30, 29, 29, null, false, true, null),
		        };
				map = musicInfo.get((this.currentPage - 1) * this.itemPerPage + i);
			
				if(map.get("IsDir").toString().equals("true"))
				{
					   
					 items[0].res=R.drawable.folder2;
					 items[2].res=0;
					
				}
				else
				{
		             String url=map.get("FilePath").toString();
					   if(compareUrl(url)){
						   items[0].res=R.drawable.backmusic;
						   items[2].res= R.drawable.playbtn;
							 
						 }else{
							   items[0].res=R.drawable.mymusic;
							   items[2].res= R.drawable.playbtn;
							
						   }
					
				
				}
				items[1].text=map.get("Name").toString();
				items[1].textSize=22;
				final int ii=i;
				 listView.setOnRowClick(new PviDataList.OnRowClickListener() {
						
						@Override
						public void OnRowClick(View v, int rowIndex) {
							// TODO Auto-generated method stub
							 setEvent(ii);
						}
					});
//				OnClickListener l = new OnClickListener(){
//
//                    @Override
//                    public void onClick(View arg0) {
//                        // TODO Auto-generated method stub
//                        setEvent(ii);
//                    }
//                    
//                };
//                items[1].l = l;
//                items[2].l = l;
				
                list.add(items);
               

			}
			
			 listView.setData(list);
		}
		
		if(deviceType==1){
		    if(id==2){
		     id=0;	
//			this.getParent().getWindow().getDecorView().getRootView().postInvalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
		    }
		    }
		final GlobalVar app = ((GlobalVar) getApplicationContext());        
        updatePagerinfo(currentPage+" / "+this.totalPage);
        //app.pbb.updateDraw();
        showPager();
        Logger.v(TAG, "setValue");
	}
    public boolean compareUrl(String s){
    	boolean flag=false;
    	int length=musiclist.size();
    	for(int i=0;i<length;i++){
    		if(musiclist.get(i).equals(s)){
    			flag=true;
    		}
    	}
    	return flag;
    }
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub

		super.onStart();
		

	}



	

	//private int init_screenoff = 0; // used to set LCD on when test and set LCD
	// OFF when test end

	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		GlobalVar appState = ((GlobalVar) getApplicationContext());
		
		deviceType=appState.deviceType;
		this.setContentView(R.layout.mymusic);
		Logger.v(TAG, "onCreate");
		listView= (PviDataList)findViewById(R.id.list);
		list = new ArrayList<PviUiItem[]>();
		//翻页处理
		//this.showPager=true;
//		final GlobalVar app = ((GlobalVar) getApplicationContext());
//		app.pbb.setPageable(this);
//		app.pbb.setItemVisible("prevpage", true);
//		app.pbb.setItemVisible("pagerinfo", true);
//		app.pbb.setItemVisible("nextpage", true); 
		mhandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if (msg.what == 1) {

					if (isChanging) {
						mhandler.sendEmptyMessageDelayed(1, 1000);
						return;
					} 

				}
				else if (msg.what == 2)
				{
					if(selIndex%itemPerPage > 0)
					{
						//musiclayout[selIndex%itemPerPage].performClick();
					}
				}
			}

		};
		

		this.curmusicname = (TextView) this.findViewById(R.id.curmusicname);
		this.curplaypos = (SeekBar) this.findViewById(R.id.curplaypos);
		this.musicprocess = (TextView) this.findViewById(R.id.musicprocess);
		this.premusic = (ImageButton) this.findViewById(R.id.premusic);
		this.pause = (ImageButton) this.findViewById(R.id.pause);
		this.stop = (ImageButton) this.findViewById(R.id.stop);
		this.nextmusic = (ImageButton) this.findViewById(R.id.nextmusic);
        this.soundlevel[0] = (TextView) this.findViewById(R.id.soundlevel0);
		this.soundlevel[1] = (TextView) this.findViewById(R.id.soundlevel1);
		this.soundlevel[2] = (TextView) this.findViewById(R.id.soundlevel2);
		this.soundlevel[3] = (TextView) this.findViewById(R.id.soundlevel3);
		this.soundlevel[4] = (TextView) this.findViewById(R.id.soundlevel4);
		this.soundlevel[5] = (TextView) this.findViewById(R.id.soundlevel5);
		this.soundlevel[6] = (TextView) this.findViewById(R.id.soundlevel6);
		this.soundlevel[7] = (TextView) this.findViewById(R.id.soundlevel7);
		
		alertpage = (LinearLayout) this.findViewById(R.id.alertpage);
		filelist = (LinearLayout) this.findViewById(R.id.filelist);
		returnback = (Button) this.findViewById(R.id.returnback);
		alertmsg = (TextView) this.findViewById(R.id.alertmsg);
		tishi = (TextView) this.findViewById(R.id.tishi);
		soundsub = (ImageButton) this.findViewById(R.id.soundsub);
		soundadd = (ImageButton) this.findViewById(R.id.soundadd);
		playmuiscLayout=(RelativeLayout)findViewById(R.id.playmusic);
		closeButton=(ImageView)findViewById(R.id.closemusisc);
		closeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playmuiscLayout.setVisibility(View.GONE);
			}
		});
		soundadd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isoundlevel < 3)
				{
					isoundlevel = isoundlevel + 1;
					setVolume();
					Intent sndintent = new Intent(MainpageActivity.VOLUME_CHANGED);
					sendBroadcast(sndintent);
				}
				else
				{
					isoundlevel = 3;
					return;
				}
			}
		});
		soundsub.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(isoundlevel > 0)
				{
					isoundlevel = isoundlevel - 1;
					setVolume();
					Intent sndintent = new Intent(MainpageActivity.VOLUME_CHANGED);
					sendBroadcast(sndintent);
				}
				else
				{
					isoundlevel = 0;
					return;
				}
			}
		});
		soundsub.setOnKeyListener(onKeyListener);
		soundadd.setOnKeyListener(onKeyListener);

		curplaypos.setOnKeyListener(onKeyListener);
		listView.setOnKeyListener(onKeyListener);
		
		pause.setOnKeyListener(onKeyListener);

		this.curplaypos.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				//				Logger.i("Reader", "progress:" + progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				isChanging = true;
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
//				if(mplayer.isPlaying()||isPause)
//				{
//					mplayer.seekTo(curplaypos.getProgress());
//				}
				if (isPlaying || isPause||IsBackgroundMusicOn()) {
					Intent intent = new Intent(service);
					intent.putExtra("operate", "moveloc");
					intent.putExtra("location", curplaypos
							.getProgress());
					sendBroadcast(intent);
				}
				isChanging = false;
			}

		});


		this.pause.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isPlaying) {
					pause.setImageResource(R.drawable.play_mymusic);
					stop.setImageResource(R.drawable.stop_mymusic);
					//mplayer.pause();
					isPause = true;
					isPlaying = false;
					isStop = false;
					Intent intent = new Intent(service);
					intent.putExtra("operate", "pause"); // "pause"
					sendBroadcast(intent);
					
				}
				else if(isPause)
				{
					pause.setImageResource(R.drawable.pause_mymusic);
					stop.setImageResource(R.drawable.stop_mymusic);
					//mplayer.start();
					Intent intent = new Intent(service);
					intent.putExtra("operate", "continue");
					sendBroadcast(intent);
					isPause = false;
					isPlaying = true;
					isStop = false;
					
				}
			}
		});

		this.stop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isPlaying || isPause) {
					Intent myintent = new Intent(service);
					stopService(myintent);
					stop.setImageResource(R.drawable.play_mymusic);
					pause.setImageResource(R.drawable.pause_mymusic);
					musicprocess.setText("0:00");
					//mplayer.stop();
					//mplayer.reset();
					isStop = true;
					isPause = false;
					isPlaying = false;
					curplaypos.setProgress(0);
					//isPlay=false;	
					//					musicindexplaying = -1;
				}
				else if(isStop){

					stop.setImageResource(R.drawable.stop_mymusic);
					Intent myintent = new Intent(service);
					myintent.putExtra("tracer", bkaction);
					String[] list = getplaymusiclist(musicindexplaying);
					myintent.putExtra("files", list);
					startService(myintent);
					isStop = false;
					isPause = false;
					isPlaying = true;
					//isPlay=true;
				}
			}
			
		});

		this.premusic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(isPlaying||isPause||isStop)
				{
					if(--musicindexplaying<0){
						musicindexplaying=musicplays.size()-1;
					}
					Intent myintent = new Intent(service);
					myintent.putExtra("tracer", bkaction);
					String[] list = getplaymusiclist(musicindexplaying);
					myintent.putExtra("files", list);
					startService(myintent);
					isPlaying = true;
					isPause = false;
					isStop = false;
					pause.setImageResource(R.drawable.pause_mymusic);
					stop.setImageResource(R.drawable.stop_mymusic);
					return;
				}
				
			}
		});
		this.nextmusic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//	if(mplayer.isPlaying())
				//	{
				if(isPlaying||isPause||isStop)
				{
					if(++musicindexplaying>musicplays.size()){
						musicindexplaying=0;
					}

					Intent myintent = new Intent(service);
					myintent.putExtra("tracer", bkaction);
					String[] list = getplaymusiclist(musicindexplaying);
					myintent.putExtra("files", list);
					startService(myintent);
					isPlaying = true;
					isPause = false;
					isStop = false;
					pause.setImageResource(R.drawable.pause_mymusic);
					stop.setImageResource(R.drawable.stop_mymusic);
					return;
				}
			}
		});

		returnback.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//				System.out.println("DirPath="+DirPath);
				sendBroadcast(new Intent(MainpageActivity.BACK));
//				if("MyMusic".equals(DirPath)){
//					sendBroadcast(new Intent(MainpageActivity.BACK));	
//				}else{
//					int lastindex = DirPath.lastIndexOf("/");	
//					DirPath = DirPath.substring(0, lastindex);
//					Intent sndintent = new Intent(MainpageActivity.START_ACTIVITY);
//					Bundle sndBundle = new Bundle();
//					sndBundle.putString("act","com.pvi.ap.reader.activity.ResCenterActivity");
//					sndBundle.putString("startType",  "allwaysCreate");  
//					sndBundle.putString("haveTitleBar", "1");
//					sndBundle.putString("DirPath", DirPath);
//					sndBundle.putString("actTabIndex", "1");
//					sndintent.putExtras(sndBundle);
//					sendBroadcast(sndintent);
//				}

			}
		});
		//musiclayout[0].requestFocus();
		
		super.onCreate(savedInstanceState);
		
		if(deviceType==1){
			
			all=(RelativeLayout)findViewById(R.id.mainblock);
			//all.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
//			curmusicname.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//			curplaypos.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			musicprocess.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//			premusic.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//			nextmusic.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//			pause.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//			stop.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//			soundsub.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//			soundadd.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//			playmuiscLayout.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
			//closeButton.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			for(int i=1;i<4;i++){
//				//soundlevel[i].setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			}
		}
	}

	private void setVolume() {
		for(int i = 1; i <= 3; i++ )
		{
			if(i <= isoundlevel)
			{
				soundlevel[i].setBackgroundResource(R.drawable.musicsound2);
			}
			else
			{

				soundlevel[i].setBackgroundResource(R.drawable.musicsound3);
			}
		}
		float m_iVolume = 0;
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
		m_iVolume = ((float)max/3)*isoundlevel;
		Logger.i("Reader", "m_iVolume : " + m_iVolume);
		if (m_iVolume < 0) 
			m_iVolume = 0;
		else if (m_iVolume > max) 
			m_iVolume = max;
		//mplayer.setVolume(m_iVolume, m_iVolume);

		mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,(int) m_iVolume, 0);
		max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		m_iVolume = ((float)max/3)*isoundlevel;
		Logger.i("Reader", "m_iVolume : " + m_iVolume);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				(int)m_iVolume , 0);
	}

	private int getMusicInfo(String dirpath, String searchkey) {
		this.musicInfo.clear();	
		String dir = "";
		if (dirpath.equals("")) {
			this.DirPath = "MyMusic";		
		} else {
			this.DirPath = dirpath;
		}

		dir= android.os.Environment.getExternalStorageDirectory()
		.getPath() + "/" + this.DirPath;
		//System.out.println(dir);
		if (!(android.os.Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED))) {

			sdnofile = true;
			return 1;   //sdcard 不存在
		}

		Logger.i("Reader", this.DirPath);
		File path = new File(dir);
		boolean a = path.mkdirs();

		File[] alldocfile = null;
		alldocfile = path.listFiles();

		if ((alldocfile==null)||(alldocfile.length == 0)){

			sdnofile = false;
			return 2; //sdcard 指定目录不存在文件
		} 
		sdnofile = false;
		String temp = "";
		for (int i = 0; i < alldocfile.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			temp = alldocfile[i].getName();
			if(temp.toLowerCase().endsWith(".mp3")||temp.toLowerCase().endsWith(".wav")||temp.toLowerCase().endsWith(".mid")||temp.toLowerCase().endsWith(".ogg")||temp.toLowerCase().endsWith(".aac")||alldocfile[i].isDirectory()){
				if (searchkey.equals("")) {
					map.put("Name", temp);
					map.put("FilePath", alldocfile[i].getAbsolutePath());
					Long  time=alldocfile[i].lastModified();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					map.put("time", formatter.format(time));
					if (alldocfile[i].isDirectory()) {
						map.put("IsDir", "true");
						
					} else if (alldocfile[i].isFile()) {
						map.put("IsDir", "false");
						
					}
					this.musicInfo.add(map);
				} else {
					if (temp.toLowerCase().contains(searchkey.toLowerCase())) {
						map.put("Name", temp);
						map.put("FilePath", alldocfile[i].getAbsolutePath());
						Long  time=alldocfile[i].lastModified();
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						map.put("time", formatter.format(time));
						if (alldocfile[i].isDirectory()) {
							map.put("IsDir", "true");	
							
						} else if (alldocfile[i].isFile()) {
							map.put("IsDir", "false");
							
						}
						this.musicInfo.add(map);
					}
				}
			
			}
		}
		
		return 0;
	}
	public void  setEvent(int i){
//		HashMap<String, Object> map = new HashMap<String, Object>();
		File file = null;
		String filename = "";
		filename=list.get(listView.mCurFoucsRow)[1].text;
		file = new File(Config.getString("MyDocPath") + DirPath + "/" + filename);
		if (file.isFile() && file.exists()) {
			//播放音乐。。。

			if(IsBackgroundMusicOn())
			{
				isbackgroundon = true;
				//通知服务背景音乐列表添加新歌曲
				Intent intent = new Intent(service);
				intent.putExtra("operate", "pause"); // "add"
				sendBroadcast(intent);
			}

			
			musicnameplaying = filename;
			musicindexplaying = i;
			
			curmusicname.setText(filename);
			playmuiscLayout.setVisibility(View.VISIBLE);
			
			musicplays.clear();
			for(int ii=0;ii<musicInfo.size();ii++){
				musicplays.add(musicInfo.get(ii).get("FilePath").toString());
			}	

			playmusic((currentPage-1)*itemPerPage+listView.mCurFoucsRow);

			return;

		} else if (file.isDirectory()) {

			DirPath = DirPath + "/"+ filename;
			getBKMusicList();
			Intent tmpIntent = new Intent(
					MainpageActivity.SET_TITLE);
			Bundle sndbundle = new Bundle();
			sndbundle.putString("title", "我的音乐[>>]"+filename);
			tmpIntent.putExtras(sndbundle);
			sendBroadcast(tmpIntent);
			
			int ret = getMusicInfo(DirPath, searchkey);
			refresh=true;
			id++;
			setValue(ret);
			//musiclayout[0].requestFocus();
		} else {
			return;
		}
	}
	private View.OnKeyListener onKeyListener=new OnKeyListener() {
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(event.getAction()==KeyEvent.ACTION_UP){
				if(keyCode == event.KEYCODE_BACK){
					 
					 return retBackTo();
				}
				return false;
			}
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				if ((keyCode == event.KEYCODE_ENTER)||(keyCode == event.KEYCODE_DPAD_CENTER)) {
					switch (v.getId()) {
//					case R.id.list:
//						setEvent(listView.mCurFoucsRow);
//						break;
                    case R.id.stop:
						stop.performClick();
						break;
					case R.id.pause:
						pause.performClick();
						break;
					case R.id.premusic:
						premusic.performClick();
						break;
					case R.id.nextmusic:
						nextmusic.performClick();
						break;
					case R.id.soundadd:
						soundadd.performClick();
						break;
					case R.id.soundsub:
						soundsub.performClick();
						break;
//					case R.id.prev:
//						prepage.performClick();
//						break;
//					case R.id.next:
//						nextpage.performClick();
//						break;
					}
					return true;
				}

				if (keyCode == event.KEYCODE_DPAD_LEFT) {
					if(listView.hasFocus())
					{
						try {
							pagechange = true;
							if (currentPage == 1) {
								return true;
							}
							currentPage--;
						    int ret = getMusicInfo(DirPath, searchkey);
							//musiclayout[0].requestFocus();
							refresh=true;
							id++;
							setValue(ret);
							listView.requestFocus();
						} catch (Exception e) {
							Logger.e("Reader", "pre page: " + e.toString());
						}
						return true;
					}
					else
					{
						return false;
					}
				}

				if (keyCode == event.KEYCODE_DPAD_RIGHT) {
					if(listView.hasFocus())
					{
						try {
							pagechange = true;
							if (currentPage == totalPage) {
								return true;
							}
							currentPage++;
							int ret = getMusicInfo(DirPath, searchkey);
							refresh=true;
							id++;
							setValue(ret);
							listView.requestFocus();
						} catch (Exception e) {
							Logger.e("Reader", "pre page: " + e.toString());
						}
						return true;
					}
					else
					{
						return false;
					}
				}
				
			}            
			return false;
		}
	};

	public static String ConventTime(long musicTime) {
		long min = musicTime / 60000;
		long sec = (musicTime - min * 60000) / 1000;
		long ms = (musicTime - min * 60000) % 1000;
		if(sec<10){
			return min + ":0" + sec;
		}
		return min + ":" + sec;
	}
	/**
	 * 调用后台服务，播放音乐
	 * @param list
	 * @param index
	 * @return
	 */
   public int playmusic(int index){
	   pause.setImageResource(R.drawable.pause_mymusic);
		stop.setImageResource(R.drawable.stop_mymusic);
	   Intent myintent = new Intent(service);
		myintent.putExtra("tracer", bkaction);
		String[] list = getplaymusiclist(index);

		myintent.putExtra("files", list);
		startService(myintent);
		isPlaying = true;
		isPause = false;
		isStop = false;
	   return 0;
   }
	private int playmusic(String filepath, int process) {
		//		musicstatus.setBackgroundResource(R.drawable.play);
		try {

			if(process == 0)
			{
				pause.setImageResource(R.drawable.pause_mymusic);
				stop.setImageResource(R.drawable.stop_mymusic);
				isPlaying = true;
				isPause = false;
				isStop = false;
				//mplayer.start();
			}
			else
			{
				pause.setImageResource(R.drawable.play_mymusic);
				stop.setImageResource(R.drawable.stop_mymusic);
				isPlaying = false;
				isPause = true;
				isStop = false;
			}
			//curplaypos.setMax(mplayer.getDuration());
			mhandler.sendEmptyMessageDelayed(1, 1000);
			return 0;

		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.e("Reader", "IllegalStateException   " + e.toString());
			return 1;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.e("Reader", "IllegalArgumentException   " + e.toString());
			return 1;
		}

	}
	private SharedPreferences mPrefs;

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		for(int i=0;i<musiclist.size();i++){
			String musicpath=musiclist.get(i);
			if(musicpath.equals(curmusicfile)){
				isBackgrounMusic=true;
				break;
			}
		}
		playmuiscLayout.setVisibility(View.GONE);
//		if(!isBackgrounMusic){
//			musicplays.clear();
//			Intent myintent = new Intent(service);
//			myintent.putExtra("operate", "pause"); // "pause"
//			sendBroadcast(myintent);
//			stopService(myintent);
//		}
		refresh=false;
		id=0;

		IsSearch=false;
		try {
			if (isPlaying || isPause) {
				SharedPreferences.Editor ed = mPrefs.edit();
				ed.putString("FileName", this.curmusicname.getText().toString());
				ed.putInt("TotalPos", this.curplaypos.getMax());
				ed.putInt("PlayPos", this.curplaypos.getProgress());
				ed.putString("CurTime", this.musicprocess.getText().toString());

				if((musicindexplaying < musicInfo.size())&&(musicindexplaying >= 0))
				{
					ed.putString("FilePath", musicInfo.get(musicindexplaying).get("FilePath").toString());
				}
				else
				{
					ed.putString("FilePath", mPrefs.getString("FilePath", "/sdcard/"));
				}
				ed.commit();
				isPause = false;
				
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.e("Reader", "onPause   " + e.toString());

		}
//		try {
//			// restore system settings about screen off timeout when test end
//			Settings.System.putInt(this.getContentResolver(),
//					Settings.System.SCREEN_OFF_TIMEOUT, init_screenoff);
//		} catch (Exception e) {
//			Logger.i("FSDS", e.toString());
//		}
		pagechange = false;
		//currentPage = 1;
		
		if (broadcastRec != null) {
			unregisterReceiver(broadcastRec);// 使用完注销广播监听函数
		}
		if (palyMusic != null) {
			unregisterReceiver(palyMusic);// 使用完注销广播监听函数
		}
		curplaypos.setProgress(0);
		musicprocess.setText("0:00");
		curmusicname.setText("");
		searchkey = "";
		super.onPause();
	}
	private String[] getbkmusiclist(int index) {
		if (index < 0) {
			return null;
		}
		String[] list = new String[musiclist.size()];
		int listidx = 0;
		for (int i = index; i < musiclist.size(); i++) {
			list[listidx++] = musiclist.get(i);
		}
		for (int i = 0; i < index; i++) {
			list[listidx++] = musiclist.get(i);
		}
		return list;
	}
	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){
        @Override
        public void onUiItemClick(PviUiItem item) {

            final PviAlertDialog dialog = new PviAlertDialog(getParent());
                dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
                dialog.setCanClose(true);
                String vTag = item.id;
                 if(vTag.equals("back1")){
                	 if(sdnofile){
     					sendBroadcast(new Intent(MainpageActivity.BACK));	
     					return ;
     				}
                    retBackTo();
                }
                if(sdnofile)
                {
//                  Toast.makeText(MyMusicActivity.this, "SD Card不存在！",
//                          Toast.LENGTH_LONG).show();
                    dialog.setMessage("SD Card不存在");
                    dialog.show();
                    closePopmenu();
                    return;
                }
               
                
                if (vTag.equals("backgroundmusicactivity")) {
                    //
                    File file = new File(
                    "/data/data/com.pvi.ap.reader/files/BackgroundMusic.list");
                    boolean a = file.exists();
                    if ((!(a && file.canRead()))||(musiclist.size()==0)) {
                        PviAlertDialog pd = new PviAlertDialog(getParent());
                        pd.setTitle("温馨提示");
                        pd.setMessage("无背景音乐，请添加背景音乐！");
                        pd.setCanClose(true);
                        pd.show();
                        closePopmenu();

                        return;

                    }

                    Intent intent = new Intent(MainpageActivity.SHOW_TIP);
                    Bundle sndBundle = new Bundle();
                    sndBundle.putString("pviapfStatusTip", "背景音乐信息获取中...");
                    intent.putExtras(sndBundle);
                    sendBroadcast(intent);

                    Intent intent1 = new Intent(MainpageActivity.START_ACTIVITY);

                    Bundle sndBundle1 = new Bundle();
                    sndBundle1.putString("act",
                    "com.pvi.ap.reader.activity.BackgroundMusicActivity");
                    sndBundle1.putString("haveStatusBar", "1");
                    sndBundle1.putString("haveTitleBar", "1");
                    sndBundle1.putString("startType", "allwaysCreate"); // 每次都create一个新实例，不设置此参数时，默认为“复用”已有的
                    intent1.putExtras(sndBundle1);
                    sendBroadcast(intent1);

                } else if (vTag.equals("addtobackground")) {
                    // 添加背景音乐

                    if((currentPage-1)*itemPerPage+listView.mCurFoucsRow>=musicInfo.size()||listView.mCurFoucsRow == -1)
                    {
                        //Toast.makeText(MyMusicActivity.this, "请选择一首音乐！", Toast.LENGTH_LONG).show();
                        dialog.setMessage("请选择一首音乐");
                        dialog.show();
                        closePopmenu();
                        return;

                    }   
                    FileOutputStream os = null;
                    HashMap<String, Object> map = null;
                    String filepath = "";
                    map = musicInfo.get((currentPage-1)*itemPerPage+listView.mCurFoucsRow);
                    filepath = map.get("FilePath").toString() + "\r\n";
                    //              map.put("IsDir", "true");
                    if(map.get("IsDir").equals("true"))
                    {
                        PviAlertDialog pd1 = new PviAlertDialog(getParent());
                        pd1.setTitle("温馨提示");
                        pd1.setMessage("不能添加目录到背景音乐库！");
                        pd1.setCanClose(true);
                        pd1.show();
                        closePopmenu();

                        return;

                    }
                    /*
                     * 判断是否可播放
                     */
                    PviAlertDialog pd = new PviAlertDialog(getParent());
                        if (musiclist.contains(map.get("FilePath").toString())) {
                            PviAlertDialog pd1 = new PviAlertDialog(getParent());
                            pd1.setTitle("温馨提示");
                            pd1.setMessage("该音乐已经是背景音乐！");
                            pd1.setCanClose(true);
                            pd1.show();
                            closePopmenu();

                            return;
                        }
                    try {
                        MediaPlayer tempmusic = new MediaPlayer();

                        tempmusic.reset();
                        tempmusic.setOnPreparedListener(null);

                        tempmusic.setDataSource(map.get("FilePath").toString());
                        tempmusic.setOnCompletionListener(null);
                        tempmusic.prepare();
                        
                        os = openFileOutput("BackgroundMusic.list",
                                Context.MODE_APPEND);

                        musiclist.add(map.get("FilePath").toString());
                        os.write(filepath.getBytes());
                        os.flush();
                        os.close();
//                          pd.setTitle("温馨提示");
//                          pd.setMessage("添加背景音乐成功！");
//                          pd.setCanClose(true);
//                          pd.show();
                        //musicimageid[selIndex%itemPerPage].setImageResource(R.drawable.backmusic);
                        setValue(0);
                        if(IsBackgroundMusicOn())
                        {
                            //通知服务背景音乐列表添加新歌曲
                            Intent intent = new Intent(service);
                            intent.putExtra("operate", "add"); // "delete"
                            intent.putExtra("file",map.get("FilePath").toString());
                            sendBroadcast(intent);
                        }

                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        
                        e.printStackTrace();
                        Logger.e("Reader", "IllegalStateException   " + e.toString());
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Logger.e("Reader", "IllegalArgumentException   " + e.toString());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        pd.setTitle("温馨提示");
                        pd.setMessage("文件格式不对或不能播放!");
                        pd.setCanClose(true);
                        pd.show();
                        e.printStackTrace();
                        Logger.e("Reader", "IOException   " + e.toString());
                    } 
                    
                } else if(vTag.equals("delbackground")){
                    if((currentPage-1)*itemPerPage+listView.mCurFoucsRow>=musicInfo.size()||listView.mCurFoucsRow == -1)
                    {
                        //Toast.makeText(MyMusicActivity.this, "请选择一首音乐！", Toast.LENGTH_LONG).show();
                        dialog.setMessage("请选择一首音乐");
                        dialog.show();
                        closePopmenu();
                        return;

                    }
                    HashMap<String, Object> map = null;
                    map = musicInfo.get((currentPage-1)*itemPerPage+listView.mCurFoucsRow);
                   String url=map.get("FilePath").toString();
                   if(compareUrl(url)){
                       if(url.equals(curmusicfile))
                        {
                            final PviAlertDialog pd = new PviAlertDialog(getParent());
                            pd.setTitle(getString(R.string.alert_dialog_delete_title));
                            pd.setMessage("此背景音乐在播放，不能删除");
                            pd.setTimeout(5000); 
                            pd.setCanClose(true);
                            pd.show();
                            closePopmenu();
                            return ;

                        }
                        else
                        {
                            final PviAlertDialog pd = new PviAlertDialog(getParent());
                            pd.setTitle(getString(R.string.alert_dialog_delete_title));
                            pd.setMessage(getString(R.string.alert_dialog_delete_message));
                            pd.setTimeout(5000); // 可选参数 延时5000ms后自动关闭

                            pd.setButton(DialogInterface.BUTTON_POSITIVE,
                                    getString(R.string.alert_dialog_delete_yes),
                                    new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    //File myfile = null;

                                    String map = null;
                                    Logger.v("size", "size0="+musiclist.size());
                                    
                                        map = musicInfo.get((currentPage-1)*itemPerPage+listView.mCurFoucsRow).get("FilePath").toString();
                                        //myfile = new File(map);
                                        //int i = musiclist.indexOf(map);
                                        musiclist.remove(map);
                                        Logger.v("size", "size="+musiclist.size());
                                        FileOutputStream os = null;
                                        String filepath = null;

                                        try {
                                            os = openFileOutput(
                                                    "BackgroundMusic.list",
                                                    Context.MODE_PRIVATE);
                                            for (int i1 = 0; i1 < musiclist.size(); i1++) {
                                                filepath = musiclist.get(i1)
                                                + "\r\n";
                                                Logger.v("filepath", "filepath="+filepath);
                                                os.write(filepath.getBytes());
                                            }
                                            os.flush();
                                            os.close();
                                        } catch (FileNotFoundException e) {
                                            // TODO Auto-generated catch block
                                            Logger.e("FileNotFoundException", e.toString());
                                            e.printStackTrace();
                                        }// create a file
                                        catch (IOException e) {
                                            // TODO Auto-generated catch block
                                            Logger.e("IOException", e.toString());
                                            e.printStackTrace();
                                        }

                                        if (IsBackgroundMusicOn()) {
                                            // 通知服务背景音乐列表添加新歌曲
                                            Intent intent = new Intent(service);
                                            intent.putExtra("operate", "delete"); // "add"
                                            intent.putExtra("file", map);
                                            sendBroadcast(intent);
                                        }

                                
                                
                                    //totalPage = 0;
                                
                                    getBKMusicList();
                                    //musicimageid[selIndex%itemPerPage].setImageResource(R.drawable.mymusic);
                                    setValue(0);
                                }
                            });

                            pd.setButton(DialogInterface.BUTTON_NEGATIVE,
                                    getString(R.string.alert_dialog_delete_no),
                                    new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    // TODO Auto-generated method stub
                                    pd.dismiss();
                                }
                            });

                            pd.show();

                        }
     
                   }else{
                       //Toast.makeText(MyMusicActivity.this, "不是背景音乐！", Toast.LENGTH_LONG).show();
                       dialog.setMessage("不是背景音乐");
                       dialog.show();
                        closePopmenu();
                        
                       return;
                   }
                }
                else if (vTag.equals("delete")) {
                    // 删除

                    // modify by kizanl 2010.11.24 121
                    // add a alert dialog to inform use
                    // with delete actions
                    // I just move the delete functions to the
                    // yes choose
                    HashMap<String, Object> map1 = null;
                    if(musicInfo.size()<=0){
                        dialog.setMessage("请选择一首音乐");
                        
                        dialog.show();
                        closePopmenu();
                        return;
                    }
                    if((currentPage-1)*itemPerPage+listView.mCurFoucsRow>=musicInfo.size()||listView.mCurFoucsRow == -1)
                    {
                        //Toast.makeText(MyMusicActivity.this, "请选择一首音乐！", Toast.LENGTH_LONG).show();
                        dialog.setMessage("请选择一首音乐");
                        dialog.show();
                        closePopmenu();
                        return;

                    }
                    map1 = musicInfo.get((currentPage-1)*itemPerPage+listView.mCurFoucsRow);
                    String filenameString=map1.get("Name").toString();
                    
                    
                    if(filenameString.equals(getbkmusicname(curmusicfile)))
                    {
//                          curplaypos.setProgress(0);
//                          musicprocess.setText("0:00");
//                          curmusicname.setText("");
//                          isPause = false;
                        dialog.setMessage("此音乐正在播放，不能删除");
                        dialog.show();
                            closePopmenu();
                        
                        return;
                    }
                    
                    final PviAlertDialog pd = new PviAlertDialog(getParent());
                    //pd.setTitle(getString(R.string.alert_dialog_delete_title));
                    pd.setTitle("删  除");
                    pd.setMessage(getString(R.string.alert_dialog_delete_message));
                    pd.setTimeout(5000); // 可选参数 延时5000ms后自动关闭
                    pd.setCanClose(true);
                    pd.setButton(DialogInterface.BUTTON_POSITIVE,
                            getString(R.string.aboutOK),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                int which) {
                            
                            HashMap<String, Object> map = null;
                            map = musicInfo.get((currentPage-1)*itemPerPage+listView.mCurFoucsRow);
                            String filepath=map.get("FilePath")
                            .toString();
                            deletedir(new File(filepath));
                                if(IsBackgroundMusicOn())
                                {
                                    //通知服务背景音乐列表添加新歌曲
                                    Intent intent = new Intent(service);
                                    intent.putExtra("operate", "delete"); // "add"
                                    intent.putExtra("file",map.get("FilePath").toString());
                                    sendBroadcast(intent);
                                }
                            selIndex = 0;
                             ret = getMusicInfo(DirPath, searchkey);
                            IsSearch=false;
                            refresh=true;
                            id++;
                            setValue(ret);
                        }
                    });

                    pd.setButton(DialogInterface.BUTTON_NEGATIVE,
                            getString(R.string.my_friend_cancel),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                int which) {
                            // TODO Auto-generated method stub
                            pd.dismiss();
                        }
                    });
                    pd.show();
                    // onStart();
                } else if (vTag.equals("search")) {
                    //
                    
                    LayoutInflater inflater=LayoutInflater.from(getParent());

                    final View view=inflater.inflate(R.layout.search, null);
                    final PviAlertDialog pd = new PviAlertDialog(getParent());
                    pd.setView(view);
                    pd.setCanClose(true);
                    //pd.setTitle(getResources().getString(R.string.bookSearch));
                    pd.setTitle("搜  索");
                    final TextView tv = (TextView)view.findViewById(R.id.hint);
                    final EditText edt = (EditText)view.findViewById(R.id.keyword);
                    Button search = (Button)view.findViewById(R.id.searchbtn);

                    search.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            currentPage = 1;
                            totalPage = 0;
                            selIndex = 0;
                            IsSearch = true;
                            searchkey = edt.getText().toString();
                            Logger.d("Reader", "searchkey: " + searchkey);
                            if (searchkey.equals("")) {
                                tv.setText("搜索关键字为空，将列举全部音乐！");
                                //Toast.makeText(MyMusicActivity.this, "搜索关键字为空，将列举全部音乐！", Toast.LENGTH_LONG).show();
                            } 
                            //                      onResume();
                            //                      getBKMusicList();
                             ret = getMusicInfo(DirPath, searchkey);
                            pd.dismiss();
                            PviUiUtil.hideInput(v);
                            refresh=true;
                            id++;
                            setValue(ret);
                        
//                              searchkey="";
//                              IsSearch = false;
                            if(sdnofile)
                            {
                                //Toast.makeText(MyMusicActivity.this, "SD Card不存在！",
                                    //  Toast.LENGTH_LONG).show();
//                                  diaLogger.setMessage("SD Card不存在");
//                                  diaLogger.show();
//                                  closePopmenu();
//                                  return;
                            }
                            else if(musicInfo.size() <= 0)
                            {
                                pd.setView(view);
                                pd.setCanClose(true);
                                pd.setTitle("此目录不存在文件！");
                                //Toast.makeText(MyMusicActivity.this, "此目录不存在文件！", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    });
                    edt.setOnFocusChangeListener(new OnFocusChangeListener() {

                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            // TODO Auto-generated method stub
                            if (hasFocus) {
                                tv.setText("");
                            }
                        }

                    });
                    pd.show();
                    
                }else if (vTag.equals("alldelete")) {
                    final PviAlertDialog pd = new PviAlertDialog(getParent());
                    //pd.setTitle(getString(R.string.alert_dialog_delete_title));
                    pd.setTitle("删除全部");
                    pd.setCanClose(true);
                    HashMap<String, Object> map = null;
                    if(musicInfo.size()<=0){
                        closePopmenu();
                        return;
                        
                    }
                    map = musicInfo.get((currentPage-1)*itemPerPage+listView.mCurFoucsRow);
                   String url=map.get("FilePath").toString();
                    if(isPlaying||url.equals(curmusicfile)){
                            pd.setMessage("有音乐或背景音乐正在播放，不能删除");
                            pd.show();
                            closePopmenu();
                            return;
                        }
                    pd.setMessage(getString(R.string.playingmusicdeleteallmsg));
                    pd.setTimeout(5000); // 可选参数 延时5000ms后自动关闭
                    pd.setButton(DialogInterface.BUTTON_POSITIVE,
                            getString(R.string.aboutOK),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                int which) {
                            
                            
                            Intent myintent = new Intent(service);
                            stopService(myintent);
                            for(int i=0;i<musicInfo.size();i++){
                                String filemusic=musicInfo.get(i).get("FilePath").toString();
                                deletedir(new File(filemusic));
                            }
                            IsSearch=false;
                            searchkey="";
                            onResume();
                        }
                    });

                    pd.setButton(DialogInterface.BUTTON_NEGATIVE,
                            getString(R.string.my_friend_cancel),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                int which) {
                            // TODO Auto-generated method stub
                            pd.dismiss();
                        }
                    });

                    pd.show();

                
                } else if (vTag.equals("filecompositor")) {
                    orderType = 1;
                    sortUtil = new QuitSortComparator("IsDir","Name","time");
                    sortUtil.setDescend2(false);
                   ret=getMusicInfo(DirPath,searchkey);
                    refresh=true;
                     id++;
                    setValue(ret);
                }else if (vTag.equals("timecompositor")) {
                    orderType = 2;
                    sortUtil = new QuitSortComparator("IsDir","time","Name");
                    
                    sortUtil.setDescend2(true);
                    ret=getMusicInfo(DirPath,searchkey);
                    refresh=true;
                    id++;
                    setValue(ret);
                }
                closePopmenu(); 
            
        }};
      /**
       * 重写返回方法
       * @return
       */
		private boolean retBackTo() {
			Logger.v("TAG->retBackTo->", "retBackTo") ;
			
			if(!"".equals(searchkey)){
				searchkey = "" ;
				ret=getMusicInfo(DirPath, searchkey);
				Intent tmpIntent = new Intent(
						MainpageActivity.SET_TITLE);
				Bundle sndbundle = new Bundle();
				sndbundle.putString("title", "我的音乐");
				tmpIntent.putExtras(sndbundle);
				sendBroadcast(tmpIntent);
				refresh=true;
				id++;
				setValue(ret);
				return  true ;
			}
			
			int lastindex = DirPath.lastIndexOf("/");
			Logger.v("lastindex", lastindex);
			
			if(lastindex != -1)
			{
				DirPath = DirPath.substring(0, lastindex);
				Logger.v("DirPath", DirPath);
				
				ret=getMusicInfo(DirPath, searchkey);	
				Intent tmpIntent = new Intent(
						MainpageActivity.SET_TITLE);
				Bundle sndbundle = new Bundle();
				sndbundle.putString("title", "我的音乐");
				tmpIntent.putExtras(sndbundle);
				sendBroadcast(tmpIntent);
				refresh=true;
				id++;
				setValue(ret);
				return  true ;
			}
			sendBroadcast(new Intent(MainpageActivity.BACK));
			return true ;
		}


	@Override
	protected void onPostResume() {
		// TODO Auto-generated method stub
		super.onPostResume();
		Logger.i("Reader", "Display MyMusicActivity");
	}
   /**
    * 获取背景音乐集合
    */
	private void getBKMusicList() {
		musiclist.clear();
		File file = new File(
		"/data/data/com.pvi.ap.reader/files/BackgroundMusic.list");
		boolean a = file.exists();
		if (a && file.canRead()) {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						new FileInputStream(file)));
				String data = null;
				while ((data = in.readLine()) != null) {
					data = data.trim();
					if ((data.length() != 0) && !data.startsWith(";")) {
						musiclist.add(data);
						Logger.i("Reader", data);
					}

				}
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		return;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		Bundle bundle=new Bundle();
		bundle.putInt("SelIndex", (currentPage-1)*itemPerPage+listView.mCurFoucsRow);
		bundle.putString("DirPath", this.DirPath);
		intent.putExtras(bundle);
		this.setIntent(intent);
		super.onNewIntent(intent);
	}
   /**
    * 判断音乐播放服务是否在后台运行
    * @return
    */
	private boolean IsBackgroundMusicOn() {
		ActivityManager myManager=(ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(30);
		for (RunningServiceInfo service:runningService)
			if (service.service.getClassName().equals("com.pvi.ap.reader.service.BackGroundMusicService")) 
				return true;
		return false;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if((event.getKeyCode()==KeyEvent.KEYCODE_VOLUME_UP)||(event.getKeyCode()==KeyEvent.KEYCODE_VOLUME_DOWN))
		{
			float m_iVolume = 0;
			AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
			int i=mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
			mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
			//m_iVolume = (float)2*max/5;
			m_iVolume =i;
			boolean getsoundlevel = true;
			for(int i1 = 3; i1 >= 1; i1--)
			{
				if(m_iVolume >=((float)max/3)*i1)
				{
					if(getsoundlevel)
					{
						isoundlevel = i1;
						getsoundlevel = false;
					}

					soundlevel[i1].setBackgroundResource(R.drawable.musicsound2);
				}
				else
				{
					soundlevel[i1].setBackgroundResource(R.drawable.musicsound3);
				}
			}
		}
		return super.dispatchKeyEvent(event);
	}
	@Override
	public OnUiItemClickListener getMenuclick() {
		return this.menuclick;
	}
	
	private String[] getplaymusiclist(int index) {
		if (index < 0) {
			return null;
		}
		
		String[] list = new String[musicplays.size()];
		int listidx = 0;
		for (int i = index; i < musicplays.size(); i++) {
               if(new File(musicplays.get(i)).isDirectory()){
            	   i++;
               }else{
			list[listidx++] = musicplays.get(i);
               }
		}
		for (int i = 0; i < index; i++) {
			list[listidx++] = musicplays.get(i);
		}
		return list;
	}
	private String getbkmusicname(String filepath) {
		String[] dirs = filepath.split("/");
		return dirs[dirs.length - 1];
	}
	  public boolean deletedir(File f){
	    if(f.isDirectory())
	   {
	        File[] files = f.listFiles();
	        for(int i=0;i<files.length;i++)
	       {
	            if(files[i].isDirectory()) deletedir(files[i]);
	            else deletefile(files[i]);
	        }
	    }
	        f.delete();
	        return true;
	        
	    }
	  public boolean deletefile(File f)
	    {
	        if (f.isFile())
	            f.delete();
	        return true;
	    }

	@Override
	public void menupan() {
		// TODO Auto-generated method stub
		super.menupan();
		try {
            if(isPlaying||isPause){
            	getMenuItem("delete").enable = false;
            	getMenuItem("alldelete").enable = false;
            	redrawMenu();
            }else {
                getMenuItem("delete").enable = true;
                getMenuItem("alldelete").enable = true;
                redrawMenu();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	private void showtip(String msg)
	{
		Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
		Bundle sndbundle = new Bundle();
		sndbundle.putString("pviapfStatusTip", msg);
		msgIntent.putExtras(sndbundle);
		sendBroadcast(msgIntent);
	}
	private void showme()
	{
		//Intent intent1 = new Intent(MainpageActivity.HIDE_TIP);
		//sendBroadcast(intent1);

		Intent tmpIntent = new Intent(
				MainpageActivity.SHOW_ME);
		Bundle bundleToSend = new Bundle();
		 bundleToSend.putString("act", "com.pvi.ap.reader.activity.ResCenterActivity");
	        bundleToSend.putString("actTabName", "我的音乐");
		bundleToSend.putString("sender",this.getClass().getName()); //TAB内嵌activity类的全名
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
		tmpIntent = null;
		bundleToSend = null;
	}

	@Override
	public void OnNextpage() {
		// TODO Auto-generated method stub
		nextPage();
	}

	@Override
	public void OnPrevpage() {
		// TODO Auto-generated method stub
		prevPage();
	}
	private void prevPage(){
		try {
			pagechange = true;
			if (currentPage == 1) {
				return ;
			}
			currentPage--;
		    int ret = getMusicInfo(DirPath, searchkey);
			refresh=true;
			id++;
			setValue(ret);
			listView.requestFocus();
		} catch (Exception e) {
			Logger.e("Reader", "pre page: " + e.toString());
		}
	}  
	private void nextPage(){
		try {
			
			if (currentPage == totalPage) {
				return ;
			}
			currentPage++;
			int ret = getMusicInfo(DirPath, searchkey);
			refresh=true;
			id++;
			setValue(ret);
			listView.requestFocus();
		} catch (Exception e) {
			Logger.e("Reader", "pre page: " + e.toString());
		}
	}  
}
