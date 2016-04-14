package com.pvi.ap.reader.external.txt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.GlobalVar;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;

import com.pvi.ap.reader.data.common.Logger;

/**
 * Reader设置面板
 * @author Elvis
 *
 */
public class ReadSetView extends AbsoluteLayout implements View.OnTouchListener {
	private String TAG = "ReadSetView" ;
//	private static final int UPDATE_MODE_FULL    = EINK_AUTO_MODE_REGIONAL| EINK_WAIT_MODE_NOWAIT | EINK_WAVEFORM_MODE_GC16| EINK_UPDATE_MODE_PARTIAL;

	View m_parent_view = this;
	Context context_local = null;
	private void init(Context context) {
		context_local = context;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStrokeWidth(5);
		mPaint.setTextSize(21);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
	//	this.setOnFocusChangeListener(this);
		this.setOnKeyListener(onKeyListener);
	}
	Paint mPaint = new Paint();
	int m_focused_ctrl_id = -1;
	public int get_focused_ctrl_id() {
		return m_focused_ctrl_id; 
	}
	/**
	 * @param context
	 * @param attrs
	 */
	public ReadSetView(Context context, AttributeSet attrs){
		super(context,attrs);
		init(context);
	}
	
	public static final String CON_LINESIZE = "LINESIZE" ;
	public static final String CON_SUFANG = "SUFANG" ;
	public static final String CON_FONESIZE = "FONESIZE" ;

	private List<PviRect> m_controls = new ArrayList<PviRect>();

	int m_next_ctrl_id = 0;
	public int get_ctrl_id() {
		m_next_ctrl_id++;
		return m_next_ctrl_id;
	}
	
	public class PviRect extends Object {
		public int m_x,m_y,m_width,m_height;
		public int m_ctrl_id;
		public String m_text = null;
		public int m_bitmap_resource_id;
		public int m_focused_bitmap_resource_id;
		private OnPviFocusChangeListener m_OnFocusChangeListener = null; 
		private OnPviClickListener m_OnClickListener = null;
		private OnPviKeyListener m_OnKeyListener = null;
		boolean m_Focusable = true;
		String m_tag;
		PviRect(int x,int y, int width,int height,int ctrl_id) {
			m_x = x; m_y = y; m_width = width; m_height = height;
			m_ctrl_id = ctrl_id;
			m_bitmap_resource_id = -1;
			m_focused_bitmap_resource_id = -1;
			m_controls.add(this);
			if(m_focused_ctrl_id == -1) {
				m_focused_ctrl_id = ctrl_id;//first inserted control has default focus
			}
		}
		void onClick() {
			if(m_OnClickListener != null) {
				m_OnClickListener.onClick(this);
			}
		}
		boolean PtInRect(int x,int y) {
			if( ((x>=m_x)&&(x<(m_x+m_width) )) &&
					((y>=m_y)&&(y<(m_y+m_height)))	){
				return true;
			}
			return false;
		}
		void setImageResource(int res_id) {
			m_bitmap_resource_id = res_id;
		}
		void setFocusedImageResource(int res_id) {
			m_focused_bitmap_resource_id = res_id;
		}
		int getId() {
			return m_ctrl_id;
		}
		String getTag() {
			return m_tag;
		}
		void setTag(String tag) {
			m_tag = tag;
		}
		void requestFocus() {
			m_focused_ctrl_id = this.m_ctrl_id;
		}
		void setFocusable(boolean val) {
			m_Focusable = val;
		}
		void setOnFocusChangeListener(OnPviFocusChangeListener OnFocusChangeListener_v) {
			m_OnFocusChangeListener = OnFocusChangeListener_v;
		}
		void setOnKeyListener(OnPviKeyListener OnKeyListener_v) {
			m_OnKeyListener =  OnKeyListener_v;
		}
		void setOnClickListener(OnPviClickListener OnClickListener_v) {
			m_OnClickListener = OnClickListener_v;
		}
	}
	public abstract class OnPviKeyListener extends Object {
		public abstract boolean onKey(PviRect v, int keyCode, KeyEvent event);
	}
	public abstract class OnPviClickListener extends Object {
		public abstract void onClick(PviRect v);
	}
	public abstract class OnPviFocusChangeListener extends Object {
		public abstract void onFocusChange (PviRect v, boolean hasFocus); 

	}
	
	/**
	 * 行间距按钮
	 */
	private PviRect linesize1   = null ;
	private PviRect linesize05 = null ;
	private PviRect linesize10 = null ;
	private PviRect linesize15 = null ;
	private PviRect linesize20 = null ;
	private PviRect linesize25 = null ;
	private PviRect linesize30 = null ;
	
	/**
	 * 
	 */
	private PviRect sufang1 = null ;
	private PviRect sufang075 = null ;
	private PviRect sufang100 = null ;
	private PviRect sufang125 = null ;
	private PviRect sufang150 = null ;
	private PviRect sufang200 = null ;
	private PviRect sufang250 = null ;
	
	/**
	 * 字体大小
	 */
	private PviRect fonesize1 = null ;
	private PviRect fonesize16 = null ;
	private PviRect fonesize18 = null ;
	private PviRect fonesize20 = null ;
	private PviRect fonesize22 = null ;
	private PviRect fonesize24 = null ;
	private PviRect fonesize26 = null ;
	/**
	 * 
	 */
	
	/**
	 * 确定取消按钮
	 */
	public Button okButton;
	public Button cancelButton;
	
	/***
	 * 设置开关 是否显示可以选择
	 */
	private boolean fontBool = true ;
	private boolean lineBool = true ;
	private boolean sufangBool = true ;
	/**
	 * 
	 */
	protected Dialog m_dlg;
	/**
	 * 设置信息map
	 */
	private Map<String,String> chooseMap = new HashMap<String,String>() ;
	
