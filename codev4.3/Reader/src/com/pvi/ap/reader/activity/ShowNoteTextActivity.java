package com.pvi.ap.reader.activity;

import java.sql.Date;
import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.listener.ListenerFactory;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;

import com.pvi.ap.reader.data.common.EPDRefresh;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.NotesInfo;

/**
 * 
 * @author rd045 kizan 2011.01.13
 * 
 */
public class ShowNoteTextActivity extends PviActivity {

	//private int themeNum;
	private int mode;

	private TextView confirmButton;

	private TextView deleteButton;

	private TextView modifyButton;
	
    private ImageView line=null;
	private TextView timeView;

	private EditText titleView;

	private EditText contentView;

	private PviAlertDialog m_dialog;

	private final static int ADD_SUCCESS = 0x01;

	private final static int MODIFY_SUCCESS = 0x02;

	private final static int RENAME_SUCCESS = 0x03;

	private final static int DELETE_SUCCESS = 0x04;

	private final static int DELETE_CONFIRM = 0x05;

	private final static int INVALID_TITLE = 0x06;

	private final static int INVALID_CONTENT = 0x07;
	private final static int INVALID_NAME = 0x08;

	private final static int ON_WORKING = 0x100;

	private final static int BACK = 0x00;
	private String id;
    private String c_title=null;
	private TextView cancelButton;
    private RelativeLayout layout=null;
	// get the flag from start intent to determine the mode if start from other
	// activity eg : MyNoteActivity
	public static final int MODE_ADD = 1;
	public static final int MODE_SHOW = 2;
	public static final int MODE_MODIFY = 3;
	public static final int MODE_RENAME = 4;

