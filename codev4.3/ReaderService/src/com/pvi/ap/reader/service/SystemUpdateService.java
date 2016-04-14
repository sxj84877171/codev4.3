package com.pvi.ap.reader.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;

import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.data.common.FileHelper;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.APInfo;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * 系统升级服务<br>
 * 该类升级G3电子书升级服务
 * 
 * @since 2010-9
 * @author 孙向锦
 * @version V1.0.0
 *          <p>
 *          <p>
 *          <br>
 *          (C)Copyright 2010-2013, by www.pvi.com.tw
 * 
 */
public class SystemUpdateService extends Service { 

	/**
	 * 系统标志
	 */
	private final static String TAG = "SystemUpdateService" + "::";
	/**
	 * 系统接收广播actionname
	 */
	public final static String DAILOGACTIVENAME = "com.pvi.ap.reader.service.sxj.SystemUpdateService";
	/**
	 * 系统接收广播
	 */
	private BroadcastReceiver mDailogReceiver = null;
	/**
	 * 定时时间
	 */
	public long mTimeTaskTime = 24 * 3600 * 1000;
	/**
	 * 定时器
	 */
	private Timer mTimer = null;
	/**
	 * 客户端版本信息
	 */
	private String mClientVersion = "PVI_P801_V1.0";
	/**
	 * 客户端手机信息
	 */
	private String modelType = "018P801_20100920_001";
	/**
	 * 升级URL
	 */
	private String updateUrl = null;
	/**
	 * 是否强制升级
	 */
	private String mMustUpdate = "false";
	/**
	 * 升级的版本信息
	 */
	private String updateVersion = null;
	/**
	 * 升级提示信息
	 */
	private String message = null;
	/**
	 * 更新需要保存的信息
	 */
	private Document dom = null;
	private String apPath = "";
	
	private String receUpdate = "com.pvi.ap.reader.service.update.ok";
	/**
	 * 定时任务启动
	 */
	private TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			getCurrentVersion();// 取得当前版本信息
			if (checkUpdate()) {// 判断是否有最新版本
			// giveSystemUpdateDaioge(updateUrl,null,message);// 提示用户是否更新
				sendMessageToMainPage();
			} else {
//				updateBySdcard();
				updateBySdcardTest();
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		registerBroadcastListener();
		addbroadcastRecFilter();
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		mTimer = new Timer();
		java.util.Date date = new java.util.Date();
		mTimer.schedule(mTimerTask, date, mTimeTaskTime);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		// if (checkUpdate()) {// 判断是否有最新版本
		// giveSystemUpdateDaioge(updateUrl,null,message);// 提示用户是否更新
		// }else{
		// updateBySdcard();
		// }
	}

	@Override
	public void onDestroy() {
		Logger.d(TAG + "onDestroy()", "onDestroy()");
		if (mTimer != null) {
			while (mTimerTask.cancel())
				;
		}
		unregisterReceiver(mDailogReceiver);
		unregisterReceiver(broadcastRec);// 使用完注销广播监听函数
		super.onDestroy();

	}

	/**
	 * Pop-up message box
	 * 
	 * @return void
	 */
	public void giveSystemUpdateDaioge(String path) {
		giveSystemUpdateDaioge(path, null, null);
	}

	/**
	 * Pop-up message box
	 * 
	 * @return void
	 */
	public void giveSystemUpdateDaioge(String path, String title, String message) {
		Intent inten = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putString("path", path);
		bundle.putString("message", message);
		inten.putExtras(bundle);
		inten.putExtra("mustUpdate", Boolean.getBoolean(mMustUpdate));
		sendBroadcast(inten);
		// inten.setClass(this, DailogActive.class);
		// inten.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// startActivity(inten);
	}