	public void setFontBool(boolean fontBool) {
		Logger.i(TAG, "setFontBool" + fontBool);
		this.fontBool = fontBool;
		bindButton();
	}
	public void setLineBool(boolean lineBool) {
		Logger.i(TAG, "setLineBool" + lineBool);
		this.lineBool = lineBool;
		bindButton();
	}
	public void setSufangBool(boolean sufangBool) {
		Logger.i(TAG, "setSufangBool" + sufangBool);
		this.sufangBool = sufangBool;
		bindButton();
	}
	/**
	 * 回调类
	 */
	private SetListener setListener = null ;
	
	/**
	 * 设置回调类
	 * @param setListener
	 */
	public void setSetListener(SetListener setListener) {
		this.setListener = setListener;
	}
	
	/**
	 * 调用设置成功或失败后返回的函数，由具体调用者去实现
	 * @author rd036
	 */
	public interface SetListener {
		public void chooseDoListener(boolean ok,Map<String,String> chooseButton);
	}
	
	/**
	 * 对话框
	 * @param dlg_v
	 */
	public void setDialog(PviAlertDialog dlg_v) {
		Logger.i(TAG, "setDialog");
		m_dlg = dlg_v;
		if (m_dlg != null) {
			m_dlg.setTitle(R.string.fontSizeDialogBoxTitle);
			if (m_dlg instanceof PviAlertDialog) {
				((PviAlertDialog) m_dlg).setView(this);
				((PviAlertDialog) m_dlg).setWinWidth(550);
			}
		}
	};
	/**
	 * 对话框
	 * @param dlg_v
	 */
	public void setDialog(Dialog dlg_v) {
		Logger.i(TAG, "setDialog");
		m_dlg = dlg_v;
		if (m_dlg != null) {
			m_dlg.setTitle(R.string.fontSizeDialogBoxTitle);
			m_dlg.setContentView(this);
		}
	};
	
	protected void onFinishInflate () {
		Logger.i(TAG, "onFinishInflate");
		super.onFinishInflate ();
		findButton();
		bindButton();
	};
	
	/***
	 * 查找按钮
	 */
	private void findButton(){
		Logger.i(TAG, "findButton");
		
		
/*
		linesize1 = (ImageView)findViewById(R.id.linesize1);
		linesize05 = (ImageView)findViewById(R.id.linesize05);
		linesize10 = (ImageView)findViewById(R.id.linesize10);
		linesize15 = (ImageView)findViewById(R.id.linesize15);
		linesize20 = (ImageView)findViewById(R.id.linesize20);
		linesize25 = (ImageView)findViewById(R.id.linesize25);
		linesize30 = (ImageView)findViewById(R.id.linesize30);
		
		sufang1 = (ImageView)findViewById(R.id.sufang1);
		sufang075 = (ImageView)findViewById(R.id.sufang75);
		sufang100 = (ImageView)findViewById(R.id.sufang100);
		sufang125 = (ImageView)findViewById(R.id.sufang125);
		sufang150 = (ImageView)findViewById(R.id.sufang150);
		sufang200 = (ImageView)findViewById(R.id.sufang200);
		sufang250 = (ImageView)findViewById(R.id.sufang250);
		
		fonesize1 = (ImageView)findViewById(R.id.fonesize1);
		fonesize16 = (ImageView)findViewById(R.id.fonesize16);
		fonesize18 = (ImageView)findViewById(R.id.fonesize18);
		fonesize20 = (ImageView)findViewById(R.id.fonesize20);
		fonesize22 = (ImageView)findViewById(R.id.fonesize22);
		fonesize24 = (ImageView)findViewById(R.id.fonesize24);
		fonesize26 = (ImageView)findViewById(R.id.fonesize26);
		
 */
		int x=0,y=0,width=70,height=80;
		x = 5; y = 1;
		linesize1 = new PviRect(x,y,width,height,R.id.linesize1);
		linesize1.setTag("3513513");
		y = y + 20;
		x = x+ width + 32;
		linesize05 = new PviRect(x,y,width,height,R.id.linesize05);
		linesize05.setTag("5");
		x = x+ width;
		linesize10 = new PviRect(x,y,width,height,R.id.linesize10);
		linesize10.setTag("10");
		x = x+ width;
		linesize15 = new PviRect(x,y,width,height,R.id.linesize15);
		linesize15.setTag("15");
		x = x+ width;
		linesize20 = new PviRect(x,y,width,height,R.id.linesize20);
		linesize20.setTag("20");
		x = x+ width;
		linesize25 = new PviRect(x,y,width,height,R.id.linesize25);
		linesize25.setTag("25");
		x = x+ width;
		linesize30 = new PviRect(x,y,width,height,R.id.linesize30);
		linesize30.setTag("30");
		
		x = 5; y = y + height + 5;
		sufang1 = new PviRect(x,y,width,height,R.id.sufang1);
		sufang1.setTag("14");
		y = y + 20;
		x = x+ width + 32;
		sufang075 = new PviRect(x,y,width,height,R.id.sufang75);
		sufang075.setTag("0.75");
		x = x+ width;
		sufang100 = new PviRect(x,y,width,height,R.id.sufang100);
		sufang100.setTag("1.0");
		x = x+ width;
		sufang125 = new PviRect(x,y,width,height,R.id.sufang125);
		sufang125.setTag("1.25");
		x = x+ width;
		sufang150 = new PviRect(x,y,width,height,R.id.sufang150);
		sufang150.setTag("1.5");
		x = x+ width;
		sufang200 = new PviRect(x,y,width,height,R.id.sufang200);
		sufang200.setTag("2.0");
		x = x+ width;
		sufang250 = new PviRect(x,y,width,height,R.id.sufang250);
		sufang250.setTag("2.5");
		
		x = 5; y = y + height + 5;
		fonesize1 = new PviRect(x,y,width,height,R.id.fonesize1);
		fonesize1.setTag("16");
		y = y + 20;
		x = x+ width + 32;
		fonesize16 = new PviRect(x,y,width,height,R.id.fonesize16);
		fonesize16.setTag("16");
		x = x+ width;
		fonesize18 = new PviRect(x,y,width,height,R.id.fonesize18);
		fonesize18.setTag("18");
		x = x+ width;
		fonesize20 = new PviRect(x,y,width,height,R.id.fonesize20);
		fonesize20.setTag("20");
		x = x+ width;
		fonesize22 = new PviRect(x,y,width,height,R.id.fonesize22);
		fonesize22.setTag("22");
		x = x+ width;
		fonesize24 = new PviRect(x,y,width,height,R.id.fonesize24);
		fonesize24.setTag("24");
		x = x+ width;
		fonesize26 = new PviRect(x,y,width,height,R.id.fonesize26);
		fonesize26.setTag("26");
		
		okButton = (Button)findViewById(R.id.ok);
		cancelButton = (Button)findViewById(R.id.cancel);
	}
	
