package com.pvi.ap.reader.activity;

import com.pvi.ap.reader.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class welcome_view extends View implements View.OnTouchListener, View.OnFocusChangeListener, View.OnKeyListener{
//	private static final String TAG = "welcome_view";
	static final int ERROR = -1;
	Context context_local = null;
	private Resources res=null;
	int focus_idx = 0;
	public static final int CLOSEBTN = 0;
	public static final int WELCOMEITEM1 = 1;
	public static final int WELCOMEITEM2 = 2;
	public static final int WELCOMEITEM3 = 3;
	public static final int WELCOMEITEM4 = 4;
	public static final int WELCOMEITEM5 = 5;
	public static final int WELCOMEITEM6 = 6;
	public static final int WELCOMEITEM7 = 7;

	private boolean ViewisFocus = false;

	int bkground = R.drawable.bg_popwin_ui1;
	int itemnormal = R.drawable.btn_normal_ui1;
	int itemsel = R.drawable.btn_pressed_ui1;
	private String title = "欢迎登录无线书城";
	private String loading = " 内容加载中...";
	private String updatetime = "";

	Paint mPaint = new Paint();
	private int closebtn_normal = R.drawable.bg_button_close_dialog_normal;
	private int closebtn_sel = R.drawable.bg_button_close_dialog_pressed;
	private Rect closebtn = new Rect();
	private Rect[] welitem = new Rect[7];

	private String [] itemstr = {"","","","","","",""};
	int getItemNum()
	{
		int itemNum = 0;
		for(int i = 0; i < itemstr.length; i++)
		{
			if(itemstr[i]!=null&&!itemstr[i].equals(""))
			{
				itemNum ++;
			}
		}
		return itemNum;
	}
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
	public int getFocusIndex()
	{
		return focus_idx;
	}
	public void setWelItem(String[] welcomeinfo)
	{
		for(int i = 0; i< welcomeinfo.length-1; i++)
		{
			this.itemstr[i] = welcomeinfo[i];
		}
		this.updatetime = welcomeinfo[welcomeinfo.length-1];
		this.refresh();
	}
	public welcome_view(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public welcome_view(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public welcome_view(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			float x = event.getX();
			float y = event.getY();

			if(x>this.closebtn.left && x < this.closebtn.right &&y>this.closebtn.top&&y<this.closebtn.bottom)
			{
				focus_idx = welcome_view.CLOSEBTN;
			}
			else if((this.getItemNum()>=1) &&this.welitem[0]!=null && x>=this.welitem[0].left && x <= this.welitem[0].right &&y>=this.welitem[0].top&&y<=this.welitem[0].bottom)
			{
				focus_idx = welcome_view.WELCOMEITEM1;
			}
			else if((this.getItemNum()>=2) &&this.welitem[1]!=null&&x>=this.welitem[1].left && x <= this.welitem[1].right &&y>=this.welitem[1].top&&y<=this.welitem[1].bottom)
			{
				focus_idx = welcome_view.WELCOMEITEM2;
			}
			else if((this.getItemNum()>=3) &&this.welitem[2]!=null&&x>=this.welitem[2].left && x <= this.welitem[2].right &&y>=this.welitem[2].top&&y<=this.welitem[2].bottom)
			{
				focus_idx = welcome_view.WELCOMEITEM3;
			}
			else if((this.getItemNum()>=4) &&this.welitem[3]!=null&&x>=this.welitem[3].left && x <= this.welitem[3].right &&y>=this.welitem[3].top&&y<=this.welitem[3].bottom)
			{
				focus_idx = welcome_view.WELCOMEITEM4;
			}
			else if((this.getItemNum()>=5) &&this.welitem[4]!=null&&x>=this.welitem[4].left && x <= this.welitem[4].right &&y>=this.welitem[4].top&&y<=this.welitem[4].bottom)
			{
				focus_idx = welcome_view.WELCOMEITEM5;
			}
			else if((this.getItemNum()>=6) &&this.welitem[5]!=null&&x>=this.welitem[5].left && x <= this.welitem[5].right &&y>=this.welitem[5].top&&y<=this.welitem[5].bottom)
			{
				focus_idx = welcome_view.WELCOMEITEM6;
			}
			else if((this.getItemNum()>=7) &&this.welitem[6]!=null&&x>=this.welitem[6].left && x <= this.welitem[6].right &&y>=this.welitem[6].top&&y<=this.welitem[6].bottom)
			{
				focus_idx = welcome_view.WELCOMEITEM7;
			}
			else
			{
				focus_idx = welcome_view.ERROR;
			}
			refresh();
	
			v.requestFocus();
			v.performClick();
		
			return true;
		}
		return false;
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
				return true;
			}
			else if(keycode == KeyEvent.KEYCODE_DPAD_RIGHT)
			{
				return true;
			}
			else if(keycode == KeyEvent.KEYCODE_DPAD_DOWN)
			{
				if(this.focus_idx < this.getItemNum())
				{
					focus_idx ++;
				}
				else
				{
					focus_idx = this.getItemNum();
				}
				this.refresh();
				return true;
			}
			else if(keycode == KeyEvent.KEYCODE_DPAD_UP)
			{
	
				if(this.focus_idx>0)
				{
					focus_idx --;
				}
				else
				{
					focus_idx = 0;
				}
				this.refresh();
				return true;
			}
			return false;
		}
		return false;
	}
	private NinePatch np = null;

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Bitmap temp = null;
		Rect bkrect = new Rect();
		int itemNum = this.getItemNum();
		bkrect.left = 0;
		bkrect.right = 402;
		if(itemNum==0)
		{
			bkrect.top=320;
			bkrect.bottom=480;
		}
		else
		{
			bkrect.top=400-(50+80+80*itemNum)/2;
			bkrect.bottom=400+(50+80+80*itemNum)/2;
		}

		temp = BitmapFactory.decodeResource(res, this.bkground);

		np = new NinePatch(temp,temp.getNinePatchChunk(), null);
		np.draw(canvas, bkrect);
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setTextSize(22);
		mPaint.setFakeBoldText(true);
		canvas.drawText(title, 201, bkrect.top + 30, mPaint);


		if(ViewisFocus&&focus_idx == welcome_view.CLOSEBTN)
		{
			temp = BitmapFactory.decodeResource(res, closebtn_sel);
		}
		else
		{
			temp = BitmapFactory.decodeResource(res, closebtn_normal);
		}
		closebtn.left = 340;
		closebtn.right = temp.getWidth() + 340;
		closebtn.top = bkrect.top;
		closebtn.bottom = bkrect.top+temp.getHeight()+4;
		canvas.drawBitmap(temp, 340, bkrect.top+2, mPaint);
		canvas.save();
		canvas.translate(20, bkrect.top + 80);
		mPaint.setTextAlign(Align.LEFT);
		mPaint.setFakeBoldText(false);
		Rect temprect = new Rect();
		Rect itemrect = new Rect();
		canvas.save();
		if(itemNum==0)
		{
			temp = BitmapFactory.decodeResource(res, this.itemnormal);
			np = new NinePatch(temp,temp.getNinePatchChunk(), null);
			temprect.left=0;
			temprect.right=360;
			temprect.top=0;
			temprect.bottom=50;
			np.draw(canvas, temprect);
			canvas.drawText(loading, 5, 30, mPaint);
		}
		else
		{
			String str = "";
			for(int i = 0; i<itemNum; i++)
			{
				itemrect = new Rect();
				itemrect.left = 20;
				itemrect.right = 380;
				itemrect.top = bkrect.top + (i+1)*80;
				itemrect.bottom = itemrect.top + 50;
				canvas.restore();
				if(ViewisFocus&&focus_idx == i+1)
				{
					temp = BitmapFactory.decodeResource(res, this.itemsel);
				}
				else
				{
					temp = BitmapFactory.decodeResource(res, this.itemnormal);
				}
				np = new NinePatch(temp,temp.getNinePatchChunk(), null);
				temprect.left=0;
				temprect.right=360;
				temprect.top=0;
				temprect.bottom=50;
				this.welitem[i] = itemrect;
				np.draw(canvas, temprect);
				str = getLine(mPaint, itemstr[i], 360);
				if(str.length() < itemstr[i].length())
				{
					str = str.subSequence(0, str.length()-2)+"...";
				}
				canvas.drawText(str, 0, 30, mPaint);
				canvas.translate(0, 80);
				canvas.save();
			}
			mPaint.setTextAlign(Align.CENTER);
			mPaint.setTextSize(20);
			canvas.drawText(updatetime, 181, 20, mPaint);
			canvas.save();
		}
	}

	private String getLine(Paint mpaint,String source, int width)
	{
		Rect rect = new Rect();
		String dest = null;
		for(int i = 1; i <= source.length(); i ++)
		{
			mpaint.getTextBounds(source, 0, i, rect);
			if(rect.right - rect.left > width)
			{
				dest = source.substring(0, i-1);
				return dest;
			}
		}
		dest = source.substring(0);
		return dest;
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
}