	/**
	 * Objective to detect radio, registered in the dialog return information
	 * 
	 * @since 2010-9
	 */
	public void registerBroadcastListener() {

		if (mDailogReceiver == null) {
			mDailogReceiver = new BroadcastReceiver() {
				public void onReceive(Context context, final Intent intent) {
					String action = intent.getAction();
					Logger.i(TAG, action);
					if (action.equals(DAILOGACTIVENAME)) {
						Logger.i(TAG, action);
						new Thread() {
							public void run() {
								updateInfoAndInstall(intent);
							}
						}.start();
					}else if(action.equals(receUpdate)){
						Bundle extras = intent.getExtras();
						final String path = extras.getString("path");
							new Thread(){
							public void run() {
								Logger.i(TAG, "path:" + path);
								if (path != null && path.startsWith("http:")) {
									// download
									sendTip("正在下载升级文件...");
									String retPath = downloadFile(path);
									if (retPath == null) {
										sendTip("下载升级文件失败");
										return;
									}
									sendTip("正在校验升级文件...");
									// unzip
									updateApk(retPath);
								} else {
									updateApk(path);
								}
							}
							}.start();
						
					}
				}
			};
			IntentFilter iFilter = new IntentFilter();
			iFilter.addAction(DAILOGACTIVENAME);
			iFilter.addAction(receUpdate);
			registerReceiver(mDailogReceiver, iFilter);

		}
	}

	/**
	 * Check whether the latest version
	 * 
	 * @return boolean
	 */
	private boolean checkUpdate() {

		String methodeName = "checkUpdate";
		HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
		HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
		ahmNamePair.put("clientVersion", mClientVersion);
		ahmNamePair.put("modelType", modelType);
		HashMap responseMap = null;
		try {
			responseMap = CPManager.checkUpdate(ahmHeaderMap, ahmNamePair);
		} catch (HttpException e) {
			Logger.e(TAG + methodeName, e);
			return false;
		} catch (IOException e) {
			Logger.e(TAG + methodeName, e);
			return false;
		}
		if (responseMap == null) {
			return false;
		}
		if (responseMap.get("result-code") == null) {
			return false;
		}
		String resultCode = responseMap.get("result-code").toString();
		if (resultCode.indexOf("3152") != -1) {
			return false;
		} else if (resultCode.indexOf("3215") != -1) {
			return false;
		} else if (resultCode.indexOf("result-code 0") != -1) {
			byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
			try {
				dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);
				Element root = dom.getDocumentElement();
				Node node = root.getElementsByTagName("mustUpdate").item(0);
				mMustUpdate = node.getFirstChild().getNodeValue();
				Node updateUrlNode = root.getElementsByTagName("updateUrl")
						.item(0);
				updateUrl = updateUrlNode.getFirstChild().getNodeValue();
				Node updateVersionNode = root.getElementsByTagName(
						"updateVersion").item(0);
				updateVersion = updateVersionNode.getFirstChild()
						.getNodeValue();
				NodeList updateMessageaList = root
						.getElementsByTagName("updateMessage");
				if (updateMessageaList != null) {
					Node updateMessageNode = updateMessageaList.item(0);
					if (updateMessageNode != null) {
						message = updateMessageNode.getFirstChild()
								.getNodeValue();
					}
				}
			} catch (ParserConfigurationException e) {
				Logger.e(TAG + methodeName, e);
				return false;
			} catch (SAXException e) {
				Logger.e(TAG + methodeName, e);
				return false;
			} catch (IOException e) {
				Logger.e(TAG + methodeName, e);
				return false;
			} catch (Exception e) {
				Logger.e(TAG + methodeName, e);
				return false;
			}

			return true;
		}