	/**
	 * 绑定按钮
	 */
	private void bindButton(){
		Logger.i(TAG, "bindButton");
		if (lineBool) {
			linesize1.setOnClickListener(chooseLayListen);
			linesize1.setFocusable(false);
			linesize1.setOnFocusChangeListener(ofcl);
			linesize05.setOnClickListener(lineListener);
			linesize05.setFocusable(true);
			linesize05.setOnFocusChangeListener(ofcl);
			linesize10.setOnClickListener(lineListener);
			linesize10.setOnFocusChangeListener(ofcl);
			linesize10.setFocusable(true);
			linesize15.setOnClickListener(lineListener);
			linesize15.setOnFocusChangeListener(ofcl);
			linesize15.setFocusable(true);
			linesize20.setOnClickListener(lineListener);
			linesize20.setOnFocusChangeListener(ofcl);
			linesize20.setFocusable(true);
			linesize25.setOnClickListener(lineListener);
			linesize25.setOnFocusChangeListener(ofcl);
			linesize25.setFocusable(true);
			linesize30.setOnClickListener(lineListener);
			linesize30.setOnFocusChangeListener(ofcl);
			linesize30.setFocusable(true);
			linesize1.setImageResource(R.drawable.linesize1b) ;
			linesize05.setImageResource(R.drawable.linesize05b);
			linesize10.setImageResource(R.drawable.linesize10b);
			linesize15.setImageResource(R.drawable.linesize15b);
			linesize20.setImageResource(R.drawable.linesize20b);
			linesize25.setImageResource(R.drawable.linesize25b);
			linesize30.setImageResource(R.drawable.linesize30b);
		} else{
			linesize1.setOnClickListener(null);
			linesize1.setOnKeyListener(null);
			linesize1.setFocusable(false);
			linesize05.setOnClickListener(null);
			linesize05.setOnKeyListener(null);
			linesize05.setFocusable(false);
			linesize10.setOnClickListener(null);
			linesize10.setOnKeyListener(null);
			linesize10.setFocusable(false);
			linesize15.setOnClickListener(null);
			linesize15.setOnKeyListener(null);
			linesize15.setFocusable(false);
			linesize20.setOnClickListener(null);
			linesize20.setOnKeyListener(null);
			linesize20.setFocusable(false);
			linesize25.setOnClickListener(null);
			linesize25.setOnKeyListener(null);
			linesize25.setFocusable(false);
			linesize30.setOnClickListener(null);
			linesize30.setOnKeyListener(null);
			linesize30.setFocusable(false);
			linesize1.setImageResource(R.drawable.linesize1c) ;
			linesize05.setImageResource(R.drawable.linesize5c);
			linesize10.setImageResource(R.drawable.linesize10c);
			linesize15.setImageResource(R.drawable.linesize15c);
			linesize20.setImageResource(R.drawable.linesize20c);
			linesize25.setImageResource(R.drawable.linesize25c);
			linesize30.setImageResource(R.drawable.linesize30c);
		}
		
		if (sufangBool) {
			sufang1.setOnClickListener(chooseLayListen);
			sufang1.setOnFocusChangeListener(ofcl);
			sufang1.setFocusable(false);
			sufang075.setOnClickListener(sufangListen);
			sufang075.setOnFocusChangeListener(ofcl);
			sufang075.setFocusable(true);
			sufang100.setOnClickListener(sufangListen);
			sufang100.setOnFocusChangeListener(ofcl);
			sufang100.setFocusable(true);
			sufang125.setOnClickListener(sufangListen);
			sufang125.setOnFocusChangeListener(ofcl);
			sufang125.setFocusable(true);
			sufang150.setOnClickListener(sufangListen);
			sufang150.setOnFocusChangeListener(ofcl);
			sufang150.setFocusable(true);
			sufang200.setOnClickListener(sufangListen);
			sufang200.setOnFocusChangeListener(ofcl);
			sufang200.setFocusable(true);
			sufang250.setOnClickListener(sufangListen);
			sufang250.setOnFocusChangeListener(ofcl);
			sufang250.setFocusable(true);
			sufang1.setImageResource(R.drawable.sufang1b) ;
			sufang075.setImageResource(R.drawable.sufang75b);
			sufang100.setImageResource(R.drawable.sufang100b);
			sufang125.setImageResource(R.drawable.sufang125b);
			sufang150.setImageResource(R.drawable.sufang150b);
			sufang200.setImageResource(R.drawable.sufang200b);
			sufang250.setImageResource(R.drawable.sufang250b);
		} else{
			sufang1.setOnClickListener(null);
			sufang1.setOnKeyListener(null);
			sufang1.setFocusable(false);
			sufang075.setOnClickListener(null);
			sufang075.setOnKeyListener(null);
			sufang075.setFocusable(false);
			sufang100.setOnClickListener(null);
			sufang100.setOnKeyListener(null);
			sufang100.setFocusable(false);
			sufang125.setOnClickListener(null);
			sufang125.setOnKeyListener(null);
			sufang125.setFocusable(false);
			sufang150.setOnClickListener(null);
			sufang150.setOnKeyListener(null);
			sufang150.setFocusable(false);
			sufang200.setOnClickListener(null);
			sufang200.setOnKeyListener(null);
			sufang200.setFocusable(false);
			sufang250.setOnClickListener(null);
			sufang250.setOnKeyListener(null);
			sufang250.setFocusable(false);
			sufang1.setImageResource(R.drawable.sufang1c) ;
			sufang075.setImageResource(R.drawable.sufang075c);
			sufang100.setImageResource(R.drawable.sufang100c);
			sufang125.setImageResource(R.drawable.sufang125c);
			sufang150.setImageResource(R.drawable.sufang150c);
			sufang200.setImageResource(R.drawable.sufang200c);
			sufang250.setImageResource(R.drawable.sufang250c);
		}
		if (fontBool) {
			fonesize1.setOnClickListener(chooseLayListen);
			fonesize1.setOnFocusChangeListener(ofcl);
			fonesize1.setFocusable(false);
			fonesize16.setOnClickListener(foneListen);
			fonesize16.setOnFocusChangeListener(ofcl);
			fonesize16.setFocusable(true);
			fonesize18.setOnClickListener(foneListen);
			fonesize18.setOnFocusChangeListener(ofcl);
			fonesize18.setFocusable(true);
			fonesize20.setOnClickListener(foneListen);
			fonesize20.setOnFocusChangeListener(ofcl);
			fonesize20.setFocusable(true);
			fonesize22.setOnClickListener(foneListen);
			fonesize22.setOnFocusChangeListener(ofcl);
			fonesize22.setFocusable(true);
			fonesize24.setOnClickListener(foneListen);
			fonesize24.setOnFocusChangeListener(ofcl);
			fonesize24.setFocusable(true);
			fonesize26.setOnClickListener(foneListen);
			fonesize26.setOnFocusChangeListener(ofcl);
			fonesize26.setFocusable(true);
			fonesize1.setImageResource(R.drawable.fonesize1b) ;
			fonesize16.setImageResource(R.drawable.fonesize16b);
			fonesize18.setImageResource(R.drawable.fonesize18b);
			fonesize20.setImageResource(R.drawable.fonesize20b);
			fonesize22.setImageResource(R.drawable.fonesize22b);
			fonesize24.setImageResource(R.drawable.fonesize24b);
			fonesize26.setImageResource(R.drawable.fonesize26b);
		} else{
			fonesize1.setOnClickListener(null);
			fonesize1.setOnKeyListener(null);
			fonesize1.setFocusable(false);
			fonesize16.setOnClickListener(null);
			fonesize16.setOnKeyListener(null);
			fonesize16.setFocusable(false);
			fonesize18.setOnClickListener(null);
			fonesize18.setOnKeyListener(null);
			fonesize18.setFocusable(false);
			fonesize20.setOnClickListener(null);
			fonesize20.setOnKeyListener(null);
			fonesize20.setFocusable(false);
			fonesize22.setOnClickListener(null);
			fonesize22.setOnKeyListener(null);
			fonesize22.setFocusable(false);
			fonesize24.setOnClickListener(null);
			fonesize24.setOnKeyListener(null);
			fonesize24.setFocusable(false);
			fonesize26.setOnClickListener(null);
			fonesize26.setOnKeyListener(null);
			fonesize26.setFocusable(false);
			fonesize1.setImageResource(R.drawable.fonesize1c) ;
			fonesize16.setImageResource(R.drawable.fonesize16c);
			fonesize18.setImageResource(R.drawable.fonesize18c);
			fonesize20.setImageResource(R.drawable.fonesize20c);
			fonesize22.setImageResource(R.drawable.fonesize22c);
			fonesize24.setImageResource(R.drawable.fonesize24c);
			fonesize26.setImageResource(R.drawable.fonesize26c);
		}
		
		okButton.setOnClickListener(okListen);
		//okButton.setOnFocusChangeListener(ofcl);
		okButton.setOnKeyListener(onKeyListener);
		okButton.setFocusable(true);
		cancelButton.setOnClickListener(cancelListen);
		//cancelButton.setOnFocusChangeListener(ofcl);
		cancelButton.setOnKeyListener(onKeyListener);
		cancelButton.setFocusable(true);
	}
	
