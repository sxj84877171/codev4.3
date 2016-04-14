package com.pvi.ap.reader.service;

import android.app.Activity;
import android.content.Intent;

/**
 * The interface class to use WatchDogService 
 * @author rd031
 *
 */
public class SoftDog {
	private Activity m_lCaller;
	/**
	 * Build a WatchDog hander class.
	 * @param oCaller Current activity, use it to seed message.
	 * @return void
	 */
	public SoftDog(Activity oCaller ){
		m_lCaller = oCaller;
	}
	/**
	 * Register specified ID and feed time.
	 * Note: The monitor try to check per 400 milliseconds.
	 * @param feeder Specify the thread ID been watched.
	 * @param time   The time want to been fed.(in milliseconds)
	 * @return void
	 */
	public void register(long lFeeder, int nTime) {
		Intent l_oIntent = new Intent("com.pvi.ap.reader.service.WatchDogService");
		l_oIntent.putExtra("regist", lFeeder);
		l_oIntent.putExtra("time", nTime);
		m_lCaller.startService(l_oIntent);
	}
	/**
	 * Register specified ID. Default feed time is 2000ms.
	 * Note: The monitor try to check per 400 milliseconds.
	 * @param feeder Specify the thread ID been watched.
	 * @return void
	 */
	public void register(long lFeeder) {
		register(lFeeder,2000);
	}
	/**
	 * Register current thread. Specify the feed time based millisecond.
	 * Note: The monitor try to check per 400 milliseconds.
	 * @param time   The time want to been fed.(in milliseconds)
	 * @return void
	 */
	public void register(int nTime) {
		register(Thread.currentThread().getId(), nTime);
	}
	/**
	 * Register current thread. Default feed time is 2000ms.
	 * Note: The monitor try to check per 400 milliseconds.
	 * @return void
	 */
	public void register() {
		register(Thread.currentThread().getId());
	}
	/**
	 * Feed food as the feeder by specified ID
	 * @return void
	 */
	public void feed(long lFeeder) {
		Intent l_oIntent = new Intent("com.pvi.ap.reader.service.WatchDogService");
		l_oIntent.putExtra("feed", lFeeder);
		m_lCaller.startService(l_oIntent);
	}
	/**
	 * Feed food as the current thread.
	 * @return void
	 */
	public void feed() {
		feed(Thread.currentThread().getId());
	}
	/**
	 * Unregister specified ID, the monitor not watch it anymore.
	 * @return void
	 */
	public void unregister(long lFeeder) {
		Intent l_oIntent = new Intent("com.pvi.ap.reader.service.WatchDogService");
		l_oIntent.putExtra("unregist", lFeeder);
		m_lCaller.startService(l_oIntent);
	}
	/**
	 * Unregister current thread, the monitor not watch it anymore.
	 * @return void
	 */
	public void unregister() {
		unregister(Thread.currentThread().getId());
	}
	/**
	 * Reboot the system.
	 * @return void
	 */
	public void reboot() {
		Intent myintent = new Intent("com.pvi.ap.reader.service.WatchDogService");
		myintent.putExtra("reboot", "now");
		m_lCaller.startService(myintent);
	}
}


