package com.pvi.ap.reader.activity;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import com.pvi.ap.reader.activity.pviappframe.PopupWindow;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.pvi.ap.reader.activity.pviappframe.PviPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.common.QuitSortComparator;

/**
 * 本地图片
 * 
 * @author 刘剑雄
 * 
 */
public class MyImageActivity extends PviActivity implements Pageable{

	private final static String TAG="MyImageActivity";
     ArrayList<HashMap<String, Object>> picList = new ArrayList<HashMap<String, Object>>();
	
	private int pageNum = 1;// 页码
	private int count = 1;// 总页
	private int rows = 0;// 数据行数
	private int number = 7;// 每页多少记录
	
	LinearLayout noneScard = null;
	LinearLayout alertret = null;
	TextView alertmsg = null;
	private int delIndex = 0;
	private String searchkey = "";
	private String dirPath = "";
	private TextView tishi = null;
	private Bitmap bm = null;
	Handler imageHandler = null;
	//private int themeNum = 1;// 换肤参数
	private boolean flag = true;// 判断是否有sdcard
	private Button returnback=null;
	private boolean flag1 = false;// 判断是否翻页
	private Handler listHandler;
	private Thread checkUpdate;
	private QuitSortComparator sortUtil = new QuitSortComparator("isdir","name","time");
	
	private int deviceType;
	private int orderType = 1;
	private final Context mContext = MyImageActivity.this;
	private boolean refresh=false;
	private int id=0;
	PviDataList listView;               //view实例
    ArrayList<PviUiItem[]> list;  
	/**
	 * 监测sdcard的广播
	 */
	private final BroadcastReceiver broadcastRec = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			final GlobalVar app = (GlobalVar) getApplicationContext();
			if (intent.getAction()
					.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                 id++;
				//onResume();
                getPicInfo(dirPath);
                showPager();
                showPic();
                listView.requestFocus();
                flag = true;
                showme();
			}
			if (intent.getAction()
					.equals(Intent.ACTION_MEDIA_REMOVED)
					|| intent.getAction().equals(
							Intent.ACTION_MEDIA_UNMOUNTED)
					|| intent.getAction().equals(
							Intent.ACTION_MEDIA_BAD_REMOVAL))