	/**
	 * 行间距监听器
	 */
	private OnPviClickListener lineListener  = new OnPviClickListener(){
		@Override
		public void onClick(PviRect v) {
			Logger.i(TAG, "lineListener + onClick" + v.getId());
			linesize05.setImageResource(R.drawable.linesize05b);
			linesize10.setImageResource(R.drawable.linesize10b);
			linesize15.setImageResource(R.drawable.linesize15b);
			linesize20.setImageResource(R.drawable.linesize20b);
			linesize25.setImageResource(R.drawable.linesize25b);
			linesize30.setImageResource(R.drawable.linesize30b);
			
			chooseLayListen.onClick(v);
			
			linesize1.setImageResource(R.drawable.linesize1a);
			int id = v.getId();
			String tag = v.getTag().toString();
			chooseMap.put(CON_LINESIZE, tag);
			switch(id){
			case R.id.linesize05:
				linesize05.setImageResource(R.drawable.linesize05a);
				linesize05.requestFocus();
				break ;
			case R.id.linesize10:
				linesize10.setImageResource(R.drawable.linesize10a);
				linesize10.requestFocus();
				break ;
			case R.id.linesize15:
				linesize15.setImageResource(R.drawable.linesize15a);
				linesize15.requestFocus();
				break ;
			case R.id.linesize20:
				linesize20.setImageResource(R.drawable.linesize20a);
				linesize20.requestFocus();
				break ;
			case R.id.linesize25:
				linesize25.setImageResource(R.drawable.linesize25a);
				linesize25.requestFocus();
				break ;
			case R.id.linesize30:
				linesize30.setImageResource(R.drawable.linesize30a);
				linesize30.requestFocus();
				break ;
			default:
				linesize05.setImageResource(R.drawable.linesize05a);
				linesize05.requestFocus();
				return ;
				
			}
		}
	};
	
