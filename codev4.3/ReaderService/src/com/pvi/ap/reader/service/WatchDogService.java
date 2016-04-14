package com.pvi.ap.reader.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * The WatchDogService provide time limit function.
 * When a thread not return the information in time,
 * the service will reboot all the system.
 * @author rd031
 *
 */

public class WatchDogService extends Service {
	
	 
	private  final static int mc_iMAX_LEN = 20;  // the max watched object number
	private long[] m_alId = null;
	private int[] m_aiFeedTime = null;
	private long[] m_alOvertime = null;
	private int m_iCount = 0;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		m_iCount = 0;
		m_alId = new long[mc_iMAX_LEN];
		m_aiFeedTime = new int[mc_iMAX_LEN];
		m_alOvertime = new long[mc_iMAX_LEN];
		Thread thread = new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				while (true) {
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					long current = System.currentTimeMillis();
					synchronized (this) {
						for (int i=0; i<m_iCount; i++) {
							if (current > m_alOvertime[i]) {
								reboot();
								break;
							}
						}  // for 
					}  // synchronized
				}  // while
			}  // run
		};  // new Thread()
		thread.start();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		if (intent.hasExtra("feed")) {
			feed(intent.getExtras().getLong("feed"));
		}
		else if (intent.hasExtra("regist")) {
			register(intent.getExtras().getLong("regist"),intent.getIntExtra("time", 2000));
		}
		else if (intent.hasExtra("unregist")) {
			unregister(intent.getExtras().getLong("unregist"));
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private void reboot() {
		Intent i = new Intent(Intent.ACTION_REBOOT);
		i.putExtra("nowait", 1);
		i.putExtra("interval", 1);
		i.putExtra("window", 0);
		sendBroadcast(i); 
	}
	private synchronized void feed(long feeder) {
		for (int i=0; i<m_iCount; i++) {
			if (feeder == m_alId[i]) {
				m_alOvertime[i] = System.currentTimeMillis() + m_aiFeedTime[i];
				break;
			}
		}
	}
	private synchronized void register(long feeder, int time) {
		if (m_iCount < mc_iMAX_LEN) {
			m_alId[m_iCount] = feeder;
			m_aiFeedTime[m_iCount] = time;
			m_alOvertime[m_iCount] = System.currentTimeMillis() + m_aiFeedTime[m_iCount];
			m_iCount++;
		}
		else
			reboot();
	}
	private synchronized void unregister(long feeder) {
		for (int i=0; i<m_iCount; i++) {
			if (feeder == m_alId[i]) {
				m_iCount--;
				for (int j=i; j<m_iCount; j++) {
					m_alId[j] = m_alId[j+1];
					m_aiFeedTime[j] = m_aiFeedTime[j+1];
					m_alOvertime[j] = m_alOvertime[j+1];
				}
				break;
			}
		}
	}
}