		return false;
	}

	/**
	 * init current version init current model type
	 */

	public void getCurrentVersion() {

	}

	/**
	 * networkConnectAvailable OK
	 * 
	 * @return
	 */
	public boolean networkConnectAvailable() {
		ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cwjManager == null) {
			return false;
		}
		if (cwjManager.getActiveNetworkInfo() == null) {
			return false;
		}

		return cwjManager.getActiveNetworkInfo().isAvailable();
	}

	private final BroadcastReceiver broadcastRec = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction()
					.equals("android.intent.action.MEDIA_MOUNTED")) {// SD卡已经成功挂载
				new Thread() {
					public void run() {
//						updateBySdcard();
						updateBySdcardTest();
					}
				}.start();

			}
		}
	};

	private void addbroadcastRecFilter() {
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addDataScheme("file");
		registerReceiver(broadcastRec, intentFilter);// 注册监听函数
	}

	private boolean hasUpdateFile(String path) {
		Logger.i(TAG, "hasUpdateFile");
		File file = new File(path);
		return file.exists();
	}

	/**
	 * 取得xml信息
	 * 
	 * @return
	 */
	public Map<String, String> getFileMessage(String fileName) {
		Logger.i(TAG, "getFileMessage start");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		map = new HashMap<String, String>();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			File file = new File(fileName);
			Document document = db.parse(file);
			Element root = document.getDocumentElement();
			Node pathNode = root.getElementsByTagName("ApPath").item(0);
			apPath = pathNode.getFirstChild().getNodeValue();
			map.put("apPath", apPath);
			Node apInfoNode = root.getElementsByTagName("ApInfo").item(0);
			String apInfo = apInfoNode.getFirstChild().getNodeValue();
			map.put("apInfo", apInfo);
			Node apVersionCodeNode = root.getElementsByTagName("ApVersionCode")
					.item(0);
			String apVersionCode = apVersionCodeNode.getFirstChild()
					.getNodeValue();
			map.put("apVersionCode", apVersionCode);
			Node apNameNode = root.getElementsByTagName("ApName").item(0);
			String apName = apNameNode.getFirstChild().getNodeValue();
			map.put("apName", apName);
			Node apVersionNameNode = root.getElementsByTagName("ApVersionName")
					.item(0);
			String apVersionName = apVersionNameNode.getFirstChild()
					.getNodeValue();
			map.put("apVersionName", apVersionName);
			Node apSizeNode = root.getElementsByTagName("ApSize").item(0);
			String apSize = apSizeNode.getFirstChild().getNodeValue();
			map.put("apSize", apSize);
			Logger.i(TAG, "getFileMessage end");
		} catch (Exception e) {
			Logger.e(TAG, e.toString());
		}
		return map;
	}
	
	
	public PackageInfo getBeenApkInfor(String packageName){
		PackageManager pm = getPackageManager();
		List<PackageInfo> list = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);
		for(PackageInfo pi : list){
			if(pi.packageName.equals(packageName)){
				Logger.e(TAG, "packageName" + pi.packageName);
				Logger.e(TAG, "versionCode" + pi.versionCode);
				Logger.e(TAG, "versionName" + pi.versionName);
				Logger.e(TAG, "sharedUserId" + pi.sharedUserId);
				Logger.e(TAG, "sharedUserLabel" + pi.sharedUserLabel);
				ApplicationInfo appInfo = pi.applicationInfo ;
				if (pi != null) {
					Logger.e(TAG, "taskAffinity" + appInfo.taskAffinity);
					Logger.e(TAG, "permission" + appInfo.permission);
					Logger.e(TAG, "processName" + appInfo.processName);
					Logger.e(TAG, "className" + appInfo.className);
					Logger.e(TAG, "descriptionRes" + appInfo.descriptionRes);
					Logger.e(TAG, "theme" + appInfo.theme);
					Logger.e(TAG, "manageSpaceActivityName" + appInfo.manageSpaceActivityName);
					Logger.e(TAG, "backupAgentName" + appInfo.backupAgentName);
					Logger.e(TAG, "sourceDir" + appInfo.sourceDir);
					Logger.e(TAG, "publicSourceDir" + appInfo.publicSourceDir);
					Logger.e(TAG, "dataDir" + appInfo.dataDir);
					Logger.e(TAG, "uid" + appInfo.uid);
				}
			}
			return pi ;
		}
		return null;
	}

	/**
	 * sd卡版本级别是否比本版本级别信息高
	 * 
	 * @param map
	 * @return
	 */
	public List<Map<String, String>> getOldAppInfo() {
		List<Map<String, String>> oldList = new ArrayList<Map<String, String>>();
		Logger.i(TAG, "List<Map<String,String>> getOldAppInfo() start");
		String[] columns = { APInfo.ApSize, APInfo.ApUpdateTime,
				APInfo.ApVersion, APInfo.ApName, APInfo.ApDeveloper };
		Cursor cur = getContentResolver().query(APInfo.CONTENT_URI, columns,
				null, null, null);
		if (cur != null && cur.moveToFirst()) {
			do {
				Map<String, String> map = new HashMap<String, String>();
				String apSize = null, apUpdateTime = null, apVersion = null;
				String apName = null, apDeveloper = null;
				apSize = cur.getString(cur.getColumnIndex(APInfo.ApSize));
				apUpdateTime = cur.getString(cur
						.getColumnIndex(APInfo.ApUpdateTime));
				apVersion = cur.getString(cur.getColumnIndex(APInfo.ApVersion));
				apName = cur.getString(cur.getColumnIndex(APInfo.ApName));
				apDeveloper = cur.getString(cur
						.getColumnIndex(APInfo.ApDeveloper));
				map.put("apSize", apSize);
				map.put("apUpdateTime", apUpdateTime);
				map.put("apVersion", apVersion);
				map.put("apName", apName);
				map.put("apDeveloper", apDeveloper);
				oldList.add(map);
			} while (cur.moveToNext());
		}
		if (cur != null && !cur.isClosed()) {
			cur.close();
		}
		return oldList;
	}

	private void saveOrUpdate(Map<String, String> tmp) {
		String[] columns = { APInfo.ApSize, APInfo.ApUpdateTime,
				APInfo.ApVersion, APInfo.ApName, APInfo.ApDeveloper };
		String apSize = tmp.get("_size");
		String apUpdateTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
				.format(new Date());
		String apVersion = tmp.get("_version");
		String apName = tmp.get("_name");
		String apDeveloper = tmp.get("_info");
		ContentValues values = new ContentValues();
		values.put(APInfo.ApDeveloper, apDeveloper);
		values.put(APInfo.ApName, apName);
		values.put(APInfo.ApUpdateTime, apUpdateTime);
		values.put(APInfo.ApSize, apSize);
		values.put(APInfo.ApVersion, apVersion);
		Cursor cur = getContentResolver().query(APInfo.CONTENT_URI, columns,
				APInfo.ApName + "= '" + apName + "'", null, null);
		if (cur != null) {
			if (cur.moveToFirst()) {
				getContentResolver().update(APInfo.CONTENT_URI, values,
						APInfo.ApName + "= '" + apName + "'", null);
			} else {
				getContentResolver().insert(APInfo.CONTENT_URI, values);
			}
			cur.close();
		}
	}

	private int compareVersion(List<Map<String, String>> oldlist,
			Map<String, String> newMess) {
		try {
			if (oldlist == null) {
				return 0;
			}
			for (Map<String, String> tmp : oldlist) {
				if (newMess.get("name") != null
						&& newMess.get("name").equals(tmp.get("apName"))) {
					String oldVersion = tmp.get("apVersion");
					String newVersion = newMess.get("version");
					float oldVersionFloat = 0;
					float newVersionFloat = 0;
					try {
						oldVersionFloat = Float.parseFloat(oldVersion);
						newVersionFloat = Float.parseFloat(newVersion);
					} catch (Exception e) {
					}
					Logger.i(TAG, "oldVersion:" + oldVersion + " newVersion:"
							+ newVersion);
					if (newVersionFloat - oldVersionFloat >= 1) {
						return 2;
					} else if (newVersionFloat - oldVersionFloat >= 0.1) {
						return 0;
					} else {
						return -1;
					}
				}
			}
		} catch (Exception e) {
			Logger.e(TAG, e);
			return -1;
		}
		return 1;
	}

	private Map<String, String> map = null;
	private List<Map<String, String>> apList = null;

	private void install(String path) {
//		Intent inten = new Intent(Intent.ACTION_VIEW);
//		inten.setDataAndType(Uri.parse("file://" + path),
//				"application/vnd.android.package-archive");
//		inten.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(inten);
		
		String cmd = " pm install -r " + path ;
		Logger.e("system",cmd);
		Bundle extras = new Bundle();
		extras.putString("cmd", cmd);
		Intent intent = new Intent("com.eink.system.set.SystemUpdateService");
		intent.putExtras(extras);
		startService(intent);
	}

	private boolean execuseCmd(String cmd) {
		Logger.i(TAG, "excuse cmd:" + cmd);
		Runtime rt = Runtime.getRuntime();
		Process p = null;
		try {
			p = rt.exec(cmd);
		} catch (IOException e) {
			Logger.e(TAG, e);
		} finally {
			if (p != null) {
				InputStream is = p.getInputStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				StringBuffer sb = new StringBuffer();
				String line = "";
				try {
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
				} catch (IOException e) {
					Logger.e(TAG, Logger.getStackTraceString(e));
				} finally {
					try {
						br.close();
						is.close();
					} catch (IOException e) {
						Logger.e(TAG, Logger.getStackTraceString(e));
					}
				}
				Logger.i(TAG, "excuse cmd result:" + sb);
				if (sb.toString().contains("success")) {
					return true;
				} else {
					return false;
				}
			}
		}

		return false;
	}

	public List<Map<String, String>> getAppInfo(String filePath) {
		Properties properties = new Properties();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			FileInputStream fin = new FileInputStream(filePath);
			properties.load(fin);
		} catch (FileNotFoundException e) {
			Logger.e(TAG, Logger.getStackTraceString(e));
		} catch (IOException e) {
			Logger.e(TAG, Logger.getStackTraceString(e));
		}

		String apk = getPropertiesByKey(properties, "apk");
		if (apk == null) {
			return null;
		}
		Logger.i(TAG, "update apk list:" + apk);
		String[] apkArr = apk.split(";");
		for (String apktmp : apkArr) {
			Map<String, String> mapInfo = new HashMap<String, String>();
			Logger.i(TAG, "the " + apktmp + "'s message load");
			mapInfo.put("name", apktmp);
			String _path = getPropertiesByKey(properties, (apktmp + "_path"));
			if (_path != null)
				mapInfo.put("path", _path);
			Logger.i(TAG, "the " + apktmp + "'s path is " + _path);
			
			String _info = getPropertiesByKey(properties, (apktmp + "_info"));
			Logger.i(TAG, "the " + apktmp + "'s info is " + _info);
			if (_info != null){
				mapInfo.put("info", _info);
			}else{
				continue ;
			}
			File file = new File(_path);
			if (!file.exists()) {
				continue;
			}
			String resultCode = md5sum(_path);
			if(!_info.trim().equals(resultCode)){
				continue ;
			}else{
				Logger.i(TAG, "the information is right");
			}
			String _version = getPropertiesByKey(properties,
					(apktmp + "_version"));
			Logger.i(TAG, "the " + apktmp + "'s version is " + _version);
			
			if (_version != null)
				mapInfo.put("version", _version);
			String _size = getPropertiesByKey(properties, (apktmp + "_size"));
			Logger.i(TAG, "the " + apktmp + "'s size is " + _size);
			String _versionReal = readApplicationInfo(_path);
			Logger.i(TAG, "_version:" + _version + "_versionReal:"
					+ _versionReal);
			if (_versionReal != null && _versionReal.equals(_version)) {
				Logger.i(TAG, "message is right !");
			} else {
				Logger.i(TAG, "message is wrong !");
			}
			mapInfo.put("size", _size);
			list.add(mapInfo);
		}
		String cmdNum = getPropertiesByKey(properties, "cmdNum");
		if (cmdNum != null && !"".equals(cmdNum)) {
			int num = 0;
			try {
				num = Integer.parseInt(cmdNum);
			} catch (NumberFormatException e) {
				num = 0;
			}
			cmd = new String[num];
			for (int i = 0; i < num; i++) {
				cmd[i] = getPropertiesByKey(properties, "cmd" + (i + 1));
			}
		}
		return list;
	}

	private String[] cmd = null;

	private void updateBySdcard() {
		String path = Constants.CON_SDCARD_PATH + "/update/update.properties";
		if (hasUpdateFile(path)) {
			Logger.i(TAG, "update file been");
			apList = getAppInfo(path);
			if (apList == null)
				return;
			List<Map<String, String>> oldlist = getOldAppInfo();
			for (int i = 0; i < apList.size(); i++) {
				Map<String, String> tmp = apList.get(i);
				if (compareVersion(oldlist, tmp) >= 0) {
					Logger.i(TAG, tmp.get("path"));
					giveSystemUpdateDaioge(tmp.get("path"));
				} else {
					apList.remove(i);
				}
			}
		}
		if (cmd != null) {
			for (String tmp : cmd) {
				if (tmp != null && !"".equals(tmp)) {
					Logger.i(TAG, "cmd(" + tmp + "):" + execuseCmd(tmp));
				}
			}
		}
	}
	
	
	private void updateBySdcardTest(){
		String retPath = Constants.CON_DOWNLOAD_PATH + File.separator + "update.zip" ;
		File file = new File(retPath);
		if(file.exists()){
//			updateApk(retPath);
			mMustUpdate = "true" ;
			updateUrl = retPath ;
			message = "SD卡升级" ;
			sendMessageToMainPage();
		}
	}

	private String readApplicationInfo(String path) {
		Logger.i(TAG, "readApplicationInfo:" + path);
		PackageManager pm = getPackageManager();
		PackageInfo packInfo = pm.getPackageArchiveInfo(path,
				PackageManager.GET_ACTIVITIES);
		if (packInfo != null) {
			String version = "" + packInfo.versionCode;
			return version;
		}
		return null;
	}

	private boolean downloadAPK(String path) {
		Logger.i(TAG, "downloadAPK--->path:" + path);
		URL u = null;
		try {
			u = new URL(path);
			URLConnection con = u.openConnection();
			int len = con.getContentLength();
			path = Constants.CON_DOWNLOAD_PATH + "Reader.apk";
			File outputFile = new File(path);
			FileDownloadThread fdt = new FileDownloadThread(u, outputFile, 0,
					len);
			fdt.start();
			while (!fdt.isFinished()) {
				Thread.sleep(1000);
				if (fdt.getRunException() != 0) {
					return false;
				}
			}
		} catch (Exception e) {
			Logger.e(TAG, e);
			return false;
		}
		return true;
	}
	
	
	private String downloadFile(String path) {
		Logger.i(TAG, "downloadAPK--->path:" + path);
		String retPath = null ;
		URL u = null;
		try {
			u = new URL(path);
			URLConnection con = u.openConnection();
			int len = con.getContentLength();
			retPath = Constants.CON_DOWNLOAD_PATH + File.separator + "update.zip" ;
			File outputFile = new File(path);
			FileDownloadThread fdt = new FileDownloadThread(u, outputFile, 0,
					len);
			fdt.start();
			while (!fdt.isFinished()) {
				Thread.sleep(1000);
				if (fdt.getRunException() != 0) {
					return null;
				}
			}
		} catch (Exception e) {
			Logger.e(TAG, e);
			return null;
		}
		return retPath;
	}

	private void updateInfoAndInstall(final Intent intent) {
		Bundle budle = intent.getExtras();
		if (budle == null)
			return;
		String path = budle.getString("path");
		Logger.i(TAG, "msg-path:" + path);
		if (path == null)
			return;
		boolean flag = true;
		if (apList != null)
			for (Map<String, String> tmp : apList) {
				if (tmp.get("path").equals(path)) {
					saveOrUpdate(tmp);
					flag = false;
					break;
				}
			}
		if (flag && !downloadAPK(path)) {
			// send message download fail
			return;
		}
		if (flag) {
			Map<String, String> tpmap = new HashMap<String, String>();
			tpmap.put("_path", path);
			tpmap.put("_version", updateVersion);
			tpmap.put("_info", message);
			tpmap.put("_name", "Reader");
			saveOrUpdate(tpmap);
		}
		install(path);
	}

	private void filecopy(String source) {
		Logger.i(TAG, "filecopy");
		String fileName = source.substring(source.lastIndexOf(File.separator),
				source.length());
		Logger.i(TAG, fileName);
		FileHelper.copy(source, Environment.getDataDirectory() + File.separator
				+ "data" + File.separator + fileName);
		String cmd = "pm install -r " + Environment.getDataDirectory()
				+ File.separator + "data" + File.separator + fileName;
		execuseCmd(cmd);
	}

	private String getPropertiesByKey(Properties properties, String key) {
//		Logger.i(TAG, "getPropertiesByKey:" + key);
		if (properties == null)
			return null;
		if (key == null)
			return null;
		String value = properties.getProperty(key);
		if (value == null)
			value = properties.getProperty(key.toUpperCase());
		if (value == null)
			value = properties.getProperty(key.toLowerCase());
		if (value == null) {
			Object[] otmp = properties.keySet().toArray();
			for (Object o : otmp) {
				if (o instanceof String) {
					if (key.toLowerCase().equals(o.toString().toLowerCase())) {
						value = properties.getProperty(o.toString());
						Logger.i(TAG, "getPropertiesByKey:" + o.toString());
						Logger.i(TAG, "getPropertiesValue:" + value);
						break;
					}
				}
			}
		}
		return value;
	}

	public static void unzip(String zipFile, String desPath) {
		Logger.i(TAG, "unzip");
		File desPathFile = new File(desPath);
		if(!desPathFile.exists()) desPathFile.mkdirs();
		OutputStream out = null;
		ZipInputStream is;
		try {
			is = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry entry = null;
			while ((entry = is.getNextEntry()) != null) {
				File f = new File(desPath + File.separator + entry.getName());
				Logger.i(TAG, f.getAbsolutePath());
				if (entry.isDirectory()) {
					f.mkdir();
				} else {
					out = new FileOutputStream(f);
					byte[] buf = new byte[1024];
					int len = 0;
					while ((len = is.read(buf)) != -1) {
						out.write(buf, 0, len);
					}
					out.close();
				}
			}
			System.out.println("end");
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	private void sendTip(String message){
		Intent intent = new Intent("com.pvi.ap.reader.mainframe.SHOW_TIP");
		Bundle sndBundle = new Bundle();
		sndBundle.putString("pviapfStatusTip", message);
		intent.putExtras(sndBundle);
		sendBroadcast(intent);
	}
	
	
	
	
	 private void sendMessageToMainPage() {
		Intent in = new Intent("com.pvi.ap.reader.service.update");
		if (mMustUpdate.equals("true")) {
			in.putExtra("mustUpdate", true);
		} else {
			in.putExtra("mustUpdate", false);
		}
		Bundle extras = new Bundle();
		Logger.i(TAG, "updateUrl:" + updateUrl);
		Logger.i(TAG, "message:" + message);
		extras.putString("path", updateUrl);
		extras.putString("message", message);
		in.putExtras(extras);
		sendBroadcast(in);
	}

	private void updateApk(String retPath) {
		unzip(retPath, Constants.CON_DOWNLOAD_PATH);
		// read file information
		String fileName = Constants.CON_DOWNLOAD_PATH + File.separator + "update" + File.separator + "update.properties" ;
		if(hasUpdateFile(fileName)){
			apList = getAppInfo(fileName);
			if(apList != null){
				for (Map<String, String> tmp : apList) {
					String path =  tmp.get("path") ;
					Logger.e(TAG, "path:" + path);
					install(path);
					try {
						Thread.sleep(120000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    	 'A', 'B', 'C', 'D', 'E', 'F' };
    
    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }
    
    
    public static String md5sum(String filename) 
    {
        InputStream fis = null;
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5 = null;
        try{
            fis = new FileInputStream(filename);
            md5 = MessageDigest.getInstance("MD5");
            while((numRead=fis.read(buffer)) > 0) {
                md5.update(buffer,0,numRead);
            }
            fis.close();
            String ret = toHexString(md5.digest());
            Logger.i(TAG, "ret:" + ret);
            return ret ;
        } catch (Exception e) {
        	Logger.e(TAG,e);
            return null;
        }finally{
        	if(fis != null){
        		try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
    }
}