	/**
	 * 字体监听器
	 */
	private OnPviClickListener foneListen = new OnPviClickListener(){
		@Override
		public void onClick(PviRect v) {
			Logger.i(TAG, "foneListen + onClick" + v.getId());
			fonesize16.setImageResource(R.drawable.fonesize16b);
			fonesize18.setImageResource(R.drawable.fonesize18b);
			fonesize20.setImageResource(R.drawable.fonesize20b);
			fonesize22.setImageResource(R.drawable.fonesize22b);
			fonesize24.setImageResource(R.drawable.fonesize24b);
			fonesize26.setImageResource(R.drawable.fonesize26b);
			
			chooseLayListen.onClick(v);
			fonesize1.setImageResource(R.drawable.fonesize1a);;
			int id = v.getId();
			String tag = v.getTag().toString();
			chooseMap.put(CON_FONESIZE, tag);
			switch(id){
			case R.id.fonesize16:
				fonesize16.setImageResource(R.drawable.fonesize16a);
				fonesize16.requestFocus();
				break;
			case R.id.fonesize18:
				fonesize18.setImageResource(R.drawable.fonesize18a);
				fonesize18.requestFocus();
				break;
			case R.id.fonesize20:
				fonesize20.setImageResource(R.drawable.fonesize20a);
				fonesize20.requestFocus();
				break;
			case R.id.fonesize22:
				fonesize22.setImageResource(R.drawable.fonesize22a);
				fonesize22.requestFocus();
				break;
			case R.id.fonesize24:
				fonesize24.setImageResource(R.drawable.fonesize24a);
				fonesize24.requestFocus();
				break;
			case R.id.fonesize26:
				fonesize26.setImageResource(R.drawable.fonesize26a);
				fonesize26.requestFocus();
				break;
			default:
				fonesize16.setImageResource(R.drawable.fonesize16a);
				fonesize16.requestFocus();
				break;
			}
		}
	};
	
	/**
	 * 监听器
	 */
	private OnPviClickListener sufangListen = new OnPviClickListener(){
		@Override
		public void onClick(PviRect v) {
			Logger.i(TAG, "sufangListen + onClick" + v.getId());
			sufang075.setImageResource(R.drawable.sufang75b);
			sufang100.setImageResource(R.drawable.sufang100b);
			sufang125.setImageResource(R.drawable.sufang125b);
			sufang150.setImageResource(R.drawable.sufang150b);
			sufang200.setImageResource(R.drawable.sufang200b);
			sufang250.setImageResource(R.drawable.sufang250b);
			
			chooseLayListen.onClick(v);
			sufang1.setImageResource(R.drawable.sufang1a);
			int id = v.getId();
			String tag = v.getTag().toString();
			chooseMap.put(CON_SUFANG, tag);
			switch(id){
			case R.id.sufang75:
				sufang075.setImageResource(R.drawable.sufang75a);
				sufang075.requestFocus();
				break ;
			case R.id.sufang100:
				sufang100.setImageResource(R.drawable.sufang100a);
				sufang100.requestFocus();
				break ;
			case R.id.sufang125:
				sufang125.setImageResource(R.drawable.sufang125a);
				sufang125.requestFocus();
				break ;
			case R.id.sufang150:
				sufang150.setImageResource(R.drawable.sufang150a);
				sufang150.requestFocus();
				break ;
			case R.id.sufang200:
				sufang200.setImageResource(R.drawable.sufang200a);
				sufang200.requestFocus();
				break ;
			case R.id.sufang250:
				sufang250.setImageResource(R.drawable.sufang250a);
				sufang250.requestFocus();
				break ;
			}
		}
		
	};
	
	/**
	 * 选择栏监听器
	 */
	private OnPviClickListener chooseLayListen = new OnPviClickListener(){
		@Override
		public void onClick(PviRect v) {
			if(lineBool){
				linesize1.setImageResource(R.drawable.linesize1b);
			}
			if(sufangBool){
				sufang1.setImageResource(R.drawable.sufang1b);
			}
			if(fontBool){
				fonesize1.setImageResource(R.drawable.fonesize1b);
			}
			if(v == null) return ;
			Logger.i(TAG, "chooseLayListen + onClick" + v.getId());
			int id = v.getId();
			switch(id){
			case R.id.linesize1:
				linesize1.setImageResource(R.drawable.linesize1a);
				break ;
			case R.id.sufang1:
				sufang1.setImageResource(R.drawable.sufang1a);
				break ;
			case R.id.fonesize1:
				fonesize1.setImageResource(R.drawable.fonesize1a);
				break ;
			default:
				break ;
			}
		}
		
	};
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
		
