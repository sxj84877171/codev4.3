package com.pvi.ap.reader.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.EPDRefresh;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.BookInfo;


/**
 * 本地书籍列表
 * 
 * @author rd034
 * 
 */
public class LocalBookListActivity extends PviActivity implements Pageable{

//	private TextView[] localbookname = new TextView[7];
//	private TextView[] localbookauthor = new TextView[7];
//	private TextView[] localbooksize = new TextView[7];
//	private ImageView[] localbooklayout = new ImageView[7];

	private String TypeName = "";
	private String TypeId = "";
	private String where;
	private String sort;
	private String s_bookName;
	String strGBL;
	
//	private TextView pagetxt = null;// 页码按钮
//
//	private ImageButton prepage = null;
//	private ImageButton nextpage = null;
//	private TextView cureText;

//	private ImageView[] bookicon = new ImageView[7];

	private int currentPage = 1;
	private int itemPerPage = 7;
	private int totalPage = 1;

	private TextView tv_typename = null;
	private TextView tv_hint = null;
	PviDataList listView;               //view实例
    ArrayList<PviUiItem[]> list; 
    private int mid=0;
	Handler h = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (strGBL.substring(0, 10).contains("Exception")) {
					PviAlertDialog errorDialog = new PviAlertDialog(getParent());
					errorDialog.setCanClose(true);
					errorDialog.setTitle(getResources().getString(
							R.string.bookIntentPrompt));
					errorDialog.setMessage(getResources().getString(
							R.string.my_friend_connectfailed));
					errorDialog.show();
				} else {
					Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
					Bundle sndbundle = new Bundle();
					sndbundle.putString("pviapfStatusTip", "进入无线书城...");
					msgIntent.putExtras(sndbundle);
					sendBroadcast(msgIntent);
					Intent tmpIntent = new Intent(
							MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("act",
							"com.pvi.ap.reader.activity.WirelessStoreMainpageActivity");
					//bundleToSend.putString("startType", "allwaysCreate");
					tmpIntent.putExtras(bundleToSend);
					sendBroadcast(tmpIntent);
				}
				break;

			default:
				break;
			}
			
		};
	};



	PviAlertDialog m_dialog;

	String id = null;

	private ArrayList<HashMap<String, String>> localbookinfo = new ArrayList<HashMap<String, String>>();

	protected void onResume() {
		// TODO Auto-generated method stub
		long TimeStart = System.currentTimeMillis();
		Logger.i("Time", "LocalBookListActivity" + Long.toString(TimeStart));
		super.onResume();
		

		try {
			Bundle bundle = this.getIntent().getExtras();
			if (bundle != null) {
				TypeId = bundle.getString("TypeId");
				TypeName = bundle.getString("TypeName");
				if (TypeName.equals("本地书库")) {
					s_bookName = bundle.getString("s_bookName");
					m_dialog = new PviAlertDialog(getParent());
					m_dialog.setTitle(getResources().getString(
							R.string.my_holdon));
					m_dialog.setMessage(getResources().getString(
							R.string.my_work));
					m_dialog.setHaveProgressBar(true);
					m_dialog.show();
					new Thread() {
						public void run() {
							try {
								//sleep(3000);
								booksearch(TypeId, s_bookName);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								m_dialog.dismiss();
							}
						}
					}.start();
					// System.out.println(bookname);
				}
			}
		} catch (Exception e) {
			Logger.e("BookName exception:", "bookname have not right!");
		}

		getsearchbook(TypeId, "", null);
		tv_typename.setText(TypeName);
		setData();
        showme();

	}

	private View.OnClickListener bookitemclick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			HashMap<String, String> map = null;
			HashMap<String, Object> data = new HashMap<String, Object>();
			switch (v.getId()) {
//			case R.id.Button01:
//				if (localbookname[0].getText().equals("")) {
//					return;
//				}
//				map = localbookinfo.get((currentPage - 1) * itemPerPage + 0);
//				break;
//			case R.id.Button02:
//				if (localbookname[1].getText().equals("")) {
//					return;
//				}
//				map = localbookinfo.get((currentPage - 1) * itemPerPage + 1);
//				break;
//			case R.id.Button03:
//				if (localbookname[2].getText().equals("")) {
//					return;
//				}
//				map = localbookinfo.get((currentPage - 1) * itemPerPage + 2);
//				break;
//			case R.id.Button04:
//				if (localbookname[3].getText().equals("")) {
//					return;
//				}
//				map = localbookinfo.get((currentPage - 1) * itemPerPage + 3);
//				break;
//			case R.id.Button05:
//				if (localbookname[4].getText().equals("")) {
//					return;
//				}
//				map = localbookinfo.get((currentPage - 1) * itemPerPage + 4);
//				break;
//			case R.id.Button06:
//				if (localbookname[5].getText().equals("")) {
//					return;
//				}
//				map = localbookinfo.get((currentPage - 1) * itemPerPage + 5);
//				break;
//			case R.id.Button07:
//				if (localbookname[6].getText().equals("")) {
//					return;
//				}
//				map = localbookinfo.get((currentPage - 1) * itemPerPage + 6);
//				break;

			}

			data.put("FilePath", map.get("BookPosition"));
			data.put("ChapterID", "");
			data.put("Offset", "");
			// data.put("UserID", UserID);
			data.put("FromPath", "0");
			data.put("CertPath", "DASDA");
			data.put("ContentID", map.get("ContentID"));
			OpenReader.gotoReader(LocalBookListActivity.this, data);
		}
	};
	private int themeNum;

	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.localbooklist_ui1);
        super.onCreate(savedInstanceState);
        listView= (PviDataList)findViewById(R.id.list);
		list = new ArrayList<PviUiItem[]>();
		//翻页处理
		this.showPager=true;
