package com.pvi.ap.reader.activity;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviPopupWindow;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiUtil;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.Bookmark;
import com.pvi.ap.reader.data.content.CommentsInfo;
import com.pvi.ap.reader.external.txt.AddBookMark;
import com.pvi.ap.reader.external.txt.AddNote;
import com.pvi.ap.reader.external.txt.CommentInfo;
import com.pvi.ap.reader.external.txt.PageContent;
import com.pvi.ap.reader.external.txt.PageCountListSer;
import com.pvi.ap.reader.external.txt.ReadSetView;

/**
 * @modify �����
 * @creation 2010-9-1
 * @since 2010-11-15
 */
public class TxtReaderActivity extends PviActivity {
	public Spanned spanned;
	private String TAG = "TxtReaderActivity";
	
	final public int a = 100 ;

	/**
	 * �ļ�·��
	 */
	private String fileNameStr = null;

	/**
	 * ���ֱ��������ʽ
	 */
	private String gb2312 = "GB2312";
	private String utf8 = "UTF-8";
	private String defaultCode = gb2312;

	/**
	 * ��ҳ��
	 */
	private int totalPage = 1;
	/**
	 * ��ҳ����
	 */
	private String firstLine = null;
	/**
	 * ���������С
	 */
	private float codeSize = 20;
	/**
	 * �м��
	 */
	private float lineSpacingf = 5f;
	/***
	 * �м��Ŵ�
	 */
	private float lineMult = 1f;

	/**
	 * ��ע��Ϣ����
	 */
	private String piZhu = null;
	/**
	 * ��������
	 */
	private String mSearchdata = null;

	/**
	 * ��һActivity������������
	 */
	private Bundle bunde;

	/**
	 * �Ի���
	 */
	private Dialog dialogs;
	/**
	 * �˵�
	 */
	private PviPopupWindow popmenu = getPopmenu();

	/**
	 * ��ǰҳ��ʾ����
	 */
//	private TextView curpage = null;
	/**
	 * ��ҳ����ʾ����
	 */
//	private TextView pages = null;

	private boolean exception = false;

	/**
	 * ˢ�½����
	 */
	private Handler handle = null;
	/**
	 * ��ע���ݿ���
	 */
	private int addannotation = 0;
	/**
	 * �ı�����
	 */
	private String contentStr = "";
	/**
	 * ������ť
	 */
	private Button bSerch = null;
	/**
	 * �����Ի���
	 */
	private View serachs = null;
	/**
	 * ��һҳ��ť
	 */
//	private View upPage;
	/**
	 * ��һҳ��ť
	 */
//	private View nextPage;
	/**
	 * �رնԻ���ť
	 */
	private Button bEnd;
	/**
	 * �������ݿ�
	 */
	private EditText look;
	/**
	 * �Ƿ������������
	 */
	private boolean serchBool = false;
	/**
	 * ��ʾ�������
	 */
	private PageTextView contentPageView = null;
	/**
	 * ҳ�����������
	 */
	private List<Integer> pageNumInfo = new ArrayList<Integer>();
	private List<Integer> pageNumInfo1 = new ArrayList<Integer>();
	private List<Integer> pageNumInfo2 = new ArrayList<Integer>();
	/**
	 * �Ķ���ǰ��
	 */
	private int curPosition = -1;
	/**
	 * ��������ʼλ��
	 */
	private int start = 0;
	/**
	 * ���������ʼλ��
	 */
	private int sta = 0;
	/**
	 * ������Ľ���λ��
	 */
	private int end = 0x7fffff;

	/**
	 * ��ǰҳ��
	 */
	private Integer curPage = 1;
	/**
	 * ÿ�μ���Ŀ��С
	 */
	private Integer block = 1024 * 10;
	/**
	 * ��Ӳ�̶�ȡ�Ŀ��С
	 */
	private Integer size = 1024 * 5;
	/**
	 * �Ƿ�ˢ�½���
	 */
	private boolean bBeflesh = false;
	/**
	 * ҳ������߳�
	 */
	private PageThread pageThread = null;
	private PageThread1 p1 = null ;
	private PageThread2 p2 = null ;
	
	private boolean error = false ;
	/**
	 * ��ǰҳ����
	 */
	private PageContent curPageContent = null;
	/**
	 * ��һҳ����
	 */
	private PageContent nextPageContent = null;
	/**
	 * ��һҳ����
	 */
	private PageContent prePageContent = null;
	/**
	 * ��ע��Ϣ�б�
	 */
	private List<CommentInfo> commentList = null;
	/**
	 * �Ƿ���Ҫ����
	 */
	private boolean needSave = false;
	/**
	 * ���ض�����ʾ
	 */
	private PviAlertDialog openDialog = null;
	/**
	 * ��������
	 */
	private boolean openTouchPage = true;
	/**
	 * ״̬ѡ��
	 */
	private int orientation = 1;
	/**
	 * ȫ��ģʽ���� --����
	 */
	private int fullScreem = 0;

	/**
	 * Activity������
	 */
	Context txtContext = null;
	/**
	 * layout�����ļ�
	 */
	private int layoutID = 0;
	
	
	private GlobalVar app = null ;  
	
	private boolean pageOk = true ;
	
	private Intent intent = null ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
//		Debug.startMethodTracing();
		Logger.i(TAG, "onCreate");
		txtContext = TxtReaderActivity.this;
		app =  ((GlobalVar) getApplicationContext());
		fullDisplay();
		this.showPager = true;
//		Configuration newConfig = txtContext.getResources().getConfiguration();
//		orientation = newConfig.orientation ;
		switch (orientation) {
		case 1:
			layoutID = R.layout.txtfilebrowser_end;
			break;
		case 2:
			layoutID = R.layout.txtfilebrowser_orientation;
			break;
		default:
			layoutID = R.layout.txtfilebrowser_end;
		}
		setContentView(layoutID);
		intent = this.getIntent();
		super.onCreate(savedInstanceState);
		