			int x = (int)event.getX();
			int y = (int)event.getY();
			int focus_id_tmp = -1;
			for(int i=0;i<m_controls.size();i++) {
				PviRect ctrl_v = m_controls.get(i);
				if(ctrl_v.PtInRect(x, y)) {
					ctrl_v.onClick();
					refresh();
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * 确定按钮监听器
	 */
	private View.OnClickListener okListen = new View.OnClickListener(){
		public void onClick(View v) {
			Logger.i(TAG, "okListen + onClick");
			if(m_dlg != null){
				m_dlg.dismiss();
			}
			if(setListener != null){
				setListener.chooseDoListener(true, chooseMap);
			}
		}
	};
	
	/**
	 * 取消按钮监听器
	 */
	private View.OnClickListener cancelListen = new View.OnClickListener(){
		public void onClick(View v) {
			Logger.i(TAG, "cancelListen + onClick");
			if(m_dlg != null){
				m_dlg.dismiss();
			}
			if(setListener != null){
				setListener.chooseDoListener(false, chooseMap);
			}
		}
	};

	/*@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Logger.i(TAG, "onKeyUp" + keyCode + event);
		if (keyCode == event.KEYCODE_ENTER || keyCode == event.KEYCODE_MENU) {
			if(m_dlg != null){
				m_dlg.dismiss();
			}
			if(setListener != null){
				setListener.chooseDoListener(true, chooseMap);
			}
			return true ;
		}
		if(keyCode == event.KEYCODE_BACK){
			if(m_dlg != null){
				m_dlg.dismiss();
			}
		}
		return true;
	}*/
	
	/**
	 * 初始化数据 给面板初始化
	 * @param chooseMap
	 */
	public void setChooseMap(Map<String, String> chooseMap) {
		Logger.i(TAG, "setChooseMap" + chooseMap.toString());
		if(chooseMap == null){
			return ;
		}
		this.chooseMap = chooseMap;
		if(chooseMap.get(CON_FONESIZE) != null && !"".equals(chooseMap.get(CON_FONESIZE).trim())){
			chooseLayListen.onClick(fonesize1);
			if(chooseMap.get(CON_FONESIZE).equals(fonesize16.getTag().toString())){
				fonesize16.setImageResource(R.drawable.fonesize16a);
				fonesize16.requestFocus();
			}
			if(chooseMap.get(CON_FONESIZE).equals(fonesize18.getTag().toString())){
				fonesize18.setImageResource(R.drawable.fonesize18a);
				fonesize18.requestFocus();
			}
			if(chooseMap.get(CON_FONESIZE).equals(fonesize20.getTag().toString())){
				fonesize20.setImageResource(R.drawable.fonesize20a);
				fonesize20.requestFocus();
			}
			if(chooseMap.get(CON_FONESIZE).equals(fonesize22.getTag().toString())){
				fonesize22.setImageResource(R.drawable.fonesize22a);
				fonesize22.requestFocus();
				
			}
			if(chooseMap.get(CON_FONESIZE).equals(fonesize24.getTag().toString())){
				fonesize24.setImageResource(R.drawable.fonesize24a);
				fonesize24.requestFocus();
				
			}
			if(chooseMap.get(CON_FONESIZE).equals(fonesize26.getTag().toString())){
				fonesize26.setImageResource(R.drawable.fonesize26a);
				fonesize26.requestFocus();
			}
		}
		
		if(chooseMap.get(CON_LINESIZE) != null && !"".equals(chooseMap.get(CON_LINESIZE).trim())){
			chooseLayListen.onClick(linesize1);
			if(chooseMap.get(CON_LINESIZE).equals(linesize05.getTag().toString())){
				lineListener.onClick(linesize05);
				linesize05.requestFocus();
			}
			if(chooseMap.get(CON_LINESIZE).equals(linesize10.getTag().toString())){
				lineListener.onClick(linesize10);
				linesize10.requestFocus();
			}
			if(chooseMap.get(CON_LINESIZE).equals(linesize15.getTag().toString())){
				lineListener.onClick(linesize15);
				linesize15.requestFocus();
			}
			if(chooseMap.get(CON_LINESIZE).equals(linesize20.getTag().toString())){
				lineListener.onClick(linesize20);
				linesize20.requestFocus();
			}
			if(chooseMap.get(CON_LINESIZE).equals(linesize25.getTag().toString())){
				lineListener.onClick(linesize25);
				linesize25.requestFocus();
			}
			if(chooseMap.get(CON_LINESIZE).equals(linesize30.getTag().toString())){
				lineListener.onClick(linesize30);
				linesize30.requestFocus();
			}
		}
		
		if(chooseMap.get(CON_SUFANG) != null && !"".equals(chooseMap.get(CON_SUFANG).trim())){
			float sufang = Float.parseFloat(chooseMap.get(CON_SUFANG));
			if(absFloat(sufang - Float.parseFloat(sufang075.getTag().toString())) < 0.1){
				sufangListen.onClick(sufang075);
				sufang075.requestFocus();
			}
			if(absFloat(sufang - Float.parseFloat(sufang100.getTag().toString())) < 0.1){
				sufangListen.onClick(sufang100);
				sufang100.requestFocus();
			}
			if(absFloat(sufang - Float.parseFloat(sufang125.getTag().toString())) < 0.1){
				sufangListen.onClick(sufang125);
				sufang125.requestFocus();
			}
			if(absFloat(sufang - Float.parseFloat(sufang150.getTag().toString())) < 0.1){
				sufangListen.onClick(sufang150);
				sufang150.requestFocus();
			}
			if(absFloat(sufang - Float.parseFloat(sufang200.getTag().toString())) < 0.1){
				sufangListen.onClick(sufang200);
				sufang200.requestFocus();
			}
			if(absFloat(sufang - Float.parseFloat(sufang250.getTag().toString())) < 0.1){
				sufangListen.onClick(sufang250);
				sufang250.requestFocus();
			}
		}
		
		
	}
	
	private View.OnKeyListener onKeyListener = new OnKeyListener() {

		public boolean onKey(View v, int keyCode, KeyEvent event) {
			Logger.i(TAG,
					"public boolean onKey(View v, int keyCode, KeyEvent event)"
							+ keyCode + event.toString() + v.toString());
			if (event.getAction() == KeyEvent.ACTION_UP) {
				if (keyCode == event.KEYCODE_DPAD_RIGHT) {
					switch (v.getId()) {
					case R.id.font_selection_dialog_top_view:
						ReadSetView this_view = (ReadSetView)(v);
						switch(this_view.get_focused_ctrl_id()) {
						case R.id.linesize1:
							linesize05.requestFocus();
							lineListener.onClick(linesize05);
							break;
						case R.id.linesize05:
							linesize10.requestFocus();
							lineListener.onClick(linesize10);
							break;
						case R.id.linesize10:
							linesize15.requestFocus();
							lineListener.onClick(linesize15);
							break;
						case R.id.linesize15:
							linesize20.requestFocus();
							lineListener.onClick(linesize20);
							break;
						case R.id.linesize20:
							linesize25.requestFocus();
							lineListener.onClick(linesize25);
							break;
						case R.id.linesize25:
							linesize30.requestFocus();
							lineListener.onClick(linesize30);
							break;
						case R.id.linesize30:
							linesize05.requestFocus();
							lineListener.onClick(linesize05);
							break;
	
						case R.id.sufang1:
							sufang075.requestFocus();
							sufangListen.onClick(sufang075);
							break;
						case R.id.sufang75:
							sufang100.requestFocus();
							sufangListen.onClick(sufang100);
							break;
						case R.id.sufang100:
							sufang125.requestFocus();
							sufangListen.onClick(sufang125);
							break;
						case R.id.sufang125:
							sufang150.requestFocus();
							sufangListen.onClick(sufang150);
							break;
						case R.id.sufang150:
							sufang200.requestFocus();
							sufangListen.onClick(sufang200);
							break;
						case R.id.sufang200:
							sufang250.requestFocus();
							sufangListen.onClick(sufang250);
							break;
						case R.id.sufang250:
							sufang075.requestFocus();
							sufangListen.onClick(sufang075);
							break;
	
						case R.id.fonesize1:
							fonesize16.requestFocus();
							foneListen.onClick(fonesize16);
							break;
						case R.id.fonesize16:
							fonesize18.requestFocus();
							foneListen.onClick(fonesize18);
							break;
						case R.id.fonesize18:
							fonesize20.requestFocus();
							foneListen.onClick(fonesize20);
							break;
						case R.id.fonesize20:
							fonesize22.requestFocus();
							foneListen.onClick(fonesize22);
							break;
						case R.id.fonesize22:
							fonesize24.requestFocus();
							foneListen.onClick(fonesize24);
							break;
						case R.id.fonesize24:
							fonesize26.requestFocus();
							foneListen.onClick(fonesize26);
							break;
						case R.id.fonesize26:
							fonesize16.requestFocus();
							foneListen.onClick(fonesize16);
							break;
						}
						break;
					case R.id.ok:
						cancelButton.requestFocus();
						break ;
					}
					refresh();
					return true;
				}
			if (keyCode == event.KEYCODE_DPAD_LEFT) {
				switch (v.getId()) {
				case R.id.font_selection_dialog_top_view:
					ReadSetView this_view = (ReadSetView)(v);
					switch(this_view.get_focused_ctrl_id()) {
					case R.id.linesize1:
						linesize30.requestFocus();
						lineListener.onClick(linesize30);
						break;
					case R.id.linesize15:
						linesize10.requestFocus();
						lineListener.onClick(linesize10);
						break;
					case R.id.linesize20:
						linesize15.requestFocus();
						lineListener.onClick(linesize15);
						break;
					case R.id.linesize25:
						linesize20.requestFocus();
						lineListener.onClick(linesize20);
						break;
					case R.id.linesize30:
						linesize25.requestFocus();
						lineListener.onClick(linesize25);
						break;
					case R.id.linesize05:
						linesize30.requestFocus();
						lineListener.onClick(linesize30);
						break;
					case R.id.linesize10:
						linesize05.requestFocus();
						lineListener.onClick(linesize05);
						break;
	
					case R.id.sufang1:
						sufang250.requestFocus();
						sufangListen.onClick(sufang250);
						break;
					case R.id.sufang125:
						sufang100.requestFocus();
						sufangListen.onClick(sufang100);
						break;
					case R.id.sufang150:
						sufang125.requestFocus();
						sufangListen.onClick(sufang125);
						break;
					case R.id.sufang200:
						sufang150.requestFocus();
						sufangListen.onClick(sufang150);
						break;
					case R.id.sufang250:
						sufang200.requestFocus();
						sufangListen.onClick(sufang200);
						break;
					case R.id.sufang75:
						sufang250.requestFocus();
						sufangListen.onClick(sufang250);
						break;
					case R.id.sufang100:
						sufang075.requestFocus();
						sufangListen.onClick(sufang075);
						break;
	
					case R.id.fonesize1:
						fonesize26.requestFocus();
						foneListen.onClick(fonesize26);
						break;
					case R.id.fonesize20:
						fonesize18.requestFocus();
						foneListen.onClick(fonesize18);
						break;
					case R.id.fonesize22:
						fonesize20.requestFocus();
						foneListen.onClick(fonesize20);
						break;
					case R.id.fonesize24:
						fonesize22.requestFocus();
						foneListen.onClick(fonesize22);
						break;
					case R.id.fonesize26:
						fonesize24.requestFocus();
						foneListen.onClick(fonesize24);
						break;
					case R.id.fonesize16:
						fonesize26.requestFocus();
						foneListen.onClick(fonesize26);
						break;
					case R.id.fonesize18:
						fonesize16.requestFocus();
						foneListen.onClick(fonesize16);
						break;
					}
					break;
				case R.id.cancel:
					okButton.requestFocus();
					break ;
				}
				refresh();
				return true;
			}
			if (keyCode == event.KEYCODE_DPAD_DOWN) {
				switch (v.getId()) {
				case R.id.font_selection_dialog_top_view:
					ReadSetView this_view = (ReadSetView)(v);
					switch(this_view.get_focused_ctrl_id()) {
					case R.id.linesize1:
					case R.id.linesize15:
					case R.id.linesize20:
					case R.id.linesize25:
					case R.id.linesize30:
					case R.id.linesize05:
					case R.id.linesize10:
						if(sufangBool){
							sufangListen.onClick(sufang075);
							refresh();
						}else if(fontBool) {
							foneListen.onClick(fonesize16);
							refresh();
						}else {
							okButton.requestFocus();
						}
						return true;
					
					case R.id.sufang1:
					case R.id.sufang125:
					case R.id.sufang150:
					case R.id.sufang200:
					case R.id.sufang250:
					case R.id.sufang75:
					case R.id.sufang100:
						if(fontBool) {
							foneListen.onClick(fonesize16);
							refresh();
						}else {
							okButton.requestFocus();
						}
						return true;
					case R.id.fonesize1:
					case R.id.fonesize20:
					case R.id.fonesize22:
					case R.id.fonesize24:
					case R.id.fonesize26:
					case R.id.fonesize16:
					case R.id.fonesize18:
						okButton.requestFocus();
						return true;
					}
					break;
				case R.id.ok:
					cancelButton.requestFocus();
					return true;
				}
			}
			if (keyCode == event.KEYCODE_DPAD_UP) {
				switch (v.getId()) {
				case R.id.font_selection_dialog_top_view:
					ReadSetView this_view = (ReadSetView)(v);
					switch(this_view.get_focused_ctrl_id()) {
					case R.id.linesize1:
					case R.id.linesize15:
					case R.id.linesize20:
					case R.id.linesize25:
					case R.id.linesize30:
					case R.id.linesize05:
					case R.id.linesize10:
						return true;
					case R.id.sufang1:
					case R.id.sufang125:
					case R.id.sufang150:
					case R.id.sufang200:
					case R.id.sufang250:
					case R.id.sufang75:
					case R.id.sufang100:
						if(lineBool) {
							lineListener.onClick(linesize05);
							refresh();
						}
						return true;
					case R.id.fonesize1:
					case R.id.fonesize20:
					case R.id.fonesize22:
					case R.id.fonesize24:
					case R.id.fonesize26:
					case R.id.fonesize16:
					case R.id.fonesize18:
						if(sufangBool) {
							sufangListen.onClick(sufang075);
							refresh();
						}else if(lineBool) {
							lineListener.onClick(linesize05);
							refresh();
						}
						return true;
					}
					break;
				case R.id.cancel:
					okButton.requestFocus();
					return  true ;
				case R.id.ok:
				{
					m_parent_view.requestFocus();
					if(fontBool){
						foneListen.onClick(fonesize16);
						refresh();
					}else if(sufangBool) {
						sufangListen.onClick(sufang075);
						refresh();
					}else if(lineBool){
						lineListener.onClick(linesize05);
						refresh();
					}
					return true ;
				}
				}
			}
			if (keyCode == event.KEYCODE_ENTER || keyCode == event.KEYCODE_DPAD_CENTER) {
				if(okButton.isFocusable()){
					okListen.onClick(okButton);
				}else if(cancelButton.isFocusable()){
					cancelListen.onClick(cancelButton);
				}else{
					okListen.onClick(okButton);
				}
			}
			if (keyCode == event.KEYCODE_BACK){
				cancelListen.onClick(cancelButton);
				return false ;
			}
			return true ;
		}
		return true ;
		}
	};
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Logger.i(TAG, "public boolean onKeyDown(int keyCode, KeyEvent event) {" + keyCode);
		return true ;
	}
	
	
	private float absFloat(float in){
		if(in < 0){
			in = -in ;
		}
		return in ;
	}
	
	private OnPviFocusChangeListener ofcl = new OnPviFocusChangeListener() {
		@Override
		public void onFocusChange(PviRect arg0, boolean arg1) {
			if(arg0 == null){
				return ;
			}
			if(arg1){
				int id = arg0.getId();
				switch (id) {
				case R.id.linesize1:chooseLayListen.onClick(linesize1);break;
				case R.id.linesize15:lineListener.onClick(linesize15);break;
				case R.id.linesize20:lineListener.onClick(linesize20);break;
				case R.id.linesize25:lineListener.onClick(linesize25);break;
				case R.id.linesize30:lineListener.onClick(linesize30);break;
				case R.id.linesize05:lineListener.onClick(linesize05);break;
				case R.id.linesize10:lineListener.onClick(linesize10);break;
				case R.id.sufang1:chooseLayListen.onClick(sufang1);break;
				case R.id.sufang125:sufangListen.onClick(sufang125);break;
				case R.id.sufang150:sufangListen.onClick(sufang150);break;
				case R.id.sufang200:sufangListen.onClick(sufang200);break;
				case R.id.sufang250:sufangListen.onClick(sufang250);break;
				case R.id.sufang75:sufangListen.onClick(sufang075);break;
				case R.id.sufang100:sufangListen.onClick(sufang100);break;
				case R.id.fonesize1:chooseLayListen.onClick(fonesize1);break;
				case R.id.fonesize20:foneListen.onClick(fonesize20);break;
				case R.id.fonesize22:foneListen.onClick(fonesize22);break;
				case R.id.fonesize24:foneListen.onClick(fonesize24);break;
				case R.id.fonesize26:foneListen.onClick(fonesize26);break;
				case R.id.fonesize16:foneListen.onClick(fonesize16);break;
				case R.id.fonesize18:foneListen.onClick(fonesize18);break;
//				case R.id.ok:okListen.onClick(okButton);break;
//				case R.id.cancel:cancelListen.onClick(cancelButton);break;
				default:chooseLayListen.onClick(null);break;
//					cancelListen.onClick(cancelButton);
//					break;
				}
			}
		}
	};
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		int idx = canvas.save();
		
		canvas.translate(0, 0);
		canvas.drawColor(Color.WHITE);

//		canvas.translate(paddingleft, btnpadtop);

		for(int i = 0;i < m_controls.size();i ++)
		{
			Bitmap temp = null;
			PviRect ctrl_v = m_controls.get(i);
			
			canvas.save();
			
			canvas.translate(ctrl_v.m_x, ctrl_v.m_y);
			
//			if(ViewisFocus && ctrl_v.get_ctrl_id() == focus_idx)
//			{
//				if(ctrl_v.m_focused_bitmap_resource_id != -1) {
//					temp = BitmapFactory.decodeResource(context_local.getResources(), ctrl_v.m_focused_bitmap_resource_id);
//				}
//			}
//			else
			{
				if(ctrl_v.m_bitmap_resource_id != -1) {
					temp = BitmapFactory.decodeResource(context_local.getResources(), ctrl_v.m_bitmap_resource_id);
				}
			}
			canvas.drawBitmap(temp, 0, 0, mPaint);
			if(temp != null) {
				temp.recycle();
			}
			if(ctrl_v.m_text != null) {
				//			mPaint.setColor(txtcolor);
				canvas.drawText(ctrl_v.m_text, 0, ctrl_v.m_text.length(), 0, 0, mPaint);
			}
			
			canvas.restore();

		}
		
		canvas.restoreToCount(idx);
	}
	public void refresh()
	{
		Logger.e(TAG, "refresh");
		if(GlobalVar.deviceType == 1)
		{
//			invalidate(UPDATE_MODE_FULL);
		}
		else
		{
			invalidate();
		}
	}
}