//		final GlobalVar app = ((GlobalVar) getApplicationContext());        
//		app.pbb.setPageable(this);
//		app.pbb.setItemVisible("prevpage", true);
//		app.pbb.setItemVisible("pagerinfo", true);
//		app.pbb.setItemVisible("nextpage", true);
		
		this.tv_hint = (TextView) this.findViewById(R.id.hint);
		this.tv_hint.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new Thread(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						strGBL = SubscribeProcess.network("getBlockList", "1",
								null, null, null);
						h.sendEmptyMessage(0);
					}
				}.start();
				
			}
		});
		this.tv_typename = (TextView) findViewById(R.id.bookclass);

		
	
		//this.pagetxt = (TextView) this.findViewById(R.id.pages);
		//cureText = (TextView) this.findViewById(R.id.curpage);
		


		//this.prepage = (ImageButton) this.findViewById(R.id.prev);
//		this.prepage.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				try {
//					if(deviceType == 1)
//						getWindow().getDecorView().getRootView().invalidate();
//					//findViewById(R.id.Button01).invalidate(0, 0, 600,800,UPDATEMODE_4);
//					if (currentPage == 1) {
//						return;
//					}
//					currentPage--;
//					setUIData();
//					
//				} catch (Exception e) {
//					Log.e("Reader", "pre page: " + e.toString());
//				}
//				return;
//			}
//		});
//		this.nextpage = (ImageButton) this.findViewById(R.id.next);
//		this.nextpage.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				try {
//					if(deviceType == 1)
//						getWindow().getDecorView().getRootView().invalidate();
//					//findViewById(R.id.Button01).invalidate(0, 0, 600,800,UPDATEMODE_4);
//					if (currentPage == totalPage) {
//						return;
//					}	
//					currentPage++;
//					setUIData();
//				} catch (Exception e) {
//					Log.e("Reader", "next page: " + e.toString());
//				}
//				return;
//			}
//		});
		
		 

		
	}

	private void setData() {
		totalPage = localbookinfo.size();
		double j = (double) totalPage / itemPerPage;
		totalPage = (int) Math.ceil(j);
        setUIData();
	}
    private void setEvent(int i){
    	HashMap<String, String> map = null;
		HashMap<String, Object> data = new HashMap<String, Object>();
		map = localbookinfo.get((currentPage - 1) * itemPerPage + i);
		data.put("FilePath", map.get("BookPosition"));
		data.put("ChapterID", "");
		data.put("Offset", "");
		// data.put("UserID", UserID);
		data.put("FromPath", "0");
		data.put("CertPath", "DASDA");
		data.put("ContentID", map.get("ContentID"));
		OpenReader.gotoReader(LocalBookListActivity.this, data);
    }
	private void setUIData() {

		list.clear();

		for (int i = 0; i < (currentPage < totalPage ? itemPerPage
				: (localbookinfo.size() - (currentPage - 1) * itemPerPage)); i++) {
			 PviUiItem[] items = new PviUiItem[]{
		                new PviUiItem("icon"+i, R.drawable.meb2, 10, 10, 50, 50, null, false, true, null),
		                new PviUiItem("text1"+i, 0, 100, 30, 200, 30, "我是一列文本", false, true, null),
		                new PviUiItem("text2"+i, 0, 540, 30, 200, 50, "我是又一列文本", false, true, null),
		                new PviUiItem("text3"+i, 0, 100, 55, 200, 25, "", false, true, null)
		        };
			 items[1].text=localbookinfo.get((currentPage - 1) * itemPerPage + i).get("Name").toString();
			 items[1].textSize=22;
			 items[2].text=localbookinfo.get((currentPage - 1) * itemPerPage + i).get("BookSize").toString();
			 items[2].textSize=19;
			 items[2].textAlign=2;
			 items[3].text=localbookinfo.get((currentPage - 1) * itemPerPage + i).get("Author").toString();
			 items[3].textSize=19;
			 final int ii=i;
			 listView.setOnRowClick(new PviDataList.OnRowClickListener() {
					
					@Override
					public void OnRowClick(View v, int rowIndex) {
						// TODO Auto-generated method stub
						 setEvent(rowIndex);
					}
				});

             list.add(items);
            

		}
		 listView.setData(list);
		if (totalPage == 0)
			totalPage = 1;
		final GlobalVar app = ((GlobalVar) getApplicationContext());        
        updatePagerinfo(currentPage+" / "+this.totalPage);
        if(deviceType==1){
		    if(mid==5){
		     mid=0;	
		  	
//		  getWindow().getDecorView().getRootView().postInvalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
		
		    }
		    }
//		pagetxt.setText(currentPage + "/" + totalPage);
		//this.pagetxt.setText(this.totalPage + "");

		//this.cureText.setText(this.currentPage + "");
		
//		this.localbooklayout[0].requestFocus();
	}

	private void booksearch(String id, String name) {

		getsearchbook(id, name, null);
		setData();
	}

	private void sortName(String id, String name, String order) {
		getsearchbook(id, name, order);
		setData();
	}

	 public void showAlert(String message){ 
	    	
		 	PviAlertDialog pd = new PviAlertDialog(getParent());
	        pd.setTitle(getResources().getString(R.string.my_friend_hint));
	        //pd.setMessage(message);
	        TextView tv = new TextView(this);
	        tv.setText(message);
	        tv.setTextSize(21);
	        tv.setGravity(Gravity.CENTER);
	        tv.setTextColor(Color.BLACK);
	        
	        pd.setView(tv);
	        pd.setCanClose(true);
	        pd.show();
	 }
	private void getsearchbook(String id, String name, String order) {
		if (android.os.Environment.getExternalStorageState() == android.os.Environment.MEDIA_UNMOUNTED) {

			showAlert("没有SD卡！");
			return;
		}

		File path = android.os.Environment.getExternalStorageDirectory();
		path = new File(path.getAbsolutePath() + File.separator + "localbook");
		path.mkdirs();
		File pathbook = new File(path.getAbsolutePath() + File.separator
				+ "book");
		pathbook.mkdir();
		File pathcert = new File(path.getAbsolutePath() + File.separator
				+ "cert");
		pathcert.mkdir();

		File[] alldocfile = null;
		alldocfile = path.listFiles();
		
		/*
		if (alldocfile == null) {
			showAlert("本地书库暂时没有书籍！");
			return;
		}*/

		String columns[] = new String[] { BookInfo._ID, BookInfo.ContentID,
				BookInfo.Name, BookInfo.Catelog, BookInfo.BookType,
				BookInfo.PathType, BookInfo.Author, BookInfo.Maker,
				BookInfo.SaleTime, BookInfo.BookPosition, BookInfo.BookSize };

		if (TypeId == null) {
			where = BookInfo.Name + " like " + "'%" + name + "%'";
		} else if (TypeId != null && name != null) {
			// where=BookInfo.Catelog+"="+"'"+TypeId+"'";
			where = BookInfo.Catelog + "=" + "'" + TypeId + "'" + " AND "
					+ BookInfo.Name + " like " + "'%" + name + "%'";
		} else
			;

		if (order == null)
			sort = null;
		else
			sort = BookInfo.Name + order;

		//System.out.println("s:" + where);
		Cursor cur = null;
		cur = managedQuery(BookInfo.CONTENT_URI, columns, where, null, sort);
		//System.out.println("s:" + cur.getCount());

		HashMap<String, String> map = null;
		int i = 0;
		this.localbookinfo.clear();
		if (cur.moveToFirst()) {
			do {
				map = new HashMap<String, String>();
				//map.put("ContentID", cur.getString(cur
				//		.getColumnIndex(BookInfo.ContentID)));
				map.put("Name", cur
						.getString(cur.getColumnIndex(BookInfo.Name)));
				Log.d("Reader", cur
						.getString(cur.getColumnIndex(BookInfo.Name)));
				map.put("Catelog", cur.getString(cur
						.getColumnIndex(BookInfo.Catelog)));
				map.put("BookType", cur.getString(cur
						.getColumnIndex(BookInfo.BookType)));
				//map.put("PathType", cur.getString(cur
				//		.getColumnIndex(BookInfo.PathType)));
				map.put("Author", cur.getString(cur
						.getColumnIndex(BookInfo.Author)));
				//map.put("Maker", cur.getString(cur
				//		.getColumnIndex(BookInfo.Maker)));
				//map.put("SaleTime", cur.getString(cur
				//		.getColumnIndex(BookInfo.SaleTime)));
				map.put("BookPosition", cur.getString(cur
						.getColumnIndex(BookInfo.BookPosition)));
				map.put("BookSize", cur.getString(cur
						.getColumnIndex(BookInfo.BookSize)));

				this.localbookinfo.add(map);
				i++;
			} while (cur.moveToNext());

		}
      if(cur!=null){
    	  cur.close();
      }
	}

	private void prevPage(){
		try {
			
			if (currentPage == 1) {
				return ;
			}
			currentPage--;
			mid++;
			setUIData();
			
		} catch (Exception e) {
			Log.e("Reader", "pre page: " + e.toString());
		}
	}
    private void nextPage(){
    	try {
			if (currentPage == totalPage) {
				return ;
			}	
			currentPage++;
			mid++;
			setUIData();
		} catch (Exception e) {
			Log.e("Reader", "next page: " + e.toString());
		}
    }
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_MENU) {

			menupan();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			sendBroadcast(new Intent(MainpageActivity.BACK));
			return true;
		}
		
