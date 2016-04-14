package com.pvi.ap.reader.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.pvi.ap.reader.data.content.NotesInfo;


/***
 * refactor by kizan 2011.01.20
 * @since 2011-04-02 @author 刘剑雄
 * 
 */
public class MyNoteActivity extends PviActivity implements Pageable{
	
	private final static String TAG="MyNoteActivity";
	private static final int NO_NOTE = 0;
	private static final int NO_CHOOSE_NOTE = 1;
	private static final int ON_WORKING = 2;
	private static final int DELETE_NOTE = 3;
	private static final int DELETE_SUCCESS = 4;
	private static final int DIMSMISS_DIALOG = 100;
    private static final long DELAY_TO_DISMISS = 1000;
    private List<String> chooseList = new ArrayList<String>();
    private TextView delBtn = null;
	private TextView addBtn = null;
	private View viewButton = null ;
	private View allChooseButton = null ;
	private int total = 0;
	private int perpage = 7;
	private int pages = 0;
	private int curpage = 1;
	private int start = 0;
	private int m_flag = 1;
	private int m_page = 0;
	private int s_flag = 0;
    private String NAME = null;
	private String order = null;
    private int dataId = -1;
    AlertDialog dlg;
    private ArrayList<String[]> notes = new ArrayList<String[]>();
    private Handler mHandler;
//	private TextView cureText;
    //private int themeNum = 1;// 换肤参数
	private RelativeLayout all=null;
	private int deviceType;
	private int m_id=0;
	//private int id=-1;
	PviDataList listView;               //view实例
    ArrayList<PviUiItem[]> list;
    PviUiItem[] items=null;
    boolean []bl=new boolean []{false,false,false,false,false,false,false};
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int msgId = msg.what;
			dismissDialog();
			switch (msgId) {
			// dialog no choosed note!
			case NO_NOTE:
				canCloseTip("温馨提示", "没有便笺，请输入");
				break;
			//
			case NO_CHOOSE_NOTE:
				canCloseTip("温馨提示", "没有选择便笺");
				break;
			// delete confirm dialog
			case DELETE_NOTE:
//				notInitiatedFocus = true;
				deleteFocusNoteTip();
				break;

			case ON_WORKING:
				onWorkingTip("操作提示", "操作进行中");
				break;

			case DIMSMISS_DIALOG:
				dismissDialog();
				break;

			}

			super.handleMessage(msg);
		}

	};

	private void dismissDialog() {
		if (pd != null && pd.isShowing()) {
			pd.dismiss();
		}
	}

	private void onWorkingTip(String title, String msg) {
		pd = new PviAlertDialog(getParent());
		pd.setTitle(title);
		pd.setMessage(msg);
		// dialog.setHaveProgressBar(true);
		pd.show();
	}

	private void canCloseTip(String title, String msg) {
		pd = new PviAlertDialog(getParent());
		pd.setTitle(title);
		pd.setMessage(msg);
		pd.setCanClose(true);
//		pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
//				new android.content.DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						pd.dismiss();
//						return;
//					}
//
//				});
		pd.show();
	}

	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//curpage = 1;
		m_id=0;
		
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		GlobalVar appState = ((GlobalVar) getApplicationContext());
		deviceType=appState.deviceType;
		setContentView(R.layout.mynote_ui1);
		listView= (PviDataList)findViewById(R.id.list);
		list = new ArrayList<PviUiItem[]>();
		 items = new PviUiItem[]{
                new PviUiItem("icon"+0, R.drawable.style2_note, 10, 3, 58, 76, null, false, true, null),
                new PviUiItem("text1"+0, 0, 100, 20, 200, 30, "", false, true, null),
                new PviUiItem("text2"+0, 0, 100, 55, 150, 25, "", false, true, null),
                new PviUiItem("text3"+0, 0, 320, 55, 150, 25, "", false, true, null),
                new PviUiItem("icon"+0, R.drawable.notcheck, 510, 30, 26, 25, null, false, true, null),
        };
		//翻页处理
		 this.showPager=true;