		try {
			init();
			initMessage();
			initHandle();
			initReaderBefore();
		} catch (Exception e) {
			Logger.e(TAG, e);
			sendBroadcast(new Intent(MainpageActivity.BACK));
			return;
		}
		sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
	}


	/**
	 * �ȴ���Ϣ��
	 */
	private void openDelay() {
		openDelay(getParent());
	}

	/**
	 * �ȴ���Ϣ��
	 */
	private void openDelay(Context txtContext) {
		if (openDialog != null && openDialog.isShowing()) {
			openDialog.dismiss();
			openDialog = null;
		}
		openDialog = new PviAlertDialog(txtContext);
		openDialog.setTitle("��ܰ��ʾ");
		openDialog.setMessage(getString(R.string.txtpleasewait), Gravity.CENTER);
		openDialog.show();
	}

	@Override
	public OnUiItemClickListener getMenuclick() {
		return this.menuclick;
	}

	/***
	 * ��ʼ����Ϣ��
	 */
	private void initHandle() {
		if(error){return;}
		handle = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.getData() != null) {
					if (exception) {
						if (openDialog != null && openDialog.isShowing()) {
							Logger.i(TAG, "dismiss dialog");
							openDialog.dismiss();
						}
						return;
					}
					Bundle bundle = msg.getData();
					if (bundle.getString("msgNum") != null) {
						int switchChoose = Integer.parseInt(bundle
								.getString("msgNum"));
						switch (switchChoose) {
						case 1:
//							curpage.setText("" + curPage);
//							pages.setText("" + totalPage);
							String p = curPage + "/" + totalPage ;
							Logger.i(TAG, "pages:" + p);
							updatePagerinfo(p);
							break;
						case 2:
							gotoNextPage();
							break;
						case 5:
							refreshGUI();
						case 3:
							refreshCurpageGUI();
						case 4:
							if (openDialog != null && openDialog.isShowing()) {
								Logger.i(TAG, "dismiss dialog");
								openDialog.dismiss();
							}
//							contentPageView.requestFocus();
						}
					}
				}
				
				
				
			}
		};
	}

	/**
	 * ��Ϣ����
	 */
	private void initMessage() throws Exception {
		bunde = intent.getExtras();
		fileNameStr = bunde.getString(CommentsInfo.FilePath);
		if(fileNameStr == null){
			error = true ;
			return ;
		}
		Intent tintent = new Intent(MainpageActivity.SET_TITLE);
		Bundle tsndBundle = new Bundle();
		tsndBundle.putString("title", fileNameStr.substring(fileNameStr
				.lastIndexOf(File.separator) + 1, fileNameStr.length()));
		tintent.putExtras(tsndBundle);
		sendBroadcast(tintent);
		readcharset();
		File txtFile = new File(fileNameStr);
		if (!txtFile.exists()) {
			Intent intent = new Intent(MainpageActivity.SHOW_TIP);
			Bundle sndBundle = new Bundle();
			sndBundle.putString("pviapfStatusTip",
					getString(R.string.txtNoBookExsit));
			sndBundle.putString("pviapfStatusTipTime", "2000");
			intent.putExtras(sndBundle);
			sendBroadcast(intent);
			throw new Exception();
		}
		if (txtFile.length() > 50 * 1024 * 1024) {
			makeToast(R.string.txttoobigsize);
		}
		File file = new File(Constants.CON_FIRST_PAGE_LOCATION
				+ fileNameStr.replace("/", ".") + orientation);
		if (file.exists()) {
			contentStr = "";
			FileInputStream fs = null;
			ObjectInputStream os = null;
			try {
				fs = new FileInputStream(file);
				os = new ObjectInputStream(fs);
				long fileLen = os.readLong();
				if (txtFile.length() != fileLen) {
					throw new Exception("file is update");
				}
				int len = os.readInt();
				pageNumInfo = new ArrayList<Integer>();
				for (int i = 0; i < len; i++) {
					pageNumInfo.add(os.readInt());
				}
			} catch (Exception e) {
				Logger.i(TAG, "file is update");
				contentStr = getPageContent(0, block);
			} finally {
				if (os != null) {
					os.close();
				}
				if (fs != null) {
					fs.close();
				}
			}
		} else {
			contentStr = getPageContent(0, block);
		}
		spanned = new SpannableStringBuilder(contentStr);
		totalPage = 1;
	}

	private void makeToast(int message) {
		Toast.makeText(txtContext, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * ��ȡ�ϴν�����������Ϣ
	 */
	private void initReaderBefore() {
		if(error){return ;}
		String columns[] = new String[] { Bookmark.FontSize,
				Bookmark.BookmarkId, Bookmark.LineSpace };
		String where = Bookmark.BookmarkType + "='" + 0 + "'" + " and "
				+ Bookmark.FilePath + "='" + fileNameStr + "'"; // filenameString
		Cursor cur = managedQuery(Bookmark.CONTENT_URI, columns, where, null,
				null);
		
		if(cur == null){return ;}
		if (cur.getCount() == 1 && cur.moveToFirst()) {
			codeSize = Float.parseFloat(cur.getString(cur
					.getColumnIndex(Bookmark.FontSize)));
			lineSpacingf = Float.parseFloat(cur.getString(cur
					.getColumnIndex(Bookmark.LineSpace)));
		}
		if (cur != null && !cur.isClosed()) {
			cur.close();
		}
		if (bunde.getString("FontSize") != null
				&& !"".equals(bunde.getString("FontSize").toString().trim())) {
			codeSize = Float.parseFloat(bunde.getString("FontSize"));
		}
		contentPageView.setTextSize(codeSize);
		if (bunde.getString("LineSpace") != null
				&& !"".equals(bunde.getString("LineSpace").toString().trim())) {
			lineSpacingf = Float.parseFloat(bunde.getString("LineSpace"));
		}
		contentPageView.setLineSpacing(lineSpacingf, lineMult);
	}

	/**
	 * View������ص�����
	 */
	private void pageFinished() {
		if(error){return ;}
		Logger.i(TAG, "pageFinished");
		if (contentPageView == null) {
			return;
		}
		if (pageNumInfo.size() <= 0) {
			pageNumInfo.addAll(contentPageView.pages);
			totalPage = pageNumInfo.size();
		} else {
			totalPage = pageNumInfo.size() - 1;
		}
		if (fullScreem == 0) {
			initCurPage();
		}
		if (curPage <= totalPage) {
			refreshGUI();
			refreshCurpageGUI();
		} else {
			bBeflesh = true;
		}
		showmessageReflesh("4");
		updatePagerinfo(curPage + "/" + totalPage);
//		curpage.setText("" + curPage);
//		pages.setText("" + totalPage);
		if(orientation == 1) {
//			pageNumInfo1 = pageNumInfo ;
//			getPageNumInfo(new File(fileNameStr).length(), 2);
		}else{
//			getPageNumInfo(new File(fileNameStr).length(), 1);
//			pageNumInfo2 = pageNumInfo ;
		}
		startPageThread();

	}

	private void showmessageReflesh(String num) {
		Message msg = new Message();
		Bundle bund = new Bundle();
		bund.putString("msgNum", num);
		msg.setData(bund);
		if (handle != null)
			handle.sendMessage(msg);
	}
	
	private void showmessageReflesh(String num,int swit) {
		if(swit == orientation){
			if(orientation == 1){
				pageNumInfo = pageNumInfo1 ;
			}else{
				pageNumInfo = pageNumInfo2 ;
			}
			showmessageReflesh(num);
		}
	}

	/***
	 * ѡ����ҳ��ʾ
	 */
	private void initCurPage() {
		if(error){return ;}
		if (bunde.getString("Offset") != null) {
			try {
				int postion = Integer.parseInt(bunde.getString("Offset"));
				commentToTxtReader(postion);
			} catch (Exception e) {
				Logger.e(TAG, e);
				curPage = 1;
			}
		} else {
			curPage = 1;
		}
	}

	/**
	 * ���������ҳ�߳�
	 */
	private void startPageThread() {
		if(error){return ;}
		nullThread(pageThread);
		pageThread = new PageThread();
		pageThread.start();
		
//		nullThread(p1);
//		p1 = new PageThread1();
//		p1.start();
//		
//		nullThread(p2);
//		p2 = new PageThread2();
//		p2.start();
	}
	

	/**
	 * ������ҳ����txt�ļ� <br>
	 * ʹ�ö��ֲ����㷨����
	 */
	private void commentToTxtReader(int postion) {
		if(error){return ;}
		int s = 0, end = totalPage;
		int mid = (s + end) / 2;
		while (true) {
			if (mid >= end) {
				curPage = mid + 1;
				break;
			}
			if (mid + 1 >= pageNumInfo.size()) {
				curPage = mid + 1;
				break;
			}
			if (mid == totalPage
					|| (pageNumInfo.get(mid) <= postion && pageNumInfo
							.get(mid + 1) > postion)) {
				curPage = mid + 1;
				break;
			}
			if (postion >= pageNumInfo.get(pageNumInfo.size() - 1)) {
				// �쳣ʱ�������ļ��ƻ���,�õ��ĵ�ǰҳ����
				curPage = pageNumInfo.size() - 1;
				break;
			}
			if (pageNumInfo.get(mid) > postion) {
				end = mid - 1;
			}
			if (pageNumInfo.get(mid) < postion) {
				s = mid + 1;
			}
			mid = (s + end) / 2;
		}
	}

	/**
	 * �����
	 */
	public void bindEvent() {
		if(error){return ;}
		super.bindEvent();
		setMenuclick(menuclick);
		// pageNumView = (TextView) findViewById(R.id.TextView02);
//		curpage = (TextView) findViewById(R.id.curpage);
//		pages = (TextView) findViewById(R.id.pages);
//		pages.setText("" + totalPage);
//		upPage = (View) findViewById(R.id.prev);
//		nextPage = (View) findViewById(R.id.next);
		bSerch = (Button) findViewById(R.id.find);
		bEnd = (Button) findViewById(R.id.close);
		contentPageView = (PageTextView) findViewById(R.id.view_contents);
		serachs = findViewById(R.id.buttonLay);
		contentPageView.setTextSize(codeSize);
		contentPageView.setOnKeyListener(onKeyListener);
		look = (EditText) findViewById(R.id.serach);
		look.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if(arg1){
					((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(arg0, 0);
				}else{
				    PviUiUtil.hideInput(arg0);
				}
			}
			
		});
//		if (upPage != null) {
//			upPage.setOnClickListener(new android.view.View.OnClickListener() {
//				public void onClick(View v) {
//					closePopmenu();
//					gotoPrevPage();
////					upPage.requestFocus();
//				}
//
//			});
//		}
//		if (nextPage != null) {
//			nextPage
//					.setOnClickListener(new android.view.View.OnClickListener() {
//						public void onClick(View v) {
//							closePopmenu();
//							gotoNextPage();
//							nextPage.requestFocus();
//						}
//					});
//		}
		if (bSerch != null) {
			bSerch.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					txtSerchContent();
				}
			});
		}
		if (bEnd != null) {
			bEnd.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					closeTxtSerchDailog();
				}
			});
		}
		contentPageView.setOnTouchListener(onTouchListenerObj);
		