			{
				 
				listView.setVisibility(View.GONE);
				
				noneScard.setVisibility(View.VISIBLE);
				returnback.requestFocus();
				hidePager();
				flag = false;
				return;
			}
		}
	};

	

	/**
	 * 初始化
	 */
	public void init() {
		listView= (PviDataList)findViewById(R.id.list);
		list = new ArrayList<PviUiItem[]>();
		//翻页处理
		this.showPager=true;
//		final GlobalVar app = ((GlobalVar) getApplicationContext());        
//		app.pbb.setPageable(this);
//		app.pbb.setItemVisible("prevpage", true);
//		app.pbb.setItemVisible("pagerinfo", true);
//		app.pbb.setItemVisible("nextpage", true);
		listView.setOnKeyListener(onKeyListener);
		this.tishi = (TextView) findViewById(R.id.tishi);
		this.noneScard = (LinearLayout) findViewById(R.id.none_sdcard);
		returnback = (Button) this.findViewById(R.id.returnback);
		this.alertmsg = (TextView) findViewById(R.id.alertmsg);
       
		returnback.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				sendBroadcast(new Intent(MainpageActivity.BACK));

			}
		});
		
	}

	
	

	public void setNull(int num) {
		tishi.setVisibility(View.INVISIBLE);
		
		flag1 = false;
		delIndex=0;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}

	protected void onResume() {
		// TODO Auto-generated method stub
		long TimeStart = System.currentTimeMillis();
		Logger.i("Time", "MyImageActivity" + Long.toString(TimeStart));
		showTip("进入我的图片，请稍候...",2000);
		super.onResume();
		Logger.v(TAG, "onResume");
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);

		intentFilter.addDataScheme("file");
		registerReceiver(broadcastRec, intentFilter);// 注册监听函数

		//EPDRefresh.refreshGCOnceFlash();
		
		// flag1=false;
		setNull(number);
		
		getPicInfo(dirPath);

		showPic();
		/**
	      * 重写状态栏返回
	      */
		((GlobalVar) getApplicationContext()).pbb.setOnBackListener(new OnBackListener(){

			@Override
			public boolean onBack() {
				// TODO Auto-generated method stub
				Logger.v(TAG, "flag="+flag);
				if(!flag){
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
		//showme();
	}

	protected void onPause() {
		// TODO Auto-generated method stub

		super.onPause();

		if (broadcastRec != null) {
			unregisterReceiver(broadcastRec);// 使用完注销广播监听函数
		}
		//pageNum = 1;
		searchkey = "";
		//dirPath = "";
		
		refresh=false;
		id=0;
		flag = false;
		if (listHandler != null) {
			listHandler.removeMessages(0);
		}
		
		System.gc();
	}

	public void onCreate(Bundle savedInstanceState) {
		GlobalVar appState = ((GlobalVar) getApplicationContext());
		
		deviceType=appState.deviceType;
		
			setContentView(R.layout.myimage);
		

		super.onCreate(savedInstanceState);
		Logger.v(TAG, "onCreate");
		init();

		// sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
		// Uri.parse("file://" + Environment.getExternalStorageDirectory())));
		
	}

	public String formatSize(long size) {
		double tempsize = 0.0;
		DecimalFormat df = new DecimalFormat("#.##");
		if (size / 1024 == 0) {
			tempsize = (double) size;
			return df.format(tempsize) + "B";
		} else if (size / (1024 * 1024) == 0) {
			tempsize = ((double) size) / 1024;
			return df.format(tempsize) + "KB";
		} else {
			tempsize = ((double) size) / (1024 * 1024);
			return df.format(tempsize) + "MB";
		}

	}

	public void getPicInfo(String path) {
		picList.clear();
		String dir = "";
		if (path.equals("")) {
			this.dirPath = "MyPic";
		} else {
			this.dirPath = path;
		}

		dir = android.os.Environment.getExternalStorageDirectory().getPath()
				+ "/" + this.dirPath;
		Logger.i("MyImage", dir);
		if (!(android.os.Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED))) {
			flag = false;
			showPager=false;
			listView.setVisibility(View.GONE);
			noneScard.setVisibility(View.VISIBLE);
			showme();
			return;

		}
		flag = true;
		showPager=true;
		listView.setVisibility(View.VISIBLE);
		noneScard.setVisibility(View.INVISIBLE);
		File mypic = new File(dir);
		mypic.mkdirs();

		File[] allpicfile = null;
		allpicfile = mypic.listFiles();
		if(allpicfile==null){
			handler1.sendEmptyMessage(0);
			return ;
		}
		int length = allpicfile.length;
		String temp = "";

		for (int i = 0; i < length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();

			temp = allpicfile[i].getName();
			if (temp.toLowerCase().endsWith("bmp")
					|| temp.toLowerCase().endsWith("gif")
					|| temp.toLowerCase().endsWith("jpeg")
					|| temp.toLowerCase().endsWith("jpg")
					|| temp.toLowerCase().endsWith("tiff")
					|| temp.toLowerCase().endsWith("tif")
					|| temp.toLowerCase().endsWith("png")||allpicfile[i].isDirectory()) {
				if (searchkey.equals("")) {
					map.put("name", temp);
					String url = dir + "/" + temp;
					map.put("url", url);
					Long time = allpicfile[i].lastModified();//最后修改时间
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					map.put("time", formatter.format(time));
					if (allpicfile[i].isDirectory()) {
						map.put("isdir", "true");
						map.put("size", "");
					} else if (allpicfile[i].isFile()) {
						long size = allpicfile[i].length();
						map.put("isdir", "false");
						map.put("size", size);
						// map.put("bitmap", setBitmap(url,size));
					}

					picList.add(map);
				} else {
					if (temp.toLowerCase().contains(searchkey.toLowerCase())) {
						map.put("name", temp);
						map.put("url", dir + "/" + temp);
						Long time = allpicfile[i].lastModified();
						SimpleDateFormat formatter = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						map.put("time", formatter.format(time));
						if (allpicfile[i].isDirectory()) {
							map.put("isdir", "true");
							map.put("size", "");
							//picList.add(map);
						} else if (allpicfile[i].isFile()) {
							map.put("isdir", "false");
							map.put("size", allpicfile[i].length());
						}
						picList.add(map);
					}
					
				}
			}

		}
		rows = picList.size();
		Logger.v(TAG, "rows="+rows);
		double j = (double) rows / number;
		count = (int) Math.ceil(j);
		//sortUtil.setDescend2(false);
		
	}

	public void setEvent(int i) {
		
		HashMap map = picList.get((pageNum - 1) * number + i);
		final String size = map.get("size").toString();
		final String isdir = map.get("isdir").toString();
		final String name = map.get("name").toString();

		final String path = map.get("url").toString();
		
		if (isdir.equals("false")) {
			String s = OpenReader.getFileExt(name);
			if (s.toLowerCase().equals("bmp") || s.toLowerCase().equals("gif")
					|| s.toLowerCase().equals("jpeg")
					|| s.toLowerCase().equals("jpg")
					|| s.toLowerCase().equals("tiff")
					|| s.toLowerCase().equals("tif")
					|| s.toLowerCase().equals("png")) {
				//showtip("读取图片内容");
				Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
				
				Bundle sndBundle = new Bundle();
				sndBundle.putString("act",
						"com.pvi.ap.reader.activity.ShowPicActivity");
				// sndBundle.putString("haveTitleBar","1");
				sndBundle.putString("startType", "allwaysCreate");
				// //每次都create一个新实例，不设置此参数时，默认为“复用”已有的
				sndBundle.putString("path", path);
				sndBundle.putString("size", size);
				sndBundle.putString("dirPath", dirPath);
				sndBundle.putString("pviapfStatusTip", getResources()
						.getString(R.string.myimageOpen));
				sndBundle.putString("searchkey",searchkey );
				intent.putExtras(sndBundle);
				sendBroadcast(intent);
			} else {
				
				Toast.makeText(MyImageActivity.this, "对不起，不支持此格式",
						Toast.LENGTH_LONG).show();
			}
		} else {
			dirPath = dirPath + "/" + name;

			pageNum = 1;
			//setNull(number);
			
			getPicInfo(dirPath);
			Intent tmpIntent = new Intent(
					MainpageActivity.SET_TITLE);
			Bundle sndbundle = new Bundle();
			if(name.indexOf("/") != -1){
				name.replace("/", "[>>]");
			}
			sndbundle.putString("title", "我的图片[>>]"+name);
			name.replace("[>>]", "/");
			tmpIntent.putExtras(sndbundle);
			sendBroadcast(tmpIntent);
			refresh=true;
			id++;
			showPic();
		}

	}

	

	
   Handler hand=new Handler(){
	   public void handleMessage(Message msg) {
			
		   final PviAlertDialog dialog = new PviAlertDialog(getParent());
			dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
			dialog.setCanClose(true);
			dialog.setMessage("文件不存在");
			dialog.show();
           
		}
   };
	public Bitmap setBitmap(String url, String size) {
		Bitmap bm1 = null;
		try {
			if(!new File(url).exists()){
				hand.sendEmptyMessage(0);
				return null;
			}
			Logger.v("size", "size="+size);
			if (size.equals("")) {
				return bm1;
			}
			long tempsize = Long.parseLong(size);

			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inDither = true;
			if (tempsize > 2.5 * 1024 * 1024) {
				opts.inSampleSize = 8;

				bm1 = BitmapFactory.decodeFile(url, opts);
			} else if (tempsize > 1024 * 1024) {
				opts.inSampleSize = 6;

				bm1 = BitmapFactory.decodeFile(url, opts);
			} else if (tempsize > 300 * 1024) {
				opts.inSampleSize = 3;

				bm1 = BitmapFactory.decodeFile(url, opts);
			} else {
				bm1 = BitmapFactory.decodeFile(url, opts);
			}
			if(bm1!=null){
			int width = bm1.getWidth();
			int heigt = bm1.getHeight();
			if (width > 54 || heigt > 72) {
				bm1 = Bitmap.createScaledBitmap(bm1, 54, 72, true);
			} else {
				bm1 = Bitmap.createScaledBitmap(bm1, width, heigt, true);
			}
			}
		} catch (OutOfMemoryError o) {
			o.printStackTrace();
			Logger.v("bitmap", o.toString());
			System.gc();

		}

		return bm1;
	}
	private void prevPage(){
	    try {
	    	if (pageNum > 1 && count >= 2 && flag1) {
				pageNum--;
				getPicInfo(dirPath);
				refresh=true;
				id++;
				showPic();
			listView.requestFocus();
			}
        } catch (Exception e) {
            Logger.i("Reader", "pre page: " + e.toString());
        }
	}
	private void nextPage(){
	    try {
	    	if (1 < count && pageNum <= (count - 1) && flag1) {
				pageNum++;
				getPicInfo(dirPath);
				refresh=true;
				id++;
				showPic();
				listView.requestFocus();
			}
        } catch (Exception e) {
            Logger.i("Reader", "pre page: " + e.toString());
        }
	}
	private View.OnKeyListener onKeyListener = new OnKeyListener() {

		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(event.getAction()==KeyEvent.ACTION_UP){
				if(keyCode==event.KEYCODE_BACK){
	            	 
					 retBackTo();
	            	 return true; 
	            	
	             }
				return false;
			}
			
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				 
//				if (keyCode == event.KEYCODE_ENTER||keyCode == event.KEYCODE_DPAD_CENTER) {
//					setEvent(listView.mCurFoucsRow);
//					return true;
//				}
//              if (keyCode == event.KEYCODE_DPAD_LEFT) {
//					//preImage.performClick();
//            	    prevPage();
//					return true;
//				}
//
//				if (keyCode == event.KEYCODE_DPAD_RIGHT) {
//					//nextImage.performClick();
//					nextPage();
//					return true;
//				}
				
            
			}

			return false;
		}
	};

	public boolean delFolder(File f) {
		boolean flag = false;

		File fs[] = f.listFiles();

		if (fs == null) {
			f.delete();
			flag = true;
			return flag;
		}
		int length = fs.length;

		for (int i = 0; i < length; i++) {

			if (fs[i].isFile()) {
				fs[i].delete();

			} else {
				delFolder(fs[i]);
			}

		}

		f.delete();
		flag = true;
		return flag;

	}
	
	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {

            final PviAlertDialog dialog = new PviAlertDialog(getParent());
            dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
            dialog.setCanClose(true);
            String vTag = item.id;
             if(vTag.equals("back1")){
            	 if(!flag){
 					sendBroadcast(new Intent(MainpageActivity.BACK));	
 					return ;
 				}
                retBackTo();
            }
            if(!flag)
            {
//              Toast.makeText(MyImageActivity.this, "SD Card不存在！",
//                      Toast.LENGTH_LONG).show();
                dialog.setMessage("SD Card不存在");
                dialog.show();
                closePopmenu();
                return;
            }
            
             

                if (vTag.equals("delete")) {
                  
                    if (picList.size() > 0 && flag1) {
                        
                        if (list.get(listView.mCurFoucsRow)[1].text.equals("")) {
//                          Toast.makeText(MyImageActivity.this,
//                                  getResources().getString(R.string.myimagenone),
//                                  Toast.LENGTH_LONG).show();
                            dialog.setMessage(getResources().getString(R.string.myimagenone));
                            dialog.show();
                            closePopmenu();
                            
                        } else {
                            if(listView.mCurFoucsRow==-1||listView.mCurFoucsRow>=list.size()||(pageNum - 1) * number+ listView.mCurFoucsRow>=picList.size()){
                                dialog.setMessage("请选择要操作的图片");
                                dialog.show();  
                                closePopmenu();
                                    return ;
                                    }
                            final PviAlertDialog pd = new PviAlertDialog(
                                    getParent());
//                          pd.setTitle(getResources().getString(
//                                  R.string.bookDelete));
                            pd.setTitle("删  除");
                            pd.setCanClose(true);
                            pd.setMessage(getResources().getString(
                                    R.string.alert_dialog_delete_message)
                                    );

                            pd.setButton(getResources().getString(
                                    R.string.bookConfirm),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                int which) {
                                            // TODO Auto-generated method stub
                                            if (picList.get(
                                                    (pageNum - 1) * number
                                                            + listView.mCurFoucsRow)
                                                    .get("isdir").toString()
                                                    .equals("false")) {
                                                File myfile = null;
                                                myfile = new File(picList.get(
                                                        (pageNum - 1) * number
                                                                + listView.mCurFoucsRow).get(
                                                        "url").toString());

                                                myfile.delete();
                                            } else {
                                                String path = dirPath
                                                        + "/"
                                                        + picList.get(
                                                                (pageNum - 1)
                                                                        * number
                                                                        + listView.mCurFoucsRow)
                                                                .get("name")
                                                                .toString();
                                                String dir = android.os.Environment
                                                        .getExternalStorageDirectory()
                                                        .getPath()
                                                        + "/" + path;
                                                File file = new File(dir);
                                                delFolder(file);
                                            }
                                            searchkey = "";
                                            onResume();
                                        }
                                    });
                            pd.setButton2(getResources().getString(
                                    R.string.bookCancel),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                int which) {
                                            // TODO Auto-generated method stub

                                        }
                                    });
                            pd.show();
                        }
                    }

                } else if (vTag.equals("deleteAll")) {
                    if (picList.size() > 0 && flag1) {
                        final PviAlertDialog pd = new PviAlertDialog(getParent());
                        //pd.setTitle(getResources().getString(R.string.bookDelete));
                        pd.setTitle("删除全部");
                        pd.setCanClose(true);
                        pd.setMessage(getResources().getString(R.string.playingmusicdeleteallmsg)
                                );

                        pd.setButton(
                                getResources().getString(R.string.bookConfirm),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        // TODO Auto-generated method stub

                                        for(int i=0;i<picList.size();i++){
                                            String filepic=picList.get(i).get("url").toString();
                                            deletedir(new File(filepic));
                                        }
                                        pageNum = 1;
                                        searchkey = "";
                                        onResume();
                                    }
                                });
                        pd.setButton2(
                                getResources().getString(R.string.bookCancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        // TODO Auto-generated method stub

                                    }
                                });
                        pd.show();
                    }
                    closePopmenu(); 
                }

                else if (vTag.equals("search")) {

                    if (flag1) {

                        LayoutInflater inflater = LayoutInflater.from(getParent());

                        final View view = inflater.inflate(R.layout.search, null);
                        final PviAlertDialog pd = new PviAlertDialog(getParent());
                        pd.setView(view);
                        pd.setCanClose(true);
                        pd.setTitle("搜  索");
                        //pd.setTitle(getResources().getString(R.string.bookSearch));
                        // final TextView tv =
                        // (TextView)view.findViewById(R.id.hint);
                        final EditText edt = (EditText) view
                                .findViewById(R.id.keyword);
                        Button search = (Button) view.findViewById(R.id.searchbtn);

                        search.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                searchkey = edt.getText().toString();
                                
                                pageNum = 1;
                                getPicInfo(dirPath);
                                //setNull(number);
                                pd.dismiss();
                                
                                PviUiUtil.hideInput(v);
                                
                                refresh=true;
                                id++;
                                showPic();

                                
                                //searchkey = "";

                            }
                        });
                        
                        pd.show();
                    }

                } else if (vTag.equals("rename")) {
                    //              
                    if (picList.size() > 0 && flag1) {
                        if (list.get(listView.mCurFoucsRow)[1].text.equals("")) {
//                          Toast.makeText(MyImageActivity.this,
//                                  getResources().getString(R.string.myimagenone),
//                                  Toast.LENGTH_LONG).show();
                            dialog.setMessage(getResources().getString(R.string.myimagenone));
                            dialog.show();
                            closePopmenu();
                        } else {
                            if(listView.mCurFoucsRow==-1||listView.mCurFoucsRow>=list.size()||(pageNum - 1) * number+ listView.mCurFoucsRow>=picList.size()){
                                dialog.setMessage("请选择要操作的图片");
                                dialog.show();  
                                closePopmenu();
                                    return ;
                                    }
                            final String name = picList.get(
                                    (pageNum - 1) * number + listView.mCurFoucsRow).get("name")
                                    .toString();
                            final String url = picList.get(
                                    (pageNum - 1) * number + listView.mCurFoucsRow).get("url")
                                    .toString();
                            final String isdir = picList.get(
                                    (pageNum - 1) * number + listView.mCurFoucsRow).get("isdir")
                                    .toString();
                            LayoutInflater inflater = LayoutInflater
                                    .from(getParent());
                            final View renameView = inflater.inflate(
                                    R.layout.rename, null);
                            final PviAlertDialog pd = new PviAlertDialog(
                                    getParent());

                            pd.setTitle(getResources().getString(
                                    R.string.myimagerename));
                            pd.setView(renameView);
                            pd.setCanClose(true);
                            final EditText edt = (EditText) renameView
                                    .findViewById(R.id.rename_name);
                            // final TextView tishiyu =
                            // (TextView)renameView.findViewById(R.id.tishiyu);
                            edt.setText("");
                            if (isdir.equals("false")) {
                                edt.setText(name.substring(0, name.indexOf(".")));
                            } else {
                                edt.setText(name);
                            }

                            pd.setButton(getResources().getString(
                                    R.string.bookConfirm),
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog,
                                                int which) {
                                            
                                            PviUiUtil.hideInput(getWindow().getDecorView());
                                            
                                            File myfile = null;
                                            myfile = new File(url);
                                            int i = url.indexOf(name);
                                            String pre_name = url.substring(0, i);
                                            String newName = edt.getText()
                                                    .toString();
                                            if (isdir.equals("false")) {
                                                String postfix = name
                                                        .substring(name
                                                                .lastIndexOf("."));
                                                if (newName == null) {

                                                    Toast
                                                            .makeText(
                                                                    MyImageActivity.this,
                                                                    getResources()
                                                                            .getString(
                                                                                    R.string.myimagerenamenonenull),
                                                                    Toast.LENGTH_LONG)
                                                            .show();
                                                } else if (!checkInput(newName)) {

                                                    Toast
                                                            .makeText(
                                                                    MyImageActivity.this,
                                                                    getResources()
                                                                            .getString(
                                                                                    R.string.myimagerenameerror),
                                                                    Toast.LENGTH_LONG)
                                                            .show();
                                                } else if (bijiao(newName + postfix)) {

                                                    Toast
                                                            .makeText(
                                                                    MyImageActivity.this,
                                                                    getResources()
                                                                            .getString(
                                                                                    R.string.myimagerenameno),
                                                                    Toast.LENGTH_LONG)
                                                            .show();
                                                } else {

                                                    try {

                                                        myfile.renameTo(new File(
                                                                pre_name + newName
                                                                        + postfix));

                                                        onResume();
                                                        edt.setText("");

                                                    } catch (Exception e) {
                                                        Logger
                                                                .e(
                                                                        "MyImageActivity string",
                                                                        e
                                                                                .toString());
                                                        return;
                                                    }
                                                }
                                            } else {

                                                if (newName == null) {

                                                    Toast
                                                            .makeText(
                                                                    MyImageActivity.this,
                                                                    getResources()
                                                                            .getString(
                                                                                    R.string.myimagerenamenonenull),
                                                                    Toast.LENGTH_LONG)
                                                            .show();
                                                } else if (!checkInput(newName)) {

                                                    Toast
                                                            .makeText(
                                                                    MyImageActivity.this,
                                                                    getResources()
                                                                            .getString(
                                                                                    R.string.myimagerenameerror),
                                                                    Toast.LENGTH_LONG)
                                                            .show();
                                                } else if (bijiao(newName)) {
                                                    Toast
                                                            .makeText(
                                                                    MyImageActivity.this,
                                                                    getResources()
                                                                            .getString(
                                                                                    R.string.myimagerenameno),
                                                                    Toast.LENGTH_LONG)
                                                            .show();

                                                } else {

                                                    try {

                                                        myfile
                                                                .renameTo(new File(
                                                                        pre_name
                                                                                + newName));

                                                        onResume();
                                                        edt.setText("");

                                                    } catch (Exception e) {
                                                        Logger
                                                                .e(
                                                                        "MyImageActivity string",
                                                                        e
                                                                                .toString());
                                                        return;
                                                    }
                                                }
                                            }

                                        }
                                    });
                            pd.setButton2(getResources().getString(
                                    R.string.bookCancel),
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                int which) {
                                            // TODO Auto-generated method stub
                                            edt.setText("");
                                            pd.dismiss();

                                        }
                                    });
                            pd.show();
                        }
                    }
                } else if (vTag.equals("filecompositor")) {
                    // 按文件名排序
                    //getPicInfo(dirPath);
                    orderType=1;
                    sortUtil = new QuitSortComparator("isdir","name","time");
                    sortUtil.setDescend2(false);
                    //setNull(number);
                    refresh=true;
                    id++;
                    showPic();

                } else if (vTag.equals("timecompositor")) {
                    // 按最后操作时间排序
                    //getPicInfo(dirPath);
                    orderType=2;
                    sortUtil = new QuitSortComparator("isdir","time","name");
                    sortUtil.setDescend2(true);
                    //setNull(number);
                    refresh=true;
                    id++;
                    showPic();
                }
                closePopmenu(); 
        
        }};


		private boolean retBackTo() {
			Logger.v("TAG->retBackTo->", "retBackTo") ;
			
			if(!"".equals(searchkey)){
				searchkey = "" ;
				Logger.v(dirPath, dirPath);
				getPicInfo(dirPath);
				//setNull(number);
				Intent tmpIntent = new Intent(
						MainpageActivity.SET_TITLE);
				Bundle sndbundle = new Bundle();
				sndbundle.putString("title", "我的图片");
				tmpIntent.putExtras(sndbundle);
				sendBroadcast(tmpIntent);
				refresh=true;
				id++;
				showPic();
				return  true ;
			}
			
			int lastindex = dirPath.lastIndexOf("/");
			Logger.v("lastindex", lastindex);
			
			if(lastindex != -1)
			{
				dirPath = dirPath.substring(0, lastindex);
				Logger.v("dirPath", dirPath);
				
				getPicInfo(dirPath);
				//setNull(number);
				Intent tmpIntent = new Intent(
						MainpageActivity.SET_TITLE);
				Bundle sndbundle = new Bundle();
				sndbundle.putString("title", "我的图片");
				tmpIntent.putExtras(sndbundle);
				sendBroadcast(tmpIntent);
				refresh=true;
				id++;
				showPic();
				return  true ;
			}
			sendBroadcast(new Intent(MainpageActivity.BACK));
			return true ;
		}
	public boolean checkInput(String s) {
		boolean flag = false;
		for (int i = 0; i < s.length(); i++) {
			flag = Character.isJavaIdentifierPart(s.charAt(i));

			if (flag == false)
				break;
		}
		return flag;
	}

	public boolean bijiao(String s) {

		boolean flag = false;
		for (int i = 0; i < picList.size(); i++) {
			if (i == (pageNum - 1) * number + listView.mCurFoucsRow)
				continue;
			if (s.equals(picList.get(i).get("name"))) {
				flag = true;
				break;
			}
		}

		return flag;
	}

	@Override
	public OnUiItemClickListener getMenuclick() {
		return this.menuclick;
	}
	class MyThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (rows == 0) {
				setNull(number);
				handler1.sendEmptyMessage(0);

			} else if (rows > 0 && rows <= number) {
				setPic(rows);

			} else if (rows > number) {
				if (pageNum == count) {
					if (rows % number == 0) {
						setPic(number);
					} else {
						setPic(rows % number);
					}
				} else {
					setPic(number);
				}
			}
		}

	};
	 public void setPic(int num){
		
		 setNull(number);
		 if(deviceType==1){
			  if(id==5){
				  id=0;
				Logger.v("MyImage", "off");
//				getWindow().getDecorView().getRootView().invalidate(View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
				Logger.v("MyImage", "on");
			  }
		     }
		   Bitmap bm=null;
		   list.clear();
		   for(int i=0;i<num;i++){
			   int id=(pageNum - 1) * number +i;
			   if(id<picList.size()&&picList.size()>0){
		    try{  
			   bm = setBitmap(picList.get(id).get("url").toString(),
						      picList.get(id).get("size").toString());
			  
			 // setValue(i);
			 // itemButton[delIndex].requestFocus();
			  tishi.setVisibility(View.INVISIBLE);
				//Elementvalue(i);
				 //Logger.v("num", "num="+num);
					if ((pageNum - 1) * number +i < picList.size()) {
						 PviUiItem[] items = new PviUiItem[]{
					                new PviUiItem("icon"+i, 0, 10, 10, 54, 72, null, false, true, null),
					                new PviUiItem("text1"+i, 0, 100, 30, 200, 50, "我是一列文本", false, true, null),
					                new PviUiItem("text2"+i, 0, 540, 30, 200, 50, "我是又一列文本", false, true, null),
					        };
						HashMap<String, Object> map = new HashMap<String, Object>();
						map = picList.get((pageNum - 1) * number + i);
						items[1].text=map.get("name").toString();
						items[1].textSize=22;
						if (map.get("isdir").toString().equals("true")) {
							items[0].res=R.drawable.myfolder;
							items[2].text="";
							} else {
						        if (bm != null) {
								items[0].pic=bm;
							     }
						        items[2].text=""+formatSize(Long.parseLong(map.get("size").toString()));
								items[2].textSize=19;
								items[2].textAlign=2;
                              }
						final int ii = i;
						 listView.setOnRowClick(new PviDataList.OnRowClickListener() {
								
								@Override
								public void OnRowClick(View v, int rowIndex) {
									// TODO Auto-generated method stub
									 setEvent(rowIndex);
								}
							});
//						OnClickListener l = new OnClickListener(){
//
//		                    @Override
//		                    public void onClick(View arg0) {
//		                        // TODO Auto-generated method stub
//		                        setEvent(ii);
//		                    }
//		                    
//		                };
//		                items[1].l = l;
//		                items[2].l = l;
					    list.add(items);
					}
					
			}catch(Exception e){
				e.toString();
			       }
			
			if (i == num - 1) {
				flag1 = true;
				   }	
		 }
			 listView.setData(list);
		   }
		   final GlobalVar app = ((GlobalVar) getApplicationContext());        
	       updatePagerinfo(pageNum+" / "+this.count);
		   if(!refresh){
		    showme();
		   }
		   Logger.v(TAG, "setPic");
	   }
    Handler handler1 = new Handler() {
		public void handleMessage(Message msg) {

			tishi.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			if (searchkey.equals("")) {

				tishi.setText(getResources().getString(
						R.string.noneimage));
				tishi.setFocusable(true);
				tishi.setFocusableInTouchMode(true);
				tishi.requestFocus();
				tishi.setOnKeyListener(onKeyListener);
			} else {

				tishi.setText(getResources().getString(
						R.string.nonesearch));
				tishi.setFocusable(true);
				tishi.setFocusableInTouchMode(true);
				tishi.requestFocus();
				tishi.setOnKeyListener(onKeyListener);
			}
			final GlobalVar app = ((GlobalVar) getApplicationContext());        
	        updatePagerinfo(1+" / "+1);
			showme();
		}
	};
	public void getImage(){
		 checkUpdate = new Thread(new MyThread());

			checkUpdate.start();
	}
	public void showPic() {
		
		if (flag) {
			if(orderType==1){
				sortUtil = new QuitSortComparator("isdir","name","time");
				sortUtil.setDescend2(false);	
				
				}else{
					sortUtil = new QuitSortComparator("isdir","time","name");
					sortUtil.setDescend2(true);
				}
			Collections.sort(picList,sortUtil);

		//	getImage();
			listHandler = new Handler();
			listHandler.post(new MyThread());

			if (count > 0) {
				if (pageNum > count) {
					pageNum = pageNum - 1;
				}

			} else {

			}
			
			
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
	public void showme()
	{
//		Intent intent1 = new Intent(MainpageActivity.HIDE_TIP);
//		sendBroadcast(intent1);

		Intent tmpIntent = new Intent(
				MainpageActivity.SHOW_ME);
		Bundle bundleToSend = new Bundle();
		 bundleToSend.putString("act", "com.pvi.ap.reader.activity.ResCenterActivity");
	        bundleToSend.putString("actTabName", "我的图片");
		bundleToSend.putString("sender",this.getClass().getName()); //TAB内嵌activity类的全名
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
		tmpIntent = null;
		bundleToSend = null;
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
		public void OnNextpage() {
			// TODO Auto-generated method stub
			nextPage();
		}




		@Override
		public void OnPrevpage() {
			// TODO Auto-generated method stub
			 prevPage();
		}
}
