package com.pvi.ap.reader.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;

import com.pvi.ap.reader.data.common.Logger;

/**
 * The BackGroundMusicService provide music playing in black function.<br>
 * This service only show the UI of volume adjust , no other interface.
 * @author rd031
 *
 */

public class BackGroundMusicService extends Service{
	
	
	public BackGroundMusicService() {
//		super("BackGroundMusicService");
	}

	private NotificationManager mNM;
	private String TAG = "BackGroundMusicService" ;
	private BackGroundMusicService self = null;
	public  final String m_aszMyID = "com.pvi.ap.reader.service.BackGroundMusicService";
	public  final String m_aszListFile = "/data/data/com.pvi.ap.reader/files/BackgroundMusic.list";
	public  final String m_aszAPFrame = "com.pvi.ap.reader.mainframe.SERVICE_RESP";
	private String m_aszTracer = null;
	private Handler m_oHandler = null;
	private Runnable m_oTask = null;
	private int m_iduration = 0;
	private int m_iLastTimeLocal = 0;
	private MediaPlayer m_oPlayer = null;
	private List<String> m_aszList = null;
	private int m_nCount = 0;
	private int m_iVolume = 0;
	private boolean m_bInTTS = false;
	private boolean m_bLoop = true;
	private BroadcastReceiver m_oUnmountReceiver = null;
	private BroadcastReceiver m_oListener = null;
	private BroadcastReceiver m_oVolumeChanged = null;


	/**
	 * Èý¸ö¹ã²¥
	 */
	private String stateIntentStr = "com.pvi.ap.reader.service.backgroundmusicservice.state" ;


	private int getCanUsedID(int start) {
		if (start < 0) return -1;
		if (m_aszList == null) return -1;
		if (start >= m_aszList.size()) {
			if (m_bLoop)
				start = 0;
			else
				return -1;
		}
		int current = start;
		for (int i=0; i<m_aszList.size(); i++) {
			File file = new File(m_aszList.get(current));
			if (file.exists()) {
				return current;
			}
			if (++current >= m_aszList.size()) {
				if (m_bLoop)
					current = 0;
				else
					break;
			}
		}
		return current; 
	}

	private int setDataSource(String path) {
		if (m_oPlayer == null) return -4;
		try {
			if (m_oPlayer.isPlaying())
				m_oPlayer.stop();
			m_oPlayer.reset();
			m_oPlayer.setOnPreparedListener(null);
			if(path.endsWith(".MID"))
			{
				Logger.d("MIDI Music:", path);
				FileInputStream in = new FileInputStream(path);
				File temp = File.createTempFile("temp", ".mid");
				FileOutputStream out = new FileOutputStream(temp);
				byte buf[] = new byte[128];
				do {
					int numread = in.read(buf);
					if (numread <= 0)
						break;
					out.write(buf, 0, numread);
				} while (true);
				m_oPlayer.setDataSource(temp.getAbsolutePath());
				try {
					in.close();
				}
				catch (IOException ex) {
					Logger.e("MIDI Music:", "error: " + ex.getMessage(), ex);
				}
			}
			else
			{
				m_oPlayer.setDataSource(path);
			}
			m_oPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			m_oPlayer.setOnCompletionListener(m_oCompletionlistener);
			m_oPlayer.setOnPreparedListener(m_oPreparedlistener);
			m_oPlayer.setOnErrorListener(errorListener);
			m_oPlayer.prepare();
			m_iduration = m_oPlayer.getDuration();
			sendPlayInfo();
			m_oPlayer.start();
			new File(path).setLastModified(System.currentTimeMillis());
			sendPlayState("play");
			return 0;
		} catch (IllegalArgumentException e) {
			return -3;
		} catch (IllegalStateException e) {
			return -2;
		} catch (IOException e) {
			return -1;
		}
	}