//		app.pbb.setPageable(pageable);
		updatePagerinfo(curPage + "/" + totalPage);
		
		if(GlobalVar.deviceType ==1) {
//			contentPageView.setUpdateMode(UPDATEMODE_4);
		}
	}
	/**
	 * ����µ���ע
	 */
	private void addNewComment() {
		if(error){return ;}
		if (sta > end) {
			int temp = sta;
			sta = end;
			end = temp;
		}
		if (commentList.size() == 0) {
			buildDialog(txtContext).show();
		} else {
			boolean flag = false;
			if (end < commentList.get(0).getStartPostion()
					|| sta > commentList.get(commentList.size() - 1)
							.getEndPostion()) {
				buildDialog(txtContext).show();
			} else {
				/*** ����ӵ���ע�Ƿ���û����ӵ���ע�� **/
				CommentInfo comm = commentList.get(0);

				for (int j = 1; j < commentList.size(); j++) {
					CommentInfo commj = commentList.get(j);
					if (end < commj.getStartPostion()
							&& sta > comm.getEndPostion()) {
						flag = true;
					}
					comm = commj;
				}
				if (flag) {
					buildDialog(txtContext).show();
				} else {
					makeToast(R.string.txtCommentHaveBeen);
				}
			}
		}
	}

	/**
	 * �˵���Ĵ���
	 */
	private OnUiItemClickListener menuclick = new OnUiItemClickListener() {

		protected void jumpPageOff() {
			Dialog dialog = bookPageDialogfile(txtContext);
			dialog.show();
		}

		protected void contentSerchLight() {

			serachs.setVisibility(View.VISIBLE);
			look.requestFocus();
		}


        @Override
        public void onUiItemClick(PviUiItem item) {
            closePopmenu();         
            String vTag = item.id;
            if (vTag.equals("navigation")) {// ���ص���ҳ��
                gotoMainActiviyPage();
            } else if (vTag.equals("JumpPage")) {// ��ת
                jumpPageOff();
            } else if (vTag.equals("WordType")) {// ����
                buildDialogautoPager(txtContext).show();
            } else if (vTag.equals("addBookMark")) {// �����ǩ
                insertTxtBookmark();
            } else if (vTag.equals("bookMarkList")) {// ���ҵ���ǩ�б�
                gotoMyBookmarPage();
            } else if (vTag.equals("TurnpageAuto")) {// �Զ���ҳ
                showDialog(10);
            } else if (vTag.equals("addannotation")) {// �����ע
                openTouchPage = false;
                addannotation = 1;
            } else if (vTag.equals("touchnote")) {// �����ע
                addannotation = 2;
                openTouchPage = false;
            } else if (vTag.equals("touchpage")) {
                addannotation = 0;
                openTouchPage = true;
            } else if (vTag.equals("showannotation")) {// ������ע
                gotoAnnotation();
            } else if (vTag.equals("search")) {// ��������
                contentSerchLight();
            } else if (vTag.equals("closeTouch")) {
                item.performClick();
            } else if (vTag.equals("fullScreem")) {
                setOpenFullScreem();
            }
        }
	};

	/**
	 * ��ת���ҵ���ǩҳ����
	 */
	private void gotoMyBookmarPage() {
		Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle sndBundle = new Bundle();
		sndBundle.putString("actID", "ACT12300");
		sndBundle.putString("SourceType", "3");
		sndBundle.putString("FilePath", fileNameStr);
		sndBundle.putString("startType", "allwaysCreate");
		intent.putExtras(sndBundle);
		sendBroadcast(intent);
	}

	/**
	 * ��ת����ע����
	 */
	private void gotoAnnotation() {
		Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
		Bundle sndbundle = new Bundle();
		sndbundle.putString("pviapfStatusTip", "�����ҵ���ע");
		msgIntent.putExtras(sndbundle);
		sendBroadcast(msgIntent);
		Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle sndBundle = new Bundle();
		sndBundle.putString("bookName", bunde.getString("FilePath").substring(
				bunde.getString("FilePath").lastIndexOf("/") + 1));
		sndBundle.putString("act", ResCenterActivity.class.getName());
		sndBundle.putString("actTabName", "�ҵ���ע");
		sndBundle.putString("haveTitleBar", "1");
		sndBundle.putString("startType", "allwaysCreate");
		intent.putExtras(sndBundle);
		sendBroadcast(intent);
	}

	/**
	 * ��ת����ҳ����
	 */
	private void gotoMainActiviyPage() {
		Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle sndBundle = new Bundle();
		sndBundle.putString("act",
				"com.pvi.ap.reader.activity.MainpageInsideActivity");
		intent.putExtras(sndBundle);
		sendBroadcast(intent);
	}

	/**
	 * ��Ӵ�����ҳ����
	 */
	private void openOrTouch(View v) {
		TextView tv = (TextView) v;
		if (openTouchPage) {
			tv.setText(R.string.openTouch);
			openTouchPage = false;
		} else {
			tv.setText(R.string.closeTouch);
			openTouchPage = true;
		}
	}

	/**
	 * ȫ������
	 */
	public void fullDisplay() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

	}

	/**
	 * �����ǩ
	 * 
	 * @return
	 */
	private boolean insertTxtBookmark() {
		bunde.putString("position", "" + pageNumInfo.get(curPage - 1));
		bunde.putString("chaptername", firstLine);
		bunde.putString("bookmarktype", "1");
		bunde.putString("operationtype", "0");
		bunde.putString("sourcetype", "3");
		AddBookMark abm = new AddBookMark(getParent());
		if (!abm.insertTxtBookmark(bunde)) {
			makeToast(R.string.txtBookmarkBean);
		}else{
			makeToast(R.string.txtAddBookmarkSucess);
		}
		return true;
	}

	/**
	 * ��һҳ
	 */
	private void gotoNextPage() {
		if(!pageOk){return ;}
		preBoolean = true;
		curPosition = -1;
		curPage++;
		start = 0;
		if (curPage > totalPage) {
			curPage = totalPage;
			if (nextBoolean) {
				makeToast(R.string.txtEndPage);
				nextBoolean = false;
			}
		} else {
			prePageContent = curPageContent;
			if (nextPageContent != null
					&& nextPageContent.getContent()!= null && !"".equals(nextPageContent.getContent())) {// ������ڣ����ڻ����ȡ
				curPageContent = nextPageContent;
				nextPageContent = null;
				refreshCurpageGUI();
				Logger.i(TAG, "reader in Memory ");
			} else {// ���治���ڣ������´�Ӳ�̶�ȡ
				new Thread() {
					public void run() {
						pageOk = false ;
						curPageContent = null;
						refreshGUI();
						Logger.i(TAG, "showdialog");
						showmessageReflesh("3");
						pageOk = true ;
					};
				}.start();
//				openDelay(txtContext);
			}
			if (nextPageContent == null) {
				getNextPageThread = new GetNextPageThread();
				getNextPageThread.start();
			}
		}

	}
	
	private int updateNum = 0 ;

	/**
	 * ˢ�µ�ǰҳ������
	 */
	private void refreshCurpageGUI() {
		if(error){return ;}
		try {
//			curpage.setText("" + curPage);
//			pages.setText("" + totalPage);
			updatePagerinfo(curPage + "/" + totalPage);

			if (curPageContent != null) {
				displayMyannotatio(curPageContent.getContent());
			}
			firstLine = getFirstLine(curPageContent.getContent());
		} catch (Exception e) {
			Logger.e(TAG, e);
		}
		if(GlobalVar.deviceType ==1 && !first){
//			contentPageView.setUpdateMode(UPDATEMODE_4);
//			updateNum ++ ;
//			if(orientation == 1){
//				if(updateNum >= 5){
////					this.getWindow().getDecorView().getRootView().invalidate(0,0,600, 800, UPDATEMODE_4);
//					updateNum = 0 ;
//				}else{
////					this.getWindow().getDecorView().getRootView().invalidate(0, 0, 600, 800, UPDATEMODE_9);
//				}
//			}else{
//				this.getWindow().getDecorView().getRootView().invalidate(0,0,800, 600, UPDATEMODE_4);
//			}
		}
		
		first = false ;
	}

	/**
	 * ��һҳ �Ƿ���ʾ��Ϣ
	 */
	private boolean preBoolean = true;
	/**
	 * ��һҳ �Ƿ���ʾ��Ϣ
	 */
	private boolean nextBoolean = true;

	/**
	 * ��һҳ
	 */
	private void gotoPrevPage() { // --------------------------------add
		if(!pageOk){return ;}
		nextBoolean = true;
		curPosition = -1;
		curPage--;
		start = 0;
		if (curPage < 1) {
			curPage = 1;
			if (preBoolean) {
				makeToast(R.string.txtFirstPage);
				preBoolean = false;
			}
		} else {
			nextPageContent = curPageContent;
			if (prePageContent != null && prePageContent.getContent() != null && !"".equals(prePageContent.getContent())) {
				curPageContent = prePageContent;
				prePageContent = null;
				refreshCurpageGUI();
				Logger.i(TAG, "reader in Memory ");
			} else {
				new Thread() {
					public void run() {
						pageOk = false ;
						curPageContent = null;
						refreshGUI();
						showmessageReflesh("3");
						pageOk = true ;
					};
				}.start();
//				openDelay(txtContext);
			}
			if (prePageContent == null) {
				getPrePageThread = new GetPrePageThread();
				getPrePageThread.start();
			}
		}

	}

	/**
	 * ȡ�����д���
	 * 
	 * @param code
	 *            �ı�����
	 * @return
	 */
	private String getFirstLine(String code) {
		if (code == null) {
			return "";
		}
		code = code.trim();
		if (code.length() > 16) {
			code = code.substring(0, 15);
		}
		return code.trim();
	}

	// ��GUI�ĸ���
	private void refreshGUI() {
		if(error){return ;}
		Logger.i(TAG, "reader in Hard Disc Drive ");
		int endPosition = 0x7fffffff;
		
		if(pageNumInfo.equals(pageNumInfo1)){
			Logger.i(TAG, "pageNumInfo  == pageNumInfo1" + "   skin:" + orientation);
		}else if(pageNumInfo.equals(pageNumInfo2)){
			Logger.i(TAG, "pageNumInfo  == pageNumInfo2" + "   skin:" + orientation);
		}else{
			Logger.i(TAG, "pageNumInfo  != pageNumInfo12" + "   skin:" + orientation);
		}
		
		
		if (curPage <= totalPage && curPage < pageNumInfo.size()) {
			endPosition = pageNumInfo.get(curPage);
		}
		contentStr = getString(R.string.txtHaveError);
		try {
			Logger.i(TAG, "startPosition:" + pageNumInfo.get(curPage - 1)
					+ "~endPosition:" + endPosition);
			contentStr = getPageContent(pageNumInfo.get(curPage - 1),
					endPosition);
			if (curPageContent == null) {
				curPageContent = new PageContent();
			}
			curPageContent.setContent(contentStr);
			curPageContent.setStartPosition(pageNumInfo.get(curPage - 1));
			curPageContent.setEndPosition(endPosition);
		} catch (Exception e) {
			Logger.e(TAG, e);
			exception = true ;
			sendBroadcast(new Intent(MainpageActivity.BACK));
			return;
		}
	}

	/**
	 * �����ע��ʾ
	 * 
	 * @param content
	 *            �ı�����
	 */
	private void displayMyannotatio(String content) {
		getMyannotatio();
		if (commentList.size() == 0) {
			contentPageView.setText(content);
		} else {
			addMyannotatio(content);
		}
	}

	/**
	 * ������ע
	 */
	private void getMyannotatio() {
		String columns[] = new String[] { CommentsInfo.StartPosition,
				CommentsInfo.EndPosition, CommentsInfo.Comment };
		String where = CommentsInfo.FilePath + "='" + fileNameStr + "'";
		Cursor cur = managedQuery(CommentsInfo.CONTENT_URI, columns, where,
				null, CommentsInfo.StartPosition + "  DESC");
		commentList = new ArrayList<CommentInfo>();
		while (cur != null && cur.moveToNext()) {
			CommentInfo comm = new CommentInfo();
			comm.setStartPostion(Integer.parseInt(cur.getString(cur
					.getColumnIndex(CommentsInfo.StartPosition))));
			comm.setEndPostion(Integer.parseInt(cur.getString(cur
					.getColumnIndex(CommentsInfo.EndPosition))));
			comm.setComment(cur.getString(cur
					.getColumnIndex(CommentsInfo.Comment)));
			commentList.add(comm);
		}
		/*** ���ţ������ݿ��е�����û�������ã�������һ�� **/
		Collections.sort(commentList, new CommentInfo());
		if (cur != null && !cur.isClosed()) {
			cur.close();
		}
	}

	/**
	 * �����ע���ı���
	 */
	private void addMyannotatio(String content) {
		SpannableString ss = new SpannableString(content);
		for (int i = 0; i < commentList.size(); i++) {
			CommentInfo comm = commentList.get(i);
			if (comm.getEndPostion() < pageNumInfo.get(curPage - 1)) {
				continue;
			}
			if (comm.getStartPostion() > pageNumInfo.get(curPage - 1)
					+ content.length()) {
				break;
			}
			if (curPage != pageNumInfo.size()) {
				if (comm.getStartPostion() > pageNumInfo.get(curPage)) {
					break;
				}
			}
			if (comm.getEndPostion() < pageNumInfo.get(curPage)) {
				int lstart = comm.getStartPostion()
						- pageNumInfo.get(curPage - 1);
				if (lstart <= 0) {
					lstart = 0;
				}
				ss.setSpan(new URLSpan(content), lstart, comm.getEndPostion()
						- pageNumInfo.get(curPage - 1),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (comm.getStartPostion() > pageNumInfo.get(curPage - 1)
					&& comm.getStartPostion() < pageNumInfo.get(curPage)
					&& comm.getEndPostion() >= pageNumInfo.get(curPage)) {
				ss
						.setSpan(new URLSpan(content),
								(comm.getStartPostion() - pageNumInfo
										.get(curPage - 1)), content.length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		contentPageView.setText(ss);
		contentPageView.setMovementMethod(LinkMovementMethod.getInstance());
	}

	/**
	 * ��ҳ�Ի���
	 * 
	 * @param context
	 * @return
	 */
	private PviAlertDialog bookPageDialogfile(Context context) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View entryView = inflater.inflate(R.layout.files, null);
		PviAlertDialog builder = new PviAlertDialog(context);
		builder.setTitle(getString(R.string.txtPageNum) + " (1~" + totalPage
				+ ")");
		builder.setView(entryView);
		builder.setButton(DialogInterface.BUTTON_POSITIVE,
				getString(R.string.txtPositiveButton),
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String pageNumberStr = ((EditText) entryView
								.findViewById(R.id.view_content)).getText()
								.toString();
						int pageNumber = curPage;
						try {
							pageNumber = Integer.parseInt(pageNumberStr);
						} catch (Exception e) {
							makeToast(R.string.txtInputErrorNumber);
							return;
						}
						if (!(pageNumber <= 0 || pageNumber > totalPage)) {
							curPage = pageNumber;
							curPageContent = null;
							prePageContent = null;
							nextPageContent = null;
							serchBool = false;
							refreshGUI();
							refreshCurpageGUI();
						} else {
							makeToast(R.string.txtInputErrorNumber);
						}

					}

				});
		builder.setButton(DialogInterface.BUTTON_NEUTRAL,
				getString(R.string.txtCancelButton),
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		return builder;
	}

	/**
	 * �ı��������ֶν���
	 * 
	 * @param mTxtData
	 */
	private void txtSearch(String mTxtData) {

		long end = 10000000000l;
		if (curPage <= 0) {
			curPage = 1;
		}
		if (curPage + 10 >= totalPage) {
			if (pageNumInfo.size() >= totalPage) {
				end = pageNumInfo.get(pageNumInfo.size() - 1);
			} else {
				end = pageNumInfo.get(totalPage);
			}
		} else {
			end = pageNumInfo.get(curPage + 10);
		}
		if (start == 0) {
			start = pageNumInfo.get(curPage - 1);
		}
		String fileContent = "";
		try {
			fileContent = getPageContent(start, end);
		} catch (Exception e) {
			Logger.e(TAG, e);
			makeToast(R.string.txtHaveError);
			sendBroadcast(new Intent(MainpageActivity.BACK));
			return;
		}
		int tx = fileContent.indexOf(mTxtData);
		if (tx != -1) {
			for (int i = curPage; i < curPage + 10 && i < pageNumInfo.size(); i++) {
				if (start + tx >= pageNumInfo.get(i - 1)
						&& start + tx < pageNumInfo.get(i)) {
					curPage = i;
					int indexPan = (int) start + tx - pageNumInfo.get(i - 1);
					start = start + tx + mTxtData.length() + 1;
					String content = "";
					try {
						content = getPageContent(pageNumInfo.get(curPage - 1),
								pageNumInfo.get(curPage));
					} catch (Exception e) {
						Logger.e(TAG, e);
						makeToast(R.string.txtHaveError);
						sendBroadcast(new Intent(MainpageActivity.BACK));
						return;
					}
					SpannableString ss = new SpannableString(content);
					int endend = indexPan + mTxtData.length();
					if (endend > ss.length()) {
						endend = ss.length();
					}
					ss.setSpan(new URLSpan(content), indexPan, endend,
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					contentPageView.setText(ss);
					contentPageView.setMovementMethod(LinkMovementMethod
							.getInstance());

					// pageNumView.setText(curPage + "/" + totalPage);
//					curpage.setText("" + curPage);
//					pages.setText("" + totalPage);
					updatePagerinfo(curPage + "/" + totalPage);

					firstLine = getFirstLine(content);
					if(((GlobalVar)getApplication()).deviceType ==1 && !first){
						updateNum ++ ;
						if(orientation == 1){
							if(updateNum >= 5){
//								this.getWindow().getDecorView().getRootView().postInvalidate(0,0,600, 800, UPDATEMODE_4);
								updateNum = 0 ;
							}
						}else{
							if (updateNum >= 5) {
//								this.getWindow().getDecorView().getRootView()
//										.postInvalidate(0, 0, 800, 600,
//												UPDATEMODE_4);
								updateNum = 0;
							}
						}
					}
					break;
				}
			}
		} else {
			makeToast(R.string.txtNoRelashipComent);
		}
	}

	/**
	 * ��ʾ��ע�Ի���
	 * 
	 * @param context
	 * @return
	 */
	
	private Dialog buildDialogfile(Context context) {
		PviAlertDialog builder = new PviAlertDialog(context);
		if (piZhu == null) {
			piZhu = "";
		}
		while (piZhu.getBytes().length < 16) {
			piZhu = " " + piZhu + " ";
		}
		builder.setTitle("��ע����");
		builder.setMessage(piZhu.toString());
		builder.setButton(getString(R.string.txtPositiveButton),
				(DialogInterface.OnClickListener) null);
		return builder;

	}

	/**
	 * �����ע�Ի���
	 * 
	 * @param context
	 * @return
	 */
	private Dialog buildDialog(final Context context) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View entryView = inflater.inflate(R.layout.files, null);
		PviAlertDialog builder = new PviAlertDialog(context);
		builder.setTitle(R.string.txtCommmentContent);
		TextView txt_view_content=(TextView)entryView.findViewById(R.id.txt_view_content);
		txt_view_content.setText("");
		builder.setView(entryView);
		builder.setButton(getString(R.string.txtPositiveButton),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						piZhu = ((EditText) entryView
								.findViewById(R.id.view_content)).getText()
								.toString();
						if ("".equals(piZhu.trim())) {
							makeToast(R.string.txtNoInputContent);
							return;
						}
						addCommentsInfo();
						refreshCurpageGUI();

					}

				});
		builder.setButton2(getString(R.string.txtCancelButton),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}
				});
		return builder;
	}

	private Thread thread = null;
	private int flag = 0;
	private int type = 0;

	/**
	 * �Զ���ҳ�Ի���
	 * 
	 * @param context
	 * @return
	 */
	private PviAlertDialog buildSetAutoDialog(Context context) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View entryView = inflater.inflate(R.layout.files_2, null);
		PviAlertDialog builder = new PviAlertDialog(context);
		builder.setTitle("��ѡ���Զ���ҳʱ��(��λ:S)");
		builder.setCanClose(true);
		builder.setView(entryView);
		final Button m_time3 = (Button) entryView.findViewById(R.id.select_3);
		final Button m_time5 = (Button) entryView.findViewById(R.id.select_5);
		final Button m_time8 = (Button) entryView.findViewById(R.id.select_8);
		final Button m_time10 = (Button) entryView.findViewById(R.id.select_10);
		
		m_time3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(type!=5){
				type = 5;
				m_time3.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.check));
				m_time5.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.notcheck));
				m_time8.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.notcheck));
				m_time10.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.notcheck));
				}
			}

		});
		m_time3.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus==true){
					if(type != 5){
						type = 5;
						m_time3.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));	
						m_time5.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
						m_time8.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
						m_time10.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
					}
				}
			}

		});
		m_time5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(type!=30){
				type = 30;
				m_time3.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.notcheck));
				m_time5.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.check));
				m_time8.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.notcheck));
				m_time10.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.notcheck));
				}
			}

		});
		m_time5.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus==true){
					if(type != 30){
						type = 30;
						m_time3.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
						m_time5.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));
						m_time8.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
						m_time10.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
					}
				}
			}

		});
		m_time8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(type!=60){
				type = 60;
				m_time3.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.notcheck));
				m_time5.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.notcheck));
				m_time8.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.check));
				m_time10.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.notcheck));
				}
			}

		});
		m_time8.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus==true){
					if(type != 60){
						type = 60;
						m_time3.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
						m_time5.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
						m_time8.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));
						m_time10.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
					}
				}
			}

		});
		m_time10.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(type!=90){
				type = 90;
				m_time3.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.notcheck));
				m_time5.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.notcheck));
				m_time8.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.notcheck));
				m_time10.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.check));
				}
			}

		});
		m_time10.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus==true){
					if(type != 90){
						type =90;
						m_time3.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
						m_time5.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
						m_time8.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
						m_time10.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));
					}
				}
			}

		});
		builder.setButton("��  ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (thread != null && flag == 1) {
					thread.interrupt();
					thread = null;
				}
				try {
					Logger.v("type", type);
					if (type > 0) {
						thread = new Thread() {
							@Override
							public void run() {
								flag = 1;
								while (true) {
									try {
										Thread.sleep(type * 1000);
									} catch (InterruptedException e) {
										flag = 0;
										return;
									}
									if (curPage >= totalPage) {
										flag = 0;
										return;
									}
									showmessageReflesh("2");
								}
							}
						};
						thread.start();
						makeToast(R.string.txtAutoTimeSucess);
					} else {
						makeToast(R.string.TimeInputErrorNumber);
						return;
					}
				} catch (Exception e) {
					makeToast(R.string.TimeInputErrorNumber);
					return;
				}
				dialog.dismiss();
			}
		});
		builder.setButton2("��  ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (thread != null) {
					closeAutoPage();
				}
				dialog.cancel();
			}
		});
		return builder;
	}

	/**
	 * �����ע
	 */
	private void addCommentsInfo() {
		ContentValues values = new ContentValues();
		values.put(CommentsInfo.UserID, bunde.getString("UserID"));
		values.put(CommentsInfo.ChapterName, firstLine);
		values.put(CommentsInfo.FilePath, bunde.getString("FilePath"));
		values.put(CommentsInfo.ContentName, bunde.getString("FilePath")
				.substring(bunde.getString("FilePath").lastIndexOf("/") + 1));
		if (sta < end) {
			values.put(CommentsInfo.StartPosition, sta);
			values.put(CommentsInfo.EndPosition, end);
		} else if (sta == end) {
			values.put(CommentsInfo.StartPosition, sta);
			values.put(CommentsInfo.EndPosition, end + 2);
		} else {
			values.put(CommentsInfo.StartPosition, end);
			values.put(CommentsInfo.EndPosition, sta);
		}

		values.put(CommentsInfo.Comment, piZhu);

		getContentResolver().insert(CommentsInfo.CONTENT_URI, values);
		makeToast(R.string.txtAddCommmentSucess);
	}

	/**
	 * �����˳�ʱ�����е�onpause��������Ҫ����һЩ���ڴ˽���
	 */
	protected void onPause() {
		super.onPause();
		if(error){return ;}
		Logger.i(TAG, "onpause");
		handle.sendEmptyMessage(4);
		stopPageThread();
		closePopmenu();
		exitInsertSystemBookMark();
		savePageInfomation();
//		Debug.stopMethodTracing();
//		finish();
	}
	
	
	@Override
	public void finish() {
		super.finish();
		if(error){return ;}
//		LocalActivityManager mLocalActivityManager = new LocalActivityManager(this,true);
//		mLocalActivityManager.destroyActivity(TxtReaderActivity.class.getName() + (new SimpleDateFormat("yyyyMMddHHmmss"))
//                .format(new Date()), true);
		Logger.i(TAG, "OK");
	}

	/**
	 * �����ҳ��Ϣ
	 */
	private void savePageInfomation() {
		if(error){return ;}
		PageCountListSer pcls = new PageCountListSer();
		if (needSave) {
			synchronized (pageNumInfo) {
				pcls.pageList = pageNumInfo;
				FileOutputStream fs = null;
				ObjectOutputStream os = null;
				String filePath = Constants.CON_FIRST_PAGE_LOCATION
						+ fileNameStr.replace("/", ".") + 1;
				File file = new File(fileNameStr);
				try {
					fs = new FileOutputStream(filePath);
					os = new ObjectOutputStream(fs);
					int len = pcls.pageList.size();
					os.writeLong(file.length());
					os.writeInt(len);
					for (int i = 0; i < len; i++) {
						os.writeInt(pcls.pageList.get(i));
					}
					os.flush();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (fs != null) {
						try {
							fs.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (os != null) {
						try {
							os.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
//			synchronized (pageNumInfo2) {
//				pcls.pageList = pageNumInfo2;
//				FileOutputStream fs = null;
//				ObjectOutputStream os = null;
//				String filePath = Constants.CON_FIRST_PAGE_LOCATION
//						+ fileNameStr.replace("/", ".") + 2;
//				File file = new File(fileNameStr);
//				try {
//					fs = new FileOutputStream(filePath);
//					os = new ObjectOutputStream(fs);
//					int len = pcls.pageList.size();
//					os.writeLong(file.length());
//					os.writeInt(len);
//					for (int i = 0; i < len; i++) {
//						os.writeInt(pcls.pageList.get(i));
//					}
//					os.flush();
//				} catch (Exception e) {
//					e.printStackTrace();
//				} finally {
//					if (fs != null) {
//						try {
//							fs.close();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//					if (os != null) {
//						try {
//							os.close();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			}
			
		}
	}

	/**
	 * ����ϵͳ��ǩ��Ϣ
	 */
	private void exitInsertSystemBookMark() {
		int posi = 1;
		try{
			posi = pageNumInfo.get(curPage - 1) ;
		}catch(Exception e){
			Logger.w(TAG, e);
		}
		bunde.putString("position", "" + posi);
		bunde.putString("chaptername", firstLine);
		bunde.putString("bookmarktype", "0");
		bunde.putString("operationtype", "0");
		bunde.putString("sourcetype", "3");
		bunde.putString("codeSize", "" + codeSize);
		bunde.putString("linespace", "" + lineSpacingf);
		AddBookMark abm = new AddBookMark(getParent());
		abm.insertTxtBookmark(bunde);
	}

	/**
	 * ֹͣ�����ҳ�߳�
	 */
	private void stopPageThread() {
		if (thread != null && flag == 1) {
			thread.interrupt();
			thread = null;
			flag = 0;
		}
		nullThread(pageThread);
//		nullThread(p1);
//		nullThread(p2);
		stopThread = false;
	}
	/**
	 * ��Ӳ�̵�����λ�ÿ�ʼ��ȡ������һλ�õ����ݡ�
	 * 
	 * @param startPosition
	 *            ��ʼλ��
	 * @param endPosition
	 *            ����λ��
	 * @return �ı�����
	 */
	public synchronized String getPageContent(long startPosition, long endPosition)
			throws Exception {
		if (endPosition - startPosition > block) {
			endPosition = startPosition + size;
		}
		StringBuilder sb = new StringBuilder();
		FileInputStream fInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader in = null;
		try {
			fInputStream = new FileInputStream(fileNameStr);
			inputStreamReader = new InputStreamReader(fInputStream, defaultCode);
			in = new BufferedReader(inputStreamReader, size);
			in.skip(startPosition);
			char temp = ' ';
			int sto = 0;
			while (startPosition < endPosition && in.ready()) {
				temp = (char) in.read();
				if (temp == '\n') {
					sto = 1;
					sb.append('\n');
				} else if (temp == '\r') {
					sto = 2;
					sb.append(' ');
				} else {
					if (sto == 2) {
						if (sb.length() >= 1) {
							sb.deleteCharAt(sb.length() - 1);
							sb.append('\n');
						}
					}
					sto = 0;
					sb.append(temp);
				}
				startPosition++;
			}
		} catch (Exception e) {
			Logger.e(TAG, e);
			exception = true;
			throw new Exception(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Logger.e(TAG, e);
					exception = true;
					throw new Exception(e);
				}
			}
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					Logger.e(TAG, e);
					exception = true;
					throw new Exception(e);
				}
			}
			if (fInputStream != null) {
				try {
					fInputStream.close();
				} catch (IOException e) {
					Logger.e(TAG, e);
					exception = true;
					throw new Exception(e);
				}
			}
		}

		return sb.toString();
	}

	/**
	 * ҳ���ʼ����ɺ����е�һ������
	 */
	public void page_finished() {
//		if(!onconfig){
			pageFinished();
//		}else{
//			onconfig = false ;
//		}
	}

	/**
	 * ���¿�ʼ�����ҳ����
	 */
	private void restart() {
		nullThread(pageThread);
//		nullThread(p1);
//		p1 = null ;
//		nullThread(p2);
//		p2 = null ;
		bBeflesh = true;
		startPageThread();
	}

	private void nullThread(Thread t){
		if(t != null){
			if(t.isAlive()){
				t.interrupt();
			}
		}
	}
	/**
	 * �ļ�������
	 */
	private FileInputStream mFInputStream = null;
	private InputStreamReader mInputStreamReader = null;
	private BufferedReader mBuffereReaderIn = null;

	/**
	 * ������
	 */
	private void openFileInputStream() throws Exception {
		if (mFInputStream == null) {
			mFInputStream = new FileInputStream(fileNameStr);
		}
		if (mInputStreamReader == null) {
			mInputStreamReader = new InputStreamReader(mFInputStream,
					defaultCode);
		}
		if (mBuffereReaderIn == null) {
			mBuffereReaderIn = new BufferedReader(mInputStreamReader, size);
		}
	}

	/**
	 * �ر���
	 */
	private void closeFileInputStream() throws Exception {
		if (mBuffereReaderIn != null) {
			mBuffereReaderIn.close();
			mBuffereReaderIn = null;
		}
		if (mInputStreamReader != null) {
			mInputStreamReader.close();
			mInputStreamReader = null;
		}
		if (mFInputStream != null) {
			mFInputStream.close();
			mFInputStream = null;
		}
	}

	private boolean have = false;
	private boolean have2 = false ;

	/***
	 * �����ҳ�߳�
	 * 
	 * @author �����
	 * @creation 2010-12-3
	 */
	public class PageThread extends Thread {

		public void run() {
			while (have) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					Logger.w(TAG, e);
					return;
				}
			}
			have = true;
			synrun();
			have = false;
		}

		@Override
		public void interrupt() {
			stopThread = false;
			super.interrupt();
		}

		private void synrun() {
			stopThread = true;
			File file = new File(fileNameStr);
			long fileLen = file.length();
			int tempLong = 0;
			if (bBeflesh) {
				Logger.i(TAG, "bBeflesh");
				tempLong = Integer.MAX_VALUE;
			} else {
				Logger.i(TAG, "no Beflesh");
				tempLong = pageNumInfo.get(pageNumInfo.size() - 1);
			}
			if (tempLong == fileLen) {
				return;
			}
			if (tempLong > fileLen) {
				pageNumInfo = new ArrayList<Integer>();
				tempLong = 0;
				needSave = true;
				pageNumInfo.add(tempLong);
			}
			float displayDensity = txtContext.getResources()
					.getDisplayMetrics().density;
			float textSize = contentPageView.getTextSize();
			int viewHeight = contentPageView.getHeight();
			int layoutWidth = contentPageView.getWidth();
			Logger.e(TAG, "viewHeight:" + viewHeight);
			Logger.e(TAG, "layoutWidth:" + layoutWidth);
			float spacingmult = 1f;
			float spacingadd = lineSpacingf;
			long startTime = System.currentTimeMillis();
			StringBuffer tempStrBuff = new StringBuffer();
			String sing = "";
			try {
				openFileInputStream();
			} catch (Exception e) {
				Logger.e(TAG, e);
				return;
			}
			try {
				mBuffereReaderIn.skip(tempLong);
			} catch (IOException e1) {
				Logger.e(TAG, e1.toString());
				return;
			}
			while (fileLen - tempLong > 0 && stopThread) {
				needSave = true;
				char temp = ' ';
				int sto = 0;
				while (tempStrBuff.length() < block && stopThread) {
					try {
						if (!mBuffereReaderIn.ready()) {
							break;
						}
						temp = (char) mBuffereReaderIn.read();
						if (temp == '\n') {
							sto = 1;
							tempStrBuff.append('\n');
						} else if (temp == '\r') {
							sto = 2;
							tempStrBuff.append(' ');
						} else {
							if (sto == 2) {
								if (tempStrBuff.length() >= 1) {
									tempStrBuff.deleteCharAt(tempStrBuff
											.length() - 1);
									tempStrBuff.append('\n');
								}
							}
							sto = 0;
							tempStrBuff.append(temp);
						}
					} catch (Exception e1) {
						Logger.e(TAG, e1.toString());
						return;
					}

				}
				sing = tempStrBuff.toString();
				SpannableStringBuilder span_strBuilder = new SpannableStringBuilder(
						sing);
				List<Integer> tempList = new ArrayList<Integer>();
				tempList = PageTextView.all_pages(span_strBuilder,
						displayDensity, layoutWidth, viewHeight, textSize,
						spacingmult, spacingadd);
				// tempList = PageTextView.all_pages(span_strBuilder,
				// layoutWidth, viewHeight,tTextPaint,
				// spacingmult, spacingadd);
				Logger.i(TAG, "tempList value:" + tempList);
				Logger.i(TAG, "tempList.size:" + tempList.size());
				pageNumInfo.remove(pageNumInfo.size() - 1);
				for (int i = 0; i < tempList.size(); i++) {
					pageNumInfo.add(tempList.get(i) + (int) tempLong);
				}
				if (tempLong == pageNumInfo.get(pageNumInfo.size() - 1)
						|| tempList.size() <= 1) {
					break;
				}
				tempStrBuff = new StringBuffer();
				tempStrBuff.append(sing.substring(tempList
						.get(tempList.size() - 1), sing.length()));
				tempLong = pageNumInfo.get(pageNumInfo.size() - 1);
				totalPage = pageNumInfo.size() - 1;
				if (System.currentTimeMillis() - startTime < 5000) {
					continue;
				}
				startTime = System.currentTimeMillis();
				showmessageReflesh("1");
				if (bBeflesh && curPosition <= pageNumInfo.get(totalPage)
						&& stopThread) {
					commentToTxtReader(curPosition);
					showmessageReflesh("5");
					bBeflesh = false;
				}
			}
			try {
				closeFileInputStream();
			} catch (Exception e) {
				Logger.e(TAG, e);
				return;
			}
			if (stopThread) {
				pageNumInfo.add((int) fileLen);
				totalPage = pageNumInfo.size() - 1;
				showmessageReflesh("1");
			}
			if (bBeflesh && stopThread) {
				commentToTxtReader(curPosition);
				showmessageReflesh("5");
				bBeflesh = false;
			}
		}
	}

	/**
	 * ����һҳ������
	 * 
	 * @author �����
	 * @creation 2010-12-2
	 */
	public class GetNextPageThread extends Thread {
		@Override
		public void run() {
			if (curPage + 1 < totalPage) {
				int endPosition = 0x7fffffff;
				if (curPage + 2 <= totalPage) {
					synchronized (pageNumInfo) {
						endPosition = pageNumInfo.get(curPage + 1);
					}
				}
				String lContentStr = "";
				try {
					lContentStr = getPageContent(pageNumInfo.get(curPage),
							endPosition);
				} catch (Exception e) {
					Logger.e(TAG, e);
				}
				if (nextPageContent == null) {
					nextPageContent = new PageContent();
				}
				nextPageContent.setStartPosition(pageNumInfo.get(curPage));
				nextPageContent.setEndPosition(endPosition);
				nextPageContent.setContent(lContentStr);
			}
		}
	}

	/**
	 * ������һҳ������
	 * 
	 * @author �����
	 * @creation 2010-12-2
	 */
	public class GetPrePageThread extends Thread {
		@Override
		public void run() {
			if (curPage - 2 >= 0) {
				int endPosition = pageNumInfo.get(curPage - 1);
				String lContentStr = "";
				try {
					lContentStr = getPageContent(pageNumInfo.get(curPage - 2),
							endPosition);
				} catch (Exception e) {
					Logger.e(TAG, e);
				}
				if (prePageContent == null) {
					prePageContent = new PageContent();
				}
				prePageContent.setEndPosition(endPosition);
				prePageContent.setStartPosition(pageNumInfo.get(curPage - 2));
				prePageContent.setContent(lContentStr);
			}
		}
	}

	/**
	 * ��һҳ�������
	 */
	private GetNextPageThread getNextPageThread = null;
	/**
	 * ��һҳ�������
	 */
	private GetPrePageThread getPrePageThread = null;
	/**
	 * �����߳��ж�
	 */
	private static boolean stopThread = true;

	/**
	 * �������˵�
	 */
	public void closePopmenu() {
		super.closePopmenu();
		if (contentPageView != null)
			contentPageView.requestFocus();
	}

	/**
	 * TXT����
	 */
	private void txtSerchContent() {
		if (look.getText() == null
				|| look.getText().toString().trim().equals("")) {
			makeToast(R.string.txtSerchComtent);
			return;
		}
		String lookup = look.getText().toString();
		if (mSearchdata != null && !mSearchdata.equals(lookup)) {
			serchBool = false;
			start = 0;
		}
		if (!serchBool) {
			mSearchdata = lookup;
			serchBool = true;
			bSerch.setText(R.string.txtNext);
			start = 0;
			txtSearch(lookup);
		} else {
			txtSearch(lookup);
		}
		PviUiUtil.hideInput(getWindow().getDecorView());
	}

	/**
	 * �ر�TXT����ѡ�
	 */
	private void closeTxtSerchDailog() {
		serachs.setVisibility(View.GONE);
		serchBool = false;
		bSerch.setText(R.string.txtSerch);
		look.setText("");
		mSearchdata = null;
		start = 0;
		contentPageView.requestFocus();
		PviUiUtil.hideInput(getWindow().getDecorView());
	}

	/**
	 * �����¼��Ĳ�������
	 */
	OnTouchListener onTouchListenerObj = new OnTouchListener() {
		private boolean in_annotation_state = false;
		private int annotation_start_pos = 0;
		URLSpan selection_span;

		private int startX = 0, startY = 0, endX = 0, endY = 0;
		private boolean pi = false ;

		@SuppressWarnings("deprecation")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Layout layout = contentPageView.getLayout();
			int line = 0;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				closePopmenu();
				startX = (int) event.getX();
				startY = (int) event.getY();
				if (layout == null) {
					return false;
				}
				line = layout.getLineForVertical(startY);
				sta = layout.getOffsetForHorizontal(line, (float) event.getX());
				end = sta;
				if (curPage <= 0) {
					curPage = 1;
				}
				int mousePosition = pageNumInfo.get(curPage - 1) + sta;
				for (int i = 0; i < commentList.size(); i++) {
					CommentInfo comm = commentList.get(i);
					if (comm.getStartPostion() <= mousePosition
							&& mousePosition <= comm.getEndPostion()) {
						piZhu = comm.getComment();
						pi = true ;
						return true;
					}
				}
				if (addannotation == 1 || addannotation == 2) {
					in_annotation_state = true;
                    GlobalVar appState = (GlobalVar)getApplicationContext();
                    if(appState.deviceType==1){
//                        contentPageView.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
                    }
					int offset = 0;
					int h_offset = 0;
					h_offset = layout.getOffsetForHorizontal(line,
							(float) event.getX());
					offset = offset + h_offset;
					annotation_start_pos = offset;
				}
				return true;
			case MotionEvent.ACTION_MOVE:
				line = layout.getLineForVertical((int) event.getY());
				end = layout.getOffsetForHorizontal(line, (float) event.getX());
				if (addannotation == 1 || addannotation == 2) {
					if (in_annotation_state) {
						int offset = 0;
						int h_offset = 0;
						h_offset = layout.getOffsetForHorizontal(line,
								(int) event.getX());
						offset = offset + h_offset;
						CharSequence char_seq = contentPageView.getText();
						SpannableStringBuilder span_builder = (SpannableStringBuilder) char_seq;
						if (char_seq != null) {
							if (selection_span == null) {
								selection_span = new URLSpan(char_seq
										.toString());
							} else {
								span_builder.removeSpan(selection_span);
								if (offset >= annotation_start_pos) {
									span_builder.setSpan(selection_span,
											annotation_start_pos, offset,
											Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
								} else {
									span_builder.setSpan(selection_span,
											offset, annotation_start_pos,
											Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
								}
							}
						}
					}
				}

				return true;
			case MotionEvent.ACTION_UP:
				endX = (int) event.getX();
				endY = (int) event.getY();
                if(in_annotation_state) {//always set update mode back
                    GlobalVar appState = (GlobalVar)getApplicationContext();
                    if(appState.deviceType==1){
//                        contentPageView.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
                    }
                }    
				if(Math.abs(endX - startX) <100 && Math.abs(endY - startY) < 100 && pi){
					dialogs = buildDialogfile(txtContext);
					dialogs.show();
					return true ;
				}
				pi = false ;
				Logger.i(TAG, "startX:" + startX + "  startY:" + startY
						+ "  endX:" + endX + "   endY:" + endY);
				if (addannotation == 1) {
					in_annotation_state = false;
					CharSequence char_seq = contentPageView.getText();
					SpannableStringBuilder span_builder = (SpannableStringBuilder) char_seq;
					if (sta != end) {
						sta += pageNumInfo.get(curPage - 1);
						end += pageNumInfo.get(curPage - 1);
						addNewComment();
					}
					if (char_seq != null) {
						if (selection_span != null) {
							span_builder.removeSpan(selection_span);
							selection_span = null;
						}
					}

				} else if (addannotation == 2) {
					CharSequence char_seq = contentPageView.getText();
					SpannableStringBuilder span_builder = (SpannableStringBuilder) char_seq;
					if (char_seq != null) {
						if (selection_span != null) {
							span_builder.removeSpan(selection_span);
							selection_span = null;
						}
					}
					if (sta > end) {
						int temp = sta;
						sta = end;
						end = temp;
					} else if (sta == end) {
						return true;
					}
					String noteInfoStr = char_seq.toString()
							.substring(sta, end);
					PviAlertDialog pad = new PviAlertDialog(txtContext);
					LayoutInflater inflater = LayoutInflater.from(txtContext);
					final View entryView = inflater.inflate(
							R.layout.addnewnote, null);
					EditText et = (EditText) entryView
							.findViewById(R.id.notecontent);
					et.setText(noteInfoStr);
					pad.setTitle("��ӱ��");
					pad.setView(entryView);
					pad.setCanClose(true);
					pad.setButton(getString(R.string.txtPositiveButton),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText et1 = (EditText) entryView
											.findViewById(R.id.notetile);
									EditText et2 = (EditText) entryView
											.findViewById(R.id.notecontent);
									String str1 = et1.getText().toString();
									String str2 = et2.getText().toString();
									Map<String, String> map = new HashMap<String, String>();
									map.put("noteName", str1);
									map.put("noteText", str2);
									Logger.i(TAG, str1);
									Logger.i(TAG, str2);
									AddNote an = new AddNote(txtContext);
									boolean su = true;
									String message = "";
									if ("".equals(str1.trim())) {
										message = "���ⲻ��Ϊ��";
										su = false;
									}
									if (su) {
										su = an.addNoteInfo(map);
										if (!su) {
											message = "������ֲ�����ͬ";
										}
									}
									Logger.i(TAG, su);
									PviAlertDialog pad2 = new PviAlertDialog(
											txtContext);
									pad2.setTitle("��������ʾ");
									if (su) {
										pad2.setMessage("��ӳɹ�");
									} else {
										pad2
												.setMessage("���ʧ��,ʧ��ԭ��Ϊ��"
														+ message);
									}
									pad2.setCanClose(true);
									pad2
											.setButton(
													getString(R.string.txtPositiveButton),
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															dialog.dismiss();
														}
													});
									pad2.show();
								}

							});
					pad.setButton2(getString(R.string.txtCancelButton),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}

							});
					pad.show();

				} else {// ������ҳ
					if (openTouchPage) {
						if (startX - endX > 100
								&& startX - endX >= Math.abs(startY - endY)) {
							gotoNextPage();
						} else if (startY - endY > 100
								&& startY - endY >= Math.abs(startX - endX)) {
							gotoPrevPage();
						} else if (endX - startX > 100
								&& endX - startX >= Math.abs(startY - endY)) {
							gotoPrevPage();
						} else if (endY - startY > 100
								&& endY - startY >= Math.abs(startX - endX)) {
							gotoNextPage();
						}
					}
				}
				return true;
			}

			return true;
		}
	};

	/**
	 * �����������ζԻ���
	 * 
	 * @param context
	 * @return
	 */
	private Dialog buildDialogautoPager(Context context) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View entryView = null;
		switch (orientation) {
		case 1:
			entryView = inflater.inflate(R.layout.readsetformer, null);
			break;
		case 2:
			entryView = inflater.inflate(R.layout.readsetformer2, null);
			break;
		default:
			entryView = inflater.inflate(R.layout.readsetformer, null);
		}
		PviAlertDialog dlg = new PviAlertDialog(context);
		dlg.setView(entryView);
		ReadSetView internalView = (ReadSetView) entryView;
		internalView.setDialog(dlg);
		internalView.setSufangBool(false);
		HashMap<String, String> chooseMap = new HashMap<String, String>();
		chooseMap.put(ReadSetView.CON_FONESIZE, "" + (int) codeSize);
		chooseMap.put(ReadSetView.CON_LINESIZE, "" + (int) lineSpacingf);
		internalView.setChooseMap(chooseMap);
		internalView.setSetListener(new ReadSetView.SetListener() {
			public void chooseDoListener(boolean ok,
					java.util.Map<String, String> chooseButton) {
				if (ok) {
					String foneSize = chooseButton
							.get(ReadSetView.CON_FONESIZE);
					String lineSize = chooseButton
							.get(ReadSetView.CON_LINESIZE);
					Logger.i(TAG, "codeSize:" + foneSize + "    lineSpacingf:"
							+ lineSize);
					if (codeSize == (int) Float.parseFloat(foneSize)
							&& lineSpacingf == (int) Float.parseFloat(lineSize)) {
						return;
					}
					codeSize = (int) Float.parseFloat(foneSize);
					lineSpacingf = (int) Float.parseFloat(lineSize);
					contentPageView.setTextSize(codeSize);
					contentPageView.setLineSpacing(lineSpacingf, lineMult);
					curPageContent = null;
					prePageContent = null;
					nextPageContent = null;
					start = 0 ;
					if (curPosition == -1) {
						curPosition = pageNumInfo.get(curPage - 1);
					}
					openDelay();
					restart();
				}
			}
		});

		return dlg;
	}

	private void closeAutoPage() {
		if (thread != null && flag == 1) {
			makeToast(R.string.txtAutoClose);
			thread.interrupt();
			thread = null;
			flag = 0;
		}
	}

	private View.OnKeyListener onKeyListener = new OnKeyListener() {
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			Logger.i("Txt", "onKeyUp(int keyCode, " + keyCode);
			if (event.getAction() == KeyEvent.ACTION_UP) {
				if (keyCode == event.KEYCODE_DPAD_LEFT) {
					gotoPrevPage();
					return true;
				}
				if (keyCode == event.KEYCODE_DPAD_UP) {
					contentPageView.requestFocus();
					return false ;
				}
				if (keyCode == event.KEYCODE_DPAD_RIGHT) {
					gotoNextPage();
					return true;
				}
				if (keyCode == event.KEYCODE_DPAD_DOWN) {
//					nextPage.requestFocus();
					return false ;
				}
				if (keyCode == event.KEYCODE_MENU) {
					if (fullScreem == 2) {
						return true;
					}
					if (popmenu != null && popmenu.isShowing()) {
						closePopmenu();
						return true;
					}
					menupan();
					return true;
				}
				if (keyCode == event.KEYCODE_ENTER
						|| keyCode == event.KEYCODE_DPAD_CENTER) {
						return true;
				}
				if (keyCode == event.KEYCODE_BACK) {
					if (popmenu != null && popmenu.isShowing()) {
						closePopmenu();
						return true;
					}
					if (fullScreem == 2) {
						setCloseFullScreem();
						return true;
					}
					sendBroadcast(new Intent(MainpageActivity.BACK));
					return true;
				}
			}
			return false;
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
	}

	private boolean first = false ;
	@Override
	protected void onResume() {
		super.onResume();
		first = true ;
		Logger.i(TAG, "onResume");
//		Settings.System.putInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 1);
//		Configuration newConfig = txtContext.getResources().getConfiguration();
//		if(skin != newConfig.orientation){
//			onConfigurationChanged(newConfig);
//		}
		showMe();
		contentPageView.requestFocus();
	}
	
	private void showMe() {
		Intent tmpIntent = new Intent(MainpageActivity.SHOW_ME);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("sender", txtContext.getClass().getName()); // TAB��Ƕactivity���ȫ��
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
		tmpIntent = null;
		bundleToSend = null;
	}

	/**
	 * ��Ӷ�txt�����ж�
	 */
	private void readcharset() {
		File file = new File(fileNameStr);
		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance(); 
		detector.add(JChardetFacade.getInstance());
		detector.add(ASCIIDetector.getInstance());
		detector.add(UnicodeDetector.getInstance());
		java.nio.charset.Charset charset = null;
		long len = file.length();
		if(len > 10* 1024){ len = 10 * 1024 ;}
		FileInputStream fis = null ;
		BufferedInputStream bis = null ;
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			bis.mark((int)len);
			charset = detector.detectCodepage(bis, (int)len);
			Logger.i(TAG, file.toURL());
		} catch (Exception ex) {
			Logger.e(TAG, ex);
		}
		if (charset != null){
			defaultCode = charset.name() ;
			Logger.i(TAG, defaultCode);
			if ("windows-1252".equals(defaultCode) && bis != null) {
				try {
					bis.reset();
					byte[] bytes = new byte[3];
					bis.read(bytes, 0, bytes.length);
					if (bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB
							&& bytes[2] == (byte) 0xBF) {
						defaultCode = utf8;
						Logger.i(TAG, "defaultCode:" + defaultCode);
					} else if (bytes[0] == (byte) 0xFF
							&& bytes[1] == (byte) 0xFE) {
						defaultCode = "UTF-16LE";
						Logger.i(TAG, "defaultCode:" + defaultCode);
					} else if (bytes[0] == (byte) 0xFE
							&& bytes[1] == (byte) 0xFF) {
						defaultCode = "UTF-16BE";
						defaultCode = "GBK" ;
						Logger.i(TAG, "defaultCode:" + defaultCode);
					} else {

					}
				} catch (Exception e) {
					Logger.e(TAG, e);

				}
			}
		}else{
			Logger.i(TAG, "charset is null");
			defaultCode =  gb2312;//java.nio.charset.Charset.defaultCharset().name();
		}
		if(bis != null){try {bis.close();} catch (IOException e) {Logger.e(TAG, e);}}
		if(fis != null){try {fis.close();} catch (IOException e) {Logger.e(TAG, e);}}
		
		Logger.i(TAG, "defaultCode" + defaultCode);
		