//		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
//			prevPage();
//			return true;
//
//		}
//		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//            nextPage();
//			return true;
//		}

		return super.onKeyUp(keyCode, event);
	}
	
	
	@Override
	public OnUiItemClickListener getMenuclick() {
		return menuclick;
	}
	
	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {

            String vTag = item.id;
            
            if (vTag.equals("search")) {
                closePopmenu();
                LayoutInflater factory = LayoutInflater
                .from(getParent());
        final View DialogView = factory.inflate(
                R.layout.search, null);
        final EditText bookNameEditText = (EditText) DialogView
                .findViewById(R.id.keyword);
        final PviAlertDialog dialog = new PviAlertDialog(getParent());
        dialog.setTitle(getResources().getString(R.string.bookSearch));
        final TextView tv = (TextView)DialogView.findViewById(R.id.hint);
        dialog.setTitle("搜  索");
        dialog.setView(DialogView);
        dialog.setCanClose(true);
        Button search = (Button)DialogView.findViewById(R.id.searchbtn);
        // 设置自定义对话框的样式
        search.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final String s_bookName = bookNameEditText.getText().toString();
               
                getsearchbook(TypeId,s_bookName, null);
               
                tv_typename.setText(TypeName);
                dialog.dismiss();
                mid++;
                setData();
                
                
            }
        });
        bookNameEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (hasFocus) {
                    tv.setText("");
                }
            }

        });
        dialog.show();
            } else if (vTag.equals("sortbybookname")) {
                String name = s_bookName;
                String order = " DESC";
                sortName(TypeId, name, order);
                closePopmenu();
            }
               
        
        }

		};


		public void showme(){
			Intent tmpIntent = new Intent(
	                MainpageActivity.SHOW_ME);
	        Bundle bundleToSend = new Bundle();
	      
	        bundleToSend.putString("sender", this.getClass().getName()); //TAB内嵌activity类的全名
	        tmpIntent.putExtras(bundleToSend);
	        sendBroadcast(tmpIntent);
	        tmpIntent = null;
	        bundleToSend = null;
		}

		@Override
		public void OnNextpage() {
			// TODO Auto-generated method stub
			this.nextPage();
		}

		@Override
		public void OnPrevpage() {
			// TODO Auto-generated method stub
			this.prevPage();
		}
		
		

	
}