//		final GlobalVar app = ((GlobalVar) getApplicationContext());        
//		app.pbb.setPageable(this);
//		app.pbb.setItemVisible("prevpage", true);
//		app.pbb.setItemVisible("pagerinfo", true);
//		app.pbb.setItemVisible("nextpage", true);
		//listView.setOnKeyListener(onKeyListener);
        
		delBtn = (TextView) findViewById(R.id.deleteButton);
		addBtn = (TextView) findViewById(R.id.addButton);
		viewButton = findViewById(R.id.viewButton);
		allChooseButton = findViewById(R.id.allchooseButton);

		delBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				notInitiatedFocus = false;
				checkDeleteNote();
				change();
			}
		});

		addBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				insertMyNote();
				change();
			}
		});
		
		viewButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(chooseList.size() <= 0){
					pd = new PviAlertDialog(getParent());
					pd.setTitle("温馨提示");
					if(chooseList.size() <= 0){
						pd.setMessage("您没有选择任何便笺");
					}else{
					}
					pd.setCanClose(true);
					pd.show();
					return ;
				}
				goToShowNote(Integer.parseInt(chooseList.get(0)));
			}
		});
		
		allChooseButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(chooseList.size() == pagedNotes.size() || chooseList.size() == 7){
					clearChoose();
					for (int i = 0; i < 7 && i < pagedNotes.size(); i++) {
						//checkButton[i].setBackgroundResource(R.drawable.notcheck);
						//checkButton[i].setTag("nochoose");
						PviUiItem[] items = null;
                        items =list.get(i);
                        items[4].res=R.drawable.notcheck;
                    	list.set(i, items);
                        listView.setData(list);
					}
				} else {
					clearChoose();
					for (int i = 0; i < 7 && i < pagedNotes.size(); i++) {
						//checkButton[i].setBackgroundResource(R.drawable.check);
						//checkButton[i].setTag("choose");
						PviUiItem[] items = null;
                        items =list.get(i);
                        items[4].res=R.drawable.check;
                    	list.set(i, items);
                        listView.setData(list);
						chooseList.add("" + i);
						
					}
				}
			}
			
		});

		
    
		mHandler = new Handler();
		super.onCreate(savedInstanceState);
		
		if(getRetView() != null){
			getRetView().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					retBackTo();
				}
			});
		}
	}

	private void initFocus(int focusId) {
		
		if (pagedNotes != null && pagedNotes.size() >= 1) {
			if(listView.mCurFoucsRow <0 ){
				listView.mCurFoucsRow = 0;
				focusId = listView.mCurFoucsRow;
			}
			
			listView.requestFocus();
		}

		
	}

	@Override
	protected void onResume() {
		showTip("进入我的便笺，请稍候...",2000);
       
		super.onResume();
		NAME = null;
		order = NotesInfo.CreateDate + " DESC";
		getMyNotes();
		// get focus after click delete
		

		Intent tmpIntent = new Intent(
                MainpageActivity.SHOW_ME);
        Bundle bundleToSend = new Bundle();
        bundleToSend.putString("act", "com.pvi.ap.reader.activity.ResCenterActivity");//TabActivity的类名
        bundleToSend.putString("actTabName", "我的便笺");
        bundleToSend.putString("sender", MyNoteActivity.this.getClass().getName()); //TAB内嵌activity类的全名
        tmpIntent.putExtras(bundleToSend);
        sendBroadcast(tmpIntent);
        tmpIntent = null;
        bundleToSend = null;
        
       
		
	}
	
	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {

            String vTag = item.id;
            if (vTag.equals("d_note")) {
                checkDeleteNote();
            } else if (vTag.equals("r_note")) {
                renameMynote();
            } else if (vTag.equals("m_note")) {
                modifyMynote();
            } else if (vTag.equals("c_note")) {
                insertMyNote();
            } else if (vTag.equals("s_note")) {
                // searchMynote();
//                  TextView tv = (TextView) v;
//                  if (tv.getText().toString().equals(
//                          getResources().getString(R.string.my_note_serch))) {
                    searchMynote();
//                      tv.setText(getResources().getString(R.string.my_note_all));
//                  } else if (tv.getText().toString().equals(
//                          getResources().getString(R.string.my_note_all))) {
//                      show_all();
//                      tv
//                              .setText(getResources().getString(
//                                      R.string.my_note_serch));
//                  } else
//                      ;
            } else if (vTag.equals("sort_note")) {
                String sort = NotesInfo.NoteName + " ASC";
                my_sort(sort);
            } else if (vTag.equals("sort_time")) {
                String sort = NotesInfo.CreateDate + " DESC";
                my_sort(sort);
            }
            closePopmenu();
        
        }};
	

	private ArrayList<String[]> pagedNotes;
	private PviAlertDialog pd;

	@Override
	public OnUiItemClickListener getMenuclick() {
        
		return this.menuclick;
	}
    
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Logger.v(TAG, "0000");
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 通知框架返回上一个子activty
			return retBackTo();
		}
		change();