//		byte[] buf = new byte[4096];
//		try {
//			FileInputStream fis = new FileInputStream(fileNameStr);
//			UniversalDetector detector = new UniversalDetector(null);
//			int nread;
//	        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
//	            detector.handleData(buf, 0, nread);
//	        }
//	        detector.dataEnd();
//	        String encoding = detector.getDetectedCharset();
//	        if (encoding != null) {
//	            System.out.println("Detected encoding = " + encoding);
//	        } else {
//	            System.out.println("No encoding detected.");
//	        }
//		} catch (FileNotFoundException e) {
//			Logger.e(TAG, e);
//		}
	}

	/**
	 * 
	 * @return
	 */
	protected Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(700);
		inFromLeft.setInterpolator(new LinearInterpolator());
		return inFromLeft;
	}

	protected void setOpenFullScreem() {
		Logger.i(TAG, "setOpenFullScreem");
		fullScreem = 2;
		sendBroadcast(new Intent(MainpageActivity.FULLSCREEN_ON));
		if (popmenu != null && popmenu.isShowing()) {
			closePopmenu();
		}
		int hight = contentPageView.getHeight();
		int with = contentPageView.getWidth();
		float scalex = 1;
		if(orientation == 1){
			scalex = (float) 560 / (float) with;
		}else{
			scalex = (float)760 / (float)with;
		}
		Logger.i(TAG, "hight:" + hight);
		if(orientation == 1){
			lineMult = 800 / (float) (hight);
		}else{
			lineMult = 600 / (float) hight ;
		}
		Logger.i(TAG, "fang da :" + lineMult);
		Logger.i(TAG, "" + scalex);
		int paddingWith = 20 ;
		setContentView(R.layout.txtfullbrowser);
		contentPageView = (PageTextView) findViewById(R.id.view_contents);
		contentPageView.setTextSize(codeSize);
		contentPageView.setPadding(paddingWith, paddingWith, paddingWith, paddingWith);
		contentPageView.setLineSpacing(lineSpacingf, lineMult);
		contentPageView.setOnTouchListener(onTouchListenerObj);
		contentPageView.setTextScaleX(scalex);
		contentPageView.setOnKeyListener(onKeyListener);
		contentPageView.requestFocus();
	}

	@Override
	public void onLowMemory() {
		Logger.i(TAG, "onLowMemory");
		handle.sendEmptyMessage(4);
		System.gc();
		finish();
		super.onLowMemory();
	}

	/**
	 * �ر�ȫ��
	 */
	protected void setCloseFullScreem() {
		Logger.i(TAG, "setCloseFullScreem");
		fullScreem = 1;
		sendBroadcast(new Intent(MainpageActivity.FULLSCREEN_OFF));
		setContentView(layoutID);
		contentPageView = (PageTextView) findViewById(R.id.view_contents);
		contentPageView.setTextSize(codeSize);
		lineMult = 1f;
		contentPageView.setLineSpacing(lineSpacingf, lineMult);
		bindEvent();
	}

	private void init() {
	}


	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == 10){
			return buildSetAutoDialog(txtContext);
		}
		return super.onCreateDialog(id);
	}
	
	
	/***
	 * ��Ļ��ת
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if(fullScreem == 2){ setCloseFullScreem();}
		if (curPosition == -1) {
			curPosition = pageNumInfo.get(curPage - 1);
		}
		orientation = newConfig.orientation ;
		if(orientation == Configuration.ORIENTATION_LANDSCAPE){
			setContentView(R.layout.txtfilebrowser_orientation);
			pageNumInfo = pageNumInfo2;
		}else{
			setContentView(R.layout.txtfilebrowser_end);
			pageNumInfo = pageNumInfo1;
		}
		
		curPageContent = null;
		prePageContent = null;
		nextPageContent = null;
		totalPage = pageNumInfo.size() - 1;
		bindEvent();
		Logger.i(TAG, "curPage" + curPage);
		commentToTxtReader(curPosition);
		Logger.i(TAG, "curPage" + curPage);
		contentPageView.setTextSize(codeSize);
		contentPageView.setLineSpacing(lineSpacingf, lineMult);
		if(orientation == Configuration.ORIENTATION_LANDSCAPE){
			layoutID = R.layout.txtfilebrowser_orientation ;
		}else{
			layoutID = R.layout.txtfilebrowser_end ;
		}
		if(((GlobalVar)getApplication()).deviceType == 1 ){
			if(orientation == Configuration.ORIENTATION_LANDSCAPE){
//				this.getWindow().getDecorView().getRootView().invalidate(0, 0, 800, 600, UPDATEMODE_4);
			}else{
//				this.getWindow().getDecorView().getRootView().invalidate(0, 0, 600, 800, UPDATEMODE_4);
			}
		}
		showmessageReflesh("5");
		onconfig = true ;
		super.onConfigurationChanged(newConfig);
	}

	private boolean onconfig = false ;
	/**
	 * �ļ�������
	 */
	private FileInputStream mFInputStream2 = null;
	private InputStreamReader mInputStreamReader2 = null;
	private BufferedReader mBuffereReaderIn2 = null;
	
	/**
	 * �ļ�������
	 */
	private FileInputStream mFInputStream1 = null;
	private InputStreamReader mInputStreamReader1 = null;
	private BufferedReader mBuffereReaderIn1 = null;
	
	
	/**
	 * ������
	 */
	private void openFileInputStream2() throws Exception {
		if (mFInputStream2 == null) {
			mFInputStream2 = new FileInputStream(fileNameStr);
		}
		if (mInputStreamReader2 == null) {
			mInputStreamReader2 = new InputStreamReader(mFInputStream2,
					defaultCode);
		}
		if (mBuffereReaderIn2 == null) {
			mBuffereReaderIn2 = new BufferedReader(mInputStreamReader2, size);
		}
	}
	
	/**
	 * ������
	 */
	private void openFileInputStream1() throws Exception {
		if (mFInputStream1 == null) {
			mFInputStream1 = new FileInputStream(fileNameStr);
		}
		if (mInputStreamReader1 == null) {
			mInputStreamReader1 = new InputStreamReader(mFInputStream1,
					defaultCode);
		}
		if (mBuffereReaderIn1 == null) {
			mBuffereReaderIn1 = new BufferedReader(mInputStreamReader1, size);
		}
	}
	
	/**
	 * �ر���
	 */
	private void closeFileInputStream2() throws Exception {
		if (mBuffereReaderIn2 != null) {
			mBuffereReaderIn2.close();
			mBuffereReaderIn2 = null;
		}
		if (mInputStreamReader2 != null) {
			mInputStreamReader2.close();
			mInputStreamReader2 = null;
		}
		if (mFInputStream2 != null) {
			mFInputStream2.close();
			mFInputStream2 = null;
		}
	}
	
	
	/**
	 * �ر���
	 */
	private void closeFileInputStream1() throws Exception {
		if (mBuffereReaderIn1 != null) {
			mBuffereReaderIn1.close();
			mBuffereReaderIn1 = null;
		}
		if (mInputStreamReader1 != null) {
			mInputStreamReader1.close();
			mInputStreamReader1 = null;
		}
		if (mFInputStream1 != null) {
			mFInputStream1.close();
			mFInputStream1 = null;
		}
	}
	
	
	
	
	public class PageThread2 extends Thread {

		public void run() {
			while (have2) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					Logger.w(TAG, e);
					return;
				}
			}
			have2 = true;
			synrun(); 
			have2 = false;
		}

		@Override
		public void interrupt() {
			stopThread = false;
			super.interrupt();
		}

		private void synrun() {
			stopThread = true;
			File file = new File(fileNameStr);
			long fileLen = file.length();
			int tempLong = 0;
			if (bBeflesh) {
				tempLong = Integer.MAX_VALUE;
			} else {
				if(pageNumInfo2.size() > 0){
					tempLong = pageNumInfo2.get(pageNumInfo2.size() - 1);
				}else{
					tempLong = Integer.MAX_VALUE;
				}
			}
			if (tempLong == fileLen) {
				return;
			}
			if (tempLong > fileLen) {
				pageNumInfo2 = new ArrayList<Integer>();
				tempLong = 0;
				needSave = true;
				pageNumInfo2.add(tempLong);
			}
			float displayDensity = txtContext.getResources()
					.getDisplayMetrics().density;
			float textSize = codeSize;
			Logger.i(TAG, "codeSize:" + textSize);
			int viewHeight = 430 ;
			int layoutWidth = 760 ;
			float spacingmult = 1f;
			float spacingadd = lineSpacingf;
			long startTime = System.currentTimeMillis();
			StringBuffer tempStrBuff = new StringBuffer();
			String sing = "";
			try {
				openFileInputStream2();
			} catch (Exception e) {
				Logger.e(TAG, e);
				return;
			}
			try {
				mBuffereReaderIn2.skip(tempLong);
			} catch (IOException e1) {
				Logger.e(TAG, e1);
				return;
			}
			while (fileLen - tempLong > 0 && stopThread) {
				needSave = true;
				char temp = ' ';
				int sto = 0;
				while (tempStrBuff.length() < block && stopThread) {
					try {
						if (!mBuffereReaderIn2.ready()) {
							break;
						}
						temp = (char) mBuffereReaderIn2.read();
						if (temp == '\n') {
							sto = 1;
							tempStrBuff.append('\n');
						} else if (temp == '\r') {
							sto = 2;
							tempStrBuff.append(' ');
						} else {
							if (sto == 2) {
								if (tempStrBuff.length() >= 1) {
									tempStrBuff.deleteCharAt(tempStrBuff
											.length() - 1);
									tempStrBuff.append('\n');
								}
							}
							sto = 0;
							tempStrBuff.append(temp);
						}
					} catch (Exception e1) {
						Logger.e(TAG, e1);
						return;
					}

				}
				sing = tempStrBuff.toString();
				SpannableStringBuilder span_strBuilder = new SpannableStringBuilder(
						sing);
				Logger.i(TAG, "pageNumInfo2:" + pageNumInfo2.size());
				List<Integer> tempList = new ArrayList<Integer>();
				tempList = PageTextView.all_pages(span_strBuilder,
						displayDensity, layoutWidth, viewHeight, textSize,
						spacingmult, spacingadd);
//				tempList =getPageList(span_strBuilder, layoutWidth, viewHeight, displayDensity, textSize, spacingmult, spacingadd);
				pageNumInfo2.remove(pageNumInfo2.size() - 1);
				for (int i = 0; i < tempList.size(); i++) {
					pageNumInfo2.add(tempList.get(i) + (int) tempLong);
				}
				if (tempLong == pageNumInfo2.get(pageNumInfo2.size() - 1)
						|| tempList.size() <= 1) {
					break;
				}
				tempStrBuff = new StringBuffer();
				tempStrBuff.append(sing.substring(tempList
						.get(tempList.size() - 1), sing.length()));
				tempLong = pageNumInfo2.get(pageNumInfo2.size() - 1);
				if(orientation == 2){
					totalPage = pageNumInfo2.size() - 1;
					pageNumInfo = pageNumInfo2 ;
				}
				if (System.currentTimeMillis() - startTime < 5000) {
					continue;
				}
				startTime = System.currentTimeMillis();
				showmessageReflesh("1",2);
				if (bBeflesh && curPosition <= pageNumInfo2.get(totalPage)
						&& stopThread && orientation == 2) {
					commentToTxtReader(curPosition);
					showmessageReflesh("5",2);
					bBeflesh = false;
				}
			}
			try {
				closeFileInputStream2();
			} catch (Exception e) {
				Logger.e(TAG, e);
			}
			if (stopThread) {
				pageNumInfo2.add((int) fileLen);
				if (orientation == 2) {
					totalPage = pageNumInfo2.size() - 1;
					showmessageReflesh("1",2);
				}
			}
			if (bBeflesh && stopThread && orientation == 2) {
				commentToTxtReader(curPosition);
				showmessageReflesh("5",2);
				bBeflesh = false;
			}
		}
	}
	
	
	
	public class PageThread1 extends Thread {

		public void run() {
			while (have) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					Logger.w(TAG, e);
					return;
				}
			}
			have = true;
			synrun();
			have = false;
		}

		@Override
		public void interrupt() {
			stopThread = false;
			super.interrupt();
		}

		private void synrun() {
			stopThread = true;
			File file = new File(fileNameStr);
			long fileLen = file.length();
			int tempLong = 0;
			if (bBeflesh) {
				tempLong = Integer.MAX_VALUE;
			} else {
				if(pageNumInfo1.size() > 0){
					tempLong = pageNumInfo1.get(pageNumInfo1.size() - 1);
				}else{
					tempLong = Integer.MAX_VALUE;
				}
			}
			if (tempLong == fileLen) {
				return;
			}
			if (tempLong > fileLen) {
				pageNumInfo1 = new ArrayList<Integer>();
				tempLong = 0;
				needSave = true;
				pageNumInfo1.add(tempLong);
			}
			float displayDensity = txtContext.getResources()
					.getDisplayMetrics().density;
			float textSize = codeSize;
			Logger.i(TAG, "textSize"  + textSize);
			int viewHeight = 630 ;
			int layoutWidth = 560 ;
			float spacingmult = 1f;
			float spacingadd = lineSpacingf;
			long startTime = System.currentTimeMillis();
			StringBuffer tempStrBuff = new StringBuffer();
			String sing = "";
			try {
				openFileInputStream1();
			} catch (Exception e) {
				Logger.e(TAG, e);
				return;
			}
			try {
				mBuffereReaderIn1.skip(tempLong);
			} catch (IOException e1) {
				Logger.e(TAG, e1);
				return;
			}
			while (fileLen - tempLong > 0 && stopThread) {
				needSave = true;
				char temp = ' ';
				int sto = 0;
				while (tempStrBuff.length() < block && stopThread) {
					try {
						if (!mBuffereReaderIn1.ready()) {
							break;
						}
						temp = (char) mBuffereReaderIn1.read();
						if (temp == '\n') {
							sto = 1;
							tempStrBuff.append('\n');
						} else if (temp == '\r') {
							sto = 2;
							tempStrBuff.append(' ');
						} else {
							if (sto == 2) {
								if (tempStrBuff.length() >= 1) {
									tempStrBuff.deleteCharAt(tempStrBuff
											.length() - 1);
									tempStrBuff.append('\n');
								}
							}
							sto = 0;
							tempStrBuff.append(temp);
						}
					} catch (Exception e1) {
						Logger.e(TAG, e1);
						return;
					}

				}
				sing = tempStrBuff.toString();
				SpannableStringBuilder span_strBuilder = new SpannableStringBuilder(
						sing);
				Logger.i(TAG, "pageNumInfo1:" + pageNumInfo1.size());
				List<Integer> tempList = new ArrayList<Integer>();
				tempList = PageTextView.all_pages(span_strBuilder,
						displayDensity, layoutWidth, viewHeight, textSize,
						spacingmult, spacingadd);
//				tempList =getPageList(span_strBuilder, layoutWidth, viewHeight, displayDensity, textSize, spacingmult, spacingadd);
				pageNumInfo1.remove(pageNumInfo1.size() - 1);
				for (int i = 0; i < tempList.size(); i++) {
					pageNumInfo1.add(tempList.get(i) + (int) tempLong);
				}
				if (tempLong == pageNumInfo1.get(pageNumInfo1.size() - 1)
						|| tempList.size() <= 1) {
					break;
				}
				tempStrBuff = new StringBuffer();
				tempStrBuff.append(sing.substring(tempList
						.get(tempList.size() - 1), sing.length()));
				tempLong = pageNumInfo1.get(pageNumInfo1.size() - 1);
				if(orientation == 1){
					totalPage = pageNumInfo1.size() - 1;
					pageNumInfo = pageNumInfo1 ;
				}
				if (System.currentTimeMillis() - startTime < 5000) {
					continue;
				}
				startTime = System.currentTimeMillis();
				showmessageReflesh("1",1);
				if (bBeflesh && curPosition <= pageNumInfo1.get(totalPage)
						&& stopThread && orientation == 1) {
					commentToTxtReader(curPosition);
					showmessageReflesh("5", 1);
					bBeflesh = false;
				}
			}
			try {
				closeFileInputStream1();
			} catch (Exception e) {
				Logger.e(TAG, e);
				return;
			}
			if (stopThread) {
				pageNumInfo1.add((int) fileLen);
				if (orientation == 1) {
					totalPage = pageNumInfo1.size() - 1;
					showmessageReflesh("1",1);
				}
			}
			if (bBeflesh && stopThread && orientation == 1) {
				commentToTxtReader(curPosition);
				showmessageReflesh("5", 1);
				bBeflesh = false;
			}
		}
	}
	
	
	
	private void getPageNumInfo(long fl,int swit){
		File file1 = new File(Constants.CON_FIRST_PAGE_LOCATION
				+ fileNameStr.replace("/", ".") + swit);
		if (file1.exists()) {
			FileInputStream fs = null;
			ObjectInputStream os = null;
			try {
				fs = new FileInputStream(file1);
				os = new ObjectInputStream(fs);
				long fileLen = os.readLong();
				if (fl != fileLen) {
					throw new Exception("file is update");
				}
				int len = os.readInt();
				if(swit == 1){
					pageNumInfo1 = new ArrayList<Integer>();
				}else{
					pageNumInfo2 = new ArrayList<Integer>();
				}
				if (swit == 1) {
					for (int i = 0; i < len; i++) {
						pageNumInfo1.add(os.readInt());
					}
				} else {
					for (int i = 0; i < len; i++) {
						pageNumInfo2.add(os.readInt());
					}
				}
			} catch (Exception e) {
				Logger.e(TAG, e);
			} finally {
				try {
					if (os != null) {
						os.close();
					}
					if (fs != null) {
						fs.close();
					}
				} catch (IOException e) {
					Logger.e(TAG, e);
				}
			}
		}
	}
	
	
	
	private List<Integer> getPageList(SpannableStringBuilder text,
			int layoutWith, int viewHeight, TextPaint mTextPaint, float spacingmult, float spacingadd) {

		List<Integer> ret = new ArrayList<Integer>();
		int ellipsisWidth = layoutWith;
		Layout mLayout = new DynamicLayout((CharSequence) text,
				mTextPaint,
				layoutWith, Layout.Alignment.ALIGN_NORMAL,
				spacingmult, spacingadd,
				true);
		try {
			int pagePos = 0;
			ret.add(pagePos);
			int trimNum = getEndPositionByLayout(mLayout, viewHeight,text);
			while (trimNum > 0) {
				pagePos = pagePos + trimNum;
				ret.add(pagePos);
				text.delete(0, trimNum);
				trimNum = getEndPositionByLayout(mLayout, viewHeight,text);
			}

		} catch (OutOfMemoryError e) {
			Logger.e(TAG, e);
		} catch (Exception e) {
			Logger.e(TAG, e);
		}

		return ret;
	}
	
	
	
	private int getEndPositionByLayout(Layout layoutV, int viewHeight,
			SpannableStringBuilder charSeq) {
		return layoutV.getLineStart(layoutV.getLineForVertical(viewHeight));
	}
	
	public void onAttachedToWindow() {
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
		super.onAttachedToWindow();
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if(fullScreem == 2){
			setCloseFullScreem();
		}
		super.onSaveInstanceState(outState);
	}
	
	

	@Override
	public void OnNextpage() {
		Logger.i(TAG, "OnNextpage");
		gotoNextPage();
	}

	@Override
	public void OnPrevpage() {
		Logger.i(TAG, "OnPrevpage");
		gotoPrevPage();
	}
		
	
}