	private synchronized void sendPlayInfo() {
		try {
			if (m_aszTracer != null) {
				Intent myintent = new Intent(m_aszTracer);
				int current = m_oPlayer.getCurrentPosition();
				myintent.putExtra("file", m_aszList.get(m_nCount));
				myintent.putExtra("position", (current > m_iduration)?m_iduration:current);
				myintent.putExtra("duration", m_iduration);
				sendBroadcast(myintent);
				m_iLastTimeLocal = current/1000;
			}
		} catch (Exception e) {
			Logger.e(TAG, e);
			exit();
		}
	}
	private synchronized void startTrace(String tracer) {
		m_aszTracer = tracer;
		if (m_oHandler == null) {
			m_oHandler = new Handler();
		}
		if (m_oTask == null) {
			m_oTask = new Runnable() {
				@Override
				public void run() {
					if (m_aszTracer != null) {
						if (m_oPlayer.getCurrentPosition()/1000 != m_iLastTimeLocal) {
							if (m_iduration <= 0) {
								if ((m_oPlayer != null) && (m_oPlayer.isPlaying()))
									m_iduration =m_oPlayer.getDuration();
							}
							if (m_iduration > 0)
								sendPlayInfo();
							m_oHandler.postDelayed(this, 900);
						}
						else 
							m_oHandler.postDelayed(this, 100);
					}
				}
			};
		}
		m_oHandler.removeCallbacks(m_oTask);
		m_oHandler.post(m_oTask);
	}
	private synchronized void stopTrace() {
		if (m_aszTracer != null) {
			m_aszTracer = null;
		}
		m_iLastTimeLocal = 0;
		if (m_oHandler != null) {
			if (m_oTask != null) {
				m_oHandler.removeCallbacks(m_oTask);
			}
			m_oHandler = null;
		}
		if (m_oTask != null) {
			m_oTask = null;
		}
	}

