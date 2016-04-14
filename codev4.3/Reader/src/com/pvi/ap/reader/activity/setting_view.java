package com.pvi.ap.reader.activity;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.data.common.Logger;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class setting_view extends View implements View.OnFocusChangeListener,
View.OnKeyListener, View.OnTouchListener{
	private static final String TAG = "setting_view";
	static final int ERROR = -1;
	Context context_local = null;
	private Resources res=null;
	private int focus_idx = 0;
	private int sel_item = 0;
	public static final int ITEM1 = 0;
	public static final int ITEM2 = 1;
	public static final int OKBTN = 2;
	public static final int CANCELBTN = 3;
	String[] itemstr = {"",""};
	private int bkground = R.drawable.timeset_back;
	private int checkbox_normal = R.drawable.notcheck;
	private int checkbox_sel = R.drawable.check;
	private int dividor = R.drawable.timeset_split;
	private int btn_sel = R.drawable.btn_pressed_ui1;
	private int btn_normal = R.drawable.btn_normal_ui1;
	private String title = "";
	private boolean ViewisFocus = false;
	private Rect[] itemrect = new Rect[2];
	private Rect okbtn = new Rect();
	private Rect cancelbtn = new Rect();

	private int itemNum = 2;
	Paint mPaint = new Paint();
	private void init(Context context)
	{
		context_local = context;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStrokeWidth(5);
		mPaint.setTextSize(19);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
		this.setOnFocusChangeListener(this);
		this.setOnKeyListener(this);
		res= context_local.getResources();
	}
	public setting_view(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public setting_view(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public setting_view(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		// TODO Auto-generated method stub
		ViewisFocus = arg1;
		refresh();
//		Logger.e(TAG, "onFocusChange " + arg1);
	}

	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if(keycode == KeyEvent.KEYCODE_DPAD_CENTER)
			{
				v.performClick();
				return true;
			}
			else if(keycode == KeyEvent.KEYCODE_DPAD_LEFT)
			{
				if(this.focus_idx==setting_view.ITEM2 || this.focus_idx==setting_view.CANCELBTN)
				{
					focus_idx --;
					if(focus_idx <= 1)
					{
						this.sel_item = focus_idx;
					}
					this.refresh();
				}
				return true;
			}
			else if(keycode == KeyEvent.KEYCODE_DPAD_RIGHT)
			{
				if(this.focus_idx==setting_view.ITEM1 || this.focus_idx==setting_view.OKBTN)
				{
					focus_idx ++;
					if(focus_idx <= 1)
					{
						this.sel_item = focus_idx;
					}
					this.refresh();
				}
				return true;
			}
			else if(keycode == KeyEvent.KEYCODE_DPAD_DOWN)
			{
				if(this.focus_idx==setting_view.ITEM1 || this.focus_idx==setting_view.ITEM2)
				{
					focus_idx = setting_view.OKBTN;
					this.refresh();
				}
				else
				{
					return false;
				}
				return true;
			}
			else if(keycode == KeyEvent.KEYCODE_DPAD_UP)
			{
				if(this.focus_idx==setting_view.OKBTN || this.focus_idx==setting_view.CANCELBTN)
				{
					focus_idx = setting_view.ITEM2;
					this.sel_item = focus_idx;
					this.refresh();
				}
				else
				{
					return false;
				}
				return true;
			}
			return false;
		}
		return false;
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			float x = event.getX();
			float y = event.getY();
			if((x>=this.okbtn.left-20) && (x <= this.okbtn.right+5) && (y>=this.okbtn.top-20)&& (y<=this.okbtn.bottom+15))
			{
				focus_idx = setting_view.OKBTN;
			}
			else if(x>=this.cancelbtn.left-5 && x <= this.cancelbtn.right+20 &&y>=this.cancelbtn.top-20&&y<=this.cancelbtn.bottom+15)
			{
				focus_idx = setting_view.CANCELBTN;
			}
			else if(this.itemrect[0]!=null && x>=this.itemrect[0].left-20 && x <= this.itemrect[0].right+20 &&y>=this.itemrect[0].top-20&&y<=this.itemrect[0].bottom+20)
			{
				focus_idx = setting_view.ITEM1;
				this.sel_item = focus_idx;
			}
			else if(this.itemrect[1]!=null&&x>=this.itemrect[1].left-20 && x <= this.itemrect[1].right+20 &&y>=this.itemrect[1].top-20&&y<=this.itemrect[1].bottom+20)
			{
				focus_idx = setting_view.ITEM2;
				this.sel_item = focus_idx;
			}
			else
			{
				focus_idx = welcome_view.ERROR;
			}
//			Logger.e(TAG,focus_idx + "is click");
			v.requestFocus();
			refresh();
			v.performClick();
		
			return true;
		}
		return false;
	}
	private Canvas canvas = null;
	private Bitmap getItem(String itemstr, Paint mPaint, boolean issel)
	{

		Bitmap bitmap = null;
		Rect tmp = new Rect();
		mPaint.getTextBounds(itemstr, 0, itemstr.length(), tmp);

		Bitmap temp =null;
		if(issel)
		{
			temp= BitmapFactory.decodeResource(res, this.checkbox_sel);
		}
		else
		{
			temp= BitmapFactory.decodeResource(res, this.checkbox_normal);
		}

		bitmap = Bitmap.createBitmap(temp.getWidth() + 10 + tmp.right-tmp.left, temp.getHeight()>tmp.bottom-tmp.top?temp.getHeight()+4:tmp.bottom-tmp.top+4, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(temp, 0, (bitmap.getHeight()-temp.getHeight())/2, mPaint);
		canvas.drawText(itemstr, temp.getWidth()+10, tmp.bottom-tmp.top, mPaint);
		canvas.save();
		return bitmap;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int y = 0;
		Bitmap bitmap = null;
		canvas.drawColor(Color.WHITE);
		canvas.save();
		canvas.translate(0, 40);
		y = y + 40;
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setTextSize(25);
		canvas.drawText(title, 300, 20, mPaint);
		bitmap = BitmapFactory.decodeResource(res, this.dividor);
		canvas.drawBitmap(bitmap, 20, 40, mPaint);
		canvas.save();
		canvas.translate(30, 40 + bitmap.getHeight() + 15);
		canvas.save();
		y = y + 40 + bitmap.getHeight() + 15;
		bitmap = BitmapFactory.decodeResource(res, this.bkground);
		canvas.translate(0, bitmap.getHeight()-10);
		y = y + bitmap.getHeight()-10;
		canvas.save();
		canvas.drawBitmap(bitmap, 300-30 -bitmap.getWidth()/2, 0, mPaint);
		canvas.save();
	
		Rect temp = null;
		int height = bitmap.getHeight();
		for(int i=0; i < itemNum; i++)
		{
			temp = new Rect();
			mPaint.setTextAlign(Align.LEFT);
			mPaint.setTextSize(22);
			if(this.sel_item == i)
			{
				bitmap = this.getItem(itemstr[i], mPaint, true);
			}
			else
			{
				bitmap = this.getItem(itemstr[i], mPaint, false);
			}

			temp.left = 30+135-bitmap.getWidth()/2 + i*270;
			temp.right = temp.left + bitmap.getWidth();
			temp.top = y;
			temp.bottom = temp.top + height;
			this.itemrect[i] = temp;
			canvas.drawBitmap(bitmap, 135-bitmap.getWidth()/2, (height-bitmap.getHeight())/2, mPaint);
			canvas.translate(270, 0);
			canvas.save();
		}
		canvas.restore();
		canvas.translate(60-270, 630 - y);
		canvas.save();
		temp = new Rect();
		temp.left=0;
		temp.right=100;
		temp.top=0;
		temp.bottom=33;
		this.okbtn.left = temp.left + 360;
		this.okbtn.right = temp.right+360;
		this.okbtn.top = temp.top + 630;
		this.okbtn.bottom = temp.bottom + 630;
		NinePatch np = null;
		if(ViewisFocus&&this.focus_idx==setting_view.OKBTN)
		{
			bitmap = BitmapFactory.decodeResource(res, this.btn_sel);
		}
		else
		{
			bitmap = BitmapFactory.decodeResource(res, this.btn_normal);
		}

		np = new NinePatch(bitmap,bitmap.getNinePatchChunk(), null);
		np.draw(canvas, temp);
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setTextSize(19);
		canvas.drawText("确  定", 50, 24, mPaint);


		temp = new Rect();
		temp.left=120;
		temp.right=220;
		temp.top=0;
		temp.bottom=33;
		this.cancelbtn.left = temp.left + 360;
		this.cancelbtn.right = temp.right+360;
		this.cancelbtn.top = temp.top + 630;
		this.cancelbtn.bottom = temp.bottom + 630;
		if(ViewisFocus&&this.focus_idx==setting_view.CANCELBTN)
		{
			bitmap = BitmapFactory.decodeResource(res, this.btn_sel);
		}
		else
		{
			bitmap = BitmapFactory.decodeResource(res, this.btn_normal);
		}
		np = new NinePatch(bitmap,bitmap.getNinePatchChunk(), null);
		np.draw(canvas, temp);
		mPaint.setTextAlign(Align.CENTER);
		canvas.drawText("取  消", 170, 24, mPaint);
		canvas.save();
	}
	public void setSettingInfo(String [] str)
	{ 
		if(str.length<4)
		{
			Logger.e(TAG, "Not Enough Parameter!");
			return;
		}
		else
		{
			this.itemstr[0] = str[0]; //第一个item字符串
			this.itemstr[1] = str[1];
			this.title = str[2];//标题
			try{
			this.sel_item = Integer.parseInt(str[3]);//选中项
			}catch(NumberFormatException e){
				Logger.v(TAG, e);
			}
			this.focus_idx = this.sel_item;
		}
	
		this.refresh();
	
	}
//	private static final int UPDATE_MODE_FULL    = EINK_AUTO_MODE_REGIONAL| EINK_WAIT_MODE_NOWAIT | EINK_WAVEFORM_MODE_GC16| EINK_UPDATE_MODE_PARTIAL;
	void refresh()
	{
		if(GlobalVar.deviceType == 1)
		{
//			invalidate(UPDATE_MODE_FULL);
		}
		else
		{
			invalidate();
		}
	}
	public int getFocusIndex()
	{
		return focus_idx;
	}
	public int getSelIndex()
	{
		return sel_item;
	}
}