//		int id=this.getCurrentFocus().getId();
//		Logger.v("id", id);
//		Logger.v("R.id.list", R.id.list);
		// 左右键翻页支持
//		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
//			if(this.getCurrentFocus().getId()==R.id.list){
//			prevPage();
//			return true;
//			}
//		}
//		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//			if(this.getCurrentFocus().getId()==R.id.list){
//			nextPage();
//			return true;
//			}
//		}
		return super.onKeyUp(keyCode, event);
	}
   private void prevPage(){
	   if (total > 0 && curpage > 1) {
			curpage = curpage - 1;
			start = (curpage - 1) * perpage + 1;
			m_id++;
			Logger.v("m_id", m_id);
			setPagedData(curpage);
			clearChoose();
			initFocus(0);
		}
   }
   private void nextPage(){
	   if (total > 0 && curpage < pages) {
			curpage = curpage + 1;
			start = (curpage - 1) * perpage + 1;
			m_id++;
			Logger.v("m_id", m_id);
			setPagedData(curpage);
			clearChoose();
			initFocus(0);
		}
   }
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Logger.v(TAG, "1111");
		change();
		int id=this.getCurrentFocus().getId();
		

		 if (keyCode == KeyEvent.KEYCODE_ENTER) {
			 if(listView.mCurFoucsRow>-1&&listView.mCurFoucsRow<7){
			   checkBox();
		    return true;
			 }
		 }
		 

		return super.onKeyDown(keyCode, event);
	}

	// 插入新note
	public void insertMyNote() {
		Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("act",
				"com.pvi.ap.reader.activity.ShowNoteTextActivity");
		bundleToSend.putString("haveStatusBar", "1");
		bundleToSend.putString("startType", "allwaysCreate");
		bundleToSend.putInt("note_mode", ShowNoteTextActivity.MODE_ADD);
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);

	}

	/***
	 * check delete action
	 */
	public void checkDeleteNote() {
		NAME = null;
		if (pagedNotes.size() == 0) {
			handler.sendEmptyMessage(NO_NOTE);
		} else if (listView.mCurFoucsRow < 0) {
			handler.sendEmptyMessage(NO_CHOOSE_NOTE);
		} else {
			handler.sendEmptyMessage(DELETE_NOTE);
		}
	}

	// 删除选中note
	public void deleteFocusNoteTip() {
		
		pd = new PviAlertDialog(getParent());
		pd.setTitle("删  除");
		if(chooseList.size() <= 0){
			pd.setMessage("您没有选择任何便笺");
			pd.setCanClose(true);
			pd.show();
			return ;
		}else{
			pd.setMessage("您确认删除此文件?");
		}
		pd.setCanClose(true);
		pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						if(chooseList.size() <= 0){
							dialog.dismiss();
							return ;
						}

						//handler.sendEmptyMessage(ON_WORKING);

						StringBuffer d_where =new StringBuffer();
						d_where.append(" 1 = 2 ");
						for(int i = 0 ; i < chooseList.size() ; i ++ ){
							d_where.append(" or ").append( NotesInfo._ID + "='"
									+ pagedNotes.get(Integer.parseInt(chooseList.get(i)))[0] + "'");
						}
						getContentResolver().delete(NotesInfo.CONTENT_URI,
								d_where.toString(), null);
						if(chooseList.size()==notes.size()){
						dataId = -1;
						}
						handler.sendEmptyMessageDelayed(DELETE_SUCCESS,
								DELAY_TO_DISMISS);

						
						getMyNotes();
						change();

						
						return;
					}

				});
		pd.setButton(DialogInterface.BUTTON_NEUTRAL, "取消",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						pd.dismiss();
						return;
					}

				});
		pd.show();

	}

	// 修改note
	public void modifyMynote() {
		
		if(chooseList.size() <= 0){
			dataId = -1;
		}else{
			dataId = Integer.parseInt(chooseList.get(0));
		}
		if (pagedNotes.size() == 0) {
			handler.sendEmptyMessage(NO_NOTE);
		} else if (dataId < 0) {
			handler.sendEmptyMessage(NO_CHOOSE_NOTE);
		} else {
			String data[] = pagedNotes.get(dataId);
			Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
			Bundle bundleToSend = new Bundle();
			bundleToSend.putString("act",
					"com.pvi.ap.reader.activity.ShowNoteTextActivity");
			bundleToSend.putString("haveStatusBar", "1");
//			bundleToSend.putString("startType", "allwaysCreate");
			bundleToSend.putString("mainTitle", data[1]);
			bundleToSend.putString("note_name", data[1]);
			bundleToSend.putString("note_text", data[2]);
			bundleToSend.putString("note_time", data[3]);
			bundleToSend.putString("note_id", data[0]);
			bundleToSend.putInt("note_mode", ShowNoteTextActivity.MODE_MODIFY);
			tmpIntent.putExtras(bundleToSend);
			sendBroadcast(tmpIntent);
		}
	}

	// 重命名note
	public void renameMynote() {

		if(chooseList.size() <= 0){
			dataId = -1;
		}else{
			dataId = Integer.parseInt(chooseList.get(0));
		}
		if (pagedNotes.size() == 0) {
			handler.sendEmptyMessage(NO_NOTE);
		} else if (dataId < 0) {
			handler.sendEmptyMessage(NO_CHOOSE_NOTE);
		} else {

			dataId = Integer.parseInt(chooseList.get(0));
			String data[] = pagedNotes.get(dataId);
			Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
			Bundle bundleToSend = new Bundle();
			bundleToSend.putString("act",
					"com.pvi.ap.reader.activity.ShowNoteTextActivity");
			bundleToSend.putString("haveStatusBar", "1");
			bundleToSend.putString("mainTitle", data[1]);
			bundleToSend.putString("startType", "allwaysCreate");
			bundleToSend.putString("note_name", data[1]);
			bundleToSend.putString("note_text", data[2]);
			bundleToSend.putString("note_time", data[3]);
			bundleToSend.putString("note_id", data[0]);
			bundleToSend.putInt("note_mode", ShowNoteTextActivity.MODE_RENAME);
			tmpIntent.putExtras(bundleToSend);
			sendBroadcast(tmpIntent);
		}
	}

	// 搜索note
	public boolean searchMynote() {
			s_flag = 1;
			LayoutInflater inflater = LayoutInflater.from(getParent());
			final View view = inflater.inflate(R.layout.search, null);
			final PviAlertDialog pd = new PviAlertDialog(getParent());
			pd.setView(view);
			pd.setCanClose(true);
			pd.setTitle("搜  索");

			final TextView tv = (TextView) view.findViewById(R.id.hint);
			final EditText edt = (EditText) view.findViewById(R.id.keyword);
			Button search = (Button) view.findViewById(R.id.searchbtn);

			search.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String searchkey = edt.getText().toString();
					pd.dismiss();
					my_search(searchkey);
					
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
			change();

		return true;
	}

	public void bindEvent() {

		super.bindEvent();
	
	}

	private void goToShowNote(int index) {

		String data[] = pagedNotes.get(index);

		Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("act",
				"com.pvi.ap.reader.activity.ShowNoteTextActivity");
		bundleToSend.putString("haveStatusBar", "1");
		Logger.e("data[1]", "data[1]" + data[1]);
		bundleToSend.putString("mainTitle", data[1]);
		bundleToSend.putString("startType", "allwaysCreate");
		bundleToSend.putString("note_name", data[1]);
		bundleToSend.putString("note_text", data[2]);
		bundleToSend.putString("note_time", data[3]);
		bundleToSend.putString("note_id", data[0]);
		bundleToSend.putInt("note_mode", ShowNoteTextActivity.MODE_SHOW);

		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);

	}

	public void show_all() {
		NAME = null;
		getMyNotes();
		change();
	}

	public void my_sort(String sort) {
		order = sort;
		curpage = 1;
		if (m_flag != 0) {
			//handler.sendEmptyMessage(ON_WORKING);
			getMyNotes();
			handler.sendEmptyMessageDelayed(DIMSMISS_DIALOG, DELAY_TO_DISMISS);

		} else if (m_flag == 0) {
			Intent intent = new Intent(MainpageActivity.SHOW_TIP);
			Bundle sndBundle = new Bundle();
			sndBundle.putString("pviapfStatusTip", getResources().getString(
					R.string.my_nullnote));
			sndBundle.putString("pviapfStatusTipTime", "3000");
			intent.putExtras(sndBundle);
			sendBroadcast(intent);
		}
	}

	// 搜索便笺工作
	public void my_search(String name) {
		if (!(name.equals(""))) {
			NAME = NotesInfo.NoteName + " like " + "'%" + name + "%'";
		} else if (name.equals("")) {
			NAME = null ;//NotesInfo.NoteName + "=" + "'" + name + "'";
		}

		curpage = 1;
		getMyNotes();
		change();
		
	}

	private void getMyNotes() {
		clearChoose();
		m_id++;
		mHandler.post(doGetMyNotes);
	}

	private Runnable doGetMyNotes = new Runnable() {
		@Override
		public void run() {
			try{
			notes.clear();
             
			String columns[] = new String[] { NotesInfo._ID, NotesInfo.NoteID,
					NotesInfo.NoteName, NotesInfo.NoteText,
					NotesInfo.CreateDate };

			String where = NAME;
			Cursor cur = getContentResolver().query(NotesInfo.CONTENT_URI,
					columns, where, null, order);

			m_flag = cur.getCount();

			if (m_flag == 0) {
//				Intent intent = new Intent(MainpageActivity.SHOW_TIP);
//				Bundle sndBundle = new Bundle();
//				sndBundle.putString("pviapfStatusTip", getResources()
//						.getString(R.string.my_nullnote));
//				sndBundle.putString("pviapfStatusTipTime", "3000");
//				intent.putExtras(sndBundle);
//				sendBroadcast(intent);
			}

			if (cur.moveToFirst()) {
				do {
					String[] ts = new String[4];
					ts[0] = cur.getString(cur.getColumnIndex(NotesInfo._ID));
					ts[1] = cur.getString(cur
							.getColumnIndex(NotesInfo.NoteName));
					ts[2] = cur.getString(cur
							.getColumnIndex(NotesInfo.NoteText));
					ts[3] = cur.getString(cur
							.getColumnIndex(NotesInfo.CreateDate));
					if(ts[3] != null && !"".equals(ts[3]) && ts[3].indexOf(':') > -1){
						ts[3] = ts[3].substring(0, ts[3].lastIndexOf(':'));
					}
					notes.add(ts);
				} while (cur.moveToNext());
			} else
				;
			cur.close();
			}catch(Exception e){
				Logger.v("getData", e.toString());
			}
			total = notes.size();
			setPagedData(curpage);

		}

	};

	/**
	 * 
	 * @param page
	 *            页码从1开始
	 */
	private void setPagedData(int page) {
		
		if (notes == null) {
			invisibleNote();
			return;
		}

		// 截取指定页
		pagedNotes = new ArrayList<String[]>();
		for (int i = (page - 1) * perpage; i < ((total <= page * perpage) ? total
				: page * perpage); i++) {

			String[] ts = notes.get(i);
			pagedNotes.add(ts);

		}

		int count = pagedNotes.size();
            
		// hidden the none data view
		for (int i = perpage; i > count  ; i--) {
			//relativeLayout[i-1].setVisibility(View.INVISIBLE);
		}
		Logger.v(TAG, count);
		list.clear();
		// 显示到UI组件上
		for (int i = 0; i < count; i++) {
			String[] ts = pagedNotes.get(i);
			items=null;
			if(ts!=null&&ts[2]!=null){
				
				if(items==null){
				items = new PviUiItem[]{
		                new PviUiItem("icon"+0, R.drawable.style2_note, 10, 3, 58, 76, null, false, true, null),
		                new PviUiItem("text1"+0, 0, 100, 20, 200, 30, "", false, true, null),
		                new PviUiItem("text2"+0, 0, 100, 55, 250, 25, "", false, true, null),
		                new PviUiItem("text3"+0, 0, 300, 55, 150, 25, "", false, true, null),
		                new PviUiItem("icon"+0, R.drawable.notcheck, 510, 30, 26, 25, null, false, true, null),
		        };
				}
				items[0].id="icon"+i;
				items[1].id="text1"+i;
				items[2].id="text2"+i;
				items[3].id="text3"+i;
				items[4].id="icon"+i;
				items[1].text=ts[1];
				items[1].textSize=22;
				items[2].text=ts[3];
				items[2].textSize=19;
				items[3].text=ts[2].length()+"byte";
				items[3].textSize=19;
				items[4].res=R.drawable.notcheck;
			  
				final int ii = i;
				 listView.setOnRowClick(new PviDataList.OnRowClickListener() {
						
						@Override
						public void OnRowClick(View v, int rowIndex) {
							// TODO Auto-generated method stub
							PviUiItem[] items = null;
	                        items =list.get(listView.mCurFoucsRow);

	                    	if(items[4].res==R.drawable.notcheck){
	                    		items[4].res=R.drawable.check;
	                    		chooseList.add("" + listView.mCurFoucsRow);
	                    	}else{
	                    		items[4].res=R.drawable.notcheck;
	                    		chooseList.remove("" + listView.mCurFoucsRow);
	                    	}
	                    	list.set(listView.mCurFoucsRow, items);
	                        listView.setData(list);
						}
					});
//				OnClickListener l = new OnClickListener(){
//
//                    @Override
//                    public void onClick(View arg0) {
//                        // TODO Auto-generated method stub
//                    	PviUiItem[] items = null;
//                        items =list.get(listView.mCurFoucsRow);
//
//                    	if(items[4].res==R.drawable.notcheck){
//                    		items[4].res=R.drawable.check;
//                    		chooseList.add("" + listView.mCurFoucsRow);
//                    	}else{
//                    		items[4].res=R.drawable.notcheck;
//                    		chooseList.remove("" + listView.mCurFoucsRow);
//                    	}
//                    	list.set(listView.mCurFoucsRow, items);
//                        listView.setData(list);
//    					
//                    }
//                    
//                };
//                items[1].l = l;
//                items[2].l = l;
//                items[3].l = l;
//                items[4].l = l;
			    list.add(items);
		
			}
			listView.setData(list);
		}
		
		Logger.v("m_id", m_id);
		if(deviceType==1){
			if(m_id==5){
				m_id=0;
			Logger.v("refrsh", "off");	
//			this.getParent().getWindow().getDecorView().getRootView().postInvalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
			Logger.v("refrsh", "on");
			}
			}

		count = pagedNotes.size();
		updatePager();
        m_page = page;
		
	}

	private void updatePager() {
		// 更新分页条
		if (total == 0) {
			pages = 1;
		} else
			pages = total / perpage;
		if (total % perpage > 0) {
			pages = pages + 1;
		}
		if (curpage > pages) {
			curpage = pages;
			setPagedData(curpage);
		}
		final GlobalVar app = ((GlobalVar) getApplicationContext());        
        updatePagerinfo(curpage+" / "+this.pages);
		change();
	}
	
	private void clearChoose() {
		for(int i = 0 ; i < 7 ; i ++){
			chooseList.clear();
		}
	}
    private void checkBox(){
    	 PviUiItem[] items = null;
        items =list.get(listView.mCurFoucsRow);

    	if(items[4].res==R.drawable.notcheck){
    		items[4].res=R.drawable.check;
    		chooseList.add("" + listView.mCurFoucsRow);
    	}else{
    		items[4].res=R.drawable.notcheck;
    		chooseList.remove("" + listView.mCurFoucsRow);
    	}
    	list.set(listView.mCurFoucsRow, items);
        listView.setData(list);
		
			}
   
	
	
	private void change(){
		if(notes == null || notes.isEmpty()){
			invisibleNote();
		}else{
			visibleNote();
		}
	}

	
	private void invisibleNote(){
		View nonote = findViewById(R.id.nonote);
		nonote.setVisibility(View.VISIBLE);
		if(NAME != null && !"".equals(NAME)){
			((TextView)findViewById(R.id.nonotemessage)).setText("没有搜索到相关记录");
		}else{
			((TextView)findViewById(R.id.nonotemessage)).setText("没有相关便签记录");
		}
		listView.setVisibility(View.GONE);
		delBtn.setVisibility(View.GONE);
		addBtn.setVisibility(View.GONE);
		viewButton.setVisibility(View.GONE);
		allChooseButton.setVisibility(View.GONE);
	}
	
	private void visibleNote(){
		View nonote = findViewById(R.id.nonote);
		nonote.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		delBtn.setVisibility(View.VISIBLE);
		addBtn.setVisibility(View.VISIBLE);
		viewButton.setVisibility(View.VISIBLE);
		allChooseButton.setVisibility(View.VISIBLE);
	}
	
	private boolean retBackTo() {
		Logger.i("TAG->retBackTo->", "retBackTo") ;
		
		if(NAME != null){
			my_search("");
			return true ;
		}
		sendBroadcast(new Intent(MainpageActivity.BACK));
		return true ;
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