	private void setVolume(Bundle mybundle) {
		if (mybundle.containsKey("sysadj")) {
			AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			int i = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + mybundle.getInt("sysadj");
			int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			if (i < 0) 
				i=0;
			else if (i > max) 
				i=max;
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);			
		}
		else if (mybundle.containsKey("sysabs")) {
			AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			int i = mybundle.getInt("sysabs");
			int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			if (i < 0) 
				i=0;
			else if (i > max) 
				i=max;
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);			
		}
		else if (mybundle.containsKey("prvadj")) {
			AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			m_iVolume += mybundle.getInt("prvadj");
			int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			if (m_iVolume < 0) 
				m_iVolume = 0;
			else if (m_iVolume > max) 
				m_iVolume = max;
			m_oPlayer.setVolume(m_iVolume, m_iVolume);			
		}
		else if (mybundle.containsKey("prvabs")) {
			AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			m_iVolume = mybundle.getInt("prvabs");
			int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			if (m_iVolume < 0) 
				m_iVolume = 0;
			else if (m_iVolume > max) 
				m_iVolume = max;
			m_oPlayer.setVolume(m_iVolume, m_iVolume);			
		}
	}

	private void registerExternalStrogeListener() {
		if (m_oPlayer == null) return;
		m_oUnmountReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(Intent.ACTION_MEDIA_EJECT)
						|| action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)
						|| action.equals(Intent.ACTION_MEDIA_REMOVED)
						|| action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
					exit();
					return;

				}
			}
		};
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		iFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		iFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		iFilter.addDataScheme("file");
		registerReceiver(m_oUnmountReceiver, iFilter);
	}

	private void registerVolumeChangedListener() {
		if (m_oPlayer == null) return;
		m_oVolumeChanged = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int streamType = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
				int volume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", -1);
				if (AudioManager.STREAM_MUSIC == streamType) {
					;
				}
				else if (9 == streamType) {
					if (m_bInTTS) {
						;
					}
				}
			}
		};
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
		iFilter.addAction("android.media.EXTRA_VOLUME_STREAM_VALUE");
		registerReceiver(m_oVolumeChanged, iFilter);
	}

	private MediaPlayer.OnCompletionListener m_oCompletionlistener = new MediaPlayer.OnCompletionListener(){
		@Override
		public void onCompletion(MediaPlayer mp) {
			Logger.e(TAG, "lenth" + mp.getCurrentPosition());
//			mp.stop();
			mp.reset(); 
			System.gc();
			if (m_oPlayer == null) return;
			if (m_aszList == null) return;
//			m_oPlayer.reset();
			
			sendPlayInfo();
			m_iduration = 0;
			if (m_aszList.size() >= 1) {
				m_nCount = getCanUsedID(++m_nCount);
				int count = 0;
				while ((m_nCount >= 0) && (count < m_aszList.size())) {
					if (0 == setDataSource(m_aszList.get(m_nCount))){
						return;
					}else{
						m_nCount = getCanUsedID(++m_nCount);
					}count++;
				}
			}
			noPlayList();
		}

	};
	private MediaPlayer.OnPreparedListener m_oPreparedlistener = new MediaPlayer.OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer mp) {
			//			if (m_iduration == 0)
			//				m_iduration = m_oPlayer.getDuration();
			//			sendPlayInfo();
		}
	};
	private MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener(){
 
		@Override
		public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
			Logger.e(TAG, "onError");
			arg0.stop();
			exit();
			return true;
		}

	};
	protected void playPause(Bundle mybundle) {
		if (mybundle.containsKey("tracer")) {
			Intent myintent = new Intent(mybundle.getString("tracer"));
			if (m_oPlayer.isPlaying()) {
				m_oPlayer.pause();
				myintent.putExtra("status", "be paused");
				sendPlayState("pause");
			}
			else {
				myintent.putExtra("status", "already paused");
				sendPlayState("pause");
			}
			sendBroadcast(myintent);
		}
		else {
			if (m_oPlayer.isPlaying()) {
				m_oPlayer.pause();
				sendPlayState("pause");
			}
		}
	}
	protected void playContinue(Bundle mybundle) {
		if (mybundle.containsKey("tracer")) {
			Intent myintent = new Intent(mybundle.getString("tracer"));
			if (m_oPlayer.isPlaying()) {
				myintent.putExtra("status", "already in play");
			}
			else {
				m_oPlayer.start();
				myintent.putExtra("status", "continue to play");
			}
			sendBroadcast(myintent);
		}
		else {
			if (!m_oPlayer.isPlaying()) {
				m_oPlayer.start();
			}
		}
		sendPlayState("play");
	}
	protected void musicAdd(Bundle mybundle) {
		if (mybundle.containsKey("file")){
			String file = mybundle.getString("file");
			if (file != null) {
				int index = m_aszList.indexOf(file);
				if (index < 0) {
					m_aszList.add(file);
				}
			}
		}
		if (mybundle.containsKey("list")) {
			List<String> list = mybundle.getStringArrayList("list");
			int len = list.size();
			if (len > 0) {
				for(int i=0; i<len; i++) {
					String file = list.get(i) ;
					if(m_aszList.indexOf(file) < 0){
						m_aszList.add(file);
					}
				}
			}
		}
	}
	protected void IsPause(Bundle mybundle) {
		String receiver = null;
		if (mybundle.containsKey("tracer"))
			receiver = mybundle.getString("tracer");
		else if (m_aszTracer != null)
			receiver = m_aszTracer;

		if (receiver != null) {
			Intent myintent = new Intent(receiver);
			if (m_oPlayer.isPlaying()) {
				myintent.putExtra("status", "play");
				sendPlayState("play");
			}
			else {
				myintent.putExtra("status", "puase");
				sendPlayState("pause");
			}
			sendBroadcast(myintent);
		}
	}
	protected void musicDelete(Bundle mybundle) {
		if (mybundle.containsKey("file")){
			String file = mybundle.getString("file");
			if (file != null) {
				int index = m_aszList.indexOf(file);
				if (index >= 0) {
					if (index == m_nCount){
						m_nCount--;
						m_aszList.remove(index);
						m_oCompletionlistener.onCompletion(m_oPlayer);
					}
					else if (index > m_nCount) {
						m_aszList.remove(index);
					}
					else {
						m_nCount--;
						m_aszList.remove(index);
					}
				}
			}
		}
	}
	private void registerBroadcastListener() {
		m_oListener = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (m_oPlayer == null) return;
				if (intent == null) return;
				String action = intent.getAction();
				if (action.equals(m_aszMyID)) {
					Bundle mybundle = intent.getExtras();
					if (mybundle == null) return;
					if (!mybundle.containsKey("operate")) return;
					String active = mybundle.getString("operate");
					if (active.equals("trace")) {
						if (mybundle.containsKey("tracer")) {
							String temp = mybundle.getString("tracer");
							if (temp.length() > 0) {
								startTrace(temp);
							}
							else
								stopTrace();
						}
						else
							stopTrace();
					}
					else if (active.equals("pause")) {
						playPause(mybundle);
					}
					else if (active.equals("continue")) {
						playContinue(mybundle);
					}
					else if (active.equals("IsPause")) {
						IsPause(mybundle);
					}
					else if (active.equals("volume")) {
						setVolume(mybundle);
					}
					else if (active.equals("add")) {
						musicAdd(mybundle);
					}
					else if (active.equals("delete")) {
						musicDelete(mybundle);
					}
					else if (active.equals("moveloc")) {
						if (mybundle.containsKey("location")){
							if ((m_iduration > 0) && (m_oPlayer != null)) {
								int local = mybundle.getInt("location");
								if ((local > 0) && (local<=m_iduration))
									m_oPlayer.seekTo(local);
							}
						}
					}
				}
			}
		};
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(m_aszMyID);
		registerReceiver(m_oListener, iFilter);
	}

	private PowerManager.WakeLock  wakeLock =  null ;
	
	@Override
	public void onCreate() {
		Logger.i(TAG, "onCreate");
		super.onCreate();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

		wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "pvi"); 
		wakeLock.acquire();

		self = this;
		if (m_oPlayer == null) 
			m_oPlayer = new MediaPlayer();
		if (m_oUnmountReceiver == null)
			registerExternalStrogeListener();
		if (m_oListener == null)
			registerBroadcastListener();
		if (m_oVolumeChanged == null)
			registerVolumeChangedListener();
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		m_iVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		m_aszList = null;
		m_nCount = 0;
	}

	/* (non-Javadoc)
	 * @see com.pvi.android.HelloAndroid.myservice#onStart(android.content.Intent, int)
	 */
	@Override
	public void onStart(Intent intent, int startId){
		Logger.i(TAG, "onStart");
		super.onStart(intent, startId);
		List<String> temp = null;
		Bundle mybundle = null;
		if (intent != null)
			mybundle = intent.getExtras();
		if (mybundle != null) {
			m_bLoop = mybundle.getBoolean("loop", true);
			if (mybundle.containsKey("files")) {
				String[] files = mybundle.getStringArray("files");
				for(String tmp:files){
					System.out.print(tmp + "  ");
					Logger.i(TAG, "file://" + tmp);
				}
				if ((files == null) || (files.length == 0)){
					noPlayList();
					return;
				}
				temp = new ArrayList<String>();
				for (int i=0; i<files.length; i++) {
					temp.add(files[i]);
				}
			}
			else if (mybundle.containsKey("list")) {
				temp = mybundle.getStringArrayList("list");
				if ((temp == null) || (temp.size() == 0)){
					noPlayList();
					return;
				}
			}
		}
		else {
			m_bLoop = true;
			Logger.i(TAG, "empty!");
		}
		if (temp == null) {
			File file = new File(m_aszListFile); 
			if (file.exists() && file.canRead()) {
				BufferedReader in = null ;
				try {
					in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
					String data = null;
					temp = new ArrayList<String>();
					while ((data = in.readLine()) != null) {
						data = data.trim();
						if ((data.length()!= 0) && !data.startsWith(";")) 
							temp.add(data);
					}
				} catch (Exception e) {
					Logger.e(TAG, e);
					noPlayList();
					return;
				}finally{
					if(in != null){
						try {
							in.close();
						} catch (IOException e) {
							Logger.e(TAG, e);
						}
					}
					
				}
			} else {
				String columns[] = new String[]{MediaStore.Audio.Media.DATA};
				String where = MediaStore.Audio.Media.IS_NOTIFICATION+"='"+1+"'";
				Cursor cur = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, where, null, MediaStore.Audio.Media._ID);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					Logger.e(TAG, e);
					noPlayList();
					return;
				}
				if ((cur == null) || (cur.getCount() == 0)){
					noPlayList();
					return;
				}
				temp = new ArrayList<String>();
				int size = cur.getCount();
				cur.moveToFirst();
				for (int i=0; i<size; i++) {
					temp.add(cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
					cur.moveToNext();
				}
				if(!cur.isClosed()){
					cur.close();
				}
			}
		}
		if ((temp == null) || (temp.size() == 0)) {
			noPlayList();
			return;
		}
		if (m_aszList == null) {
			m_aszList = temp;
			m_nCount = 0;
		}
		else if (!m_aszList.equals(temp) || (m_nCount >= temp.size()) || (m_nCount < 0)) {
			m_aszList = temp;
			m_nCount = 0;
		}
		m_nCount = 0 ;
		m_nCount = getCanUsedID(m_nCount);
		int count = 0;
		while ((m_nCount >= 0) && (count < m_aszList.size())) {
			if (0 == setDataSource(m_aszList.get(m_nCount))) {
				if ((mybundle != null) && (mybundle.containsKey("tracer"))) {
					String tracer = mybundle.getString("tracer");
					if (tracer.length() > 0) {
						startTrace(tracer);
					}
					else
						stopTrace();
				}
				return;
			}
			else{
				m_nCount = getCanUsedID(++m_nCount);
			}
			count++;
		}
		noPlayList();
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		onStart(intent,startId);
		return  START_REDELIVER_INTENT;
	}

	protected void noPlayList()
	{
		Logger.i(TAG, "noPlayList");
		Intent myintent = new Intent(m_aszAPFrame);
		Bundle bd = new Bundle();
		bd.putString("serviceName", "BackGroundMusicService");
		bd.putString("para1", "1");
		myintent.putExtras(bd);
		sendBroadcast(myintent);
		exit();
	}

	private void exit()
	{
//		self.getBaseContext().stopService(new Intent(m_aszMyID));
		try {
//			self.getBaseContext().stopService(new Intent(m_aszMyID));
			stopSelf();
		} catch (Exception e) {
			onDestroy();
		}
	}

	@Override
	public void onDestroy(){
		Logger.i(TAG, "onDestroy");
		wakeLock.release();
		stopTrace();
		sendPlayState("stop");
		if (m_oPlayer != null) {
			if(m_oPlayer.isPlaying()){
				m_oPlayer.stop();
			}
			m_oPlayer.release();
			m_oPlayer = null;
		}
		if (m_oUnmountReceiver != null) {
			unregisterReceiver(m_oUnmountReceiver);
			m_oUnmountReceiver = null;
		}
		if (m_oListener != null) {
			unregisterReceiver(m_oListener);
			m_oListener = null;
		}
		if (m_oVolumeChanged != null) {
			unregisterReceiver(m_oVolumeChanged);
			m_oVolumeChanged = null;
		}
		self = null;
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void sendPlayState(String state){
		Logger.i("sendPlayState", "sendPlayState:" + state);
		Intent stateIntent = new Intent(stateIntentStr);
		Bundle extras = new Bundle();
		extras.putString("state", state);
		stateIntent.putExtras(extras);
		sendBroadcast(stateIntent);
	}

//	@Override
//	protected void onHandleIntent(Intent intent) {
//		
//	}
}