	// the handler to control the UI dialog
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dismissDialog();
			switch (msg.what) {
			// go back
			case BACK:
				Intent intent = new Intent(MainpageActivity.BACK);
				sendBroadcast(intent);
				Logger.v("ShowNoteActivity", "Back to Note List");
				break;
			// add note success tip
			case ADD_SUCCESS:
				showSuccessMsg("增加便笺成功！");
				break;
			// modify note success tip
			case MODIFY_SUCCESS:
				showSuccessMsg("便笺修改成功！");
				break;
			// rename the note
			case RENAME_SUCCESS:
				showSuccessMsg("重命名成功");
				break;
			// delete note success tip
			case DELETE_SUCCESS:
				showSuccessMsg("删除便笺成功！");
				break;
			// confirm delete note tip
			case DELETE_CONFIRM:
				deleteNoteTip();
				break;
			// note title invalid tip
			case INVALID_TITLE:
				showInValidMsg("便笺标题不合法", titleView);
				break;
			case INVALID_NAME:
				showInValidMsg("便笺名字不能相同", titleView);
				break;
			// note content invalid tip
			case INVALID_CONTENT:
				showInValidMsg("便笺内容不合法", contentView);
				break;
			// on working tips
			case ON_WORKING:
				onWorkingTip();
				break;
			}

		}

	};

	private void showInValidMsg(String msg, final View focusView) {
		m_dialog = new PviAlertDialog(getParent());
		m_dialog.setTitle(getResources().getString(R.string.bookIntentPrompt));
		m_dialog.setMessage(msg);
		m_dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						focusView.requestFocus();
						return;
					}

				});
		m_dialog.show();

	}

	public void deleteNoteTip() {
		m_dialog = new PviAlertDialog(getParent());
		m_dialog.setTitle("删除提示");
		m_dialog.setMessage("确定删除此便笺 ?");
		m_dialog.setCanClose(true);
		m_dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.sendEmptyMessage(ON_WORKING);
						String d_where = NotesInfo._ID + "='" + id + "'";
						getContentResolver().delete(NotesInfo.CONTENT_URI,
								d_where, null);
						handler.sendEmptyMessage(DELETE_SUCCESS);

						return;
					}

				});
		m_dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "取消",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						m_dialog.dismiss();
						return;
					}

				});
		m_dialog.show();

	}

	/**
	 * show success msg dialog
	 * 
	 * 
	 * @param msg
	 */
	private void showSuccessMsg(String msg) {
		m_dialog = new PviAlertDialog(getParent());
		m_dialog.setTitle(getResources().getString(R.string.bookIntentPrompt));
		m_dialog.setMessage(msg);
		m_dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.sendEmptyMessage(BACK);
						return;
					}

				});
		m_dialog.show();
	}

	private void onWorkingTip() {
		m_dialog = new PviAlertDialog(getParent());
		m_dialog.setTitle(getResources().getString(R.string.my_holdon));
		m_dialog.setMessage(getResources().getString(R.string.my_work));
		// dialog.setHaveProgressBar(true);
		m_dialog.show();
	}

	/**
	 * dismiss the dialog
	 */
	private void dismissDialog() {
		if (m_dialog != null && m_dialog.isShowing()) {
			m_dialog.dismiss();
		}
	}

	/**
	 * check the note data whether correct or not return true if all correct
	 * else false
	 * 
	 * @return
	 */
	private boolean checkContent() {
		String titleText = titleView.getText().toString();
		String contentText = titleView.getText().toString();
		if ("".equals(titleText)) {
			handler.sendEmptyMessage(INVALID_TITLE);
			return false;
		} else if(checkName(titleText)){
			if(c_title!=null){
				if(c_title.equals(titleText)){
					c_title=null;
					return true;
				}
			}
			handler.sendEmptyMessage(INVALID_NAME);
			return false;
		}else if ("".equals(contentText)) {
			handler.sendEmptyMessage(INVALID_CONTENT);
			return false;
		}
		return true;
	}
  public boolean checkName(String name){
	 boolean flag=false; 
	 String columns[]=new String[]{
			 NotesInfo._ID,
			 NotesInfo.NoteName
	 };
	 Cursor cur = null;
	 try{
	 String where=NotesInfo.NoteName+"='"+name+"'";
	cur=getContentResolver().query(NotesInfo.CONTENT_URI, columns, where, null, null);
	if(cur.getCount()>0){
		flag=true;
	}
	 }catch(Exception e){
		 
	 }finally{
		 if(cur!=null){
			 cur.close();
		 }
	 }
	 return flag;
  }
	/**
	 * the click listener for buttons
	 */
	private View.OnClickListener btnClickListener = new View.OnClickListener() {
		// TODO : should we catch the exception case ?
		@Override
		public void onClick(View view) {
			int vId = view.getId();
			switch (vId) {
			case R.id.modifyButton:
				enableTitle();
				enableContent();
				enableButton();
				showTip(View.VISIBLE);
				mode = MODE_MODIFY;
				break;
			case R.id.deleteButton:
				handler.sendEmptyMessage(DELETE_CONFIRM);
				break;
			// back button
			case R.id.cancelButton:
				handler.sendEmptyMessage(BACK);
				break;
			case R.id.confirmButton:

				if (mode == MODE_SHOW) {
					handler.sendEmptyMessage(BACK);
				} else {
					if (!checkContent()) {
						return;
					}
					// show tips
					//handler.sendEmptyMessage(ON_WORKING);
					SimpleDateFormat formatter = new SimpleDateFormat(
							"MM/dd/yyyy HH:mm:ss");
					Date CurTime = new Date(System.currentTimeMillis());
					final String now = formatter.format(CurTime);
					final ContentValues values = new ContentValues();
					final String noteTitle = titleView.getText().toString();
					final String noteContent = contentView.getText().toString();
					values.put(NotesInfo.NoteName, noteTitle);
					values.put(NotesInfo.NoteText, noteContent);
					values.put(NotesInfo.CreateDate, now);
					if (mode == MODE_MODIFY) {
						String r_where = NotesInfo._ID + "='" + id + "'";
						getContentResolver().update(NotesInfo.CONTENT_URI,
								values, r_where, null);
						//handler.sendEmptyMessage(MODIFY_SUCCESS);
						handler.sendEmptyMessage(BACK);
					} else if (mode == MODE_RENAME) {
						String r_where = NotesInfo._ID + "='" + id + "'";
						getContentResolver().update(NotesInfo.CONTENT_URI,
								values, r_where, null);
						handler.sendEmptyMessage(BACK);
						//handler.sendEmptyMessage(RENAME_SUCCESS);
					} else if (mode == MODE_ADD) {
						getContentResolver().insert(NotesInfo.CONTENT_URI,
								values);
						//handler.sendEmptyMessage(ADD_SUCCESS);
						handler.sendEmptyMessage(BACK);
					}
				}
				break;
			}
		}
	};

	@Override
	public OnUiItemClickListener getMenuclick() {
		return this.menuclick;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 控制全屏

		GlobalVar appState = ((GlobalVar) getApplicationContext());
		
		
			setContentView(R.layout.shownote_ui1);
		

		super.onCreate(savedInstanceState);

		Bundle notebundle = this.getIntent().getExtras();

		mode = notebundle.getInt("note_mode");
        
		confirmButton = (TextView) findViewById(R.id.confirmButton);
		confirmButton.setOnClickListener(btnClickListener);

		deleteButton = (TextView) findViewById(R.id.deleteButton);
		deleteButton.setOnClickListener(btnClickListener);

		modifyButton = (TextView) findViewById(R.id.modifyButton);
		modifyButton.setOnClickListener(btnClickListener);

		cancelButton = (TextView) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(btnClickListener);

		titleView = (EditText) findViewById(R.id.title);
		contentView = (EditText) findViewById(R.id.content);
		timeView = (TextView) findViewById(R.id.time);
		layout=(RelativeLayout)findViewById(R.id.RelativeLayout02);
		line=(ImageView)findViewById(R.id.ImageView01);
		line.setVisibility(View.VISIBLE);
		// add new note mode hidden two content relative buttons
		if (mode == MODE_ADD) {
			enableButton();
			enableTitle();
			enableContent();
			line.setVisibility(View.INVISIBLE);
			showTip(View.VISIBLE);
		} else {
			String title = notebundle.getString("note_name");
			c_title=title;
			String text = notebundle.getString("note_text");
			String time = notebundle.getString("note_time");
			id = notebundle.getString("note_id");
			titleView.setText(title);
			contentView.setText(text);
			timeView.setText(time);
			
			if (mode == MODE_MODIFY || mode == MODE_RENAME) {
				enableButton();
				if (mode != MODE_RENAME) {
					
					enableContent();
				}
				enableTitle();
				line.setVisibility(View.INVISIBLE);
				showTip(View.VISIBLE);
			} else {
				showTip(View.GONE);
				
				cancelButton.setFocusable(true);
				cancelButton.requestFocus();
				confirmButton.setVisibility(View.GONE);
			}

		}

		findViewById(R.id.catalogBtn01).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Bundle bundleToSend = new Bundle();
						Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
						bundleToSend.putString("act",  "com.pvi.ap.reader.activity.ResCenterActivity");
						bundleToSend.putString("haveTitleBar","1");
						bundleToSend.putString("startType",  "allwaysCreate"); 
						bundleToSend.putString("actTabName",  "我的文档");     
						intent.putExtras(bundleToSend);
						sendBroadcast(intent);
					}
					
				});
		findViewById(R.id.catalogBtn02).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Bundle bundleToSend = new Bundle();
						Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
						bundleToSend.putString("act",  "com.pvi.ap.reader.activity.ResCenterActivity");
						bundleToSend.putString("haveTitleBar","1");
						bundleToSend.putString("startType",  "allwaysCreate"); 
						bundleToSend.putString("actTabName",  "我的音乐");     
						intent.putExtras(bundleToSend);
						sendBroadcast(intent);
					}
					
				});
		findViewById(R.id.catalogBtn03).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Bundle bundleToSend = new Bundle();
						Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
						bundleToSend.putString("act",  "com.pvi.ap.reader.activity.ResCenterActivity");
						bundleToSend.putString("haveTitleBar","1");
						bundleToSend.putString("startType",  "allwaysCreate"); 
						bundleToSend.putString("actTabName",  "我的图片");     
						intent.putExtras(bundleToSend);
						sendBroadcast(intent);
					}
					
				});
		findViewById(R.id.catalogBtn04).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Bundle bundleToSend = new Bundle();
						Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
						bundleToSend.putString("act",  "com.pvi.ap.reader.activity.ResCenterActivity");
						bundleToSend.putString("haveTitleBar","1");
						bundleToSend.putString("startType",  "allwaysCreate"); 
						bundleToSend.putString("actTabName",  "我的便笺");     
						intent.putExtras(bundleToSend);
						sendBroadcast(intent);
					}
					
				});
		findViewById(R.id.catalogBtn05).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Bundle bundleToSend = new Bundle();
						Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
						bundleToSend.putString("act",  "com.pvi.ap.reader.activity.ResCenterActivity");
						bundleToSend.putString("haveTitleBar","1");
						bundleToSend.putString("startType",  "allwaysCreate"); 
						bundleToSend.putString("actTabName",  "本地书库");     
						intent.putExtras(bundleToSend);
						sendBroadcast(intent);
					}
					
				});
		findViewById(R.id.catalogBtn06).setOnClickListener(
				ListenerFactory.getMyAnnotationClickListner(this));
	}

	private void enableContent() {
		contentView.setFocusable(true);
		contentView.setEnabled(true);
		contentView.setBackgroundResource(R.drawable.wireless_bg);

	}

	/**
	 * enable title edit
	 */
	private void enableTitle() {
		
		titleView.setFocusable(true);
		titleView.setEnabled(true);
		titleView.setBackgroundResource(R.drawable.wireless_bg);
	}
	
	/**
	 * control to display tip
	 */
	private void showTip(int mode){
		findViewById(R.id.titleTip).setVisibility(mode);
		findViewById(R.id.contentTip).setVisibility(mode);
	}
	
	
	private void enableButton() {
		modifyButton.setVisibility(View.GONE);
		deleteButton.setVisibility(View.GONE);
		confirmButton.setVisibility(View.VISIBLE);
	}
	
	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {

            String vTag = item.id;
               if (vTag.equals("m_note")) {
                   enableContent();
                   mode = MODE_MODIFY;
               }
               closePopmenu();         
       
        }};



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		c_title=null;
		//hideInput();
	}

	public void onResume() {
		Intent tmpIntent = new Intent(
                MainpageActivity.SHOW_ME);
        Bundle bundleToSend = new Bundle();
        bundleToSend.putString("act", "com.pvi.ap.reader.activity.ResCenterActivity");//TabActivity的类名
        bundleToSend.putString("actTabName", "我的便笺");
        bundleToSend.putString("sender", ShowNoteTextActivity.this.getClass().getName()); //TAB内嵌activity类的全名
        tmpIntent.putExtras(bundleToSend);
        sendBroadcast(tmpIntent);
        tmpIntent = null;
        bundleToSend = null;
        layout.setFocusable(true);
        layout.setFocusableInTouchMode(true);
        layout.requestFocus();
		super.onResume();
		
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==event.KEYCODE_MENU){
			menupan();
			if(this.subpopmenu!=null&&!this.popmenu.isShowing()){
				this.subpopmenu.dismiss();
			}
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

}